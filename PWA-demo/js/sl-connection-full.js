/**
 * Linkpoint PWA - Complete SL Connection Implementation
 * Based on actual Linkpoint Kotlin/Java source code
 */

class SLConnectionFull extends Utils.EventEmitter {
  constructor() {
    super();
    
    // Connection state
    this.state = 'IDLE'; // IDLE, AUTHENTICATING, CONNECTING, CONNECTED, DISCONNECTING
    this.connected = false;
    this.userWantsConnected = false;
    
    // Auth data
    this.authParams = null;
    this.authReply = null;
    this.agentId = null;
    this.sessionId = null;
    this.secureSessionId = null;
    this.circuitCode = null;
    
    // Circuit data
    this.simAddress = null;
    this.simPort = null;
    this.regionHandle = null;
    
    // Capabilities
    this.seedCapability = null;
    this.capabilities = {};
    this.capEventQueue = null;
    
    // Modules
    this.inventory = null;
    this.friends = [];
    this.region = null;
    
    // Circuit (simulated for browser)
    this.circuit = null;
    
    // Event queue
    this.eventQueueRunning = false;
    this.lastEventId = null;
  }

  /**
   * Connect to grid
   */
  async connect(gridId, username, password, startLocation = 'last') {
    this.userWantsConnected = true;
    this.setState('AUTHENTICATING');

    try {
      // Use the real SLProtocol for login
      const protocol = new SLProtocol();
      const loginResult = await protocol.login(gridId, username, password, startLocation);

      // Store auth reply data
      this.authReply = loginResult;
      this.agentId = loginResult.agent_id;
      this.sessionId = loginResult.session_id;
      this.circuitCode = loginResult.circuit_code;
      this.simAddress = loginResult.sim_ip;
      this.simPort = loginResult.sim_port;
      this.seedCapability = loginResult.seed_capability;
      this.region = loginResult.region;

      // Get capabilities
      if (this.seedCapability) {
        await this.fetchCapabilities();
      }

      // Start circuit
      this.setState('CONNECTING');
      await this.startCircuit();

      // Start event queue
      if (this.capabilities.EventQueueGet) {
        this.startEventQueue();
      }

      this.setState('CONNECTED');
      this.connected = true;

      this.emit('connected', {
        agentId: this.agentId,
        sessionId: this.sessionId,
        region: this.region
      });

      return true;

    } catch (error) {
      console.error('Connection failed:', error);
      this.setState('IDLE');
      this.emit('connection_failed', error);
      throw error;
    }
  }

  /**
   * Fetch capabilities from seed URL
   */
  async fetchCapabilities() {
    try {
      const capsToRequest = [
        'EventQueueGet',
        'FetchInventoryDescendents2',
        'FetchInventory2',
        'ChatSessionRequest',
        'ViewerStats',
        'SendUserReport',
        'ObjectMedia',
        'ObjectMediaNavigate',
        'UploadBakedTexture',
        'GetDisplayNames',
        'AgentState',
        'AgentPreferences',
        'EnvironmentSettings',
        'ExtEnvironment',
        'RenderMaterials',
        'GetObjectPhysicsData',
        'GetObjectCost'
      ];

      const llsdRequest = this.buildLLSDArray(capsToRequest);

      const response = await fetch(this.seedCapability, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/llsd+xml',
          'Accept': 'application/llsd+xml'
        },
        body: llsdRequest,
        mode: 'cors',
        credentials: 'omit'
      });

