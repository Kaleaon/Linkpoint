/**
 * Linkpoint PWA - CORS Handler
 * Manages CORS bypass for different environments
 */

class CORSHandler {
  constructor() {
    this.environment = this.detectEnvironment();
    // Updated CORS proxies - corsproxy.io has restrictions now
    this.corsProxies = [
      { 
        url: 'https://api.allorigins.win/raw?url=', 
        name: 'AllOrigins',
        encode: true
      },
      { 
        url: 'https://api.codetabs.com/v1/proxy?quest=', 
        name: 'CodeTabs',
        encode: true
      },
      {
        url: 'https://thingproxy.freeboard.io/fetch/',
        name: 'ThingProxy',
        encode: false
      }
    ];
    this.currentProxyIndex = 0;
  }

  /**
   * Detect current environment
   */
  detectEnvironment() {
    // Capacitor mobile app
    if (window.Capacitor && window.Capacitor.Plugins.CapacitorHttp) {
      return {
        type: 'capacitor',
        name: 'Capacitor Mobile App',
        corsSupport: 'native',
        needsProxy: false
      };
    }
    
    // Electron desktop app
    if (window.ELECTRON_PROXY_URL) {
      return {
        type: 'electron',
        name: 'Electron Desktop App',
        corsSupport: 'proxy',
        proxyUrl: window.ELECTRON_PROXY_URL,
        needsProxy: false
      };
    }
    
    // Tauri desktop app
    if (window.TAURI_PROXY_URL || window.__TAURI__) {
      return {
        type: 'tauri',
        name: 'Tauri Desktop App',
        corsSupport: 'proxy',
        proxyUrl: window.TAURI_PROXY_URL,
        needsProxy: false
      };
    }
    
    // Check if installed as PWA
    const isInstalled = window.matchMedia('(display-mode: standalone)').matches || 
                        window.navigator.standalone || 
                        document.referrer.includes('android-app://');
    
    if (isInstalled) {
      return {
        type: 'pwa-installed',
        name: 'Installed Progressive Web App',
        corsSupport: 'public-proxy',
        needsProxy: true
      };
    }
    
    // Regular web browser
    return {
      type: 'browser',
      name: 'Web Browser',
      corsSupport: 'public-proxy',
      needsProxy: true
    };
  }

  /**
   * Get environment info for display
   */
  getEnvironmentInfo() {
    return this.environment;
  }

  /**
   * Make CORS-safe request
   */
  async makeRequest(url, options = {}) {
    const env = this.environment;
    
    try {
      // 1. Capacitor native HTTP (mobile)
      if (env.type === 'capacitor') {
        console.log('[CORS] Using Capacitor native HTTP');
        const { CapacitorHttp } = window.Capacitor.Plugins;
        
        const response = await CapacitorHttp.request({
          url: url,
          method: options.method || 'GET',
          headers: options.headers || {},
          data: options.body
        });
        
        return {
          ok: response.status >= 200 && response.status < 300,
          status: response.status,
          statusText: response.statusText || '',
          data: response.data,
          text: async () => typeof response.data === 'string' ? response.data : JSON.stringify(response.data),
          json: async () => typeof response.data === 'object' ? response.data : JSON.parse(response.data)
        };
      }
      
      // 2. Electron/Tauri proxy
      if (env.type === 'electron' || env.type === 'tauri') {
        console.log(`[CORS] Using ${env.name} proxy`);
        const proxyEndpoint = `${env.proxyUrl}/sl-login`;
        const response = await fetch(proxyEndpoint, {
          method: options.method || 'POST',
          headers: options.headers || {},
          body: options.body
        });
        return response;
      }
      
      // 3. Browser/PWA - Try direct first (might work for some endpoints)
      if (env.type === 'pwa-installed' || env.type === 'browser') {
        // Try direct connection first
        try {
          const response = await fetch(url, {
            method: options.method || 'GET',
            headers: options.headers || {},
            body: options.body,
            mode: 'cors'
          });
          
          console.log('[CORS] Direct connection succeeded');
          return response;
        } catch (directError) {
          // CORS blocked, use proxy
          console.log('[CORS] Direct connection failed, using public CORS proxy');
          return await this.usePublicProxy(url, options);
        }
      }
      
    } catch (error) {
      console.error('[CORS] Request failed:', error);
      throw this.enhanceError(error);
    }
  }

  /**
   * Use public CORS proxy
   */
  async usePublicProxy(url, options) {
    const errors = [];
    
    // Try each proxy in sequence
    for (let i = 0; i < this.corsProxies.length; i++) {
      const proxy = this.corsProxies[this.currentProxyIndex];
      const proxiedUrl = proxy.encode ? 
        proxy.url + encodeURIComponent(url) : 
        proxy.url + url;
      
      try {
        console.log(`[CORS] Trying ${proxy.name} proxy...`);
        
        const response = await fetch(proxiedUrl, {
          method: options.method || 'GET',
          headers: options.headers || {},
          body: options.body,
          signal: AbortSignal.timeout(10000) // 10 second timeout
        });
        
        if (response.ok) {
          console.log(`[CORS] ${proxy.name} proxy succeeded`);
          return response;
        } else {
          errors.push(`${proxy.name}: HTTP ${response.status}`);
          console.warn(`[CORS] ${proxy.name} returned ${response.status}`);
        }
      } catch (error) {
        errors.push(`${proxy.name}: ${error.message}`);
        console.warn(`[CORS] ${proxy.name} failed:`, error.message);
      }
      
      // Try next proxy
      this.currentProxyIndex = (this.currentProxyIndex + 1) % this.corsProxies.length;
    }
    
    // All proxies failed
    const errorMessage = `All CORS proxies failed:\n${errors.join('\n')}`;
    console.error('[CORS]', errorMessage);
    
    throw new Error(errorMessage + '\n\n💡 Solutions:\n1. Try without VPN\n2. Try different network\n3. Use desktop app (no CORS issues)');
  }

