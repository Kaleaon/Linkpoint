package com.lumiyaviewer.lumiya.slproto.prims

import com.lumiyaviewer.lumiya.slproto.types.LLVector2
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.ArrayList

class PrimProfile {
    val MIN_DETAIL_FACES: Int = 6
    private val tableScale: FloatArray = {1.0f, 1.0f, 1.0f, 0.5f, 0.707107f, 0.53f, 0.525f, 0.5f}
    Boolean Concave = false
    Boolean Dirty = true
    LLVector3[] EdgeCenters
    LLVector3[] EdgeNormals
    ArrayList<Face> Faces = ArrayList<>()
    LLVector2[] Normals
    Boolean Open = false
    ArrayList<LLVector3> Profile = ArrayList<>()
    Int Total = 2
    Int TotalOut = 0

    class Face {
        val LL_FACE_INNER_SIDE: Short = 4
        val LL_FACE_OUTER_SIDE_0: Short = 32
        val LL_FACE_OUTER_SIDE_1: Short = 64
        val LL_FACE_OUTER_SIDE_2: Short = 128
        val LL_FACE_OUTER_SIDE_3: Short = 256
        val LL_FACE_PATH_BEGIN: Short = 1
        val LL_FACE_PATH_END: Short = 2
        val LL_FACE_PROFILE_BEGIN: Short = 8
        val LL_FACE_PROFILE_END: Short = 16
        Boolean Cap
        Int Count
        Short FaceID
        Boolean Flat
        Int Index
        Float ScaleU
    }

    private Face addCap(Short s) {
        Face face = Face()
        face.Index = 0
        face.Count = this.Total
        face.ScaleU = 1.0f
        face.Cap = true
        face.FaceID = s
        this.Faces.add(face)
        return face
    }

    private Face addFace(Int i, Int i2, Float f, Short s, Boolean z) {
        Face face = Face()
        face.Index = i
        face.Count = i2
        face.ScaleU = f
        face.Flat = z
        face.Cap = false
        face.FaceID = s
        this.Faces.add(face)
        return face
    }

    private Face addHole(PrimProfileParams primProfileParams, Boolean z, Float f, Float f2, Float f3, Float f4, Int i) {
        this.TotalOut = this.Total
        genNGon(primProfileParams, Math.toInt().floor(f.toDouble()), f2, -1.0f, f4, i)
        Face addFace = addFace(this.TotalOut, this.Total - this.TotalOut, 0.0f, 4, z)
        LLVector3[] lLVector3Arr = LLVector3[this.Total]
        Int i2 = this.TotalOut
        while (true) {
            Int i3 = i2
            if (i3 >= this.Total) {
                break
            }
            lLVector3Arr[i3] = LLVector3(this.Profile.get(i3))
            lLVector3Arr[i3].mul(f3)
            i2 = i3 + 1
        }
        Int i4 = this.Total - 1
        Int i5 = this.TotalOut
        while (i5 < this.Total) {
            this.Profile.set(i5, lLVector3Arr[i4])
            i5++
            i4--
        }
        Int i6 = 0
        while (true) {
            Int i7 = i6
            if (i7 >= this.Faces.size()) {
                return addFace
            }
            if (this.Faces.get(i7).Cap) {
                this.Faces.get(i7).Count *= 2
            }
            i6 = i7 + 1
        }
    }

