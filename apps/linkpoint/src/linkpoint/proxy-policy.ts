/**
 * The browser proxy is deliberately limited to known login hosts.  It is not a
 * general-purpose HTTP relay: accepting arbitrary URLs would turn a deployed
 * viewer proxy into an SSRF/open-proxy service.
 */
export const DEFAULT_PROXY_ALLOWED_HOSTS = [
  'login.agni.lindenlab.com',
  'login.aditi.lindenlab.com',
];

export function getAllowedProxyHosts(value = process.env.SL_PROXY_ALLOWED_HOSTS): Set<string> {
  const hosts = value
    ? value.split(',').map((host) => host.trim().toLowerCase()).filter(Boolean)
    : DEFAULT_PROXY_ALLOWED_HOSTS;

  return new Set(hosts);
}

export function validateProxyTarget(rawUrl: unknown, allowedHosts = getAllowedProxyHosts()): URL {
  if (typeof rawUrl !== 'string' || rawUrl.trim() === '') {
    throw new Error('Target URL is required');
  }

  let target: URL;
  try {
    target = new URL(rawUrl);
  } catch {
    throw new Error('Target URL must be an absolute URL');
  }

  if (target.protocol !== 'https:') {
    throw new Error('Only HTTPS targets are allowed');
  }
  if (target.username || target.password) {
    throw new Error('Target URL must not include credentials');
  }
  if (!allowedHosts.has(target.hostname.toLowerCase())) {
    throw new Error('Target host is not allowed');
  }

  return target;
}

export function parseSecureProxyTarget(rawUrl: unknown): URL {
  if (typeof rawUrl !== 'string' || rawUrl.trim() === '') throw new Error('Target URL is required');
  let target: URL;
  try { target = new URL(rawUrl); } catch { throw new Error('Target URL must be an absolute URL'); }
  if (target.protocol !== 'https:') throw new Error('Only HTTPS targets are allowed');
  if (target.username || target.password) throw new Error('Target URL must not include credentials');
  return target;
}
