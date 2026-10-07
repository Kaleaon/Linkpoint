/**
 * Linkpoint PWA - Enhanced Chat (Features 36-40 & Bi-Directional Grid Mute List Sync)
 *
 * Phase 2: Core Protocol Extensions - Priority 3
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 76-81)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/chatsrc/
 *
 * Extends chat functionality with history, filtering, mute list, range, and typing indicators.
 * Implements bi-directional grid mute list synchronization with persistent storage caching.
 */

import { Utils } from '../utils';
import { slBridge } from '../sl-bridge';

export const MuteFlags = {
  NONE: 0,
  MUTE_CHAT: 1,
  MUTE_VOICE: 2,
  MUTE_PARTICLES: 4,
  MUTE_SOUNDS: 8,
  MUTE_ALL: 1 | 2 | 4 | 8,
};

export enum MuteType {
  BY_NAME = 0,
  AGENT = 1,
  OBJECT = 2,
  GROUP = 3,
  EXTERNAL = 4,
}

export interface MuteListEntry {
  id: string; // UUID or "00000000-0000-0000-0000-000000000000"
  name: string;
  type: MuteType;
  flags: number;
  timestamp?: number;
}

export function calculateCRC32(data: Uint8Array | string): number {
  const bytes = typeof data === 'string' ? new TextEncoder().encode(data) : data;
  let crc = 0xFFFFFFFF;
  for (let i = 0; i < bytes.length; i++) {
    crc ^= bytes[i];
    for (let j = 0; j < 8; j++) {
      const mask = -(crc & 1);
      crc = (crc >>> 1) ^ (0xEDB88320 & mask);
    }
  }
  return (crc ^ 0xFFFFFFFF) >>> 0;
}

export class ChatExtended {
  private protocol: any;
  private chatHistory: any[] = [];
  private maxHistorySize: number = 1000;
  private filters: Map<string, Function> = new Map();
  private entries: Map<string, MuteListEntry> = new Map();
  private muteList: Set<string> = new Set();
  private mutedObjects: Set<string> = new Set();
  private typingUsers: Map<string, number> = new Map();
  private typingTimeout: number = 5000; // milliseconds
  private cachedCRC: number = 0;

  constructor(protocolManager?: any) {
    this.protocol = protocolManager;
    this.loadFromStorage();
    this.setupProtocolListeners();
  }

  private setupProtocolListeners() {
    if (!this.protocol) return;

    if (typeof this.protocol.on === 'function') {
      const onRequestMute = () => {
        void this.requestMuteList();
      };
      this.protocol.on('connected', onRequestMute);
      this.protocol.on('login_success', onRequestMute);

      this.protocol.on('MuteListUpdate', (data: any) => {
        void this.handleMuteListUpdate(data);
      });

      this.protocol.on('UseCachedMuteList', () => {
        this.handleUseCachedMuteList();
      });

      this.protocol.on('UpdateMuteListEntry', (data: any) => {
        this.handleServerMuteEntryUpdated(data);
      });

      this.protocol.on('RemoveMuteListEntry', (data: any) => {
        this.handleServerMuteEntryRemoved(data);
      });
    }
  }

  /**
   * Load cached mute list and CRC32 from persistent storage.
   */
  public loadFromStorage() {
    try {
      const savedEntries = Utils.storage.get('linkpoint_mute_entries_v2', null);
      const savedCRC = Utils.storage.get('linkpoint_mute_crc32', 0);
      if (typeof savedCRC === 'number' && !isNaN(savedCRC)) {
        this.cachedCRC = savedCRC;
      } else {
        this.cachedCRC = 0;
      }

      this.entries.clear();
      this.muteList.clear();
      this.mutedObjects.clear();

      if (Array.isArray(savedEntries)) {
        for (const entry of savedEntries) {
          if (entry && typeof entry === 'object' && entry.name !== undefined) {
            const id = typeof entry.id === 'string' ? entry.id : '00000000-0000-0000-0000-000000000000';
            const name = typeof entry.name === 'string' ? entry.name : '';
            const type = typeof entry.type === 'number' ? entry.type : MuteType.AGENT;
            const flags = typeof entry.flags === 'number' ? entry.flags : MuteFlags.MUTE_ALL;
            const cleanEntry: MuteListEntry = {
              id,
              name,
              type,
              flags,
              timestamp: entry.timestamp || Date.now(),
            };
            const key = this.getEntryKey(cleanEntry.id, cleanEntry.name, cleanEntry.type);
            this.entries.set(key, cleanEntry);
            this.syncSetsForEntry(cleanEntry);
          }
        }
      }
    } catch (err) {
      console.warn('[ChatExtended] Failed to load mute list from storage:', err);
      this.entries.clear();
      this.muteList.clear();
      this.mutedObjects.clear();
      this.cachedCRC = 0;
    }
  }

