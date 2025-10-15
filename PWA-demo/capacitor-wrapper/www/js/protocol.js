/**
 * Linkpoint PWA - Protocol Manager
 * Handles LLSD protocol and Second Life/OpenSim communication
 */

class ProtocolManager extends Utils.EventEmitter {
  constructor() {
    super();
    this.sessionId = null;
    this.agentId = null;
    this.regionHandle = null;
    this.circuitCode = null;
    this.connected = false;
    this.websocket = null;
    this.heartbeatInterval = null;
    this.capabilities = {};
  }

  /**
   * Grid configurations
   */
  static GRIDS = {
    agni: {
      name: 'Second Life (Main Grid)',
      loginUrl: 'https://login.agni.lindenlab.com/cgi-bin/login.cgi'
    },
    aditi: {
      name: 'Second Life Beta (Aditi)',
      loginUrl: 'https://login.aditi.lindenlab.com/cgi-bin/login.cgi'
    },
    osgrid: {
      name: 'OSGrid',
      loginUrl: 'http://login.osgrid.org/'
    }
  };

  /**
   * Serialize to LLSD XML
   */
  serializeLLSD(data) {
    const escapeXml = (str) => {
      return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&apos;');
    };

    const serialize = (obj) => {
      if (obj === null || obj === undefined) {
        return '<undef />';
      }
      
      if (typeof obj === 'boolean') {
        return obj ? '<boolean>true</boolean>' : '<boolean>false</boolean>';
      }
      
      if (typeof obj === 'number') {
        if (Number.isInteger(obj)) {
          return `<integer>${obj}</integer>`;
        }
        return `<real>${obj}</real>`;
      }
      
      if (typeof obj === 'string') {
        return `<string>${escapeXml(obj)}</string>`;
      }
      
      if (Array.isArray(obj)) {
        const items = obj.map(serialize).join('');
        return `<array>${items}</array>`;
      }
      
      if (typeof obj === 'object') {
        const entries = Object.entries(obj)
          .map(([key, value]) => `<key>${escapeXml(key)}</key>${serialize(value)}`)
          .join('');
        return `<map>${entries}</map>`;
      }
      
      return '<undef />';
    };

    return `<?xml version="1.0" encoding="UTF-8"?><llsd>${serialize(data)}</llsd>`;
  }

