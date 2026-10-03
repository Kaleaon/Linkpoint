# C++ Integration Guide for Linkpoint

## Overview

This guide documents the successful integration of C++ components from the Second Life ecosystem into the Linkpoint Android viewer. The primary focus is on the Basis Universal texture transcoder, which enables modern GPU-accelerated texture compression.

## What Was Integrated

### 1. Basis Universal Transcoder (C++)

**Source**: https://github.com/BinomialLLC/basis_universal  
**Location**: `app/src/main/cpp/basis_universal/`  
**Purpose**: Runtime transcoding of KTX2 textures to GPU-native formats

**Key Files**:
- `basisu_transcoder.cpp` - Main transcoder implementation (~24k lines)
- `basisu_transcoder.h` - Public API interface  
- `basisu_transcoder_internal.h` - Internal implementation details
- `basisu_*.inc` - Lookup tables for various GPU formats

### 2. JNI Bridge Layer

**Location**: `app/src/main/cpp/jni/basis_transcoder_jni.cpp`  
**Purpose**: Java Native Interface for C++ transcoder access

**Key Functions**:
- `nativeInit()` - Initialize transcoder subsystem
- `nativeCreateTranscoder()` - Create transcoder instance
- `nativeInitTranscoder()` - Initialize with KTX2 data
- `nativeTranscodeTexture()` - Perform transcoding operation
- `nativeDestroyTranscoder()` - Cleanup transcoder instance

### 3. Java Wrapper API

**Location**: `app/src/main/java/com/lumiyaviewer/lumiya/render/ModernTextureManager.java`  
**Purpose**: High-level Java API for texture loading and management

**Key Features**:
- GPU capability detection (ASTC, ETC2, BC7 support)
- Automatic format selection for optimal performance
- KTX2 file loading and transcoding
- OpenGL texture upload helpers

## Build System Integration

### CMake Configuration

**File**: `app/src/main/cpp/CMakeLists.txt`

```cmake
# Set C++14 standard for modern features
set(CMAKE_CXX_STANDARD 14)

# Include Basis Universal sources
set(BASIS_TRANSCODER_SOURCES
    basis_universal/basisu_transcoder.cpp
)

# Android-specific optimizations
add_definitions(
    -DBASISU_SUPPORT_SSE=0      # Disable SSE for ARM
    -DBASISU_SUPPORT_OPENCL=0   # Disable OpenCL
    -DANDROID                   # Android platform flag
)

# Optimization flags
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math")
```

### Gradle Integration

**File**: `app/build.gradle`

```gradle
android {
    defaultConfig {
        // NDK configuration
        ndk {
            abiFilters 'armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64'
        }
        
        externalNativeBuild {
            cmake {
                cppFlags "-std=c++14 -O3 -ffast-math"
                arguments "-DANDROID_STL=c++_shared"
            }
        }
    }
    
    // CMake build configuration
    externalNativeBuild {
        cmake {
            path "src/main/cpp/CMakeLists.txt"
            version "3.10.2"
        }
    }
}
```

## Usage Examples

### Basic Texture Loading

```java
// Initialize texture manager
ModernTextureManager textureManager = new ModernTextureManager(context);

// Load KTX2 texture from assets
InputStream ktx2Stream = assets.open("textures/diffuse.ktx2");
TextureData textureData = textureManager.loadKTX2Texture(ktx2Stream);

// Upload to OpenGL
int textureId = GLES20.glGenTextures(1)[0];
GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

if (textureData.isCompressed()) {
    GLES20.glCompressedTexImage2D(
        GLES20.GL_TEXTURE_2D, 0, textureData.getOpenGLFormat(),
        textureData.width, textureData.height, 0,
        textureData.data.length, ByteBuffer.wrap(textureData.data)
    );
}
```

### GPU Capability Detection