  /**
   * Save active mute list and CRC32 to persistent storage.
   */
  public saveToStorage() {
    try {
      const array = Array.from(this.entries.values());
      Utils.storage.set('linkpoint_mute_entries_v2', array);
      Utils.storage.set('linkpoint_mute_crc32', this.cachedCRC);
    } catch (err) {
      console.warn('[ChatExtended] Failed to save mute list to storage:', err);
    }
  }

  private getEntryKey(id: string, name: string, type: MuteType | number): string {
    const cleanId = (id || '').toLowerCase().trim();
    const cleanName = (name || '').toLowerCase().trim();
    return `${type}:${cleanId}:${cleanName}`;
  }

  private syncSetsForEntry(entry: MuteListEntry) {
    const cleanId = entry.id ? entry.id.trim() : '';
    const cleanName = entry.name ? entry.name.trim() : '';

    if (entry.type === MuteType.AGENT || entry.type === MuteType.EXTERNAL || entry.type === MuteType.BY_NAME) {
      if (cleanId && cleanId !== '00000000-0000-0000-0000-000000000000') {
        this.muteList.add(cleanId);
      }
      if (cleanName) {
        this.muteList.add(cleanName);
      }
    } else if (entry.type === MuteType.OBJECT) {
      if (cleanId && cleanId !== '00000000-0000-0000-0000-000000000000') {
        this.mutedObjects.add(cleanId);
      }
      if (cleanName) {
        this.mutedObjects.add(cleanName);
      }
    }
  }

  /**
   * Requirement 1: Issue MuteListRequest with agent UUID, session ID, and local cached CRC32 checksum.
   */
  public async requestMuteList(): Promise<void> {
    const agentId = this.protocol?.agentId || slBridge.sessionId;
    const sessionId = this.protocol?.sessionId || slBridge.sessionId;

    console.log(`[ChatExtended] Requesting mute list (CRC: 0x${this.cachedCRC.toString(16)})`);

    if (this.protocol && typeof this.protocol.requestMuteList === 'function') {
      await this.protocol.requestMuteList(this.cachedCRC);
    } else if (slBridge.connected) {
      await slBridge.requestMuteList(this.cachedCRC);
    }
  }

  /**
   * Requirement 2: Handle MuteListUpdate, fetch file via Xfer, parse entries, update storage & memory.
   */
  public async handleMuteListUpdate(data: any): Promise<void> {
    const filename = typeof data === 'string' ? data : data?.filename || data?.fileName || '';
    if (!filename) {
      console.log('[ChatExtended] MuteListUpdate received with empty filename');
      return;
    }

    console.log(`[ChatExtended] Fetching mute list file via Xfer: ${filename}`);
    let content = '';
    if (this.protocol && typeof this.protocol.fetchXfer === 'function') {
      content = await this.protocol.fetchXfer(filename);
    } else if (slBridge.connected) {
      content = await slBridge.fetchXfer(filename);
    }

    if (typeof content === 'string') {
      this.parseMuteListData(content);
    }
  }

  /**
   * Parse mute list file text content, calculate CRC32, update memory and persistent storage.
   */
  public parseMuteListData(content: string): void {
    this.cachedCRC = calculateCRC32(content);
    this.entries.clear();
    this.muteList.clear();
    this.mutedObjects.clear();

    const lines = content.split(/\r?\n/);
    for (const line of lines) {
      const trimmed = line.trim();
      if (!trimmed) continue;

      // Line format: type_int id_uuid flags_int name
      const parts = trimmed.split(/\s+/);
      if (parts.length >= 3) {
        const typeInt = parseInt(parts[0], 10);
        const type = !isNaN(typeInt) && MuteType[typeInt] !== undefined ? (typeInt as MuteType) : MuteType.AGENT;
        const id = parts[1];
        let flags = MuteFlags.MUTE_ALL;
        let name = '';

        if (parts.length >= 4) {
          const parsedFlags = parseInt(parts[2], 10);
          if (!isNaN(parsedFlags)) {
            flags = parsedFlags;
            name = parts.slice(3).join(' ');
          } else {
            name = parts.slice(2).join(' ');
          }
        } else {
          name = parts[2];
        }

        const entry: MuteListEntry = {
          id,
          name: name.trim(),
          type,
          flags,
          timestamp: Date.now(),
        };

        const key = this.getEntryKey(entry.id, entry.name, entry.type);
        this.entries.set(key, entry);
        this.syncSetsForEntry(entry);
      }
    }

    this.saveToStorage();
    console.log(`[ChatExtended] Parsed ${this.entries.size} mute entries from Xfer file (CRC: 0x${this.cachedCRC.toString(16)})`);
  }

