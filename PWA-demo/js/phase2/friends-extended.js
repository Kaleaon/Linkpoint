/**
 * Linkpoint PWA - Friends Extensions (Features 46-50)
 * 
 * Phase 2: Core Protocol Extensions - Priority 3
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 90-95)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/
 * 
 * Extends friend functionality with requests, notifications, permissions, calling cards, and groups.
 * 
 * Features:
 * - Feature 46: Friend requests
 * - Feature 47: Online notifications
 * - Feature 48: Friend permissions
 * - Feature 49: Calling cards
 * - Feature 50: Friend groups
 * 
 * @module phase2/friends-extended
 */

/**
 * Friends Extended Manager
 * Manages advanced friend features
 */
class FriendsExtended {
  constructor() {
    this.friends = new Map();
    this.friendRequests = new Map();
    this.friendGroups = new Map();
    this.onlineStatusListeners = new Set();
  }

  /**
   * Feature 46: Friend requests
   * Send friend request
   * 
   * @param {string} userId - Target user UUID
   * @param {string} message - Optional request message
   * @returns {Promise<Object>}
   * 
   * TODO: Implement friend request capability handling
   * TODO: Add request expiration
   */
  async sendFriendRequest(userId, message = '') {
    if (!userId || typeof userId !== 'string') {
      throw new Error('Valid user ID required');
    }
    
    const request = {
      id: `req-${Date.now()}`,
      targetUserId: userId,
      message: message,
      status: 'pending',
      timestamp: Date.now()
    };
    
    this.friendRequests.set(request.id, request);
    console.log(`[FriendsExtended] Sent friend request to: ${userId}`);
    
    return Promise.resolve(request);
  }

  /**
   * Accept friend request
   * @param {string} requestId - Request UUID
   * @returns {Promise<void>}
   */
  async acceptFriendRequest(requestId) {
    const request = this.friendRequests.get(requestId);
    if (!request) {
      throw new Error(`Friend request not found: ${requestId}`);
    }
    
    request.status = 'accepted';
    
    // Add to friends list
    this.addFriend(request.targetUserId, {
      accepted: true,
      timestamp: Date.now()
    });
    
    console.log(`[FriendsExtended] Accepted friend request: ${requestId}`);
    return Promise.resolve();
  }

  /**
   * Decline friend request
   * @param {string} requestId - Request UUID
   */
  declineFriendRequest(requestId) {
    const request = this.friendRequests.get(requestId);
    if (request) {
      request.status = 'declined';
      console.log(`[FriendsExtended] Declined friend request: ${requestId}`);
    }
  }

  /**
   * Add friend to list
   * @param {string} friendId - Friend user UUID
   * @param {Object} friendData - Friend data
   */
  addFriend(friendId, friendData = {}) {
    if (!friendId || typeof friendId !== 'string') {
      throw new Error('Valid friend ID required');
    }
    
    const friend = {
      id: friendId,
      name: friendData.name || 'Friend',
      onlineStatus: friendData.onlineStatus || 'offline',
      permissions: friendData.permissions || {},
      group: friendData.group || null,
      notes: friendData.notes || '',
      timestamp: friendData.timestamp || Date.now(),
      ...friendData
    };
    
    this.friends.set(friendId, friend);
    console.log(`[FriendsExtended] Added friend: ${friend.name}`);
  }

  /**
   * Feature 47: Online notifications
   * Update friend online status
   * 
   * @param {string} friendId - Friend user UUID
   * @param {string} status - Online status ('online', 'offline', 'away')
   * 
   * TODO: Implement status change notifications
   * TODO: Add presence event broadcasting
   */
  updateFriendStatus(friendId, status) {
    if (!friendId || !status) {
      throw new Error('Valid friend ID and status required');
    }
    
    const friend = this.friends.get(friendId);
    if (friend) {
      const oldStatus = friend.onlineStatus;
      friend.onlineStatus = status;
      
      // Notify listeners
      if (oldStatus !== status) {
        this.notifyStatusChange(friendId, status, oldStatus);
      }
      
      console.log(`[FriendsExtended] Friend ${friendId} status: ${status}`);
    }
  }

  /**
   * Register online status listener
   * @param {Function} listener - Callback (friendId, newStatus, oldStatus) => void
   */
  addStatusListener(listener) {
    if (typeof listener === 'function') {
      this.onlineStatusListeners.add(listener);
    }
  }

  /**
   * Notify all listeners of status change
   * @private
   */
  notifyStatusChange(friendId, newStatus, oldStatus) {
    for (const listener of this.onlineStatusListeners) {
      try {
        listener(friendId, newStatus, oldStatus);
      } catch (error) {
        console.error('[FriendsExtended] Status listener error:', error);
      }
    }
  }

