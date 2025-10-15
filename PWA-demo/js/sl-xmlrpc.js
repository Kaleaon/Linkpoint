/**
 * Linkpoint PWA - XML-RPC Client for Second Life Login
 * Based on Linkpoint Android implementation
 */

class XMLRPCClient {
  /**
   * Build XML-RPC login request
   */
  static buildLoginRequest(params) {
    const fields = [];
    
    // Add all login parameters
    fields.push({ name: 'first', value: params.firstName });
    fields.push({ name: 'last', value: params.lastName });
    fields.push({ name: 'passwd', value: `$1$${params.passwordHash}` });
    fields.push({ name: 'start', value: params.startLocation || 'last' });
    fields.push({ name: 'channel', value: params.channel || 'Linkpoint PWA' });
    fields.push({ name: 'version', value: params.version || '1.0.0' });
    fields.push({ name: 'platform', value: 'Web' });
    fields.push({ name: 'platform_version', value: navigator.userAgent });
    fields.push({ name: 'mac', value: params.macAddress || this.generateMAC() });
    fields.push({ name: 'id0', value: params.id0 || this.generateID0() });
    fields.push({ name: 'agree_to_tos', value: 'true' });
    fields.push({ name: 'read_critical', value: 'true' });
    fields.push({ name: 'viewer_digest', value: params.viewerDigest || this.generateViewerDigest() });
    
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
    
    // Add all fields
    for (const field of fields) {
      xml += '<member>\n';
      xml += `<name>${this.escapeXml(field.name)}</name>\n`;
      xml += `<value><string>${this.escapeXml(field.value)}</string></value>\n`;
      xml += '</member>\n';
    }
    
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
   */
  static async sendRequest(url, xmlRequest) {
    try {
      // Use Electron proxy if available
      const targetUrl = window.ELECTRON_PROXY_URL 
        ? `${window.ELECTRON_PROXY_URL}/sl-login`
        : url;
      
      if (window.IS_ELECTRON) {
        console.log('[SL] Using Electron proxy for login');
      }
      
      const response = await fetch(targetUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/xml',
          'Accept': 'text/xml, application/xml',
          'User-Agent': 'Linkpoint PWA/1.0.0'
        },
        body: xmlRequest
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }

      const responseText = await response.text();
      return this.parseLoginResponse(responseText);
      
    } catch (error) {
      console.error('XML-RPC request failed:', error);
      throw error;
    }
  }

  /**
   * Parse XML-RPC login response
   */
  static parseLoginResponse(xmlText) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xmlText, 'text/xml');
    
    // Check for parse errors
    const parseError = doc.querySelector('parsererror');
    if (parseError) {
      throw new Error('XML parse error: ' + parseError.textContent);
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
   * Calculate MD5 hash
   */
  static async md5(str) {
    const encoder = new TextEncoder();
    const data = encoder.encode(str);
    const hashBuffer = await crypto.subtle.digest('MD5', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
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
