package com.lumiyaviewer.lumiya.render.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.RenderableManager.PrimitiveType
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
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
            // Create a simple test scene
            createTestTriangle()
            
            // Set initial camera position
            updateCameraTransform()
            
            Log.i(TAG, "FilamentWorldRenderer initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize world renderer", e)
            throw e
        }
    }
    
    /**
     * Create a simple test triangle to verify rendering works
     */
    private fun createTestTriangle() {
        // Create a simple colored material (inline material)
        testMaterial = createSimpleMaterial()
        
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
     * Create a simple unlit material
     */
    private fun createSimpleMaterial(): Material {
        // Create a simple unlit material using Filament's material builder
        // For now, we'll create a basic colored material inline
        val materialPackage = """
            material {
                name : SimpleMaterial,
                shadingModel : unlit,
                vertexDomain : world,
                parameters : [
                    { type : float3, name : baseColor }
                ],
                requires : [ color ]
            }
            
            fragment {
                void material(inout MaterialInputs material) {
                    prepareMaterial(material);
                    material.baseColor.rgb = getColor().rgb;
                }
            }
            
            vertex {
                void materialVertex(inout MaterialVertexInputs material) {
                }
            }
        """.trimIndent()
        
        // Note: In production, materials should be precompiled with matc
        // For now, we'll create a simple hardcoded material
        try {
            val material = Material.Builder()
                .payload(createBasicMaterialPayload(), 0)
                .build(engine)
            return material
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create material, using default", e)
            // Create a fallback material
            return createFallbackMaterial()
        }
    }
    
    /**
     * Create a fallback material when material compilation fails
     */
    private fun createFallbackMaterial(): Material {
        // Create the simplest possible material
        val packageData = ByteBuffer.allocateDirect(1024)
        packageData.put(0.toByte()) // Placeholder
        packageData.flip()
        
        return Material.Builder()
            .payload(packageData, packageData.remaining())
            .build(engine)
    }
    
    /**
     * Create a basic material payload
     * Note: This is a placeholder - real materials need to be compiled with matc
     */
    private fun createBasicMaterialPayload(): ByteBuffer {
        // This is a placeholder - in a real app, you would:
        // 1. Write material files (.mat)
        // 2. Compile them with matc tool to .filamat
        // 3. Load the .filamat files from assets
        val buffer = ByteBuffer.allocateDirect(1024)
        buffer.put(0.toByte())
        buffer.flip()
        return buffer
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
            
            // Destroy buffers
            testVertexBuffer?.let { engine.destroyVertexBuffer(it) }
            testIndexBuffer?.let { engine.destroyIndexBuffer(it) }
            testMaterial?.let { engine.destroyMaterial(it) }
            
            Log.i(TAG, "FilamentWorldRenderer destroyed")
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying world renderer", e)
        }
    }
}
