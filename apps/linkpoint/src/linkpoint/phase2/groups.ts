/**
 * Linkpoint PWA - Groups System (Features 41-45)
 *
 * Phase 2: Core Protocol Extensions - Priority 3
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 83-88)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/groups/
 *
 * Manages group information, members, roles, chat, and notices.
 */

import type { CapabilitiesManager } from './capabilities';
import type { NoticeStore, SavedNotice } from '../notices';

export class GroupsManager {
  private protocol: any;
  private capabilitiesManager?: CapabilitiesManager;
  private noticeStore?: NoticeStore;
  private groups: Map<string, any> = new Map();
  private groupMembers: Map<string, Map<string, any>> = new Map();
  private groupRoles: Map<string, Map<string, any>> = new Map();
  private groupNotices: Map<string, any[]> = new Map();

  constructor(
    protocolManager?: any,
    capabilitiesManager?: CapabilitiesManager,
    noticeStore?: NoticeStore
  ) {
    this.protocol = protocolManager;
    this.capabilitiesManager = capabilitiesManager;
    this.noticeStore = noticeStore;
  }

  setCapabilitiesManager(capabilitiesManager: CapabilitiesManager) {
    this.capabilitiesManager = capabilitiesManager;
  }

  setNoticeStore(noticeStore: NoticeStore) {
    this.noticeStore = noticeStore;
  }

  /**
   * Feature 41: Group info
   * Set or update group information
   */
  setGroupInfo(groupId: string, groupInfo: any) {
    if (!groupId || typeof groupId !== 'string') {
      throw new Error('Valid group ID required');
    }
    if (!groupInfo || typeof groupInfo !== 'object') {
      throw new Error('Valid group info required');
    }

    const group = {
      id: groupId,
      name: groupInfo.name || 'Group',
      charter: groupInfo.charter || '',
      insignia: groupInfo.insignia || null,
      founderId: groupInfo.founderId || null,
      membershipFee: groupInfo.membershipFee || 0,
      openEnrollment: groupInfo.openEnrollment || false,
      ...groupInfo
    };

    this.groups.set(groupId, group);
    console.log(`[Groups] Updated group info: ${group.name}`);
  }

  /**
   * Get group information
   */
  getGroupInfo(groupId: string) {
    return this.groups.get(groupId) || null;
  }

  /**
   * Feature 42: Group members
   * Add or update group member
   */
  addGroupMember(groupId: string, memberId: string, memberData: any = {}) {
    if (!groupId || !memberId) {
      throw new Error('Valid group ID and member ID required');
    }

    if (!this.groupMembers.has(groupId)) {
      this.groupMembers.set(groupId, new Map());
    }

    const member = {
      id: memberId,
      title: memberData.title || '',
      contribution: memberData.contribution || 0,
      onlineStatus: memberData.onlineStatus || 'unknown',
      powers: memberData.powers || 0,
      ...memberData
    };

    this.groupMembers.get(groupId)!.set(memberId, member);
    console.log(`[Groups] Added member ${memberId} to group ${groupId}`);
  }

  /**
   * Get group members
   */
  getGroupMembers(groupId: string) {
    return this.groupMembers.get(groupId) || new Map();
  }

  /**
   * Feature 43: Group roles
   * Add or update group role
   */
  addGroupRole(groupId: string, roleId: string, roleData: any) {
    if (!groupId || !roleId) {
      throw new Error('Valid group ID and role ID required');
    }
    if (!roleData || typeof roleData !== 'object') {
      throw new Error('Valid role data required');
    }

    if (!this.groupRoles.has(groupId)) {
      this.groupRoles.set(groupId, new Map());
    }

    const role = {
      id: roleId,
      name: roleData.name || 'Role',
      title: roleData.title || '',
      description: roleData.description || '',
      powers: roleData.powers || 0,
      members: roleData.members || [],
      ...roleData
    };

    this.groupRoles.get(groupId)!.set(roleId, role);
    console.log(`[Groups] Added role ${role.name} to group ${groupId}`);
  }

