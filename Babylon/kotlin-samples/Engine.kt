package com.babylonkt.core

import com.babylonkt.graphics.GraphicsDevice
import com.babylonkt.math.Vector3
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Configuration for the Babylon Kotlin engine.
 */
data class EngineConfiguration(
    val width: Int,
    val height: Int,
    val antialias: Boolean = true,
    val preserveDrawingBuffer: Boolean = false,
    val stencil: Boolean = true,
    val targetFPS: Int = 60,
    val adaptToDeviceRatio: Boolean = true,
    val powerPreference: PowerPreference = PowerPreference.HIGH_PERFORMANCE
)

enum class PowerPreference {
    LOW_POWER,
    HIGH_PERFORMANCE,
    DEFAULT
}

/**
 * The main engine class responsible for managing the rendering lifecycle,
 * scenes, and graphics device.
 */
class Engine(
    private val config: EngineConfiguration,
    private val graphicsDevice: GraphicsDevice
) {
    // ==================== Properties ====================
    
    private val scenes = mutableListOf<Scene>()
    private var activeScene: Scene? = null
    
    private var isRunning = false
    private var renderJob: Job? = null
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Performance monitoring
    private var frameCount = 0
    private var lastFPSUpdate = System.currentTimeMillis()
    private var currentFPS = 0f
    
    // Timing
    private val targetFrameTime = 1000f / config.targetFPS
    private var lastFrameTime = System.currentTimeMillis()
    private var deltaTime = 0f
    
    // State
    var width: Int = config.width
        private set
    var height: Int = config.height
        private set
    
    var isDisposed = false
        private set
    
    // ==================== Initialization ====================
    
    init {
        graphicsDevice.initialize(config)
        println("Babylon Kotlin Engine initialized")
        println("  Resolution: ${config.width}x${config.height}")
        println("  Antialiasing: ${config.antialias}")
        println("  Target FPS: ${config.targetFPS}")
    }
    
    // ==================== Scene Management ====================
    
    /**
     * Creates a new scene and adds it to the engine.
     */
    fun createScene(): Scene {
        val scene = Scene(this)
        scenes.add(scene)
        if (activeScene == null) {
            activeScene = scene
        }
        return scene
    }
    
    /**
     * Sets the active scene to be rendered.
     */
    fun setActiveScene(scene: Scene) {
        require(scenes.contains(scene)) { "Scene is not registered with this engine" }
        activeScene = scene
    }
    
    /**
     * Removes a scene from the engine.
     */
    fun removeScene(scene: Scene) {
        scenes.remove(scene)
        if (activeScene == scene) {
            activeScene = scenes.firstOrNull()
        }
        scene.dispose()
    }
    
    // ==================== Render Loop ====================
    
    /**
     * Starts the render loop with a custom render function.
     */
    fun runRenderLoop(renderFunction: () -> Unit) {
        if (isRunning) {
            println("Warning: Render loop is already running")
            return
        }
        
        isRunning = true
        renderJob = scope.launch {
            while (isRunning && !isDisposed) {
                val frameStartTime = System.currentTimeMillis()
                
                // Calculate delta time
                deltaTime = (frameStartTime - lastFrameTime) / 1000f
                lastFrameTime = frameStartTime
                
                // Execute render function
                withContext(Dispatchers.Main) {
                    try {
                        renderFunction()
                    } catch (e: Exception) {
                        println("Error in render loop: ${e.message}")
                        e.printStackTrace()
                    }
                }
                
                // Update FPS counter
                updateFPS()
                
                // Frame rate limiting
                val frameTime = System.currentTimeMillis() - frameStartTime
                val sleepTime = (targetFrameTime - frameTime).toLong()
                if (sleepTime > 0) {
                    delay(sleepTime)
                }
            }
        }
    }
    
    /**
     * Starts the render loop using the active scene.
     */
    fun runRenderLoop() {
        runRenderLoop {
            activeScene?.render()
        }
    }
    
    /**
     * Stops the render loop.
     */
    fun stopRenderLoop() {
        isRunning = false
        renderJob?.cancel()
        renderJob = null
    }
    
    // ==================== Rendering ====================
    
    /**
     * Renders a single frame of the active scene.
     */
    fun render() {
        if (isDisposed) {
            println("Warning: Cannot render - engine is disposed")
            return
        }
        
        activeScene?.render()
    }
    
    /**
     * Clears the screen with the specified color.
     */
    fun clear(r: Float = 0f, g: Float = 0f, b: Float = 0f, a: Float = 1f) {
        graphicsDevice.clear(r, g, b, a)
    }
    
    // ==================== Window Management ====================
    
    /**
     * Resizes the rendering viewport.
     */
    fun resize(newWidth: Int, newHeight: Int) {
        width = newWidth
        height = newHeight
        graphicsDevice.resize(newWidth, newHeight)
        
        // Update all cameras in all scenes
        scenes.forEach { scene ->
            scene.cameras.forEach { camera ->
                camera.onResize(newWidth, newHeight)
            }
        }
        
        println("Engine resized to ${newWidth}x${newHeight}")
    }
    
    // ==================== Performance Monitoring ====================
    
    private fun updateFPS() {
        frameCount++
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - lastFPSUpdate
        
        if (elapsed >= 1000) {
            currentFPS = (frameCount * 1000f) / elapsed
            frameCount = 0
            lastFPSUpdate = currentTime
        }
    }
    
    /**
     * Returns the current frames per second.
     */
    fun getFPS(): Float = currentFPS
    
    /**
     * Returns the delta time (time since last frame in seconds).
     */
    fun getDeltaTime(): Float = deltaTime
    
    /**
     * Returns performance statistics.
     */
    fun getPerformanceStats(): PerformanceStats {
        return PerformanceStats(
            fps = currentFPS,
            deltaTime = deltaTime,
            sceneCount = scenes.size,
            drawCalls = graphicsDevice.getDrawCallCount(),
            triangles = graphicsDevice.getTriangleCount()
        )
    }
    
    // ==================== Resource Management ====================
    
    /**
     * Disposes of the engine and all its resources.
     */
    fun dispose() {
        if (isDisposed) return
        
        println("Disposing Babylon Kotlin Engine...")
        
        stopRenderLoop()
        
        // Dispose all scenes
        scenes.forEach { it.dispose() }
        scenes.clear()
        
        // Dispose graphics device
        graphicsDevice.dispose()
        
        // Cancel coroutine scope
        scope.cancel()
        
        isDisposed = true
        println("Engine disposed successfully")
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Takes a screenshot of the current frame.
     */
    suspend fun captureScreenshot(): ByteArray = withContext(Dispatchers.IO) {
        graphicsDevice.captureScreenshot()
    }
    
    /**
     * Returns the graphics device for advanced operations.
     */
    fun getGraphicsDevice(): GraphicsDevice = graphicsDevice
    
    override fun toString(): String {
        return "Engine(resolution=${width}x${height}, fps=$currentFPS, scenes=${scenes.size})"
    }
}

/**
 * Performance statistics for the engine.
 */
data class PerformanceStats(
    val fps: Float,
    val deltaTime: Float,
    val sceneCount: Int,
    val drawCalls: Int,
    val triangles: Int
) {
    override fun toString(): String {
        return """
            Performance Stats:
              FPS: ${"%.1f".format(fps)}
              Delta Time: ${"%.3f".format(deltaTime)}s
              Scenes: $sceneCount
              Draw Calls: $drawCalls
              Triangles: $triangles
        """.trimIndent()
    }
}

/**
 * Builder for creating an Engine instance.
 */
class EngineBuilder {
    private var width: Int = 1280
    private var height: Int = 720
    private var antialias: Boolean = true
    private var preserveDrawingBuffer: Boolean = false
    private var stencil: Boolean = true
    private var targetFPS: Int = 60
    private var adaptToDeviceRatio: Boolean = true
    private var powerPreference: PowerPreference = PowerPreference.HIGH_PERFORMANCE
    
    fun setResolution(width: Int, height: Int) = apply {
        this.width = width
        this.height = height
    }
    
    fun setAntialias(enabled: Boolean) = apply {
        this.antialias = enabled
    }
    
    fun setPreserveDrawingBuffer(enabled: Boolean) = apply {
        this.preserveDrawingBuffer = enabled
    }
    
    fun setStencil(enabled: Boolean) = apply {
        this.stencil = enabled
    }
    
    fun setTargetFPS(fps: Int) = apply {
        this.targetFPS = fps
    }
    
    fun setAdaptToDeviceRatio(enabled: Boolean) = apply {
        this.adaptToDeviceRatio = enabled
    }
    
    fun setPowerPreference(preference: PowerPreference) = apply {
        this.powerPreference = preference
    }
    
    fun build(graphicsDevice: GraphicsDevice): Engine {
        val config = EngineConfiguration(
            width = width,
            height = height,
            antialias = antialias,
            preserveDrawingBuffer = preserveDrawingBuffer,
            stencil = stencil,
            targetFPS = targetFPS,
            adaptToDeviceRatio = adaptToDeviceRatio,
            powerPreference = powerPreference
        )
        return Engine(config, graphicsDevice)
    }
}

/**
 * DSL function for creating an engine.
 */
fun engine(
    graphicsDevice: GraphicsDevice,
    block: EngineBuilder.() -> Unit = {}
): Engine {
    return EngineBuilder().apply(block).build(graphicsDevice)
}