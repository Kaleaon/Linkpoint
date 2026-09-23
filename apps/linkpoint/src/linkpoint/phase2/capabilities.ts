/**
 * Linkpoint PWA - Capabilities Manager (Features 9-12)
 *
 * Phase 2: Core Protocol Extensions - Priority 1
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 28-33)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/
 *
 * Manages Second Life capability URLs with caching and retry logic.
 */

export class CapabilitiesManager {
  private capabilities: Map<string, any> = new Map();
  public seedUrl: string | null = null;
  private cacheTimeout: number = 3600000; // 1 hour default
  private retryAttempts: number = 3;
  private retryDelay: number = 1000;

  constructor(options: any = {}) {
    this.cacheTimeout = options.cacheTimeout || 3600000;
    this.retryAttempts = options.retryAttempts || 3;
    this.retryDelay = options.retryDelay || 1000;
  }

  /**
   * Feature 9: Capability caching
   * Cache a capability URL with expiration
   */
  setCapability(name: string, url: string, expires: number | null = null) {
    if (!name || typeof name !== 'string') throw new Error('Valid capability name required');
    if (!url || typeof url !== 'string') throw new Error('Valid capability URL required');

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
   */
  resolveCapability(name: string): string | null {
    const cap = this.capabilities.get(name);
    if (!cap) return null;
    if (cap.expires && Date.now() > cap.expires) {
      this.capabilities.delete(name);
      return null;
    }
    return cap.url;
  }

  /**
   * Feature 10: Seed capability parsing
   * Parse seed capability response and cache all capabilities
   */
  async parseSeedCapability(seedData: any) {
    if (!seedData || typeof seedData !== 'object') throw new Error('Valid seed data required');

    if (seedData.seed_url) this.seedUrl = seedData.seed_url;
    for (const [name, url] of Object.entries(seedData)) {
      if (name !== 'seed_url' && typeof url === 'string') {
        this.setCapability(name, url);
      }
    }
    console.log(`[Capabilities] Parsed ${this.capabilities.size} capabilities`);
  }

  /**
   * Feature 12: Timeout and retry logic
   * Fetch capability with retry support
   */
  async fetchWithRetry(url: string, attempt: number = 1): Promise<Response> {
    try {
      const response = await fetch(url);
      if (!response.ok && attempt < this.retryAttempts) {
        await new Promise(r => setTimeout(r, this.retryDelay * attempt));
        return this.fetchWithRetry(url, attempt + 1);
      }
      return response;
    } catch (error) {
      if (attempt < this.retryAttempts) {
        await new Promise(r => setTimeout(r, this.retryDelay * attempt));
        return this.fetchWithRetry(url, attempt + 1);
      }
      throw error;
    }
  }

  hasCapability(name: string): boolean {
    return this.resolveCapability(name) !== null;
  }

  clearCache() {
    this.capabilities.clear();
  }
}
