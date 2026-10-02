import { createRequire } from 'node:module';
import { describe, expect, it, vi } from 'vitest';

const require = createRequire(import.meta.url);
const { profileUsername, profilePhotoUrl, fetchProfilePhoto, MAX_BYTES } = require('../../../core/sl-profile-photo.cjs');

const reply = (status: number, body: Uint8Array | string = '', type = 'image/png') => ({
  status, ok: status >= 200 && status < 300,
  headers: { get: (key: string) => (key.toLowerCase() === 'content-type' ? type : null) },
  arrayBuffer: async () => (typeof body === 'string' ? new TextEncoder().encode(body) : body).buffer,
});

describe('profile usernames', () => {
  it('maps resident names to web usernames', () => {
    expect(profileUsername('Philip Linden')).toBe('philip.linden');
    expect(profileUsername('  Pat   Resident ')).toBe('pat');
    expect(profileUsername('pat.example')).toBe('pat.example');
    expect(profileUsername('Resident')).toBe('resident'); // a lone word is kept
  });

  it('rejects anything that could change the URL', () => {
    for (const bad of ['', '   ', '../etc', 'a/b', 'a?b', 'a#b', 'a b%2f', 'ünï cödé', '.hidden', 'x'.repeat(80), null, undefined, 5]) {
      expect(profileUsername(bad as any)).toBeNull();
    }
  });

  it('builds thumbnail and full-size URLs', () => {
    expect(profilePhotoUrl('Philip Linden')).toBe('https://my-secondlife.s3.amazonaws.com/users/philip.linden/thumb_sl_image.png');
    expect(profilePhotoUrl('Philip Linden', false)).toBe('https://my-secondlife.s3.amazonaws.com/users/philip.linden/sl_image.png');
    expect(profilePhotoUrl('../x')).toBeNull();
  });
});

describe('fetchProfilePhoto', () => {
  it('returns the picture as base64 with its content type', async () => {
    const fetchImpl = vi.fn().mockResolvedValue(reply(200, new Uint8Array([1, 2, 3, 4])));
    const result = await fetchProfilePhoto('Philip Linden', { fetchImpl });
    expect(result).toEqual({ contentType: 'image/png', base64: Buffer.from([1, 2, 3, 4]).toString('base64') });
    expect(fetchImpl.mock.calls[0][0]).toContain('/philip.linden/thumb_sl_image.png');
    expect(fetchImpl.mock.calls[0][1].redirect).toBe('error');
  });

  it('reports a resident with no public picture as null, not an error', async () => {
    for (const status of [403, 404]) {
      expect(await fetchProfilePhoto('Nobody Here', { fetchImpl: vi.fn().mockResolvedValue(reply(status)) })).toBeNull();
    }
  });

  it('does not accept a non-image or empty response as a picture', async () => {
    expect(await fetchProfilePhoto('a b', { fetchImpl: vi.fn().mockResolvedValue(reply(200, '<html>', 'text/html')) })).toBeNull();
    expect(await fetchProfilePhoto('a b', { fetchImpl: vi.fn().mockResolvedValue(reply(200, new Uint8Array(0))) })).toBeNull();
  });

  it('refuses oversized responses, bad names and server errors', async () => {
    await expect(fetchProfilePhoto('a b', { fetchImpl: vi.fn().mockResolvedValue(reply(200, new Uint8Array(MAX_BYTES + 1))) })).rejects.toThrow(/too large/);
    await expect(fetchProfilePhoto('../x', { fetchImpl: vi.fn() })).rejects.toThrow(/valid resident name/);
    await expect(fetchProfilePhoto('a b', { fetchImpl: vi.fn().mockResolvedValue(reply(500)) })).rejects.toThrow(/500/);
  });
});
