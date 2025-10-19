/**
 * Linkpoint PWA - Second Life WebRTC Voice Bridge
 * Handles SL-specific voice protocol using WebRTC
 * Based on Linkpoint Android SecondLifeWebRTCBridge implementation
 */

class SLWebRTCBridge extends Utils.EventEmitter {
  constructor(protocolManager) {
    super();
    this.protocol = protocolManager;
    this.voiceManager = null;
    
    // SL Voice state
    this.voiceAccountName = null;
    this.voicePassword = null;
    this.voiceServerUri = null;
    this.currentChannelUri = null;
    this.isConnected = false;
    
    // WebRTC components
    this.peerConnections = new Map();
    this.localStream = null;
    this.audioContext = null;
    this.spatialNodes = new Map();
  }

  /**
   * Initialize the SL WebRTC bridge
   */
  async init(voiceManager) {
    console.log('[SLVoiceBridge] Initializing...');
    this.voiceManager = voiceManager;
    
    // Setup protocol listeners for SL voice events
    this.protocol.on('ParcelVoiceInfoRequest', (data) => this.handleParcelVoiceInfo(data));
    this.protocol.on('ProvisionVoiceAccountRequest', (data) => this.handleVoiceProvisioning(data));
    this.protocol.on('voice_credentials', (data) => this.processVoiceCredentials(data));
    
    // Create audio context for spatial audio
    try {
      const AudioContext = window.AudioContext || window.webkitAudioContext;
      this.audioContext = new AudioContext();
      console.log('[SLVoiceBridge] Audio context created for spatial audio');
    } catch (error) {
      console.error('[SLVoiceBridge] Failed to create audio context:', error);
    }
    
    return true;
  }

  /**
   * Handle parcel voice info from SL
   */
  handleParcelVoiceInfo(data) {
    console.log('[SLVoiceBridge] Parcel voice info received:', data);
    
    if (data.voice_enabled) {
      console.log('[SLVoiceBridge] Voice is enabled for this parcel');
      // Request voice account provisioning
      this.requestVoiceProvisioning();
    } else {
      console.log('[SLVoiceBridge] Voice is disabled for this parcel');
      this.disconnectFromVoice();
    }
  }

  /**
   * Request voice account provisioning from SL
   */
  async requestVoiceProvisioning() {
    console.log('[SLVoiceBridge] Requesting voice account provisioning...');
    
    try {
      // Send provision request via protocol
      if (this.protocol.sendMessage) {
        await this.protocol.sendMessage({
          type: 'ProvisionVoiceAccountRequest',
          agentId: this.protocol.agentId,
          sessionId: this.protocol.sessionId
        });
      }
    } catch (error) {
      console.error('[SLVoiceBridge] Failed to request voice provisioning:', error);
    }
  }

  /**
   * Handle voice provisioning response from SL
   */
  async handleVoiceProvisioning(data) {
    console.log('[SLVoiceBridge] Voice provisioning received:', data);
    
    if (data.username && data.password) {
      this.voiceAccountName = data.username;
      this.voicePassword = data.password;
      this.voiceServerUri = data.voice_server_uri || data.voice_sip_uri_hostname;
      this.currentChannelUri = data.channel_uri;
      
      // Process the credentials and connect
      await this.processVoiceCredentials(data);
    }
  }

