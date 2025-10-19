# Filament Rendering Integration

This directory contains the integration of Google's Filament rendering engine into the Lumiya Viewer app. Filament is a modern, physically-based rendering (PBR) engine designed for mobile and desktop platforms.

## Overview

Filament has been integrated to provide a modern, high-performance rendering pipeline for the virtual world viewer. This integration replaces or coexists with the legacy OpenGL ES rendering code.

## Architecture

### Core Components

#### 1. **FilamentRenderContext** (`FilamentRenderContext.kt`)
The main wrapper around Filament's core components:
- **Engine**: Creates and manages all Filament resources
- **Renderer**: Handles the actual rendering
- **Scene**: Contains all renderable entities, lights, and environment
- **View**: Defines viewport, camera, and scene for rendering
- **Camera**: Controls the viewing perspective
- **SwapChain**: Represents the rendering surface

Key features:
- Automatic Filament JNI initialization
- Lifecycle management (initialize, attach, detach, destroy)
- Surface callback handling for swap chain management
- Automatic viewport updates on resize

#### 2. **FilamentWorldRenderer** (`FilamentWorldRenderer.kt`)
Manages world-specific rendering:
- Scene content creation (terrain, objects, avatars)
- Camera positioning and control
- Material and mesh management
- Test scene with colored triangle for verification

#### 3. **FilamentSurfaceView** (`FilamentSurfaceView.kt`)
A custom SurfaceView that manages the rendering loop:
- Choreographer-based frame scheduling (similar to GLSurfaceView)
- Automatic lifecycle handling (attach/detach/pause/resume)
- Integration with FilamentRenderContext and FilamentWorldRenderer

#### 4. **FilamentWorldViewActivity** (`FilamentWorldViewActivity.kt`)
Modern activity demonstrating Filament rendering:
- Gesture controls for camera movement
- Touch-based camera rotation
- Pinch-to-zoom support
- Loading overlay UI

#### 5. **FilamentTestActivity** (`FilamentTestActivity.kt`)
Simple test activity for verifying Filament integration:
- Minimal UI
- Renders a test triangle
- Useful for debugging and validation

## Dependencies

Added to `app/build.gradle`:
```gradle
implementation 'com.google.android.filament:filament-android:1.66.0'
implementation 'com.google.android.filament:filament-utils-android:1.66.0'
implementation 'com.google.android.filament:gltfio-android:1.66.0'
```

## Usage

### Basic Setup

```kotlin
// Create and initialize Filament surface view
val filamentView = FilamentSurfaceView(context)
filamentView.initializeWorldRenderer()

// Set camera position
filamentView.getWorldRenderer()?.setCameraPosition(
    LLVector3(0f, -20f, 10f),
    rotationX = 0f,
    rotationY = 0f
)

// Add to layout
container.addView(filamentView)
```

### Activity Lifecycle

```kotlin
override fun onResume() {
    super.onResume()
    filamentSurfaceView.onResume()
}

override fun onPause() {
    filamentSurfaceView.onPause()
    super.onPause()
}

override fun onDestroy() {
    filamentSurfaceView.destroy()
    super.onDestroy()
}
```

### Custom Rendering

To create custom renderables:

```kotlin
val renderer = filamentView.getWorldRenderer()
val context = filamentView.getRenderContext()

// Create entity
val entity = context.entityManager.create()

// Build renderable
RenderableManager.Builder(1)
    .boundingBox(Box(...))
    .geometry(0, PrimitiveType.TRIANGLES, vertexBuffer, indexBuffer)
    .material(0, material.defaultInstance)
    .build(context.engine, entity)

// Add to scene
context.scene.addEntity(entity)
```

## Features

### Current Implementation
- ✅ Filament engine initialization
- ✅ Basic scene rendering
- ✅ Camera control (position, rotation)
- ✅ Test triangle rendering
- ✅ Surface lifecycle management
- ✅ Gesture controls (rotation, zoom)
- ✅ Touch input handling

