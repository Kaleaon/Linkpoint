/**
 * PWA Integration Module for Linkpoint
 * Integrates all native capabilities with progressive web app features
 */

class PWAIntegration {
  constructor() {
    this.enhancedNotifications = null;
    this.fileSystem = null;
    this.networkStatus = null;
    this.deviceInfo = null;
    this.secureStorage = null;
    this.backgroundSync = null;
    this.haptics = null;
    this.badge = null;
    
    this.isCapacitor = typeof window.Capacitor !== 'undefined';
    this.initialized = false;
  }

  /**
   * Initialize all PWA capabilities
   */
  async init() {
    if (this.initialized) {
      return;
    }

    console.log('🚀 Initializing PWA Integration...');

    try {
      // Initialize all modules
      this.enhancedNotifications = new EnhancedNotifications();
      await this.enhancedNotifications.init();

      this.fileSystem = new FileSystem();

      this.networkStatus = new NetworkStatus();
      await this.networkStatus.init();

      this.deviceInfo = new DeviceInfo();
      
      this.secureStorage = new SecureStorage();
      await this.secureStorage.init();

      this.backgroundSync = new BackgroundSync();
      await this.backgroundSync.init();

      this.haptics = new Haptics();
      await this.haptics.init();

      this.badge = new Badge();
      await this.badge.init();

      // Setup event listeners
      this.setupEventListeners();

      // Register background sync queues
      await this.registerSyncQueues();

      this.initialized = true;
      console.log('✅ PWA Integration initialized successfully');

      // Log capabilities
      await this.logCapabilities();

    } catch (error) {
      console.error('Failed to initialize PWA integration:', error);
      throw error;
    }
  }

  /**
   * Setup event listeners for all modules
   */
  setupEventListeners() {
    // Network status changes
    this.networkStatus.on('change', (status) => {
      console.log('📡 Network status changed:', status);
      
      if (status.connected) {
        this.handleOnline();
      } else {
        this.handleOffline();
      }
    });

    // Background sync events
    this.backgroundSync.on('syncStarted', (event) => {
      console.log('🔄 Sync started:', event.tag);
    });

    this.backgroundSync.on('syncCompleted', (event) => {
      console.log('✅ Sync completed:', event);
      if (event.synced > 0) {
        this.haptics.success();
        this.showSyncNotification(event);
      }
    });

    this.backgroundSync.on('syncItem', async (event) => {
      // Let the app handle actual sync logic
      window.dispatchEvent(new CustomEvent('pwa-sync-item', { detail: event }));
    });
  }

  /**
   * Register default sync queues
   */
  async registerSyncQueues() {
    const queues = [
      'messages',
      'inventory',
      'preferences',
      'offline-actions'
    ];

    for (const tag of queues) {
      try {
        await this.backgroundSync.register(tag);
      } catch (error) {
        console.warn(`Failed to register sync queue ${tag}:`, error);
      }
    }
  }

  /**
   * Handle online event
   */
  handleOnline() {
    console.log('🌐 Connection restored');
    
    // Trigger sync
    this.backgroundSync.syncAll();
    
    // Show notification
    if (this.enhancedNotifications) {
      this.enhancedNotifications.show('Connection Restored', {
        body: 'You are back online. Syncing data...',
        type: 'info',
        vibrate: true,
        sound: false
      });
    }

    // Emit event for app
    window.dispatchEvent(new CustomEvent('pwa-online'));
  }

  /**
   * Handle offline event
   */
  handleOffline() {
    console.log('📴 Connection lost');
    
    // Show notification
    if (this.enhancedNotifications) {
      this.enhancedNotifications.show('Offline Mode', {
        body: 'Working offline. Changes will sync when connection is restored.',
        type: 'warning',
        vibrate: true,
        sound: false
      });
    }

    // Emit event for app
    window.dispatchEvent(new CustomEvent('pwa-offline'));
  }

  /**
   * Show sync completion notification
   */
  showSyncNotification(event) {
    if (!this.enhancedNotifications) return;

    const message = event.failed > 0
      ? `Synced ${event.synced} items, ${event.failed} failed`
      : `Successfully synced ${event.synced} items`;

    this.enhancedNotifications.show('Sync Complete', {
      body: message,
      type: event.failed > 0 ? 'warning' : 'success',
      vibrate: false,
      sound: false,
      duration: 3000
    });
  }

  /**
   * Queue data for background sync
   */
  async queueForSync(tag, data) {
    try {
      await this.backgroundSync.enqueue(tag, data);
      return true;
    } catch (error) {
      console.error(`Failed to queue data for sync:`, error);
      return false;
    }
  }

  /**
   * Save sensitive data securely
   */
  async saveSecure(key, value) {
    try {
      await this.secureStorage.set(key, value);
      return true;
    } catch (error) {
      console.error(`Failed to save secure data:`, error);
      return false;
    }
  }

  /**
   * Load sensitive data securely
   */
  async loadSecure(key) {
    try {
      return await this.secureStorage.get(key);
    } catch (error) {
      console.error(`Failed to load secure data:`, error);
      return null;
    }
  }