    private Unit genNGon(PrimProfileParams primProfileParams, Int i, Float f, Float f2, Float f3, Int i2) {
        Float f4
        Float f5
        Float f6 = primProfileParams.Begin
        Float f7 = primProfileParams.End
        Float f8 = 1.0f / (i.toFloat())
        Float f9 = 6.2831855f * f8 * f3
        Int round = Math.round((i.toFloat()) / f3)
        Float f10 = round < 8 ? tableScale[round] : 0.5f
        Float floor = (Float) (Math.floor((Double) ((i.toFloat()) * f6)) / ((Double) (i.toFloat())))
        Float f11 = 6.2831855f * ((floor * f3) + f)
        LLVector3 lLVector3 = LLVector3((Math.toFloat().cos(f11.toDouble())) * f10, (Math.toFloat().sin(f11.toDouble())) * f10, floor)
        Float f12 = floor + f8
        Float f13 = f11 + f9
        LLVector3 lLVector32 = LLVector3((Math.toFloat().cos(f13.toDouble())) * f10, (Math.toFloat().sin(f13.toDouble())) * f10, f12)
        Float f14 = (f6 - floor) * (i.toFloat())
        if (f14 < 0.9999f) {
            this.Profile.add(LLVector3.lerp(lLVector3, lLVector32, f14))
            f5 = f13
            f4 = f12
        } else {
            f5 = f13
            f4 = f12
        }
        while (f4 < f7) {
            LLVector3 lLVector33 = LLVector3((Math.toFloat().cos(f5.toDouble())) * f10, (Math.toFloat().sin(f5.toDouble())) * f10, f4)
            if (this.Profile.size() > 0) {
                LLVector3 lLVector34 = this.Profile.get(this.Profile.size() - 1)
                for (Int i3 = 0; i3 < i2; i3++) {
                    this.Profile.add(LLVector3.lerp(lLVector34, lLVector33, (1.0f / ((Float) (i2 + 1))) * ((Float) (i3 + 1))))
                }
            }
            this.Profile.add(lLVector33)
            f5 += f9
            f4 += f8
            lLVector3 = lLVector33
        }
        LLVector3 lLVector35 = LLVector3((Math.toFloat().cos(f5.toDouble())) * f10, f10 * (Math.toFloat().sin(f5.toDouble())), f4)
        Float f15 = (f7 - (f4 - f8)) * (i.toFloat())
        if (f15 > 1.0E-4f) {
            LLVector3 lerp = LLVector3.lerp(lLVector3, lLVector35, f15)
            if (this.Profile.size() > 0) {
                LLVector3 lLVector36 = this.Profile.get(this.Profile.size() - 1)
                for (Int i4 = 0; i4 < i2; i4++) {
                    this.Profile.add(LLVector3.lerp(lLVector36, lerp, (1.0f / ((Float) (i2 + 1))) * ((Float) (i4 + 1))))
                }
            }
            this.Profile.add(lerp)
        }
        if ((f7 - f6) * f3 < 0.99f) {
            this.Open = true
            this.Concave = (f7 - f6) * f3 > 0.5f
            if (primProfileParams.Hollow <= 0.0f) {
                this.Profile.add(LLVector3(0.0f, 0.0f, 0.0f))
            }
        } else {
            this.Open = false
            this.Concave = false
        }
        this.Total = this.Profile.size()
    }

    private Int getNumNGonPoints(PrimProfileParams primProfileParams, Int i, Float f, Float f2, Float f3, Int i2) {
        Float f4
        Float f5 = primProfileParams.Begin
        Float f6 = primProfileParams.End
        Float f7 = 1.0f / (i.toFloat())
        Float floor = (Float) (Math.floor((Double) ((i.toFloat()) * f5)) / ((Double) (i.toFloat())))
        Float f8 = floor + f7
        if ((f5 - floor) * (i.toFloat()) < 0.9999f) {
            Float f9 = f8
            i3 = 1
            f4 = f9
        } else {
            Float f10 = f8
            i3 = 0
            f4 = f10
        }
        while (f4 < f6) {
            f4 += f7
            i3++
        }
        if ((f6 - (f4 - f7)) * (i.toFloat()) > 1.0E-4f) {
            i3++
        }
        return ((f6 - f5) * f3 >= 0.99f || primProfileParams.Hollow > 0.0f) ? i3 : i3 + 1
    }

