/**
 * PWA Demo - Showcasing all native capabilities
 * This file demonstrates how to use all the PWA integration features
 */

class PWADemo {
  constructor() {
    this.pwa = window.pwaIntegration;
  }

  /**
   * Initialize demo
   */
  async init() {
    console.log('🎮 PWA Demo initializing...');

    // Wait for PWA integration to be ready
    if (!this.pwa.initialized) {
      await new Promise(resolve => {
        const checkInterval = setInterval(() => {
          if (this.pwa.initialized) {
            clearInterval(checkInterval);
            resolve();
          }
        }, 100);
      });
    }

    this.setupDemoHandlers();
    console.log('✅ PWA Demo ready');
  }

  /**
   * Setup demo event handlers
   */
  setupDemoHandlers() {
    // Handle background sync items
    window.addEventListener('pwa-sync-item', async (event) => {
      const { tag, item, resolve } = event.detail;
      
      console.log(`🔄 Processing sync item for ${tag}:`, item);
      
      // Simulate processing
      try {
        // Your sync logic here (e.g., upload to server)
        await this.processSyncItem(tag, item.data);
        resolve(true); // Mark as successfully processed
      } catch (error) {
        console.error('Sync item processing failed:', error);
        resolve(false); // Keep in queue for retry
      }
    });

    // Handle online/offline events
    window.addEventListener('pwa-online', () => {
      console.log('📶 App is online');
      this.pwa.haptics.success();
    });

    window.addEventListener('pwa-offline', () => {
      console.log('📵 App is offline');
      this.pwa.haptics.warning();
    });
  }

  /**
   * Demonstrate notifications
   */
  async demoNotifications() {
    console.log('🔔 Testing notifications...');

    // Request permission
    const granted = await this.pwa.enhancedNotifications.requestPermission();
    if (!granted) {
      console.warn('Notification permission denied');
      return;
    }

    // Show different types of notifications
    await this.pwa.notify('Welcome!', {
      body: 'This is a success notification',
      type: 'success',
      vibrate: true
    });

    setTimeout(async () => {
      await this.pwa.notify('Friend Online', {
        body: 'John Doe is now online',
        type: 'friend_online',
        vibrate: true,
        icon: '/assets/icons/icon-96x96.png'
      });
    }, 2000);

    setTimeout(async () => {
      await this.pwa.notify('New Message', {
        body: 'You have a new instant message',
        type: 'message',
        vibrate: true,
        actionUrl: '/?view=chat'
      });
    }, 4000);
  }

  /**
   * Demonstrate file system operations
   */
  async demoFileSystem() {
    console.log('📁 Testing file system...');

    try {
      // Write a file
      await this.pwa.fileSystem.writeFile('demo/test.txt', 'Hello from Linkpoint PWA!', {
        directory: 'data'
      });
      console.log('✅ File written');

      // Read the file
      const content = await this.pwa.fileSystem.readFile('demo/test.txt', {
        directory: 'data'
      });
      console.log('✅ File read:', content);

      // Check if file exists
      const exists = await this.pwa.fileSystem.exists('demo/test.txt', {
        directory: 'data'
      });
      console.log('✅ File exists:', exists);

      // Get file info
      const stat = await this.pwa.fileSystem.stat('demo/test.txt', {
        directory: 'data'
      });
      console.log('✅ File info:', stat);

    } catch (error) {
      console.error('File system demo failed:', error);
    }
  }

  /**
   * Demonstrate secure storage
   */
  async demoSecureStorage() {
    console.log('🔒 Testing secure storage...');

    try {
      // Store credentials
      await this.pwa.saveSecure('user_credentials', {
        username: 'demo_user',
        token: 'abc123xyz',
        timestamp: Date.now()
      });
      console.log('✅ Credentials stored securely');

      // Retrieve credentials
      const credentials = await this.pwa.loadSecure('user_credentials');
      console.log('✅ Credentials retrieved:', credentials);

      // Store preferences
      await this.pwa.saveSecure('user_preferences', {
        theme: 'dark',
        notifications: true,
        language: 'en'
      });
      console.log('✅ Preferences stored securely');

      // List all keys
      const keys = await this.pwa.secureStorage.keys();
      console.log('✅ All secure keys:', keys);

    } catch (error) {
      console.error('Secure storage demo failed:', error);
    }
  }

  /**
   * Demonstrate background sync
   */
  async demoBackgroundSync() {
    console.log('🔄 Testing background sync...');

    try {
      // Queue some messages
      await this.pwa.queueForSync('messages', {
        to: 'user123',
        text: 'Hello from offline mode!',
        timestamp: Date.now()
      });

      await this.pwa.queueForSync('messages', {
        to: 'user456',
        text: 'Another offline message',
        timestamp: Date.now()
      });

      console.log('✅ Messages queued for sync');

      // Check queue size
      const size = await this.pwa.backgroundSync.getQueueSize('messages');
      console.log('✅ Queue size:', size);

      // Trigger sync manually
      await this.pwa.backgroundSync.sync('messages');

    } catch (error) {
      console.error('Background sync demo failed:', error);
    }
  }

