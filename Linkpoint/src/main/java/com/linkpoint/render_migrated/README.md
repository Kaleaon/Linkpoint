# 3D Rendering System (render)

## Overview

The rendering system is the heart of Linkpoint's 3D graphics engine, responsible for displaying the virtual world, avatars, objects, and effects in real-time. This module implements a modern, mobile-optimized rendering pipeline based on OpenGL ES.

## Architecture

### Core Rendering Pipeline

```
Frame Rendering Flow:
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Scene Graph   │ -> │  Frustum Culling │ -> │ Render Batching │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                        │                        │
         v                        v                        v
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│ Spatial Indexing│    │   LOD Selection  │    │  Draw Commands  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### Module Structure

```
render/
├── avatar/               # Avatar rendering and animation
│   ├── AvatarRenderer.java
│   ├── SkeletonCache.java
│   └── AnimationBlender.java
├── glres/               # OpenGL resource management
│   ├── TextureManager.java
│   ├── BufferManager.java
│   └── ShaderCache.java
├── programs/            # Shader programs
│   ├── PBRShader.java
│   ├── TerrainShader.java
│   └── ParticleShader.java
├── spatial/             # Spatial indexing and culling
│   ├── OctreeNode.java
│   ├── FrustumCuller.java
│   └── LODManager.java
├── terrain/             # Terrain rendering
│   ├── TerrainPatch.java
│   ├── HeightmapLoader.java
│   └── TerrainMeshGenerator.java
└── tex/                 # Texture management
    ├── TextureCache.java
    ├── BasisUniversalDecoder.java
    └── CompressionManager.java
```

## Key Features

### 🎨 **Modern Graphics Pipeline**
- **PBR (Physically Based Rendering)**: Realistic material system
- **HDR Rendering**: High dynamic range lighting
- **Deferred Shading**: Efficient multi-light rendering
- **Post-Processing Effects**: Bloom, tone mapping, FXAA

### 🏃 **Performance Optimizations**
- **Frustum Culling**: Only render visible objects
- **Occlusion Culling**: Skip objects behind others
- **LOD (Level of Detail)**: Adaptive quality based on distance
- **Render Batching**: Minimize draw calls

### 📱 **Mobile-Specific Features**
- **Adaptive Quality**: Dynamic quality adjustment
- **Thermal Management**: Prevent device overheating
- **Battery Optimization**: Efficient GPU usage
- **Memory Management**: Careful texture and buffer management

### 🌍 **Virtual World Rendering**
- **Terrain System**: Large-scale landscape rendering
- **Water Rendering**: Realistic water with reflections
- **Sky System**: Dynamic sky dome with day/night cycle
- **Particle Effects**: Smoke, fire, sparkles, and custom effects

## Rendering Components

### Avatar Rendering System

```java
public class AvatarRenderer {
    // High-performance avatar rendering with skinning
    public void renderAvatar(Avatar avatar, Matrix4f viewMatrix) {
        // Skeletal animation and mesh deformation
        SkeletonPose pose = animationBlender.blend(avatar.getAnimations());
        Mesh deformedMesh = skeletonCache.deformMesh(avatar.getMesh(), pose);
        
        // Render with PBR materials
        pbrShader.bind();
        pbrShader.setUniforms(avatar.getMaterials(), lighting);
        deformedMesh.render();
    }
}
```

### Terrain Rendering

```java
public class TerrainRenderer {
    // Large-scale terrain with dynamic LOD
    public void renderTerrain(Camera camera) {
        List<TerrainPatch> visiblePatches = spatialIndex.query(camera.getFrustum());
        
        for (TerrainPatch patch : visiblePatches) {
            int lodLevel = lodManager.calculateLOD(patch, camera);
            Mesh terrainMesh = patch.getMesh(lodLevel);
            
            terrainShader.bind();
            terrainShader.setTextures(patch.getTextures());
            terrainMesh.render();
        }
    }
}
```

### Texture Management

```java
public class ModernTextureManager {
    // Basis Universal texture support for optimal compression
    public Texture loadTexture(String assetPath) {
        if (assetPath.endsWith(".ktx2")) {
            return basisDecoder.decode(assetPath);
        }
        return legacyTextureLoader.load(assetPath);
    }
    
    // Automatic texture compression for mobile
    public void optimizeForDevice(DeviceCapabilities caps) {
        if (caps.supportsASTC()) {
            compressionFormat = TextureFormat.ASTC;
        } else if (caps.supportsETC2()) {
            compressionFormat = TextureFormat.ETC2;
        }
    }
}
```

## Shader System

### PBR (Physically Based Rendering)

The modern PBR pipeline provides realistic material rendering:

```glsl
// Fragment shader for PBR materials
#version 300 es
precision highp float;

in vec3 v_worldPos;
in vec3 v_normal;
in vec2 v_texCoord;

