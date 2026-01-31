# Lumiya to Filament Renderer Architecture Guide

This document explains how Lumiya Viewer's OpenGL ES 2.0/3.0 renderer architecture maps to Linkpoint's Google Filament-based renderer, helping developers understand the translation between these two rendering systems.

## Overview

| Lumiya (OpenGL ES) | Linkpoint (Filament) | Notes |
|-------------------|---------------------|-------|
| `WorldViewRenderer` | `RenderManager` | Main rendering orchestrator |
| `RenderContext` | `Engine` + `Scene` + `View` | Rendering state management |
| `DrawableObject` | `PrimInstance` | Per-object rendering data |
| `DrawablePrim` | `PrimRenderer` | Primitive geometry handling |
| `DrawableGeometry` | `VertexBuffer` + `IndexBuffer` | GPU geometry buffers |
| `PrimProgram` / `BasicPrimProgram` | `Material` + `MaterialInstance` | Shader programs |
| `GLLoadedTexture` | `Texture` | Texture management |

## Architecture Comparison

### Lumiya's Rendering Pipeline

```
WorldViewRenderer (GLSurfaceView.Renderer)
    └── RenderContext
        ├── PrimProgram (GLSL shaders)
        ├── AvatarProgram
        ├── SkyProgram
        ├── WaterProgram
        └── DrawableStore
            └── DrawableObject
                ├── DrawablePrim
                │   ├── DrawableGeometry (vertex/index buffers)
                │   └── DrawableFaceTexture[]
                └── DrawableHoverText
```

### Linkpoint's Rendering Pipeline

```
RenderManager
    ├── Engine (Filament core)
    ├── Scene
    ├── View + Camera
    ├── Renderer
    ├── MaterialLoader
    │   ├── Material (compiled .filamat)
    │   └── MaterialInstance[]
    ├── PrimRenderer
    │   ├── PrimMesh (VertexBuffer + IndexBuffer)
    │   └── PrimInstance[]
    ├── SceneManager
    ├── TerrainRenderer
    ├── WaterRenderer
    └── SkyRenderer
```

## Key Component Mappings

### 1. Geometry Management

**Lumiya's DrawableGeometry:**
```java
// Lumiya uses DirectByteBuffer for efficient native memory access
public final class DrawableGeometry {
    private final GLLoadableBuffer VertexBuffer;  // position + normal
    private final GLLoadableBuffer TexCoordsBuffer;
    private final GLLoadableBuffer IndexBuffer;
    private final int VertexSizeBytes;  // vertices * 4 * 6 (floats: x,y,z,nx,ny,nz)
    private final int IndexSizeBytes;   // indices * 2 (shorts)
}
```

**Linkpoint's PrimMesh (Filament):**
```kotlin
// Filament uses structured VertexBuffer with declared attributes
data class PrimMesh(
    val vertexBuffer: VertexBuffer,  // Combined: position + normal + texcoord
    val indexBuffer: IndexBuffer
)

// Vertex layout: x,y,z (position) + nx,ny,nz (normal) + u,v (texcoord) = 8 floats
val vertexBuffer = VertexBuffer.Builder()
    .vertexCount(vertexCount)
    .bufferCount(1)
    .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, 32)
    .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT3, 12, 32)
    .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 24, 32)
    .build(engine)
```

### 2. Shader/Material System

**Lumiya's Shader Programs:**
```java
// Custom GLSL shaders compiled at runtime
public class BasicPrimProgram extends ShaderProgram {
    public int LightAmbientColor;
    public int LightDiffuseColor;
    public int LightDiffuseDir;
    public int uMVPMatrix;
    public int uObjWorldMatrix;
    // Uses GLES20.glGetUniformLocation() for shader uniforms
}
```

**Linkpoint's Materials (Filament):**
```kotlin
// Filament uses MaterialBuilder for runtime compilation
// or pre-compiled .filamat files
class MaterialLoader(context: Context, engine: Engine) {
    // Material source (GLSL-like but Filament-specific)
    private const val LIT_MATERIAL_SOURCE = """
        material {
            name : LitDefault,
            shadingModel : lit,
            parameters : [
                { type : float4, name : baseColor },
                { type : float, name : metallic },
                { type : float, name : roughness }
            ]
        }
        fragment {
            void material(inout MaterialInputs material) {
                prepareMaterial(material);
                material.baseColor = materialParams.baseColor;
                material.metallic = materialParams.metallic;
                material.roughness = materialParams.roughness;
            }
        }
    """
}
```

