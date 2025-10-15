/**
 * Device Info API for Linkpoint PWA
 * Bridges Java plugin with browser User Agent and Device APIs
 */

class DeviceInfo {
  constructor() {
    this.isCapacitor = typeof window.Capacitor !== 'undefined';
    this.plugin = this.isCapacitor ? window.Capacitor.Plugins.DeviceInfo : null;
    this.cachedInfo = null;
  }

  /**
   * Get comprehensive device information
   */
  async getInfo() {
    if (this.cachedInfo) {
      return this.cachedInfo;
    }

    if (this.plugin) {
      try {
        this.cachedInfo = await this.plugin.getInfo();
        return this.cachedInfo;
      } catch (error) {
        console.error('Failed to get device info from plugin:', error);
      }
    }

    // Fall back to browser APIs
    this.cachedInfo = {
      // Platform info
      platform: this.getPlatform(),
      userAgent: navigator.userAgent,
      
      // Device info (from user agent parsing)
      manufacturer: this.getManufacturer(),
      model: this.getModel(),
      
      // OS info
      osVersion: this.getOSVersion(),
      
      // App info
      appVersion: '1.0.0', // From manifest or package.json
      
      // Display info
      screenWidth: window.screen.width,
      screenHeight: window.screen.height,
      screenDensity: window.devicePixelRatio || 1,
      
      // Locale info
      language: navigator.language.split('-')[0],
      locale: navigator.language,
      timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      timezoneOffset: new Date().getTimezoneOffset() * -60,
      
      // Browser info
      browser: this.getBrowserInfo(),
      
      // Unique ID (persistent in localStorage)
      deviceId: this.getDeviceId()
    };

    return this.cachedInfo;
  }

  /**
   * Get battery information
   */
  async getBatteryInfo() {
    if (this.plugin) {
      try {
        return await this.plugin.getBatteryInfo();
      } catch (error) {
        console.error('Failed to get battery info from plugin:', error);
      }
    }

    // Fall back to browser Battery API
    if ('getBattery' in navigator) {
      try {
        const battery = await navigator.getBattery();
        return {
          level: battery.level,
          isCharging: battery.charging,
          chargingTime: battery.chargingTime,
          dischargingTime: battery.dischargingTime
        };
      } catch (error) {
        console.error('Battery API not available:', error);
      }
    }

    return {
      level: 1.0,
      isCharging: false
    };
  }

  /**
   * Get memory information
   */
  async getMemoryInfo() {
    if (this.plugin) {
      try {
        return await this.plugin.getMemoryInfo();
      } catch (error) {
        console.error('Failed to get memory info from plugin:', error);
      }
    }

    // Fall back to browser Memory API (Chrome only)
    if (performance.memory) {
      return {
        totalMemory: performance.memory.jsHeapSizeLimit,
        usedMemory: performance.memory.usedJSHeapSize,
        availableMemory: performance.memory.jsHeapSizeLimit - performance.memory.usedJSHeapSize,
        percentUsed: (performance.memory.usedJSHeapSize / performance.memory.jsHeapSizeLimit) * 100
      };
    }

    return null;
  }

  /**
   * Get storage information
   */
  async getStorageInfo() {
    if (this.plugin) {
      try {
        return await this.plugin.getStorageInfo();
      } catch (error) {
        console.error('Failed to get storage info from plugin:', error);
      }
    }

    // Fall back to browser Storage API
    if ('storage' in navigator && 'estimate' in navigator.storage) {
      try {
        const estimate = await navigator.storage.estimate();
        return {
          internalTotal: estimate.quota,
          internalUsed: estimate.usage,
          internalAvailable: estimate.quota - estimate.usage
        };
      } catch (error) {
        console.error('Storage API not available:', error);
      }
    }

    return null;
  }

  /**
   * Get device capabilities
   */
  async getCapabilities() {
    if (this.plugin) {
      try {
        return await this.plugin.getCapabilities();
      } catch (error) {
        console.error('Failed to get capabilities from plugin:', error);
      }
    }

    // Fall back to feature detection
    return {
      hasCamera: 'mediaDevices' in navigator && 'getUserMedia' in navigator.mediaDevices,
      hasBluetooth: 'bluetooth' in navigator,
      hasNFC: 'nfc' in navigator || 'NDEFReader' in window,
      hasGPS: 'geolocation' in navigator,
      hasWifi: navigator.onLine,
      hasMicrophone: 'mediaDevices' in navigator && 'getUserMedia' in navigator.mediaDevices,
      hasTouchscreen: 'ontouchstart' in window || navigator.maxTouchPoints > 0,
      hasAccelerometer: 'DeviceMotionEvent' in window,
      hasGyroscope: 'DeviceOrientationEvent' in window,
      hasVibration: 'vibrate' in navigator,
      hasNotifications: 'Notification' in window,
      hasServiceWorker: 'serviceWorker' in navigator,
      hasWebGL: this.hasWebGL(),
      hasWebRTC: 'RTCPeerConnection' in window
    };
  }

