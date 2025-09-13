package com.lumiyaviewer.lumiya.modern.samples;

import android.content.Context;
import android.util.Log;

import com.lumiyaviewer.lumiya.modern.graphics.ModernRenderPipeline;
import com.lumiyaviewer.lumiya.modern.graphics.ModernTextureManager;
import com.lumiyaviewer.lumiya.modern.protocol.HybridSLTransport;
import com.lumiyaviewer.lumiya.modern.protocol.WebSocketEventClient;
import com.lumiyaviewer.lumiya.modern.auth.OAuth2AuthManager;
import com.lumiyaviewer.lumiya.modern.assets.ModernAssetManager;

/**
 * Demonstration of modern Linkpoint components
 * Shows integration of HTTP/2, WebSocket, and advanced graphics
 */
public class ModernLinkpointDemo {
    private static final String TAG = "ModernLinkpointDemo";
    
    private final Context context;
    private final HybridSLTransport transport;
    private final ModernTextureManager textureManager;
    private final ModernRenderPipeline renderPipeline;
    private final OAuth2AuthManager authManager;
    private final ModernAssetManager assetManager;
    
    public ModernLinkpointDemo(Context context) {
        this.context = context;
        this.transport = new HybridSLTransport();
        this.textureManager = new ModernTextureManager(context);
        this.renderPipeline = new ModernRenderPipeline();
        this.authManager = new OAuth2AuthManager(context);
        this.assetManager = new ModernAssetManager(context);
        
        Log.i(TAG, "Modern Linkpoint components initialized with full feature set");
    }
    
    /**
     * Initialize modern Second Life connection
     */
    public void connectToSecondLife(String eventQueueUrl, String seedCapability, String authToken) {
        Log.i(TAG, "Connecting to Second Life with modern protocols...");
        
        // Set authentication for all transport layers
        transport.setAuthToken(authToken);
        
        // Initialize hybrid transport (HTTP/2 + WebSocket)
        transport.initialize(eventQueueUrl, seedCapability);
        
        // Subscribe to real-time events
        transport.subscribeToEvents("chat", new WebSocketEventClient.EventListener() {
            @Override
            public void onEvent(WebSocketEventClient.EventMessage event) {
                Log.d(TAG, "Received chat event: " + event.getData());
                // TODO: Process chat message
            }
        });
        
        transport.subscribeToEvents("objectUpdate", new WebSocketEventClient.EventListener() {
            @Override
            public void onEvent(WebSocketEventClient.EventMessage event) {
                Log.d(TAG, "Received object update: " + event.getData());
                // TODO: Update 3D world objects
            }
        });
        
        Log.i(TAG, "Modern transport layer connected successfully");
    }
    
    /**
     * Initialize modern graphics system
     */
    public void initializeGraphics() {
        Log.i(TAG, "Initializing modern graphics system...");
        
        // Initialize render pipeline with modern OpenGL ES 3.0+ features
        boolean success = renderPipeline.initialize();
        if (success) {
            if (renderPipeline.isModernPipelineAvailable()) {
                Log.i(TAG, "Modern PBR rendering pipeline enabled");
            } else {
                Log.i(TAG, "Legacy rendering pipeline active (OpenGL ES 2.0)");
            }
        }
        
        // Check optimal texture format for this device
        int optimalFormat = textureManager.getOptimalTextureFormat();
        String formatName = ModernTextureManager.getFormatName(optimalFormat);
        Log.i(TAG, "Optimal texture format for this device: " + formatName);
        
        Log.i(TAG, "Graphics system initialized successfully");
    }
    
    /**
     * Load texture with modern transcoding
     */
    public void loadTexture(String textureId) {
        Log.d(TAG, "Loading texture with modern pipeline: " + textureId);
        
        textureManager.loadTextureAsync(textureId, ModernTextureManager.TexturePriority.NORMAL)
            .thenAccept(textureHandle -> {
                if (textureHandle > 0) {
                    Log.d(TAG, "Texture loaded successfully: " + textureId + " -> " + textureHandle);
                    // TODO: Use texture in rendering
                } else {
                    Log.w(TAG, "Failed to load texture: " + textureId);
                }
            })
            .exceptionally(throwable -> {
                Log.e(TAG, "Error loading texture: " + textureId, throwable);
                return null;
            });
    }
    