  /**
   * Feature 48: Friend permissions
   * Set friend permissions
   * 
   * @param {string} friendId - Friend user UUID
   * @param {Object} permissions - Permission flags
   * 
   * TODO: Implement full permission system (map/track/modify)
   */
  setFriendPermissions(friendId, permissions) {
    if (!friendId || !permissions) {
      throw new Error('Valid friend ID and permissions required');
    }
    
    const friend = this.friends.get(friendId);
    if (friend) {
      friend.permissions = {
        canSeeOnline: permissions.canSeeOnline || false,
        canSeeOnMap: permissions.canSeeOnMap || false,
        canModifyObjects: permissions.canModifyObjects || false,
        ...permissions
      };
      console.log(`[FriendsExtended] Updated permissions for: ${friendId}`);
    }
  }

  /**
   * Get friend permissions
   * @param {string} friendId - Friend user UUID
   * @returns {Object|null}
   */
  getFriendPermissions(friendId) {
    return this.friends.get(friendId)?.permissions || null;
  }

  /**
   * Feature 49: Calling cards
   * Create calling card for friend
   * 
   * @param {string} friendId - Friend user UUID
   * @returns {Object}
   * 
   * TODO: Implement calling card inventory integration
   */
  createCallingCard(friendId) {
    const friend = this.friends.get(friendId);
    if (!friend) {
      throw new Error(`Friend not found: ${friendId}`);
    }
    
    const callingCard = {
      id: `card-${friendId}`,
      targetId: friendId,
      targetName: friend.name,
      created: Date.now(),
      type: 'calling_card'
    };
    
    console.log(`[FriendsExtended] Created calling card for: ${friend.name}`);
    return callingCard;
  }

  /**
   * Feature 50: Friend groups
   * Create friend group/folder
   * 
   * @param {string} groupName - Group name
   * @returns {Object}
   * 
   * TODO: Implement friend organization system
   */
  createFriendGroup(groupName) {
    if (!groupName || typeof groupName !== 'string') {
      throw new Error('Valid group name required');
    }
    
    const groupId = `group-${Date.now()}`;
    const group = {
      id: groupId,
      name: groupName,
      members: [],
      created: Date.now()
    };
    
    this.friendGroups.set(groupId, group);
    console.log(`[FriendsExtended] Created friend group: ${groupName}`);
    
    return group;
  }

  /**
   * Add friend to group
   * @param {string} friendId - Friend user UUID
   * @param {string} groupId - Group UUID
   */
  addFriendToGroup(friendId, groupId) {
    const friend = this.friends.get(friendId);
    const group = this.friendGroups.get(groupId);
    
    if (!friend || !group) {
      throw new Error('Friend or group not found');
    }
    
    if (!group.members.includes(friendId)) {
      group.members.push(friendId);
      friend.group = groupId;
      console.log(`[FriendsExtended] Added ${friendId} to group ${group.name}`);
    }
  }

  /**
   * Get friends by group
   * @param {string} groupId - Group UUID
   * @returns {Array}
   */
  getFriendsByGroup(groupId) {
    const group = this.friendGroups.get(groupId);
    if (!group) {
      return [];
    }
    
    return group.members.map(id => this.friends.get(id)).filter(Boolean);
  }

  /**
   * Get online friends
   * @returns {Array}
   */
  getOnlineFriends() {
    return Array.from(this.friends.values()).filter(f => f.onlineStatus === 'online');
  }

  /**
   * Get statistics
   * @returns {Object}
   */
  getStats() {
    return {
      totalFriends: this.friends.size,
      onlineFriends: this.getOnlineFriends().length,
      pendingRequests: Array.from(this.friendRequests.values()).filter(r => r.status === 'pending').length,
      friendGroups: this.friendGroups.size
    };
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { FriendsExtended };
}

/**
 * Example Usage:
 * 
 * const friends = new FriendsExtended();
 * 
 * // Send friend request
 * await friends.sendFriendRequest('user-123', 'Want to be friends?');
 * 
 * // Accept request
 * await friends.acceptFriendRequest('req-456');
 * 
 * // Update status
 * friends.updateFriendStatus('user-123', 'online');
 * 
 * // Set permissions
 * friends.setFriendPermissions('user-123', {
 *   canSeeOnline: true,
 *   canSeeOnMap: true
 * });
 * 
 * // Create calling card
 * const card = friends.createCallingCard('user-123');
 * 
 * // Create and use friend groups
 * const group = friends.createFriendGroup('Best Friends');
 * friends.addFriendToGroup('user-123', group.id);
 */