  /**
   * Get group roles
   */
  getGroupRoles(groupId: string) {
    return this.groupRoles.get(groupId) || new Map();
  }

  /**
   * Feature 44: Group chat
   * Send group chat message
   */
  async sendGroupChat(groupId: string, message: string) {
    if (!groupId || !message) {
      throw new Error('Valid group ID and message required');
    }

    const chatMsg = {
      groupId: groupId,
      message: message,
      timestamp: Date.now(),
      type: 'group'
    };

    console.log(`[Groups] Sending group chat to ${groupId}: ${message}`);

    if (this.protocol && typeof this.protocol.sendChat === 'function') {
      try {
        // Group chat usually uses a specific channel or type (e.g. type 2 for group chat)
        await this.protocol.sendChat(message, 0, 2);
      } catch (error) {
        console.error('[Groups] Failed to send group chat:', error);
        throw error;
      }
    } else {
      console.warn('[Groups] Protocol handler not available');
    }

    return Promise.resolve(chatMsg);
  }

  /**
   * Feature 45: Group notices
   * Add group notice
   */
  addGroupNotice(groupId: string, noticeData: any) {
    if (!groupId || !noticeData) {
      throw new Error('Valid group ID and notice data required');
    }

    if (!this.groupNotices.has(groupId)) {
      this.groupNotices.set(groupId, []);
    }

    const notice = {
      id: noticeData.id || `notice-${Date.now()}`,
      subject: noticeData.subject || 'Notice',
      message: noticeData.message || '',
      from: noticeData.from || 'Unknown',
      timestamp: noticeData.timestamp || Date.now(),
      hasAttachment: noticeData.hasAttachment || false,
      attachment: noticeData.attachment || null,
      ...noticeData
    };

    this.groupNotices.get(groupId)!.push(notice);
    console.log(`[Groups] Added notice to group ${groupId}: ${notice.subject}`);
  }

  /**
   * Get group notices
   */
  getGroupNotices(groupId: string, limit: number = 25) {
    const notices = this.groupNotices.get(groupId) || [];
    return notices.slice(-limit);
  }

  /**
   * Fetches group notices using dual-transport strategy (HTTP capability query with 10s timeout,
   * falling back to UDP GroupNoticesListRequest packets) and enforced 5-minute TTL caching.
   */
  async requestGroupNotices(
    groupId: string,
    options: {
      forceRefresh?: boolean;
      capabilitiesManager?: CapabilitiesManager;
      noticeStore?: NoticeStore;
      timeoutMs?: number;
    } = {}
  ): Promise<SavedNotice[]> {
    if (!groupId || typeof groupId !== 'string') {
      throw new Error('Valid group ID required');
    }

    const capMgr = options.capabilitiesManager || this.capabilitiesManager;
    const store = options.noticeStore || this.noticeStore;
    const timeoutMs = options.timeoutMs ?? 10000; // 10s timeout

    // Check 5-minute TTL Cache
    if (store && !options.forceRefresh) {
      const cache = store.getGroupCache(groupId, 300000);
      if (cache && cache.valid) {
        console.log(`[Groups] Returning cached notices for group ${groupId} (within TTL)`);
        return cache.notices;
      }
      if (cache && !cache.valid) {
        console.log(`[Groups] TTL expired for group ${groupId}. Triggering background re-validation.`);
        // Background re-validation request
        this.fetchGroupNoticesNetwork(groupId, capMgr, store, timeoutMs).catch((err) => {
          console.warn(`[Groups] Background notice re-validation failed for group ${groupId}:`, err);
        });
        return cache.notices;
      }
    }

    return this.fetchGroupNoticesNetwork(groupId, capMgr, store, timeoutMs);
  }

