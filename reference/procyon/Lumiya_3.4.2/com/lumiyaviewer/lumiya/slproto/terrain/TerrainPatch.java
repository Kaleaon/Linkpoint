// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.terrain;

import com.lumiyaviewer.lumiya.utils.BitBuffer;

public class TerrainPatch
{
    private static int[] CopyMatrix16;
    private static int[] CopyMatrix32;
    private static float[] CosineTable16;
    private static float[] DequantizeTable16;
    private static float[] DequantizeTable32;
    public static final int END_OF_PATCHES = 97;
    private static float OO_SQRT2;
    private static float[] QuantizeTable16;
    float DCOffset;
    int PatchIDs;
    int QuantWBits;
    int Range;
    int WordBits;
    public float[] heightMap;
    int[] patches;
    
    static {
        TerrainPatch.DequantizeTable16 = new float[256];
        TerrainPatch.DequantizeTable32 = new float[256];
        TerrainPatch.CosineTable16 = new float[256];
        TerrainPatch.CopyMatrix16 = new int[256];
        TerrainPatch.CopyMatrix32 = new int[256];
        TerrainPatch.QuantizeTable16 = new float[256];
        TerrainPatch.OO_SQRT2 = 0.70710677f;
        BuildDequantizeTable16();
        SetupCosines16();
        BuildCopyMatrix16();
        BuildQuantizeTable16();
    }
    
    private static void BuildCopyMatrix16() {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 1;
        int n5 = 0;
        while (n3 < 16 && n2 < 16) {
            TerrainPatch.CopyMatrix16[n2 * 16 + n3] = n;
            int n6 = 0;
            Label_0058: {
                if (n5 == 0) {
                    if (n4 != 0) {
                        if (n3 < 15) {
                            ++n3;
                        }
                        else {
                            ++n2;
                        }
                        n6 = 0;
                        n5 = 1;
                    }
                    else {
                        if (n2 < 15) {
                            ++n2;
                        }
                        else {
                            ++n3;
                        }
                        n6 = 1;
                        n5 = 1;
                    }
                }
                else if (n4 != 0) {
                    final int n7 = n3 + 1;
                    final int n8 = n2 - 1;
                    if (n7 != 15) {
                        n2 = n8;
                        n3 = n7;
                        n6 = n4;
                        if (n8 != 0) {
                            break Label_0058;
                        }
                    }
                    n5 = 0;
                    n2 = n8;
                    n3 = n7;
                    n6 = n4;
                }
                else {
                    final int n9 = n3 - 1;
                    final int n10 = n2 + 1;
                    if (n10 != 15) {
                        n2 = n10;
                        n3 = n9;
                        n6 = n4;
                        if (n9 != 0) {
                            break Label_0058;
                        }
                    }
                    n5 = 0;
                    n2 = n10;
                    n3 = n9;
                    n6 = n4;
                }
            }
            ++n;
            n4 = n6;
        }
    }
    
    private static void BuildDequantizeTable16() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                TerrainPatch.DequantizeTable16[i * 16 + j] = (j + i) * 2.0f + 1.0f;
            }
        }
    }
    
    private static void BuildQuantizeTable16() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                TerrainPatch.QuantizeTable16[i * 16 + j] = 1.0f / ((j + (float)i) * 2.0f + 1.0f);
            }
        }
    }
    
    public static TerrainPatch DecompressPatch(final BitBuffer bitBuffer, int i) {
        final int n = 0;
        final int bits = bitBuffer.getBits(8);
        if (bits == 97) {
            return null;
        }
        final TerrainPatch terrainPatch = new TerrainPatch();
        terrainPatch.QuantWBits = bits;
        terrainPatch.DCOffset = bitBuffer.getFloat();
        terrainPatch.Range = bitBuffer.getBits(16);
        terrainPatch.PatchIDs = bitBuffer.getBits(10);
        terrainPatch.WordBits = (bits & 0xF) + 2;
        terrainPatch.patches = new int[i * i];
        for (int j = 0; j < i * i; ++j) {
            if (bitBuffer.getBits(1) != 0) {
                int k = j;
                if (bitBuffer.getBits(1) == 0) {
                    while (k < i * i) {
                        terrainPatch.patches[k] = 0;
                        ++k;
                    }
                    break;
                }
                if (bitBuffer.getBits(1) != 0) {
                    terrainPatch.patches[j] = bitBuffer.getBits(terrainPatch.WordBits) * -1;
                }
                else {
                    terrainPatch.patches[j] = bitBuffer.getBits(terrainPatch.WordBits);
                }
            }
            else {
                terrainPatch.patches[j] = 0;
            }
        }
        final float[] array = new float[i * i];
        final float[] heightMap = new float[i * i];
        final int n2 = (terrainPatch.QuantWBits >> 4) + 2;
        final float n3 = 1.0f / (1 << n2) * terrainPatch.Range;
        final float n4 = (float)(1 << n2 - 1);
        final float dcOffset = terrainPatch.DCOffset;
        if (i == 16) {
            for (i = 0; i < 256; ++i) {
                array[i] = terrainPatch.patches[TerrainPatch.CopyMatrix16[i]] * TerrainPatch.DequantizeTable16[i];
            }
            final float[] array2 = new float[256];
            for (i = 0; i < 16; ++i) {
                IDCTColumn16(array, array2, i);
            }
            int n5 = 0;
            while (true) {
                i = n;
                if (n5 >= 16) {
                    break;
                }
                IDCTLine16(array2, array, n5);
                ++n5;
            }
        }
        else {
            int n6 = 0;
            while (true) {
                i = n;
                if (n6 >= 1024) {
                    break;
                }
                array[n6] = terrainPatch.patches[TerrainPatch.CopyMatrix32[n6]] * TerrainPatch.DequantizeTable32[n6];
                ++n6;
            }
        }
        while (i < array.length) {
            heightMap[i] = array[i] * n3 + (dcOffset + n4 * n3);
            ++i;
        }
        terrainPatch.heightMap = heightMap;
        return terrainPatch;
    }
    
    private static void IDCTColumn16(final float[] array, final float[] array2, final int n) {
        for (int i = 0; i < 16; ++i) {
            float n2 = array[n] * TerrainPatch.OO_SQRT2;
            for (int j = 1; j < 16; ++j) {
                final int n3 = j * 16;
                n2 += TerrainPatch.CosineTable16[n3 + i] * array[n3 + n];
            }
            array2[i * 16 + n] = n2;
        }
    }
    
    private static void IDCTLine16(final float[] array, final float[] array2, int i) {
        final int n = i * 16;
        float n2;
        int j;
        for (i = 0; i < 16; ++i) {
            n2 = array[n] * TerrainPatch.OO_SQRT2;
            for (j = 1; j < 16; ++j) {
                n2 += array[n + j] * TerrainPatch.CosineTable16[j * 16 + i];
            }
            array2[n + i] = n2 * 0.125f;
        }
    }
    
    private static void SetupCosines16() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                TerrainPatch.CosineTable16[i * 16 + j] = (float)Math.cos((j * 2.0f + 1.0f) * i * 0.09817477f);
            }
        }
    }
    
    public int getX() {
        return this.PatchIDs >> 5;
    }
    
    public int getY() {
        return this.PatchIDs & 0x1F;
    }
}
