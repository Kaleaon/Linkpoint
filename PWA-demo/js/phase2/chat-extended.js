/**
 * Linkpoint PWA - Enhanced Chat (Features 36-40)
 * 
 * Phase 2: Core Protocol Extensions - Priority 3
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 76-81)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/chatsrc/
 * 
 * Extends chat functionality with history, filtering, mute list, range, and typing indicators.
 * 
 * Features:
 * - Feature 36: Chat history persistence
 * - Feature 37: Chat filtering
 * - Feature 38: Mute list
 * - Feature 39: Chat range (whisper/shout)
 * - Feature 40: Typing indicators
 * 
 * @module phase2/chat-extended
 */

/**
 * Enhanced Chat Manager
 * Extends basic chat with advanced features
 */
class ChatExtended {
  constructor() {
    this.chatHistory = [];
    this.maxHistorySize = 1000;
    this.filters = new Map();
    this.muteList = new Set();
    this.typingUsers = new Map();
    this.typingTimeout = 5000; // milliseconds
  }

  /**
   * Feature 36: Chat history persistence
   * Add message to chat history
   * 
   * @param {Object} message - Chat message data
   * 
   * TODO: Implement IndexedDB persistence for chat history
   * TODO: Add search functionality across history
   */
  addToHistory(message) {
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
   * @param {number} limit - Maximum messages to return
   * @returns {Array}
   */
  getHistory(limit = 100) {
    return this.chatHistory.slice(-limit);
  }

  /**
   * Feature 37: Chat filtering
   * Add chat filter rule
   * 
   * @param {string} filterName - Filter identifier
   * @param {Function} filterFn - Filter function (message) => boolean
   * 
   * TODO: Add preset filters (profanity, spam, etc.)
   */
  addFilter(filterName, filterFn) {
    if (!filterName || typeof filterFn !== 'function') {
      throw new Error('Valid filter name and function required');
    }
    
    this.filters.set(filterName, filterFn);
    console.log(`[ChatExtended] Added filter: ${filterName}`);
  }

  /**
   * Remove filter
   * @param {string} filterName - Filter identifier
   */
  removeFilter(filterName) {
    if (this.filters.delete(filterName)) {
      console.log(`[ChatExtended] Removed filter: ${filterName}`);
    }
  }

  /**
   * Apply filters to message
   * @param {Object} message - Message to filter
   * @returns {boolean} True if message passes all filters
   */
  applyFilters(message) {
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
   * 
   * @param {string} userId - User UUID to mute
   * 
   * TODO: Implement persistent mute list storage
   * TODO: Add mute expiration/duration support
   */
  muteUser(userId) {
    if (!userId || typeof userId !== 'string') {
      throw new Error('Valid user ID required');
    }
    
    this.muteList.add(userId);
    console.log(`[ChatExtended] Muted user: ${userId}`);
  }

  /**
   * Remove user from mute list
   * @param {string} userId - User UUID to unmute
   */
  unmuteUser(userId) {
    if (this.muteList.delete(userId)) {
      console.log(`[ChatExtended] Unmuted user: ${userId}`);
    }
  }

  /**
   * Check if user is muted
   * @param {string} userId - User UUID
   * @returns {boolean}
   */
  isUserMuted(userId) {
    return this.muteList.has(userId);
  }

  /**
   * Feature 39: Chat range (whisper/shout)
   * Send message with specific range
   * 
   * @param {string} text - Message text
   * @param {string} range - 'whisper', 'normal', or 'shout'
   * @returns {Promise<Object>} Sent message
   * 
   * TODO: Implement range-based chat channels
   * TODO: Add range validation
   */
  async sendWithRange(text, range = 'normal') {
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
    // TODO: Send to protocol handler
    
    return Promise.resolve(message);
  }

  /**
   * Feature 40: Typing indicators
   * Update typing indicator for user
   * 
   * @param {string} userId - User UUID
   * @param {boolean} isTyping - Typing state
   * 
   * TODO: Implement typing state broadcast
   */
  setUserTyping(userId, isTyping = true) {
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
   * @returns {Array<string>} Array of user IDs
   */
  getTypingUsers() {
    // Clean up expired typing indicators
    const now = Date.now();
    for (const [userId, timestamp] of this.typingUsers) {
      if (now - timestamp > this.typingTimeout) {
        this.typingUsers.delete(userId);
      }
    }
    
    return Array.from(this.typingUsers.keys());
  }

  /**
   * Clear chat history
   */
  clearHistory() {
    const count = this.chatHistory.length;
    this.chatHistory = [];
    console.log(`[ChatExtended] Cleared ${count} messages from history`);
  }

  /**
   * Get statistics
   * @returns {Object}
   */
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

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { ChatExtended };
}

/**
 * Example Usage:
 * 
 * const chat = new ChatExtended();
 * 
 * // Add message to history
 * chat.addToHistory({
 *   from: 'John Doe',
 *   text: 'Hello, world!',
 *   type: 'local'
 * });
 * 
 * // Add filter
 * chat.addFilter('profanity', (msg) => {
 *   return !msg.text.toLowerCase().includes('badword');
 * });
 * 
 * // Mute user
 * chat.muteUser('user-123');
 * 
 * // Send with range
 * await chat.sendWithRange('I am here!', 'shout');
 * 
 * // Set typing indicator
 * chat.setUserTyping('user-456', true);
 */
