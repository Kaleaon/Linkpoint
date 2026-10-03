import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { LiveRegionAnnouncerService } from '../../services/LiveRegionAnnouncer';
import { Utils } from '../utils';
import { contrastRatio, meetsAA } from '../../theme/contrast';

class MockChatManager extends Utils.EventEmitter {
  public messages: any[] = [];
}

describe('LiveRegionAnnouncerService & Theme Contrast Tests', () => {
  let announcer: LiveRegionAnnouncerService;
  let mockChat: MockChatManager;
  let mockDomElement: HTMLElement;

  beforeEach(() => {
    announcer = new LiveRegionAnnouncerService();
    announcer.setThrottleMs(10); // Fast throttle for tests
    mockChat = new MockChatManager();

    mockDomElement = document.createElement('div');
    mockDomElement.id = 'live-region-announcer';
    mockDomElement.className = 'sr-only live-region-announcer';
    document.body.appendChild(mockDomElement);

    announcer.registerDOMElement(mockDomElement);
  });

  afterEach(() => {
    announcer.destroy();
    if (mockDomElement && mockDomElement.parentNode) {
      mockDomElement.parentNode.removeChild(mockDomElement);
    }
    vi.restoreAllMocks();
  });

  describe('ARIA Attributes & Registration', () => {
    it('sets role="log", aria-live="polite", and aria-atomic="false" on registered DOM element', () => {
      expect(mockDomElement.getAttribute('role')).toBe('log');
      expect(mockDomElement.getAttribute('aria-live')).toBe('polite');
      expect(mockDomElement.getAttribute('aria-atomic')).toBe('false');
      expect(mockDomElement.classList.contains('sr-only')).toBe(true);
    });
  });

  describe('Message Formatting', () => {
    it('formats spatial/local chat messages correctly', () => {
      const data = { type: 'local', sender: 'Avatar Alpha', text: 'Hello region!' };
      const formatted = announcer.formatChatMessage(data);
      expect(formatted).toBe('[Spatial Chat] Avatar Alpha: Hello region!');
    });

    it('formats group chat messages correctly', () => {
      const data = { type: 'group', groupName: 'Builders Club', sender: 'Bob', text: 'Meeting at 5' };
      const formatted = announcer.formatChatMessage(data);
      expect(formatted).toBe('[Group Chat - Builders Club] Bob: Meeting at 5');
    });

    it('formats instant messages correctly', () => {
      const data = { type: 'im', sender: 'Alice', recipientName: 'Bob', text: 'Secret message' };
      const formatted = announcer.formatChatMessage(data);
      expect(formatted).toBe('[IM - Alice to Bob] Secret message');
    });

    it('returns null for empty or invalid messages', () => {
      expect(announcer.formatChatMessage(null)).toBeNull();
      expect(announcer.formatChatMessage({ text: '  ' })).toBeNull();
    });
  });

  describe('Event Bus Subscription & Queueing', () => {
    it('subscribes to ChatManager events and updates live region', async () => {
      announcer.attachChatBus(mockChat);

      mockChat.emit('message_received', {
        type: 'local',
        sender: 'TestUser',
        text: 'Live region check',
      });

      // Wait for async queue process
      await new Promise((r) => setTimeout(r, 50));

      expect(mockDomElement.textContent).toBe('[Spatial Chat] TestUser: Live region check');
    });

    it('queues rapid chat messages and flushes them sequentially', async () => {
      announcer.setThrottleMs(50);
      announcer.attachChatBus(mockChat);

      mockChat.emit('message_received', { type: 'local', sender: 'User1', text: 'Message 1' });
      mockChat.emit('message_received', { type: 'local', sender: 'User2', text: 'Message 2' });
      mockChat.emit('message_received', { type: 'local', sender: 'User3', text: 'Message 3' });

      // First message is displayed immediately
      expect(mockDomElement.textContent).toBe('[Spatial Chat] User1: Message 1');
      expect(announcer.getQueueLength()).toBe(2);

      // Wait for throttle to flush queue
      await new Promise((r) => setTimeout(r, 150));

      expect(mockDomElement.textContent).toBe('[Spatial Chat] User3: Message 3');
      expect(announcer.getQueueLength()).toBe(0);
    });
  });

  describe('Focus Stability (ARIA22)', () => {
    it('preserves document.activeElement focus during live region updates', async () => {
      const input = document.createElement('input');
      input.id = '3d-control-input';
      document.body.appendChild(input);
      input.focus();

      expect(document.activeElement).toBe(input);

      announcer.announce('Focus test notification');
      await new Promise((r) => setTimeout(r, 50));

      expect(mockDomElement.textContent).toBe('Focus test notification');
      expect(document.activeElement).toBe(input);

      document.body.removeChild(input);
    });
  });

  describe('Theme Token Contrast Verification (WCAG 1.4.3 Level AA)', () => {
    it('enforces text contrast ratio of at least 4.5:1 for --chat-text (#FFFFFF) on --chat-bg-scrim (#121214)', () => {
      const foreground = '#FFFFFF';
      const background = '#121214';

      const ratio = contrastRatio(foreground, background);
      expect(ratio).toBeGreaterThanOrEqual(4.5);
      expect(meetsAA(foreground, background)).toBe(true);
      expect(ratio).toBeGreaterThan(15); // Pure white on dark scrim is ~18:1
    });

    it('confirms contrast ratios for chat overlay background colors satisfy WCAG AA', () => {
      const text = '#FFFFFF';
      const darkScrim = '#121214';
      const translucentScrim = 'rgba(18, 18, 20, 0.92)';

      expect(meetsAA(text, darkScrim)).toBe(true);
      expect(meetsAA(text, translucentScrim)).toBe(true);
    });
  });
});
