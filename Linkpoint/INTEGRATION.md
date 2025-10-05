# Linkpoint Integration Guide

This document explains how to integrate Linkpoint into an existing Android project or use it as a standalone application.

## 🏗️ Architecture Overview

Linkpoint uses a modern, modular architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         LinkpointApplication            │
│      (Application Singleton)            │
└─────────────────┬───────────────────────┘
                  │
        ┌─────────┼─────────┐
        │         │         │
┌───────▼────┐ ┌──▼──────┐ ┌▼────────────┐
│    Auth    │ │  Voice  │ │  Protocol   │
│  Manager   │ │ Manager │ │   Manager   │
└────────────┘ └─────────┘ └─────────────┘
        │         │         │
        └─────────┼─────────┘
                  │
        ┌─────────▼─────────┐
        │    UI Activities   │
        │  - Main Activity   │
        │  - World Activity  │
        │  - Settings        │
        └────────────────────┘
```

## 🔧 Component Integration

### 1. Authentication Manager

```kotlin
// Initialize
val authManager = LinkpointAuthManager()

// Authenticate
scope.launch {
    val success = authManager.authenticateWithSecondLife(
        firstName = "John",
        lastName = "Doe",
        password = "password",
        callback = object : LinkpointAuthManager.AuthCallback {
            override fun onAuthSuccess(agentId: String, sessionId: String) {
                // Handle success
            }
            override fun onAuthFailure(error: String) {
                // Handle failure
            }
            override fun onAuthProgress(message: String) {
                // Show progress
            }
        }
    )
}

// Cleanup
authManager.cleanup()
```

### 2. Voice Manager

```kotlin
// Initialize
val voiceManager = LinkpointVoiceManager(
    context = applicationContext,
    callback = voiceCallback
)

// Setup voice
scope.launch {
    // Initialize WebRTC
    val initialized = voiceManager.initialize()
    
    if (initialized) {
        // Connect to channel
        val connected = voiceManager.connectToVoiceChannel(
            channelUri = "sip:channel@voice.secondlife.com",
            authToken = "your-auth-token"
        )
        
        if (connected) {
            // Voice is ready
        }
    }
}

// Mute/unmute
voiceManager.setMicrophoneMuted(true)

// Adjust volume
voiceManager.setSpeakerVolume(0.8f)
voiceManager.setMicrophoneVolume(1.0f)

// Disconnect
scope.launch {
    voiceManager.leaveVoiceChannel(channelUri)
}

// Cleanup
voiceManager.cleanup()
```

### 3. Graphics Pipeline

```kotlin
// Create GLSurfaceView
val glSurfaceView = GLSurfaceView(context).apply {
    setEGLContextClientVersion(3) // OpenGL ES 3.2
    
    val pipeline = LinkpointRenderPipeline(context)
    setRenderer(pipeline)
    
    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
}

// Add to layout
rootLayout.addView(glSurfaceView)
```

### 4. Protocol Manager

```kotlin
// Initialize
val protocolManager = LinkpointProtocolManager()

// Send LLSD request
scope.launch {
    val llsdData = protocolManager.createLLSDMap(mapOf(
        "agent_id" to agentId,
        "session_id" to sessionId,
        "method" to "GetInventory"
    ))
    
    val result = protocolManager.sendLLSDRequest(
        url = "https://cap.secondlife.com/cap/abc123",
        llsdData = llsdData
    )
    
    result.onSuccess { response ->
        // Handle success
    }.onFailure { error ->
        // Handle error
    }
}

// Register capability
protocolManager.registerCapability(
    capName = "FetchInventory2",
    capUrl = "https://cap.secondlife.com/cap/xyz789"
)

// Invoke capability
scope.launch {
    val result = protocolManager.invokeCapability(
        capName = "FetchInventory2",
        llsdData = llsdData
    )
}

