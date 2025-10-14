package com.lumiyaviewer.lumiya.slproto.terrain

import com.google.common.primitives.Floats
import java.util.Arrays

class TerrainPatchHeightMap {
    private Int hashCode = getHashCode()
    private Float[] heightMap
    private Int mapHeight
    private Int mapWidth
    private Float[] normalMap
    private Float waterHeight

    TerrainPatchHeightMap(Float f, Float[] fArr, Float[] fArr2, Int i, Int i2) {
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

    Boolean equals(Any obj) {
        if (!(obj instanceof TerrainPatchHeightMap)) {
            return false
        }
        TerrainPatchHeightMap terrainPatchHeightMap = (TerrainPatchHeightMap) obj
        if (terrainPatchHeightMap.waterHeight == this.waterHeight && terrainPatchHeightMap.mapWidth == this.mapWidth && terrainPatchHeightMap.mapHeight == this.mapHeight && Arrays.equals(terrainPatchHeightMap.heightMap, this.heightMap)) {
            return Arrays.equals(terrainPatchHeightMap.normalMap, this.normalMap)
        }
        return false
    }

    Float[] getHeightArray() {
        return this.heightMap
    }

    Int getMapHeight() {
        return this.mapHeight
    }

    Int getMapWidth() {
        return this.mapWidth
    }

    Float getMaxHeight() {
        return Floats.max(this.heightMap)
    }

    Float getMinHeight() {
        return Floats.min(this.heightMap)
    }

    Float[] getNormalArray() {
        return this.normalMap
    }

    Float getWaterHeight() {
        return this.waterHeight
    }

    Int hashCode() {
        return this.hashCode
    }
}
