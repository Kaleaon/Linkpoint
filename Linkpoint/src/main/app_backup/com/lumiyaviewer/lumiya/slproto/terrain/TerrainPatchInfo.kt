package com.lumiyaviewer.lumiya.slproto.terrain

import java.util.Arrays

class TerrainPatchInfo(
    val heightMap: TerrainPatchHeightMap,
    val textures: TerrainTextures,
    f: Float,
    f2: Float,
    f3: Float,
    f4: Float
) {
    private val hashCode: Int
    private val layerMask: Int
    private val textureHeightMap: FloatArray

    init {
        this.textureHeightMap = textures.getTextureHeightMap(
            heightMap.getHeightArray(),
            heightMap.getMapWidth(),
            heightMap.getMapHeight(),
            f, f2, f3, f4
        )
        this.layerMask = textures.getNeededLayerMask(this.textureHeightMap)
        this.hashCode = calculateHashCode()
    }

    private fun calculateHashCode(): Int {
        return heightMap.hashCode() + textures.hashCode() + layerMask + Arrays.hashCode(textureHeightMap)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TerrainPatchInfo) return false
        
        if (this.heightMap != other.heightMap || 
            this.textures != other.textures || 
            this.layerMask != other.layerMask) {
            return false
        }
        return Arrays.equals(this.textureHeightMap, other.textureHeightMap)
    }

    fun getLayerMask(): Int = layerMask

    fun getMaxHeight(): Float = heightMap.getMaxHeight()

    fun getMinHeight(): Float = heightMap.getMinHeight()

    fun getTextureHeightMap(): FloatArray = textureHeightMap

    override fun hashCode(): Int = hashCode
}
