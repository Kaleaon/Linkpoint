package com.linkpoint.slproto.terrain

import com.google.common.primitives.Floats
import java.util.Arrays

class TerrainPatchHeightMap {
    private Int hashCode = getHashCode()
    private FloatArray heightMap
    private Int mapHeight
    private Int mapWidth
    private FloatArray normalMap
    private Float waterHeight

    TerrainPatchHeightMap(Float f, FloatArray fArr, FloatArray fArr2, Int i, Int i2) {
        this.waterHeight = f
        this.mapWidth = i
        this.mapHeight = i2
        this.heightMap = Float[fArr.size]
        System.arraycopy(fArr, 0, this.heightMap, 0, fArr.size)
        this.normalMap = Float[fArr2.size]
        System.arraycopy(fArr2, 0, this.normalMap, 0, fArr2.size)
    }

    private Int getHashCode() {
        return Float.floatToIntBits(this.waterHeight) + 0 + Arrays.hashCode(this.heightMap) + Arrays.hashCode(this.normalMap) + this.mapWidth + this.mapHeight
    }

    fun equals(Any obj): Boolean {
        if (!(obj instanceof TerrainPatchHeightMap)) {
            return false
        }
        TerrainPatchHeightMap terrainPatchHeightMap = (TerrainPatchHeightMap) obj
        if (terrainPatchHeightMap.waterHeight == this.waterHeight && terrainPatchHeightMap.mapWidth == this.mapWidth && terrainPatchHeightMap.mapHeight == this.mapHeight && Arrays.equals(terrainPatchHeightMap.heightMap, this.heightMap)) {
            return Arrays.equals(terrainPatchHeightMap.normalMap, this.normalMap)
        }
        return false
    }

    fun getHeightArray(): FloatArray {
        return this.heightMap
    }

    fun getMapHeight(): Int {
        return this.mapHeight
    }

    fun getMapWidth(): Int {
        return this.mapWidth
    }

    fun getMaxHeight(): Float {
        return Floats.max(this.heightMap)
    }

    fun getMinHeight(): Float {
        return Floats.min(this.heightMap)
    }

    fun getNormalArray(): FloatArray {
        return this.normalMap
    }

    fun getWaterHeight(): Float {
        return this.waterHeight
    }

    fun hashCode(): Int {
        return this.hashCode
    }
}