  /**
   * Enhance error with helpful message
   */
  enhanceError(error) {
    if (error.message.includes('CORS') || error.message.includes('Failed to fetch') || error.message.includes('502') || error.message.includes('403')) {
      const env = this.environment;
      let helpMessage = '🚫 Connection Error\n\n';
      
      if (error.message.includes('502') || error.message.includes('Bad Gateway')) {
        helpMessage += '⚠️ HTTP 502 Error (Bad Gateway)\n\n';
        helpMessage += 'This often happens when:\n';
        helpMessage += '• Using a VPN (try disabling it)\n';
        helpMessage += '• Network filtering/firewall\n';
        helpMessage += '• CORS proxy is temporarily down\n';
        helpMessage += '• ISP is blocking the connection\n\n';
      } else if (error.message.includes('403') || error.message.includes('Forbidden')) {
        helpMessage += '⚠️ HTTP 403 Error (Forbidden)\n\n';
        helpMessage += 'This happens when:\n';
        helpMessage += '• CORS proxy has rate limits\n';
        helpMessage += '• VPN/proxy is detected and blocked\n';
        helpMessage += '• IP address is temporarily banned\n\n';
      }
      
      if (env.type === 'browser' || env.type === 'pwa-installed') {
        helpMessage += '💡 Solutions:\n';
        helpMessage += '1. **Try without VPN** - Most VPNs cause CORS proxy issues\n';
        helpMessage += '2. **Use different network** - Try mobile hotspot or different WiFi\n';
        helpMessage += '3. **Download desktop app** - ZERO CORS issues, no proxy needed\n';
        helpMessage += '4. **Wait and retry** - Proxy may be temporarily unavailable\n\n';
        helpMessage += '📱 Best Solution: Use Electron/Tauri desktop app for direct connections\n';
      } else if (env.type === 'pwa-installed') {
        helpMessage += '💡 The PWA is using public CORS proxies which may be blocked by your VPN.\n';
        helpMessage += 'For best experience with VPN, use the desktop app.';
      }
      
      error.helpMessage = helpMessage;
      error.isNetworkError = true;
    }
    
    return error;
  }

  /**
   * Check CORS status for a URL
   */
  async checkCORSStatus(url) {
    try {
      const testUrl = new URL(url).origin;
      const response = await fetch(testUrl, {
        method: 'HEAD',
        mode: 'cors'
      });
      
      return {
        supported: true,
        url: testUrl,
        message: 'Direct CORS access is supported'
      };
    } catch (error) {
      return {
        supported: false,
        url: url,
        message: 'CORS is blocked - using proxy',
        error: error.message
      };
    }
  }

  /**
   * Get recommended solution based on environment
   */
  getRecommendedSolution() {
    const env = this.environment;
    
    const solutions = {
      browser: {
        primary: 'Install as PWA',
        alternatives: ['Download Desktop App', 'Use Mobile App'],
        instructions: 'Click the install button in your browser or download a native app for best performance.'
      },
      'pwa-installed': {
        primary: 'Currently using CORS proxy',
        alternatives: ['Upgrade to Desktop App'],
        instructions: 'PWA is working with a public CORS proxy. For better reliability, consider the desktop app.'
      },
      electron: {
        primary: 'Using local proxy (optimal)',
        alternatives: [],
        instructions: 'You are using the optimal setup with local CORS bypass.'
      },
      tauri: {
        primary: 'Using local proxy (optimal)',
        alternatives: [],
        instructions: 'You are using the optimal setup with local CORS bypass.'
      },
      capacitor: {
        primary: 'Using native HTTP (optimal)',
        alternatives: [],
        instructions: 'You are using the optimal setup with native mobile HTTP.'
      }
    };
    
    return solutions[env.type] || solutions.browser;
  }

  /**
   * Show CORS status in UI
   */
  displayStatus() {
    const env = this.environment;
    const solution = this.getRecommendedSolution();
    
    console.log('═══════════════════════════════════════════════');
    console.log('🌐 CORS Handler Status');
    console.log('═══════════════════════════════════════════════');
    console.log(`Environment: ${env.name}`);
    console.log(`Type: ${env.type}`);
    console.log(`CORS Support: ${env.corsSupport}`);
    console.log(`Needs Proxy: ${env.needsProxy ? 'Yes' : 'No'}`);
    console.log(`\nRecommendation: ${solution.primary}`);
    console.log(`Instructions: ${solution.instructions}`);
    console.log('═══════════════════════════════════════════════');
    
    return {
      environment: env,
      solution: solution
    };
  }
}

// Create global instance
window.corsHandler = new CORSHandler();

// Display status on load
if (typeof console !== 'undefined') {
  window.corsHandler.displayStatus();
}

// Make available globally
window.CORSHandler = CORSHandler;
