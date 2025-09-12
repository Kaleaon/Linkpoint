# Implementation Roadmap: LibreMetaverse, Firestorm, and Second Life Integration
## Comprehensive Implementation Guide with Extreme Detail

This document provides a comprehensive, step-by-step roadmap based on the analysis of three major Second Life ecosystem repositories and their integration potential with the Linkpoint (Lumiya Viewer) implementation.

### Analyzed Repositories:
- **LibreMetaverse** (cinderblocks/libremetaverse): Modern C#/.NET protocol library (71 stars, active development)
- **Firestorm Viewer** (FirestormViewer/phoenix-firestorm): Advanced C++ third-party viewer (76 stars, active)  
- **Second Life Viewer** (secondlife/viewer): Official Linden Lab C++ client (250 stars, reference implementation)

### Linkpoint Current Architecture Assessment:
- **6,249 Java files** with mobile-optimized Second Life client implementation
- **833 protocol implementation files** in slproto package - comprehensive SL protocol coverage
- **Advanced rendering system** located in `com.lumiyaviewer.lumiya.render` with multi-version OpenGL ES support
- **Mobile-first architecture** with sophisticated memory management and battery optimization
- **Existing integration points**: EventBus system, ModernTextureManager, comprehensive CAPS support

---

## Strategic Analysis and Integration Opportunities

### 1. Protocol Layer Modernization

**Current Integration Points:**
- `com.lumiyaviewer.lumiya.slproto.SLCircuit` - Main protocol handler (833 implementation files)
- `com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest` - CAPS HTTP support
- `com.lumiyaviewer.lumiya.slproto.auth` - Authentication system
- `com.lumiyaviewer.lumiya.slproto.caps` - Capabilities system

**Detailed Analysis:** Linkpoint already implements 833 protocol files, indicating sophisticated SL protocol coverage comparable to LibreMetaverse's 300+ message types.

**LibreMetaverse Integration Opportunities:**

#### A. HTTP/2 CAPS Enhancement
**Target Location:** `com.lumiyaviewer.lumiya.slproto.caps`

```java
// New file: com.lumiyaviewer.lumiya.slproto.caps.HTTP2CapsClient
public class HTTP2CapsClient extends AbstractCapsHandler {
    private final OkHttpClient http2Client;
    private final ConcurrentHashMap<String, CompletableFuture<CapsResponse>> pendingRequests;
    private final ExecutorService asyncExecutor;
    
    // Integration with existing CAPS system
    private final ExistingCapsManager legacyManager;
    
    public HTTP2CapsClient(ExistingCapsManager legacyManager) {
        this.legacyManager = legacyManager;
        this.http2Client = new OkHttpClient.Builder()
            .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
            .build();
        this.asyncExecutor = Executors.newCachedThreadPool();
        this.pendingRequests = new ConcurrentHashMap<>();
    }
    
    @Override
    public CompletableFuture<CapsResponse> sendCapsRequest(CapsRequest request) {
        // Fallback logic for mobile networks
        if (NetworkUtils.isMobileNetwork() && !NetworkUtils.hasStrongSignal()) {
            return legacyManager.sendCapsRequest(request); // Use existing HTTP/1.1
        }
        
        // HTTP/2 multiplexing for bulk operations
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request http2Request = new Request.Builder()
                    .url(request.getUrl())
                    .method(request.getMethod(), createRequestBody(request))
                    .addHeader("User-Agent", "Linkpoint-Mobile/1.0")
                    .build();
                    
                Response response = http2Client.newCall(http2Request).execute();
                return parseCapsResponse(response);
            } catch (IOException e) {
                // Fallback to legacy CAPS on network errors
                return legacyManager.sendCapsRequest(request).join();
            }
        }, asyncExecutor);
    }
    
    // Mobile-specific optimizations
    public void optimizeForMobile() {
        // Compress request bodies for mobile networks
        enableGzipCompression();
        // Batch small requests together
        enableRequestBatching();
        // Prioritize critical requests
        enableRequestPrioritization();
    }
}
```

#### B. WebSocket Event Streaming
**Target Location:** `com.lumiyaviewer.lumiya.slproto.events`

```java
// New file: com.lumiyaviewer.lumiya.slproto.events.WebSocketEventStream
public class WebSocketEventStream {
    private final WebSocket webSocket;
    private final EventBus eventBus; // Use existing Linkpoint EventBus
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    
    // Integration with existing UDP circuit
    private final SLCircuit udpFallback;
    
    public WebSocketEventStream(SLCircuit udpFallback, EventBus eventBus) {
        this.udpFallback = udpFallback;
        this.eventBus = eventBus;
    }
    
    public void connect(String wsUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS) // Keep-alive connection
            .build();
            
        Request request = new Request.Builder()
            .url(wsUrl)
            .build();
            
        this.webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                handleWebSocketMessage(text);
            }
            
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // Fallback to UDP for reliability
                isConnected.set(false);
                udpFallback.enableFullUDPMode();
            }
        });
    }
    
    private void handleWebSocketMessage(String message) {
        try {
            // Parse WebSocket message and convert to Linkpoint event format
            SLWebSocketMessage wsMessage = parseWebSocketMessage(message);
            
            // Convert to existing event system
            switch (wsMessage.getType()) {
                case "chat":
                    eventBus.post(new ChatMessageEvent(wsMessage.getData()));
                    break;
                case "object_update":
                    eventBus.post(new ObjectUpdateEvent(wsMessage.getData()));
                    break;
                // ... other message types
            }
        } catch (Exception e) {
            // Log error but don't crash - mobile resilience
            Log.w("WebSocketEventStream", "Failed to parse message", e);
        }
    }
    
    // Mobile battery optimization
    public void enableBatteryOptimization() {
        if (getBatteryLevel() < 0.2f) { // Less than 20% battery
            // Reduce WebSocket message frequency
            setMessageRateLimit(5000); // 5 second intervals
            // Fall back to UDP for critical messages only
            udpFallback.enableCriticalOnlyMode();
        }
    }
}
```

#### C. Enhanced Authentication System
**Target Location:** `com.lumiyaviewer.lumiya.slproto.auth`

```java
// Enhancement to existing auth system
public class OAuth2AuthenticationHandler {
    // Integrate with existing authentication classes
    private final ExistingAuthManager existingAuth;
    private final SecureStorage secureStorage;
    
    public OAuth2AuthenticationHandler(ExistingAuthManager existingAuth) {
        this.existingAuth = existingAuth;
        this.secureStorage = new AndroidKeystoreSecureStorage();
    }
    
    public CompletableFuture<AuthResult> authenticateAsync(LoginCredentials credentials) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Try OAuth2 first (modern auth)
                OAuth2Result oauth2Result = performOAuth2Authentication(credentials);
                if (oauth2Result.isSuccessful()) {
                    return new AuthResult(oauth2Result);
                }
            } catch (OAuth2Exception e) {
                Log.w("OAuth2Auth", "OAuth2 failed, falling back to legacy", e);
            }
            
            // Fallback to existing authentication system
            return existingAuth.authenticate(credentials);
        });
    }
    
    private OAuth2Result performOAuth2Authentication(LoginCredentials credentials) {
        // Implementation of OAuth2 flow specifically for mobile
        // Includes PKCE for security, refresh token handling, etc.
        
        // Mobile-specific considerations:
        // - Store tokens securely using Android Keystore
        // - Handle app lifecycle for token refresh
        // - Graceful degradation for network issues
        
        OAuthConfig config = new OAuthConfig.Builder()
            .clientId("linkpoint-mobile")
            .redirectUri("linkpoint://oauth-callback")
            .scope("agent:read agent:write inventory:read")
            .enablePKCE() // More secure for mobile
            .build();
            
        return performOAuthFlow(config, credentials);
    }
}
```

### 2. Rendering System Enhancement

**Current Integration Points:**
- `com.lumiyaviewer.lumiya.render.ModernTextureManager` - Texture management
- `com.lumiyaviewer.lumiya.render.tex` - Texture system (7+ classes)
- `com.lumiyaviewer.lumiya.render.spatial` - Spatial indexing and culling

**Detailed Analysis:** Linkpoint already has sophisticated rendering with ModernTextureManager and multi-version OpenGL ES support.

**Firestorm-Inspired PBR Integration:**

#### A. Adaptive PBR Material System
**Target Location:** `com.lumiyaviewer.lumiya.render.materials` (new package)

```java
// New file: com.lumiyaviewer.lumiya.render.materials.PBRMaterialSystem
public class PBRMaterialSystem {
    // Integration with existing texture system
    private final ModernTextureManager textureManager;
    private final DeviceCapabilityDetector deviceDetector;
    
    // PBR material caches
    private final LRUCache<UUID, PBRMaterial> materialCache;
    private final LRUCache<UUID, CompiledShader> shaderCache;
    
    public PBRMaterialSystem(ModernTextureManager textureManager) {
        this.textureManager = textureManager;
        this.deviceDetector = new DeviceCapabilityDetector();
        this.materialCache = new LRUCache<>(200); // Adjust for mobile memory
        this.shaderCache = new LRUCache<>(50);   // Limited shader variants
    }
    
    public void renderPrimitive(SLPrimitive primitive, RenderContext context) {
        // Adaptive rendering based on device capabilities
        if (deviceDetector.supportsAdvancedPBR()) {
            renderAdvancedPBR(primitive, context);
        } else if (deviceDetector.supportsBasicPBR()) {
            renderSimplifiedPBR(primitive, context);
        } else {
            // Fallback to existing rendering system
            fallbackToLegacyRenderer(primitive, context);
        }
    }
    
    private void renderAdvancedPBR(SLPrimitive primitive, RenderContext context) {
        // Full PBR implementation for high-end devices
        PBRMaterial material = getMaterial(primitive.getMaterialId());
        
        // Enable all PBR features
        enableMetallicRoughnessWorkflow();
        enableImageBasedLighting();
        enableEnvironmentReflections();
        
        // Bind PBR textures using existing texture manager
        bindPBRTextures(material, context);
        
        // Use advanced shader with full PBR pipeline
        CompiledShader pbrShader = getOrCompileShader("advanced_pbr", context);
        pbrShader.use();
        
        // Set PBR uniforms
        setPBRUniforms(material, context);
        
        // Render with full quality
        renderWithPBR(primitive, context);
    }
    
    private void renderSimplifiedPBR(SLPrimitive primitive, RenderContext context) {
        // Simplified PBR for mid-range devices
        PBRMaterial material = getMaterial(primitive.getMaterialId());
        
        // Enable essential PBR features only
        enableBasicMetallicRoughness();
        disableEnvironmentReflections(); // Save performance
        
        // Use simplified shader
        CompiledShader simplePbrShader = getOrCompileShader("simple_pbr", context);
        simplePbrShader.use();
        
        // Render with reduced quality but maintained visual fidelity
        renderWithSimplifiedPBR(primitive, context);
    }
    
    // Device capability detection for mobile optimization
    private class DeviceCapabilityDetector {
        public boolean supportsAdvancedPBR() {
            return context.getOpenGLESVersion() >= 3.0 &&
                   getTotalRAM() > 4L * 1024 * 1024 * 1024 && // 4GB+ RAM
                   getBatteryLevel() > 0.3f; // 30%+ battery
        }
        
        public boolean supportsBasicPBR() {
            return context.getOpenGLESVersion() >= 2.0 &&
                   getTotalRAM() > 2L * 1024 * 1024 * 1024; // 2GB+ RAM
        }
    }
}
```

#### B. Advanced Lighting System
**Target Location:** `com.lumiyaviewer.lumiya.render.lighting` (new package)

```java
// New file: com.lumiyaviewer.lumiya.render.lighting.DynamicLightingManager
public class DynamicLightingManager {
    // Integration with existing spatial indexing
    private final SpatialObjectIndex spatialIndex;
    private final List<DynamicLight> activeLights;
    private final int maxLights;
    
    public DynamicLightingManager(SpatialObjectIndex spatialIndex) {
        this.spatialIndex = spatialIndex;
        this.activeLights = new ArrayList<>();
        // Mobile optimization: limit active lights based on device performance
        this.maxLights = calculateMaxLights();
    }
    
    private int calculateMaxLights() {
        // Adaptive light count based on device capabilities
        if (isHighEndDevice()) {
            return 8; // High-end: 8 dynamic lights
        } else if (isMidRangeDevice()) {
            return 4; // Mid-range: 4 dynamic lights
        } else {
            return 2; // Low-end: 2 dynamic lights
        }
    }
    
    public void updateLighting(Vector3 cameraPosition, RenderContext context) {
        // Use existing spatial indexing to find nearby lights efficiently
        List<SLLightSource> nearbyLights = spatialIndex.findLightsInRadius(
            cameraPosition, 
            getLightCullDistance()
        );
        
        // Sort by distance and importance
        nearbyLights.sort((a, b) -> {
            float distA = Vector3.distance(a.getPosition(), cameraPosition);
            float distB = Vector3.distance(b.getPosition(), cameraPosition);
            
            // Weight by importance and distance
            float scoreA = a.getIntensity() / (distA + 1.0f);
            float scoreB = b.getIntensity() / (distB + 1.0f);
            
            return Float.compare(scoreB, scoreA); // Higher score first
        });
        
        // Update active lights list (limited by maxLights)
        activeLights.clear();
        for (int i = 0; i < Math.min(nearbyLights.size(), maxLights); i++) {
            activeLights.add(convertToDynamicLight(nearbyLights.get(i)));
        }
        
        // Update shader uniforms
        updateLightingUniforms(context);
    }
    
    private void updateLightingUniforms(RenderContext context) {
        // Bind lighting data to current shader
        Shader activeShader = context.getCurrentShader();
        
        activeShader.setInt("u_numLights", activeLights.size());
        
        for (int i = 0; i < activeLights.size(); i++) {
            DynamicLight light = activeLights.get(i);
            String prefix = "u_lights[" + i + "]";
            
            activeShader.setVector3(prefix + ".position", light.getPosition());
            activeShader.setVector3(prefix + ".color", light.getColor());
            activeShader.setFloat(prefix + ".intensity", light.getIntensity());
            activeShader.setFloat(prefix + ".range", light.getRange());
            activeShader.setInt(prefix + ".type", light.getType().ordinal());
        }
    }
    
    // Mobile battery optimization
    public void adjustForBatteryLevel(float batteryLevel) {
        if (batteryLevel < 0.2f) { // Low battery
            maxLights = Math.max(1, maxLights / 2); // Reduce light count
            disableAdvancedLightingFeatures();
        } else if (batteryLevel < 0.5f) { // Medium battery
            maxLights = (int) (maxLights * 0.75f); // Reduce by 25%
        }
    }
}
```

### 3. Asset Management Modernization

**Current Integration Points:**
- `com.lumiyaviewer.lumiya.render.ModernTextureManager` - Already implements modern texture handling
- `com.lumiyaviewer.lumiya.slproto.assets` - Asset management system
- `com.lumiyaviewer.lumiya.slproto.textures` - Texture protocol handling

**Detailed Analysis:** Linkpoint already has ModernTextureManager, indicating advanced texture capabilities.

**Multi-Repository Inspired Improvements:**

#### A. Enhanced Modern Asset Pipeline  
**Target Location:** Enhance existing `com.lumiyaviewer.lumiya.render.ModernTextureManager`

```java
// Enhancement to existing ModernTextureManager class
public class EnhancedModernTextureManager extends ModernTextureManager {
    // Additional capabilities from repository analysis
    private final HTTP2AssetFetcher http2Fetcher;
    private final BasisUniversalDecoder basisDecoder;
    private final ASTCTranscoder astcTranscoder;
    private final TieredAssetCache tieredCache;
    
    public EnhancedModernTextureManager(Context context) {
        super(context); // Initialize existing functionality
        
        // Add new capabilities
        this.http2Fetcher = new HTTP2AssetFetcher(context);
        this.basisDecoder = new BasisUniversalDecoder(); // From existing C++ integration
        this.astcTranscoder = new ASTCTranscoder();
        this.tieredCache = new TieredAssetCache(context);
    }
    
    @Override
    public CompletableFuture<Texture> fetchTexture(UUID textureId, TexturePriority priority) {
        // Enhanced fetch logic with multiple fallbacks
        return tieredCache.getAsync(textureId)
            .orElseGet(() -> fetchFromNetwork(textureId, priority))
            .thenCompose(this::processTexture)
            .thenApply(texture -> {
                // Cache the processed texture
                tieredCache.putAsync(textureId, texture, priority);
                return texture;
            })
            .exceptionally(error -> {
                // Fallback to parent implementation on error
                Log.w("EnhancedTextureManager", "Enhanced fetch failed, using fallback", error);
                return super.fetchTexture(textureId, priority).join();
            });
    }
    
    private CompletableFuture<RawTextureData> fetchFromNetwork(UUID textureId, TexturePriority priority) {
        // Try HTTP/2 first for efficiency
        if (NetworkUtils.supportsHTTP2() && !NetworkUtils.isLimitedConnection()) {
            return http2Fetcher.fetchAsync(textureId, priority)
                .exceptionally(error -> {
                    Log.d("EnhancedTextureManager", "HTTP/2 fetch failed, trying legacy");
                    // Fallback to existing fetch mechanism
                    return super.fetchTextureData(textureId, priority).join();
                });
        } else {
            // Use existing fetch for limited connections
            return super.fetchTextureData(textureId, priority);
        }
    }
    
    private CompletableFuture<Texture> processTexture(RawTextureData rawData) {
        return CompletableFuture.supplyAsync(() -> {
            // Determine optimal format for device
            TextureFormat optimalFormat = determineOptimalFormat(rawData);
            
            switch (optimalFormat) {
                case ASTC:
                    return astcTranscoder.transcode(rawData);
                case BASIS_UNIVERSAL:
                    return basisDecoder.decode(rawData);
                case LEGACY_COMPRESSED:
                    return processLegacyTexture(rawData); // Existing logic
                default:
                    return createUncompressedTexture(rawData);
            }
        });
    }
    
    private TextureFormat determineOptimalFormat(RawTextureData rawData) {
        // Mobile GPU optimization
        if (supportsASTC() && rawData.isHighResolution()) {
            return TextureFormat.ASTC; // Best compression for modern mobile GPUs
        } else if (supportsBasisUniversal()) {
            return TextureFormat.BASIS_UNIVERSAL; // Universal format
        } else {
            return TextureFormat.LEGACY_COMPRESSED; // Fallback
        }
    }
    
    // Integration with existing tiered caching strategy
    private class TieredAssetCache {
        private final LRUCache<UUID, Texture> memoryCache;
        private final DiskLRUCache diskCache;
        private final CloudCache cloudBackup;
        
        public TieredAssetCache(Context context) {
            // Configure cache sizes based on device capabilities
            long maxMemory = Runtime.getRuntime().maxMemory();
            int memoryCacheSize = (int) (maxMemory / 8); // 1/8 of max memory
            
            this.memoryCache = new LRUCache<>(memoryCacheSize);
            this.diskCache = DiskLRUCache.open(
                new File(context.getCacheDir(), "textures"), 
                1, 1, 
                100 * 1024 * 1024 // 100MB disk cache
            );
            this.cloudBackup = new CloudCache(context); // Optional cloud backup
        }
        
        public Optional<CompletableFuture<Texture>> getAsync(UUID textureId) {
            // Try memory cache first
            Texture memoryTexture = memoryCache.get(textureId);
            if (memoryTexture != null) {
                return Optional.of(CompletableFuture.completedFuture(memoryTexture));
            }
            
            // Try disk cache
            return Optional.of(
                CompletableFuture.supplyAsync(() -> {
                    try {
                        DiskLRUCache.Snapshot snapshot = diskCache.get(textureId.toString());
                        if (snapshot != null) {
                            Texture diskTexture = loadTextureFromDisk(snapshot);
                            memoryCache.put(textureId, diskTexture); // Promote to memory
                            return diskTexture;
                        }
                    } catch (IOException e) {
                        Log.w("TieredAssetCache", "Disk cache read failed", e);
                    }
                    
                    // Not found in cache
                    throw new CacheException("Texture not found in cache");
                })
            );
        }
        
        public void putAsync(UUID textureId, Texture texture, TexturePriority priority) {
            // Store in memory cache immediately
            memoryCache.put(textureId, texture);
            
            // Store in disk cache asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    DiskLRUCache.Editor editor = diskCache.edit(textureId.toString());
                    if (editor != null) {
                        saveTextureToDisk(texture, editor.newOutputStream(0));
                        editor.commit();
                    }
                } catch (IOException e) {
                    Log.w("TieredAssetCache", "Disk cache write failed", e);
                }
            });
            
            // Optionally backup to cloud for high-priority textures
            if (priority == TexturePriority.HIGH && cloudBackup.isEnabled()) {
                cloudBackup.backupAsync(textureId, texture);
            }
        }
    }
}
```

---

## Detailed Implementation Phases

### Phase 1: Foundation Layer (Months 1-6) - DETAILED IMPLEMENTATION

**Estimated Effort:** 600-800 development hours across 3 senior developers
**Risk Level:** Medium - Building on existing stable foundation

#### Phase 1.1: HTTP/2 CAPS Integration (Months 1-2)
**Prerequisites:** Complete audit of existing `com.lumiyaviewer.lumiya.slproto.caps` package

**Week 1-2: Requirements Analysis and Architecture Design**
- **Task 1.1.1:** Audit existing CAPS implementation
  - **Location:** `com.lumiyaviewer.lumiya.slproto.caps`
  - **Deliverable:** Document current CAPS message types and usage patterns
  - **Validation:** Generate CAPS usage metrics from existing codebase
  
- **Task 1.1.2:** Design HTTP/2 integration strategy
  - **Deliverable:** Architecture document showing HTTP/2 integration points
  - **Key Decisions:** 
    - Fallback strategy for HTTP/1.1 compatibility
    - Connection pooling strategy for mobile battery optimization
    - Request prioritization system
  
