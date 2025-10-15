/**
 * Linkpoint PWA - Notifications Manager
 */

class NotificationsManager extends Utils.EventEmitter {
  constructor(protocolManager, authManager, preferences) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
    this.preferences = preferences;
    this.notifications = [];
    this.unreadCount = 0;
    this.permission = 'default';
  }

  /**
   * Initialize notifications
   */
  async init() {
    // Check notification support
    if ('Notification' in window) {
      this.permission = Notification.permission;
    }

    // Setup protocol listeners
    this.protocol.on('notification', (data) => this.handleNotification(data));

    // Load saved notifications
    this.loadNotifications();
  }

  /**
   * Request permission for desktop notifications
   */
  async requestPermission() {
    if (!('Notification' in window)) {
      Utils.showToast('Notifications not supported', 'warning');
      return false;
    }

    if (this.permission === 'granted') {
      return true;
    }

    try {
      this.permission = await Notification.requestPermission();
      
      if (this.permission === 'granted') {
        Utils.showToast('Notifications enabled', 'success');
        return true;
      } else {
        Utils.showToast('Notification permission denied', 'warning');
        return false;
      }
    } catch (error) {
      console.error('Failed to request notification permission:', error);
      return false;
    }
  }

  /**
   * Show notification
   */
  async show(title, options = {}) {
    const notification = {
      id: Utils.generateUUID(),
      title,
      body: options.body || '',
      icon: options.icon || '/assets/icons/icon-96x96.png',
      timestamp: Date.now(),
      read: false,
      type: options.type || 'info',
      data: options.data || {}
    };

    // Add to notifications list
    this.notifications.unshift(notification);
    this.unreadCount++;
    this.saveNotifications();

    // Update badge
    this.updateBadge();

    // Show toast
    if (this.preferences.get('notifications', 'toastNotifications')) {
      Utils.showToast(title, options.type || 'info');
    }

    // Show desktop notification
    if (this.permission === 'granted' && 
        this.preferences.get('notifications', 'desktopNotifications')) {
      this.showDesktopNotification(notification);
    }

    // Play sound
    if (this.preferences.get('notifications', 'soundEnabled')) {
      this.playNotificationSound();
    }

    this.emit('notification_received', notification);
    return notification;
  }

  /**
   * Show desktop notification
   */
  showDesktopNotification(notification) {
    const desktopNotif = new Notification(notification.title, {
      body: notification.body,
      icon: notification.icon,
      tag: notification.id,
      requireInteraction: false,
      silent: false
    });

    desktopNotif.onclick = () => {
      window.focus();
      this.markAsRead(notification.id);
      desktopNotif.close();
      
      if (notification.data.action) {
        this.handleNotificationAction(notification.data.action, notification.data);
      }
    };

    // Auto-close after 5 seconds
    setTimeout(() => desktopNotif.close(), 5000);
  }

  /**
   * Play notification sound
   */
  playNotificationSound() {
    try {
      const audio = new Audio();
      audio.src = 'data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmwhBTGJ0fPTgjMGHm7A7+OZRQ0PVqvn77BdGAg+ldf0yHcsCS8=' ;
      audio.volume = 0.3;
      audio.play().catch(() => {});
    } catch (error) {
      // Ignore audio errors
    }
  }

  /**
   * Handle notification from protocol
   */
  handleNotification(data) {
    const type = data.type || 'info';
    let title = data.title || 'Notification';
    let body = data.message || '';

    // Handle specific notification types
    switch (type) {
      case 'friend_online':
        if (!this.preferences.get('notifications', 'friendOnline')) return;
        title = 'Friend Online';
        body = `${data.name} is now online`;
        break;

      case 'friend_offline':
        if (!this.preferences.get('notifications', 'friendOffline')) return;
        title = 'Friend Offline';
        body = `${data.name} is now offline`;
        break;

      case 'group_notice':
        if (!this.preferences.get('notifications', 'groupNotices')) return;
        title = `Group Notice: ${data.group_name}`;
        body = data.subject;
        break;

      case 'inventory_received':
        if (!this.preferences.get('notifications', 'inventoryReceived')) return;
        title = 'Inventory Offer';
        body = `${data.sender} sent you ${data.item_name}`;
        break;

      case 'teleport_offer':
        if (!this.preferences.get('notifications', 'teleportOffers')) return;
        title = 'Teleport Offer';
        body = `${data.sender} invites you to ${data.location}`;
        break;

      case 'instant_message':
        title = `IM from ${data.sender}`;
        body = data.message;
        break;
    }

    this.show(title, {
      body,
      type,
      data: data
    });
  }

  /**
   * Handle notification action
   */
  handleNotificationAction(action, data) {
    switch (action) {
      case 'open_chat':
        window.app?.switchView('chat');
        break;
      case 'accept_teleport':
        window.app?.teleport?.acceptOffer(data.offer_id);
        break;
      case 'view_inventory':
        window.app?.switchView('inventory');
        break;
      case 'show_friend':
        window.app?.switchView('friends');
        break;
    }
  }

  /**
   * Mark notification as read
   */
  markAsRead(notificationId) {
    const notification = this.notifications.find(n => n.id === notificationId);
    if (notification && !notification.read) {
      notification.read = true;
      this.unreadCount = Math.max(0, this.unreadCount - 1);
      this.updateBadge();
      this.saveNotifications();
      this.emit('notification_read', notificationId);
    }
  }

  /**
   * Mark all as read
   */
  markAllAsRead() {
    this.notifications.forEach(n => n.read = true);
    this.unreadCount = 0;
    this.updateBadge();
    this.saveNotifications();
    this.emit('all_notifications_read');
  }

  /**
   * Delete notification
   */
  deleteNotification(notificationId) {
    const index = this.notifications.findIndex(n => n.id === notificationId);
    if (index !== -1) {
      const notification = this.notifications[index];
      if (!notification.read) {
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      }
      this.notifications.splice(index, 1);
      this.updateBadge();
      this.saveNotifications();
      this.emit('notification_deleted', notificationId);
    }
  }

  /**
   * Clear all notifications
   */
  clearAll() {
    this.notifications = [];
    this.unreadCount = 0;
    this.updateBadge();
    this.saveNotifications();
    Utils.showToast('All notifications cleared', 'info');
    this.emit('all_notifications_cleared');
  }

  /**
   * Get all notifications
   */
  getAll() {
    return this.notifications;
  }

  /**
   * Get unread notifications
   */
  getUnread() {
    return this.notifications.filter(n => !n.read);
  }

  /**
   * Get unread count
   */
  getUnreadCount() {
    return this.unreadCount;
  }

  /**
   * Update badge count
   */
  updateBadge() {
    // Update UI badge
    if (window.app) {
      window.app.updateNotificationCount(this.unreadCount);
    }

    // Update PWA badge (if supported)
    if ('setAppBadge' in navigator) {
      if (this.unreadCount > 0) {
        navigator.setAppBadge(this.unreadCount).catch(() => {});
      } else {
        navigator.clearAppBadge().catch(() => {});
      }
    }
  }

  /**
   * Save notifications to storage
   */
  saveNotifications() {
    // Save only last 100 notifications
    const toSave = this.notifications.slice(0, 100);
    Utils.storage.set('linkpoint_notifications', toSave);
    Utils.storage.set('linkpoint_unread_count', this.unreadCount);
  }

  /**
   * Load notifications from storage
   */
  loadNotifications() {
    this.notifications = Utils.storage.get('linkpoint_notifications', []);
    this.unreadCount = Utils.storage.get('linkpoint_unread_count', 0);
    this.updateBadge();
  }

  /**
   * Test notification
   */
  test() {
    this.show('Test Notification', {
      body: 'This is a test notification from Linkpoint',
      type: 'info',
      data: { action: 'none' }
    });
  }
}

// Make available globally
window.NotificationsManager = NotificationsManager;
