/*
 * Viewer Integration Test - Tests for the complete Second Life viewer system
 */

package lindenlab.llsd.viewer.secondlife;

import lindenlab.llsd.viewer.secondlife.app.SecondLifeViewer;
import lindenlab.llsd.viewer.secondlife.cache.CacheManager;
import lindenlab.llsd.viewer.secondlife.cache.CacheStatistics;
import lindenlab.llsd.viewer.secondlife.config.ViewerConfiguration;
import lindenlab.llsd.viewer.secondlife.rendering.AdvancedRenderingSystem;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Integration tests for the complete Second Life viewer system including
 * cache management, rendering system, and configuration.
 */
public class ViewerIntegrationTest {
    
    private SecondLifeViewer viewer;
    private ViewerConfiguration configuration;
    
    @BeforeEach
    void setUp() {
        configuration = new ViewerConfiguration();
        viewer = new SecondLifeViewer();
    }
    
    @AfterEach
    void tearDown() {
        if (viewer != null && viewer.isInitialized()) {
            viewer.shutdown().join();
        }
    }
    
    @Test
    @DisplayName("Viewer should initialize with default configuration")
    void testViewerInitialization() {
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        
        assertTrue(initResult.join(), "Viewer should initialize successfully");
        assertTrue(viewer.isInitialized(), "Viewer should report as initialized");
        assertFalse(viewer.isRunning(), "Viewer should not be running yet");
    }
    
    @Test
    @DisplayName("Cache system should support configurable storage and sizes")
    void testCacheSystemConfiguration() {
        // Test different storage locations
        configuration.setCacheStorageLocation(CacheManager.StorageLocation.EXTERNAL);
        configuration.setMaxCacheSize(50L * 1024 * 1024 * 1024); // 50GB
        
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        assertTrue(initResult.join(), "Viewer should initialize with custom cache config");
        
        CacheManager cacheManager = viewer.getCacheManager();
        assertNotNull(cacheManager, "Cache manager should be available");
        
        CacheStatistics stats = cacheManager.getStatistics();
        assertEquals(CacheManager.StorageLocation.EXTERNAL, stats.getStorageLocation());
        assertEquals(50L * 1024 * 1024 * 1024, stats.getMaxSize());
        
        // Test cache operations
        CompletableFuture<Boolean> storeResult = cacheManager.store(
                CacheManager.CacheType.TEXTURE, 
                "test-texture-1", 
                "test texture data".getBytes()
        );
        assertTrue(storeResult.join(), "Should be able to store cache item");
        
        CompletableFuture<byte[]> retrieveResult = cacheManager.retrieve(
                CacheManager.CacheType.TEXTURE, 
                "test-texture-1"
        );
        assertNotNull(retrieveResult.join(), "Should be able to retrieve cached item");
        assertEquals("test texture data", new String(retrieveResult.join()));
    }
    
    @Test
    @DisplayName("Cache system should support up to 200GB capacity")
    void testMaximumCacheCapacity() {
        // Test maximum cache size
        configuration.setMaxCacheSize(CacheManager.MAX_CACHE_SIZE); // 200GB
        
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        assertTrue(initResult.join(), "Viewer should initialize with maximum cache size");
        
        CacheStatistics stats = viewer.getCacheManager().getStatistics();
        assertEquals(CacheManager.MAX_CACHE_SIZE, stats.getMaxSize());
        assertEquals(200L * 1024 * 1024 * 1024, stats.getMaxSize());
    }
    
    @Test
    @DisplayName("Rendering system should support fine-grained quality controls")
    void testAdvancedRenderingControls() {
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        assertTrue(initResult.join(), "Viewer should initialize successfully");
        
        AdvancedRenderingSystem renderingSystem = viewer.getRenderingSystem();
        assertNotNull(renderingSystem, "Rendering system should be available");
        
        // Test quality presets
        renderingSystem.applyUltraLowPreset();
        assertEquals(0.1f, renderingSystem.getQualitySettings().getOverallQuality(), 0.01f);
        
        renderingSystem.applyLowPreset();
        assertEquals(0.3f, renderingSystem.getQualitySettings().getOverallQuality(), 0.01f);
        
        renderingSystem.applyBalancedPreset();
        assertEquals(0.6f, renderingSystem.getQualitySettings().getOverallQuality(), 0.01f);
        
        renderingSystem.applyHighPreset();
        assertEquals(0.8f, renderingSystem.getQualitySettings().getOverallQuality(), 0.01f);
        
        renderingSystem.applyUltraPreset();
        assertEquals(1.0f, renderingSystem.getQualitySettings().getOverallQuality(), 0.01f);
        
        // Test individual settings
        assertNotNull(renderingSystem.getTextureSettings());
        assertNotNull(renderingSystem.getShadowSettings());
        assertNotNull(renderingSystem.getParticleSettings());
        assertNotNull(renderingSystem.getAvatarSettings());
    }
    
