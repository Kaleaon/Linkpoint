package com.linkpoint.render.terrain

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.floor

/**
 * Renders Second Life terrain
 * Handles heightmap data and texture splatting
 */
class TerrainRenderer(
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "TerrainRenderer"
        
        // SL terrain constants
        const val REGION_WIDTH = 256f
        const val REGION_HEIGHT = 256f
        const val PATCH_SIZE = 16
        const val PATCHES_PER_SIDE = 16
    }
    
    // Heightmap data (256x256 floats)
    private var heightmap = FloatArray(257 * 257)
    
    // Terrain patches for LOD
    private val patches = Array(PATCHES_PER_SIDE * PATCHES_PER_SIDE) { TerrainPatch() }
    
    // Filament entities
    private var terrainEntity: Int = 0
    private var vertexBuffer: VertexBuffer? = null
    private var indexBuffer: IndexBuffer? = null
    private var materialInstance: MaterialInstance? = null
    
    // Terrain textures (4 detail textures)
    private var detailTextures = arrayOfNulls<Texture>(4)
    private var detailScales = floatArrayOf(1f, 1f, 1f, 1f)
    
    /**
     * Initialize terrain renderer
     */
    fun initialize(material: Material) {
        materialInstance = material.createInstance()
        
        // Create terrain mesh
        createTerrainMesh()
        
        // Add to scene
        scene.addEntity(terrainEntity)
        
        Log.i(TAG, "Terrain renderer initialized")
    }
    
    /**
     * Update heightmap from simulator data
     */
    fun updateHeightmap(patchX: Int, patchY: Int, heights: FloatArray) {
        if (heights.size != PATCH_SIZE * PATCH_SIZE) return
        
        // Copy heights to main heightmap
        for (y in 0 until PATCH_SIZE) {
            for (x in 0 until PATCH_SIZE) {
                val globalX = patchX * PATCH_SIZE + x
                val globalY = patchY * PATCH_SIZE + y
                if (globalX <= 256 && globalY <= 256) {
                    heightmap[globalY * 257 + globalX] = heights[y * PATCH_SIZE + x]
                }
            }
        }
        
        // Mark patch as dirty
        val patchIndex = patchY * PATCHES_PER_SIDE + patchX
        if (patchIndex < patches.size) {
            patches[patchIndex].dirty = true
        }
    }
    
    /**
     * Set full heightmap
     */
    fun setHeightmap(heights: FloatArray) {
        if (heights.size >= 257 * 257) {
            heights.copyInto(heightmap)
            patches.forEach { it.dirty = true }
            rebuildMesh()
        }
    }
    
    /**
     * Set terrain textures
     */
    fun setDetailTexture(index: Int, texture: Texture, scale: Float = 1f) {
        if (index in 0..3) {
            detailTextures[index] = texture
            detailScales[index] = scale
            updateMaterial()
        }
    }
    
    /**
     * Update terrain (rebuild dirty patches)
     */
    fun update() {
        var needsRebuild = false
        for (patch in patches) {
            if (patch.dirty) {
                needsRebuild = true
                patch.dirty = false
            }
        }
        
        if (needsRebuild) {
            rebuildMesh()
        }
    }
    
    /**
     * Get height at position
     */
    fun getHeightAt(x: Float, y: Float): Float {
        val clampedX = x.coerceIn(0f, REGION_WIDTH)
        val clampedY = y.coerceIn(0f, REGION_HEIGHT)
        
        val ix = floor(clampedX).toInt().coerceIn(0, 255)
        val iy = floor(clampedY).toInt().coerceIn(0, 255)
        
        val fx = clampedX - ix
        val fy = clampedY - iy
        
        // Bilinear interpolation
        val h00 = heightmap[iy * 257 + ix]
        val h10 = heightmap[iy * 257 + ix + 1]
        val h01 = heightmap[(iy + 1) * 257 + ix]
        val h11 = heightmap[(iy + 1) * 257 + ix + 1]
        
        val h0 = h00 + (h10 - h00) * fx
        val h1 = h01 + (h11 - h01) * fx
        
        return h0 + (h1 - h0) * fy
    }
    
    /**
     * Get normal at position
     */
    fun getNormalAt(x: Float, y: Float): LLVector3 {
        val step = 1f
        val h = getHeightAt(x, y)
        val hx = getHeightAt(x + step, y)
        val hy = getHeightAt(x, y + step)
        
        val dx = LLVector3(step, 0f, hx - h)
        val dy = LLVector3(0f, step, hy - h)
        
        return dy.cross(dx).normalize()
    }
    
    private fun createTerrainMesh() {
        val resolution = 257 // Vertices per side
        val vertexCount = resolution * resolution
        val indexCount = (resolution - 1) * (resolution - 1) * 6
        
        // Position + Normal + UV
        val stride = (3 + 3 + 2) * 4
        val vertexData = ByteBuffer.allocateDirect(vertexCount * stride)
            .order(ByteOrder.nativeOrder())
        
        // Generate vertices
        for (y in 0 until resolution) {
            for (x in 0 until resolution) {
                val px = x.toFloat()
                val py = y.toFloat()
                val pz = heightmap[y * resolution + x]
                
                // Position
                vertexData.putFloat(px)
                vertexData.putFloat(py)
                vertexData.putFloat(pz)
                
                // Normal (calculate)
                val normal = getNormalAt(px, py)
                vertexData.putFloat(normal.x)
                vertexData.putFloat(normal.y)
                vertexData.putFloat(normal.z)
                
                // UV
                vertexData.putFloat(px / REGION_WIDTH)
                vertexData.putFloat(py / REGION_HEIGHT)
            }
        }
        vertexData.flip()
        
        // Generate indices
        val indexData = ByteBuffer.allocateDirect(indexCount * 2)
            .order(ByteOrder.nativeOrder())
        
        for (y in 0 until resolution - 1) {
            for (x in 0 until resolution - 1) {
                val i00 = y * resolution + x
                val i10 = y * resolution + x + 1
                val i01 = (y + 1) * resolution + x
                val i11 = (y + 1) * resolution + x + 1
                
                // Triangle 1
                indexData.putShort(i00.toShort())
                indexData.putShort(i10.toShort())
                indexData.putShort(i01.toShort())
                
                // Triangle 2
                indexData.putShort(i10.toShort())
                indexData.putShort(i11.toShort())
                indexData.putShort(i01.toShort())
            }
        }
        indexData.flip()
        
        // Create Filament buffers
        vertexBuffer = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(1)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride)
            .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT3, 12, stride)
            .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 24, stride)
            .build(engine)
        
        vertexBuffer?.setBufferAt(engine, 0, vertexData)
        
        indexBuffer = IndexBuffer.Builder()
            .indexCount(indexCount)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        
        indexBuffer?.setBuffer(engine, indexData)
        
        // Create renderable
        terrainEntity = EntityManager.get().create()
        
        // Validate required components
        val vb = vertexBuffer ?: throw IllegalStateException("Vertex buffer not initialized")
        val ib = indexBuffer ?: throw IllegalStateException("Index buffer not initialized")
        val mat = materialInstance ?: throw IllegalStateException("Material instance not initialized")
        
        RenderableManager.Builder(1)
            .boundingBox(Box(0f, 0f, 0f, 256f, 256f, 100f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .material(0, mat)
            .build(engine, terrainEntity)
    }
    
    private fun rebuildMesh() {
        // Update vertex positions based on heightmap
        val resolution = 257
        val stride = (3 + 3 + 2) * 4
        val vertexData = ByteBuffer.allocateDirect(resolution * resolution * stride)
            .order(ByteOrder.nativeOrder())
        
        for (y in 0 until resolution) {
            for (x in 0 until resolution) {
                val px = x.toFloat()
                val py = y.toFloat()
                val pz = heightmap[y * resolution + x]
                
                vertexData.putFloat(px)
                vertexData.putFloat(py)
                vertexData.putFloat(pz)
                
                val normal = getNormalAt(px, py)
                vertexData.putFloat(normal.x)
                vertexData.putFloat(normal.y)
                vertexData.putFloat(normal.z)
                
                vertexData.putFloat(px / REGION_WIDTH)
                vertexData.putFloat(py / REGION_HEIGHT)
            }
        }
        vertexData.flip()
        
        vertexBuffer?.setBufferAt(engine, 0, vertexData)
    }
    
    private fun updateMaterial() {
        materialInstance?.let { mat ->
            detailTextures.forEachIndexed { index, texture ->
                texture?.let {
                    mat.setParameter("detail$index", it, TextureSampler(
                        TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
                        TextureSampler.MagFilter.LINEAR,
                        TextureSampler.WrapMode.REPEAT
                    ))
                }
            }
            mat.setParameter("detailScales", detailScales[0], detailScales[1], 
                detailScales[2], detailScales[3])
        }
    }
    
    fun shutdown() {
        if (terrainEntity != 0) {
            scene.removeEntity(terrainEntity)
            engine.destroyEntity(terrainEntity)
        }
        vertexBuffer?.let { engine.destroyVertexBuffer(it) }
        indexBuffer?.let { engine.destroyIndexBuffer(it) }
        materialInstance?.let { engine.destroyMaterialInstance(it) }
    }
}

private class TerrainPatch {
    var dirty = true
    var lod = 0
}
