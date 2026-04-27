/**
 * Linkpoint PWA - Friends Extended (Features 46-50)
 * Ported from Android slproto/users/
 * 
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/
 * See: PWA-demo/ANDROID_PORT_ROADMAP.md Phase 2, Priority 3 (Features 46-50)
 * 
 * Handles extended friend functionality including friend requests, online
 * notifications, friend permissions, calling cards, and friend groups.
 */

/**
 * Friend Rights - permission flags
 */
export const FriendRights = {
  NONE: 0,
  CAN_SEE_ONLINE: 1 << 0,
  CAN_SEE_ON_MAP: 1 << 1,
  CAN_MODIFY_OBJECTS: 1 << 2
};

/**
 * Friend Status
 */
export const FriendStatus = {
  ONLINE: 'online',
  OFFLINE: 'offline',
  UNKNOWN: 'unknown'
};

/**
 * FriendsExtended class
 * Manages friend relationships and interactions
 */
class FriendsExtended {
  constructor() {
    // Feature 46: Friend requests
    this.pendingRequests = new Map(); // UUID -> request data
    this.sentRequests = new Map(); // UUID -> request data
    
    // Feature 47: Online notifications
    this.friends = new Map(); // UUID -> friend data
    this.onlineStatusCallbacks = [];
    this.statusChangeHistory = []; // Track status changes
    
    // Feature 48: Friend permissions
    this.myRights = new Map(); // UUID -> rights I've granted
    this.theirRights = new Map(); // UUID -> rights they've granted me
    
    // Feature 49: Calling cards (stored in inventory, tracked here)
    this.callingCards = new Map(); // UUID -> calling card data
    
    // Feature 50: Friend groups/folders
    this.friendGroups = new Map(); // groupName -> Set of UUIDs
    this.defaultGroups = ['Close Friends', 'Acquaintances', 'Business'];
    
    // Additional features
    this.recentInteractions = []; // Track recent interactions with friends
    this.blockedUsers = new Set(); // Blocked users
    this.changeCallbacks = [];
    
    this.initDefaultGroups();
  }
  
  /**
   * Initialize default friend groups
   */
  initDefaultGroups() {
    this.defaultGroups.forEach(groupName => {
      if (!this.friendGroups.has(groupName)) {
        this.friendGroups.set(groupName, new Set());
      }
    });
  }
  
  /**
   * Feature 46: Send friend request
   * @param {string} targetUUID - UUID of user to befriend
   * @param {string} message - Optional message with request
   */
  sendFriendRequest(targetUUID, message = '') {
    console.log(`Sending friend request to ${targetUUID}`);
    
    const request = {
      targetUUID,
      message,
      timestamp: Date.now(),
      status: 'pending'
    };
    
    this.sentRequests.set(targetUUID, request);
    
    // TODO: Send OfferCallingCard message
    // TODO: Wait for acceptance/decline
  }
  
  /**
   * Accept friend request
   * @param {string} fromUUID - UUID of requester
   */
  acceptFriendRequest(fromUUID) {
    console.log(`Accepting friend request from ${fromUUID}`);
    
    const request = this.pendingRequests.get(fromUUID);
    if (!request) {
      console.warn('Friend request not found');
      return;
    }
    
    // Add to friends list
    this.addFriend({
      uuid: fromUUID,
      rightsGiven: FriendRights.CAN_SEE_ONLINE,
      rightsHas: FriendRights.CAN_SEE_ONLINE
    });
    
    this.pendingRequests.delete(fromUUID);
    
    // TODO: Send AcceptFriendship message
    // TODO: Create calling card in inventory
  }
  
  /**
   * Decline friend request
   * @param {string} fromUUID - UUID of requester
   */
  declineFriendRequest(fromUUID) {
    console.log(`Declining friend request from ${fromUUID}`);
    
    // Get session ID before deleting the request
    const request = this.pendingRequests.get(fromUUID);
    const sessionID = request ? request.sessionID : null;

    this.pendingRequests.delete(fromUUID);
    
    // Send DeclineFriendship message via protocol manager
    if (window.app && window.app.protocol) {
      window.app.protocol.sendMessage('DeclineFriendship', {
        agent_id: window.app.auth.user.id,
        session_id: window.app.protocol.sessionId,
        transaction_id: sessionID || '00000000-0000-0000-0000-000000000000'
      });
    }
  }
  