```java
ModernTextureManager manager = new ModernTextureManager(context);

// Check what formats are supported
int optimalFormat = manager.getOptimalTextureFormat();
String formatName = ModernTextureManager.getFormatName(optimalFormat);

Log.i(TAG, "Optimal texture format: " + formatName);
// Output examples:
// "Optimal texture format: ASTC_4x4_RGBA"  (high-end devices)
// "Optimal texture format: ETC2_RGBA"      (mid-range devices)
// "Optimal texture format: RGBA32"         (fallback)
```

## Asset Pipeline Integration

### Converting Textures to KTX2

Use the provided conversion script:

```bash
# Make script executable
chmod +x scripts/convert_textures.sh

# Convert all textures in assets/textures/ to KTX2 format
./scripts/convert_textures.sh
```

Manual conversion with basisu tool:

```bash
# High quality UASTC encoding with mipmaps
basisu -ktx2 -uastc -uastc_level 2 -mipmap input.png -output_file output.ktx2

# Fast ETC1S encoding for smaller files
basisu -ktx2 -etc1s -q 128 -mipmap input.png -output_file output.ktx2
```

## Performance Benefits

### Memory Usage Reduction

| Format | Compression Ratio | Memory Savings |
|--------|------------------|----------------|
| ASTC 4x4 | 8:1 | 87.5% reduction |
| ETC2 | 4:1 | 75% reduction |
| BC7 | 4:1 | 75% reduction |

### Loading Performance

- **GPU-native decompression**: No CPU overhead for texture decompression
- **Reduced memory bandwidth**: Compressed textures transfer faster
- **Hardware acceleration**: Modern GPUs decode compressed formats in hardware

### Example Performance Metrics

For a 1024x1024 RGBA texture:
- **Uncompressed**: 4MB memory, ~50ms CPU decompression
- **ASTC 4x4**: 512KB memory, ~1ms GPU decompression
- **ETC2**: 1MB memory, ~1ms GPU decompression

## Testing and Validation

### Unit Tests

Run the provided test class to verify integration:

```java
// In your Application.onCreate() or test method
ModernTextureManagerTest.runBasicTests(context);
```

### Build Verification

```bash
# Check that native library builds correctly
gradle assembleDebug

# Verify library is included in APK
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep libbasis_transcoder.so
```

## Common Issues and Solutions

### 1. Native Library Loading Failure

**Error**: `UnsatisfiedLinkError: couldn't find libbasis_transcoder.so`

**Solutions**:
- Verify NDK is properly configured in build.gradle
- Check that CMakeLists.txt is in correct location
- Ensure target ABI is included in ndk.abiFilters

### 2. Transcoding Failure

**Error**: `Failed to transcode texture data`

**Solutions**:
- Verify KTX2 file is valid (check with basisu tool)
- Ensure sufficient memory for transcoding operation
- Check that target format is supported on device

### 3. GPU Format Support Issues

**Error**: Texture appears corrupted or black

**Solutions**:
- Verify GPU extension detection is working correctly
- Fall back to RGBA32 format for debugging
- Check OpenGL error state after texture upload

## Future Enhancements

### Planned Improvements

1. **Multi-threading**: Background transcoding with thread pool
2. **Caching**: Disk cache for transcoded textures
3. **Streaming**: Progressive loading for large textures
4. **HDR Support**: High dynamic range texture formats

### Integration Opportunities

1. **More C++ Components**: Additional SecondLife C++ utilities
2. **OpenCV Integration**: Computer vision for texture analysis
3. **Physics Engines**: Bullet Physics or similar for enhanced simulation

## References

- [Basis Universal GitHub](https://github.com/BinomialLLC/basis_universal)
- [KTX2 Specification](https://www.khronos.org/ktx/)
- [Android NDK Documentation](https://developer.android.com/ndk)
- [OpenGL ES Texture Compression](https://www.khronos.org/opengl/wiki/Texture_Compression)

## Contributing

When adding new C++ components:

1. Create subdirectory in `app/src/main/cpp/`
2. Update CMakeLists.txt with new sources
3. Create JNI wrapper if Java access needed
4. Add appropriate preprocessor definitions
5. Test on multiple Android architectures
6. Document integration steps