# Basis Universal C++ Integration Documentation

## Overview

This document describes the integration of the Basis Universal texture transcoder C++ library into the Linkpoint Android viewer. This integration enables modern texture compression formats (ASTC, ETC2, BC7) through runtime transcoding of KTX2 texture containers.

## Architecture

### Components

1. **C++ Transcoder Library** (`app/src/main/cpp/basis_universal/`)
   - Basis Universal transcoder source code
   - Supports transcoding to multiple GPU-native formats
   - Cross-platform C++ implementation

2. **JNI Wrapper** (`app/src/main/cpp/jni/basis_transcoder_jni.cpp`)
   - Java Native Interface bridge
   - Exposes transcoder functionality to Java code
   - Handles memory management and error handling

3. **Java Interface** (`ModernTextureManager.java`)
   - High-level Java API for texture loading
   - GPU capability detection
   - Automatic format selection

4. **NDK Build System** (`CMakeLists.txt`)
   - Compiles C++ code for Android platforms
   - Handles cross-compilation for ARM, x86 architectures

### Data Flow

```
KTX2 File → ModernTextureManager → JNI Bridge → C++ Transcoder → GPU Format
```

1. KTX2 texture file is loaded by ModernTextureManager
2. Java code calls native methods through JNI
3. C++ transcoder processes the data
4. Transcoded texture data is returned to Java
5. Texture is uploaded to GPU in optimal format

## Usage

### Basic Usage

```java
ModernTextureManager textureManager = new ModernTextureManager(context);

// Load KTX2 texture from assets
InputStream ktx2Stream = assets.open("texture.ktx2");
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
} else {
    GLES20.glTexImage2D(
        GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
        textureData.width, textureData.height, 0,
        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
        ByteBuffer.wrap(textureData.data)
    );
}
```

### Advanced Usage

```java
// Force specific format
TextureData astcTexture = textureManager.loadKTX2Texture(
    inputStream, ModernTextureManager.FORMAT_ASTC_4x4_RGBA
);

// Check GPU capabilities
int optimalFormat = textureManager.getOptimalTextureFormat();
String formatName = ModernTextureManager.getFormatName(optimalFormat);
Log.i(TAG, "Using optimal format: " + formatName);
```

## Supported Formats

### Compressed Formats

| Format | Constant | OpenGL Extension | Target Hardware |
|--------|----------|------------------|-----------------|
| ASTC 4x4 | `FORMAT_ASTC_4x4_RGBA` | `GL_KHR_texture_compression_astc_ldr` | High-end mobile GPUs |
| ETC2 | `FORMAT_ETC2_RGBA` | `GL_OES_compressed_ETC2_RGB8_texture` | Mid-range mobile GPUs |
| BC7 | `FORMAT_BC7_RGBA` | `GL_EXT_texture_compression_bptc` | Desktop/high-end GPUs |

### Fallback Format

| Format | Constant | Usage |
|--------|----------|-------|
| RGBA32 | `FORMAT_RGBA32` | Uncompressed fallback for all GPUs |

## Performance Benefits

### Memory Usage

- **ASTC 4x4**: ~8:1 compression ratio vs uncompressed RGBA
- **ETC2**: ~4:1 compression ratio vs uncompressed RGBA
- **BC7**: ~4:1 compression ratio vs uncompressed RGBA

### Loading Performance

- GPU-native formats eliminate CPU decompression overhead
- Hardware-accelerated texture decompression
- Reduced memory bandwidth requirements

### Quality

- Perceptually optimized compression algorithms
- Better quality than legacy JPEG2000 approach
- Supports HDR and normal maps

## Build Requirements

### Android NDK

- NDK version 21 or higher
- CMake 3.10.2 or higher
- C++14 compiler support

### Dependencies

- Android API level 14+ (for OpenGL ES 2.0)
- OpenGL ES 3.0+ recommended for best format support

## Error Handling

### Native Library Loading

```java
static {
    try {
        System.loadLibrary("basis_transcoder");
        Log.i(TAG, "Native library loaded successfully");
    } catch (UnsatisfiedLinkError e) {
        Log.e(TAG, "Failed to load native library", e);
        // Handle graceful fallback to legacy texture system
    }
}
```

### Transcoding Errors

- Invalid KTX2 data: IOException with descriptive message
- Unsupported format: Automatic fallback to RGBA32
- Memory allocation failure: Immediate cleanup and error reporting

## Integration with Existing Code

### Replacing TextureCache

The ModernTextureManager is designed to replace the existing OpenJPEG-based TextureCache:

```java
// Old approach
TextureCache textureCache = new TextureCache();
Texture texture = textureCache.loadTexture(imageData);

// New approach
ModernTextureManager textureManager = new ModernTextureManager(context);
TextureData textureData = textureManager.loadKTX2Texture(ktx2Stream);
```

### Asset Pipeline Integration

KTX2 files should be generated using the `basisu` command-line tool:

```bash
# Convert PNG to KTX2
basisu -ktx2 -uastc -uastc_level 4 input.png -output_file output.ktx2

# Batch conversion
find assets/textures -name "*.png" -exec basisu -ktx2 -uastc {} \;
```

## Debugging

### Logging

The implementation provides detailed logging at various levels:

```
I/ModernTextureManager: ModernTextureManager initialized with GPU capabilities:
I/ModernTextureManager:   ASTC support: true
I/ModernTextureManager:   ETC2 support: true
I/ModernTextureManager:   BC7 support: false
I/BasisTranscoder: KTX2 transcoder initialized successfully
I/BasisTranscoder: Successfully transcoded texture: 65536 bytes
```

### Common Issues

1. **Library Loading Failure**: Check NDK configuration and ABI filters
2. **Transcoding Failure**: Verify KTX2 file integrity and format support
3. **Performance Issues**: Monitor texture size and mip level usage

## Future Enhancements

### Planned Features

1. **Multi-threading**: Background transcoding for improved performance
2. **Caching**: Disk cache for transcoded textures
3. **Streaming**: Progressive texture loading for large textures
4. **HDR Support**: High dynamic range texture formats

### Migration Strategy

1. **Phase 1**: Parallel implementation alongside existing system
2. **Phase 2**: Gradual migration of texture assets to KTX2
3. **Phase 3**: Complete replacement of legacy texture system

## References

- [Basis Universal Repository](https://github.com/BinomialLLC/basis_universal)
- [KTX2 Specification](https://www.khronos.org/ktx/)
- [Android NDK Documentation](https://developer.android.com/ndk)
- [OpenGL ES Texture Compression](https://www.khronos.org/opengl/wiki/Texture_Compression)