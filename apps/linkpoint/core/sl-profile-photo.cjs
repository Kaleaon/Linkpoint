/**
 * Public Second Life profile pictures.
 *
 * Residents' web profile pictures are served from a public bucket keyed by the
 * web username (`first.last`, or just `first` for the default "Resident" last
 * name). Shared by the web server and the Electron main process so both fetch
 * the same way, with the same validation.
 *
 * Checked by hand against live responses: a resident with a picture answers 200
 * with a PNG; an unknown name answers 403. Nothing here fabricates an image: a
 * missing picture is reported as missing.
 */

const BASE = 'https://my-secondlife.s3.amazonaws.com/users';
/** Larger than this is refused rather than buffered (the full-size picture is a few hundred KB). */
const MAX_BYTES = 2 * 1024 * 1024;
const USERNAME_PATTERN = /^[a-z0-9][a-z0-9._-]{0,62}$/;

/**
 * Web username for a resident name: lower case, spaces become dots, and the
 * default "Resident" last name is dropped. Returns null if the result is not a
 * plausible username, which also keeps odd input out of the URL.
 */
function profileUsername(name) {
  if (typeof name !== 'string') return null;
  const parts = name.trim().toLowerCase().split(/\s+/).filter(Boolean);
  if (!parts.length) return null;
  if (parts.length > 1 && parts[parts.length - 1] === 'resident') parts.pop();
  const username = parts.join('.');
  return USERNAME_PATTERN.test(username) ? username : null;
}

function profilePhotoUrl(name, thumbnail = true) {
  const username = profileUsername(name);
  return username ? `${BASE}/${username}/${thumbnail ? 'thumb_sl_image.png' : 'sl_image.png'}` : null;
}

/**
 * Fetch a resident's profile picture. `fetchImpl` is injectable for tests.
 * Resolves `{ contentType, base64 }`, or `null` when the resident has no public
 * picture. Throws only for a bad name, an oversize response or a network error.
 */
async function fetchProfilePhoto(name, { thumbnail = true, fetchImpl = globalThis.fetch, timeoutMs = 8000 } = {}) {
  const url = profilePhotoUrl(name, thumbnail);
  if (!url) throw new Error('That is not a valid resident name');
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeoutMs);
  try {
    const response = await fetchImpl(url, { signal: controller.signal, redirect: 'error' });
    if (response.status === 403 || response.status === 404) return null;
    if (!response.ok) throw new Error(`The profile picture service answered ${response.status}`);
    const contentType = String(response.headers.get('content-type') || '').split(';')[0].trim().toLowerCase();
    if (!/^image\/(png|jpeg|gif|webp)$/.test(contentType)) return null;
    const bytes = Buffer.from(await response.arrayBuffer());
    if (bytes.length === 0) return null;
    if (bytes.length > MAX_BYTES) throw new Error('The profile picture is too large');
    return { contentType, base64: bytes.toString('base64') };
  } finally {
    clearTimeout(timer);
  }
}

module.exports = { profileUsername, profilePhotoUrl, fetchProfilePhoto, MAX_BYTES };
