# Step-by-Step Integration Roadmap: Completing the Decompiled Client

## Overview

This roadmap provides detailed, actionable steps to transform your decompiled Lumiya code into a complete, working Android Second Life client using open source components. Each step includes specific commands, code examples, and validation checkpoints.

## Phase 1: Critical Foundation (Weeks 1-4)

### Week 1: Repository Setup and Dependencies

#### Step 1.1: Set Up Open Source Dependencies
```bash
# Navigate to project root
cd /app

# Add Git submodules for key dependencies
git submodule add https://github.com/uclouvain/openjpeg.git external/openjpeg
git submodule add https://github.com/BinomialLLC/basis_universal.git external/basis_universal  
git submodule add https://github.com/cinderblocks/libremetaverse.git external/libremetaverse
git submodule add https://github.com/FirestormViewer/phoenix-firestorm.git external/firestorm

# Initialize submodules
git submodule update --init --recursive

# Create reference directory for analysis
mkdir -p external/analysis
```

#### Step 1.2: Update Build Configuration
**Update `app/build.gradle`:**
```gradle
dependencies {
    // OAuth2 authentication
    implementation 'net.openid:appauth:0.11.1'
    
    // JSON processing (for LibreMetaverse message porting)
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // WebRTC for voice (Vivox replacement)
    implementation 'org.webrtc:google-webrtc:1.0.32006'
    
    // Enhanced networking (already included, ensure latest)
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    
    // Existing dependencies remain unchanged...
}
```

#### Step 1.3: Analyze Current Native Library Status
```bash
# Check what native libraries are actually built
./gradlew assembleDebug
ls -la app/build/intermediates/cmake/debug/obj/

# Expected output should show stub libraries:
# - libopenjpeg.so (stub)
# - librawbuf.so (stub)  
# - libbasis_transcoder.so (stub)
# - libvivoxsdk.so (stub)
```

**Validation Checkpoint:**
- ✅ All submodules downloaded successfully
- ✅ Gradle build completes without errors
- ✅ APK generates with stub native libraries

### Week 2: OpenJPEG Integration (Critical for Texture Loading)

#### Step 2.1: Integrate Real OpenJPEG Implementation

**Update `app/src/main/cpp/CMakeLists.txt`:**
```cmake
# Add after existing cmake_minimum_required
# Add OpenJPEG subdirectory
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/../../../external/openjpeg external/openjpeg)

# Update OPENJPEG_SOURCES to include real implementation
set(OPENJPEG_SOURCES
    # Remove stub, add real implementation
    jni/openjpeg_real_implementation.cpp
    jni/basis_transcoder_jni.cpp
    # Keep existing basis universal sources
    basis_universal/basisu_transcoder.cpp
)

# Update target linking for openjpeg library
target_link_libraries(
    openjpeg
    openjp2  # Link to real OpenJPEG library
    ${log-lib}
    ${android-lib}
    ${EGL-lib}
    ${GLESv3-lib}
)

# Include OpenJPEG headers
target_include_directories(openjpeg PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/../../../external/openjpeg/src/lib/openjp2
)
```

#### Step 2.2: Create Real OpenJPEG JNI Implementation

