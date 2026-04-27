# Linkpoint Reconstruction Plan: From Decompilation to Working Client

## Current Situation Analysis

You have successfully **decompiled the Lumiya APK** and now need to reconstruct a working Android Second Life client using freely available open source components. Here's what we have and what's missing:

### ✅ What We Have (Decompiled Assets)

**Java Source Code (1,300+ files):**
- Complete decompiled Lumiya viewer Java codebase
- Second Life protocol implementation (`slproto` package)
- 3D rendering system (`render` package) 
- UI system with mobile optimizations
- Resource management and caching systems
- Authentication and networking code

**Partial Native Components:**
- Basis Universal transcoder implementation
- Stub libraries for missing components
- CMake build configuration

**Build Infrastructure:**
- Android Gradle build files
- CI/CD pipeline configuration
- Development tooling (CLI editor, testing scripts)

### ❌ What's Missing (Need Open Source Replacements)

**Critical Missing Components:**

1. **Native Libraries:**
   - OpenJPEG JPEG2000 decoder (actual implementation)
   - Voice chat libraries (Vivox SDK - proprietary)
   - Audio processing libraries
   - DirectByteBuffer native implementation

2. **Protocol Components:**
   - Modern authentication (OAuth2)
   - HTTP/2 CAPS implementation
   - WebSocket event streaming
   - Updated message definitions

3. **Graphics Components:**
   - PBR (Physically-Based Rendering) shaders
   - Modern texture compression support
   - Deferred rendering pipeline
   - Vulkan backend for high-end devices

## Reconstruction Strategy: Open Source Integration

### Phase 1: Foundation Libraries (Weeks 1-4)

#### 1.1 Protocol Implementation - LibreMetaverse Integration

