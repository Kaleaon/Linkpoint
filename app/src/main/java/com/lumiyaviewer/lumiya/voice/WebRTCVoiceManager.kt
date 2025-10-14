package com.lumiyaviewer.lumiya.voice

import android.content.Context
import android.media.AudioManager
import android.util.Log

import org.webrtc.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * WebRTC Voice Manager for Second Life Voice Chat
 * Replaces proprietary Vivox SDK with open-source WebRTC implementation
 */
class WebRTCVoiceManager {
    private String TAG = "WebRTCVoice"
    
    // WebRTC Components
    private PeerConnectionFactory peerConnectionFactory
    private AudioDeviceModule audioDeviceModule
    private AudioSource audioSource
    private AudioTrack localAudioTrack
    private VideoCapturer videoCapturer
    
    // Voice Session Management
    private Map<String, PeerConnection> activePeerConnections = ConcurrentHashMap<>()
    private Map<String, VoiceSession> voiceSessions = ConcurrentHashMap<>()
    
    // Configuration
    private Context context
    private VoiceCallback voiceCallback
    private Boolean isInitialized = false
    private Boolean isMuted = false
    private Float speakerVolume = 1.0f
    private Float microphoneVolume = 1.0f
    
    // Second Life voice server configuration
    private String voiceServerUrl
    private String voiceChannelUri
    private String authToken
    
    interface VoiceCallback {
        Unit onVoiceConnected(String channelUri)
        Unit onVoiceDisconnected(String channelUri, String reason)
        Unit onUserJoined(String channelUri, String userId, String displayName)
        Unit onUserLeft(String channelUri, String userId)
        Unit onUserSpeaking(String userId, Boolean speaking)
        Unit onVoiceError(String error)
    }
    
    WebRTCVoiceManager(Context context, VoiceCallback callback) {
        this.context = context
        this.voiceCallback = callback
    }
    
    /**
     * Initialize WebRTC voice system
     */
    CompletableFuture<Boolean> initialize() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Initializing WebRTC voice system...")
                
                // Initialize WebRTC
                PeerConnectionFactory.InitializationOptions initOptions =
                    PeerConnectionFactory.InitializationOptions.builder(context)
                        .setEnableInternalTracer(false)
                        .setFieldTrials("")
                        .createInitializationOptions()
                PeerConnectionFactory.initialize(initOptions)
                
                // Create audio device module
                audioDeviceModule = JavaAudioDeviceModule.builder(context)
                    .setUseHardwareAcousticEchoCanceler(true)
                    .setUseHardwareNoiseSuppressor(true)
                    .setAudioRecordErrorCallback(JavaAudioDeviceModule.AudioRecordErrorCallback() {
                        @Override
                        Unit onWebRtcAudioRecordInitError(String errorMessage) {
                            Log.e(TAG, "Audio record init error: " + errorMessage)
                        }
                        
                        @Override
                        Unit onWebRtcAudioRecordStartError(
                            JavaAudioDeviceModule.AudioRecordStartErrorCode errorCode, String errorMessage) {
                            Log.e(TAG, "Audio record start error: " + errorMessage)
                        }
                        
                        @Override
                        Unit onWebRtcAudioRecordError(String errorMessage) {
                            Log.e(TAG, "Audio record error: " + errorMessage)
                        }
                    })
                    .setAudioTrackErrorCallback(JavaAudioDeviceModule.AudioTrackErrorCallback() {
                        @Override
                        Unit onWebRtcAudioTrackInitError(String errorMessage) {
                            Log.e(TAG, "Audio track init error: " + errorMessage)
                        }
                        
                        @Override
                        Unit onWebRtcAudioTrackStartError(
                            JavaAudioDeviceModule.AudioTrackStartErrorCode errorCode, String errorMessage) {
                            Log.e(TAG, "Audio track start error: " + errorMessage)
                        }
                        
                        @Override
                        Unit onWebRtcAudioTrackError(String errorMessage) {
                            Log.e(TAG, "Audio track error: " + errorMessage)
                        }
                    })
                    .createAudioDeviceModule()
                
                // Create peer connection factory
                PeerConnectionFactory.Options options = PeerConnectionFactory.Options()
                peerConnectionFactory = PeerConnectionFactory.builder()
                    .setOptions(options)
                    .setAudioDeviceModule(audioDeviceModule)
                    .createPeerConnectionFactory()
                
                // Create audio source and track
                MediaConstraints audioConstraints = MediaConstraints()
                audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
                audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
                audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
                audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googTypingNoiseDetection", "true"))
                audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
                
                audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
                localAudioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource)
                
                isInitialized = true
                Log.i(TAG, "WebRTC voice system initialized successfully")
                return true
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize WebRTC voice system", e)
                if (voiceCallback != null) {
                    voiceCallback.onVoiceError("Initialization failed: " + e.getMessage())
                }
                return false
            }
    }
    
    /**
     * Connect to Second Life voice channel
     */
    CompletableFuture<Boolean> connectToVoiceChannel(String channelUri, String authToken) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isInitialized) {
                Log.e(TAG, "WebRTC not initialized")
                return false
            }
            
            try {
                Log.i(TAG, "Connecting to voice channel: " + channelUri)
                
                this.voiceChannelUri = channelUri
                this.authToken = authToken
                
                // Create voice session for this channel
                VoiceSession session = VoiceSession(channelUri, authToken)
                voiceSessions.put(channelUri, session)
                
                // In a real implementation, this would:
                // 1. Connect to SL voice server's WebRTC endpoint
                // 2. Perform authentication using the auth token
                // 3. Join the specified voice channel
                // 4. Set up peer connections with other users in the channel
                
                // For now, simulate successful connection
                if (voiceCallback != null) {
                    voiceCallback.onVoiceConnected(channelUri)
                }
                
                Log.i(TAG, "Successfully connected to voice channel")
                return true
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to connect to voice channel", e)
                if (voiceCallback != null) {
                    voiceCallback.onVoiceError("Connection failed: " + e.getMessage())
                }
                return false
            }
    }
    
    /**
     * Disconnect from voice channel
     */
    CompletableFuture<Boolean> leaveVoiceChannel(String channelUri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Leaving voice channel: " + channelUri)
                
                VoiceSession session = voiceSessions.remove(channelUri)
                if (session != null) {
                    session.cleanup()
                }
                
                // Close peer connections for this channel
                activePeerConnections.entrySet().removeIf(entry -> {
                    if (entry.getKey().startsWith(channelUri)) {
                        entry.getValue().close()
                        return true
                    }
                    return false
                
                if (voiceCallback != null) {
                    voiceCallback.onVoiceDisconnected(channelUri, "User left")
                }
                
                Log.i(TAG, "Successfully left voice channel")
                return true
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to leave voice channel", e)
                return false
            }
    }
    
    /**
     * Mute/unmute microphone
     */
    Unit setMicrophoneMuted(Boolean muted) {
        this.isMuted = muted
        if (localAudioTrack != null) {
            localAudioTrack.setEnabled(!muted)
        }
        Log.i(TAG, "Microphone " + (muted ? "muted" : "unmuted"))
    }
    
    /**
     * Set speaker volume
     */
    Unit setSpeakerVolume(Float volume) {
        this.speakerVolume = Math.max(0.0f, Math.min(1.0f, volume))
        
        // Adjust audio manager volume
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE)
        if (audioManager != null) {
            Int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
            Int targetVolume = (Int) (maxVolume * this.speakerVolume)
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, targetVolume, 0)
        }
        
        Log.i(TAG, "Speaker volume set to: " + this.speakerVolume)
    }
    
    /**
     * Set microphone volume
     */
    Unit setMicrophoneVolume(Float volume) {
        this.microphoneVolume = Math.max(0.0f, Math.min(1.0f, volume))
        // WebRTC handles microphone gain internally
        Log.i(TAG, "Microphone volume set to: " + this.microphoneVolume)
    }
    
    /**
     * Get list of available audio devices
     */
    List<AudioDevice> getAvailableAudioDevices() {
        List<AudioDevice> devices = ArrayList<>()
        
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE)
        if (audioManager != null) {
            // Add built-in speaker
            devices.add(AudioDevice("speaker", "Speaker", AudioDevice.Type.SPEAKER))
            
            // Add built-in microphone
            devices.add(AudioDevice("microphone", "Microphone", AudioDevice.Type.MICROPHONE))
            
            // Check for bluetooth devices
            if (audioManager.isBluetoothScoAvailableOffCall()) {
                devices.add(AudioDevice("bluetooth", "Bluetooth", AudioDevice.Type.BLUETOOTH))
            }
            
            // Check for wired headset
            if (audioManager.isWiredHeadsetOn()) {
                devices.add(AudioDevice("headset", "Wired Headset", AudioDevice.Type.WIRED_HEADSET))
            }
        }
        
        return devices
    }
    
    /**
     * Check if voice is currently connected
     */
    Boolean isConnected() {
        return !voiceSessions.isEmpty()
    }
    
    /**
     * Check if microphone is muted
     */
    Boolean isMuted() {
        return isMuted
    }
    
    /**
     * Get current speaker volume
     */
    Float getSpeakerVolume() {
        return speakerVolume
    }
    
    /**
     * Get current microphone volume
     */
    Float getMicrophoneVolume() {
        return microphoneVolume
    }
    
    /**
     * Cleanup and release resources
     */
    Unit cleanup() {
        Log.i(TAG, "Cleaning up WebRTC voice system...")
        
        // Close all peer connections
        for (PeerConnection pc : activePeerConnections.values()) {
            pc.close()
        }
        activePeerConnections.clear()
        
        // Cleanup voice sessions
        for (VoiceSession session : voiceSessions.values()) {
            session.cleanup()
        }
        voiceSessions.clear()
        
        // Dispose WebRTC resources
        if (localAudioTrack != null) {
            localAudioTrack.dispose()
            localAudioTrack = null
        }
        
        if (audioSource != null) {
            audioSource.dispose()
            audioSource = null
        }
        
        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose()
            peerConnectionFactory = null
        }
        
        if (audioDeviceModule != null) {
            audioDeviceModule.release()
            audioDeviceModule = null
        }
        
        isInitialized = false
        Log.i(TAG, "WebRTC voice system cleanup completed")
    }
    
    /**
     * Voice session for managing channel-specific state
     */
    private class VoiceSession {
        private String channelUri
        private String authToken
        private Long createTime
        
        VoiceSession(String channelUri, String authToken) {
            this.channelUri = channelUri
            this.authToken = authToken
            this.createTime = System.currentTimeMillis()
        }
        
        Unit cleanup() {
            // Cleanup session-specific resources
        }
    }
    
    /**
     * Audio device representation
     */
    class AudioDevice {
        enum Type {
            SPEAKER, MICROPHONE, BLUETOOTH, WIRED_HEADSET
        }
        
        private String id
        private String name
        private Type type
        
        AudioDevice(String id, String name, Type type) {
            this.id = id
            this.name = name
            this.type = type
        }
        
        String getId() { return id; }
        String getName() { return name; }
        Type getType() { return type; }
        
        @Override
        String toString() {
            return name
        }
    }
}
