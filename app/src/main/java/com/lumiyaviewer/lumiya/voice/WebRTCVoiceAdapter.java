package com.lumiyaviewer.lumiya.voice;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.CompletableFuture;

/**
 * WebRTC Voice Adapter for Lumiya
 * Bridges the existing Vivox-based voice system with WebRTC implementation
 * Provides drop-in replacement for Vivox functionality
 */
public class WebRTCVoiceAdapter {
    private static final String TAG = "WebRTCVoiceAdapter";
    
    private static WebRTCVoiceAdapter instance;
    private WebRTCVoiceManager webRTCManager;
    private Context context;
    private boolean isInitialized = false;
    
    // Voice state
    private String currentVoiceAccountName;
    private String currentVoicePassword;
    private String currentChannelUri;
    private boolean isConnectedToVoice = false;
    private boolean isMicrophoneMuted = false;
    private float speakerVolume = 1.0f;
    private float microphoneVolume = 1.0f;
    
    // Callback interface for voice events
    public interface VoiceAdapterCallback {
        void onVoiceInitialized(boolean success);
        void onVoiceChannelConnected(String channelUri);
        void onVoiceChannelDisconnected(String channelUri);
        void onVoiceUserJoined(String userId, String displayName);
        void onVoiceUserLeft(String userId);
        void onVoiceError(String error);
    }
    
    private VoiceAdapterCallback adapterCallback;
    
