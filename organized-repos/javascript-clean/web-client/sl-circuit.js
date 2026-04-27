/**
 * Linkpoint PWA - Circuit Communication
 * Handles UDP-like message communication with Second Life simulators
 * Based on Linkpoint Android SLCircuit implementation
 */

class SLCircuit extends Utils.EventEmitter {
  constructor(protocol) {
    super();
    this.protocol = protocol;
    
    // Circuit state
    this.connected = false;
    this.circuitCode = null;
    this.agentId = null;
    this.sessionId = null;
    
    // Message queues
    this.outgoingQueue = [];
    this.unackedQueue = [];
    this.receivedAcks = [];
    this.pendingAcks = [];
    
    // Sequence numbers
    this.lastSeqNum = 0;
    this.lastReceivedSeqNum = 0;
    
    // Timing
    this.lastReceivedTime = 0;
    this.lastPingSent = 0;
    this.pingInterval = 5000; // 5 seconds
    this.messageTimeout = 5000; // 5 seconds
    this.maxRetries = 3;
    
    // Ping tracking
    this.pingSentCount = 0;
    this.lastPingID = 0;
    
    // For browser, we use WebSocket or long-polling instead of raw UDP
    this.transportType = 'websocket'; // 'websocket' or 'polling'
  }

  /**
   * Start circuit connection
   */
  async start(simAddress, simPort) {
    this.simAddress = simAddress;
    this.simPort = simPort;
    
    // In browser, we can't use raw UDP
    // Options:
    // 1. Use WebSocket bridge server
    // 2. Use WebRTC data channels
    // 3. Use HTTP capabilities only
    
    // For now, use capabilities-based communication
    console.log(`Circuit connection to ${simAddress}:${simPort}`);
    console.log('Using HTTPS capabilities for browser-based communication');
    
    this.connected = true;
    this.emit('connected');
  }

  /**
   * Send UseCircuitCode message
   */
  async sendUseCircuitCode() {
    if (!this.circuitCode || !this.agentId || !this.sessionId) {
      throw new Error('Circuit parameters not set');
    }

    // In browser, this would be sent via WebSocket or capability
    console.log('SendUseCircuitCode:', {
      code: this.circuitCode,
      agentId: this.agentId,
      sessionId: this.sessionId
    });
    
    this.emit('use_circuit_code_sent');
  }

  /**
   * Send CompleteAgentMovement
   */
  async sendCompleteAgentMovement() {
    console.log('SendCompleteAgentMovement');
    this.emit('complete_agent_movement_sent');
  }

  /**
   * Send RegionHandshakeReply
   */
  async sendRegionHandshakeReply() {
    console.log('SendRegionHandshakeReply');
    this.emit('region_handshake_reply_sent');
  }

  /**
   * Send message
   */
  sendMessage(message) {
    if (!this.connected) {
      console.warn('Circuit not connected');
      return;
    }

    message.seqNum = ++this.lastSeqNum;
    message.sentTimeMillis = Date.now();
    
    this.outgoingQueue.push(message);
    
    if (message.isReliable) {
      this.unackedQueue.push(message);
    }

    this.processOutgoing();
  }

  /**
   * Process outgoing queue
   */
  processOutgoing() {
    while (this.outgoingQueue.length > 0) {
      const message = this.outgoingQueue.shift();
      this.transmitMessage(message);
    }
  }

  /**
   * Transmit message (via capability or WebSocket)
   */
  transmitMessage(message) {
    console.log(`TX: ${message.getMessageName()} (seq: ${message.seqNum})`);
    
    // In browser, translate to HTTP capability call
    this.emit('message_sent', message);
  }

  /**
   * Handle received message
   */
  handleMessage(message) {
    this.lastReceivedTime = Date.now();
    this.lastReceivedSeqNum = message.seqNum;
    
    // Add to pending ACKs if reliable
    if (message.isReliable) {
      this.pendingAcks.push(message.seqNum);
      this.sendAcks();
    }

    // Process message
    console.log(`RX: ${message.getMessageName()} (seq: ${message.seqNum})`);
    this.emit('message_received', message);
  }

  /**
   * Send ACKs
   */
  sendAcks() {
    if (this.pendingAcks.length === 0) return;
    
    // Bundle ACKs
    const acks = this.pendingAcks.splice(0, 255); // Max 255 ACKs per packet
    console.log(`Sending ${acks.length} ACKs`);
    
    // In real implementation, send PacketAck message
    this.emit('acks_sent', acks);
  }

  /**
   * Process resends for unacked messages
   */
  processResends() {
    const now = Date.now();
    const resends = [];
    
    this.unackedQueue = this.unackedQueue.filter(message => {
      if (now >= message.sentTimeMillis + this.messageTimeout) {
        message.retries++;
        
        if (message.retries > this.maxRetries) {
          console.warn(`Message timeout: ${message.getMessageName()}`);
          this.emit('message_timeout', message);
          return false; // Remove from queue
        } else {
          message.isResent = true;
          message.sentTimeMillis = now;
          resends.push(message);
          return true; // Keep in queue
        }
      }
      return true;
    });
    
    // Resend messages
    for (const message of resends) {
      this.outgoingQueue.push(message);
    }
    
    if (resends.length > 0) {
      this.processOutgoing();
    }
  }

  /**
   * Handle ACK
   */
  handleAck(seqNum) {
    this.unackedQueue = this.unackedQueue.filter(msg => msg.seqNum !== seqNum);
  }

  /**
   * Send ping
   */
  sendPing() {
    if (!this.connected) return;
    
    const now = Date.now();
    if (now - this.lastPingSent < this.pingInterval) return;
    
    this.lastPingID = (this.lastPingID + 1) % 256;
    this.lastPingSent = now;
    this.pingSentCount++;
    
    console.log(`Ping #${this.lastPingID}`);
    this.emit('ping_sent', this.lastPingID);
  }

  /**
   * Handle pong
   */
  handlePong(pingID) {
    const rtt = Date.now() - this.lastPingSent;
    console.log(`Pong #${pingID}, RTT: ${rtt}ms`);
    this.pingSentCount = 0;
    this.emit('pong_received', { pingID, rtt });
  }

  /**
   * Check circuit health
   */
  checkHealth() {
    const now = Date.now();
    
    // Check for timeout
    if (this.connected && now - this.lastReceivedTime > 30000) {
      console.warn('Circuit timeout - no messages received');
      this.emit('timeout');
    }
    
    // Check ping timeout
    if (this.pingSentCount > 3) {
      console.warn('Circuit timeout - no pong responses');
      this.emit('timeout');
    }
    
    // Send ping if needed
    if (now - this.lastReceivedTime > 10000) {
      this.sendPing();
    }
  }

  /**
   * Stop circuit
   */
  stop() {
    this.connected = false;
    this.outgoingQueue = [];
    this.unackedQueue = [];
    this.emit('disconnected');
  }
}

// Make available globally
window.SLCircuit = SLCircuit;
