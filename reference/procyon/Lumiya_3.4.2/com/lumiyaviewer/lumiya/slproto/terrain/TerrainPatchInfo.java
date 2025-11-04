// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.terrain;

import java.util.Arrays;

public class TerrainPatchInfo
{
    private final int hashCode;
    private final TerrainPatchHeightMap heightMap;
    private final int layerMask;
    private final float[] textureHeightMap;
    private final TerrainTextures textures;
    
    public TerrainPatchInfo(final TerrainPatchHeightMap heightMap, final TerrainTextures textures, final float n, final float n2, final float n3, final float n4) {
        this.heightMap = heightMap;
        this.textures = textures;
        this.textureHeightMap = textures.getTextureHeightMap(heightMap.getHeightArray(), heightMap.getMapWidth(), heightMap.getMapHeight(), n, n2, n3, n4);
        this.layerMask = textures.getNeededLayerMask(this.textureHeightMap);
        this.hashCode = this.getHashCode();
    }
    
    private int getHashCode() {
        return this.heightMap.hashCode() + this.textures.hashCode() + this.layerMask + Arrays.hashCode(this.textureHeightMap);
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (!(o instanceof TerrainPatchInfo)) {
            return false;
        }
        final TerrainPatchInfo terrainPatchInfo = (TerrainPatchInfo)o;
        boolean equals = b;
        if (this.heightMap.equals(terrainPatchInfo.heightMap)) {
            equals = b;
            if (this.textures.equals(terrainPatchInfo.textures)) {
                equals = b;
                if (this.layerMask == terrainPatchInfo.layerMask) {
                    equals = Arrays.equals(this.textureHeightMap, terrainPatchInfo.textureHeightMap);
                }
            }
        }
        return equals;
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
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
