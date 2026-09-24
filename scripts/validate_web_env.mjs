const allowed = new Set(['/', '/Linkpoint/']);
const base = process.env.VITE_BASE_PATH || '/';
if (!allowed.has(base) && !/^\/[A-Za-z0-9._-]+\/$/.test(base)) {
  console.error('VITE_BASE_PATH must be / or a single safe path segment ending in /.');
  process.exit(1);
}
const proxy = process.env.VITE_SL_PROXY_URL;
if (proxy) {
  const url = new URL(proxy);
  if (url.protocol !== 'https:' || url.username || url.password) {
    console.error('VITE_SL_PROXY_URL must be HTTPS and contain no embedded credentials.');
    process.exit(1);
  }
}
