/**
 * Linkpoint PWA - Real Second Life Protocol Implementation
 * Based on Linkpoint Android slproto package
 */

class SLProtocol extends Utils.EventEmitter {
  constructor() {
    super();
    this.sessionId = null;
    this.secureSessionId = null;
    this.agentId = null;
    this.circuitCode = null;
    this.simAddress = null;
    this.simPort = null;
    this.seedCapability = null;
    this.connected = false;
    this.capabilities = {};
    this.region = null;
    this.inventoryRoot = null;
    this.friends = [];
    
    // WebSocket bridge for UDP-like communication
    this.wsConnection = null;
    this.messageQueue = [];
    this.seqNum = 0;
    
    // Message handlers
    this.messageHandlers = new Map();
    this.setupMessageHandlers();
  }

  /**
   * Setup message handlers
   */
  setupMessageHandlers() {
    this.messageHandlers.set(SLMessageTypes.MessageIDs.CHAT_FROM_SIMULATOR, 
      (msg) => this.handleChatFromSimulator(msg));
    this.messageHandlers.set(SLMessageTypes.MessageIDs.OBJECT_UPDATE, 
      (msg) => this.handleObjectUpdate(msg));
    this.messageHandlers.set(SLMessageTypes.MessageIDs.REGION_HANDSHAKE, 
      (msg) => this.handleRegionHandshake(msg));
  }

  /**
   * Grid configurations (real Second Life URLs)
   * 
   * Note on HTTPS vs HTTP:
   * - Second Life (Agni/Aditi): Uses HTTPS with SSL/TLS 1.2+ for secure authentication
   *   SSL certificates are regularly updated (latest renewal April 2025)
   * - OSGrid: Currently uses HTTP only - HTTPS not supported for login endpoint
   *   (OSGrid website uses HTTPS, but login.osgrid.org viewer endpoint is HTTP only)
   * 
   * Research findings (2024-2025):
   * - Second Life maintains HTTPS support with modern TLS protocols
   * - OSGrid login remains HTTP-only; no HTTPS endpoint available
   */
  static GRIDS = {
    agni: {
      name: 'Second Life (Main Grid - Agni)',
      loginUrl: 'https://login.agni.lindenlab.com/cgi-bin/login.cgi',
      isSecondLife: true
    },
    aditi: {
      name: 'Second Life Beta Grid (Aditi)',
      loginUrl: 'https://login.aditi.lindenlab.com/cgi-bin/login.cgi',
      isSecondLife: true
    },
    osgrid: {
      name: 'OSGrid',
      loginUrl: 'http://login.osgrid.org/', // HTTP only - HTTPS not available for viewer login
      isOpenSim: true
    }
  };

  /**
   * Real login to Second Life/OpenSim grid
   */
  async login(gridId, username, password, startLocation = 'last') {
    const grid = SLProtocol.GRIDS[gridId];
    if (!grid) {
      throw new Error('Invalid grid selected');
    }

    // Parse username
    const nameParts = username.replace(/[._]/g, ' ').trim().split(/\s+/);
    if (nameParts.length < 1) {
      throw new Error('Invalid username format');
    }

    const firstName = nameParts[0];
    const lastName = nameParts.length > 1 ? nameParts[1] : 'Resident';

    try {
      // Hash password with MD5 (Second Life requirement)
      const passwordHash = await XMLRPCClient.hashPassword(password);

      // Build login parameters
      const loginParams = {
        firstName,
        lastName,
        passwordHash,
        startLocation,
        channel: 'Linkpoint PWA',
        version: '1.0.0',
        macAddress: XMLRPCClient.generateMAC(),
        id0: XMLRPCClient.generateID0(),
        viewerDigest: XMLRPCClient.generateViewerDigest(),
        loginUri: grid.loginUrl
      };

      // Build XML-RPC request
      const xmlRequest = XMLRPCClient.buildLoginRequest(loginParams);
      
      console.log('Logging in to', grid.name);
      console.log('User:', firstName, lastName);

      // Send login request
      const response = await XMLRPCClient.sendRequest(grid.loginUrl, xmlRequest);

      // Check for login success
      if (!response.login || response.login === 'false') {
        throw new Error(response.message || 'Login failed');
      }

      // Check for indeterminate response (MFA, etc.)
      if (response.login === 'indeterminate') {
        if (response.next_method && response.next_url) {
          // Handle MFA or additional steps
          throw new Error('Additional authentication required (not yet supported in PWA)');
        }
      }

      // Parse successful login response
      this.sessionId = response.session_id;
      this.secureSessionId = response.secure_session_id;
      this.agentId = response.agent_id;
      this.circuitCode = parseInt(response.circuit_code);
      this.simAddress = response.sim_ip;
      this.simPort = parseInt(response.sim_port);
      this.seedCapability = response.seed_capability;
      this.inventoryRoot = response['inventory-root']?.[0]?.folder_id;
      
      // Parse friends list
      if (response['buddy-list']) {
        this.friends = response['buddy-list'].map(friend => ({
          id: friend.buddy_id,
          rightsGiven: friend.buddy_rights_given || 0,
          rightsHas: friend.buddy_rights_has || 0
        }));
      }

      // Get additional data
      this.region = {
        name: response['sim_name'] || 'Unknown',
        x: parseInt(response.region_x) || 0,
        y: parseInt(response.region_y) || 0,
        sizeX: parseInt(response.region_size_x) || 256,
        sizeY: parseInt(response.region_size_y) || 256,
        handle: response.region_handle
      };

      this.connected = true;

      // Fetch capabilities from seed capability
      if (this.seedCapability) {
        await this.fetchCapabilities();
      }

      // Start event queue
      if (this.capabilities.EventQueueGet) {
        this.startEventQueue();
      }

      this.emit('login_success', {
        agentId: this.agentId,
        sessionId: this.sessionId,
        firstName,
        lastName,
        message: response.message,
        region: this.region,
        inventoryRoot: this.inventoryRoot,
        friendsCount: this.friends.length
      });

      return {
        success: true,
        agent_id: this.agentId,
        session_id: this.sessionId,
        first_name: firstName,
        last_name: lastName,
        message: response.message || 'Login successful',
        region: this.region,
        circuit_code: this.circuitCode,
        sim_ip: this.simAddress,
        sim_port: this.simPort,
        seed_capability: this.seedCapability
      };

    } catch (error) {
      console.error('Login error:', error);
      this.emit('login_failed', error);
      throw error;
    }
  }