  public handleUseCachedMuteList(): void {
    console.log('[ChatExtended] Grid confirmed local cached mute list is up to date.');
  }

  public handleServerMuteEntryUpdated(data: any): void {
    if (!data) return;
    const entry: MuteListEntry = {
      id: data.id || data.muteId || '00000000-0000-0000-0000-000000000000',
      name: data.name || data.muteName || '',
      type: typeof data.type === 'number' ? data.type : MuteType.AGENT,
      flags: typeof data.flags === 'number' ? data.flags : MuteFlags.MUTE_ALL,
      timestamp: Date.now(),
    };

    const key = this.getEntryKey(entry.id, entry.name, entry.type);
    this.entries.set(key, entry);
    this.syncSetsForEntry(entry);
    this.saveToStorage();
  }

  public handleServerMuteEntryRemoved(data: any): void {
    if (!data) return;
    const id = typeof data === 'string' ? data : data.id || data.muteId;
    const name = typeof data === 'object' ? data.name || data.muteName : '';

    for (const [key, entry] of Array.from(this.entries.entries())) {
      if ((id && entry.id.toLowerCase() === id.toLowerCase()) || (name && entry.name.toLowerCase() === name.toLowerCase())) {
        this.entries.delete(key);
      }
    }

    if (id) {
      this.muteList.delete(id);
      this.mutedObjects.delete(id);
    }
    if (name) {
      this.muteList.delete(name);
      this.mutedObjects.delete(name);
    }

    this.saveToStorage();
  }

  /**
   * Requirement 3: Add user to mute list, save to disk, and transmit UpdateMuteListEntry packet.
   */
  public muteUser(userId: string, name: string = '', flags: number = MuteFlags.MUTE_ALL) {
    if (!userId || typeof userId !== 'string') {
      throw new Error('Valid user ID required');
    }

    const cleanId = userId.trim();
    const cleanName = name.trim() || cleanId;
    const isUuid = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(cleanId);

    const entry: MuteListEntry = {
      id: cleanId || '00000000-0000-0000-0000-000000000000',
      name: cleanName,
      type: isUuid ? MuteType.AGENT : MuteType.BY_NAME,
      flags,
      timestamp: Date.now(),
    };

    const key = this.getEntryKey(entry.id, entry.name, entry.type);
    this.entries.set(key, entry);
    if (cleanId) this.muteList.add(cleanId);
    if (cleanName) this.muteList.add(cleanName);
    this.syncSetsForEntry(entry);
    this.saveToStorage();

    console.log(`[ChatExtended] Muted user: ${cleanName} (${entry.id})`);

    // Transmit UpdateMuteListEntry asynchronously to grid server
    void this.sendUpdateMuteListEntryPacket(entry);
  }

  /**
   * Requirement 3: Remove user from mute list, save to disk, and transmit RemoveMuteListEntry packet.
   */
  public unmuteUser(userId: string, name: string = '') {
    if (!userId) return;
    const cleanId = userId.trim();
    const cleanName = name.trim();

    let removedEntry: MuteListEntry | null = null;
    for (const [key, entry] of Array.from(this.entries.entries())) {
      if (
        (cleanId && entry.id.toLowerCase() === cleanId.toLowerCase()) ||
        (cleanName && entry.name.toLowerCase() === cleanName.toLowerCase()) ||
        (cleanId && entry.name.toLowerCase() === cleanId.toLowerCase())
      ) {
        removedEntry = entry;
        if (entry.id) this.muteList.delete(entry.id);
        if (entry.name) this.muteList.delete(entry.name);
        this.entries.delete(key);
      }
    }

    this.muteList.delete(cleanId);
    if (cleanName) this.muteList.delete(cleanName);

    this.saveToStorage();
    console.log(`[ChatExtended] Unmuted user: ${cleanName || cleanId}`);

    const targetEntry: MuteListEntry = removedEntry || {
      id: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(cleanId)
        ? cleanId
        : '00000000-0000-0000-0000-000000000000',
      name: cleanName || cleanId,
      type: MuteType.AGENT,
      flags: MuteFlags.MUTE_ALL,
    };

    void this.sendRemoveMuteListEntryPacket(targetEntry);
  }

