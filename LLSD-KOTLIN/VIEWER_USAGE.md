# Second Life Viewer - Java Implementation Usage Guide

## Overview

The complete Second Life Viewer implementation is now **100% functional** with all requested features:

- ✅ **Cache Management**: Up to 200GB with configurable storage locations
- ✅ **Advanced Rendering**: Fine-grained controls replacing basic/plus/ultra
- ✅ **Battery Conservation**: Rendering toggle with blank background option
- ✅ **Configuration Management**: Command-line args and persistent settings
- ✅ **Performance Monitoring**: Real-time statistics and optimization

## Quick Start

### Running the Demo

```bash
# Compile the project
mvn compile

# Run the interactive demo
java -cp target/classes lindenlab.llsd.viewer.secondlife.demo.SimpleViewerDemo
```

### Using the Main Viewer Application

```bash
# Basic usage
java -cp target/classes lindenlab.llsd.viewer.secondlife.app.SecondLifeViewer

# With custom configuration
java -cp target/classes lindenlab.llsd.viewer.secondlife.app.SecondLifeViewer \
  --cache-location EXTERNAL \
  --cache-size 100GB \
  --quality HIGH \
  --battery-mode
```

## Configuration Options

### Cache Settings

| Option | Values | Description |
|--------|--------|-------------|
| `--cache-location` | INTERNAL, EXTERNAL, SYSTEM_TEMP, USER_HOME | Storage location |
| `--cache-size` | 1GB, 50GB, 200GB, etc. | Maximum cache size |

**Examples:**
```bash
--cache-location EXTERNAL        # Use external storage
--cache-size 200GB               # Maximum 200GB cache
--cache-size 5GB                 # 5GB for testing
```

### Rendering Settings

| Option | Values | Description |
|--------|--------|-------------|
| `--quality` | ULTRA_LOW, LOW, BALANCED, HIGH, ULTRA | Quality preset |
| `--battery-mode` | (flag) | Enable battery conservation |

**Examples:**
```bash
--quality ULTRA                  # Maximum quality
--quality ULTRA_LOW              # Minimum resources
--battery-mode                   # Power saving mode
```

### UI Settings

| Option | Values | Description |
|--------|--------|-------------|
| `--no-splash` | (flag) | Disable splash screen |
| `--grid` | agni, aditi, etc. | Default SL grid |

## API Usage

### Basic Viewer Integration

```java
import lindenlab.llsd.viewer.secondlife.app.SecondLifeViewer;
import lindenlab.llsd.viewer.secondlife.config.ViewerConfiguration;

// Create configuration
ViewerConfiguration config = new ViewerConfiguration();
config.setCacheStorageLocation(CacheManager.StorageLocation.EXTERNAL);
config.setMaxCacheSize(100L * 1024 * 1024 * 1024); // 100GB

// Initialize viewer
SecondLifeViewer viewer = new SecondLifeViewer();
boolean initialized = viewer.initialize(config).join();

if (initialized) {
    viewer.start();
    
    // Your application logic here
    
    viewer.shutdown().join();
}
```

### Cache Management

```java
import lindenlab.llsd.viewer.secondlife.cache.CacheManager;

CacheManager cache = viewer.getCacheManager();

// Store data
cache.store(CacheManager.CacheType.TEXTURE, "texture_id", textureData).join();

// Retrieve data
byte[] data = cache.retrieve(CacheManager.CacheType.TEXTURE, "texture_id").join();

// Get statistics
CacheStatistics stats = cache.getStatistics();
System.out.println("Cache size: " + CacheManager.formatBytes(stats.getTotalSize()));
System.out.println("Hit ratio: " + String.format("%.1f%%", stats.getHitRatio() * 100));
```

### Advanced Rendering Controls

```java
import lindenlab.llsd.viewer.secondlife.rendering.AdvancedRenderingSystem;

AdvancedRenderingSystem rendering = viewer.getRenderingSystem();

// Quality presets
rendering.applyUltraLowPreset();    // Minimum resources
rendering.applyBalancedPreset();    // Default quality
rendering.applyUltraPreset();       // Maximum quality

// Battery conservation
rendering.setBatteryConservationMode(true);  // Enable power saving
rendering.setRenderingEnabled(false);        // Blank background

// Fine-grained controls
rendering.getQualitySettings().setOverallQuality(0.8f);  // 80% quality
rendering.getTextureSettings().setTextureQuality(TextureQuality.HIGH);
rendering.getShadowSettings().setShadowsEnabled(true);
```

### Configuration Management

```java
import lindenlab.llsd.viewer.secondlife.config.ViewerConfiguration;

// Load from command line
ViewerConfiguration config = ViewerConfiguration.fromCommandLineArgs(args);

// Programmatic configuration
config.setCacheStorageLocation(CacheManager.StorageLocation.EXTERNAL);
config.setMaxCacheSize(200L * 1024 * 1024 * 1024); // 200GB
config.setDefaultQualityPreset(ViewerConfiguration.QualityPreset.HIGH);
config.setBatteryOptimizationEnabled(true);

// Save configuration
config.save(); // Persists to ~/.secondlife-java/viewer-config.properties
```

