import { createHmac, timingSafeEqual } from 'node:crypto';

type CapabilityPermitPayload = { exp: number; hosts: string[] };

const encode = (value: string) => Buffer.from(value).toString('base64url');
const decode = (value: string) => Buffer.from(value, 'base64url').toString('utf8');

/** Issues short-lived permits for capability hosts returned by a trusted login service. */
export class CapabilityPermitService {
  constructor(private readonly secret: string, private readonly ttlMs = 5 * 60_000) {
    if (secret.length < 32) throw new Error('PROXY_PERMIT_SECRET must contain at least 32 characters');
  }

  issue(capabilityUrls: string[]): string | null {
    const hosts = [...new Set(capabilityUrls.flatMap((raw) => {
      try {
        const url = new URL(raw);
        return url.protocol === 'https:' && !url.username && !url.password ? [url.hostname.toLowerCase()] : [];
      } catch { return []; }
    }))];
    if (hosts.length === 0) return null;
    const payload = encode(JSON.stringify({ exp: Date.now() + this.ttlMs, hosts } satisfies CapabilityPermitPayload));
    return `${payload}.${this.sign(payload)}`;
  }

  permits(target: URL, token: unknown): boolean {
    if (typeof token !== 'string') return false;
    const [payload, signature, ...extra] = token.split('.');
    if (!payload || !signature || extra.length) return false;
    const expected = this.sign(payload);
    if (signature.length !== expected.length || !timingSafeEqual(Buffer.from(signature), Buffer.from(expected))) return false;
    try {
      const parsed = JSON.parse(decode(payload)) as CapabilityPermitPayload;
      return parsed.exp > Date.now() && parsed.hosts.includes(target.hostname.toLowerCase());
    } catch { return false; }
  }

  private sign(payload: string) {
    return createHmac('sha256', this.secret).update(payload).digest('base64url');
  }
}

export function extractSeedCapability(xml: string): string[] {
  const match = xml.match(/<name>\s*seed_capability\s*<\/name>\s*<value>\s*<string>([^<]+)<\/string>/i);
  return match ? [match[1].trim()] : [];
}
