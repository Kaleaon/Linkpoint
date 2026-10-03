# Comprehensive Lumiya Viewer Modernization Guide

## Table of Contents
1. [Executive Summary](#executive-summary)
2. [Current State Analysis](#current-state-analysis)
3. [Reference Architecture Integration](#reference-architecture-integration)
4. [Broken Code Areas & Fixes](#broken-code-areas--fixes)
5. [Modernization Roadmap](#modernization-roadmap)
6. [Implementation Details](#implementation-details)
7. [Testing & Validation](#testing--validation)
8. [Future Evolution](#future-evolution)

## Executive Summary

This document provides comprehensive documentation for modernizing the Lumiya Viewer to work optimally with Second Life, integrating insights and solutions from leading projects in the metaverse and virtual world ecosystem:

- **@webaverse-studios/webaverse** - Modern WebXR/3D web technologies and asset pipeline
- **OMI Group (https://github.com/omigroup)** - Open Metaverse Interoperability standards
- **@cinderblocks/libremetaverse** - Contemporary C# Second Life protocol implementation
- **Second Life OpenMetaverse Community** - Legacy protocol insights and modernization discussions

### Key Modernization Goals
1. **Protocol Modernization**: Update Second Life protocol implementation using LibreMetaverse insights
2. **Graphics Pipeline Upgrade**: Integrate modern 3D rendering techniques from Webaverse
3. **Interoperability**: Implement OMI standards for cross-platform compatibility
4. **Performance Optimization**: Apply contemporary mobile graphics optimization
5. **Asset Pipeline**: Modernize texture, mesh, and animation handling

## Current State Analysis

### Existing Strengths
The Lumiya Viewer codebase demonstrates sophisticated engineering:

```java
// Strong foundation in Second Life protocol implementation
SLAgentCircuit.java        - Main protocol handler (300+ message types)
SLConnection.java          - Network layer with UDP reliability
ResourceManager.java       - Advanced asset caching system
RenderContext.java         - Multi-version OpenGL ES support
```

### Architecture Overview
```
Lumiya Architecture (Current)
├── Protocol Layer (slproto/)
│   ├── UDP Circuit Management
│   ├── Message Serialization (150+ types)
│   ├── CAPS HTTP Integration
│   └── Authentication & Sessions
├── Graphics Engine (render/)
│   ├── OpenGL ES 1.1/2.0/3.0 Support
│   ├── Shader Programs (Avatar, Terrain, Sky, Water)
│   ├── Spatial Indexing (Octree)
│   └── Asset Pipeline (Textures, Meshes, Animations)
└── Resource Management (res/)
    ├── Multi-tier Caching (LRU)
    ├── Background Loading
    └── Memory Pool Management
```

## Reference Architecture Integration

### 1. Webaverse Integration Points

The Webaverse project provides cutting-edge insights for 3D web applications that can modernize Lumiya's graphics pipeline:

#### Modern Asset Pipeline (from Webaverse)
```javascript
// Webaverse approach - convert to Java equivalent
class ModernAssetLoader {
  // Basis Universal texture compression
  async loadBasisTexture(url) {
    const basis = await this.basisLoader.load(url);
    return this.transcodeToOptimalFormat(basis);
  }
  
  // glTF 2.0 mesh loading with Draco compression
  async loadGLTFMesh(url) {
    const gltf = await this.gltfLoader.load(url);
    return this.processGLTFScene(gltf);
  }
}
```

**Java Implementation for Lumiya:**
```java
// New class: ModernAssetPipeline.java
public class ModernAssetPipeline {
    // Integrate Basis Universal for optimal mobile texture compression
    private BasisUniversalDecoder basisDecoder;
    
    // Support glTF 2.0 format for Second Life mesh assets
    private GLTFLoader gltfLoader;
    
    public GLTexture loadOptimizedTexture(String assetId) {
        // Replace JPEG2000 with Basis Universal where supported
        if (supportsBasisUniversal()) {
            return loadBasisTexture(assetId);
        }
        return fallbackToJPEG2000(assetId);
    }
}
```

#### WebXR-Inspired 3D Rendering
```java
// Enhanced rendering pipeline inspired by Webaverse WebXR
public class ModernRenderPipeline {
    // Physically Based Rendering from Webaverse
    private PBRShaderProgram pbrShader;
    
    // Image-based lighting
    private IBLRenderer iblRenderer;
    
    // Modern post-processing stack
    private PostProcessingStack postFX;
    
    public void renderFrame(Camera camera, Scene scene) {
        // Deferred rendering approach from Webaverse
        gBuffer.bindForWriting();
        renderSceneToGBuffer(scene);
        
        // PBR lighting pass
        pbrShader.bind();
        iblRenderer.applyEnvironmentLighting();
        renderLightingPass();
        
        // Post-processing
        postFX.apply(finalImage);
    }
}
```

### 2. OMI Group Standards Integration

The Open Metaverse Interoperability group defines standards for cross-platform virtual worlds:

#### glTF Extensions for Virtual Worlds
```java
// Implement OMI-specified glTF extensions
public class OMIExtensionHandler {
    // OMI_collider - Physics collision shapes
    public CollisionShape parseOMICollider(GLTFNode node) {
        JSONObject collider = node.getExtension("OMI_collider");
        return createCollisionShape(collider);
    }
    
    // OMI_spawn_point - Avatar spawn locations
    public SpawnPoint parseOMISpawnPoint(GLTFScene scene) {
        JSONObject spawn = scene.getExtension("OMI_spawn_point");
        return new SpawnPoint(spawn.getVector3("position"), 
                            spawn.getQuaternion("rotation"));
    }
    
    // OMI_vrm - Avatar format
    public AvatarData parseOMIVRM(GLTFDocument gltf) {
        return VRMLoader.loadAvatar(gltf);
    }
}
```

#### Universal Scene Description (USD) Support
```java
// Future: USD support for interoperability
public class USDSceneLoader {
    // Load USD scenes for cross-platform compatibility
    public Scene loadUSDScene(String usdFile) {
        USDStage stage = USDStage.open(usdFile);
        return convertUSDToLumiyaScene(stage);
    }
}
```

### 3. LibreMetaverse Modern Implementation

@cinderblocks/libremetaverse provides the most up-to-date Second Life protocol implementation:

#### Enhanced Message Handling
```java
// Updated from LibreMetaverse improvements
public class ModernSLProtocol {
    // HTTP/2 support for CAPS
    private HTTP2Client capsClient;
    
    // Enhanced message parsing with protobuf support
    private MessageParser protobufParser;
    
    // Modern authentication with OAuth2
    private OAuth2Handler authHandler;
    
    public void connectToGrid(GridInfo grid, LoginCredentials creds) {
        // Use modern authentication flow
        AuthToken token = authHandler.authenticate(creds);
        
        // Establish HTTP/2 CAPS connection
        capsClient.connect(grid.getCapsURL(), token);
        
        // Fall back to UDP for legacy messages
        udpCircuit.connect(grid.getSimulatorIP());
    }
}
```

#### Enhanced Asset Handling
```java
// Modern asset pipeline from LibreMetaverse
public class ModernAssetManager {
    // HTTP/2 asset fetching
    private AssetHTTPClient assetClient;
    
    // Modern texture formats
    private TextureTranscoder textureTranscoder;
    
    public Future<Texture> fetchTexture(UUID textureId) {
        return assetClient.fetchAssetAsync(textureId)
            .thenApply(this::transcodeTexture);
    }
    
    private Texture transcodeTexture(byte[] rawData) {
        // Transcode JPEG2000 to optimal mobile format
        if (isMobileDevice()) {
            return textureTranscoder.transcodeToASTC(rawData);
        }
        return textureTranscoder.transcodeToBC7(rawData);
    }
}
```

## Broken Code Areas & Fixes

### 1. Android Build System Issues

**Problem**: Resource conflicts and build failures
```
Error: Duplicate value for resource 'attr/fontStyle'
Error: Can't process attribute android:pathData="@string/path_password_eye"
```

**Root Cause**: Conflicting resources between app resources and AndroidX libraries

**Fix**: Update build configuration and resource management
```gradle
// app/build.gradle - Updated configuration
android {
    // Fix resource conflicts
    packagingOptions {
        pickFirst '**/libjnidispatch.so'
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/LICENSE*'
        exclude 'META-INF/NOTICE*'
    }
    
    // Resolve attribute conflicts
    buildFeatures {
        buildConfig true
    }
}
```

**Implementation**:
```java
// Create ResourceConflictResolver.java
public class ResourceConflictResolver {
    public static void resolveDrawableConflicts() {
        // Programmatically handle resource conflicts
        // Use app-specific resource names to avoid conflicts
    }
}
```

### 2. Legacy OpenGL ES Support

**Problem**: Supporting OpenGL ES 1.1 limits modern GPU features
```java
// Current problematic code in RenderContext.java
if (hasGL30) { 
    useModernPath(); 
} else if (hasGL20) { 
    useShaderPath(); 
} else { 
    useLegacyPath(); // This path is problematic
}
```

**Fix**: Establish OpenGL ES 3.0 baseline
```java
// Updated RenderContext.java
public class ModernRenderContext {
    private static final int MIN_GL_VERSION = 0x30000; // OpenGL ES 3.0
    
    public boolean initialize() {
        if (!hasRequiredGLVersion()) {
            throw new UnsupportedOperationException(
                "OpenGL ES 3.0 required. Current device not supported.");
        }
        
        // Use only modern rendering path
        return initializeModernPipeline();
    }
    
    private boolean initializeModernPipeline() {
        // Uniform Buffer Objects for efficient data transfer
        setupUniformBuffers();
        
        // Transform feedback for GPU-based animation
        setupTransformFeedback();
        
        // Compute shaders for particle systems
        setupComputeShaders();
        
        return true;
    }
}
```

### 3. Texture Memory Management

**Problem**: JPEG2000 decompression is CPU-intensive and memory-heavy
```java
// Problematic current implementation
public class TextureCache {
    private JPEG2000Decoder decoder; // CPU-intensive
    
    public GLTexture loadTexture(UUID textureId) {
        byte[] j2kData = fetchFromNetwork(textureId);
        BufferedImage image = decoder.decode(j2kData); // Slow!
        return uploadToGPU(image);
    }
}
```

**Fix**: Implement modern texture compression pipeline
```java
// New ModernTextureManager.java
public class ModernTextureManager {
    private BasisUniversalTranscoder basisTranscoder;
    private ASTC_Encoder astcEncoder;
    private GPU_TexturePool texturePool;
    
    public Future<GLTexture> loadTextureAsync(UUID textureId) {
        return CompletableFuture.supplyAsync(() -> {
            // Check for pre-transcoded format
            if (hasBasisUniversalVersion(textureId)) {
                return loadBasisTexture(textureId);
            }
            
            // Fallback: transcode JPEG2000 on background thread
            byte[] j2kData = fetchFromNetwork(textureId);
            return transcodeToOptimalFormat(j2kData);
        });
    }
    
    private GLTexture transcodeToOptimalFormat(byte[] j2kData) {
        // For mobile devices, use ASTC
        if (DeviceCapabilities.supportsASTC()) {
            byte[] astcData = astcEncoder.encode(j2kData);
            return texturePool.createASTC_Texture(astcData);
        }
        
        // For other devices, use ETC2
        byte[] etc2Data = ETC2_Encoder.encode(j2kData);
        return texturePool.createETC2_Texture(etc2Data);
    }
}
```

### 4. Network Protocol Limitations

**Problem**: UDP-only protocol limits modern features
```java
// Current limited implementation
public class SLConnection {
    private DatagramSocket udpSocket; // Limited to UDP
    
    public void sendMessage(SLMessage message) {
        byte[] data = message.serialize();
        udpSocket.send(new DatagramPacket(data, data.length));
    }
}
```

**Fix**: Hybrid protocol implementation
```java
// New HybridSLConnection.java  
public class HybridSLConnection {
    private HTTP2Client capsClient;     // For modern features
    private DatagramSocket udpSocket;   // For legacy compatibility
    private WebSocketClient wsClient;   // For real-time events
    
    public void sendMessage(SLMessage message) {
        if (message.supportsHTTP2()) {
            // Use HTTP/2 for better performance
            capsClient.sendAsync(message.toHTTP2Request());
        } else {
            // Fall back to UDP for legacy messages
            udpSocket.send(message.toUDPPacket());
        }
    }
    
    public void subscribeToEvents() {
        // Use WebSockets for real-time updates
        wsClient.subscribe("object_updates", this::handleObjectUpdate);
        wsClient.subscribe("avatar_events", this::handleAvatarEvent);
    }
}
```

### 5. Avatar Animation System

**Problem**: Basic keyframe animation without modern features
```java
// Current basic animation
public class AvatarAnimation {
    private List<Keyframe> keyframes;
    
    public void updateAnimation(float deltaTime) {
        // Simple linear interpolation only
        currentFrame += deltaTime * frameRate;
        interpolateKeyframes();
    }
}
```

**Fix**: Modern skeletal animation system
```java
// New ModernAvatarAnimator.java
public class ModernAvatarAnimator {
    private SkeletonData skeleton;
    private AnimationBlendTree blendTree;
    private GPU_AnimationBuffer gpuBuffer;
    
    public void updateAnimation(float deltaTime) {
        // GPU-accelerated skeletal animation
        blendTree.update(deltaTime);
        
        // Upload bone matrices to GPU
        Matrix4f[] boneMatrices = skeleton.calculateBoneMatrices();
        gpuBuffer.uploadBoneData(boneMatrices);
        
        // Use transform feedback for cloth simulation
        clothSimulation.updateOnGPU(deltaTime);
    }
    
    public void blendAnimations(AnimationClip... clips) {
        // Modern animation blending
        blendTree.setBlendWeights(calculateBlendWeights(clips));
    }
}
```

## Modernization Roadmap

### Phase 1: Foundation (Months 1-3)
**Goal**: Establish modern development foundation

#### 1.1 Build System Modernization
- [ ] Fix Android resource conflicts
- [ ] Update Gradle and Android build tools
- [ ] Implement modern dependency management
- [ ] Add automated testing infrastructure

#### 1.2 Graphics API Baseline
- [ ] Remove OpenGL ES 1.1 support
- [ ] Establish OpenGL ES 3.0 minimum requirement
- [ ] Implement Vulkan backend for high-end devices
- [ ] Add graphics API abstraction layer

#### 1.3 Network Protocol Updates
- [ ] Implement HTTP/2 CAPS client
- [ ] Add WebSocket support for real-time events  
- [ ] Modernize message serialization (protobuf support)
- [ ] Implement OAuth2 authentication

### Phase 2: Core Systems (Months 4-8)
**Goal**: Modernize core rendering and asset systems

#### 2.1 Asset Pipeline Overhaul
- [ ] Implement Basis Universal texture support
- [ ] Add glTF 2.0 mesh loading
- [ ] Modernize animation format support
- [ ] Implement progressive mesh streaming

#### 2.2 Rendering Pipeline
- [ ] Implement Physically Based Rendering (PBR)
- [ ] Add Image-Based Lighting (IBL)
- [ ] Implement deferred rendering pipeline
- [ ] Add temporal anti-aliasing (TAA)

#### 2.3 Performance Optimization
- [ ] Multi-threaded rendering
- [ ] GPU-driven culling
- [ ] Clustered deferred lighting
- [ ] Async compute for post-processing

### Phase 3: Advanced Features (Months 9-12)
**Goal**: Implement cutting-edge features

#### 3.1 OMI Standards Integration
- [ ] glTF extensions support (OMI_collider, OMI_spawn_point)
- [ ] VRM avatar format support
- [ ] Universal Scene Description (USD) integration
- [ ] Cross-platform asset compatibility

#### 3.2 Modern Graphics Features
- [ ] Screen-space reflections
- [ ] Volumetric lighting
- [ ] Real-time global illumination
- [ ] Advanced post-processing stack

#### 3.3 WebXR Integration
- [ ] WebXR API support for browser compatibility
- [ ] VR/AR rendering pipeline
- [ ] Cross-platform input handling
- [ ] Spatial anchoring

### Phase 4: Future Technologies (Months 12+)
**Goal**: Prepare for next-generation features

#### 4.1 AI-Enhanced Graphics
- [ ] DLSS/FSR upsampling integration
- [ ] AI-driven level-of-detail
- [ ] Neural network denoising
- [ ] Intelligent asset streaming

#### 4.2 Ray Tracing Support
- [ ] Hardware ray tracing for reflections
- [ ] RT global illumination
- [ ] RT shadows and ambient occlusion
- [ ] Hybrid rasterization/ray tracing

#### 4.3 Cloud Integration
- [ ] Cloud rendering for low-end devices
- [ ] Distributed physics simulation
- [ ] Edge computing integration
- [ ] Progressive streaming from CDN

## Implementation Details

### Modern Development Environment Setup

#### Required Tools and Dependencies
```gradle
// Updated app/build.gradle
dependencies {
    // Modern graphics libraries
    implementation 'org.khronos:vulkan-loader:1.3.+'
    implementation 'org.lwjgl:lwjgl-vulkan:3.3.+'
    
    // Asset pipeline
    implementation 'com.github.KhronosGroup:glTF-Validator:+'
    implementation 'com.basis-universal:basis-loader:+'
    
    // Network modernization
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'org.eclipse.jetty.http2:http2-client:11.0.+'
    
    // Protocol buffers
    implementation 'com.google.protobuf:protobuf-java:3.24.+'
    
    // Modern UI
    implementation 'androidx.compose:compose-bom:2023.10.01'
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'
}
```

#### Development Infrastructure
```yaml
# .github/workflows/modernization-ci.yml
name: Lumiya Modernization CI
on: [push, pull_request]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Setup Android SDK
        uses: android-actions/setup-android@v2
      - name: Run modernization tests
        run: |
          ./gradlew testModernizationChanges
          ./gradlew validateGraphicsAPI
          ./gradlew benchmarkPerformance
```

### Key Implementation Files

#### 1. Modern Asset Pipeline
```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/modern/assets/
package com.lumiyaviewer.lumiya.modern.assets;

public class ModernAssetPipeline {
    private final BasisUniversalTranscoder basisTranscoder;
    private final GLTFLoader gltfLoader;
    private final DracoDecoder dracoDecoder;
    
    public CompletableFuture<Asset> loadAssetAsync(AssetReference ref) {
        return CompletableFuture.supplyAsync(() -> {
            switch (ref.getFormat()) {
                case GLTF2:
                    return loadGLTFAsset(ref);
                case BASIS_UNIVERSAL:
                    return loadBasisTexture(ref);
                case DRACO_MESH:
                    return loadDracoMesh(ref);
                default:
                    return loadLegacyAsset(ref);
            }
        });
    }
    
    private Asset loadGLTFAsset(AssetReference ref) {
        GLTFDocument gltf = gltfLoader.load(ref.getURL());
        
        // Process OMI extensions
        OMIExtensionProcessor processor = new OMIExtensionProcessor();
        processor.processExtensions(gltf);
        
        return convertToLumiyaAsset(gltf);
    }
}
```

#### 2. Vulkan Rendering Backend
```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/modern/vulkan/
package com.lumiyaviewer.lumiya.modern.vulkan;

public class VulkanRenderer implements ModernRenderer {
    private VkDevice device;
    private VkCommandPool commandPool;
    private VkRenderPass renderPass;
    
    @Override
    public void initialize(SurfaceView surfaceView) {
        // Initialize Vulkan
        createVulkanInstance();
        selectPhysicalDevice();
        createLogicalDevice();
        createSwapchain(surfaceView);
        createRenderPass();
        
        // Setup PBR pipeline
        createPBRPipeline();
    }
    
    @Override
    public void renderFrame(Scene scene, Camera camera) {
        VkCommandBuffer cmdBuffer = beginFrame();
        
        // Render scene with modern pipeline
        vkCmdBeginRenderPass(cmdBuffer, renderPass, VK_SUBPASS_CONTENTS_INLINE);
        
        // PBR pass
        renderPBRPass(scene, camera);
        
        // Post-processing
        renderPostProcessing();
        
        vkCmdEndRenderPass(cmdBuffer);
        endFrame(cmdBuffer);
    }
}
```

#### 3. Modern Network Protocol
```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/modern/protocol/
package com.lumiyaviewer.lumiya.modern.protocol;

public class ModernSLProtocol {
    private final HTTP2Client capsClient;
    private final WebSocketClient eventClient;
    private final UDPCircuit legacyCircuit;
    
    public void connectToGrid(GridInfo grid, AuthCredentials creds) {
        // Modern OAuth2 authentication
        AuthToken token = authenticateOAuth2(creds);
        
        // HTTP/2 CAPS connection for modern features
        capsClient.connect(grid.getCapsURL(), token);
        
        // WebSocket for real-time events
        eventClient.connect(grid.getEventURL(), token);
        
        // UDP fallback for legacy compatibility
        legacyCircuit.connect(grid.getSimulatorEndpoint());
    }
    
    public CompletableFuture<Void> sendMessage(ProtocolMessage message) {
        if (message.supportsModernTransport()) {
            return capsClient.sendAsync(message.toHTTP2());
        } else {
            return legacyCircuit.sendAsync(message.toUDP());
        }
    }
}
```

## Testing & Validation

### Automated Testing Framework
```java
// New file: app/src/test/java/com/lumiyaviewer/lumiya/modern/
@RunWith(AndroidJUnit4.class)
public class ModernizationValidationTests {
    
    @Test
    public void testVulkanRendererInitialization() {
        VulkanRenderer renderer = new VulkanRenderer();
        assertTrue("Vulkan should initialize successfully", 
                  renderer.initialize(mockSurface));
    }
    
    @Test
    public void testBasisUniversalTextureLoading() {
        ModernAssetPipeline pipeline = new ModernAssetPipeline();
        CompletableFuture<Texture> future = 
            pipeline.loadBasisTextureAsync(testTextureId);
        
        Texture texture = future.get(5, TimeUnit.SECONDS);
        assertNotNull("Texture should load successfully", texture);
    }
    
    @Test
    public void testModernProtocolCompatibility() {
        ModernSLProtocol protocol = new ModernSLProtocol();
        assertTrue("Should maintain legacy message compatibility",
                  protocol.supportsLegacyMessages());
    }
}
```

### Performance Benchmarking
```java
@Benchmark
public class ModernizationBenchmarks {
    
    @Benchmark
    public void benchmarkVulkanVsOpenGL() {
        // Compare rendering performance
        long vulkanTime = measureRenderTime(vulkanRenderer);
        long openglTime = measureRenderTime(openglRenderer);
        
        assertTrue("Vulkan should be faster", vulkanTime < openglTime);
    }
    
    @Benchmark
    public void benchmarkBasisVsJPEG2000() {
        // Compare texture loading performance
        long basisTime = measureTextureLoad(basisTexture);
        long jpeg2000Time = measureTextureLoad(jpeg2000Texture);
        
        assertTrue("Basis should decode faster", basisTime < jpeg2000Time);
    }
}
```

## Future Evolution

### Integration with Emerging Technologies

#### 1. WebAssembly (WASM) Integration
Future phases could include WebAssembly modules for:
- Cross-platform protocol libraries
- Shared asset processing pipelines
- Performance-critical algorithms

#### 2. Machine Learning Integration
- AI-enhanced graphics (DLSS/FSR style upsampling)
- Intelligent asset streaming based on user behavior
- Natural language processing for enhanced chat features
- Procedural content generation

#### 3. Blockchain/NFT Integration
- Decentralized asset ownership verification
- Cross-platform asset portability
- Creator economy features

#### 4. Advanced XR Features
- Mixed Reality (MR) support for real-world integration
- Haptic feedback integration
- Eye tracking for foveated rendering
- Brain-computer interface support

### Long-term Architecture Vision

```
Future Lumiya Architecture (2025+)
├── Universal Runtime
│   ├── WebAssembly Core
│   ├── Native Platform Bindings
│   └── Cross-Platform Asset System
├── AI-Enhanced Pipeline
│   ├── Intelligent Culling
│   ├── Procedural Generation
│   └── Natural Language Processing
├── XR-First Rendering
│   ├── Foveated Rendering
│   ├── Spatial Computing
│   └── Neural Radiance Fields
└── Decentralized Protocol
    ├── Blockchain Integration
    ├── Distributed Computing
    └── Edge Computing Optimization
```

This comprehensive modernization guide provides a clear roadmap for transforming Lumiya Viewer into a cutting-edge Second Life client that leverages the best practices and technologies from the modern metaverse ecosystem while maintaining compatibility with the existing Second Life infrastructure.