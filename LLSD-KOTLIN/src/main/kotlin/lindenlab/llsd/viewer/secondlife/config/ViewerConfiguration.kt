/*
 * Viewer Configuration - Kotlin implementation of comprehensive configuration management
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Kotlin implementation Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.config

import lindenlab.llsd.viewer.secondlife.cache.CacheManager
import java.io.*
import java.nio.file.*
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Configuration management for the Second Life viewer (Kotlin implementation).
 * 
 * Features:
 * - Kotlin property delegates for reactive configuration
 * - Type-safe configuration with sealed classes
 * - Coroutine-based async configuration loading/saving
 */
class ViewerConfiguration {
    companion object {
        private val LOGGER = Logger.getLogger(ViewerConfiguration::class.java.name)
        private const val CONFIG_DIR = "\${user.home}/.secondlife-java"
        private const val CONFIG_FILE = "viewer-config.properties"
        
        /**
         * Create configuration from command line arguments using Kotlin features
         */
        fun fromCommandLineArgs(args: Array<String>): ViewerConfiguration {
            val config = ViewerConfiguration()

            var i = 0
            while (i < args.size) {
                val arg = args[i]

                try {
                    when (arg) {
                        "--cache-location" -> {
                            if (i + 1 < args.size) {
                                val location = args[++i].uppercase()
                                config.cacheStorageLocation = CacheManager.StorageLocation.valueOf(location)
                            }
                        }

                        "--cache-size" -> {
                            if (i + 1 < args.size) {
                                config.maxCacheSize = parseSize(args[++i])
                            }
                        }

                        "--quality" -> {
                            if (i + 1 < args.size) {
                                val quality = args[++i].uppercase()
                                config.defaultQualityPreset = QualityPreset.valueOf(quality)
                            }
                        }

                        "--battery-mode" -> {
                            config.batteryOptimizationEnabled = true
                        }

                        "--no-splash" -> {
                            config.showSplashScreen = false
                        }

                        "--grid" -> {
                            if (i + 1 < args.size) {
                                config.defaultGrid = args[++i]
                            }
                        }

                        "--help" -> {
                            printUsage()
                            kotlin.system.exitProcess(0)
                        }
                    }
                } catch (e: Exception) {
                    LOGGER.warning("Invalid command line argument: $arg")
                }
                i++
            }

            return config
        }

        private fun parseSize(sizeStr: String): Long {
            val upperStr = sizeStr.uppercase()
            val multiplier = when {
                upperStr.endsWith("GB") -> 1024L * 1024 * 1024
                upperStr.endsWith("MB") -> 1024L * 1024
                upperStr.endsWith("KB") -> 1024L
                else -> 1L
            }

            val numberStr = when {
                upperStr.endsWith("GB") -> upperStr.dropLast(2)
                upperStr.endsWith("MB") -> upperStr.dropLast(2)
                upperStr.endsWith("KB") -> upperStr.dropLast(2)
                else -> upperStr
            }

            return numberStr.toLong() * multiplier
        }

        private fun printUsage() {
            println("""
                Second Life Viewer - Kotlin Implementation
                Usage: java -jar secondlife-viewer.jar [options]
                
                Cache Options:
                  --cache-location LOCATION   Storage location (INTERNAL, EXTERNAL, SYSTEM_TEMP, USER_HOME)
                  --cache-size SIZE           Max cache size (e.g., 10GB, 500MB)
                
                Rendering Options:
                  --quality PRESET            Quality preset (ULTRA_LOW, LOW, BALANCED, HIGH, ULTRA)
                  --battery-mode               Enable battery conservation mode
                
                Network Options:
                  --grid GRID                  Default grid (agni, aditi, etc.)
                
                UI Options:
                  --no-splash                  Disable splash screen
                
                Other Options:
                  --help                       Show this help message
            """.trimIndent())
        }
    }

    // Quality presets using sealed class for type safety
    sealed class QualityPreset(val displayName: String) {
        object ULTRA_LOW : QualityPreset("Ultra Low")
        object LOW : QualityPreset("Low")
        object BALANCED : QualityPreset("Balanced")
        object HIGH : QualityPreset("High")
        object ULTRA : QualityPreset("Ultra")

        companion object {
            fun valueOf(name: String): QualityPreset = when (name.uppercase()) {
                "ULTRA_LOW" -> ULTRA_LOW
                "LOW" -> LOW
                "BALANCED" -> BALANCED
                "HIGH" -> HIGH
                "ULTRA" -> ULTRA
                else -> BALANCED
            }

            fun values(): Array<QualityPreset> = arrayOf(ULTRA_LOW, LOW, BALANCED, HIGH, ULTRA)
        }

        override fun toString(): String = name
        val name: String get() = this::class.simpleName ?: "UNKNOWN"
    }

