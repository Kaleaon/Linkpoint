import { describe, expect, it, vi } from 'vitest';
import { ChatManager } from '../chat';

describe('ChatManager RLV Interception & Suppression', () => {
  it('registers rlvHandler and routes @ commands to processCommand', async () => {
    const manager = new ChatManager({ sendChat: vi.fn() }, { isLoggedIn: () => true });

    const mockRlvHandler = {
      enabled: true,
      processCommand: vi.fn(),
    };

    manager.setRlvHandler(mockRlvHandler);

    await manager.handleIncomingMessage({
      type: 'local',
      fromId: 'object-123',
      fromName: 'Restraint Device',
      message: '@detach=n',
      ownerId: 'my-agent-id',
    });

    expect(mockRlvHandler.processCommand).toHaveBeenCalledWith('@detach=n', 'object-123', true, 0);
    // Suppressed from chat history
    expect(manager.messages).toHaveLength(0);
  });

  it('allows normal spatial chat messages through to transcript', async () => {
    const manager = new ChatManager({ sendChat: vi.fn() }, { isLoggedIn: () => true });

    const mockRlvHandler = {
      enabled: true,
      processCommand: vi.fn(),
    };

    manager.setRlvHandler(mockRlvHandler);

    await manager.handleIncomingMessage({
      type: 'local',
      fromId: 'object-123',
      fromName: 'Restraint Device',
      message: 'Hello nearby residents!',
      ownerId: 'my-agent-id',
    });

    expect(mockRlvHandler.processCommand).not.toHaveBeenCalled();
    expect(manager.messages).toHaveLength(1);
    expect(manager.messages[0].text).toBe('Hello nearby residents!');
  });
});
