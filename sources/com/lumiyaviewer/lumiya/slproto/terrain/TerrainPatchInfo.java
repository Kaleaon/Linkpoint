// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.terrain;

import java.util.Arrays;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.terrain:
//            TerrainPatchHeightMap, TerrainTextures

public class TerrainPatchInfo
{

    private final int hashCode = getHashCode();
    private final TerrainPatchHeightMap heightMap;
    private final int layerMask;
    private final float textureHeightMap[];
    private final TerrainTextures textures;

    public TerrainPatchInfo(TerrainPatchHeightMap terrainpatchheightmap, TerrainTextures terraintextures, float f, float f1, float f2, float f3)
    {
        heightMap = terrainpatchheightmap;
        textures = terraintextures;
        textureHeightMap = terraintextures.getTextureHeightMap(terrainpatchheightmap.getHeightArray(), terrainpatchheightmap.getMapWidth(), terrainpatchheightmap.getMapHeight(), f, f1, f2, f3);
        layerMask = terraintextures.getNeededLayerMask(textureHeightMap);
    }

    private int getHashCode()
    {
        return heightMap.hashCode() + textures.hashCode() + layerMask + Arrays.hashCode(textureHeightMap);
    }

    public boolean equals(Object obj)
    {
        boolean flag1 = false;
        if (!(obj instanceof TerrainPatchInfo))
        {
            return false;
        }
        obj = (TerrainPatchInfo)obj;
        boolean flag = flag1;
        if (heightMap.equals(((TerrainPatchInfo) (obj)).heightMap))
        {
            flag = flag1;
            if (textures.equals(((TerrainPatchInfo) (obj)).textures))
            {
                flag = flag1;
                if (layerMask == ((TerrainPatchInfo) (obj)).layerMask)
                {
                    flag = Arrays.equals(textureHeightMap, ((TerrainPatchInfo) (obj)).textureHeightMap);
                }
            }
        }
        return flag;
    }

    public TerrainPatchHeightMap getHeightMap()
    {
        return heightMap;
    }

    public final int getLayerMask()
    {
        return layerMask;
    }

    public float getMaxHeight()
    {
        return heightMap.getMaxHeight();
    }

    public float getMinHeight()
    {
        return heightMap.getMinHeight();
    }

    public final float[] getTextureHeightMap()
    {
        return textureHeightMap;
    }

    public TerrainTextures getTextures()
    {
        return textures;
    }

    public int hashCode()
    {
        return hashCode;
    }
}
