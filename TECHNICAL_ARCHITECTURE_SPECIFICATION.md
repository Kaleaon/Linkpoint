# 🏗️ LINKPOINT TECHNICAL ARCHITECTURE SPECIFICATION

## 📐 System Architecture Overview

This document provides detailed technical specifications for the Linkpoint (Lumiya Second Life Viewer) Android application architecture, focusing on implementation details, data flows, and technical design decisions.

---

## 🎯 Architecture Philosophy

### Core Architectural Principles

#### **1. Layered Architecture Pattern**
The application follows a strict layered architecture to ensure separation of concerns, testability, and maintainability:

```
┌─────────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                     │
│  Activities, Fragments, Views, UI Controllers          │
├─────────────────────────────────────────────────────────┤
│                  APPLICATION LAYER                      │
│  Business Logic, Use Cases, Application Services       │
├─────────────────────────────────────────────────────────┤
│                   DOMAIN LAYER                         │
│  Entities, Domain Services, Business Rules             │
├─────────────────────────────────────────────────────────┤
│                INFRASTRUCTURE LAYER                     │
│  Network, Database, File System, External APIs         │
├─────────────────────────────────────────────────────────┤
│                   NATIVE LAYER                         │
│  JNI, C++ Libraries, Hardware Abstraction             │
└─────────────────────────────────────────────────────────┘
```

#### **2. Event-Driven Architecture**
Central event bus system for loose coupling between components:

```java
// Event Bus Implementation
public class EventBus {
    private final Map<Class<?>, List<EventHandler<?>>> handlers = new ConcurrentHashMap<>();
    private final ExecutorService eventExecutor = Executors.newCachedThreadPool();
    
    public <T> void publish(T event) {
        List<EventHandler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            eventHandlers.forEach(handler -> 
                eventExecutor.submit(() -> ((EventHandler<T>) handler).handle(event))
            );
        }
    }
    
    public <T> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
}
```

#### **3. Dependency Injection Pattern**
Clean dependency management without heavyweight frameworks:

```java
// Service Locator Pattern for DI
public class ServiceRegistry {
    private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();
    
    public static <T> void register(Class<T> serviceClass, T implementation) {
        services.put(serviceClass, implementation);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> serviceClass) {
        return (T) services.get(serviceClass);
    }
}
```

---

## 🔌 Component Architecture

### 1. Protocol Layer Architecture

#### **1.1 Circuit Management System**

```java
/**
 * Second Life UDP Circuit Implementation
 * Handles reliable UDP communication with SL servers
 */
public class SLAgentCircuit {
    // Connection State Management
    private volatile CircuitState state = CircuitState.DISCONNECTED;
    private final AtomicInteger sequenceNumber = new AtomicInteger(0);
    private final Map<Integer, PendingMessage> pendingAcks = new ConcurrentHashMap<>();
    
    // Message Processing Pipeline
    private final BlockingQueue<SLMessage> incomingMessages = new LinkedBlockingQueue<>();
    private final BlockingQueue<SLMessage> outgoingMessages = new LinkedBlockingQueue<>();
    
    // Threading Model
    private final ExecutorService networkExecutor = Executors.newFixedThreadPool(3);
    private final ScheduledExecutorService keepaliveExecutor = 
        Executors.newSingleThreadScheduledExecutor();
    
    /**
     * Message Processing Flow:
     * 1. Raw UDP packets received on network thread
     * 2. Deserialized and queued for processing
     * 3. Processed on background thread
     * 4. Events published to application layer
     */
    public void processIncomingMessage(byte[] packetData) {
        networkExecutor.submit(() -> {
            SLMessage message = MessageParser.parse(packetData);
            if (message.requiresAck()) {
                sendAcknowledgment(message.getSequenceNumber());
            }
            incomingMessages.offer(message);
            eventBus.publish(new MessageReceivedEvent(message));
        });
    }
}
```

#### **1.2 Message System Architecture**

