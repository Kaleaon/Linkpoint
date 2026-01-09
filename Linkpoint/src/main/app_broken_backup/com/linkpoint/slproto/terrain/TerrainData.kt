package com.linkpoint.slproto.terrain
import java.util.*

import androidx.core.view.InputDeviceCompat
import com.linkpoint.Debug
import com.linkpoint.render.spatial.SpatialIndex
import com.linkpoint.slproto.messages.RegionHandshake
import com.linkpoint.utils.BitBuffer

class TerrainData {
    Int PatchesPerEdge = 16
    Int PatchesSize = 16
    Int TerrainPerEdge = 256
    private val heightMap: FloatArray = FloatArray(65536)
    private val patchDirtyMap: BooleanArray = BooleanArray(256)
    private volatile TerrainTextures terrainTextures = TerrainTextures()
    private Int validCount = 0
    private val validMap: BooleanArray = BooleanArray(65536)
    private val vertexHeights: FloatArray = FloatArray(66049)
    private Any vertexLock = Any()
    private val vertexNormals: FloatArray = FloatArray(132098)
    private val vertexValids: BooleanArray = BooleanArray(66049)
    private Float waterHeight = 0.0f
    private Boolean waterHeightValid = false

    private synchronized Unit SetWaterHeight(Float f) {
        if (this.waterHeight != f || (!this.waterHeightValid)) {
            this.waterHeight = f
            this.waterHeightValid = true
            updateEntireTerrain()
        }
    }

    private Unit markVerticesDirty(Int i, Int i2, Int i3, Int i4) {
        Int i5 = i / 16
        Int i6 = i2 / 16
        Int i7 = i3 / 16
        Int i8 = i4 / 16
        synchronized (this.vertexLock) {
            for (Int i9 = i6; i9 <= i8; i9++) {
                for (Int i10 = i5; i10 <= i7; i10++) {
                    if (i10 >= 0 && i10 < 16 && i9 >= 0 && i9 < 16) {
                        this.patchDirtyMap[(i9 * 16) + i10] = true
                    }
                }
            }
        }
        for (Int i11 = i6; i11 <= i8; i11++) {
            for (Int i12 = i5; i12 <= i7; i12++) {
                if (i12 >= 0 && i12 < 16 && i11 >= 0 && i11 < 16) {
                    SpatialIndex.getInstance().updateTerrainPatch(i12, i11, this)
                }
            }
        }
    }

