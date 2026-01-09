package com.linkpoint.slproto.prims

import kotlin.math.*

import com.linkpoint.slproto.prims.PrimPath
import com.linkpoint.slproto.types.LLVector2
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.Vector2Array
import com.linkpoint.slproto.types.Vector3Array
import com.linkpoint.slproto.types.VertexArray
import java.util.ArrayList

class PrimVolumeFace {
    val BOTTOM_MASK: Int = 1024
    val CAP_MASK: Int = 2
    val END_MASK: Int = 4
    val FLAT_MASK: Int = 256
    val HOLLOW_MASK: Int = 64
    val INNER_MASK: Int = 16
    val OPEN_MASK: Int = 128
    val OUTER_MASK: Int = 32
    val SIDE_MASK: Int = 8
    val SINGLE_MASK: Int = 1
    val TOP_MASK: Int = 512
    Int BeginS
    Int BeginT
    LLVector3 Center
    IntArray Edge
    LLVector3[] Extents = {LLVector3(), LLVector3()}
    Int ID
    ShortArray Indices
    Vector3Array Normals
    Int NumIndices
    Int NumS
    Int NumT
    Int NumVertices
    Vector3Array Positions
    LLVector2[] TexCoordExtents = {LLVector2(), LLVector2()}
    Vector2Array TexCoords
    Int TypeMask
    VertexArray vertexArray

