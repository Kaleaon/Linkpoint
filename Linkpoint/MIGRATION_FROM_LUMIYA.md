# Migration from Lumiya to Linkpoint

This guide helps you migrate from the existing Lumiya codebase to the modern Linkpoint architecture.

## 🎯 Why Migrate?

### Linkpoint Advantages

| Feature | Lumiya (Java) | Linkpoint (Kotlin) |
|---------|---------------|-------------------|
| Language | Java | Modern Kotlin |
| Voice | Vivox (proprietary) | WebRTC (open source) |
| Graphics | OpenGL ES 2.0 | OpenGL ES 3.2 + PBR |
| Async | Callbacks/Threads | Coroutines |
| Architecture | Legacy | Modern MVVM |
| Material Design | v1 | v3 |
| Dependencies | Outdated | Latest |

## 📋 Migration Checklist

### Phase 1: Setup
- [ ] Create Linkpoint module
- [ ] Configure Gradle dependencies
- [ ] Set up Kotlin configuration
- [ ] Copy assets and resources

### Phase 2: Core Components
- [ ] Migrate authentication
- [ ] Migrate voice system
- [ ] Migrate graphics pipeline
- [ ] Migrate protocol handlers

### Phase 3: Features
- [ ] Migrate inventory system
- [ ] Migrate chat system
- [ ] Migrate avatar system
- [ ] Migrate world rendering

### Phase 4: Testing & Polish
- [ ] Unit tests
- [ ] Integration tests
- [ ] Performance testing
- [ ] User acceptance testing

## 🔄 Component Mapping

### Authentication

**Lumiya (Java):**
```java
// Old authentication approach
public class AuthManager {
    public void login(String first, String last, String pwd, LoginCallback cb) {
        new Thread(() -> {
            try {
                // XML-RPC login
                HttpURLConnection conn = ...
                // Manual XML parsing
            } catch (Exception e) {
                cb.onError(e);
            }
        }).start();
    }
}
```

**Linkpoint (Kotlin):**
```kotlin
// Modern authentication with coroutines
class LinkpointAuthManager {
    suspend fun authenticateWithSecondLife(
        firstName: String,
        lastName: String, 
        password: String,
        callback: AuthCallback
    ): Boolean = withContext(Dispatchers.IO) {
        // Modern OkHttp + LLSD
        val result = sendLLSDRequest(...)
        // Automatic parsing
    }
}
```

### Voice System

**Lumiya (Java):**
```java
// Old Vivox-based voice
public class VoiceManager {
    private VxClientProxy vivoxClient;
    
    public void connectVoice(String uri) {
        // Proprietary Vivox SDK
        vivoxClient.connect(uri);
    }
}
```

**Linkpoint (Kotlin):**
```kotlin
// Modern WebRTC voice
class LinkpointVoiceManager {
    suspend fun connectToVoiceChannel(
        channelUri: String,
        authToken: String
    ): Boolean = withContext(Dispatchers.IO) {
        // Open source WebRTC
        val session = VoiceSession(channelUri, authToken)
        // Modern audio processing
    }
}
```

### Graphics Rendering

**Lumiya (Java):**
```java
// Old fixed-function pipeline
public class Renderer implements GLSurfaceView.Renderer {
    public void onDrawFrame(GL10 gl) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        // Fixed function
    }
}
```

**Linkpoint (Kotlin):**
```kotlin
// Modern shader-based pipeline
class LinkpointRenderPipeline : GLSurfaceView.Renderer {
    override fun onDrawFrame(gl: GL10?) {
        // Modern PBR shaders
        GLES32.glUseProgram(shaderProgram)
        // Physically based rendering
    }
}
```

## 🔧 Step-by-Step Migration

### Step 1: Convert Java to Kotlin

Use Android Studio's automatic converter:

1. Open Java file in Lumiya
2. Code → Convert Java File to Kotlin File
3. Review and fix warnings
4. Test functionality

**Example:**

Java:
```java
public class Avatar {
    private String name;
    private UUID id;
    
    public Avatar(String name, UUID id) {
        this.name = name;
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
}
```

Kotlin:
```kotlin
data class Avatar(
    val name: String,
    val id: UUID
)
```

### Step 2: Migrate to Coroutines

Replace callbacks with coroutines:

**Before (Lumiya):**
```java
public void loadInventory(InventoryCallback callback) {
    new Thread(() -> {
        try {
            List<Item> items = fetchInventory();
            handler.post(() -> callback.onSuccess(items));
        } catch (Exception e) {
            handler.post(() -> callback.onError(e));
        }
    }).start();
}
```

**After (Linkpoint):**
```kotlin
suspend fun loadInventory(): Result<List<Item>> = 
    withContext(Dispatchers.IO) {
        try {
            val items = fetchInventory()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
```

### Step 3: Update Voice to WebRTC

**Migration strategy:**

1. Remove Vivox dependencies
2. Add WebRTC library
3. Create WebRTC manager
4. Update voice UI
5. Test audio quality

**Code changes:**

```kotlin
// Old: Vivox
// dependencies {
//     implementation 'com.vivox:sdk:...'
// }

// New: WebRTC
dependencies {
    implementation 'io.getstream:stream-webrtc-android:1.0.7'
}

// Initialize WebRTC
val audioDeviceModule = JavaAudioDeviceModule.builder(context)
    .setUseHardwareAcousticEchoCanceler(true)
    .createAudioDeviceModule()
```

### Step 4: Upgrade Graphics

**Shader migration:**

1. Replace fixed-function with shaders
2. Implement PBR materials
3. Add modern lighting
4. Update texture loading

