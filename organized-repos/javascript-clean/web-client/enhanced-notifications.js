/**
 * Enhanced Notifications API for Linkpoint PWA
 * Bridges Java plugin with browser Notification API
 */

class EnhancedNotifications {
  constructor() {
    this.isCapacitor = typeof window.Capacitor !== 'undefined';
    this.plugin = this.isCapacitor ? window.Capacitor.Plugins.EnhancedNotifications : null;
    this.permission = 'default';
    this.activeNotifications = new Map();
  }

  /**
   * Initialize notifications
   */
  async init() {
    if ('Notification' in window) {
      this.permission = Notification.permission;
    }
    
    // Check permission via plugin if available
    if (this.plugin) {
      try {
        const result = await this.plugin.checkPermission();
        this.permission = result.granted ? 'granted' : 'denied';
      } catch (error) {
        console.warn('Failed to check notification permission:', error);
      }
    }
  }

  /**
   * Request notification permission
   */
  async requestPermission() {
    if (this.plugin) {
      try {
        const result = await this.plugin.requestPermission();
        this.permission = result.granted ? 'granted' : 'denied';
        return result.granted;
      } catch (error) {
        console.error('Plugin permission request failed:', error);
      }
    }

    // Fall back to browser API
    if ('Notification' in window) {
      this.permission = await Notification.requestPermission();
      return this.permission === 'granted';
    }

    return false;
  }

  /**
   * Show notification with advanced options
   */
  async show(title, options = {}) {
    const notificationOptions = {
      title,
      body: options.body || '',
      channelType: this.getChannelType(options.type),
      priority: options.priority || 'default',
      autoCancel: options.autoCancel !== false,
      vibrate: options.vibrate !== false,
      sound: options.sound !== false,
      actionUrl: options.actionUrl || ''
    };

    // Use plugin if available (Android)
    if (this.plugin) {
      try {
        const result = await this.plugin.show(notificationOptions);
        return result.notificationId;
      } catch (error) {
        console.error('Plugin notification failed:', error);
      }
    }

    // Fall back to browser Notification API
    if (this.permission === 'granted' && 'Notification' in window) {
      const notification = new Notification(title, {
        body: options.body || '',
        icon: options.icon || '/assets/icons/icon-96x96.png',
        badge: '/assets/icons/icon-72x72.png',
        tag: options.tag || `notification-${Date.now()}`,
        requireInteraction: options.requireInteraction || false,
        silent: !options.sound,
        vibrate: options.vibrate ? this.getVibrationPattern(options.type) : undefined,
        data: options.data || {}
      });

      const notificationId = Date.now();
      this.activeNotifications.set(notificationId, notification);

      notification.onclick = () => {
        if (options.onClick) {
          options.onClick();
        }
        if (options.actionUrl) {
          window.location.href = options.actionUrl;
        }
        notification.close();
        this.activeNotifications.delete(notificationId);
      };

      notification.onclose = () => {
        this.activeNotifications.delete(notificationId);
      };

      // Auto-close after duration if specified
      if (options.duration) {
        setTimeout(() => {
          notification.close();
          this.activeNotifications.delete(notificationId);
        }, options.duration);
      }

      return notificationId;
    }

    return null;
  }

  /**
   * Cancel a specific notification
   */
  async cancel(notificationId) {
    if (this.plugin) {
      try {
        await this.plugin.cancel({ notificationId });
        return true;
      } catch (error) {
        console.error('Failed to cancel notification:', error);
      }
    }

    // Browser API
    const notification = this.activeNotifications.get(notificationId);
    if (notification) {
      notification.close();
      this.activeNotifications.delete(notificationId);
      return true;
    }

    return false;
  }

  /**
   * Cancel all notifications
   */
  async cancelAll() {
    if (this.plugin) {
      try {
        await this.plugin.cancelAll();
        return true;
      } catch (error) {
        console.error('Failed to cancel all notifications:', error);
      }
    }

    // Browser API
    this.activeNotifications.forEach(notification => notification.close());
    this.activeNotifications.clear();
    return true;
  }

  /**
   * Trigger vibration
   */
  async vibrate(pattern = 'default') {
    if (this.plugin) {
      try {
        await this.plugin.vibrate({ pattern });
        return true;
      } catch (error) {
        console.error('Plugin vibration failed:', error);
      }
    }

    // Fall back to browser Vibration API
    if ('vibrate' in navigator) {
      const vibrationPattern = this.getVibrationPatternArray(pattern);
      navigator.vibrate(vibrationPattern);
      return true;
    }

    return false;
  }

  // Helper methods

  getChannelType(type) {
    const channelMap = {
      'message': 'messages',
      'im': 'messages',
      'chat': 'messages',
      'friend': 'friends',
      'friend_online': 'friends',
      'friend_offline': 'friends',
      'group': 'groups',
      'group_notice': 'groups',
      'inventory': 'inventory',
      'teleport': 'inventory'
    };

    return channelMap[type] || 'default';
  }

  getVibrationPattern(type) {
    const patterns = {
      'messages': [200, 100, 200],
      'friends': [200],
      'groups': [100, 100, 100, 100, 100],
      'default': [300]
    };

    const channelType = this.getChannelType(type);
    return patterns[channelType] || patterns.default;
  }

  getVibrationPatternArray(pattern) {
    const patterns = {
      'short': [100],
      'medium': [300],
      'long': [500],
      'double': [200, 100, 200],
      'triple': [150, 100, 150, 100, 150],
      'sos': [100, 100, 100, 100, 100, 300, 300, 300, 100, 100, 100],
      'default': [250]
    };

    return patterns[pattern] || patterns.default;
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = EnhancedNotifications;
}

// Make available globally
window.EnhancedNotifications = EnhancedNotifications;