  private async fetchGroupNoticesNetwork(
    groupId: string,
    capMgr?: CapabilitiesManager,
    store?: NoticeStore,
    timeoutMs: number = 10000
  ): Promise<SavedNotice[]> {
    const capUrl = capMgr?.getGroupNoticesListUrl?.() || capMgr?.resolveCapability('GroupNoticesList');

    // 1. Try HTTP Capability query first if URL is present
    if (capUrl) {
      try {
        console.log(`[Groups] Requesting GroupNoticesList via HTTP capability: ${capUrl}`);
        const notices = await this.queryGroupNoticesHttpCap(capUrl, groupId, timeoutMs);
        if (store) {
          store.setGroupCache(groupId, notices);
          store.setHistoryUnavailable(groupId, false);
        }
        for (const notice of notices) {
          this.addGroupNotice(groupId, notice);
        }
        return notices;
      } catch (err) {
        console.warn(`[Groups] HTTP GroupNoticesList query failed or timed out. Falling back to UDP:`, err);
      }
    }

    // 2. Fallback to UDP GroupNoticesListRequest
    try {
      console.log(`[Groups] Dispatching UDP GroupNoticesListRequest for group ${groupId}`);
      const notices = await this.sendGroupNoticesListUdp(groupId, timeoutMs);
      if (store) {
        store.setGroupCache(groupId, notices);
        store.setHistoryUnavailable(groupId, false);
      }
      for (const notice of notices) {
        this.addGroupNotice(groupId, notice);
      }
      return notices;
    } catch (udpErr) {
      console.error(`[Groups] Both HTTP capability and UDP packet retries failed for group ${groupId}:`, udpErr);
      if (store) {
        store.setHistoryUnavailable(groupId, true);
      }
      throw new Error('Notice history unavailable from grid server.');
    }
  }

  private async queryGroupNoticesHttpCap(
    capUrl: string,
    groupId: string,
    timeoutMs: number
  ): Promise<SavedNotice[]> {
    const controller = typeof AbortController !== 'undefined' ? new AbortController() : null;
    const timeoutId = controller ? setTimeout(() => controller.abort(), timeoutMs) : null;

    try {
      const response = await fetch(capUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/llsd+xml',
        },
        body: `<llsd><map><key>group_id</key><uuid>${groupId}</uuid></map></llsd>`,
        signal: controller?.signal,
      });