**Example PBR implementation:**

```kotlin
// Fragment shader with PBR
private const val FRAGMENT_SHADER = """
    #version 320 es
    precision highp float;
    
    // PBR inputs
    uniform sampler2D uAlbedoMap;
    uniform sampler2D uNormalMap;
    uniform sampler2D uMetallicRoughnessMap;
    
    // Cook-Torrance BRDF
    vec3 cookTorranceBRDF(...) {
        // Modern PBR calculation
    }
"""
```

### Step 5: Update Protocol Handling

**Use modern HTTP client:**

```kotlin
// Old: HttpURLConnection
// val conn = URL(url).openConnection() as HttpURLConnection

// New: OkHttp with HTTP/2
val client = OkHttpClient.Builder()
    .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
    .build()

val request = Request.Builder()
    .url(url)
    .build()

val response = client.newCall(request).execute()
```

## 📊 Feature Comparison

### Voice Features

| Feature | Lumiya | Linkpoint |
|---------|--------|-----------|
| Spatial audio | ✓ (Vivox) | ✓ (WebRTC) |
| Echo cancellation | ✓ | ✓✓ (Hardware + Software) |
| Noise suppression | ✓ | ✓✓ (Hardware + Software) |
| Bluetooth support | ✓ | ✓ |
| Low latency | ~ | ✓✓ |
| Open source | ✗ | ✓ |

### Graphics Features

| Feature | Lumiya | Linkpoint |
|---------|--------|-----------|
| OpenGL version | ES 2.0 | ES 3.2 |
| PBR materials | ✗ | ✓ |
| HDR lighting | ✗ | ✓ |
| Real-time shadows | ✗ | ✓ |
| Post-processing | Limited | Full |
| Mobile optimized | ✓ | ✓✓ |

## 🧪 Testing Migration

### Unit Tests

```kotlin
class MigrationTest {
    @Test
    fun `verify voice manager compatibility`() = runTest {
        // Test new manager with old data
        val legacyChannelUri = "sip:old-format@vivox.com"
        val modernChannelUri = convertToWebRTC(legacyChannelUri)
        
        assertTrue(modernChannelUri.isValid())
    }
    
    @Test
    fun `verify graphics pipeline compatibility`() {
        // Test shader compilation
        val shader = compilePBRShader()
        assertTrue(shader != 0)
    }
}
```

### Integration Tests

```kotlin
@Test
fun `end to end voice connection`() = runTest {
    val voiceManager = LinkpointVoiceManager(context, callback)
    
    // Initialize
    val initialized = voiceManager.initialize()
    assertTrue(initialized)
    
    // Connect
    val connected = voiceManager.connectToVoiceChannel(uri, token)
    assertTrue(connected)
    
    // Verify
    assertTrue(voiceManager.isConnected())
    
    // Cleanup
    voiceManager.cleanup()
}
```

## ⚠️ Common Issues

### Issue 1: Voice Quality

**Problem:** Voice quality worse than Lumiya

**Solution:**
```kotlin
// Enable hardware audio processing
audioDeviceModule = JavaAudioDeviceModule.builder(context)
    .setUseHardwareAcousticEchoCanceler(true)
    .setUseHardwareNoiseSuppressor(true)
    .createAudioDeviceModule()

// Use optimal audio constraints
audioConstraints.mandatory.add(
    MediaConstraints.KeyValuePair("googAutoGainControl", "true")
)
```

### Issue 2: Graphics Performance

**Problem:** Frame rate lower than Lumiya

**Solution:**
```kotlin
// Optimize shader compilation
val shader = compileShader(type, source)
GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, status, 0)

// Use VAOs for better performance
GLES32.glBindVertexArray(vao)

// Batch rendering
renderBatch(objects)
```

### Issue 3: Memory Usage

**Problem:** Higher memory consumption

**Solution:**
```kotlin
// Implement object pooling
val avatarPool = object : Pool<Avatar>() {
    override fun create() = Avatar()
}

// Use weak references for caches
val textureCache = WeakHashMap<String, Texture>()

// Cleanup unused resources
fun cleanup() {
    textureCache.clear()
    avatarPool.clear()
}
```

## 📈 Performance Optimization

### Before Migration
```
Lumiya Stats:
- Startup time: 3.5s
- Voice latency: 150ms
- Frame rate: 30 FPS
- Memory: 250 MB
```

### After Migration (Target)
```
Linkpoint Stats:
- Startup time: 2.0s  (↓43%)
- Voice latency: 80ms  (↓47%)
- Frame rate: 60 FPS  (↑100%)
- Memory: 200 MB      (↓20%)
```

## 🎓 Learning Resources

### Kotlin
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Kotlin for Java Developers](https://www.coursera.org/learn/kotlin-for-java-developers)

### Coroutines
- [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android Coroutines](https://developer.android.com/kotlin/coroutines)

### WebRTC
- [WebRTC for Android](https://webrtc.org/native-code/android/)
- [Stream WebRTC Docs](https://getstream.io/video/docs/android/)

### Modern Android
- [Android Jetpack](https://developer.android.com/jetpack)
- [Material Design 3](https://m3.material.io/)

## 🤝 Getting Help

- **GitHub Issues**: Report bugs and request features
- **Discord**: Join Linkpoint community
- **Documentation**: Check integration guide
- **Stack Overflow**: Tag `linkpoint` and `second-life`

---

**Migration Timeline Estimate:**

- Small project (1-2 features): 1-2 weeks
- Medium project (core features): 1-2 months
- Full migration: 3-6 months

Take it step by step, and don't hesitate to ask for help!