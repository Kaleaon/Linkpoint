/**
 * Linkpoint PWA - Groups Manager (Features 41-45)
 * Ported from Android slproto/users/ and message handlers
 * 
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/
 * See: PWA-demo/ANDROID_PORT_ROADMAP.md Phase 2, Priority 3 (Features 41-45)
 * 
 * Handles group-related functionality including group information, members,
 * roles, group chat, and group notices.
 */

/**
 * Group Powers - bitfield flags for group permissions
 */
export const GroupPowers = {
  NONE: 0n,
  LAND_DEED: 1n << 0n,
  LAND_RELEASE: 1n << 1n,
  LAND_SET_SALE: 1n << 2n,
  LAND_DIVIDE_JOIN: 1n << 3n,
  FIND_PLACES: 1n << 17n,
  LAND_CHANGE_IDENTITY: 1n << 18n,
  LAND_SET_LANDING_POINT: 1n << 19n,
  LAND_CHANGE_MEDIA: 1n << 20n,
  LAND_EDIT: 1n << 21n,
  LAND_OPTIONS: 1n << 22n,
  ALLOW_CREATE_EVENT: 1n << 27n,
  ALLOW_DELETE_EVENT: 1n << 28n,
  ALLOW_SEND_NOTICE: 1n << 29n,
  ALLOW_RECEIVE_NOTICE: 1n << 30n,
  ALLOW_START_PROPOSAL: 1n << 31n,
  ALLOW_VOTE_ON_PROPOSAL: 1n << 32n
};

/**
 * GroupsManager class
 * Manages group information, membership, and interactions
 */
class GroupsManager {
  constructor() {
    // Feature 41: Group info
    this.groups = new Map();
    this.activeGroupUUID = null;
    
    // Feature 42: Group members
    this.groupMembers = new Map(); // groupUUID -> Map of memberUUID -> member data
    
    // Feature 43: Group roles
    this.groupRoles = new Map(); // groupUUID -> Map of roleUUID -> role data
    
    // Feature 44: Group chat
    this.groupChatSessions = new Map(); // groupUUID -> chat session
    
    // Feature 45: Group notices
    this.groupNotices = new Map(); // groupUUID -> array of notices
    
    // TODO: Implement group capability requests
    // TODO: Connect to protocol layer
    // TODO: Implement group invitations
    // TODO: Add group land management
  }
  
  /**
   * Feature 41: Set/update group information
   * @param {Object} groupData - Group information
   */
  setGroupInfo(groupData) {
    this.groups.set(groupData.uuid, {
      uuid: groupData.uuid,
      name: groupData.name,
      charter: groupData.charter || '',
      insigniaID: groupData.insigniaID || null,
      founderID: groupData.founderID,
      membershipFee: groupData.membershipFee || 0,
      openEnrollment: groupData.openEnrollment || false,
      showInList: groupData.showInList || false,
      allowPublish: groupData.allowPublish || false,
      maturePublish: groupData.maturePublish || false,
      ownerRole: groupData.ownerRole || null,
      memberCount: groupData.memberCount || 0,
      roleCount: groupData.roleCount || 0,
      groupPowers: groupData.groupPowers || GroupPowers.NONE,
      money: groupData.money || 0
    });
  }
  
  /**
   * Get group information
   * @param {string} groupUUID - UUID of the group
   * @returns {Object|null} Group information or null if not found
   */
  getGroupInfo(groupUUID) {
    return this.groups.get(groupUUID) || null;
  }
  
  /**
   * Get all groups the user belongs to
   * @returns {Array} Array of group objects
   */
  getAllGroups() {
    return Array.from(this.groups.values());
  }
  
  /**
   * Set active group (group title shown in-world)
   * @param {string} groupUUID - UUID of group to activate (null for none)
   */
  setActiveGroup(groupUUID) {
    this.activeGroupUUID = groupUUID;
    // TODO: Send ActivateGroup message to server
  }
  
