package com.lumiyaviewer.lumiya.slproto.terrain

import java.util.Arrays

class TerrainPatchInfo {
    private Int hashCode = getHashCode()
    private TerrainPatchHeightMap heightMap
    private Int layerMask
    private Float[] textureHeightMap
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

    Boolean equals(Any obj) {
        if (!(obj instanceof TerrainPatchInfo)) {
            return false
        }
        TerrainPatchInfo terrainPatchInfo = (TerrainPatchInfo) obj
        if (!this.heightMap.equals(terrainPatchInfo.heightMap) || !this.textures.equals(terrainPatchInfo.textures) || this.layerMask != terrainPatchInfo.layerMask) {
            return false
        }
        return Arrays.equals(this.textureHeightMap, terrainPatchInfo.textureHeightMap)
    }

    TerrainPatchHeightMap getHeightMap() {
        return this.heightMap
    }

    Int getLayerMask() {
        return this.layerMask
    }

    Float getMaxHeight() {
        return this.heightMap.getMaxHeight()
    }

    Float getMinHeight() {
        return this.heightMap.getMinHeight()
    }

    Float[] getTextureHeightMap() {
        return this.textureHeightMap
    }

    TerrainTextures getTextures() {
        return this.textures
    }

    Int hashCode() {
        return this.hashCode
    }
}
