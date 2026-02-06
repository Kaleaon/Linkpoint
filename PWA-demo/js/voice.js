/**
 * Linkpoint PWA - Voice Chat Module (WebRTC)
 * Based on Linkpoint Android voice implementation
 */

class VoiceManager extends Utils.EventEmitter {
  constructor(protocolManager, authManager) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
    this.enabled = false;
    this.muted = false;
    this.volume = 1.0;
    this.localStream = null;
    this.peerConnections = new Map();
    this.spatialAudioEnabled = true;
    this.audioContext = null;
    this.voiceChannel = null;
    this.slVoiceBridge = null; // SL-specific voice bridge
  }

  /**
   * Initialize voice chat
   */
  async init() {
    // Check WebRTC support
    if (!this.isWebRTCSupported()) {
      console.warn('WebRTC not supported');
      return false;
    }

    // Create audio context for spatial audio
    try {
      this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
      console.log('Audio context created');
    } catch (error) {
      console.error('Failed to create audio context:', error);
    }

    // Initialize SL WebRTC bridge
    if (window.SLWebRTCBridge) {
      this.slVoiceBridge = new SLWebRTCBridge(this.protocol);
      await this.slVoiceBridge.init(this);
      
      // Forward bridge events
      this.slVoiceBridge.on('voice_connected', () => {
        this.emit('voice_connected');
      });
      
      this.slVoiceBridge.on('voice_disconnected', () => {
        this.emit('voice_disconnected');
      });
      
      this.slVoiceBridge.on('user_joined', (data) => {
        this.emit('voice_participant_joined', data);
      });
      
      this.slVoiceBridge.on('user_left', (data) => {
        this.emit('voice_participant_left', data);
      });
      
      console.log('[Voice] SL WebRTC bridge initialized');
    }

    // Setup protocol listeners
    this.protocol.on('voice_invite', (data) => this.handleVoiceInvite(data));
    this.protocol.on('voice_participant_joined', (data) => this.handleParticipantJoined(data));
    this.protocol.on('voice_participant_left', (data) => this.handleParticipantLeft(data));

    return true;
  }

  /**
   * Check WebRTC support
   */
  isWebRTCSupported() {
    return !!(navigator.mediaDevices &&
              navigator.mediaDevices.getUserMedia &&
              window.RTCPeerConnection);
  }

  /**
   * Enable voice chat
   */
  async enable() {
    if (this.enabled) return true;

    try {
      // Request microphone permission
      this.localStream = await navigator.mediaDevices.getUserMedia({
        audio: {
          echoCancellation: true,
          noiseSuppression: true,
          autoGainControl: true,
          sampleRate: 48000
        }
      });

      console.log('Microphone access granted');
      this.enabled = true;
      this.emit('voice_enabled');
      Utils.showToast('Voice chat enabled', 'success');
      return true;
    } catch (error) {
      console.error('Failed to enable voice:', error);
      Utils.showToast('Microphone permission denied', 'error');
      this.emit('voice_error', error);
      return false;
    }
  }

  /**
   * Disable voice chat
   */
  disable() {
    if (!this.enabled) return;

    // Stop local stream
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => track.stop());
      this.localStream = null;
    }

    // Close all peer connections
    this.peerConnections.forEach((pc, userId) => {
      pc.close();
    });
    this.peerConnections.clear();

    this.enabled = false;
    this.emit('voice_disabled');
    Utils.showToast('Voice chat disabled', 'info');
  }

  /**
   * Join voice channel
   */
  async joinChannel(channelId, channelName) {
    if (!this.enabled) {
      await this.enable();
    }

    this.voiceChannel = {
      id: channelId,
      name: channelName,
      participants: []
    };

    // Notify server
    this.protocol.sendMessage('VoiceJoinChannel', {
      channel_id: channelId,
      channel_name: channelName
    });

    this.emit('channel_joined', this.voiceChannel);
    Utils.showToast(`Joined voice channel: ${channelName}`, 'success');
  }

  /**
   * Leave voice channel
   */
  leaveChannel() {
    if (!this.voiceChannel) return;

    const channelName = this.voiceChannel.name;

    // Notify server
    this.protocol.sendMessage('VoiceLeaveChannel', {
      channel_id: this.voiceChannel.id
    });

    // Disconnect all participants
    this.peerConnections.forEach((pc) => pc.close());
    this.peerConnections.clear();

    this.voiceChannel = null;
    this.emit('channel_left');
    Utils.showToast(`Left voice channel: ${channelName}`, 'info');
  }

  /**
   * Toggle mute
   */
  toggleMute() {
    if (!this.localStream) return;

    this.muted = !this.muted;
    this.localStream.getAudioTracks().forEach(track => {
      track.enabled = !this.muted;
    });

    this.emit('mute_changed', this.muted);
    Utils.showToast(this.muted ? 'Muted' : 'Unmuted', 'info');
    return this.muted;
  }

  /**
   * Set volume
   */
  setVolume(volume) {
    this.volume = Utils.clamp(volume, 0, 1);
    this.emit('volume_changed', this.volume);
  }

  /**
   * Enable/disable spatial audio
   */
  setSpatialAudio(enabled) {
    this.spatialAudioEnabled = enabled;
    this.emit('spatial_audio_changed', enabled);
  }

  /**
   * Update listener position for spatial audio
   */
  updateListenerPosition(x, y, z) {
    if (!this.audioContext || !this.spatialAudioEnabled) return;

    const listener = this.audioContext.listener;
    if (listener.positionX) {
      listener.positionX.value = x;
      listener.positionY.value = y;
      listener.positionZ.value = z;
    } else {
      listener.setPosition(x, y, z);
    }
  }

  /**
   * Handle voice invite
   */
  handleVoiceInvite(data) {
    this.emit('voice_invite_received', data);
  }

  /**
   * Handle participant joined
   */
  async handleParticipantJoined(data) {
    const userId = data.user_id;
    
    // Create peer connection
    const pc = new RTCPeerConnection({
      iceServers: [
        { urls: 'stun:stun.l.google.com:19302' },
        { urls: 'stun:stun1.l.google.com:19302' }
      ]
    });

    // Add local stream
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => {
        pc.addTrack(track, this.localStream);
      });
    }

    // Handle remote stream
    pc.ontrack = (event) => {
      this.handleRemoteTrack(userId, event.streams[0], data);
    };

    // Handle ICE candidates
    pc.onicecandidate = (event) => {
      if (event.candidate) {
        this.protocol.sendMessage('VoiceICECandidate', {
          user_id: userId,
          candidate: event.candidate
        });
      }
    };

    this.peerConnections.set(userId, pc);

    // Create and send offer
    const offer = await pc.createOffer();
    await pc.setLocalDescription(offer);
    
    this.protocol.sendMessage('VoiceOffer', {
      user_id: userId,
      offer: offer
    });

    this.emit('participant_joined', data);
  }

  /**
   * Handle participant left
   */
  handleParticipantLeft(data) {
    const userId = data.user_id;
    
    const pc = this.peerConnections.get(userId);
    if (pc) {
      pc.close();
      this.peerConnections.delete(userId);
    }

    this.emit('participant_left', data);
  }

  /**
   * Handle remote audio track
   */
  handleRemoteTrack(userId, stream, userData) {
    // Create audio element
    const audio = new Audio();
    audio.srcObject = stream;
    audio.autoplay = true;
    audio.volume = this.volume;

    // Apply spatial audio if enabled
    if (this.spatialAudioEnabled && this.audioContext && userData.position) {
      const source = this.audioContext.createMediaStreamSource(stream);
      const panner = this.audioContext.createPanner();
      
      panner.panningModel = 'HRTF';
      panner.distanceModel = 'inverse';
      panner.refDistance = 1;
      panner.maxDistance = 100;
      panner.rolloffFactor = 1;
      
      // Set position
      if (panner.positionX) {
        panner.positionX.value = userData.position.x;
        panner.positionY.value = userData.position.y;
        panner.positionZ.value = userData.position.z;
      } else {
        panner.setPosition(
          userData.position.x,
          userData.position.y,
          userData.position.z
        );
      }

      source.connect(panner);
      panner.connect(this.audioContext.destination);
    }

    this.emit('remote_track_received', { userId, stream });
  }

  /**
   * Get active participants
   */
  getParticipants() {
    return Array.from(this.peerConnections.keys());
  }

  /**
   * Check if voice is enabled
   */
  isEnabled() {
    return this.enabled;
  }

  /**
   * Check if muted
   */
  isMuted() {
    return this.muted;
  }

  /**
   * Get current volume
   */
  getVolume() {
    return this.volume;
  }

  /**
   * Clean up
   */
  destroy() {
    this.disable();
    if (this.audioContext) {
      this.audioContext.close();
    }
  }
}

// Make available globally
window.VoiceManager = VoiceManager;
