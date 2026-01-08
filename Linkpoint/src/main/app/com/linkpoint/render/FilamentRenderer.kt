package com.linkpoint.render

import android.content.Context
import android.util.Log
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Advanced 3D renderer using Google Filament engine
 * Provides physically-based rendering (PBR) for Second Life/OpenSim worlds
 */
class FilamentRenderer(private val context: Context) {
    
    companion object {
        private const val TAG = "FilamentRenderer"
        
        init {
            Filament.init()
        }
    }
    
    private lateinit var engine: Engine
    private lateinit var renderer: Renderer
    private lateinit var scene: Scene
    private lateinit var view: View
    private lateinit var camera: Camera
    
    private var swapChain: SwapChain? = null
    private lateinit var uiHelper: UiHelper
    private lateinit var displayHelper: DisplayHelper
    
    private var isInitialized = false
    
    // Camera settings
    private var cameraPosition = floatArrayOf(0f, 1.5f, 5f)
    private var cameraTarget = floatArrayOf(0f, 0f, 0f)
    private var cameraUp = floatArrayOf(0f, 1f, 0f)
    
    /**
     * Initialize the Filament renderer
     */
    fun initialize(surfaceView: SurfaceView) {
        Log.d(TAG, "Initializing Filament renderer")
        
        // Create engine with best available backend
        engine = Engine.create(Engine.Backend.OPENGL)
        renderer = engine.createRenderer()
        scene = engine.createScene()
        view = engine.createView()
        
        // Create camera
        val cameraEntity = engine.entityManager.create()
        camera = engine.createCamera(cameraEntity)
        
        // Configure view
        view.scene = scene
        view.camera = camera
        
        // Configure advanced rendering features
        configureAdvancedRendering()
        
        // Setup display helper
        displayHelper = DisplayHelper(context)
        
        // Setup UI helper
        uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
        uiHelper.renderCallback = createRenderCallback()
        uiHelper.attachTo(surfaceView)
        
        isInitialized = true
        Log.i(TAG, "Filament renderer initialized")
    }
    
    private fun configureAdvancedRendering() {
        // Post-processing
        view.isPostProcessingEnabled = true
        
        // Anti-aliasing - FXAA for performance, TAA for quality
        view.antiAliasing = View.AntiAliasing.FXAA
        
        // Screen-space ambient occlusion (SSAO)
        view.ambientOcclusionOptions = view.ambientOcclusionOptions.apply {
            enabled = true
            radius = 0.3f
            power = 1.0f
            bias = 0.0005f
            intensity = 1.0f
            quality = View.QualityLevel.MEDIUM
        }
        
        // Bloom for HDR highlights
        view.bloomOptions = view.bloomOptions.apply {
            enabled = true
            strength = 0.1f
            resolution = 360
            levels = 6
            blendMode = View.BloomOptions.BlendMode.ADD
        }
        
        // Fog for atmospheric effects
        view.fogOptions = view.fogOptions.apply {
            enabled = false  // Enable when needed
            density = 0.015f
            height = 0f
            heightFalloff = 1f
            inScatteringStart = 0f
            inScatteringSize = -1f
        }
        
        // Dynamic resolution for performance
        view.dynamicResolutionOptions = view.dynamicResolutionOptions.apply {
            enabled = true
            homogeneousScaling = false
            minScale = 0.5f
            maxScale = 1.0f
            quality = View.QualityLevel.MEDIUM
        }
        
        // Tone mapping
        view.colorGrading = ColorGrading.Builder()
            .toneMapping(ColorGrading.ToneMapping.ACES_LEGACY)
            .build(engine)
    }
    
    private fun createRenderCallback() = object : UiHelper.RendererCallback {
        override fun onNativeWindowChanged(surface: android.view.Surface) {
            swapChain?.let { engine.destroySwapChain(it) }
            swapChain = engine.createSwapChain(surface)
            // Note: displayHelper.attach requires a Display, not Surface
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
            val aspect = width.toFloat() / height.toFloat()
            camera.setProjection(
                45.0,          // FOV
                aspect.toDouble(),
                0.1,           // Near plane
                1000.0,        // Far plane
                Camera.Fov.VERTICAL
            )
            view.viewport = Viewport(0, 0, width, height)
        }
    }
    
