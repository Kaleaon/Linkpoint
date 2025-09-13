# Linkpoint Modern Components API Documentation

## 📋 Overview

This document provides comprehensive API documentation for all modern components in the Linkpoint sample application. These components represent the state-of-the-art in mobile virtual world client technology.

## 🏗️ Architecture Overview

The modern Linkpoint architecture consists of several interconnected systems:

```
ModernLinkpointDemo (Orchestrator)
├── OAuth2AuthManager (Authentication)
├── HybridSLTransport (Network Transport)
│   ├── HTTP2CapsClient (CAPS Protocol)
│   └── WebSocketEventClient (Real-time Events)
├── ModernAssetManager (Asset Management)
├── ModernRenderPipeline (Graphics Rendering)
├── ModernTextureManager (Texture Processing)
└── LumiyaApp (Application Context)
```

## 🔐 OAuth2AuthManager

Modern authentication system using OAuth2 flows for secure Second Life access.

### Class Definition
```java
package com.lumiyaviewer.lumiya.modern.auth;

public class OAuth2AuthManager {
    public OAuth2AuthManager(Context context)
    public CompletableFuture<AuthResult> authenticateUser(String username, String password)
    public void refreshToken(String refreshToken)
    public boolean isTokenValid()
    public void logout()
}
```

### Key Methods

#### `authenticateUser(String username, String password)`
- **Purpose**: Authenticates user with Second Life using modern OAuth2 flow
- **Parameters**: 
  - `username`: Second Life username
  - `password`: Second Life password  
- **Returns**: `CompletableFuture<AuthResult>` containing authentication result
- **Example**:
```java
authManager.authenticateUser("john.doe", "password123")
    .thenAccept(result -> {
        if (result.isSuccess()) {
            String token = result.getToken();
            // Use authentication token
        }
    });
```

#### `isTokenValid()`
- **Purpose**: Checks if current authentication token is still valid
- **Returns**: `boolean` indicating token validity
- **Usage**: Call before making authenticated requests

### Authentication Flow

1. **Token Request**: OAuth2AuthManager requests tokens from SL servers
2. **Secure Storage**: Tokens stored using Android Keystore
3. **Automatic Refresh**: Handles token refresh transparently
4. **Session Management**: Maintains authentication state across app lifecycle

## 🌐 HybridSLTransport

Modern transport layer combining HTTP/2 CAPS and WebSocket events for optimal performance.

### Class Definition
```java
package com.lumiyaviewer.lumiya.modern.protocol;

public class HybridSLTransport {
    public HybridSLTransport()
    public void initialize(String eventQueueUrl, String seedCapability)
    public void setAuthToken(String token)
    public CompletableFuture<MessageResponse> sendMessage(ModernMessage message)
    public void subscribeToEvents(String eventType, WebSocketEventClient.EventListener listener)
    public boolean isConnected()
    public void shutdown()
}
```

### Key Features

#### Dual Transport Modes
- **HTTP/2 CAPS**: For request/response operations
- **WebSocket Events**: For real-time event streaming

#### `initialize(String eventQueueUrl, String seedCapability)`
- **Purpose**: Initializes both transport layers
- **Parameters**:
  - `eventQueueUrl`: WebSocket endpoint for real-time events
  - `seedCapability`: HTTP/2 CAPS seed URL
- **Example**:
```java
transport.initialize(
    "wss://simulator.secondlife.com/eventqueue",
    "https://simulator.secondlife.com/caps/seed"
);
```

#### `sendMessage(ModernMessage message)`
- **Purpose**: Sends message using optimal transport method
- **Returns**: `CompletableFuture<MessageResponse>` with server response
- **Auto-routing**: Automatically chooses HTTP/2 or WebSocket based on message type

### Message Types

#### ModernMessage (Abstract Base)
```java
public abstract class ModernMessage {
    public abstract String getType();
    public abstract String toLLSDXML();
}
```

Common implementations:
- `ChatMessage`: In-world chat
- `ObjectUpdateMessage`: Object state changes
- `AssetRequestMessage`: Asset downloads
- `TeleportMessage`: Avatar movement

## 📦 ModernAssetManager

Intelligent asset streaming system with adaptive quality and efficient caching.

### Class Definition
```java
package com.lumiyaviewer.lumiya.modern.assets;

public class ModernAssetManager {
    public ModernAssetManager(Context context)
    public CompletableFuture<AssetData> loadAsset(String assetId, AssetType type)
    public void setQualityLevel(QualityLevel level)
    public void clearCache()
    public long getCacheSize()
    public void shutdown()
}
```

### Asset Types
```java
public enum AssetType {
    TEXTURE,        // Images and materials
    MESH,           // 3D geometry
    ANIMATION,      // Avatar animations
    SOUND,          // Audio assets
    SCRIPT          // LSL scripts
}
```

### Quality Levels
```java
public enum QualityLevel {
    LOW,            // Optimized for slow connections
    MEDIUM,         // Balanced quality/performance
    HIGH,           // High quality for fast connections
    ULTRA           // Maximum quality for high-end devices
}
```

### Key Features

