import { beforeEach, describe, expect, it, vi } from 'vitest';

const googleFetch = vi.fn();
vi.mock('../googleApi', () => ({ googleFetch: (...args: any[]) => googleFetch(...args) }));

import { buildPerson, deleteGoogleContact, fetchSlContacts, profileUrl, pushContact } from '../googleContacts';
import type { Contact } from '../../linkpoint/contacts';

const json = (body: any) => ({ json: async () => body });
const contact = (over: Partial<Contact> = {}): Contact => ({ id: 'uuid-1', name: 'Pat Resident', note: '', photo: null, links: [], googleResourceName: null, savedAt: 1, updatedAt: 1, ...over });

beforeEach(() => googleFetch.mockReset());

describe('profileUrl', () => {
  it('builds the web profile address, dropping the default Resident last name', () => {
    expect(profileUrl('Philip Linden')).toBe('https://my.secondlife.com/philip.linden');
    expect(profileUrl('Pat Resident')).toBe('https://my.secondlife.com/pat');
    expect(profileUrl('../x')).toBeNull();
    expect(profileUrl('')).toBeNull();
  });
});

describe('buildPerson', () => {
  it('sends only what the user kept, and invents nothing', () => {
    const person: any = buildPerson(contact(), 'Second Life');
    expect(person.names).toEqual([{ givenName: 'Pat', familyName: 'Resident' }]);
    expect(person.userDefined).toEqual([{ key: 'SL_UUID', value: 'uuid-1' }, { key: 'SL_NAME', value: 'Pat Resident' }, { key: 'SL_GRID', value: 'Second Life' }]);
    expect(person.biographies).toBeUndefined();
    expect(person.organizations).toBeUndefined();
    expect(person.urls).toEqual([{ value: 'https://my.secondlife.com/pat', type: 'profile' }]);
  });

  it('adds the note and links, keeping a Discord username as a plain field', () => {
    const person: any = buildPerson(contact({
      name: 'Madonna', note: ' Met at the market ',
      links: [{ service: 'telegram', label: '@pat_user', url: 'https://t.me/pat_user' }, { service: 'discord', label: 'pat.user', url: null }],
    }), 'Second Life');
    expect(person.names).toEqual([{ givenName: 'Madonna' }]);
    expect(person.biographies).toEqual([{ value: 'Met at the market', contentType: 'TEXT_PLAIN' }]);
    expect(person.urls).toContainEqual({ value: 'https://t.me/pat_user', type: 'Telegram' });
    expect(person.userDefined).toContainEqual({ key: 'Discord', value: 'pat.user' });
  });
});

describe('fetchSlContacts', () => {
  it('follows every page and keeps only contacts made from Second Life', async () => {
    googleFetch
      .mockResolvedValueOnce(json({ connections: [{ resourceName: 'people/c1', userDefined: [{ key: 'SL_UUID', value: 'u1' }, { key: 'SL_NAME', value: 'One' }] }, { resourceName: 'people/c2' }], nextPageToken: 'p2' }))
      .mockResolvedValueOnce(json({ connections: [{ resourceName: 'people/c3', userDefined: [{ key: 'SL_UUID', value: 'u3' }] }, { resourceName: '../evil', userDefined: [{ key: 'SL_UUID', value: 'u4' }] }] }));
    const found = await fetchSlContacts();
    expect([...found.keys()]).toEqual(['u1', 'u3']);
    expect(found.get('u1')).toEqual({ resourceName: 'people/c1', slUuid: 'u1', slName: 'One' });
    expect(googleFetch.mock.calls[1][1]).toContain('pageToken=p2');
  });

  it('returns nothing when there are no connections', async () => {
    googleFetch.mockResolvedValueOnce(json({}));
    expect((await fetchSlContacts()).size).toBe(0);
  });
});

describe('pushContact', () => {
  it('creates a contact, then uploads the user\'s photo', async () => {
    googleFetch.mockResolvedValueOnce(json({ resourceName: 'people/c9' })).mockResolvedValueOnce(json({}));
    const result = await pushContact(contact({ photo: 'data:image/jpeg;base64,AAAA' }), { grid: 'Second Life' });
    expect(result).toEqual({ resourceName: 'people/c9', created: true, photoUploaded: true });
    expect(googleFetch.mock.calls[0][1]).toContain('people:createContact');
    expect(JSON.parse(googleFetch.mock.calls[1][2].body)).toEqual({ photoBytes: 'AAAA' });
    expect(googleFetch.mock.calls[1][1]).toBe('https://people.googleapis.com/v1/people/c9:updateContactPhoto');
  });

  it('does not create a duplicate for a resident already in Google Contacts', async () => {
    const existing = new Map([['uuid-1', { resourceName: 'people/c5', slUuid: 'uuid-1', slName: 'Pat Resident' }]]);
    const result = await pushContact(contact(), { grid: 'Second Life', existing });
    expect(result).toEqual({ resourceName: 'people/c5', created: false, photoUploaded: null });
    expect(googleFetch).not.toHaveBeenCalled();
  });

  it('reports a failed photo upload instead of hiding it, and still counts the contact as created', async () => {
    googleFetch.mockResolvedValueOnce(json({ resourceName: 'people/c9' })).mockRejectedValueOnce(new Error('Photo too large'));
    const result = await pushContact(contact({ photo: 'data:image/jpeg;base64,AAAA' }), { grid: 'Second Life' });
    expect(result).toMatchObject({ created: true, photoUploaded: false, photoError: 'Photo too large' });
  });

  it('sends no photo request when there is no photo, and rejects a suspicious resource name', async () => {
    googleFetch.mockResolvedValueOnce(json({ resourceName: 'people/c9' }));
    expect((await pushContact(contact(), { grid: 'g' })).photoUploaded).toBeNull();
    expect(googleFetch).toHaveBeenCalledTimes(1);
    googleFetch.mockResolvedValueOnce(json({ resourceName: 'people/../../x' }));
    await expect(pushContact(contact(), { grid: 'g' })).rejects.toThrow(/unexpected contact id/);
  });
});

describe('deleteGoogleContact', () => {
  it('deletes by a valid resource name only', async () => {
    googleFetch.mockResolvedValueOnce(json({}));
    await deleteGoogleContact('people/c1');
    expect(googleFetch.mock.calls[0][1]).toBe('https://people.googleapis.com/v1/people/c1:deleteContact');
    await expect(deleteGoogleContact('people/../x')).rejects.toThrow(/not a Google contact id/);
  });
});