  /**
   * Fetch capabilities from seed capability URL
   */
  async fetchCapabilities() {
    if (!this.seedCapability) {
      console.warn('No seed capability URL');
      return;
    }

    try {
      // Build capability request
      const capsRequest = [
        'EventQueueGet',
        'ChatSessionRequest',
        'SendUserReport',
        'ViewerStats',
        'SendPostcard',
        'ObjectMedia',
        'ObjectMediaNavigate',
        'FetchInventoryDescendents2',
        'FetchInventory2',
        'UploadBakedTexture',
        'UpdateScriptAgent',
        'UpdateNotecardAgentInventory',
        'CopyInventoryFromNotecard',
        'CreateInventoryCategory',
        'UpdateGestureAgentInventory',
        'UpdateAnimSet',
        'ParcelPropertiesUpdate',
        'GetDisplayNames',
        'AgentState',
        'AgentPreferences',
        'UpdateAgentLanguage',
        'EnvironmentSettings',
        'ExtEnvironment'
      ];

      const response = await fetch(this.seedCapability, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/llsd+xml',
          'Accept': 'application/llsd+xml, application/llsd+binary'
        },
        body: this.buildLLSDArray(capsRequest)
      });

      if (!response.ok) {
        throw new Error(`Capabilities fetch failed: ${response.status}`);
      }

      const responseText = await response.text();
      const caps = this.parseLLSDXML(responseText);
      
      // Store capabilities
      if (caps && typeof caps === 'object') {
        this.capabilities = caps;
        console.log('Capabilities fetched:', Object.keys(caps).length, 'capabilities');
        this.emit('capabilities_fetched', caps);
      }

    } catch (error) {
      console.error('Failed to fetch capabilities:', error);
      // Non-fatal error, continue without capabilities
    }
  }

  /**
   * Build LLSD array for capability request
   */
  buildLLSDArray(items) {
    let xml = '<?xml version="1.0" encoding="UTF-8"?>\n';
    xml += '<llsd>\n';
    xml += '<array>\n';
    for (const item of items) {
      xml += `<string>${this.escapeXml(item)}</string>\n`;
    }
    xml += '</array>\n';
    xml += '</llsd>\n';
    return xml;
  }

  /**
   * Parse LLSD XML response
   */
  parseLLSDXML(xml) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xml, 'text/xml');
    
    const llsdElement = doc.querySelector('llsd');
    if (!llsdElement) {
      throw new Error('Invalid LLSD XML');
    }

    const firstChild = Array.from(llsdElement.children).find(el => el.nodeType === 1);
    if (!firstChild) {
      return null;
    }

    return this.parseLLSDElement(firstChild);
  }

  /**
   * Parse LLSD element recursively
   */
  parseLLSDElement(element) {
    const tagName = element.tagName.toLowerCase();
    const text = element.textContent.trim();

    switch (tagName) {
      case 'undef':
        return null;
      case 'boolean':
        return text === 'true' || text === '1';
      case 'integer':
        return parseInt(text);
      case 'real':
        return parseFloat(text);
      case 'string':
        return text;
      case 'uuid':
        return text;
      case 'date':
        return new Date(text);
      case 'uri':
        return text;
      case 'binary':
        return atob(text);
      case 'map':
        return this.parseLLSDMap(element);
      case 'array':
        return this.parseLLSDArray(element);
      default:
        return text;
    }
  }

  /**
   * Parse LLSD map
   */
  parseLLSDMap(mapElement) {
    const result = {};
    let currentKey = null;

    for (const child of mapElement.children) {
      if (child.tagName.toLowerCase() === 'key') {
        currentKey = child.textContent.trim();
      } else if (currentKey !== null) {
        result[currentKey] = this.parseLLSDElement(child);
        currentKey = null;
      }
    }

    return result;
  }

  /**
   * Parse LLSD array
   */
  parseLLSDArray(arrayElement) {
    const result = [];

    for (const child of arrayElement.children) {
      result.push(this.parseLLSDElement(child));
    }

    return result;
  }

  /**
   * Escape XML
   */
  escapeXml(str) {
    return XMLRPCClient.escapeXml(str);
  }

  /**
   * Get capability URL
   */
  getCapability(name) {
    return this.capabilities[name];
  }

  /**
   * Check if connected
   */
  isConnected() {
    return this.connected;
  }

  /**
   * Get session info
   */
  getSessionInfo() {
    return {
      sessionId: this.sessionId,
      secureSessionId: this.secureSessionId,
      agentId: this.agentId,
      circuitCode: this.circuitCode,
      simAddress: this.simAddress,
      simPort: this.simPort,
      connected: this.connected,
      region: this.region
    };
  }

  /**
   * Logout
   */
  async logout() {
    this.sessionId = null;
    this.secureSessionId = null;
    this.agentId = null;
    this.circuitCode = null;
    this.connected = false;
    this.capabilities = {};
    
    this.emit('logout');
  }
}

// Make available globally
window.SLProtocol = SLProtocol;
