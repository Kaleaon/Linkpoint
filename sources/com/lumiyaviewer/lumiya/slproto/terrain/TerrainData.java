// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.utils.BitBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.terrain:
//            TerrainTextures, TerrainPatch, TerrainPatchInfo, TerrainPatchHeightMap

public class TerrainData
{

    public static final int PatchesPerEdge = 16;
    public static final int PatchesSize = 16;
    public static final int TerrainPerEdge = 256;
    private final float heightMap[] = new float[0x10000];
    private final boolean patchDirtyMap[] = new boolean[256];
    private volatile TerrainTextures terrainTextures;
    private int validCount;
    private final boolean validMap[] = new boolean[0x10000];
    private final float vertexHeights[] = new float[0x10201];
    private final Object vertexLock = new Object();
    private final float vertexNormals[] = new float[0x20402];
    private final boolean vertexValids[] = new boolean[0x10201];
    private float waterHeight;
    private boolean waterHeightValid;

    public TerrainData()
    {
        waterHeight = 0.0F;
        waterHeightValid = false;
        validCount = 0;
        terrainTextures = new TerrainTextures();
    }

    private void SetWaterHeight(float f)
    {
        this;
        JVM INSTR monitorenter ;
        if (waterHeight != f || waterHeightValid ^ true)
        {
            waterHeight = f;
            waterHeightValid = true;
            updateEntireTerrain();
        }
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    private void markVerticesDirty(int i, int j, int k, int l)
    {
        int i1;
        int j1;
        i /= 16;
        j /= 16;
        i1 = k / 16;
        j1 = l / 16;
        Object obj = vertexLock;
        obj;
        JVM INSTR monitorenter ;
        k = j;
_L5:
        if (k > j1)
        {
            break; /* Loop/switch isn't completed */
        }
        l = i;
_L3:
        if (l > i1) goto _L2; else goto _L1
_L1:
        if (l < 0 || l >= 16 || k < 0 || k >= 16)
        {
            continue; /* Loop/switch isn't completed */
        }
        patchDirtyMap[k * 16 + l] = true;
        l++;
          goto _L3
_L2:
        k++;
        if (true) goto _L5; else goto _L4
_L4:
        if (j > j1)
        {
            break; /* Loop/switch isn't completed */
        }
        for (k = i; k <= i1; k++)
        {
            if (k >= 0 && k < 16 && j >= 0 && j < 16)
            {
                SpatialIndex.getInstance().updateTerrainPatch(k, j, this);
            }
        }

        break MISSING_BLOCK_LABEL_162;
        Exception exception;
        exception;
        throw exception;
        j++;
        if (true) goto _L4; else goto _L6
_L6:
    }

    private void updateVerticesInRegion(int i, int j, int k, int l)
    {
        for (int j1 = j; j1 <= l; j1++)
        {
            int k1 = i;
            while (k1 <= k) 
            {
                int l1 = Math.min(Math.max(0, k1 - 1), 255);
                int i2 = Math.min(Math.max(0, j1 - 1), 255);
                int j2 = Math.min(Math.max(0, k1), 255);
                int k2 = Math.min(Math.max(0, j1), 255);
                float f2 = 0.0F;
                int i1 = 0;
                j = i1;
                float f = f2;
                if (l1 >= 0)
                {
                    j = i1;
                    f = f2;
                    if (l1 < 256)
                    {
                        j = i1;
                        f = f2;
                        if (i2 >= 0)
                        {
                            j = i1;
                            f = f2;
                            if (i2 < 256)
                            {
                                j = i1;
                                f = f2;
                                if (validMap[i2 * 256 + l1])
                                {
                                    f = 0.0F + heightMap[i2 * 256 + l1];
                                    j = 1;
                                }
                            }
                        }
                    }
                }
                i1 = j;
                f2 = f;
                if (j2 >= 0)
                {
                    i1 = j;
                    f2 = f;
                    if (j2 < 256)
                    {
                        i1 = j;
                        f2 = f;
                        if (i2 >= 0)
                        {
                            i1 = j;
                            f2 = f;
                            if (i2 < 256)
                            {
                                i1 = j;
                                f2 = f;
                                if (validMap[i2 * 256 + j2])
                                {
                                    f2 = f + heightMap[i2 * 256 + j2];
                                    i1 = j + 1;
                                }
                            }
                        }
                    }
                }
                j = i1;
                f = f2;
                if (l1 >= 0)
                {
                    j = i1;
                    f = f2;
                    if (l1 < 256)
                    {
                        j = i1;
                        f = f2;
                        if (k2 >= 0)
                        {
                            j = i1;
                            f = f2;
                            if (k2 < 256)
                            {
                                j = i1;
                                f = f2;
                                if (validMap[k2 * 256 + l1])
                                {
                                    f = f2 + heightMap[k2 * 256 + l1];
                                    j = i1 + 1;
                                }
                            }
                        }
                    }
                }
                i1 = j;
                f2 = f;
                if (j2 >= 0)
                {
                    i1 = j;
                    f2 = f;
                    if (j2 < 256)
                    {
                        i1 = j;
                        f2 = f;
                        if (k2 >= 0)
                        {
                            i1 = j;
                            f2 = f;
                            if (k2 < 256)
                            {
                                i1 = j;
                                f2 = f;
                                if (validMap[k2 * 256 + j2])
                                {
                                    f2 = f + heightMap[k2 * 256 + j2];
                                    i1 = j + 1;
                                }
                            }
                        }
                    }
                }
                if (i1 == 4)
                {
                    vertexHeights[j1 * 257 + k1] = f2 / (float)i1;
                    float f1 = heightMap[k2 * 256 + j2];
                    f2 = heightMap[l1 + k2 * 256];
                    float f3 = heightMap[k2 * 256 + j2];
                    float f4 = heightMap[i2 * 256 + j2];
                    vertexNormals[(j1 * 257 + k1) * 2] = f1 - f2;
                    vertexNormals[(j1 * 257 + k1) * 2 + 1] = f3 - f4;
                    vertexValids[j1 * 257 + k1] = true;
                } else
                {
                    vertexValids[j1 * 257 + k1] = false;
                }
                k1++;
            }
        }

    }

    public void ApplyRegionInfo(com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake.RegionInfo regioninfo)
    {
        this;
        JVM INSTR monitorenter ;
        SetWaterHeight(regioninfo.WaterHeight);
        regioninfo = new TerrainTextures(regioninfo);
        if (!regioninfo.equals(terrainTextures))
        {
            terrainTextures = regioninfo;
            updateEntireTerrain();
        }
        this;
        JVM INSTR monitorexit ;
        return;
        regioninfo;
        throw regioninfo;
    }

    public void ProcessLayerData(byte abyte0[])
    {
        BitBuffer bitbuffer;
        int l;
        bitbuffer = new BitBuffer(abyte0);
        int i = bitbuffer.getBits(16);
        l = bitbuffer.getBits(8);
        Debug.Log(String.format("Terrain: ProcessLayerData: stride 0x%x patchSize 0x%x type 0x%x", new Object[] {
            Integer.valueOf(i), Integer.valueOf(l), Integer.valueOf(bitbuffer.getBits(8))
        }));
        abyte0 = ((byte []) (vertexLock));
        abyte0;
        JVM INSTR monitorenter ;
_L2:
        TerrainPatch terrainpatch;
        if (bitbuffer.isEOF())
        {
            break MISSING_BLOCK_LABEL_90;
        }
        terrainpatch = TerrainPatch.DecompressPatch(bitbuffer, l);
        if (terrainpatch != null)
        {
            break MISSING_BLOCK_LABEL_112;
        }
        abyte0;
        JVM INSTR monitorexit ;
        Debug.Printf("Terrain: LayerData received, valid count is now %d", new Object[] {
            Integer.valueOf(validCount)
        });
        return;
        int i1;
        int j1;
        i1 = terrainpatch.getX();
        j1 = terrainpatch.getY();
        if (i1 >= 16 || j1 >= 16) goto _L2; else goto _L1
_L1:
        int j = 0;
          goto _L3
_L8:
        int k;
        int l1;
        if (k >= l)
        {
            break MISSING_BLOCK_LABEL_331;
        }
        l1 = i1 * 16 + k;
        if (l1 < 0 || l1 >= 256) goto _L5; else goto _L4
_L4:
        int k1;
        heightMap[k1 * 256 + l1] = terrainpatch.heightMap[j * l + k];
        if (!validMap[k1 * 256 + l1])
        {
            validCount = validCount + 1;
            validMap[l1 + k1 * 256] = true;
        }
          goto _L5
_L7:
        markVerticesDirty(i1 * 16, j1 * 16, (i1 + 1) * 16 + 1, (j1 + 1) * 16 + 1);
          goto _L2
        Exception exception;
        exception;
        throw exception;
_L3:
        if (j >= l) goto _L7; else goto _L6
_L6:
        k1 = j1 * 16 + j;
        if (k1 < 0 || k1 >= 256)
        {
            break MISSING_BLOCK_LABEL_331;
        }
        k = 0;
          goto _L8
_L5:
        k++;
          goto _L8
        j++;
          goto _L3
    }

    public TerrainPatchInfo getPatchInfo(int i, int j)
    {
        Object obj = vertexLock;
        obj;
        JVM INSTR monitorenter ;
        if (patchDirtyMap[j * 16 + i])
        {
            patchDirtyMap[j * 16 + i] = false;
            updateVerticesInRegion(i * 16, j * 16, (i + 1) * 16, (j + 1) * 16);
        }
        obj;
        JVM INSTR monitorexit ;
        int k;
        boolean flag;
        k = 0;
        flag = true;
_L5:
        if (k >= 17) goto _L2; else goto _L1
_L1:
        int k1 = 0;
_L3:
        boolean flag1 = flag;
        if (k1 < 17)
        {
            if (vertexValids[(j * 16 + k) * 257 + k1 + i * 16])
            {
                break MISSING_BLOCK_LABEL_140;
            }
            flag1 = false;
        }
        k++;
        flag = flag1;
        continue; /* Loop/switch isn't completed */
        Exception exception;
        exception;
        throw exception;
        k1++;
        if (true) goto _L3; else goto _L2
_L2:
        if (flag)
        {
            float af[] = new float[289];
            float af1[] = new float[578];
            for (int l = 0; l < 17; l++)
            {
                int j1 = (j * 16 + l) * 257;
                for (int i1 = 0; i1 < 17; i1++)
                {
                    float f = vertexHeights[j1 + i1 + i * 16];
                    float f2 = vertexNormals[(j1 + i1 + i * 16) * 2];
                    float f4 = vertexNormals[(j1 + i1 + i * 16) * 2 + 1];
                    af[l * 17 + i1] = f;
                    af1[(l * 17 + i1) * 2] = f2;
                    af1[(l * 17 + i1) * 2 + 1] = f4;
                }

            }

            float f1 = (float)i / 16F;
            float f3 = (float)i / 16F;
            return new TerrainPatchInfo(new TerrainPatchHeightMap(waterHeight, af, af1, 17, 17), terrainTextures, f1, f3, 0.0625F, 0.0625F);
        }
        return null;
        if (true) goto _L5; else goto _L4
_L4:
    }

    public final boolean isUnderWater(float f)
    {
        return waterHeightValid && f < waterHeight;
    }

    public void reset()
    {
        boolean flag = false;
        Object obj = vertexLock;
        obj;
        JVM INSTR monitorenter ;
        waterHeightValid = false;
        validCount = 0;
        int i = 0;
_L2:
        if (i >= 0x10000)
        {
            break; /* Loop/switch isn't completed */
        }
        validMap[i] = false;
        i++;
        if (true) goto _L2; else goto _L1
_L6:
        int j;
        j = ((flag) ? 1 : 0);
        if (i >= 0x10201)
        {
            break MISSING_BLOCK_LABEL_66;
        }
        vertexValids[i] = false;
        i++;
        continue; /* Loop/switch isn't completed */
_L4:
        if (j >= 256)
        {
            break; /* Loop/switch isn't completed */
        }
        patchDirtyMap[j] = false;
        j++;
        if (true) goto _L4; else goto _L3
_L3:
        return;
        Exception exception;
        exception;
        throw exception;
_L1:
        i = 0;
        if (true) goto _L6; else goto _L5
_L5:
    }

    public void updateEntireTerrain()
    {
        markVerticesDirty(0, 0, 256, 256);
    }
}