    Int getNumPoints(PrimProfileParams primProfileParams, Boolean z, Float f, Int i, Boolean z2, Int i2) {
        if (f < 0.0f) {
            f = 0.0f
        }
        Float f2 = primProfileParams.Hollow
        switch (primProfileParams.CurveType & 15) {
            case 0:
                Float f3 = 6.0f * f
                if (f2 != 0.0f && (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) == 32) {
                    f3 = (Float) (Math.ceil((Double) (f3 / 4.0f)) * 4.0d)
                }
                Int i3 = f3.toInt()
                if (z2) {
                    i3 = i2
                }
                Int numNGonPoints = getNumNGonPoints(primProfileParams, i3, 0.0f, 0.0f, 1.0f, 0)
                return f2 != 0.0f ? numNGonPoints * 2 : numNGonPoints
            case 1:
                Int numNGonPoints2 = getNumNGonPoints(primProfileParams, 4, -0.375f, 0.0f, 1.0f, i)
                return f2 != 0.0f ? numNGonPoints2 * 2 : numNGonPoints2
            case 2:
            case 3:
            case 4:
                Int numNGonPoints3 = getNumNGonPoints(primProfileParams, 3, 0.0f, 0.0f, 1.0f, i)
                return f2 != 0.0f ? numNGonPoints3 * 2 : numNGonPoints3
            case 5:
                Float f4 = 6.0f * f * 0.5f
                if (f2 != 0.0f && (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) == 32) {
                    f4 = (Float) (Math.ceil((Double) (f4 / 2.0f)) * 2.0d)
                }
                Int numNGonPoints4 = getNumNGonPoints(primProfileParams, Math.toInt().floor(f4.toDouble()), 0.5f, 0.0f, 0.5f, 0)
                if (f2 != 0.0f) {
                    numNGonPoints4 *= 2
                }
                return ((((primProfileParams.End - primProfileParams.Begin) > 1.0f ? 1 : ((primProfileParams.End - primProfileParams.Begin) == 1.0f ? 0 : -1)) < 0) || f2 != 0.0f) ? numNGonPoints4 : numNGonPoints4 + 1
            default:
                return 0
        }
    }

    /* access modifiers changed from: protected */
    Unit genNormals(PrimProfileParams primProfileParams) {
        LLVector3 lLVector3
        Int size = this.Profile.size()
        Int i = this.TotalOut != 0 ? this.TotalOut : this.Total / 2
        this.EdgeNormals = LLVector3[(size * 2)]
        this.EdgeCenters = LLVector3[(size * 2)]
        this.Normals = LLVector2[size]
        Boolean z = primProfileParams.Hollow > 0.0f
        for (Int i2 = 0; i2 < size; i2++) {
            this.Normals[i2] = LLVector2(this.Profile.get(i2).x, this.Profile.get(i2).y)
            if (z && i2 >= i) {
                this.Normals[i2].mul(-1.0f)
            }
            if ((this.toDouble().Normals[i2].magVec()) < 0.001d) {
                Int i3 = i2 + -1 >= 0 ? i2 - 1 : size - 1
                Int i4 = i3 + -1 >= 0 ? i3 - 1 : size - 1
                Int i5 = i2 + 1 < size ? i2 + 1 : 0
                Int i6 = i5 + 1 < size ? i5 + 1 : 0
                this.Normals[i2] = LLVector2.sum(LLVector2((this.Profile.get(i3).x + this.Profile.get(i3).x) - this.Profile.get(i4).x, (this.Profile.get(i3).y + this.Profile.get(i3).y) - this.Profile.get(i4).y), LLVector2((this.Profile.get(i5).x + this.Profile.get(i5).x) - this.Profile.get(i6).x, (this.Profile.get(i5).y + this.Profile.get(i5).y) - this.Profile.get(i6).y))
                this.Normals[i2].mul(0.5f)
            }
            this.Normals[i2].normVec()
        }
        Int i7 = this.Concave ? 2 : 1
        for (Int i8 = 0; i8 < i7; i8++) {
            Int i9 = 0
            while (true) {
                Int i10 = i9
                if (i10 >= this.Total) {
                    break
                }
                LLVector3 lLVector32 = LLVector3(this.Profile.get(i10))
                lLVector32.z = 0.0f
                if (this.Concave && i8 == 0 && i10 == (this.Total - 1) / 2) {
                    lLVector3 = this.Profile.get(this.Total - 1)
                } else if (!this.Concave || i8 != 1 || i10 != this.Total - 1) {
                    LLVector3 lLVector33 = LLVector3()
                    LLVector3 lLVector34 = LLVector3()
                    lLVector3 = lLVector33
                    Int i11 = (i10 + 1) % this.Total
                    while (lLVector34.magVecSquared() < 1.0E-4f) {
                        lLVector3 = this.Profile.get(i11)
                        lLVector34.setSub(lLVector3, lLVector32)
                        i11 = (i11 + 1) % this.Total
                        if (i11 == i10) {
                            break
                        }
                    }
                } else {
                    lLVector3 = this.Profile.get((this.Total - 1) / 2)
                }
                lLVector3.z = 0.0f
                LLVector3 sub = LLVector3.sub(lLVector3, lLVector32)
                sub.setCross(LLVector3.z_axis)
                sub.normVec()
                this.EdgeNormals[(i8 * size) + i10] = sub
                this.EdgeCenters[(i8 * size) + i10] = LLVector3.lerp(lLVector32, lLVector3, 0.5f)
                i9 = i10 + 1
            }
        }
    }