**Create `app/src/main/cpp/jni/openjpeg_real_implementation.cpp`:**
```cpp
#include <jni.h>
#include <android/log.h>
#include <openjpeg.h>
#include <vector>

#define LOG_TAG "OpenJPEGDecoder"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_lumiyaviewer_lumiya_openjpeg_OpenJPEGDecoder_decodeJ2K(
    JNIEnv *env, jobject thiz, jbyteArray j2k_data) {
    
    if (!j2k_data) {
        LOGE("Input data is null");
        return nullptr;
    }
    
    jsize data_len = env->GetArrayLength(j2k_data);
    if (data_len <= 0) {
        LOGE("Input data length is invalid: %d", data_len);
        return nullptr;
    }
    
    // Get input data
    jbyte* input_data = env->GetByteArrayElements(j2k_data, nullptr);
    if (!input_data) {
        LOGE("Failed to get input data");
        return nullptr;
    }
    
    // Initialize OpenJPEG decoder
    opj_dparameters_t parameters;
    opj_set_default_decoder_parameters(&parameters);
    
    opj_codec_t* codec = opj_create_decompress(OPJ_CODEC_J2K);
    if (!codec) {
        LOGE("Failed to create OpenJPEG codec");
        env->ReleaseByteArrayElements(j2k_data, input_data, JNI_ABORT);
        return nullptr;
    }
    
    // Set up decoder
    if (!opj_setup_decoder(codec, &parameters)) {
        LOGE("Failed to setup OpenJPEG decoder");
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(j2k_data, input_data, JNI_ABORT);
        return nullptr;
    }
    
    // Create input stream
    opj_stream_t* stream = opj_stream_create_default_memory_stream(
        (OPJ_UINT8*)input_data, data_len, OPJ_TRUE);
    if (!stream) {
        LOGE("Failed to create input stream");
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(j2k_data, input_data, JNI_ABORT);
        return nullptr;
    }
    
    // Decode image
    opj_image_t* image = nullptr;
    if (!opj_read_header(stream, codec, &image)) {
        LOGE("Failed to read J2K header");
        opj_stream_destroy(stream);
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(j2k_data, input_data, JNI_ABORT);
        return nullptr;
    }
    
    if (!opj_decode(codec, stream, image)) {
        LOGE("Failed to decode J2K image");
        opj_image_destroy(image);
        opj_stream_destroy(stream);
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(j2k_data, input_data, JNI_ABORT);
        return nullptr;
    }
    
    // Convert to RGB format for Android
    if (image->numcomps < 3) {
        LOGE("Image has insufficient components: %d", image->numcomps);
        opj_image_destroy(image);
        opj_stream_destroy(stream);
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(j2k_data, input_data, JNI_ABORT);
        return nullptr;
    }
    
    // Extract RGB data
    int width = image->comps[0].w;
    int height = image->comps[0].h;
    std::vector<uint8_t> rgb_data(width * height * 3);
    
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int idx = y * width + x;
            int rgb_idx = idx * 3;
            
            // OpenJPEG stores data as signed integers, convert to unsigned bytes
            rgb_data[rgb_idx + 0] = (uint8_t)std::max(0, std::min(255, image->comps[0].data[idx])); // R
            rgb_data[rgb_idx + 1] = (uint8_t)std::max(0, std::min(255, image->comps[1].data[idx])); // G  
            rgb_data[rgb_idx + 2] = (uint8_t)std::max(0, std::min(255, image->comps[2].data[idx])); // B
        }
    }
    
    // Create Java byte array
    jbyteArray result = env->NewByteArray(rgb_data.size());
    if (result) {
        env->SetByteArrayRegion(result, 0, rgb_data.size(), (jbyte*)rgb_data.data());
        LOGI("Successfully decoded J2K image: %dx%d", width, height);
    }
    
    // Cleanup
    opj_image_destroy(image);
    opj_stream_destroy(stream);
    opj_destroy_codec(codec);
    env->ReleaseByteArrayElements(j2k_data, input_data, JNI_ABORT);
    
    return result;
}

// Additional JNI method to get image dimensions
extern "C" JNIEXPORT jintArray JNICALL
Java_com_lumiyaviewer_lumiya_openjpeg_OpenJPEGDecoder_getJ2KDimensions(
    JNIEnv *env, jobject thiz, jbyteArray j2k_data) {
    
    // Similar implementation but only return width/height
    // Implementation details similar to above but return dimensions only
    // This helps Java code allocate proper bitmap sizes
    
    return nullptr; // Placeholder - implement similar to decode method
}
```

#### Step 2.3: Test OpenJPEG Integration

**Create test method in Java:**
```java
// File: app/src/main/java/com/lumiyaviewer/lumiya/openjpeg/OpenJPEGDecoder.java
public class OpenJPEGDecoder {
    static {
        System.loadLibrary("openjpeg");
    }
    
    public native byte[] decodeJ2K(byte[] j2kData);
    public native int[] getJ2KDimensions(byte[] j2kData);
    
    // Test method
    public static boolean testDecoder() {
        OpenJPEGDecoder decoder = new OpenJPEGDecoder();
        
        // Create minimal test J2K data (you'll need real test data)
        byte[] testData = getTestJ2KData();
        
        try {
            byte[] result = decoder.decodeJ2K(testData);
            return result != null && result.length > 0;
        } catch (Exception e) {
            Log.e("OpenJPEGTest", "Decoder test failed", e);
            return false;
        }
    }
    
    private static byte[] getTestJ2KData() {
        // Return minimal valid J2K data for testing
        // You can extract this from a real SL texture for testing
        return new byte[0]; // Placeholder
    }
}
```

**Build and test:**
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug

# Check that real OpenJPEG library is included
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep openjpeg
# Should show libopenjpeg.so with larger size than before

