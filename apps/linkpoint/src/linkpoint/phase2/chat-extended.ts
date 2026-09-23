/**
 * Linkpoint PWA - Enhanced Chat (Features 36-40)
 *
 * Phase 2: Core Protocol Extensions - Priority 3
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 76-81)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/chatsrc/
 *
 * Extends chat functionality with history, filtering, mute list, range, and typing indicators.
 */

export class ChatExtended {
  private protocol: any;
  private chatHistory: any[] = [];
  private maxHistorySize: number = 1000;
  private filters: Map<string, Function> = new Map();
  private muteList: Set<string> = new Set();
  private typingUsers: Map<string, number> = new Map();
  private typingTimeout: number = 5000; // milliseconds

  constructor(protocolManager?: any) {
    this.protocol = protocolManager;
  }

  /**
   * Feature 36: Chat history persistence
   * Add message to chat history
   */
  addToHistory(message: any) {
    if (!message || typeof message !== 'object') {
      throw new Error('Valid message object required');
    }

    const chatMsg = {
      id: message.id || `msg-${Date.now()}`,
      from: message.from || 'Unknown',
      text: message.text || '',
      type: message.type || 'local', // local, whisper, shout, system
      channel: message.channel || 0,
      timestamp: message.timestamp || Date.now(),
      ...message
    };

    this.chatHistory.push(chatMsg);

    // Trim history if exceeds max size
    if (this.chatHistory.length > this.maxHistorySize) {
      this.chatHistory.shift();
    }

    console.log(`[ChatExtended] Added to history: ${chatMsg.from}: ${chatMsg.text.substring(0, 50)}`);
  }

  /**
   * Get chat history
   */
  getHistory(limit: number = 100) {
    return this.chatHistory.slice(-limit);
  }

  /**
   * Feature 37: Chat filtering
   * Add chat filter rule
   */
  addFilter(filterName: string, filterFn: Function) {
    if (!filterName || typeof filterFn !== 'function') {
      throw new Error('Valid filter name and function required');
    }

    this.filters.set(filterName, filterFn);
    console.log(`[ChatExtended] Added filter: ${filterName}`);
  }

  /**
   * Remove filter
   */
  removeFilter(filterName: string) {
    if (this.filters.delete(filterName)) {
      console.log(`[ChatExtended] Removed filter: ${filterName}`);
    }
  }

  /**
   * Apply filters to message
   */
  applyFilters(message: any): boolean {
    for (const [name, filterFn] of this.filters) {
      try {
        if (!filterFn(message)) {
          console.log(`[ChatExtended] Message blocked by filter: ${name}`);
          return false;
        }
      } catch (error) {
        console.error(`[ChatExtended] Filter error (${name}):`, error);
      }
    }
    return true;
  }

  /**
   * Feature 38: Mute list
   * Add user to mute list
   */
  muteUser(userId: string) {
    if (!userId || typeof userId !== 'string') {
      throw new Error('Valid user ID required');
    }

    this.muteList.add(userId);
    console.log(`[ChatExtended] Muted user: ${userId}`);
  }

  /**
   * Remove user from mute list
   */
  unmuteUser(userId: string) {
    if (this.muteList.delete(userId)) {
      console.log(`[ChatExtended] Unmuted user: ${userId}`);
    }
  }

  /**
   * Check if user is muted
   */
  isUserMuted(userId: string): boolean {
    return this.muteList.has(userId);
  }

  getMutedUsers() {
    return Array.from(this.muteList);
  }

  /**
   * Feature 39: Chat range (whisper/shout)
   * Send message with specific range
   */
  async sendWithRange(text: string, range: string = 'normal') {
    if (!text || typeof text !== 'string') {
      throw new Error('Valid message text required');
    }

    const validRanges = ['whisper', 'normal', 'shout'];
    if (!validRanges.includes(range)) {
      throw new Error(`Invalid range. Must be one of: ${validRanges.join(', ')}`);
    }

    const message = {
      text: text,
      range: range,
      channel: range === 'whisper' ? 1 : range === 'shout' ? 2 : 0,
      timestamp: Date.now()
    };

    console.log(`[ChatExtended] Sending ${range} message: ${text}`);
    if (this.protocol && typeof this.protocol.sendChat === 'function') {
      try {
        await this.protocol.sendChat(text, message.channel, 1);
      } catch (error) {
        console.error('[ChatExtended] Failed to send message:', error);
        throw error;
      }
    } else {
      console.warn('[ChatExtended] Protocol handler not available');
    }

    return Promise.resolve(message);
  }

  /**
   * Feature 40: Typing indicators
   * Update typing indicator for user
   */
  setUserTyping(userId: string, isTyping: boolean = true) {
    if (!userId || typeof userId !== 'string') {
      throw new Error('Valid user ID required');
    }

    if (isTyping) {
      this.typingUsers.set(userId, Date.now());
      console.log(`[ChatExtended] User typing: ${userId}`);

      // Auto-clear after timeout
      setTimeout(() => {
        const lastUpdate = this.typingUsers.get(userId);
        if (lastUpdate && Date.now() - lastUpdate >= this.typingTimeout) {
          this.typingUsers.delete(userId);
          console.log(`[ChatExtended] User stopped typing (timeout): ${userId}`);
        }
      }, this.typingTimeout);
    } else {
      this.typingUsers.delete(userId);
      console.log(`[ChatExtended] User stopped typing: ${userId}`);
    }
  }

  /**
   * Get currently typing users
   */
  getTypingUsers(): string[] {
    // Clean up expired typing indicators
    const now = Date.now();
    for (const [userId, timestamp] of this.typingUsers) {
      if (now - timestamp > this.typingTimeout) {
        this.typingUsers.delete(userId);
      }
    }

    return Array.from(this.typingUsers.keys());
  }

  clearHistory() {
    this.chatHistory = [];
  }

  getStats() {
    return {
      historySize: this.chatHistory.length,
      maxHistorySize: this.maxHistorySize,
      activeFilters: this.filters.size,
      mutedUsers: this.muteList.size,
      typingUsers: this.typingUsers.size
    };
  }
}