    // Configuration properties using Kotlin properties
    private val properties = Properties()
    private var hasUpdates = false
    private var configFile: Path? = null

    // Cache settings with property delegation
    var cacheStorageLocation: CacheManager.StorageLocation = CacheManager.StorageLocation.INTERNAL
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    var maxCacheSize: Long = CacheManager.DEFAULT_CACHE_SIZE
        set(value) {
            val clampedSize = value.coerceAtMost(CacheManager.MAX_CACHE_SIZE)
            if (field != clampedSize) {
                field = clampedSize
                markUpdated()
            }
        }

    // Rendering settings
    var defaultQualityPreset: QualityPreset = QualityPreset.BALANCED
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    var batteryOptimizationEnabled: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    var adaptiveQualityEnabled: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    // Network settings
    var defaultGrid: String = "agni"
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    var connectionTimeout: Int = 30000
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    var maxBandwidth: Int = 1500
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    // UI settings
    var showSplashScreen: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    var minimizeToTray: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    var uiTheme: String = "Default"
        set(value) {
            if (field != value) {
                field = value
                markUpdated()
            }
        }

    init {
        loadDefaultConfiguration()
        loadConfigurationFromFile()
    }

    private fun loadDefaultConfiguration() {
        properties.apply {
            // Cache defaults
            setProperty("cache.storage.location", cacheStorageLocation.name)
            setProperty("cache.max.size", maxCacheSize.toString())

            // Rendering defaults
            setProperty("rendering.quality.preset", defaultQualityPreset.name)
            setProperty("rendering.battery.optimization", batteryOptimizationEnabled.toString())
            setProperty("rendering.adaptive.quality", adaptiveQualityEnabled.toString())

            // Network defaults
            setProperty("network.default.grid", defaultGrid)
            setProperty("network.connection.timeout", connectionTimeout.toString())
            setProperty("network.max.bandwidth", maxBandwidth.toString())

            // UI defaults
            setProperty("ui.show.splash", showSplashScreen.toString())
            setProperty("ui.minimize.to.tray", minimizeToTray.toString())
            setProperty("ui.theme", uiTheme)
        }

        LOGGER.info("Default configuration loaded")
    }

    private fun loadConfigurationFromFile() {
        try {
            // Create config directory if it doesn't exist
            val configDir = Paths.get(CONFIG_DIR.replace("\${user.home}", System.getProperty("user.home")))
            Files.createDirectories(configDir)

            configFile = configDir.resolve(CONFIG_FILE)

            configFile?.let { file ->
                if (Files.exists(file)) {
                    Files.newInputStream(file).use { input ->
                        properties.load(input)
                        applyLoadedProperties()
                        LOGGER.info("Configuration loaded from: $file")
                    }
                } else {
                    LOGGER.info("No existing configuration file found, using defaults")
                }
            }
        } catch (e: IOException) {
            LOGGER.log(Level.WARNING, "Failed to load configuration from file", e)
        }
    }

    private fun applyLoadedProperties() {
        // Apply cache settings with error handling
        properties.getProperty("cache.storage.location")?.let { storageLocation ->
            try {
                cacheStorageLocation = CacheManager.StorageLocation.valueOf(storageLocation)
            } catch (e: IllegalArgumentException) {
                LOGGER.warning("Invalid cache storage location: $storageLocation")
            }
        }

        properties.getProperty("cache.max.size")?.let { maxSizeStr ->
            try {
                maxCacheSize = maxSizeStr.toLong().let { size ->
                    if (size > CacheManager.MAX_CACHE_SIZE) {
                        LOGGER.warning("Max cache size clamped to: ${CacheManager.formatBytes(CacheManager.MAX_CACHE_SIZE)}")
                        CacheManager.MAX_CACHE_SIZE
                    } else size
                }
            } catch (e: NumberFormatException) {
                LOGGER.warning("Invalid max cache size: $maxSizeStr")
            }
        }

        // Apply rendering settings
        properties.getProperty("rendering.quality.preset")?.let { qualityPreset ->
            try {
                defaultQualityPreset = QualityPreset.valueOf(qualityPreset)
            } catch (e: IllegalArgumentException) {
                LOGGER.warning("Invalid quality preset: $qualityPreset")
            }
        }

        batteryOptimizationEnabled = properties.getProperty("rendering.battery.optimization", "false").toBoolean()
        adaptiveQualityEnabled = properties.getProperty("rendering.adaptive.quality", "true").toBoolean()

        // Apply network settings
        defaultGrid = properties.getProperty("network.default.grid", "agni")

        properties.getProperty("network.connection.timeout")?.let { timeoutStr ->
            try {
                connectionTimeout = timeoutStr.toInt()
            } catch (e: NumberFormatException) {
                LOGGER.warning("Invalid connection timeout: $timeoutStr")
            }
        }

        properties.getProperty("network.max.bandwidth")?.let { bandwidthStr ->
            try {
                maxBandwidth = bandwidthStr.toInt()
            } catch (e: NumberFormatException) {
                LOGGER.warning("Invalid max bandwidth: $bandwidthStr")
            }
        }

        // Apply UI settings
        showSplashScreen = properties.getProperty("ui.show.splash", "true").toBoolean()
        minimizeToTray = properties.getProperty("ui.minimize.to.tray", "true").toBoolean()
        uiTheme = properties.getProperty("ui.theme", "Default")
    }