  /**
   * Parse LLSD XML
   */
  parseLLSD(xml) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xml, 'text/xml');

    const parse = (node) => {
      const tagName = node.tagName;
      
      if (tagName === 'undef') return null;
      if (tagName === 'boolean') return node.textContent === 'true';
      if (tagName === 'integer') return parseInt(node.textContent);
      if (tagName === 'real') return parseFloat(node.textContent);
      if (tagName === 'string') return node.textContent;
      if (tagName === 'uuid') return node.textContent;
      if (tagName === 'date') return new Date(node.textContent);
      if (tagName === 'uri') return node.textContent;
      if (tagName === 'binary') return atob(node.textContent);
      
      if (tagName === 'array') {
        const result = [];
        for (let child of node.children) {
          result.push(parse(child));
        }
        return result;
      }
      
      if (tagName === 'map') {
        const result = {};
        let key = null;
        for (let child of node.children) {
          if (child.tagName === 'key') {
            key = child.textContent;
          } else if (key !== null) {
            result[key] = parse(child);
            key = null;
          }
        }
        return result;
      }
      
      return null;
    };

    const llsd = doc.getElementsByTagName('llsd')[0];
    return llsd ? parse(llsd.children[0]) : null;
  }

  /**
   * Login to grid
   */
  async login(gridId, username, password, startLocation = 'last') {
    const grid = ProtocolManager.GRIDS[gridId];
    if (!grid) {
      throw new Error('Invalid grid selected');
    }

    // Parse username (FirstName LastName or FirstName.LastName)
    const nameParts = username.replace('.', ' ').split(' ');
    if (nameParts.length < 2) {
      throw new Error('Invalid username format. Use "FirstName LastName"');
    }

    const loginParams = {
      first: nameParts[0],
      last: nameParts[1],
      passwd: password,
      start: startLocation,
      channel: 'Linkpoint PWA',
      version: '1.0.0',
      platform: navigator.platform,
      mac: Utils.generateUUID(),
      id0: Utils.generateUUID(),
      agree_to_tos: 'true',
      read_critical: 'true',
      viewer_digest: Utils.generateUUID(),
      options: [
        'inventory-root',
        'inventory-skeleton',
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
        'login-flags',
        'global-textures'
      ]
    };

    try {
      // In a real implementation, this would use XML-RPC
      // For demo purposes, we'll simulate a successful login
      await Utils.sleep(1500); // Simulate network delay

      // Simulated login response
      const response = {
        login: 'true',
        session_id: Utils.generateUUID(),
        secure_session_id: Utils.generateUUID(),
        agent_id: Utils.generateUUID(),
        first_name: nameParts[0],
        last_name: nameParts[1],
        look_at: '[r1,r1,r0]',
        home: "{'region_handle':[r255232, r256512], 'position':[r128, r128, r30], 'look_at':[r1, r0, r0]}",
        message: 'Welcome to ' + grid.name,
        circuit_code: Math.floor(Math.random() * 1000000),
        sim_ip: '127.0.0.1',
        sim_port: 13000,
        region_x: 256000,
        region_y: 256000,
        seed_capability: 'https://sim.example.com/cap/' + Utils.generateUUID(),
        start_location: startLocation
      };

      this.sessionId = response.session_id;
      this.agentId = response.agent_id;
      this.circuitCode = response.circuit_code;
      this.connected = true;

      this.emit('login_success', response);
      return response;
    } catch (error) {
      this.emit('login_failed', error);
      throw error;
    }
  }

  /**
   * Establish WebSocket connection for real-time updates
   */
  connectWebSocket(url) {
    if (this.websocket) {
      this.websocket.close();
    }

    this.websocket = new WebSocket(url);

    this.websocket.onopen = () => {
      console.log('WebSocket connected');
      this.emit('websocket_connected');
      this.startHeartbeat();
    };

    this.websocket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        this.handleMessage(data);
      } catch (error) {
        console.error('WebSocket message error:', error);
      }
    };

    this.websocket.onerror = (error) => {
      console.error('WebSocket error:', error);
      this.emit('websocket_error', error);
    };

    this.websocket.onclose = () => {
      console.log('WebSocket disconnected');
      this.emit('websocket_disconnected');
      this.stopHeartbeat();
    };
  }

  /**
   * Handle incoming messages
   */
  handleMessage(data) {
    const messageType = data.type || data.message;
    
    switch (messageType) {
      case 'ChatFromSimulator':
        this.emit('chat_message', data);
        break;
      case 'AgentDataUpdate':
        this.emit('agent_update', data);
        break;
      case 'ObjectUpdate':
        this.emit('object_update', data);
        break;
      case 'AvatarAnimation':
        this.emit('avatar_animation', data);
        break;
      default:
        this.emit('message', data);
    }
  }

  /**
   * Send message to simulator
   */
  sendMessage(type, data) {
    if (!this.connected) {
      throw new Error('Not connected to grid');
    }

    const message = {
      type,
      session_id: this.sessionId,
      agent_id: this.agentId,
      ...data
    };

    if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
      this.websocket.send(JSON.stringify(message));
    } else {
      console.warn('WebSocket not ready, message queued');
      // In a real implementation, queue messages for retry
    }
  }

  /**
   * Start heartbeat to keep connection alive
   */
  startHeartbeat() {
    this.stopHeartbeat();
    this.heartbeatInterval = setInterval(() => {
      this.sendMessage('Ping', { timestamp: Date.now() });
    }, 30000); // Every 30 seconds
  }

  /**
   * Stop heartbeat
   */
  stopHeartbeat() {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval);
      this.heartbeatInterval = null;
    }
  }

  /**
   * Logout from grid
   */
  async logout() {
    this.stopHeartbeat();
    
    if (this.websocket) {
      this.websocket.close();
      this.websocket = null;
    }

    this.sessionId = null;
    this.agentId = null;
    this.connected = false;

    this.emit('logout');
  }

  /**
   * Get capabilities
   */
  async getCapabilities(seedUrl) {
    // In a real implementation, fetch capabilities from seed URL
    // For demo, return simulated capabilities
    this.capabilities = {
      EventQueueGet: seedUrl + '/event_queue',
      ChatSessionRequest: seedUrl + '/chat',
      SendUserReport: seedUrl + '/report',
      ViewerStats: seedUrl + '/stats'
    };
    return this.capabilities;
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
      agentId: this.agentId,
      circuitCode: this.circuitCode,
      connected: this.connected
    };
  }
}

// Make available globally
window.ProtocolManager = ProtocolManager;
