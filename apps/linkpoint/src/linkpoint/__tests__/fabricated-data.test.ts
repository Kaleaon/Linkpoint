import { describe, expect, it } from 'vitest';
import { ChatManager } from '../chat';
import { isFabricatedContact, purgeFabricatedMessages, purgeFabricatedStorage, STORAGE_KEYS } from '../fabricated-data';

const memory = (initial: Record<string, any> = {}) => {
  const data: Record<string, any> = { ...initial };
  return { data, get: (k: string, f: any) => (k in data ? data[k] : f), set: (k: string, v: any) => { data[k] = v; } };
};

const seeded = [
  { id: 'seed-local-1', sender: 'Nyx Vaher', senderId: 'nyx-uuid', text: 'x' },
  { id: 'seed-im-kit-1', sender: 'Kit Sandalwood', senderId: 'kit-uuid', recipientId: 'ruth-uuid', text: 'x' },
  { id: 'abc', sender: 'Nyx Vaher', senderId: '5f1b6c5e-aaaa-bbbb-cccc-000000000001', text: 'a real resident who shares a name' },
  { id: 'def', sender: 'Me', senderId: 'real-id', text: 'mine' },
];

describe('purging fabricated stored data', () => {
  it('removes seeded messages by id and placeholder contact, never by display name', () => {
    const { messages, removed } = purgeFabricatedMessages(seeded);
    expect(removed).toBe(2);
    expect(messages.map((m) => m.id)).toEqual(['abc', 'def']);
    expect(isFabricatedContact('nyx-uuid')).toBe(true);
    expect(isFabricatedContact('Nyx Vaher')).toBe(false);
    expect(isFabricatedContact(undefined)).toBe(false);
  });

  it('cleans history and IM sessions in storage and reports what it removed', () => {
    const store = memory({
      [STORAGE_KEYS.chatHistory]: seeded,
      [STORAGE_KEYS.openSessions]: [
        { contactId: 'nyx-uuid', contactName: 'Nyx Vaher', openedAt: 1 },
        { contactId: 'real-uuid', contactName: 'Someone Real', openedAt: 2 },
      ],
    });
    const report = purgeFabricatedStorage(store);
    expect(report).toEqual({ messagesRemoved: 2, sessionsRemoved: 1 });
    expect(store.data[STORAGE_KEYS.chatHistory]).toHaveLength(2);
    expect(store.data[STORAGE_KEYS.openSessions]).toEqual([{ contactId: 'real-uuid', contactName: 'Someone Real', openedAt: 2 }]);
  });

  it('writes nothing when there is nothing to remove, and tolerates missing or broken storage', () => {
    let writes = 0;
    const clean = { get: () => [{ id: 'x', senderId: 'real' }], set: () => { writes++; } };
    expect(purgeFabricatedStorage(clean)).toEqual({ messagesRemoved: 0, sessionsRemoved: 0 });
    expect(writes).toBe(0);
    expect(purgeFabricatedStorage({ get: () => null, set: () => undefined })).toEqual({ messagesRemoved: 0, sessionsRemoved: 0 });
    expect(purgeFabricatedStorage({ get: () => { throw new Error('denied'); }, set: () => undefined })).toEqual({ messagesRemoved: 0, sessionsRemoved: 0 });
  });
});

describe('ChatManager no longer invents conversations', () => {
  it('starts empty when there is no saved history', () => {
    localStorage.clear();
    const chat = new ChatManager({ on: () => undefined } as any, { user: { id: 'me', fullName: 'Me Resident' }, getUserDisplayName: () => 'Me Resident' } as any);
    chat.init();
    expect(chat.messages).toEqual([]);
    expect(chat.openSessions.size).toBe(0);
    expect((chat as any).seedInitialMessages).toBeUndefined();
  });

  it('drops previously saved fabricated messages on load and rewrites clean history', () => {
    localStorage.clear();
    const store = memory({ [STORAGE_KEYS.chatHistory]: seeded });
    localStorage.setItem('linkpoint_chat_history', JSON.stringify(seeded));
    const chat = new ChatManager({ on: () => undefined } as any, { user: { id: 'me' }, getUserDisplayName: () => 'Me' } as any);
    chat.init();
    expect(chat.messages.map((m: any) => m.id)).toEqual(['abc', 'def']);
    void store;
  });
});
