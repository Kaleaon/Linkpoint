// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.terrain;

import com.lumiyaviewer.lumiya.utils.BitBuffer;

public class TerrainPatch
{

    private static int CopyMatrix16[] = new int[256];
    private static int CopyMatrix32[] = new int[256];
    private static float CosineTable16[] = new float[256];
    private static float DequantizeTable16[] = new float[256];
    private static float DequantizeTable32[] = new float[256];
    public static final int END_OF_PATCHES = 97;
    private static float OO_SQRT2 = 0.7071068F;
    private static float QuantizeTable16[] = new float[256];
    float DCOffset;
    int PatchIDs;
    int QuantWBits;
    int Range;
    int WordBits;
    public float heightMap[];
    int patches[];

    public TerrainPatch()
    {
    }

    private static void BuildCopyMatrix16()
    {
        int i;
        int j;
        boolean flag;
        int k;
        boolean flag2;
        k = 0;
        j = 0;
        i = 0;
        flag2 = true;
        flag = false;
_L2:
        boolean flag1;
        if (i >= 16 || j >= 16)
        {
            break MISSING_BLOCK_LABEL_197;
        }
        CopyMatrix16[j * 16 + i] = k;
        if (flag)
        {
            break; /* Loop/switch isn't completed */
        }
        if (flag2)
        {
            if (i < 15)
            {
                i++;
            } else
            {
                j++;
            }
            flag1 = false;
            flag = true;
        } else
        {
            if (j < 15)
            {
                j++;
            } else
            {
                i++;
            }
            flag1 = true;
            flag = true;
        }
_L5:
        k++;
        flag2 = flag1;
        if (true) goto _L2; else goto _L1
_L1:
        int l;
        int i1;
        if (!flag2)
        {
            break MISSING_BLOCK_LABEL_152;
        }
        l = i + 1;
        i1 = j - 1;
        if (l == 15) goto _L4; else goto _L3
_L3:
        j = i1;
        i = l;
        flag1 = flag2;
        if (i1 != 0) goto _L5; else goto _L4
_L4:
        flag = false;
        j = i1;
        i = l;
        flag1 = flag2;
          goto _L5
        l = i - 1;
        i1 = j + 1;
        if (i1 == 15) goto _L7; else goto _L6
_L6:
        j = i1;
        i = l;
        flag1 = flag2;
        if (l != 0) goto _L5; else goto _L7
_L7:
        flag = false;
        j = i1;
        i = l;
        flag1 = flag2;
          goto _L5
    }

    private static void BuildDequantizeTable16()
    {
        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                DequantizeTable16[i * 16 + j] = (float)(j + i) * 2.0F + 1.0F;
            }

        }

    }

    private static void BuildQuantizeTable16()
    {
        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                QuantizeTable16[i * 16 + j] = 1.0F / (((float)j + (float)i) * 2.0F + 1.0F);
            }

        }

    }

    public static TerrainPatch DecompressPatch(BitBuffer bitbuffer, int i)
    {
        TerrainPatch terrainpatch;
        int j;
        boolean flag;
        flag = false;
        j = bitbuffer.getBits(8);
        if (j == 97)
        {
            return null;
        }
        terrainpatch = new TerrainPatch();
        terrainpatch.QuantWBits = j;
        terrainpatch.DCOffset = bitbuffer.getFloat();
        terrainpatch.Range = bitbuffer.getBits(16);
        terrainpatch.PatchIDs = bitbuffer.getBits(10);
        terrainpatch.WordBits = (j & 0xf) + 2;
        terrainpatch.patches = new int[i * i];
        j = 0;
_L2:
        int j1;
        if (j >= i * i)
        {
            break MISSING_BLOCK_LABEL_222;
        }
        if (bitbuffer.getBits(1) == 0)
        {
            break MISSING_BLOCK_LABEL_210;
        }
        j1 = j;
        if (bitbuffer.getBits(1) == 0)
        {
            break; /* Loop/switch isn't completed */
        }
        if (bitbuffer.getBits(1) != 0)
        {
            j1 = bitbuffer.getBits(terrainpatch.WordBits);
            terrainpatch.patches[j] = j1 * -1;
        } else
        {
            j1 = bitbuffer.getBits(terrainpatch.WordBits);
            terrainpatch.patches[j] = j1;
        }
_L3:
        j++;
        if (true) goto _L2; else goto _L1
_L1:
        for (; j1 < i * i; j1++)
        {
            terrainpatch.patches[j1] = 0;
        }

        break MISSING_BLOCK_LABEL_222;
        terrainpatch.patches[j] = 0;
          goto _L3
        bitbuffer = new float[i * i];
        float af[] = new float[i * i];
        int k = (terrainpatch.QuantWBits >> 4) + 2;
        float f = (1.0F / (float)(1 << k)) * (float)terrainpatch.Range;
        float f1 = 1 << k - 1;
        float f2 = terrainpatch.DCOffset;
        if (i == 16)
        {
            for (i = 0; i < 256; i++)
            {
                bitbuffer[i] = (float)terrainpatch.patches[CopyMatrix16[i]] * DequantizeTable16[i];
            }

            float af1[] = new float[256];
            for (i = 0; i < 16; i++)
            {
                IDCTColumn16(bitbuffer, af1, i);
            }

            int l = 0;
            do
            {
                i = ((flag) ? 1 : 0);
                if (l >= 16)
                {
                    break;
                }
                IDCTLine16(af1, bitbuffer, l);
                l++;
            } while (true);
        } else
        {
            int i1 = 0;
            do
            {
                i = ((flag) ? 1 : 0);
                if (i1 >= 1024)
                {
                    break;
                }
                bitbuffer[i1] = (float)terrainpatch.patches[CopyMatrix32[i1]] * DequantizeTable32[i1];
                i1++;
            } while (true);
        }
        for (; i < bitbuffer.length; i++)
        {
            af[i] = bitbuffer[i] * f + (f2 + f1 * f);
        }

        terrainpatch.heightMap = af;
        return terrainpatch;
    }

    private static void IDCTColumn16(float af[], float af1[], int i)
    {
        for (int j = 0; j < 16; j++)
        {
            float f = OO_SQRT2;
            f = af[i] * f;
            for (int k = 1; k < 16; k++)
            {
                int l = k * 16;
                float f1 = af[l + i];
                f += CosineTable16[l + j] * f1;
            }

            af1[j * 16 + i] = f;
        }

    }

    private static void IDCTLine16(float af[], float af1[], int i)
    {
        int k = i * 16;
        for (i = 0; i < 16; i++)
        {
            float f = OO_SQRT2;
            f = af[k] * f;
            for (int j = 1; j < 16; j++)
            {
                f += af[k + j] * CosineTable16[j * 16 + i];
            }

            af1[k + i] = f * 0.125F;
        }

    }

    private static void SetupCosines16()
    {
        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                CosineTable16[i * 16 + j] = (float)Math.cos(((float)j * 2.0F + 1.0F) * (float)i * 0.09817477F);
            }

        }

    }

    public int getX()
    {
        return PatchIDs >> 5;
    }

    public int getY()
    {
        return PatchIDs & 0x1f;
    }

    static 
    {
        BuildDequantizeTable16();
        SetupCosines16();
        BuildCopyMatrix16();
        BuildQuantizeTable16();
    }
}
