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
    data class Diagnostics(
        val visiblePatchCount: Int,
        val dirtyPatchCount: Int
    )

    companion object {
        private const val TAG = "TerrainRenderer"

        // SL terrain constants
        const val REGION_WIDTH = 256f
        const val REGION_HEIGHT = 256f
        const val PATCH_SIZE = 16
        const val PATCHES_PER_SIDE = 16
    }

    var regionWidth: Float = REGION_WIDTH
        private set
    var regionHeight: Float = REGION_HEIGHT
        private set

    // Heightmap data ((width+1) x (height+1) floats)
    private var heightmap = FloatArray(257 * 257)

    // Terrain patches for LOD
    private var patches = Array(PATCHES_PER_SIDE * PATCHES_PER_SIDE) { TerrainPatch() }

    // Filament entities
    private var terrainEntity: Int = 0
    private var vertexBuffer: VertexBuffer? = null
    private var indexBuffer: IndexBuffer? = null
    private var materialInstance: MaterialInstance? = null

    // Terrain textures (4 detail textures)
    private var detailTextures = arrayOfNulls<Texture>(4)
    private var detailScales = floatArrayOf(1f, 1f, 1f, 1f)

    // Per-corner elevation blend bounds: order is (0,0) (1,0) (0,1) (1,1).
    // Defaults are sane low-elevation values so terrain doesn't render as a
    // solid colour before RegionHandshake-derived bounds arrive.
    private var startHeights = floatArrayOf(0f, 0f, 0f, 0f)
    private var heightRanges = floatArrayOf(60f, 60f, 60f, 60f)
    private var detailUVScale = 16f // meters per detail texture tile

    /**
     * Configure active region dimensions and re-allocate buffers matching active size.
     */
    fun setRegionSize(width: Float, height: Float) {
        val newW = width.coerceIn(256f, 2048f)
        val newH = height.coerceIn(256f, 2048f)
        if (regionWidth != newW || regionHeight != newH) {
            regionWidth = newW
            regionHeight = newH
            val resX = regionWidth.toInt() + 1
            val resY = regionHeight.toInt() + 1
            heightmap = FloatArray(resX * resY)
            val patchesX = regionWidth.toInt() / PATCH_SIZE
            val patchesY = regionHeight.toInt() / PATCH_SIZE
            patches = Array(patchesX * patchesY) { TerrainPatch() }
            if (materialInstance != null) {
                createTerrainMesh()
            }
        }
    }

    /**
     * Initialize terrain renderer
     */
    fun initialize(material: Material) {
        materialInstance = material.createInstance()

        // Create terrain mesh
        createTerrainMesh()

        // Add to scene
        scene.addEntity(terrainEntity)

        Log.i(TAG, "Terrain renderer initialized (${regionWidth.toInt()}x${regionHeight.toInt()})")
    }

    /**
     * Update heightmap from simulator data
     */
    fun updateHeightmap(patchX: Int, patchY: Int, heights: FloatArray) {
        if (heights.size != PATCH_SIZE * PATCH_SIZE) return

        val resX = regionWidth.toInt() + 1
        val resY = regionHeight.toInt() + 1
        val patchesPerSideX = regionWidth.toInt() / PATCH_SIZE

        // Copy heights to main heightmap
        for (y in 0 until PATCH_SIZE) {
            for (x in 0 until PATCH_SIZE) {
                val globalX = patchX * PATCH_SIZE + x
                val globalY = patchY * PATCH_SIZE + y
                if (globalX <= regionWidth.toInt() && globalY <= regionHeight.toInt()) {
                    heightmap[globalY * resX + globalX] = heights[y * PATCH_SIZE + x]
                }
            }
        }

        // Mark patch as dirty
        val patchIndex = patchY * patchesPerSideX + patchX
        if (patchIndex < patches.size) {
            patches[patchIndex].dirty = true
        }
    }

    /**
     * Set full heightmap with optional dimension parameters.
     */
    fun setHeightmap(heights: FloatArray, width: Int = regionWidth.toInt(), height: Int = regionHeight.toInt()) {
        if (width.toFloat() != regionWidth || height.toFloat() != regionHeight) {
            setRegionSize(width.toFloat(), height.toFloat())
        }
        val resX = regionWidth.toInt() + 1
        val resY = regionHeight.toInt() + 1
        val expectedSize = resX * resY
        if (heights.size >= expectedSize) {
            System.arraycopy(heights, 0, heightmap, 0, expectedSize)
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
     * Push the per-corner elevation blend bounds parsed from RegionHandshake.
     * Both arrays must be length 4 in the order (0,0), (1,0), (0,1), (1,1)
     * (matching what the splatting shader expects).
     */
    fun setHeightBlendParams(startsByCorner: FloatArray, rangesByCorner: FloatArray) {
        if (startsByCorner.size != 4 || rangesByCorner.size != 4) return
        startsByCorner.copyInto(startHeights)
        rangesByCorner.copyInto(heightRanges)
        updateMaterial()
    }

    fun setDetailUVScale(metersPerTile: Float) {
        detailUVScale = metersPerTile.coerceAtLeast(0.5f)
        updateMaterial()
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
        val clampedX = x.coerceIn(0f, regionWidth)
        val clampedY = y.coerceIn(0f, regionHeight)

        val resX = regionWidth.toInt() + 1
        val resY = regionHeight.toInt() + 1

        val ix = floor(clampedX).toInt().coerceIn(0, resX - 2)
        val iy = floor(clampedY).toInt().coerceIn(0, resY - 2)

        val fx = clampedX - ix
        val fy = clampedY - iy

        // Bilinear interpolation
        val h00 = heightmap[iy * resX + ix]
        val h10 = heightmap[iy * resX + ix + 1]
        val h01 = heightmap[(iy + 1) * resX + ix]
        val h11 = heightmap[(iy + 1) * resX + ix + 1]

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
        if (terrainEntity != 0) {
            scene.removeEntity(terrainEntity)
            engine.destroyEntity(terrainEntity)
            terrainEntity = 0
        }
        vertexBuffer?.let { engine.destroyVertexBuffer(it); vertexBuffer = null }
        indexBuffer?.let { engine.destroyIndexBuffer(it); indexBuffer = null }

        val resX = regionWidth.toInt() + 1
        val resY = regionHeight.toInt() + 1
        val vertexCount = resX * resY
        val indexCount = (resX - 1) * (resY - 1) * 6

        val stride = (3 + 3 + 2 + 2) * 4
        val vertexData = ByteBuffer.allocateDirect(vertexCount * stride)
            .order(ByteOrder.nativeOrder())

        for (y in 0 until resY) {
            for (x in 0 until resX) {
                val px = x.toFloat()
                val py = y.toFloat()
                val pz = heightmap[y * resX + x]

                vertexData.putFloat(px)
                vertexData.putFloat(py)
                vertexData.putFloat(pz)

                val normal = getNormalAt(px, py)
                vertexData.putFloat(normal.x)
                vertexData.putFloat(normal.y)
                vertexData.putFloat(normal.z)

                vertexData.putFloat(px / regionWidth)
                vertexData.putFloat(py / regionHeight)

                vertexData.putFloat(pz)
                vertexData.putFloat(0f)
            }
        }
        vertexData.flip()

        val indexData = ByteBuffer.allocateDirect(indexCount * 2)
            .order(ByteOrder.nativeOrder())

        for (y in 0 until resY - 1) {
            for (x in 0 until resX - 1) {
                val i00 = y * resX + x
                val i10 = y * resX + x + 1
                val i01 = (y + 1) * resX + x
                val i11 = (y + 1) * resX + x + 1

                indexData.putShort(i00.toShort())
                indexData.putShort(i10.toShort())
                indexData.putShort(i01.toShort())

                indexData.putShort(i10.toShort())
                indexData.putShort(i11.toShort())
                indexData.putShort(i01.toShort())
            }
        }
        indexData.flip()

        vertexBuffer = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(1)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride)
            .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT3, 12, stride)
            .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 24, stride)
            .attribute(VertexAttribute.UV1, 0, AttributeType.FLOAT2, 32, stride)
            .build(engine)

        vertexBuffer?.setBufferAt(engine, 0, vertexData)

        indexBuffer = IndexBuffer.Builder()
            .indexCount(indexCount)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)

        indexBuffer?.setBuffer(engine, indexData)

        terrainEntity = EntityManager.get().create()

        val vb = vertexBuffer ?: throw IllegalStateException("Vertex buffer not initialized")
        val ib = indexBuffer ?: throw IllegalStateException("Index buffer not initialized")
        val mat = materialInstance ?: throw IllegalStateException("Material instance not initialized")

        RenderableManager.Builder(1)
            .boundingBox(Box(0f, 0f, 0f, regionWidth, regionHeight, 1000f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .material(0, mat)
            .build(engine, terrainEntity)
    }

    private fun rebuildMesh() {
        val resX = regionWidth.toInt() + 1
        val resY = regionHeight.toInt() + 1
        val stride = (3 + 3 + 2 + 2) * 4
        val vertexData = ByteBuffer.allocateDirect(resX * resY * stride)
            .order(ByteOrder.nativeOrder())

        for (y in 0 until resY) {
            for (x in 0 until resX) {
                val px = x.toFloat()
                val py = y.toFloat()
                val pz = heightmap[y * resX + x]

                vertexData.putFloat(px)
                vertexData.putFloat(py)
                vertexData.putFloat(pz)

                val normal = getNormalAt(px, py)
                vertexData.putFloat(normal.x)
                vertexData.putFloat(normal.y)
                vertexData.putFloat(normal.z)

                vertexData.putFloat(px / regionWidth)
                vertexData.putFloat(py / regionHeight)

                vertexData.putFloat(pz)
                vertexData.putFloat(0f)
            }
        }
        vertexData.flip()

        vertexBuffer?.setBufferAt(engine, 0, vertexData)
    }

    private fun updateMaterial() {
        val mat = materialInstance ?: return
        val sampler = TextureSampler(
            TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
            TextureSampler.MagFilter.LINEAR,
            TextureSampler.WrapMode.REPEAT
        )
        detailTextures.forEachIndexed { index, texture ->
            texture?.let { mat.setParameter("detail$index", it, sampler) }
        }
        try {
            mat.setParameter(
                "startHeights",
                startHeights[0], startHeights[1], startHeights[2], startHeights[3]
            )
            mat.setParameter(
                "heightRanges",
                heightRanges[0], heightRanges[1], heightRanges[2], heightRanges[3]
            )
            mat.setParameter("detailScale", detailUVScale)
        } catch (e: Exception) {
            Log.v(TAG, "Terrain material params not applied: ${e.message}")
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

    fun getDiagnostics(): Diagnostics {
        val dirty = patches.count { it.dirty }
        val patchesX = regionWidth.toInt() / PATCH_SIZE
        val patchesY = regionHeight.toInt() / PATCH_SIZE
        val visible = if (terrainEntity != 0) patchesX * patchesY else 0
        return Diagnostics(
            visiblePatchCount = visible,
            dirtyPatchCount = dirty
        )
    }
}

private class TerrainPatch {
    var dirty = true
    var lod = 0
}