  /**
   * Update app badge with count
   */
  async updateBadge(count) {
    try {
      await this.badge.set(count);
      return true;
    } catch (error) {
      console.error('Failed to update badge:', error);
      return false;
    }
  }

  /**
   * Show notification with haptic feedback
   */
  async notify(title, options = {}) {
    // Show notification
    const notificationId = await this.enhancedNotifications.show(title, options);

    // Trigger haptic feedback
    if (options.haptic !== false) {
      const hapticType = options.type === 'error' ? 'error' : 
                         options.type === 'warning' ? 'warning' : 'success';
      await this.haptics.notification(hapticType);
    }

    return notificationId;
  }

  /**
   * Request all necessary permissions
   */
  async requestPermissions() {
    const results = {};

    // Notifications
    try {
      results.notifications = await this.enhancedNotifications.requestPermission();
    } catch (error) {
      results.notifications = false;
    }

    // Geolocation
    if ('geolocation' in navigator) {
      try {
        await new Promise((resolve, reject) => {
          navigator.geolocation.getCurrentPosition(resolve, reject);
        });
        results.location = true;
      } catch (error) {
        results.location = false;
      }
    }

    return results;
  }

  /**
   * Save application state for offline use
   */
  async saveOfflineState(state) {
    try {
      await this.secureStorage.set('offline_state', state);
      await this.fileSystem.writeFile('offline_state.json', JSON.stringify(state, null, 2), {
        directory: 'cache'
      });
      return true;
    } catch (error) {
      console.error('Failed to save offline state:', error);
      return false;
    }
  }

  /**
   * Load application state from offline storage
   */
  async loadOfflineState() {
    try {
      // Try secure storage first
      const state = await this.secureStorage.get('offline_state');
      if (state) {
        return state;
      }

      // Fall back to file system
      const stateJson = await this.fileSystem.readFile('offline_state.json', {
        directory: 'cache'
      });
      return JSON.parse(stateJson);
    } catch (error) {
      console.error('Failed to load offline state:', error);
      return null;
    }
  }

  /**
   * Log device and PWA capabilities
   */
  async logCapabilities() {
    console.group('📱 Device & PWA Capabilities');

    // Device info
    const deviceInfo = await this.deviceInfo.getInfo();
    console.log('Device:', {
      platform: deviceInfo.platform,
      model: deviceInfo.model,
      os: deviceInfo.osVersion
    });

    // Capabilities
    const capabilities = await this.deviceInfo.getCapabilities();
    console.log('Features:', capabilities);

    // Network
    const networkStatus = await this.networkStatus.getStatus();
    console.log('Network:', networkStatus);

    // Storage
    const storageInfo = await this.deviceInfo.getStorageInfo();
    if (storageInfo) {
      console.log('Storage:', {
        total: this.formatBytes(storageInfo.internalTotal),
        used: this.formatBytes(storageInfo.internalUsed),
        available: this.formatBytes(storageInfo.internalAvailable)
      });
    }

    // Memory
    const memoryInfo = await this.deviceInfo.getMemoryInfo();
    if (memoryInfo) {
      console.log('Memory:', {
        total: this.formatBytes(memoryInfo.totalMemory),
        used: this.formatBytes(memoryInfo.usedMemory),
        percentUsed: memoryInfo.percentUsed.toFixed(1) + '%'
      });
    }

    // Battery
    const batteryInfo = await this.deviceInfo.getBatteryInfo();
    if (batteryInfo) {
      console.log('Battery:', {
        level: (batteryInfo.level * 100).toFixed(0) + '%',
        charging: batteryInfo.isCharging
      });
    }

    // PWA Status
    console.log('PWA:', {
      serviceWorker: 'serviceWorker' in navigator,
      notifications: this.enhancedNotifications.permission,
      badge: this.supported,
      haptics: this.haptics.available,
      backgroundSync: 'sync' in ServiceWorkerRegistration.prototype,
      isCapacitor: this.isCapacitor
    });

    console.groupEnd();
  }

  /**
   * Format bytes to human-readable format
   */
  formatBytes(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }

  /**
   * Export all capabilities for external use
   */
  export() {
    return {
      notifications: this.enhancedNotifications,
      fileSystem: this.fileSystem,
      networkStatus: this.networkStatus,
      deviceInfo: this.deviceInfo,
      secureStorage: this.secureStorage,
      backgroundSync: this.backgroundSync,
      haptics: this.haptics,
      badge: this.badge
    };
  }
}

// Create global instance
const pwaIntegration = new PWAIntegration();

// Auto-initialize when DOM is ready
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => {
    pwaIntegration.init().catch(error => {
      console.error('PWA Integration initialization failed:', error);
    });
  });
} else {
  pwaIntegration.init().catch(error => {
    console.error('PWA Integration initialization failed:', error);
  });
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { PWAIntegration, pwaIntegration };
}

// Make available globally
window.PWAIntegration = PWAIntegration;
window.pwaIntegration = pwaIntegration;
