import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

const tokens: Record<string, string | null> = { contacts: 'tok-c', calendar: null };
const dropped: string[] = [];
vi.mock('../googleAuth', () => ({
  getGoogleToken: (feature: string) => tokens[feature],
  dropGoogleToken: (feature: string) => { dropped.push(feature); tokens[feature] = null; },
}));

import { GoogleApiError, googleFetch } from '../googleApi';

const response = (status: number, body: any = {}) => new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } });

beforeEach(() => { tokens.contacts = 'tok-c'; tokens.calendar = null; dropped.length = 0; });
afterEach(() => vi.unstubAllGlobals());

describe('googleFetch', () => {
  it('sends the feature\'s own token and a JSON content type only when there is a body', async () => {
    const fetchMock = vi.fn().mockResolvedValue(response(200, { ok: true }));
    vi.stubGlobal('fetch', fetchMock);
    await googleFetch('contacts', 'https://example.test/a');
    await googleFetch('contacts', 'https://example.test/b', { method: 'POST', body: '{}' });
    expect(fetchMock.mock.calls[0][1].headers.Authorization).toBe('Bearer tok-c');
    expect(fetchMock.mock.calls[0][1].headers['Content-Type']).toBeUndefined();
    expect(fetchMock.mock.calls[1][1].headers['Content-Type']).toBe('application/json');
  });

  it('asks the user to sign in, without calling Google, when there is no token', async () => {
    const fetchMock = vi.fn();
    vi.stubGlobal('fetch', fetchMock);
    const error: GoogleApiError = await googleFetch('calendar', 'https://example.test').catch((e) => e);
    expect(error).toBeInstanceOf(GoogleApiError);
    expect(error.signInRequired).toBe(true);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it('drops an expired token and asks for sign-in again', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response(401)));
    const error: GoogleApiError = await googleFetch('contacts', 'https://example.test').catch((e) => e);
    expect(error.signInRequired).toBe(true);
    expect(error.message).toMatch(/expired/);
    expect(dropped).toEqual(['contacts']);
  });

  it('reports Google\'s own message for refusals and other failures', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValueOnce(response(403, { error: { message: 'People API has not been used in project' } })).mockResolvedValueOnce(response(500, 'not json')).mockResolvedValueOnce(response(404, { error: { message: 'Not Found' } })));
    await expect(googleFetch('contacts', 'x')).rejects.toThrow(/Google refused: People API has not been used/);
    await expect(googleFetch('contacts', 'x')).rejects.toThrow(/Google answered 500/);
    const notFound: GoogleApiError = await googleFetch('contacts', 'x').catch((e) => e);
    expect(notFound.status).toBe(404);
    expect(notFound.signInRequired).toBe(false);
  });
});
