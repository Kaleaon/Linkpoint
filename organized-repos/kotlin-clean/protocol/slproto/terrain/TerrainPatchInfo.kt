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

    private Int getHashCode() {
        return this.heightMap.hashCode() + this.textures.hashCode() + this.layerMask + Arrays.hashCode(this.textureHeightMap)
    }

    public Boolean equals(Object obj) {
        if (!(obj instanceof TerrainPatchInfo)) {
            return false
        }
        TerrainPatchInfo terrainPatchInfo = (TerrainPatchInfo) obj
        if (!this.heightMap.equals(terrainPatchInfo.heightMap) || !this.textures.equals(terrainPatchInfo.textures) || this.layerMask != terrainPatchInfo.layerMask) {
            return false
        }
        return Arrays.equals(this.textureHeightMap, terrainPatchInfo.textureHeightMap)
    }

    public TerrainPatchHeightMap getHeightMap() {
        return this.heightMap
    }

    val Int getLayerMask() {
        return this.layerMask
    }

    public Float getMaxHeight() {
        return this.heightMap.getMaxHeight()
    }

    public Float getMinHeight() {
        return this.heightMap.getMinHeight()
    }

    val FloatArray getTextureHeightMap() {
        return this.textureHeightMap
    }

    public TerrainTextures getTextures() {
        return this.textures
    }

    public Int hashCode() {
        return this.hashCode
    }
}
