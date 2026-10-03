/*
 * Viewer Configuration - Comprehensive configuration management for Second Life viewer
 */

package lindenlab.llsd.viewer.secondlife.config;

import lindenlab.llsd.viewer.secondlife.cache.CacheManager;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Configuration management for the Second Life viewer.
 * Handles all viewer settings including cache, rendering, network, and UI preferences.
 */
public class ViewerConfiguration {
    private static final Logger LOGGER = Logger.getLogger(ViewerConfiguration.class.getName());
    
    // Configuration file locations
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.secondlife-java";
    private static final String CONFIG_FILE = "viewer-config.properties";
    
    // Quality presets
    public enum QualityPreset {
        ULTRA_LOW, LOW, BALANCED, HIGH, ULTRA
    }
    
    // Configuration properties
    private final Properties properties;
    private boolean hasUpdates = false;
    private Path configFile;
    
    // Cache settings
    private CacheManager.StorageLocation cacheStorageLocation = CacheManager.StorageLocation.INTERNAL;
    private long maxCacheSize = CacheManager.DEFAULT_CACHE_SIZE;
    
    // Rendering settings
    private QualityPreset defaultQualityPreset = QualityPreset.BALANCED;
    private boolean batteryOptimizationEnabled = false;
    private boolean adaptiveQualityEnabled = true;
    
    // Network settings
    private String defaultGrid = "agni"; // Second Life main grid
    private int connectionTimeout = 30000; // 30 seconds
    private int maxBandwidth = 1500; // KB/s
    
    // UI settings
    private boolean showSplashScreen = true;
    private boolean minimizeToTray = true;
    private String uiTheme = "Default";
    
    public ViewerConfiguration() {
        this.properties = new Properties();
        loadDefaultConfiguration();
        loadConfigurationFromFile();
    }
    
    private void loadDefaultConfiguration() {
        // Cache defaults
        properties.setProperty("cache.storage.location", cacheStorageLocation.name());
        properties.setProperty("cache.max.size", String.valueOf(maxCacheSize));
        
        // Rendering defaults
        properties.setProperty("rendering.quality.preset", defaultQualityPreset.name());
        properties.setProperty("rendering.battery.optimization", String.valueOf(batteryOptimizationEnabled));
        properties.setProperty("rendering.adaptive.quality", String.valueOf(adaptiveQualityEnabled));
        
        // Network defaults
        properties.setProperty("network.default.grid", defaultGrid);
        properties.setProperty("network.connection.timeout", String.valueOf(connectionTimeout));
        properties.setProperty("network.max.bandwidth", String.valueOf(maxBandwidth));
        
        // UI defaults
        properties.setProperty("ui.show.splash", String.valueOf(showSplashScreen));
        properties.setProperty("ui.minimize.to.tray", String.valueOf(minimizeToTray));
        properties.setProperty("ui.theme", uiTheme);
        
        LOGGER.info("Default configuration loaded");
    }
    