**Message Type Hierarchy:**
```java
// Base Message Class
public abstract class SLMessage {
    protected final MessageType type;
    protected final int sequenceNumber;
    protected final boolean reliable;
    
    public abstract void serialize(ByteBuffer buffer);
    public abstract void deserialize(ByteBuffer buffer);
}

// Specific Message Implementation
public class AgentUpdateMessage extends SLMessage {
    private LLVector3 position;
    private LLQuaternion rotation;
    private int controlFlags;
    private byte[] agentData;
    
    @Override
    public void serialize(ByteBuffer buffer) {
        buffer.putInt(type.getValue());
        position.serialize(buffer);
        rotation.serialize(buffer);
        buffer.putInt(controlFlags);
        buffer.put(agentData);
    }
}
```

**Message Processing Pipeline:**
```
Raw UDP Packet → Packet Parser → Message Router → Handler Registry → Business Logic
                      ↓
                 Acknowledgment → Reliability Layer → Network Transmission
```

### 2. Graphics Engine Architecture

#### **2.1 Rendering Pipeline**

```java
/**
 * Multi-threaded OpenGL ES Rendering Architecture
 */
public class RenderEngine {
    // OpenGL Context Management
    private EGLDisplay display;
    private EGLContext renderContext;
    private EGLSurface surface;
    
    // Rendering Threads
    private final HandlerThread renderThread = new HandlerThread("RenderThread");
    private final Handler renderHandler;
    
    // Resource Management
    private final GPUResourceManager resourceManager;
    private final ShaderProgramManager shaderManager;
    private final TextureManager textureManager;
    
    /**
     * Rendering Pipeline Flow:
     * 1. Scene Update (Main Thread)
     * 2. Culling & LOD Selection (Background Thread)
     * 3. Command Generation (Background Thread)  
     * 4. GPU Command Submission (Render Thread)
     * 5. Present Frame (Render Thread)
     */
    public void renderFrame(Scene scene, Camera camera) {
        renderHandler.post(() -> {
            // Make OpenGL context current
            EGL14.eglMakeCurrent(display, surface, surface, renderContext);
            
            // Clear frame buffer
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
            
            // Render scene components
            renderSkybox(scene.getSkybox(), camera);
            renderTerrain(scene.getTerrain(), camera);
            renderObjects(scene.getVisibleObjects(), camera);
            renderAvatars(scene.getAvatars(), camera);
            renderEffects(scene.getParticleEffects(), camera);
            renderUI(scene.getHUDElements(), camera);
            
            // Present frame
            EGL14.eglSwapBuffers(display, surface);
            
            // Performance monitoring
            frameProfiler.endFrame();
        });
    }
}
```

#### **2.2 Shader Management System**

```java
/**
 * Shader Program Management with Hot Reloading
 */
public class ShaderProgramManager {
    private final Map<String, ShaderProgram> loadedShaders = new HashMap<>();
    private final Map<String, Long> shaderTimestamps = new HashMap<>();
    
    public class ShaderProgram {
        private final int programId;
        private final Map<String, Integer> uniformLocations = new HashMap<>();
        private final Map<String, Integer> attributeLocations = new HashMap<>();
        
        public void setUniform(String name, float value) {
            int location = getUniformLocation(name);
            GLES30.glUniform1f(location, value);
        }
        
        public void setUniform(String name, LLVector3 value) {
            int location = getUniformLocation(name);
            GLES30.glUniform3f(location, value.x, value.y, value.z);
        }
        
        private int getUniformLocation(String name) {
            return uniformLocations.computeIfAbsent(name, 
                n -> GLES30.glGetUniformLocation(programId, n));
        }
    }
}
```

### 3. Resource Management Architecture

#### **3.1 Multi-Tier Caching System**

