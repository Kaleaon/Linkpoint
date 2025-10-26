package com.linkpoint.slproto.terrain

import java.util.Arrays

class TerrainPatchInfo {
    private val Int hashCode = getHashCode()
    private val TerrainPatchHeightMap heightMap
    private val Int layerMask
    private val FloatArray textureHeightMap
    private val TerrainTextures textures

    public TerrainPatchInfo(TerrainPatchHeightMap terrainPatchHeightMap, TerrainTextures terrainTextures, Float f, Float f2, Float f3, Float f4) {
        this.heightMap = terrainPatchHeightMap
        this.textures = terrainTextures
        this.textureHeightMap = terrainTextures.getTextureHeightMap(terrainPatchHeightMap.getHeightArray(), terrainPatchHeightMap.getMapWidth(), terrainPatchHeightMap.getMapHeight(), f, f2, f3, f4)
        this.layerMask = terrainTextures.getNeededLayerMask(this.textureHeightMap)
    }

     private fun getHashCode(): Int {
        return this.heightMap.hashCode() + this.textures.hashCode() + this.layerMask + Arrays.hashCode(this.textureHeightMap)
    }

     public override fun equals(obj: Object): Boolean {
        if (!(obj instanceof TerrainPatchInfo)) {
            return false
        }
        val terrainPatchInfo: TerrainPatchInfo = (TerrainPatchInfo) obj
        if (!this.heightMap.equals(terrainPatchInfo.heightMap) || !this.textures.equals(terrainPatchInfo.textures) || this.layerMask != terrainPatchInfo.layerMask) {
            return false
        }
        return Arrays.equals(this.textureHeightMap, terrainPatchInfo.textureHeightMap)
    }

     public fun getHeightMap(): TerrainPatchHeightMap {
        return this.heightMap
    }

    val Int getLayerMask() {
        return this.layerMask
    }

     public fun getMaxHeight(): Float {
        return this.heightMap.getMaxHeight()
    }

     public fun getMinHeight(): Float {
        return this.heightMap.getMinHeight()
    }

    val FloatArray getTextureHeightMap() {
        return this.textureHeightMap
    }

     public fun getTextures(): TerrainTextures {
        return this.textures
    }

     public override fun hashCode(): Int {
        return this.hashCode
    }
}