  /**
   * Requirement 3: Mute object, save to disk, send UpdateMuteListEntry packet.
   */
  public muteObject(nameOrId: string, name: string = '', flags: number = MuteFlags.MUTE_ALL) {
    if (!nameOrId || typeof nameOrId !== 'string') return;
    const clean = nameOrId.trim();
    const cleanName = name.trim() || clean;
    const isUuid = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(clean);

    const entry: MuteListEntry = {
      id: clean || '00000000-0000-0000-0000-000000000000',
      name: cleanName,
      type: MuteType.OBJECT,
      flags,
      timestamp: Date.now(),
    };

    const key = this.getEntryKey(entry.id, entry.name, entry.type);
    this.entries.set(key, entry);
    if (clean) this.mutedObjects.add(clean);
    if (cleanName) this.mutedObjects.add(cleanName);
    this.syncSetsForEntry(entry);
    this.saveToStorage();

    console.log(`[ChatExtended] Muted object: ${cleanName} (${entry.id})`);
    void this.sendUpdateMuteListEntryPacket(entry);
  }

  /**
   * Requirement 3: Unmute object, save to disk, send RemoveMuteListEntry packet.
   */
  public unmuteObject(nameOrId: string) {
    if (!nameOrId) return;
    const clean = nameOrId.trim();

    let removedEntry: MuteListEntry | null = null;
    for (const [key, entry] of Array.from(this.entries.entries())) {
      if (
        entry.type === MuteType.OBJECT &&
        (entry.id.toLowerCase() === clean.toLowerCase() || entry.name.toLowerCase() === clean.toLowerCase())
      ) {
        removedEntry = entry;
        if (entry.id) this.mutedObjects.delete(entry.id);
        if (entry.name) this.mutedObjects.delete(entry.name);
        this.entries.delete(key);
      }
    }

    this.mutedObjects.delete(clean);
    if (removedEntry?.id) this.mutedObjects.delete(removedEntry.id);
    if (removedEntry?.name) this.mutedObjects.delete(removedEntry.name);

    this.saveToStorage();

    console.log(`[ChatExtended] Unmuted object: ${clean}`);

    const targetEntry: MuteListEntry = removedEntry || {
      id: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(clean)
        ? clean
        : '00000000-0000-0000-0000-000000000000',
      name: clean,
      type: MuteType.OBJECT,
      flags: MuteFlags.MUTE_ALL,
    };

    void this.sendRemoveMuteListEntryPacket(targetEntry);
  }

  private async sendUpdateMuteListEntryPacket(entry: MuteListEntry): Promise<void> {
    if (this.protocol && typeof this.protocol.updateMuteListEntry === 'function') {
      await this.protocol.updateMuteListEntry(entry);
    } else if (slBridge.connected) {
      await slBridge.updateMuteListEntry(entry);
    }
  }

  private async sendRemoveMuteListEntryPacket(entry: MuteListEntry): Promise<void> {
    if (this.protocol && typeof this.protocol.removeMuteListEntry === 'function') {
      await this.protocol.removeMuteListEntry(entry);
    } else if (slBridge.connected) {
      await slBridge.removeMuteListEntry(entry);
    }
  }