    /**
     * Send modern message
     */
    public void sendMessage(String messageType, String content) {
        Log.d(TAG, "Sending modern message: " + messageType);
        
        // Create a sample modern message
        HybridSLTransport.ModernMessage message = new HybridSLTransport.ModernMessage(messageType) {
            @Override
            public String toLLSDXML() {
                return "<llsd><map><key>type</key><string>" + getType() + 
                       "</string><key>content</key><string>" + content + "</string></map></llsd>";
            }
        };
        
        transport.sendMessage(message)
            .thenAccept(response -> {
                Log.d(TAG, "Message sent successfully: " + response.getType());
            })
            .exceptionally(throwable -> {
                Log.e(TAG, "Failed to send message", throwable);
                return null;
            });
    }
    
    /**
     * Render frame with modern pipeline
     */
    public void renderFrame(float[] viewMatrix, float[] projectionMatrix) {
        ModernRenderPipeline.RenderParams params = new ModernRenderPipeline.RenderParams();
        
        // Set up matrices
        System.arraycopy(viewMatrix, 0, params.viewMatrix, 0, 16);
        System.arraycopy(projectionMatrix, 0, params.projectionMatrix, 0, 16);
        
        // Set camera position (example)
        params.cameraPosition[0] = 128.0f;
        params.cameraPosition[1] = 128.0f;
        params.cameraPosition[2] = 20.0f;
        
        // Set lighting (example - sun direction)
        params.directionalLight[0] = -0.5f;  // X direction
        params.directionalLight[1] = -0.8f;  // Y direction
        params.directionalLight[2] = -0.3f;  // Z direction
        params.directionalLight[3] = 1.0f;   // Intensity
        
        // Render the frame
        renderPipeline.renderFrame(params);
    }
    
    /**
     * Shutdown modern components
     */
    public void shutdown() {
        Log.i(TAG, "Shutting down modern Linkpoint components");
        
        transport.shutdown();
        textureManager.shutdown();
        renderPipeline.cleanup();
        
        // Shutdown new components
        if (assetManager != null) {
            assetManager.shutdown();
        }
        
        Log.i(TAG, "Modern components shut down successfully");
    }
    
    /**
     * Demonstrate modern authentication with OAuth2
     */
    public void demonstrateModernAuthentication(String username, String password) {
        Log.i(TAG, "Demonstrating modern OAuth2 authentication...");
        
        authManager.authenticateUser(username, password)
            .thenAccept(result -> {
                if (result.isSuccess()) {
                    Log.i(TAG, "OAuth2 authentication successful!");
                    transport.setAuthToken(result.getToken());
                    demonstrateAssetStreaming();
                } else {
                    Log.w(TAG, "Authentication failed: " + result.getMessage());
                }
            });
    }
    
    /**
     * Demonstrate intelligent asset streaming
     */
    public void demonstrateAssetStreaming() {
        Log.i(TAG, "Demonstrating intelligent asset streaming...");
        
        String[] textureIds = {"texture_001", "texture_002", "texture_003"};
        
        for (String textureId : textureIds) {
            assetManager.loadAsset(textureId, ModernAssetManager.AssetType.TEXTURE)
                .thenAccept(assetData -> {
                    if (assetData != null) {
                        Log.d(TAG, "Loaded texture asset: " + assetData.getId());
                        textureManager.processModernTexture(assetData.getData());
                    }
                });
        }
    }
    
    /**
     * Get authentication manager
     */
    public OAuth2AuthManager getAuthManager() {
        return authManager;
    }
    
    /**
     * Get asset manager  
     */
    public ModernAssetManager getAssetManager() {
        return assetManager;
    }
    
    /**
     * Get connection status
     */
    public boolean isConnected() {
        return transport.isConnected();
    }
    
    /**
     * Get graphics capabilities info
     */
    public String getGraphicsInfo() {
        boolean modernPipeline = renderPipeline.isModernPipelineAvailable();
        int textureFormat = textureManager.getOptimalTextureFormat();
        String formatName = ModernTextureManager.getFormatName(textureFormat);
        
        return String.format("Modern Pipeline: %s, Optimal Texture: %s", 
                           modernPipeline ? "Available" : "Legacy", formatName);
    }
}