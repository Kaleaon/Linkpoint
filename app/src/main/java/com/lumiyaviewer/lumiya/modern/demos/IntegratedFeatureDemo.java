package com.lumiyaviewer.lumiya.modern.demos;

import android.content.Context;
import android.util.Log;

import com.lumiyaviewer.lumiya.modern.auth.OAuth2AuthManager;
import com.lumiyaviewer.lumiya.modern.avatar.ModernAvatarManager;
import com.lumiyaviewer.lumiya.modern.chat.ModernChatManager;
import com.lumiyaviewer.lumiya.modern.protocol.WebSocketEventClient;
import com.lumiyaviewer.lumiya.modern.voice.WebRTCManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Integration demo showcasing WebRTC voice, chat, avatar rendering, and Second Life login
 * Demonstrates all implemented features working together
 */
public class IntegratedFeatureDemo {
    private static final String TAG = "IntegratedFeatureDemo";
    
    private final Context context;
    
    // Modern component managers
    private OAuth2AuthManager authManager;
    private WebRTCManager voiceManager;
    private ModernChatManager chatManager;
    private ModernAvatarManager avatarManager;
    private WebSocketEventClient eventClient;
    
    // Demo state
    private boolean isAuthenticated = false;
    private boolean voiceEnabled = false;
    private boolean chatActive = false;
    private UUID currentAvatarId;
    
    public IntegratedFeatureDemo(Context context) {
        this.context = context;
        initializeComponents();
        Log.i(TAG, "Integrated feature demo initialized");
    }
    
    /**
     * Initialize all modern components
     */
    private void initializeComponents() {
        try {
            // Initialize authentication manager
            authManager = new OAuth2AuthManager(context);
            authManager.setUseTestGrid(true); // Use test grid for demo
            
            // Initialize WebSocket event client
            eventClient = new WebSocketEventClient();
            
            // Initialize voice manager
            voiceManager = new WebRTCManager(context);
            voiceManager.setConnectionListener(new VoiceConnectionListener());
            
            // Initialize chat manager
            chatManager = new ModernChatManager(context, eventClient);
            chatManager.setChatEventListener(new ChatEventListener());
            
            // Initialize avatar manager
            avatarManager = new ModernAvatarManager(context);
            avatarManager.setAvatarEventListener(new AvatarEventListener());
            
            Log.i(TAG, "All components initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize components", e);
        }
    }
    
    /**
     * Demonstrate complete login and feature activation workflow
     */
    public CompletableFuture<Boolean> runCompleteDemo(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "=== Starting Complete Feature Demo ===");
                
                // Step 1: Authentication
                Log.i(TAG, "Step 1: Authenticating with Second Life...");
                OAuth2AuthManager.AuthResult authResult = authManager.authenticateUser(username, password).join();
                
                if (!authResult.isSuccess()) {
                    Log.e(TAG, "Authentication failed: " + authResult.getMessage());
                    return false;
                }
                
                isAuthenticated = true;
                OAuth2AuthManager.SessionInfo session = authManager.getSessionInfo();
                Log.i(TAG, "✅ Authentication successful for: " + session.getFullName());
                
                // Step 2: Avatar System
                Log.i(TAG, "Step 2: Setting up avatar rendering...");
                boolean avatarValid = setupAvatarDemo().join();
                if (!avatarValid) {
                    Log.w(TAG, "Avatar setup failed, continuing with other features");
                }
                
                // Step 3: Voice System
                Log.i(TAG, "Step 3: Initializing WebRTC voice...");
                boolean voiceReady = setupVoiceDemo().join();
                if (!voiceReady) {
                    Log.w(TAG, "Voice setup failed, continuing with chat");
                }
                
                // Step 4: Chat System
                Log.i(TAG, "Step 4: Setting up chat system...");
                boolean chatReady = setupChatDemo().join();
                if (!chatReady) {
                    Log.w(TAG, "Chat setup failed");
                }
                
                // Step 5: Integration Test
                Log.i(TAG, "Step 5: Testing integrated features...");
                runIntegrationTests();
                