**Week 3-4: Core HTTP/2 Infrastructure**
- **Task 1.1.3:** Implement HTTP2CapsClient class
  ```java
  // File: com/lumiyaviewer/lumiya/slproto/caps/HTTP2CapsClient.java
  
  public class HTTP2CapsClient implements ICapsClient {
      // Integration with existing EventBus system
      private final EventBus eventBus;
      private final ExistingCapsManager legacyManager;
      
      // HTTP/2 specific components
      private final OkHttpClient http2Client;
      private final RequestPrioritizer prioritizer;
      private final ConnectionPoolManager poolManager;
      
      public HTTP2CapsClient(EventBus eventBus, ExistingCapsManager legacyManager) {
          this.eventBus = eventBus;
          this.legacyManager = legacyManager;
          
          // Configure HTTP/2 with mobile optimizations
          this.http2Client = new OkHttpClient.Builder()
              .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
              .connectionPool(new ConnectionPool(3, 5, TimeUnit.MINUTES)) // Conservative for mobile
              .readTimeout(30, TimeUnit.SECONDS) // Generous timeout for mobile networks
              .connectTimeout(15, TimeUnit.SECONDS)
              .retryOnConnectionFailure(true)
              .addInterceptor(new MobileBatteryInterceptor()) // Custom mobile optimization
              .build();
              
          this.prioritizer = new RequestPrioritizer();
          this.poolManager = new ConnectionPoolManager(this.http2Client);
      }
      
      @Override
      public CompletableFuture<CapsResponse> sendCapsRequest(CapsRequest request) {
          // Mobile network condition check
          NetworkCondition condition = NetworkUtils.getCurrentNetworkCondition();
          
          if (shouldUseLegacyCAPS(condition, request)) {
              return legacyManager.sendCapsRequest(request);
          }
          
          return executeHTTP2Request(request);
      }
      
      private boolean shouldUseLegacyCAPS(NetworkCondition condition, CapsRequest request) {
          return condition == NetworkCondition.POOR ||
                 condition == NetworkCondition.OFFLINE ||
                 (condition == NetworkCondition.LIMITED && request.getPriority() == Priority.LOW) ||
                 getBatteryLevel() < 0.1f; // Less than 10% battery
      }
      
      private CompletableFuture<CapsResponse> executeHTTP2Request(CapsRequest request) {
          return CompletableFuture.supplyAsync(() -> {
              try {
                  Request http2Request = buildHTTP2Request(request);
                  Response response = http2Client.newCall(http2Request).execute();
                  
                  if (response.isSuccessful()) {
                      CapsResponse capsResponse = parseCapsResponse(response);
                      
                      // Emit event through existing EventBus
                      eventBus.post(new CapsResponseEvent(request.getType(), capsResponse));
                      
                      return capsResponse;
                  } else {
                      throw new CapsException("HTTP/2 request failed: " + response.code());
                  }
              } catch (IOException | CapsException e) {
                  Log.w("HTTP2CapsClient", "HTTP/2 request failed, falling back to legacy", e);
                  // Automatic fallback
                  return legacyManager.sendCapsRequest(request).join();
              }
          }, getAsyncExecutor());
      }
  }
  ```
  
  **Testing Requirements:**
  - Unit tests for HTTP/2 client initialization
  - Integration tests with existing CAPS system
  - Network condition simulation tests
  - Fallback mechanism validation tests
  
**Week 5-6: Mobile Battery Optimization Integration**
- **Task 1.1.4:** Implement MobileBatteryInterceptor
  ```java
  // Custom interceptor for mobile battery optimization
  public class MobileBatteryInterceptor implements Interceptor {
      @Override
      public Response intercept(Chain chain) throws IOException {
          Request originalRequest = chain.request();
          
          // Battery level check
          float batteryLevel = BatteryUtils.getCurrentLevel();
          
          if (batteryLevel < 0.2f) { // Low battery mode
              // Reduce request timeout
              Request.Builder builder = originalRequest.newBuilder()
                  .header("X-Mobile-Battery", "low")
                  .header("X-Prefer-Compression", "true");
                  
              // Enable aggressive compression
              if (originalRequest.body() != null) {
                  RequestBody compressedBody = new GzipRequestBody(originalRequest.body());
                  builder.method(originalRequest.method(), compressedBody);
              }
              
              originalRequest = builder.build();
          }
          
          return chain.proceed(originalRequest);
      }
  }
  ```

**Week 7-8: Testing and Validation**
- **Task 1.1.5:** Comprehensive testing suite
  - **Performance Tests:** Measure HTTP/2 vs HTTP/1.1 performance on various mobile networks
  - **Battery Impact Tests:** Validate battery consumption improvements
  - **Fallback Tests:** Ensure seamless degradation under poor network conditions
  - **Load Tests:** Test connection pooling under high CAPS usage

**Deliverables for Phase 1.1:**
- ✅ HTTP2CapsClient implementation
- ✅ MobileBatteryInterceptor for power optimization  
- ✅ Comprehensive test suite with 90%+ coverage
- ✅ Performance benchmarks showing 15-30% improvement in bulk operations
- ✅ Documentation for integration with existing CAPS system

#### Phase 1.2: WebSocket Event Streaming (Months 2-3)
**Prerequisites:** HTTP/2 CAPS integration complete and tested

**Week 9-10: WebSocket Architecture Design**
- **Task 1.2.1:** Design event streaming architecture
  - **Integration Point:** `com.lumiyaviewer.lumiya.slproto.events`
  - **Key Requirement:** Seamless integration with existing EventBus system
  - **Architecture Decision:** Hybrid WebSocket + UDP approach
  
```java
// File: com/lumiyaviewer/lumiya/slproto/events/HybridEventManager.java

public class HybridEventManager {
    // Existing systems
    private final EventBus eventBus; // Use existing Linkpoint EventBus
    private final SLCircuit udpCircuit; // Existing UDP protocol handler
    
    // New WebSocket capabilities
    private final WebSocketEventStream webSocketStream;
    private final EventTypeRouter eventRouter;
    private final ConnectionFailureHandler failureHandler;
    
    // Mobile optimization components
    private final BatteryAwareScheduler scheduler;
    private final NetworkConditionMonitor networkMonitor;
    
    public HybridEventManager(EventBus eventBus, SLCircuit udpCircuit) {
        this.eventBus = eventBus;
        this.udpCircuit = udpCircuit;
        
        this.webSocketStream = new WebSocketEventStream();
        this.eventRouter = new EventTypeRouter();
        this.failureHandler = new ConnectionFailureHandler(udpCircuit);
        
        this.scheduler = new BatteryAwareScheduler();
        this.networkMonitor = new NetworkConditionMonitor();
        
        setupEventRouting();
    }
    
    private void setupEventRouting() {
        // Define which events use WebSocket vs UDP based on characteristics
        eventRouter.addRule(EventType.CHAT_MESSAGE, TransportMode.WEBSOCKET); // Real-time, low priority
        eventRouter.addRule(EventType.OBJECT_UPDATE, TransportMode.HYBRID); // Depends on update frequency
        eventRouter.addRule(EventType.AGENT_MOVEMENT, TransportMode.UDP); // Critical, low latency required
        eventRouter.addRule(EventType.INVENTORY_UPDATE, TransportMode.WEBSOCKET); // Bulk, non-critical
    }
    
    public void startEventStreaming(String webSocketUrl) {
        // Start WebSocket connection with fallback strategy
        webSocketStream.connect(webSocketUrl)
            .thenAccept(connection -> {
                Log.i("HybridEventManager", "WebSocket connected, enabling hybrid mode");
                enableHybridMode();
            })
            .exceptionally(error -> {
                Log.w("HybridEventManager", "WebSocket connection failed, using UDP only", error);
                enableUDPOnlyMode();
                return null;
            });
    }
    
    private void enableHybridMode() {
        // Route events based on optimal transport
        eventBus.register(SLEvent.class, this::routeEvent);
        
        // Monitor connection health
        scheduler.scheduleAtFixedRate(this::monitorConnectionHealth, 
            30, 30, TimeUnit.SECONDS);
    }
    
    private void routeEvent(SLEvent event) {
        TransportMode mode = eventRouter.getTransportMode(event.getType());
        NetworkCondition condition = networkMonitor.getCurrentCondition();
        
        // Adapt routing based on current conditions
        TransportMode actualMode = adaptTransportForConditions(mode, condition, event);
        
        switch (actualMode) {
            case WEBSOCKET:
                webSocketStream.sendEvent(event);
                break;
            case UDP:
                udpCircuit.sendEvent(event);
                break;
            case HYBRID:
                // Use WebSocket for non-critical, UDP for critical
                if (event.getPriority() == Priority.CRITICAL) {
                    udpCircuit.sendEvent(event);
                } else {
                    webSocketStream.sendEvent(event);
                }
                break;
        }
    }
    
    private TransportMode adaptTransportForConditions(TransportMode preferred, 
                                                      NetworkCondition condition, 
                                                      SLEvent event) {
        // Battery optimization: prefer WebSocket when battery is good
        if (getBatteryLevel() > 0.8f && condition == NetworkCondition.GOOD) {
            return TransportMode.WEBSOCKET; // More efficient for bulk operations
        }
        
        // Poor network: prefer UDP for reliability
        if (condition == NetworkCondition.POOR || condition == NetworkCondition.LIMITED) {
            return TransportMode.UDP;
        }
        
        // Low battery: minimize connections
        if (getBatteryLevel() < 0.2f) {
            // Prefer whichever connection is already established
            return webSocketStream.isConnected() ? TransportMode.WEBSOCKET : TransportMode.UDP;
        }
        
        return preferred; // Use original preference
    }
}
```

**Week 11-12: WebSocket Implementation with Mobile Optimizations**

```java
// File: com/lumiyaviewer/lumiya/slproto/events/WebSocketEventStream.java

public class WebSocketEventStream {
    private WebSocket webSocket;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final ReconnectionStrategy reconnectionStrategy;
    private final MessageQueue messageQueue; // For offline message buffering
    private final EventBus eventBus;
    
    // Mobile-specific optimizations
    private final BatteryOptimizer batteryOptimizer;
    private final NetworkAdaptiveScheduler scheduler;
    private final CompressionHandler compressionHandler;
    
    public WebSocketEventStream(EventBus eventBus) {
        this.eventBus = eventBus;
        this.reconnectionStrategy = new ExponentialBackoffReconnection();
        this.messageQueue = new MessageQueue(1000); // Buffer up to 1000 messages
        
        this.batteryOptimizer = new BatteryOptimizer();
        this.scheduler = new NetworkAdaptiveScheduler();
        this.compressionHandler = new CompressionHandler();
    }
    
    public CompletableFuture<WebSocketConnection> connect(String wsUrl) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OkHttpClient client = buildOptimizedClient();
                
                Request request = new Request.Builder()
                    .url(wsUrl)
                    .addHeader("User-Agent", "Linkpoint-Mobile/1.0")
                    .addHeader("X-Device-Type", "mobile")
                    .addHeader("X-Battery-Level", String.valueOf(getBatteryLevel()))
                    .build();
                
                CompletableFuture<WebSocketConnection> connectionFuture = new CompletableFuture<>();
                
                this.webSocket = client.newWebSocket(request, new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        isConnected.set(true);
                        Log.i("WebSocketEventStream", "WebSocket connected");
                        
                        // Start mobile optimizations
                        batteryOptimizer.startOptimization();
                        scheduler.startAdaptiveScheduling();
                        
                        // Send buffered messages
                        flushMessageQueue();
                        
                        connectionFuture.complete(new WebSocketConnection(webSocket));
                    }
                    
                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        handleIncomingMessage(text);
                    }
                    
                    @Override
                    public void onMessage(WebSocket webSocket, ByteString bytes) {
                        // Handle compressed binary messages
                        String decompressed = compressionHandler.decompress(bytes.toByteArray());
                        handleIncomingMessage(decompressed);
                    }
                    
                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                        isConnected.set(false);
                        Log.w("WebSocketEventStream", "WebSocket connection failed", t);
                        
                        // Trigger reconnection strategy
                        reconnectionStrategy.scheduleReconnection(() -> connect(wsUrl));
                        
                        connectionFuture.completeExceptionally(t);
                    }
                    
                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                        isConnected.set(false);
                        Log.i("WebSocketEventStream", "WebSocket closed: " + reason);
                        
                        // Clean shutdown of optimizations
                        batteryOptimizer.stopOptimization();
                        scheduler.stopAdaptiveScheduling();
                    }
                });
                
                return connectionFuture.get(15, TimeUnit.SECONDS); // 15 second connect timeout
                
            } catch (Exception e) {
                throw new WebSocketException("Failed to establish WebSocket connection", e);
            }
        });
    }
    
    private OkHttpClient buildOptimizedClient() {
        return new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS) // Keep-alive connection
            .connectTimeout(15, TimeUnit.SECONDS) // Generous for mobile networks
            .pingInterval(30, TimeUnit.SECONDS) // Keep connection alive
            .retryOnConnectionFailure(true)
            .addInterceptor(new MobileWebSocketInterceptor())
            .build();
    }
    
    private void handleIncomingMessage(String message) {
        try {
            // Parse WebSocket message format
            WebSocketMessage wsMessage = JsonUtils.fromJson(message, WebSocketMessage.class);
            
            // Convert to existing Linkpoint event system
            SLEvent slEvent = convertToSLEvent(wsMessage);
            
            // Post to existing EventBus
            eventBus.post(slEvent);
            
        } catch (Exception e) {
            Log.w("WebSocketEventStream", "Failed to process incoming message", e);
            // Don't crash on message parse errors - mobile resilience
        }
    }
    
    private SLEvent convertToSLEvent(WebSocketMessage wsMessage) {
        // Map WebSocket message types to existing Linkpoint event types
        switch (wsMessage.getType()) {
            case "chat":
                return new ChatMessageEvent(wsMessage.getData());
            case "object_update":
                return new ObjectUpdateEvent(wsMessage.getData());
            case "inventory_update":
                return new InventoryUpdateEvent(wsMessage.getData());
            case "agent_update":
                return new AgentUpdateEvent(wsMessage.getData());
            default:
                return new GenericSLEvent(wsMessage.getType(), wsMessage.getData());
        }
    }
    
    // Mobile battery optimization
    private class BatteryOptimizer {
        private ScheduledExecutorService scheduler;
        private volatile boolean optimizationEnabled = false;
        
        public void startOptimization() {
            if (optimizationEnabled) return;
            
            optimizationEnabled = true;
            scheduler = Executors.newSingleThreadScheduledExecutor();
            
            // Monitor battery level every 5 minutes
            scheduler.scheduleAtFixedRate(this::adjustForBatteryLevel, 0, 5, TimeUnit.MINUTES);
        }
        
        private void adjustForBatteryLevel() {
            float batteryLevel = getBatteryLevel();
            
            if (batteryLevel < 0.15f) { // Critical battery level
                // Reduce message frequency dramatically
                setMessageRateLimit(60000); // 1 message per minute max
                enableAggressiveCompression();
                
            } else if (batteryLevel < 0.3f) { // Low battery level
                // Reduce message frequency moderately
                setMessageRateLimit(10000); // 1 message per 10 seconds max
                enableStandardCompression();
                
            } else { // Normal battery levels
                // Full message frequency
                setMessageRateLimit(1000); // 1 message per second max
                disableCompression(); // Save CPU cycles
            }
        }
        
        public void stopOptimization() {
            optimizationEnabled = false;
            if (scheduler != null) {
                scheduler.shutdown();
            }
        }
    }
}
```

**Week 13-14: Integration Testing and Validation**
- **Task 1.2.2:** Integration testing with existing EventBus
- **Task 1.2.3:** Network condition simulation testing
- **Task 1.2.4:** Battery optimization validation
- **Task 1.2.5:** Load testing under various mobile scenarios

**Deliverables for Phase 1.2:**
- ✅ HybridEventManager implementation
- ✅ WebSocketEventStream with mobile optimizations
- ✅ Battery-aware message routing and rate limiting
- ✅ Comprehensive test suite covering all network conditions
- ✅ Performance metrics showing improved real-time responsiveness

#### Phase 1.3: Enhanced Authentication System (Months 3-4)

**Week 15-16: OAuth2 Architecture and Security Design**
- **Task 1.3.1:** Design OAuth2 integration with existing auth system
  - **Integration Point:** `com.lumiyaviewer.lumiya.slproto.auth`
  - **Security Requirements:** Android Keystore integration, PKCE support, secure token storage
  
```java
// File: com/lumiyaviewer/lumiya/slproto/auth/OAuth2AuthenticationManager.java

public class OAuth2AuthenticationManager {
    // Integration with existing authentication
    private final ExistingAuthManager existingAuth;
    private final SecureTokenStorage tokenStorage;
    private final EventBus eventBus;
    
    // OAuth2 specific components
    private final OAuthClient oauthClient;
    private final PKCEGenerator pkceGenerator;
    private final TokenRefreshScheduler refreshScheduler;
    
    // Mobile security components
    private final AndroidKeystoreWrapper keystoreWrapper;
    private final BiometricAuthenticator biometricAuth;
    
    public OAuth2AuthenticationManager(ExistingAuthManager existingAuth, EventBus eventBus) {
        this.existingAuth = existingAuth;
        this.eventBus = eventBus;
        
        this.tokenStorage = new AndroidKeystoreTokenStorage();
        this.oauthClient = new OAuthClient();
        this.pkceGenerator = new PKCEGenerator();
        this.refreshScheduler = new TokenRefreshScheduler();
        
        this.keystoreWrapper = new AndroidKeystoreWrapper();
        this.biometricAuth = new BiometricAuthenticator();
    }
    
    public CompletableFuture<AuthResult> authenticateAsync(LoginCredentials credentials) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check if we have valid cached OAuth2 tokens
                Optional<OAuth2Tokens> cachedTokens = tokenStorage.getCachedTokens(credentials.getUsername());
                
                if (cachedTokens.isPresent() && !cachedTokens.get().isExpired()) {
                    // Use cached tokens
                    return createAuthResult(cachedTokens.get(), credentials);
                }
                
                // Try refresh token if available
                if (cachedTokens.isPresent() && cachedTokens.get().hasValidRefreshToken()) {
                    Optional<OAuth2Tokens> refreshedTokens = refreshTokens(cachedTokens.get());
                    if (refreshedTokens.isPresent()) {
                        return createAuthResult(refreshedTokens.get(), credentials);
                    }
                }
                
                // Perform new OAuth2 authentication
                OAuth2Tokens newTokens = performOAuth2Flow(credentials);
                tokenStorage.storeTokens(credentials.getUsername(), newTokens);
                
                // Schedule automatic token refresh
                refreshScheduler.scheduleRefresh(credentials.getUsername(), newTokens);
                
                return createAuthResult(newTokens, credentials);
                
            } catch (OAuth2Exception e) {
                Log.w("OAuth2AuthManager", "OAuth2 authentication failed, falling back to legacy", e);
                
                // Fallback to existing authentication system
                return existingAuth.authenticate(credentials);
            }
        });
    }
    
    private OAuth2Tokens performOAuth2Flow(LoginCredentials credentials) throws OAuth2Exception {
        // Generate PKCE parameters for security
        PKCEParameters pkce = pkceGenerator.generatePKCE();
        
        // Configure OAuth2 request
        AuthorizationRequest authRequest = new AuthorizationRequest.Builder()
            .clientId("linkpoint-mobile")
            .redirectUri("linkpoint://oauth-callback")
            .scope("agent:read agent:write inventory:read world:access")
            .codeChallenge(pkce.getCodeChallenge())
            .codeChallengeMethod("S256")
            .state(generateSecureState())
            .build();
        
        // For mobile, use embedded browser or system browser
        AuthorizationResponse authResponse = performAuthorizationFlow(authRequest);
        
        if (authResponse.isSuccessful()) {
            // Exchange authorization code for tokens
            TokenRequest tokenRequest = new TokenRequest.Builder()
                .code(authResponse.getCode())
                .clientId("linkpoint-mobile")
                .redirectUri("linkpoint://oauth-callback")
                .codeVerifier(pkce.getCodeVerifier())
                .build();
                
            return exchangeCodeForTokens(tokenRequest);
        } else {
            throw new OAuth2Exception("Authorization failed: " + authResponse.getError());
        }
    }
    
    // Mobile-specific secure token storage using Android Keystore
    private class AndroidKeystoreTokenStorage implements SecureTokenStorage {
        private final String KEYSTORE_ALIAS_PREFIX = "linkpoint_oauth_";
        
        @Override
        public void storeTokens(String username, OAuth2Tokens tokens) {
            try {
                // Encrypt tokens using Android Keystore
                String keystoreAlias = KEYSTORE_ALIAS_PREFIX + username;
                
                // Generate or retrieve encryption key from Android Keystore
                SecretKey secretKey = keystoreWrapper.getOrCreateSecretKey(keystoreAlias);
                
                // Encrypt token data
                String encryptedTokens = encrypt(tokens.toJson(), secretKey);
                
                // Store encrypted tokens in SharedPreferences
                SharedPreferences prefs = getSecurePreferences();
                prefs.edit()
                    .putString(keystoreAlias + "_tokens", encryptedTokens)
                    .putLong(keystoreAlias + "_timestamp", System.currentTimeMillis())
                    .apply();
                    
                Log.d("TokenStorage", "OAuth2 tokens stored securely for user: " + username);
                
            } catch (Exception e) {
                Log.e("TokenStorage", "Failed to store OAuth2 tokens securely", e);
                throw new SecurityException("Token storage failed", e);
            }
        }
        
        @Override
        public Optional<OAuth2Tokens> getCachedTokens(String username) {
            try {
                String keystoreAlias = KEYSTORE_ALIAS_PREFIX + username;
                
                SharedPreferences prefs = getSecurePreferences();
                String encryptedTokens = prefs.getString(keystoreAlias + "_tokens", null);
                
                if (encryptedTokens == null) {
                    return Optional.empty();
                }
                
                // Decrypt tokens using Android Keystore
                SecretKey secretKey = keystoreWrapper.getSecretKey(keystoreAlias);
                if (secretKey == null) {
                    Log.w("TokenStorage", "Keystore key not found for user: " + username);
                    return Optional.empty();
                }
                
                String decryptedTokens = decrypt(encryptedTokens, secretKey);
                OAuth2Tokens tokens = OAuth2Tokens.fromJson(decryptedTokens);
                
                return Optional.of(tokens);
                
            } catch (Exception e) {
                Log.w("TokenStorage", "Failed to retrieve cached tokens for user: " + username, e);
                return Optional.empty();
            }
        }
        
        // Additional security: require biometric authentication for token access
        public CompletableFuture<Optional<OAuth2Tokens>> getCachedTokensWithBiometric(String username) {
            return biometricAuth.authenticateAsync("Access saved login credentials")
                .thenApply(biometricResult -> {
                    if (biometricResult.isSuccessful()) {
                        return getCachedTokens(username);
                    } else {
                        Log.i("TokenStorage", "Biometric authentication failed or cancelled");
                        return Optional.empty();
                    }
                });
        }
    }
}
```

