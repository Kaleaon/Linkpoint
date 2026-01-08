package com.lumiyaviewer.lumiya.slproto.terrain

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake
import com.lumiyaviewer.lumiya.utils.BitBuffer
import kotlin.math.max
import kotlin.math.min

class TerrainData {
    val PatchesPerEdge = 16
    val PatchesSize = 16
    val TerrainPerEdge = 256
    
    private val heightMap = FloatArray(65536)
    private val patchDirtyMap = BooleanArray(256)
    @Volatile private var terrainTextures = TerrainTextures()
    private var validCount = 0
    private val validMap = BooleanArray(65536)
    private val vertexHeights = FloatArray(66049)
    private val vertexLock = Any()
    private val vertexNormals = FloatArray(132098)
    private val vertexValids = BooleanArray(66049)
    private var waterHeight = 0.0f
    private var waterHeightValid = false

    @Synchronized
    fun setWaterHeight(f: Float) {
        if (waterHeight != f || !waterHeightValid) {
            waterHeight = f
            waterHeightValid = true
            updateEntireTerrain()
        }
    }

    private fun markVerticesDirty(minX: Int, minY: Int, maxX: Int, maxY: Int) {
        val patchMinX = minX / 16
        val patchMinY = minY / 16
        val patchMaxX = maxX / 16
        val patchMaxY = maxY / 16
        
        synchronized(vertexLock) {
            for (py in patchMinY..patchMaxY) {
                for (px in patchMinX..patchMaxX) {
                    if (px in 0 until 16 && py in 0 until 16) {
                        patchDirtyMap[(py * 16) + px] = true
                    }
                }
            }
        }
        
        for (py in patchMinY..patchMaxY) {
            for (px in patchMinX..patchMaxX) {
                if (px in 0 until 16 && py in 0 until 16) {
                    SpatialIndex.getInstance().updateTerrainPatch(px, py, this)
                }
            }
        }
    }

    private fun updateVerticesInRegion(minX: Int, minY: Int, maxX: Int, maxY: Int) {
        var y = minY
        while (y <= maxY) {
            for (x in minX..maxX) {
                val xMinus = min(max(0, x - 1), 255)
                val yMinus = min(max(0, y - 1), 255)
                val xClamped = min(max(0, x), 255)
                val yClamped = min(max(0, y), 255)
                
                var sum = 0.0f
                var count = 0
                
                if (validMap[(yMinus * 256) + xMinus]) {
                    sum += heightMap[(yMinus * 256) + xMinus]
                    count++
                }
                if (validMap[(yMinus * 256) + xClamped]) {
                    sum += heightMap[(yMinus * 256) + xClamped]
                    count++
                }
                if (validMap[(yClamped * 256) + xMinus]) {
                    sum += heightMap[(yClamped * 256) + xMinus]
                    count++
                }
                if (validMap[(yClamped * 256) + xClamped]) {
                    sum += heightMap[(yClamped * 256) + xClamped]
                    count++
                }
                
                val idx = (y * 257) + x
                if (count == 4) {
                    vertexHeights[idx] = sum / count.toFloat()
                    val dx = heightMap[(yClamped * 256) + xClamped] - heightMap[xMinus + (yClamped * 256)]
                    val dy = heightMap[(yClamped * 256) + xClamped] - heightMap[(yMinus * 256) + xClamped]
                    vertexNormals[idx * 2] = dx
                    vertexNormals[(idx * 2) + 1] = dy
                    vertexValids[idx] = true
                } else {
                    vertexValids[idx] = false
                }
            }
            y++
        }
    }

    @Synchronized
    fun applyRegionInfo(regionInfo: RegionHandshake.RegionInfo) {
        setWaterHeight(regionInfo.WaterHeight)
        val newTextures = TerrainTextures(regionInfo)
        if (newTextures != terrainTextures) {
            terrainTextures = newTextures
            updateEntireTerrain()
        }
    }

