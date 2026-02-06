/**
 * Network Status API for Linkpoint PWA
 * Bridges Java plugin with browser Network Information API
 */

class NetworkStatus extends Utils.EventEmitter {
  constructor() {
    super();
    this.isCapacitor = typeof window.Capacitor !== 'undefined';
    this.plugin = this.isCapacitor ? window.Capacitor.Plugins.NetworkStatus : null;
    this.currentStatus = {
      connected: navigator.onLine,
      connectionType: 'unknown',
      hasInternet: navigator.onLine
    };
    this.monitoring = false;
  }

  /**
   * Initialize network monitoring
   */
  async init() {
    await this.getStatus();
    this.startMonitoring();
  }

  /**
   * Get current network status
   */
  async getStatus() {
    if (this.plugin) {
      try {
        const status = await this.plugin.getStatus();
        this.currentStatus = status;
        return status;
      } catch (error) {
        console.error('Failed to get network status:', error);
      }
    }

    // Fall back to browser Network Information API
    const connection = navigator.connection || navigator.mozConnection || navigator.webkitConnection;
    
    this.currentStatus = {
      connected: navigator.onLine,
      connectionType: this.getConnectionType(connection),
      hasInternet: navigator.onLine,
      effectiveType: connection?.effectiveType,
      downlink: connection?.downlink,
      rtt: connection?.rtt,
      saveData: connection?.saveData
    };

    return this.currentStatus;
  }

  /**
   * Start monitoring network changes
   */
  async startMonitoring() {
    if (this.monitoring) {
      return;
    }

    this.monitoring = true;

    // Use plugin for native monitoring
    if (this.plugin) {
      try {
        await this.plugin.startMonitoring();
        
        // Listen for plugin events
        await this.plugin.addListener('networkStatusChange', (status) => {
          this.currentStatus = status;
          this.emit('change', status);
        });

        return;
      } catch (error) {
        console.warn('Plugin monitoring failed, falling back to browser API:', error);
      }
    }

    // Fall back to browser events
    window.addEventListener('online', this.handleOnline.bind(this));
    window.addEventListener('offline', this.handleOffline.bind(this));

    const connection = navigator.connection || navigator.mozConnection || navigator.webkitConnection;
    if (connection) {
      connection.addEventListener('change', this.handleConnectionChange.bind(this));
    }
  }

  /**
   * Stop monitoring network changes
   */
  async stopMonitoring() {
    if (!this.monitoring) {
      return;
    }

    this.monitoring = false;

    if (this.plugin) {
      try {
        await this.plugin.stopMonitoring();
        await this.plugin.removeAllListeners();
      } catch (error) {
        console.error('Failed to stop plugin monitoring:', error);
      }
    }

    // Remove browser event listeners
    window.removeEventListener('online', this.handleOnline);
    window.removeEventListener('offline', this.handleOffline);

    const connection = navigator.connection || navigator.mozConnection || navigator.webkitConnection;
    if (connection) {
      connection.removeEventListener('change', this.handleConnectionChange);
    }
  }

  /**
   * Check if network is available
   */
  isConnected() {
    return this.currentStatus.connected;
  }

  /**
   * Get connection type
   */
  getConnectionType() {
    return this.currentStatus.connectionType;
  }

  /**
   * Check if connection is metered
   */
  isMetered() {
    const type = this.currentStatus.connectionType;
    return type === 'cellular' || type === '3g' || type === '4g' || type === '5g';
  }

  /**
   * Check if connection is fast enough for features
   */
  isFastConnection() {
    const effectiveType = this.currentStatus.effectiveType;
    return effectiveType === '4g' || effectiveType === '5g' || 
           this.currentStatus.connectionType === 'wifi' ||
           this.currentStatus.connectionType === 'ethernet';
  }

  // Event handlers

  handleOnline() {
    this.getStatus().then(() => {
      this.emit('change', this.currentStatus);
      this.emit('online', this.currentStatus);
    });
  }

  handleOffline() {
    this.currentStatus.connected = false;
    this.currentStatus.hasInternet = false;
    this.emit('change', this.currentStatus);
    this.emit('offline', this.currentStatus);
  }

  handleConnectionChange() {
    this.getStatus().then(() => {
      this.emit('change', this.currentStatus);
    });
  }

  // Helper methods

  getConnectionType(connection) {
    if (!navigator.onLine) {
      return 'none';
    }

    if (!connection) {
      return 'unknown';
    }

    const type = connection.type;
    const effectiveType = connection.effectiveType;

    // Map browser connection types to our types
    if (type === 'wifi') return 'wifi';
    if (type === 'ethernet') return 'ethernet';
    if (type === 'cellular') {
      if (effectiveType === '4g') return '4g';
      if (effectiveType === '3g') return '3g';
      if (effectiveType === '2g') return '2g';
      return 'cellular';
    }

    // Use effective type as fallback
    if (effectiveType === '4g') return '4g';
    if (effectiveType === '3g') return '3g';
    if (effectiveType === '2g') return '2g';
    if (effectiveType === 'slow-2g') return '2g';

    return 'unknown';
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = NetworkStatus;
}

// Make available globally
window.NetworkStatus = NetworkStatus;