**Week 17-18: Biometric Integration and Advanced Security**

```java
// File: com/lumiyaviewer/lumiya/slproto/auth/BiometricAuthenticator.java

public class BiometricAuthenticator {
    private static final String TAG = "BiometricAuthenticator";
    
    public CompletableFuture<BiometricResult> authenticateAsync(String reason) {
        CompletableFuture<BiometricResult> future = new CompletableFuture<>();
        
        if (!BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            // Biometric not available, return success (fallback to password)
            future.complete(BiometricResult.notAvailable());
            return future;
        }
        
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Linkpoint Authentication")
            .setSubtitle(reason)
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | 
                                    BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build();
        
        BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, 
            ContextCompat.getMainExecutor(context), 
            new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    future.complete(BiometricResult.success());
                }
                
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    future.complete(BiometricResult.error(errorCode, errString.toString()));
                }
                
                @Override
                public void onAuthenticationFailed() {
                    future.complete(BiometricResult.failed());
                }
            });
        
        biometricPrompt.authenticate(promptInfo);
        return future;
    }
}
```

**Week 19-20: Testing and Integration Validation**
- **Task 1.3.2:** Security audit of OAuth2 implementation
- **Task 1.3.3:** Integration testing with existing authentication flows
- **Task 1.3.4:** Biometric authentication testing across device types
- **Task 1.3.5:** Token refresh and lifecycle testing

**Deliverables for Phase 1.3:**
- ✅ OAuth2AuthenticationManager with PKCE and secure token storage
- ✅ AndroidKeystoreTokenStorage with hardware-backed security
- ✅ BiometricAuthenticator for enhanced security
- ✅ Comprehensive security testing and audit results
- ✅ Fallback mechanisms ensuring compatibility with existing auth flows

#### Phase 1.4: Basis Universal Texture Support (Months 4-6)

**Week 21-24: Basis Universal Integration and Optimization**

**Prerequisites:** Review existing `com.lumiyaviewer.lumiya.render.ModernTextureManager` and C++ integration at `app/src/main/cpp/`

```java
// Enhancement to existing ModernTextureManager
// File: com/lumiyaviewer/lumiya/render/EnhancedModernTextureManager.java

public class EnhancedModernTextureManager extends ModernTextureManager {
    // C++ JNI integration for Basis Universal
    private final BasisUniversalJNI basisJNI;
    private final TextureTranscoder transcoder;
    private final FormatSelector formatSelector;
    
    // Mobile GPU optimization
    private final DeviceGPUProfiler gpuProfiler;
    private final CompressionStrategySelector compressionSelector;
    
    public EnhancedModernTextureManager(Context context) {
        super(context); // Initialize existing ModernTextureManager
        
        this.basisJNI = new BasisUniversalJNI();
        this.transcoder = new TextureTranscoder(basisJNI);
        this.formatSelector = new FormatSelector();
        
        this.gpuProfiler = new DeviceGPUProfiler(context);
        this.compressionSelector = new CompressionStrategySelector(gpuProfiler);
        
        // Initialize Basis Universal transcoder
        initializeBasisUniversal();
    }
    
    private void initializeBasisUniversal() {
        try {
            // Load native library
            System.loadLibrary("basis_universal");
            
            // Initialize transcoder with mobile-optimized settings
            boolean success = basisJNI.initializeTranscoder();
            if (!success) {
                Log.w(TAG, "Failed to initialize Basis Universal transcoder, using fallback");
                // Fall back to parent texture manager
                return;
            }
            
            Log.i(TAG, "Basis Universal transcoder initialized successfully");
            
        } catch (UnsatisfiedLinkError e) {
            Log.w(TAG, "Basis Universal native library not available, using fallback", e);
            // Graceful fallback to existing texture system
        }
    }
    
    @Override
    public CompletableFuture<Texture> loadTextureAsync(UUID textureId, TexturePriority priority) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Try to load as Basis Universal first
                Optional<RawTextureData> basisData = fetchBasisUniversalTexture(textureId);
                
                if (basisData.isPresent()) {
                    return transcodeBasisUniversalTexture(basisData.get(), textureId);
                } else {
                    // Fallback to existing texture loading
                    return super.loadTextureAsync(textureId, priority).join();
                }
                
            } catch (Exception e) {
                Log.w(TAG, "Basis Universal texture loading failed for " + textureId + 
                          ", falling back to legacy", e);
                          
                // Always fallback to existing system on error
                return super.loadTextureAsync(textureId, priority).join();
            }
        });
    }
    
    private Texture transcodeBasisUniversalTexture(RawTextureData basisData, UUID textureId) {
        // Determine optimal target format for current device
        GPUTextureFormat targetFormat = compressionSelector.selectOptimalFormat(basisData);
        
        Log.d(TAG, "Transcoding Basis Universal texture " + textureId + " to " + targetFormat);
        
        switch (targetFormat) {
            case ASTC_4x4:
                return transcodeToASTC(basisData, 4, 4);
                
            case ETC2_RGB:
                return transcodeToETC2(basisData);
                
            case BC7_RGBA:
                return transcodeToBC7(basisData);
                
            case PVRTC1_4BPP_RGB:
                return transcodeToPVRTC1(basisData);
                
            default:
                // Fallback to uncompressed RGBA
                return transcodeToRGBA32(basisData);
        }
    }
    
    private Texture transcodeToASTC(RawTextureData basisData, int blockWidth, int blockHeight) {
        // Use JNI to call Basis Universal transcoder
        TranscodeResult result = basisJNI.transcodeToASTC(
            basisData.getData(),
            basisData.getWidth(),
            basisData.getHeight(),
            blockWidth,
            blockHeight
        );
        
        if (result.isSuccessful()) {
            return createGLTexture(result.getData(), result.getWidth(), result.getHeight(), 
                                 GL_COMPRESSED_RGBA_ASTC_4x4_KHR);
        } else {
            throw new TextureException("ASTC transcoding failed: " + result.getError());
        }
    }
    
    // Mobile GPU format selection strategy
    private class CompressionStrategySelector {
        private final DeviceGPUProfiler gpuProfiler;
        
        public CompressionStrategySelector(DeviceGPUProfiler gpuProfiler) {
            this.gpuProfiler = gpuProfiler;
        }
        
        public GPUTextureFormat selectOptimalFormat(RawTextureData textureData) {
            GPUInfo gpuInfo = gpuProfiler.getGPUInfo();
            
            // Priority order for mobile GPUs
            if (gpuInfo.supportsASTC()) {
                // ASTC provides the best compression ratio for mobile
                return textureData.hasAlpha() ? GPUTextureFormat.ASTC_4x4 : GPUTextureFormat.ASTC_4x4;
            }
            
            if (gpuInfo.supportsETC2()) {
                // ETC2 is widely supported on Android
                return textureData.hasAlpha() ? GPUTextureFormat.ETC2_RGBA : GPUTextureFormat.ETC2_RGB;
            }
            
            if (gpuInfo.supportsPVRTC() && gpuInfo.isPowerVR()) {
                // PVRTC for PowerVR GPUs (older iOS devices, some Android)
                return GPUTextureFormat.PVRTC1_4BPP_RGB;
            }
            
            if (gpuInfo.supportsBC7()) {
                // BC7 for high-end mobile GPUs with desktop-class features
                return GPUTextureFormat.BC7_RGBA;
            }
            
            // Ultimate fallback: uncompressed
            return GPUTextureFormat.RGBA32;
        }
    }
    
    // Device GPU profiling for optimal format selection
    private class DeviceGPUProfiler {
        private final GPUInfo gpuInfo;
        
        public DeviceGPUProfiler(Context context) {
            this.gpuInfo = profileCurrentGPU(context);
        }
        
        private GPUInfo profileCurrentGPU(Context context) {
            String renderer = getGLRenderer();
            String vendor = getGLVendor();
            String version = getGLVersion();
            
            return new GPUInfo.Builder()
                .renderer(renderer)
                .vendor(vendor)
                .version(version)
                .supportsASTC(checkExtension("GL_KHR_texture_compression_astc_ldr"))
                .supportsETC2(checkExtension("GL_OES_compressed_ETC2_RGB8_texture"))
                .supportsPVRTC(checkExtension("GL_IMG_texture_compression_pvrtc"))
                .supportsBC7(checkExtension("GL_EXT_texture_compression_bptc"))
                .build();
        }
        
        public GPUInfo getGPUInfo() {
            return gpuInfo;
        }
    }
}

// JNI bridge to C++ Basis Universal library
// File: com/lumiyaviewer/lumiya/render/BasisUniversalJNI.java

public class BasisUniversalJNI {
    static {
        System.loadLibrary("basis_universal");
    }
    
    // Native method declarations
    public native boolean initializeTranscoder();
    public native TranscodeResult transcodeToASTC(byte[] basisData, int width, int height, 
                                                  int blockWidth, int blockHeight);
    public native TranscodeResult transcodeToETC2(byte[] basisData, int width, int height);
    public native TranscodeResult transcodeToBC7(byte[] basisData, int width, int height);
    public native TranscodeResult transcodeToPVRTC1(byte[] basisData, int width, int height);
    public native TranscodeResult transcodeToRGBA32(byte[] basisData, int width, int height);
    public native void cleanup();
}
```

**Week 25-26: C++ Native Implementation**

```cpp
// File: app/src/main/cpp/jni/basis_universal_jni.cpp

#include <jni.h>
#include <android/log.h>
#include "basis_universal/transcoder/basisu_transcoder.h"

#define LOG_TAG "BasisUniversalJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using namespace basisu;

// Global transcoder instance
static basisu_transcoder* g_transcoder = nullptr;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_lumiyaviewer_lumiya_render_BasisUniversalJNI_initializeTranscoder(JNIEnv *env, jobject thiz) {
    try {
        // Initialize Basis Universal transcoder
        if (!g_transcoder) {
            g_transcoder = new basisu_transcoder();
        }
        
        // Validate transcoder initialization
        if (!g_transcoder->validate_header_quick_check()) {
            LOGE("Basis Universal transcoder validation failed");
            return JNI_FALSE;
        }
        
        LOGI("Basis Universal transcoder initialized successfully");
        return JNI_TRUE;
        
    } catch (const std::exception& e) {
        LOGE("Exception during transcoder initialization: %s", e.what());
        return JNI_FALSE;
    }
}

JNIEXPORT jobject JNICALL
Java_com_lumiyaviewer_lumiya_render_BasisUniversalJNI_transcodeToASTC(JNIEnv *env, jobject thiz,
                                                                      jbyteArray basisData,
                                                                      jint width, jint height,
                                                                      jint blockWidth, jint blockHeight) {
    if (!g_transcoder) {
        LOGE("Transcoder not initialized");
        return createTranscodeErrorResult(env, "Transcoder not initialized");
    }
    
    // Get byte array data
    jbyte* data = env->GetByteArrayElements(basisData, nullptr);
    jsize dataSize = env->GetArrayLength(basisData);
    
    try {
        // Validate the Basis Universal data
        if (!g_transcoder->validate_header(data, dataSize)) {
            LOGE("Invalid Basis Universal header");
            env->ReleaseByteArrayElements(basisData, data, JNI_ABORT);
            return createTranscodeErrorResult(env, "Invalid Basis Universal header");
        }
        
        // Start transcoding
        if (!g_transcoder->start_transcoding(data, dataSize)) {
            LOGE("Failed to start transcoding");
            env->ReleaseByteArrayElements(basisData, data, JNI_ABORT);
            return createTranscodeErrorResult(env, "Failed to start transcoding");
        }
        
        // Get image info
        basisu_image_info imageInfo;
        if (!g_transcoder->get_image_info(data, dataSize, imageInfo, 0)) {
            LOGE("Failed to get image info");
            env->ReleaseByteArrayElements(basisData, data, JNI_ABORT);
            return createTranscodeErrorResult(env, "Failed to get image info");
        }
        
        // Calculate output buffer size for ASTC 4x4
        uint32_t outputSize = g_transcoder->get_bytes_per_block(transcoder_texture_format::cTFASTC_4x4) *
                             ((imageInfo.m_width + 3) / 4) * ((imageInfo.m_height + 3) / 4);
        
        // Allocate output buffer
        std::vector<uint8_t> outputBuffer(outputSize);
        
        // Perform transcoding to ASTC 4x4
        if (!g_transcoder->transcode_image_level(
                data, dataSize, 0, 0,
                outputBuffer.data(), outputSize,
                transcoder_texture_format::cTFASTC_4x4)) {
            LOGE("ASTC transcoding failed");
            env->ReleaseByteArrayElements(basisData, data, JNI_ABORT);
            return createTranscodeErrorResult(env, "ASTC transcoding failed");
        }
        
        // Create Java byte array for result
        jbyteArray resultArray = env->NewByteArray(outputSize);
        env->SetByteArrayRegion(resultArray, 0, outputSize, 
                               reinterpret_cast<const jbyte*>(outputBuffer.data()));
        
        // Release input data
        env->ReleaseByteArrayElements(basisData, data, JNI_ABORT);
        
        // Create successful TranscodeResult
        return createTranscodeSuccessResult(env, resultArray, imageInfo.m_width, imageInfo.m_height);
        
    } catch (const std::exception& e) {
        LOGE("Exception during ASTC transcoding: %s", e.what());
        env->ReleaseByteArrayElements(basisData, data, JNI_ABORT);
        return createTranscodeErrorResult(env, e.what());
    }
}

// Helper function to create successful transcode result
jobject createTranscodeSuccessResult(JNIEnv* env, jbyteArray data, jint width, jint height) {
    jclass resultClass = env->FindClass("com/lumiyaviewer/lumiya/render/TranscodeResult");
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "([BII)V");
    return env->NewObject(resultClass, constructor, data, width, height);
}

// Helper function to create error transcode result
jobject createTranscodeErrorResult(JNIEnv* env, const char* error) {
    jclass resultClass = env->FindClass("com/lumiyaviewer/lumiya/render/TranscodeResult");
    jmethodID errorConstructor = env->GetMethodID(resultClass, "<init>", "(Ljava/lang/String;)V");
    jstring errorString = env->NewStringUTF(error);
    return env->NewObject(resultClass, errorConstructor, errorString);
}

} // extern "C"
```

**Week 27-28: Integration Testing and Performance Validation**
- **Task 1.4.1:** Comprehensive testing of Basis Universal integration
- **Task 1.4.2:** Performance benchmarking vs existing texture system
- **Task 1.4.3:** Memory usage validation and optimization
- **Task 1.4.4:** Device compatibility testing across GPU vendors

**Deliverables for Phase 1.4:**
- ✅ EnhancedModernTextureManager with Basis Universal support
- ✅ Complete C++ JNI bridge for Basis Universal transcoder
- ✅ Mobile GPU optimization with format selection strategy
- ✅ Comprehensive test suite covering all supported texture formats
- ✅ Performance benchmarks showing 30-50% reduction in texture memory usage

---

### Phase 1 Summary and Success Metrics

**Overall Phase 1 Deliverables (Month 6 Completion):**
- ✅ HTTP/2 CAPS integration with 15-30% performance improvement in bulk operations
- ✅ WebSocket event streaming with battery-aware optimization
- ✅ OAuth2 authentication with Android Keystore security and biometric support
- ✅ Basis Universal texture support with mobile GPU optimization

**Success Metrics:**
- **Performance:** 20% overall improvement in network operations efficiency
- **Battery Life:** 15% improvement through optimized protocols and adaptive features
- **Security:** Hardware-backed token storage with biometric authentication
- **Compatibility:** 100% backward compatibility with existing Linkpoint systems
- **Memory Usage:** 30-50% reduction in texture memory usage through Basis Universal

**Risk Mitigation Achieved:**
- All integrations include fallback to existing Linkpoint systems
- Comprehensive testing ensures no regression in existing functionality
- Mobile-first optimizations maintain Linkpoint's core advantages

---

### Phase 2: Advanced Features (Months 7-12) - DETAILED IMPLEMENTATION

**Estimated Effort:** 800-1000 development hours across 4 senior developers
**Risk Level:** Medium-High - Advanced graphics and UX features

**Prerequisites:** Phase 1 complete and validated in production environment

#### Phase 2.1: Adaptive PBR System Implementation (Months 7-8)
**Integration Foundation:** Building on existing `com.lumiyaviewer.lumiya.render` system and completed Basis Universal support

**Week 29-30: PBR Architecture Design and Shader Development**

- **Task 2.1.1:** Design PBR material system architecture
  - **Integration Points:** 
    - `com.lumiyaviewer.lumiya.render.ModernTextureManager` (Enhanced in Phase 1)
    - `com.lumiyaviewer.lumiya.render.spatial.SpatialObjectIndex` (Existing spatial system)
    - `com.lumiyaviewer.lumiya.render.tex` (Existing texture classes)

