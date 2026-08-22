package com.carpimmacerasi.app;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.PluginMethod;

import java.io.File;

@CapacitorPlugin(name = "ApkUpdater")
public class ApkUpdaterPlugin extends Plugin {
    private static final String APK_MIME_TYPE = "application/vnd.android.package-archive";

    @PluginMethod
    public void getVersion(PluginCall call) {
        try {
            PackageInfo packageInfo = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0);
            JSObject version = new JSObject();
            version.put("versionName", packageInfo.versionName);
            version.put("versionCode", Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    ? packageInfo.getLongVersionCode()
                    : packageInfo.versionCode);
            call.resolve(version);
        } catch (Exception exception) {
            call.reject("Uygulama surumu okunamadi.", exception);
        }
    }

    @PluginMethod
    public void downloadAndInstall(PluginCall call) {
        String rawUrl = call.getString("url");
        Uri downloadUrl = rawUrl == null ? null : Uri.parse(rawUrl);
        if (downloadUrl == null || !"https".equals(downloadUrl.getScheme())) {
            call.reject("Gecersiz guncelleme baglantisi.");
            return;
        }

        File downloadDir = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadDir == null) {
            call.reject("Guncelleme dosyasi icin depolama alani kullanilamiyor.");
            return;
        }

        String fileName = "carpim-macerasi-update-" + System.currentTimeMillis() + ".apk";
        File apkFile = new File(downloadDir, fileName);
        DownloadManager.Request request = new DownloadManager.Request(downloadUrl)
                .setTitle("Carpim Macerasi guncelleniyor")
                .setDescription("Guncelleme indiriliyor")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(apkFile));
        DownloadManager downloadManager = (DownloadManager) getContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) != downloadId) {
                    return;
                }
                try {
                    context.unregisterReceiver(this);
                    installApk(apkFile);
                } catch (Exception ignored) {
                    // The DownloadManager notification still lets the user open a completed download.
                }
            }
        };
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getContext().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            getContext().registerReceiver(receiver, filter);
        }
        call.resolve();
    }

    private void installApk(File apkFile) {
        Uri apkUri = FileProvider.getUriForFile(
                getContext(), getContext().getPackageName() + ".fileprovider", apkFile);
        Intent installIntent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(apkUri, APK_MIME_TYPE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(installIntent);
    }
}