  /**
   * Check if device is rooted (Android only)
   */
  async isRooted() {
    if (this.plugin) {
      try {
        const result = await this.plugin.isRooted();
        return result.rooted;
      } catch (error) {
        console.error('Failed to check root status:', error);
      }
    }

    return false;
  }

  // Helper methods

  getPlatform() {
    const ua = navigator.userAgent.toLowerCase();
    if (ua.includes('android')) return 'android';
    if (ua.includes('iphone') || ua.includes('ipad') || ua.includes('ipod')) return 'ios';
    if (ua.includes('windows')) return 'windows';
    if (ua.includes('mac')) return 'macos';
    if (ua.includes('linux')) return 'linux';
    return 'web';
  }

  getManufacturer() {
    const ua = navigator.userAgent.toLowerCase();
    if (ua.includes('samsung')) return 'Samsung';
    if (ua.includes('huawei')) return 'Huawei';
    if (ua.includes('xiaomi')) return 'Xiaomi';
    if (ua.includes('oppo')) return 'Oppo';
    if (ua.includes('vivo')) return 'Vivo';
    if (ua.includes('oneplus')) return 'OnePlus';
    if (ua.includes('google pixel')) return 'Google';
    if (ua.includes('iphone') || ua.includes('ipad')) return 'Apple';
    return 'Unknown';
  }

  getModel() {
    const ua = navigator.userAgent;
    // Try to extract model from user agent
    const modelMatch = ua.match(/\(([^)]+)\)/);
    return modelMatch ? modelMatch[1] : 'Unknown';
  }

  getOSVersion() {
    const ua = navigator.userAgent;
    
    // Android
    const androidMatch = ua.match(/Android\s+([\d.]+)/);
    if (androidMatch) return androidMatch[1];
    
    // iOS
    const iosMatch = ua.match(/OS\s+([\d_]+)/);
    if (iosMatch) return iosMatch[1].replace(/_/g, '.');
    
    // Windows
    const windowsMatch = ua.match(/Windows\s+NT\s+([\d.]+)/);
    if (windowsMatch) return windowsMatch[1];
    
    // Mac
    const macMatch = ua.match(/Mac\s+OS\s+X\s+([\d_]+)/);
    if (macMatch) return macMatch[1].replace(/_/g, '.');
    
    return 'Unknown';
  }

  getBrowserInfo() {
    const ua = navigator.userAgent;
    
    if (ua.includes('Chrome')) {
      const match = ua.match(/Chrome\/([\d.]+)/);
      return { name: 'Chrome', version: match ? match[1] : 'Unknown' };
    }
    if (ua.includes('Firefox')) {
      const match = ua.match(/Firefox\/([\d.]+)/);
      return { name: 'Firefox', version: match ? match[1] : 'Unknown' };
    }
    if (ua.includes('Safari') && !ua.includes('Chrome')) {
      const match = ua.match(/Version\/([\d.]+)/);
      return { name: 'Safari', version: match ? match[1] : 'Unknown' };
    }
    if (ua.includes('Edge')) {
      const match = ua.match(/Edge?\/([\d.]+)/);
      return { name: 'Edge', version: match ? match[1] : 'Unknown' };
    }
    
    return { name: 'Unknown', version: 'Unknown' };
  }

  getDeviceId() {
    // Get or create persistent device ID
    let deviceId = localStorage.getItem('linkpoint_device_id');
    
    if (!deviceId) {
      // Generate a random device ID
      deviceId = 'web-' + this.generateUUID();
      localStorage.setItem('linkpoint_device_id', deviceId);
    }
    
    return deviceId;
  }

  generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }

  getConnectionType(connection) {
    if (!navigator.onLine) {
      return 'none';
    }

    if (!connection) {
      return 'unknown';
    }

    const type = connection.type;
    const effectiveType = connection.effectiveType;

    if (type === 'wifi') return 'wifi';
    if (type === 'ethernet') return 'ethernet';
    if (type === 'cellular' || type === 'wimax') {
      if (effectiveType === '4g') return '4g';
      if (effectiveType === '3g') return '3g';
      if (effectiveType === '2g') return '2g';
      return 'cellular';
    }

    return 'unknown';
  }

  hasWebGL() {
    try {
      const canvas = document.createElement('canvas');
      return !!(canvas.getContext('webgl') || canvas.getContext('experimental-webgl'));
    } catch (e) {
      return false;
    }
  }

  // Event handling

  handleOnline() {
    this.getStatus();
  }

  handleOffline() {
    this.currentStatus.connected = false;
    this.emit('change', this.currentStatus);
  }

  handleConnectionChange() {
    this.getStatus();
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = DeviceInfo;
}

// Make available globally
window.DeviceInfo = DeviceInfo;
