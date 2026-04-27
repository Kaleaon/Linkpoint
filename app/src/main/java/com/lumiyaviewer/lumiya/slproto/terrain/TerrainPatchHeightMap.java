package com.lumiyaviewer.lumiya.slproto.terrain;

import com.google.common.primitives.Floats;
import java.util.Arrays;

public class TerrainPatchHeightMap {
    private final int hashCode = getHashCode();
    private final float[] heightMap;
    private final int mapHeight;
    private final int mapWidth;
    private final float[] normalMap;
    private final float waterHeight;

    public TerrainPatchHeightMap(float f, float[] fArr, float[] fArr2, int i, int i2) {
        this.waterHeight = f;
        this.mapWidth = i;
        this.mapHeight = i2;
        this.heightMap = new float[fArr.length];
        System.arraycopy(fArr, 0, this.heightMap, 0, fArr.length);
        this.normalMap = new float[fArr2.length];
        System.arraycopy(fArr2, 0, this.normalMap, 0, fArr2.length);
    }

    private int getHashCode() {
        return Float.floatToIntBits(this.waterHeight) + 0 + Arrays.hashCode(this.heightMap) + Arrays.hashCode(this.normalMap) + this.mapWidth + this.mapHeight;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TerrainPatchHeightMap)) {
            return false;
        }
        TerrainPatchHeightMap terrainPatchHeightMap = (TerrainPatchHeightMap) obj;
        if (terrainPatchHeightMap.waterHeight == this.waterHeight && terrainPatchHeightMap.mapWidth == this.mapWidth && terrainPatchHeightMap.mapHeight == this.mapHeight && Arrays.equals(terrainPatchHeightMap.heightMap, this.heightMap)) {
            return Arrays.equals(terrainPatchHeightMap.normalMap, this.normalMap);
        }
        return false;
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

    public int hashCode() {
        return this.hashCode;
    }
}
