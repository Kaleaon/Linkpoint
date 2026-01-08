package com.lumiyaviewer.lumiya.slproto.terrain

import java.util.Arrays
import kotlin.math.max
import kotlin.math.min

class TerrainPatchHeightMap {
    private val hashCode: Int
    private val heightMap: FloatArray
    private val mapHeight: Int
    private val mapWidth: Int
    private val normalMap: FloatArray
    private val waterHeight: Float

    constructor(f: Float, fArr: FloatArray, fArr2: FloatArray, i: Int, i2: Int) {
        this.waterHeight = f
        this.mapWidth = i
        this.mapHeight = i2
        this.heightMap = FloatArray(fArr.size)
        System.arraycopy(fArr, 0, this.heightMap, 0, fArr.size)
        this.normalMap = FloatArray(fArr2.size)
        System.arraycopy(fArr2, 0, this.normalMap, 0, fArr2.size)
        this.hashCode = calculateHashCode()
    }

    private fun calculateHashCode(): Int {
        return java.lang.Float.floatToIntBits(this.waterHeight) + 0 + Arrays.hashCode(this.heightMap) + Arrays.hashCode(this.normalMap) + this.mapWidth + this.mapHeight
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TerrainPatchHeightMap) {
            return false
        }
        if (other.waterHeight == this.waterHeight && other.mapWidth == this.mapWidth && other.mapHeight == this.mapHeight && Arrays.equals(other.heightMap, this.heightMap)) {
            return Arrays.equals(other.normalMap, this.normalMap)
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
        if (heightMap.isEmpty()) return 0f
        var maxVal = heightMap[0]
        for (v in heightMap) {
            if (v > maxVal) maxVal = v
        }
        return maxVal
    }

    fun getMinHeight(): Float {
        if (heightMap.isEmpty()) return 0f
        var minVal = heightMap[0]
        for (v in heightMap) {
            if (v < minVal) minVal = v
        }
        return minVal
    }

    fun getNormalArray(): FloatArray {
        return this.normalMap
    }

    fun getWaterHeight(): Float {
        return this.waterHeight
    }

    override fun hashCode(): Int {
        return this.hashCode
    }
}
