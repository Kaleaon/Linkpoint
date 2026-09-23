/**
 * Linkpoint PWA - Groups System (Features 41-45)
 *
 * Phase 2: Core Protocol Extensions - Priority 3
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 83-88)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/groups/
 *
 * Manages group information, members, roles, chat, and notices.
 */

export class GroupsManager {
  private protocol: any;
  private groups: Map<string, any> = new Map();
  private groupMembers: Map<string, Map<string, any>> = new Map();
  private groupRoles: Map<string, Map<string, any>> = new Map();
  private groupNotices: Map<string, any[]> = new Map();

  constructor(protocolManager?: any) {
    this.protocol = protocolManager;
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