// Cleanup
protocolManager.cleanup()
```

## 📱 Using as Standalone App

### 1. Build and Install

```bash
cd Linkpoint
./gradlew assembleDebug
adb install -r build/outputs/apk/debug/Linkpoint-debug.apk
```

### 2. Run on Emulator

```bash
./gradlew installDebug
adb shell am start -n com.linkpoint/.ui.LinkpointMainActivity
```

### 3. Run Tests

```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest
```

## 🔗 Integrating into Existing Project

### Method 1: As Library Module

1. Copy Linkpoint folder to your project
2. Add to `settings.gradle`:
```gradle
include ':app', ':Linkpoint'
```

3. Add dependency in `app/build.gradle`:
```gradle
dependencies {
    implementation project(':Linkpoint')
}
```

### Method 2: As AAR Library

1. Build AAR:
```bash
cd Linkpoint
./gradlew assembleRelease
```

2. Copy AAR from `build/outputs/aar/`

3. Add to your project:
```gradle
dependencies {
    implementation files('libs/Linkpoint-release.aar')
}
```

## 🎨 Customization

### Branding

Edit `res/values/strings.xml`:
```xml
<string name="app_name">Your App Name</string>
<string name="app_subtitle">Your Subtitle</string>
```

Edit `res/values/colors.xml`:
```xml
<color name="linkpoint_primary">#YourColor</color>
```

### Voice Configuration

Modify in `LinkpointVoiceManager.kt`:
```kotlin
// Audio constraints
audioConstraints.mandatory.add(
    MediaConstraints.KeyValuePair("googNoiseSuppression", "true")
)
```

### Graphics Quality

Adjust in `LinkpointRenderPipeline.kt`:
```kotlin
// Shader quality
private const val FRAGMENT_SHADER = """
    #version 320 es
    precision mediump float; // Change to highp for better quality
    ...
"""
```

## 🔐 Security Best Practices

### 1. Credential Storage

Use Android Keystore for sensitive data:
```kotlin
// Example using EncryptedSharedPreferences
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedPrefs = EncryptedSharedPreferences.create(
    context,
    "linkpoint_secure_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

### 2. Network Security

Enable certificate pinning:
```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("*.secondlife.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .build()

val client = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

## 🧪 Testing Integration

### Unit Test Example

```kotlin
class LinkpointAuthTest {
    @Test
    fun testAuthentication() = runTest {
        val authManager = LinkpointAuthManager()
        
        val callback = mock<LinkpointAuthManager.AuthCallback>()
        
        val result = authManager.authenticateWithSecondLife(
            firstName = "Test",
            lastName = "User",
            password = "test",
            callback = callback
        )
        
        // Assertions
        verify(callback).onAuthProgress(any())
    }
}
```

### Integration Test Example

```kotlin
@RunWith(AndroidJUnit4::class)
class LinkpointIntegrationTest {
    @Test
    fun testVoiceInitialization() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val callback = mock<LinkpointVoiceManager.VoiceCallback>()
        
        val voiceManager = LinkpointVoiceManager(context, callback)
        val initialized = voiceManager.initialize()
        
        assertTrue(initialized)
        voiceManager.cleanup()
    }
}
```

## 📊 Performance Monitoring

### Add Performance Tracking

```kotlin
class PerformanceMonitor {
    fun measureTime(block: String, action: () -> Unit) {
        val start = System.nanoTime()
        action()
        val duration = (System.nanoTime() - start) / 1_000_000
        Log.d("Performance", "$block took ${duration}ms")
    }
}

// Usage
performanceMonitor.measureTime("Authentication") {
    authManager.authenticate(...)
}
```

## 🐛 Debugging

### Enable Debug Logging

Add to `LinkpointApplication.kt`:
```kotlin
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
}
```

### Network Debugging

Enable OkHttp logging:
```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
```

## 📚 Additional Resources

- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [WebRTC Documentation](https://webrtc.org/getting-started/overview)
- [Second Life Protocol Documentation](https://wiki.secondlife.com/wiki/Protocol)

## 🆘 Troubleshooting

### Voice Not Working
- Check microphone permissions
- Verify WebRTC initialization
- Test on physical device (emulator has limited audio)

### Authentication Fails
- Verify credentials
- Check network connectivity
- Ensure HTTPS is working
- Check firewall settings

### Graphics Issues
- Verify OpenGL ES 3.2 support
- Check GPU compatibility
- Test on different devices

## 💡 Tips

1. **Use Coroutines**: All async operations use coroutines
2. **Handle Lifecycle**: Cleanup managers in onDestroy()
3. **Test on Device**: Voice and graphics work best on real devices
4. **Monitor Memory**: Use LeakCanary for memory leak detection
5. **Profile Performance**: Use Android Profiler

---

For more help, see the [main README](README.md) or open an issue on GitHub.