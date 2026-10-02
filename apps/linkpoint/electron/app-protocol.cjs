// Serves the built web app to the desktop window over a privileged custom scheme.
//
// Chromium refuses fetch() of file:// URLs, so a window loaded with loadFile() cannot read the bundled
// avatar meshes and animations. A standard, fetch-capable scheme can. Requests are confined to dist/.
const path = require('node:path');
const { pathToFileURL } = require('node:url');

const APP_SCHEME = 'linkpoint';
const APP_URL = `${APP_SCHEME}://app/index.html`;

/** Scheme registration; must run before the app is ready. */
const APP_SCHEME_REGISTRATION = {
  scheme: APP_SCHEME,
  privileges: { standard: true, secure: true, supportFetchAPI: true, stream: true },
};

/**
 * Absolute file inside `root` for a request URL, or null when the URL is not for this app or escapes `root`.
 * `/` and directory-style paths map to index.html.
 */
function resolveAppFile(root, requestUrl) {
  let url;
  try { url = new URL(requestUrl); } catch { return null; }
  if (url.protocol !== `${APP_SCHEME}:` || url.hostname !== 'app') return null;
  let relative;
  try { relative = decodeURIComponent(url.pathname); } catch { return null; }
  if (relative.includes('\0')) return null;
  relative = relative.replace(/^\/+/, '');
  if (!relative || relative.endsWith('/')) relative += 'index.html';
  const base = path.resolve(root);
  const file = path.resolve(base, relative);
  if (file !== base && !file.startsWith(base + path.sep)) return null;
  return file;
}

/** Install the protocol handler (call after the app is ready). */
function registerAppProtocol({ protocol, net }, root) {
  protocol.handle(APP_SCHEME, async (request) => {
    const file = resolveAppFile(root, request.url);
    if (!file) return new Response('Not found', { status: 404 });
    try {
      return await net.fetch(pathToFileURL(file).toString());
    } catch {
      return new Response('Not found', { status: 404 });
    }
  });
}

module.exports = { APP_SCHEME, APP_URL, APP_SCHEME_REGISTRATION, resolveAppFile, registerAppProtocol };
