import { cp, mkdir, rm } from 'node:fs/promises';

const files = ['index.html', 'manifest.json', 'sw.js', 'icon-192.png', 'icon-512.png'];

await rm('web', { recursive: true, force: true });
await mkdir('web', { recursive: true });
await Promise.all(files.map((file) => cp(file, `web/${file}`)));
