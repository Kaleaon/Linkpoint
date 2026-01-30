/**
 * Linkpoint PWA - Chat Extended (Features 36-40)
 * Ported from Android slproto/chat/
 * 
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/chat/
 * See: PWA-demo/ANDROID_PORT_ROADMAP.md Phase 2, Priority 3 (Features 36-40)
 * 
 * Handles enhanced chat features including chat history persistence, filtering,
 * mute lists, chat range (whisper/shout), and typing indicators.
 */

/**
 * Chat Types
 */
export const ChatType = {
  WHISPER: 0,
  NORMAL: 1,
  SHOUT: 2,
  SAY: 3,
  START_TYPING: 4,
  STOP_TYPING: 5,
  DEBUG: 6,
  REGION: 7,
  OWNER: 8,
  DIRECT: 9
};

/**
 * Chat Source Types
 */
export const ChatSourceType = {
  AGENT: 0,
  OBJECT: 1,
  SYSTEM: 2
};

/**
 * ChatExtended class
 * Manages advanced chat features beyond basic messaging
 */
class ChatExtended {
  constructor() {
    // Feature 36: Chat history persistence
    this.chatHistory = [];
    this.maxHistorySize = 1000;
    this.persistenceEnabled = true;
    
    // Feature 37: Chat filtering
    this.filters = new Set();
    this.blockedPhrases = new Set();
    
    // Feature 38: Mute list
    this.muteList = new Set();
    this.mutedObjects = new Set();
    
    // Feature 39: Chat range settings
    this.whisperRange = 10; // meters
    this.normalRange = 20;
    this.shoutRange = 100;
    
    // Feature 40: Typing indicators
    this.typingUsers = new Map(); // UUID -> timestamp
    this.typingTimeout = 5000; // 5 seconds
    this.typingCheckInterval = null;
    this.localUserUUID = null; // Cached local user UUID for performance
    
    // Additional features
    this.dbName = 'ChatHistory';
    this.dbVersion = 1;
    this.db = null;
    this.initIndexedDB();
    this.initTypingCheck();
  }
  
  /**
   * Initialize IndexedDB for chat history persistence
   */
  async initIndexedDB() {
    if (!('indexedDB' in window)) {
      console.warn('IndexedDB not available, using localStorage fallback');
      this.loadHistory(); // Load from localStorage
      return;
    }
    
    try {
      const request = indexedDB.open(this.dbName, this.dbVersion);
      
      request.onerror = () => {
        console.error('Failed to open IndexedDB');
        this.loadHistory(); // Fallback to localStorage
      };
      
      request.onsuccess = (event) => {
        this.db = event.target.result;
        this.loadHistoryFromDB();
      };
      
      request.onupgradeneeded = (event) => {
        const db = event.target.result;
        
        if (!db.objectStoreNames.contains('messages')) {
          const objectStore = db.createObjectStore('messages', { keyPath: 'id', autoIncrement: true });
          objectStore.createIndex('timestamp', 'timestamp', { unique: false });
          objectStore.createIndex('fromUUID', 'fromUUID', { unique: false });
          objectStore.createIndex('channel', 'channel', { unique: false });
        }
      };
    } catch (error) {
      console.error('IndexedDB initialization error:', error);
      this.loadHistory(); // Fallback
    }
  }
  
  /**
   * Feature 36: Add message to chat history with persistence
   * @param {Object} message - Chat message object
   */
  addToHistory(message) {
    const historyEntry = {
      timestamp: message.timestamp || Date.now(),
      fromUUID: message.fromUUID,
      fromName: message.fromName,
      message: message.message,
      type: message.type || ChatType.NORMAL,
      sourceType: message.sourceType || ChatSourceType.AGENT,
      position: message.position || null,
      channel: message.channel || 0
    };
    
    // Check filters before adding
    if (this.isFiltered(historyEntry)) {
      return;
    }
    
    this.chatHistory.push(historyEntry);
    
    // Trim history if too large
    if (this.chatHistory.length > this.maxHistorySize) {
      this.chatHistory = this.chatHistory.slice(-this.maxHistorySize);
    }
    
    // Persist to storage
    if (this.persistenceEnabled) {
      this.persistHistory();
    }
  }
  
