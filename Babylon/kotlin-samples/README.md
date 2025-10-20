# Babylon Kotlin - Sample Code

This directory contains comprehensive sample code demonstrating how to implement a Kotlin version of Babylon.js for mobile and desktop platforms.

## Overview

The sample code demonstrates:

1. **Core Math Library** - Vector3, Matrix4x4, Quaternion with operator overloading
2. **Engine Architecture** - Main engine class with render loop and scene management
3. **Scene Management** - Scene graph, node hierarchy, and object management
4. **Mesh System** - Geometry, vertex data, bounding volumes, and instancing
5. **Camera System** - Multiple camera types (Free, ArcRotate, Follow, Universal)
6. **Material System** - Standard, PBR, Shader, and Node materials
7. **Graphics Abstraction** - Platform-agnostic graphics device interface
8. **Android Integration** - Complete Android example with GLSurfaceView

## File Structure

```
kotlin-samples/
├── Vector3.kt              # 3D vector math with operators
├── Matrix4x4.kt            # 4x4 transformation matrices
├── Quaternion.kt           # Quaternion rotations
├── Engine.kt               # Main engine class
├── Scene.kt                # Scene management
├── Mesh.kt                 # Mesh and geometry
├── Camera.kt               # Camera implementations
├── Material.kt             # Material system
├── GraphicsDevice.kt       # Graphics abstraction layer
├── AndroidExample.kt       # Android integration example
└── README.md               # This file
```

## Key Features Demonstrated

### 1. Kotlin Idioms

**Data Classes for Value Objects:**
```kotlin
data class Vector3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
}
```

**Extension Functions:**
```kotlin
fun Mesh.setPosition(x: Float, y: Float, z: Float) {
    position = Vector3(x, y, z)
}
```

**Sealed Classes for State:**
```kotlin
sealed class LoadingState {
    object NotStarted : LoadingState()
    data class Loading(val progress: Float) : LoadingState()
    data class Loaded(val asset: Asset) : LoadingState()
}
```

**Coroutines for Async Operations:**
```kotlin
suspend fun Scene.loadModelAsync(url: String): Mesh = withContext(Dispatchers.IO) {
    val data = downloadModel(url)
    withContext(Dispatchers.Main) {
        parseAndCreateMesh(data)
    }
}
```

### 2. DSL for Scene Construction

```kotlin
val myScene = scene(engine) {
    camera("camera1") {
        position = Vector3(0f, 5f, -10f)
        setTarget(Vector3.Zero())
    }
    
    light("light1") {
        intensity = 0.7f
    }
    
    box("box1") {
        position = Vector3(0f, 1f, 0f)
        material = StandardMaterial("boxMat", this@scene).apply {
            diffuseColor = Color3.Red()
        }
    }
}
```

### 3. Type-Safe Builder Pattern

```kotlin
val engine = engine(graphicsDevice) {
    setResolution(1920, 1080)
    setAntialias(true)
    setTargetFPS(60)
    setPowerPreference(PowerPreference.HIGH_PERFORMANCE)
}
```

### 4. Platform Abstraction

The `GraphicsDevice` interface provides a clean abstraction over different graphics APIs:

- **Android**: OpenGL ES 3.0+
- **iOS**: Metal (via Kotlin/Native)
- **Desktop**: OpenGL 4.x (via LWJGL)
- **Future**: Vulkan, WebGPU

### 5. Performance Optimizations

**Inline Value Classes:**
```kotlin
@JvmInline
value class TextureHandle(val id: Int) // Zero runtime overhead
```

**Object Pooling:**
```kotlin
class Vector3Pool(initialSize: Int = 100) {
    fun obtain(): Vector3
    fun free(vector: Vector3)
}
```

**Frustum Culling:**
```kotlin
val visibleMeshes = meshes.filter { mesh ->
    mesh.isVisible && isInFrustum(mesh, camera)
}
```

## Usage Examples

### Basic Scene Setup

```kotlin
// Create graphics device (platform-specific)
val graphicsDevice = OpenGLESGraphicsDevice()

// Create engine
val engine = Engine(
    EngineConfiguration(
        width = 1920,
        height = 1080,
        antialias = true,
        targetFPS = 60
    ),
    graphicsDevice
)

// Create scene
val scene = engine.createScene()

// Add camera
val camera = FreeCamera("camera", Vector3(0f, 5f, -10f), scene)
camera.setTarget(Vector3.Zero())
scene.addCamera(camera)

// Add light
val light = HemisphericLight("light", Vector3.Up(), scene)
scene.addLight(light)

// Create mesh
val box = Mesh("box", scene)
box.position = Vector3(0f, 1f, 0f)
box.material = StandardMaterial("boxMat", scene).apply {
    diffuseColor = Color3(1f, 0f, 0f)
}
scene.addMesh(box)

// Start render loop
engine.runRenderLoop {
    scene.render()
}
```

### Android Integration

```kotlin
class BabylonGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: BabylonRenderer
    
    init {
        setEGLContextClientVersion(3)
        renderer = BabylonRenderer(context)
        setRenderer(renderer)
    }
}

class BabylonRenderer(context: Context) : GLSurfaceView.Renderer {
    private lateinit var engine: Engine
    private lateinit var scene: Scene
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val graphicsDevice = OpenGLESGraphicsDevice()
        engine = Engine(config, graphicsDevice)
        scene = createScene(engine)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        scene.render()
    }
}
```