# Install and test
adb install app/build/outputs/apk/debug/app-debug.apk
adb logcat | grep -E "OpenJPEG|lumiya"
```

**Validation Checkpoint:**
- ✅ OpenJPEG library builds without errors
- ✅ APK size increases (indicates real library inclusion)
- ✅ Test method executes without crashing
- ✅ Logs show successful OpenJPEG initialization

### Week 3: OAuth2 Authentication Implementation

#### Step 3.1: Implement OAuth2 Authentication Manager

**Create `app/src/main/java/com/lumiyaviewer/lumiya/auth/OAuth2AuthManager.java`:**
```java
package com.lumiyaviewer.lumiya.auth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.openid.appauth.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OAuth2AuthManager {
    private static final String TAG = "OAuth2AuthManager";
    
    // Second Life OAuth2 endpoints
    private static final String SL_AUTH_ENDPOINT = "https://id.secondlife.com/oauth/authorize";
    private static final String SL_TOKEN_ENDPOINT = "https://id.secondlife.com/oauth/token";
    private static final String SL_CLIENT_ID = "linkpoint-mobile"; // You'll need to register this
    private static final String REDIRECT_URI = "linkpoint://oauth-callback";
    
    private final Context context;
    private final AuthorizationService authService;
    private final ExecutorService executor;
    private final SecureTokenStorage tokenStorage;
    
    public OAuth2AuthManager(@NonNull Context context) {
        this.context = context;
        this.authService = new AuthorizationService(context);
        this.executor = Executors.newCachedThreadPool();
        this.tokenStorage = new SecureTokenStorage(context);
    }
    
    public CompletableFuture<AuthResult> authenticateAsync(@NonNull String username) {
        CompletableFuture<AuthResult> future = new CompletableFuture<>();
        
        // Check for cached valid tokens first
        OAuth2Tokens cachedTokens = tokenStorage.getCachedTokens(username);
        if (cachedTokens != null && !cachedTokens.isExpired()) {
            future.complete(AuthResult.success(cachedTokens));
            return future;
        }
        
        // Try refresh token if available
        if (cachedTokens != null && cachedTokens.hasValidRefreshToken()) {
            refreshTokenAsync(cachedTokens)
                .thenAccept(refreshedTokens -> {
                    if (refreshedTokens != null) {
                        tokenStorage.storeTokens(username, refreshedTokens);
                        future.complete(AuthResult.success(refreshedTokens));
                    } else {
                        // Refresh failed, need new auth
                        performNewAuthFlow(username, future);
                    }
                })
                .exceptionally(error -> {
                    Log.w(TAG, "Token refresh failed", error);
                    performNewAuthFlow(username, future);
                    return null;
                });
        } else {
            // No valid tokens, perform new OAuth2 flow
            performNewAuthFlow(username, future);
        }
        
        return future;
    }
    
    private void performNewAuthFlow(String username, CompletableFuture<AuthResult> future) {
        try {
            // Create service configuration
            AuthorizationServiceConfiguration serviceConfig = 
                new AuthorizationServiceConfiguration(
                    Uri.parse(SL_AUTH_ENDPOINT),
                    Uri.parse(SL_TOKEN_ENDPOINT)
                );
            
            // Generate PKCE parameters for security
            String codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier();
            String codeChallenge = CodeVerifierUtil.deriveCodeChallenge(codeVerifier);
            
            // Create authorization request
            AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
                serviceConfig,
                SL_CLIENT_ID,
                ResponseTypeValues.CODE,
                Uri.parse(REDIRECT_URI))
                .setScopes("agent:read", "agent:write", "inventory:read")
                .setCodeChallenge(codeChallenge, CodeVerifierUtil.CODE_CHALLENGE_METHOD_S256)
                .setAdditionalParameters(Map.of("username", username))
                .build();
            
            // Launch authorization intent
            Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
            
            // This requires activity context - you'll need to handle this in your activity
            // For now, complete with error indicating need for user interaction
            future.complete(AuthResult.needsUserInteraction(authIntent, codeVerifier));
            
        } catch (Exception e) {
            Log.e(TAG, "OAuth2 flow failed", e);
            future.complete(AuthResult.error("OAuth2 authentication failed: " + e.getMessage()));
        }
    }
    
    public CompletableFuture<OAuth2Tokens> handleAuthorizationResponse(
            Intent responseIntent, String codeVerifier) {
        
        CompletableFuture<OAuth2Tokens> future = new CompletableFuture<>();
        
        AuthorizationResponse response = AuthorizationResponse.fromIntent(responseIntent);
        AuthorizationException error = AuthorizationException.fromIntent(responseIntent);
        
        if (response != null) {
            // Exchange authorization code for tokens
            TokenRequest tokenRequest = response.createTokenExchangeRequest(
                Map.of("code_verifier", codeVerifier)
            );
            
            authService.performTokenRequest(tokenRequest, (tokenResponse, tokenError) -> {
                if (tokenResponse != null) {
                    OAuth2Tokens tokens = new OAuth2Tokens(
                        tokenResponse.accessToken,
                        tokenResponse.refreshToken,
                        tokenResponse.accessTokenExpirationTime
                    );
                    future.complete(tokens);
                } else {
                    Log.e(TAG, "Token exchange failed", tokenError);
                    future.complete(null);
                }
            });
        } else {
            Log.e(TAG, "Authorization failed", error);
            future.complete(null);
        }
        
        return future;
    }
    
    private CompletableFuture<OAuth2Tokens> refreshTokenAsync(OAuth2Tokens currentTokens) {
        // Implementation similar to token exchange but using refresh token
        // Details omitted for brevity
        return CompletableFuture.completedFuture(null);
    }
    
    public void shutdown() {
        authService.dispose();
        executor.shutdown();
    }
}
```

#### Step 3.2: Integrate with Existing Authentication System

**Enhance existing `com.lumiyaviewer.lumiya.slproto.auth` classes:**
```java
// Update existing auth system to try OAuth2 first
public class EnhancedAuthManager {
    private final OAuth2AuthManager oauth2Manager;
    private final LegacyAuthManager legacyManager; // Existing system
    