    Boolean generate(PrimProfileParams primProfileParams, Boolean z, Float f, Int i, Boolean z2, Int i2) {
        Float f2
        Byte b
        Float f3
        Byte b2
        if (!this.Dirty && (!z2)) {
            return false
        }
        this.Dirty = false
        if (f < 0.0f) {
            f = 0.0f
        }
        this.Profile.clear()
        this.Faces.clear()
        Float f4 = primProfileParams.Begin
        Float f5 = primProfileParams.End
        Float f6 = primProfileParams.Hollow
        if (f4 > f5 - 0.01f) {
            return false
        }
        Int i3 = 0
        switch (primProfileParams.CurveType & 15) {
            case 0:
                Float f7 = 6.0f * f
                if (f6 != 0.0f) {
                    Byte b3 = (Byte) (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK)
                    if (b3 == 32) {
                        f3 = (Float) (Math.ceil((Double) (f7 / 4.0f)) * 4.0d)
                        b2 = b3
                    } else {
                        f3 = f7
                        b2 = b3
                    }
                } else {
                    f3 = f7
                    b2 = 0
                }
                Int i4 = f3.toInt()
                if (z2) {
                    i4 = i2
                }
                genNGon(primProfileParams, i4, 0.0f, 0.0f, 1.0f, 0)
                if (z) {
                    addCap(1)
                }
                if (!this.Open || f6 != 0.0f) {
                    addFace(0, this.Total, 0.0f, 32, false)
                } else {
                    addFace(0, this.Total - 1, 0.0f, 32, false)
                }
                if (f6 != 0.0f) {
                    switch (b2) {
                        case 32:
                            addHole(primProfileParams, true, 4.0f, 0.0f, f6, 1.0f, i)
                            break
                        case 48:
                            addHole(primProfileParams, true, 3.0f, 0.0f, f6, 1.0f, i)
                            break
                        default:
                            addHole(primProfileParams, false, f3, 0.0f, f6, 1.0f, 0)
                            break
                    }
                }
                break
            case 1:
                genNGon(primProfileParams, 4, -0.375f, 0.0f, 1.0f, i)
                if (z) {
                    addCap(1)
                }
                Int floor = Math.toInt().floor((Double) (4.0f * f4))
                while (true) {
                    Int i5 = floor
                    Int i6 = i3
                    if (i5 >= (Math.toInt().floor((Double) ((4.0f * f5) + 0.999f)))) {
                        Int i7 = 0
                        while (true) {
                            Int i8 = i7
                            if (i8 >= this.Profile.size()) {
                                if (f6 != 0.0f) {
                                    switch (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) {
                                        case 16:
                                            addHole(primProfileParams, false, 6.0f * f, -0.375f, f6, 1.0f, 0)
                                            break
                                        case 48:
                                            addHole(primProfileParams, true, 3.0f, -0.375f, f6, 1.0f, i)
                                            break
                                        default:
                                            addHole(primProfileParams, true, 4.0f, -0.375f, f6, 1.0f, i)
                                            break
                                    }
                                }
                                if (z) {
                                    this.Faces.get(0).Count = this.Total
                                    break
                                }
                            } else {
                                this.Profile.get(i8).z *= 4.0f
                                i7 = i8 + 1
                            }
                        }
                    } else {
                        i3 = i6 + 1
                        addFace((i + 1) * i6, i + 2, 1.0f, (Short) (32 << i5), true)
                        floor = i5 + 1
                    }
                }
                break
            case 2:
            case 3:
            case 4:
                genNGon(primProfileParams, 3, 0.0f, 0.0f, 1.0f, i)
                Int i9 = 0
                while (true) {
                    Int i10 = i9
                    if (i10 >= this.Profile.size()) {
                        if (z) {
                            addCap(1)
                        }
                        Int floor2 = Math.toInt().floor((Double) (3.0f * f4))
                        while (floor2 < (Math.toInt().floor((Double) ((3.0f * f5) + 0.999f)))) {
                            addFace(i3 * (i + 1), i + 2, 1.0f, (Short) (32 << floor2), true)
                            floor2++
                            i3++
                        }
                        if (f6 != 0.0f) {
                            Float f8 = f6 / 2.0f
                            switch (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) {
                                case 16:
                                    addHole(primProfileParams, false, 6.0f * f, 0.0f, f8, 1.0f, 0)
                                    break
                                case 32:
                                    addHole(primProfileParams, true, 4.0f, 0.0f, f8, 1.0f, i)
                                    break
                                default:
                                    addHole(primProfileParams, true, 3.0f, 0.0f, f8, 1.0f, i)
                                    break
                            }
                        }
                    } else {
                        this.Profile.get(i10).z *= 3.0f
                        i9 = i10 + 1
                    }
                }
                break
            case 5:
                Float f9 = 6.0f * f * 0.5f
                if (f6 != 0.0f) {
                    Byte b4 = (Byte) (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK)
                    if (b4 == 32) {
                        f2 = (Float) (Math.ceil((Double) (f9 / 2.0f)) * 2.0d)
                        b = b4
                    } else {
                        f2 = f9
                        b = b4
                    }
                } else {
                    f2 = f9
                    b = 0
                }
                genNGon(primProfileParams, Math.toInt().floor(f2.toDouble()), 0.5f, 0.0f, 0.5f, 0)
                if (z) {
                    addCap(1)
                }
                if (!this.Open || f6 != 0.0f) {
                    addFace(0, this.Total, 0.0f, 32, false)
                } else {
                    addFace(0, this.Total - 1, 0.0f, 32, false)
                }
                if (f6 != 0.0f) {
                    switch (b) {
                        case 32:
                            addHole(primProfileParams, true, 2.0f, 0.5f, f6, 0.5f, i)
                            break
                        case 48:
                            addHole(primProfileParams, true, 3.0f, 0.5f, f6, 0.5f, i)
                            break
                        default:
                            addHole(primProfileParams, false, f2, 0.5f, f6, 0.5f, 0)
                            break
                    }
                }
                if (f5 - f4 >= 1.0f) {
                    if (f6 == 0.0f) {
                        this.Open = false
                        this.Profile.add(LLVector3(this.Profile.get(0)))
                        this.Total++
                        break
                    }
                } else {
                    this.Open = true
                    break
                }
                break
        }
        if (z) {
            addCap(2)
        }
        if (!this.Open) {
            return true
        }
        addFace(this.Total - 1, 2, 0.5f, 8, true)
        if (f6 != 0.0f) {
            addFace(this.TotalOut - 1, 2, 0.5f, 16, true)
            return true
        }
        addFace(this.Total - 2, 2, 0.5f, 16, true)
        return true
    }
}