#### Adaptive Quality
- Automatically adjusts asset quality based on:
  - Network speed and reliability
  - Device capabilities
  - Battery status
  - User preferences

#### Intelligent Caching
- **Cache Size**: Configurable up to 256MB
- **Cache Strategy**: LRU (Least Recently Used) eviction
- **Persistent Cache**: Survives app restarts
- **Cache Validation**: Ensures asset integrity

#### `loadAsset(String assetId, AssetType type)`
- **Purpose**: Loads asset with optimal quality and caching
- **Parameters**:
  - `assetId`: UUID of the asset to load
  - `type`: Type of asset (texture, mesh, etc.)
- **Returns**: `CompletableFuture<AssetData>` with loaded asset
- **Example**:
```java
assetManager.loadAsset("550e8400-e29b-41d4-a716-446655440000", AssetType.TEXTURE)
    .thenAccept(assetData -> {
        byte[] textureData = assetData.getData();
        // Process texture data
    });
```

## 🎨 ModernRenderPipeline

Advanced OpenGL ES 3.0+ rendering pipeline with physically-based rendering (PBR).

### Class Definition
```java
package com.lumiyaviewer.lumiya.modern.graphics;

public class ModernRenderPipeline {
    public ModernRenderPipeline()
    public boolean initialize()
    public boolean isModernPipelineAvailable()
    public void renderFrame(RenderParams params)
    public void cleanup()
}
```

### RenderParams Structure
```java
public static class RenderParams {
    public float[] viewMatrix = new float[16];          // Camera view matrix
    public float[] projectionMatrix = new float[16];    // Projection matrix
    public float[] cameraPosition = new float[3];       // Camera world position
    public float[] directionalLight = new float[4];     // Sun light direction + intensity
    public List<PointLight> pointLights;                // Dynamic point lights
    public float ambientIntensity = 0.2f;               // Ambient lighting level
}
```

### Graphics Features

#### Modern OpenGL ES 3.0+ Only
- **No Legacy Support**: Completely removes ES 1.1 compatibility
- **Mandatory ES 3.0+**: Requires modern GPU capabilities
- **Desktop-Quality**: Brings high-end graphics to mobile

#### PBR (Physically Based Rendering)
- **Metallic/Roughness Workflow**: Industry-standard material system
- **Image-Based Lighting**: Realistic environment reflections
- **Normal Mapping**: Detailed surface textures
- **Dynamic Lighting**: Real-time light calculations

#### `renderFrame(RenderParams params)`
- **Purpose**: Renders a complete frame using modern pipeline
- **Parameters**: `RenderParams` containing camera and lighting data
- **Performance**: Optimized for 60fps on modern hardware
- **Example**:
```java
ModernRenderPipeline.RenderParams params = new ModernRenderPipeline.RenderParams();
// Set up view and projection matrices
System.arraycopy(viewMatrix, 0, params.viewMatrix, 0, 16);
System.arraycopy(projectionMatrix, 0, params.projectionMatrix, 0, 16);

// Set camera position
params.cameraPosition[0] = 128.0f; // X
params.cameraPosition[1] = 128.0f; // Y  
params.cameraPosition[2] = 20.0f;  // Z

// Set directional lighting (sun)
params.directionalLight[0] = -0.5f;  // X direction
params.directionalLight[1] = -0.8f;  // Y direction
params.directionalLight[2] = -0.3f;  // Z direction
params.directionalLight[3] = 1.0f;   // Intensity

renderPipeline.renderFrame(params);
```

## 🖼️ ModernTextureManager

Advanced texture processing with GPU optimization and modern compression formats.

### Class Definition
```java
package com.lumiyaviewer.lumiya.modern.graphics;

public class ModernTextureManager {
    public ModernTextureManager(Context context)
    public int getOptimalTextureFormat()
    public CompletableFuture<Integer> loadTextureAsync(String textureId, TexturePriority priority)
    public void processModernTexture(byte[] textureData)
    public void shutdown()
}
```

### Texture Formats
- **ASTC 4x4**: Highest quality compressed format
- **ETC2**: Widely supported modern compression
- **RGBA32**: Uncompressed fallback for compatibility

### Priority Levels
```java
public enum TexturePriority {
    LOW,        // Background textures
    NORMAL,     // Standard world textures
    HIGH,       // Avatar textures
    CRITICAL    // UI and essential textures
}
```

### Key Features

#### Basis Universal Integration
- **Advanced Compression**: Next-generation texture compression
- **GPU Optimization**: Hardware-accelerated transcoding
- **Format Detection**: Automatic optimal format selection

#### `getOptimalTextureFormat()`
- **Purpose**: Determines best texture format for current device
- **Returns**: OpenGL format constant (GL_COMPRESSED_ASTC_4x4, etc.)
- **Auto-detection**: Based on GPU capabilities and performance

#### `loadTextureAsync(String textureId, TexturePriority priority)`
- **Purpose**: Loads texture with priority-based processing
- **Parameters**:
  - `textureId`: Asset UUID of texture
  - `priority`: Loading priority level
