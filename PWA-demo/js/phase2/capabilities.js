/**
 * Linkpoint PWA - Capabilities Manager (Features 9-12)
 * 
 * Phase 2: Core Protocol Extensions - Priority 1
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 28-33)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/
 * 
 * Manages Second Life capability URLs with caching and retry logic.
 * 
 * Features:
 * - Feature 9: Capability caching
 * - Feature 10: Seed capability parsing
 * - Feature 11: Capability URL resolution
 * - Feature 12: Timeout and retry logic
 * 
 * @module phase2/capabilities
 */

/**
 * Capabilities Manager
 * Manages capability URLs for Second Life protocol
 */
class CapabilitiesManager {
  /**
   * @param {Object} options - Configuration options
   */
  constructor(options = {}) {
    this.capabilities = new Map();
    this.seedUrl = null;
    this.cacheTimeout = options.cacheTimeout || 3600000; // 1 hour default
    this.retryAttempts = options.retryAttempts || 3;
    this.retryDelay = options.retryDelay || 1000; // milliseconds
  }

  /**
   * Feature 9: Capability caching
   * Cache a capability URL with expiration
   * 
   * @param {string} name - Capability name
   * @param {string} url - Capability URL
   * @param {number} expires - Optional expiration timestamp
   * 
   * TODO: Implement persistent storage (IndexedDB)
   * TODO: Add cache invalidation strategies
   */
  setCapability(name, url, expires = null) {
    if (!name || typeof name !== 'string') {
      throw new Error('Valid capability name required');
    }
    if (!url || typeof url !== 'string') {
      throw new Error('Valid capability URL required');
    }
    
    this.capabilities.set(name, {
      url: url,
      timestamp: Date.now(),
      expires: expires || (Date.now() + this.cacheTimeout)
    });
    
    console.log(`[Capabilities] Cached: ${name}`);
  }

  /**
   * Feature 11: Capability URL resolution
   * Resolve a capability URL by name
   * 
   * @param {string} name - Capability name to resolve
   * @returns {string|null} Capability URL or null if not found/expired
   */
  resolveCapability(name) {
    if (!name || typeof name !== 'string') {
      return null;
    }
    
    const cap = this.capabilities.get(name);
    
    if (!cap) {
      console.warn(`[Capabilities] Not found: ${name}`);
      return null;
    }
    
    // Check expiration
    if (cap.expires && Date.now() > cap.expires) {
      console.warn(`[Capabilities] Expired: ${name}`);
      this.capabilities.delete(name);
      return null;
    }
    
    return cap.url;
  }

  /**
   * Feature 10: Seed capability parsing
   * Parse seed capability response and cache all capabilities
   * 
   * @param {Object} seedData - Seed capability response data
   * @returns {Promise<void>}
   * 
   * TODO: Implement LLSD parser for seed capability format
   * TODO: Add validation for capability format
   */
  async parseSeedCapability(seedData) {
    if (!seedData || typeof seedData !== 'object') {
      throw new Error('Valid seed data required');
    }
    
    // Store seed URL if provided
    if (seedData.seed_url) {
      this.seedUrl = seedData.seed_url;
    }
    
    // Parse and cache all capabilities from seed
    for (const [name, url] of Object.entries(seedData)) {
      if (name !== 'seed_url' && typeof url === 'string') {
        this.setCapability(name, url);
      }
    }
    
    console.log(`[Capabilities] Parsed ${this.capabilities.size} capabilities from seed`);
    return Promise.resolve();
  }

  /**
   * Feature 12: Timeout and retry logic
   * Fetch capability with retry support
   * 
   * @param {string} url - URL to fetch
   * @param {number} attempt - Current attempt number
   * @returns {Promise<Response>}
   * 
   * TODO: Implement exponential backoff
   * TODO: Add circuit breaker pattern
   */
  async fetchWithRetry(url, attempt = 1) {
    if (!url || typeof url !== 'string') {
      throw new Error('Valid URL required');
    }
    
    try {
      console.log(`[Capabilities] Fetch attempt ${attempt}: ${url}`);
      
      // TODO: Implement actual fetch with timeout
      // This is a placeholder for the retry mechanism
      const response = await fetch(url);
      
      if (!response.ok && attempt < this.retryAttempts) {
        await this.delay(this.retryDelay * attempt);
        return this.fetchWithRetry(url, attempt + 1);
      }
      
      return response;
    } catch (error) {
      if (attempt < this.retryAttempts) {
        console.warn(`[Capabilities] Retry ${attempt}/${this.retryAttempts}`);
        await this.delay(this.retryDelay * attempt);
        return this.fetchWithRetry(url, attempt + 1);
      }
      throw error;
    }
  }

  /**
   * Helper: Delay utility for retry logic
   * @private
   */
  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  /**
   * Check if capability exists and is valid
   * @param {string} name - Capability name
   * @returns {boolean}
   */
  hasCapability(name) {
    return this.resolveCapability(name) !== null;
  }

  /**
   * Clear all cached capabilities
   */
  clearCache() {
    const count = this.capabilities.size;
    this.capabilities.clear();
    console.log(`[Capabilities] Cleared ${count} cached capabilities`);
  }

  /**
   * Get cache statistics
   * @returns {Object}
   */
  getStats() {
    return {
      totalCapabilities: this.capabilities.size,
      seedUrl: this.seedUrl,
      cacheTimeout: this.cacheTimeout
    };
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { CapabilitiesManager };
}

/**
 * Example Usage:
 * 
 * const caps = new CapabilitiesManager({
 *   cacheTimeout: 3600000,
 *   retryAttempts: 3
 * });
 * 
 * // Parse seed capability
 * await caps.parseSeedCapability({
 *   EventQueueGet: 'https://sim.example.com/cap/eventqueue',
 *   ChatSessionRequest: 'https://sim.example.com/cap/chat'
 * });
 * 
 * // Resolve capability
 * const eventQueueUrl = caps.resolveCapability('EventQueueGet');
 */