  /**
   * Receive friend request
   * @param {Object} requestData - Request information
   */
  receiveFriendRequest(requestData) {
    this.pendingRequests.set(requestData.fromUUID, {
      fromUUID: requestData.fromUUID,
      fromName: requestData.fromName,
      message: requestData.message || '',
      timestamp: Date.now(),
      sessionID: requestData.sessionID
    });
    
    // Notify callbacks
    this.notifyFriendRequestReceived(requestData);
  }
  
  /**
   * Get pending friend requests
   * @returns {Array} Array of pending request objects
   */
  getPendingRequests() {
    return Array.from(this.pendingRequests.values());
  }
  
  /**
   * Get sent friend requests
   * @returns {Array} Array of sent request objects
   */
  getSentRequests() {
    return Array.from(this.sentRequests.values());
  }
  
  /**
   * Add friend to list
   * @param {Object} friendData - Friend information
   */
  addFriend(friendData) {
    this.friends.set(friendData.uuid, {
      uuid: friendData.uuid,
      name: friendData.name || '',
      status: friendData.status || FriendStatus.UNKNOWN,
      rightsGiven: friendData.rightsGiven || FriendRights.CAN_SEE_ONLINE,
      rightsHas: friendData.rightsHas || FriendRights.CAN_SEE_ONLINE,
      onlineSince: friendData.onlineSince || null,
      regionName: friendData.regionName || '',
      groups: friendData.groups || []
    });
    
    // Store rights
    this.myRights.set(friendData.uuid, friendData.rightsGiven);
    this.theirRights.set(friendData.uuid, friendData.rightsHas);
  }
  
  /**
   * Remove friend
   * @param {string} friendUUID - UUID of friend to remove
   */
  removeFriend(friendUUID) {
    console.log(`Removing friend ${friendUUID}`);
    
    this.friends.delete(friendUUID);
    this.myRights.delete(friendUUID);
    this.theirRights.delete(friendUUID);
    
    // Remove from all groups
    this.friendGroups.forEach(group => {
      group.delete(friendUUID);
    });
    
    // TODO: Send TerminateFriendship message
    // TODO: Remove calling card from inventory
  }
  
  /**
   * Get friend
   * @param {string} friendUUID - UUID of friend
   * @returns {Object|null} Friend data or null
   */
  getFriend(friendUUID) {
    return this.friends.get(friendUUID) || null;
  }
  
  /**
   * Get all friends
   * @param {Object} filters - Optional filters (status, group, etc.)
   * @returns {Array} Array of friend objects
   */
  getAllFriends(filters = {}) {
    let friends = Array.from(this.friends.values());
    
    // Filter by status
    if (filters.status) {
      friends = friends.filter(f => f.status === filters.status);
    }
    
    // Filter by group
    if (filters.group) {
      const groupMembers = this.friendGroups.get(filters.group);
      if (groupMembers) {
        friends = friends.filter(f => groupMembers.has(f.uuid));
      } else {
        friends = [];
      }
    }
    
    return friends;
  }
  
  /**
   * Feature 47: Update friend online status
   * @param {string} friendUUID - UUID of friend
   * @param {boolean} isOnline - True if friend is online
   * @param {Object} details - Additional details (region, etc.)
   */
  updateFriendStatus(friendUUID, isOnline, details = {}) {
    const friend = this.friends.get(friendUUID);
    if (!friend) return;
    
    const wasOnline = friend.status === FriendStatus.ONLINE;
    friend.status = isOnline ? FriendStatus.ONLINE : FriendStatus.OFFLINE;
    friend.onlineSince = isOnline ? (details.onlineSince || Date.now()) : null;
    friend.regionName = details.regionName || '';
    
    // Notify if status changed
    if (wasOnline !== isOnline) {
      this.notifyStatusChange(friendUUID, isOnline);
    }
  }
  
  /**
   * Register callback for friend online/offline notifications
   * @param {Function} callback - Callback function(friendUUID, isOnline)
   */
  onStatusChange(callback) {
    this.onlineStatusCallbacks.push(callback);
  }
  
