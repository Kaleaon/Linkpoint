// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.terrain;

import java.util.Arrays;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake;
import java.util.UUID;

public class TerrainTextures
{
    private static UUID[] defaultTerrainTextures;
    private final float[] terrainHeightRange;
    private final float[] terrainStartHeight;
    private final UUID[] textureIDs;
    
    static {
        TerrainTextures.defaultTerrainTextures = new UUID[] { UUID.fromString("0bc58228-74a0-7e83-89bc-5c23464bcec5"), UUID.fromString("63338ede-0037-c4fd-855b-015d77112fc8"), UUID.fromString("303cd381-8560-7579-23f1-f0a880799740"), UUID.fromString("53a2f406-4895-1d13-d541-d2e3b86bc19c") };
    }
    
    public TerrainTextures() {
        this.terrainStartHeight = new float[4];
        this.terrainHeightRange = new float[4];
        this.textureIDs = new UUID[4];
        System.arraycopy(TerrainTextures.defaultTerrainTextures, 0, this.textureIDs, 0, 4);
    }
    
    public TerrainTextures(final RegionHandshake.RegionInfo regionInfo) {
        this.terrainStartHeight = new float[4];
        this.terrainHeightRange = new float[4];
        final UUID[] textureIDs = { regionInfo.TerrainDetail0, regionInfo.TerrainDetail1, regionInfo.TerrainDetail2, regionInfo.TerrainDetail3 };
        for (int i = 0; i < 4; ++i) {
            Debug.Printf("Terrain: texture[%d] = %s", i, textureIDs[i].toString());
            if (textureIDs[i] == null || textureIDs[i].equals(UUIDPool.ZeroUUID)) {
                textureIDs[i] = TerrainTextures.defaultTerrainTextures[i];
            }
        }
        this.textureIDs = textureIDs;
        this.terrainStartHeight[0] = regionInfo.TerrainStartHeight00;
        this.terrainStartHeight[1] = regionInfo.TerrainStartHeight01;
        this.terrainStartHeight[2] = regionInfo.TerrainStartHeight10;
        this.terrainStartHeight[3] = regionInfo.TerrainStartHeight11;
        this.terrainHeightRange[0] = regionInfo.TerrainHeightRange00;
        this.terrainHeightRange[1] = regionInfo.TerrainHeightRange01;
        this.terrainHeightRange[2] = regionInfo.TerrainHeightRange10;
        this.terrainHeightRange[3] = regionInfo.TerrainHeightRange11;
    }
    
    private static float bilinearCorners(final float[] array, final float n, final float n2) {
        return (array[0] * n + array[1] * (1.0f - n)) * n2 + (array[2] * n + array[3] * (1.0f - n)) * (1.0f - n2);
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (!(o instanceof TerrainTextures)) {
            return false;
        }
        final TerrainTextures terrainTextures = (TerrainTextures)o;
        boolean equals = b;
        if (Arrays.equals(this.textureIDs, terrainTextures.textureIDs)) {
            equals = b;
            if (Arrays.equals(this.terrainStartHeight, terrainTextures.terrainStartHeight)) {
                equals = Arrays.equals(this.terrainHeightRange, terrainTextures.terrainHeightRange);
            }
        }
        return equals;
    }
    
    public int getNeededLayerMask(final float[] array) {
        int n = 0;
        for (final float n2 : array) {
            final int n3 = (int)Math.floor(n2);
            final int n4 = n |= 1 << n3;
            if (n2 - n3 != 0.0f) {
                n = (n4 | 1 << n3 + 1);
            }
        }
        return n & 0xF;
    }
    
    public float[] getTextureHeightMap(final float[] array, final int n, final int n2, final float n3, final float n4, final float n5, final float n6) {
        final float[] array2 = new float[n * n2];
        for (int i = 0; i < n2; ++i) {
            final float n7 = i / (float)(n2 - 1);
            for (int j = 0; j < n; ++j) {
                final float n8 = j / (float)(n - 1);
                array2[i * n + j] = Math.min(3.0f, Math.max(0.0f, (array[i * n + j] - bilinearCorners(this.terrainStartHeight, n8 * n5 + n3, n7 * n6 + n4)) * 4.0f / bilinearCorners(this.terrainHeightRange, n8 * n5 + n3, n7 * n6 + n4)));
            }
        }
        return array2;
    }
    
    public UUID getTextureUUID(final int n) {
        return this.textureIDs[n];
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.textureIDs) + Arrays.hashCode(this.terrainStartHeight) + Arrays.hashCode(this.terrainHeightRange);
    }
}