    /**
     * Render a frame
     */
    fun render(frameTimeNanos: Long): Boolean {
        if (!isInitialized || swapChain == null) return false
        
        if (!uiHelper.isReadyToRender) return false
        
        // Update camera
        camera.lookAt(
            cameraPosition[0].toDouble(), cameraPosition[1].toDouble(), cameraPosition[2].toDouble(),
            cameraTarget[0].toDouble(), cameraTarget[1].toDouble(), cameraTarget[2].toDouble(),
            cameraUp[0].toDouble(), cameraUp[1].toDouble(), cameraUp[2].toDouble()
        )
        
        // Begin frame
        if (renderer.beginFrame(swapChain!!, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
            return true
        }
        
        return false
    }
    
    /**
     * Set camera position and orientation
     */
    fun setCamera(
        position: FloatArray,
        target: FloatArray,
        up: FloatArray = floatArrayOf(0f, 1f, 0f)
    ) {
        cameraPosition = position
        cameraTarget = target
        cameraUp = up
    }
    
    /**
     * Create a simple mesh entity
     */
    fun createMesh(
        vertices: FloatArray,
        indices: ShortArray,
        normals: FloatArray? = null,
        uvs: FloatArray? = null
    ): Int {
        val entity = engine.entityManager.create()
        
        // Create vertex buffer
        val vertexCount = vertices.size / 3
        val vb = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(if (normals != null && uvs != null) 3 else if (normals != null || uvs != null) 2 else 1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .apply {
                if (normals != null) {
                    attribute(VertexBuffer.VertexAttribute.TANGENTS, 1, VertexBuffer.AttributeType.FLOAT4, 0, 16)
                }
                if (uvs != null) {
                    attribute(VertexBuffer.VertexAttribute.UV0, if (normals != null) 2 else 1, VertexBuffer.AttributeType.FLOAT2, 0, 8)
                }
            }
            .build(engine)
        
        // Set vertex data
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
            .flip()
        
        vb.setBufferAt(engine, 0, vertexBuffer as ByteBuffer)
        
        // Create index buffer
        val ib = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        
        val indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indices)
            .flip()
        
        ib.setBuffer(engine, indexBuffer as ByteBuffer)
        
        // Create default material
        val material = createDefaultMaterial()
        
        // Create renderable
        RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib, 0, indices.size)
            .material(0, material.defaultInstance)
            .castShadows(true)
            .receiveShadows(true)
            .build(engine, entity)
        
        scene.addEntity(entity)
        
        return entity
    }
    
    /**
     * Create a default PBR material
     */
    private fun createDefaultMaterial(): Material {
        // Simple material data for a white diffuse material
        val materialData = context.assets.open("materials/default.filamat").use { 
            it.readBytes() 
        }
        
        return Material.Builder()
            .payload(ByteBuffer.wrap(materialData), materialData.size)
            .build(engine)
    }
    
    /**
     * Create indirect lighting for PBR
     */
    fun setupIndirectLight(intensity: Float = 30000f) {
        val ibl = IndirectLight.Builder()
            .intensity(intensity)
            .build(engine)
        
        scene.indirectLight = ibl
    }
    
    /**
     * Create a directional light (sun)
     */
    fun createSunLight(
        direction: FloatArray = floatArrayOf(0f, -1f, -0.8f),
        color: FloatArray = floatArrayOf(0.98f, 0.92f, 0.89f),
        intensity: Float = 100000f
    ): Int {
        val sunEntity = engine.entityManager.create()
        
        LightManager.Builder(LightManager.Type.SUN)
            .color(color[0], color[1], color[2])
            .intensity(intensity)
            .direction(direction[0], direction[1], direction[2])
            .castShadows(true)
            .sunAngularRadius(1.9f)
            .sunHaloSize(10.0f)
            .sunHaloFalloff(80.0f)
            .build(engine, sunEntity)
        
        scene.addEntity(sunEntity)
        
        return sunEntity
    }
    
    /**
     * Clean up resources
     */
    fun destroy() {
        if (!isInitialized) return
        
        Log.d(TAG, "Destroying Filament renderer")
        
        uiHelper.detach()
        
        engine.destroyRenderer(renderer)
        engine.destroyView(view)
        engine.destroyScene(scene)
        engine.destroyCameraComponent(camera.entity)
        engine.entityManager.destroy(camera.entity)
        
        swapChain?.let { engine.destroySwapChain(it) }
        
        engine.destroy()
        
        isInitialized = false
    }
}