**Source:** `cinderblocks/libremetaverse` (C#/.NET)
**Target:** Replace/enhance `com.lumiyaviewer.lumiya.slproto`

**Key Components to Port:**
```
LibreMetaverse/OpenMetaverse/          → com.lumiyaviewer.lumiya.slproto/
├── NetworkManager.cs                  → SLCircuit.java (enhance)
├── Messages/                          → messages/ (update)
├── Types/                            → types/ (enhance)  
├── Assets/                           → assets/ (modernize)
├── Primitives/                       → objects/ (update)
└── Authentication/                   → auth/ (replace with OAuth2)
```

**Implementation Plan:**
1. **Map C# patterns to Java:** Convert LibreMetaverse's event-driven architecture to Java EventBus
2. **Extract message definitions:** Update message types and protocol handling
3. **Authentication modernization:** Replace login/password with OAuth2 + PKCE
4. **Asset management:** Integrate modern asset fetching with HTTP/2

#### 1.2 Native Library Reconstruction

**Sources:**
- OpenJPEG: `uclouvain/openjpeg` 
- Basis Universal: `BinomialLLC/basis_universal`
- Audio: `libsndfile/libsndfile`, `oSIP/oRTP`

**Reconstruction Tasks:**

**A. OpenJPEG Integration**
```bash
# Add as Git submodule
git submodule add https://github.com/uclouvain/openjpeg.git app/src/main/cpp/openjpeg

# Update CMakeLists.txt
add_subdirectory(openjpeg)
target_link_libraries(openjpeg-native openjp2)
```

**B. Basis Universal (Already Partial)**
```bash
# Complete the existing integration
git submodule add https://github.com/BinomialLLC/basis_universal.git app/src/main/cpp/basis_universal
# Update existing CMakeLists.txt to include encoder/decoder
```

**C. Audio Libraries Replacement**
- **For Vivox (proprietary):** Create WebRTC-based voice chat using `google/webrtc`
- **For Audio Processing:** Use `libsndfile` and `oRTP` (both open source)

### Phase 2: Graphics Modernization (Weeks 5-8)

#### 2.1 PBR Rendering - Firestorm Integration

**Source:** `FirestormViewer/phoenix-firestorm`
**Target:** Enhance `com.lumiyaviewer.lumiya.render`

**Key Components to Adapt:**
```
phoenix-firestorm/indra/newview/       → com.lumiyaviewer.lumiya.render/
├── llviewershadermgr.cpp             → ShaderManager.java (port)
├── lldrawpoolbump.cpp                → PBRMaterialPool.java (adapt)
├── llspatialpartition.cpp            → spatial/ (enhance)
└── pipeline.cpp                      → RenderPipeline.java (modernize)
```

**Mobile Adaptation Strategy:**
1. **Shader Simplification:** Convert desktop GLSL to mobile GLSL ES
2. **LOD Management:** Implement aggressive level-of-detail for mobile
3. **Battery Optimization:** Dynamic quality scaling based on battery level
4. **Memory Management:** Mobile-specific texture and geometry pooling

#### 2.2 Modern Graphics APIs

**Implementation:**
```java
// File: app/src/main/java/com/lumiyaviewer/lumiya/render/ModernGraphicsPipeline.java
public class ModernGraphicsPipeline {
    // Detect and use best available graphics API
    private GraphicsAPI detectBestAPI() {
        if (deviceSupportsVulkan() && Build.VERSION.SDK_INT >= 24) {
            return GraphicsAPI.VULKAN;  // Best performance
        } else if (deviceSupportsOpenGLES31()) {
            return GraphicsAPI.OPENGL_ES_31; // Compute shaders
        } else if (deviceSupportsOpenGLES30()) {
            return GraphicsAPI.OPENGL_ES_30; // Modern pipeline
        } else {
            return GraphicsAPI.OPENGL_ES_20; // Fallback
        }
    }
}
```

### Phase 3: Advanced Features (Weeks 9-12)

#### 3.1 Modern Authentication - OAuth2 Implementation

**Source:** Standard OAuth2 libraries + Second Life's OAuth endpoint documentation
**Target:** Replace `com.lumiyaviewer.lumiya.slproto.auth`

**Implementation:**
```java
// File: app/src/main/java/com/lumiyaviewer/lumiya/auth/OAuth2SecondLifeAuth.java
public class OAuth2SecondLifeAuth {
    private static final String SL_AUTH_ENDPOINT = "https://id.secondlife.com/oauth/authorize";
    private static final String SL_TOKEN_ENDPOINT = "https://id.secondlife.com/oauth/token";
    
    public CompletableFuture<AuthResult> authenticateAsync(String username, String password) {
        // Implement PKCE OAuth2 flow
        // Store tokens securely in Android Keystore
        // Handle token refresh automatically
    }
}
```

#### 3.2 Voice Chat Replacement - WebRTC Integration

**Source:** `google/webrtc` + `signalapp/ringrtc` (mobile-optimized WebRTC)
**Target:** Replace proprietary Vivox with open source voice

**Architecture:**
```
WebRTC Voice System:
├── WebRTCVoiceClient.java        (replaces VivoxClient)
├── AudioManager.java             (handles mobile audio routing)
├── VoiceChannelManager.java      (manages SL voice channels)
└── SignalingService.java         (WebSocket-based signaling)
```

## Detailed Integration Instructions

### Step 1: Set Up Open Source Dependencies

**Add Git Submodules:**
```bash
# Protocol libraries
git submodule add https://github.com/cinderblocks/libremetaverse.git external/libremetaverse

# Native libraries  
git submodule add https://github.com/uclouvain/openjpeg.git app/src/main/cpp/openjpeg
git submodule add https://github.com/BinomialLLC/basis_universal.git app/src/main/cpp/basis_universal
git submodule add https://github.com/libsndfile/libsndfile.git app/src/main/cpp/libsndfile

# Graphics references
git submodule add https://github.com/FirestormViewer/phoenix-firestorm.git external/firestorm
git submodule add https://github.com/secondlife/viewer.git external/secondlife-viewer
```

**Update build.gradle:**
```gradle
dependencies {
    // OAuth2 support
    implementation 'net.openid:appauth:0.11.1'
    
    // Modern networking
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    
    // WebRTC for voice (replace Vivox)
    implementation 'org.webrtc:google-webrtc:1.0.32006'
    
    // Existing dependencies...
}
```

### Step 2: LibreMetaverse Protocol Integration

**Map Key Components:**

1. **Message System Integration:**
```bash
# Extract message definitions from LibreMetaverse
cd external/libremetaverse/OpenMetaverse/Messages
# Port .cs message definitions to Java

# Enhance existing slproto with modern message handling
# File: app/src/main/java/com/lumiyaviewer/lumiya/slproto/messages/
```

2. **Authentication Replacement:**
```java
// Replace existing login system
// File: app/src/main/java/com/lumiyaviewer/lumiya/slproto/auth/ModernAuthManager.java

public class ModernAuthManager {
    public CompletableFuture<LoginResponse> loginAsync(LoginRequest request) {
        // Try OAuth2 first
        return oauth2Auth.authenticateAsync(request)
            .exceptionally(error -> {
                // Fallback to legacy auth for compatibility
                return legacyAuth.authenticate(request);
            });
    }
}
```

### Step 3: Graphics Pipeline Enhancement

**Port Firestorm Shaders to Mobile:**

1. **Extract PBR Shaders:**
```bash
# From Firestorm
cd external/firestorm/indra/newview/app_settings/shaders/class3/deferred/
# Convert GLSL desktop shaders to GLSL ES mobile shaders
```

2. **Implement Mobile PBR:**
```java
// File: app/src/main/java/com/lumiyaviewer/lumiya/render/pbr/MobilePBRRenderer.java
public class MobilePBRRenderer {
    public void renderPrimitive(SLPrimitive primitive) {
        if (deviceCapabilities.supportsAdvancedPBR()) {
            renderFullPBR(primitive);
        } else {
            renderSimplifiedPBR(primitive);  // Fallback for low-end devices
        }
    }
}
```

### Step 4: Native Library Integration

**Complete Native Implementation:**

1. **OpenJPEG Integration:**
```cmake
# Update app/src/main/cpp/CMakeLists.txt
add_subdirectory(openjpeg)

add_library(
    lumiya-native
    SHARED
    ${EXISTING_SOURCES}
    jni/openjpeg_wrapper.cpp
)

target_link_libraries(
    lumiya-native
    openjp2
    ${log-lib}
)
```

2. **Create JNI Wrapper:**
```cpp
// File: app/src/main/cpp/jni/openjpeg_wrapper.cpp
#include <jni.h>
#include <openjpeg.h>

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_lumiyaviewer_lumiya_openjpeg_OpenJPEGDecoder_decodeJ2K(
    JNIEnv *env, 
    jobject thiz, 
    jbyteArray j2k_data) {
    
    // Implement actual JPEG2000 decoding using OpenJPEG
    // Replace the stub implementation
}
```

## Testing and Validation Strategy

### Phase 1 Testing: Core Functionality
1. **Protocol Testing:** Connect to Second Life test grid
2. **Authentication Testing:** OAuth2 flow with real SL servers
3. **Asset Loading:** Download and display textures/meshes
4. **Basic Rendering:** Ensure 3D world displays correctly

### Phase 2 Testing: Advanced Features  
1. **PBR Rendering:** Test material rendering quality
2. **Voice Chat:** WebRTC voice functionality
3. **Performance:** Mobile optimization effectiveness
4. **Compatibility:** Test across different Android versions/devices

### Phase 3 Testing: Integration
1. **Full Client Testing:** Complete Second Life client functionality
2. **OpenSimulator Testing:** Compatibility with OpenSim grids
3. **Stress Testing:** Memory usage, battery life, network conditions
4. **User Testing:** Real-world usage scenarios

## Expected Challenges and Solutions

### Challenge 1: C# to Java Porting
**Problem:** LibreMetaverse is C#/.NET, need Java equivalent
**Solution:** Use existing Java patterns, focus on protocol compatibility not code similarity

### Challenge 2: Desktop to Mobile Graphics
**Problem:** Firestorm shaders designed for desktop GPUs
**Solution:** Create adaptive rendering pipeline with quality levels

### Challenge 3: Proprietary Component Replacement
**Problem:** Some components (Vivox) are proprietary
**Solution:** Use modern open source alternatives (WebRTC for voice)

### Challenge 4: Legal and IP Concerns
**Problem:** Ensure compliance with licenses and IP
**Solution:** Only use open source components, respect all licenses, clean-room implementation where needed

## Success Metrics

**Phase 1 Success:** 
- ✅ APK builds without errors
- ✅ Connects to Second Life successfully
- ✅ Basic 3D world rendering works
- ✅ Avatar movement and interaction

**Phase 2 Success:**
- ✅ Modern graphics rendering (PBR materials)
- ✅ Voice chat functionality
- ✅ All major SL features working
- ✅ Performance acceptable on mid-range devices

**Phase 3 Success:**
- ✅ Feature parity with desktop viewers
- ✅ OpenSimulator compatibility
- ✅ App store ready quality
- ✅ Active user community

## Conclusion

This reconstruction plan provides a systematic approach to transforming your decompiled Lumiya code into a fully functional, modern Android Second Life client using only open source components. The key is methodical integration of battle-tested open source libraries while maintaining mobile optimization and compatibility with existing Second Life infrastructure.

The project is ambitious but achievable with the right approach and patience. Each phase builds on the previous one, allowing for incremental testing and validation along the way.