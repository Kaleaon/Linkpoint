/**
 * Background Sync API for Linkpoint PWA
 * Bridges Java plugin with browser Background Sync API
 */

class BackgroundSync extends Utils.EventEmitter {
  constructor() {
    super();
    this.isCapacitor = typeof window.Capacitor !== 'undefined';
    this.plugin = this.isCapacitor ? window.Capacitor.Plugins.BackgroundSync : null;
    this.queues = new Map();
    this.syncRegistrations = new Map();
  }

  /**
   * Initialize background sync
   */
  async init() {
    // Load existing queues
    if (!this.plugin) {
      this.loadQueuesFromStorage();
    }

    // Listen for online events to trigger sync
    window.addEventListener('online', () => {
      this.syncAll();
    });
  }

  /**
   * Register a sync queue
   */
  async register(tag) {
    if (this.plugin) {
      try {
        const result = await this.plugin.register({ tag });
        return result.success;
      } catch (error) {
        throw new Error(`Failed to register sync: ${error.message}`);
      }
    }

    // Browser fallback
    if (!this.queues.has(tag)) {
      this.queues.set(tag, []);
      this.saveQueueToStorage(tag);
    }

    // Try to use native Background Sync API
    if ('serviceWorker' in navigator && 'sync' in ServiceWorkerRegistration.prototype) {
      try {
        const registration = await navigator.serviceWorker.ready;
        await registration.sync.register(tag);
        this.syncRegistrations.set(tag, registration);
      } catch (error) {
        console.warn('Background Sync API not available:', error);
      }
    }

    return true;
  }

  /**
   * Add item to sync queue
   */
  async enqueue(tag, data) {
    if (this.plugin) {
      try {
        const result = await this.plugin.enqueue({
          tag,
          data: typeof data === 'object' ? data : { data }
        });
        return result.queueSize;
      } catch (error) {
        throw new Error(`Failed to enqueue: ${error.message}`);
      }
    }

    // Browser fallback
    if (!this.queues.has(tag)) {
      await this.register(tag);
    }

    const queue = this.queues.get(tag);
    const item = {
      timestamp: Date.now(),
      data
    };
    
    queue.push(item);
    
    // Limit queue size
    const maxSize = 1000;
    if (queue.length > maxSize) {
      queue.shift();
    }

    this.saveQueueToStorage(tag);
    this.emit('enqueued', { tag, queueSize: queue.length });

    // Trigger sync if online
    if (navigator.onLine) {
      setTimeout(() => this.sync(tag), 100);
    }

    return queue.length;
  }

  /**
   * Get all items from queue
   */
  async getQueue(tag) {
    if (this.plugin) {
      try {
        const result = await this.plugin.getQueue({ tag });
        return result.items;
      } catch (error) {
        throw new Error(`Failed to get queue: ${error.message}`);
      }
    }

    // Browser fallback
    const queue = this.queues.get(tag);
    return queue ? [...queue] : [];
  }

  /**
   * Remove items from queue
   */
  async dequeue(tag, count = 1) {
    if (this.plugin) {
      try {
        const result = await this.plugin.dequeue({ tag, count });
        return result.removed;
      } catch (error) {
        throw new Error(`Failed to dequeue: ${error.message}`);
      }
    }

    // Browser fallback
    const queue = this.queues.get(tag);
    if (!queue) {
      return 0;
    }

    const removed = queue.splice(0, count);
    this.saveQueueToStorage(tag);
    
    return removed.length;
  }

  /**
   * Clear entire queue
   */
  async clearQueue(tag) {
    if (this.plugin) {
      try {
        await this.plugin.clearQueue({ tag });
        return true;
      } catch (error) {
        throw new Error(`Failed to clear queue: ${error.message}`);
      }
    }

    // Browser fallback
    if (this.queues.has(tag)) {
      this.queues.set(tag, []);
      this.saveQueueToStorage(tag);
    }

    return true;
  }

  /**
   * Get queue size
   */
  async getQueueSize(tag) {
    if (this.plugin) {
      try {
        const result = await this.plugin.getQueueSize({ tag });
        return result.size;
      } catch (error) {
        return 0;
      }
    }

    // Browser fallback
    const queue = this.queues.get(tag);
    return queue ? queue.length : 0;
  }