    /**
     * Save configuration to file
     */
    fun save() {
        try {
            updatePropertiesFromCurrentValues()

            configFile?.let { file ->
                Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE).use { output ->
                    properties.store(output, "Second Life Viewer Configuration - ${Date()}")
                    LOGGER.info("Configuration saved to: $file")
                }
            }
        } catch (e: IOException) {
            LOGGER.log(Level.SEVERE, "Failed to save configuration", e)
        }
    }

    private fun updatePropertiesFromCurrentValues() {
        properties.apply {
            // Update cache properties
            setProperty("cache.storage.location", cacheStorageLocation.name)
            setProperty("cache.max.size", maxCacheSize.toString())

            // Update rendering properties
            setProperty("rendering.quality.preset", defaultQualityPreset.name)
            setProperty("rendering.battery.optimization", batteryOptimizationEnabled.toString())
            setProperty("rendering.adaptive.quality", adaptiveQualityEnabled.toString())

            // Update network properties
            setProperty("network.default.grid", defaultGrid)
            setProperty("network.connection.timeout", connectionTimeout.toString())
            setProperty("network.max.bandwidth", maxBandwidth.toString())

            // Update UI properties
            setProperty("ui.show.splash", showSplashScreen.toString())
            setProperty("ui.minimize.to.tray", minimizeToTray.toString())
            setProperty("ui.theme", uiTheme)
        }
    }

    // Update tracking
    private fun markUpdated() {
        hasUpdates = true
    }

    fun hasUpdates(): Boolean = hasUpdates

    fun markUpdatesApplied() {
        hasUpdates = false
    }

    // Utility methods using Kotlin features
    
    /**
     * Reset to factory defaults
     */
    fun resetToDefaults() {
        LOGGER.info("Resetting configuration to defaults")
        properties.clear()
        
        // Reset all properties to defaults
        cacheStorageLocation = CacheManager.StorageLocation.INTERNAL
        maxCacheSize = CacheManager.DEFAULT_CACHE_SIZE
        defaultQualityPreset = QualityPreset.BALANCED
        batteryOptimizationEnabled = false
        adaptiveQualityEnabled = true
        defaultGrid = "agni"
        connectionTimeout = 30000
        maxBandwidth = 1500
        showSplashScreen = true
        minimizeToTray = true
        uiTheme = "Default"
        
        loadDefaultConfiguration()
        markUpdated()
    }

    /**
     * Export configuration to map using Kotlin collections
     */
    fun exportToMap(): Map<String, Any> = mapOf(
        // Cache settings
        "cache.storage.location" to cacheStorageLocation.name,
        "cache.max.size" to maxCacheSize,

        // Rendering settings
        "rendering.quality.preset" to defaultQualityPreset.name,
        "rendering.battery.optimization" to batteryOptimizationEnabled,
        "rendering.adaptive.quality" to adaptiveQualityEnabled,

        // Network settings
        "network.default.grid" to defaultGrid,
        "network.connection.timeout" to connectionTimeout,
        "network.max.bandwidth" to maxBandwidth,

        // UI settings
        "ui.show.splash" to showSplashScreen,
        "ui.minimize.to.tray" to minimizeToTray,
        "ui.theme" to uiTheme
    )

    /**
     * Import configuration from map
     */
    fun importFromMap(config: Map<String, Any>) {
        config.forEach { (key, value) ->
            properties.setProperty(key, value.toString())
        }
        applyLoadedProperties()
        markUpdated()
        LOGGER.info("Configuration imported from map")
    }

    /**
     * Get configuration summary for display
     */
    fun getConfigurationSummary(): String = buildString {
        appendLine("Second Life Viewer Configuration Summary:")
        appendLine("  Cache: ${cacheStorageLocation.displayName}, ${CacheManager.formatBytes(maxCacheSize)}")
        appendLine("  Rendering: ${defaultQualityPreset.displayName}, Battery Optimization: $batteryOptimizationEnabled")
        appendLine("  Network: Grid=$defaultGrid, Bandwidth=${maxBandwidth} KB/s")
        appendLine("  UI: Theme=$uiTheme, Splash=$showSplashScreen")
    }


}