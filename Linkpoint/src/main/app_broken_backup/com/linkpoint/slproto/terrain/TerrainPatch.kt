package com.linkpoint.slproto.terrain

import kotlin.math.*

import com.linkpoint.utils.BitBuffer

class TerrainPatch {
    private val CopyMatrix16 = IntArray(256)
    private val CopyMatrix32 = IntArray(256)
    private val CosineTable16 = FloatArray(256)
    private val DequantizeTable16 = FloatArray(256)
    private val DequantizeTable32 = FloatArray(256)
    private val OO_SQRT2: Float = 0.70710677f
    private val QuantizeTable16 = FloatArray(256)
    
    var DCOffset: Float = 0f
    var PatchIDs: Int = 0
    var QuantWBits: Int = 0
    var Range: Int = 0
    var WordBits: Int = 0
    var heightMap: FloatArray? = null
    var patches: IntArray? = null

    companion object {
        const val END_OF_PATCHES: Int = 97
    }

    init {
        BuildDequantizeTable16()
        SetupCosines16()
        BuildCopyMatrix16()
        BuildQuantizeTable16()
    }

    private fun BuildCopyMatrix16() {
        var i = 0
        var i2 = 0
        var i3 = 0
        var z = true
        var z2 = false
        while (i3 < 16 && i2 < 16) {
            val i4 = i + 1
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

    private fun BuildDequantizeTable16() {
        for (i in 0 until 16) {
            for (i2 in 0 until 16) {
                DequantizeTable16[(i * 16) + i2] = ((i2 + i).toFloat() * 2.0f) + 1.0f
            }
        }
    }

    private fun BuildQuantizeTable16() {
        for (i in 0 until 16) {
            for (i2 in 0 until 16) {
                QuantizeTable16[(i * 16) + i2] = 1.0f / (((i2.toFloat() + i.toFloat()) * 2.0f) + 1.0f)
            }
        }
    }

    fun DecompressPatch(bitBuffer: BitBuffer, i: Int): TerrainPatch? {
        val bits = bitBuffer.getBits(8)
        if (bits == 97) {
            return null
        }
        val terrainPatch = TerrainPatch()
        terrainPatch.QuantWBits = bits
        terrainPatch.DCOffset = bitBuffer.getFloat()
        terrainPatch.Range = bitBuffer.getBits(16)
        terrainPatch.PatchIDs = bitBuffer.getBits(10)
        terrainPatch.WordBits = (bits and 15) + 2
        terrainPatch.patches = IntArray(i * i)
        val patchesArray = terrainPatch.patches!!
        var i2 = 0
        while (true) {
            if (i2 >= i * i) {
                break
            }
            if (bitBuffer.getBits(1) == 0) {
                patchesArray[i2] = 0
            } else if (bitBuffer.getBits(1) == 0) {
                while (i2 < i * i) {
                    patchesArray[i2] = 0
                    i2++
                }
            } else if (bitBuffer.getBits(1) != 0) {
                patchesArray[i2] = bitBuffer.getBits(terrainPatch.WordBits) * -1
            } else {
                patchesArray[i2] = bitBuffer.getBits(terrainPatch.WordBits)
            }
            i2++
        }
        val fArr = FloatArray(i * i)
        val fArr2 = FloatArray(i * i)
        val i3 = (terrainPatch.QuantWBits shr 4) + 2
        val f = (1.0f / (1 shl i3).toFloat()) * terrainPatch.Range.toFloat()
        val f2 = terrainPatch.DCOffset + ((1 shl (i3 - 1)).toFloat() * f)
        if (i == 16) {
            for (i4 in 0 until 256) {
                fArr[i4] = patchesArray[CopyMatrix16[i4]].toFloat() * DequantizeTable16[i4]
            }
            val fArr3 = FloatArray(256)
            for (i5 in 0 until 16) {
                IDCTColumn16(fArr, fArr3, i5)
            }
            for (i6 in 0 until 16) {
                IDCTLine16(fArr3, fArr, i6)
            }
        } else {
            for (i7 in 0 until 1024) {
                fArr[i7] = patchesArray[CopyMatrix32[i7]].toFloat() * DequantizeTable32[i7]
            }
        }
        for (i8 in fArr.indices) {
            fArr2[i8] = (fArr[i8] * f) + f2
        }
        terrainPatch.heightMap = fArr2
        return terrainPatch
    }

    private fun IDCTColumn16(fArr: FloatArray, fArr2: FloatArray, i: Int) {
        for (i2 in 0 until 16) {
            var f = fArr[i] * OO_SQRT2
            for (i3 in 1 until 16) {
                val i4 = i3 * 16
                f += CosineTable16[i4 + i2] * fArr[i4 + i]
            }
            fArr2[(i2 * 16) + i] = f
        }
    }

    private fun IDCTLine16(fArr: FloatArray, fArr2: FloatArray, i: Int) {
        val i2 = i * 16
        for (i3 in 0 until 16) {
            var f = fArr[i2] * OO_SQRT2
            for (i4 in 1 until 16) {
                f += fArr[i2 + i4] * CosineTable16[(i4 * 16) + i3]
            }
            fArr2[i2 + i3] = f * 0.125f
        }
    }

    private fun SetupCosines16() {
        for (i in 0 until 16) {
            for (i2 in 0 until 16) {
                CosineTable16[(i * 16) + i2] = cos(((i2.toFloat() * 2.0f + 1.0f) * i.toFloat() * 0.09817477f).toDouble()).toFloat()
            }
        }
    }

    fun getX(): Int {
        return this.PatchIDs shr 5
    }

    fun getY(): Int {
        return this.PatchIDs and 31
    }
}