uniform sampler2D u_albedoMap;
uniform sampler2D u_normalMap;
uniform sampler2D u_metallicRoughnessMap;
uniform sampler2D u_emissiveMap;

uniform vec3 u_lightDirection;
uniform vec3 u_lightColor;
uniform vec3 u_cameraPos;

out vec4 fragColor;

vec3 calculatePBR(vec3 albedo, float metallic, float roughness, vec3 normal, vec3 lightDir, vec3 viewDir) {
    // Cook-Torrance BRDF implementation
    // ... PBR lighting calculations
}

void main() {
    vec3 albedo = texture(u_albedoMap, v_texCoord).rgb;
    vec3 normal = normalize(texture(u_normalMap, v_texCoord).rgb * 2.0 - 1.0);
    vec2 metallicRoughness = texture(u_metallicRoughnessMap, v_texCoord).rg;
    vec3 emissive = texture(u_emissiveMap, v_texCoord).rgb;
    
    vec3 color = calculatePBR(albedo, metallicRoughness.r, metallicRoughness.g, normal, u_lightDirection, normalize(u_cameraPos - v_worldPos));
    color += emissive;
    
    fragColor = vec4(color, 1.0);
}
```

### Terrain Shaders

```glsl
// Multi-texture terrain blending
#version 300 es
precision mediump float;

in vec2 v_texCoord;
in vec3 v_worldPos;
in vec3 v_normal;

uniform sampler2D u_texture0;  // Grass
uniform sampler2D u_texture1;  // Rock
uniform sampler2D u_texture2;  // Sand
uniform sampler2D u_texture3;  // Snow
uniform sampler2D u_blendMap;  // RGBA blend weights

out vec4 fragColor;

void main() {
    vec4 blendWeights = texture(u_blendMap, v_texCoord);
    
    vec3 grass = texture(u_texture0, v_texCoord * 32.0).rgb;
    vec3 rock = texture(u_texture1, v_texCoord * 16.0).rgb;
    vec3 sand = texture(u_texture2, v_texCoord * 24.0).rgb;
    vec3 snow = texture(u_texture3, v_texCoord * 8.0).rgb;
    
    vec3 color = grass * blendWeights.r +
                 rock * blendWeights.g +
                 sand * blendWeights.b +
                 snow * blendWeights.a;
    
    fragColor = vec4(color, 1.0);
}
```

## Performance Features

### Adaptive Quality System

```java
public class AdaptiveQualityManager {
    private float currentFPS;
    private int targetFPS = 30;
    private QualityLevel currentQuality = QualityLevel.MEDIUM;
    
    public void updateQuality() {
        if (currentFPS < targetFPS - 5) {
            reduceQuality();
        } else if (currentFPS > targetFPS + 10) {
            increaseQuality();
        }
    }
    
    private void reduceQuality() {
        switch (currentQuality) {
            case HIGH:
                // Reduce shadow resolution, disable post-processing
                shadowManager.setResolution(512);
                postProcessor.setEnabled(false);
                currentQuality = QualityLevel.MEDIUM;
                break;
            case MEDIUM:
                // Reduce draw distance, simplify shaders
                camera.setDrawDistance(128.0f);
                shaderManager.useSimplifiedShaders(true);
                currentQuality = QualityLevel.LOW;
                break;
        }
    }
}
```

### Memory Management

```java
public class RenderMemoryManager {
    private static final int MAX_TEXTURE_MEMORY = 256 * 1024 * 1024; // 256MB
    private static final int MAX_VERTEX_BUFFER_MEMORY = 64 * 1024 * 1024; // 64MB
    
    public void cleanupUnusedResources() {
        textureCache.cleanup();
        meshCache.cleanup();
        shaderCache.cleanup();
        
        if (getCurrentMemoryUsage() > MAX_TEXTURE_MEMORY * 0.9f) {
            textureCache.forceCleanup();
        }
    }
}
```

## Spatial Systems

### Octree Spatial Indexing

```java
public class OctreeNode {
    private static final int MAX_OBJECTS_PER_NODE = 10;
    private static final int MAX_DEPTH = 6;
    
    private BoundingBox bounds;
    private List<RenderObject> objects;
    private OctreeNode[] children;
    
    public List<RenderObject> query(Frustum frustum) {
        if (!frustum.intersects(bounds)) {
            return Collections.emptyList();
        }
        
        List<RenderObject> result = new ArrayList<>();
        
        // Add objects in this node
        for (RenderObject obj : objects) {
            if (frustum.contains(obj.getBounds())) {
                result.add(obj);
            }
        }
        
        // Query children
        if (children != null) {
            for (OctreeNode child : children) {
                result.addAll(child.query(frustum));
            }
        }
        
        return result;
    }
}
```

### Level of Detail (LOD)

```java
public class LODManager {
    private static final float[] LOD_DISTANCES = {25.0f, 50.0f, 100.0f, 200.0f};
    