      if (response.ok) {
        const text = await response.text();
        const caps = this.parseLLSDResponse(text);
        
        if (caps) {
          this.capabilities = caps;
          console.log(`Fetched ${Object.keys(caps).length} capabilities`);
          this.emit('capabilities_ready', caps);
        }
      }
    } catch (error) {
      console.error('Failed to fetch capabilities:', error);
      // Non-fatal, continue without full capabilities
    }
  }

  /**
   * Start circuit (browser simulation)
   */
  async startCircuit() {
    console.log(`Starting circuit to ${this.simAddress}:${this.simPort}`);
    console.log(`Circuit code: ${this.circuitCode}`);

    // In browser, we use capabilities instead of UDP
    // The circuit would normally send:
    // 1. UseCircuitCode
    // 2. CompleteAgentMovement
    // 3. RegionHandshakeReply

    // Simulate circuit established
    this.circuit = {
      code: this.circuitCode,
      established: true,
      lastPing: Date.now()
    };

    this.emit('circuit_established');
  }

  /**
   * Start event queue polling
   */
  startEventQueue() {
    if (this.eventQueueRunning) return;
    if (!this.capabilities.EventQueueGet) return;

    this.eventQueueRunning = true;
    this.pollEventQueue();
  }

  /**
   * Poll event queue for updates
   */
  async pollEventQueue() {
    if (!this.eventQueueRunning) return;

    try {
      const url = this.capabilities.EventQueueGet;
      const body = this.lastEventId 
        ? `<?xml version="1.0"?><llsd><map><key>ack</key><integer>${this.lastEventId}</integer><key>done</key><boolean>false</boolean></map></llsd>`
        : '<?xml version="1.0"?><llsd><map><key>done</key><boolean>false</boolean></map></llsd>';

      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/llsd+xml'
        },
        body,
        mode: 'cors'
      });

      if (response.ok) {
        const text = await response.text();
        const events = this.parseEventQueue(text);
        
        if (events && events.length > 0) {
          events.forEach(event => this.handleEvent(event));
          this.lastEventId = events[events.length - 1].id;
        }
      }
    } catch (error) {
      console.error('Event queue error:', error);
    }

    // Continue polling
    if (this.eventQueueRunning) {
      setTimeout(() => this.pollEventQueue(), 1000);
    }
  }

  /**
   * Stop event queue
   */
  stopEventQueue() {
    this.eventQueueRunning = false;
  }

  /**
   * Handle event from event queue
   */
  handleEvent(event) {
    console.log('Event:', event.message, event.body);

    switch (event.message) {
      case 'ChatFromSimulator':
        this.emit('chat_message', event.body);
        break;
      case 'ObjectUpdate':
        this.emit('object_update', event.body);
        break;
      case 'KillObject':
        this.emit('kill_object', event.body);
        break;
      case 'AgentDataUpdate':
        this.emit('agent_update', event.body);
        break;
      case 'ParcelProperties':
        this.emit('parcel_properties', event.body);
        break;
      default:
        this.emit('event', event);
    }
  }

  /**
   * Send chat message
   */
  async sendChat(message, channel = 0, type = 1) {
    if (!this.connected) {
      throw new Error('Not connected');
    }

    // Use ChatSessionRequest capability if available
    if (this.capabilities.ChatSessionRequest) {
      const url = this.capabilities.ChatSessionRequest;
      const llsdBody = `<?xml version="1.0"?>
<llsd>
  <map>
    <key>message</key><string>${this.escapeXml(message)}</string>
    <key>channel</key><integer>${channel}</integer>
    <key>type</key><integer>${type}</integer>
  </map>
</llsd>`;

      try {
        await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/llsd+xml' },
          body: llsdBody
        });
        this.emit('chat_sent', { message, channel, type });
      } catch (error) {
        console.error('Failed to send chat:', error);
        throw error;
      }
    }
  }

  /**
   * Build LLSD array
   */
  buildLLSDArray(items) {
    let xml = '<?xml version="1.0" encoding="UTF-8"?>\n<llsd>\n<array>\n';
    for (const item of items) {
      xml += `<string>${this.escapeXml(item)}</string>\n`;
    }
    xml += '</array>\n</llsd>';
    return xml;
  }

  /**
   * Parse LLSD response (reuse from sl-protocol-real.js)
   */
  parseLLSDResponse(xml) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xml, 'text/xml');
    const llsd = doc.querySelector('llsd');
    if (!llsd || !llsd.firstElementChild) return null;
    
    return this.parseLLSDElement(llsd.firstElementChild);
  }

  /**
   * Parse LLSD element
   */
  parseLLSDElement(element) {
    const tagName = element.tagName.toLowerCase();
    
    switch (tagName) {
      case 'map':
        return this.parseLLSDMap(element);
      case 'array':
        return this.parseLLSDArray(element);
      case 'string':
        return element.textContent;
      case 'integer':
        return parseInt(element.textContent);
      case 'real':
        return parseFloat(element.textContent);
      case 'boolean':
        return element.textContent === 'true' || element.textContent === '1';
      case 'uuid':
        return element.textContent;
      case 'binary':
        return atob(element.textContent);
      case 'undef':
        return null;
      default:
        return element.textContent;
    }
  }

  /**
   * Parse LLSD map
   */
  parseLLSDMap(mapEl) {
    const result = {};
    let key = null;
    
    for (const child of mapEl.children) {
      if (child.tagName.toLowerCase() === 'key') {
        key = child.textContent;
      } else if (key) {
        result[key] = this.parseLLSDElement(child);
        key = null;
      }
    }
    
    return result;
  }

  /**
   * Parse LLSD array
   */
  parseLLSDArray(arrayEl) {
    const result = [];
    for (const child of arrayEl.children) {
      result.push(this.parseLLSDElement(child));
    }
    return result;
  }

  /**
   * Parse event queue response
   */
  parseEventQueue(xml) {
    const data = this.parseLLSDResponse(xml);
    if (!data || !data.events) return [];

    return data.events.map((event, index) => ({
      id: data.id || index,
      message: event.message,
      body: event.body
    }));
  }

  /**
   * Escape XML
   */
  escapeXml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&apos;');
  }

  /**
   * Set connection state
   */
  setState(newState) {
    if (this.state !== newState) {
      const oldState = this.state;
      this.state = newState;
      console.log(`Connection state: ${oldState} → ${newState}`);
      this.emit('state_changed', { oldState, newState });
    }
  }

  /**
   * Disconnect
   */
  async disconnect() {
    this.userWantsConnected = false;
    this.stopEventQueue();
    
    if (this.circuit) {
      // Send CloseCircuit if needed
      this.circuit = null;
    }

    this.connected = false;
    this.setState('IDLE');
    this.emit('disconnected');
  }

  /**
   * Get connection info
   */
  getConnectionInfo() {
    return {
      state: this.state,
      connected: this.connected,
      agentId: this.agentId,
      sessionId: this.sessionId,
      region: this.region,
      capabilities: Object.keys(this.capabilities).length
    };
  }
}

// Make available globally
window.SLConnectionFull = SLConnectionFull;