### PBR Material Example

```kotlin
val pbrMaterial = PBRMaterial("metal", scene).apply {
    albedoColor = Color3(0.8f, 0.8f, 0.8f)
    metallic = 1.0f
    roughness = 0.3f
    
    // Load textures
    albedoTexture = scene.loadTextureAsync("textures/metal_albedo.png")
    normalTexture = scene.loadTextureAsync("textures/metal_normal.png")
    metallicRoughnessTexture = scene.loadTextureAsync("textures/metal_mr.png")
}

mesh.material = pbrMaterial
```

### Animation Example

```kotlin
// Rotate mesh continuously
engine.runRenderLoop {
    val deltaTime = engine.getDeltaTime()
    mesh.rotation.y += deltaTime * 1f // 1 radian per second
    scene.render()
}
```

### Camera Control Example

```kotlin
// Arc rotate camera with mouse control
val camera = ArcRotateCamera(
    name = "camera",
    alpha = 0f,
    beta = PI.toFloat() / 4f,
    radius = 10f,
    target = Vector3.Zero(),
    scene = scene
)

// Handle mouse drag
fun onMouseDrag(deltaX: Float, deltaY: Float) {
    camera.rotate(
        deltaX / camera.angularSensibilityX,
        deltaY / camera.angularSensibilityY
    )
}

// Handle mouse wheel
fun onMouseWheel(delta: Float) {
    camera.zoom(delta / camera.wheelPrecision)
}
```

### Async Asset Loading

```kotlin
lifecycleScope.launch {
    try {
        // Load model asynchronously
        val model = scene.loadModelAsync("models/character.glb")
        model.position = Vector3(0f, 0f, 0f)
        
        // Load texture asynchronously
        val texture = scene.loadTextureAsync("textures/character.png")
        model.material?.diffuseTexture = texture
        
        println("Assets loaded successfully")
    } catch (e: Exception) {
        println("Error loading assets: ${e.message}")
    }
}
```

### Picking Example

```kotlin
// Handle touch/click events
fun onTouch(x: Int, y: Int) {
    val pickingInfo = scene.pick(x, y)
    
    if (pickingInfo?.hit == true) {
        val pickedMesh = pickingInfo.pickedMesh
        println("Picked mesh: ${pickedMesh?.name}")
        println("Hit point: ${pickingInfo.pickedPoint}")
        
        // Change color on pick
        (pickedMesh?.material as? StandardMaterial)?.apply {
            diffuseColor = Color3(1f, 1f, 0f) // Yellow
        }
    }
}
```

## Comparison with Babylon.js

### JavaScript (Babylon.js)
```javascript
const engine = new BABYLON.Engine(canvas, true);
const scene = new BABYLON.Scene(engine);

const camera = new BABYLON.FreeCamera("camera", new BABYLON.Vector3(0, 5, -10), scene);
camera.setTarget(BABYLON.Vector3.Zero());

const light = new BABYLON.HemisphericLight("light", new BABYLON.Vector3(0, 1, 0), scene);

const box = BABYLON.MeshBuilder.CreateBox("box", {}, scene);
box.position.y = 1;

engine.runRenderLoop(() => {
    scene.render();
});
```

### Kotlin (Babylon Kotlin)
```kotlin
val engine = engine(graphicsDevice) {
    setAntialias(true)
}
val scene = engine.createScene()

val camera = FreeCamera("camera", Vector3(0f, 5f, -10f), scene)
camera.setTarget(Vector3.Zero())

val light = HemisphericLight("light", Vector3.Up(), scene)

val box = MeshBuilder.createBox("box", scene = scene)
box.position = Vector3(0f, 1f, 0f)

engine.runRenderLoop {
    scene.render()
}
```

## Benefits of Kotlin Implementation

1. **Type Safety**: Compile-time type checking prevents many runtime errors
2. **Null Safety**: Eliminates null pointer exceptions
3. **Coroutines**: Clean async/await syntax without callback hell
4. **Data Classes**: Immutable value objects with structural equality
5. **Extension Functions**: Add functionality without inheritance
6. **Operator Overloading**: Natural mathematical expressions
7. **Multiplatform**: Share code between Android, iOS, and Desktop
8. **Performance**: Native compilation on mobile platforms
9. **Modern Language**: Latest language features and best practices
10. **Interoperability**: Easy integration with existing Kotlin/Java code

## Next Steps

To implement a full Kotlin Babylon engine:

1. **Complete Math Library**: Add more vector/matrix operations
2. **Implement Graphics Backends**: OpenGL ES, Metal, Vulkan
3. **Asset Loaders**: glTF, OBJ, FBX parsers
4. **Physics Integration**: Bullet Physics or custom engine
5. **Animation System**: Skeletal animation, blend trees
6. **Particle Systems**: GPU-accelerated particles
7. **Post-Processing**: Bloom, DOF, SSAO, etc.
8. **GUI System**: 2D UI overlay
9. **Audio System**: 3D spatial audio
10. **Networking**: Multiplayer support

## Resources

- [Babylon.js Documentation](https://doc.babylonjs.com/)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [OpenGL ES 3.0 Specification](https://www.khronos.org/opengles/)
- [Metal Programming Guide](https://developer.apple.com/metal/)
- [LWJGL (Desktop OpenGL)](https://www.lwjgl.org/)

## License

This sample code is provided as a reference implementation for educational purposes.