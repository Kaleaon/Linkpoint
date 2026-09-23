/**
 * Linkpoint PWA - Friends Extensions (Features 46-50)
 *
 * Phase 2: Core Protocol Extensions - Priority 3
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 90-95)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/
 *
 * Extends friend functionality with requests, notifications, permissions, calling cards, and groups.
 */

export class FriendsExtended {
  private friends: Map<string, any> = new Map();
  private friendRequests: Map<string, any> = new Map();
  private friendGroups: Map<string, any> = new Map();
  private onlineStatusListeners: Set<Function> = new Set();

  /**
   * Feature 46: Friend requests
   * Send friend request
   */
  async sendFriendRequest(userId: string, message: string = '') {
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
   */
  async acceptFriendRequest(requestId: string) {
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
   */
  declineFriendRequest(requestId: string) {
    const request = this.friendRequests.get(requestId);
    if (request) {
      request.status = 'declined';
      console.log(`[FriendsExtended] Declined friend request: ${requestId}`);
    }
  }

  /**
   * Add friend to list
   */
  addFriend(friendId: string, friendData: any = {}) {
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
   */
  updateFriendStatus(friendId: string, status: string) {
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
   */
  addStatusListener(listener: Function) {
    if (typeof listener === 'function') {
      this.onlineStatusListeners.add(listener);
    }
  }

  removeStatusListener(listener: Function) {
    this.onlineStatusListeners.delete(listener);
  }

  /**
   * Notify all listeners of status change
   */
  private notifyStatusChange(friendId: string, newStatus: string, oldStatus: string) {
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
   */
  setFriendPermissions(friendId: string, permissions: any) {
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
   */
  getFriendPermissions(friendId: string) {
    return this.friends.get(friendId)?.permissions || null;
  }

  /**
   * Feature 49: Calling cards
   * Create calling card for friend
   */
  createCallingCard(friendId: string) {
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
   */
  createFriendGroup(groupName: string) {
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
   */
  addFriendToGroup(friendId: string, groupId: string) {
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
   */
  getFriendsByGroup(groupId: string) {
    const group = this.friendGroups.get(groupId);
    if (!group) {
      return [];
    }

    return group.members.map((id: string) => this.friends.get(id)).filter(Boolean);
  }

  /**
   * Get online friends
   */
  getOnlineFriends() {
    return Array.from(this.friends.values()).filter(f => f.onlineStatus === 'online');
  }

  /** Snapshot for UI rendering; callers cannot mutate the backing map. */
  getFriends() {
    return Array.from(this.friends.values()).map(friend => ({ ...friend }));
  }

  getStats() {
    let onlineFriends = 0;
    for (const f of this.friends.values()) {
      if (f.onlineStatus === 'online') {
        onlineFriends++;
      }
    }

    let pendingRequests = 0;
    for (const r of this.friendRequests.values()) {
      if (r.status === 'pending') {
        pendingRequests++;
      }
    }

    return {
      totalFriends: this.friends.size,
      onlineFriends,
      pendingRequests,
      friendGroups: this.friendGroups.size
    };
  }
}
