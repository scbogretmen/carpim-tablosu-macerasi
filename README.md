# Carpim Tablosu Macerasi

## Yayinlama

`main` dalina her gonderim, GitHub Pages uzerindeki web/PWA surumunu yayinlar. Android APK icin imzali bir etiket gonderin:

```powershell
git tag v1.0.0
git push origin v1.0.0
```

Workflow APK'yi GitHub Releases sayfasina ekler. Uygulama GitHub Pages'taki oyunu yukledigi icin oyun arayuzu ve icerik guncellemeleri otomatik gelir. Yeni bir APK Release'i ciktiginda uygulama acilisinda kullaniciya guncelleme sorulur; kabul edilirse Android'in kurulum onayina yonlendirilir, reddedilirse hicbir degisiklik yapilmaz.

Her APK guncellemesini `vMAJOR.MINOR.PATCH` bicimindeki artan bir etiketle yayinlayin. Ornek: `v1.0.1`.

## Bir kerelik GitHub ayarlari

1. GitHub deposunda `Settings > Pages > Build and deployment` alanindan `GitHub Actions` secin.
2. `Settings > Secrets and variables > Actions` alanina su imzalama bilgilerini ekleyin: `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`.
3. Ilk imzalama anahtarini bir kez olusturun; bundan sonra ayni anahtari kullanin. Anahtar degisirse Android eski APK'nin ustune yeni surumu kurmaz.

## Yerelde Android

```powershell
npm install
npm run android:add
npm run android:prepare
```
