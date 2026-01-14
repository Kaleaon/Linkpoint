package com.linkpoint.render.prims

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

/**
 * Renders Second Life primitives (prims)
 * Supports box, cylinder, sphere, torus, and sculpted prims
 */
class PrimRenderer(
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "PrimRenderer"
        
        // Pcode types
        const val PCODE_PRIM = 9
        const val PCODE_AVATAR = 47
        const val PCODE_GRASS = 95
        const val PCODE_NEW_TREE = 111
        const val PCODE_PARTICLE = 143
        const val PCODE_TREE = 255
    }
    
    private val prims = ConcurrentHashMap<Int, PrimInstance>()
    private val primMeshes = ConcurrentHashMap<PrimShape, PrimMesh>()
    
    private var defaultMaterial: MaterialInstance? = null
    private val transformManager = engine.transformManager
    
    /**
     * Initialize with default material
     */
    fun initialize(material: Material) {
        defaultMaterial = material.createInstance()
        
        // Pre-generate common shapes
        generateBoxMesh()
        generateSphereMesh()
        generateCylinderMesh()
        
        Log.i(TAG, "PrimRenderer initialized")
    }
    
    /**
     * Add or update a prim from ObjectUpdate
     */
    fun updatePrim(data: ObjectUpdateData) {
        if (data.pcode != PCODE_PRIM) return
        
        val prim = prims.getOrPut(data.localId) {
            createPrim(data)
        }
        
        // Update transform
        prim.position = data.position
        prim.rotation = data.rotation
        prim.scale = data.scale
        updateTransform(prim)
        
        // Update material if texture changed
        if (!data.textureEntry.contentEquals(prim.textureEntry)) {
            prim.textureEntry = data.textureEntry
            updatePrimMaterial(prim, data.textureEntry)
        }
    }
    
    /**
     * Remove a prim
     */
    fun removePrim(localId: Int) {
        prims.remove(localId)?.let { prim ->
            scene.removeEntity(prim.entity)
            engine.destroyEntity(prim.entity)
        }
    }
    
    /**
     * Get prim by local ID
     */
    fun getPrim(localId: Int): PrimInstance? = prims[localId]
    
    /**
     * Get all prims
     */
    fun getAllPrims(): Collection<PrimInstance> = prims.values
    
    private fun createPrim(data: ObjectUpdateData): PrimInstance {
        val entity = EntityManager.get().create()
        
        // Determine shape from path/profile curves
        val shape = determineShape(data)
        val mesh = getOrCreateMesh(shape)
        
        // Create renderable
        RenderableManager.Builder(1)
            .boundingBox(Box(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, mesh.vertexBuffer, mesh.indexBuffer)
            .material(0, defaultMaterial!!)
            .culling(true)
            .receiveShadows(true)
            .castShadows(true)
            .build(engine, entity)
        
        // Add transform
        val ti = transformManager.create(entity)
        
        scene.addEntity(entity)
        
        return PrimInstance(
            localId = data.localId,
            fullId = data.fullId,
            entity = entity,
            transformInstance = ti,
            shape = shape,
            position = data.position,
            rotation = data.rotation,
            scale = data.scale,
            textureEntry = data.textureEntry
        )
    }
    
    private fun determineShape(data: ObjectUpdateData): PrimShape {
        // Simplified shape detection
        // Real implementation would parse path/profile data
        return PrimShape.BOX
    }
    
    private fun getOrCreateMesh(shape: PrimShape): PrimMesh {
        return primMeshes.getOrPut(shape) {
            when (shape) {
                PrimShape.BOX -> generateBoxMesh()
                PrimShape.SPHERE -> generateSphereMesh()
                PrimShape.CYLINDER -> generateCylinderMesh()
                PrimShape.TORUS -> generateTorusMesh()
                PrimShape.PRISM -> generatePrismMesh()
                PrimShape.RING -> generateRingMesh()
            }
        }
    }
    
    private fun generateBoxMesh(): PrimMesh {
        // 6 faces, 4 vertices each = 24 vertices
        // 6 faces, 2 triangles each = 36 indices
        val vertices = floatArrayOf(
            // Front face
            -0.5f, -0.5f,  0.5f,  0f,  0f,  1f,  0f, 0f,
             0.5f, -0.5f,  0.5f,  0f,  0f,  1f,  1f, 0f,
             0.5f,  0.5f,  0.5f,  0f,  0f,  1f,  1f, 1f,
            -0.5f,  0.5f,  0.5f,  0f,  0f,  1f,  0f, 1f,
            // Back face
            -0.5f, -0.5f, -0.5f,  0f,  0f, -1f,  1f, 0f,
            -0.5f,  0.5f, -0.5f,  0f,  0f, -1f,  1f, 1f,
             0.5f,  0.5f, -0.5f,  0f,  0f, -1f,  0f, 1f,
             0.5f, -0.5f, -0.5f,  0f,  0f, -1f,  0f, 0f,
            // Top face
            -0.5f,  0.5f, -0.5f,  0f,  1f,  0f,  0f, 0f,
            -0.5f,  0.5f,  0.5f,  0f,  1f,  0f,  0f, 1f,
             0.5f,  0.5f,  0.5f,  0f,  1f,  0f,  1f, 1f,
             0.5f,  0.5f, -0.5f,  0f,  1f,  0f,  1f, 0f,
            // Bottom face
            -0.5f, -0.5f, -0.5f,  0f, -1f,  0f,  0f, 1f,
             0.5f, -0.5f, -0.5f,  0f, -1f,  0f,  1f, 1f,
             0.5f, -0.5f,  0.5f,  0f, -1f,  0f,  1f, 0f,
            -0.5f, -0.5f,  0.5f,  0f, -1f,  0f,  0f, 0f,
            // Right face
             0.5f, -0.5f, -0.5f,  1f,  0f,  0f,  0f, 0f,
             0.5f,  0.5f, -0.5f,  1f,  0f,  0f,  0f, 1f,
             0.5f,  0.5f,  0.5f,  1f,  0f,  0f,  1f, 1f,
             0.5f, -0.5f,  0.5f,  1f,  0f,  0f,  1f, 0f,
            // Left face
            -0.5f, -0.5f, -0.5f, -1f,  0f,  0f,  1f, 0f,
            -0.5f, -0.5f,  0.5f, -1f,  0f,  0f,  0f, 0f,
            -0.5f,  0.5f,  0.5f, -1f,  0f,  0f,  0f, 1f,
            -0.5f,  0.5f, -0.5f, -1f,  0f,  0f,  1f, 1f
        )
        
        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3,       // front
            4, 5, 6, 4, 6, 7,       // back
            8, 9, 10, 8, 10, 11,    // top
            12, 13, 14, 12, 14, 15, // bottom
            16, 17, 18, 16, 18, 19, // right
            20, 21, 22, 20, 22, 23  // left
        )
        
        return createMesh(vertices, indices)
    }
    
    private fun generateSphereMesh(): PrimMesh {
        val segments = 24
        val rings = 16
        
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        
        for (y in 0..rings) {
            val phi = PI * y / rings
            
            for (x in 0..segments) {
                val theta = 2 * PI * x / segments
                
                val px = (sin(phi) * cos(theta) * 0.5).toFloat()
                val py = (cos(phi) * 0.5).toFloat()
                val pz = (sin(phi) * sin(theta) * 0.5).toFloat()
                
                // Normal (same as position for sphere)
                val nx = (sin(phi) * cos(theta)).toFloat()
                val ny = cos(phi).toFloat()
                val nz = (sin(phi) * sin(theta)).toFloat()
                
                // UV
                val u = x.toFloat() / segments
                val v = y.toFloat() / rings
                
                vertices.addAll(listOf(px, py, pz, nx, ny, nz, u, v))
            }
        }
        
        for (y in 0 until rings) {
            for (x in 0 until segments) {
                val i0 = (y * (segments + 1) + x).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = (i0 + segments + 1).toShort()
                val i3 = (i2 + 1).toShort()
                
                indices.addAll(listOf(i0, i2, i1, i1, i2, i3))
            }
        }
        
        return createMesh(vertices.toFloatArray(), indices.toShortArray())
    }
    
    private fun generateCylinderMesh(): PrimMesh {
        val segments = 24
        
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        
        // Side
        for (i in 0..segments) {
            val theta = 2 * PI * i / segments
            val x = (cos(theta) * 0.5).toFloat()
            val z = (sin(theta) * 0.5).toFloat()
            val nx = cos(theta).toFloat()
            val nz = sin(theta).toFloat()
            val u = i.toFloat() / segments
            
            // Bottom
            vertices.addAll(listOf(x, -0.5f, z, nx, 0f, nz, u, 0f))
            // Top
            vertices.addAll(listOf(x, 0.5f, z, nx, 0f, nz, u, 1f))
        }
        
        // Side indices
        for (i in 0 until segments) {
            val b0 = (i * 2).toShort()
            val t0 = (i * 2 + 1).toShort()
            val b1 = (i * 2 + 2).toShort()
            val t1 = (i * 2 + 3).toShort()
            
            indices.addAll(listOf(b0, b1, t0, t0, b1, t1))
        }
        
        // Top cap center
        val topCenter = (vertices.size / 8).toShort()
        vertices.addAll(listOf(0f, 0.5f, 0f, 0f, 1f, 0f, 0.5f, 0.5f))
        
        // Top cap vertices
        val topStart = (vertices.size / 8).toShort()
        for (i in 0..segments) {
            val theta = 2 * PI * i / segments
            val x = (cos(theta) * 0.5).toFloat()
            val z = (sin(theta) * 0.5).toFloat()
            vertices.addAll(listOf(x, 0.5f, z, 0f, 1f, 0f, 
                (cos(theta) * 0.5 + 0.5).toFloat(), (sin(theta) * 0.5 + 0.5).toFloat()))
        }
        
        // Top cap indices
        for (i in 0 until segments) {
            indices.addAll(listOf(topCenter, (topStart + i).toShort(), (topStart + i + 1).toShort()))
        }
        
        // Bottom cap center
        val bottomCenter = (vertices.size / 8).toShort()
        vertices.addAll(listOf(0f, -0.5f, 0f, 0f, -1f, 0f, 0.5f, 0.5f))
        
        // Bottom cap vertices
        val bottomStart = (vertices.size / 8).toShort()
        for (i in 0..segments) {
            val theta = 2 * PI * i / segments
            val x = (cos(theta) * 0.5).toFloat()
            val z = (sin(theta) * 0.5).toFloat()
            vertices.addAll(listOf(x, -0.5f, z, 0f, -1f, 0f,
                (cos(theta) * 0.5 + 0.5).toFloat(), (sin(theta) * 0.5 + 0.5).toFloat()))
        }
        
        // Bottom cap indices
        for (i in 0 until segments) {
            indices.addAll(listOf(bottomCenter, (bottomStart + i + 1).toShort(), (bottomStart + i).toShort()))
        }
        
        return createMesh(vertices.toFloatArray(), indices.toShortArray())
    }
    
    private fun generateTorusMesh(): PrimMesh {
        val majorSegments = 24
        val minorSegments = 12
        val majorRadius = 0.35f
        val minorRadius = 0.15f
        
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        
        for (i in 0..majorSegments) {
            val u = 2 * PI * i / majorSegments
            
            for (j in 0..minorSegments) {
                val v = 2 * PI * j / minorSegments
                
                val x = ((majorRadius + minorRadius * cos(v)) * cos(u)).toFloat()
                val y = (minorRadius * sin(v)).toFloat()
                val z = ((majorRadius + minorRadius * cos(v)) * sin(u)).toFloat()
                
                val nx = (cos(v) * cos(u)).toFloat()
                val ny = sin(v).toFloat()
                val nz = (cos(v) * sin(u)).toFloat()
                
                vertices.addAll(listOf(x, y, z, nx, ny, nz,
                    i.toFloat() / majorSegments, j.toFloat() / minorSegments))
            }
        }
        
        for (i in 0 until majorSegments) {
            for (j in 0 until minorSegments) {
                val i0 = (i * (minorSegments + 1) + j).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = (i0 + minorSegments + 1).toShort()
                val i3 = (i2 + 1).toShort()
                
                indices.addAll(listOf(i0, i2, i1, i1, i2, i3))
            }
        }
        
        return createMesh(vertices.toFloatArray(), indices.toShortArray())
    }
    
    private fun generatePrismMesh(): PrimMesh {
        // Triangular prism
        val vertices = floatArrayOf(
            // Front triangle
            0f, 0.5f, 0.5f, 0f, 0f, 1f, 0.5f, 1f,
            -0.5f, -0.5f, 0.5f, 0f, 0f, 1f, 0f, 0f,
            0.5f, -0.5f, 0.5f, 0f, 0f, 1f, 1f, 0f,
            // Back triangle
            0f, 0.5f, -0.5f, 0f, 0f, -1f, 0.5f, 1f,
            0.5f, -0.5f, -0.5f, 0f, 0f, -1f, 1f, 0f,
            -0.5f, -0.5f, -0.5f, 0f, 0f, -1f, 0f, 0f,
            // Bottom
            -0.5f, -0.5f, 0.5f, 0f, -1f, 0f, 0f, 0f,
            -0.5f, -0.5f, -0.5f, 0f, -1f, 0f, 0f, 1f,
            0.5f, -0.5f, -0.5f, 0f, -1f, 0f, 1f, 1f,
            0.5f, -0.5f, 0.5f, 0f, -1f, 0f, 1f, 0f,
            // Left side
            0f, 0.5f, 0.5f, -0.894f, 0.447f, 0f, 1f, 0f,
            -0.5f, -0.5f, 0.5f, -0.894f, 0.447f, 0f, 0f, 0f,
            -0.5f, -0.5f, -0.5f, -0.894f, 0.447f, 0f, 0f, 1f,
            0f, 0.5f, -0.5f, -0.894f, 0.447f, 0f, 1f, 1f,
            // Right side
            0f, 0.5f, -0.5f, 0.894f, 0.447f, 0f, 0f, 1f,
            0.5f, -0.5f, -0.5f, 0.894f, 0.447f, 0f, 1f, 1f,
            0.5f, -0.5f, 0.5f, 0.894f, 0.447f, 0f, 1f, 0f,
            0f, 0.5f, 0.5f, 0.894f, 0.447f, 0f, 0f, 0f
        )
        
        val indices = shortArrayOf(
            0, 1, 2,       // front
            3, 4, 5,       // back
            6, 7, 8, 6, 8, 9,  // bottom
            10, 11, 12, 10, 12, 13, // left
            14, 15, 16, 14, 16, 17  // right
        )
        
        return createMesh(vertices, indices)
    }
    
    private fun generateRingMesh(): PrimMesh {
        // Similar to torus but with larger hole
        return generateTorusMesh() // Use torus as placeholder
    }
    
    private fun createMesh(vertices: FloatArray, indices: ShortArray): PrimMesh {
        val stride = 8 * 4 // 3 pos + 3 normal + 2 uv, all floats
        val vertexCount = vertices.size / 8
        
        val vertexData = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
        for (v in vertices) {
            vertexData.putFloat(v)
        }
        vertexData.flip()
        
        val indexData = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
        for (i in indices) {
            indexData.putShort(i)
        }
        indexData.flip()
        
        val vertexBuffer = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(1)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride)
            .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT3, 12, stride)
            .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 24, stride)
            .build(engine)
        
        vertexBuffer.setBufferAt(engine, 0, vertexData)
        
        val indexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        
        indexBuffer.setBuffer(engine, indexData)
        
        return PrimMesh(vertexBuffer, indexBuffer)
    }
    
    private fun updateTransform(prim: PrimInstance) {
        val m = FloatArray(16)
        
        // Build transform matrix: T * R * S
        val r = prim.rotation
        r.toMatrix(m)
        
        // Apply scale
        m[0] *= prim.scale.x; m[1] *= prim.scale.x; m[2] *= prim.scale.x
        m[4] *= prim.scale.y; m[5] *= prim.scale.y; m[6] *= prim.scale.y
        m[8] *= prim.scale.z; m[9] *= prim.scale.z; m[10] *= prim.scale.z
        
        // Apply translation
        m[12] = prim.position.x
        m[13] = prim.position.y
        m[14] = prim.position.z
        
        transformManager.setTransform(prim.transformInstance, m)
    }
    
    private fun updatePrimMaterial(prim: PrimInstance, textureEntry: ByteArray) {
        // Parse texture entry to extract texture UUIDs and material properties
        // Texture entry format:
        // - Default face texture UUID (16 bytes)
        // - Face-specific texture overrides (bitfield + UUID pairs)
        // - RGBA color
        // - Repeat U/V, Offset U/V, Rotation
        
        if (textureEntry.isEmpty()) return
        
        try {
            val buffer = java.nio.ByteBuffer.wrap(textureEntry)
                .order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // Extract default texture UUID (first 16 bytes)
            if (buffer.remaining() >= 16) {
                val uuidBytes = ByteArray(16)
                buffer.get(uuidBytes)
                val defaultTextureId = bytesToUUID(uuidBytes)
                
                // Request texture if valid
                if (defaultTextureId != UUID(0, 0)) {
                    // Texture loading would be triggered here
                    Log.d(TAG, "Prim ${prim.localId} default texture: $defaultTextureId")
                }
            }
            
            // Skip face-specific textures for now (complex bitfield parsing)
            // A full implementation would parse the complete texture entry
            
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse texture entry for prim ${prim.localId}", e)
        }
    }
    
    private fun bytesToUUID(bytes: ByteArray): UUID {
        val buffer = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.BIG_ENDIAN)
        return UUID(buffer.long, buffer.long)
    }
    
    fun shutdown() {
        for (prim in prims.values) {
            scene.removeEntity(prim.entity)
            engine.destroyEntity(prim.entity)
        }
        prims.clear()
        
        for (mesh in primMeshes.values) {
            engine.destroyVertexBuffer(mesh.vertexBuffer)
            engine.destroyIndexBuffer(mesh.indexBuffer)
        }
        primMeshes.clear()
        
        defaultMaterial?.let { engine.destroyMaterialInstance(it) }
    }
}

enum class PrimShape {
    BOX, SPHERE, CYLINDER, TORUS, PRISM, RING
}

data class PrimMesh(
    val vertexBuffer: VertexBuffer,
    val indexBuffer: IndexBuffer
)

data class PrimInstance(
    val localId: Int,
    val fullId: UUID,
    val entity: Int,
    val transformInstance: Int,
    val shape: PrimShape,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var scale: LLVector3,
    var textureEntry: ByteArray
)