### Planned Features
- ⏳ Material loading from assets
- ⏳ glTF model loading (using gltfio)
- ⏳ Terrain rendering
- ⏳ Avatar rendering
- ⏳ Dynamic lighting
- ⏳ Shadow mapping
- ⏳ Texture loading and management
- ⏳ Skybox/environment maps
- ⏳ Post-processing effects

## Filament Feature Level

The integration uses **FEATURE_LEVEL_1** for broad device compatibility:
- OpenGL ES 3.0+ support
- Basic physically-based rendering
- Post-processing disabled for compatibility

To use higher feature levels (FEATURE_LEVEL_2 or 3), update `FilamentRenderContext.initialize()`:
```kotlin
engine = Engine.Builder()
    .featureLevel(Engine.FeatureLevel.FEATURE_LEVEL_2)
    .build()
```

## Materials

### Current Approach
Currently using inline material creation (placeholder). This needs to be replaced with:

1. **Material Files**: Write `.mat` material definitions
2. **Compilation**: Use `matc` tool to compile to `.filamat`
3. **Loading**: Load `.filamat` files from assets

Example material workflow:
```bash
# Compile material
matc -o app/src/main/assets/materials/basic.filamat basic.mat

# Load in code
val materialData = assets.openFd("materials/basic.filamat").use { fd ->
    val input = fd.createInputStream()
    val buffer = ByteBuffer.allocate(fd.length.toInt())
    Channels.newChannel(input).read(buffer)
    buffer.flip()
    buffer
}

val material = Material.Builder()
    .payload(materialData, materialData.remaining())
    .build(engine)
```

## Performance Considerations

1. **Resource Management**: Always destroy Filament resources explicitly
2. **Frame Pacing**: Uses Choreographer for smooth 60fps rendering
3. **Memory**: Filament is memory-efficient but requires proper cleanup
4. **Threading**: All Filament calls must be on the same thread

## Debugging

Enable Filament logging:
```kotlin
// In FilamentRenderContext
Log.i(TAG, "Engine feature level: ${engine.activeFeatureLevel}")
Log.i(TAG, "Backend: ${engine.backend}")
```

Common issues:
- **Black screen**: Check material compilation and loading
- **Crash on destroy**: Ensure proper resource cleanup order
- **Performance**: Check for resource leaks, reduce draw calls

## Testing

Two test activities are provided:
1. **FilamentTestActivity**: Minimal test (just triangle)
2. **FilamentWorldViewActivity**: Full featured with controls

To launch test activity:
```bash
adb shell am start -n com.lumiyaviewer.lumiya/.ui.render.FilamentTestActivity
```

## References

- [Filament Documentation](https://google.github.io/filament/)
- [Filament GitHub](https://github.com/google/filament)
- [Filament Android Samples](https://github.com/google/filament/tree/main/android/samples)
- [Material Guide](https://google.github.io/filament/Materials.html)

## Migration from OpenGL ES

Key differences from legacy OpenGL renderer:
- **No GL calls**: Use Filament API instead of raw OpenGL
- **Entity-Component**: Objects are entities with components
- **Materials**: Precompiled materials instead of shaders
- **Automatic optimization**: Filament handles many optimizations

## Future Improvements

1. **Material System**: Implement proper material compilation and loading
2. **Asset Pipeline**: Set up build-time material compilation
3. **Model Loading**: Integrate glTF loader for 3D models
4. **Advanced Lighting**: Add IBL, shadows, and dynamic lights
5. **Performance Profiling**: Add metrics and optimization
6. **Texture Streaming**: Implement LOD and streaming for large textures

## Notes

- Filament source code is available in `/Filament/` but prebuilt binaries from Maven Central are used
- Building Filament from source requires CMake and significant build infrastructure
- The current implementation uses basic rendering; many advanced features are not yet utilized