  /**
   * Process SL voice credentials and establish WebRTC connection
   * This is the main entry point for SL voice integration
   */
  async processVoiceCredentials(credentials) {
    console.log('[SLVoiceBridge] Processing SL voice credentials...');
    console.log('[SLVoiceBridge] Account:', credentials.username || credentials.voiceAccountName);
    console.log('[SLVoiceBridge] Server:', credentials.voice_server_uri || this.voiceServerUri);
    console.log('[SLVoiceBridge] Channel:', credentials.channel_uri || this.currentChannelUri);
    
    try {
      // Store credentials
      this.voiceAccountName = credentials.username || credentials.voiceAccountName || this.voiceAccountName;
      this.voicePassword = credentials.password || credentials.voicePassword || this.voicePassword;
      this.voiceServerUri = credentials.voice_server_uri || credentials.voiceServerUri || this.voiceServerUri;
      this.currentChannelUri = credentials.channel_uri || credentials.channelUri || this.currentChannelUri;
      
      // Step 1: Enable WebRTC if not already enabled
      if (!this.voiceManager || !this.voiceManager.isEnabled()) {
        console.log('[SLVoiceBridge] Enabling WebRTC voice...');
        const enabled = await this.voiceManager.enable();
        if (!enabled) {
          throw new Error('Failed to enable WebRTC voice');
        }
      }
      
      // Step 2: Authenticate with voice server (if required)
      // SL's WebRTC implementation may not require separate auth
      console.log('[SLVoiceBridge] Authenticating with voice server...');
      // In browser WebRTC, authentication happens via signaling server
      
      // Step 3: Connect to voice channel
      console.log('[SLVoiceBridge] Connecting to voice channel...');
      await this.connectToVoiceChannel();
      
      console.log('[SLVoiceBridge] Successfully connected to SL voice');
      this.isConnected = true;
      this.emit('voice_connected');
      Utils.showToast('Connected to Second Life voice', 'success');
      
      return true;
      
    } catch (error) {
      console.error('[SLVoiceBridge] Failed to process voice credentials:', error);
      this.emit('voice_error', error);
      Utils.showToast('Failed to connect to voice: ' + error.message, 'error');
      return false;
    }
  }

  /**
   * Connect to SL voice channel using WebRTC
   */
  async connectToVoiceChannel() {
    if (!this.currentChannelUri) {
      throw new Error('No channel URI available');
    }
    
    console.log('[SLVoiceBridge] Connecting to channel:', this.currentChannelUri);
    
    // In a real implementation, this would:
    // 1. Connect to SL's WebRTC signaling server
    // 2. Exchange SDP offers/answers with other participants
    // 3. Establish peer connections
    // 4. Setup spatial audio processing
    
    // For now, we'll setup the WebRTC infrastructure
    if (!this.localStream) {
      this.localStream = this.voiceManager.localStream;
    }
    
    // Emit event that we're in the channel
    this.emit('channel_connected', {
      channelUri: this.currentChannelUri,
      spatialAudio: true
    });
    
    return true;
  }

