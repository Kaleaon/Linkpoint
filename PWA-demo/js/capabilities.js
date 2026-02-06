/**
 * Linkpoint PWA - Capabilities Manager (Features 9-12)
 * Ported from Android slproto/caps/
 * Manages Second Life capability URLs
 */

class CapabilitiesManager {
  constructor() {
    this.capabilities = new Map();
    this.seedUrl = null;
    this.regionHandle = null;
    this.cacheTimeout = 3600000; // 1 hour
    this.retryAttempts = 3;
    this.retryDelay = 1000;
  }
  
  /**
   * Feature 9: Capability caching
   * Cache capability URLs with expiration
   */
  setCapability(name, url, expires = null) {
    this.capabilities.set(name, {
      url: url,
      timestamp: Date.now(),
      expires: expires || (Date.now() + this.cacheTimeout)
    });
  }
  
  /**
   * Get cached capability
   */
  getCapability(name) {
    const cap = this.capabilities.get(name);
    
    if (!cap) {
      return null;
    }
    
    // Check expiration
    if (cap.expires && Date.now() > cap.expires) {
      this.capabilities.delete(name);
      return null;
    }
    
    return cap.url;
  }
  
  /**
   * Check if capability exists
   */
  hasCapability(name) {
    return this.getCapability(name) !== null;
  }
  
  /**
   * Feature 10: Seed capability parsing
   * Parse and cache capabilities from seed response
   */
  async fetchFromSeed(seedUrl, requestedCaps = []) {
    this.seedUrl = seedUrl;
    
    try {
      const response = await this.retryFetch(seedUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/llsd+xml'
        },
        body: this.createSeedRequest(requestedCaps)
      });
      
      if (!response.ok) {
        throw new Error(`Seed capability request failed: ${response.status}`);
      }
      
      const xmlText = await response.text();
      const caps = LLSD.parseXML(xmlText);
      this.parseSeedResponse(caps);
      
      return caps;
    } catch (error) {
      console.error('Failed to fetch capabilities from seed:', error);
      throw error;
    }
  }
  
  /**
   * Parse seed response and cache capabilities
   */
  parseSeedResponse(caps) {
    if (!caps || typeof caps !== 'object') {
      return;
    }
    
    // Check if response has 'capabilities' key (standard seed response)
    const capsMap = caps.capabilities || caps;

    for (const [name, url] of Object.entries(capsMap)) {
      if (typeof url === 'string') {
        this.setCapability(name, url);
      }
    }
    
    console.log(`Cached ${this.capabilities.size} capabilities`);
  }
  
  /**
   * Create seed request LLSD
   */
  createSeedRequest(requestedCaps) {
    const data = {
      capabilities: requestedCaps
    };
    return LLSD.buildXML(data);
  }
  
  /**
   * Feature 11: Capability URL resolution
   * Resolve capability URL with parameters
   */
  resolveCapability(name, params = {}) {
    const baseUrl = this.getCapability(name);
    
    if (!baseUrl) {
      return null;
    }
    
    // If no params, return base URL
    if (Object.keys(params).length === 0) {
      return baseUrl;
    }
    
    // Add query parameters
    const url = new URL(baseUrl);
    for (const [key, value] of Object.entries(params)) {
      url.searchParams.append(key, value);
    }
    
    return url.toString();
  }
  
  /**
   * Batch resolve multiple capabilities
   */
  resolveCapabilities(names) {
    const result = {};
    for (const name of names) {
      const url = this.getCapability(name);
      if (url) {
        result[name] = url;
      }
    }
    return result;
  }
  
  /**
   * Feature 12: Timeout and retry logic
   * Fetch with automatic retry
   */
  async retryFetch(url, options = {}, attempt = 0) {
    try {
      const controller = new AbortController();
      const timeout = setTimeout(() => controller.abort(), 30000);
      
      const response = await fetch(url, {
        ...options,
        signal: controller.signal
      });
      
      clearTimeout(timeout);
      return response;
      
    } catch (error) {
      if (attempt < this.retryAttempts) {
        console.log(`Retry attempt ${attempt + 1} for ${url}`);
        await this.delay(this.retryDelay * Math.pow(2, attempt));
        return this.retryFetch(url, options, attempt + 1);
      }
      throw error;
    }
  }
  
  /**
   * Make capability request with retry
   */
  async requestCapability(name, options = {}) {
    const url = this.getCapability(name);
    
    if (!url) {
      throw new Error(`Capability ${name} not found`);
    }
    
    return this.retryFetch(url, options);
  }
  
  /**
   * POST to capability with retry
   */
  async postToCapability(name, data) {
    return this.requestCapability(name, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    });
  }
  
  /**
   * Clear all cached capabilities
   */
  clear() {
    this.capabilities.clear();
    this.seedUrl = null;
  }
  
  /**
   * Get all capability names
   */
  getCapabilityNames() {
    return Array.from(this.capabilities.keys());
  }
  
  /**
   * Refresh capabilities from seed
   */
  async refresh() {
    if (!this.seedUrl) {
      throw new Error('No seed URL available');
    }
    
    return this.fetchFromSeed(this.seedUrl);
  }
  
  /**
   * Utility: delay function
   */
  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
  
  /**
   * Utility: XML escape
   */
  escapeXml(str) {
    return String(str).replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&apos;');
  }
  
  /**
   * Get statistics
   */
  getStats() {
    return {
      total: this.capabilities.size,
      expired: Array.from(this.capabilities.values())
        .filter(cap => cap.expires && Date.now() > cap.expires).length
    };
  }
}

// Export
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { CapabilitiesManager };
}
