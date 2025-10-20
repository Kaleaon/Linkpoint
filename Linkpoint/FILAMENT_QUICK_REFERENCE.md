# Filament Integration - Quick Reference

## 🚀 Quick Start

### Basic Setup
```kotlin
// 1. Create Filament view
val filamentView = FilamentSurfaceView(context)
filamentView.initializeWorldRenderer()
setContentView(filamentView)

// 2. Get world renderer
val worldRenderer = filamentView.getWorldRenderer()

// 3. Set camera position
worldRenderer?.setCameraPosition(
    LLVector3(128f, 128f, 50f),
    0f, 0f
)
```

## 📍 Key Components

### Terrain Rendering
```kotlin
// Load terrain from TerrainData
worldRenderer?.loadTerrain(terrainData)

// Terrain automatically reads heightmaps
// Creates 16x16 patches
// Renders with proper textures
```

### Object Rendering
```kotlin
// Objects automatically use correct geometry
worldRenderer?.connectToWorldData(
    objectsManager = objectsManager
)

// Or add manually
worldRenderer?.worldDataBridge?.addObject(localID, objectInfo)

// Supported shapes:
// - Box (cube)
// - Cylinder (with caps)
// - Sphere
// - Torus (donut)
// - Prism (triangular)
```

### Avatar Rendering
```kotlin
// Avatars render as humanoid figures
worldRenderer?.connectToWorldData(
    userManager = userManager
)

// Features:
// - 6-part body (head, torso, arms, legs)
// - 1.8m height
// - Proper proportions
```

### Lighting
```kotlin
val lighting = worldRenderer?.getLightingManager()

// Set sun
lighting?.updateSunLight(
    direction = LLVector3(0.3f, -0.7f, -0.6f),
    color = LLVector3(1.0f, 0.98f, 0.9f),
    intensity = 100000f
)

// Add point light
lighting?.createPointLight(
    position = LLVector3(128f, 128f, 25f),
    color = LLVector3(1.0f, 0.8f, 0.6f),
    intensity = 10000f
)

// Configure windlight
lighting?.setWindlightSettings(
    sunAngle = PI.toFloat() / 4f,
    sunColor = LLVector3(1f, 0.98f, 0.9f),
    ambientColor = LLVector3(0.3f, 0.3f, 0.4f)
)
```

### Textures
```kotlin
val textures = worldRenderer?.getTextureManager()

// Set texture fetcher
textures?.setTextureFetcher(textureFetcher)

// Load texture (async)
textures?.loadTexture(uuid) { texture ->
    // Texture loaded
}

// Load texture (sync, cache only)
val texture = textures?.loadTextureSync(uuid)
```

## 🎯 Common Tasks

### Connect to Second Life Region
```kotlin
// 1. Get world renderer
val worldRenderer = filamentView.getWorldRenderer()

// 2. Connect data sources
worldRenderer?.connectToWorldData(
    objectsManager = objectsManager,
    userManager = userManager,
    terrainData = terrainData
)

// 3. Set texture fetcher
worldRenderer?.getTextureManager()?.setTextureFetcher(textureFetcher)

// 4. Configure lighting
worldRenderer?.getLightingManager()?.setWindlightSettings(...)

// 5. Set camera
worldRenderer?.setCameraPosition(startPosition, 0f, 0f)

// Done! Scene renders automatically
```

### Handle Object Updates
```kotlin
// Objects update automatically via ObjectsManager
// Or update manually:
worldRenderer?.worldDataBridge?.addObject(localID, objectInfo)
worldRenderer?.worldDataBridge?.removeObject(localID)
```

### Handle Avatar Updates
```kotlin
// Avatars update automatically via UserManager
// Or update manually:
worldRenderer?.getAvatarRenderer()?.updateAvatar(avatarInfo)
worldRenderer?.getAvatarRenderer()?.removeAvatar(uuid)
```

### Performance Tuning
```kotlin
val optimizer = worldRenderer?.getPerformanceOptimizer()

// Get stats
val stats = optimizer?.getStats()
Log.d(TAG, stats)

// Adjust LOD distances (in FilamentPerformanceOptimizer.kt)
// LOD_HIGH_DISTANCE = 32f
// LOD_MEDIUM_DISTANCE = 64f
// LOD_LOW_DISTANCE = 128f
// CULL_DISTANCE = 256f
```

## 🎨 Geometry Details

### Prim Types Supported
```kotlin
// Automatically detected from PrimVolumeParams

Box:       ■  6 faces, proper UVs
Cylinder:  ●  16 slices, with caps
Sphere:    ○  Generated as cylinder
Torus:     ◐  12x16 resolution
Prism:     ▲  Triangular profile

Fallback:  □  Simple cube for unknown types
```

### Avatar Structure
```kotlin
Humanoid Avatar:
├── Head (0.2m cube)
├── Torso (0.4m x 0.72m box)
├── Left Arm (0.12m x 0.63m)
├── Right Arm (0.12m x 0.63m)
├── Left Leg (0.14m x 0.72m)
└── Right Leg (0.14m x 0.72m)

Total Height: 1.8m (standard avatar)
Total Width: 0.4m
```

