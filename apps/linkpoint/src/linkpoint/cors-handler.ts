/**
 * Linkpoint PWA - CORS Handler
 * Manages CORS bypass for different environments
 */

export class CORSHandler {
  public environment: any;
  private corsProxies: any[];
  private customProxyUrl: string | null;
  private capabilityPermit: string | null = null;

  constructor() {
    this.environment = this.detectEnvironment();
    this.customProxyUrl = ((import.meta as any).env.VITE_SL_PROXY_URL || '').trim() || null;
    this.checkLocalProxy();
    this.corsProxies = this.buildProxyList();
  }

  private buildProxyList() {
    const proxies: { url: string; name: string; encode: boolean }[] = [];

    if (this.customProxyUrl) {
      proxies.unshift({
        url: this.customProxyUrl,
        name: 'Custom Proxy',
        encode: true
      });
    }

    const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
    if (isLocalhost) {
      proxies.unshift({
        url: '/api/proxy?url=',
        name: 'Local Server Proxy',
        encode: true
      });
    }

    return proxies;
  }

  /**
   * Check if local proxy is reachable
   */
  async checkLocalProxy() {
    // Unit tests do not run the Express development proxy.
    if ((import.meta as any).env.MODE === 'test') return;
    const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
    if (!isLocalhost) return;

    try {
      const response = await fetch('/api/health');
      if (response.ok) {
        const data = await response.json();
        console.log('[CORS] Local proxy is reachable:', data);
      } else {
        console.warn('[CORS] Local proxy health check failed:', response.status);
      }
    } catch (error) {
      console.error('[CORS] Local proxy is unreachable:', error);
    }
  }

  /**
   * Detect current environment
   */
  detectEnvironment() {
    // Capacitor mobile app
    if ((window as any).Capacitor && (window as any).Capacitor.Plugins.CapacitorHttp) {
      return {
        type: 'capacitor',
        name: 'Capacitor Mobile App',
        corsSupport: 'native',
        needsProxy: false
      };
    }

    // Check if installed as PWA
    const isInstalled = window.matchMedia('(display-mode: standalone)').matches ||
                        (window.navigator as any).standalone ||
                        document.referrer.includes('android-app://');

    if (isInstalled) {
      return {
        type: 'pwa-installed',
        name: 'Installed Progressive Web App',
        corsSupport: 'server-or-custom-proxy',
        needsProxy: true
      };
    }

    // Regular web browser
      return {
        type: 'browser',
        name: 'Web Browser',
        corsSupport: 'server-or-custom-proxy',
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
  async makeRequest(url: string, options: any = {}) {
    const env = this.environment;

    try {
      // 1. Capacitor native HTTP (mobile)
      if (env.type === 'capacitor') {
        console.log('[CORS] Using Capacitor native HTTP');
        const { CapacitorHttp } = (window as any).Capacitor.Plugins;

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

      // 2. Browser/PWA - Try direct first (might work for some endpoints)
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
          this.capturePermit(response);
          return response;
        } catch (directError) {
          // CORS blocked, use proxy
          console.log('[CORS] Direct connection failed, using configured CORS proxies');
          return await this.useTrustedProxy(url, options);
        }
      }

    } catch (error: any) {
      console.error('[CORS] Request failed:', error);
      throw this.enhanceError(error);
    }
    return null;
  }

  /**
   * Use a Linkpoint-operated proxy configured for this deployment.
   */
  async useTrustedProxy(url: string, options: any) {
    const errors: string[] = [];

    // Never send login credentials or capability traffic through public proxy
    // services.  A configured proxy must be operated by the viewer provider.
    for (const proxy of this.corsProxies) {
      const proxiedUrl = proxy.encode ?
        proxy.url + encodeURIComponent(url) :
        proxy.url + url;

      try {
        console.log(`[CORS] Trying ${proxy.name} proxy: ${proxiedUrl}`);

        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 30000); // Increased to 30s

        const response = await fetch(proxiedUrl, {
          method: options.method || 'GET',
          headers: {
            ...(options.headers || {}),
            ...(this.capabilityPermit ? { 'X-Linkpoint-Capability-Permit': this.capabilityPermit } : {}),
          },
          body: options.body,
          signal: controller.signal
        });

        clearTimeout(timeoutId);

        if (response.ok) {
          this.capturePermit(response);
          console.log(`[CORS] ${proxy.name} proxy succeeded`);
          return response;
        } else {
          const errorText = await response.text().catch(() => 'No error body');
          errors.push(`${proxy.name}: HTTP ${response.status} - ${errorText.substring(0, 100)}`);
          console.warn(`[CORS] ${proxy.name} returned ${response.status}:`, errorText);
        }
      } catch (error: any) {
        errors.push(`${proxy.name}: ${error.message}`);
        console.warn(`[CORS] ${proxy.name} failed:`, error.message);
      }

    }

    // All proxies failed
    const errorMessage = this.corsProxies.length === 0
      ? 'No trusted Second Life proxy is configured.'
      : `All trusted Second Life proxies failed:\n${errors.join('\n')}`;
    console.error('[CORS]', errorMessage);

    throw new Error(errorMessage + '\n\n💡 Provide VITE_SL_PROXY_URL for a dedicated Linkpoint proxy.');
  }

  private capturePermit(response: Response) {
    const permit = response.headers.get('x-linkpoint-capability-permit');
    if (permit) this.capabilityPermit = permit;
  }

  /**
   * Enhance error with helpful message
   */
  enhanceError(error: any) {
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
        helpMessage += '3. **Set VITE_SL_PROXY_URL** - Dedicated proxy for stable SL access\n';
        helpMessage += '4. **Download desktop app** - ZERO CORS issues, no proxy needed\n';
        helpMessage += '5. **Wait and retry** - Proxy may be temporarily unavailable\n\n';
        helpMessage += '📱 Best Solution: Use Electron/Tauri desktop app for direct connections\n';
      }

      error.helpMessage = helpMessage;
      error.isNetworkError = true;
    }

    return error;
  }

  /**
   * Get recommended solution based on environment
   */
  getRecommendedSolution() {
    const env = this.environment;

    const solutions: Record<string, any> = {
      browser: {
        primary: 'Configure dedicated proxy',
        alternatives: ['Install as PWA', 'Download Desktop App', 'Use Mobile App'],
        instructions: 'Set VITE_SL_PROXY_URL for reliable Second Life access, then deploy.'
      },
      'pwa-installed': {
        primary: 'Using CORS proxy',
        alternatives: ['Upgrade to Desktop App'],
        instructions: 'If available, configure VITE_SL_PROXY_URL for better reliability than public proxies.'
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

export const corsHandler = new CORSHandler();
