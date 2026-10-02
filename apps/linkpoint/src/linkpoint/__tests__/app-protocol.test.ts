import { describe, expect, it, vi } from 'vitest';
import { createRequire } from 'node:module';
import path from 'node:path';

const require = createRequire(import.meta.url);
const { APP_SCHEME, APP_SCHEME_REGISTRATION, APP_URL, registerAppProtocol, resolveAppFile } = require('../../../electron/app-protocol.cjs');

const root = path.resolve('/opt/app/dist');

describe('desktop app protocol', () => {
  it('registers a standard, secure, fetch-capable scheme', () => {
    expect(APP_SCHEME_REGISTRATION).toEqual({ scheme: 'linkpoint', privileges: expect.objectContaining({ standard: true, secure: true, supportFetchAPI: true }) });
    expect(APP_URL).toBe('linkpoint://app/index.html');
  });

  it('maps requests inside dist, including bundled avatar and animation assets', () => {
    expect(resolveAppFile(root, 'linkpoint://app/index.html')).toBe(path.join(root, 'index.html'));
    expect(resolveAppFile(root, 'linkpoint://app/')).toBe(path.join(root, 'index.html'));
    expect(resolveAppFile(root, 'linkpoint://app/assets/index-abc.js?x=1#y')).toBe(path.join(root, 'assets', 'index-abc.js'));
    expect(resolveAppFile(root, 'linkpoint://app/avatar/meshes.json')).toBe(path.join(root, 'avatar', 'meshes.json'));
    expect(resolveAppFile(root, 'linkpoint://app/anims/038fcec9-5ebd-8a8e-0e2e-6e71a0a1ac53')).toBe(path.join(root, 'anims', '038fcec9-5ebd-8a8e-0e2e-6e71a0a1ac53'));
    expect(resolveAppFile(root, 'linkpoint://app/sub%20dir/a%20b.png')).toBe(path.join(root, 'sub dir', 'a b.png'));
  });

  it('never resolves outside dist: URL parsing clamps dot segments, and encoded escapes are refused', () => {
    // plain ../ segments are collapsed by URL parsing at the scheme's root, so they stay inside dist
    for (const url of ['linkpoint://app/../secret', 'linkpoint://app/%2e%2e/secret', 'linkpoint://app/a/../../b']) {
      const file = resolveAppFile(root, url);
      expect(file === null || file.startsWith(root + path.sep), url).toBe(true);
    }
    // an encoded slash survives URL parsing and would escape after decoding: refused
    for (const url of ['linkpoint://app/..%2f..%2fetc/passwd', 'linkpoint://app/%2e%2e%2f%2e%2e%2fetc/passwd', 'linkpoint://app/a%2f..%2f..%2f..%2fb']) {
      expect(resolveAppFile(root, url), url).toBeNull();
    }
  });

  it('refuses URLs that are not for this app, or are malformed', () => {
    for (const url of ['linkpoint://app/%00', 'linkpoint://other/index.html', 'https://app/index.html', 'file:///etc/passwd', 'not a url', 'linkpoint://app/%E0%A4%A']) {
      expect(resolveAppFile(root, url), url).toBeNull();
    }
  });

  it('serves resolved files through the net module and answers 404 for refused or missing ones', async () => {
    let handler: (request: { url: string }) => Promise<Response>;
    const protocol = { handle: vi.fn((scheme: string, fn: typeof handler) => { handler = fn; }) };
    const net = { fetch: vi.fn(async (url: string) => (url.includes('missing') ? Promise.reject(new Error('ENOENT')) : new Response(`served ${url}`))) };
    registerAppProtocol({ protocol, net }, root);
    expect(protocol.handle).toHaveBeenCalledWith(APP_SCHEME, expect.any(Function));
    const ok = await handler!({ url: 'linkpoint://app/avatar/meshes.json' });
    expect(ok.status).toBe(200);
    expect(await ok.text()).toContain('avatar/meshes.json');
    expect((await handler!({ url: 'linkpoint://app/..%2fx' })).status).toBe(404);
    expect((await handler!({ url: 'linkpoint://app/missing.png' })).status).toBe(404);
    expect(net.fetch).toHaveBeenCalledTimes(2);
  });
});
