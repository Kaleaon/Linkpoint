// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Arrays;
import java.util.UUID;

public class TerrainTextures
{

    private static UUID defaultTerrainTextures[] = {
        UUID.fromString("0bc58228-74a0-7e83-89bc-5c23464bcec5"), UUID.fromString("63338ede-0037-c4fd-855b-015d77112fc8"), UUID.fromString("303cd381-8560-7579-23f1-f0a880799740"), UUID.fromString("53a2f406-4895-1d13-d541-d2e3b86bc19c")
    };
    private final float terrainHeightRange[];
    private final float terrainStartHeight[];
    private final UUID textureIDs[];

    public TerrainTextures()
    {
        terrainStartHeight = new float[4];
        terrainHeightRange = new float[4];
        textureIDs = new UUID[4];
        System.arraycopy(defaultTerrainTextures, 0, textureIDs, 0, 4);
    }

    public TerrainTextures(com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake.RegionInfo regioninfo)
    {
        terrainStartHeight = new float[4];
        terrainHeightRange = new float[4];
        UUID auuid[] = new UUID[4];
        auuid[0] = regioninfo.TerrainDetail0;
        auuid[1] = regioninfo.TerrainDetail1;
        auuid[2] = regioninfo.TerrainDetail2;
        auuid[3] = regioninfo.TerrainDetail3;
        for (int i = 0; i < 4; i++)
        {
            Debug.Printf("Terrain: texture[%d] = %s", new Object[] {
                Integer.valueOf(i), auuid[i].toString()
            });
            if (auuid[i] == null || auuid[i].equals(UUIDPool.ZeroUUID))
            {
                auuid[i] = defaultTerrainTextures[i];
            }
        }

        textureIDs = auuid;
        terrainStartHeight[0] = regioninfo.TerrainStartHeight00;
        terrainStartHeight[1] = regioninfo.TerrainStartHeight01;
        terrainStartHeight[2] = regioninfo.TerrainStartHeight10;
        terrainStartHeight[3] = regioninfo.TerrainStartHeight11;
        terrainHeightRange[0] = regioninfo.TerrainHeightRange00;
        terrainHeightRange[1] = regioninfo.TerrainHeightRange01;
        terrainHeightRange[2] = regioninfo.TerrainHeightRange10;
        terrainHeightRange[3] = regioninfo.TerrainHeightRange11;
    }

    private static float bilinearCorners(float af[], float f, float f1)
    {
        return (af[0] * f + af[1] * (1.0F - f)) * f1 + (af[2] * f + af[3] * (1.0F - f)) * (1.0F - f1);
    }

    public boolean equals(Object obj)
    {
        boolean flag1 = false;
        if (!(obj instanceof TerrainTextures))
        {
            return false;
        }
        obj = (TerrainTextures)obj;
        boolean flag = flag1;
        if (Arrays.equals(textureIDs, ((TerrainTextures) (obj)).textureIDs))
        {
            flag = flag1;
            if (Arrays.equals(terrainStartHeight, ((TerrainTextures) (obj)).terrainStartHeight))
            {
                flag = Arrays.equals(terrainHeightRange, ((TerrainTextures) (obj)).terrainHeightRange);
            }
        }
        return flag;
    }

    public int getNeededLayerMask(float af[])
    {
        int i = 0;
        int l = af.length;
        for (int j = 0; j < l; j++)
        {
            float f = af[j];
            int i1 = (int)Math.floor(f);
            int k = i | 1 << i1;
            i = k;
            if (f - (float)i1 != 0.0F)
            {
                i = k | 1 << i1 + 1;
            }
        }

        return i & 0xf;
    }

    public float[] getTextureHeightMap(float af[], int i, int j, float f, float f1, float f2, float f3)
    {
        float af1[] = new float[i * j];
        for (int k = 0; k < j; k++)
        {
            float f4 = (float)k / (float)(j - 1);
            for (int l = 0; l < i; l++)
            {
                float f6 = (float)l / (float)(i - 1);
                float f5 = bilinearCorners(terrainStartHeight, f6 * f2 + f, f4 * f3 + f1);
                f6 = bilinearCorners(terrainHeightRange, f6 * f2 + f, f4 * f3 + f1);
                af1[k * i + l] = Math.min(3F, Math.max(0.0F, ((af[k * i + l] - f5) * 4F) / f6));
            }

        }

        return af1;
    }

    public UUID getTextureUUID(int i)
    {
        return textureIDs[i];
    }

    public int hashCode()
    {
        return Arrays.hashCode(textureIDs) + Arrays.hashCode(terrainStartHeight) + Arrays.hashCode(terrainHeightRange);
    }

}
