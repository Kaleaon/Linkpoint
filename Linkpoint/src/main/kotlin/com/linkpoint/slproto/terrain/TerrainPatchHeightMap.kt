package com.linkpoint.slproto.terrain

import com.google.common.primitives.Floats
import java.util.Arrays

class TerrainPatchHeightMap {
    private val Int hashCode = getHashCode()
    private val Float[] heightMap
    private val Int mapHeight
    private val Int mapWidth
    private val Float[] normalMap
    private val Float waterHeight

    public TerrainPatchHeightMap(Float f, Float[] fArr, Float[] fArr2, Int i, Int i2) {
        this.waterHeight = f
        this.mapWidth = i
        this.mapHeight = i2
        this.heightMap = Float[fArr.length]
        System.arraycopy(fArr, 0, this.heightMap, 0, fArr.length)
        this.normalMap = Float[fArr2.length]
        System.arraycopy(fArr2, 0, this.normalMap, 0, fArr2.length)
    }

    private Int getHashCode() {
        return Float.floatToIntBits(this.waterHeight) + 0 + Arrays.hashCode(this.heightMap) + Arrays.hashCode(this.normalMap) + this.mapWidth + this.mapHeight
    }

    public Boolean equals(Object obj) {
        if (!(obj instanceof TerrainPatchHeightMap)) {
            return false
        }
        TerrainPatchHeightMap terrainPatchHeightMap = (TerrainPatchHeightMap) obj
        if (terrainPatchHeightMap.waterHeight == this.waterHeight && terrainPatchHeightMap.mapWidth == this.mapWidth && terrainPatchHeightMap.mapHeight == this.mapHeight && Arrays.equals(terrainPatchHeightMap.heightMap, this.heightMap)) {
            return Arrays.equals(terrainPatchHeightMap.normalMap, this.normalMap)
        }
        return false
    }

    val Float[] getHeightArray() {
        return this.heightMap
    }

    val Int getMapHeight() {
        return this.mapHeight
    }

    val Int getMapWidth() {
        return this.mapWidth
    }

    public Float getMaxHeight() {
        return Floats.max(this.heightMap)
    }

    public Float getMinHeight() {
        return Floats.min(this.heightMap)
    }

    val Float[] getNormalArray() {
        return this.normalMap
    }

    val Float getWaterHeight() {
        return this.waterHeight
    }

    public Int hashCode() {
        return this.hashCode
    }
}