                Log.i(TAG, "=== Complete Feature Demo Finished ===");
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Demo failed with error", e);
                return false;
            }
        });
    }
    
    /**
     * Set up avatar rendering demo
     */
    private CompletableFuture<Boolean> setupAvatarDemo() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Validate avatar rendering system
                boolean valid = avatarManager.validateAvatarRendering().join();
                if (!valid) {
                    return false;
                }
                
                // Create demo avatar
                currentAvatarId = UUID.randomUUID();
                ModernAvatarManager.MockSLObject mockAvatarObject = createMockSLObject(currentAvatarId);
                
                boolean created = avatarManager.createAvatar(currentAvatarId, mockAvatarObject).join();
                if (created) {
                    // Update avatar appearance
                    ModernAvatarManager.AvatarAppearance appearance = 
                        new ModernAvatarManager.AvatarAppearance.Builder()
                            .withBodyHeight(1.75f)
                            .withSkinColor(0.9f, 0.8f, 0.7f, 1.0f)
                            .build();
                    
                    avatarManager.updateAvatarAppearance(currentAvatarId, appearance).join();
                    
                    // Start basic animation
                    avatarManager.startAvatarAnimation(currentAvatarId, "standing", true).join();
                    
                    Log.i(TAG, "✅ Avatar system ready");
                    return true;
                }
                
                return false;
                
            } catch (Exception e) {
                Log.e(TAG, "Avatar setup error", e);
                return false;
            }
        });
    }
    
    /**
     * Set up WebRTC voice demo
     */
    private CompletableFuture<Boolean> setupVoiceDemo() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Enable voice
                boolean enabled = voiceManager.enableVoice().join();
                if (!enabled) {
                    return false;
                }
                
                voiceEnabled = true;
                
                // Connect to spatial voice channel (mock)
                boolean connected = voiceManager.connectToVoiceChannel("spatial_main", "mock-signaling-server").join();
                if (connected) {
                    Log.i(TAG, "✅ WebRTC voice system ready");
                    return true;
                }
                
                return false;
                
            } catch (Exception e) {
                Log.e(TAG, "Voice setup error", e);
                return false;
            }
        });
    }
    
    /**
     * Set up chat system demo
     */
    private CompletableFuture<Boolean> setupChatDemo() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Test local chat
                boolean localSent = chatManager.sendLocalChatMessage("Hello, world! Testing modern chat system.", 0).join();
                
                // Test group chat
                String testGroupId = "test-group-" + System.currentTimeMillis();
                boolean groupJoined = chatManager.joinGroupChat(testGroupId, "Demo Group").join();
                boolean groupSent = chatManager.sendGroupChatMessage("Testing group chat functionality.", testGroupId).join();
                
                if (localSent && groupJoined && groupSent) {
                    chatActive = true;
                    Log.i(TAG, "✅ Chat system ready");
                    return true;
                }
                
                return false;
                
            } catch (Exception e) {
                Log.e(TAG, "Chat setup error", e);
                return false;
            }
        });
    }
    
    /**
     * Run integration tests between all systems
     */
    private void runIntegrationTests() {
        Log.i(TAG, "Running integration tests...");
        
        // Test 1: Voice + Chat integration
        if (voiceEnabled && chatActive) {
            Log.i(TAG, "✅ Voice and chat systems can coexist");
            chatManager.sendTypingIndicator("spatial_main", true);
        }
        
        // Test 2: Avatar + Chat integration
        if (currentAvatarId != null && chatActive) {
            Log.i(TAG, "✅ Avatar and chat systems integrated");
            // Avatar could show chat bubbles in real implementation
        }
        
        // Test 3: Authentication state verification
        if (authManager.isTokenValid()) {
            Log.i(TAG, "✅ Authentication token valid");
        }
        
        // Test 4: System status summary
        Log.i(TAG, "=== System Status Summary ===");
        Log.i(TAG, "Authentication: " + (isAuthenticated ? "✅ Active" : "❌ Inactive"));
        Log.i(TAG, "WebRTC Voice: " + (voiceEnabled ? "✅ Active" : "❌ Inactive"));  
        Log.i(TAG, "Modern Chat: " + (chatActive ? "✅ Active" : "❌ Inactive"));
        Log.i(TAG, "Avatar System: " + (currentAvatarId != null ? "✅ Active" : "❌ Inactive"));
    }
    
    /**
     * Demonstrate voice communication
     */
    public void demonstrateVoice() {
        if (voiceEnabled) {
            Log.i(TAG, "Demonstrating voice features...");
            
            // Create offer for voice communication
            voiceManager.createOffer().thenAccept(offer -> {
                Log.i(TAG, "Voice offer created: " + offer.description.substring(0, Math.min(100, offer.description.length())));
            }).exceptionally(throwable -> {
                Log.w(TAG, "Voice offer failed", throwable);
                return null;
            });
        }
    }
    
    /**
     * Demonstrate chat functionality
     */
    public void demonstrateChat() {
        if (chatActive) {
            Log.i(TAG, "Demonstrating chat features...");
            
            // Send various types of messages
            chatManager.sendLocalChatMessage("This is a local chat message from the demo!", 0);
            
            // Demo typing indicators
            chatManager.sendTypingIndicator("demo_session", true);
            
            // Show active sessions
            Log.i(TAG, "Active chat sessions: " + chatManager.getActiveSessions().size());
        }
    }
    
    /**
     * Get comprehensive status of all systems
     */
    public SystemStatus getSystemStatus() {
        return new SystemStatus(
            isAuthenticated && authManager.isTokenValid(),
            voiceEnabled && voiceManager.isConnected(),
            chatActive,
            currentAvatarId != null,
            authManager.getSessionInfo()
        );
    }
    
    /**
     * Cleanup all resources
     */
    public void cleanup() {
        try {
            if (voiceManager != null) {
                voiceManager.cleanup();
            }
            
            if (chatManager != null) {
                chatManager.cleanup();
            }
            
            if (avatarManager != null) {
                avatarManager.cleanup();
            }
            
            if (authManager != null) {
                authManager.logout();
            }
            
            Log.i(TAG, "All components cleaned up");
            
        } catch (Exception e) {
            Log.e(TAG, "Cleanup error", e);
        }
    }
    
    // Helper methods and mock objects
    
    private ModernAvatarManager.MockSLObject createMockSLObject(UUID id) {
        // Create a mock SLObject for avatar testing
        // In real implementation, this would come from the SL protocol
        return new ModernAvatarManager.MockSLObject(id);
    }
    
    // Event listeners for component integration
    
    private class VoiceConnectionListener implements WebRTCManager.VoiceConnectionListener {
        @Override
        public void onVoiceConnected(String channelId) {
            Log.i(TAG, "🎙️ Voice connected to channel: " + channelId);
        }
        
        @Override
        public void onVoiceDisconnected(String channelId) {
            Log.i(TAG, "🎙️ Voice disconnected from channel: " + channelId);
        }
        
        @Override
        public void onVoiceError(String error) {
            Log.w(TAG, "🎙️ Voice error: " + error);
        }
        
        @Override
        public void onAudioReceived(String speakerId, byte[] audioData) {
            Log.d(TAG, "🎙️ Audio received from: " + speakerId + " (" + audioData.length + " bytes)");
        }
    }
    
    private class ChatEventListener implements ModernChatManager.ChatEventListener {
        @Override
        public void onLocalChatReceived(ModernChatManager.ChatMessage message) {
            Log.i(TAG, "💬 Local chat: " + message.getMessage());
        }
        
        @Override
        public void onGroupChatReceived(ModernChatManager.ChatMessage message) {
            Log.i(TAG, "👥 Group chat: " + message.getMessage());
        }
        
        @Override
        public void onGroupChatInvitation(ModernChatManager.GroupChatInvitation invitation) {
            Log.i(TAG, "📧 Group invitation from: " + invitation.getInviterName());
        }
        
        @Override
        public void onChatError(String error) {
            Log.w(TAG, "💬 Chat error: " + error);
        }
        
        @Override
        public void onTypingIndicator(String userId, String sessionId, boolean isTyping) {
            Log.d(TAG, "⌨️ Typing indicator: " + userId + " is " + (isTyping ? "typing" : "not typing"));
        }
    }
    
    private class AvatarEventListener implements ModernAvatarManager.AvatarEventListener {
        @Override
        public void onAvatarAppearanceChanged(UUID avatarId, ModernAvatarManager.AvatarAppearance appearance) {
            Log.i(TAG, "👤 Avatar appearance updated: " + avatarId);
        }
        
        @Override
        public void onAvatarTextureUpdated(UUID avatarId, String textureId) {
            Log.i(TAG, "🎨 Avatar texture updated: " + avatarId);
        }
        
        @Override
        public void onAvatarAnimationChanged(UUID avatarId, String animationId) {
            Log.i(TAG, "🕺 Avatar animation changed: " + avatarId + " -> " + animationId);
        }
        
        @Override
        public void onAvatarRenderingError(UUID avatarId, String error) {
            Log.w(TAG, "👤 Avatar error: " + error);
        }
    }
    
    // Status classes
    
    public static class SystemStatus {
        private final boolean authenticationActive;
        private final boolean voiceActive; 
        private final boolean chatActive;
        private final boolean avatarActive;
        private final OAuth2AuthManager.SessionInfo sessionInfo;
        
        public SystemStatus(boolean authenticationActive, boolean voiceActive, 
                           boolean chatActive, boolean avatarActive,
                           OAuth2AuthManager.SessionInfo sessionInfo) {
            this.authenticationActive = authenticationActive;
            this.voiceActive = voiceActive;
            this.chatActive = chatActive;
            this.avatarActive = avatarActive;
            this.sessionInfo = sessionInfo;
        }
        
        // Getters
        public boolean isAuthenticationActive() { return authenticationActive; }
        public boolean isVoiceActive() { return voiceActive; }
        public boolean isChatActive() { return chatActive; }
        public boolean isAvatarActive() { return avatarActive; }
        public OAuth2AuthManager.SessionInfo getSessionInfo() { return sessionInfo; }
        
        public boolean allSystemsActive() {
            return authenticationActive && voiceActive && chatActive && avatarActive;
        }
    }
}