  /**
   * Notify listeners of status change
   * @param {string} friendUUID - UUID of friend
   * @param {boolean} isOnline - True if friend came online
   */
  notifyStatusChange(friendUUID, isOnline) {
    const friend = this.getFriend(friendUUID);
    this.onlineStatusCallbacks.forEach(cb => {
      cb(friendUUID, isOnline, friend);
    });
  }
  
  /**
   * Notify listeners of friend request received
   * @param {Object} requestData - Request data
   */
  notifyFriendRequestReceived(requestData) {
    // TODO: Implement notification system
    console.log('Friend request received:', requestData);
  }
  
  /**
   * Feature 48: Grant rights to friend
   * @param {string} friendUUID - UUID of friend
   * @param {number} rights - Rights to grant (bitfield from FriendRights)
   */
  grantRights(friendUUID, rights) {
    console.log(`Granting rights ${rights} to friend ${friendUUID}`);
    
    this.myRights.set(friendUUID, rights);
    
    const friend = this.friends.get(friendUUID);
    if (friend) {
      friend.rightsGiven = rights;
    }
    
    // TODO: Send GrantUserRights message
  }
  
  /**
   * Get rights granted to friend
   * @param {string} friendUUID - UUID of friend
   * @returns {number} Rights bitfield
   */
  getRightsGranted(friendUUID) {
    return this.myRights.get(friendUUID) || FriendRights.NONE;
  }
  
  /**
   * Get rights granted by friend
   * @param {string} friendUUID - UUID of friend
   * @returns {number} Rights bitfield
   */
  getRightsReceived(friendUUID) {
    return this.theirRights.get(friendUUID) || FriendRights.NONE;
  }
  
  /**
   * Check if friend has granted specific right
   * @param {string} friendUUID - UUID of friend
   * @param {number} right - Right to check (from FriendRights)
   * @returns {boolean} True if right is granted
   */
  hasRight(friendUUID, right) {
    const rights = this.theirRights.get(friendUUID) || FriendRights.NONE;
    return (rights & right) !== 0;
  }
  
  /**
   * Feature 49: Store calling card
   * @param {string} friendUUID - UUID of friend
   * @param {string} callingCardUUID - UUID of calling card inventory item
   */
  storeCallingCard(friendUUID, callingCardUUID) {
    this.callingCards.set(friendUUID, {
      uuid: callingCardUUID,
      friendUUID: friendUUID,
      timestamp: Date.now()
    });
  }
  
  /**
   * Get calling card for friend
   * @param {string} friendUUID - UUID of friend
   * @returns {Object|null} Calling card data or null
   */
  getCallingCard(friendUUID) {
    return this.callingCards.get(friendUUID) || null;
  }
  
  /**
   * Feature 50: Create friend group
   * @param {string} groupName - Name of the group
   */
  createFriendGroup(groupName) {
    if (!this.friendGroups.has(groupName)) {
      this.friendGroups.set(groupName, new Set());
    }
  }
  
  /**
   * Delete friend group
   * @param {string} groupName - Name of the group
   */
  deleteFriendGroup(groupName) {
    this.friendGroups.delete(groupName);
  }
  
  /**
   * Add friend to group
   * @param {string} friendUUID - UUID of friend
   * @param {string} groupName - Name of the group
   */
  addToGroup(friendUUID, groupName) {
    if (!this.friendGroups.has(groupName)) {
      this.createFriendGroup(groupName);
    }
    
    this.friendGroups.get(groupName).add(friendUUID);
    
    const friend = this.friends.get(friendUUID);
    if (friend) {
      if (!friend.groups.includes(groupName)) {
        friend.groups.push(groupName);
      }
    }
  }
  
  /**
   * Remove friend from group
   * @param {string} friendUUID - UUID of friend
   * @param {string} groupName - Name of the group
   */
  removeFromGroup(friendUUID, groupName) {
    const group = this.friendGroups.get(groupName);
    if (group) {
      group.delete(friendUUID);
    }
    
    const friend = this.friends.get(friendUUID);
    if (friend) {
      friend.groups = friend.groups.filter(g => g !== groupName);
    }
  }
  