    private WebRTCVoiceAdapter(Context context) {
        this.context = context.getApplicationContext();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized WebRTCVoiceAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new WebRTCVoiceAdapter(context);
        }
        return instance;
    }
    
    /**
     * Set callback for voice events
     */
    public void setCallback(VoiceAdapterCallback callback) {
        this.adapterCallback = callback;
    }
    
    /**
     * Initialize WebRTC voice system
     * Replaces Vivox vx_initialize()
     */
    public CompletableFuture<Boolean> initialize() {
        if (isInitialized) {
            Log.i(TAG, "WebRTC voice adapter already initialized");
            return CompletableFuture.completedFuture(true);
        }
        
        Log.i(TAG, "Initializing WebRTC voice adapter...");
        
        webRTCManager = new WebRTCVoiceManager(context, new WebRTCVoiceManager.VoiceCallback() {
            @Override
            public void onVoiceConnected(String channelUri) {
                isConnectedToVoice = true;
                currentChannelUri = channelUri;
                if (adapterCallback != null) {
                    adapterCallback.onVoiceChannelConnected(channelUri);
                }
            }
            
            @Override
            public void onVoiceDisconnected(String channelUri, String reason) {
                isConnectedToVoice = false;
                currentChannelUri = null;
                if (adapterCallback != null) {
                    adapterCallback.onVoiceChannelDisconnected(channelUri);
                }
            }
            
            @Override
            public void onUserJoined(String channelUri, String userId, String displayName) {
                if (adapterCallback != null) {
                    adapterCallback.onVoiceUserJoined(userId, displayName);
                }
            }
            
            @Override
            public void onUserLeft(String channelUri, String userId) {
                if (adapterCallback != null) {
                    adapterCallback.onVoiceUserLeft(userId);
                }
            }
            
            @Override
            public void onUserSpeaking(String userId, boolean speaking) {
                // Can be used for UI indicators
            }
            
            @Override
            public void onVoiceError(String error) {
                if (adapterCallback != null) {
                    adapterCallback.onVoiceError(error);
                }
            }
        
        return webRTCManager.initialize()
            .thenApply(success -> {
                isInitialized = success;
                if (adapterCallback != null) {
                    adapterCallback.onVoiceInitialized(success);
                }
                Log.i(TAG, "WebRTC voice adapter initialization " + (success ? "successful" : "failed"));
                return success;
    }
    
    /**
     * Shutdown voice system
     * Replaces Vivox vx_uninitialize()
     */
    public void shutdown() {
        Log.i(TAG, "Shutting down WebRTC voice adapter...");
        
        if (webRTCManager != null) {
            webRTCManager.cleanup();
            webRTCManager = null;
        }
        
        isInitialized = false;
        isConnectedToVoice = false;
        currentChannelUri = null;
        currentVoiceAccountName = null;
        currentVoicePassword = null;
        
        Log.i(TAG, "WebRTC voice adapter shutdown completed");
    }
    
    /**
     * Account login for voice
     * Replaces Vivox vx_req_account_login
     */
    public CompletableFuture<Boolean> accountLogin(String accountName, String password, String serverUri) {
        Log.i(TAG, "Voice account login: " + accountName + " to " + serverUri);
        
        if (!isInitialized) {
            Log.e(TAG, "Voice adapter not initialized");
            return CompletableFuture.completedFuture(false);
        }
        
        this.currentVoiceAccountName = accountName;
        this.currentVoicePassword = password;
        
        // In a real implementation, this would authenticate with the voice server
        // For now, simulate successful login
        return CompletableFuture.completedFuture(true);
    }
    
    /**
     * Account logout
     * Replaces Vivox vx_req_account_logout
     */
    public CompletableFuture<Boolean> accountLogout() {
        Log.i(TAG, "Voice account logout");
        
        // Disconnect from any active channels first
        if (isConnectedToVoice && currentChannelUri != null) {
            sessionTerminate(currentChannelUri);
        }
        
        currentVoiceAccountName = null;
        currentVoicePassword = null;
        
        return CompletableFuture.completedFuture(true);
    }
    
    /**
     * Connect to voice channel
     * Replaces Vivox vx_req_session_create + vx_req_session_media_connect
     */
    public CompletableFuture<Boolean> sessionConnect(String channelUri, String authToken) {
        Log.i(TAG, "Connecting to voice session: " + channelUri);
        
        if (!isInitialized) {
            Log.e(TAG, "Voice adapter not initialized");
            return CompletableFuture.completedFuture(false);
        }
        
        if (webRTCManager == null) {
            Log.e(TAG, "WebRTC manager not available");
            return CompletableFuture.completedFuture(false);
        }
        
        return webRTCManager.connectToVoiceChannel(channelUri, authToken);
    }
    
    /**
     * Disconnect from voice channel
     * Replaces Vivox vx_req_session_terminate
     */
    public CompletableFuture<Boolean> sessionTerminate(String channelUri) {
        Log.i(TAG, "Terminating voice session: " + channelUri);
        
        if (webRTCManager == null) {
            return CompletableFuture.completedFuture(true);
        }
        
        return webRTCManager.leaveVoiceChannel(channelUri);
    }
    
    /**
     * Set local speaker volume
     * Replaces Vivox vx_req_connector_set_local_speaker_volume
     */
    public void setSpeakerVolume(float volume) {
        this.speakerVolume = Math.max(0.0f, Math.min(1.0f, volume));
        
        if (webRTCManager != null) {
            webRTCManager.setSpeakerVolume(this.speakerVolume);
        }
        
        Log.i(TAG, "Speaker volume set to: " + this.speakerVolume);
    }
    
    /**
     * Set local microphone volume
     * Replaces Vivox vx_req_connector_set_local_mic_volume
     */
    public void setMicrophoneVolume(float volume) {
        this.microphoneVolume = Math.max(0.0f, Math.min(1.0f, volume));
        
        if (webRTCManager != null) {
            webRTCManager.setMicrophoneVolume(this.microphoneVolume);
        }
        
        Log.i(TAG, "Microphone volume set to: " + this.microphoneVolume);
    }
    
    /**
     * Mute/unmute local microphone
     * Replaces Vivox vx_req_connector_mute_local_mic
     */
    public void setMicrophoneMuted(boolean muted) {
        this.isMicrophoneMuted = muted;
        
        if (webRTCManager != null) {
            webRTCManager.setMicrophoneMuted(muted);
        }
        
        Log.i(TAG, "Microphone " + (muted ? "muted" : "unmuted"));
    }
    
    /**
     * Check if voice system is initialized
     * Replaces Vivox vx_is_initialized
     */
    public boolean isVoiceInitialized() {
        return isInitialized;
    }
    
    /**
     * Check if connected to voice channel
     */
    public boolean isConnectedToChannel() {
        return isConnectedToVoice;
    }
    
    /**
     * Get current channel URI
     */
    public String getCurrentChannelUri() {
        return currentChannelUri;
    }
    
    /**
     * Check if microphone is muted
     */
    public boolean isMicrophoneMuted() {
        return isMicrophoneMuted;
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
     * Process voice credentials from Second Life
     * This handles SL-specific voice authentication and channel setup
     */
    public CompletableFuture<Boolean> processSecondLifeVoiceCredentials(
            String slVoiceUser, String slVoicePassword, String slVoiceServer, String channelUri) {
        
        Log.i(TAG, "Processing Second Life voice credentials...");
        Log.i(TAG, "Voice user: " + slVoiceUser);
        Log.i(TAG, "Voice server: " + slVoiceServer);
        Log.i(TAG, "Channel URI: " + channelUri);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: Account login with SL voice credentials
                boolean accountLoginSuccess = accountLogin(slVoiceUser, slVoicePassword, slVoiceServer).join();
                if (!accountLoginSuccess) {
                    Log.e(TAG, "Voice account login failed");
                    return false;
                }
                
                // Step 2: Connect to the voice channel
                boolean channelConnectSuccess = sessionConnect(channelUri, null).join();
                if (!channelConnectSuccess) {
                    Log.e(TAG, "Voice channel connection failed");
                    return false;
                }
                
                Log.i(TAG, "Second Life voice credentials processed successfully");
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to process SL voice credentials", e);
                return false;
            }
    }
    
    /**
     * Get build version info
     * Replaces Vivox BUILD_VERSION_get, BUILD_DATE_get, etc.
     */
    public String getBuildVersion() {
        return "WebRTC-1.0";
    }
    
    public String getBuildDate() {
        return "2024-09-13";
    }
    
    public String getBuildHost() {
        return "Linkpoint-WebRTC";
    }
}