  /**
   * Requirement 4: Filtering against synchronized mute list.
   */
  public isUserMuted(userId: string, name?: string, flag: number = 0): boolean {
    if (!userId && !name) return false;
    const cleanId = (userId || '').trim().toLowerCase();
    const cleanName = (name || '').trim().toLowerCase();

    for (const entry of this.entries.values()) {
      if (entry.type === MuteType.AGENT || entry.type === MuteType.BY_NAME || entry.type === MuteType.EXTERNAL) {
        const idMatch = cleanId && entry.id.toLowerCase() === cleanId;
        const nameMatch = cleanName && entry.name.toLowerCase() === cleanName;
        const idNameMatch = cleanId && entry.name.toLowerCase() === cleanId;
        if (idMatch || nameMatch || idNameMatch) {
          return flag === 0 ? entry.flags !== 0 : (entry.flags & flag) !== 0;
        }
      }
    }

    if (cleanId && Array.from(this.muteList).some(m => m.toLowerCase() === cleanId)) return true;
    if (cleanName && Array.from(this.muteList).some(m => m.toLowerCase() === cleanName)) return true;
    return false;
  }

  public isObjectMuted(nameOrId: string, flag: number = 0): boolean {
    if (!nameOrId) return false;
    const clean = nameOrId.trim().toLowerCase();

    for (const entry of this.entries.values()) {
      if (entry.type === MuteType.OBJECT) {
        if (entry.id.toLowerCase() === clean || entry.name.toLowerCase() === clean) {
          return flag === 0 ? entry.flags !== 0 : (entry.flags & flag) !== 0;
        }
      }
    }

    if (this.mutedObjects.has(clean)) return true;
    return Array.from(this.mutedObjects).some(m => clean === m.toLowerCase());
  }

  public isGroupMuted(groupId: string, name?: string, flag: number = 0): boolean {
    if (!groupId && !name) return false;
    const cleanId = (groupId || '').trim().toLowerCase();
    const cleanName = (name || '').trim().toLowerCase();

    for (const entry of this.entries.values()) {
      if (entry.type === MuteType.GROUP) {
        if ((cleanId && entry.id.toLowerCase() === cleanId) || (cleanName && entry.name.toLowerCase() === cleanName)) {
          return flag === 0 ? entry.flags !== 0 : (entry.flags & flag) !== 0;
        }
      }
    }
    return false;
  }

  public isMuted(sourceId: string, sourceName?: string, flag: number = 0): boolean {
    return this.isUserMuted(sourceId, sourceName, flag) ||
           this.isObjectMuted(sourceId, flag) ||
           (sourceName ? this.isObjectMuted(sourceName, flag) : false) ||
           this.isGroupMuted(sourceId, sourceName, flag);
  }

  /**
   * Check if an incoming message object should be filtered.
   */
  public shouldFilterMessage(msg: any): boolean {
    if (!msg) return false;
    const senderId = msg.senderId || msg.fromId || msg.from || msg.OwnerID || '';
    const senderName = msg.sender || msg.fromName || msg.FromName || '';
    const isObject = msg.isObject || msg.sourceType === 2 || msg.sourceType === 'object';

    if (isObject) {
      return this.isObjectMuted(senderId, MuteFlags.MUTE_CHAT) || this.isObjectMuted(senderName, MuteFlags.MUTE_CHAT);
    } else if (msg.type === 'group' || msg.chatType === 'group' || msg.groupId) {
      return this.isGroupMuted(msg.groupId, msg.groupName, MuteFlags.MUTE_CHAT) || this.isUserMuted(senderId, senderName, MuteFlags.MUTE_CHAT);
    } else {
      return this.isUserMuted(senderId, senderName, MuteFlags.MUTE_CHAT);
    }
  }

  public getMutedUsers(): string[] {
    const set = new Set<string>();
    for (const entry of this.entries.values()) {
      if (entry.type === MuteType.AGENT || entry.type === MuteType.BY_NAME || entry.type === MuteType.EXTERNAL) {
        set.add(entry.name || entry.id);
      }
    }
    for (const item of this.muteList) set.add(item);
    return Array.from(set);
  }

  public getMutedObjects(): string[] {
    const set = new Set<string>();
    for (const entry of this.entries.values()) {
      if (entry.type === MuteType.OBJECT) {
        set.add(entry.name || entry.id);
      }
    }
    for (const item of this.mutedObjects) set.add(item);
    return Array.from(set);
  }

  public getAllMuteEntries(): MuteListEntry[] {
    return Array.from(this.entries.values());
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
    if (this.shouldFilterMessage(message)) {
      console.log(`[ChatExtended] Message blocked by mute list: ${message.sender || message.fromName}`);
      return false;
    }

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
      mutedUsers: this.entries.size || this.muteList.size,
      typingUsers: this.typingUsers.size,
      cachedCRC: this.cachedCRC,
    };
  }
}