  /**
   * Demonstrate haptics
   */
  async demoHaptics() {
    console.log('📳 Testing haptics...');

    if (!this.pwa.haptics.available) {
      console.warn('Haptics not available on this device');
      return;
    }

    // Light tap
    await this.pwa.haptics.light();
    await this.delay(500);

    // Medium tap
    await this.pwa.haptics.medium();
    await this.delay(500);

    // Heavy tap
    await this.pwa.haptics.heavy();
    await this.delay(500);

    // Success pattern
    await this.pwa.haptics.success();
    await this.delay(1000);

    // Custom pattern
    await this.pwa.haptics.vibratePattern([100, 50, 100, 50, 100]);

    console.log('✅ Haptics demo complete');
  }

  /**
   * Demonstrate badge updates
   */
  async demoBadge() {
    console.log('🔢 Testing badge...');

    try {
      // Set badge to 1
      await this.pwa.updateBadge(1);
      await this.delay(1000);

      // Increment
      await this.pwa.badge.increment();
      await this.delay(1000);

      // Increment by 3
      await this.pwa.badge.increment(3);
      await this.delay(1000);

      // Decrement
      await this.pwa.badge.decrement();
      await this.delay(1000);

      // Clear
      await this.pwa.badge.clear();

      console.log('✅ Badge demo complete');
    } catch (error) {
      console.error('Badge demo failed:', error);
    }
  }

  /**
   * Demonstrate network monitoring
   */
  async demoNetworkMonitoring() {
    console.log('📡 Testing network monitoring...');

    // Get current status
    const status = await this.pwa.networkStatus.getStatus();
    console.log('Current network status:', status);
    console.log('Connected:', this.pwa.networkStatus.isConnected());
    console.log('Connection type:', this.pwa.networkStatus.getConnectionType());
    console.log('Is metered:', this.pwa.networkStatus.isMetered());
    console.log('Is fast:', this.pwa.networkStatus.isFastConnection());

    // Monitor changes
    this.pwa.networkStatus.on('change', (newStatus) => {
      console.log('📡 Network changed:', newStatus);
      
      // Adjust app behavior based on connection
      if (newStatus.connectionType === 'cellular') {
        console.log('On cellular - reducing bandwidth usage');
      } else if (newStatus.connectionType === 'wifi') {
        console.log('On WiFi - full bandwidth available');
      }
    });
  }

  /**
   * Demonstrate device info
   */
  async demoDeviceInfo() {
    console.log('📱 Testing device info...');

    const info = await this.pwa.deviceInfo.getInfo();
    console.log('Device info:', info);

    const battery = await this.pwa.deviceInfo.getBatteryInfo();
    if (battery) {
      console.log('Battery:', {
        level: (battery.level * 100).toFixed(0) + '%',
        charging: battery.isCharging
      });
    }

    const memory = await this.pwa.deviceInfo.getMemoryInfo();
    if (memory) {
      console.log('Memory usage:', memory.percentUsed.toFixed(1) + '%');
    }

    const capabilities = await this.pwa.deviceInfo.getCapabilities();
    console.log('Device capabilities:', capabilities);
  }

  /**
   * Run all demos
   */
  async runAllDemos() {
    console.log('🎯 Running all PWA demos...\n');

    await this.demoDeviceInfo();
    await this.delay(1000);

    await this.demoNetworkMonitoring();
    await this.delay(1000);

    await this.demoSecureStorage();
    await this.delay(1000);

    await this.demoFileSystem();
    await this.delay(1000);

    await this.demoBackgroundSync();
    await this.delay(1000);

    await this.demoHaptics();
    await this.delay(1000);

    await this.demoBadge();
    await this.delay(1000);

    await this.demoNotifications();

    console.log('\n🎉 All demos complete!');
  }

  /**
   * Process a sync item (example implementation)
   */
  async processSyncItem(tag, data) {
    console.log(`Processing ${tag} sync item:`, data);
    
    // Simulate API call
    await this.delay(500);
    
    // Example: Send message to server
    if (tag === 'messages') {
      // await fetch('/api/messages', { method: 'POST', body: JSON.stringify(data) });
      console.log('Message would be sent to server:', data);
    }
    
    return true;
  }

  /**
   * Utility: Delay helper
   */
  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}

// Create global instance
const pwaDemo = new PWADemo();

// Make available globally
window.PWADemo = PWADemo;
window.pwaDemo = pwaDemo;

// Auto-initialize demo
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => {
    pwaDemo.init();
  });
} else {
  pwaDemo.init();
}