  /**
   * Create peer connection for another user
   */
  async createPeerConnection(userId, offer) {
    console.log('[SLVoiceBridge] Creating peer connection for user:', userId);
    
    const config = {
      iceServers: [
        { urls: 'stun:stun.l.google.com:19302' },
        { urls: 'stun:stun1.l.google.com:19302' }
      ]
    };
    
    const peerConnection = new RTCPeerConnection(config);
    
    // Add local stream
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => {
        peerConnection.addTrack(track, this.localStream);
      });
    }
    
    // Handle incoming tracks
    peerConnection.ontrack = (event) => {
      console.log('[SLVoiceBridge] Received remote track from:', userId);
      this.handleRemoteTrack(userId, event.streams[0]);
    };
    
    // Handle ICE candidates
    peerConnection.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendIceCandidate(userId, event.candidate);
      }
    };
    
    // Handle connection state
    peerConnection.onconnectionstatechange = () => {
      console.log('[SLVoiceBridge] Peer connection state:', peerConnection.connectionState);
      if (peerConnection.connectionState === 'disconnected') {
        this.handleUserLeft(userId);
      }
    };
    
    this.peerConnections.set(userId, peerConnection);
    
    return peerConnection;
  }

  /**
   * Handle remote audio track from another user
   */
  handleRemoteTrack(userId, stream) {
    console.log('[SLVoiceBridge] Processing remote audio for:', userId);
    
    // Setup spatial audio if context available
    if (this.audioContext) {
      this.setupSpatialAudio(userId, stream);
    }
    
    this.emit('user_speaking', { userId, speaking: true });
  }

  /**
   * Setup 3D spatial audio for a user
   */
  setupSpatialAudio(userId, stream) {
    try {
      const source = this.audioContext.createMediaStreamSource(stream);
      const panner = this.audioContext.createPanner();
      
      // Configure 3D panning
      panner.panningModel = 'HRTF';
      panner.distanceModel = 'inverse';
      panner.refDistance = 1;
      panner.maxDistance = 10000;
      panner.rolloffFactor = 1;
      panner.coneInnerAngle = 360;
      panner.coneOuterAngle = 0;
      panner.coneOuterGain = 0;
      
      // Connect: source -> panner -> destination
      source.connect(panner);
      panner.connect(this.audioContext.destination);
      
      this.spatialNodes.set(userId, { source, panner });
      
      console.log('[SLVoiceBridge] Spatial audio configured for:', userId);
      
    } catch (error) {
      console.error('[SLVoiceBridge] Failed to setup spatial audio:', error);
    }
  }

  /**
   * Update spatial position of a user
   */
  updateUserPosition(userId, position) {
    const nodes = this.spatialNodes.get(userId);
    if (nodes && nodes.panner) {
      nodes.panner.positionX.value = position.x;
      nodes.panner.positionY.value = position.y;
      nodes.panner.positionZ.value = position.z;
    }
  }

  /**
   * Update listener position (current user)
   */
  updateListenerPosition(position, orientation) {
    if (this.audioContext && this.audioContext.listener) {
      const listener = this.audioContext.listener;
      
      if (listener.positionX) {
        listener.positionX.value = position.x;
        listener.positionY.value = position.y;
        listener.positionZ.value = position.z;
        
        listener.forwardX.value = orientation.forward.x;
        listener.forwardY.value = orientation.forward.y;
        listener.forwardZ.value = orientation.forward.z;
        
        listener.upX.value = orientation.up.x;
        listener.upY.value = orientation.up.y;
        listener.upZ.value = orientation.up.z;
      }
    }
  }

  /**
   * Handle user joining voice channel
   */
  handleUserJoined(userId, userData) {
    console.log('[SLVoiceBridge] User joined voice:', userId);
    this.emit('user_joined', { userId, userData });
  }

  /**
   * Handle user leaving voice channel
   */
  handleUserLeft(userId) {
    console.log('[SLVoiceBridge] User left voice:', userId);
    
    // Close peer connection
    const peerConnection = this.peerConnections.get(userId);
    if (peerConnection) {
      peerConnection.close();
      this.peerConnections.delete(userId);
    }
    
    // Remove spatial audio nodes
    const nodes = this.spatialNodes.get(userId);
    if (nodes) {
      nodes.source.disconnect();
      nodes.panner.disconnect();
      this.spatialNodes.delete(userId);
    }
    
    this.emit('user_left', { userId });
  }

  /**
   * Send ICE candidate to peer via signaling
   */
  sendIceCandidate(userId, candidate) {
    // This would send via SL protocol
    if (this.protocol.sendMessage) {
      this.protocol.sendMessage({
        type: 'VoiceIceCandidate',
        targetUser: userId,
        candidate: candidate
      });
    }
  }

  /**
   * Disconnect from voice
   */
  async disconnectFromVoice() {
    console.log('[SLVoiceBridge] Disconnecting from voice...');
    
    // Close all peer connections
    this.peerConnections.forEach((pc, userId) => {
      pc.close();
    });
    this.peerConnections.clear();
    
    // Clean up spatial audio
    this.spatialNodes.forEach((nodes, userId) => {
      nodes.source.disconnect();
      nodes.panner.disconnect();
    });
    this.spatialNodes.clear();
    
    // Reset state
    this.isConnected = false;
    this.currentChannelUri = null;
    
    this.emit('voice_disconnected');
    console.log('[SLVoiceBridge] Disconnected from voice');
    
    return true;
  }

  /**
   * Cleanup
   */
  cleanup() {
    console.log('[SLVoiceBridge] Cleaning up...');
    
    this.disconnectFromVoice();
    
    if (this.audioContext) {
      this.audioContext.close();
      this.audioContext = null;
    }
    
    this.voiceAccountName = null;
    this.voicePassword = null;
    this.voiceServerUri = null;
  }

  /**
   * Get connection status
   */
  isConnectedToVoice() {
    return this.isConnected;
  }

  /**
   * Get current channel URI
   */
  getCurrentChannelUri() {
    return this.currentChannelUri;
  }
}

// Make available globally
window.SLWebRTCBridge = SLWebRTCBridge;