  /**
   * Get active group UUID
   * @returns {string|null} Active group UUID or null
   */
  getActiveGroup() {
    return this.activeGroupUUID;
  }
  
  /**
   * Feature 42: Add/update group member
   * @param {string} groupUUID - UUID of the group
   * @param {Object} memberData - Member information
   */
  setGroupMember(groupUUID, memberData) {
    if (!this.groupMembers.has(groupUUID)) {
      this.groupMembers.set(groupUUID, new Map());
    }
    
    const members = this.groupMembers.get(groupUUID);
    members.set(memberData.uuid, {
      uuid: memberData.uuid,
      title: memberData.title || '',
      isOwner: memberData.isOwner || false,
      contribution: memberData.contribution || 0,
      onlineStatus: memberData.onlineStatus || 'offline',
      powers: memberData.powers || GroupPowers.NONE,
      lastOnline: memberData.lastOnline || null
    });
  }
  
  /**
   * Get group members
   * @param {string} groupUUID - UUID of the group
   * @returns {Array} Array of member objects
   */
  getGroupMembers(groupUUID) {
    const members = this.groupMembers.get(groupUUID);
    return members ? Array.from(members.values()) : [];
  }
  
  /**
   * Get specific group member
   * @param {string} groupUUID - UUID of the group
   * @param {string} memberUUID - UUID of the member
   * @returns {Object|null} Member information or null
   */
  getGroupMember(groupUUID, memberUUID) {
    const members = this.groupMembers.get(groupUUID);
    return members ? members.get(memberUUID) || null : null;
  }
  
  /**
   * Feature 43: Add/update group role
   * @param {string} groupUUID - UUID of the group
   * @param {Object} roleData - Role information
   */
  setGroupRole(groupUUID, roleData) {
    if (!this.groupRoles.has(groupUUID)) {
      this.groupRoles.set(groupUUID, new Map());
    }
    
    const roles = this.groupRoles.get(groupUUID);
    roles.set(roleData.uuid, {
      uuid: roleData.uuid,
      name: roleData.name,
      title: roleData.title || '',
      description: roleData.description || '',
      powers: roleData.powers || GroupPowers.NONE,
      memberCount: roleData.memberCount || 0
    });
  }
  
  /**
   * Get group roles
   * @param {string} groupUUID - UUID of the group
   * @returns {Array} Array of role objects
   */
  getGroupRoles(groupUUID) {
    const roles = this.groupRoles.get(groupUUID);
    return roles ? Array.from(roles.values()) : [];
  }
  
  /**
   * Get specific group role
   * @param {string} groupUUID - UUID of the group
   * @param {string} roleUUID - UUID of the role
   * @returns {Object|null} Role information or null
   */
  getGroupRole(groupUUID, roleUUID) {
    const roles = this.groupRoles.get(groupUUID);
    return roles ? roles.get(roleUUID) || null : null;
  }
  
  /**
   * Check if member has specific power
   * @param {string} groupUUID - UUID of the group
   * @param {string} memberUUID - UUID of the member
   * @param {bigint} power - Power to check (from GroupPowers)
   * @returns {boolean} True if member has the power
   */
  hasPower(groupUUID, memberUUID, power) {
    const member = this.getGroupMember(groupUUID, memberUUID);
    if (!member) return false;
    
    return (member.powers & power) !== 0n;
  }
  
  /**
   * Feature 44: Send group chat message
   * @param {string} groupUUID - UUID of the group
   * @param {string} message - Chat message
   */
  sendGroupChat(groupUUID, message) {
    console.log(`Sending group chat to ${groupUUID}: ${message}`);
    
    // TODO: Send ImprovedInstantMessage with dialog type GROUP_CHAT
    // TODO: Route through group chat session
  }
  
  /**
   * Start group chat session
   * @param {string} groupUUID - UUID of the group
   */
  startGroupChatSession(groupUUID) {
    if (!this.groupChatSessions.has(groupUUID)) {
      this.groupChatSessions.set(groupUUID, {
        groupUUID,
        sessionID: this.generateSessionID(),
        messages: [],
        participants: new Set()
      });
    }
    
    // TODO: Send StartGroupChatSession capability request
  }
  
