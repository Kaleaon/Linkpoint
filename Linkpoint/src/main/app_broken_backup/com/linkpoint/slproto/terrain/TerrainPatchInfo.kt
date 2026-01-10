package com.linkpoint.slproto.terrain

import java.util.Arrays

class TerrainPatchInfo {
    private Int hashCode = getHashCode()
    private TerrainPatchHeightMap heightMap
    private Int layerMask
    private FloatArray textureHeightMap
    private TerrainTextures textures

    TerrainPatchInfo(TerrainPatchHeightMap terrainPatchHeightMap, TerrainTextures terrainTextures, Float f, Float f2, Float f3, Float f4) {
        this.heightMap = terrainPatchHeightMap
        this.textures = terrainTextures
        this.textureHeightMap = terrainTextures.getTextureHeightMap(terrainPatchHeightMap.getHeightArray(), terrainPatchHeightMap.getMapWidth(), terrainPatchHeightMap.getMapHeight(), f, f2, f3, f4)
        this.layerMask = terrainTextures.getNeededLayerMask(this.textureHeightMap)
    }

    private Int getHashCode() {
        return this.heightMap.hashCode() + this.textures.hashCode() + this.layerMask + Arrays.hashCode(this.textureHeightMap)
    }

    fun equals(Any obj): Boolean {
        if (!(obj is TerrainPatchInfo)) {
            return false
        }
        TerrainPatchInfo terrainPatchInfo = (TerrainPatchInfo) obj
        if (!this.heightMap.equals(terrainPatchInfo.heightMap) || !this.textures.equals(terrainPatchInfo.textures) || this.layerMask != terrainPatchInfo.layerMask) {
            return false
        }
        return Arrays.equals(this.textureHeightMap, terrainPatchInfo.textureHeightMap)
    }

    fun getHeightMap(): TerrainPatchHeightMap {
        return this.heightMap
    }

    fun getLayerMask(): Int {
        return this.layerMask
    }

    fun getMaxHeight(): Float {
        return this.heightMap.getMaxHeight()
    }

    fun getMinHeight(): Float {
        return this.heightMap.getMinHeight()
    }

    fun getTextureHeightMap(): FloatArray {
        return this.textureHeightMap
    }

    fun getTextures(): TerrainTextures {
        return this.textures
    }

    fun hashCode(): Int {
        return this.hashCode
    }
}
