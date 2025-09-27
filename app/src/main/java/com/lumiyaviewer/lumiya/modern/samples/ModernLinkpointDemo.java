package com.lumiyaviewer.lumiya.modern.samples;
import java.util.*;

import android.content.Context;
import android.util.Log;

import com.lumiyaviewer.lumiya.modern.graphics.ModernRenderPipeline;
import com.lumiyaviewer.lumiya.modern.graphics.ModernTextureManager;
import com.lumiyaviewer.lumiya.modern.protocol.HybridSLTransport;
import com.lumiyaviewer.lumiya.modern.protocol.WebSocketEventClient;
import com.lumiyaviewer.lumiya.modern.auth.OAuth2AuthManager;
import com.lumiyaviewer.lumiya.modern.assets.ModernAssetManager;
import com.lumiyaviewer.lumiya.modern.llsd.ModernLLSDCodec;

/**
 * Demonstration of modern Linkpoint components
 * Shows integration of HTTP/2, WebSocket, LLSD codec, and advanced graphics
 */
public class ModernLinkpointDemo {
    private static final String TAG = "ModernLinkpointDemo";
    
    private final Context context;
    private final HybridSLTransport transport;
    private final ModernTextureManager textureManager;
    private final ModernRenderPipeline renderPipeline;
    private final OAuth2AuthManager authManager;
    private final ModernAssetManager assetManager;
    private final ProtocolCompatibilityDemo protocolDemo;
    
    public ModernLinkpointDemo(Context context) {
        this.context = context;
        
        Log.i(TAG, "Initializing ModernLinkpointDemo components...");
        
        // Initialize components with individual error handling to prevent cascading failures
        this.transport = initializeTransport();
        this.textureManager = initializeTextureManager(context);
        this.renderPipeline = initializeRenderPipeline(); 
        this.authManager = initializeAuthManager(context);
        this.assetManager = initializeAssetManager(context);
        this.protocolDemo = initializeProtocolDemo(context);
        
        Log.i(TAG, "Modern Linkpoint components initialized with graceful error handling");
    }
    
    /**
     * Initialize transport with error handling
     */
    private HybridSLTransport initializeTransport() {
        try {
            return new HybridSLTransport();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize transport - using null fallback", e);
            return null;
        }
    }
    
    /**
     * Initialize texture manager with error handling
     */
    private ModernTextureManager initializeTextureManager(Context context) {
        try {
            return new ModernTextureManager(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize texture manager - using null fallback", e);
            return null;
        }
    }
    
    /**
     * Initialize render pipeline with error handling
     */
    private ModernRenderPipeline initializeRenderPipeline() {
        try {
            return new ModernRenderPipeline();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize render pipeline - using null fallback", e);
            return null;
        }
    }
    
    /**
     * Initialize auth manager with error handling
     */
    private OAuth2AuthManager initializeAuthManager(Context context) {
        try {
            return new OAuth2AuthManager(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize auth manager - using null fallback", e);
            return null; 
        }
    }
    
    /**
     * Initialize asset manager with error handling
     */
    private ModernAssetManager initializeAssetManager(Context context) {
        try {
            return new ModernAssetManager(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize asset manager - using null fallback", e);
            return null;
        }
    }
    
    /**
     * Initialize protocol demo with error handling
     */
    private ProtocolCompatibilityDemo initializeProtocolDemo(Context context) {
        try {
            return new ProtocolCompatibilityDemo(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize protocol demo - using null fallback", e);
            return null;
        }
    }
    
    /**
     * Initialize modern Second Life connection
     */
    public void connectToSecondLife(String eventQueueUrl, String seedCapability, String authToken) {
        Log.i(TAG, "Connecting to Second Life with modern protocols...");
        
        if (transport == null) {
            Log.w(TAG, "Transport not available - cannot connect to Second Life");
            return;
        }
        
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Failed to connect to Second Life", e);
        }
    }
    
    /**
     * Initialize modern graphics system with OpenGL ES 3.0 baseline
     */
    public void initializeGraphics() {
        Log.i(TAG, "Initializing modernized graphics system (ES 3.0+ only)...");
        
        if (renderPipeline == null) {
            Log.w(TAG, "Render pipeline not available - graphics initialization skipped");
            return;
        }
        
        try {
            // Initialize render pipeline with mandatory OpenGL ES 3.0+ features
            boolean success = renderPipeline.initialize();
            if (success) {
                if (renderPipeline.isModernPipelineAvailable()) {
                    Log.i(TAG, "Modern PBR rendering pipeline enabled (ES 3.0+)");
                    Log.i(TAG, "Legacy ES 1.1/2.0 code paths removed");
                } else {
                    Log.e(TAG, "CRITICAL: OpenGL ES 3.0+ required but not available");
                    Log.e(TAG, "Device does not meet minimum graphics requirements");
                    return;
                }
            }
            
            // Initialize modernized texture manager  
            if (textureManager != null) {
                try {
                    int optimalFormat = textureManager.getOptimalTextureFormat();
                    String formatName = getFormatName(optimalFormat);
                    Log.i(TAG, "Optimal texture format: " + formatName + " (ES 3.0 ETC2+ only)");
                    Log.i(TAG, "Legacy JPEG2000 format support removed");
                } catch (Exception e) {
                    Log.w(TAG, "Modern texture manager not fully available - using fallback", e);
                }
            } else {
                Log.w(TAG, "Texture manager not available - texture operations will be limited");
            }
            
            Log.i(TAG, "Graphics system initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize graphics system", e);
        }
    }
    
    /**
     * Load texture with modern transcoding
     */
    public void loadTexture(String textureId) {
        Log.d(TAG, "Loading texture with modern pipeline: " + textureId);
        
        if (textureManager == null) {
            Log.w(TAG, "Texture manager not available - texture loading skipped");
            return;
        }
        
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Failed to initiate texture loading: " + textureId, e);
        }
    }
    
    /**
     * Send modern message
     */
    public void sendMessage(String messageType, String content) {
        Log.d(TAG, "Sending modern message: " + messageType);
        
        if (transport == null) {
            Log.w(TAG, "Transport not available - message sending skipped");
            return;
        }
        
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Failed to create or send message", e);
        }
    }
    
    /**
     * Render frame with modern pipeline
     */
    public void renderFrame(float[] viewMatrix, float[] projectionMatrix) {
        if (renderPipeline == null) {
            Log.w(TAG, "Render pipeline not available - frame rendering skipped");
            return;
        }
        
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error during frame rendering", e);
        }
    }
    
