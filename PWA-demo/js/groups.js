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
    this.roleMembers = new Map(); // groupUUID -> Map of roleUUID -> Set of memberUUIDs
    
    // Feature 44: Group chat
    this.groupChatSessions = new Map(); // groupUUID -> chat session
    this.chatHistories = new Map(); // groupUUID -> message array
    
    // Feature 45: Group notices
    this.groupNotices = new Map(); // groupUUID -> array of notices
    
    // Additional features
    this.groupInvitations = new Map(); // Pending invitations
    this.groupLandParcels = new Map(); // groupUUID -> array of parcel data
    this.groupProposals = new Map(); // groupUUID -> array of proposals
    this.changeCallbacks = [];
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
   * @param {string} senderUUID - UUID of the sender
   * @param {string} subject - Notice subject
   * @param {string} message - Notice message
   * @param {Object} attachment - Optional inventory attachment
   */
  sendGroupNotice(groupUUID, senderUUID, subject, message, attachment = null) {
    console.log(`Sending group notice to ${groupUUID}: ${subject}`);
    
    // Validate permissions (ALLOW_SEND_NOTICE)
    if (!this.hasPower(groupUUID, senderUUID, GroupPowers.ALLOW_SEND_NOTICE)) {
      console.warn(`User ${senderUUID} missing ALLOW_SEND_NOTICE permission for group ${groupUUID}`);
      throw new Error("You do not have permission to send notices to this group.");
    }

    // TODO: Send GroupNoticeRequest capability request
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
  
  /**
   * Send group invitation
   * @param {string} groupUUID - UUID of the group
   * @param {string} targetUUID - UUID of user to invite
   * @param {string} roleUUID - UUID of role to assign
   */
  async sendInvitation(groupUUID, targetUUID, roleUUID) {
    console.log(`Sending invitation to ${targetUUID} for group ${groupUUID}`);
    
    const invitation = {
      groupUUID,
      targetUUID,
      roleUUID,
      timestamp: Date.now(),
      status: 'pending'
    };
    
    this.groupInvitations.set(targetUUID, invitation);
    // TODO: Send InviteGroupMember message
  }
  
  /**
   * Accept group invitation
   * @param {string} groupUUID - UUID of the group
   */
  async acceptInvitation(groupUUID) {
    const invitation = this.groupInvitations.get(groupUUID);
    if (!invitation) {
      console.warn('No pending invitation for this group');
      return false;
    }
    
    invitation.status = 'accepted';
    this.groupInvitations.delete(groupUUID);
    
    // TODO: Send AcceptInvitation message
    return true;
  }
  
  /**
   * Decline group invitation
   * @param {string} groupUUID - UUID of the group
   */
  declineInvitation(groupUUID) {
    this.groupInvitations.delete(groupUUID);
    // TODO: Send DeclineInvitation message
  }
  
  /**
   * Get pending invitations
   * @returns {Array} Array of pending invitations
   */
  getPendingInvitations() {
    return Array.from(this.groupInvitations.values())
      .filter(inv => inv.status === 'pending');
  }
  
  /**
   * Assign member to role
   * @param {string} groupUUID - UUID of the group
   * @param {string} memberUUID - UUID of the member
   * @param {string} roleUUID - UUID of the role
   */
  assignMemberToRole(groupUUID, memberUUID, roleUUID) {
    if (!this.roleMembers.has(groupUUID)) {
      this.roleMembers.set(groupUUID, new Map());
    }
    
    const roleMap = this.roleMembers.get(groupUUID);
    if (!roleMap.has(roleUUID)) {
      roleMap.set(roleUUID, new Set());
    }
    
    roleMap.get(roleUUID).add(memberUUID);
    
    // Update member data
    const member = this.getGroupMember(groupUUID, memberUUID);
    if (member) {
      const role = this.getGroupRole(groupUUID, roleUUID);
      if (role) {
        member.title = role.title;
        member.powers = role.powers;
      }
    }
    
    // TODO: Send UpdateGroupMemberRole message
  }
  
  /**
   * Remove member from role
   * @param {string} groupUUID - UUID of the group
   * @param {string} memberUUID - UUID of the member
   * @param {string} roleUUID - UUID of the role
   */
  removeMemberFromRole(groupUUID, memberUUID, roleUUID) {
    const roleMap = this.roleMembers.get(groupUUID);
    if (!roleMap) return;
    
    const members = roleMap.get(roleUUID);
    if (members) {
      members.delete(memberUUID);
    }
    
    // TODO: Send UpdateGroupMemberRole message
  }
  
  /**
   * Get members in specific role
   * @param {string} groupUUID - UUID of the group
   * @param {string} roleUUID - UUID of the role
   * @returns {Array} Array of member UUIDs
   */
  getMembersInRole(groupUUID, roleUUID) {
    const roleMap = this.roleMembers.get(groupUUID);
    if (!roleMap) return [];
    
    const members = roleMap.get(roleUUID);
    return members ? Array.from(members) : [];
  }
  
  // Removed duplicate getGroupChatHistory method (see line 510 for the active implementation)
  
  /**
   * Clear group chat history
   * @param {string} groupUUID - UUID of the group
   */
  clearGroupChatHistory(groupUUID) {
    this.chatHistories.delete(groupUUID);
  }
  
  /**
   * Eject member from group
   * @param {string} groupUUID - UUID of the group
   * @param {string} memberUUID - UUID of member to eject
   */
  async ejectMember(groupUUID, memberUUID) {
    console.log(`Ejecting member ${memberUUID} from group ${groupUUID}`);
    
    const members = this.groupMembers.get(groupUUID);
    if (members) {
      members.delete(memberUUID);
    }
    
    // Remove from all roles
    const roleMap = this.roleMembers.get(groupUUID);
    if (roleMap) {
      roleMap.forEach(members => members.delete(memberUUID));
    }
    
    // TODO: Send EjectGroupMember message
  }
  
  /**
   * Update group charter
   * @param {string} groupUUID - UUID of the group
   * @param {string} charter - New charter text
   */
  updateCharter(groupUUID, charter) {
    const group = this.groups.get(groupUUID);
    if (group) {
      group.charter = charter;
      this.notifyChange('group-updated', groupUUID);

      if (window.app && window.app.protocol) {
        window.app.protocol.sendMessage('UpdateGroupInfo', {
          uuid: group.uuid,
          charter: group.charter,
          showInList: group.showInList,
          insigniaID: group.insigniaID,
          membershipFee: group.membershipFee,
          openEnrollment: group.openEnrollment,
          allowPublish: group.allowPublish,
          maturePublish: group.maturePublish
        });
      }
    }
  }
  
  /**
   * Set group insignia
   * @param {string} groupUUID - UUID of the group
   * @param {string} insigniaID - Texture UUID for group insignia
   */
  setInsignia(groupUUID, insigniaID) {
    const group = this.groups.get(groupUUID);
    if (group) {
      group.insigniaID = insigniaID;
      this.notifyChange('group-updated', groupUUID);

      if (window.app && window.app.protocol) {
        window.app.protocol.sendMessage('UpdateGroupInfo', {
          uuid: group.uuid,
          charter: group.charter,
          showInList: group.showInList,
          insigniaID: group.insigniaID,
          membershipFee: group.membershipFee,
          openEnrollment: group.openEnrollment,
          allowPublish: group.allowPublish,
          maturePublish: group.maturePublish
        });
      }
    }
  }
  
  /**
   * Add land parcel to group
   * @param {string} groupUUID - UUID of the group
   * @param {Object} parcelData - Parcel information
   */
  addLandParcel(groupUUID, parcelData) {
    if (!this.groupLandParcels.has(groupUUID)) {
      this.groupLandParcels.set(groupUUID, []);
    }
    
    this.groupLandParcels.get(groupUUID).push({
      parcelID: parcelData.parcelID,
      name: parcelData.name,
      area: parcelData.area,
      location: parcelData.location,
      forSale: parcelData.forSale || false
    });
  }
  
  /**
   * Get group land parcels
   * @param {string} groupUUID - UUID of the group
   * @returns {Array} Array of parcel data
   */
  getLandParcels(groupUUID) {
    return this.groupLandParcels.get(groupUUID) || [];
  }
  
  /**
   * Create group proposal
   * @param {string} groupUUID - UUID of the group
   * @param {Object} proposalData - Proposal information
   */
  createProposal(groupUUID, proposalData) {
    if (!this.groupProposals.has(groupUUID)) {
      this.groupProposals.set(groupUUID, []);
    }
    
    const proposal = {
      proposalID: this.generateSessionID(),
      title: proposalData.title,
      text: proposalData.text,
      createdBy: proposalData.createdBy,
      createdAt: Date.now(),
      votesFor: 0,
      votesAgainst: 0,
      quorum: proposalData.quorum || 0.5,
      duration: proposalData.duration || 604800000, // 7 days
      status: 'active',
      votes: new Map()
    };
    
    this.groupProposals.get(groupUUID).push(proposal);
    
    // TODO: Send CreateProposal message
    return proposal.proposalID;
  }
  
  /**
   * Vote on proposal
   * @param {string} groupUUID - UUID of the group
   * @param {string} proposalID - UUID of the proposal
   * @param {boolean} vote - True for yes, false for no
   * @param {string} voterUUID - UUID of voter
   */
  voteOnProposal(groupUUID, proposalID, vote, voterUUID) {
    const proposals = this.groupProposals.get(groupUUID);
    if (!proposals) return;
    
    const proposal = proposals.find(p => p.proposalID === proposalID);
    if (!proposal) return;
    
    // Record vote
    proposal.votes.set(voterUUID, vote);
    
    // Update counts
    proposal.votesFor = 0;
    proposal.votesAgainst = 0;
    proposal.votes.forEach(v => {
      if (v) proposal.votesFor++;
      else proposal.votesAgainst++;
    });
    
    // TODO: Send VoteOnProposal message
  }
  
  /**
   * Get group proposals
   * @param {string} groupUUID - UUID of the group
   * @returns {Array} Array of proposals
   */
  getProposals(groupUUID) {
    return this.groupProposals.get(groupUUID) || [];
  }
  
  /**
   * Register change callback
   * @param {Function} callback - Callback function(event, groupUUID)
   */
  onChange(callback) {
    this.changeCallbacks.push(callback);
  }
  
  /**
   * Notify change
   * @param {string} event - Event type
   * @param {string} groupUUID - UUID of affected group
   */
  notifyChange(event, groupUUID) {
    this.changeCallbacks.forEach(cb => {
      try {
        cb(event, groupUUID);
      } catch (error) {
        console.error('Error in group change callback:', error);
      }
    });
  }
  
  /**
   * Get all groups summary
   * @returns {Object} Summary statistics
   */
  getSummary() {
    return {
      totalGroups: this.groups.size,
      activeGroup: this.activeGroupUUID,
      totalMembers: Array.from(this.groupMembers.values())
        .reduce((sum, members) => sum + members.size, 0),
      totalNotices: Array.from(this.groupNotices.values())
        .reduce((sum, notices) => sum + notices.length, 0),
      pendingInvitations: this.getPendingInvitations().length
    };
  }
}

// Export singleton instance
const groupsManager = new GroupsManager();
export default groupsManager;
export { GroupsManager };