```java
// File: com/lumiyaviewer/lumiya/render/materials/PBRMaterialSystem.java

public class PBRMaterialSystem {
    // Integration with Phase 1 enhancements
    private final EnhancedModernTextureManager textureManager;
    private final SpatialObjectIndex spatialIndex; // Use existing spatial indexing
    
    // PBR-specific components
    private final PBRShaderManager shaderManager;
    private final MaterialCache materialCache;
    private final EnvironmentMapManager envMapManager;
    private final LightProbeSystem lightProbeSystem;
    
    // Mobile optimization components
    private final DeviceCapabilityTester capabilityTester;
    private final PerformanceMonitor performanceMonitor;
    private final AdaptiveQualityManager qualityManager;
    
    public PBRMaterialSystem(EnhancedModernTextureManager textureManager, 
                            SpatialObjectIndex spatialIndex) {
        this.textureManager = textureManager;
        this.spatialIndex = spatialIndex;
        
        this.shaderManager = new PBRShaderManager();
        this.materialCache = new MaterialCache(500); // 500 cached materials max
        this.envMapManager = new EnvironmentMapManager(textureManager);
        this.lightProbeSystem = new LightProbeSystem();
        
        this.capabilityTester = new DeviceCapabilityTester();
        this.performanceMonitor = new PerformanceMonitor();
        this.qualityManager = new AdaptiveQualityManager(performanceMonitor);
        
        initializePBRSystem();
    }
    
    private void initializePBRSystem() {
        // Test device capabilities for PBR features
        PBRCapabilities capabilities = capabilityTester.testPBRCapabilities();
        
        Log.i("PBRMaterialSystem", "PBR Capabilities: " + capabilities.toString());
        
        // Initialize appropriate shader variants based on capabilities
        if (capabilities.supportsAdvancedPBR()) {
            shaderManager.loadAdvancedPBRShaders();
            envMapManager.enableHDREnvironmentMaps();
            lightProbeSystem.enableLightProbes();
        } else if (capabilities.supportsBasicPBR()) {
            shaderManager.loadBasicPBRShaders();
            envMapManager.enableLDREnvironmentMaps();
        } else {
            // PBR not supported - system will gracefully fallback to existing renderer
            Log.i("PBRMaterialSystem", "PBR not supported on this device, using legacy renderer");
        }
    }
    
    public void renderPrimitive(SLPrimitive primitive, RenderContext context) {
        // Adaptive rendering based on current performance
        PBRQualityLevel qualityLevel = qualityManager.getCurrentQualityLevel();
        
        switch (qualityLevel) {
            case HIGH:
                renderAdvancedPBR(primitive, context);
                break;
            case MEDIUM:
                renderStandardPBR(primitive, context);
                break;
            case LOW:
                renderSimplifiedPBR(primitive, context);
                break;
            case DISABLED:
                // Fallback to existing legacy renderer
                fallbackToLegacyRenderer(primitive, context);
                break;
        }
        
        // Performance monitoring for adaptive quality
        performanceMonitor.recordRenderTime(System.currentTimeMillis());
    }
    
    private void renderAdvancedPBR(SLPrimitive primitive, RenderContext context) {
        // Full PBR implementation for high-end devices
        UUID materialId = primitive.getMaterialId();
        PBRMaterial material = materialCache.get(materialId);
        
        if (material == null) {
            material = loadPBRMaterial(materialId);
            materialCache.put(materialId, material);
        }
        
        // Bind advanced PBR shader
        PBRShader shader = shaderManager.getAdvancedPBRShader();
        shader.use();
        
        // Bind PBR textures using enhanced texture manager
        bindPBRTextures(material, shader, context);
        
        // Set up advanced lighting
        setupAdvancedLighting(primitive, shader, context);
        
        // Enable environment reflections
        setupEnvironmentReflections(material, shader, context);
        
        // Render with full PBR pipeline
        primitive.render(context);
    }
    
    private void renderStandardPBR(SLPrimitive primitive, RenderContext context) {
        // Standard PBR for mid-range devices
        UUID materialId = primitive.getMaterialId();
        PBRMaterial material = materialCache.get(materialId);
        
        if (material == null) {
            material = loadPBRMaterial(materialId);
            materialCache.put(materialId, material);
        }
        
        // Use standard PBR shader (fewer features than advanced)
        PBRShader shader = shaderManager.getStandardPBRShader();
        shader.use();
        
        // Bind essential PBR textures only
        bindEssentialPBRTextures(material, shader, context);
        
        // Simplified lighting model
        setupStandardLighting(primitive, shader, context);
        
        // No environment reflections (performance optimization)
        
        primitive.render(context);
    }
    
    private void renderSimplifiedPBR(SLPrimitive primitive, RenderContext context) {
        // Minimal PBR for low-end devices
        UUID materialId = primitive.getMaterialId();
        
        // Use simplified material loading
        SimplePBRMaterial material = loadSimplifiedPBRMaterial(materialId);
        
        // Use basic PBR shader with minimal features
        PBRShader shader = shaderManager.getSimplePBRShader();
        shader.use();
        
        // Only albedo and normal maps
        bindBasicTextures(material, shader, context);
        
        // Basic lighting only
        setupBasicLighting(primitive, shader, context);
        
        primitive.render(context);
    }
    
    private void bindPBRTextures(PBRMaterial material, PBRShader shader, RenderContext context) {
        // Use enhanced texture manager from Phase 1
        CompletableFuture<Void> textureBinding = CompletableFuture.allOf(
            // Albedo map
            textureManager.loadTextureAsync(material.getAlbedoMapId(), TexturePriority.HIGH)
                .thenAccept(texture -> shader.bindTexture("u_albedoMap", texture, 0)),
                
            // Normal map
            textureManager.loadTextureAsync(material.getNormalMapId(), TexturePriority.HIGH)
                .thenAccept(texture -> shader.bindTexture("u_normalMap", texture, 1)),
                
            // Metallic-roughness map
            textureManager.loadTextureAsync(material.getMetallicRoughnessMapId(), TexturePriority.MEDIUM)
                .thenAccept(texture -> shader.bindTexture("u_metallicRoughnessMap", texture, 2)),
                
            // Occlusion map
            textureManager.loadTextureAsync(material.getOcclusionMapId(), TexturePriority.MEDIUM)
                .thenAccept(texture -> shader.bindTexture("u_occlusionMap", texture, 3)),
                
            // Emissive map
            textureManager.loadTextureAsync(material.getEmissiveMapId(), TexturePriority.LOW)
                .thenAccept(texture -> shader.bindTexture("u_emissiveMap", texture, 4))
        );
        
        // Wait for texture binding to complete (with timeout for mobile)
        try {
            textureBinding.get(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Log.w("PBRMaterialSystem", "Texture binding timeout, using fallback textures");
            bindFallbackTextures(shader);
        } catch (Exception e) {
            Log.w("PBRMaterialSystem", "Texture binding failed, using fallback textures", e);
            bindFallbackTextures(shader);
        }
    }
    
    private void setupAdvancedLighting(SLPrimitive primitive, PBRShader shader, RenderContext context) {
        Vector3 primitivePosition = primitive.getPosition();
        
        // Use existing spatial indexing to find nearby lights efficiently
        List<SLLightSource> nearbyLights = spatialIndex.findLightsInRadius(
            primitivePosition, 
            50.0f // 50 meter radius
        );
        
        // Sort lights by influence on this primitive
        nearbyLights.sort((a, b) -> {
            float influenceA = calculateLightInfluence(a, primitive);
            float influenceB = calculateLightInfluence(b, primitive);
            return Float.compare(influenceB, influenceA); // Higher influence first
        });
        
        // Limit to maximum lights supported by shader
        int maxLights = shader.getMaxSupportedLights();
        List<SLLightSource> activeLights = nearbyLights.subList(0, 
            Math.min(nearbyLights.size(), maxLights));
        
        // Bind lighting data to shader
        shader.setInt("u_numLights", activeLights.size());
        
        for (int i = 0; i < activeLights.size(); i++) {
            SLLightSource light = activeLights.get(i);
            String prefix = "u_lights[" + i + "]";
            
            shader.setVector3(prefix + ".position", light.getPosition());
            shader.setVector3(prefix + ".color", light.getColor());
            shader.setFloat(prefix + ".intensity", light.getIntensity());
            shader.setFloat(prefix + ".range", light.getRange());
            shader.setInt(prefix + ".type", light.getType().ordinal());
        }
        
        // Global lighting parameters
        shader.setVector3("u_globalAmbient", context.getGlobalAmbientLight());
        shader.setVector3("u_sunDirection", context.getSunDirection());
        shader.setVector3("u_sunColor", context.getSunColor());
        shader.setFloat("u_sunIntensity", context.getSunIntensity());
    }
    
    private void setupEnvironmentReflections(PBRMaterial material, PBRShader shader, RenderContext context) {
        // Use environment map manager for reflections
        EnvironmentMap envMap = envMapManager.getCurrentEnvironmentMap(context);
        
        if (envMap != null) {
            // Bind environment map for reflections
            shader.bindCubeMap("u_environmentMap", envMap.getCubeMap(), 5);
            shader.setFloat("u_reflectionStrength", material.getReflectionStrength());
            shader.setFloat("u_environmentRotation", context.getEnvironmentRotation());
        } else {
            // Use simple gradient fallback for reflections
            shader.setVector3("u_skyColor", context.getSkyColor());
            shader.setVector3("u_horizonColor", context.getHorizonColor());
            shader.setFloat("u_reflectionStrength", material.getReflectionStrength() * 0.5f);
        }
    }
    
    // Adaptive quality management for mobile performance
    private class AdaptiveQualityManager {
        private final PerformanceMonitor performanceMonitor;
        private PBRQualityLevel currentQualityLevel;
        private long lastQualityCheck = 0;
        private static final long QUALITY_CHECK_INTERVAL = 5000; // 5 seconds
        
        // Performance thresholds
        private static final float TARGET_FRAMERATE = 30.0f; // Target 30 FPS minimum
        private static final float HIGH_QUALITY_THRESHOLD = 45.0f; // Enable high quality at 45+ FPS
        private static final float LOW_QUALITY_THRESHOLD = 25.0f; // Drop to low quality below 25 FPS
        
        public AdaptiveQualityManager(PerformanceMonitor performanceMonitor) {
            this.performanceMonitor = performanceMonitor;
            this.currentQualityLevel = determineInitialQualityLevel();
        }
        
        private PBRQualityLevel determineInitialQualityLevel() {
            // Base initial quality on device capabilities
            if (capabilityTester.isHighEndDevice()) {
                return PBRQualityLevel.HIGH;
            } else if (capabilityTester.isMidRangeDevice()) {
                return PBRQualityLevel.MEDIUM;
            } else {
                return PBRQualityLevel.LOW;
            }
        }
        
        public PBRQualityLevel getCurrentQualityLevel() {
            long currentTime = System.currentTimeMillis();
            
            // Check if it's time to reassess quality level
            if (currentTime - lastQualityCheck > QUALITY_CHECK_INTERVAL) {
                reassessQualityLevel();
                lastQualityCheck = currentTime;
            }
            
            return currentQualityLevel;
        }
        
        private void reassessQualityLevel() {
            float currentFramerate = performanceMonitor.getCurrentFramerate();
            float averageFramerate = performanceMonitor.getAverageFramerate();
            float batteryLevel = getBatteryLevel();
            
            // Consider performance, battery, and thermal throttling
            boolean isThrottling = performanceMonitor.isThermalThrottling();
            
            PBRQualityLevel newQualityLevel = currentQualityLevel;
            
            // Upgrade quality if performance is good
            if (currentFramerate > HIGH_QUALITY_THRESHOLD && 
                averageFramerate > HIGH_QUALITY_THRESHOLD &&
                batteryLevel > 0.3f && !isThrottling) {
                
                if (currentQualityLevel == PBRQualityLevel.MEDIUM) {
                    newQualityLevel = PBRQualityLevel.HIGH;
                } else if (currentQualityLevel == PBRQualityLevel.LOW) {
                    newQualityLevel = PBRQualityLevel.MEDIUM;
                } else if (currentQualityLevel == PBRQualityLevel.DISABLED) {
                    newQualityLevel = PBRQualityLevel.LOW;
                }
            }
            
            // Downgrade quality if performance is poor
            if (currentFramerate < LOW_QUALITY_THRESHOLD || 
                averageFramerate < TARGET_FRAMERATE ||
                batteryLevel < 0.15f || isThrottling) {
                
                if (currentQualityLevel == PBRQualityLevel.HIGH) {
                    newQualityLevel = PBRQualityLevel.MEDIUM;
                } else if (currentQualityLevel == PBRQualityLevel.MEDIUM) {
                    newQualityLevel = PBRQualityLevel.LOW;
                } else if (currentQualityLevel == PBRQualityLevel.LOW) {
                    newQualityLevel = PBRQualityLevel.DISABLED;
                }
            }
            
            if (newQualityLevel != currentQualityLevel) {
                Log.i("AdaptiveQualityManager", 
                      "PBR quality changed from " + currentQualityLevel + " to " + newQualityLevel +
                      " (FPS: " + currentFramerate + ", Battery: " + (batteryLevel * 100) + "%)");
                      
                currentQualityLevel = newQualityLevel;
                
                // Notify system of quality change
                onQualityLevelChanged(currentQualityLevel, newQualityLevel);
            }
        }
        
        private void onQualityLevelChanged(PBRQualityLevel oldLevel, PBRQualityLevel newLevel) {
            // Reload appropriate shaders for new quality level
            shaderManager.switchToQualityLevel(newLevel);
            
            // Adjust other systems based on quality change
            if (newLevel.ordinal() < oldLevel.ordinal()) {
                // Quality decreased - reduce resource usage
                materialCache.reduceSize();
                envMapManager.reduceQuality();
            } else {
                // Quality increased - allow more resources
                materialCache.increaseSize();
                envMapManager.increaseQuality();
            }
        }
    }
}

// PBR Shader Management System
// File: com/lumiyaviewer/lumiya/render/materials/PBRShaderManager.java

public class PBRShaderManager {
    private final Map<PBRQualityLevel, PBRShader> shaderVariants;
    private final ShaderCompiler shaderCompiler;
    private final ShaderCache shaderCache;
    
    // Shader source code management
    private final ShaderSourceManager sourceManager;
    
    public PBRShaderManager() {
        this.shaderVariants = new HashMap<>();
        this.shaderCompiler = new ShaderCompiler();
        this.shaderCache = new ShaderCache();
        this.sourceManager = new ShaderSourceManager();
    }
    
    public void loadAdvancedPBRShaders() {
        PBRShader advancedShader = compileAdvancedPBRShader();
        shaderVariants.put(PBRQualityLevel.HIGH, advancedShader);
    }
    
    public void loadBasicPBRShaders() {
        PBRShader basicShader = compileBasicPBRShader();
        shaderVariants.put(PBRQualityLevel.MEDIUM, basicShader);
        
        PBRShader simpleShader = compileSimplePBRShader();
        shaderVariants.put(PBRQualityLevel.LOW, simpleShader);
    }
    
    private PBRShader compileAdvancedPBRShader() {
        // Advanced PBR shader with all features enabled
        String vertexSource = sourceManager.getShaderSource("pbr_advanced.vert");
        String fragmentSource = sourceManager.getShaderSource("pbr_advanced.frag");
        
        // Add feature defines for advanced shader
        Map<String, String> defines = new HashMap<>();
        defines.put("MAX_LIGHTS", "8");
        defines.put("ENABLE_IBL", "1");
        defines.put("ENABLE_REFLECTIONS", "1");
        defines.put("ENABLE_SHADOWS", "1");
        defines.put("ENABLE_SUBSURFACE", "1");
        
        return shaderCompiler.compile("PBR_Advanced", vertexSource, fragmentSource, defines);
    }
    
    private PBRShader compileBasicPBRShader() {
        // Standard PBR shader with moderate features
        String vertexSource = sourceManager.getShaderSource("pbr_standard.vert");
        String fragmentSource = sourceManager.getShaderSource("pbr_standard.frag");
        
        Map<String, String> defines = new HashMap<>();
        defines.put("MAX_LIGHTS", "4");
        defines.put("ENABLE_IBL", "1");
        defines.put("ENABLE_REFLECTIONS", "0");
        defines.put("ENABLE_SHADOWS", "0");
        defines.put("ENABLE_SUBSURFACE", "0");
        
        return shaderCompiler.compile("PBR_Standard", vertexSource, fragmentSource, defines);
    }
    
    private PBRShader compileSimplePBRShader() {
        // Simple PBR shader with minimal features
        String vertexSource = sourceManager.getShaderSource("pbr_simple.vert");
        String fragmentSource = sourceManager.getShaderSource("pbr_simple.frag");
        
        Map<String, String> defines = new HashMap<>();
        defines.put("MAX_LIGHTS", "2");
        defines.put("ENABLE_IBL", "0");
        defines.put("ENABLE_REFLECTIONS", "0");
        defines.put("ENABLE_SHADOWS", "0");
        defines.put("ENABLE_SUBSURFACE", "0");
        
        return shaderCompiler.compile("PBR_Simple", vertexSource, fragmentSource, defines);
    }
    
    public PBRShader getAdvancedPBRShader() {
        return shaderVariants.get(PBRQualityLevel.HIGH);
    }
    
    public PBRShader getStandardPBRShader() {
        return shaderVariants.get(PBRQualityLevel.MEDIUM);
    }
    
    public PBRShader getSimplePBRShader() {
        return shaderVariants.get(PBRQualityLevel.LOW);
    }
    
    public void switchToQualityLevel(PBRQualityLevel qualityLevel) {
        // Preload shaders for the new quality level if not already loaded
        switch (qualityLevel) {
            case HIGH:
                if (!shaderVariants.containsKey(PBRQualityLevel.HIGH)) {
                    loadAdvancedPBRShaders();
                }
                break;
            case MEDIUM:
            case LOW:
                if (!shaderVariants.containsKey(PBRQualityLevel.MEDIUM) ||
                    !shaderVariants.containsKey(PBRQualityLevel.LOW)) {
                    loadBasicPBRShaders();
                }
                break;
        }
    }
}
```

**Week 31-32: PBR Shader Development and Testing**

```glsl
// File: app/src/main/assets/shaders/pbr_advanced.frag

#version 300 es
precision mediump float;

// Material inputs
uniform sampler2D u_albedoMap;
uniform sampler2D u_normalMap;
uniform sampler2D u_metallicRoughnessMap;
uniform sampler2D u_occlusionMap;
uniform sampler2D u_emissiveMap;

// Environment mapping
#if ENABLE_IBL
uniform samplerCube u_environmentMap;
uniform float u_environmentRotation;
#endif

// Lighting
#define MAX_LIGHTS 8
struct Light {
    vec3 position;
    vec3 color;
    float intensity;
    float range;
    int type; // 0=directional, 1=point, 2=spot
};

uniform Light u_lights[MAX_LIGHTS];
uniform int u_numLights;
uniform vec3 u_globalAmbient;

// Camera
uniform vec3 u_cameraPosition;

// Material properties
uniform vec4 u_baseColorFactor;
uniform float u_metallicFactor;
uniform float u_roughnessFactor;
uniform vec3 u_emissiveFactor;
uniform float u_reflectionStrength;

// Input from vertex shader
in vec3 v_worldPosition;
in vec3 v_worldNormal;
in vec3 v_worldTangent;
in vec3 v_worldBitangent;
in vec2 v_texCoord;

// Output
out vec4 fragColor;

// PBR calculation functions
vec3 getNormalFromMap() {
    vec3 tangentNormal = texture(u_normalMap, v_texCoord).rgb * 2.0 - 1.0;
    
    mat3 TBN = mat3(
        normalize(v_worldTangent),
        normalize(v_worldBitangent),
        normalize(v_worldNormal)
    );
    
    return normalize(TBN * tangentNormal);
}

float distributionGGX(vec3 N, vec3 H, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH * NdotH;
    
    float num = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = 3.14159265 * denom * denom;
    
    return num / denom;
}

float geometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r * r) / 8.0;
    
    float num = NdotV;
    float denom = NdotV * (1.0 - k) + k;
    
    return num / denom;
}

float geometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = geometrySchlickGGX(NdotV, roughness);
    float ggx1 = geometrySchlickGGX(NdotL, roughness);
    
    return ggx1 * ggx2;
}

vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

#if ENABLE_IBL
vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness) {
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
}

vec3 sampleEnvironmentMap(vec3 direction, float roughness) {
    // Apply environment rotation
    float cosRot = cos(u_environmentRotation);
    float sinRot = sin(u_environmentRotation);
    vec3 rotatedDir = vec3(
        direction.x * cosRot - direction.z * sinRot,
        direction.y,
        direction.x * sinRot + direction.z * cosRot
    );
    
    // Sample environment map with appropriate mip level for roughness
    float mipLevel = roughness * 6.0; // Assuming 7 mip levels (0-6)
    return textureLod(u_environmentMap, rotatedDir, mipLevel).rgb;
}
#endif

void main() {
    // Sample material textures
    vec4 albedo = texture(u_albedoMap, v_texCoord) * u_baseColorFactor;
    vec3 metallicRoughness = texture(u_metallicRoughnessMap, v_texCoord).rgb;
    float metallic = metallicRoughness.b * u_metallicFactor;
    float roughness = metallicRoughness.g * u_roughnessFactor;
    float occlusion = texture(u_occlusionMap, v_texCoord).r;
    vec3 emissive = texture(u_emissiveMap, v_texCoord).rgb * u_emissiveFactor;
    
    // Get normal from normal map
    vec3 N = getNormalFromMap();
    vec3 V = normalize(u_cameraPosition - v_worldPosition);
    
    // Calculate F0 (base reflectance)
    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo.rgb, metallic);
    
    // Direct lighting calculation
    vec3 Lo = vec3(0.0);
    
    for(int i = 0; i < u_numLights && i < MAX_LIGHTS; ++i) {
        vec3 L;
        float attenuation = 1.0;
        
        if(u_lights[i].type == 0) { // Directional light
            L = normalize(-u_lights[i].position);
        } else { // Point light
            vec3 lightVector = u_lights[i].position - v_worldPosition;
            float distance = length(lightVector);
            L = lightVector / distance;
            
            // Distance attenuation
            attenuation = 1.0 / (1.0 + 0.09 * distance + 0.032 * distance * distance);
            
            // Range attenuation
            float rangeFactor = max(0.0, 1.0 - distance / u_lights[i].range);
            attenuation *= rangeFactor * rangeFactor;
        }
        
        vec3 H = normalize(V + L);
        vec3 radiance = u_lights[i].color * u_lights[i].intensity * attenuation;
        
        // BRDF calculation
        float NDF = distributionGGX(N, H, roughness);
        float G = geometrySmith(N, V, L, roughness);
        vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);
        
        vec3 kS = F;
        vec3 kD = vec3(1.0) - kS;
        kD *= 1.0 - metallic;
        
        vec3 numerator = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.001;
        vec3 specular = numerator / denominator;
        
        float NdotL = max(dot(N, L), 0.0);
        Lo += (kD * albedo.rgb / 3.14159265 + specular) * radiance * NdotL;
    }
    
    // Ambient lighting
    vec3 ambient = u_globalAmbient * albedo.rgb * occlusion;
    
#if ENABLE_IBL
    // Image-based lighting
    vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);
    vec3 kS = F;
    vec3 kD = 1.0 - kS;
    kD *= 1.0 - metallic;
    
    // Diffuse IBL
    vec3 diffuseIBL = sampleEnvironmentMap(N, 1.0) * albedo.rgb;
    
    // Specular IBL
    vec3 R = reflect(-V, N);
    vec3 specularIBL = sampleEnvironmentMap(R, roughness);
    
    vec3 ibl = (kD * diffuseIBL + specularIBL * F) * u_reflectionStrength;
    ambient = mix(ambient, ibl, 0.8); // Blend with ambient
#endif
    
    vec3 color = ambient + Lo + emissive;
    
    // Tone mapping and gamma correction
    color = color / (color + vec3(1.0));
    color = pow(color, vec3(1.0/2.2));
    
    fragColor = vec4(color, albedo.a);
}
```

**Week 33-34: Performance Testing and Mobile Optimization**

- **Task 2.1.2:** Comprehensive performance testing across device tiers
- **Task 2.1.3:** Thermal throttling response optimization
- **Task 2.1.4:** Battery impact measurement and optimization
- **Task 2.1.5:** Integration testing with existing rendering pipeline

**Deliverables for Phase 2.1:**
- ✅ Complete PBR material system with adaptive quality management
- ✅ Three-tier shader system (Advanced/Standard/Simple) with automatic switching
- ✅ Environment mapping with HDR support for capable devices
- ✅ Performance monitoring and thermal throttling response
- ✅ Seamless fallback to existing rendering system

#### Phase 2.2: Advanced Asset Pipeline (Months 8-9)

**Week 35-36: Multi-Format Transcoding System**

Building on Phase 1 Basis Universal implementation:

```java
// File: com/lumiyaviewer/lumiya/render/assets/AdvancedAssetPipeline.java

public class AdvancedAssetPipeline {
    // Integration with Phase 1 enhancements
    private final EnhancedModernTextureManager textureManager;
    private final HTTP2CapsClient capsClient; // From Phase 1
    
    // Advanced asset processing
    private final MultiFormatTranscoder multiTranscoder;
    private final AssetQualitySelector qualitySelector;
    private final ProgressiveLoadingManager progressiveLoader;
    private final AssetCompressionOptimizer compressionOptimizer;
    
    // Mobile optimization
    private final NetworkBandwidthMonitor bandwidthMonitor;
    private final StorageOptimizer storageOptimizer;
    private final BackgroundProcessingManager backgroundProcessor;
    
    public AdvancedAssetPipeline(EnhancedModernTextureManager textureManager,
                                HTTP2CapsClient capsClient) {
        this.textureManager = textureManager;
        this.capsClient = capsClient;
        
        this.multiTranscoder = new MultiFormatTranscoder();
        this.qualitySelector = new AssetQualitySelector();
        this.progressiveLoader = new ProgressiveLoadingManager();
        this.compressionOptimizer = new AssetCompressionOptimizer();
        
        this.bandwidthMonitor = new NetworkBandwidthMonitor();
        this.storageOptimizer = new StorageOptimizer();
        this.backgroundProcessor = new BackgroundProcessingManager();
    }
    
    public CompletableFuture<ProcessedAsset> processAssetAsync(UUID assetId, 
                                                               AssetType assetType, 
                                                               AssetPriority priority) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Determine optimal processing strategy based on device and network conditions
                ProcessingStrategy strategy = determineProcessingStrategy(assetType, priority);
                
                switch (strategy) {
                    case PROGRESSIVE_LOADING:
                        return processProgressively(assetId, assetType, priority);
                    case BACKGROUND_PROCESSING:
                        return processInBackground(assetId, assetType, priority);
                    case IMMEDIATE_PROCESSING:
                        return processImmediately(assetId, assetType, priority);
                    case CACHED_ONLY:
                        return getCachedAsset(assetId).orElse(null);
                    default:
                        throw new AssetProcessingException("Unknown processing strategy: " + strategy);
                }
            } catch (Exception e) {
                Log.w("AdvancedAssetPipeline", "Asset processing failed for " + assetId, e);
                // Fallback to existing texture manager
                if (assetType == AssetType.TEXTURE) {
                    return textureManager.loadTextureAsync(assetId, 
                        convertPriority(priority)).join();
                } else {
                    throw new AssetProcessingException("Asset processing failed", e);
                }
            }
        }, getAssetProcessingExecutor());
    }
    
    private ProcessingStrategy determineProcessingStrategy(AssetType assetType, AssetPriority priority) {
        NetworkCondition networkCondition = bandwidthMonitor.getCurrentNetworkCondition();
        DevicePerformanceLevel devicePerformance = getDevicePerformanceLevel();
        StorageAvailability storageStatus = storageOptimizer.getStorageStatus();
        float batteryLevel = getBatteryLevel();
        
        // High priority assets always processed immediately
        if (priority == AssetPriority.CRITICAL) {
            return ProcessingStrategy.IMMEDIATE_PROCESSING;
        }
        
        // Low battery: prefer cached assets
        if (batteryLevel < 0.15f) {
            return ProcessingStrategy.CACHED_ONLY;
        }
        
        // Poor network: use cached or background processing
        if (networkCondition == NetworkCondition.POOR || networkCondition == NetworkCondition.LIMITED) {
            return backgroundProcessor.hasCapacity() ? 
                ProcessingStrategy.BACKGROUND_PROCESSING : ProcessingStrategy.CACHED_ONLY;
        }
        
        // Good network + high performance: progressive loading for better UX
        if (networkCondition == NetworkCondition.EXCELLENT && 
            devicePerformance == DevicePerformanceLevel.HIGH) {
            return ProcessingStrategy.PROGRESSIVE_LOADING;
        }
        
        // Default: background processing for non-critical assets
        return ProcessingStrategy.BACKGROUND_PROCESSING;
    }
    
    private ProcessedAsset processProgressively(UUID assetId, AssetType assetType, AssetPriority priority) {
        // Progressive loading: load low quality first, then enhance
        ProgressiveAssetLoader loader = new ProgressiveAssetLoader(assetId, assetType);
        
        // Stage 1: Load thumbnail/low quality version quickly
        ProcessedAsset lowQuality = loader.loadLowQuality();
        
        // Stage 2: Load medium quality in background
        CompletableFuture<ProcessedAsset> mediumQualityFuture = 
            CompletableFuture.supplyAsync(() -> loader.loadMediumQuality(), 
                backgroundProcessor.getExecutor());
        
        // Stage 3: Load high quality in background (if device supports it)
        CompletableFuture<ProcessedAsset> highQualityFuture = null;
        if (qualitySelector.shouldLoadHighQuality(assetType)) {
            highQualityFuture = mediumQualityFuture.thenCompose(mediumAsset -> 
                CompletableFuture.supplyAsync(() -> loader.loadHighQuality(), 
                    backgroundProcessor.getExecutor()));
        }
        
        // Return immediately with low quality, upgrade asynchronously
        scheduleProgressiveUpgrade(assetId, mediumQualityFuture, highQualityFuture);
        
        return lowQuality;
    }
    
    private void scheduleProgressiveUpgrade(UUID assetId, 
                                          CompletableFuture<ProcessedAsset> mediumFuture,
                                          CompletableFuture<ProcessedAsset> highFuture) {
        // Medium quality upgrade
        mediumFuture.thenAccept(mediumAsset -> {
            // Replace low quality asset in cache
            updateAssetInCache(assetId, mediumAsset);
            
            // Notify rendering system of upgrade
            notifyAssetUpgrade(assetId, AssetQuality.MEDIUM);
        }).exceptionally(error -> {
            Log.w("AdvancedAssetPipeline", "Medium quality upgrade failed for " + assetId, error);
            return null;
        });
        
        // High quality upgrade (if requested)
        if (highFuture != null) {
            highFuture.thenAccept(highAsset -> {
                // Final upgrade to high quality
                updateAssetInCache(assetId, highAsset);
                notifyAssetUpgrade(assetId, AssetQuality.HIGH);
            }).exceptionally(error -> {
                Log.w("AdvancedAssetPipeline", "High quality upgrade failed for " + assetId, error);
                return null;
            });
        }
    }
    
    // Multi-format transcoding system
    private class MultiFormatTranscoder {
        private final Map<AssetFormat, FormatTranscoder> transcoders;
        
        public MultiFormatTranscoder() {
            this.transcoders = new HashMap<>();
            initializeTranscoders();
        }
        
        private void initializeTranscoders() {
            // Texture transcoders
            transcoders.put(AssetFormat.BASIS_UNIVERSAL, new BasisUniversalTranscoder());
            transcoders.put(AssetFormat.ASTC, new ASTCTranscoder());
            transcoders.put(AssetFormat.ETC2, new ETC2Transcoder());
            transcoders.put(AssetFormat.JPEG2000, new JPEG2000Transcoder()); // Existing
            
            // 3D model transcoders
            transcoders.put(AssetFormat.GLTF, new GLTFTranscoder());
            transcoders.put(AssetFormat.DRACO_COMPRESSED, new DracoTranscoder());
            
            // Audio transcoders
            transcoders.put(AssetFormat.OPUS, new OpusTranscoder());
            transcoders.put(AssetFormat.AAC_LC, new AACTranscoder());
        }
        
        public ProcessedAsset transcode(RawAssetData rawData, AssetFormat targetFormat) {
            FormatTranscoder transcoder = transcoders.get(targetFormat);
            if (transcoder == null) {
                throw new UnsupportedAssetFormatException("No transcoder for format: " + targetFormat);
            }
            
            return transcoder.transcode(rawData, targetFormat);
        }
        
        public List<AssetFormat> getSupportedFormats() {
            return new ArrayList<>(transcoders.keySet());
        }
    }
    
    // Intelligent asset quality selection
    private class AssetQualitySelector {
        private final DeviceProfiler deviceProfiler;
        private final UserPreferenceManager userPrefs;
        
        public AssetQualitySelector() {
            this.deviceProfiler = new DeviceProfiler();
            this.userPrefs = new UserPreferenceManager();
        }
        
        public AssetQuality selectOptimalQuality(AssetType assetType, 
                                                AssetPriority priority,
                                                float distanceFromCamera) {
            // User preference override
            AssetQuality userPreference = userPrefs.getQualityPreference(assetType);
            if (userPreference != AssetQuality.AUTO) {
                return adjustForDevice(userPreference, assetType);
            }
            
            // Distance-based quality selection
            if (distanceFromCamera > 100.0f) { // Far away objects
                return AssetQuality.LOW;
            } else if (distanceFromCamera > 25.0f) { // Medium distance
                return AssetQuality.MEDIUM;
            }
            
            // Close objects: select based on device capabilities
            DeviceCapabilities capabilities = deviceProfiler.getDeviceCapabilities();
            
            if (capabilities.isHighEnd() && getBatteryLevel() > 0.3f) {
                return AssetQuality.HIGH;
            } else if (capabilities.isMidRange()) {
                return AssetQuality.MEDIUM;
            } else {
                return AssetQuality.LOW;
            }
        }
        
        public boolean shouldLoadHighQuality(AssetType assetType) {
            return deviceProfiler.getDeviceCapabilities().isHighEnd() &&
                   getBatteryLevel() > 0.4f &&
                   userPrefs.isHighQualityEnabled(assetType);
        }
        
        private AssetQuality adjustForDevice(AssetQuality requestedQuality, AssetType assetType) {
            DeviceCapabilities capabilities = deviceProfiler.getDeviceCapabilities();
            
            // Downgrade quality if device can't handle it
            if (requestedQuality == AssetQuality.HIGH && !capabilities.isHighEnd()) {
                return AssetQuality.MEDIUM;
            }
            
            if (requestedQuality == AssetQuality.MEDIUM && capabilities.isLowEnd()) {
                return AssetQuality.LOW;
            }
            
            return requestedQuality;
        }
    }
    
    // Background processing manager for non-critical assets
    private class BackgroundProcessingManager {
        private final ExecutorService backgroundExecutor;
        private final Semaphore processingSlots;
        private final AtomicInteger activeProcessingTasks;
        private final Queue<Runnable> processingQueue;
        
        private static final int MAX_CONCURRENT_PROCESSING = 3; // Limit for mobile
        private static final int MAX_QUEUE_SIZE = 50;
        
        public BackgroundProcessingManager() {
            this.backgroundExecutor = Executors.newFixedThreadPool(MAX_CONCURRENT_PROCESSING,
                r -> {
                    Thread thread = new Thread(r, "AssetProcessor");
                    thread.setPriority(Thread.MIN_PRIORITY); // Low priority background processing
                    return thread;
                });
            
            this.processingSlots = new Semaphore(MAX_CONCURRENT_PROCESSING);
            this.activeProcessingTasks = new AtomicInteger(0);
            this.processingQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
            
            startBackgroundMonitoring();
        }
        
        public CompletableFuture<ProcessedAsset> processInBackground(UUID assetId, 
                                                                    AssetType assetType,
                                                                    AssetPriority priority) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    processingSlots.acquire(); // Wait for available processing slot
                    activeProcessingTasks.incrementAndGet();
                    
                    // Perform asset processing
                    ProcessedAsset result = performAssetProcessing(assetId, assetType, priority);
                    
                    return result;
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssetProcessingException("Background processing interrupted", e);
                } finally {
                    activeProcessingTasks.decrementAndGet();
                    processingSlots.release();
                }
            }, backgroundExecutor);
        }
        
        public ExecutorService getExecutor() {
            return backgroundExecutor;
        }
        
        public boolean hasCapacity() {
            return processingSlots.availablePermits() > 0 && 
                   processingQueue.size() < MAX_QUEUE_SIZE * 0.8f;
        }
        
        private void startBackgroundMonitoring() {
            // Monitor system resources and adjust processing accordingly
            ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
            monitor.scheduleAtFixedRate(this::adjustProcessingForSystemState, 
                10, 10, TimeUnit.SECONDS);
        }
        
        private void adjustProcessingForSystemState() {
            float batteryLevel = getBatteryLevel();
            boolean isThermalThrottling = isThermalThrottling();
            boolean isCharging = isBatteryCharging();
            
            // Reduce background processing on low battery or thermal throttling
            if (batteryLevel < 0.2f || isThermalThrottling) {
                if (!isCharging) {
                    // Pause all background processing
                    pauseBackgroundProcessing();
                }
            } else if (batteryLevel > 0.5f && !isThermalThrottling) {
                // Resume background processing
                resumeBackgroundProcessing();
            }
        }
        
        private void pauseBackgroundProcessing() {
            // Implementation for pausing background asset processing
            Log.i("BackgroundProcessingManager", "Pausing background processing due to system constraints");
        }
        
        private void resumeBackgroundProcessing() {
            // Implementation for resuming background asset processing
            Log.i("BackgroundProcessingManager", "Resuming background processing");
        }
    }
}
```

**Week 37-38: Storage Optimization and Caching Strategy**

```java
// File: com/lumiyaviewer/lumiya/render/assets/StorageOptimizer.java

public class StorageOptimizer {
    private final TieredStorageManager storageManager;
    private final CompressionManager compressionManager;
    private final CacheEvictionStrategy evictionStrategy;
    
    // Storage monitoring
    private final StorageMonitor storageMonitor;
    private final UsageAnalyzer usageAnalyzer;
    
    public StorageOptimizer() {
        this.storageManager = new TieredStorageManager();
        this.compressionManager = new CompressionManager();
        this.evictionStrategy = new LRUWithPriorityEvictionStrategy();
        
        this.storageMonitor = new StorageMonitor();
        this.usageAnalyzer = new UsageAnalyzer();
        
        initializeStorageOptimization();
    }
    
    private void initializeStorageOptimization() {
        // Set up storage monitoring
        storageMonitor.setStorageThresholds(
            0.9f, // Warning at 90% full
            0.95f, // Critical at 95% full
            0.98f  // Emergency cleanup at 98% full
        );
        
        // Start background storage optimization
        startStorageOptimization();
    }
    
    public void optimizeStorage() {
        StorageStatus status = storageMonitor.getCurrentStorageStatus();
        
        if (status.getUsagePercentage() > 0.9f) {
            Log.i("StorageOptimizer", "Storage usage high (" + 
                  (status.getUsagePercentage() * 100) + "%), starting optimization");
            
            // Emergency storage cleanup
            performEmergencyCleanup();
        } else if (status.getUsagePercentage() > 0.8f) {
            // Regular storage optimization
            performRegularOptimization();
        }
    }
    
    private void performEmergencyCleanup() {
        // Step 1: Remove least recently used low-priority assets
        List<CachedAsset> candidatesForRemoval = evictionStrategy.selectAssetsForEviction(
            storageManager.getAllCachedAssets(), 0.3f); // Remove 30% of cache
        
        long freedSpace = 0;
        for (CachedAsset asset : candidatesForRemoval) {
            long assetSize = asset.getSizeOnDisk();
            if (storageManager.removeAsset(asset.getId())) {
                freedSpace += assetSize;
            }
        }
        
        // Step 2: Compress remaining assets if needed
        if (storageMonitor.getCurrentStorageStatus().getUsagePercentage() > 0.85f) {
            compressLargeAssets();
        }
        
        Log.i("StorageOptimizer", "Emergency cleanup freed " + (freedSpace / 1024 / 1024) + " MB");
    }
    
    private void performRegularOptimization() {
        // Analyze usage patterns to optimize storage
        UsagePattern pattern = usageAnalyzer.analyzeUsagePattern();
        
        // Compress infrequently used assets
        List<CachedAsset> infrequentAssets = pattern.getInfrequentlyUsedAssets();
        for (CachedAsset asset : infrequentAssets) {
            if (!asset.isCompressed() && asset.getSize() > 1024 * 1024) { // 1MB threshold
                compressionManager.compressAsset(asset);
            }
        }
        
        // Pre-load frequently used assets to faster storage tier
        List<CachedAsset> frequentAssets = pattern.getFrequentlyUsedAssets();
        for (CachedAsset asset : frequentAssets) {
            storageManager.promoteToFastTier(asset.getId());
        }
    }
    
    private void compressLargeAssets() {
        List<CachedAsset> largeAssets = storageManager.getAssetsLargerThan(5 * 1024 * 1024); // 5MB+
        
        for (CachedAsset asset : largeAssets) {
            if (!asset.isCompressed()) {
                long originalSize = asset.getSize();
                boolean compressed = compressionManager.compressAsset(asset);
                
                if (compressed) {
                    long newSize = asset.getSize();
                    long saved = originalSize - newSize;
                    Log.d("StorageOptimizer", "Compressed asset " + asset.getId() + 
                          ", saved " + (saved / 1024) + " KB");
                }
            }
        }
    }
    
    // Tiered storage management for different asset priorities
    private class TieredStorageManager {
        private final File fastCacheDir; // Internal storage for critical assets
        private final File standardCacheDir; // Internal storage for regular assets  
        private final File archiveCacheDir; // External storage for archived assets
        
        public TieredStorageManager() {
            Context context = getApplicationContext();
            this.fastCacheDir = new File(context.getCacheDir(), "assets_fast");
            this.standardCacheDir = new File(context.getCacheDir(), "assets_standard");
            this.archiveCacheDir = new File(context.getExternalCacheDir(), "assets_archive");
            
            // Ensure directories exist
            fastCacheDir.mkdirs();
            standardCacheDir.mkdirs();
            archiveCacheDir.mkdirs();
        }
        
        public void storeAsset(CachedAsset asset, StorageTier tier) {
            File targetDir = getDirectoryForTier(tier);
            File assetFile = new File(targetDir, asset.getId().toString());
            
            try {
                // Store asset with appropriate compression
                if (tier == StorageTier.ARCHIVE) {
                    // Use higher compression for archived assets
                    compressionManager.storeCompressed(asset, assetFile, CompressionLevel.HIGH);
                } else if (tier == StorageTier.STANDARD) {
                    // Medium compression for standard assets
                    compressionManager.storeCompressed(asset, assetFile, CompressionLevel.MEDIUM);
                } else {
                    // No compression for fast tier assets
                    storeUncompressed(asset, assetFile);
                }
                
                // Update asset metadata
                asset.setStorageTier(tier);
                asset.setStorageLocation(assetFile.getAbsolutePath());
                
            } catch (IOException e) {
                Log.e("TieredStorageManager", "Failed to store asset " + asset.getId(), e);
                throw new StorageException("Asset storage failed", e);
            }
        }
        
        public void promoteToFastTier(UUID assetId) {
            // Move frequently accessed asset to fast storage tier
            CachedAsset asset = findAsset(assetId);
            if (asset != null && asset.getStorageTier() != StorageTier.FAST) {
                // Check if fast tier has capacity
                if (hasCapacity(StorageTier.FAST, asset.getSize())) {
                    moveAsset(asset, StorageTier.FAST);
                }
            }
        }
        
        private File getDirectoryForTier(StorageTier tier) {
            switch (tier) {
                case FAST: return fastCacheDir;
                case STANDARD: return standardCacheDir;
                case ARCHIVE: return archiveCacheDir;
                default: throw new IllegalArgumentException("Unknown storage tier: " + tier);
            }
        }
    }
    
    // Compression management system
    private class CompressionManager {
        private final Map<CompressionType, AssetCompressor> compressors;
        
        public CompressionManager() {
            this.compressors = new HashMap<>();
            initializeCompressors();
        }
        
        private void initializeCompressors() {
            compressors.put(CompressionType.ZSTD, new ZstdCompressor());
            compressors.put(CompressionType.LZ4, new LZ4Compressor());
            compressors.put(CompressionType.GZIP, new GzipCompressor());
        }
        
        public boolean compressAsset(CachedAsset asset) {
            // Select optimal compression type based on asset type and size
            CompressionType optimalType = selectOptimalCompression(asset);
            AssetCompressor compressor = compressors.get(optimalType);
            
            if (compressor == null) {
                Log.w("CompressionManager", "No compressor available for type: " + optimalType);
                return false;
            }
            
            try {
                // Compress asset in-place
                long originalSize = asset.getSize();
                CompressedAssetData compressed = compressor.compress(asset.getData());
                
                // Update asset with compressed data
                asset.setCompressedData(compressed);
                asset.setCompressionType(optimalType);
                
                long newSize = compressed.getSize();
                float compressionRatio = (float) newSize / originalSize;
                
                Log.d("CompressionManager", "Asset " + asset.getId() + " compressed from " +
                      (originalSize / 1024) + " KB to " + (newSize / 1024) + " KB " +
                      "(" + (int)(compressionRatio * 100) + "%)");
                
                return true;
                
            } catch (Exception e) {
                Log.e("CompressionManager", "Failed to compress asset " + asset.getId(), e);
                return false;
            }
        }
        
        private CompressionType selectOptimalCompression(CachedAsset asset) {
            // ZSTD for best compression ratio and good decompression speed
            if (asset.getType() == AssetType.TEXTURE && asset.getSize() > 1024 * 1024) {
                return CompressionType.ZSTD;
            }
            
            // LZ4 for fast decompression (critical assets)
            if (asset.getPriority() == AssetPriority.HIGH || asset.getPriority() == AssetPriority.CRITICAL) {
                return CompressionType.LZ4;
            }
            
            // GZIP for general purpose compression
            return CompressionType.GZIP;
        }
    }
    
    public StorageAvailability getStorageStatus() {
        StorageStatus status = storageMonitor.getCurrentStorageStatus();
        
        if (status.getUsagePercentage() > 0.95f) {
            return StorageAvailability.CRITICAL;
        } else if (status.getUsagePercentage() > 0.85f) {
            return StorageAvailability.LIMITED;
        } else {
            return StorageAvailability.AVAILABLE;
        }
    }
}
```

**Deliverables for Phase 2.2:**
- ✅ Multi-format transcoding system supporting Basis Universal, ASTC, ETC2, GLTF, Draco
- ✅ Progressive loading system with quality upgrades
- ✅ Intelligent storage optimization with tiered caching
- ✅ Background processing manager with system resource awareness
- ✅ Comprehensive asset compression system

**Week 39-40: Integration Testing and Validation**
- **Task 2.2.1:** End-to-end asset pipeline testing
- **Task 2.2.2:** Storage optimization validation across device storage scenarios
- **Task 2.2.3:** Performance impact measurement and optimization
- **Task 2.2.4:** Network condition adaptation testing

#### Phase 2.3: Advanced User Experience Features (Months 9-10)

**Week 41-42: Enhanced Real-Time Communication System**

Building on Phase 1 WebSocket implementation:

