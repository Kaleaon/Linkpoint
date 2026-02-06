/*
 * Second Life Viewer - Main application class for the complete Second Life viewer
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.app;

import lindenlab.llsd.viewer.secondlife.cache.CacheManager;
import lindenlab.llsd.viewer.secondlife.cache.CacheStatistics;
import lindenlab.llsd.viewer.secondlife.rendering.AdvancedRenderingSystem;
import lindenlab.llsd.viewer.secondlife.config.ViewerConfiguration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main Second Life Viewer application.
 * 
 * This is the central class that coordinates all viewer subsystems:
 * - Cache management with configurable storage up to 200GB
 * - Advanced rendering with fine-grained controls
 * - Network connectivity and protocol handling
 * - User interface and interaction systems
 * - Configuration management
 * - Performance monitoring and optimization
 */
public class SecondLifeViewer {
    private static final Logger LOGGER = Logger.getLogger(SecondLifeViewer.class.getName());
    
    // Application metadata
    public static final String VERSION = "1.0.0-Java";
    public static final String BUILD_DATE = "2024-12-19";
    public static final String USER_AGENT = "SecondLife-Java/" + VERSION;
    
    // Core subsystems
    private CacheManager cacheManager;
    private AdvancedRenderingSystem renderingSystem;
    private ViewerConfiguration configuration;
    
    // Application state
    private boolean initialized = false;
    private boolean running = false;
    private boolean shuttingDown = false;
    
    // Threading
    private ScheduledExecutorService mainExecutor;
    private ScheduledExecutorService performanceExecutor;
    
    // Statistics and monitoring
    private final Map<String, Object> applicationStats = new HashMap<>();
    private long startTime;
    private long frameCount = 0;
    private double averageFPS = 0.0;
    
