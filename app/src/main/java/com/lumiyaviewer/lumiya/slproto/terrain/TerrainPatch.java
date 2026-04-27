package com.lumiyaviewer.lumiya.slproto.terrain;
import java.util.*;

import com.lumiyaviewer.lumiya.utils.BitBuffer;

public class TerrainPatch {
    private static int[] CopyMatrix16 = new int[256];
    private static int[] CopyMatrix32 = new int[256];
    private static float[] CosineTable16 = new float[256];
    private static float[] DequantizeTable16 = new float[256];
    private static float[] DequantizeTable32 = new float[256];
    public static final int END_OF_PATCHES = 97;
    private static float OO_SQRT2 = 0.70710677f;
    private static float[] QuantizeTable16 = new float[256];
    float DCOffset;
    int PatchIDs;
    int QuantWBits;
    int Range;
    int WordBits;
    public float[] heightMap;
    int[] patches;

    static {
        BuildDequantizeTable16();
        SetupCosines16();
        BuildCopyMatrix16();
        BuildQuantizeTable16();
    }

    private static void BuildCopyMatrix16() {
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        boolean z = true;
        boolean z2 = false;
        while (i3 < 16 && i2 < 16) {
            int i4 = i + 1;
            CopyMatrix16[(i2 * 16) + i3] = i;
            if (!z2) {
                if (z) {
                    if (i3 < 15) {
                        i3++;
                    } else {
                        i2++;
                    }
                    z = false;
                    z2 = true;
                } else {
                    if (i2 < 15) {
                        i2++;
                    } else {
                        i3++;
                    }
                    z = true;
                    z2 = true;
                }
            } else if (z) {
                i3++;
                i2--;
                if (i3 == 15 || i2 == 0) {
                    z2 = false;
                }
            } else {
                i3--;
                i2++;
                if (i2 == 15 || i3 == 0) {
                    z2 = false;
                }
            }
            i = i4;
        }
    }

    private static void BuildDequantizeTable16() {
        for (int i = 0; i < 16; i++) {
            for (int i2 = 0; i2 < 16; i2++) {
                DequantizeTable16[(i * 16) + i2] = (((float) (i2 + i)) * 2.0f) + 1.0f;
            }
        }
    }

    private static void BuildQuantizeTable16() {
        for (int i = 0; i < 16; i++) {
            for (int i2 = 0; i2 < 16; i2++) {
                QuantizeTable16[(i * 16) + i2] = 1.0f / (((((float) i2) + ((float) i)) * 2.0f) + 1.0f);
            }
        }
    }

    public static TerrainPatch DecompressPatch(BitBuffer bitBuffer, int i) {
        int bits = bitBuffer.getBits(8);
        if (bits == 97) {
            return null;
        }
        TerrainPatch terrainPatch = new TerrainPatch();
        terrainPatch.QuantWBits = bits;
        terrainPatch.DCOffset = bitBuffer.getFloat();
        terrainPatch.Range = bitBuffer.getBits(16);
        terrainPatch.PatchIDs = bitBuffer.getBits(10);
        terrainPatch.WordBits = (bits & 15) + 2;
        terrainPatch.patches = new int[(i * i)];
        int i2 = 0;
        while (true) {
            if (i2 >= i * i) {
                break;
            }
            if (bitBuffer.getBits(1) == 0) {
                terrainPatch.patches[i2] = 0;
            } else if (bitBuffer.getBits(1) == 0) {
                while (i2 < i * i) {
                    terrainPatch.patches[i2] = 0;
                    i2++;
                }
            } else if (bitBuffer.getBits(1) != 0) {
                terrainPatch.patches[i2] = bitBuffer.getBits(terrainPatch.WordBits) * -1;
            } else {
                terrainPatch.patches[i2] = bitBuffer.getBits(terrainPatch.WordBits);
            }
            i2++;
        }
        float[] fArr = new float[(i * i)];
        float[] fArr2 = new float[(i * i)];
        int i3 = (terrainPatch.QuantWBits >> 4) + 2;
        float f = (1.0f / ((float) (1 << i3))) * ((float) terrainPatch.Range);
        float f2 = terrainPatch.DCOffset + (((float) (1 << (i3 - 1))) * f);
        if (i == 16) {
            for (int i4 = 0; i4 < 256; i4++) {
                fArr[i4] = ((float) terrainPatch.patches[CopyMatrix16[i4]]) * DequantizeTable16[i4];
            }
            float[] fArr3 = new float[256];
            for (int i5 = 0; i5 < 16; i5++) {
                IDCTColumn16(fArr, fArr3, i5);
            }
            for (int i6 = 0; i6 < 16; i6++) {
                IDCTLine16(fArr3, fArr, i6);
            }
        } else {
            for (int i7 = 0; i7 < 1024; i7++) {
                fArr[i7] = ((float) terrainPatch.patches[CopyMatrix32[i7]]) * DequantizeTable32[i7];
            }
        }
        for (int i8 = 0; i8 < fArr.length; i8++) {
            fArr2[i8] = (fArr[i8] * f) + f2;
        }
        terrainPatch.heightMap = fArr2;
        return terrainPatch;
    }

    private static void IDCTColumn16(float[] fArr, float[] fArr2, int i) {
        for (int i2 = 0; i2 < 16; i2++) {
            float f = fArr[i] * OO_SQRT2;
            for (int i3 = 1; i3 < 16; i3++) {
                int i4 = i3 * 16;
                f += CosineTable16[i4 + i2] * fArr[i4 + i];
            }
            fArr2[(i2 * 16) + i] = f;
        }
    }

    private static void IDCTLine16(float[] fArr, float[] fArr2, int i) {
        int i2 = i * 16;
        for (int i3 = 0; i3 < 16; i3++) {
            float f = fArr[i2] * OO_SQRT2;
            for (int i4 = 1; i4 < 16; i4++) {
                f += fArr[i2 + i4] * CosineTable16[(i4 * 16) + i3];
            }
            fArr2[i2 + i3] = f * 0.125f;
        }
    }

    private static void SetupCosines16() {
        for (int i = 0; i < 16; i++) {
            for (int i2 = 0; i2 < 16; i2++) {
                CosineTable16[(i * 16) + i2] = (float) Math.cos((double) (((((float) i2) * 2.0f) + 1.0f) * ((float) i) * 0.09817477f));
            }
        }
    }

    public int getX() {
        return this.PatchIDs >> 5;
    }

    public int getY() {
        return this.PatchIDs & 31;
    }
}
