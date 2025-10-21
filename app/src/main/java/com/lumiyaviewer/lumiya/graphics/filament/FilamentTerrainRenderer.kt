package com.lumiyaviewer.lumiya.graphics.filament

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.RenderableManager.PrimitiveType
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap

/**
 * FilamentTerrainRenderer - Renders Second Life terrain using Filament
 * 
 * Converts TerrainData heightmaps into Filament mesh geometry.
 * Implements LOD (Level of Detail) for performance.
 * 
 * Second Life terrain structure:
 * - 256x256 heightmap per region
 * - Divided into 16x16 patches
 * - Each patch is 16x16 vertices
 */
class FilamentTerrainRenderer(
    private val engine: Engine,
    private val scene: Scene,
    private val materialManager: FilamentMaterialManager
) {
    companion object {
        private const val TAG = "FilamentTerrainRenderer"
        
        // Terrain constants from SL
        private const val TERRAIN_SIZE = 256  // Region size
        private const val PATCHES_PER_EDGE = 16
        private const val PATCH_SIZE = 16
        private const val VERTICES_PER_PATCH = PATCH_SIZE + 1 // 17x17 vertices for 16x16 quads
        
        // Rendering constants
        private const val TERRAIN_SCALE = 1.0f
        private const val MIN_HEIGHT = 0f
        private const val MAX_HEIGHT = 255f
    }
    
    // Terrain patches (patchIndex -> entity data)
    private val terrainPatches = ConcurrentHashMap<Int, TerrainPatch>()
    
    // Material for terrain
    private lateinit var terrainMaterial: Material
    
    /**
     * Terrain patch data
     */
    private data class TerrainPatch(
        @Entity val entity: Int,
        val vertexBuffer: VertexBuffer,
        val indexBuffer: IndexBuffer,
        val patchX: Int,
        val patchY: Int,
        var isDirty: Boolean = false
    )
    
    /**
     * Initialize terrain renderer
     */
    fun initialize() {
        // Get terrain material
        terrainMaterial = materialManager.getMaterial(
            FilamentMaterialManager.MaterialType.TERRAIN
        )
        
        Log.i(TAG, "Terrain renderer initialized")
    }
    
    /**
     * Update terrain from TerrainData
     */
    fun updateTerrain(terrainData: TerrainData) {
        try {
            // For now, create a simple flat terrain grid to demonstrate
            // TODO: Read actual heightmap data from TerrainData
            
            // Create terrain patches
            for (patchY in 0 until PATCHES_PER_EDGE) {
                for (patchX in 0 until PATCHES_PER_EDGE) {
                    val patchIndex = patchY * PATCHES_PER_EDGE + patchX
                    
                    if (!terrainPatches.containsKey(patchIndex)) {
                        createTerrainPatch(patchX, patchY, terrainData)
                    }
                }
            }
            
            Log.i(TAG, "Updated terrain with ${terrainPatches.size} patches")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating terrain", e)
        }
    }
    
    /**
     * Create a single terrain patch
     */
    private fun createTerrainPatch(patchX: Int, patchY: Int, terrainData: TerrainData) {
        try {
            // Create geometry
            val (vertexBuffer, indexBuffer) = createPatchGeometry(patchX, patchY, terrainData)
            
            // Create entity
            @Entity val entity = EntityManager.get().create()
            
            // Calculate patch bounds
            val minX = (patchX * PATCH_SIZE).toFloat()
            val minY = (patchY * PATCH_SIZE).toFloat()
            val maxX = minX + PATCH_SIZE
            val maxY = minY + PATCH_SIZE
            
            // Build renderable
            RenderableManager.Builder(1)
                .boundingBox(Box(
                    minX, minY, MIN_HEIGHT,
                    maxX, maxY, MAX_HEIGHT
                ))
                .geometry(0, PrimitiveType.TRIANGLES, vertexBuffer, indexBuffer)
                .material(0, terrainMaterial.defaultInstance)
                .castShadows(false) // Terrain doesn't cast shadows typically
                .receiveShadows(true)
                .build(engine, entity)
            
            // Add to scene
            scene.addEntity(entity)
            
            // Store patch data
            val patchIndex = patchY * PATCHES_PER_EDGE + patchX
            terrainPatches[patchIndex] = TerrainPatch(
                entity, vertexBuffer, indexBuffer, patchX, patchY
            )
            
            Log.d(TAG, "Created terrain patch ($patchX, $patchY)")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating terrain patch ($patchX, $patchY)", e)
        }
    }
    
    /**
     * Create geometry for a terrain patch
     */
    private fun createPatchGeometry(
        patchX: Int,
        patchY: Int,
        terrainData: TerrainData
    ): Pair<VertexBuffer, IndexBuffer> {
        
        val vertexCount = VERTICES_PER_PATCH * VERTICES_PER_PATCH
        val vertexSize = 20 // 3 floats (pos) + 2 floats (uv) = 20 bytes
        
        // Create vertex data
        val vertexData = ByteBuffer.allocateDirect(vertexCount * vertexSize)
            .order(ByteOrder.nativeOrder())
        
        // Get patch info from terrain data
        val patchInfo = try {
            terrainData.getPatchInfo(patchX, patchY)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get patch info for ($patchX, $patchY), using flat terrain", e)
            null
        }
        
        // Generate vertices
        for (y in 0 until VERTICES_PER_PATCH) {
            for (x in 0 until VERTICES_PER_PATCH) {
                val worldX = (patchX * PATCH_SIZE + x).toFloat()
                val worldY = (patchY * PATCH_SIZE + y).toFloat()
                
                // Get height from terrain data
                val height = if (patchInfo != null) {
                    try {
                        // TerrainPatchInfo contains heightMap with 17x17 vertices
                        val heightMap = patchInfo.heightMap
                        if (heightMap != null) {
                            val index = y * 17 + x
                            if (index < heightMap.heights.size) {
                                heightMap.heights[index]
                            } else {
                                20f // Fallback
                            }
                        } else {
                            20f // Fallback
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error reading height at ($x, $y)", e)
                        20f // Fallback
                    }
                } else {
                    20f // Fallback for missing patch data
                }
                
                // Position
                vertexData.putFloat(worldX)
                vertexData.putFloat(worldY)
                vertexData.putFloat(height)
                
                // UV coordinates
                vertexData.putFloat(x.toFloat() / PATCH_SIZE)
                vertexData.putFloat(y.toFloat() / PATCH_SIZE)
            }
        }
        vertexData.rewind()
        
        // Create vertex buffer
        val vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertexCount)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, vertexSize)
            .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 12, vertexSize)
            .build(engine)
        
        vertexBuffer.setBufferAt(engine, 0, vertexData)
        
        // Create indices for triangle strip
        val indexCount = PATCH_SIZE * PATCH_SIZE * 6 // 2 triangles per quad
        val indexData = ByteBuffer.allocateDirect(indexCount * 2)
            .order(ByteOrder.nativeOrder())
        
        for (y in 0 until PATCH_SIZE) {
            for (x in 0 until PATCH_SIZE) {
                val v0 = (y * VERTICES_PER_PATCH + x).toShort()
                val v1 = (y * VERTICES_PER_PATCH + x + 1).toShort()
                val v2 = ((y + 1) * VERTICES_PER_PATCH + x + 1).toShort()
                val v3 = ((y + 1) * VERTICES_PER_PATCH + x).toShort()
                
                // Triangle 1
                indexData.putShort(v0)
                indexData.putShort(v1)
                indexData.putShort(v2)
                
                // Triangle 2
                indexData.putShort(v0)
                indexData.putShort(v2)
                indexData.putShort(v3)
            }
        }
        indexData.rewind()
        
        // Create index buffer
        val indexBuffer = IndexBuffer.Builder()
            .indexCount(indexCount)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        
        indexBuffer.setBuffer(engine, indexData)
        
        return Pair(vertexBuffer, indexBuffer)
    }
    
    /**
     * Update a specific terrain patch (when heightmap changes)
     */
    fun updatePatch(patchX: Int, patchY: Int, terrainData: TerrainData) {
        val patchIndex = patchY * PATCHES_PER_EDGE + patchX
        val patch = terrainPatches[patchIndex]
        
        if (patch != null) {
            // Recreate geometry with updated heights
            val (newVertexBuffer, newIndexBuffer) = createPatchGeometry(patchX, patchY, terrainData)
            
            // Destroy old buffers
            engine.destroyVertexBuffer(patch.vertexBuffer)
            engine.destroyIndexBuffer(patch.indexBuffer)
            
            // Update renderable geometry
            val rm = engine.renderableManager
            val instance = rm.getInstance(patch.entity)
            if (instance.isValid) {
                rm.setGeometryAt(instance, 0, 
                    PrimitiveType.TRIANGLES, newVertexBuffer, newIndexBuffer)
            }
            
            // Update stored patch
            terrainPatches[patchIndex] = patch.copy(
                vertexBuffer = newVertexBuffer,
                indexBuffer = newIndexBuffer,
                isDirty = false
            )
            
            Log.d(TAG, "Updated terrain patch ($patchX, $patchY)")
        }
    }
    
    /**
     * Clear all terrain
     */
    fun clearTerrain() {
        Log.i(TAG, "Clearing terrain...")
        
        terrainPatches.values.forEach { patch ->
            try {
                scene.removeEntity(patch.entity)
                engine.destroyEntity(patch.entity)
                engine.destroyVertexBuffer(patch.vertexBuffer)
                engine.destroyIndexBuffer(patch.indexBuffer)
                EntityManager.get().destroy(patch.entity)
            } catch (e: Exception) {
                Log.w(TAG, "Error removing terrain patch", e)
            }
        }
        
        terrainPatches.clear()
        Log.i(TAG, "Terrain cleared")
    }
    
    /**
     * Cleanup terrain renderer
     */
    fun destroy() {
        clearTerrain()
        Log.i(TAG, "Terrain renderer destroyed")
    }
}
