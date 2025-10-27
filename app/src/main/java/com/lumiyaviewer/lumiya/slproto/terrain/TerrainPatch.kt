package com.lumiyaviewer.lumiya.slproto.terrain
import java.util.*

import com.lumiyaviewer.lumiya.utils.BitBuffer

class TerrainPatch {
    private IntArray CopyMatrix16 = IntArray(256)
    private IntArray CopyMatrix32 = IntArray(256)
    private FloatArray CosineTable16 = FloatArray(256)
    private FloatArray DequantizeTable16 = FloatArray(256)
    private FloatArray DequantizeTable32 = FloatArray(256)
    val END_OF_PATCHES: Int = 97
    private val OO_SQRT2: Float = 0.70710677f
    private FloatArray QuantizeTable16 = FloatArray(256)
    Float DCOffset
    Int PatchIDs
    Int QuantWBits
    Int Range
    Int WordBits
    FloatArray heightMap
    IntArray patches

    {
        BuildDequantizeTable16()
        SetupCosines16()
        BuildCopyMatrix16()
        BuildQuantizeTable16()
    }

    private Unit BuildCopyMatrix16() {
        Int i = 0
        Int i2 = 0
        Int i3 = 0
        Boolean z = true
        Boolean z2 = false
        while (i3 < 16 && i2 < 16) {
            Int i4 = i + 1
            CopyMatrix16[(i2 * 16) + i3] = i
            if (!z2) {
                if (z) {
                    if (i3 < 15) {
                        i3++
                    } else {
                        i2++
                    }
                    z = false
                    z2 = true
                } else {
                    if (i2 < 15) {
                        i2++
                    } else {
                        i3++
                    }
                    z = true
                    z2 = true
                }
            } else if (z) {
                i3++
                i2--
                if (i3 == 15 || i2 == 0) {
                    z2 = false
                }
            } else {
                i3--
                i2++
                if (i2 == 15 || i3 == 0) {
                    z2 = false
                }
            }
            i = i4
        }
    }

    private Unit BuildDequantizeTable16() {
        for (Int i = 0; i < 16; i++) {
            for (Int i2 = 0; i2 < 16; i2++) {
                DequantizeTable16[(i * 16) + i2] = (((Float) (i2 + i)) * 2.0f) + 1.0f
            }
        }
    }

    private Unit BuildQuantizeTable16() {
        for (Int i = 0; i < 16; i++) {
            for (Int i2 = 0; i2 < 16; i2++) {
                QuantizeTable16[(i * 16) + i2] = 1.0f / ((((i2.toFloat()) + (i.toFloat())) * 2.0f) + 1.0f)
            }
        }
    }

    TerrainPatch DecompressPatch(BitBuffer bitBuffer, Int i) {
        Int bits = bitBuffer.getBits(8)
        if (bits == 97) {
            return null
        }
        TerrainPatch terrainPatch = TerrainPatch()
        terrainPatch.QuantWBits = bits
        terrainPatch.DCOffset = bitBuffer.getFloat()
        terrainPatch.Range = bitBuffer.getBits(16)
        terrainPatch.PatchIDs = bitBuffer.getBits(10)
        terrainPatch.WordBits = (bits & 15) + 2
        terrainPatch.patches = Int[(i * i)]
        Int i2 = 0
        while (true) {
            if (i2 >= i * i) {
                break
            }
            if (bitBuffer.getBits(1) == 0) {
                terrainPatch.patches[i2] = 0
            } else if (bitBuffer.getBits(1) == 0) {
                while (i2 < i * i) {
                    terrainPatch.patches[i2] = 0
                    i2++
                }
            } else if (bitBuffer.getBits(1) != 0) {
                terrainPatch.patches[i2] = bitBuffer.getBits(terrainPatch.WordBits) * -1
            } else {
                terrainPatch.patches[i2] = bitBuffer.getBits(terrainPatch.WordBits)
            }
            i2++
        }
        FloatArray fArr = Float[(i * i)]
        FloatArray fArr2 = Float[(i * i)]
        Int i3 = (terrainPatch.QuantWBits >> 4) + 2
        Float f = (1.0f / ((Float) (1 << i3))) * (terrainPatch.toFloat().Range)
        Float f2 = terrainPatch.DCOffset + (((Float) (1 << (i3 - 1))) * f)
        if (i == 16) {
            for (Int i4 = 0; i4 < 256; i4++) {
                fArr[i4] = (terrainPatch.toFloat().patches[CopyMatrix16[i4]]) * DequantizeTable16[i4]
            }
            FloatArray fArr3 = FloatArray(256)
            for (Int i5 = 0; i5 < 16; i5++) {
                IDCTColumn16(fArr, fArr3, i5)
            }
            for (Int i6 = 0; i6 < 16; i6++) {
                IDCTLine16(fArr3, fArr, i6)
            }
        } else {
            for (Int i7 = 0; i7 < 1024; i7++) {
                fArr[i7] = (terrainPatch.toFloat().patches[CopyMatrix32[i7]]) * DequantizeTable32[i7]
            }
        }
        for (Int i8 = 0; i8 < fArr.length; i8++) {
            fArr2[i8] = (fArr[i8] * f) + f2
        }
        terrainPatch.heightMap = fArr2
        return terrainPatch
    }

    private Unit IDCTColumn16(FloatArray fArr, FloatArray fArr2, Int i) {
        for (Int i2 = 0; i2 < 16; i2++) {
            Float f = fArr[i] * OO_SQRT2
            for (Int i3 = 1; i3 < 16; i3++) {
                Int i4 = i3 * 16
                f += CosineTable16[i4 + i2] * fArr[i4 + i]
            }
            fArr2[(i2 * 16) + i] = f
        }
    }

    private Unit IDCTLine16(FloatArray fArr, FloatArray fArr2, Int i) {
        Int i2 = i * 16
        for (Int i3 = 0; i3 < 16; i3++) {
            Float f = fArr[i2] * OO_SQRT2
            for (Int i4 = 1; i4 < 16; i4++) {
                f += fArr[i2 + i4] * CosineTable16[(i4 * 16) + i3]
            }
            fArr2[i2 + i3] = f * 0.125f
        }
    }

    private Unit SetupCosines16() {
        for (Int i = 0; i < 16; i++) {
            for (Int i2 = 0; i2 < 16; i2++) {
                CosineTable16[(i * 16) + i2] = Math.toFloat().cos((Double) ((((i2.toFloat()) * 2.0f) + 1.0f) * (i.toFloat()) * 0.09817477f))
            }
        }
    }

    Int getX() {
        return this.PatchIDs >> 5
    }

    Int getY() {
        return this.PatchIDs & 31
    }
}
