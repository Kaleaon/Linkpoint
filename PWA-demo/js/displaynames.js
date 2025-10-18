/**
 * Linkpoint PWA - Display Names System
 * Ported from Android SLDisplayNameFetcher.kt
 * Handles Second Life display names and username resolution
 */

class DisplayNameCache {
  constructor() {
    this.cache = new Map(); // UUID -> DisplayName mapping
    this.pendingRequests = new Set();
    this.maxCacheSize = 1000;
    this.cacheTimeout = 3600000; // 1 hour in milliseconds
  }
  
  /**
   * Display name object structure
   */
  createDisplayName(data) {
    return {
      uuid: data.uuid || '',
      username: data.username || '',
      displayName: data.display_name || data.username || '',
      legacyFirstName: data.legacy_first_name || '',
      legacyLastName: data.legacy_last_name || '',
      isDefaultName: data.is_default_name || false,
      displayNameExpires: data.display_name_expires || null,
      timestamp: Date.now()
    };
  }
  
  /**
   * Get display name from cache
   * @param {string} uuid - Agent UUID
   * @returns {Object|null} - Display name object or null
   */
  get(uuid) {
    const cached = this.cache.get(uuid);
    
    if (!cached) {
      return null;
    }
    
    // Check if expired
    if (Date.now() - cached.timestamp > this.cacheTimeout) {
      this.cache.delete(uuid);
      return null;
    }
    
    return cached;
  }
  
  /**
   * Set display name in cache
   * @param {string} uuid - Agent UUID
   * @param {Object} displayName - Display name object
   */
  set(uuid, displayName) {
    // Enforce cache size limit
    if (this.cache.size >= this.maxCacheSize) {
      // Remove oldest entry
      const firstKey = this.cache.keys().next().value;
      this.cache.delete(firstKey);
    }
    
    this.cache.set(uuid, displayName);
  }
  
  /**
   * Clear cache
   */
  clear() {
    this.cache.clear();
    this.pendingRequests.clear();
  }
  
  /**
   * Get cache statistics
   * @returns {Object} - Cache stats
   */
  getStats() {
    return {
      size: this.cache.size,
      maxSize: this.maxCacheSize,
      pendingRequests: this.pendingRequests.size
    };
  }
}

/**
 * Display name fetcher
 */
class DisplayNameFetcher {
  constructor(protocol) {
    this.protocol = protocol;
    this.cache = new DisplayNameCache();
    this.batchSize = 100; // Max UUIDs per batch request
    this.batchDelay = 100; // Delay before sending batch (ms)
    this.batchQueue = [];
    this.batchTimer = null;
  }
  
  /**
   * Fetch display name for a single UUID
   * @param {string} uuid - Agent UUID
   * @returns {Promise<Object>} - Display name object
   */
  async fetchDisplayName(uuid) {
    // Check cache first
    const cached = this.cache.get(uuid);
    if (cached) {
      return cached;
    }
    
    // Check if already pending
    if (this.cache.pendingRequests.has(uuid)) {
      // Wait for pending request
      return new Promise((resolve) => {
        const checkInterval = setInterval(() => {
          const result = this.cache.get(uuid);
          if (result) {
            clearInterval(checkInterval);
            resolve(result);
          }
        }, 100);
        
        // Timeout after 10 seconds
        setTimeout(() => {
          clearInterval(checkInterval);
          resolve(this.createFallbackDisplayName(uuid));
        }, 10000);
      });
    }
    
    // Mark as pending
    this.cache.pendingRequests.add(uuid);
    
    try {
      // Fetch from capability
      const displayName = await this.fetchFromCapability(uuid);
      this.cache.set(uuid, displayName);
      return displayName;
    } catch (error) {
      console.error(`Failed to fetch display name for ${uuid}:`, error);
      return this.createFallbackDisplayName(uuid);
    } finally {
      this.cache.pendingRequests.delete(uuid);
    }
  }
  
  /**
   * Fetch multiple display names in batch
   * @param {Array<string>} uuids - Array of agent UUIDs
   * @returns {Promise<Array<Object>>} - Array of display name objects
   */
  async fetchDisplayNames(uuids) {
    const results = [];
    const toFetch = [];
    
    // Check cache first
    for (const uuid of uuids) {
      const cached = this.cache.get(uuid);
      if (cached) {
        results.push(cached);
      } else {
        toFetch.push(uuid);
      }
    }
    
    // Fetch uncached UUIDs in batches
    for (let i = 0; i < toFetch.length; i += this.batchSize) {
      const batch = toFetch.slice(i, i + this.batchSize);
      const batchResults = await this.fetchBatch(batch);
      results.push(...batchResults);
    }
    
    return results;
  }
  