  /**
   * Get friend groups
   * @returns {Array} Array of group names
   */
  getFriendGroups() {
    return Array.from(this.friendGroups.keys());
  }
  
  /**
   * Get friends in group
   * @param {string} groupName - Name of the group
   * @returns {Array} Array of friend UUIDs
   */
  getFriendsInGroup(groupName) {
    const group = this.friendGroups.get(groupName);
    return group ? Array.from(group) : [];
  }
  
  /**
   * Search friends by name
   * @param {string} query - Search query
   * @returns {Array} Array of matching friends
   */
  searchFriends(query) {
    const lowerQuery = query.toLowerCase();
    const results = [];
    
    this.friends.forEach((friend, uuid) => {
      if (friend.name.toLowerCase().includes(lowerQuery)) {
        results.push({ uuid, ...friend });
      }
    });
    
    return results;
  }
  
  /**
   * Get friends sorted by last online time
   * @param {number} limit - Number of friends to return
   * @returns {Array} Array of friends
   */
  getRecentlyOnlineFriends(limit = 10) {
    const friendsArray = Array.from(this.friends.entries())
      .map(([uuid, friend]) => ({ uuid, ...friend }))
      .filter(f => f.onlineSince);
    
    friendsArray.sort((a, b) => b.onlineSince - a.onlineSince);
    
    return friendsArray.slice(0, limit);
  }
  
  /**
   * Track interaction with friend
   * @param {string} friendUUID - UUID of friend
   * @param {string} type - Type of interaction (chat, teleport, etc.)
   */
  recordInteraction(friendUUID, type) {
    this.recentInteractions.push({
      friendUUID,
      type,
      timestamp: Date.now()
    });
    
    // Keep only last 100 interactions
    if (this.recentInteractions.length > 100) {
      this.recentInteractions.shift();
    }
    
    // Update friend's last interaction time
    const friend = this.friends.get(friendUUID);
    if (friend) {
      friend.lastInteraction = Date.now();
    }
  }
  
  /**
   * Get recent interactions
   * @param {number} limit - Number of interactions to return
   * @returns {Array} Array of interactions
   */
  getRecentInteractions(limit = 20) {
    return this.recentInteractions.slice(-limit);
  }
  
  /**
   * Get interactions with specific friend
   * @param {string} friendUUID - UUID of friend
   * @returns {Array} Array of interactions
   */
  getInteractionsWith(friendUUID) {
    return this.recentInteractions.filter(i => i.friendUUID === friendUUID);
  }
  
  /**
   * Block user
   * @param {string} userUUID - UUID of user to block
   */
  blockUser(userUUID) {
    this.blockedUsers.add(userUUID);
    
    // Remove from friends if present
    this.removeFriend(userUUID);
    
    // TODO: Send block message to server
  }
  
  /**
   * Unblock user
   * @param {string} userUUID - UUID of user to unblock
   */
  unblockUser(userUUID) {
    this.blockedUsers.delete(userUUID);
    // TODO: Send unblock message to server
  }
  
  /**
   * Check if user is blocked
   * @param {string} userUUID - UUID of user
   * @returns {boolean} True if blocked
   */
  isBlocked(userUUID) {
    return this.blockedUsers.has(userUUID);
  }
  
  /**
   * Get blocked users
   * @returns {Array} Array of blocked user UUIDs
   */
  getBlockedUsers() {
    return Array.from(this.blockedUsers);
  }
  
  /**
   * Get friend statistics
   * @returns {Object} Friend statistics
   */
  getStatistics() {
    const onlineFriends = Array.from(this.friends.values())
      .filter(f => f.status === FriendStatus.ONLINE);
    
    return {
      totalFriends: this.friends.size,
      onlineFriends: onlineFriends.length,
      offlineFriends: this.friends.size - onlineFriends.length,
      pendingRequests: this.pendingRequests.size,
      sentRequests: this.sentRequests.size,
      totalGroups: this.friendGroups.size,
      blockedUsers: this.blockedUsers.size,
      recentInteractions: this.recentInteractions.length
    };
  }
  
  /**
   * Get friends with specific permission
   * @param {number} permission - Friend right flag
   * @returns {Array} Array of friend UUIDs
   */
  getFriendsWithPermission(permission) {
    const results = [];
    
    this.myRights.forEach((rights, uuid) => {
      if (rights & permission) {
        results.push(uuid);
      }
    });
    
    return results;
  }
  
