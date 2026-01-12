/**
 * Linkpoint PWA - XML-RPC Client for Second Life Login
 * Based on Linkpoint Android implementation
 */

class XMLRPCClient {
  /**
   * Build XML-RPC login request
   */
  static buildLoginRequest(params) {
    // String fields (non-boolean)
    const stringFields = [];
    
    // Add all login parameters as strings
    stringFields.push({ name: 'first', value: params.firstName });
    stringFields.push({ name: 'last', value: params.lastName });
    stringFields.push({ name: 'passwd', value: `$1$${params.passwordHash}` });
    stringFields.push({ name: 'start', value: params.startLocation || 'last' });
    stringFields.push({ name: 'channel', value: params.channel || 'Linkpoint PWA' });
    stringFields.push({ name: 'version', value: params.version || '1.0.0' });
    stringFields.push({ name: 'platform', value: 'Web' });
    stringFields.push({ name: 'platform_version', value: navigator.userAgent });
    stringFields.push({ name: 'mac', value: params.macAddress || this.generateMAC() });
    stringFields.push({ name: 'id0', value: params.id0 || this.generateID0() });
    stringFields.push({ name: 'viewer_digest', value: params.viewerDigest || this.generateViewerDigest() });
    
    // Add options array
    const options = [
      'inventory-root',
      'inventory-skeleton',
      'inventory-lib-root',
      'inventory-lib-owner',
      'inventory-skel-lib',
      'initial-outfit',
      'gestures',
      'display_names',
      'event_categories',
      'event_notifications',
      'classified_categories',
      'adult_compliant',
      'buddy-list',
      'newuser-config',
      'ui-config',
      'advanced-mode',
      'max-agent-groups',
      'map-server-url',
      'voice-config',
      'tutorial_setting',
      'login-flags',
      'global-textures'
    ];
    
    // Build XML
    let xml = '<?xml version="1.0"?>\n';
    xml += '<methodCall>\n';
    xml += '<methodName>login_to_simulator</methodName>\n';
    xml += '<params>\n';
    xml += '<param>\n';
    xml += '<value><struct>\n';
    
    // Add all string fields
    for (const field of stringFields) {
      xml += '<member>\n';
      xml += `<name>${this.escapeXml(field.name)}</name>\n`;
      xml += `<value><string>${this.escapeXml(field.value)}</string></value>\n`;
      xml += '</member>\n';
    }
    
    // Add boolean fields - MUST use <boolean>1</boolean> format, NOT <string>true</string>
    // Second Life's login server requires XML-RPC boolean format per spec
    xml += '<member>\n';
    xml += '<name>agree_to_tos</name>\n';
    xml += '<value><boolean>1</boolean></value>\n';
    xml += '</member>\n';
    
    xml += '<member>\n';
    xml += '<name>read_critical</name>\n';
    xml += '<value><boolean>1</boolean></value>\n';
    xml += '</member>\n';
    
    // Add options array
    xml += '<member>\n';
    xml += '<name>options</name>\n';
    xml += '<value><array><data>\n';
    for (const option of options) {
      xml += `<value><string>${option}</string></value>\n`;
    }
    xml += '</data></array></value>\n';
    xml += '</member>\n';
    
    xml += '</struct></value>\n';
    xml += '</param>\n';
    xml += '</params>\n';
    xml += '</methodCall>\n';
    
    return xml;
  }