    private Boolean createCap(PrimVolume primVolume) {
        if ((this.TypeMask & 64) == 0 && (this.TypeMask & 128) == 0 && primVolume.volumeParams.PathParams.Begin == 0.0f && primVolume.volumeParams.PathParams.End == 1.0f && primVolume.getProfileType() == 1 && primVolume.getPathType() == 16) {
            return createUnCutCubeCap(primVolume)
        }
        Vector3Array vector3Array = primVolume.Mesh
        ArrayList<LLVector3> arrayList = primVolume.Profile.Profile
        var size: Int = arrayList.size()
        var size2: Int = (arrayList.size() - 2) * 3
        if ((this.TypeMask & 64) == 0 && (this.TypeMask & 128) == 0) {
            resizeVertices(size + 1)
            resizeIndices(size2 + 3)
        } else {
            resizeVertices(size)
            resizeIndices(size2)
        }
        var size3: Int = (this.TypeMask & 512) != 0 ? primVolume.Profile.Total * (primVolume.Path.Path.size() - 1) : this.BeginS
        LLVector2 lLVector2 = LLVector2()
        LLVector2 lLVector22 = LLVector2()
        LLVector3 lLVector3 = this.Extents[0]
        LLVector3 lLVector32 = this.Extents[1]
        Vector2Array vector2Array = this.TexCoords
        Vector3Array vector3Array2 = this.Positions
        Vector3Array vector3Array3 = this.Normals
        for (i2 in 0 until size) {
            if ((this.TypeMask & 512) != 0) {
                vector2Array.set(i2, 0.5f + arrayList.get(i2).x, arrayList.get(i2).y + 0.5f)
            } else {
                vector2Array.set(i2, 0.5f + arrayList.get(i2).x, 0.5f - arrayList.get(i2).y)
            }
            vector3Array2.set(i2, vector3Array, i2 + size3)
            if (i2 == 0) {
                vector3Array2.get(i2, lLVector3)
                vector3Array2.get(i2, lLVector32)
                vector2Array.get(i2, lLVector2)
                vector2Array.get(i2, lLVector22)
            } else {
                vector3Array2.minMaxVector(i2, lLVector3, lLVector32)
                vector2Array.minMaxVector(i2, lLVector2, lLVector22)
            }
        }
        this.Center = LLVector3(lLVector3)
        this.Center.add(lLVector32)
        this.Center.mul(0.5f)
        LLVector2 sum = LLVector2.sum(lLVector2, lLVector22)
        sum.mul(0.5f)
        LLVector3 lLVector33 = LLVector3(this.Center)
        LLVector3 lLVector34 = LLVector3(this.Center)
        vector3Array2.subFromVector(lLVector33, 0)
        vector3Array2.subFromVector(lLVector34, 1)
        LLVector3 cross = (this.TypeMask & 512) != 0 ? LLVector3.cross(lLVector33, lLVector34) : LLVector3.cross(lLVector34, lLVector33)
        cross.normVec()
        if ((this.TypeMask & 64) == 0 && (this.TypeMask & 128) == 0) {
            vector3Array2.set(size, this.Center)
            vector2Array.set(size, sum.x, sum.y)
            i = size + 1
        } else {
            i = size
        }
        vector3Array3.fill(0, i, cross)
        if ((this.TypeMask & 64) == 0) {
            var i3: Int = 2
            var i4: Int = 1
            if ((this.TypeMask & 512) != 0) {
                i3 = 1
                i4 = 2
            }
            for (i5 in 0 until i - 2) {
                this.Indices[i5 * 3] = (Short) (i - 1)
                this.Indices[(i5 * 3) + i3] = (Short) i5
                this.Indices[(i5 * 3) + i4] = (Short) (i5 + 1)
            }
            return true
        } else if ((this.TypeMask & 512) != 0) {
            var i6: Int = 0
            var i7: Int = i - 1
            var i8: Int = 0
            while (true) {
                var i9: Int = i8
                if (i7 - i6 <= 1) {
                    return true
                }
                LLVector3 lLVector35 = LLVector3(arrayList.get(i6))
                LLVector3 lLVector36 = LLVector3(arrayList.get(i7))
                LLVector3 lLVector37 = LLVector3(arrayList.get(i6 + 1))
                LLVector3 lLVector38 = LLVector3(arrayList.get(i7 - 1))
                lLVector35.z = 0.0f
                lLVector36.z = 0.0f
                lLVector37.z = 0.0f
                lLVector38.z = 0.0f
                var f: Float = ((lLVector35.x * lLVector37.y) - (lLVector37.x * lLVector35.y)) + ((lLVector37.x * lLVector36.y) - (lLVector36.x * lLVector37.y)) + ((lLVector36.x * lLVector35.y) - (lLVector35.x * lLVector36.y))
                var f2: Float = ((lLVector35.x * lLVector38.y) - (lLVector38.x * lLVector35.y)) + ((lLVector38.x * lLVector37.y) - (lLVector37.x * lLVector38.y)) + ((lLVector37.x * lLVector35.y) - (lLVector35.x * lLVector37.y))
                var f3: Float = ((lLVector36.x * lLVector35.y) - (lLVector35.x * lLVector36.y)) + ((lLVector35.x * lLVector38.y) - (lLVector38.x * lLVector35.y)) + ((lLVector38.x * lLVector36.y) - (lLVector36.x * lLVector38.y))
                var f4: Float = ((lLVector36.x * lLVector37.y) - (lLVector37.x * lLVector36.y)) + ((lLVector37.x * lLVector38.y) - (lLVector38.x * lLVector37.y)) + ((lLVector38.x * lLVector36.y) - (lLVector36.x * lLVector38.y))
                var z3: Boolean = true
                var z4: Boolean = true
                if (f < 0.0f) {
                    z3 = false
                }
                if (f4 < 0.0f) {
                    z3 = false
                }
                if (f3 < 0.0f) {
                    z4 = false
                }
                if (f2 < 0.0f) {
                    z4 = false
                }
                if (!z3) {
                    z2 = false
                } else if (!z4) {
                    z2 = true
                } else {
                    z2 = LLVector3.sub(lLVector35, lLVector37).magVecSquared() < LLVector3.sub(lLVector36, lLVector38).magVecSquared()
                }
                if (z2) {
                    var i10: Int = i9 + 1
                    this.Indices[i9] = (Short) i6
                    var i11: Int = i10 + 1
                    this.Indices[i10] = (Short) (i6 + 1)
                    i8 = i11 + 1
                    this.Indices[i11] = (Short) i7
                    i6++
                } else {
                    var i12: Int = i9 + 1
                    this.Indices[i9] = (Short) i6
                    var i13: Int = i12 + 1
                    this.Indices[i12] = (Short) (i7 - 1)
                    i8 = i13 + 1
                    this.Indices[i13] = (Short) i7
                    i7--
                }
            }
        } else {
            var i14: Int = 0
            var i15: Int = i - 1
            var i16: Int = 0
            while (true) {
                var i17: Int = i16
                if (i15 - i14 <= 1) {
                    return true
                }
                LLVector3 lLVector39 = LLVector3(arrayList.get(i14))
                LLVector3 lLVector310 = LLVector3(arrayList.get(i15))
                LLVector3 lLVector311 = LLVector3(arrayList.get(i14 + 1))
                LLVector3 lLVector312 = LLVector3(arrayList.get(i15 - 1))
                lLVector39.z = 0.0f
                lLVector310.z = 0.0f
                lLVector311.z = 0.0f
                lLVector312.z = 0.0f
                var f5: Float = ((lLVector39.x * lLVector311.y) - (lLVector311.x * lLVector39.y)) + ((lLVector311.x * lLVector310.y) - (lLVector310.x * lLVector311.y)) + ((lLVector310.x * lLVector39.y) - (lLVector39.x * lLVector310.y))
                var f6: Float = ((lLVector39.x * lLVector312.y) - (lLVector312.x * lLVector39.y)) + ((lLVector312.x * lLVector311.y) - (lLVector311.x * lLVector312.y)) + ((lLVector311.x * lLVector39.y) - (lLVector39.x * lLVector311.y))
                var f7: Float = ((lLVector310.x * lLVector39.y) - (lLVector39.x * lLVector310.y)) + ((lLVector39.x * lLVector312.y) - (lLVector312.x * lLVector39.y)) + ((lLVector312.x * lLVector310.y) - (lLVector310.x * lLVector312.y))
                var f8: Float = ((lLVector310.x * lLVector311.y) - (lLVector311.x * lLVector310.y)) + ((lLVector311.x * lLVector312.y) - (lLVector312.x * lLVector311.y)) + ((lLVector312.x * lLVector310.y) - (lLVector310.x * lLVector312.y))
                var z5: Boolean = true
                var z6: Boolean = true
                if (f5 < 0.0f) {
                    z5 = false
                }
                if (f8 < 0.0f) {
                    z5 = false
                }
                if (f7 < 0.0f) {
                    z6 = false
                }
                if (f6 < 0.0f) {
                    z6 = false
                }
                if (!z5) {
                    z = false
                } else if (!z6) {
                    z = true
                } else {
                    z = LLVector3.sub(lLVector39, lLVector311).magVecSquared() < LLVector3.sub(lLVector310, lLVector312).magVecSquared()
                }
                if (z) {
                    var i18: Int = i17 + 1
                    this.Indices[i17] = (Short) i14
                    var i19: Int = i18 + 1
                    this.Indices[i18] = (Short) i15
                    i16 = i19 + 1
                    this.Indices[i19] = (Short) (i14 + 1)
                    i14++
                } else {
                    var i20: Int = i17 + 1
                    this.Indices[i17] = (Short) i14
                    var i21: Int = i20 + 1
                    this.Indices[i20] = (Short) i15
                    i16 = i21 + 1
                    this.Indices[i21] = (Short) (i15 - 1)
                    i15--
                }
            }
        }
    }