  /**
   * Check if friend can see me online
   * @param {string} friendUUID - UUID of friend
   * @returns {boolean} True if they can see online status
   */
  canFriendSeeOnline(friendUUID) {
    const rights = this.myRights.get(friendUUID) || 0;
    return (rights & FriendRights.CAN_SEE_ONLINE) !== 0;
  }
  
  /**
   * Check if friend can see me on map
   * @param {string} friendUUID - UUID of friend
   * @returns {boolean} True if they can see on map
   */
  canFriendSeeOnMap(friendUUID) {
    const rights = this.myRights.get(friendUUID) || 0;
    return (rights & FriendRights.CAN_SEE_ON_MAP) !== 0;
  }
  
  /**
   * Check if friend can modify my objects
   * @param {string} friendUUID - UUID of friend
   * @returns {boolean} True if they can modify objects
   */
  canFriendModifyObjects(friendUUID) {
    const rights = this.myRights.get(friendUUID) || 0;
    return (rights & FriendRights.CAN_MODIFY_OBJECTS) !== 0;
  }
  
  /**
   * Get status change history
   * @param {number} limit - Number of changes to return
   * @returns {Array} Array of status changes
   */
  getStatusChangeHistory(limit = 50) {
    return this.statusChangeHistory.slice(-limit);
  }
  
  /**
   * Export friends list to JSON
   * @returns {string} JSON string of friends
   */
  exportToJSON() {
    const friendsData = {
      friends: Array.from(this.friends.entries()).map(([uuid, friend]) => ({
        uuid,
        ...friend
      })),
      groups: Array.from(this.friendGroups.entries()).map(([name, members]) => ({
        name,
        members: Array.from(members)
      })),
      myRights: Array.from(this.myRights.entries()).map(([uuid, rights]) => ({ uuid, rights })),
      theirRights: Array.from(this.theirRights.entries()).map(([uuid, rights]) => ({ uuid, rights }))
    };
    
    return JSON.stringify(friendsData, null, 2);
  }
  
  /**
   * Import friends from JSON
   * @param {string} jsonData - JSON string of friends data
   */
  importFromJSON(jsonData) {
    try {
      const data = JSON.parse(jsonData);
      
      if (data.friends) {
        data.friends.forEach(friend => {
          this.friends.set(friend.uuid, friend);
        });
      }
      
      if (data.groups) {
        data.groups.forEach(group => {
          this.friendGroups.set(group.name, new Set(group.members));
        });
      }
      
      if (data.myRights) {
        data.myRights.forEach(({ uuid, rights }) => {
          this.myRights.set(uuid, rights);
        });
      }
      
      if (data.theirRights) {
        data.theirRights.forEach(({ uuid, rights }) => {
          this.theirRights.set(uuid, rights);
        });
      }
      
      console.log('Successfully imported friends data');
    } catch (error) {
      console.error('Failed to import friends data:', error);
      throw error;
    }
  }
  
  /**
   * Register change callback
   * @param {Function} callback - Callback function
   */
  onChange(callback) {
    this.changeCallbacks.push(callback);
  }
  
  /**
   * Notify change
   * @param {string} event - Event type
   * @param {string} friendUUID - UUID of affected friend
   */
  notifyChange(event, friendUUID) {
    this.changeCallbacks.forEach(cb => {
      try {
        cb(event, friendUUID);
      } catch (error) {
        console.error('Error in friend change callback:', error);
      }
    });
  }
  
  /**
   * Clear all friend data
   */
  clear() {
    this.friends.clear();
    this.pendingRequests.clear();
    this.sentRequests.clear();
    this.myRights.clear();
    this.theirRights.clear();
    this.callingCards.clear();
    this.recentInteractions = [];
    this.statusChangeHistory = [];
    this.blockedUsers.clear();
    
    // Keep default groups
    this.friendGroups.clear();
    this.initDefaultGroups();
  }
}

// Export singleton instance
const friendsExtended = new FriendsExtended();
export default friendsExtended;
export { FriendsExtended, FriendRights, FriendStatus };
