package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.RenderableManager.PrimitiveType
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * FilamentWorldRenderer - Renders the virtual world using Filament
 * 
 * This class manages scene content, including terrain, objects, avatars, etc.
 */
class FilamentWorldRenderer(
    private val context: Context,
    private val renderContext: FilamentRenderContext
) {
    
    companion object {
        private const val TAG = "FilamentWorldRenderer"
    }
    
    private val engine: Engine
        get() = renderContext.engine
    
    private val scene: Scene
        get() = renderContext.scene
    
    private val camera: Camera
        get() = renderContext.camera
    
    // Camera state
    private var cameraPosition = LLVector3(128f, 128f, 25f)
    private var cameraRotationX = 0f
    private var cameraRotationY = 0f
    
    // Managers
    private lateinit var materialManager: FilamentMaterialManager
    private lateinit var textureManager: FilamentTextureManager
    private lateinit var lightingManager: FilamentLightingManager
    private lateinit var worldDataBridge: FilamentWorldDataBridge
    private lateinit var terrainRenderer: FilamentTerrainRenderer
    private lateinit var gltfLoader: FilamentGltfLoader
    private lateinit var avatarRenderer: FilamentAvatarRenderer
    private lateinit var performanceOptimizer: FilamentPerformanceOptimizer
    private var lastFrameTimeNs: Long = -1L
    
    // Test renderable (a simple triangle for now)
    @Entity private var testRenderable = 0
    private var testMaterial: Material? = null
    private var testVertexBuffer: VertexBuffer? = null
    private var testIndexBuffer: IndexBuffer? = null
    
    /**
     * Initialize the world renderer and create initial scene content
     */
    fun initialize() {
        if (!renderContext.isInitialized) {
            throw IllegalStateException("FilamentRenderContext must be initialized first")
        }
        
        try {
            // Initialize material manager
            materialManager = FilamentMaterialManager(context, engine)
            materialManager.preloadMaterials()
            
            // Initialize texture manager
            textureManager = FilamentTextureManager(context, engine)
            
            // Initialize lighting manager
            lightingManager = FilamentLightingManager(engine, scene)
            lightingManager.initialize()
            
            // Initialize terrain renderer
            terrainRenderer = FilamentTerrainRenderer(engine, scene, materialManager)
            terrainRenderer.initialize()
            
            // Initialize glTF loader
            gltfLoader = FilamentGltfLoader(context, engine, scene)
            
            // Initialize avatar renderer
            avatarRenderer = FilamentAvatarRenderer(
                context, engine, scene, materialManager, textureManager, gltfLoader
            )
            avatarRenderer.initialize()
            
            // Initialize performance optimizer
            performanceOptimizer = FilamentPerformanceOptimizer(engine, scene)
            
            // Initialize world data bridge
            worldDataBridge = FilamentWorldDataBridge(context, renderContext, materialManager, avatarRenderer)
            
            // Create a simple test scene
            createTestTriangle()
            
            // Set initial camera position
            updateCameraTransform()
            
            Log.i(TAG, "FilamentWorldRenderer initialized with all managers")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize world renderer", e)
            throw e
        }
    }
    
    /**
     * Create a simple test triangle to verify rendering works
     */
    private fun createTestTriangle() {
        // Get material from material manager
        testMaterial = materialManager.getMaterial(FilamentMaterialManager.MaterialType.UNLIT_COLOR)
        
        // Create vertex and index buffers
        createTriangleMesh()
        
        // Create renderable entity
        testRenderable = renderContext.entityManager.create()
        
        // Build the renderable
        RenderableManager.Builder(1)
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 5.0f, 5.0f, 0.1f))
            .geometry(0, PrimitiveType.TRIANGLES, testVertexBuffer!!, testIndexBuffer!!, 0, 3)
            .material(0, testMaterial!!.defaultInstance)
            .build(engine, testRenderable)
        
        // Add to scene
        scene.addEntity(testRenderable)
        
        Log.i(TAG, "Test triangle created")
    }
    
    /**
     * Create a simple unlit material using MaterialBuilder
     * 
     * This uses runtime material compilation. In production, materials should be:
     * 1. Written as .mat files
     * 2. Compiled offline with matc tool to .filamat
     * 3. Loaded from assets at runtime
     * 
     * Runtime compilation is slower but works for testing.
     */
    private fun createSimpleMaterial(): Material {
        try {
            // Initialize MaterialBuilder (only needed once per process)
            MaterialBuilder.init()
            
            // Build a simple unlit material with vertex colors
            val materialPackage = MaterialBuilder()
                .platform(MaterialBuilder.Platform.MOBILE)
                .name("UnlitVertexColor")
                .shading(MaterialBuilder.Shading.UNLIT)
                .require(MaterialBuilder.VertexAttribute.COLOR)
                .material("""
                    void material(inout MaterialInputs material) {
                        prepareMaterial(material);
                        material.baseColor = getColor();
                    }
                """.trimIndent())
                .optimization(MaterialBuilder.Optimization.NONE)
                .build(engine.backend)
            
            if (!materialPackage.isValid) {
                throw RuntimeException("Generated invalid material package")
            }
            
            val material = Material.Builder()
                .payload(materialPackage.buffer, materialPackage.size)
                .build(engine)
            
            // Cleanup MaterialBuilder resources
            MaterialBuilder.shutdown()
            
            Log.i(TAG, "Created runtime-compiled unlit material with vertex colors")
            return material
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create material with MaterialBuilder", e)
            throw RuntimeException("Unable to create material for rendering", e)
        }
    }
    
    /**
     * Create triangle mesh geometry
     */
    private fun createTriangleMesh() {
        val floatSize = 4
        val intSize = 4
        val shortSize = 2
        val vertexSize = 3 * floatSize + intSize // position (xyz) + color (rgba)
        
        // Define vertices
        data class Vertex(val x: Float, val y: Float, val z: Float, val color: Int)
        
        fun ByteBuffer.putVertex(v: Vertex): ByteBuffer {
            putFloat(v.x)
            putFloat(v.y)
            putFloat(v.z)
            putInt(v.color)
            return this
        }
        
        // Create a triangle at the origin
        val vertexCount = 3
        val a1 = PI * 2.0 / 3.0
        val a2 = PI * 4.0 / 3.0
        val scale = 5.0f
        
        val vertexData = ByteBuffer.allocateDirect(vertexCount * vertexSize)
            .order(ByteOrder.nativeOrder())
            .putVertex(Vertex(scale * 1.0f, scale * 0.0f, 0.0f, 0xffff0000.toInt()))
            .putVertex(Vertex(scale * cos(a1).toFloat(), scale * sin(a1).toFloat(), 0.0f, 0xff00ff00.toInt()))
            .putVertex(Vertex(scale * cos(a2).toFloat(), scale * sin(a2).toFloat(), 0.0f, 0xff0000ff.toInt()))
            .flip()
        
        // Create vertex buffer
        testVertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertexCount)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, vertexSize)
            .attribute(VertexAttribute.COLOR, 0, AttributeType.UBYTE4, 3 * floatSize, vertexSize)
            .normalized(VertexAttribute.COLOR)
            .build(engine)
        
        testVertexBuffer!!.setBufferAt(engine, 0, vertexData)
        
        // Create index buffer
        val indexData = ByteBuffer.allocateDirect(vertexCount * shortSize)
            .order(ByteOrder.nativeOrder())
            .putShort(0)
            .putShort(1)
            .putShort(2)
            .flip()
        
        testIndexBuffer = IndexBuffer.Builder()
            .indexCount(3)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        
        testIndexBuffer!!.setBuffer(engine, indexData)
    }
    
    /**
     * Update camera position and orientation
     */
    fun setCameraPosition(position: LLVector3, rotationX: Float, rotationY: Float) {
        cameraPosition = position
        cameraRotationX = rotationX
        cameraRotationY = rotationY
        updateCameraTransform()
    }
    
    /**
     * Update camera transform based on position and rotation
     */
    private fun updateCameraTransform() {
        val transform = FloatArray(16)
        
        // Create view matrix (camera transform)
        // For now, just position the camera looking at the origin
        val eye = doubleArrayOf(
            cameraPosition.x.toDouble(),
            cameraPosition.y.toDouble(),
            cameraPosition.z.toDouble()
        )
        val center = doubleArrayOf(0.0, 0.0, 0.0)
        val up = doubleArrayOf(0.0, 0.0, 1.0)
        
        camera.lookAt(
            eye[0], eye[1], eye[2],
            center[0], center[1], center[2],
            up[0], up[1], up[2]
        )
    }
    
    /**
     * Connect to world data sources
     */
    fun connectToWorldData(
        objectsManager: ObjectsManager? = null,
        userManager: UserManager? = null,
        terrainData: TerrainData? = null
    ) {
        objectsManager?.let { worldDataBridge.setObjectsManager(it) }
        userManager?.let { worldDataBridge.setUserManager(it) }
        terrainData?.let { worldDataBridge.setTerrainData(it) }
        
        // Start syncing
        worldDataBridge.startSync()
        
        lastFrameTimeNs = -1L
        Log.i(TAG, "Connected to world data sources")
    }

    fun onFrame(frameTimeNanos: Long) {
        if (!::avatarRenderer.isInitialized) return
        if (lastFrameTimeNs < 0L) {
            lastFrameTimeNs = frameTimeNanos
            return
        }

        val deltaSeconds = (frameTimeNanos - lastFrameTimeNs) / 1_000_000_000f
        lastFrameTimeNs = frameTimeNanos
        if (deltaSeconds.isFinite() && deltaSeconds > 0f) {
            avatarRenderer.tick(deltaSeconds.coerceIn(0f, 0.25f))
        }
    }
    
    /**
     * Disconnect from world data
     */
    fun disconnectFromWorldData() {
        worldDataBridge.stopSync()
        worldDataBridge.clearAll()
        Log.i(TAG, "Disconnected from world data")
    }
    
    /**
     * Get the world data bridge for direct access
     */
    fun getWorldDataBridge(): FilamentWorldDataBridge = worldDataBridge
    
    /**
     * Get the lighting manager for direct access
     */
    fun getLightingManager(): FilamentLightingManager = lightingManager
    
    /**
     * Get the texture manager for direct access
     */
    fun getTextureManager(): FilamentTextureManager = textureManager
    
    /**
     * Get the material manager for direct access
     */
    fun getMaterialManager(): FilamentMaterialManager = materialManager
    
    /**
     * Get the terrain renderer for direct access
     */
    fun getTerrainRenderer(): FilamentTerrainRenderer = terrainRenderer
    
    /**
     * Load and display terrain
     */
    fun loadTerrain(terrainData: TerrainData) {
        terrainRenderer.updateTerrain(terrainData)
        Log.i(TAG, "Terrain loaded and displayed")
    }
    
    /**
     * Get the glTF loader for direct access
     */
    fun getGltfLoader(): FilamentGltfLoader = gltfLoader
    
    /**
     * Get the avatar renderer for direct access
     */
    fun getAvatarRenderer(): FilamentAvatarRenderer = avatarRenderer
    
    /**
     * Get the performance optimizer for direct access
     */
    fun getPerformanceOptimizer(): FilamentPerformanceOptimizer = performanceOptimizer
    
    /**
     * Cleanup renderer resources
     */
    fun destroy() {
        try {
            // Remove from scene
            if (testRenderable != 0) {
                scene.removeEntity(testRenderable)
                engine.destroyEntity(testRenderable)
                renderContext.entityManager.destroy(testRenderable)
            }
            
            // Destroy buffers (but not materials - manager handles those)
            testVertexBuffer?.let { engine.destroyVertexBuffer(it) }
            testIndexBuffer?.let { engine.destroyIndexBuffer(it) }
            
            // Destroy managers in reverse order
            if (::worldDataBridge.isInitialized) {
                worldDataBridge.destroy()
            }
            if (::avatarRenderer.isInitialized) {
                avatarRenderer.destroy()
            }
            if (::gltfLoader.isInitialized) {
                gltfLoader.destroy()
            }
            if (::terrainRenderer.isInitialized) {
                terrainRenderer.destroy()
            }
            if (::lightingManager.isInitialized) {
                lightingManager.destroy()
            }
            if (::textureManager.isInitialized) {
                textureManager.destroy()
            }
            if (::materialManager.isInitialized) {
                materialManager.destroy()
            }
            
            lastFrameTimeNs = -1L
            Log.i(TAG, "FilamentWorldRenderer destroyed")
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying world renderer", e)
        }
    }
}