    private Boolean createSide(PrimVolume primVolume) {
        var z: Boolean = (this.TypeMask & 256) != 0
        Byte b = primVolume.volumeParams.SculptType
        Byte b2 = (Byte) (b & 7)
        var z2: Boolean = (b & 64) != 0
        var z3: Boolean = (b & Byte.MIN_VALUE) != 0
        var z4: Boolean = z2 ? !z3 : z3
        Vector3Array vector3Array = primVolume.Mesh
        ArrayList<LLVector3> arrayList = primVolume.Profile.Profile
        ArrayList<PrimPath.PathPoint> arrayList2 = primVolume.Path.Path
        var i6: Int = primVolume.Profile.Total
        var i7: Int = (this.NumS - 1) * (this.NumT - 1) * 6
        resizeVertices(this.NumS * this.NumT)
        resizeIndices(i7)
        this.Edge = Int[i7]
        Vector3Array vector3Array2 = this.Positions
        Vector3Array vector3Array3 = this.Normals
        Vector2Array vector2Array = this.TexCoords
        var floor: Int = Math.toInt().floor(arrayList.toDouble().get(this.BeginS).z)
        var i8: Int = ((this.TypeMask & 16) == 0 || (this.TypeMask & 256) == 0 || this.NumS <= 2) ? this.NumS : this.NumS / 2
        var i9: Int = this.BeginT
        var i10: Int = 0
        while (true) {
            var i11: Int = i9
            if (i11 >= this.BeginT + this.NumT) {
                break
            }
            var f: Float = arrayList2.get(i11).TexT
            var i12: Int = 0
            var i13: Int = i10
            while (i12 < i8) {
                var f2: Float = ((this.TypeMask & 4) != 0 || this.BeginS + i12 >= arrayList.size()) ? i12 != 0 ? 1.0f : 0.0f : !z ? arrayList.get(this.BeginS + i12).z : arrayList.get(this.BeginS + i12).z - (floor.toFloat())
                if (z4) {
                    f2 = 1.0f - f2
                }
                var i14: Int = this.BeginS + i12 >= i6 ? this.BeginS + i12 + ((i11 - 1) * i6) : this.BeginS + i12 + (i6 * i11)
                vector3Array2.set(i13, vector3Array, i14)
                vector2Array.set(i13, f2, f)
                var i15: Int = i13 + 1
                if ((this.TypeMask & 16) == 0 || (this.TypeMask & 256) == 0 || this.NumS <= 2 || i12 <= 0) {
                    i5 = i15
                } else {
                    vector3Array2.set(i15, vector3Array, i14)
                    vector2Array.set(i15, f2, f)
                    i5 = i15 + 1
                }
                i12++
                i13 = i5
            }
            if ((this.TypeMask & 16) == 0 || (this.TypeMask & 256) == 0 || this.NumS <= 2) {
                i10 = i13
            } else {
                var i16: Int = (this.TypeMask & 128) != 0 ? i8 - 1 : 0
                var i17: Int = this.BeginS + i16 + (i6 * i11)
                var f3: Float = this.BeginS + i16 < arrayList.size() ? arrayList.get(i16 + this.BeginS).z - (floor.toFloat()) : i16 != 0 ? 1.0f : 0.0f
                vector3Array2.set(i13, vector3Array, i17)
                vector2Array.set(i13, f3, f)
                i10 = i13 + 1
            }
            i9 = i11 + 1
        }
        LLVector3 lLVector3 = this.Extents[0]
        LLVector3 lLVector32 = this.Extents[1]
        vector3Array2.get(0, lLVector3)
        vector3Array2.get(0, lLVector32)
        vector3Array2.minMaxVector(lLVector3, lLVector32)
        this.Center = LLVector3(lLVector3)
        this.Center.add(lLVector32)
        this.Center.mul(0.5f)
        var i18: Int = 0
        var i19: Int = 0
        var z5: Boolean = (this.TypeMask & 256) != 0
        for (i20 in 0 until this.NumT - 1) {
            for (i21 in 0 until this.NumS - 1) {
                var i22: Int = i18 + 1
                this.Indices[i18] = (Short) ((this.NumS * i20) + i21)
                var i23: Int = i22 + 1
                this.Indices[i22] = (Short) (i21 + 1 + (this.NumS * (i20 + 1)))
                var i24: Int = i23 + 1
                this.Indices[i23] = (Short) ((this.NumS * (i20 + 1)) + i21)
                var i25: Int = i24 + 1
                this.Indices[i24] = (Short) ((this.NumS * i20) + i21)
                var i26: Int = i25 + 1
                this.Indices[i25] = (Short) (i21 + 1 + (this.NumS * i20))
                i18 = i26 + 1
                this.Indices[i26] = (Short) (i21 + 1 + (this.NumS * (i20 + 1)))
                var i27: Int = i19 + 1
                this.Edge[i19] = ((this.NumS - 1) * 2 * i20) + (i21 * 2) + 1
                if (i20 < this.NumT - 2) {
                    this.Edge[i27] = ((this.NumS - 1) * 2 * (i20 + 1)) + (i21 * 2) + 1
                    i = i27 + 1
                } else if (this.NumT <= 3 || primVolume.Path.Open) {
                    this.Edge[i27] = -1
                    i = i27 + 1
                } else {
                    this.Edge[i27] = (i21 * 2) + 1
                    i = i27 + 1
                }
                if (i21 > 0) {
                    this.Edge[i] = ((((this.NumS - 1) * 2) * i20) + (i21 * 2)) - 1
                    i2 = i + 1
                } else if (z5 || primVolume.Path.Open) {
                    this.Edge[i] = -1
                    i2 = i + 1
                } else {
                    this.Edge[i] = ((this.NumS - 1) * 2 * i20) + ((this.NumS - 2) * 2) + 1
                    i2 = i + 1
                }
                if (i20 > 0) {
                    i3 = i2 + 1
                    this.Edge[i2] = ((this.NumS - 1) * 2 * (i20 - 1)) + (i21 * 2)
                } else if (this.NumT <= 3 || primVolume.Path.Open) {
                    i3 = i2 + 1
                    this.Edge[i2] = -1
                } else {
                    i3 = i2 + 1
                    this.Edge[i2] = ((this.NumS - 1) * 2 * (this.NumT - 2)) + (i21 * 2)
                }
                if (i21 < this.NumS - 2) {
                    i4 = i3 + 1
                    this.Edge[i3] = ((this.NumS - 1) * 2 * i20) + ((i21 + 1) * 2)
                } else if (z5 || primVolume.Path.Open) {
                    i4 = i3 + 1
                    this.Edge[i3] = -1
                } else {
                    i4 = i3 + 1
                    this.Edge[i3] = (this.NumS - 1) * 2 * i20
                }
                i19 = i4 + 1
                this.Edge[i4] = ((this.NumS - 1) * 2 * i20) + (i21 * 2)
            }
        }
        this.Normals.clear()
        LLVector3[] lLVector3Arr = LLVector3[3]
        ShortArray sArr = ShortArray(3)
        for (i28 in 0 until 3) {
            lLVector3Arr[i28] = LLVector3()
        }
        LLVector3 lLVector33 = LLVector3()
        LLVector3 lLVector34 = LLVector3()
        for (i29 in 0 until this.NumIndices / 3) {
            for (i30 in 0 until 3) {
                sArr[i30] = this.Indices[(i29 * 3) + i30]
                vector3Array2.get(sArr[i30], lLVector3Arr[i30])
            }
            lLVector33.setSub(lLVector3Arr[0], lLVector3Arr[1])
            lLVector34.setSub(lLVector3Arr[0], lLVector3Arr[2])
            lLVector33.setCross(lLVector34)
            for (i31 in 0 until 3) {
                this.Normals.add(sArr[i31], lLVector33)
            }
            this.Normals.add(sArr[(i29 & 1) + 1], lLVector33)
        }
        LLVector3 lLVector35 = LLVector3()
        LLVector3 lLVector36 = LLVector3()
        LLVector3 lLVector37 = LLVector3()
        vector3Array2.get(0, lLVector36)
        vector3Array2.get(this.NumS * (this.NumT - 2), lLVector37)
        lLVector35.setSub(lLVector36, lLVector37)
        var z6: Boolean = lLVector35.dot(lLVector35) < 1.0E-6f
        vector3Array2.get(this.NumS - 1, lLVector36)
        vector3Array2.get(((this.NumS * (this.NumT - 2)) + this.NumS) - 1, lLVector37)
        lLVector35.setSub(lLVector36, lLVector37)
        var z7: Boolean = lLVector35.dot(lLVector35) < 1.0E-6f
        if (b2 == 0) {
            if (!primVolume.Path.Open) {
                for (i32 in 0 until this.NumS) {
                    vector3Array3.setAdd(i32, (this.NumS * (this.NumT - 1)) + i32)
                }
            }
            if (!primVolume.Path.Open && (!z6)) {
                for (i33 in 0 until this.NumT) {
                    vector3Array3.setAdd(this.NumS * i33, ((this.NumS * i33) + this.NumS) - 1)
                }
            }
            if (primVolume.getPathType() != 32 || (primVolume.getProfileType() & 15) != 5) {
                return true
            }
            if (z6) {
                for (i34 in 0 until this.NumT) {
                    vector3Array3.set(this.NumS * i34, 1.0f, 0.0f, 0.0f)
                }
            }
            if (!z7) {
                return true
            }
            for (i35 in 0 until this.NumT) {
                vector3Array3.set(((this.NumS * i35) + this.NumS) - 1, -1.0f, 0.0f, 0.0f)
            }
            return true
        }
        var z8: Boolean = b2 == 1
        var z9: Boolean = b2 == 1 || b2 == 2 || b2 == 4
        var z10: Boolean = b2 == 2
        if (z8) {
            LLVector3 lLVector38 = LLVector3()
            for (i36 in 0 until this.NumS) {
                vector3Array3.addToVector(i36, lLVector38)
            }
            for (i37 in 0 until this.NumS) {
                vector3Array3.set(i37, lLVector38)
            }
            lLVector38.set(0.0f, 0.0f, 0.0f)
            for (i38 in 0 until this.NumS) {
                vector3Array3.addToVector((this.NumS * (this.NumT - 1)) + i38, lLVector38)
            }
            for (i39 in 0 until this.NumS) {
                vector3Array3.set((this.NumS * (this.NumT - 1)) + i39, lLVector38)
            }
        }
        if (z9) {
            for (i40 in 0 until this.NumT) {
                vector3Array3.setAdd(this.NumS * i40, ((this.NumS * i40) + this.NumS) - 1)
            }
        }
        if (!z10) {
            return true
        }
        for (i41 in 0 until this.NumS) {
            vector3Array3.setAdd(i41, (this.NumS * (this.NumT - 1)) + i41)
        }
        return true
    }

