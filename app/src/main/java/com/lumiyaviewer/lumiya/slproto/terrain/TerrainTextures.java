package com.lumiyaviewer.lumiya.slproto.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Arrays;
import java.util.UUID;

public class TerrainTextures {
    private static UUID[] defaultTerrainTextures = {UUID.fromString("0bc58228-74a0-7e83-89bc-5c23464bcec5"), UUID.fromString("63338ede-0037-c4fd-855b-015d77112fc8"), UUID.fromString("303cd381-8560-7579-23f1-f0a880799740"), UUID.fromString("53a2f406-4895-1d13-d541-d2e3b86bc19c")};
    private final float[] terrainHeightRange;
    private final float[] terrainStartHeight;
    private final UUID[] textureIDs;

    public TerrainTextures() {
        this.terrainStartHeight = new float[4];
        this.terrainHeightRange = new float[4];
        this.textureIDs = new UUID[4];
        System.arraycopy(defaultTerrainTextures, 0, this.textureIDs, 0, 4);
    }

    public TerrainTextures(RegionHandshake.RegionInfo regionInfo) {
        this.terrainStartHeight = new float[4];
        this.terrainHeightRange = new float[4];
        UUID[] uuidArr = {regionInfo.TerrainDetail0, regionInfo.TerrainDetail1, regionInfo.TerrainDetail2, regionInfo.TerrainDetail3};
        for (int i = 0; i < 4; i++) {
            Debug.Printf("Terrain: texture[%d] = %s", Integer.valueOf(i), uuidArr[i].toString());
            if (uuidArr[i] == null || uuidArr[i].equals(UUIDPool.ZeroUUID)) {
                uuidArr[i] = defaultTerrainTextures[i];
            }
        }
        this.textureIDs = uuidArr;
        this.terrainStartHeight[0] = regionInfo.TerrainStartHeight00;
        this.terrainStartHeight[1] = regionInfo.TerrainStartHeight01;
        this.terrainStartHeight[2] = regionInfo.TerrainStartHeight10;
        this.terrainStartHeight[3] = regionInfo.TerrainStartHeight11;
        this.terrainHeightRange[0] = regionInfo.TerrainHeightRange00;
        this.terrainHeightRange[1] = regionInfo.TerrainHeightRange01;
        this.terrainHeightRange[2] = regionInfo.TerrainHeightRange10;
        this.terrainHeightRange[3] = regionInfo.TerrainHeightRange11;
    }

    private static float bilinearCorners(float[] fArr, float f, float f2) {
        return (((fArr[0] * f) + (fArr[1] * (1.0f - f))) * f2) + (((fArr[2] * f) + (fArr[3] * (1.0f - f))) * (1.0f - f2));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TerrainTextures)) {
            return false;
        }
        TerrainTextures terrainTextures = (TerrainTextures) obj;
        if (!Arrays.equals(this.textureIDs, terrainTextures.textureIDs) || !Arrays.equals(this.terrainStartHeight, terrainTextures.terrainStartHeight)) {
            return false;
        }
        return Arrays.equals(this.terrainHeightRange, terrainTextures.terrainHeightRange);
    }

    public int getNeededLayerMask(float[] fArr) {
        int i = 0;
        for (float f : fArr) {
            int floor = (int) Math.floor((double) f);
            i |= 1 << floor;
            if (f - ((float) floor) != 0.0f) {
                i |= 1 << (floor + 1);
            }
        }
        return i & 15;
    }

    public float[] getTextureHeightMap(float[] fArr, int i, int i2, float f, float f2, float f3, float f4) {
        float[] fArr2 = new float[(i * i2)];
        for (int i3 = 0; i3 < i2; i3++) {
            float f5 = ((float) i3) / ((float) (i2 - 1));
            for (int i4 = 0; i4 < i; i4++) {
                float f6 = ((float) i4) / ((float) (i - 1));
                int i5 = (i3 * i) + i4;
                fArr2[i5] = Math.min(3.0f, Math.max(0.0f, ((fArr[(i3 * i) + i4] - bilinearCorners(this.terrainStartHeight, (f6 * f3) + f, (f5 * f4) + f2)) * 4.0f) / bilinearCorners(this.terrainHeightRange, (f6 * f3) + f, (f5 * f4) + f2)));
            }
        }
        return fArr2;
    }

    public UUID getTextureUUID(int i) {
        return this.textureIDs[i];
    }

    public int hashCode() {
        return Arrays.hashCode(this.textureIDs) + Arrays.hashCode(this.terrainStartHeight) + Arrays.hashCode(this.terrainHeightRange);
    }
}
