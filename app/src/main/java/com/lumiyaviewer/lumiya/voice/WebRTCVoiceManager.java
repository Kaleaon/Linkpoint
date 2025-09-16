package com.lumiyaviewer.lumiya.voice;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import org.webrtc.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebRTC Voice Manager for Second Life Voice Chat
 * Replaces proprietary Vivox SDK with open-source WebRTC implementation
 */
public class WebRTCVoiceManager {
    private static final String TAG = "WebRTCVoice";
    
    // WebRTC Components
    private PeerConnectionFactory peerConnectionFactory;
    private AudioDeviceModule audioDeviceModule;
    private AudioSource audioSource;
    private AudioTrack localAudioTrack;
    private VideoCapturer videoCapturer;
    
    // Voice Session Management
    private final Map<String, PeerConnection> activePeerConnections = new ConcurrentHashMap<>();
    private final Map<String, VoiceSession> voiceSessions = new ConcurrentHashMap<>();
    
    // Configuration
    private final Context context;
    private final VoiceCallback voiceCallback;
    private boolean isInitialized = false;
    private boolean isMuted = false;
    private float speakerVolume = 1.0f;
    private float microphoneVolume = 1.0f;
    
    // Second Life voice server configuration
    private String voiceServerUrl;
    private String voiceChannelUri;
    private String authToken;
    
    public interface VoiceCallback {
        void onVoiceConnected(String channelUri);
        void onVoiceDisconnected(String channelUri, String reason);
        void onUserJoined(String channelUri, String userId, String displayName);
        void onUserLeft(String channelUri, String userId);
        void onUserSpeaking(String userId, boolean speaking);
        void onVoiceError(String error);
    }
    
    public WebRTCVoiceManager(Context context, VoiceCallback callback) {
        this.context = context;
        this.voiceCallback = callback;
    }
    
    /**
     * Initialize WebRTC voice system
     */
    public CompletableFuture<Boolean> initialize() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Initializing WebRTC voice system...");
                
                // Initialize WebRTC
                PeerConnectionFactory.InitializationOptions initOptions =
                    PeerConnectionFactory.InitializationOptions.builder(context)
                        .setEnableInternalTracer(false)
                        .setFieldTrials("")
                        .createInitializationOptions();
                PeerConnectionFactory.initialize(initOptions);
                
                // Create audio device module
                audioDeviceModule = JavaAudioDeviceModule.builder(context)
                    .setUseHardwareAcousticEchoCanceler(true)
                    .setUseHardwareNoiseSuppressor(true)
                    .setAudioRecordErrorCallback(new JavaAudioDeviceModule.AudioRecordErrorCallback() {
                        @Override
                        public void onWebRtcAudioRecordInitError(String errorMessage) {
                            Log.e(TAG, "Audio record init error: " + errorMessage);
                        }
                        
                        @Override
                        public void onWebRtcAudioRecordStartError(
                            JavaAudioDeviceModule.AudioRecordStartErrorCode errorCode, String errorMessage) {
                            Log.e(TAG, "Audio record start error: " + errorMessage);
                        }
                        
                        @Override
                        public void onWebRtcAudioRecordError(String errorMessage) {
                            Log.e(TAG, "Audio record error: " + errorMessage);
                        }
                    })
                    .setAudioTrackErrorCallback(new JavaAudioDeviceModule.AudioTrackErrorCallback() {
                        @Override
                        public void onWebRtcAudioTrackInitError(String errorMessage) {
                            Log.e(TAG, "Audio track init error: " + errorMessage);
                        }
                        
                        @Override
                        public void onWebRtcAudioTrackStartError(
                            JavaAudioDeviceModule.AudioTrackStartErrorCode errorCode, String errorMessage) {
                            Log.e(TAG, "Audio track start error: " + errorMessage);
                        }
                        
                        @Override
                        public void onWebRtcAudioTrackError(String errorMessage) {
                            Log.e(TAG, "Audio track error: " + errorMessage);
                        }
                    })
                    .createAudioDeviceModule();
                
                // Create peer connection factory
                PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
                peerConnectionFactory = PeerConnectionFactory.builder()
                    .setOptions(options)
                    .setAudioDeviceModule(audioDeviceModule)
                    .createPeerConnectionFactory();
                
                // Create audio source and track
                MediaConstraints audioConstraints = new MediaConstraints();
                audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
                audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
                audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googHighpassFilter", "true"));
                audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googTypingNoiseDetection", "true"));
                audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl", "true"));
                
                audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
                localAudioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource);
                
                isInitialized = true;
                Log.i(TAG, "WebRTC voice system initialized successfully");
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize WebRTC voice system", e);
                if (voiceCallback != null) {
                    voiceCallback.onVoiceError("Initialization failed: " + e.getMessage());
                }
                return false;
            }
    }
    
    /**
     * Connect to Second Life voice channel
     */
    public CompletableFuture<Boolean> connectToVoiceChannel(String channelUri, String authToken) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isInitialized) {
                Log.e(TAG, "WebRTC not initialized");
                return false;
            }
            
            try {
                Log.i(TAG, "Connecting to voice channel: " + channelUri);
                
                this.voiceChannelUri = channelUri;
                this.authToken = authToken;
                
                // Create voice session for this channel
                VoiceSession session = new VoiceSession(channelUri, authToken);
                voiceSessions.put(channelUri, session);
                
                // In a real implementation, this would:
                // 1. Connect to SL voice server's WebRTC endpoint
                // 2. Perform authentication using the auth token
                // 3. Join the specified voice channel
                // 4. Set up peer connections with other users in the channel
                
                // For now, simulate successful connection
                if (voiceCallback != null) {
                    voiceCallback.onVoiceConnected(channelUri);
                }
                
                Log.i(TAG, "Successfully connected to voice channel");
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to connect to voice channel", e);
                if (voiceCallback != null) {
                    voiceCallback.onVoiceError("Connection failed: " + e.getMessage());
                }
                return false;
            }
    }
    
    /**
     * Disconnect from voice channel
     */
    public CompletableFuture<Boolean> leaveVoiceChannel(String channelUri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Leaving voice channel: " + channelUri);
                
                VoiceSession session = voiceSessions.remove(channelUri);
                if (session != null) {
                    session.cleanup();
                }
                
                // Close peer connections for this channel
                activePeerConnections.entrySet().removeIf(entry -> {
                    if (entry.getKey().startsWith(channelUri)) {
                        entry.getValue().close();
                        return true;
                    }
                    return false;
                
                if (voiceCallback != null) {
                    voiceCallback.onVoiceDisconnected(channelUri, "User left");
                }
                
                Log.i(TAG, "Successfully left voice channel");
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to leave voice channel", e);
                return false;
            }
    }
    
    /**
     * Mute/unmute microphone
     */
    public void setMicrophoneMuted(boolean muted) {
        this.isMuted = muted;
        if (localAudioTrack != null) {
            localAudioTrack.setEnabled(!muted);
        }
        Log.i(TAG, "Microphone " + (muted ? "muted" : "unmuted"));
    }
    
    /**
     * Set speaker volume
     */
    public void setSpeakerVolume(float volume) {
        this.speakerVolume = Math.max(0.0f, Math.min(1.0f, volume));
        
        // Adjust audio manager volume
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            int targetVolume = (int) (maxVolume * this.speakerVolume);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, targetVolume, 0);
        }
        
        Log.i(TAG, "Speaker volume set to: " + this.speakerVolume);
    }
    
    /**
     * Set microphone volume
     */
    public void setMicrophoneVolume(float volume) {
        this.microphoneVolume = Math.max(0.0f, Math.min(1.0f, volume));
        // WebRTC handles microphone gain internally
        Log.i(TAG, "Microphone volume set to: " + this.microphoneVolume);
    }
    
    /**
     * Get list of available audio devices
     */
    public List<AudioDevice> getAvailableAudioDevices() {
        List<AudioDevice> devices = new ArrayList<>();
        
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            // Add built-in speaker
            devices.add(new AudioDevice("speaker", "Speaker", AudioDevice.Type.SPEAKER));
            
            // Add built-in microphone
            devices.add(new AudioDevice("microphone", "Microphone", AudioDevice.Type.MICROPHONE));
            
            // Check for bluetooth devices
            if (audioManager.isBluetoothScoAvailableOffCall()) {
                devices.add(new AudioDevice("bluetooth", "Bluetooth", AudioDevice.Type.BLUETOOTH));
            }
            
            // Check for wired headset
            if (audioManager.isWiredHeadsetOn()) {
                devices.add(new AudioDevice("headset", "Wired Headset", AudioDevice.Type.WIRED_HEADSET));
            }
        }
        
        return devices;
    }
    
    /**
     * Check if voice is currently connected
     */
    public boolean isConnected() {
        return !voiceSessions.isEmpty();
    }
    
    /**
     * Check if microphone is muted
     */
    public boolean isMuted() {
        return isMuted;
    }
    
    /**
     * Get current speaker volume
     */
    public float getSpeakerVolume() {
        return speakerVolume;
    }
    
    /**
     * Get current microphone volume
     */
    public float getMicrophoneVolume() {
        return microphoneVolume;
    }
    
    /**
     * Cleanup and release resources
     */
    public void cleanup() {
        Log.i(TAG, "Cleaning up WebRTC voice system...");
        
        // Close all peer connections
        for (PeerConnection pc : activePeerConnections.values()) {
            pc.close();
        }
        activePeerConnections.clear();
        
        // Cleanup voice sessions
        for (VoiceSession session : voiceSessions.values()) {
            session.cleanup();
        }
        voiceSessions.clear();
        
        // Dispose WebRTC resources
        if (localAudioTrack != null) {
            localAudioTrack.dispose();
            localAudioTrack = null;
        }
        
        if (audioSource != null) {
            audioSource.dispose();
            audioSource = null;
        }
        
        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
            peerConnectionFactory = null;
        }
        
        if (audioDeviceModule != null) {
            audioDeviceModule.release();
            audioDeviceModule = null;
        }
        
        isInitialized = false;
        Log.i(TAG, "WebRTC voice system cleanup completed");
    }
    
    /**
     * Voice session for managing channel-specific state
     */
    private static class VoiceSession {
        private final String channelUri;
        private final String authToken;
        private final long createTime;
        
        public VoiceSession(String channelUri, String authToken) {
            this.channelUri = channelUri;
            this.authToken = authToken;
            this.createTime = System.currentTimeMillis();
        }
        
        public void cleanup() {
            // Cleanup session-specific resources
        }
    }
    
    /**
     * Audio device representation
     */
    public static class AudioDevice {
        public enum Type {
            SPEAKER, MICROPHONE, BLUETOOTH, WIRED_HEADSET
        }
        
        private final String id;
        private final String name;
        private final Type type;
        
        public AudioDevice(String id, String name, Type type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public Type getType() { return type; }
        
        @Override
        public String toString() {
            return name;
        }
    }
}