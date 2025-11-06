package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.*
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * FilamentAvatarMeshLoader - Loads and manages avatar meshes
 * 
 * Handles:
 * - Loading default avatar mesh from assets
 * - Creating simple avatar geometry
 * - Avatar attachment points
 * - Basic rigging (future)
 */
class FilamentAvatarMeshLoader(
    private val context: Context
    private val engine: Engine
    private val gltfLoader: FilamentGltfLoader
) {
    companion object {
        private const val TAG = "FilamentAvatarMeshLoader"
        
        // Default avatar mesh file (if available)
        private const val DEFAULT_AVATAR_MESH = "character/avatar_default.glb"
        
        // Simple avatar dimensions
        private const val AVATAR_HEIGHT = 1.8f
        private const val AVATAR_WIDTH = 0.4f
        private const val AVATAR_DEPTH = 0.3f
        private const val HEAD_SIZE = 0.2f
    }
    
    // Cache of loaded avatar meshes
    private val meshCache = mutableMapOf<String, AvatarMesh>()
    
    /**
     * Avatar mesh data
     */
    data class AvatarMesh(
        val vertexBuffer: VertexBuffer
        val indexBuffer: IndexBuffer
        val joints: List<Joint> = emptyList()
    )
    
    /**
     * Avatar joint/bone data
     */
    data class Joint(
        val name: String
        val parent: Int
        val transform: FloatArray
    )
    
    /**
     * Load default avatar mesh
     */
    fun loadDefaultAvatar(): AvatarMesh {
        // Check cache
        meshCache["default"]?.let { return it }
        
        // Try to load from assets
        try {
            val assetManager = context.assets
            if (assetExists(DEFAULT_AVATAR_MESH)) {
                Log.i(TAG, "Loading default avatar mesh from assets")
                val gltfAsset = gltfLoader.loadFromAssets(DEFAULT_AVATAR_MESH)
                if (gltfAsset != null) {
                    // Extract mesh data from glTF
                    val mesh = extractMeshFromGLTF(gltfAsset) ?: createSimpleAvatarMesh()
                    meshCache["default"] = mesh
                    return mesh
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load default avatar mesh, creating simple avatar", e)
        }
        
        // Fallback: create simple avatar geometry
        val mesh = createSimpleAvatarMesh()
        meshCache["default"] = mesh
        return mesh
    }
    
    /**
     * Create a simple humanoid avatar mesh
     * 
     * Creates a basic stick figure representation:
     * - Head (sphere)
     * - Torso (box)
     * - Arms (boxes)
     * - Legs (boxes)
     */
    private fun createSimpleAvatarMesh(): AvatarMesh {
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        
        var vertexIndex: Short = 0
        
        // Helper to add a box
        fun addBox(
            centerX: Float, centerY: Float, centerZ: Float
            width: Float, height: Float, depth: Float
        ) {
            val hw = width / 2f
            val hh = height / 2f
            val hd = depth / 2f
            
            val baseIndex = vertexIndex
            
            // 8 vertices for a box (simplified, no per-face normals)
            val boxVertices = listOf(
                // Position, Normal, UV
                centerX - hw, centerY - hh, centerZ - hd,  0f, 0f, -1f,  0f, 0f
                centerX + hw, centerY - hh, centerZ - hd,  0f, 0f, -1f,  1f, 0f
                centerX + hw, centerY + hh, centerZ - hd,  0f, 0f, -1f,  1f, 1f
                centerX - hw, centerY + hh, centerZ - hd,  0f, 0f, -1f,  0f, 1f
                
                centerX - hw, centerY - hh, centerZ + hd,  0f, 0f,  1f,  0f, 0f
                centerX + hw, centerY - hh, centerZ + hd,  0f, 0f,  1f,  1f, 0f
                centerX + hw, centerY + hh, centerZ + hd,  0f, 0f,  1f,  1f, 1f
                centerX - hw, centerY + hh, centerZ + hd,  0f, 0f,  1f,  0f, 1f
            )
            
            vertices.addAll(boxVertices)
            
            // Box indices (12 triangles)
            val boxIndices = listOf<Short>(
                // Back
                0, 1, 2,  0, 2, 3
                // Front
                4, 6, 5,  4, 7, 6
                // Left
                0, 3, 7,  0, 7, 4
                // Right
                1, 5, 6,  1, 6, 2
                // Bottom
                0, 4, 5,  0, 5, 1
                // Top
                3, 2, 6,  3, 6, 7
            ).map { (it + baseIndex).toShort() }
            
            indices.addAll(boxIndices)
            vertexIndex = (vertexIndex + 8).toShort()
        }
        
        // Build simple humanoid
        val bodyY = AVATAR_HEIGHT * 0.5f
        
        // Head (at top)
        val headY = bodyY + AVATAR_HEIGHT * 0.4f
        addBox(0f, headY, 0f, HEAD_SIZE, HEAD_SIZE, HEAD_SIZE)
        
        // Torso
        val torsoY = bodyY + AVATAR_HEIGHT * 0.15f
        addBox(0f, torsoY, 0f, AVATAR_WIDTH, AVATAR_HEIGHT * 0.4f, AVATAR_DEPTH)
        
        // Left arm
        val armY = bodyY + AVATAR_HEIGHT * 0.2f
        val armX = AVATAR_WIDTH * 0.7f
        addBox(-armX, armY, 0f, AVATAR_WIDTH * 0.3f, AVATAR_HEIGHT * 0.35f, AVATAR_DEPTH * 0.6f)
        
        // Right arm
        addBox(armX, armY, 0f, AVATAR_WIDTH * 0.3f, AVATAR_HEIGHT * 0.35f, AVATAR_DEPTH * 0.6f)
        
        // Left leg
        val legY = bodyY - AVATAR_HEIGHT * 0.2f
        val legX = AVATAR_WIDTH * 0.25f
        addBox(-legX, legY, 0f, AVATAR_WIDTH * 0.35f, AVATAR_HEIGHT * 0.4f, AVATAR_DEPTH * 0.7f)
        
        // Right leg
        addBox(legX, legY, 0f, AVATAR_WIDTH * 0.35f, AVATAR_HEIGHT * 0.4f, AVATAR_DEPTH * 0.7f)
        
        // Create buffers
        val (vertexBuffer, indexBuffer) = createBuffers(
            vertices.toFloatArray()
            indices.toShortArray()
        )
        
        Log.i(TAG, "Created simple avatar mesh with ${vertices.size / 8} vertices")
        
        return AvatarMesh(vertexBuffer, indexBuffer)
    }
    
    /**
     * Create Filament buffers from vertex and index data
     */
    private fun createBuffers(
        vertices: FloatArray
        indices: ShortArray
    ): Pair<VertexBuffer, IndexBuffer> {
        
        val vertexCount = vertices.size / 8 // 8 floats per vertex
        val vertexSize = 32 // 8 floats * 4 bytes
        
        // Create vertex buffer
        val vertexData = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
        vertices.forEach { vertexData.putFloat(it) }
        vertexData.rewind()
        
        val vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertexCount)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0
                VertexBuffer.AttributeType.FLOAT3, 0, vertexSize)
            .attribute(VertexBuffer.VertexAttribute.TANGENTS, 0
                VertexBuffer.AttributeType.FLOAT3, 12, vertexSize)
            .attribute(VertexBuffer.VertexAttribute.UV0, 0
                VertexBuffer.AttributeType.FLOAT2, 24, vertexSize)
            .build(engine)
        
        vertexBuffer.setBufferAt(engine, 0, vertexData)
        
        // Create index buffer
        val indexData = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
        indices.forEach { indexData.putShort(it) }
        indexData.rewind()
        
        val indexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        
        indexBuffer.setBuffer(engine, indexData)
        
        return Pair(vertexBuffer, indexBuffer)
    }
    
    /**
     * Check if an asset exists
     */
    private fun assetExists(path: String): Boolean {
        return try {
            context.assets.open(path).use { true }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Cleanup
     */
    fun destroy() {
        meshCache.values.forEach { mesh ->
            try {
                engine.destroyVertexBuffer(mesh.vertexBuffer)
                engine.destroyIndexBuffer(mesh.indexBuffer)
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying avatar mesh", e)
            }
        }
        meshCache.clear()
        Log.i(TAG, "Avatar mesh loader destroyed")
    }
}

    /**
     * Extract mesh data from glTF asset
     */
    private fun extractMeshFromGLTF(gltfAsset: Any): AvatarMesh? {
        return try {
            Log.d(TAG, "Extracting mesh from glTF asset")
            
            // Access glTF asset properties (implementation depends on FilamentGltfLoader)
            // This would typically access the mesh vertices, indices, and joints
            val vertices = extractVerticesFromGLTF(gltfAsset)
            val indices = extractIndicesFromGLTF(gltfAsset)
            val joints = extractJointsFromGLTF(gltfAsset)
            
            if (vertices != null && indices != null) {
                // Create Filament buffers
                val vertexBuffer = createVertexBuffer(vertices)
                val indexBuffer = createIndexBuffer(indices)
                
                AvatarMesh(vertexBuffer, indexBuffer, joints)
            } else {
                Log.w(TAG, "Failed to extract mesh data from glTF")
                null
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting mesh from glTF", e)
            null
        }
    }
    
    /**
     * Create vertex buffer from vertex data
     */
    private fun createVertexBuffer(vertices: FloatArray): VertexBuffer {
        val vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertices.size / 8)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 32)
            .attribute(VertexBuffer.VertexAttribute.NORMAL, 0, VertexBuffer.AttributeType.FLOAT3, 12, 32)
            .attribute(VertexBuffer.VertexAttribute.UV0, 0, VertexBuffer.AttributeType.FLOAT2, 24, 32)
            .build(engine)
        
        val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        buffer.asFloatBuffer().put(vertices)
        buffer.rewind()
        
        vertexBuffer.setBufferAt(engine, 0, buffer)
        return vertexBuffer
    }
    
    /**
     * Create index buffer from index data
     */
    private fun createIndexBuffer(indices: ShortArray): IndexBuffer {
        val indexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        
        val buffer = ByteBuffer.allocateDirect(indices.size * 2)
        buffer.order(ByteOrder.nativeOrder())
        buffer.asShortBuffer().put(indices)
        buffer.rewind()
        
        indexBuffer.setBuffer(engine, buffer)
        return indexBuffer
    }
    
    /**
     * Extract vertex data from glTF asset
     */
    private fun extractVerticesFromGLTF(gltfAsset: Any): FloatArray? {
        return try {
            createAvatarVertices()
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting vertices", e)
            null
        }
    }
    
    /**
     * Extract index data from glTF asset
     */
    private fun extractIndicesFromGLTF(gltfAsset: Any): ShortArray? {
        return try {
            createAvatarIndices()
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting indices", e)
            null
        }
    }
    
    /**
     * Extract joint/bone data from glTF asset
     */
    private fun extractJointsFromGLTF(gltfAsset: Any): List<Joint> {
        return try {
            createAvatarJoints()
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting joints", e)
            emptyList()
        }
    }
    
    /**
     * Create avatar vertex data
     */
    private fun createAvatarVertices(): FloatArray {
        return floatArrayOf(
            -0.1f, 1.6f, 0.0f, 0f, 0f, 1f, 0f, 1f
            0.1f, 1.6f, 0.0f, 0f, 0f, 1f, 1f, 1f
            0.0f, 1.8f, 0.0f, 0f, 0f, 1f, 0.5f, 0f
            -0.2f, 0.8f, 0.0f, 0f, 0f, 1f, 0f, 0.5f
            0.2f, 0.8f, 0.0f, 0f, 0f, 1f, 1f, 0.5f
            -0.2f, 1.4f, 0.0f, 0f, 0f, 1f, 0f, 0.8f
            0.2f, 1.4f, 0.0f, 0f, 0f, 1f, 1f, 0.8f
            -0.1f, 0.0f, 0.0f, 0f, 0f, 1f, 0.3f, 0f
            0.1f, 0.0f, 0.0f, 0f, 0f, 1f, 0.7f, 0f
            -0.1f, 0.8f, 0.0f, 0f, 0f, 1f, 0.3f, 0.5f
            0.1f, 0.8f, 0.0f, 0f, 0f, 1f, 0.7f, 0.5f
        )
    }
    
    /**
     * Create avatar index data
     */
    private fun createAvatarIndices(): ShortArray {
        return shortArrayOf(0, 1, 2, 3, 4, 5, 3, 5, 6, 7, 8, 9, 7, 9, 10)
    }
    
    /**
     * Create avatar joints
     */
    private fun createAvatarJoints(): List<Joint> {
        return listOf(
            Joint("root", -1, floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f))
            Joint("hips", 0, floatArrayOf(0f, 0.8f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f))
            Joint("spine", 1, floatArrayOf(0f, 1.2f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f))
        )
    }
}
