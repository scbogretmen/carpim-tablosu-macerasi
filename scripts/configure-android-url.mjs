import { readFile, writeFile } from 'node:fs/promises';

const remoteUrl = process.env.CAPACITOR_SERVER_URL;
if (remoteUrl) {
  const config = JSON.parse(await readFile('capacitor.config.json', 'utf8'));
  config.server = { url: remoteUrl, cleartext: false };
  await writeFile('capacitor.config.json', `${JSON.stringify(config, null, 2)}\n`);
}
