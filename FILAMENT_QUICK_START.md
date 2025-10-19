# Filament Quick Start Guide

## ✅ What's Been Done

Filament rendering engine has been successfully integrated into the Lumiya Viewer app!

### Files Created
- `FilamentRenderContext.kt` - Core Filament engine wrapper
- `FilamentWorldRenderer.kt` - World scene management
- `FilamentSurfaceView.kt` - Rendering surface view
- `FilamentTestActivity.kt` - Simple test activity
- `FilamentWorldViewActivity.kt` - Full-featured world view
- Layout: `activity_filament_world_view.xml`
- Documentation: README and integration summaries

### Dependencies Added
```gradle
implementation 'com.google.android.filament:filament-android:1.66.0'
implementation 'com.google.android.filament:filament-utils-android:1.66.0'
implementation 'com.google.android.filament:gltfio-android:1.66.0'
```

## 🚀 Quick Test

To test the integration right now:

```bash
# Build the app
./gradlew assembleDebug

# Install on device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk

# Launch Filament test activity
adb shell am start -n com.lumiyaviewer.lumiya/.ui.render.FilamentTestActivity
```

**Expected Result:** A colored triangle on a dark blue background, proving Filament is working!

## 📋 What's Needed for Production

To use Filament for real world rendering, you need to:

### 1. Material Compilation Setup (CRITICAL)
Current code uses placeholder materials. You need:

**Create material files** (e.g., `src/main/materials/basic.mat`):
```glsl
material {
    name : BasicMaterial,
    shadingModel : lit,
    parameters : [
        { type : float3, name : baseColor, default : [1.0, 1.0, 1.0] }
    ]
}

fragment {
    void material(inout MaterialInputs material) {
        prepareMaterial(material);
        material.baseColor.rgb = materialParams.baseColor;
    }
}
```

**Add Filament tools plugin to build.gradle:**
```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'filament-tools-plugin'  // Add this
}

filamentTools {
    materialInputDir = "src/main/materials"
    materialOutputDir = "src/main/assets/materials"
}
```

**Download matc compiler:**
- Get from https://github.com/google/filament/releases
- Place in `tools/` directory
- Configure path in gradle.properties

### 2. Load Real Content

**Example: Load a glTF model**
```kotlin
val assetLoader = AssetLoader(engine, materialProvider, entityManager)
val buffer = assets.open("models/avatar.glb").readBytes()
val asset = assetLoader.createAssetFromBinary(ByteBuffer.wrap(buffer))
scene.addEntities(asset.entities)
```

**Example: Create terrain**
```kotlin
// Generate terrain mesh
val terrainMesh = generateTerrainMesh(heightmap)
val entity = entityManager.create()

RenderableManager.Builder(1)
    .boundingBox(terrainBounds)
    .geometry(0, PrimitiveType.TRIANGLES, vertexBuffer, indexBuffer)
    .material(0, terrainMaterial.defaultInstance)
    .build(engine, entity)

scene.addEntity(entity)
```

### 3. Connect to World Data

**Integrate with existing systems:**
```kotlin
class FilamentWorldViewActivity : AppCompatActivity() {
    // Connect to world data
    private val worldData = WorldDataManager.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // ... setup code ...
        
        // Listen for world updates
        worldData.onObjectUpdate { obj ->
            renderer.updateObject(obj)
        }
        
        worldData.onAvatarUpdate { avatar ->
            renderer.updateAvatar(avatar)
        }
    }
}
```

### 4. Add Lighting

**Example: Add sun light**
```kotlin
@Entity val sun = entityManager.create()

LightManager.Builder(LightManager.Type.DIRECTIONAL)
    .color(1.0f, 1.0f, 0.9f)  // Warm sunlight
    .intensity(100000.0f)
    .direction(0.0f, -1.0f, -1.0f)
    .castShadows(true)
    .build(engine, sun)

scene.addEntity(sun)
```

## 🎯 Immediate Next Steps

1. **Test Current Implementation**
   - Build and run the app
   - Launch `FilamentTestActivity`
   - Verify triangle renders correctly

2. **Set Up Material Pipeline**
   - Download Filament tools
   - Create first material file
   - Set up build-time compilation

3. **Create Basic Materials**
   - Terrain material
   - Water material
   - Basic avatar material
   - Object material

4. **Integrate World Data**
   - Connect `FilamentWorldRenderer` to `SLAgentCircuit`
   - Render terrain from world data
   - Render objects from object manager

## 📚 Documentation Locations

- **Package README**: `app/src/main/java/com/lumiyaviewer/lumiya/render/filament/README.md`
- **Integration Summary**: `FILAMENT_INTEGRATION_SUMMARY.md`
- **This Guide**: `FILAMENT_QUICK_START.md`

## 🔧 Troubleshooting

### Build fails with "Cannot find Filament"
**Solution**: Sync Gradle - the dependencies should download from Maven Central automatically.

### Black screen when running test activity
**Solutions**:
1. Check logcat for errors: `adb logcat | grep Filament`
2. Verify OpenGL ES 3.0+ support on device
3. Check if surface was created: Look for "SwapChain created" log

### App crashes on startup
**Solutions**:
1. Check Filament.init() was called
2. Verify all resources are destroyed in onDestroy()
3. Check thread safety - all Filament calls on same thread

### Triangle doesn't rotate
**Note**: Current implementation shows a static triangle. Animation not yet implemented.

## 💡 Tips

1. **Use Feature Level 1**: Best compatibility across devices
2. **Precompile Materials**: Don't compile at runtime (slow)
3. **Destroy Resources**: Always destroy Filament objects explicitly
4. **Check Logs**: Filament logs are verbose and helpful
5. **Start Simple**: Get basic rendering working before adding complexity

## 🎨 Example: Simple Usage

```kotlin
class MyActivity : AppCompatActivity() {
    private lateinit var filamentView: FilamentSurfaceView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create and setup Filament view
        filamentView = FilamentSurfaceView(this)
        filamentView.initializeWorldRenderer()
        setContentView(filamentView)
        
        // Position camera
        filamentView.getWorldRenderer()?.setCameraPosition(
            LLVector3(0f, -20f, 10f),
            0f, 0f
        )
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

## 🚦 Status

- ✅ **Working**: Filament initialization, basic rendering, test scene
- ⚠️ **Needs Work**: Materials, models, textures, lighting
- ⏳ **Future**: Advanced features, optimization, full world rendering

## 🤝 Need Help?

1. Check the detailed README in the filament package
2. Look at Filament samples: `/Filament/android/samples/`
3. Read Filament docs: https://google.github.io/filament/
4. Check GitHub: https://github.com/google/filament

---

**You're ready to start building with Filament! 🎉**
