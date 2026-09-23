import { describe, expect, it, vi } from 'vitest';
import { ChatManager } from '../chat';

describe('ChatManager', () => {
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
});