  /**
   * Get chat history
   * @param {number} limit - Maximum number of messages to return
   * @param {Object} filters - Optional filters (type, source, fromUUID, etc.)
   * @returns {Array} Array of chat messages
   */
  getHistory(limit = 100, filters = {}) {
    let history = [...this.chatHistory];
    
    // Apply filters
    if (filters.type !== undefined) {
      history = history.filter(msg => msg.type === filters.type);
    }
    
    if (filters.sourceType !== undefined) {
      history = history.filter(msg => msg.sourceType === filters.sourceType);
    }
    
    if (filters.fromUUID) {
      history = history.filter(msg => msg.fromUUID === filters.fromUUID);
    }
    
    if (filters.channel !== undefined) {
      history = history.filter(msg => msg.channel === filters.channel);
    }
    
    if (filters.since) {
      history = history.filter(msg => msg.timestamp >= filters.since);
    }
    
    // Return most recent messages up to limit
    return history.slice(-limit);
  }
  
  /**
   * Clear chat history
   * @param {Object} options - Clear options (before timestamp, channel, etc.)
   */
  clearHistory(options = {}) {
    if (options.before) {
      this.chatHistory = this.chatHistory.filter(msg => msg.timestamp >= options.before);
    } else if (options.channel !== undefined) {
      this.chatHistory = this.chatHistory.filter(msg => msg.channel !== options.channel);
    } else {
      this.chatHistory = [];
    }
    
    if (this.persistenceEnabled) {
      this.persistHistory();
    }
  }
  
  /**
   * Persist history to storage
   * TODO: Implement IndexedDB storage
   */
  async persistHistory() {
    if (this.db) {
      await this.persistToIndexedDB();
    } else {
      this.persistToLocalStorage();
    }
  }
  
  /**
   * Persist to IndexedDB
   */
  async persistToIndexedDB() {
    if (!this.db) return;
    
    try {
      const transaction = this.db.transaction(['messages'], 'readwrite');
      const objectStore = transaction.objectStore('messages');
      
      // Clear old data first
      objectStore.clear();
      
      // Add current history
      this.chatHistory.forEach(msg => {
        objectStore.add(msg);
      });
      
      await new Promise((resolve, reject) => {
        transaction.oncomplete = resolve;
        transaction.onerror = reject;
      });
    } catch (error) {
      console.error('Failed to persist to IndexedDB:', error);
      this.persistToLocalStorage(); // Fallback
    }
  }
  
  /**
   * Persist to localStorage (fallback)
   */
  persistToLocalStorage() {
    try {
      const recentHistory = this.chatHistory.slice(-100); // Only save last 100
      localStorage.setItem('chat_history', JSON.stringify(recentHistory));
    } catch (e) {
      console.warn('Failed to persist chat history:', e);
    }
  }
  
  /**
   * Load history from IndexedDB
   */
  async loadHistoryFromDB() {
    if (!this.db) return;
    
    try {
      const transaction = this.db.transaction(['messages'], 'readonly');
      const objectStore = transaction.objectStore('messages');
      const request = objectStore.getAll();
      
      request.onsuccess = () => {
        this.chatHistory = request.result || [];
        console.log(`Loaded ${this.chatHistory.length} messages from IndexedDB`);
      };
      
      request.onerror = () => {
        console.error('Failed to load from IndexedDB');
        this.loadHistory(); // Fallback to localStorage
      };
    } catch (error) {
      console.error('Error loading from IndexedDB:', error);
      this.loadHistory();
    }
  }
  
  /**
   * Load history from storage
   * TODO: Implement IndexedDB loading
   */
  loadHistory() {
    try {
      const stored = localStorage.getItem('chat_history');
      if (stored) {
        this.chatHistory = JSON.parse(stored);
      }
    } catch (e) {
      console.warn('Failed to load chat history:', e);
    }
  }
  
  /**
   * Feature 37: Add chat filter
   * @param {Function} filterFn - Filter function(message) => boolean (true to filter out)
   */
  addFilter(filterFn) {
    this.filters.add(filterFn);
  }
  
  /**
   * Remove chat filter
   * @param {Function} filterFn - Filter function to remove
   */
  removeFilter(filterFn) {
    this.filters.delete(filterFn);
  }
  