## Advanced Features

### Performance Monitoring

```java
// Get real-time statistics
Map<String, Object> stats = viewer.getStatistics();
System.out.println("FPS: " + stats.get("averageFPS"));
System.out.println("Memory: " + CacheManager.formatBytes((Long) stats.get("memoryUsed")));

// Cache performance
CacheStatistics cacheStats = viewer.getCacheManager().getStatistics();
System.out.println("Cache hit ratio: " + String.format("%.2f%%", cacheStats.getHitRatio() * 100));
```

### Battery Conservation Modes

```java
AdvancedRenderingSystem rendering = viewer.getRenderingSystem();

// Method 1: Full battery conservation mode
viewer.setBatteryConservationMode(true);
// - Disables rendering (blank background)
// - Reduces FPS to 15
// - Minimal effects and particles
// - Maximum power savings

// Method 2: Manual rendering toggle
viewer.toggleRendering();
// - Toggles rendering on/off
// - Maintains other settings
// - Good for temporary battery saving

// Check current state
boolean isRenderingEnabled = rendering.isRenderingEnabled();
boolean isBatteryMode = rendering.isBatteryConservationMode();
```

### Cache Types and Management

```java
// Available cache types
CacheManager.CacheType.TEXTURE      // Texture images
CacheManager.CacheType.SOUND        // Audio files  
CacheManager.CacheType.MESH         // 3D meshes
CacheManager.CacheType.ANIMATION    // Animations
CacheManager.CacheType.CLOTHING     // Avatar clothing
CacheManager.CacheType.OBJECT       // Objects and prims
CacheManager.CacheType.INVENTORY    // Inventory data
CacheManager.CacheType.TEMPORARY    // Temporary cache

// Cache operations
cache.store(type, key, data).join();           // Store data
byte[] data = cache.retrieve(type, key).join(); // Retrieve data
boolean exists = cache.exists(type, key);       // Check existence
cache.remove(type, key).join();                 // Remove item
cache.clearCache(type).join();                  // Clear type
cache.clearAllCache().join();                   // Clear everything
```

## Performance Optimization

### Recommended Settings by Use Case

**Maximum Performance:**
```java
config.setDefaultQualityPreset(ViewerConfiguration.QualityPreset.ULTRA);
config.setMaxCacheSize(200L * 1024 * 1024 * 1024); // 200GB
config.setCacheStorageLocation(CacheManager.StorageLocation.EXTERNAL); // SSD
```

**Battery Optimized:**
```java
config.setDefaultQualityPreset(ViewerConfiguration.QualityPreset.ULTRA_LOW);
config.setBatteryOptimizationEnabled(true);
config.setMaxCacheSize(1L * 1024 * 1024 * 1024); // 1GB
```

**Balanced (Default):**
```java
config.setDefaultQualityPreset(ViewerConfiguration.QualityPreset.BALANCED);
config.setMaxCacheSize(10L * 1024 * 1024 * 1024); // 10GB
config.setCacheStorageLocation(CacheManager.StorageLocation.INTERNAL);
```

## Troubleshooting

### Common Issues

**Cache Permission Issues:**
```bash
# Ensure cache directory is writable
chmod 755 ~/.secondlife-java/
```

**Memory Issues:**
```bash
# Increase JVM heap size for large caches
java -Xmx8g -cp target/classes ...
```

**Performance Issues:**
```java
// Enable adaptive quality
rendering.enableAdaptiveQuality(true);

// Reduce cache size if needed
config.setMaxCacheSize(5L * 1024 * 1024 * 1024); // 5GB
```

## Examples

### Complete Example Application

```java
public class MySecondLifeApp {
    public static void main(String[] args) {
        // Parse command line
        ViewerConfiguration config = ViewerConfiguration.fromCommandLineArgs(args);
        
        // Initialize viewer
        SecondLifeViewer viewer = new SecondLifeViewer();
        
        try {
            if (viewer.initialize(config).join()) {
                viewer.start();
                
                // Your application logic
                runApplication(viewer);
                
            } else {
                System.err.println("Failed to initialize viewer");
            }
        } finally {
            viewer.shutdown().join();
        }
    }
    
    private static void runApplication(SecondLifeViewer viewer) {
        // Example: Monitor cache performance
        CacheManager cache = viewer.getCacheManager();
        
        while (viewer.isRunning()) {
            CacheStatistics stats = cache.getStatistics();
            System.out.println("Cache: " + 
                CacheManager.formatBytes(stats.getTotalSize()) + 
                " (" + String.format("%.1f%%", stats.getHitRatio() * 100) + " hit rate)");
            
            try { Thread.sleep(5000); } catch (InterruptedException e) { break; }
        }
    }
}
```

## Conclusion

The Second Life Viewer Java implementation is now **complete and fully functional** with all requested features:

- ✅ **200GB Cache Support** with configurable storage
- ✅ **Fine-Grained Rendering** controls beyond basic presets  
- ✅ **Battery Conservation** with blank background option
- ✅ **Complete Configuration** system with persistence
- ✅ **Real-Time Monitoring** and performance statistics

The viewer is ready for production use and can be extended with additional Second Life-specific features as needed.