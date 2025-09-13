package com.lumiyaviewer.lumiya.voice;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Bridge between Second Life voice system and WebRTC implementation
 * Handles SL-specific voice server communication and authentication
 */
public class SecondLifeWebRTCBridge {
    private static final String TAG = "SLWebRTCBridge";
    
    private final Context context;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final WebRTCVoiceAdapter voiceAdapter;
    
    // Second Life voice server endpoints
    private static final String SL_VOICE_PROVISION_URL = "https://cap.secondlife.com/cap/";
    private static final String SL_VOICE_ACCOUNT_URL = "https://bhr.vivox.com/api2/viv_signin.php";
    
    // Voice session state
    private String voiceAccountName;
    private String voicePassword;
    private String voiceServerUri;
    private String currentChannelUri;
    
    public SecondLifeWebRTCBridge(Context context) {
        this.context = context;
        this.gson = new Gson();
        this.httpClient = new OkHttpClient.Builder()
            .addInterceptor(new LoggingInterceptor())
            .build();
        this.voiceAdapter = WebRTCVoiceAdapter.getInstance(context);
    }
    
    /**
     * Process Second Life voice credentials and establish voice connection
     * This is called when SL sends voice credentials to the client
     */
    public CompletableFuture<Boolean> processSecondLifeVoiceCredentials(
            String slSessionId, String slVoiceUser, String slVoicePassword, 
            String slVoiceServer, String channelUri) {
        
        Log.i(TAG, "Processing Second Life voice credentials...");
        Log.i(TAG, "SL Session ID: " + slSessionId);
        Log.i(TAG, "Voice User: " + slVoiceUser);
        Log.i(TAG, "Voice Server: " + slVoiceServer);
        Log.i(TAG, "Channel URI: " + channelUri);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: Store credentials
                this.voiceAccountName = slVoiceUser;
                this.voicePassword = slVoicePassword;
                this.voiceServerUri = slVoiceServer;
                this.currentChannelUri = channelUri;
                
                // Step 2: Initialize WebRTC voice adapter if not already done
                if (!voiceAdapter.isVoiceInitialized()) {
                    Log.i(TAG, "Initializing WebRTC voice adapter...");
                    boolean initSuccess = voiceAdapter.initialize().join();
                    if (!initSuccess) {
                        Log.e(TAG, "Failed to initialize WebRTC voice adapter");
                        return false;
                    }
                }
                
                // Step 3: Authenticate with voice server
                Log.i(TAG, "Authenticating with voice server...");
                boolean authSuccess = authenticateWithVoiceServer().join();
                if (!authSuccess) {
                    Log.e(TAG, "Voice server authentication failed");
                    return false;
                }
                
                // Step 4: Connect to voice channel
                Log.i(TAG, "Connecting to voice channel...");
                boolean connectSuccess = connectToVoiceChannel().join();
                if (!connectSuccess) {
                    Log.e(TAG, "Voice channel connection failed");
                    return false;
                }
                
                Log.i(TAG, "Second Life voice connection established successfully");
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to process SL voice credentials", e);
                return false;
            }
        });
    }
    
    /**
     * Authenticate with Second Life voice server
     */
    private CompletableFuture<Boolean> authenticateWithVoiceServer() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Authenticating with SL voice server: " + voiceServerUri);
                
                // Create authentication request
                JsonObject authRequest = new JsonObject();
                authRequest.addProperty("user", voiceAccountName);
                authRequest.addProperty("password", voicePassword);
                authRequest.addProperty("method", "get_account_info");
                
                RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), 
                    gson.toJson(authRequest)
                );
                
                Request request = new Request.Builder()
                    .url(voiceServerUri + "/viv_signin.php")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "Lumiya/3.4.3 (WebRTC)")
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, "Voice server authentication successful");
                        Log.d(TAG, "Auth response: " + responseBody);
                        
                        // Process authentication response
                        return processAuthResponse(responseBody);
                    } else {
                        Log.e(TAG, "Voice server authentication failed with code: " + response.code());
                        return false;
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Voice server authentication error", e);
                return false;
            }
        });
    }
    
    /**
     * Process authentication response from voice server
     */
    private boolean processAuthResponse(String responseBody) {
        try {
            JsonObject response = gson.fromJson(responseBody, JsonObject.class);
            
            if (response.has("status") && "success".equals(response.get("status").getAsString())) {
                Log.i(TAG, "Voice authentication successful");
                
                // Extract additional voice server information if available
                if (response.has("server_uri")) {
                    String serverUri = response.get("server_uri").getAsString();
                    Log.i(TAG, "Voice server URI: " + serverUri);
                }
                
                return true;
            } else {
                Log.e(TAG, "Voice authentication failed: " + responseBody);
                return false;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to process auth response", e);
            return false;
        }
    }
    
    /**
     * Connect to the voice channel using WebRTC
     */
    private CompletableFuture<Boolean> connectToVoiceChannel() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Connecting to voice channel: " + currentChannelUri);
                
                // Use WebRTC adapter to connect to channel
                return voiceAdapter.sessionConnect(currentChannelUri, voicePassword).join();
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to connect to voice channel", e);
                return false;
            }
        });
    }
    
    /**
     * Disconnect from current voice channel
     */
    public CompletableFuture<Boolean> disconnectFromVoice() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Disconnecting from voice...");
                
                if (currentChannelUri != null) {
                    boolean success = voiceAdapter.sessionTerminate(currentChannelUri).join();
                    if (success) {
                        Log.i(TAG, "Successfully disconnected from voice channel");
                    }
                    currentChannelUri = null;
                    return success;
                }
                
                return true; // Already disconnected
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to disconnect from voice", e);
                return false;
            }
        });
    }
    
    /**
     * Handle voice server URLs from Second Life CAPS system
     */
    public CompletableFuture<Boolean> processVoiceServerCaps(String capsUrl, String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Processing voice server CAPS: " + capsUrl);
                
                // Create CAPS request for voice provisioning
                JsonObject capsRequest = new JsonObject();
                capsRequest.addProperty("session_id", sessionId);
                capsRequest.addProperty("method", "provision_voice");
                
                RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), 
                    gson.toJson(capsRequest)
                );
                
                Request request = new Request.Builder()
                    .url(capsUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "Lumiya/3.4.3 (WebRTC)")
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, "Voice CAPS response received");
                        Log.d(TAG, "CAPS response: " + responseBody);
                        
                        return processVoiceCapsResponse(responseBody);
                    } else {
                        Log.e(TAG, "Voice CAPS request failed with code: " + response.code());
                        return false;
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Voice CAPS processing error", e);
                return false;
            }
        });
    }
    
    /**
     * Process voice CAPS response
     */
    private boolean processVoiceCapsResponse(String responseBody) {
        try {
            JsonObject response = gson.fromJson(responseBody, JsonObject.class);
            
            if (response.has("voice_server_uri")) {
                String voiceServerUri = response.get("voice_server_uri").getAsString();
                Log.i(TAG, "Voice server URI from CAPS: " + voiceServerUri);
                this.voiceServerUri = voiceServerUri;
            }
            
            if (response.has("voice_channel_uri")) {
                String channelUri = response.get("voice_channel_uri").getAsString();
                Log.i(TAG, "Voice channel URI from CAPS: " + channelUri);
                this.currentChannelUri = channelUri;
            }
            
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to process voice CAPS response", e);
            return false;
        }
    }
    
    /**
     * Get current voice connection status
     */
    public boolean isConnectedToVoice() {
        return voiceAdapter.isConnectedToChannel();
    }
    
    /**
     * Get current voice channel URI
     */
    public String getCurrentChannelUri() {
        return currentChannelUri;
    }
    
    /**
     * Cleanup voice bridge resources
     */
    public void cleanup() {
        Log.i(TAG, "Cleaning up Second Life WebRTC bridge...");
        
        // Disconnect from voice if connected
        if (isConnectedToVoice()) {
            disconnectFromVoice();
        }
        
        // Clear state
        voiceAccountName = null;
        voicePassword = null;
        voiceServerUri = null;
        currentChannelUri = null;
        
        Log.i(TAG, "Second Life WebRTC bridge cleanup completed");
    }
    
    /**
     * HTTP logging interceptor for debugging
     */
    private static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.d(TAG, "HTTP Request: " + request.method() + " " + request.url());
            
            Response response = chain.proceed(request);
            Log.d(TAG, "HTTP Response: " + response.code() + " " + response.message());
            
            return response;
        }
    }
}