```java
// File: com/lumiyaviewer/lumiya/slproto/communication/AdvancedCommunicationManager.java

public class AdvancedCommunicationManager {
    // Integration with Phase 1 WebSocket system
    private final HybridEventManager hybridEventManager;
    private final WebSocketEventStream webSocketStream;
    
    // Advanced communication features
    private final VoiceProximityManager voiceProximityManager;
    private final ChatHistoryManager chatHistoryManager;
    private final PresenceManager presenceManager;
    private final NotificationManager notificationManager;
    
    // Mobile optimizations
    private final OfflineMessageQueue offlineQueue;
    private final BandwidthAdaptiveCompression compressionManager;
    private final PushNotificationIntegration pushIntegration;
    
    public AdvancedCommunicationManager(HybridEventManager hybridEventManager,
                                       WebSocketEventStream webSocketStream) {
        this.hybridEventManager = hybridEventManager;
        this.webSocketStream = webSocketStream;
        
        this.voiceProximityManager = new VoiceProximityManager();
        this.chatHistoryManager = new ChatHistoryManager();
        this.presenceManager = new PresenceManager();
        this.notificationManager = new NotificationManager();
        
        this.offlineQueue = new OfflineMessageQueue();
        this.compressionManager = new BandwidthAdaptiveCompression();
        this.pushIntegration = new PushNotificationIntegration();
        
        initializeCommunicationSystem();
    }
    
    private void initializeCommunicationSystem() {
        // Set up communication event handlers
        hybridEventManager.registerEventHandler(ChatMessageEvent.class, this::handleChatMessage);
        hybridEventManager.registerEventHandler(VoiceDataEvent.class, this::handleVoiceData);
        hybridEventManager.registerEventHandler(PresenceUpdateEvent.class, this::handlePresenceUpdate);
        
        // Initialize offline message handling
        offlineQueue.loadPersistedMessages();
        
        // Set up push notification integration
        pushIntegration.initialize();
    }
    
    public CompletableFuture<Void> sendChatMessage(UUID recipientId, String message, ChatType chatType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create chat message with mobile optimizations
                ChatMessage chatMessage = ChatMessage.builder()
                    .senderId(getCurrentUserId())
                    .recipientId(recipientId)
                    .message(compressionManager.compressIfNeeded(message))
                    .chatType(chatType)
                    .timestamp(System.currentTimeMillis())
                    .messageId(UUID.randomUUID())
                    .build();
                
                // Route message based on connection status
                if (hybridEventManager.isConnected()) {
                    // Send immediately via WebSocket or UDP
                    return hybridEventManager.sendEvent(new ChatMessageEvent(chatMessage));
                } else {
                    // Queue for offline delivery
                    offlineQueue.queueMessage(chatMessage);
                    
                    // Try push notification as fallback
                    if (chatType == ChatType.PRIVATE_MESSAGE) {
                        pushIntegration.sendOfflineMessage(recipientId, message);
                    }
                    
                    return CompletableFuture.completedFuture(null);
                }
            } catch (Exception e) {
                Log.e("AdvancedCommunicationManager", "Failed to send chat message", e);
                throw new CommunicationException("Chat message send failed", e);
            }
        });
    }
    
    private void handleChatMessage(ChatMessageEvent event) {
        ChatMessage message = event.getMessage();
        
        // Process incoming message with mobile optimizations
        try {
            // Decompress message if needed
            String decompressedMessage = compressionManager.decompressIfNeeded(message.getMessage());
            ChatMessage processedMessage = message.withMessage(decompressedMessage);
            
            // Store in chat history
            chatHistoryManager.storeMessage(processedMessage);
            
            // Update UI
            notifyUIOfNewMessage(processedMessage);
            
            // Handle notifications based on app state
            if (!isAppInForeground()) {
                notificationManager.showChatNotification(processedMessage);
            }
            
            // Update presence information
            presenceManager.updateLastActivity(message.getSenderId());
            
        } catch (Exception e) {
            Log.w("AdvancedCommunicationManager", "Failed to process incoming chat message", e);
        }
    }
    
    // Voice proximity system for spatial audio
    private class VoiceProximityManager {
        private final SpatialAudioProcessor audioProcessor;
        private final ProximityCalculator proximityCalculator;
        private final VoiceQualityAdaptor qualityAdaptor;
        
        // Integration with existing spatial system
        private final SpatialObjectIndex spatialIndex;
        
        public VoiceProximityManager() {
            this.audioProcessor = new SpatialAudioProcessor();
            this.proximityCalculator = new ProximityCalculator();
            this.qualityAdaptor = new VoiceQualityAdaptor();
            
            // Use existing Linkpoint spatial indexing system
            this.spatialIndex = getSpatialObjectIndex(); 
        }
        
        public void processVoiceData(VoiceDataEvent event) {
            UUID speakerId = event.getSpeakerId();
            byte[] voiceData = event.getVoiceData();
            Vector3 speakerPosition = event.getSpeakerPosition();
            
            // Calculate proximity to current user
            Vector3 listenerPosition = getCurrentUserPosition();
            float distance = Vector3.distance(speakerPosition, listenerPosition);
            
            // Apply distance-based processing
            ProcessedVoiceData processedVoice = processVoiceForDistance(voiceData, distance);
            
            // Apply spatial audio effects
            if (distance <= MAX_VOICE_RANGE && processedVoice != null) {
                SpatialAudioData spatialAudio = audioProcessor.applySpatialEffects(
                    processedVoice, speakerPosition, listenerPosition);
                
                // Play processed audio
                playVoiceAudio(spatialAudio);
                
                // Update speaker presence
                presenceManager.updateSpeakerStatus(speakerId, true);
            } else {
                // Speaker out of range
                presenceManager.updateSpeakerStatus(speakerId, false);
            }
        }
        
        private ProcessedVoiceData processVoiceForDistance(byte[] voiceData, float distance) {
            // Adaptive voice quality based on distance and network conditions
            VoiceQuality targetQuality = qualityAdaptor.calculateOptimalQuality(distance);
            
            NetworkCondition networkCondition = getNetworkCondition();
            
            // Adjust quality based on network conditions
            if (networkCondition == NetworkCondition.POOR) {
                targetQuality = VoiceQuality.LOW; // Prioritize connectivity
            } else if (networkCondition == NetworkCondition.LIMITED) {
                targetQuality = VoiceQuality.MEDIUM; // Balance quality and bandwidth
            }
            
            // Process voice data according to target quality
            return audioProcessor.processVoice(voiceData, targetQuality);
        }
    }
    
    // Offline message queue for mobile connectivity
    private class OfflineMessageQueue {
        private final Queue<QueuedMessage> messageQueue;
        private final MessagePersistence persistence;
        private final RetryScheduler retryScheduler;
        
        private static final int MAX_QUEUE_SIZE = 1000;
        private static final long MAX_MESSAGE_AGE = 24 * 60 * 60 * 1000; // 24 hours
        
        public OfflineMessageQueue() {
            this.messageQueue = new PriorityQueue<>(new MessagePriorityComparator());
            this.persistence = new MessagePersistence();
            this.retryScheduler = new RetryScheduler();
        }
        
        public void queueMessage(ChatMessage message) {
            // Check queue capacity
            if (messageQueue.size() >= MAX_QUEUE_SIZE) {
                // Remove oldest low-priority message
                removeOldestLowPriorityMessage();
            }
            
            QueuedMessage queuedMessage = new QueuedMessage(
                message, 
                System.currentTimeMillis(),
                calculateMessagePriority(message)
            );
            
            messageQueue.offer(queuedMessage);
            
            // Persist to storage
            persistence.persistMessage(queuedMessage);
            
            Log.d("OfflineMessageQueue", "Queued message for offline delivery: " + message.getMessageId());
        }
        
        public void processOfflineMessages() {
            if (messageQueue.isEmpty()) return;
            
            Log.i("OfflineMessageQueue", "Processing " + messageQueue.size() + " offline messages");
            
            List<QueuedMessage> successfullySent = new ArrayList<>();
            List<QueuedMessage> failedMessages = new ArrayList<>();
            
            while (!messageQueue.isEmpty()) {
                QueuedMessage queuedMessage = messageQueue.poll();
                
                // Check if message is too old
                if (isMessageExpired(queuedMessage)) {
                    Log.d("OfflineMessageQueue", "Discarding expired message: " + 
                          queuedMessage.getMessage().getMessageId());
                    persistence.removePersistedMessage(queuedMessage);
                    continue;
                }
                
                // Attempt to send message
                try {
                    boolean sent = attemptToSendMessage(queuedMessage.getMessage());
                    if (sent) {
                        successfullySent.add(queuedMessage);
                    } else {
                        failedMessages.add(queuedMessage);
                    }
                } catch (Exception e) {
                    Log.w("OfflineMessageQueue", "Failed to send offline message", e);
                    failedMessages.add(queuedMessage);
                }
            }
            
            // Handle results
            for (QueuedMessage sent : successfullySent) {
                persistence.removePersistedMessage(sent);
            }
            
            // Re-queue failed messages with exponential backoff
            for (QueuedMessage failed : failedMessages) {
                retryScheduler.scheduleRetry(failed);
            }
            
            Log.i("OfflineMessageQueue", "Offline message processing complete. " +
                  "Sent: " + successfullySent.size() + ", Failed: " + failedMessages.size());
        }
        
        private MessagePriority calculateMessagePriority(ChatMessage message) {
            // Prioritize messages based on type and recipient
            switch (message.getChatType()) {
                case PRIVATE_MESSAGE:
                    return MessagePriority.HIGH;
                case GROUP_MESSAGE:
                    return MessagePriority.MEDIUM;
                case LOCAL_CHAT:
                    return MessagePriority.LOW;
                case SYSTEM_MESSAGE:
                    return MessagePriority.CRITICAL;
                default:
                    return MessagePriority.MEDIUM;
            }
        }
        
        private boolean isMessageExpired(QueuedMessage message) {
            long age = System.currentTimeMillis() - message.getQueuedTime();
            return age > MAX_MESSAGE_AGE;
        }
        
        private void removeOldestLowPriorityMessage() {
            // Find and remove the oldest low-priority message
            QueuedMessage oldest = null;
            for (QueuedMessage message : messageQueue) {
                if (message.getPriority() == MessagePriority.LOW) {
                    if (oldest == null || message.getQueuedTime() < oldest.getQueuedTime()) {
                        oldest = message;
                    }
                }
            }
            
            if (oldest != null) {
                messageQueue.remove(oldest);
                persistence.removePersistedMessage(oldest);
            }
        }
    }
    
    // Push notification integration for offline communication
    private class PushNotificationIntegration {
        private final FirebaseMessaging firebaseMessaging;
        private final NotificationChannelManager channelManager;
        private final TokenManager tokenManager;
        
        public PushNotificationIntegration() {
            this.firebaseMessaging = FirebaseMessaging.getInstance();
            this.channelManager = new NotificationChannelManager();
            this.tokenManager = new TokenManager();
        }
        
        public void initialize() {
            // Set up notification channels
            channelManager.createNotificationChannels();
            
            // Register for FCM token updates
            firebaseMessaging.getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    tokenManager.updateFCMToken(token);
                    
                    // Register token with server
                    registerTokenWithServer(token);
                }
            });
        }
        
        public void sendOfflineMessage(UUID recipientId, String message) {
            // Send push notification request to server
            // Server will deliver push notification if recipient is offline
            
            OfflinePushRequest request = OfflinePushRequest.builder()
                .senderId(getCurrentUserId())
                .recipientId(recipientId)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
            
            CompletableFuture.supplyAsync(() -> {
                try {
                    // Send via CAPS or HTTP/2 (Phase 1 integration)
                    return sendPushNotificationRequest(request);
                } catch (Exception e) {
                    Log.w("PushNotificationIntegration", "Failed to send push notification request", e);
                    return false;
                }
            });
        }
        
        private void registerTokenWithServer(String fcmToken) {
            TokenRegistrationRequest request = TokenRegistrationRequest.builder()
                .userId(getCurrentUserId())
                .fcmToken(fcmToken)
                .deviceId(getDeviceId())
                .appVersion(getAppVersion())
                .build();
            
            // Use Phase 1 HTTP/2 CAPS client for efficient token registration
            // This ensures the server can send push notifications when user is offline
        }
    }
}

// Enhanced presence management for better social features
// File: com/lumiyaviewer/lumiya/slproto/communication/PresenceManager.java

public class PresenceManager {
    private final Map<UUID, UserPresence> userPresences;
    private final PresenceBroadcaster presenceBroadcaster;
    private final ActivityMonitor activityMonitor;
    
    // Integration with existing spatial system
    private final SpatialObjectIndex spatialIndex;
    
    // Mobile optimizations
    private final BatteryAwarePresenceUpdates batteryOptimizer;
    private final NetworkConditionAdaptor networkAdaptor;
    
    public PresenceManager() {
        this.userPresences = new ConcurrentHashMap<>();
        this.presenceBroadcaster = new PresenceBroadcaster();
        this.activityMonitor = new ActivityMonitor();
        
        this.spatialIndex = getSpatialObjectIndex();
        
        this.batteryOptimizer = new BatteryAwarePresenceUpdates();
        this.networkAdaptor = new NetworkConditionAdaptor();
        
        initializePresenceSystem();
    }
    
    private void initializePresenceSystem() {
        // Start activity monitoring
        activityMonitor.startMonitoring();
        
        // Start presence broadcasting with mobile optimizations
        batteryOptimizer.startOptimizedPresenceBroadcasting();
        
        // Set up network condition adaptation
        networkAdaptor.adaptPresenceUpdatesForNetwork();
    }
    
    public void updateUserPresence(UUID userId, UserPresence presence) {
        UserPresence previousPresence = userPresences.get(userId);
        userPresences.put(userId, presence);
        
        // Notify UI of presence changes
        if (previousPresence == null || !previousPresence.equals(presence)) {
            notifyPresenceChanged(userId, previousPresence, presence);
        }
        
        // Update spatial tracking if user is nearby
        if (isUserNearby(userId)) {
            updateSpatialPresence(userId, presence);
        }
    }
    
    public CompletableFuture<Void> broadcastPresenceUpdate() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserPresence currentPresence = getCurrentUserPresence();
                
                // Optimize presence update based on conditions
                PresenceUpdateStrategy strategy = networkAdaptor.getOptimalUpdateStrategy();
                
                switch (strategy) {
                    case FULL_UPDATE:
                        return broadcastFullPresence(currentPresence);
                    case DELTA_UPDATE:
                        return broadcastPresenceDelta(currentPresence);
                    case MINIMAL_UPDATE:
                        return broadcastMinimalPresence(currentPresence);
                    case SKIP_UPDATE:
                        return CompletableFuture.completedFuture(null);
                    default:
                        throw new IllegalStateException("Unknown presence update strategy: " + strategy);
                }
            } catch (Exception e) {
                Log.w("PresenceManager", "Failed to broadcast presence update", e);
                throw new PresenceException("Presence broadcast failed", e);
            }
        });
    }
    
    private UserPresence getCurrentUserPresence() {
        return UserPresence.builder()
            .userId(getCurrentUserId())
            .status(activityMonitor.getCurrentStatus())
            .location(getCurrentUserPosition())
            .activity(activityMonitor.getCurrentActivity())
            .lastActive(System.currentTimeMillis())
            .deviceType(DeviceType.MOBILE)
            .appVersion(getAppVersion())
            .build();
    }
    
    // Battery-aware presence updates
    private class BatteryAwarePresenceUpdates {
        private ScheduledExecutorService scheduler;
        private volatile boolean optimizationEnabled = false;
        
        // Update intervals based on battery level
        private static final long NORMAL_UPDATE_INTERVAL = 30000; // 30 seconds
        private static final long LOW_BATTERY_UPDATE_INTERVAL = 120000; // 2 minutes
        private static final long CRITICAL_BATTERY_UPDATE_INTERVAL = 300000; // 5 minutes
        
        public void startOptimizedPresenceBroadcasting() {
            if (optimizationEnabled) return;
            
            optimizationEnabled = true;
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "PresenceBroadcaster");
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            });
            
            // Start with normal interval, adjust based on battery level
            scheduleNextUpdate(NORMAL_UPDATE_INTERVAL);
        }
        
        private void scheduleNextUpdate(long intervalMs) {
            scheduler.schedule(() -> {
                try {
                    // Broadcast presence update
                    broadcastPresenceUpdate().join();
                    
                    // Calculate next update interval based on current conditions
                    long nextInterval = calculateNextUpdateInterval();
                    scheduleNextUpdate(nextInterval);
                    
                } catch (Exception e) {
                    Log.w("BatteryAwarePresenceUpdates", "Presence update failed", e);
                    // Continue with normal interval on error
                    scheduleNextUpdate(NORMAL_UPDATE_INTERVAL);
                }
            }, intervalMs, TimeUnit.MILLISECONDS);
        }
        
        private long calculateNextUpdateInterval() {
            float batteryLevel = getBatteryLevel();
            boolean isCharging = isBatteryCharging();
            ActivityLevel activityLevel = activityMonitor.getActivityLevel();
            
            // Faster updates when charging or highly active
            if (isCharging || activityLevel == ActivityLevel.HIGH) {
                return NORMAL_UPDATE_INTERVAL;
            }
            
            // Slower updates on low battery
            if (batteryLevel < 0.15f) {
                return CRITICAL_BATTERY_UPDATE_INTERVAL;
            } else if (batteryLevel < 0.3f) {
                return LOW_BATTERY_UPDATE_INTERVAL;
            } else {
                return NORMAL_UPDATE_INTERVAL;
            }
        }
    }
    
    // Network condition adaptation for presence updates
    private class NetworkConditionAdaptor {
        public PresenceUpdateStrategy getOptimalUpdateStrategy() {
            NetworkCondition condition = getNetworkCondition();
            float batteryLevel = getBatteryLevel();
            
            // Critical battery: skip non-essential updates
            if (batteryLevel < 0.1f) {
                return PresenceUpdateStrategy.SKIP_UPDATE;
            }
            
            // Adapt to network conditions
            switch (condition) {
                case EXCELLENT:
                    return PresenceUpdateStrategy.FULL_UPDATE;
                case GOOD:
                    return PresenceUpdateStrategy.FULL_UPDATE;
                case FAIR:
                    return PresenceUpdateStrategy.DELTA_UPDATE;
                case POOR:
                    return PresenceUpdateStrategy.MINIMAL_UPDATE;
                case LIMITED:
                    return PresenceUpdateStrategy.MINIMAL_UPDATE;
                case OFFLINE:
                    return PresenceUpdateStrategy.SKIP_UPDATE;
                default:
                    return PresenceUpdateStrategy.DELTA_UPDATE;
            }
        }
        
        public void adaptPresenceUpdatesForNetwork() {
            // Monitor network condition changes and adjust presence broadcasting
            NetworkMonitor.getInstance().addNetworkChangeListener(condition -> {
                Log.d("NetworkConditionAdaptor", "Network condition changed to: " + condition);
                
                // Immediately adjust presence broadcasting strategy
                PresenceUpdateStrategy strategy = getOptimalUpdateStrategy();
                presenceBroadcaster.setUpdateStrategy(strategy);
            });
        }
    }
}
```

**Week 43-44: Advanced UI Responsiveness and Animation System**

```java
// File: com/lumiyaviewer/lumiya/ui/animation/AdvancedAnimationSystem.java

public class AdvancedAnimationSystem {
    // Integration with existing UI system
    private final EventBus eventBus;
    
    // Animation components
    private final AnimationEngine animationEngine;
    private final TransitionManager transitionManager;
    private final PerformanceAdaptiveAnimations adaptiveAnimations;
    private final GestureAnimationHandler gestureHandler;
    
    // Mobile optimization
    private final FrameRateMonitor frameRateMonitor;
    private final BatteryAwareAnimations batteryOptimizer;
    private final AnimationQualityManager qualityManager;
    
    public AdvancedAnimationSystem(EventBus eventBus) {
        this.eventBus = eventBus;
        
        this.animationEngine = new AnimationEngine();
        this.transitionManager = new TransitionManager();
        this.adaptiveAnimations = new PerformanceAdaptiveAnimations();
        this.gestureHandler = new GestureAnimationHandler();
        
        this.frameRateMonitor = new FrameRateMonitor();
        this.batteryOptimizer = new BatteryAwareAnimations();
        this.qualityManager = new AnimationQualityManager();
        
        initializeAnimationSystem();
    }
    
    private void initializeAnimationSystem() {
        // Start frame rate monitoring
        frameRateMonitor.startMonitoring();
        
        // Initialize battery-aware optimizations
        batteryOptimizer.initialize();
        
        // Set up performance adaptive animations
        adaptiveAnimations.initialize(frameRateMonitor);
        
        // Register for relevant events
        eventBus.register(UIPerformanceEvent.class, this::handleUIPerformanceEvent);
        eventBus.register(BatteryLevelEvent.class, this::handleBatteryLevelEvent);
    }
    
    public CompletableFuture<Void> animateViewTransition(View fromView, View toView, 
                                                         TransitionType transitionType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Determine optimal animation quality
                AnimationQuality quality = qualityManager.getOptimalAnimationQuality();
                
                // Create transition based on type and quality
                ViewTransition transition = transitionManager.createTransition(
                    fromView, toView, transitionType, quality);
                
                // Execute animation with performance monitoring
                return animationEngine.executeTransition(transition);
                
            } catch (Exception e) {
                Log.w("AdvancedAnimationSystem", "View transition animation failed", e);
                // Fallback to immediate transition
                performImmediateTransition(fromView, toView);
                return CompletableFuture.completedFuture(null);
            }
        });
    }
    
    public void animateObjectInteraction(SLObject object, InteractionType interactionType) {
        // Create context-aware animation for object interaction
        ObjectInteractionAnimation animation = createInteractionAnimation(object, interactionType);
        
        // Adapt animation based on current performance
        AnimationQuality quality = qualityManager.getCurrentQuality();
        animation.setQuality(quality);
        
        // Execute animation
        animationEngine.executeObjectAnimation(animation);
        
        // Provide haptic feedback if supported
        if (supportsHapticFeedback()) {
            provideHapticFeedback(interactionType);
        }
    }
    
    private ObjectInteractionAnimation createInteractionAnimation(SLObject object, 
                                                                  InteractionType interactionType) {
        switch (interactionType) {
            case TOUCH:
                return new TouchInteractionAnimation(object);
            case GRAB:
                return new GrabInteractionAnimation(object);
            case RELEASE:
                return new ReleaseInteractionAnimation(object);
            case HOVER:
                return new HoverInteractionAnimation(object);
            default:
                return new GenericInteractionAnimation(object, interactionType);
        }
    }
    
    // Performance adaptive animations
    private class PerformanceAdaptiveAnimations {
        private final Map<AnimationType, AnimationProfile> animationProfiles;
        private AnimationPerformanceLevel currentPerformanceLevel;
        
        public PerformanceAdaptiveAnimations() {
            this.animationProfiles = new HashMap<>();
            this.currentPerformanceLevel = AnimationPerformanceLevel.NORMAL;
            
            initializeAnimationProfiles();
        }
        
        public void initialize(FrameRateMonitor frameRateMonitor) {
            // Monitor frame rate and adapt animations
            frameRateMonitor.addFrameRateListener(frameRate -> {
                AnimationPerformanceLevel newLevel = determinePerformanceLevel(frameRate);
                if (newLevel != currentPerformanceLevel) {
                    currentPerformanceLevel = newLevel;
                    adaptAnimationsForPerformance(newLevel);
                }
            });
        }
        
        private AnimationPerformanceLevel determinePerformanceLevel(float frameRate) {
            if (frameRate >= 55.0f) {
                return AnimationPerformanceLevel.HIGH;
            } else if (frameRate >= 40.0f) {
                return AnimationPerformanceLevel.NORMAL;
            } else if (frameRate >= 25.0f) {
                return AnimationPerformanceLevel.LOW;
            } else {
                return AnimationPerformanceLevel.MINIMAL;
            }
        }
        
        private void adaptAnimationsForPerformance(AnimationPerformanceLevel level) {
            Log.i("PerformanceAdaptiveAnimations", "Adapting animations for performance level: " + level);
            
            switch (level) {
                case HIGH:
                    enableHighQualityAnimations();
                    break;
                case NORMAL:
                    enableStandardAnimations();
                    break;
                case LOW:
                    enableLowQualityAnimations();
                    break;
                case MINIMAL:
                    enableMinimalAnimations();
                    break;
            }
        }
        
        private void enableHighQualityAnimations() {
            // Enable all animation features
            animationEngine.setInterpolationQuality(InterpolationQuality.HIGH);
            animationEngine.setParticleSystemsEnabled(true);
            animationEngine.setComplexTransitionsEnabled(true);
            animationEngine.setMotionBlurEnabled(true);
        }
        
        private void enableStandardAnimations() {
            // Standard animation quality
            animationEngine.setInterpolationQuality(InterpolationQuality.MEDIUM);
            animationEngine.setParticleSystemsEnabled(true);
            animationEngine.setComplexTransitionsEnabled(true);
            animationEngine.setMotionBlurEnabled(false);
        }
        
        private void enableLowQualityAnimations() {
            // Reduce animation complexity
            animationEngine.setInterpolationQuality(InterpolationQuality.LOW);
            animationEngine.setParticleSystemsEnabled(false);
            animationEngine.setComplexTransitionsEnabled(false);
            animationEngine.setMotionBlurEnabled(false);
        }
        
        private void enableMinimalAnimations() {
            // Minimal animations only
            animationEngine.setInterpolationQuality(InterpolationQuality.LINEAR);
            animationEngine.setParticleSystemsEnabled(false);
            animationEngine.setComplexTransitionsEnabled(false);
            animationEngine.setMotionBlurEnabled(false);
            
            // Disable non-essential animations
            animationEngine.disableDecorativeAnimations();
        }
    }
    
    // Battery-aware animation optimizations
    private class BatteryAwareAnimations {
        private BatteryOptimizationLevel currentLevel;
        private ScheduledExecutorService batteryMonitor;
        
        public void initialize() {
            this.currentLevel = BatteryOptimizationLevel.NORMAL;
            
            // Start battery level monitoring
            batteryMonitor = Executors.newSingleThreadScheduledExecutor();
            batteryMonitor.scheduleAtFixedRate(this::checkBatteryLevel, 0, 30, TimeUnit.SECONDS);
        }
        
        private void checkBatteryLevel() {
            float batteryLevel = getBatteryLevel();
            boolean isCharging = isBatteryCharging();
            
            BatteryOptimizationLevel newLevel = determineBatteryOptimizationLevel(batteryLevel, isCharging);
            
            if (newLevel != currentLevel) {
                currentLevel = newLevel;
                adaptAnimationsForBattery(newLevel);
            }
        }
        
        private BatteryOptimizationLevel determineBatteryOptimizationLevel(float batteryLevel, 
                                                                           boolean isCharging) {
            if (isCharging) {
                return BatteryOptimizationLevel.NORMAL; // No optimization when charging
            }
            
            if (batteryLevel < 0.1f) {
                return BatteryOptimizationLevel.CRITICAL;
            } else if (batteryLevel < 0.2f) {
                return BatteryOptimizationLevel.HIGH;
            } else if (batteryLevel < 0.4f) {
                return BatteryOptimizationLevel.MEDIUM;
            } else {
                return BatteryOptimizationLevel.NORMAL;
            }
        }
        
        private void adaptAnimationsForBattery(BatteryOptimizationLevel level) {
            Log.i("BatteryAwareAnimations", "Adapting animations for battery level: " + level);
            
            switch (level) {
                case CRITICAL:
                    // Disable almost all animations
                    animationEngine.setAnimationsEnabled(false);
                    transitionManager.useInstantTransitions(true);
                    break;
                    
                case HIGH:
                    // Minimal animations only
                    animationEngine.setAnimationsEnabled(true);
                    animationEngine.disableDecorativeAnimations();
                    transitionManager.useSimpleTransitions(true);
                    break;
                    
                case MEDIUM:
                    // Reduce animation complexity
                    animationEngine.setAnimationsEnabled(true);
                    animationEngine.reduceAnimationComplexity(0.5f);
                    transitionManager.useSimpleTransitions(false);
                    break;
                    
                case NORMAL:
                    // Normal animation behavior
                    animationEngine.setAnimationsEnabled(true);
                    animationEngine.resetAnimationComplexity();
                    transitionManager.useSimpleTransitions(false);
                    break;
            }
        }
    }
}
```

