/**
 * Linkpoint PWA - Friends Management
 */

class FriendsManager extends Utils.EventEmitter {
  constructor(protocolManager, authManager) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
    this.friends = new Map();
    this.onlineFriends = new Set();
    this.friendRequests = [];
  }

  /**
   * Initialize friends manager
   */
  async init() {
    // Setup protocol listeners
    this.protocol.on('friend_online', (data) => this.handleFriendOnline(data));
    this.protocol.on('friend_offline', (data) => this.handleFriendOffline(data));
    this.protocol.on('friend_request', (data) => this.handleFriendRequest(data));
    this.protocol.on('friend_removed', (data) => this.handleFriendRemoved(data));

    // Setup UI
    this.setupUI();
  }

  /**
   * Setup UI
   */
  setupUI() {
    const addFriendBtn = document.getElementById('add-friend');
    addFriendBtn?.addEventListener('click', () => this.showAddFriendDialog());

    const friendsList = document.getElementById('friends-list');
    friendsList?.addEventListener('click', (e) => {
      const friendCard = e.target.closest('.friend-card');
      if (friendCard) {
        this.selectFriend(friendCard.dataset.friendId);
      }
    });
  }

  /**
   * Load friends list
   */
  async load() {
    if (!this.auth.isLoggedIn()) {
      Utils.showToast('Please login first', 'warning');
      return;
    }

    try {
      // Request friends from server
      this.protocol.sendMessage('FetchFriendsList', {
        agent_id: this.auth.getUser().id
      });

      Utils.showToast('Loading friends...', 'info');

      // Simulate loading for demo
      await Utils.sleep(1000);
      this.createDemoFriends();

    } catch (error) {
      console.error('Failed to load friends:', error);
      Utils.showToast('Failed to load friends', 'error');
    }
  }

  /**
   * Create demo friends for testing
   */
  createDemoFriends() {
    const demoFriends = [
      { name: 'Alice Wonderland', online: true, location: 'Sandbox' },
      { name: 'Bob Builder', online: true, location: 'Welcome Area' },
      { name: 'Charlie Brown', online: false, lastSeen: '2 hours ago' },
      { name: 'Diana Prince', online: false, lastSeen: '1 day ago' }
    ];

    demoFriends.forEach(friend => {
      const friendId = Utils.generateUUID();
      this.friends.set(friendId, {
        id: friendId,
        name: friend.name,
        online: friend.online,
        location: friend.location,
        lastSeen: friend.lastSeen,
        permissions: {
          canSeeOnline: true,
          canMapMe: true,
          canEditMyObjects: false
        }
      });

      if (friend.online) {
        this.onlineFriends.add(friendId);
      }
    });

    this.renderFriendsList();
    this.emit('friends_loaded');
  }

  /**
   * Render friends list
   */
  renderFriendsList() {
    const container = document.getElementById('friends-list');
    if (!container) return;

    container.innerHTML = '';

    if (this.friends.size === 0) {
      container.innerHTML = '<div class="empty-state"><p>No friends yet</p></div>';
      return;
    }

    // Separate online and offline friends
    const onlineFriends = [];
    const offlineFriends = [];

    this.friends.forEach(friend => {
      if (friend.online) {
        onlineFriends.push(friend);
      } else {
        offlineFriends.push(friend);
      }
    });

    // Render online friends
    if (onlineFriends.length > 0) {
      const onlineHeader = document.createElement('div');
      onlineHeader.className = 'friends-header';
      onlineHeader.innerHTML = `<h3>Online (${onlineFriends.length})</h3>`;
      container.appendChild(onlineHeader);

      onlineFriends.forEach(friend => {
        container.appendChild(this.createFriendCard(friend));
      });
    }

    // Render offline friends
    if (offlineFriends.length > 0) {
      const offlineHeader = document.createElement('div');
      offlineHeader.className = 'friends-header';
      offlineHeader.innerHTML = `<h3>Offline (${offlineFriends.length})</h3>`;
      container.appendChild(offlineHeader);

      offlineFriends.forEach(friend => {
        container.appendChild(this.createFriendCard(friend));
      });
    }
  }

  /**
   * Create friend card element
   */
  createFriendCard(friend) {
    const card = document.createElement('div');
    card.className = `friend-card ${friend.online ? 'online' : 'offline'}`;
    card.dataset.friendId = friend.id;

    const statusIcon = friend.online ? '🟢' : '⚫';
    const statusText = friend.online ? friend.location : friend.lastSeen;

    card.innerHTML = `
      <div class="friend-avatar">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke-width="2"/>
        </svg>
      </div>
      <div class="friend-info">
        <div class="friend-name">${friend.name}</div>
        <div class="friend-status">${statusIcon} ${statusText}</div>
      </div>
      <div class="friend-actions">
        ${friend.online ? `
          <button class="action-btn" onclick="window.app.friends.teleportToFriend('${friend.id}')" title="Teleport">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M5 12h14M12 5l7 7-7 7" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
          <button class="action-btn" onclick="window.app.friends.sendIM('${friend.id}')" title="Send IM">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke-width="2"/>
            </svg>
          </button>
        ` : ''}
        <button class="action-btn" onclick="window.app.friends.showFriendProfile('${friend.id}')" title="Profile">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <circle cx="12" cy="12" r="10" stroke-width="2"/>
            <path d="M12 16v-4M12 8h.01" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>
    `;

    return card;
  }

  /**
   * Add friend
   */
  async addFriend(name) {
    if (!name) return;

    try {
      // Send friend request
      this.protocol.sendMessage('SendFriendRequest', {
        target_name: name
      });

      Utils.showToast(`Friend request sent to ${name}`, 'success');
      this.emit('friend_request_sent', name);

    } catch (error) {
      console.error('Failed to send friend request:', error);
      Utils.showToast('Failed to send friend request', 'error');
    }
  }

  /**
   * Remove friend
   */
  async removeFriend(friendId) {
    const friend = this.friends.get(friendId);
    if (!friend) return;

    const confirmed = confirm(`Remove ${friend.name} from friends?`);
    if (!confirmed) return;

    try {
      this.protocol.sendMessage('RemoveFriend', {
        friend_id: friendId
      });

      this.friends.delete(friendId);
      this.onlineFriends.delete(friendId);
      this.renderFriendsList();

      Utils.showToast(`Removed ${friend.name} from friends`, 'info');
      this.emit('friend_removed', friendId);

    } catch (error) {
      console.error('Failed to remove friend:', error);
      Utils.showToast('Failed to remove friend', 'error');
    }
  }

  /**
   * Teleport to friend
   */
  teleportToFriend(friendId) {
    const friend = this.friends.get(friendId);
    if (!friend || !friend.online) {
      Utils.showToast('Friend is not online', 'warning');
      return;
    }

    this.protocol.sendMessage('TeleportToFriend', {
      friend_id: friendId
    });

    Utils.showToast(`Teleporting to ${friend.name}...`, 'info');
    this.emit('teleport_to_friend', friend);
  }

  /**
   * Send instant message
   */
  sendIM(friendId) {
    const friend = this.friends.get(friendId);
    if (!friend) return;

    // Switch to chat view and open IM
    window.app?.switchView('chat');
    this.emit('open_im', friend);
  }

  /**
   * Show friend profile
   */
  showFriendProfile(friendId) {
    const friend = this.friends.get(friendId);
    if (!friend) return;

    this.emit('show_profile', friend);
    Utils.showToast(`Profile: ${friend.name} (coming soon)`, 'info');
  }

  /**
   * Show add friend dialog
   */
  showAddFriendDialog() {
    const name = prompt('Enter friend name (FirstName LastName):');
    if (name) {
      this.addFriend(name);
    }
  }

  /**
   * Select friend
   */
  selectFriend(friendId) {
    const friend = this.friends.get(friendId);
    if (!friend) return;

    // Update UI
    document.querySelectorAll('.friend-card').forEach(card => {
      card.classList.remove('selected');
    });

    const card = document.querySelector(`[data-friend-id="${friendId}"]`);
    card?.classList.add('selected');

    this.emit('friend_selected', friend);
  }

  /**
   * Handle friend online
   */
  handleFriendOnline(data) {
    const friend = this.friends.get(data.friend_id);
    if (friend) {
      friend.online = true;
      friend.location = data.location;
      this.onlineFriends.add(data.friend_id);
      this.renderFriendsList();

      Utils.showToast(`${friend.name} is now online`, 'info');
      this.emit('friend_online', friend);
    }
  }

  /**
   * Handle friend offline
   */
  handleFriendOffline(data) {
    const friend = this.friends.get(data.friend_id);
    if (friend) {
      friend.online = false;
      friend.lastSeen = 'Just now';
      this.onlineFriends.delete(data.friend_id);
      this.renderFriendsList();

      Utils.showToast(`${friend.name} is now offline`, 'info');
      this.emit('friend_offline', friend);
    }
  }

  /**
   * Handle friend request
   */
  handleFriendRequest(data) {
    this.friendRequests.push(data);
    this.emit('friend_request_received', data);

    const accept = confirm(`${data.name} wants to be your friend. Accept?`);
    if (accept) {
      this.acceptFriendRequest(data.id);
    } else {
      this.declineFriendRequest(data.id);
    }
  }

  /**
   * Accept friend request
   */
  acceptFriendRequest(requestId) {
    this.protocol.sendMessage('AcceptFriendRequest', {
      request_id: requestId
    });

    this.friendRequests = this.friendRequests.filter(req => req.id !== requestId);
    Utils.showToast('Friend request accepted', 'success');
  }

  /**
   * Decline friend request
   */
  declineFriendRequest(requestId) {
    this.protocol.sendMessage('DeclineFriendRequest', {
      request_id: requestId
    });

    this.friendRequests = this.friendRequests.filter(req => req.id !== requestId);
    Utils.showToast('Friend request declined', 'info');
  }

  /**
   * Handle friend removed
   */
  handleFriendRemoved(data) {
    this.friends.delete(data.friend_id);
    this.onlineFriends.delete(data.friend_id);
    this.renderFriendsList();
  }

  /**
   * Get online friends count
   */
  getOnlineCount() {
    return this.onlineFriends.size;
  }

  /**
   * Get total friends count
   */
  getTotalCount() {
    return this.friends.size;
  }
}

// Make available globally
window.FriendsManager = FriendsManager;