```java
/**
 * Hierarchical Resource Caching
 * L1: GPU Memory (VRAM)
 * L2: System Memory (RAM) 
 * L3: Internal Storage
 * L4: External Network
 */
public class ResourceCache<T> {
    // Cache Levels
    private final LRUCache<String, T> l1Cache; // GPU/Memory
    private final LRUCache<String, WeakReference<T>> l2Cache; // Weak refs
    private final DiskLruCache l3Cache; // Persistent storage
    
    // Cache Configuration
    private final int l1MaxSize;
    private final int l2MaxSize;
    private final long l3MaxSize;
    
    public T get(String key) {
        // Try L1 cache first (fastest)
        T resource = l1Cache.get(key);
        if (resource != null) {
            cacheHitStats.recordL1Hit();
            return resource;
        }
        
        // Try L2 cache (memory)
        WeakReference<T> weakRef = l2Cache.get(key);
        if (weakRef != null) {
            resource = weakRef.get();
            if (resource != null) {
                cacheHitStats.recordL2Hit();
                // Promote to L1
                l1Cache.put(key, resource);
                return resource;
            }
        }
        
        // Try L3 cache (disk)
        resource = loadFromDisk(key);
        if (resource != null) {
            cacheHitStats.recordL3Hit();
            // Promote to higher levels
            l2Cache.put(key, new WeakReference<>(resource));
            l1Cache.put(key, resource);
            return resource;
        }
        
        // Load from network (L4)
        return loadFromNetwork(key);
    }
}
```

#### **3.2 Asset Pipeline Architecture**

```java
/**
 * Asset Processing Pipeline
 * Raw Asset → Format Detection → Processing → Optimization → Caching → GPU Upload
 */
public class AssetPipeline {
    private final ExecutorService processingExecutor = 
        Executors.newFixedThreadPool(4, 
            new ThreadPoolExecutor.CallerRunsPolicy());
    
    public CompletableFuture<ProcessedAsset> processAsset(RawAsset asset) {
        return CompletableFuture.supplyAsync(() -> {
            // Format Detection
            AssetFormat format = detectFormat(asset);
            
            // Processing Chain
            ProcessedAsset processed = switch(format) {
                case JPEG2000 -> processJPEG2000(asset);
                case BASIS_UNIVERSAL -> processBasisUniversal(asset);
                case GLTF -> processGLTF(asset);
                case WAV -> processAudio(asset);
                default -> throw new UnsupportedAssetException(format);
            };
            
            // Mobile Optimization
            if (processed.requiresOptimization()) {
                processed = optimizeForMobile(processed);
            }
            
            // Cache the result
            assetCache.put(asset.getId(), processed);
            
            return processed;
        }, processingExecutor);
    }
}
```

### 4. Voice System Architecture

#### **4.1 WebRTC Integration**

```java
/**
 * WebRTC Voice System replacing Vivox
 */
public class WebRTCVoiceManager {
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private AudioDeviceModule audioDeviceModule;
    
    // Audio Configuration
    private final AudioProcessingSettings audioSettings = AudioProcessingSettings.builder()
        .setEchoCancellation(true)
        .setNoiseSuppression(true)
        .setAutoGainControl(true)
        .setHighpassFilter(true)
        .build();
    
    public void initializeWebRTC() {
        // Initialize PeerConnection Factory
        PeerConnectionFactory.InitializationOptions initOptions =
            PeerConnectionFactory.InitializationOptions.builder()
                .setFieldTrials("")
                .setEnableInternalTracer(false)
                .createInitializationOptions();
        PeerConnectionFactory.initialize(initOptions);
        
        // Create factory with audio processing
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setAudioDeviceModule(createAudioDeviceModule())
            .setAudioProcessingFactory(createAudioProcessingFactory())
            .createPeerConnectionFactory();
    }
    
    /**
     * Second Life Voice Integration
     * SL Voice Credentials → WebRTC Signaling → Peer Connection → Audio Stream
     */
    public void connectToSLVoiceChannel(SLVoiceCredentials credentials) {
        // Process SL voice server authentication
        VoiceAuthToken token = authenticateWithSLVoiceServer(credentials);
        
        // Create WebRTC peer connection
        PeerConnection.RTCConfiguration config = new PeerConnection.RTCConfiguration(
            Arrays.asList(
                PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
                PeerConnection.IceServer.builder("turn:" + token.getTurnServer())
                    .setUsername(token.getUsername())
                    .setPassword(token.getPassword())
                    .createIceServer()
            )
        );
        
        peerConnection = peerConnectionFactory.createPeerConnection(config, pcObserver);
        
        // Add local audio stream
        AudioSource audioSource = peerConnectionFactory.createAudioSource(audioSettings);
        AudioTrack localAudioTrack = peerConnectionFactory.createAudioTrack("audio", audioSource);
        peerConnection.addTrack(localAudioTrack, Arrays.asList("voice_channel"));
    }
}
```