    /**
     * Shutdown modern components
     */
    public void shutdown() {
        Log.i(TAG, "Shutting down modern Linkpoint components");
        
        try {
            if (transport != null) {
                transport.shutdown();
            }
            if (textureManager != null) {
                textureManager.shutdown();
            }
            if (renderPipeline != null) {
                renderPipeline.cleanup();
            }
            
            // Shutdown new components
            if (assetManager != null) {
                assetManager.shutdown();
            }
            
            Log.i(TAG, "Modern components shut down successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error during component shutdown", e);
        }
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
     * Test modern LLSD codec and protocol compatibility
     */
    public void testModernLLSDCodec() {
        Log.i(TAG, "Testing Modern LLSD Codec with LibreMetaverse compatibility...");
        protocolDemo.runFullDemo();
    }
    
    /**
     * Get connection status
     */
    public boolean isConnected() {
        if (transport == null) {
            return false;
        }
        try {
            return transport.isConnected();
        } catch (Exception e) {
            Log.e(TAG, "Error checking connection status", e);
            return false;
        }
    }
    
    /**
     * Get graphics capabilities info
     */
    public String getGraphicsInfo() {
        try {
            boolean modernPipeline = (renderPipeline != null) && renderPipeline.isModernPipelineAvailable();
            
            int textureFormat = 0;
            if (textureManager != null) {
                textureFormat = textureManager.getOptimalTextureFormat();
            }
            String formatName = getFormatName(textureFormat);
            
            return String.format("Modern Pipeline: %s, Optimal Texture: %s", 
                               modernPipeline ? "Available" : "Legacy", formatName);
        } catch (Exception e) {
            Log.e(TAG, "Error getting graphics info", e);
            return "Graphics info unavailable";
        }
    }
    
    /**
     * Demonstrate modernized graphics pipeline
     * Shows the removal of OpenGL ES 1.1 compatibility and ES 3.0+ features
     */
    public void demonstrateModernGraphics() {
        Log.i(TAG, "=== Modern Graphics Pipeline Demonstration ===");
        
        // Show capabilities that are now mandatory (ES 3.0+)
        Log.i(TAG, "Mandatory ES 3.0+ features:");
        Log.i(TAG, "  - Programmable shaders (vertex/fragment)");
        Log.i(TAG, "  - Vertex Array Objects (VAOs)");
        Log.i(TAG, "  - Uniform Buffer Objects (UBOs)");
        Log.i(TAG, "  - ETC2 texture compression");
        Log.i(TAG, "  - Transform feedback");
        Log.i(TAG, "  - Multiple render targets (MRT)");
        
        // Show removed legacy features
        Log.i(TAG, "Removed legacy ES 1.1 features:");
        Log.i(TAG, "  - Fixed-function pipeline");
        Log.i(TAG, "  - Matrix stack (glPushMatrix/glPopMatrix)");
        Log.i(TAG, "  - Immediate mode rendering");
        Log.i(TAG, "  - glTexEnv texture combiners");
        Log.i(TAG, "  - Client-side vertex arrays");
        
        // Demonstrate modern PBR capabilities
        try {
            if (renderPipeline != null && renderPipeline.isModernPipelineAvailable()) {
                Log.i(TAG, "PBR pipeline features available:");
                Log.i(TAG, "  - Metallic/Roughness workflow");
                Log.i(TAG, "  - Normal mapping");
                Log.i(TAG, "  - Image-based lighting");
                Log.i(TAG, "  - Dynamic directional/point lights");
            } else {
                Log.i(TAG, "PBR pipeline features: Not available (render pipeline null or not modern)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking PBR pipeline features", e);
        }
        
        Log.i(TAG, "=============================================");
    }
    
    /**
     * Helper method to get texture format name
     */
    private String getFormatName(int format) {
        switch (format) {
            case 0x93B0: return "ASTC 4x4 RGBA";
            case 0x9278: return "ETC2 RGBA"; 
            case 0x1908: return "RGBA32";
            default: return "Unknown Format";
        }
    }
}