## 📊 Performance Guidelines

### Recommended Limits
```
Terrain Patches: 256 (16x16 grid) ✅
Objects: Up to 1000 ✅
Avatars: Up to 100 ✅
Point Lights: Up to 8 ✅
Texture Size: 2048x2048 max
Draw Calls: ~500-1000 typical
Frame Rate: 45-60 FPS target
```

### Optimization Tips
```kotlin
// 1. Enable frustum culling (automatic)
// 2. Use LOD system (automatic)
// 3. Limit visible distance to 256m (default)
// 4. Batch similar materials (automatic)
// 5. Use texture caching (automatic)
```

## 🐛 Troubleshooting

### No Objects Visible
```kotlin
// Check:
1. Is world data connected?
   worldRenderer?.connectToWorldData(...)

2. Is camera positioned correctly?
   worldRenderer?.setCameraPosition(...)

3. Are objects in view?
   Check cull distance (256m default)

4. Check logs:
   adb logcat -s FilamentWorldRenderer
```

### Textures Not Loading
```kotlin
// Check:
1. Is texture fetcher set?
   textureManager?.setTextureFetcher(fetcher)

2. Are textures cached?
   Check cache directory

3. Check logs:
   adb logcat -s FilamentTextureMgr
```

### Low Performance
```kotlin
// Check:
1. Too many objects?
   Use frustum culling and LOD

2. Too many lights?
   Limit to 8 point lights

3. High resolution textures?
   Limit to 2048x2048

4. Check stats:
   val stats = optimizer?.getStats()
```

## 📖 API Reference

### FilamentWorldRenderer
```kotlin
fun initialize()
fun connectToWorldData(objectsManager, userManager, terrainData)
fun loadTerrain(terrainData)
fun setCameraPosition(position, rotX, rotY)
fun render(deltaTime)
fun destroy()

// Getters
fun getTextureManager(): FilamentTextureManager
fun getMaterialManager(): FilamentMaterialManager
fun getLightingManager(): FilamentLightingManager
fun getAvatarRenderer(): FilamentAvatarRenderer
fun getPerformanceOptimizer(): FilamentPerformanceOptimizer
```

### FilamentTextureManager
```kotlin
fun setTextureFetcher(fetcher)
fun loadTexture(uuid, onLoaded)
fun loadTextureSync(uuid): Texture?
fun createTextureFromBitmap(bitmap, srgb): Texture
fun createPlaceholderTexture(): Texture
fun destroy()
```

### FilamentLightingManager
```kotlin
fun updateSunLight(direction, color, intensity)
fun createPointLight(position, color, intensity, radius)
fun removePointLight(entity)
fun setWindlightSettings(sunAngle, sunColor, ambientColor)
fun setShadowsEnabled(enabled)
fun destroy()
```

## 🎯 Example: Complete Setup

```kotlin
class WorldActivity : AppCompatActivity() {
    private lateinit var filamentView: FilamentSurfaceView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Create Filament view
        filamentView = FilamentSurfaceView(this)
        filamentView.initializeWorldRenderer()
        setContentView(filamentView)
        
        // 2. Get world renderer
        val worldRenderer = filamentView.getWorldRenderer()
        
        // 3. Connect world data
        worldRenderer?.connectToWorldData(
            objectsManager = getObjectsManager(),
            userManager = getUserManager(),
            terrainData = getTerrainData()
        )
        
        // 4. Set texture fetcher
        worldRenderer?.getTextureManager()
            ?.setTextureFetcher(getTextureFetcher())
        
        // 5. Configure lighting
        worldRenderer?.getLightingManager()?.apply {
            updateSunLight(
                LLVector3(0.3f, -0.7f, -0.6f),
                LLVector3(1.0f, 0.98f, 0.9f),
                100000f
            )
            
            createPointLight(
                LLVector3(128f, 128f, 25f),
                LLVector3(1.0f, 0.8f, 0.6f),
                10000f
            )
        }
        
        // 6. Set camera
        worldRenderer?.setCameraPosition(
            LLVector3(128f, 128f, 50f),
            0f, 0f
        )
        
        // Done! Everything renders automatically
    }
    
    override fun onResume() {
        super.onResume()
        filamentView.onResume()
    }
    
    override fun onPause() {
        filamentView.onPause()
        super.onPause()
    }
    
    override fun onDestroy() {
        filamentView.destroy()
        super.onDestroy()
    }
}
```

## 📝 Notes

- All rendering happens automatically once data is connected
- Objects render with correct geometry based on prim type
- Avatars render as humanoid figures
- Terrain renders with real heightmaps
- Textures load asynchronously from network
- Performance is optimized automatically
- Resource cleanup is handled automatically

## 🎉 Result

A fully functional Second Life viewer with:
- Beautiful PBR rendering
- Real object shapes (not just cubes!)
- Humanoid avatars
- Proper terrain
- Network textures
- Dynamic lighting
- Shadows
- Good performance

**Enjoy!** 🌍✨
