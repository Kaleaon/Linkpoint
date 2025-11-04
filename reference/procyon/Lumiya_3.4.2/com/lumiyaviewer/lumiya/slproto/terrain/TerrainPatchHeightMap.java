// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.terrain;

import com.google.common.primitives.Floats;
import java.util.Arrays;

public class TerrainPatchHeightMap
{
    private final int hashCode;
    private final float[] heightMap;
    private final int mapHeight;
    private final int mapWidth;
    private final float[] normalMap;
    private final float waterHeight;
    
    public TerrainPatchHeightMap(final float waterHeight, final float[] array, final float[] array2, final int mapWidth, final int mapHeight) {
        this.waterHeight = waterHeight;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        System.arraycopy(array, 0, this.heightMap = new float[array.length], 0, array.length);
        System.arraycopy(array2, 0, this.normalMap = new float[array2.length], 0, array2.length);
        this.hashCode = this.getHashCode();
    }
    
    private int getHashCode() {
        return Float.floatToIntBits(this.waterHeight) + 0 + Arrays.hashCode(this.heightMap) + Arrays.hashCode(this.normalMap) + this.mapWidth + this.mapHeight;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (!(o instanceof TerrainPatchHeightMap)) {
            return false;
        }
        final TerrainPatchHeightMap terrainPatchHeightMap = (TerrainPatchHeightMap)o;
        boolean equals = b;
        if (terrainPatchHeightMap.waterHeight == this.waterHeight) {
            equals = b;
            if (terrainPatchHeightMap.mapWidth == this.mapWidth) {
                equals = b;
                if (terrainPatchHeightMap.mapHeight == this.mapHeight) {
                    equals = b;
                    if (Arrays.equals(terrainPatchHeightMap.heightMap, this.heightMap)) {
                        equals = Arrays.equals(terrainPatchHeightMap.normalMap, this.normalMap);
                    }
                }
            }
        }
        return equals;
    }
    
    public final float[] getHeightArray() {
        return this.heightMap;
    }
    
    public final int getMapHeight() {
        return this.mapHeight;
    }
    
    public final int getMapWidth() {
        return this.mapWidth;
    }
    
    public float getMaxHeight() {
        return Floats.max(this.heightMap);
    }
    
    public float getMinHeight() {
        return Floats.min(this.heightMap);
    }
    
    public final float[] getNormalArray() {
        return this.normalMap;
    }
    
    public final float getWaterHeight() {
        return this.waterHeight;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