- **Returns**: `CompletableFuture<Integer>` with OpenGL texture handle
- **Example**:
```java
textureManager.loadTextureAsync("texture-uuid", TexturePriority.HIGH)
    .thenAccept(textureHandle -> {
        // Use texture handle for rendering
        glBindTexture(GL_TEXTURE_2D, textureHandle);
    });
```

## 🎮 ModernLinkpointDemo

Main orchestrator class that coordinates all modern components for comprehensive demonstrations.

### Class Definition
```java
package com.lumiyaviewer.lumiya.modern.samples;

public class ModernLinkpointDemo {
    public ModernLinkpointDemo(Context context)
    public void connectToSecondLife(String eventQueueUrl, String seedCapability, String authToken)
    public void initializeGraphics()
    public void demonstrateModernAuthentication(String username, String password)
    public void demonstrateAssetStreaming()
    public void demonstrateModernGraphics()
    public String getGraphicsInfo()
    public boolean isConnected()
    public void shutdown()
}
```

### Integration Methods

#### `connectToSecondLife(String eventQueueUrl, String seedCapability, String authToken)`
- **Purpose**: Demonstrates complete connection workflow
- **Features**:
  - Sets up hybrid transport
  - Subscribes to event streams
  - Configures authentication
  - Tests connection reliability

#### `demonstrateModernGraphics()`
- **Purpose**: Shows advanced graphics capabilities
- **Demonstrations**:
  - Lists mandatory ES 3.0+ features
  - Shows removed legacy ES 1.1 code paths
  - Displays PBR pipeline capabilities
  - Reports graphics capabilities

### System Integration

The `ModernLinkpointDemo` class provides a unified interface to all modern components, making it easy to:
- Test individual systems
- Demonstrate complete workflows  
- Benchmark performance
- Validate capabilities

## 🔧 Usage Examples

### Complete Authentication + Connection Flow
```java
ModernLinkpointDemo demo = new ModernLinkpointDemo(context);

// Authenticate
demo.demonstrateModernAuthentication("username", "password");

// Connect using modern transport
demo.connectToSecondLife(
    "wss://sim.secondlife.com/eventqueue",
    "https://sim.secondlife.com/caps/seed", 
    "auth-token"
);

// Initialize graphics
demo.initializeGraphics();

// Test asset streaming
demo.demonstrateAssetStreaming();
```

### Custom Asset Loading with Quality Control
```java
ModernAssetManager assetManager = new ModernAssetManager(context);

// Set quality based on device capabilities
if (isHighEndDevice()) {
    assetManager.setQualityLevel(QualityLevel.ULTRA);
} else {
    assetManager.setQualityLevel(QualityLevel.MEDIUM);
}

// Load critical textures first
assetManager.loadAsset("avatar-texture-uuid", AssetType.TEXTURE)
    .thenAccept(avatarTexture -> {
        // Process avatar texture
    });
```

### Advanced Graphics Rendering
```java
ModernRenderPipeline pipeline = new ModernRenderPipeline();
if (pipeline.initialize()) {
    ModernRenderPipeline.RenderParams params = new ModernRenderPipeline.RenderParams();
    
    // Set up scene parameters
    setupCameraMatrices(params);
    configureLighting(params);
    
    // Render frame
    pipeline.renderFrame(params);
}
```

## 📊 Performance Considerations

### Memory Usage
- **Authentication**: Minimal overhead (~1MB)
- **Transport**: ~5MB for connection pools
- **Assets**: Up to 256MB configurable cache
- **Graphics**: ~50MB GPU memory pools
- **Total**: ~300MB maximum with full cache

### CPU Usage
- **Authentication**: Minimal, async operations
- **Network**: Optimized with connection pooling
- **Assets**: Multi-threaded loading/processing
- **Graphics**: GPU-accelerated where possible

### Battery Impact
- **Idle**: Minimal impact with efficient event handling
- **Active Use**: Moderate, optimized for mobile
- **Graphics Testing**: Higher during intensive operations

## 🔍 Debugging and Monitoring

### Logging
All components use consistent logging with configurable levels:
```java
Log.i(TAG, "Operation completed successfully");
Log.d(TAG, "Debug information: " + details);
Log.w(TAG, "Warning: potential issue detected");
Log.e(TAG, "Error occurred", exception);
```

### Performance Monitoring
Each component provides metrics for:
- Operation latency
- Success/failure rates  
- Resource utilization
- Cache hit ratios

### Error Handling
Comprehensive error handling with:
- Graceful degradation
- Automatic retries
- User-friendly error messages
- Detailed logging for debugging

---

## 🎯 Summary

The Modern Linkpoint components represent the cutting edge of mobile virtual world client technology. They provide:

- **Production-Ready Quality**: Enterprise-level code with comprehensive error handling
- **Future-Proof Architecture**: Built on modern standards and best practices
- **Mobile-Optimized**: Specifically designed for mobile device constraints
- **Extensible Design**: Easy to extend and customize for specific needs

These APIs form the foundation for the next generation of mobile virtual world applications, bringing desktop-quality experiences to mobile devices while maintaining optimal performance and battery efficiency.

For additional examples and implementation details, see the sample application source code and the comprehensive user guide.