    private void loadConfigurationFromFile() {
        try {
            // Create config directory if it doesn't exist
            Path configDir = Paths.get(CONFIG_DIR);
            Files.createDirectories(configDir);
            
            configFile = configDir.resolve(CONFIG_FILE);
            
            if (Files.exists(configFile)) {
                try (InputStream input = Files.newInputStream(configFile)) {
                    properties.load(input);
                    applyLoadedProperties();
                    LOGGER.info("Configuration loaded from: " + configFile);
                }
            } else {
                LOGGER.info("No existing configuration file found, using defaults");
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load configuration from file", e);
        }
    }
    
    private void applyLoadedProperties() {
        // Apply cache settings
        String storageLocation = properties.getProperty("cache.storage.location");
        if (storageLocation != null) {
            try {
                cacheStorageLocation = CacheManager.StorageLocation.valueOf(storageLocation);
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Invalid cache storage location: " + storageLocation);
            }
        }
        
        String maxSizeStr = properties.getProperty("cache.max.size");
        if (maxSizeStr != null) {
            try {
                maxCacheSize = Long.parseLong(maxSizeStr);
                if (maxCacheSize > CacheManager.MAX_CACHE_SIZE) {
                    maxCacheSize = CacheManager.MAX_CACHE_SIZE;
                    LOGGER.warning("Max cache size clamped to: " + CacheManager.formatBytes(maxCacheSize));
                }
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid max cache size: " + maxSizeStr);
            }
        }
        
        // Apply rendering settings
        String qualityPreset = properties.getProperty("rendering.quality.preset");
        if (qualityPreset != null) {
            try {
                defaultQualityPreset = QualityPreset.valueOf(qualityPreset);
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Invalid quality preset: " + qualityPreset);
            }
        }
        
        batteryOptimizationEnabled = Boolean.parseBoolean(properties.getProperty("rendering.battery.optimization", "false"));
        adaptiveQualityEnabled = Boolean.parseBoolean(properties.getProperty("rendering.adaptive.quality", "true"));
        
        // Apply network settings
        defaultGrid = properties.getProperty("network.default.grid", "agni");
        
        String timeoutStr = properties.getProperty("network.connection.timeout");
        if (timeoutStr != null) {
            try {
                connectionTimeout = Integer.parseInt(timeoutStr);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid connection timeout: " + timeoutStr);
            }
        }
        
        String bandwidthStr = properties.getProperty("network.max.bandwidth");
        if (bandwidthStr != null) {
            try {
                maxBandwidth = Integer.parseInt(bandwidthStr);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid max bandwidth: " + bandwidthStr);
            }
        }
        
        // Apply UI settings
        showSplashScreen = Boolean.parseBoolean(properties.getProperty("ui.show.splash", "true"));
        minimizeToTray = Boolean.parseBoolean(properties.getProperty("ui.minimize.to.tray", "true"));
        uiTheme = properties.getProperty("ui.theme", "Default");
    }
    
    /**
     * Save configuration to file
     */
    public void save() {
        try {
            // Update properties with current values
            updatePropertiesFromCurrentValues();
            
            // Write to file
            try (OutputStream output = Files.newOutputStream(configFile, 
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                properties.store(output, "Second Life Viewer Configuration - " + new Date());
                LOGGER.info("Configuration saved to: " + configFile);
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save configuration", e);
        }
    }
    
    private void updatePropertiesFromCurrentValues() {
        // Update cache properties
        properties.setProperty("cache.storage.location", cacheStorageLocation.name());
        properties.setProperty("cache.max.size", String.valueOf(maxCacheSize));
        
        // Update rendering properties
        properties.setProperty("rendering.quality.preset", defaultQualityPreset.name());
        properties.setProperty("rendering.battery.optimization", String.valueOf(batteryOptimizationEnabled));
        properties.setProperty("rendering.adaptive.quality", String.valueOf(adaptiveQualityEnabled));
        
        // Update network properties
        properties.setProperty("network.default.grid", defaultGrid);
        properties.setProperty("network.connection.timeout", String.valueOf(connectionTimeout));
        properties.setProperty("network.max.bandwidth", String.valueOf(maxBandwidth));
        
        // Update UI properties
        properties.setProperty("ui.show.splash", String.valueOf(showSplashScreen));
        properties.setProperty("ui.minimize.to.tray", String.valueOf(minimizeToTray));
        properties.setProperty("ui.theme", uiTheme);
    }
    
    // Cache settings getters and setters
    
    public CacheManager.StorageLocation getCacheStorageLocation() {
        return cacheStorageLocation;
    }
    
    public void setCacheStorageLocation(CacheManager.StorageLocation location) {
        if (this.cacheStorageLocation != location) {
            this.cacheStorageLocation = location;
            markUpdated();
        }
    }
    
    public long getMaxCacheSize() {
        return maxCacheSize;
    }
    
    public void setMaxCacheSize(long maxSize) {
        long clampedSize = Math.min(maxSize, CacheManager.MAX_CACHE_SIZE);
        if (this.maxCacheSize != clampedSize) {
            this.maxCacheSize = clampedSize;
            markUpdated();
        }
    }
    
    // Rendering settings getters and setters
    
    public QualityPreset getDefaultQualityPreset() {
        return defaultQualityPreset;
    }
    
    public void setDefaultQualityPreset(QualityPreset preset) {
        if (this.defaultQualityPreset != preset) {
            this.defaultQualityPreset = preset;
            markUpdated();
        }
    }
    
    public boolean isBatteryOptimizationEnabled() {
        return batteryOptimizationEnabled;
    }
    
    public void setBatteryOptimizationEnabled(boolean enabled) {
        if (this.batteryOptimizationEnabled != enabled) {
            this.batteryOptimizationEnabled = enabled;
            markUpdated();
        }
    }
    
    public boolean isAdaptiveQualityEnabled() {
        return adaptiveQualityEnabled;
    }
    
    public void setAdaptiveQualityEnabled(boolean enabled) {
        if (this.adaptiveQualityEnabled != enabled) {
            this.adaptiveQualityEnabled = enabled;
            markUpdated();
        }
    }
    
    // Network settings getters and setters
    
    public String getDefaultGrid() {
        return defaultGrid;
    }
    
    public void setDefaultGrid(String grid) {
        if (!this.defaultGrid.equals(grid)) {
            this.defaultGrid = grid;
            markUpdated();
        }
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(int timeout) {
        if (this.connectionTimeout != timeout) {
            this.connectionTimeout = timeout;
            markUpdated();
        }
    }
    
    public int getMaxBandwidth() {
        return maxBandwidth;
    }
    
    public void setMaxBandwidth(int bandwidth) {
        if (this.maxBandwidth != bandwidth) {
            this.maxBandwidth = bandwidth;
            markUpdated();
        }
    }
    
    // UI settings getters and setters
    
    public boolean isShowSplashScreen() {
        return showSplashScreen;
    }
    
    public void setShowSplashScreen(boolean show) {
        if (this.showSplashScreen != show) {
            this.showSplashScreen = show;
            markUpdated();
        }
    }
    
    public boolean isMinimizeToTray() {
        return minimizeToTray;
    }
    
    public void setMinimizeToTray(boolean minimize) {
        if (this.minimizeToTray != minimize) {
            this.minimizeToTray = minimize;
            markUpdated();
        }
    }
    
    public String getUITheme() {
        return uiTheme;
    }
    
    public void setUITheme(String theme) {
        if (!this.uiTheme.equals(theme)) {
            this.uiTheme = theme;
            markUpdated();
        }
    }
    
    // Update tracking
    
    private void markUpdated() {
        hasUpdates = true;
    }
    
    public boolean hasUpdates() {
        return hasUpdates;
    }
    
    public void markUpdatesApplied() {
        hasUpdates = false;
    }
    
    /**
     * Create configuration from command line arguments
     */
    public static ViewerConfiguration fromCommandLineArgs(String[] args) {
        ViewerConfiguration config = new ViewerConfiguration();
        
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            try {
                switch (arg) {
                    case "--cache-location":
                        if (i + 1 < args.length) {
                            String location = args[++i].toUpperCase();
                            config.setCacheStorageLocation(CacheManager.StorageLocation.valueOf(location));
                        }
                        break;
                        
                    case "--cache-size":
                        if (i + 1 < args.length) {
                            long size = parseSize(args[++i]);
                            config.setMaxCacheSize(size);
                        }
                        break;
                        
                    case "--quality":
                        if (i + 1 < args.length) {
                            String quality = args[++i].toUpperCase();
                            config.setDefaultQualityPreset(QualityPreset.valueOf(quality));
                        }
                        break;
                        
                    case "--battery-mode":
                        config.setBatteryOptimizationEnabled(true);
                        break;
                        
                    case "--no-splash":
                        config.setShowSplashScreen(false);
                        break;
                        
                    case "--grid":
                        if (i + 1 < args.length) {
                            config.setDefaultGrid(args[++i]);
                        }
                        break;
                        
                    case "--help":
                        printUsage();
                        System.exit(0);
                        break;
                }
            } catch (Exception e) {
                LOGGER.warning("Invalid command line argument: " + arg);
            }
        }
        
        return config;
    }
    
    private static long parseSize(String sizeStr) {
        sizeStr = sizeStr.toUpperCase();
        long multiplier = 1;
        
        if (sizeStr.endsWith("GB")) {
            multiplier = 1024L * 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("MB")) {
            multiplier = 1024L * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("KB")) {
            multiplier = 1024L;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        }
        
        return Long.parseLong(sizeStr) * multiplier;
    }
    
    private static void printUsage() {
        System.out.println("Second Life Viewer - Java Implementation");
        System.out.println("Usage: java -jar secondlife-viewer.jar [options]");
        System.out.println();
        System.out.println("Cache Options:");
        System.out.println("  --cache-location LOCATION   Storage location (INTERNAL, EXTERNAL, SYSTEM_TEMP, USER_HOME)");
        System.out.println("  --cache-size SIZE           Max cache size (e.g., 10GB, 500MB)");
        System.out.println();
        System.out.println("Rendering Options:");
        System.out.println("  --quality PRESET            Quality preset (ULTRA_LOW, LOW, BALANCED, HIGH, ULTRA)");
        System.out.println("  --battery-mode               Enable battery conservation mode");
        System.out.println();
        System.out.println("Network Options:");
        System.out.println("  --grid GRID                  Default grid (agni, aditi, etc.)");
        System.out.println();
        System.out.println("UI Options:");
        System.out.println("  --no-splash                  Disable splash screen");
        System.out.println();
        System.out.println("Other Options:");
        System.out.println("  --help                       Show this help message");
    }
}