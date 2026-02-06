/*
 * Second Life Viewer - Kotlin implementation of the main viewer application
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Kotlin implementation Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.app

import kotlinx.coroutines.*
import lindenlab.llsd.viewer.secondlife.cache.CacheManager
import lindenlab.llsd.viewer.secondlife.cache.CacheStatistics
import lindenlab.llsd.viewer.secondlife.config.ViewerConfiguration
import lindenlab.llsd.viewer.secondlife.rendering.AdvancedRenderingSystem
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Main Second Life Viewer application (Kotlin implementation).
 * 
 * Features:
 * - Coroutine-based async operations
 * - Reactive configuration with Kotlin properties
 * - Comprehensive viewer subsystem coordination
 * - Battery conservation and performance optimization
 */
class SecondLifeViewer {
    companion object {
        private val LOGGER = Logger.getLogger(SecondLifeViewer::class.java.name)
        const val VERSION = "1.0.0-Kotlin"
        const val BUILD_DATE = "2024-12-19"
        const val USER_AGENT = "SecondLife-Kotlin/$VERSION"
        
        /**
         * Main entry point for the Kotlin application
         */
        @JvmStatic
        fun main(args: Array<String>) {
            // Set logging format
            System.setProperty("java.util.logging.SimpleFormatter.format", "[%1\$tT] %4\$s: %5\$s%6\$s%n")

            LOGGER.info("Starting Second Life Viewer Application (Kotlin)")

            runBlocking {
                val viewer = SecondLifeViewer()

                // Parse command line arguments
                val config = ViewerConfiguration.fromCommandLineArgs(args)

                try {
                    // Initialize and start viewer
                    if (viewer.initialize(config)) {
                        viewer.start()
                        LOGGER.info("Viewer is now running. Press Ctrl+C to exit.")

                        // Keep running until interrupted
                        try {
                            while (viewer.isRunning()) {
                                delay(1000)
                            }
                        } catch (e: CancellationException) {
                            LOGGER.info("Application interrupted, shutting down...")
                        }
                    } else {
                        LOGGER.severe("Failed to initialize viewer")
                        kotlin.system.exitProcess(1)
                    }
                } catch (e: Exception) {
                    LOGGER.log(Level.SEVERE, "Failed to start viewer", e)
                    kotlin.system.exitProcess(1)
                } finally {
                    viewer.shutdown()
                }
            }
        }

        /**
         * Create a viewer instance with specific configuration
         */
        suspend fun createViewer(
            cacheLocation: CacheManager.StorageLocation = CacheManager.StorageLocation.INTERNAL,
            cacheSize: Long = CacheManager.DEFAULT_CACHE_SIZE,
            qualityPreset: ViewerConfiguration.QualityPreset = ViewerConfiguration.QualityPreset.BALANCED,
            batteryOptimization: Boolean = false
        ): SecondLifeViewer {
            val config = ViewerConfiguration().apply {
                cacheStorageLocation = cacheLocation
                maxCacheSize = cacheSize
                defaultQualityPreset = qualityPreset
                batteryOptimizationEnabled = batteryOptimization
            }

            val viewer = SecondLifeViewer()
            if (!viewer.initialize(config)) {
                throw RuntimeException("Failed to initialize viewer")
            }

            return viewer
        }
    }

    // Core subsystems
    private var cacheManager: CacheManager? = null
    private var renderingSystem: AdvancedRenderingSystem? = null
    private var configuration: ViewerConfiguration? = null

    // Application state using atomic primitives
    private val initialized = AtomicBoolean(false)
    private val running = AtomicBoolean(false)
    private val shuttingDown = AtomicBoolean(false)

    // Statistics and monitoring
    private val applicationStats = ConcurrentHashMap<String, Any>()
    private val startTime = AtomicLong(System.currentTimeMillis())
    private val frameCount = AtomicLong(0)
    private var averageFPS: Double = 0.0