    private Boolean createUnCutCubeCap(PrimVolume primVolume) {
        Vector3Array vector3Array = primVolume.Mesh
        ArrayList<LLVector3> arrayList = primVolume.Profile.Profile
        var i2: Int = primVolume.Profile.Total
        var size: Int = primVolume.Path.Path.size()
        var size2: Int = (arrayList.size() - 1) / 4
        LLVector3 lLVector3 = this.Extents[0]
        LLVector3 lLVector32 = this.Extents[1]
        var i3: Int = (this.TypeMask & 512) != 0 ? i2 * (size - 1) : this.BeginS
        VertexArray vertexArray2 = VertexArray(4)
        LLVector3 lLVector33 = LLVector3()
        Vector3Array vertices = vertexArray2.getVertices()
        Vector2Array texCoords = vertexArray2.getTexCoords()
        var i4: Int = 0
        while (true) {
            var i5: Int = i4
            if (i5 >= 4) {
                break
            }
            vertices.set(i5, vector3Array, (size2 * i5) + i3)
            texCoords.set(i5, 0.5f + arrayList.get(size2 * i5).x, 0.5f - arrayList.get(size2 * i5).y)
            i4 = i5 + 1
        }
        LLVector3 lLVector34 = LLVector3()
        vertices.getSub(1, 0, lLVector33)
        vertices.getSub(2, 1, lLVector34)
        lLVector33.setCross(lLVector34)
        lLVector33.normVec()
        if ((this.TypeMask & 512) == 0) {
            lLVector33.mul(-1.0f)
        } else {
            texCoords.swap(0, 3)
            texCoords.swap(1, 2)
        }
        resizeVertices((size2 + 1) * (size2 + 1))
        Vector3Array vector3Array2 = this.Positions
        var i6: Int = 0
        LLVector3 lLVector35 = LLVector3()
        LLVector3 lLVector36 = LLVector3()
        LLVector2 lLVector2 = LLVector2()
        LLVector2 lLVector22 = LLVector2()
        var i7: Int = 0
        while (true) {
            var i8: Int = i7
            if (i8 >= size2 + 1) {
                break
            }
            var i9: Int = 0
            while (true) {
                var i10: Int = i9
                if (i10 >= size2 + 1) {
                    break
                }
                this.vertexArray.LerpPlanarVertex(i6, vertexArray2, 0, vertexArray2, 1, vertexArray2, 3, (i8.toFloat()) / (size2.toFloat()), (i10.toFloat()) / (size2.toFloat()), lLVector35, lLVector36, lLVector2, lLVector22)
                this.vertexArray.getNormals().set(i6, lLVector33)
                if (i8 == 0 && i10 == 0) {
                    vector3Array2.get(i6, lLVector3)
                    vector3Array2.get(i6, lLVector32)
                } else {
                    vector3Array2.minMaxVector(i6, lLVector3, lLVector32)
                }
                i6++
                i9 = i10 + 1
            }
            i7 = i8 + 1
        }
        this.Center = LLVector3(lLVector3)
        this.Center.add(lLVector32)
        this.Center.mul(0.5f)
        resizeIndices(size2 * size2 * 6)
        ShortArray sArr = this.Indices
        ShortArray sArr2 = {0, 1, (Short) (size2 + 1 + 1), (Short) (size2 + 1 + 1), (Short) (size2 + 1), 0}
        var i11: Int = 0
        var i12: Int = 0
        while (true) {
            var i13: Int = i12
            if (i13 >= size2) {
                return true
            }
            var i14: Int = 0
            while (i14 < size2) {
                if ((this.TypeMask & 512) != 0) {
                    i = i11
                    var i15: Int = 5
                    while (i15 >= 0) {
                        sArr[i] = (Short) (((size2 + 1) * i14) + i13 + sArr2[i15])
                        i15--
                        i++
                    }
                } else {
                    var i16: Int = i11
                    var i17: Int = 0
                    while (i17 < 6) {
                        sArr[i] = (Short) (((size2 + 1) * i14) + i13 + sArr2[i17])
                        i17++
                        i16 = i + 1
                    }
                }
                i14++
                i11 = i
            }
            i12 = i13 + 1
        }
    }

