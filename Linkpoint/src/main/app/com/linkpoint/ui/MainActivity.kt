package com.linkpoint.ui

import android.content.Intent
import android.os.Bundle
import android.view.Choreographer
import android.view.SurfaceView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.linkpoint.R
import com.linkpoint.assets.CacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main activity with advanced Filament-based 3D rendering
 * 
 * Features quick access to cache management to prevent cache-related failures
 */
class MainActivity : AppCompatActivity() {
    
    companion object {
        init {
            // Load Filament native libraries
            Filament.init()
        }
    }
    
    private lateinit var surfaceView: SurfaceView
    private lateinit var choreographer: Choreographer
    private lateinit var displayHelper: DisplayHelper
    private lateinit var uiHelper: UiHelper
    
    private lateinit var engine: Engine
    private lateinit var renderer: Renderer
    private lateinit var scene: Scene
    private lateinit var view: View
    private lateinit var camera: Camera
    
    private var swapChain: SwapChain? = null
    
    private lateinit var cacheManager: CacheManager
    
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            choreographer.postFrameCallback(this)
            
            // Safely check both conditions before rendering
            val chain = swapChain
            if (chain != null && uiHelper.isReadyToRender) {
                // Render frame
                if (renderer.beginFrame(chain, frameTimeNanos)) {
                    renderer.render(view)
                    renderer.endFrame()
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        surfaceView = findViewById(R.id.surfaceView)
        choreographer = Choreographer.getInstance()
        displayHelper = DisplayHelper(this)
        cacheManager = CacheManager(this)
        
        setupFilament()
        setupSurface()
        setupNavigation()
        
        // Check cache health on startup
        checkCacheHealth()
    }
    
    private fun setupNavigation() {
        findViewById<ImageButton>(R.id.chatButton).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
        
        findViewById<ImageButton>(R.id.inventoryButton).setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
        
        findViewById<ImageButton>(R.id.mapButton).setOnClickListener {
            startActivity(Intent(this, MinimapActivity::class.java))
        }
        
        findViewById<ImageButton>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // Cache button - quick access to cache management
        findViewById<ImageButton>(R.id.cacheButton)?.setOnClickListener {
            showCacheQuickMenu()
        }
        
        // Long-press settings to go directly to cache settings
        findViewById<ImageButton>(R.id.settingsButton).setOnLongClickListener {
            showCacheQuickMenu()
            true
        }
    }
    
    private fun checkCacheHealth() {
        lifecycleScope.launch {
            try {
                val result = cacheManager.checkAndMaintainCache()
                if (!result.cacheHealthy) {
                    // Show warning to user
                    Toast.makeText(
                        this@MainActivity,
                        "⚠️ Cache maintenance performed: ${result.actionTaken}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                // Silently fail - don't disrupt user experience
            }
        }
    }
    
    private fun updateCacheStatusIndicator() {
        lifecycleScope.launch {
            try {
                val stats = cacheManager.getCacheStats()
                
                // Update FPS text to show cache status if there's an issue
                findViewById<TextView>(R.id.fpsText)?.let { fpsText ->
                    val currentText = fpsText.text.toString()
                    if (stats.isLowSpace || stats.usagePercent > 95) {
                        fpsText.text = "$currentText | Cache: ⚠️"
                    } else if (stats.usagePercent > 80) {
                        fpsText.text = "$currentText | Cache: ${stats.usagePercent}%"
                    }
                }
            } catch (e: Exception) {
                // Silently ignore
            }
        }
    }
    
    private fun showCacheQuickMenu() {
        lifecycleScope.launch {
            try {
                val stats = cacheManager.getCacheStats()
                
                val options = arrayOf(
                    "📊 Cache Status: ${stats.getFormattedTotalSize()} / ${stats.getFormattedMaxSize()} (${stats.usagePercent}%)",
                    "🗑️ Clear All Cache",
                    "🖼️ Clear Textures Only",
                    "⚙️ Cache Settings"
                )
                
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Quick Cache Management")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> showDetailedCacheInfo()
                            1 -> confirmAndClearCache()
                            2 -> clearTexturesOnly()
                            3 -> openCacheSettings()
                        }
                    }
                    .setNegativeButton("Close", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error reading cache", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showDetailedCacheInfo() {
        lifecycleScope.launch {
            try {
                val stats = cacheManager.getCacheStats()
                
                val message = buildString {
                    appendLine("📦 Total: ${stats.getFormattedTotalSize()} (${stats.totalFileCount} files)")
                    appendLine("📏 Maximum: ${stats.getFormattedMaxSize()}")
                    appendLine("📈 Usage: ${stats.usagePercent}%")
                    appendLine()
                    appendLine("🖼️ Textures: ${cacheManager.formatSize(stats.texturesSizeBytes)}")
                    appendLine("🧊 Meshes: ${cacheManager.formatSize(stats.meshesSizeBytes)}")
                    appendLine("🔊 Sounds: ${cacheManager.formatSize(stats.soundsSizeBytes)}")
                    appendLine()
                    appendLine("💾 Device Free: ${cacheManager.formatSize(stats.availableSpaceBytes)}")
                    if (stats.isLowSpace) {
                        appendLine()
                        appendLine("⚠️ WARNING: Device storage is low!")
                        appendLine("Consider clearing cache to improve performance.")
                    }
                    if (stats.usagePercent < 30 && stats.totalFileCount < 200) {
                        appendLine()
                        appendLine("💡 TIP: Your cache is small.")
                        appendLine("Increasing cache size may improve loading speed.")
                    }
                }
                
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Cache Details")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .setNeutralButton("Clear Cache") { _, _ ->
                        confirmAndClearCache()
                    }
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun confirmAndClearCache() {
        AlertDialog.Builder(this)
            .setTitle("Clear All Cache?")
            .setMessage("This will delete all cached textures, meshes, and sounds.\n\nAssets will need to be re-downloaded on next use.\n\nContinue?")
            .setPositiveButton("Clear") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val result = cacheManager.clearAllCache()
                        Toast.makeText(
                            this@MainActivity,
                            "✓ Cleared ${cacheManager.formatSize(result.clearedBytes)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Error clearing cache", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun clearTexturesOnly() {
        lifecycleScope.launch {
            try {
                val result = cacheManager.clearCache(com.linkpoint.assets.CacheableAssetType.TEXTURES)
                Toast.makeText(
                    this@MainActivity,
                    "✓ Cleared ${cacheManager.formatSize(result.clearedBytes)} of textures",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error clearing textures", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun openCacheSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        // Could add extra to scroll to cache section
        startActivity(intent)
    }
    
    private fun setupTouchControls() {
        // Add touch handling for camera control
        surfaceView.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                android.view.MotionEvent.ACTION_MOVE -> {
                    // Pan/rotate camera based on touch
                    // This would be connected to camera controls
                }
            }
            true
        }
    }
    
    private fun setupFilament() {
        // Create Filament engine with best available backend
        engine = Engine.create(Engine.Backend.OPENGL)
        renderer = engine.createRenderer()
        scene = engine.createScene()
        view = engine.createView()
        camera = engine.createCamera(engine.entityManager.create())
        
        // Configure view with advanced rendering options
        view.scene = scene
        view.camera = camera
        
        // Enable post-processing effects
        view.isPostProcessingEnabled = true
        
        // Configure anti-aliasing (TAA for best quality)
        view.antiAliasing = View.AntiAliasing.FXAA
        
        // Enable screen-space ambient occlusion
        view.ambientOcclusionOptions = view.ambientOcclusionOptions.apply {
            enabled = true
            radius = 0.3f
            power = 1.0f
            intensity = 1.0f
        }
        
        // Enable bloom effect for bright areas
        view.bloomOptions = view.bloomOptions.apply {
            enabled = true
            strength = 0.1f
            resolution = 360
        }
        
        // Set up the camera with safe aspect ratio (may be 0 before layout)
        val w = surfaceView.width
        val h = surfaceView.height
        val aspectRatio = if (h > 0) w.toFloat() / h.toFloat() else 16f / 9f  // Default to 16:9
        camera.setProjection(45.0, aspectRatio.toDouble(), 0.1, 1000.0, Camera.Fov.VERTICAL)
        camera.lookAt(
            0.0, 0.0, 5.0,    // eye position
            0.0, 0.0, 0.0,    // target
            0.0, 1.0, 0.0     // up vector
        )
        
        // Create default indirect light for PBR rendering
        val ibl = IndirectLight.Builder()
            .intensity(30000f)
            .build(engine)
        scene.indirectLight = ibl
        
        // Create a simple default scene
        createDefaultScene()
    }
    
    private fun createDefaultScene() {
        // Create a simple ground plane and sky
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Scene setup would go here
                // For now, we have a basic empty scene
            }
        }
    }
    
    private fun setupSurface() {
        uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
        uiHelper.renderCallback = object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: android.view.Surface) {
                swapChain?.let { engine.destroySwapChain(it) }
                swapChain = engine.createSwapChain(surface)
                // Safely attach display helper - surfaceView.display may be null on some devices
                surfaceView.display?.let { display ->
                    displayHelper.attach(renderer, display)
                }
            }
            
            override fun onDetachedFromSurface() {
                displayHelper.detach()
                swapChain?.let {
                    engine.destroySwapChain(it)
                    engine.flushAndWait()
                    swapChain = null
                }
            }
            
            override fun onResized(width: Int, height: Int) {
                val aspectRatio = width.toFloat() / height.toFloat()
                camera.setProjection(45.0, aspectRatio.toDouble(), 0.1, 1000.0, Camera.Fov.VERTICAL)
                view.viewport = Viewport(0, 0, width, height)
            }
        }
        
        uiHelper.attachTo(surfaceView)
    }
    
    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(frameCallback)
        // Update cache status indicator
        updateCacheStatusIndicator()
    }
    
    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameCallback)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Clean up Filament resources
        choreographer.removeFrameCallback(frameCallback)
        
        uiHelper.detach()
        
        engine.destroyRenderer(renderer)
        engine.destroyView(view)
        engine.destroyScene(scene)
        engine.destroyCameraComponent(camera.entity)
        engine.entityManager.destroy(camera.entity)
        
        swapChain?.let { engine.destroySwapChain(it) }
        
        engine.destroy()
    }
}