### 3. Object Transform Management

**Lumiya:**
```java
// Direct OpenGL matrix operations
renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
renderContext.glPushObjectScale(scaleX, scaleY, scaleZ);
// ... render ...
renderContext.glPopObjectScale();
renderContext.glObjWorldPopMatrix();
```

**Linkpoint (Filament):**
```kotlin
// TransformManager handles scene graph transforms
val transformManager = engine.transformManager
val ti = transformManager.create(entity)

// Set transform matrix directly
transformManager.setTransform(ti, transformMatrix)

// Or use helper method
private fun updateTransform(prim: PrimInstance) {
    val matrix = FloatArray(16)
    android.opengl.Matrix.setIdentityM(matrix, 0)
    android.opengl.Matrix.translateM(matrix, 0, 
        prim.position.x, prim.position.y, prim.position.z)
    // Apply rotation quaternion and scale...
    transformManager.setTransform(prim.transformInstance, matrix)
}
```

### 4. Texture Entry Parsing

Both systems need to parse SL's TextureEntry format (little-endian):

**Lumiya:**
```java
// SLTextureEntry.java parses the binary format
public class SLTextureEntry {
    private SLTextureEntryFace defaultTexture;
    private SLTextureEntryFace[] faces;
    // Parses UUID (big-endian), color, scale, offset, rotation (all little-endian)
}
```

**Linkpoint:**
```kotlin
// MessageParser.kt and PrimRenderer.kt handle texture entry
private fun updatePrimMaterial(prim: PrimInstance, textureEntry: ByteArray) {
    if (textureEntry.isEmpty()) return
    try {
        val buffer = ByteBuffer.wrap(textureEntry)
            .order(ByteOrder.LITTLE_ENDIAN)
        
        // Extract default texture UUID (first 16 bytes, big-endian)
        val uuidBytes = ByteArray(16)
        buffer.get(uuidBytes)
        val textureId = bytesToUUID(uuidBytes)  // Uses BIG_ENDIAN
        // ...
    }
}

private fun bytesToUUID(bytes: ByteArray): UUID {
    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
    return UUID(buffer.long, buffer.long)
}
```

## Endianness Reference

**Critical for protocol compatibility:**

| Data Type | Byte Order | Context |
|-----------|-----------|---------|
| UUIDs | BIG_ENDIAN | All UUID serialization |
| Packet headers (flags, seq#) | BIG_ENDIAN | UDP packet structure |
| Message payload (ints, floats) | LITTLE_ENDIAN | All SL message data |
| Texture coordinates | LITTLE_ENDIAN | In TextureEntry |
| Vector3/Quaternion | LITTLE_ENDIAN | Position, rotation data |
| Rendering buffers | nativeOrder() | GPU-bound data |

## Migration Considerations

### From Lumiya to Filament

1. **Shader Translation**: Lumiya's GLSL shaders must be converted to Filament's material format
2. **Buffer Layout**: Lumiya separates vertex/normal/texcoord buffers; Filament prefers interleaved
3. **State Management**: Lumiya uses explicit GL state; Filament uses declarative materials
4. **Transform Stack**: Lumiya uses matrix stack; Filament uses TransformManager hierarchy

### Performance Notes

- Filament handles instancing better for repeated geometry
- Filament's PBR materials are more expensive but higher quality
- MaterialBuilder.build() should be cached; don't compile at runtime repeatedly
- Use pre-compiled `.filamat` files for production

## API Quick Reference

### Creating a Renderable Entity

```kotlin
// Filament pattern
val entity = EntityManager.get().create()

RenderableManager.Builder(1)
    .boundingBox(Box(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f))
    .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, 
              vertexBuffer, indexBuffer)
    .material(0, materialInstance)
    .culling(true)
    .receiveShadows(true)
    .castShadows(true)
    .build(engine, entity)

scene.addEntity(entity)
```

### Setting Material Parameters

```kotlin
// Filament MaterialInstance
materialInstance.setParameter("baseColor", 
    Colors.RgbaType.SRGB, 1.0f, 1.0f, 1.0f, 1.0f)
materialInstance.setParameter("metallic", 0.0f)
materialInstance.setParameter("roughness", 0.5f)
```

## See Also

- [Graphics_Engine_Modernization_Plan.md](Graphics_Engine_Modernization_Plan.md)
- [Lumiya_Modernization_Guide.md](Lumiya_Modernization_Guide.md)
- [Filament Documentation](https://google.github.io/filament/)