  /**
   * Fetch a batch of display names
   * @param {Array<string>} uuids - Array of UUIDs
   * @returns {Promise<Array<Object>>} - Array of display name objects
   */
  async fetchBatch(uuids) {
    try {
      const cap = this.protocol.getCapability('GetDisplayNames');
      if (!cap) {
        console.warn('GetDisplayNames capability not available');
        return uuids.map(uuid => this.createFallbackDisplayName(uuid));
      }
      
      const response = await fetch(cap, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/llsd+xml'
        },
        body: this.createBatchRequest(uuids)
      });
      
      const data = await response.json();
      return this.parseBatchResponse(data);
    } catch (error) {
      console.error('Batch display name fetch failed:', error);
      return uuids.map(uuid => this.createFallbackDisplayName(uuid));
    }
  }
  
  /**
   * Fetch from capability
   * @param {string} uuid - Agent UUID
   * @returns {Promise<Object>} - Display name object
   */
  async fetchFromCapability(uuid) {
    const cap = this.protocol.getCapability('GetDisplayNames');
    if (!cap) {
      throw new Error('GetDisplayNames capability not available');
    }
    
    const response = await fetch(`${cap}?ids=${uuid}`);
    const data = await response.json();
    
    if (data && data.agents && data.agents.length > 0) {
      return this.cache.createDisplayName(data.agents[0]);
    }
    
    throw new Error('No display name data received');
  }
  
  /**
   * Create batch request XML
   * @param {Array<string>} uuids - UUIDs to fetch
   * @returns {string} - LLSD XML request
   */
  createBatchRequest(uuids) {
    // Simple LLSD array
    let xml = '<?xml version="1.0" encoding="UTF-8"?>\n';
    xml += '<llsd><array>\n';
    for (const uuid of uuids) {
      xml += `  <uuid>${uuid}</uuid>\n`;
    }
    xml += '</array></llsd>';
    return xml;
  }
  
  /**
   * Parse batch response
   * @param {Object} data - Response data
   * @returns {Array<Object>} - Display name objects
   */
  parseBatchResponse(data) {
    const results = [];
    
    if (data && data.agents) {
      for (const agent of data.agents) {
        const displayName = this.cache.createDisplayName(agent);
        this.cache.set(agent.uuid, displayName);
        results.push(displayName);
      }
    }
    
    return results;
  }
  
  /**
   * Create fallback display name (when fetch fails)
   * @param {string} uuid - Agent UUID
   * @returns {Object} - Fallback display name
   */
  createFallbackDisplayName(uuid) {
    return this.cache.createDisplayName({
      uuid: uuid,
      username: uuid.substr(0, 8) + '...',
      display_name: 'Unknown User',
      is_default_name: true
    });
  }
  
  /**
   * Queue UUID for batch fetching
   * @param {string} uuid - Agent UUID
   * @returns {Promise<Object>} - Display name object
   */
  queueForBatch(uuid) {
    return new Promise((resolve) => {
      this.batchQueue.push({ uuid, resolve });
      
      // Clear existing timer
      if (this.batchTimer) {
        clearTimeout(this.batchTimer);
      }
      
      // Set new timer or process immediately if batch is full
      if (this.batchQueue.length >= this.batchSize) {
        this.processBatch();
      } else {
        this.batchTimer = setTimeout(() => this.processBatch(), this.batchDelay);
      }
    });
  }
  
  /**
   * Process queued batch
   */
  async processBatch() {
    if (this.batchQueue.length === 0) {
      return;
    }
    
    const batch = this.batchQueue.splice(0, this.batchSize);
    const uuids = batch.map(item => item.uuid);
    
    try {
      const results = await this.fetchBatch(uuids);
      
      // Resolve promises
      for (let i = 0; i < batch.length; i++) {
        const result = results[i] || this.createFallbackDisplayName(batch[i].uuid);
        batch[i].resolve(result);
      }
    } catch (error) {
      console.error('Batch processing failed:', error);
      
      // Resolve with fallback names
      for (const item of batch) {
        item.resolve(this.createFallbackDisplayName(item.uuid));
      }
    }
  }
  
  /**
   * Format display name for UI
   * @param {Object} displayName - Display name object
   * @param {boolean} showUsername - Whether to show username
   * @returns {string} - Formatted name
   */
  formatDisplayName(displayName, showUsername = false) {
    if (!displayName) {
      return 'Unknown User';
    }
    
    if (displayName.isDefaultName || !displayName.displayName) {
      // Use legacy name format
      return `${displayName.legacyFirstName} ${displayName.legacyLastName}`.trim() || displayName.username;
    }
    
    if (showUsername) {
      return `${displayName.displayName} (${displayName.username})`;
    }
    
    return displayName.displayName;
  }
}

// Export classes
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { DisplayNameCache, DisplayNameFetcher };
}