    private Unit resizeIndices(Int i) {
        if (i != this.NumIndices) {
            if (i != 0) {
                this.Indices = Short[i]
            } else {
                this.Indices = null
            }
            this.NumIndices = i
        }
    }

    private Unit resizeVertices(Int i) {
        if (this.NumVertices != i) {
            if (i != 0) {
                this.vertexArray = VertexArray(i)
                this.Positions = this.vertexArray.getVertices()
                this.Normals = this.vertexArray.getNormals()
                this.TexCoords = this.vertexArray.getTexCoords()
            } else {
                this.Positions = null
                this.Normals = null
                this.TexCoords = null
                this.vertexArray = null
            }
            this.NumVertices = i
        }
    }

    fun create(PrimVolume primVolume): Boolean {
        var createCap: Boolean = (this.TypeMask & 2) != 0 ? createCap(primVolume) : ((this.TypeMask & 4) == 0 && (this.TypeMask & 8) == 0) ? false : createSide(primVolume)
        if (createCap) {
            this.TexCoordExtents[0] = LLVector2(1.0f, 1.0f)
            this.TexCoordExtents[1] = LLVector2(0.0f, 0.0f)
            this.TexCoords.minMaxVector(this.TexCoordExtents[0], this.TexCoordExtents[1])
            this.TexCoordExtents[0].x = max(0.0f, this.TexCoordExtents[0].x)
            this.TexCoordExtents[0].y = max(0.0f, this.TexCoordExtents[0].y)
            this.TexCoordExtents[1].x = min(1.0f, this.TexCoordExtents[1].x)
            this.TexCoordExtents[1].y = min(1.0f, this.TexCoordExtents[1].y)
        }
        return createCap
    }
}