    // Coroutine scopes using structured concurrency
    private val viewerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val performanceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        LOGGER.info("Initializing Second Life Viewer $VERSION")
    }

    /**
     * Initialize the viewer with default configuration
     */
    suspend fun initialize(): Boolean = initialize(ViewerConfiguration())

    /**
     * Initialize the viewer with custom configuration using coroutines
     */
    suspend fun initialize(config: ViewerConfiguration): Boolean = withContext(Dispatchers.IO) {
        try {
            if (initialized.get()) {
                LOGGER.warning("Viewer already initialized")
                return@withContext true
            }

            LOGGER.info("Starting viewer initialization...")
            configuration = config

            // Initialize subsystems concurrently
            val initJobs = listOf(
                async { initializeCacheSystem() },
                async { initializeRenderingSystem() }
            )

            // Wait for all initialization to complete
            initJobs.awaitAll()

            // Start performance monitoring
            startPerformanceMonitoring()

            // Register shutdown hook
            registerShutdownHook()

            initialized.set(true)
            LOGGER.info("Viewer initialization complete")
            true

        } catch (e: Exception) {
            LOGGER.log(Level.SEVERE, "Failed to initialize viewer", e)
            false
        }
    }

    private suspend fun initializeCacheSystem() = withContext(Dispatchers.IO) {
        val config = configuration ?: return@withContext

        // Initialize cache with configured settings
        val location = config.cacheStorageLocation
        val maxSize = config.maxCacheSize

        cacheManager = CacheManager(location, maxSize)

        LOGGER.info("Cache system initialized: ${location.displayName}, ${CacheManager.formatBytes(maxSize)}")
    }

    private suspend fun initializeRenderingSystem() = withContext(Dispatchers.Default) {
        val config = configuration ?: return@withContext

        renderingSystem = AdvancedRenderingSystem().apply {
            // Apply configured rendering settings
            if (config.batteryOptimizationEnabled) {
                isBatteryConservationMode = true
            }

            // Apply quality preset
            when (config.defaultQualityPreset) {
                ViewerConfiguration.QualityPreset.ULTRA_LOW -> applyUltraLowPreset()
                ViewerConfiguration.QualityPreset.LOW -> applyLowPreset()
                ViewerConfiguration.QualityPreset.BALANCED -> applyBalancedPreset()
                ViewerConfiguration.QualityPreset.HIGH -> applyHighPreset()
                ViewerConfiguration.QualityPreset.ULTRA -> applyUltraPreset()
            }
        }

        LOGGER.info("Rendering system initialized")
    }

    private fun startPerformanceMonitoring() {
        // Performance statistics update every second
        performanceScope.launch {
            while (isActive && !shuttingDown.get()) {
                updatePerformanceStatistics()
                delay(1000)
            }
        }

        // Maintenance tasks every minute
        performanceScope.launch {
            while (isActive && !shuttingDown.get()) {
                delay(60000)
                performMaintenanceTasks()
            }
        }

        LOGGER.info("Performance monitoring started")
    }

    /**
     * Start the main viewer loop using coroutines
     */
    fun start() {
        if (!initialized.get()) {
            throw IllegalStateException("Viewer not initialized")
        }

        if (running.get()) {
            LOGGER.warning("Viewer already running")
            return
        }

        running.set(true)
        LOGGER.info("Starting Second Life Viewer")

        // Start main loop with coroutines
        viewerScope.launch {
            mainLoop()
        }

        LOGGER.info("Viewer started successfully")
    }

    /**
     * Main application loop using Kotlin coroutines
     */
    private suspend fun mainLoop() {
        while (running.get() && !shuttingDown.get()) {
            try {
                // Update frame counter
                frameCount.incrementAndGet()

                // Update rendering system
                renderingSystem?.let { rendering ->
                    if (rendering.isRenderingEnabled) {
                        rendering.updateAdaptiveQuality()
                    }
                }

                // Update statistics
                updateApplicationStatistics()

                // Frame rate control (~60 FPS)
                delay(16)

            } catch (e: Exception) {
                LOGGER.log(Level.WARNING, "Error in main loop", e)
            }
        }
    }

    private suspend fun updatePerformanceStatistics() {
        val currentTime = System.currentTimeMillis()
        val uptime = currentTime - startTime.get()

        // Calculate average FPS
        averageFPS = if (uptime > 0) {
            (frameCount.get() * 1000.0) / uptime
        } else 0.0

        // Update application statistics
        applicationStats.apply {
            put("uptime", uptime)
            put("frameCount", frameCount.get())
            put("averageFPS", averageFPS)
            put("memoryUsed", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
            put("maxMemory", Runtime.getRuntime().maxMemory())
        }

        // Log performance periodically
        if (frameCount.get() % 3600 == 0L) { // Every minute at 60 FPS
            LOGGER.info("Performance: %.1f FPS avg, %.1f MB memory".format(
                averageFPS,
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0))
            )
        }
    }

    private suspend fun performMaintenanceTasks() {
        try {
            val config = configuration ?: return

            // Check for configuration updates
            if (config.hasUpdates()) {
                applyConfigurationUpdates()
            }

            // Garbage collection hint (if memory pressure is high)
            val usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            val maxMemory = Runtime.getRuntime().maxMemory()
            if (usedMemory > maxMemory * 0.8) {
                System.gc()
                LOGGER.fine("Performed garbage collection (memory pressure)")
            }

        } catch (e: Exception) {
            LOGGER.log(Level.WARNING, "Error in maintenance tasks", e)
        }
    }

    private suspend fun updateApplicationStatistics() {
        // Update render statistics from rendering system
        renderingSystem?.let { rendering ->
            val renderStats = rendering.getRenderStatistics()
            applicationStats.putAll(renderStats)
        }

        // Update cache statistics
        cacheManager?.let { cache ->
            val cacheStats = cache.getStatistics()
            applicationStats.apply {
                put("cacheHitRatio", cacheStats.hitRatio)
                put("cacheSize", cacheStats.totalSize)
                put("cacheUtilization", cacheStats.usagePercent)
            }
        }
    }

    private suspend fun applyConfigurationUpdates() {
        val config = configuration ?: return
        val cache = cacheManager ?: return
        val rendering = renderingSystem ?: return

        LOGGER.info("Applying configuration updates")

        // Update cache configuration
        val currentStats = cache.getStatistics()
        if (config.cacheStorageLocation != currentStats.storageLocation) {
            cache.setStorageLocation(config.cacheStorageLocation)
        }

        if (config.maxCacheSize != currentStats.maxSize) {
            cache.setMaxCacheSize(config.maxCacheSize)
        }

        // Update rendering configuration
        rendering.isBatteryConservationMode = config.batteryOptimizationEnabled

        config.markUpdatesApplied()
    }

    // Public API methods using Kotlin properties and coroutines

    /**
     * Toggle rendering on/off for battery conservation
     */
    fun toggleRendering() {
        renderingSystem?.let { rendering ->
            val currentState = rendering.isRenderingEnabled
            rendering.isRenderingEnabled = !currentState

            val status = if (currentState) "disabled" else "enabled"
            LOGGER.info("Rendering $status by user request")
        }
    }

    /**
     * Set battery conservation mode
     */
    fun setBatteryConservationMode(enabled: Boolean) {
        renderingSystem?.let { rendering ->
            rendering.isBatteryConservationMode = enabled
        }
        configuration?.let { config ->
            config.batteryOptimizationEnabled = enabled
        }

        LOGGER.info("Battery conservation mode ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Get current application statistics
     */
    fun getStatistics(): Map<String, Any> = HashMap(applicationStats)

    /**
     * Get cache manager for external configuration
     */
    fun getCacheManager(): CacheManager? = cacheManager

    /**
     * Get rendering system for external configuration
     */
    fun getRenderingSystem(): AdvancedRenderingSystem? = renderingSystem

    /**
     * Get configuration manager
     */
    fun getConfiguration(): ViewerConfiguration? = configuration

    /**
     * Check if viewer is running
     */
    fun isRunning(): Boolean = running.get() && !shuttingDown.get()

    /**
     * Check if viewer is initialized
     */
    fun isInitialized(): Boolean = initialized.get()

    private fun registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(Thread {
            if (running.get() && !shuttingDown.get()) {
                LOGGER.info("Shutdown hook triggered")
                runBlocking { shutdown() }
            }
        })
    }

    /**
     * Shutdown the viewer gracefully using coroutines
     */
    suspend fun shutdown() = withContext(Dispatchers.IO) {
        if (shuttingDown.getAndSet(true)) return@withContext

        running.set(false)
        LOGGER.info("Shutting down Second Life Viewer...")

        try {
            // Shutdown rendering system
            renderingSystem?.shutdown()

            // Shutdown cache system
            cacheManager?.shutdown()

            // Save configuration
            configuration?.save()

            // Cancel coroutine scopes
            viewerScope.cancel()
            performanceScope.cancel()

            LOGGER.info("Second Life Viewer shutdown complete")

        } catch (e: Exception) {
            LOGGER.log(Level.SEVERE, "Error during shutdown", e)
        }
    }
}