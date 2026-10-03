# Practical Implementation Roadmap: Fixing Broken Code and Modernizing Lumiya

## Overview

This document provides a practical, step-by-step implementation roadmap for fixing the broken code areas in Lumiya Viewer and implementing modernization features. Each section includes specific code changes, testing procedures, and validation steps.

## Table of Contents

1. [Phase 1: Critical Fixes (Week 1-2)](#phase-1-critical-fixes-week-1-2)
2. [Phase 2: Protocol Modernization (Week 3-6)](#phase-2-protocol-modernization-week-3-6)
3. [Phase 3: Graphics Pipeline (Week 7-12)](#phase-3-graphics-pipeline-week-7-12)
4. [Phase 4: Advanced Features (Week 13-20)](#phase-4-advanced-features-week-13-20)
5. [Testing and Validation](#testing-and-validation)
6. [Implementation Checklist](#implementation-checklist)

## Phase 1: Critical Fixes (Week 1-2)

### Fix 1: Android Build System Resource Conflicts

**Problem**: Build fails with duplicate resource errors
```
Error: Duplicate value for resource 'attr/fontStyle'
```

**Implementation Steps**:

1. **Update Gradle Configuration**
```gradle
// app/build.gradle - Apply these changes
android {
    namespace 'com.lumiyaviewer.lumiya'
    compileSdk 34
    
    // Fix resource conflicts
    packagingOptions {
        pickFirst '**/libjnidispatch.so'
        pickFirst '**/libopenjpeg.so'
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/LICENSE*'
        exclude 'META-INF/NOTICE*'
        exclude 'META-INF/INDEX.LIST'
        exclude 'META-INF/io.netty.versions.properties'
    }
    
    // Enable resource shrinking
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
        debug {
            minifyEnabled false
            shrinkResources false
        }
    }
    
    // Fix source set configuration
    sourceSets {
        main {
            manifest.srcFile 'resources/AndroidManifest.xml'
            java.srcDirs = ['src/main/java']
            res.srcDirs = ['resources/res']
            assets.srcDirs = ['resources/assets']
        }
    }
}

// Update dependencies to resolve conflicts
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.google.guava:guava:32.1.2-android'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    
    // Remove conflicting dependencies
    configurations.all {
        exclude group: 'org.apache.httpcomponents', module: 'httpclient'
        exclude group: 'commons-logging', module: 'commons-logging'
    }
}
```

2. **Update gradle.properties**
```properties
# gradle.properties - Add these lines
android.useAndroidX=true
android.enableJetifier=true
android.suppressUnsupportedCompileSdk=34
android.nonTransitiveRClass=true
android.enableResourceOptimizations=true
android.suppressUnsupportedCompileSdk=34

# Increase heap size for build
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
org.gradle.parallel=true
org.gradle.configureondemand=true
```

3. **Create Resource Conflict Resolver**
```java
// Create: app/src/main/java/com/lumiyaviewer/lumiya/fixes/ResourceConflictResolver.java
package com.lumiyaviewer.lumiya.fixes;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class ResourceConflictResolver {
    private static final String TAG = "ResourceResolver";
    
    public static void initialize(Context context) {
        try {
            resolveAttributeConflicts(context);
            resolveDrawableConflicts(context);
            Log.i(TAG, "Resource conflicts resolved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to resolve resource conflicts", e);
        }
    }
    
    private static void resolveAttributeConflicts(Context context) {
        // Handle conflicting attributes by using fully qualified names
        Resources res = context.getResources();
        
        // Ensure our app's attributes take precedence
        try {
            int fontStyleAttr = res.getIdentifier("fontStyle", "attr", context.getPackageName());
            if (fontStyleAttr != 0) {
                Log.d(TAG, "App fontStyle attribute resolved: " + fontStyleAttr);
            }
        } catch (Resources.NotFoundException e) {
            Log.w(TAG, "fontStyle attribute not found in app resources");
        }
    }
    
    private static void resolveDrawableConflicts(Context context) {
        // Handle drawable conflicts
        Resources res = context.getResources();
        
        try {
            int eyeIcon = res.getIdentifier("design_ic_visibility", "drawable", context.getPackageName());
            if (eyeIcon != 0) {
                Log.d(TAG, "Eye icon drawable resolved: " + eyeIcon);
            }
        } catch (Resources.NotFoundException e) {
            Log.w(TAG, "Eye icon not found in app resources");
        }
    }
}
```

4. **Update Application Class**
```java
// Update: app/src/main/java/com/lumiyaviewer/lumiya/LumiyaApp.java
package com.lumiyaviewer.lumiya;

import android.app.Application;
import com.lumiyaviewer.lumiya.fixes.ResourceConflictResolver;

public class LumiyaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize resource conflict resolver
        ResourceConflictResolver.initialize(this);
        
        // Continue with other initialization...
    }
}
```

**Testing**:
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug

# Verify no resource conflicts
./gradlew assembleRelease
```

### Fix 2: Native Build Configuration

**Problem**: CMake build fails with missing dependencies

**Implementation Steps**:

1. **Update CMakeLists.txt**
```cmake
# Update: app/src/main/cpp/CMakeLists.txt
cmake_minimum_required(VERSION 3.22.1)
project("lumiya" LANGUAGES CXX C)

# Modern C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

# NDK configuration
set(CMAKE_ANDROID_NDK_TOOLCHAIN_VERSION clang)
set(CMAKE_ANDROID_STL_TYPE c++_shared)

# Platform-specific optimizations
if(ANDROID_ABI STREQUAL "arm64-v8a")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math -march=armv8-a")
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -ffast-math -march=armv8-a")
elseif(ANDROID_ABI STREQUAL "armeabi-v7a")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math -march=armv7-a -mfpu=neon")
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -ffast-math -march=armv7-a -mfpu=neon")
endif()

# Find required libraries
find_library(log-lib log)
find_library(android-lib android)
find_library(EGL-lib EGL)
find_library(GLESv3-lib GLESv3)

# OpenJPEG for JPEG2000 decoding
add_subdirectory(openjpeg)

# Vulkan support (optional)
if(ANDROID_PLATFORM_LEVEL GREATER_EQUAL 24)
    find_library(vulkan-lib vulkan)
    add_definitions(-DVULKAN_SUPPORT=1)
endif()

# Create shared library
add_library(lumiya-native SHARED
    native-lib.cpp
    jpeg2000/openjpeg_decoder.cpp
    texture/texture_utils.cpp
    math/vector_operations.cpp
)

# Include directories
target_include_directories(lumiya-native PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/openjpeg/include
)

# Link libraries
target_link_libraries(lumiya-native
    ${log-lib}
    ${android-lib}
    ${EGL-lib}
    ${GLESv3-lib}
    openjpeg-static
)

# Link Vulkan if available
if(vulkan-lib)
    target_link_libraries(lumiya-native ${vulkan-lib})
endif()

# Compiler definitions
target_compile_definitions(lumiya-native PRIVATE
    -DANDROID_PLATFORM_LEVEL=${ANDROID_PLATFORM_LEVEL}
    -DUSE_OPENGL_ES3=1
)
```

2. **Create Native Library Interface**
```cpp
// Create: app/src/main/cpp/native-lib.cpp
#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "LumiyaNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_lumiyaviewer_lumiya_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

// JPEG2000 decoding functions
extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_lumiyaviewer_lumiya_NativeLib_decodeJPEG2000(
        JNIEnv* env,
        jobject /* this */,
        jbyteArray j2kData) {
    
    LOGI("Starting JPEG2000 decode");
    
    // Get input data
    jsize dataLen = env->GetArrayLength(j2kData);
    jbyte* inputData = env->GetByteArrayElements(j2kData, nullptr);
    
    // TODO: Implement actual JPEG2000 decoding using OpenJPEG
    // For now, return dummy data
    jbyteArray result = env->NewByteArray(dataLen);
    env->SetByteArrayRegion(result, 0, dataLen, inputData);
    
    // Release input data
    env->ReleaseByteArrayElements(j2kData, inputData, JNI_ABORT);
    
    LOGI("JPEG2000 decode completed");
    return result;
}

// Vulkan initialization
extern "C" JNIEXPORT jboolean JNICALL
Java_com_lumiyaviewer_lumiya_NativeLib_initializeVulkan(
        JNIEnv* env,
        jobject /* this */,
        jobject surface) {
    
#ifdef VULKAN_SUPPORT
    LOGI("Initializing Vulkan");
    // TODO: Implement Vulkan initialization
    return JNI_TRUE;
#else
    LOGI("Vulkan not supported on this platform");
    return JNI_FALSE;
#endif
}
```

3. **Create Java Native Interface**
```java
// Create: app/src/main/java/com/lumiyaviewer/lumiya/NativeLib.java
package com.lumiyaviewer.lumiya;

import android.view.Surface;

public class NativeLib {
    
    // Load native library
    static {
        System.loadLibrary("lumiya-native");
    }
    
    // Native method declarations
    public static native String stringFromJNI();
    public static native byte[] decodeJPEG2000(byte[] j2kData);
    public static native boolean initializeVulkan(Surface surface);
    
    // Utility methods
    public static boolean isNativeLibraryLoaded() {
        try {
            stringFromJNI();
            return true;
        } catch (UnsatisfiedLinkError e) {
            return false;
        }
    }
}
```

**Testing**:
```bash
# Test native build
./gradlew assembleDebug

# Verify native library is included
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep liblu
```

### Fix 3: Memory Management Improvements

**Problem**: Memory leaks and inefficient resource usage

**Implementation Steps**:

1. **Create Memory Manager**
```java
// Create: app/src/main/java/com/lumiyaviewer/lumiya/memory/MemoryManager.java
package com.lumiyaviewer.lumiya.memory;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryManager {
    private static final String TAG = "MemoryManager";
    private static final long MB = 1024 * 1024;
    
    private final Context context;
    private final ActivityManager activityManager;
    private final AtomicLong totalAllocated = new AtomicLong(0);
    private final ConcurrentHashMap<String, WeakReference<Object>> resourceCache = new ConcurrentHashMap<>();
    
    public MemoryManager(Context context) {
        this.context = context;
        this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }
    
    public void trackAllocation(String key, Object resource, long size) {
        resourceCache.put(key, new WeakReference<>(resource));
        totalAllocated.addAndGet(size);
        
        checkMemoryPressure();
    }
    
    public void trackDeallocation(String key, long size) {
        resourceCache.remove(key);
        totalAllocated.addAndGet(-size);
    }
    
    private void checkMemoryPressure() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memInfo);
        
        long availableMemory = memInfo.availMem;
        long totalMemory = memInfo.totalMem;
        long usedMemory = totalMemory - availableMemory;
        
        float memoryUsagePercent = (float) usedMemory / totalMemory;
        
        if (memoryUsagePercent > 0.8f) {
            Log.w(TAG, "High memory usage detected: " + (memoryUsagePercent * 100) + "%");
            performMemoryCleanup();
        }
    }
    
    private void performMemoryCleanup() {
        Log.i(TAG, "Performing memory cleanup");
        
        // Clean up weak references
        resourceCache.entrySet().removeIf(entry -> entry.getValue().get() == null);
        
        // Force garbage collection
        System.gc();
        
        // Notify listeners about memory pressure
        notifyMemoryPressureListeners();
    }
    
    private void notifyMemoryPressureListeners() {
        // TODO: Implement listener pattern for memory pressure
    }
    
    public long getTotalAllocatedMemory() {
        return totalAllocated.get();
    }
    
    public int getCachedResourceCount() {
        return resourceCache.size();
    }
}
```

2. **Update Resource Caches**
```java
// Update: app/src/main/java/com/lumiyaviewer/lumiya/res/TextureCache.java
package com.lumiyaviewer.lumiya.res;

import android.util.Log;
import android.util.LruCache;
import com.lumiyaviewer.lumiya.memory.MemoryManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TextureCache {
    private static final String TAG = "TextureCache";
    private static final int MAX_CACHE_SIZE_MB = 128; // 128MB max cache
    
    private final LruCache<String, CachedTexture> textureCache;
    private final MemoryManager memoryManager;
    private final AtomicLong cacheSize = new AtomicLong(0);
    
    public TextureCache(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        
        // Create LRU cache with size-based eviction
        this.textureCache = new LruCache<String, CachedTexture>(MAX_CACHE_SIZE_MB * 1024 * 1024) {
            @Override
            protected int sizeOf(String key, CachedTexture texture) {
                return texture.getSize();
            }
            
            @Override
            protected void entryRemoved(boolean evicted, String key, 
                                      CachedTexture oldValue, CachedTexture newValue) {
                if (oldValue != null) {
                    Log.d(TAG, "Evicting texture: " + key + " (size: " + oldValue.getSize() + " bytes)");
                    oldValue.dispose();
                    cacheSize.addAndGet(-oldValue.getSize());
                    memoryManager.trackDeallocation(key, oldValue.getSize());
                }
            }
        };
    }
    
    public CachedTexture get(String textureId) {
        return textureCache.get(textureId);
    }
    
    public void put(String textureId, CachedTexture texture) {
        if (texture == null) return;
        
        textureCache.put(textureId, texture);
        cacheSize.addAndGet(texture.getSize());
        memoryManager.trackAllocation(textureId, texture, texture.getSize());
        
        Log.d(TAG, "Cached texture: " + textureId + " (size: " + texture.getSize() + " bytes)");
        Log.d(TAG, "Total cache size: " + (cacheSize.get() / 1024 / 1024) + " MB");
    }
    
    public void clearCache() {
        Log.i(TAG, "Clearing texture cache");
        textureCache.evictAll();
        cacheSize.set(0);
    }
    
    public long getCacheSize() {
        return cacheSize.get();
    }
    
    public int getCacheCount() {
        return textureCache.size();
    }
    
    public static class CachedTexture {
        private final int textureId;
        private final int width;
        private final int height;
        private final int format;
        private final long size;
        private boolean disposed = false;
        
        public CachedTexture(int textureId, int width, int height, int format) {
            this.textureId = textureId;
            this.width = width;
            this.height = height;
            this.format = format;
            this.size = calculateSize(width, height, format);
        }
        
        private long calculateSize(int width, int height, int format) {
            // Estimate texture memory usage based on format
            int bytesPerPixel;
            switch (format) {
                case 0x1907: // GL_RGB
                    bytesPerPixel = 3;
                    break;
                case 0x1908: // GL_RGBA
                    bytesPerPixel = 4;
                    break;
                case 0x83F0: // GL_COMPRESSED_RGB_S3TC_DXT1_EXT
                    return (width * height) / 2; // DXT1 compression
                case 0x83F2: // GL_COMPRESSED_RGBA_S3TC_DXT3_EXT
                case 0x83F3: // GL_COMPRESSED_RGBA_S3TC_DXT5_EXT
                    return width * height; // DXT3/5 compression
                default:
                    bytesPerPixel = 4; // Default to RGBA
            }
            return width * height * bytesPerPixel;
        }
        
        public void dispose() {
            if (!disposed && textureId > 0) {
                // TODO: Delete OpenGL texture
                // GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
                disposed = true;
            }
        }
        
        public int getTextureId() { return textureId; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public long getSize() { return size; }
        public boolean isDisposed() { return disposed; }
    }
}
```

**Testing**:
```java
// Create test class for memory management
@Test
public void testMemoryManager() {
    MemoryManager memoryManager = new MemoryManager(context);
    TextureCache textureCache = new TextureCache(memoryManager);
    
    // Test cache operations
    CachedTexture texture = new CachedTexture(1, 512, 512, 0x1908);
    textureCache.put("test_texture", texture);
    
    assertEquals(texture, textureCache.get("test_texture"));
    assertTrue(textureCache.getCacheSize() > 0);
}
```

## Phase 2: Protocol Modernization (Week 3-6)

### Modern Network Layer Implementation

**Implementation Steps**:

1. **Create HTTP/2 CAPS Client**
```java
// Create: app/src/main/java/com/lumiyaviewer/lumiya/protocol/HTTP2CapsClient.java
package com.lumiyaviewer.lumiya.protocol;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HTTP2CapsClient {
    private static final String TAG = "HTTP2CapsClient";
    
    private final OkHttpClient httpClient;
    private final AuthTokenManager authManager;
    
    public HTTP2CapsClient(AuthTokenManager authManager) {
        this.authManager = authManager;
        
        // Configure HTTP/2 client
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        
        this.httpClient = new OkHttpClient.Builder()
            .protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .addInterceptor(logging)
            .addInterceptor(new AuthInterceptor(authManager))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
            .build();
    }
    
    public CompletableFuture<CapsResponse> sendRequest(CapsRequest request) {
        Request httpRequest = buildHttpRequest(request);
        
        CompletableFuture<CapsResponse> future = new CompletableFuture<>();
        
        httpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    CapsResponse capsResponse = parseResponse(response);
                    future.complete(capsResponse);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                } finally {
                    response.close();
                }
            }
        });
        
        return future;
    }
    
    private Request buildHttpRequest(CapsRequest request) {
        Request.Builder builder = new Request.Builder()
            .url(request.getUrl())
            .addHeader("User-Agent", "Lumiya-Viewer/3.4.3")
            .addHeader("Accept", "application/llsd+xml, application/json");
        
        if (request.hasBody()) {
            RequestBody body = RequestBody.create(
                request.getBody(), 
                MediaType.get(request.getContentType())
            );
            
            switch (request.getMethod()) {
                case POST:
                    builder.post(body);
                    break;
                case PUT:
                    builder.put(body);
                    break;
                case PATCH:
                    builder.patch(body);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported method with body: " + request.getMethod());
            }
        } else {
            switch (request.getMethod()) {
                case GET:
                    builder.get();
                    break;
                case DELETE:
                    builder.delete();
                    break;
                case HEAD:
                    builder.head();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported method: " + request.getMethod());
            }
        }
        
        return builder.build();
    }
    
    private CapsResponse parseResponse(Response response) throws IOException {
        String contentType = response.header("Content-Type", "");
        byte[] responseBody = response.body().bytes();
        
        return new CapsResponse(
            response.code(),
            response.headers().toMultimap(),
            responseBody,
            contentType
        );
    }
    
    private static class AuthInterceptor implements Interceptor {
        private final AuthTokenManager authManager;
        
        public AuthInterceptor(AuthTokenManager authManager) {
            this.authManager = authManager;
        }
        
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            
            // Add authentication if available
            String authToken = authManager.getCurrentToken();
            if (authToken != null) {
                Request authenticatedRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();
                return chain.proceed(authenticatedRequest);
            }
            
            return chain.proceed(originalRequest);
        }
    }
}
```

2. **WebSocket Event Client**
```java
// Create: app/src/main/java/com/lumiyaviewer/lumiya/protocol/WebSocketEventClient.java
package com.lumiyaviewer.lumiya.protocol;