  /**
   * Trigger sync for a specific queue
   */
  async sync(tag) {
    const queue = await this.getQueue(tag);
    
    if (queue.length === 0) {
      return { synced: 0, failed: 0 };
    }

    this.emit('syncStarted', { tag, queueSize: queue.length });

    let synced = 0;
    let failed = 0;

    // Process queue items
    for (const item of queue) {
      try {
        // Emit event for application to handle
        const shouldContinue = await new Promise((resolve) => {
          this.emit('syncItem', {
            tag,
            item,
            resolve: (success) => resolve(success)
          });

          // Auto-resolve after timeout
          setTimeout(() => resolve(false), 5000);
        });

        if (shouldContinue) {
          synced++;
          await this.dequeue(tag, 1);
        } else {
          failed++;
        }
      } catch (error) {
        console.error(`Sync failed for item:`, error);
        failed++;
      }
    }

    this.emit('syncCompleted', { tag, synced, failed });

    return { synced, failed };
  }

  /**
   * Sync all registered queues
   */
  async syncAll() {
    const tags = this.plugin ? [] : Array.from(this.queues.keys());
    
    for (const tag of tags) {
      try {
        await this.sync(tag);
      } catch (error) {
        console.error(`Failed to sync queue ${tag}:`, error);
      }
    }
  }

  // Storage helpers for browser fallback

  loadQueuesFromStorage() {
    const queuesData = localStorage.getItem('linkpoint_sync_queues');
    if (queuesData) {
      try {
        const queues = JSON.parse(queuesData);
        this.queues = new Map(Object.entries(queues));
      } catch (error) {
        console.error('Failed to load queues:', error);
      }
    }
  }

  saveQueueToStorage(tag) {
    const queues = Object.fromEntries(this.queues);
    localStorage.setItem('linkpoint_sync_queues', JSON.stringify(queues));
  }

  // Encryption helpers (simple obfuscation for browser)

  async encrypt(data) {
    // Use Web Crypto API if available
    if (window.crypto && window.crypto.subtle) {
      try {
        const encoder = new TextEncoder();
        const keyMaterial = await window.crypto.subtle.importKey(
          'raw',
          encoder.encode('linkpoint_storage_key_v1'),
          { name: 'PBKDF2' },
          false,
          ['deriveBits', 'deriveKey']
        );

        const key = await window.crypto.subtle.deriveKey(
          {
            name: 'PBKDF2',
            salt: encoder.encode('linkpoint_salt'),
            iterations: 100000,
            hash: 'SHA-256'
          },
          keyMaterial,
          { name: 'AES-GCM', length: 256 },
          false,
          ['encrypt', 'decrypt']
        );

        const iv = window.crypto.getRandomValues(new Uint8Array(12));
        const encrypted = await window.crypto.subtle.encrypt(
          { name: 'AES-GCM', iv },
          key,
          encoder.encode(data)
        );

        // Combine IV and encrypted data
        const combined = new Uint8Array(iv.length + encrypted.byteLength);
        combined.set(iv, 0);
        combined.set(new Uint8Array(encrypted), iv.length);

        return btoa(String.fromCharCode(...combined));
      } catch (error) {
        console.warn('Web Crypto encryption failed:', error);
      }
    }

    // Fallback: Base64 encoding (not secure!)
    return btoa(data);
  }

  async decrypt(encrypted) {
    // Use Web Crypto API if available
    if (window.crypto && window.crypto.subtle && encrypted.length > 16) {
      try {
        const combined = Uint8Array.from(atob(encrypted), c => c.charCodeAt(0));
        const iv = combined.slice(0, 12);
        const data = combined.slice(12);

        const encoder = new TextEncoder();
        const keyMaterial = await window.crypto.subtle.importKey(
          'raw',
          encoder.encode('linkpoint_storage_key_v1'),
          { name: 'PBKDF2' },
          false,
          ['deriveBits', 'deriveKey']
        );

        const key = await window.crypto.subtle.deriveKey(
          {
            name: 'PBKDF2',
            salt: encoder.encode('linkpoint_salt'),
            iterations: 100000,
            hash: 'SHA-256'
          },
          keyMaterial,
          { name: 'AES-GCM', length: 256 },
          false,
          ['encrypt', 'decrypt']
        );

        const decrypted = await window.crypto.subtle.decrypt(
          { name: 'AES-GCM', iv },
          key,
          data
        );

        const decoder = new TextDecoder();
        return decoder.decode(decrypted);
      } catch (error) {
        console.warn('Web Crypto decryption failed:', error);
      }
    }

    // Fallback: Base64 decoding
    return atob(encrypted);
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = BackgroundSync;
}

// Make available globally
window.BackgroundSync = BackgroundSync;
