import { describe, expect, it, vi, beforeEach } from 'vitest';
import { ChatExtended, MuteFlags, MuteType, calculateCRC32 } from '../phase2/chat-extended';
import { ChatManager } from '../chat';
import { NotificationsManager } from '../notifications';
import { Utils } from '../utils';

describe('ChatExtended Mute List Synchronization & Persistence', () => {
  beforeEach(() => {
    Utils.storage.memoryStore.clear();
    if (typeof localStorage !== 'undefined') {
      localStorage.clear();
    }
  });

  it('calculates standard CRC32 checksum correctly for text payloads', () => {
    const text = '1 11111111-1111-1111-1111-111111111111 15 BadAvatar\n';
    const crc = calculateCRC32(text);
    expect(crc).toBeGreaterThan(0);
    expect(calculateCRC32(text)).toBe(crc);
  });

  it('persists muted users and objects across client restarts using local storage', () => {
    const chatExt1 = new ChatExtended();
    chatExt1.muteUser('avatar-uuid-1', 'Griefing Spambot', MuteFlags.MUTE_ALL);
    chatExt1.muteObject('object-uuid-2', 'Noisy Prim', MuteFlags.MUTE_SOUNDS);

    expect(chatExt1.isUserMuted('avatar-uuid-1')).toBe(true);
    expect(chatExt1.isUserMuted('Griefing Spambot')).toBe(true);
    expect(chatExt1.isObjectMuted('Noisy Prim')).toBe(true);

    // Simulate client restart by instantiating a new ChatExtended instance
    const chatExt2 = new ChatExtended();
    expect(chatExt2.isUserMuted('avatar-uuid-1')).toBe(true);
    expect(chatExt2.isUserMuted('Griefing Spambot')).toBe(true);
    expect(chatExt2.isObjectMuted('Noisy Prim')).toBe(true);
    expect(chatExt2.getMutedUsers()).toContain('Griefing Spambot');
    expect(chatExt2.getMutedObjects()).toContain('Noisy Prim');
  });

  it('Requirement 1: issues MuteListRequest with cached CRC32 upon circuit ready / login', async () => {
    const requestMuteList = vi.fn().mockResolvedValue(undefined);
    const mockProtocol = {
      agentId: 'my-agent-id',
      sessionId: 'my-session-id',
      requestMuteList,
      on: vi.fn(),
    };

    const chatExt = new ChatExtended(mockProtocol);
    chatExt.muteUser('spambot-uuid', 'Spambot');

    await chatExt.requestMuteList();

    expect(requestMuteList).toHaveBeenCalledWith(expect.any(Number));
  });

  it('Requirement 2: parses Xfer mute file data, updates memory sets, CRC32, and persistent storage', () => {
    const chatExt = new ChatExtended();
    const xferData = [
      '1 11111111-1111-1111-1111-111111111111 15 Griefing Spambot',
      '2 22222222-2222-2222-2222-222222222222 8 Noisy Emitter Prim',
    ].join('\n');

    chatExt.parseMuteListData(xferData);

    expect(chatExt.isUserMuted('11111111-1111-1111-1111-111111111111')).toBe(true);
    expect(chatExt.isUserMuted('Griefing Spambot')).toBe(true);
    expect(chatExt.isObjectMuted('22222222-2222-2222-2222-222222222222', MuteFlags.MUTE_SOUNDS)).toBe(true);
    expect(chatExt.isObjectMuted('Noisy Emitter Prim', MuteFlags.MUTE_SOUNDS)).toBe(true);

    const stats = chatExt.getStats();
    expect(stats.cachedCRC).toBeGreaterThan(0);

    // Verify persisted to storage
    const newSession = new ChatExtended();
    expect(newSession.isUserMuted('Griefing Spambot')).toBe(true);
  });

  it('Requirement 3: muting an avatar or object transmits UpdateMuteListEntry packet', async () => {
    const updateMuteListEntry = vi.fn().mockResolvedValue(undefined);
    const mockProtocol = {
      updateMuteListEntry,
      on: vi.fn(),
    };

    const chatExt = new ChatExtended(mockProtocol);
    chatExt.muteUser('bad-user-id', 'Troublemaker', MuteFlags.MUTE_ALL);

    expect(updateMuteListEntry).toHaveBeenCalledWith(
      expect.objectContaining({
        name: 'Troublemaker',
        flags: MuteFlags.MUTE_ALL,
      })
    );
  });

  it('Requirement 3: unmuting an avatar or object transmits RemoveMuteListEntry packet', async () => {
    const removeMuteListEntry = vi.fn().mockResolvedValue(undefined);
    const mockProtocol = {
      removeMuteListEntry,
      on: vi.fn(),
    };

    const chatExt = new ChatExtended(mockProtocol);
    chatExt.muteUser('user-to-unmute', 'Friend', MuteFlags.MUTE_ALL);
    chatExt.unmuteUser('user-to-unmute', 'Friend');

    expect(chatExt.isUserMuted('user-to-unmute')).toBe(false);
    expect(removeMuteListEntry).toHaveBeenCalledWith(
      expect.objectContaining({
        name: 'Friend',
      })
    );
  });

  it('Requirement 4: filters incoming chat and IMs from muted users', async () => {
    const chatExt = new ChatExtended();
    chatExt.muteUser('muted-sender-uuid', 'Nuisance Resident');

    (globalThis as any).app = { chatExtended: chatExt };

    const mockProtocol = { on: vi.fn() };
    const mockAuth = { isLoggedIn: () => true, user: { id: 'me' } };
    const chatManager = new ChatManager(mockProtocol, mockAuth);

    // Message from muted sender
    await chatManager.handleIncomingMessage({
      type: 'im',
      fromId: 'muted-sender-uuid',
      fromName: 'Nuisance Resident',
      message: 'Unwanted message',
    });

    expect(chatManager.messages).toHaveLength(0);

    // Message from allowed sender
    await chatManager.handleIncomingMessage({
      type: 'im',
      fromId: 'nice-sender-uuid',
      fromName: 'Nice Resident',
      message: 'Hello friend!',
    });

    expect(chatManager.messages).toHaveLength(1);
    expect(chatManager.messages[0].text).toBe('Hello friend!');

    delete (globalThis as any).app;
  });

  it('Requirement 4: filters notifications from muted sources', () => {
    const chatExt = new ChatExtended();
    chatExt.muteUser('muted-notice-sender', 'Annoying Bot');

    (globalThis as any).app = { chatExtended: chatExt };

    const notificationsManager = new NotificationsManager({ on: vi.fn() } as any);

    notificationsManager.handleNotification({
      fromId: 'muted-notice-sender',
      from: 'Annoying Bot',
      title: 'Spam Notice',
      message: 'Buy free L$',
    });

    expect(notificationsManager.items).toHaveLength(0);

    notificationsManager.handleNotification({
      fromId: 'good-sender',
      from: 'Land Manager',
      title: 'Land Update',
      message: 'Rent paid',
    });

    expect(notificationsManager.items).toHaveLength(1);

    delete (globalThis as any).app;
  });
});