      if (timeoutId) clearTimeout(timeoutId);

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }

      const text = await response.text();
      return this.parseGroupNoticesResponse(text, groupId);
    } catch (err) {
      if (timeoutId) clearTimeout(timeoutId);
      throw err;
    }
  }

  private parseGroupNoticesResponse(rawText: string, groupId: string): SavedNotice[] {
    const notices: SavedNotice[] = [];
    try {
      if (rawText.trim().startsWith('{') || rawText.trim().startsWith('[')) {
        const data = JSON.parse(rawText);
        const items = Array.isArray(data) ? data : data.notices || data.notice_list || [];
        for (const item of items) {
          notices.push(this.mapNoticeItem(item, groupId));
        }
      } else {
        const noticeMatches = rawText.match(/<map>[\s\S]*?<\/map>/g) || [];
        for (const mapXml of noticeMatches) {
          const id = this.extractXmlTag(mapXml, 'notice_id') || this.extractXmlTag(mapXml, 'id') || `notice-${Date.now()}-${Math.random()}`;
          const subject = this.extractXmlTag(mapXml, 'subject') || 'Group Notice';
          const message = this.extractXmlTag(mapXml, 'message') || '';
          const from = this.extractXmlTag(mapXml, 'from_name') || this.extractXmlTag(mapXml, 'from') || 'Group Admin';
          const timestamp = Number(this.extractXmlTag(mapXml, 'timestamp')) || Date.now();
          const hasAtt = mapXml.includes('<key>has_attachment</key><boolean>1</boolean>') || mapXml.includes('<key>has_attachment</key><boolean>true</boolean>');

          notices.push({
            id,
            groupId,
            subject,
            message,
            from,
            timestamp,
            calendar: null,
            attachment: hasAtt ? {
              hasAttachment: true,
              attachmentName: this.extractXmlTag(mapXml, 'attachment_name') || 'Attached Item',
              attachmentItemId: this.extractXmlTag(mapXml, 'attachment_item_id') || null,
              attachmentType: Number(this.extractXmlTag(mapXml, 'attachment_type')) || null,
              attachmentOwnerId: this.extractXmlTag(mapXml, 'attachment_owner_id') || null,
              savedToInventoryAt: null,
            } : null,
            hasAttachment: hasAtt,
          });
        }
      }
    } catch (e) {
      console.error('[Groups] Failed parsing GroupNotices HTTP response:', e);
    }
    return notices;
  }

  private extractXmlTag(xml: string, tag: string): string | null {
    const regex = new RegExp(`<key>${tag}<\/key>\\s*<(?:string|uuid|integer|real|boolean)>([^<]*)<\/(?:string|uuid|integer|real|boolean)>`, 'i');
    const match = xml.match(regex);
    return match ? match[1] : null;
  }

  private mapNoticeItem(item: any, groupId: string): SavedNotice {
    return {
      id: item.notice_id || item.id || `notice-${Date.now()}-${Math.random()}`,
      groupId: item.group_id || groupId,
      subject: item.subject || 'Group Notice',
      message: item.message || '',
      from: item.from_name || item.from || 'Group Admin',
      timestamp: Number(item.timestamp) || Date.now(),
      calendar: null,
      attachment: item.has_attachment || item.attachment ? {
        hasAttachment: true,
        attachmentName: item.attachment_name || item.attachment?.attachmentName || null,
        attachmentItemId: item.attachment_item_id || item.attachment?.attachmentItemId || null,
        attachmentType: item.attachment_type ?? item.attachment?.attachmentType ?? null,
        attachmentOwnerId: item.attachment_owner_id || item.attachment?.attachmentOwnerId || null,
        savedToInventoryAt: null,
      } : null,
      hasAttachment: Boolean(item.has_attachment || item.attachment),
    };
  }

  private sendGroupNoticesListUdp(groupId: string, timeoutMs: number): Promise<SavedNotice[]> {
    return new Promise((resolve, reject) => {
      let resolved = false;
      const timeout = setTimeout(() => {
        if (!resolved) {
          resolved = true;
          reject(new Error('UDP GroupNoticesListRequest timed out'));
        }
      }, timeoutMs);

      const handleReply = (data: any) => {
        if (data && (data.groupId === groupId || data.GroupID === groupId || !data.groupId)) {
          if (!resolved) {
            resolved = true;
            clearTimeout(timeout);
            if (this.protocol && typeof this.protocol.off === 'function') {
              this.protocol.off('GroupNoticesListReply', handleReply);
              this.protocol.off('group_notices_list_reply', handleReply);
            }
            const items = Array.isArray(data.notices) ? data.notices : Array.isArray(data) ? data : [data];
            resolve(items.map((i: any) => this.mapNoticeItem(i, groupId)));
          }
        }
      };

      if (this.protocol && typeof this.protocol.on === 'function') {
        this.protocol.on('GroupNoticesListReply', handleReply);
        this.protocol.on('group_notices_list_reply', handleReply);
      }

      if (this.protocol && typeof this.protocol.send === 'function') {
        this.protocol.send('GroupNoticesListRequest', { GroupData: { GroupID: groupId } });
      } else {
        clearTimeout(timeout);
        reject(new Error('UDP transport unavailable'));
      }
    });
  }

  /**
   * Get user's groups
   */
  getUserGroups(userId: string) {
    const userGroups = [];
    for (const [groupId, members] of this.groupMembers) {
      if (members.has(userId)) {
        const groupInfo = this.groups.get(groupId);
        if (groupInfo) {
          userGroups.push(groupInfo);
        }
      }
    }
    return userGroups;
  }

  /** Snapshot of group records received during this session. */
  getGroups() {
    return Array.from(this.groups.values()).map(group => ({ ...group }));
  }

  getStats() {
    let totalMembers = 0;
    for (const members of this.groupMembers.values()) {
      totalMembers += members.size;
    }

    let totalRoles = 0;
    for (const roles of this.groupRoles.values()) {
      totalRoles += roles.size;
    }

    let totalNotices = 0;
    for (const notices of this.groupNotices.values()) {
      totalNotices += notices.length;
    }

    return {
      totalGroups: this.groups.size,
      totalMembers,
      totalRoles,
      totalNotices
    };
  }
}
