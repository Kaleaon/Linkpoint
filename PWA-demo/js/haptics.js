/**
 * Haptics API for Linkpoint PWA
 * Bridges Java plugin with browser Vibration API
 */

class Haptics {
  constructor() {
    this.isCapacitor = typeof window.Capacitor !== 'undefined';
    this.plugin = this.isCapacitor ? window.Capacitor.Plugins.Haptics : null;
    this.available = false;
  }

  /**
   * Initialize haptics
   */
  async init() {
    this.available = await this.isAvailable();
  }

  /**
   * Check if haptics are available
   */
  async isAvailable() {
    if (this.plugin) {
      try {
        const result = await this.plugin.isAvailable();
        return result.available;
      } catch (error) {
        console.error('Failed to check haptics availability:', error);
      }
    }

    // Browser fallback
    return 'vibrate' in navigator;
  }

  /**
   * Trigger haptic impact feedback
   */
  async impact(style = 'medium') {
    if (!this.available) {
      return false;
    }

    if (this.plugin) {
      try {
        await this.plugin.impact({ style });
        return true;
      } catch (error) {
        console.error('Haptic impact failed:', error);
        return false;
      }
    }

    // Browser fallback
    if ('vibrate' in navigator) {
      const duration = this.getDurationForStyle(style);
      navigator.vibrate(duration);
      return true;
    }

    return false;
  }

  /**
   * Trigger notification haptic pattern
   */
  async notification(type = 'success') {
    if (!this.available) {
      return false;
    }

    if (this.plugin) {
      try {
        await this.plugin.notification({ type });
        return true;
      } catch (error) {
        console.error('Haptic notification failed:', error);
        return false;
      }
    }

    // Browser fallback
    if ('vibrate' in navigator) {
      const pattern = this.getPatternForNotification(type);
      navigator.vibrate(pattern);
      return true;
    }

    return false;
  }

  /**
   * Trigger custom vibration
   */
  async vibrate(duration = 200) {
    if (!this.available) {
      return false;
    }

    if (this.plugin) {
      try {
        await this.plugin.vibrate({ duration });
        return true;
      } catch (error) {
        console.error('Vibration failed:', error);
        return false;
      }
    }

    // Browser fallback
    if ('vibrate' in navigator) {
      navigator.vibrate(duration);
      return true;
    }

    return false;
  }

  /**
   * Trigger custom vibration pattern
   */
  async vibratePattern(pattern) {
    if (!this.available) {
      return false;
    }

    if (this.plugin) {
      try {
        await this.plugin.vibratePattern({ pattern });
        return true;
      } catch (error) {
        console.error('Pattern vibration failed:', error);
        return false;
      }
    }

    // Browser fallback
    if ('vibrate' in navigator) {
      navigator.vibrate(pattern);
      return true;
    }

    return false;
  }

  /**
   * Cancel all vibrations
   */
  async cancel() {
    if (this.plugin) {
      try {
        await this.plugin.cancel();
        return true;
      } catch (error) {
        console.error('Failed to cancel vibration:', error);
        return false;
      }
    }

    // Browser fallback
    if ('vibrate' in navigator) {
      navigator.vibrate(0);
      return true;
    }

    return false;
  }

  /**
   * Trigger selection changed haptic
   */
  async selectionChanged() {
    if (!this.available) {
      return false;
    }

    if (this.plugin) {
      try {
        await this.plugin.selectionChanged();
        return true;
      } catch (error) {
        console.error('Selection haptic failed:', error);
        return false;
      }
    }

    // Browser fallback - light tap
    if ('vibrate' in navigator) {
      navigator.vibrate(10);
      return true;
    }

    return false;
  }

  /**
   * Convenience methods for common haptic patterns
   */

  async light() {
    return this.impact('light');
  }

  async medium() {
    return this.impact('medium');
  }

  async heavy() {
    return this.impact('heavy');
  }

  async success() {
    return this.notification('success');
  }

  async warning() {
    return this.notification('warning');
  }

  async error() {
    return this.notification('error');
  }

  // Helper methods

  getDurationForStyle(style) {
    const durations = {
      'light': 50,
      'medium': 100,
      'heavy': 200
    };

    return durations[style] || 100;
  }

  getPatternForNotification(type) {
    const patterns = {
      'success': [0, 50, 100, 50],
      'warning': [0, 100, 100, 100],
      'error': [0, 200, 100, 200, 100, 200]
    };

    return patterns[type] || [0, 100];
  }

  // Storage helpers (for queues in browser mode)

  loadQueuesFromStorage() {
    const queuesData = localStorage.getItem('linkpoint_sync_queues');
    if (queuesData) {
      try {
        const queues = JSON.parse(queuesData);
        this.queues = new Map(Object.entries(queues));
      } catch (error) {
        console.error('Failed to load sync queues:', error);
      }
    }
  }

  saveQueueToStorage(tag) {
    const queues = Object.fromEntries(this.queues);
    localStorage.setItem('linkpoint_sync_queues', JSON.stringify(queues));
  }

  async syncAll() {
    for (const tag of this.queues.keys()) {
      this.emit('syncTriggered', { tag });
    }
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = Haptics;
}

// Make available globally
window.Haptics = Haptics;