---

## 🔄 Data Flow Architecture

### Message Flow Diagrams

#### **1. Login Authentication Flow**
```
User Input → LoginActivity → AuthenticationService → SL Grid Server
                ↓                      ↓                    ↓
            UI Update ← EventBus ← LoginResponse ← XML-RPC Response
                ↓
        WorldActivity Launch
```

#### **2. Chat Message Flow**
```
User Types → ChatInput → MessageBuilder → SLAgentCircuit → UDP Transmission
                                                ↓
Remote Message → UDP Reception → MessageParser → EventBus → ChatFragment
                                                    ↓
                                              Database Storage
```

#### **3. Asset Loading Flow**
```
Asset Request → AssetCache Check → Network Request → Asset Processing → GPU Upload
      ↓               ↓               ↓                    ↓              ↓
  Cache Hit → Return Asset    Cache Miss → Download → Optimize → Display
```

### State Management

#### **Application State Architecture**
```java
/**
 * Centralized Application State Management
 */
public class ApplicationState {
    // Connection State
    private volatile ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private volatile GridInfo currentGrid;
    private volatile AgentInfo agentInfo;
    
    // World State
    private final ConcurrentHashMap<UUID, Avatar> nearbyAvatars = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Prim> nearbyObjects = new ConcurrentHashMap<>();
    private volatile RegionInfo currentRegion;
    
    // UI State
    private volatile String currentChatChannel = "Local";
    private volatile boolean voiceEnabled = false;
    private volatile CameraMode cameraMode = CameraMode.THIRD_PERSON;
    
    // State Change Events
    public void setConnectionState(ConnectionState newState) {
        ConnectionState oldState = this.connectionState;
        this.connectionState = newState;
        eventBus.publish(new ConnectionStateChangedEvent(oldState, newState));
    }
}
```

---

## 🔧 Database Schema

### SQLite Database Design

```sql
-- Core application tables
CREATE TABLE user_accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    grid_uri TEXT NOT NULL,
    last_login_time INTEGER,
    auto_login BOOLEAN DEFAULT FALSE,
    encrypted_password BLOB
);

-- Chat history storage
CREATE TABLE chat_messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id TEXT NOT NULL,
    sender_uuid TEXT,
    sender_name TEXT,
    message_text TEXT NOT NULL,
    message_type INTEGER, -- 0=local, 1=IM, 2=group
    timestamp INTEGER NOT NULL,
    region_name TEXT,
    position_x REAL,
    position_y REAL,
    position_z REAL,
    INDEX idx_chat_timestamp (timestamp),
    INDEX idx_chat_session (session_id)
);

-- Asset cache metadata
CREATE TABLE asset_cache (
    asset_uuid TEXT PRIMARY KEY,
    asset_type INTEGER NOT NULL, -- texture, sound, animation, etc
    file_path TEXT NOT NULL,
    file_size INTEGER NOT NULL,
    last_accessed INTEGER NOT NULL,
    creation_time INTEGER NOT NULL,
    content_hash TEXT,
    compression_format INTEGER,
    INDEX idx_cache_accessed (last_accessed),
    INDEX idx_cache_type (asset_type)
);

-- Friends and contacts
CREATE TABLE contacts (
    contact_uuid TEXT PRIMARY KEY,
    display_name TEXT,
    username TEXT,
    online_status INTEGER DEFAULT 0,
    last_seen INTEGER,
    relationship_type INTEGER, -- friend, blocked, etc
    notes TEXT,
    INDEX idx_contacts_status (online_status)
);

-- Landmark and location data
CREATE TABLE landmarks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    region_name TEXT NOT NULL,
    position_x REAL NOT NULL,
    position_y REAL NOT NULL, 
    position_z REAL NOT NULL,
    grid_uri TEXT NOT NULL,
    creation_time INTEGER NOT NULL,
    category TEXT,
    INDEX idx_landmarks_region (region_name),
    INDEX idx_landmarks_category (category)
);

-- Application preferences
CREATE TABLE preferences (
    key TEXT PRIMARY KEY,
    value TEXT,
    data_type INTEGER -- 0=string, 1=integer, 2=boolean, 3=float
);
```