    fun processLayerData(data: ByteArray) {
        val bitBuffer = BitBuffer(data)
        val stride = bitBuffer.getBits(16)
        val patchSize = bitBuffer.getBits(8)
        val type = bitBuffer.getBits(8)
        
        Debug.Log("Terrain: ProcessLayerData: stride 0x%x patchSize 0x%x type 0x%x".format(stride, patchSize, type))
        
        synchronized(vertexLock) {
            while (!bitBuffer.isEOF()) {
                val patch = TerrainPatch.DecompressPatch(bitBuffer, patchSize) ?: break
                val x = patch.getX()
                val y = patch.getY()
                
                if (x < 16 && y < 16) {
                    for (py in 0 until patchSize) {
                        val globalY = (y * 16) + py
                        if (globalY in 0 until 256) {
                            for (px in 0 until patchSize) {
                                val globalX = (x * 16) + px
                                if (globalX in 0 until 256) {
                                    heightMap[(globalY * 256) + globalX] = patch.heightMap[(py * patchSize) + px]
                                    if (!validMap[(globalY * 256) + globalX]) {
                                        validCount++
                                        validMap[(globalY * 256) + globalX] = true
                                    }
                                }
                            }
                        }
                    }
                    markVerticesDirty(x * 16, y * 16, ((x + 1) * 16) + 1, ((y + 1) * 16) + 1)
                }
            }
        }
        Debug.Printf("Terrain: LayerData received, valid count is now %d", validCount)
    }

    fun getPatchInfo(patchX: Int, patchY: Int): TerrainPatchInfo? {
        synchronized(vertexLock) {
            if (patchDirtyMap[(patchY * 16) + patchX]) {
                patchDirtyMap[(patchY * 16) + patchX] = false
                updateVerticesInRegion(patchX * 16, patchY * 16, (patchX + 1) * 16, (patchY + 1) * 16)
            }
        }
        
        var isValid = true
        val startIdx = ((patchY * 16) * 257) + (patchX * 16)
        
        outer@ for (y in 0 until 17) {
            val rowOffset = startIdx + (y * 257)
            for (x in 0 until 17) {
                if (!vertexValids[rowOffset + x]) {
                    isValid = false
                    break@outer
                }
            }
        }
        
        if (!isValid) {
            return null
        }
        
        val heights = FloatArray(289)
        val normals = FloatArray(578)
        
        for (y in 0 until 17) {
            val rowOffset = startIdx + (y * 257)
            for (x in 0 until 17) {
                val idx = rowOffset + x
                heights[(y * 17) + x] = vertexHeights[idx]
                normals[((y * 17) + x) * 2] = vertexNormals[idx * 2]
                normals[(((y * 17) + x) * 2) + 1] = vertexNormals[(idx * 2) + 1]
            }
        }
        
        return TerrainPatchInfo(
            TerrainPatchHeightMap(waterHeight, heights, normals, 17, 17),
            terrainTextures,
            patchX.toFloat() / 16.0f,
            patchY.toFloat() / 16.0f,
            0.0625f,
            0.0625f
        )
    }

    fun isUnderWater(f: Float): Boolean {
        return waterHeightValid && f < waterHeight
    }

    fun reset() {
        synchronized(vertexLock) {
            waterHeightValid = false
            validCount = 0
            validMap.fill(false)
            vertexValids.fill(false)
            patchDirtyMap.fill(false)
        }
    }

    fun updateEntireTerrain() {
        markVerticesDirty(0, 0, 256, 256)
    }
}

// Helper classes needed for compilation
data class TerrainPatchInfo(
    val heightMap: TerrainPatchHeightMap,
    val textures: TerrainTextures,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

data class TerrainPatchHeightMap(
    val waterHeight: Float,
    val heights: FloatArray,
    val normals: FloatArray,
    val width: Int,
    val height: Int
)

// Placeholder for TerrainTextures and TerrainPatch if they are not defined elsewhere
// Assuming they are defined elsewhere or need stubbing.
// The decompiled code referenced them, so they might exist. 
// If errors occur, I will add them.
