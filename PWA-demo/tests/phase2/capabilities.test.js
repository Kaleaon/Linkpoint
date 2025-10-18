/**
 * Unit tests for Capabilities module
 * Tests capability caching, resolution, and retry logic
 */

// Import the module
const { CapabilitiesManager } = require('../../js/phase2/capabilities.js');

describe('CapabilitiesManager', () => {
  let caps;

  beforeEach(() => {
    caps = new CapabilitiesManager();
  });

  describe('Constructor', () => {
    test('should initialize with default options', () => {
      expect(caps.capabilities.size).toBe(0);
      expect(caps.seedUrl).toBeNull();
      expect(caps.cacheTimeout).toBe(3600000);
      expect(caps.retryAttempts).toBe(3);
      expect(caps.retryDelay).toBe(1000);
    });

    test('should accept custom options', () => {
      const customCaps = new CapabilitiesManager({
        cacheTimeout: 7200000,
        retryAttempts: 5,
        retryDelay: 2000
      });
      
      expect(customCaps.cacheTimeout).toBe(7200000);
      expect(customCaps.retryAttempts).toBe(5);
      expect(customCaps.retryDelay).toBe(2000);
    });
  });

  describe('setCapability', () => {
    test('should throw error if name is invalid', () => {
      expect(() => caps.setCapability()).toThrow('Valid capability name required');
      expect(() => caps.setCapability(123, 'url')).toThrow('Valid capability name required');
    });

    test('should throw error if URL is invalid', () => {
      expect(() => caps.setCapability('EventQueue')).toThrow('Valid capability URL required');
      expect(() => caps.setCapability('EventQueue', 123)).toThrow('Valid capability URL required');
    });

    test('should cache capability with default expiration', () => {
      const name = 'EventQueueGet';
      const url = 'https://sim.example.com/cap/eventqueue';
      
      caps.setCapability(name, url);
      
      const cached = caps.capabilities.get(name);
      expect(cached).toBeDefined();
      expect(cached.url).toBe(url);
      expect(cached.timestamp).toBeDefined();
      expect(cached.expires).toBeGreaterThan(Date.now());
    });

    test('should cache capability with custom expiration', () => {
      const name = 'EventQueueGet';
      const url = 'https://sim.example.com/cap/eventqueue';
      const expires = Date.now() + 10000;
      
      caps.setCapability(name, url, expires);
      
      const cached = caps.capabilities.get(name);
      expect(cached.expires).toBe(expires);
    });
  });

  describe('resolveCapability', () => {
    test('should return null for invalid input', () => {
      expect(caps.resolveCapability()).toBeNull();
      expect(caps.resolveCapability(123)).toBeNull();
    });

    test('should return null for non-existent capability', () => {
      expect(caps.resolveCapability('NonExistent')).toBeNull();
    });

    test('should return URL for valid capability', () => {
      const name = 'EventQueueGet';
      const url = 'https://sim.example.com/cap/eventqueue';
      
      caps.setCapability(name, url);
      
      expect(caps.resolveCapability(name)).toBe(url);
    });

    test('should return null for expired capability', () => {
      const name = 'EventQueueGet';
      const url = 'https://sim.example.com/cap/eventqueue';
      const expires = Date.now() - 1000; // Already expired
      
      caps.setCapability(name, url, expires);
      
      expect(caps.resolveCapability(name)).toBeNull();
      expect(caps.capabilities.has(name)).toBe(false);
    });
  });

  describe('parseSeedCapability', () => {
    test('should throw error if seed data is invalid', async () => {
      await expect(caps.parseSeedCapability()).rejects.toThrow('Valid seed data required');
      await expect(caps.parseSeedCapability('invalid')).rejects.toThrow('Valid seed data required');
    });

    test('should parse and cache capabilities from seed', async () => {
      const seedData = {
        EventQueueGet: 'https://sim.example.com/cap/eventqueue',
        ChatSessionRequest: 'https://sim.example.com/cap/chat',
        ViewerStats: 'https://sim.example.com/cap/stats'
      };
      
      await caps.parseSeedCapability(seedData);
      
      expect(caps.capabilities.size).toBe(3);
      expect(caps.resolveCapability('EventQueueGet')).toBe(seedData.EventQueueGet);
      expect(caps.resolveCapability('ChatSessionRequest')).toBe(seedData.ChatSessionRequest);
      expect(caps.resolveCapability('ViewerStats')).toBe(seedData.ViewerStats);
    });

    test('should store seed URL if provided', async () => {
      const seedData = {
        seed_url: 'https://sim.example.com/seed',
        EventQueueGet: 'https://sim.example.com/cap/eventqueue'
      };
      
      await caps.parseSeedCapability(seedData);
      
      expect(caps.seedUrl).toBe(seedData.seed_url);
      expect(caps.capabilities.has('seed_url')).toBe(false);
    });

    test('should skip non-string values', async () => {
      const seedData = {
        ValidCap: 'https://example.com/cap',
        InvalidCap1: 123,
        InvalidCap2: null,
        InvalidCap3: {}
      };
      
      await caps.parseSeedCapability(seedData);
      
      expect(caps.capabilities.size).toBe(1);
      expect(caps.hasCapability('ValidCap')).toBe(true);
      expect(caps.hasCapability('InvalidCap1')).toBe(false);
    });
  });

  describe('hasCapability', () => {
    test('should return false for non-existent capability', () => {
      expect(caps.hasCapability('NonExistent')).toBe(false);
    });

    test('should return true for valid capability', () => {
      caps.setCapability('TestCap', 'https://example.com/cap');
      expect(caps.hasCapability('TestCap')).toBe(true);
    });

    test('should return false for expired capability', () => {
      const expires = Date.now() - 1000;
      caps.setCapability('TestCap', 'https://example.com/cap', expires);
      expect(caps.hasCapability('TestCap')).toBe(false);
    });
  });

  describe('clearCache', () => {
    test('should clear all cached capabilities', () => {
      caps.setCapability('Cap1', 'https://example.com/cap1');
      caps.setCapability('Cap2', 'https://example.com/cap2');
      
      expect(caps.capabilities.size).toBe(2);
      
      caps.clearCache();
      
      expect(caps.capabilities.size).toBe(0);
    });
  });

  describe('getStats', () => {
    test('should return correct statistics', () => {
      caps.seedUrl = 'https://example.com/seed';
      caps.setCapability('Cap1', 'https://example.com/cap1');
      caps.setCapability('Cap2', 'https://example.com/cap2');
      
      const stats = caps.getStats();
      
      expect(stats.totalCapabilities).toBe(2);
      expect(stats.seedUrl).toBe('https://example.com/seed');
      expect(stats.cacheTimeout).toBe(3600000);
    });
  });

  describe('delay', () => {
    test('should delay for specified time', async () => {
      const start = Date.now();
      await caps.delay(100);
      const elapsed = Date.now() - start;
      
      expect(elapsed).toBeGreaterThanOrEqual(100);
      expect(elapsed).toBeLessThan(200);
    });
  });

  describe('fetchWithRetry', () => {
    test('should throw error if URL is invalid', async () => {
      await expect(caps.fetchWithRetry()).rejects.toThrow('Valid URL required');
    });

    // Note: Full fetchWithRetry testing would require mocking fetch
    // This is a minimal test to verify the structure
    test('should accept valid URL', async () => {
      // Mock fetch for this test
      global.fetch = jest.fn().mockResolvedValue({ ok: true });
      
      const response = await caps.fetchWithRetry('https://example.com/test');
      
      expect(response.ok).toBe(true);
      expect(global.fetch).toHaveBeenCalledWith('https://example.com/test');
      
      // Clean up
      delete global.fetch;
    });
  });
});