    public CompletableFuture<LoginResult> loginAsync(LoginCredentials credentials) {
        // Try OAuth2 first
        return oauth2Manager.authenticateAsync(credentials.getUsername())
            .thenCompose(authResult -> {
                if (authResult.isSuccess()) {
                    return convertToLoginResult(authResult);
                } else {
                    // Fallback to legacy authentication
                    Log.i("Auth", "OAuth2 failed, trying legacy auth");
                    return legacyManager.loginAsync(credentials);
                }
            });
    }
}
```

**Validation Checkpoint:**
- ✅ OAuth2 implementation compiles without errors
- ✅ Integration with existing auth system works
- ✅ Fallback to legacy auth maintains compatibility
- ✅ Secure token storage using Android Keystore

### Week 4: HTTP/2 CAPS Enhancement

#### Step 4.1: Enhance Existing CAPS System

**Update existing CAPS implementation in `com.lumiyaviewer.lumiya.slproto.caps`:**
```java
// Enhance existing CAPS client to use HTTP/2
public class ModernCapsClient extends ExistingCapsClient {
    private final OkHttpClient http2Client;
    
    public ModernCapsClient() {
        super(); // Initialize existing functionality
        
        // Create HTTP/2 enabled client
        this.http2Client = new OkHttpClient.Builder()
            .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
            .build();
    }
    
    @Override
    public CompletableFuture<CapsResponse> sendRequest(CapsRequest request) {
        // Try HTTP/2 first, fallback to existing implementation
        if (supportsHTTP2() && !isLimitedConnection()) {
            return sendHTTP2Request(request)
                .exceptionally(error -> {
                    Log.d("ModernCaps", "HTTP/2 failed, falling back to legacy");
                    return super.sendRequest(request).join();
                });
        } else {
            return super.sendRequest(request);
        }
    }
    
    private CompletableFuture<CapsResponse> sendHTTP2Request(CapsRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            Request http2Request = new Request.Builder()
                .url(request.getUrl())
                .method(request.getMethod(), createRequestBody(request))
                .build();
                
            try (Response response = http2Client.newCall(http2Request).execute()) {
                if (response.isSuccessful()) {
                    return parseCapsResponse(response);
                } else {
                    throw new IOException("HTTP/2 request failed: " + response.code());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
```

## Phase 1 Validation

After completing Phase 1, you should have:

**✅ Critical Foundation Complete:**
1. **Real OpenJPEG decoder** - Can decode Second Life textures
2. **OAuth2 authentication** - Modern, secure authentication flow  
3. **HTTP/2 CAPS** - Improved performance for asset downloads
4. **Stable build system** - All components compile and integrate

**Test Checklist:**
```bash
# Build test
./gradlew clean assembleDebug

# APK size should be larger (real libraries included)
ls -lh app/build/outputs/apk/debug/app-debug.apk

# Install and basic functionality test
adb install app/build/outputs/apk/debug/app-debug.apk

# Launch app and check logs for successful initialization
adb logcat | grep -E "OpenJPEG|OAuth2|ModernCaps"
```

**Expected Results:**
- APK builds successfully with larger size
- App launches without native library errors
- OpenJPEG decoder initializes correctly
- OAuth2 system is ready for authentication
- HTTP/2 CAPS shows improved performance

This completes the critical foundation phase. The next phases will build upon this foundation to add voice chat, advanced graphics, and protocol modernization.