    private Unit updateVerticesInRegion(Int i, Int i2, Int i3, Int i4) {
        while (i2 <= i4) {
            for (Int i5 = i; i5 <= i3; i5++) {
                Int min = Math.min(Math.max(0, i5 - 1), 255)
                Int min2 = Math.min(Math.max(0, i2 - 1), 255)
                Int min3 = Math.min(Math.max(0, i5), 255)
                Int min4 = Math.min(Math.max(0, i2), 255)
                Float f = 0.0f
                Int i6 = 0
                if (min >= 0 && min < 256 && min2 >= 0 && min2 < 256 && this.validMap[(min2 * 256) + min]) {
                    f = 0.0f + this.heightMap[(min2 * 256) + min]
                    i6 = 1
                }
                if (min3 >= 0 && min3 < 256 && min2 >= 0 && min2 < 256 && this.validMap[(min2 * 256) + min3]) {
                    f += this.heightMap[(min2 * 256) + min3]
                    i6++
                }
                if (min >= 0 && min < 256 && min4 >= 0 && min4 < 256 && this.validMap[(min4 * 256) + min]) {
                    f += this.heightMap[(min4 * 256) + min]
                    i6++
                }
                if (min3 >= 0 && min3 < 256 && min4 >= 0 && min4 < 256 && this.validMap[(min4 * 256) + min3]) {
                    f += this.heightMap[(min4 * 256) + min3]
                    i6++
                }
                if (i6 == 4) {
                    this.vertexHeights[(i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5] = f / (i6.toFloat())
                    Float f2 = this.heightMap[(min4 * 256) + min3] - this.heightMap[min + (min4 * 256)]
                    Float f3 = this.heightMap[(min4 * 256) + min3] - this.heightMap[(min2 * 256) + min3]
                    this.vertexNormals[((i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5) * 2] = f2
                    this.vertexNormals[(((i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5) * 2) + 1] = f3
                    this.vertexValids[(i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5] = true
                } else {
                    this.vertexValids[(i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5] = false
                }
            }
            i2++
        }
    }

    synchronized Unit ApplyRegionInfo(RegionHandshake.RegionInfo regionInfo) {
        SetWaterHeight(regionInfo.WaterHeight)
        TerrainTextures terrainTextures2 = TerrainTextures(regionInfo)
        if (!terrainTextures2.equals(this.terrainTextures)) {
            this.terrainTextures = terrainTextures2
            updateEntireTerrain()
        }
    }

    fun ProcessLayerData(ByteArray bArr): Unit {
        TerrainPatch DecompressPatch
        BitBuffer bitBuffer = BitBuffer(bArr)
        Int bits = bitBuffer.getBits(16)
        Int bits2 = bitBuffer.getBits(8)
        Debug.Log(String.format("Terrain: ProcessLayerData: stride 0x%x patchSize 0x%x type 0x%x", Any[]{Int.valueOf(bits), Int.valueOf(bits2), Int.valueOf(bitBuffer.getBits(8))}))
        synchronized (this.vertexLock) {
            while (!bitBuffer.isEOF() && (DecompressPatch = TerrainPatch.DecompressPatch(bitBuffer, bits2)) != null) {
                Int x = DecompressPatch.getX()
                Int y = DecompressPatch.getY()
                if (x < 16 && y < 16) {
                    for (i in 0 until bits2) {
                        Int i2 = (y * 16) + i
                        if (i2 >= 0 && i2 < 256) {
                            for (i3 in 0 until bits2) {
                                Int i4 = (x * 16) + i3
                                if (i4 >= 0 && i4 < 256) {
                                    this.heightMap[(i2 * 256) + i4] = DecompressPatch.heightMap[(i * bits2) + i3]
                                    if (!this.validMap[(i2 * 256) + i4]) {
                                        this.validCount++
                                        this.validMap[i4 + (i2 * 256)] = true
                                    }
                                }
                            }
                        }
                    }
                    markVerticesDirty(x * 16, y * 16, ((x + 1) * 16) + 1, ((y + 1) * 16) + 1)
                }
            }
        }
        Debug.Printf("Terrain: LayerData received, valid count is now %d", Int.valueOf(this.validCount))
    }

    fun getPatchInfo(Int i, Int i2): TerrainPatchInfo {
        synchronized (this.vertexLock) {
            if (this.patchDirtyMap[(i2 * 16) + i]) {
                this.patchDirtyMap[(i2 * 16) + i] = false
                updateVerticesInRegion(i * 16, i2 * 16, (i + 1) * 16, (i2 + 1) * 16)
            }
        }
        Boolean z = true
        for (i3 in 0 until 17) {
            Int i4 = ((i2 * 16) + i3) * InputDeviceCompat.SOURCE_KEYBOARD
            Int i5 = 0
            while (true) {
                if (i5 >= 17) {
                    break
                } else if (!this.vertexValids[i4 + i5 + (i * 16)]) {
                    z = false
                    break
                } else {
                    i5++
                }
            }
        }
        if (!z) {
            return null
        }
        FloatArray fArr = FloatArray(289)
        FloatArray fArr2 = FloatArray(578)
        Int i6 = 0
        while (true) {
            Int i7 = i6
            if (i7 < 17) {
                Int i8 = ((i2 * 16) + i7) * InputDeviceCompat.SOURCE_KEYBOARD
                for (i9 in 0 until 17) {
                    Float f = this.vertexHeights[i8 + i9 + (i * 16)]
                    Float f2 = this.vertexNormals[(i8 + i9 + (i * 16)) * 2]
                    Float f3 = this.vertexNormals[((i8 + i9 + (i * 16)) * 2) + 1]
                    fArr[(i7 * 17) + i9] = f
                    fArr2[((i7 * 17) + i9) * 2] = f2
                    fArr2[(((i7 * 17) + i9) * 2) + 1] = f3
                }
                i6 = i7 + 1
            } else {
                return TerrainPatchInfo(TerrainPatchHeightMap(this.waterHeight, fArr, fArr2, 17, 17), this.terrainTextures, (i.toFloat()) / 16.0f, (i.toFloat()) / 16.0f, 0.0625f, 0.0625f)
            }
        }
    }

    fun isUnderWater(Float f): Boolean {
        return this.waterHeightValid && f < this.waterHeight
    }

    fun reset(): Unit {
        synchronized (this.vertexLock) {
            this.waterHeightValid = false
            this.validCount = 0
            for (i in 0 until 65536) {
                this.validMap[i] = false
            }
            for (i2 in 0 until 66049) {
                this.vertexValids[i2] = false
            }
            for (i3 in 0 until 256) {
                this.patchDirtyMap[i3] = false
            }
        }
    }

    fun updateEntireTerrain(): Unit {
        markVerticesDirty(0, 0, 256, 256)
    }
}