    public SecondLifeViewer() {
        LOGGER.info("Initializing Second Life Viewer " + VERSION);
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Initialize the viewer with default configuration
     */
    public CompletableFuture<Boolean> initialize() {
        return initialize(new ViewerConfiguration());
    }
    
    /**
     * Initialize the viewer with custom configuration
     */
    public CompletableFuture<Boolean> initialize(ViewerConfiguration config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (initialized) {
                    LOGGER.warning("Viewer already initialized");
                    return true;
                }
                
                LOGGER.info("Starting viewer initialization...");
                this.configuration = config;
                
                // Initialize thread pools
                initializeThreadPools();
                
                // Initialize cache system
                initializeCacheSystem();
                
                // Initialize rendering system
                initializeRenderingSystem();
                
                // Start performance monitoring
                startPerformanceMonitoring();
                
                // Register shutdown hook
                registerShutdownHook();
                
                initialized = true;
                LOGGER.info("Viewer initialization complete");
                
                return true;
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize viewer", e);
                return false;
            }
        });
    }
    
    private void initializeThreadPools() {
        this.mainExecutor = Executors.newScheduledThreadPool(4);
        this.performanceExecutor = Executors.newScheduledThreadPool(2);
        LOGGER.info("Thread pools initialized");
    }
    
    private void initializeCacheSystem() {
        // Initialize cache with configured settings
        CacheManager.StorageLocation location = configuration.getCacheStorageLocation();
        long maxSize = configuration.getMaxCacheSize();
        
        this.cacheManager = new CacheManager(location, maxSize);
        
        LOGGER.info("Cache system initialized: " + 
                   location.getDisplayName() + ", " + 
                   CacheManager.formatBytes(maxSize));
    }
    
    private void initializeRenderingSystem() {
        this.renderingSystem = new AdvancedRenderingSystem();
        
        // Apply configured rendering settings
        if (configuration.isBatteryOptimizationEnabled()) {
            renderingSystem.setBatteryConservationMode(true);
        }
        
        // Apply quality preset
        switch (configuration.getDefaultQualityPreset()) {
            case ULTRA_LOW:
                renderingSystem.applyUltraLowPreset();
                break;
            case LOW:
                renderingSystem.applyLowPreset();
                break;
            case BALANCED:
                renderingSystem.applyBalancedPreset();
                break;
            case HIGH:
                renderingSystem.applyHighPreset();
                break;
            case ULTRA:
                renderingSystem.applyUltraPreset();
                break;
        }
        
        LOGGER.info("Rendering system initialized");
    }
    
    private void startPerformanceMonitoring() {
        performanceExecutor.scheduleAtFixedRate(
                this::updatePerformanceStatistics,
                1, 1, TimeUnit.SECONDS
        );
        
        performanceExecutor.scheduleAtFixedRate(
                this::performMaintenanceTasks,
                60, 60, TimeUnit.SECONDS
        );
        
        LOGGER.info("Performance monitoring started");
    }
    
    /**
     * Start the main viewer loop
     */
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Viewer not initialized");
        }
        
        if (running) {
            LOGGER.warning("Viewer already running");
            return;
        }
        
        running = true;
        LOGGER.info("Starting Second Life Viewer");
        
        // Start main loop
        mainExecutor.scheduleAtFixedRate(
                this::mainLoop,
                0, 16, TimeUnit.MILLISECONDS // ~60 FPS
        );
        
        LOGGER.info("Viewer started successfully");
    }
    
    /**
     * Main application loop
     */
    private void mainLoop() {
        if (!running || shuttingDown) return;
        
        try {
            // Update frame counter
            frameCount++;
            
            // Update rendering system
            if (renderingSystem.isRenderingEnabled()) {
                // Rendering updates would go here
            }
            
            // Update statistics
            updateApplicationStatistics();
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error in main loop", e);
        }
    }
    
    private void updatePerformanceStatistics() {
        long currentTime = System.currentTimeMillis();
        long uptime = currentTime - startTime;
        
        // Calculate average FPS
        if (uptime > 0) {
            averageFPS = (frameCount * 1000.0) / uptime;
        }
        
        // Update application statistics
        applicationStats.put("uptime", uptime);
        applicationStats.put("frameCount", frameCount);
        applicationStats.put("averageFPS", averageFPS);
        applicationStats.put("memoryUsed", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        applicationStats.put("maxMemory", Runtime.getRuntime().maxMemory());
        
        // Log performance periodically
        if (frameCount % 3600 == 0) { // Every minute at 60 FPS
            LOGGER.info(String.format("Performance: %.1f FPS avg, %.1f MB memory", 
                       averageFPS, 
                       (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0)));
        }
    }
    
    private void performMaintenanceTasks() {
        try {
            // Cache maintenance is handled automatically by CacheManager
            
            // Check for configuration updates
            if (configuration.hasUpdates()) {
                applyConfigurationUpdates();
            }
            
            // Garbage collection hint (if memory pressure is high)
            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long maxMemory = Runtime.getRuntime().maxMemory();
            if (usedMemory > maxMemory * 0.8) {
                System.gc();
                LOGGER.fine("Performed garbage collection (memory pressure)");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error in maintenance tasks", e);
        }
    }
    
    private void updateApplicationStatistics() {
        // Update render statistics from rendering system
        Map<String, Object> renderStats = renderingSystem.getRenderStatistics();
        applicationStats.putAll(renderStats);
        
        // Update cache statistics
        CacheStatistics cacheStats = cacheManager.getStatistics();
        applicationStats.put("cacheHitRatio", cacheStats.getHitRatio());
        applicationStats.put("cacheSize", cacheStats.getTotalSize());
        applicationStats.put("cacheUtilization", cacheStats.getUsagePercent());
    }
    
    private void applyConfigurationUpdates() {
        LOGGER.info("Applying configuration updates");
        
        // Update cache configuration
        if (configuration.getCacheStorageLocation() != cacheManager.getStatistics().getStorageLocation()) {
            cacheManager.setStorageLocation(configuration.getCacheStorageLocation());
        }
        
        if (configuration.getMaxCacheSize() != cacheManager.getStatistics().getMaxSize()) {
            cacheManager.setMaxCacheSize(configuration.getMaxCacheSize());
        }
        
        // Update rendering configuration
        renderingSystem.setBatteryConservationMode(configuration.isBatteryOptimizationEnabled());
        
        configuration.markUpdatesApplied();
    }
    
    // Public API methods
    
    /**
     * Toggle rendering on/off for battery conservation
     */
    public void toggleRendering() {
        boolean currentState = renderingSystem.isRenderingEnabled();
        renderingSystem.setRenderingEnabled(!currentState);
        
        String status = currentState ? "disabled" : "enabled";
        LOGGER.info("Rendering " + status + " by user request");
    }
    
    /**
     * Set battery conservation mode
     */
    public void setBatteryConservationMode(boolean enabled) {
        renderingSystem.setBatteryConservationMode(enabled);
        configuration.setBatteryOptimizationEnabled(enabled);
        
        LOGGER.info("Battery conservation mode " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Get current application statistics
     */
    public Map<String, Object> getStatistics() {
        return new HashMap<>(applicationStats);
    }
    
    /**
     * Get cache manager for external configuration
     */
    public CacheManager getCacheManager() {
        return cacheManager;
    }
    
    /**
     * Get rendering system for external configuration
     */
    public AdvancedRenderingSystem getRenderingSystem() {
        return renderingSystem;
    }
    
    /**
     * Get configuration manager
     */
    public ViewerConfiguration getConfiguration() {
        return configuration;
    }
    
    /**
     * Check if viewer is running
     */
    public boolean isRunning() {
        return running && !shuttingDown;
    }
    
    /**
     * Check if viewer is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (running && !shuttingDown) {
                LOGGER.info("Shutdown hook triggered");
                shutdown().join();
            }
        }));
    }
    
    /**
     * Shutdown the viewer gracefully
     */
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            if (shuttingDown) return;
            
            shuttingDown = true;
            running = false;
            
            LOGGER.info("Shutting down Second Life Viewer...");
            
            try {
                // Shutdown rendering system
                if (renderingSystem != null) {
                    renderingSystem.shutdown();
                }
                
                // Shutdown cache system
                if (cacheManager != null) {
                    cacheManager.shutdown();
                }
                
                // Save configuration
                if (configuration != null) {
                    configuration.save();
                }
                
                // Shutdown thread pools
                shutdownExecutors();
                
                LOGGER.info("Second Life Viewer shutdown complete");
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error during shutdown", e);
            }
        });
    }
    
    private void shutdownExecutors() {
        if (mainExecutor != null) {
            mainExecutor.shutdown();
            try {
                if (!mainExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    mainExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                mainExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (performanceExecutor != null) {
            performanceExecutor.shutdown();
            try {
                if (!performanceExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    performanceExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                performanceExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Main entry point for the application
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", 
                          "[%1$tT] %4$s: %5$s%6$s%n");
        
        LOGGER.info("Starting Second Life Viewer Application");
        
        SecondLifeViewer viewer = new SecondLifeViewer();
        
        // Parse command line arguments
        ViewerConfiguration config = ViewerConfiguration.fromCommandLineArgs(args);
        
        // Initialize and start viewer
        viewer.initialize(config).thenAccept(success -> {
            if (success) {
                viewer.start();
                LOGGER.info("Viewer is now running. Press Ctrl+C to exit.");
            } else {
                LOGGER.severe("Failed to initialize viewer");
                System.exit(1);
            }
        }).exceptionally(throwable -> {
            LOGGER.log(Level.SEVERE, "Failed to start viewer", throwable);
            System.exit(1);
            return null;
        });
        
        // Keep main thread alive
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            LOGGER.info("Main thread interrupted, shutting down...");
            viewer.shutdown().join();
        }
    }
}