  /**
   * End group chat session
   * @param {string} groupUUID - UUID of the group
   */
  endGroupChatSession(groupUUID) {
    this.groupChatSessions.delete(groupUUID);
    // TODO: Send leave session message
  }
  
  /**
   * Add message to group chat session
   * @param {string} groupUUID - UUID of the group
   * @param {Object} message - Chat message
   */
  addGroupChatMessage(groupUUID, message) {
    const session = this.groupChatSessions.get(groupUUID);
    if (session) {
      session.messages.push({
        timestamp: Date.now(),
        fromUUID: message.fromUUID,
        fromName: message.fromName,
        message: message.message
      });
    }
  }
  
  /**
   * Get group chat history
   * @param {string} groupUUID - UUID of the group
   * @returns {Array} Array of chat messages
   */
  getGroupChatHistory(groupUUID) {
    const session = this.groupChatSessions.get(groupUUID);
    return session ? session.messages : [];
  }
  
  /**
   * Feature 45: Add group notice
   * @param {string} groupUUID - UUID of the group
   * @param {Object} noticeData - Notice information
   */
  addGroupNotice(groupUUID, noticeData) {
    if (!this.groupNotices.has(groupUUID)) {
      this.groupNotices.set(groupUUID, []);
    }
    
    const notices = this.groupNotices.get(groupUUID);
    notices.unshift({
      noticeID: noticeData.noticeID,
      timestamp: noticeData.timestamp || Date.now(),
      fromName: noticeData.fromName,
      subject: noticeData.subject,
      message: noticeData.message,
      hasInventory: noticeData.hasInventory || false,
      inventoryType: noticeData.inventoryType || null,
      inventoryName: noticeData.inventoryName || null
    });
    
    // Keep only last 50 notices
    if (notices.length > 50) {
      notices.length = 50;
    }
  }
  
  /**
   * Get group notices
   * @param {string} groupUUID - UUID of the group
   * @returns {Array} Array of notice objects
   */
  getGroupNotices(groupUUID) {
    return this.groupNotices.get(groupUUID) || [];
  }
  
  /**
   * Send group notice
   * @param {string} groupUUID - UUID of the group
   * @param {string} subject - Notice subject
   * @param {string} message - Notice message
   * @param {Object} attachment - Optional inventory attachment
   */
  sendGroupNotice(groupUUID, subject, message, attachment = null) {
    console.log(`Sending group notice to ${groupUUID}: ${subject}`);
    
    // TODO: Send GroupNoticeRequest capability request
    // TODO: Validate permissions (ALLOW_SEND_NOTICE)
  }
  
  /**
   * Join group
   * @param {string} groupUUID - UUID of the group to join
   */
  async joinGroup(groupUUID) {
    console.log(`Joining group ${groupUUID}`);
    
    // TODO: Send JoinGroupRequest
    // TODO: Handle membership fee if required
    // TODO: Update group list on success
  }
  
  /**
   * Leave group
   * @param {string} groupUUID - UUID of the group to leave
   */
  async leaveGroup(groupUUID) {
    console.log(`Leaving group ${groupUUID}`);
    
    // TODO: Send LeaveGroupRequest
    // TODO: Remove from groups map
    // TODO: Clear associated data
    
    this.groups.delete(groupUUID);
    this.groupMembers.delete(groupUUID);
    this.groupRoles.delete(groupUUID);
    this.groupNotices.delete(groupUUID);
    
    if (this.activeGroupUUID === groupUUID) {
      this.activeGroupUUID = null;
    }
  }
  
  /**
   * Generate session ID
   * @returns {string} Session ID
   */
  generateSessionID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }
}

// Export singleton instance
const groupsManager = new GroupsManager();
export default groupsManager;
export { GroupsManager, GroupPowers };