    @Test
    @DisplayName("Battery conservation mode should disable rendering and apply power saving")
    void testBatteryConservationMode() {
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        assertTrue(initResult.join(), "Viewer should initialize successfully");
        
        AdvancedRenderingSystem renderingSystem = viewer.getRenderingSystem();
        
        // Initially rendering should be enabled
        assertTrue(renderingSystem.isRenderingEnabled());
        assertFalse(renderingSystem.isBatteryConservationMode());
        
        // Enable battery conservation mode
        viewer.setBatteryConservationMode(true);
        
        // Rendering should be disabled and battery mode enabled
        assertFalse(renderingSystem.isRenderingEnabled());
        assertTrue(renderingSystem.isBatteryConservationMode());
        
        // Disable battery conservation mode
        viewer.setBatteryConservationMode(false);
        
        // Rendering should be re-enabled
        assertTrue(renderingSystem.isRenderingEnabled());
        assertFalse(renderingSystem.isBatteryConservationMode());
    }
    
    @Test
    @DisplayName("Rendering toggle should work independently of battery mode")
    void testRenderingToggle() {
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        assertTrue(initResult.join(), "Viewer should initialize successfully");
        
        AdvancedRenderingSystem renderingSystem = viewer.getRenderingSystem();
        
        // Initially rendering should be enabled
        assertTrue(renderingSystem.isRenderingEnabled());
        
        // Toggle rendering off
        viewer.toggleRendering();
        assertFalse(renderingSystem.isRenderingEnabled());
        
        // Toggle rendering back on
        viewer.toggleRendering();
        assertTrue(renderingSystem.isRenderingEnabled());
    }
    
    @Test
    @DisplayName("Configuration should support command line arguments")
    void testCommandLineConfiguration() {
        String[] args = {
            "--cache-location", "EXTERNAL",
            "--cache-size", "100GB", 
            "--quality", "HIGH",
            "--battery-mode",
            "--no-splash"
        };
        
        ViewerConfiguration config = ViewerConfiguration.fromCommandLineArgs(args);
        
        assertEquals(CacheManager.StorageLocation.EXTERNAL, config.getCacheStorageLocation());
        assertEquals(100L * 1024 * 1024 * 1024, config.getMaxCacheSize());
        assertEquals(ViewerConfiguration.QualityPreset.HIGH, config.getDefaultQualityPreset());
        assertTrue(config.isBatteryOptimizationEnabled());
        assertFalse(config.isShowSplashScreen());
    }
    
    @Test
    @DisplayName("Viewer should start and maintain main loop")
    void testViewerMainLoop() throws InterruptedException {
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        assertTrue(initResult.join(), "Viewer should initialize successfully");
        
        // Start the viewer
        viewer.start();
        assertTrue(viewer.isRunning(), "Viewer should be running after start");
        
        // Let it run for a short time
        Thread.sleep(100);
        
        // Check statistics are being updated
        Map<String, Object> stats = viewer.getStatistics();
        assertNotNull(stats, "Statistics should be available");
        assertTrue(stats.containsKey("uptime"), "Should have uptime statistic");
        assertTrue(stats.containsKey("frameCount"), "Should have frame count statistic");
        assertTrue(stats.containsKey("memoryUsed"), "Should have memory usage statistic");
        
        // Verify some basic statistics
        assertTrue((Long) stats.get("uptime") > 0, "Uptime should be positive");
        assertTrue((Long) stats.get("frameCount") >= 0, "Frame count should be non-negative");
    }
    