    public int calculateLOD(RenderObject object, Camera camera) {
        float distance = Vector3f.distance(object.getPosition(), camera.getPosition());
        
        for (int i = 0; i < LOD_DISTANCES.length; i++) {
            if (distance < LOD_DISTANCES[i]) {
                return i;
            }
        }
        
        return LOD_DISTANCES.length; // Lowest detail
    }
}
```

## Lighting System

### Dynamic Lighting

```java
public class LightingSystem {
    private List<Light> lights = new ArrayList<>();
    private ShadowMapper shadowMapper;
    
    public void renderWithLighting(List<RenderObject> objects, Camera camera) {
        // Sort lights by importance
        lights.sort((a, b) -> Float.compare(b.getIntensity(), a.getIntensity()));
        
        // Render shadow maps for primary lights
        for (int i = 0; i < Math.min(lights.size(), 4); i++) {
            Light light = lights.get(i);
            if (light.castsShadows()) {
                shadowMapper.renderShadowMap(light, objects);
            }
        }
        
        // Forward rendering with lights
        for (RenderObject obj : objects) {
            renderObjectWithLights(obj, lights, camera);
        }
    }
}
```

## Post-Processing Effects

```java
public class PostProcessor {
    private FrameBuffer hdrBuffer;
    private FrameBuffer bloomBuffer;
    private ToneMappingShader toneMappingShader;
    private BloomShader bloomShader;
    
    public void process(Texture sceneTexture) {
        // HDR to LDR tone mapping
        hdrBuffer.bind();
        toneMappingShader.render(sceneTexture);
        
        // Bloom effect
        bloomBuffer.bind();
        bloomShader.render(hdrBuffer.getColorTexture());
        
        // Final composite
        screenQuad.render(bloomBuffer.getColorTexture());
    }
}
```

## Configuration

### Rendering Settings
```xml
<resources>
    <!-- Quality Settings -->
    <string name="default_quality_level">medium</string>
    <bool name="enable_pbr_rendering">true</bool>
    <bool name="enable_shadows">true</bool>
    <bool name="enable_post_processing">true</bool>
    
    <!-- Performance Settings -->
    <integer name="target_fps">30</integer>
    <integer name="max_draw_distance">256</integer>
    <integer name="max_visible_objects">1000</integer>
    
    <!-- Memory Settings -->
    <integer name="texture_memory_limit_mb">256</integer>
    <integer name="vertex_buffer_limit_mb">64</integer>
</resources>
```

### Device-Specific Optimizations
```java
DeviceCapabilities caps = DeviceCapabilities.detect();
if (caps.isLowEndDevice()) {
    renderer.setQualityLevel(QualityLevel.LOW);
    renderer.setDrawDistance(64.0f);
} else if (caps.isHighEndDevice()) {
    renderer.setQualityLevel(QualityLevel.HIGH);
    renderer.enableAdvancedEffects(true);
}
```

## Testing and Profiling

### Performance Metrics
- **Frame Time**: Target 33ms for 30 FPS
- **Draw Calls**: Minimize to reduce CPU overhead
- **Memory Usage**: Monitor texture and buffer memory
- **Thermal Impact**: Prevent device overheating

### Debugging Tools
```java
public class RenderDebugger {
    public void drawWireframe(boolean enabled);
    public void showBoundingBoxes(boolean enabled);
    public void displayFrameStats(boolean enabled);
    public void showLightInfluence(boolean enabled);
}
```

## Future Enhancements

### 🚀 **Planned Features**
- **Vulkan Backend**: Next-generation graphics API
- **Ray Tracing**: Hardware-accelerated ray tracing
- **Variable Rate Shading**: Optimize rendering performance
- **Mesh Shaders**: GPU-driven geometry processing

### 🔬 **Research Areas**
- **AI-Assisted LOD**: Machine learning for optimal detail levels
- **Neural Rendering**: AI-enhanced rendering techniques
- **Procedural Generation**: GPU-based procedural content
- **XR Integration**: Extended reality rendering pipeline

## Contributing

### 📝 **Development Guidelines**
- Profile all performance-critical changes
- Test on multiple device configurations
- Maintain backward compatibility with older GPUs
- Document shader modifications thoroughly
- Consider mobile-specific constraints

### 🧪 **Testing Requirements**
- Visual regression testing for shader changes
- Performance benchmarking on target devices
- Memory leak detection and profiling
- Battery usage analysis for new features

## Related Documentation

- [Graphics Engine Roadmap](../../../../../../../docs/Graphics_Engine_Roadmap.md)
- [Basis Universal Integration](../../../../../../../docs/Basis_Universal_Integration.md)
- [CPP Integration Guide](../../../../../../../docs/CPP_Integration_Guide.md)
- [API Analysis and Improvements](../../../../../../../docs/API_Analysis_and_Improvements.md)