**Week 45-46: Testing and Optimization**
- **Task 2.3.1:** Communication system integration testing
- **Task 2.3.2:** Voice proximity system validation
- **Task 2.3.3:** Animation system performance testing
- **Task 2.3.4:** Battery impact measurement and optimization

**Deliverables for Phase 2.3:**
- ✅ Advanced communication system with offline message queuing
- ✅ Voice proximity system with spatial audio
- ✅ Push notification integration for offline scenarios
- ✅ Performance-adaptive animation system
- ✅ Battery-aware optimizations for all UI components

### Phase 2 Summary and Success Metrics (Month 12 Completion)

**Overall Phase 2 Deliverables:**
- ✅ Complete PBR material system with three-tier adaptive quality (Advanced/Standard/Simple)
- ✅ Multi-format asset pipeline with progressive loading and intelligent compression
- ✅ Advanced communication system with offline capabilities and spatial voice
- ✅ Performance-adaptive animation system with battery optimization

**Success Metrics:**
- **Visual Quality:** 40-60% improvement in rendering quality on capable devices
- **Performance:** Maintained 30+ FPS across all device tiers with adaptive quality
- **Storage Efficiency:** 50-70% reduction in storage usage through advanced compression
- **User Experience:** Real-time communication improvements and smooth animations
- **Battery Optimization:** 20% improvement in battery life through adaptive systems

---

### Phase 3: Ecosystem Integration and Advanced Features (Months 13-18+) - DETAILED IMPLEMENTATION

**Estimated Effort:** 1200-1500 development hours across 5+ senior developers
**Risk Level:** High - Cross-platform expansion and ecosystem integration

**Prerequisites:** Phases 1 and 2 complete and stable in production

#### Phase 3.1: Cross-Platform Foundation Development (Months 13-14)

**Week 47-48: Desktop Companion Application Architecture**

Building on the existing Java codebase for cross-platform expansion:

```java
// File: com/lumiyaviewer/desktop/LinkpointDesktopApplication.java

public class LinkpointDesktopApplication {
    // Shared core components from mobile implementation
    private final SharedProtocolManager protocolManager;
    private final SharedAssetPipeline assetPipeline;
    private final SharedCommunicationManager communicationManager;
    
    // Desktop-specific components
    private final DesktopRenderingEngine desktopRenderer;
    private final DesktopUIManager uiManager;
    private final DesktopInputHandler inputHandler;
    private final DesktopWindowManager windowManager;
    
    // Cross-platform synchronization
    private final DeviceSynchronizationManager syncManager;
    private final SharedStateManager stateManager;
    private final CrossPlatformSettingsManager settingsManager;
    
    public LinkpointDesktopApplication() {
        // Initialize shared components with desktop optimizations
        this.protocolManager = new SharedProtocolManager(PlatformType.DESKTOP);
        this.assetPipeline = new SharedAssetPipeline(PlatformType.DESKTOP);
        this.communicationManager = new SharedCommunicationManager(PlatformType.DESKTOP);
        
        // Initialize desktop-specific components
        this.desktopRenderer = new DesktopRenderingEngine();
        this.uiManager = new DesktopUIManager();
        this.inputHandler = new DesktopInputHandler();
        this.windowManager = new DesktopWindowManager();
        
        // Initialize cross-platform features
        this.syncManager = new DeviceSynchronizationManager();
        this.stateManager = new SharedStateManager();
        this.settingsManager = new CrossPlatformSettingsManager();
        
        initializeDesktopApplication();
    }
    
    private void initializeDesktopApplication() {
        // Set up desktop-specific optimizations
        configureDesktopPerformance();
        
        // Initialize rendering system with desktop capabilities
        initializeDesktopRendering();
        
        // Set up cross-platform synchronization
        initializeCrossPlatformSync();
        
        // Start application
        startDesktopApplication();
    }
    
    private void configureDesktopPerformance() {
        // Desktop performance profile - utilize full system resources
        PerformanceProfile desktopProfile = PerformanceProfile.builder()
            .cpuCoreUsage(Runtime.getRuntime().availableProcessors()) // Use all CPU cores
            .memoryUsage(getAvailableMemory() * 0.6f) // Use 60% of available memory
            .gpuAcceleration(true) // Enable full GPU acceleration
            .diskCaching(true) // Enable aggressive disk caching
            .networkOptimization(NetworkOptimizationLevel.HIGH) // High network optimization
            .build();
            
        protocolManager.setPerformanceProfile(desktopProfile);
        assetPipeline.setPerformanceProfile(desktopProfile);
    }
    
    private void initializeDesktopRendering() {
        // Desktop rendering capabilities - much more powerful than mobile
        DesktopRenderingCapabilities capabilities = DesktopRenderingCapabilities.builder()
            .maxTextureSize(8192) // 8K textures supported
            .supportsPBR(true) // Full PBR support
            .supportsAdvancedLighting(true) // Dynamic shadows, reflections
            .supportsPostProcessing(true) // Advanced post-processing effects
            .supportsMultipleMonitors(true) // Multi-monitor support
            .supportsHighRefreshRate(true) // 144Hz+ support
            .build();
            
        desktopRenderer.initialize(capabilities);
        
        // Enable advanced features not available on mobile
        desktopRenderer.enableAdvancedPostProcessing();
        desktopRenderer.enableDynamicShadows();
        desktopRenderer.enableScreenSpaceReflections();
        desktopRenderer.enableAntiAliasing(AntiAliasingType.MSAA_8X);
    }
    
    private void initializeCrossPlatformSync() {
        // Set up synchronization with mobile device
        syncManager.initialize(getCurrentUserId());
        
        // Register for cross-platform events
        syncManager.registerSyncHandler(SyncEventType.SETTINGS_CHANGED, this::handleSettingsSync);
        syncManager.registerSyncHandler(SyncEventType.FRIENDS_LIST_UPDATED, this::handleFriendsListSync);
        syncManager.registerSyncHandler(SyncEventType.CHAT_HISTORY_UPDATED, this::handleChatHistorySync);
        syncManager.registerSyncHandler(SyncEventType.BOOKMARKS_CHANGED, this::handleBookmarksSync);
        
        // Start background synchronization
        syncManager.startBackgroundSync();
    }
    
    // Cross-platform state synchronization
    private class DeviceSynchronizationManager {
        private final CloudSyncService cloudSync;
        private final LocalSyncService localSync;
        private final ConflictResolutionManager conflictResolver;
        
        // Synchronization components
        private final SyncScheduler syncScheduler;
        private final SyncDataCompressor dataCompressor;
        private final EncryptionManager encryptionManager;
        
        public DeviceSynchronizationManager() {
            this.cloudSync = new CloudSyncService();
            this.localSync = new LocalSyncService();
            this.conflictResolver = new ConflictResolutionManager();
            
            this.syncScheduler = new SyncScheduler();
            this.dataCompressor = new SyncDataCompressor();
            this.encryptionManager = new EncryptionManager();
        }
        
        public void initialize(UUID userId) {
            // Initialize cloud sync service
            cloudSync.initialize(userId);
            
            // Initialize local network sync for same-network devices
            localSync.initialize(userId);
            
            // Set up encryption for sensitive data
            encryptionManager.initializeUserEncryption(userId);
        }
        
        public CompletableFuture<Void> syncDataBetweenDevices(SyncDataType dataType) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    // Get data to sync from desktop
                    SyncData desktopData = gatherSyncData(dataType);
                    
                    // Compress and encrypt data
                    CompressedSyncData compressedData = dataCompressor.compress(desktopData);
                    EncryptedSyncData encryptedData = encryptionManager.encrypt(compressedData);
                    
                    // Try local sync first (same network)
                    boolean localSyncSuccess = localSync.syncData(encryptedData);
                    
                    if (!localSyncSuccess) {
                        // Fall back to cloud sync
                        boolean cloudSyncSuccess = cloudSync.syncData(encryptedData);
                        
                        if (!cloudSyncSuccess) {
                            throw new SyncException("Both local and cloud sync failed");
                        }
                    }
                    
                    Log.i("DeviceSynchronizationManager", "Successfully synced " + dataType);
                    return null;
                    
                } catch (Exception e) {
                    Log.e("DeviceSynchronizationManager", "Failed to sync " + dataType, e);
                    throw new SyncException("Data synchronization failed", e);
                }
            });
        }
        
        public void startBackgroundSync() {
            // Schedule regular synchronization
            syncScheduler.scheduleRepeating(() -> {
                // Sync critical data frequently
                syncDataBetweenDevices(SyncDataType.SETTINGS);
                syncDataBetweenDevices(SyncDataType.CHAT_HISTORY);
            }, 5, TimeUnit.MINUTES);
            
            // Sync less critical data less frequently
            syncScheduler.scheduleRepeating(() -> {
                syncDataBetweenDevices(SyncDataType.BOOKMARKS);
                syncDataBetweenDevices(SyncDataType.FRIENDS_LIST);
                syncDataBetweenDevices(SyncDataType.CACHED_ASSETS);
            }, 30, TimeUnit.MINUTES);
        }
        
        // Local network synchronization for devices on same network
        private class LocalSyncService {
            private final NetworkDiscoveryService discoveryService;
            private final P2PConnectionManager p2pManager;
            private final LocalSyncProtocolHandler protocolHandler;
            
            public LocalSyncService() {
                this.discoveryService = new NetworkDiscoveryService();
                this.p2pManager = new P2PConnectionManager();
                this.protocolHandler = new LocalSyncProtocolHandler();
            }
            
            public void initialize(UUID userId) {
                // Start network discovery for other Linkpoint devices
                discoveryService.startDiscovery("linkpoint-sync", userId);
                
                // Set up P2P connection handling
                p2pManager.setConnectionHandler(this::handleP2PConnection);
                
                // Initialize sync protocol
                protocolHandler.initialize();
            }
            
            public boolean syncData(EncryptedSyncData data) {
                try {
                    // Discover nearby Linkpoint devices
                    List<DiscoveredDevice> nearbyDevices = discoveryService.getDiscoveredDevices();
                    
                    if (nearbyDevices.isEmpty()) {
                        Log.d("LocalSyncService", "No nearby devices found for local sync");
                        return false;
                    }
                    
                    // Attempt to sync with mobile device
                    for (DiscoveredDevice device : nearbyDevices) {
                        if (device.getDeviceType() == DeviceType.MOBILE) {
                            boolean success = syncWithDevice(device, data);
                            if (success) {
                                Log.i("LocalSyncService", "Successfully synced with mobile device: " + 
                                      device.getDeviceId());
                                return true;
                            }
                        }
                    }
                    
                    Log.d("LocalSyncService", "Local sync failed with all discovered devices");
                    return false;
                    
                } catch (Exception e) {
                    Log.w("LocalSyncService", "Local sync failed", e);
                    return false;
                }
            }
            
            private boolean syncWithDevice(DiscoveredDevice device, EncryptedSyncData data) {
                try {
                    // Establish P2P connection
                    P2PConnection connection = p2pManager.connectToDevice(device);
                    
                    if (connection != null && connection.isConnected()) {
                        // Send sync data using local protocol
                        SyncResult result = protocolHandler.sendSyncData(connection, data);
                        return result.isSuccessful();
                    }
                    
                    return false;
                    
                } catch (Exception e) {
                    Log.w("LocalSyncService", "Failed to sync with device: " + device.getDeviceId(), e);
                    return false;
                }
            }
        }
        
        // Cloud synchronization service
        private class CloudSyncService {
            private final CloudStorageClient storageClient;
            private final SyncConflictDetector conflictDetector;
            private final CloudSyncEncryption cloudEncryption;
            
            public CloudSyncService() {
                this.storageClient = new CloudStorageClient();
                this.conflictDetector = new SyncConflictDetector();
                this.cloudEncryption = new CloudSyncEncryption();
            }
            
            public void initialize(UUID userId) {
                // Initialize cloud storage client with user credentials
                storageClient.initialize(userId);
                
                // Set up cloud encryption
                cloudEncryption.initializeForUser(userId);
            }
            
            public boolean syncData(EncryptedSyncData data) {
                try {
                    // Check for conflicts with cloud data
                    CloudSyncMetadata cloudMetadata = storageClient.getMetadata(data.getDataType());
                    
                    if (conflictDetector.hasConflict(data.getMetadata(), cloudMetadata)) {
                        // Resolve conflict before syncing
                        SyncData resolvedData = conflictResolver.resolveConflict(data, cloudMetadata);
                        data = encryptionManager.encrypt(dataCompressor.compress(resolvedData));
                    }
                    
                    // Upload to cloud storage
                    CloudUploadResult result = storageClient.uploadSyncData(data);
                    
                    if (result.isSuccessful()) {
                        Log.i("CloudSyncService", "Successfully synced " + data.getDataType() + " to cloud");
                        return true;
                    } else {
                        Log.w("CloudSyncService", "Cloud sync failed: " + result.getError());
                        return false;
                    }
                    
                } catch (Exception e) {
                    Log.e("CloudSyncService", "Cloud sync failed", e);
                    return false;
                }
            }
        }
    }
    
    // Desktop rendering engine with advanced capabilities
    private class DesktopRenderingEngine {
        // Integration with shared rendering components from mobile
        private final SharedRenderingCore sharedCore;
        
        // Desktop-specific advanced features
        private final AdvancedPostProcessingPipeline postProcessing;
        private final DynamicShadowSystem shadowSystem;
        private final ScreenSpaceReflectionSystem reflectionSystem;
        private final AntiAliasingSystem antiAliasing;
        private final MultiMonitorSupport multiMonitor;
        
        // Performance optimization for desktop
        private final DesktopPerformanceMonitor performanceMonitor;
        private final DesktopResourceManager resourceManager;
        
        public DesktopRenderingEngine() {
            this.sharedCore = new SharedRenderingCore(PlatformType.DESKTOP);
            
            this.postProcessing = new AdvancedPostProcessingPipeline();
            this.shadowSystem = new DynamicShadowSystem();
            this.reflectionSystem = new ScreenSpaceReflectionSystem();
            this.antiAliasing = new AntiAliasingSystem();
            this.multiMonitor = new MultiMonitorSupport();
            
            this.performanceMonitor = new DesktopPerformanceMonitor();
            this.resourceManager = new DesktopResourceManager();
        }
        
        public void initialize(DesktopRenderingCapabilities capabilities) {
            // Initialize shared rendering core with desktop capabilities
            sharedCore.initialize(capabilities);
            
            // Initialize advanced desktop features
            if (capabilities.supportsPostProcessing()) {
                postProcessing.initialize();
            }
            
            if (capabilities.supportsAdvancedLighting()) {
                shadowSystem.initialize();
                reflectionSystem.initialize();
            }
            
            if (capabilities.supportsMultipleMonitors()) {
                multiMonitor.initialize();
            }
            
            // Start performance monitoring
            performanceMonitor.startMonitoring();
            
            Log.i("DesktopRenderingEngine", "Desktop rendering engine initialized with capabilities: " + 
                  capabilities.toString());
        }
        
        public void renderFrame(RenderContext context) {
            try {
                // Start frame timing
                performanceMonitor.startFrameTiming();
                
                // Render using shared core
                sharedCore.renderFrame(context);
                
                // Apply desktop-specific post-processing
                if (postProcessing.isEnabled()) {
                    postProcessing.applyPostProcessingEffects(context);
                }
                
                // Apply anti-aliasing
                if (antiAliasing.isEnabled()) {
                    antiAliasing.applyAntiAliasing(context);
                }
                
                // Handle multi-monitor rendering
                if (multiMonitor.isMultiMonitorSetup()) {
                    multiMonitor.renderToAdditionalMonitors(context);
                }
                
                // Finish frame timing
                performanceMonitor.endFrameTiming();
                
            } catch (Exception e) {
                Log.e("DesktopRenderingEngine", "Desktop frame rendering failed", e);
                // Fallback to shared core only
                sharedCore.renderFrame(context);
            }
        }
        
        // Advanced post-processing pipeline not available on mobile
        private class AdvancedPostProcessingPipeline {
            private final List<PostProcessingEffect> effects;
            private final FramebufferManager framebufferManager;
            
            public AdvancedPostProcessingPipeline() {
                this.effects = new ArrayList<>();
                this.framebufferManager = new FramebufferManager();
            }
            
            public void initialize() {
                // Initialize advanced post-processing effects
                effects.add(new ScreenSpaceAmbientOcclusion());
                effects.add(new ScreenSpaceReflections());
                effects.add(new MotionBlur());
                effects.add(new DepthOfField());
                effects.add(new Bloom());
                effects.add(new ToneMapping());
                effects.add(new ColorGrading());
                
                // Initialize framebuffer chain
                framebufferManager.initializePostProcessingChain(effects.size());
                
                Log.i("AdvancedPostProcessingPipeline", "Initialized " + effects.size() + 
                      " post-processing effects");
            }
            
            public void applyPostProcessingEffects(RenderContext context) {
                if (effects.isEmpty()) return;
                
                // Apply effects in sequence using ping-pong framebuffers
                Framebuffer sourceBuffer = context.getCurrentFramebuffer();
                
                for (int i = 0; i < effects.size(); i++) {
                    PostProcessingEffect effect = effects.get(i);
                    Framebuffer targetBuffer = framebufferManager.getNextFramebuffer();
                    
                    // Apply effect
                    effect.apply(sourceBuffer, targetBuffer, context);
                    
                    // Ping-pong for next effect
                    sourceBuffer = targetBuffer;
                }
                
                // Final result is in sourceBuffer
                context.setFinalFramebuffer(sourceBuffer);
            }
        }
        
        // Dynamic shadow system for realistic lighting
        private class DynamicShadowSystem {
            private final ShadowMapManager shadowMapManager;
            private final CascadedShadowMaps cascadedShadows;
            private final SoftShadowRenderer softShadows;
            
            public DynamicShadowSystem() {
                this.shadowMapManager = new ShadowMapManager();
                this.cascadedShadows = new CascadedShadowMaps();
                this.softShadows = new SoftShadowRenderer();
            }
            
            public void initialize() {
                // Initialize shadow mapping with high resolution for desktop
                shadowMapManager.initialize(4096); // 4K shadow maps
                
                // Set up cascaded shadow maps for sun shadows
                cascadedShadows.initialize(4); // 4 cascade levels
                
                // Initialize soft shadow rendering
                softShadows.initialize();
                
                Log.i("DynamicShadowSystem", "Dynamic shadow system initialized");
            }
            
            public void renderShadows(List<LightSource> lights, RenderContext context) {
                // Render shadows for all relevant lights
                for (LightSource light : lights) {
                    if (light.castsShadows() && light.isVisible(context.getCamera())) {
                        renderLightShadows(light, context);
                    }
                }
                
                // Render cascaded sun shadows
                if (context.hasSunLight()) {
                    cascadedShadows.renderSunShadows(context.getSunLight(), context);
                }
            }
            
            private void renderLightShadows(LightSource light, RenderContext context) {
                ShadowMap shadowMap = shadowMapManager.getShadowMapForLight(light);
                
                // Render to shadow map
                shadowMap.bind();
                renderSceneFromLightPerspective(light, context);
                shadowMap.unbind();
                
                // Apply soft shadows
                if (softShadows.isEnabled()) {
                    softShadows.applySoftShadowing(shadowMap, light);
                }
            }
        }
    }
}
```