  /**
   * Add blocked phrase
   * @param {string} phrase - Phrase to block
   */
  blockPhrase(phrase) {
    this.blockedPhrases.add(phrase.toLowerCase());
  }
  
  /**
   * Remove blocked phrase
   * @param {string} phrase - Phrase to unblock
   */
  unblockPhrase(phrase) {
    this.blockedPhrases.delete(phrase.toLowerCase());
  }
  
  /**
   * Check if message should be filtered
   * @param {Object} message - Message to check
   * @returns {boolean} True if message should be filtered out
   */
  isFiltered(message) {
    // Check mute list
    if (this.isMuted(message.fromUUID)) {
      return true;
    }
    
    // Check blocked phrases
    const msgText = message.message.toLowerCase();
    for (const phrase of this.blockedPhrases) {
      if (msgText.includes(phrase)) {
        return true;
      }
    }
    
    // Check custom filters
    for (const filterFn of this.filters) {
      if (filterFn(message)) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Feature 38: Add user/object to mute list
   * @param {string} uuid - UUID to mute
   * @param {boolean} isObject - True if muting an object
   */
  mute(uuid, isObject = false) {
    if (isObject) {
      this.mutedObjects.add(uuid);
    } else {
      this.muteList.add(uuid);
    }
    
    // TODO: Send MuteListUpdate to server
    // TODO: Persist mute list
  }
  
  /**
   * Remove user/object from mute list
   * @param {string} uuid - UUID to unmute
   * @param {boolean} isObject - True if unmuting an object
   */
  unmute(uuid, isObject = false) {
    if (isObject) {
      this.mutedObjects.delete(uuid);
    } else {
      this.muteList.delete(uuid);
    }
    
    // TODO: Send MuteListUpdate to server
    // TODO: Persist mute list
  }
  
  /**
   * Check if user/object is muted
   * @param {string} uuid - UUID to check
   * @returns {boolean} True if muted
   */
  isMuted(uuid) {
    return this.muteList.has(uuid) || this.mutedObjects.has(uuid);
  }
  
  /**
   * Get mute list
   * @returns {Object} Object with muteList and mutedObjects arrays
   */
  getMuteList() {
    return {
      muteList: Array.from(this.muteList),
      mutedObjects: Array.from(this.mutedObjects)
    };
  }
  
  /**
   * Feature 39: Get chat range for message type
   * @param {number} chatType - Chat type (WHISPER, NORMAL, SHOUT)
   * @returns {number} Range in meters
   */
  getChatRange(chatType) {
    switch (chatType) {
      case ChatType.WHISPER:
        return this.whisperRange;
      case ChatType.SHOUT:
        return this.shoutRange;
      case ChatType.NORMAL:
      case ChatType.SAY:
      default:
        return this.normalRange;
    }
  }
  
  /**
   * Set chat range
   * @param {number} chatType - Chat type
   * @param {number} range - Range in meters
   */
  setChatRange(chatType, range) {
    switch (chatType) {
      case ChatType.WHISPER:
        this.whisperRange = range;
        break;
      case ChatType.SHOUT:
        this.shoutRange = range;
        break;
      case ChatType.NORMAL:
      case ChatType.SAY:
        this.normalRange = range;
        break;
    }
  }
  
  /**
   * Get local user UUID (cached for performance)
   * @returns {string|null} UUID of local user or null
   */
  getLocalUserUUID() {
    // Return cached value if available
    if (this.localUserUUID) {
      return this.localUserUUID;
    }
    
    // Retrieve and cache the UUID
    if (window.app && window.app.protocol && window.app.protocol.agentId) {
      this.localUserUUID = window.app.protocol.agentId;
      return this.localUserUUID;
    }
    if (window.app && window.app.auth) {
      const user = window.app.auth.getUser();
      if (user && user.id) {
        this.localUserUUID = user.id;
        return this.localUserUUID;
      }
    }
    return null;
  }

  /**
   * Send typing indicator packet
   * @param {boolean} isTyping - True if start typing, false if stop
   */
  sendTypingPacket(isTyping) {
    if (!window.app || !window.app.protocol) {
      console.warn('Cannot send typing indicator: Protocol not initialized');
      return;
    }

    const type = isTyping ? ChatType.START_TYPING : ChatType.STOP_TYPING;
    const protocol = window.app.protocol;

    try {
      if (protocol.sendChat) {
        // SLConnectionFull uses sendChat
        protocol.sendChat('', 0, type);
      } else if (protocol.sendMessage) {
        // ProtocolManager uses sendMessage
        // ChatFromViewer message structure (verified)
        protocol.sendMessage('ChatFromViewer', {
          message: '',
          channel: 0,
          type: type
        });
      } else {
        console.warn('Cannot send typing indicator: No suitable method found');
      }
    } catch (error) {
      console.error('Failed to send typing indicator:', error);
    }
  }

  /**
   * Feature 40: Start typing indicator
   * @param {string} userUUID - UUID of typing user
   */
  startTyping(userUUID) {
    // Check if this is the local user starting to type
    const localId = this.getLocalUserUUID();
    if (localId && userUUID === localId) {
      this.sendTypingPacket(true);
    }

    // Add to typing users after sending packet to maintain consistency
    this.typingUsers.set(userUUID, Date.now());
  }
  
  /**
   * Stop typing indicator
   * @param {string} userUUID - UUID of user who stopped typing
   */
  stopTyping(userUUID) {
    // Check if this is the local user stopping typing
    const localId = this.getLocalUserUUID();
    if (localId && userUUID === localId) {
      this.sendTypingPacket(false);
    }

    // Remove from typing users after sending packet to maintain consistency
    this.typingUsers.delete(userUUID);
  }
  
  /**
   * Get currently typing users
   * @returns {Array} Array of UUIDs of typing users
   */
  getTypingUsers() {
    return Array.from(this.typingUsers.keys());
  }
  
  /**
   * Initialize typing indicator timeout check
   */
  initTypingCheck() {
    this.typingCheckInterval = setInterval(() => {
      const now = Date.now();
      const toRemove = [];
      
      this.typingUsers.forEach((timestamp, uuid) => {
        if (now - timestamp > this.typingTimeout) {
          toRemove.push(uuid);
        }
      });
      
      toRemove.forEach(uuid => this.typingUsers.delete(uuid));
    }, 1000);
  }
  
  /**
   * Clean up resources
   */
  destroy() {
    if (this.typingCheckInterval) {
      clearInterval(this.typingCheckInterval);
    }
    
    if (this.db) {
      this.db.close();
    }
  }
  
  /**
   * Export chat history to JSON
   * @param {Object} filters - Optional filters
   * @returns {string} JSON string of chat history
   */
  exportToJSON(filters = {}) {
    const history = this.getHistory(this.maxHistorySize, filters);
    return JSON.stringify(history, null, 2);
  }
  
  /**
   * Export chat history to plain text
   * @param {Object} filters - Optional filters
   * @returns {string} Plain text chat history
   */
  exportToText(filters = {}) {
    const history = this.getHistory(this.maxHistorySize, filters);
    let output = '';
    
    for (const msg of history) {
      const date = new Date(msg.timestamp).toLocaleString();
      const typeStr = this.getChatTypeName(msg.type);
      output += `[${date}] [${typeStr}] ${msg.fromName}: ${msg.message}\n`;
    }
    
    return output;
  }
  
  /**
   * Export chat history to CSV
   * @param {Object} filters - Optional filters
   * @returns {string} CSV formatted chat history
   */
  exportToCSV(filters = {}) {
    const history = this.getHistory(this.maxHistorySize, filters);
    let csv = 'Timestamp,Type,Source,From UUID,From Name,Message,Channel\n';
    
    for (const msg of history) {
      const date = new Date(msg.timestamp).toISOString();
      const typeStr = this.getChatTypeName(msg.type);
      const sourceStr = this.getSourceTypeName(msg.sourceType);
      const message = msg.message.replace(/"/g, '""'); // Escape quotes
      
      csv += `"${date}","${typeStr}","${sourceStr}","${msg.fromUUID}","${msg.fromName}","${message}",${msg.channel}\n`;
    }
    
    return csv;
  }
  
  /**
   * Download chat export
   * @param {string} format - Export format ('json', 'text', 'csv')
   * @param {Object} filters - Optional filters
   */
  downloadExport(format = 'text', filters = {}) {
    let content, filename, mimeType;
    
    switch (format) {
      case 'json':
        content = this.exportToJSON(filters);
        filename = `chat_export_${Date.now()}.json`;
        mimeType = 'application/json';
        break;
      case 'csv':
        content = this.exportToCSV(filters);
        filename = `chat_export_${Date.now()}.csv`;
        mimeType = 'text/csv';
        break;
      case 'text':
      default:
        content = this.exportToText(filters);
        filename = `chat_export_${Date.now()}.txt`;
        mimeType = 'text/plain';
        break;
    }
    
    const blob = new Blob([content], { type: mimeType });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  }
  
  /**
   * Search chat history
   * @param {string} query - Search query
   * @param {Object} options - Search options
   * @returns {Array} Matching messages
   */
  searchHistory(query, options = {}) {
    const caseSensitive = options.caseSensitive || false;
    const searchTerm = caseSensitive ? query : query.toLowerCase();
    
    return this.chatHistory.filter(msg => {
      const messageText = caseSensitive ? msg.message : msg.message.toLowerCase();
      const nameText = caseSensitive ? msg.fromName : msg.fromName.toLowerCase();
      
      if (options.searchInMessage !== false && messageText.includes(searchTerm)) {
        return true;
      }
      
      if (options.searchInName && nameText.includes(searchTerm)) {
        return true;
      }
      
      return false;
    });
  }
  
  /**
   * Get statistics about chat history
   * @returns {Object} Chat statistics
   */
  getStatistics() {
    const stats = {
      totalMessages: this.chatHistory.length,
      byType: {},
      bySource: {},
      byChannel: {},
      uniqueSenders: new Set(),
      dateRange: {
        oldest: null,
        newest: null
      }
    };
    
    for (const msg of this.chatHistory) {
      // Count by type
      const typeName = this.getChatTypeName(msg.type);
      stats.byType[typeName] = (stats.byType[typeName] || 0) + 1;
      
      // Count by source
      const sourceName = this.getSourceTypeName(msg.sourceType);
      stats.bySource[sourceName] = (stats.bySource[sourceName] || 0) + 1;
      
      // Count by channel
      stats.byChannel[msg.channel] = (stats.byChannel[msg.channel] || 0) + 1;
      
      // Unique senders
      stats.uniqueSenders.add(msg.fromUUID);
      
      // Date range
      if (!stats.dateRange.oldest || msg.timestamp < stats.dateRange.oldest) {
        stats.dateRange.oldest = msg.timestamp;
      }
      if (!stats.dateRange.newest || msg.timestamp > stats.dateRange.newest) {
        stats.dateRange.newest = msg.timestamp;
      }
    }
    
    stats.uniqueSenders = stats.uniqueSenders.size;
    
    return stats;
  }
  
  /**
   * Get chat type name
   * @param {number} type - Chat type
   * @returns {string} Type name
   */
  getChatTypeName(type) {
    const names = Object.keys(ChatType);
    for (const name of names) {
      if (ChatType[name] === type) {
        return name;
      }
    }
    return 'UNKNOWN';
  }
  
  /**
   * Get source type name
   * @param {number} type - Source type
   * @returns {string} Type name
   */
  getSourceTypeName(type) {
    const names = Object.keys(ChatSourceType);
    for (const name of names) {
      if (ChatSourceType[name] === type) {
        return name;
      }
    }
    return 'UNKNOWN';
  }
  
  /**
   * Import chat history from JSON
   * @param {string} jsonData - JSON string of chat history
   */
  importFromJSON(jsonData) {
    try {
      const imported = JSON.parse(jsonData);
      
      if (!Array.isArray(imported)) {
        throw new Error('Invalid format: expected array');
      }
      
      for (const msg of imported) {
        this.addToHistory(msg);
      }
      
      console.log(`Imported ${imported.length} messages`);
    } catch (error) {
      console.error('Failed to import chat history:', error);
      throw error;
    }
  }
}

// Export singleton instance
const chatExtended = new ChatExtended();
export default chatExtended;
export { ChatExtended, ChatType, ChatSourceType };