    @Test
    @DisplayName("Cache should support all asset types")
    void testCacheAssetTypes() {
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        assertTrue(initResult.join(), "Viewer should initialize successfully");
        
        CacheManager cacheManager = viewer.getCacheManager();
        
        // Test all cache types
        for (CacheManager.CacheType type : CacheManager.CacheType.values()) {
            String key = "test-" + type.name().toLowerCase();
            byte[] data = ("test data for " + type.name()).getBytes();
            
            CompletableFuture<Boolean> storeResult = cacheManager.store(type, key, data);
            assertTrue(storeResult.join(), "Should be able to store " + type + " data");
            
            assertTrue(cacheManager.exists(type, key), "Cache item should exist");
            
            CompletableFuture<byte[]> retrieveResult = cacheManager.retrieve(type, key);
            assertNotNull(retrieveResult.join(), "Should be able to retrieve " + type + " data");
            assertArrayEquals(data, retrieveResult.join(), "Retrieved data should match stored data");
        }
    }
    
    @Test
    @DisplayName("Configuration should persist and load correctly")
    void testConfigurationPersistence() {
        // Create configuration with custom settings
        ViewerConfiguration originalConfig = new ViewerConfiguration();
        originalConfig.setCacheStorageLocation(CacheManager.StorageLocation.USER_HOME);
        originalConfig.setMaxCacheSize(25L * 1024 * 1024 * 1024); // 25GB
        originalConfig.setDefaultQualityPreset(ViewerConfiguration.QualityPreset.HIGH);
        originalConfig.setBatteryOptimizationEnabled(true);
        originalConfig.setShowSplashScreen(false);
        
        // Save configuration
        originalConfig.save();
        
        // Create new configuration instance (should load from file)
        ViewerConfiguration loadedConfig = new ViewerConfiguration();
        
        // Verify settings were loaded correctly
        assertEquals(CacheManager.StorageLocation.USER_HOME, loadedConfig.getCacheStorageLocation());
        assertEquals(25L * 1024 * 1024 * 1024, loadedConfig.getMaxCacheSize());
        assertEquals(ViewerConfiguration.QualityPreset.HIGH, loadedConfig.getDefaultQualityPreset());
        assertTrue(loadedConfig.isBatteryOptimizationEnabled());
        assertFalse(loadedConfig.isShowSplashScreen());
    }
    
    @Test
    @DisplayName("Viewer should shutdown gracefully")
    void testGracefulShutdown() {
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        assertTrue(initResult.join(), "Viewer should initialize successfully");
        
        viewer.start();
        assertTrue(viewer.isRunning(), "Viewer should be running");
        
        // Shutdown viewer
        CompletableFuture<Void> shutdownResult = viewer.shutdown();
        shutdownResult.join();
        
        assertFalse(viewer.isRunning(), "Viewer should not be running after shutdown");
    }
    
    @Test
    @DisplayName("Cache statistics should provide comprehensive information")
    void testCacheStatistics() {
        CompletableFuture<Boolean> initResult = viewer.initialize(configuration);
        assertTrue(initResult.join(), "Viewer should initialize successfully");
        
        CacheManager cacheManager = viewer.getCacheManager();
        
        // Add some test data
        cacheManager.store(CacheManager.CacheType.TEXTURE, "test1", "data1".getBytes()).join();
        cacheManager.store(CacheManager.CacheType.SOUND, "test2", "data2".getBytes()).join();
        
        // Access one item to create a hit
        cacheManager.retrieve(CacheManager.CacheType.TEXTURE, "test1").join();
        
        // Try to access non-existent item to create a miss
        cacheManager.retrieve(CacheManager.CacheType.TEXTURE, "nonexistent").join();
        
        CacheStatistics stats = cacheManager.getStatistics();
        
        assertNotNull(stats, "Statistics should be available");
        assertTrue(stats.getTotalSize() > 0, "Total cache size should be positive");
        assertTrue(stats.getTotalHits() > 0, "Should have at least one cache hit");
        assertTrue(stats.getTotalMisses() > 0, "Should have at least one cache miss");
        assertTrue(stats.getTotalWrites() > 0, "Should have cache writes");
        assertEquals(configuration.getMaxCacheSize(), stats.getMaxSize(), "Max size should match configuration");
        
        // Test formatted output
        String statsString = stats.toString();
        assertNotNull(statsString, "Statistics string should be available");
        assertTrue(statsString.contains("Cache Statistics"), "Should contain statistics header");
    }
}