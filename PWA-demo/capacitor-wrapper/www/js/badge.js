/**
 * Badge API for Linkpoint PWA
 * Bridges Java plugin with browser Badge API
 */

class Badge {
  constructor() {
    this.isCapacitor = typeof window.Capacitor !== 'undefined';
    this.plugin = this.isCapacitor ? window.Capacitor.Plugins.Badge : null;
    this.currentCount = 0;
    this.supported = false;
  }

  /**
   * Initialize badge
   */
  async init() {
    this.supported = await this.isSupported();
  }

  /**
   * Set badge count
   */
  async set(count) {
    if (count < 0) {
      count = 0;
    }

    this.currentCount = count;

    // Use plugin if available (Android)
    if (this.plugin) {
      try {
        const result = await this.plugin.set({ count });
        return result.success;
      } catch (error) {
        console.error('Plugin badge set failed:', error);
      }
    }

    // Fall back to browser Badge API
    if ('setAppBadge' in navigator) {
      try {
        if (count > 0) {
          await navigator.setAppBadge(count);
        } else {
          await navigator.clearAppBadge();
        }
        return true;
      } catch (error) {
        console.error('Browser badge API failed:', error);
      }
    }

    // Fall back to updating document title
    this.updateDocumentTitle(count);
    
    return false;
  }

  /**
   * Clear badge count
   */
  async clear() {
    return this.set(0);
  }

  /**
   * Get current badge count
   */
  async get() {
    if (this.plugin) {
      try {
        const result = await this.plugin.get();
        this.currentCount = result.count;
        return result.count;
      } catch (error) {
        console.error('Failed to get badge count:', error);
      }
    }

    return this.currentCount;
  }

  /**
   * Increment badge count
   */
  async increment(amount = 1) {
    const newCount = this.currentCount + amount;
    return this.set(newCount);
  }

  /**
   * Decrement badge count
   */
  async decrement(amount = 1) {
    const newCount = Math.max(0, this.currentCount - amount);
    return this.set(newCount);
  }

  /**
   * Check if badges are supported
   */
  async isSupported() {
    if (this.plugin) {
      try {
        const result = await this.plugin.isSupported();
        return result.supported;
      } catch (error) {
        console.error('Failed to check badge support:', error);
      }
    }

    // Check for browser Badge API
    return 'setAppBadge' in navigator;
  }

  // Helper methods

  updateDocumentTitle(count) {
    // Update document title with unread count
    const baseTitle = 'Linkpoint';
    
    if (count > 0) {
      document.title = `(${count}) ${baseTitle}`;
    } else {
      document.title = baseTitle;
    }
  }

  /**
   * Update favicon with badge (advanced fallback)
   */
  updateFavicon(count) {
    if (count <= 0) {
      this.clearFaviconBadge();
      return;
    }

    try {
      const favicon = document.querySelector('link[rel="icon"]');
      if (!favicon) return;

      const canvas = document.createElement('canvas');
      canvas.width = 32;
      canvas.height = 32;
      const ctx = canvas.getContext('2d');

      // Draw original favicon
      const img = new Image();
      img.onload = () => {
        ctx.drawImage(img, 0, 0, 32, 32);

        // Draw badge circle
        ctx.fillStyle = '#ff0000';
        ctx.beginPath();
        ctx.arc(24, 8, 8, 0, 2 * Math.PI);
        ctx.fill();

        // Draw count text
        ctx.fillStyle = '#ffffff';
        ctx.font = 'bold 10px Arial';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(count > 99 ? '99+' : count.toString(), 24, 8);

        // Update favicon
        favicon.href = canvas.toDataURL('image/png');
      };
      img.src = favicon.href;
    } catch (error) {
      console.error('Failed to update favicon badge:', error);
    }
  }

  clearFaviconBadge() {
    // Restore original favicon
    const favicon = document.querySelector('link[rel="icon"]');
    if (favicon && favicon.dataset.original) {
      favicon.href = favicon.dataset.original;
    }
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = Badge;
}

// Make available globally
window.Badge = Badge;