**Week 49-50: Desktop UI and Input Management**

```java
// File: com/lumiyaviewer/desktop/ui/DesktopUIManager.java

public class DesktopUIManager {
    // Integration with shared UI components
    private final SharedUIComponents sharedUI;
    
    // Desktop-specific UI components
    private final DesktopWindowManager windowManager;
    private final MenuBarManager menuManager;
    private final ToolbarManager toolbarManager;
    private final DockingSystemManager dockingManager;
    private final MultiMonitorUIManager multiMonitorUI;
    
    // Input handling
    private final DesktopInputHandler inputHandler;
    private final KeyboardShortcutManager shortcutManager;
    private final MouseInteractionManager mouseManager;
    
    // UI optimization
    private final UIScalingManager scalingManager;
    private final ThemeManager themeManager;
    private final AccessibilityManager accessibilityManager;
    
    public DesktopUIManager() {
        this.sharedUI = new SharedUIComponents(PlatformType.DESKTOP);
        
        this.windowManager = new DesktopWindowManager();
        this.menuManager = new MenuBarManager();
        this.toolbarManager = new ToolbarManager();
        this.dockingManager = new DockingSystemManager();
        this.multiMonitorUI = new MultiMonitorUIManager();
        
        this.inputHandler = new DesktopInputHandler();
        this.shortcutManager = new KeyboardShortcutManager();
        this.mouseManager = new MouseInteractionManager();
        
        this.scalingManager = new UIScalingManager();
        this.themeManager = new ThemeManager();
        this.accessibilityManager = new AccessibilityManager();
        
        initializeDesktopUI();
    }
    
    private void initializeDesktopUI() {
        // Set up desktop-specific UI components
        setupDesktopWindowing();
        setupDesktopInput();
        setupDesktopAccessibility();
        
        // Initialize multi-monitor support
        multiMonitorUI.detectAndConfigureMonitors();
        
        // Set up UI scaling for different DPI
        scalingManager.configureScalingForSystem();
        
        Log.i("DesktopUIManager", "Desktop UI system initialized");
    }
    
    private void setupDesktopWindowing() {
        // Main application window
        windowManager.createMainWindow("Linkpoint Desktop", 1920, 1080);
        
        // Secondary windows for advanced features
        windowManager.registerSecondaryWindow("inventory", "Inventory Manager");
        windowManager.registerSecondaryWindow("chat", "Chat Console");
        windowManager.registerSecondaryWindow("minimap", "World Map");
        windowManager.registerSecondaryWindow("settings", "Settings Panel");
        
        // Set up window state persistence
        windowManager.enableWindowStatePersistence();
    }
    
    private void setupDesktopInput() {
        // Register keyboard shortcuts
        shortcutManager.registerShortcut("Ctrl+N", this::openNewWindow);
        shortcutManager.registerShortcut("Ctrl+I", this::toggleInventory);
        shortcutManager.registerShortcut("Ctrl+M", this::toggleMinimap);
        shortcutManager.registerShortcut("Ctrl+T", this::openChatWindow);
        shortcutManager.registerShortcut("F11", this::toggleFullscreen);
        shortcutManager.registerShortcut("Alt+Tab", this::cycleWindows);
        
        // Set up mouse interaction handling
        mouseManager.enableAdvancedMouseFeatures();
        mouseManager.configureMouseSensitivity();
        
        // Enable input handler
        inputHandler.initialize();
    }
    
    // Desktop window management with multi-monitor support
    private class DesktopWindowManager {
        private final Map<String, DesktopWindow> windows;
        private final WindowStateManager stateManager;
        private final WindowLayoutManager layoutManager;
        
        public DesktopWindowManager() {
            this.windows = new HashMap<>();
            this.stateManager = new WindowStateManager();
            this.layoutManager = new WindowLayoutManager();
        }
        
        public DesktopWindow createMainWindow(String title, int width, int height) {
            DesktopWindow mainWindow = new DesktopWindow(title, WindowType.MAIN);
            mainWindow.setSize(width, height);
            mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            
            // Set up main window content
            setupMainWindowContent(mainWindow);
            
            // Center on primary monitor
            mainWindow.centerOnPrimaryMonitor();
            
            // Register window
            windows.put("main", mainWindow);
            
            // Make visible
            mainWindow.setVisible(true);
            
            return mainWindow;
        }
        
        public void registerSecondaryWindow(String windowId, String title) {
            DesktopWindow secondaryWindow = new DesktopWindow(title, WindowType.SECONDARY);
            
            // Set up secondary window properties
            secondaryWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            secondaryWindow.setSize(800, 600);
            
            // Load window state if previously saved
            WindowState savedState = stateManager.loadWindowState(windowId);
            if (savedState != null) {
                secondaryWindow.restoreState(savedState);
            }
            
            // Register window
            windows.put(windowId, secondaryWindow);
            
            Log.d("DesktopWindowManager", "Registered secondary window: " + windowId);
        }
        
        private void setupMainWindowContent(DesktopWindow mainWindow) {
            // Create main content panel
            JPanel mainContent = new JPanel(new BorderLayout());
            
            // Add menu bar
            JMenuBar menuBar = menuManager.createMainMenuBar();
            mainWindow.setJMenuBar(menuBar);
            
            // Add toolbar
            JToolBar toolBar = toolbarManager.createMainToolbar();
            mainContent.add(toolBar, BorderLayout.NORTH);
            
            // Add main 3D viewport
            Desktop3DViewport viewport = new Desktop3DViewport();
            mainContent.add(viewport, BorderLayout.CENTER);
            
            // Add status bar
            JPanel statusBar = createStatusBar();
            mainContent.add(statusBar, BorderLayout.SOUTH);
            
            // Add dockable panels
            setupDockablePanels(mainContent);
            
            mainWindow.setContentPane(mainContent);
        }
        
        private void setupDockablePanels(JPanel mainContent) {
            // Create docking framework
            DockingDesktop dockingDesktop = new DockingDesktop();
            
            // Create dockable panels
            Dockable inventoryPanel = createInventoryPanel();
            Dockable chatPanel = createChatPanel();
            Dockable minimapPanel = createMinimapPanel();
            
            // Add to docking desktop
            dockingDesktop.addDockable("inventory", inventoryPanel);
            dockingDesktop.addDockable("chat", chatPanel);
            dockingDesktop.addDockable("minimap", minimapPanel);
            
            // Set default layout
            layoutManager.applyDefaultLayout(dockingDesktop);
            
            // Add docking desktop to main content
            mainContent.add(dockingDesktop, BorderLayout.CENTER);
        }
        
        public void enableWindowStatePersistence() {
            // Save window states on application exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                for (Map.Entry<String, DesktopWindow> entry : windows.entrySet()) {
                    String windowId = entry.getKey();
                    DesktopWindow window = entry.getValue();
                    
                    WindowState state = window.captureState();
                    stateManager.saveWindowState(windowId, state);
                }
            }));
        }
    }
    
    // Desktop input handler with advanced mouse and keyboard support
    private class DesktopInputHandler {
        private final MouseInputProcessor mouseProcessor;
        private final KeyboardInputProcessor keyboardProcessor;
        private final GamepadInputProcessor gamepadProcessor; // Optional gamepad support
        
        // Input mapping
        private final InputMappingManager mappingManager;
        private final InputContextManager contextManager;
        
        public DesktopInputHandler() {
            this.mouseProcessor = new MouseInputProcessor();
            this.keyboardProcessor = new KeyboardInputProcessor();
            this.gamepadProcessor = new GamepadInputProcessor();
            
            this.mappingManager = new InputMappingManager();
            this.contextManager = new InputContextManager();
        }
        
        public void initialize() {
            // Initialize input processors
            mouseProcessor.initialize();
            keyboardProcessor.initialize();
            gamepadProcessor.initialize();
            
            // Load input mappings
            mappingManager.loadInputMappings();
            
            // Set up input contexts
            setupInputContexts();
            
            Log.i("DesktopInputHandler", "Desktop input system initialized");
        }
        
        private void setupInputContexts() {
            // Different input contexts for different UI states
            contextManager.registerContext("world_view", createWorldViewInputContext());
            contextManager.registerContext("inventory", createInventoryInputContext());
            contextManager.registerContext("chat", createChatInputContext());
            contextManager.registerContext("menu", createMenuInputContext());
            
            // Set default context
            contextManager.setActiveContext("world_view");
        }
        
        private InputContext createWorldViewInputContext() {
            InputContext worldView = new InputContext("world_view");
            
            // Mouse controls for world view
            worldView.addMouseHandler(MouseEvent.BUTTON1, this::handlePrimaryClick);
            worldView.addMouseHandler(MouseEvent.BUTTON2, this::handleMiddleClick);
            worldView.addMouseHandler(MouseEvent.BUTTON3, this::handleSecondaryClick);
            worldView.addMouseMotionHandler(this::handleMouseMove);
            worldView.addMouseWheelHandler(this::handleMouseWheel);
            
            // Keyboard controls for world view
            worldView.addKeyHandler(KeyEvent.VK_W, this::handleMoveForward);
            worldView.addKeyHandler(KeyEvent.VK_S, this::handleMoveBackward);
            worldView.addKeyHandler(KeyEvent.VK_A, this::handleMoveLeft);
            worldView.addKeyHandler(KeyEvent.VK_D, this::handleMoveRight);
            worldView.addKeyHandler(KeyEvent.VK_Q, this::handleMoveUp);
            worldView.addKeyHandler(KeyEvent.VK_E, this::handleMoveDown);
            worldView.addKeyHandler(KeyEvent.VK_SHIFT, this::handleModifierShift);
            worldView.addKeyHandler(KeyEvent.VK_CONTROL, this::handleModifierCtrl);
            
            return worldView;
        }
        
        // Advanced mouse interaction management
        private class MouseInteractionManager {
            private final CameraController cameraController;
            private final ObjectInteractionHandler objectHandler;
            private final SelectionManager selectionManager;
            
            // Mouse state tracking
            private final MouseState mouseState;
            private final DragGestureDetector dragDetector;
            private final ClickGestureDetector clickDetector;
            
            public MouseInteractionManager() {
                this.cameraController = new CameraController();
                this.objectHandler = new ObjectInteractionHandler();
                this.selectionManager = new SelectionManager();
                
                this.mouseState = new MouseState();
                this.dragDetector = new DragGestureDetector();
                this.clickDetector = new ClickGestureDetector();
            }
            
            public void enableAdvancedMouseFeatures() {
                // Enable high-precision mouse input
                enableHighPrecisionMouse();
                
                // Set up gesture detection
                dragDetector.setMinimumDragDistance(5.0f);
                clickDetector.setDoubleClickInterval(300);
                
                // Configure mouse sensitivity
                configureMouseSensitivity();
            }
            
            private void enableHighPrecisionMouse() {
                // Enable raw mouse input for precise camera control
                if (isRawMouseInputSupported()) {
                    enableRawMouseInput();
                    Log.i("MouseInteractionManager", "Raw mouse input enabled");
                }
            }
            
            public void handleMouseInput(MouseEvent event) {
                // Update mouse state
                mouseState.update(event);
                
                // Detect gestures
                GestureType gesture = detectGesture(event);
                
                switch (gesture) {
                    case CAMERA_ROTATE:
                        cameraController.handleRotation(event);
                        break;
                    case CAMERA_PAN:
                        cameraController.handlePanning(event);
                        break;
                    case CAMERA_ZOOM:
                        cameraController.handleZoom(event);
                        break;
                    case OBJECT_SELECT:
                        objectHandler.handleSelection(event);
                        break;
                    case OBJECT_INTERACT:
                        objectHandler.handleInteraction(event);
                        break;
                    case DRAG_SELECT:
                        selectionManager.handleDragSelection(event);
                        break;
                    default:
                        // Handle as standard mouse event
                        handleStandardMouseEvent(event);
                }
            }
            
            private GestureType detectGesture(MouseEvent event) {
                // Determine mouse gesture based on current state and event
                if (dragDetector.isDragging() && mouseState.isRightButtonPressed()) {
                    return GestureType.CAMERA_ROTATE;
                } else if (dragDetector.isDragging() && mouseState.isMiddleButtonPressed()) {
                    return GestureType.CAMERA_PAN;
                } else if (event.getID() == MouseEvent.MOUSE_WHEEL) {
                    return GestureType.CAMERA_ZOOM;
                } else if (clickDetector.isDoubleClick() && mouseState.isLeftButtonPressed()) {
                    return GestureType.OBJECT_INTERACT;
                } else if (clickDetector.isSingleClick() && mouseState.isLeftButtonPressed()) {
                    return GestureType.OBJECT_SELECT;
                } else if (dragDetector.isDragging() && mouseState.isLeftButtonPressed()) {
                    return GestureType.DRAG_SELECT;
                } else {
                    return GestureType.STANDARD;
                }
            }
        }
    }
    
    // Multi-monitor UI support
    private class MultiMonitorUIManager {
        private final List<MonitorConfiguration> monitors;
        private final MonitorDetector monitorDetector;
        private final UIDistributionManager distributionManager;
        
        public MultiMonitorUIManager() {
            this.monitors = new ArrayList<>();
            this.monitorDetector = new MonitorDetector();
            this.distributionManager = new UIDistributionManager();
        }
        
        public void detectAndConfigureMonitors() {
            // Detect all available monitors
            List<GraphicsDevice> devices = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()
            );
            
            monitors.clear();
            for (int i = 0; i < devices.size(); i++) {
                GraphicsDevice device = devices.get(i);
                MonitorConfiguration config = createMonitorConfiguration(device, i);
                monitors.add(config);
            }
            
            Log.i("MultiMonitorUIManager", "Detected " + monitors.size() + " monitors");
            
            // Configure UI distribution if multiple monitors
            if (monitors.size() > 1) {
                configureMutliMonitorLayout();
            }
        }
        
        private void configureMutliMonitorLayout() {
            // Primary monitor: main application window
            MonitorConfiguration primaryMonitor = getPrimaryMonitor();
            distributionManager.assignMainWindowToMonitor(primaryMonitor);
            
            // Secondary monitors: utility windows
            List<MonitorConfiguration> secondaryMonitors = getSecondaryMonitors();
            
            if (secondaryMonitors.size() >= 1) {
                // Second monitor: inventory and chat
                distributionManager.assignWindowsToMonitor(
                    secondaryMonitors.get(0), 
                    Arrays.asList("inventory", "chat")
                );
            }
            
            if (secondaryMonitors.size() >= 2) {
                // Third monitor: minimap and system information
                distributionManager.assignWindowsToMonitor(
                    secondaryMonitors.get(1),
                    Arrays.asList("minimap", "system_info")
                );
            }
            
            Log.i("MultiMonitorUIManager", "Configured multi-monitor layout for " + 
                  monitors.size() + " monitors");
        }
    }
}
```

**Week 51-52: Testing and Integration**

- **Task 3.1.1:** Desktop application architecture testing
- **Task 3.1.2:** Cross-platform synchronization validation
- **Task 3.1.3:** Multi-monitor support testing
- **Task 3.1.4:** Desktop input system validation

**Deliverables for Phase 3.1:**
- ✅ Complete desktop companion application with advanced UI
- ✅ Cross-platform synchronization system (local P2P + cloud backup)
- ✅ Multi-monitor support with intelligent UI distribution
- ✅ Advanced input handling with mouse, keyboard, and gamepad support
- ✅ Desktop rendering engine with post-processing and advanced lighting

---

### Phase 3: Ecosystem Integration (12+ months)
**Focus:** Broader ecosystem participation

1. **Cross-Platform Foundation**
   - Desktop companion app using shared Java codebase
   - Leverage existing mobile optimizations
   - Expand Linkpoint's reach

2. **Developer Tools**
   - Enhanced debugging inspired by all three repositories
   - Performance profiling tools
   - Asset pipeline utilities

3. **Community Integration**
   - Contribute mobile innovations back to ecosystem
   - Participate in viewer standards development
   - Share optimization techniques

---

## Technical Implementation Guidelines

### 1. Maintain Mobile-First Principles

```java
// Always provide mobile-optimized fallbacks
public class FeatureDetector {
    public static boolean supportsAdvancedFeatures(DeviceInfo device) {
        return device.hasOpenGLES30() && 
               device.getTotalRAM() > 3 * 1024 * 1024 * 1024 && // 3GB+
               device.getBatteryLevel() > 0.3f; // 30%+
    }
}

// Adaptive feature enablement
if (FeatureDetector.supportsAdvancedFeatures(deviceInfo)) {
    enablePBRRendering();
    enableAdvancedLighting();
} else {
    // Use existing optimized systems
    useLegacyRenderer();
}
```

### 2. Selective Integration Strategy

**Adopt When:**
- Feature provides clear mobile benefit
- Implementation doesn't compromise performance
- Maintains backward compatibility
- Enhances user experience on mobile

**Avoid When:**
- Feature requires desktop-class resources
- Implementation adds complexity without mobile benefit
- Conflicts with existing mobile optimizations

### 3. Performance Monitoring

```java
public class IntegrationMetrics {
    // Track impact of new features
    private PerformanceMonitor monitor;
    
    public void evaluateFeature(String featureName, Runnable feature) {
        long startTime = System.currentTimeMillis();
        int startBattery = getBatteryLevel();
        
        feature.run();
        
        long endTime = System.currentTimeMillis();
        int endBattery = getBatteryLevel();
        
        // Log impact for evaluation
        monitor.recordFeatureImpact(featureName, 
            endTime - startTime, 
            startBattery - endBattery);
    }
}
```

---

## Java-Specific Implementation Notes

### Comparison with C#/.NET (LibreMetaverse)

**Advantages of Java Implementation:**
- **Cross-platform compatibility** - Android, desktop JVM
- **Memory safety** - automatic garbage collection
- **Strong typing** - compile-time error detection
- **Rich ecosystem** - extensive libraries and tools

**LibreMetaverse Patterns Adaptable to Java:**
```java
// Event-driven architecture
public class EventDrivenProtocol {
    private final EventBus eventBus = EventBusFactory.create();
    
    public <T extends Event> void subscribe(Class<T> eventType, Consumer<T> handler) {
        eventBus.register(eventType, handler);
    }
}

// Async/await equivalent using CompletableFuture
public CompletableFuture<LoginResponse> loginAsync(LoginCredentials credentials) {
    return CompletableFuture
        .supplyAsync(() -> authenticateCredentials(credentials))
        .thenCompose(this::establishConnection)
        .thenApply(this::parseLoginResponse);
}
```

### Comparison with C++ (Firestorm/Second Life)

**Java Advantages:**
- **Memory management** - reduces crashes common in C++
- **Development speed** - faster iteration cycles
- **Platform abstraction** - JVM handles OS differences
- **Modern language features** - lambdas, streams, generics

**C++ Patterns Adaptable to Java:**
```java
// Resource management with try-with-resources
public class GLResourceManager implements AutoCloseable {
    private final List<Integer> textures = new ArrayList<>();
    
    public int createTexture() {
        int texture = GL.glGenTextures();
        textures.add(texture);
        return texture;
    }
    
    @Override
    public void close() {
        textures.forEach(GL::glDeleteTextures);
        textures.clear();
    }
}

// Usage
try (GLResourceManager resources = new GLResourceManager()) {
    int texture = resources.createTexture();
    // Use texture
} // Automatic cleanup
```

---

## Success Metrics and Validation

### Performance Metrics
- **Frame rate improvement** - maintain 60fps on high-end devices, 30fps on low-end
- **Memory usage reduction** - 20% decrease through better asset management
- **Battery life improvement** - 15% increase through protocol optimizations
- **Network efficiency** - 30% reduction in data usage

### Compatibility Metrics
- **Device compatibility** - maintain support for Android 4.4+ (API 19+)
- **OpenGL ES compatibility** - support ES 1.1, 2.0, and 3.0
- **Protocol compatibility** - 100% compatibility with Second Life grid
- **Feature parity** - maintain existing Linkpoint functionality

### User Experience Metrics
- **Visual quality improvement** - enhanced textures and lighting on capable devices
- **Responsiveness** - reduced UI lag through async operations
- **Reliability** - decreased crash rates and network errors
- **Feature richness** - expanded capabilities without complexity

---

## Conclusion

This analysis demonstrates that Linkpoint occupies a unique position in the Second Life ecosystem as the only modern, mobile-first implementation. The recommendations provide a path to selectively integrate the best techniques from LibreMetaverse, Firestorm, and the official Second Life viewer while maintaining Linkpoint's core mobile advantages.

The key to successful integration is maintaining the mobile-first philosophy that makes Linkpoint successful while adding modern features that enhance the experience on capable devices. This approach ensures that Linkpoint continues to serve its core mobile user base while expanding capabilities for the future of mobile computing.

**Next Steps:**
1. Review and prioritize recommendations based on development resources
2. Create detailed technical specifications for Phase 1 implementations
3. Set up monitoring and metrics collection for integration validation
4. Begin with lowest-risk, highest-impact improvements (HTTP/2 CAPS, Basis Universal)
5. Establish feedback loops with the user community for feature validation