  /**
   * Send XML-RPC request
   * Uses CORSHandler for automatic environment detection and CORS bypass
   */
  static async sendRequest(url, xmlRequest) {
    try {
      // Use CORS handler if available
      if (window.corsHandler) {
        console.log('[SL] Using CORSHandler for request');
        const response = await window.corsHandler.makeRequest(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'text/xml',
            'Accept': 'text/xml, application/xml'
          },
          body: xmlRequest
        });
        
        if (!response.ok) {
          const errorText = await response.text();
          console.error('[XMLRPCClient] HTTP error response:', errorText.substring(0, 500));
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const responseText = await response.text();
        
        // Check if response looks like XML
        if (!responseText.trim().startsWith('<?xml') && !responseText.trim().startsWith('<methodResponse')) {
          console.error('[XMLRPCClient] Response is not XML:', responseText.substring(0, 500));
          throw new Error('Server returned an invalid response. This may be a CORS issue. Try using the app as an installed PWA or desktop app for better connectivity.');
        }
        
        return this.parseLoginResponse(responseText);
      }
      
      // Fallback to original implementation
      console.log('[SL] CORSHandler not available, using fallback');
      return await this.sendRequestFallback(url, xmlRequest);
      
    } catch (error) {
      console.error('XML-RPC request failed:', error);
      
      // Show helpful error message
      if (error.helpMessage) {
        const userMessage = error.helpMessage;
        console.error(userMessage);
        
        // Show in UI if Utils is available
        if (window.Utils && window.Utils.showToast) {
          Utils.showToast('Connection failed. Check console for details.', 'error');
        }
      }
      
      throw error;
    }
  }
  
  /**
   * Fallback implementation (original code)
   */
  static async sendRequestFallback(url, xmlRequest) {
    try {
      // Detect environment and use appropriate method
      
      // 1. Capacitor (Mobile) - Use native HTTP
      if (window.Capacitor && window.Capacitor.Plugins.CapacitorHttp) {
        console.log('[SL] Using Capacitor native HTTP for login');
        const { CapacitorHttp } = window.Capacitor.Plugins;
        const response = await CapacitorHttp.post({
          url: url,
          headers: {
            'Content-Type': 'text/xml',
            'Accept': 'text/xml, application/xml'
          },
          data: xmlRequest
        });
        return this.parseLoginResponse(response.data);
      }
      
      // 2. Electron - Use local proxy
      if (window.ELECTRON_PROXY_URL) {
        console.log('[SL] Using Electron proxy for login');
        const targetUrl = `${window.ELECTRON_PROXY_URL}/sl-login`;
        const response = await fetch(targetUrl, {
          method: 'POST',
          headers: {
            'Content-Type': 'text/xml',
            'Accept': 'text/xml, application/xml'
          },
          body: xmlRequest
        });
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        const responseText = await response.text();
        return this.parseLoginResponse(responseText);
      }
      
      // 3. Tauri - Use local proxy
      if (window.TAURI_PROXY_URL) {
        console.log('[SL] Using Tauri proxy for login');
        const targetUrl = `${window.TAURI_PROXY_URL}/sl-login`;
        const response = await fetch(targetUrl, {
          method: 'POST',
          headers: {
            'Content-Type': 'text/xml',
            'Accept': 'text/xml, application/xml'
          },
          body: xmlRequest
        });
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        const responseText = await response.text();
        return this.parseLoginResponse(responseText);
      }
      
      // 4. Browser - Check if installed as PWA or web
      const isInstalled = window.matchMedia('(display-mode: standalone)').matches || 
                          window.navigator.standalone || 
                          document.referrer.includes('android-app://');
      
      if (isInstalled) {
        // Installed PWA - Try direct connection (may work on some platforms)
        console.log('[SL] Using direct fetch (installed PWA)');
        try {
          const response = await fetch(url, {
            method: 'POST',
            headers: {
              'Content-Type': 'text/xml',
              'Accept': 'text/xml, application/xml'
            },
            body: xmlRequest
          });

          if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
          }

          const responseText = await response.text();
          return this.parseLoginResponse(responseText);
        } catch (directError) {
          // If direct connection fails in installed PWA, fall through to CORS proxy
          console.warn('[SL] Direct connection failed in installed PWA, trying CORS proxy:', directError.message);
        }
      }
      
      // 5. Web Browser or fallback - Try multiple CORS proxies
      console.log('[SL] Using CORS proxy for web browser');
      
      // List of CORS proxies to try (in order of preference)
      // Updated to match cors-handler.js - corsproxy.io has restrictions now
      const corsProxies = [
        { name: 'AllOrigins', url: 'https://api.allorigins.win/raw?url=', encode: true },
        { name: 'CodeTabs', url: 'https://api.codetabs.com/v1/proxy?quest=', encode: true },
        { name: 'ThingProxy', url: 'https://thingproxy.freeboard.io/fetch/', encode: false }
      ];
      
      let lastError = null;
      
      // Try each proxy until one works
      for (const proxy of corsProxies) {
        try {
          console.log(`[XMLRPCClient] Trying ${proxy.name}...`);
          const proxiedUrl = proxy.encode ? 
            proxy.url + encodeURIComponent(url) : 
            proxy.url + url;
          
          const response = await fetch(proxiedUrl, {
            method: 'POST',
            headers: {
              'Content-Type': 'text/xml',
              'Accept': 'text/xml, application/xml'
            },
            body: xmlRequest
          });

          if (!response.ok) {
            const errorText = await response.text();
            console.warn(`[XMLRPCClient] ${proxy.name} failed with HTTP ${response.status}:`, errorText.substring(0, 200));
            lastError = new Error(`${proxy.name}: HTTP ${response.status}`);
            continue; // Try next proxy
          }

          const responseText = await response.text();
          
          // Check if response looks like XML
          if (!responseText.trim().startsWith('<?xml') && !responseText.trim().startsWith('<methodResponse')) {
            console.warn(`[XMLRPCClient] ${proxy.name} returned non-XML:`, responseText.substring(0, 200));
            lastError = new Error(`${proxy.name}: Invalid response format`);
            continue; // Try next proxy
          }
          
          console.log(`[XMLRPCClient] ✅ Success with ${proxy.name}`);
          return this.parseLoginResponse(responseText);
          
        } catch (proxyError) {
          console.warn(`[XMLRPCClient] ${proxy.name} failed:`, proxyError.message);
          lastError = proxyError;
          continue; // Try next proxy
        }
      }
      
      // If all proxies failed, throw a helpful error
      console.error('[XMLRPCClient] All CORS proxies failed. Last error:', lastError);
      throw new Error('Unable to connect to Second Life servers. All CORS proxies failed. Please try:\n1. Installing the app as a PWA (Add to Home Screen)\n2. Using a desktop app version\n3. Trying again later when the service is available');
      
    } catch (error) {
      console.error('XML-RPC request failed:', error);
      
      // Provide helpful error message based on context
      if (error.message && (error.message.includes('CORS') || error.message.includes('Failed to fetch'))) {
        throw new Error('Network error: Unable to connect to Second Life servers. Please check your internet connection or try again later.');
      }
      
      throw error;
    }
  }

  /**
   * Parse XML-RPC login response
   */
  static parseLoginResponse(xmlText) {
    // Log response for debugging
    console.log('[XMLRPCClient] Response length:', xmlText?.length || 0);
    console.log('[XMLRPCClient] Response preview:', xmlText?.substring(0, 200) || 'empty');
    
    const parser = new DOMParser();
    const doc = parser.parseFromString(xmlText, 'text/xml');
    
    // Check for parse errors
    const parseError = doc.querySelector('parsererror');
    if (parseError) {
      console.error('[XMLRPCClient] XML parse error. Response text:', xmlText);
      throw new Error('Invalid server response. The server may be temporarily unavailable or the CORS proxy is not working correctly. Please try again later.');
    }

    const result = {};
    
    // Navigate to the response struct
    const methodResponse = doc.querySelector('methodResponse');
    if (!methodResponse) {
      throw new Error('Invalid XML-RPC response');
    }

    // Check for fault
    const fault = methodResponse.querySelector('fault');
    if (fault) {
      const faultStruct = this.parseStruct(fault.querySelector('value struct'));
      throw new Error(`Login failed: ${faultStruct.faultString || 'Unknown error'}`);
    }

    // Parse params
    const params = methodResponse.querySelector('params param value struct');
    if (!params) {
      throw new Error('No params in response');
    }

    return this.parseStruct(params);
  }

  /**
   * Parse XML struct into object
   */
  static parseStruct(structElement) {
    const result = {};
    
    if (!structElement) return result;
    
    const members = structElement.querySelectorAll('member');
    members.forEach(member => {
      const nameEl = member.querySelector('name');
      const valueEl = member.querySelector('value');
      
      if (nameEl && valueEl) {
        const name = nameEl.textContent.trim();
        const value = this.parseValue(valueEl);
        result[name] = value;
      }
    });
    
    return result;
  }

  /**
   * Parse XML value
   */
  static parseValue(valueElement) {
    // Get the first child element
    const firstChild = Array.from(valueElement.children).find(el => el.nodeType === 1);
    
    if (!firstChild) {
      return valueElement.textContent.trim();
    }
    
    const tagName = firstChild.tagName.toLowerCase();
    const text = firstChild.textContent.trim();
    
    switch (tagName) {
      case 'string':
        return text;
      case 'int':
      case 'i4':
        return parseInt(text);
      case 'boolean':
        return text === '1' || text === 'true';
      case 'double':
        return parseFloat(text);
      case 'struct':
        return this.parseStruct(firstChild);
      case 'array':
        return this.parseArray(firstChild);
      default:
        return text;
    }
  }

  /**
   * Parse XML array
   */
  static parseArray(arrayElement) {
    const result = [];
    const data = arrayElement.querySelector('data');
    
    if (data) {
      const values = data.querySelectorAll(':scope > value');
      values.forEach(valueEl => {
        result.push(this.parseValue(valueEl));
      });
    }
    
    return result;
  }

  /**
   * Escape XML special characters
   */
  static escapeXml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&apos;');
  }

  /**
   * Generate MAC address
   */
  static generateMAC() {
    const hex = '0123456789ABCDEF';
    let mac = '';
    for (let i = 0; i < 6; i++) {
      if (i > 0) mac += ':';
      mac += hex[Math.floor(Math.random() * 16)];
      mac += hex[Math.floor(Math.random() * 16)];
    }
    return mac;
  }

  /**
   * Generate ID0
   */
  static generateID0() {
    return Utils.generateUUID();
  }

  /**
   * Generate viewer digest
   */
  static generateViewerDigest() {
    return Utils.generateUUID();
  }

  /**
   * Calculate MD5 hash (pure JavaScript implementation)
   * Note: crypto.subtle.digest does NOT support MD5, so we implement it ourselves
   */
  static async md5(str) {
    // MD5 implementation in JavaScript
    // Based on the RSA Data Security, Inc. MD5 Message-Digest Algorithm
    
    function md5cycle(x, k) {
      let a = x[0], b = x[1], c = x[2], d = x[3];
      
      a = ff(a, b, c, d, k[0], 7, -680876936);
      d = ff(d, a, b, c, k[1], 12, -389564586);
      c = ff(c, d, a, b, k[2], 17, 606105819);
      b = ff(b, c, d, a, k[3], 22, -1044525330);
      a = ff(a, b, c, d, k[4], 7, -176418897);
      d = ff(d, a, b, c, k[5], 12, 1200080426);
      c = ff(c, d, a, b, k[6], 17, -1473231341);
      b = ff(b, c, d, a, k[7], 22, -45705983);
      a = ff(a, b, c, d, k[8], 7, 1770035416);
      d = ff(d, a, b, c, k[9], 12, -1958414417);
      c = ff(c, d, a, b, k[10], 17, -42063);
      b = ff(b, c, d, a, k[11], 22, -1990404162);
      a = ff(a, b, c, d, k[12], 7, 1804603682);
      d = ff(d, a, b, c, k[13], 12, -40341101);
      c = ff(c, d, a, b, k[14], 17, -1502002290);
      b = ff(b, c, d, a, k[15], 22, 1236535329);
      
      a = gg(a, b, c, d, k[1], 5, -165796510);
      d = gg(d, a, b, c, k[6], 9, -1069501632);
      c = gg(c, d, a, b, k[11], 14, 643717713);
      b = gg(b, c, d, a, k[0], 20, -373897302);
      a = gg(a, b, c, d, k[5], 5, -701558691);
      d = gg(d, a, b, c, k[10], 9, 38016083);
      c = gg(c, d, a, b, k[15], 14, -660478335);
      b = gg(b, c, d, a, k[4], 20, -405537848);
      a = gg(a, b, c, d, k[9], 5, 568446438);
      d = gg(d, a, b, c, k[14], 9, -1019803690);
      c = gg(c, d, a, b, k[3], 14, -187363961);
      b = gg(b, c, d, a, k[8], 20, 1163531501);
      a = gg(a, b, c, d, k[13], 5, -1444681467);
      d = gg(d, a, b, c, k[2], 9, -51403784);
      c = gg(c, d, a, b, k[7], 14, 1735328473);
      b = gg(b, c, d, a, k[12], 20, -1926607734);
      
      a = hh(a, b, c, d, k[5], 4, -378558);
      d = hh(d, a, b, c, k[8], 11, -2022574463);
      c = hh(c, d, a, b, k[11], 16, 1839030562);
      b = hh(b, c, d, a, k[14], 23, -35309556);
      a = hh(a, b, c, d, k[1], 4, -1530992060);
      d = hh(d, a, b, c, k[4], 11, 1272893353);
      c = hh(c, d, a, b, k[7], 16, -155497632);
      b = hh(b, c, d, a, k[10], 23, -1094730640);
      a = hh(a, b, c, d, k[13], 4, 681279174);
      d = hh(d, a, b, c, k[0], 11, -358537222);
      c = hh(c, d, a, b, k[3], 16, -722521979);
      b = hh(b, c, d, a, k[6], 23, 76029189);
      a = hh(a, b, c, d, k[9], 4, -640364487);
      d = hh(d, a, b, c, k[12], 11, -421815835);
      c = hh(c, d, a, b, k[15], 16, 530742520);
      b = hh(b, c, d, a, k[2], 23, -995338651);
      
      a = ii(a, b, c, d, k[0], 6, -198630844);
      d = ii(d, a, b, c, k[7], 10, 1126891415);
      c = ii(c, d, a, b, k[14], 15, -1416354905);
      b = ii(b, c, d, a, k[5], 21, -57434055);
      a = ii(a, b, c, d, k[12], 6, 1700485571);
      d = ii(d, a, b, c, k[3], 10, -1894986606);
      c = ii(c, d, a, b, k[10], 15, -1051523);
      b = ii(b, c, d, a, k[1], 21, -2054922799);
      a = ii(a, b, c, d, k[8], 6, 1873313359);
      d = ii(d, a, b, c, k[15], 10, -30611744);
      c = ii(c, d, a, b, k[6], 15, -1560198380);
      b = ii(b, c, d, a, k[13], 21, 1309151649);
      a = ii(a, b, c, d, k[4], 6, -145523070);
      d = ii(d, a, b, c, k[11], 10, -1120210379);
      c = ii(c, d, a, b, k[2], 15, 718787259);
      b = ii(b, c, d, a, k[9], 21, -343485551);
      
      x[0] = add32(a, x[0]);
      x[1] = add32(b, x[1]);
      x[2] = add32(c, x[2]);
      x[3] = add32(d, x[3]);
    }
    
    function cmn(q, a, b, x, s, t) {
      a = add32(add32(a, q), add32(x, t));
      return add32((a << s) | (a >>> (32 - s)), b);
    }
    
    function ff(a, b, c, d, x, s, t) {
      return cmn((b & c) | ((~b) & d), a, b, x, s, t);
    }
    
    function gg(a, b, c, d, x, s, t) {
      return cmn((b & d) | (c & (~d)), a, b, x, s, t);
    }
    
    function hh(a, b, c, d, x, s, t) {
      return cmn(b ^ c ^ d, a, b, x, s, t);
    }
    
    function ii(a, b, c, d, x, s, t) {
      return cmn(c ^ (b | (~d)), a, b, x, s, t);
    }
    
    function add32(a, b) {
      return (a + b) & 0xFFFFFFFF;
    }
    
    function md5blk(s) {
      const md5blks = [];
      for (let i = 0; i < 64; i += 4) {
        md5blks[i >> 2] = s.charCodeAt(i) + 
                          (s.charCodeAt(i + 1) << 8) + 
                          (s.charCodeAt(i + 2) << 16) + 
                          (s.charCodeAt(i + 3) << 24);
      }
      return md5blks;
    }
    
    function rhex(n) {
      let s = '', j = 0;
      for (; j < 4; j++) {
        s += ((n >> (j * 8 + 4)) & 0x0F).toString(16) + 
             ((n >> (j * 8)) & 0x0F).toString(16);
      }
      return s;
    }
    
    function hex(x) {
      for (let i = 0; i < x.length; i++) {
        x[i] = rhex(x[i]);
      }
      return x.join('');
    }
    
    // Convert string to UTF-8 bytes
    const utf8Str = unescape(encodeURIComponent(str));
    const n = utf8Str.length;
    const state = [1732584193, -271733879, -1732584194, 271733878];
    let i;
    
    for (i = 64; i <= n; i += 64) {
      md5cycle(state, md5blk(utf8Str.substring(i - 64, i)));
    }
    
    const tail = utf8Str.substring(i - 64);
    const tailArr = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
    for (i = 0; i < tail.length; i++) {
      tailArr[i >> 2] |= tail.charCodeAt(i) << ((i % 4) << 3);
    }
    tailArr[i >> 2] |= 0x80 << ((i % 4) << 3);
    if (i > 55) {
      md5cycle(state, tailArr);
      for (i = 0; i < 16; i++) tailArr[i] = 0;
    }
    tailArr[14] = n * 8;
    md5cycle(state, tailArr);
    
    return hex(state);
  }

  /**
   * Hash password for Second Life (MD5)
   */
  static async hashPassword(password) {
    // Second Life uses MD5 hash
    const hash = await this.md5(password);
    return hash.toLowerCase();
  }
}

// Make available globally
window.XMLRPCClient = XMLRPCClient;