---

## 📊 Performance Monitoring

### Metrics Collection Architecture

```java
/**
 * Performance Monitoring and Analytics
 */
public class PerformanceMonitor {
    private final MetricsCollector metricsCollector;
    private final ScheduledExecutorService metricsExecutor;
    
    // Performance Metrics
    public static class Metrics {
        // Rendering Performance
        public final AtomicLong frameCount = new AtomicLong(0);
        public final AtomicLong frameTimes = new AtomicLong(0); // nanoseconds
        public final AtomicInteger droppedFrames = new AtomicInteger(0);
        
        // Memory Usage
        public volatile long javaHeapUsed;
        public volatile long nativeHeapUsed;
        public volatile long gpuMemoryUsed;
        
        // Network Performance
        public final AtomicLong bytesReceived = new AtomicLong(0);
        public final AtomicLong bytesSent = new AtomicLong(0);
        public final AtomicLong messagesReceived = new AtomicLong(0);
        public final AtomicLong messagesSent = new AtomicLong(0);
        
        // Battery Usage
        public volatile int batteryLevel;
        public volatile boolean chargingState;
        public volatile long batteryDrainRate; // mAh per hour
    }
    
    public void startMonitoring() {
        metricsExecutor.scheduleAtFixedRate(() -> {
            collectRenderingMetrics();
            collectMemoryMetrics();
            collectNetworkMetrics();
            collectBatteryMetrics();
            
            // Report to analytics (if enabled)
            if (analyticsEnabled) {
                reportMetrics();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
```

### Error Handling & Recovery

```java
/**
 * Centralized Error Handling System
 */
public class ErrorHandler {
    private final Map<Class<? extends Exception>, ErrorRecoveryStrategy> recoveryStrategies;
    
    public void handleError(Exception error, Context context) {
        // Log error with context
        errorLogger.log(error, context);
        
        // Determine recovery strategy
        ErrorRecoveryStrategy strategy = recoveryStrategies.get(error.getClass());
        if (strategy != null) {
            strategy.recover(error, context);
        } else {
            // Default recovery
            defaultRecoveryStrategy.recover(error, context);
        }
        
        // Notify user if necessary
        if (shouldNotifyUser(error)) {
            showUserNotification(error);
        }
    }
    
    // Specific recovery strategies
    public static class NetworkErrorRecovery implements ErrorRecoveryStrategy {
        @Override
        public void recover(Exception error, Context context) {
            // Exponential backoff retry
            scheduleRetry(calculateBackoffDelay());
            // Switch to cached content if available
            enableOfflineMode();
        }
    }
}
```

---

## 🔐 Security Architecture

### Authentication & Security

