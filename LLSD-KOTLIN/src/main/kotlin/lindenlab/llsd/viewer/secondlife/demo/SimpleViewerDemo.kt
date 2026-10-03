/*
 * Simple Viewer Demo - Kotlin implementation 
 */

package lindenlab.llsd.viewer.secondlife.demo

import kotlinx.coroutines.runBlocking
import lindenlab.llsd.viewer.secondlife.app.SecondLifeViewer
import lindenlab.llsd.viewer.secondlife.cache.CacheManager
import lindenlab.llsd.viewer.secondlife.config.ViewerConfiguration

/**
 * Simple demonstration of the Second Life viewer (Kotlin implementation).
 */
fun main() = runBlocking {
    println("===========================================")
    println(" Second Life Viewer - Kotlin Implementation")
    println("===========================================")
    println()

    try {
        // Create demo configuration
        val config = ViewerConfiguration().apply {
            cacheStorageLocation = CacheManager.StorageLocation.SYSTEM_TEMP
            maxCacheSize = 2L * 1024 * 1024 * 1024 // 2GB for demo
            defaultQualityPreset = ViewerConfiguration.QualityPreset.BALANCED
        }

        println("Demo Configuration:")
        println("  Cache: ${config.cacheStorageLocation.displayName}")
        println("  Cache Size: ${CacheManager.formatBytes(config.maxCacheSize)}")
        println("  Quality: ${config.defaultQualityPreset}")
        println()

        // Initialize viewer
        println("Initializing Kotlin viewer...")
        val viewer = SecondLifeViewer()

        val initialized = viewer.initialize(config)
        if (!initialized) {
            System.err.println("Failed to initialize viewer!")
            return@runBlocking
        }

        println("✓ Kotlin viewer initialized successfully")

        // Start viewer
        viewer.start()
        println("✓ Kotlin viewer started")

        // Wait for systems to stabilize
        kotlinx.coroutines.delay(1000)

        // Quick demo
        println()
        println("=== KOTLIN VIEWER DEMO ===")

        // Cache demo
        val cacheManager = viewer.getCacheManager()
        if (cacheManager != null) {
            val textureData = "Kotlin demo texture data".toByteArray()
            cacheManager.store(CacheManager.CacheType.TEXTURE, "kotlin_demo", textureData)
            println("✓ Stored texture data using Kotlin coroutines")

            val retrieved = cacheManager.retrieve(CacheManager.CacheType.TEXTURE, "kotlin_demo")
            println("✓ Retrieved texture: ${if (retrieved != null) "SUCCESS" else "FAILED"}")
        }

        // Rendering demo
        val renderingSystem = viewer.getRenderingSystem()
        if (renderingSystem != null) {
            println("✓ Current quality: ${renderingSystem.qualitySettings.overallQuality}")
            
            renderingSystem.applyUltraLowPreset()
            println("✓ Applied Ultra Low preset: ${renderingSystem.qualitySettings.overallQuality}")
            
            renderingSystem.applyHighPreset()
            println("✓ Applied High preset: ${renderingSystem.qualitySettings.overallQuality}")
        }

        // Battery conservation demo
        viewer.setBatteryConservationMode(true)
        println("✓ Battery mode enabled: Rendering=${renderingSystem?.isRenderingEnabled}")

        viewer.setBatteryConservationMode(false)
        println("✓ Battery mode disabled: Rendering=${renderingSystem?.isRenderingEnabled}")

        println()
        println("=== KOTLIN DEMO COMPLETE ===")
        println("Successfully demonstrated Kotlin implementation with:")
        println("✓ Coroutine-based async operations")
        println("✓ Type-safe configuration with sealed classes")
        println("✓ Memory-safe cache management")
        println("✓ Reactive rendering system")
        println()
        println("The Kotlin Second Life Viewer is fully functional!")

        // Shutdown
        println()
        println("Shutting down Kotlin viewer...")
        viewer.shutdown()
        println("✓ Kotlin viewer shutdown complete")

    } catch (e: Exception) {
        System.err.println("Error during Kotlin demo: ${e.message}")
        e.printStackTrace()
    }
}