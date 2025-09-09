package com.lumiyaviewer.lumiya.slproto.terrain;

import java.util.Arrays;

public class TerrainPatchInfo {
    private final int hashCode = getHashCode();
    private final TerrainPatchHeightMap heightMap;
    private final int layerMask;
    private final float[] textureHeightMap;
    private final TerrainTextures textures;

    public TerrainPatchInfo(TerrainPatchHeightMap terrainPatchHeightMap, TerrainTextures terrainTextures, float f, float f2, float f3, float f4) {
        this.heightMap = terrainPatchHeightMap;
        this.textures = terrainTextures;
        this.textureHeightMap = terrainTextures.getTextureHeightMap(terrainPatchHeightMap.getHeightArray(), terrainPatchHeightMap.getMapWidth(), terrainPatchHeightMap.getMapHeight(), f, f2, f3, f4);
        this.layerMask = terrainTextures.getNeededLayerMask(this.textureHeightMap);
    }

    private int getHashCode() {
        return this.heightMap.hashCode() + this.textures.hashCode() + this.layerMask + Arrays.hashCode(this.textureHeightMap);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TerrainPatchInfo)) {
            return false;
        }
        TerrainPatchInfo terrainPatchInfo = (TerrainPatchInfo) obj;
        if (!this.heightMap.equals(terrainPatchInfo.heightMap) || !this.textures.equals(terrainPatchInfo.textures) || this.layerMask != terrainPatchInfo.layerMask) {
            return false;
        }
        return Arrays.equals(this.textureHeightMap, terrainPatchInfo.textureHeightMap);
    }

    public TerrainPatchHeightMap getHeightMap() {
        return this.heightMap;
    }

    public final int getLayerMask() {
        return this.layerMask;
    }

    public float getMaxHeight() {
        return this.heightMap.getMaxHeight();
    }

    public float getMinHeight() {
        return this.heightMap.getMinHeight();
    }

    public final float[] getTextureHeightMap() {
        return this.textureHeightMap;
    }

    public TerrainTextures getTextures() {
        return this.textures;
    }

    public int hashCode() {
        return this.hashCode;
    }
}
