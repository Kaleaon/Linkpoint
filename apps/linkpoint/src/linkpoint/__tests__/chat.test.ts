import { describe, expect, it, vi, beforeEach } from 'vitest';
import { ChatManager } from '../chat';
import { Utils } from '../utils';

describe('ChatManager', () => {
  beforeEach(() => {
    Utils.storage.remove('linkpoint_chat_history');
    Utils.storage.remove('linkpoint_auto_reply_config');
  });

  it('rejects sends while disconnected', async () => {
    const manager = new ChatManager({ sendChat: vi.fn() }, { isLoggedIn: () => false });
    await expect(manager.sendMessage('hello')).rejects.toThrow('Not connected');
  });

  it('sends through the protocol before adding the local transcript entry', async () => {
    const sendChat = vi.fn().mockResolvedValue(undefined);
    const manager = new ChatManager(
      { sendChat },
      { isLoggedIn: () => true, getUserDisplayName: () => 'Test Resident', user: { id: 'agent-id' } },
    );

    await manager.sendMessage('hello', 0, 1);

    expect(sendChat).toHaveBeenCalledWith('hello', 0, 1);
    expect(manager.messages).toHaveLength(1);
    expect(manager.messages[0]).toMatchObject({ sender: 'Test Resident', senderId: 'agent-id', text: 'hello', type: 'local' });
  });

  it('does not add a message when the protocol rejects it', async () => {
    const manager = new ChatManager(
      { sendChat: vi.fn().mockRejectedValue(new Error('transport unavailable')) },
      { isLoggedIn: () => true },
    );

    await expect(manager.sendMessage('hello')).rejects.toThrow('transport unavailable');
    expect(manager.messages).toHaveLength(0);
  });

  it('sends instant messages to the selected resident UUID', async () => {
    const sendInstantMessage = vi.fn().mockResolvedValue(undefined);
    const manager = new ChatManager(
      { sendInstantMessage },
      { isLoggedIn: () => true, getUserDisplayName: () => 'Test Resident', user: { id: 'agent-id' } },
    );

    await manager.sendInstantMessage('friend-id', 'private hello', 'Friend Resident');

    expect(sendInstantMessage).toHaveBeenCalledWith('friend-id', 'private hello');
    expect(manager.getIMMessages('friend-id')).toEqual([
      expect.objectContaining({ recipientId: 'friend-id', recipientName: 'Friend Resident', text: 'private hello' }),
    ]);
  });

  it('sends group messages through the group channel and records the group', async () => {
    const sendGroupMessage = vi.fn().mockResolvedValue(undefined);
    const sendChat = vi.fn();
    const manager = new ChatManager(
      { sendGroupMessage, sendChat },
      { isLoggedIn: () => true, getUserDisplayName: () => 'Test Resident', user: { id: 'agent-id' } },
    );

    await manager.sendGroupMessage('group-id', 'hello group', 'Builders');

    expect(sendGroupMessage).toHaveBeenCalledWith('group-id', 'hello group');
    expect(sendChat).not.toHaveBeenCalled();
    expect(manager.messages[0]).toMatchObject({ type: 'group', groupId: 'group-id', groupName: 'Builders', text: 'hello group' });
  });

  it('never speaks a group message in local chat when group chat is unavailable', async () => {
    const sendChat = vi.fn().mockResolvedValue(undefined);
    const manager = new ChatManager({ sendChat }, { isLoggedIn: () => true });

    await expect(manager.sendGroupMessage('group-id', 'secret')).rejects.toThrow('Group chat is unavailable');
    expect(sendChat).not.toHaveBeenCalled();
    expect(manager.messages).toHaveLength(0);
  });

  it('allows users to configure away status and set custom away message', () => {
    const manager = new ChatManager({}, { isLoggedIn: () => true });
    expect(manager.isAutoReplyEnabled()).toBe(false);

    manager.setAutoReplyEnabled(true);
    expect(manager.isAutoReplyEnabled()).toBe(true);

    manager.setAwayMessage('AFK building a prim castle, back at 2 PM SLT.');
    expect(manager.getAwayMessage()).toBe('AFK building a prim castle, back at 2 PM SLT.');

    const saved = Utils.storage.get('linkpoint_auto_reply_config');
    expect(saved).toEqual({
      enabled: true,
      awayMessage: 'AFK building a prim castle, back at 2 PM SLT.',
    });
  });

  it('triggers an auto-reply for incoming IMs when enabled', async () => {
    const sendChat = vi.fn().mockResolvedValue(undefined);
    const manager = new ChatManager(
      { sendChat },
      { isLoggedIn: () => true, user: { id: 'my-agent-id', fullName: 'My Avatar' } },
    );

    manager.setAutoReplyEnabled(true);
    manager.setAwayMessage('At the beach club, send notecard.');

    const autoReplySentListener = vi.fn();
    manager.on('auto_reply_sent', autoReplySentListener);

    await manager.handleIncomingMessage({
      type: 'im',
      fromId: 'friend-uuid-1',
      fromName: 'Steller Sunshine',
      message: 'Hey, are you free to chat?',
    });

    // Verify protocol call was made with auto-response
    expect(sendChat).toHaveBeenCalledWith('[Auto-Response] At the beach club, send notecard.', 0, 4);

    // Verify auto-response message was added to chat history
    const autoReplyMsg = manager.messages.find((m) => m.isAutoReply);
    expect(autoReplyMsg).toBeDefined();
    expect(autoReplyMsg?.recipientId).toBe('friend-uuid-1');
    expect(autoReplyMsg?.text).toBe('[Auto-Response] At the beach club, send notecard.');
    expect(autoReplySentListener).toHaveBeenCalledWith(expect.objectContaining({
      recipientId: 'friend-uuid-1',
      text: '[Auto-Response] At the beach club, send notecard.',
    }));
  });

  it('does not send duplicate auto-replies to the same resident in the same session', async () => {
    const sendChat = vi.fn().mockResolvedValue(undefined);
    const manager = new ChatManager(
      { sendChat },
      { isLoggedIn: () => true, user: { id: 'my-agent-id', fullName: 'My Avatar' } },
    );

    manager.setAutoReplyEnabled(true);
    manager.setAwayMessage('Busy right now.');

    manager.handleIncomingMessage({
      type: 'im',
      fromId: 'resident-uuid',
      fromName: 'Bob Resident',
      message: 'First ping',
    });

    manager.handleIncomingMessage({
      type: 'im',
      fromId: 'resident-uuid',
      fromName: 'Bob Resident',
      message: 'Second ping right away',
    });

    expect(sendChat).toHaveBeenCalledTimes(1);
    expect(manager.hasAutoRepliedTo('resident-uuid')).toBe(true);
  });

  it('does not trigger auto-reply when auto-reply is disabled or for local chat', async () => {
    const sendChat = vi.fn().mockResolvedValue(undefined);
    const manager = new ChatManager(
      { sendChat },
      { isLoggedIn: () => true, user: { id: 'my-agent-id', fullName: 'My Avatar' } },
    );

    manager.setAutoReplyEnabled(false);

    manager.handleIncomingMessage({
      type: 'im',
      fromId: 'sender-1',
      fromName: 'Other Resident',
      message: 'Hello',
    });

    manager.setAutoReplyEnabled(true);
    manager.handleIncomingMessage({
      type: 'local',
      fromId: 'sender-2',
      fromName: 'Nearby Resident',
      message: 'Local chat message',
    });

    expect(sendChat).not.toHaveBeenCalled();
    expect(manager.messages.some((m) => m.isAutoReply)).toBe(false);
  });

  it('does not auto-reply to messages from own avatar', async () => {
    const sendChat = vi.fn().mockResolvedValue(undefined);
    const manager = new ChatManager(
      { sendChat },
      { isLoggedIn: () => true, user: { id: 'my-agent-id', fullName: 'My Avatar' } },
    );

    manager.setAutoReplyEnabled(true);

    manager.handleIncomingMessage({
      type: 'im',
      fromId: 'my-agent-id',
      fromName: 'My Avatar',
      message: 'Echo from another device',
    });

    expect(sendChat).not.toHaveBeenCalled();
  });
});
