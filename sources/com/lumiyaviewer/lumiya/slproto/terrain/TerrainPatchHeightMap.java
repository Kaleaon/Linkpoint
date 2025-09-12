// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.terrain;

import com.google.common.primitives.Floats;
import java.util.Arrays;

public class TerrainPatchHeightMap
{

    private final int hashCode = getHashCode();
    private final float heightMap[];
    private final int mapHeight;
    private final int mapWidth;
    private final float normalMap[];
    private final float waterHeight;

    public TerrainPatchHeightMap(float f, float af[], float af1[], int i, int j)
    {
        waterHeight = f;
        mapWidth = i;
        mapHeight = j;
        heightMap = new float[af.length];
        System.arraycopy(af, 0, heightMap, 0, af.length);
        normalMap = new float[af1.length];
        System.arraycopy(af1, 0, normalMap, 0, af1.length);
    }

    private int getHashCode()
    {
        return Float.floatToIntBits(waterHeight) + 0 + Arrays.hashCode(heightMap) + Arrays.hashCode(normalMap) + mapWidth + mapHeight;
    }

    public boolean equals(Object obj)
    {
        boolean flag1 = false;
        if (!(obj instanceof TerrainPatchHeightMap))
        {
            return false;
        }
        obj = (TerrainPatchHeightMap)obj;
        boolean flag = flag1;
        if (((TerrainPatchHeightMap) (obj)).waterHeight == waterHeight)
        {
            flag = flag1;
            if (((TerrainPatchHeightMap) (obj)).mapWidth == mapWidth)
            {
                flag = flag1;
                if (((TerrainPatchHeightMap) (obj)).mapHeight == mapHeight)
                {
                    flag = flag1;
                    if (Arrays.equals(((TerrainPatchHeightMap) (obj)).heightMap, heightMap))
                    {
                        flag = Arrays.equals(((TerrainPatchHeightMap) (obj)).normalMap, normalMap);
                    }
                }
            }
        }
        return flag;
    }

    public final float[] getHeightArray()
    {
        return heightMap;
    }

    public final int getMapHeight()
    {
        return mapHeight;
    }

    public final int getMapWidth()
    {
        return mapWidth;
    }

    public float getMaxHeight()
    {
        return Floats.max(heightMap);
    }

    public float getMinHeight()
    {
        return Floats.min(heightMap);
    }

    public final float[] getNormalArray()
    {
        return normalMap;
    }

    public final float getWaterHeight()
    {
        return waterHeight;
    }

    public int hashCode()
    {
        return hashCode;
    }
}