```java
/**
 * Secure Credential Management
 */
public class SecureCredentialManager {
    private final KeyStore androidKeyStore;
    private final SecretKey encryptionKey;
    
    public void storeCredentials(String username, String password) {
        try {
            // Generate encryption key in Android Keystore
            KeyGenerator keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(
                username + "_key",
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(300)
                .build();
            keyGen.init(keySpec);
            SecretKey key = keyGen.generateKey();
            
            // Encrypt password
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedPassword = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            
            // Store encrypted password
            SharedPreferences prefs = getSecurePreferences();
            prefs.edit()
                .putString(username + "_encrypted", Base64.encodeToString(encryptedPassword, Base64.NO_WRAP))
                .putString(username + "_iv", Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP))
                .apply();
                
        } catch (Exception e) {
            throw new SecurityException("Failed to store credentials securely", e);
        }
    }
}
```

---

## 🧪 Testing Architecture

### Testing Strategy Implementation

```java
/**
 * Comprehensive Testing Framework
 */
public class TestingFramework {
    
    // Unit Testing
    @Test
    public void testMessageSerialization() {
        AgentUpdateMessage message = new AgentUpdateMessage();
        message.setPosition(new LLVector3(128, 128, 25));
        message.setRotation(new LLQuaternion(0, 0, 0, 1));
        
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        message.serialize(buffer);
        
        buffer.flip();
        AgentUpdateMessage deserialized = new AgentUpdateMessage();
        deserialized.deserialize(buffer);
        
        assertEquals(message.getPosition(), deserialized.getPosition());
        assertEquals(message.getRotation(), deserialized.getRotation());
    }
    
    // Integration Testing
    @Test
    public void testSLServerCommunication() {
        MockSLServer mockServer = new MockSLServer();
        mockServer.start();
        
        SLAgentCircuit circuit = new SLAgentCircuit();
        circuit.connect("127.0.0.1", mockServer.getPort());
        
        // Test login sequence
        LoginRequest loginRequest = new LoginRequest("test", "password");
        circuit.sendMessage(loginRequest);
        
        // Verify response
        LoginResponse response = circuit.waitForMessage(LoginResponse.class, 5000);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        
        mockServer.stop();
    }
    
    // Performance Testing
    @Test
    public void testRenderingPerformance() {
        RenderEngine engine = new RenderEngine();
        Scene testScene = createTestScene(1000); // 1000 objects
        Camera camera = new Camera();
        
        long startTime = System.nanoTime();
        for (int i = 0; i < 60; i++) { // 60 frames
            engine.renderFrame(testScene, camera);
        }
        long totalTime = System.nanoTime() - startTime;
        
        double avgFrameTime = totalTime / 60.0 / 1_000_000.0; // milliseconds
        assertTrue("Frame time too high: " + avgFrameTime + "ms", 
                   avgFrameTime < 16.67); // 60 FPS target
    }
}
```

---

## 📈 Scalability Considerations

### Future Architecture Extensions

#### **1. Microservices Preparation**
```java
// Service Interface for future microservices migration
public interface AssetService {
    CompletableFuture<Asset> getAsset(UUID assetId);
    CompletableFuture<Void> uploadAsset(Asset asset);
    CompletableFuture<List<Asset>> searchAssets(AssetSearchCriteria criteria);
}

// Current monolithic implementation
public class LocalAssetService implements AssetService {
    // Implementation using local resources
}

// Future cloud service implementation
public class CloudAssetService implements AssetService {
    // Implementation using REST APIs
}
```

#### **2. Plugin Architecture**
```java
// Plugin interface for extensibility
public interface LumiyaPlugin {
    String getName();
    String getVersion();
    void initialize(PluginContext context);
    void shutdown();
    
    // Optional UI contribution
    default List<MenuItem> contributeMenuItems() { return Collections.emptyList(); }
    default List<Fragment> contributeFragments() { return Collections.emptyList(); }
}
```

---

*This Technical Architecture Specification provides detailed implementation guidance for the Linkpoint application's technical foundation. It complements the main Comprehensive App Design Document with specific architectural decisions, design patterns, and implementation details.*

**Document Version**: 1.0  
**Last Updated**: December 2024  
**Next Review**: March 2025