/**
 * The browser proxy is deliberately limited to known login hosts.  It is not a
 * general-purpose HTTP relay: accepting arbitrary URLs would turn a deployed
 * viewer proxy into an SSRF/open-proxy service.
 */
export const DEFAULT_PROXY_ALLOWED_HOSTS = [
  'login.agni.lindenlab.com',
  'login.aditi.lindenlab.com',
  'login.osgrid.org',
  'grid.kitely.com',
  'localhost',
  '127.0.0.1',
];

const dynamicAllowedHosts = new Set<string>();

export function registerForeignCapabilityHost(rawUrlOrHost: string): void {
  try {
    let host = String(rawUrlOrHost || '').trim().toLowerCase();
    if (host.includes('://')) {
      const url = new URL(host);
      host = url.hostname.toLowerCase();
    } else if (host.includes('/')) {
      host = host.split('/')[0].toLowerCase();
    }
    if (host.includes(':')) {
      host = host.split(':')[0].toLowerCase();
    }
    if (host) dynamicAllowedHosts.add(host);
  } catch {
    /* ignore invalid host */
  }
}

export function clearForeignCapabilityHosts(): void {
  dynamicAllowedHosts.clear();
}

export function getAllowedProxyHosts(value = process.env.SL_PROXY_ALLOWED_HOSTS): Set<string> {
  const hosts = value
    ? value.split(',').map((host) => host.trim().toLowerCase()).filter(Boolean)
    : DEFAULT_PROXY_ALLOWED_HOSTS;

  const set = new Set(hosts);
  for (const host of dynamicAllowedHosts) {
    set.add(host);
  }
  return set;
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

  const host = target.hostname.toLowerCase();
  const isLocal = ['localhost', '127.0.0.1'].includes(host);
  const isDynamicForeign = dynamicAllowedHosts.has(host);

  if (target.protocol !== 'https:' && !isLocal && !isDynamicForeign) {
    throw new Error('Only HTTPS targets are allowed');
  }
  if (target.username || target.password) {
    throw new Error('Target URL must not include credentials');
  }
  if (!allowedHosts.has(host)) {
    throw new Error('Target host is not allowed');
  }

  return target;
}

export function parseSecureProxyTarget(rawUrl: unknown): URL {
  if (typeof rawUrl !== 'string' || rawUrl.trim() === '') throw new Error('Target URL is required');
  let target: URL;
  try { target = new URL(rawUrl); } catch { throw new Error('Target URL must be an absolute URL'); }
  if (target.protocol !== 'https:' && !['localhost', '127.0.0.1'].includes(target.hostname.toLowerCase())) throw new Error('Only HTTPS targets are allowed');
  if (target.username || target.password) throw new Error('Target URL must not include credentials');
  return target;
}