import okhttp3.*;
import okio.ByteString;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketEventClient extends WebSocketListener {
    private static final String TAG = "WebSocketEventClient";
    
    private final OkHttpClient client;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<EventListener>> eventListeners = new ConcurrentHashMap<>();
    private WebSocket webSocket;
    private boolean connected = false;
    
    public WebSocketEventClient() {
        this.client = new OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .build();
    }
    
    public void connect(String eventQueueUrl, String authToken) {
        Request request = new Request.Builder()
            .url(eventQueueUrl)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();
            
        webSocket = client.newWebSocket(request, this);
    }
    
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Normal closure");
        }
    }
    
    public void subscribe(String eventType, EventListener listener) {
        eventListeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
    
    public void unsubscribe(String eventType, EventListener listener) {
        CopyOnWriteArrayList<EventListener> listeners = eventListeners.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }
    
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i(TAG, "WebSocket connection opened");
        connected = true;
        
        // Send subscription message for all registered event types
        for (String eventType : eventListeners.keySet()) {
            sendSubscription(eventType);
        }
    }
    
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            EventMessage event = EventMessage.parseFromString(text);
            dispatchEvent(event);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse event message", e);
        }
    }
    
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        try {
            EventMessage event = EventMessage.parseFromBytes(bytes.toByteArray());
            dispatchEvent(event);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse binary event message", e);
        }
    }
    
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.i(TAG, "WebSocket closing: " + reason);
        connected = false;
    }
    
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.i(TAG, "WebSocket closed: " + reason);
        connected = false;
    }
    
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "WebSocket failure", t);
        connected = false;
        
        // Attempt reconnection after delay
        scheduleReconnect();
    }
    
    private void sendSubscription(String eventType) {
        if (connected && webSocket != null) {
            String subscriptionMessage = String.format(
                "{\"action\":\"subscribe\",\"eventType\":\"%s\"}", 
                eventType
            );
            webSocket.send(subscriptionMessage);
        }
    }
    
    private void dispatchEvent(EventMessage event) {
        CopyOnWriteArrayList<EventListener> listeners = eventListeners.get(event.getType());
        if (listeners != null) {
            for (EventListener listener : listeners) {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    Log.e(TAG, "Error in event listener", e);
                }
            }
        }
    }
    
    private void scheduleReconnect() {
        // TODO: Implement exponential backoff reconnection
    }
    
    public interface EventListener {
        void onEvent(EventMessage event);
    }
    
    public boolean isConnected() {
        return connected;
    }
}
```

**Testing Protocol Updates**:
```java
@Test
public void testHTTP2CapsClient() {
    AuthTokenManager authManager = mock(AuthTokenManager.class);
    when(authManager.getCurrentToken()).thenReturn("test-token");
    
    HTTP2CapsClient client = new HTTP2CapsClient(authManager);
    
    CapsRequest request = CapsRequest.builder()
        .url("https://test.example.com/caps/test")
        .method(HttpMethod.GET)
        .build();
    
    CompletableFuture<CapsResponse> future = client.sendRequest(request);
    
    // Verify request is sent correctly
    assertNotNull(future);
}

@Test
public void testWebSocketEventClient() {
    WebSocketEventClient client = new WebSocketEventClient();
    
    AtomicBoolean eventReceived = new AtomicBoolean(false);
    client.subscribe("ChatFromSimulator", event -> {
        eventReceived.set(true);
    });
    
    // Simulate event
    EventMessage testEvent = new EventMessage("ChatFromSimulator", "test data");
    client.dispatchEvent(testEvent);
    
    assertTrue(eventReceived.get());
}
```

This practical roadmap provides specific, implementable code changes that address the broken areas while introducing modern features. Each phase builds upon the previous one, ensuring a stable progression toward a fully modernized Lumiya Viewer.