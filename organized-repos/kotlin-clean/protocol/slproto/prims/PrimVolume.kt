package com.linkpoint.slproto.prims

import com.linkpoint.Debug
import com.linkpoint.render.GLTexture
import com.linkpoint.slproto.prims.PrimProfile
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector2
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.Vector3Array
import java.util.ArrayList

class PrimVolume {
    private const val FLEXI_PATH_REZ: Int = 16
    private const val SCULPT_REZ_1: Int = 6
    private const val SCULPT_REZ_2: Int = 8
    private const val SCULPT_REZ_3: Int = 16
    private const val SCULPT_REZ_4: Int = 32
    private Float Detail
    private Int FaceMask
    private Boolean GenerateSingleFace
    private LLVector3 LODScaleBias
    Vector3Array Mesh
    PrimPath Path
    PrimProfile Profile
    private Int SculptLevel
    private Boolean Unique
    public ArrayList<PrimVolumeFace> VolumeFaces = ArrayList<>()
    private Int sculptRequestedS
    private Int sculptRequestedT
    PrimVolumeParams volumeParams

    @JvmStatic
    PrimVolume create(PrimVolumeParams primVolumeParams, Float f, Boolean z, Boolean z2, GLTexture gLTexture) {
        if (primVolumeParams.isSculpt()) {
            if (gLTexture == null) {
                return null
            }
            Debug.Log("Sculpt: using sculpt texture " + primVolumeParams.SculptID)
        }
        PrimVolume primVolume = PrimVolume()
        primVolume.volumeParams = primVolumeParams
        primVolume.Detail = f
        primVolume.GenerateSingleFace = z
        primVolume.Unique = z2
        primVolume.Path = PrimPath()
        primVolume.Profile = PrimProfile()
        primVolume.FaceMask = 0
        primVolume.LODScaleBias = LLVector3(1.0f, 1.0f, 1.0f)
        if (!primVolumeParams.isSculpt()) {
            primVolume.generate()
            primVolume.createVolumeFaces()
            return primVolume
        } else if (!primVolume.sculpt(gLTexture.getWidth(), gLTexture.getHeight(), gLTexture.getNumComponents(), gLTexture, 1)) {
            return null
        } else {
            return primVolume
        }
    }

    private Unit createVolumeFaces() {
        if (!this.GenerateSingleFace) {
            Int numFaces = getNumFaces()
            this.VolumeFaces.ensureCapacity(numFaces)
            for (Int i = 0; i < numFaces; i++) {
                PrimVolumeFace primVolumeFace = PrimVolumeFace()
                PrimProfile.Face face = this.Profile.Faces.get(i)
                primVolumeFace.BeginS = face.Index
                primVolumeFace.NumS = face.Count
                primVolumeFace.BeginT = 0
                primVolumeFace.NumT = this.Path.Path.size()
                primVolumeFace.ID = i
                if (this.volumeParams.ProfileParams.Hollow > 0.0f) {
                    primVolumeFace.TypeMask |= 64
                }
                if (this.Profile.Open) {
                    primVolumeFace.TypeMask |= 128
                }
                if (face.Cap) {
                    primVolumeFace.TypeMask |= 2
                    if (face.FaceID == 1) {
                        primVolumeFace.TypeMask |= 512
                    } else {
                        primVolumeFace.TypeMask |= 1024
                    }
                } else if ((face.FaceID & 24) != 0) {
                    primVolumeFace.TypeMask |= 260
                } else {
                    primVolumeFace.TypeMask |= 8
                    if (face.Flat) {
                        primVolumeFace.TypeMask |= 256
                    }
                    if ((face.FaceID & 4) != 0) {
                        primVolumeFace.TypeMask |= 16
                        if (face.Flat && primVolumeFace.NumS > 2) {
                            primVolumeFace.NumS *= 2
                        }
                    } else {
                        primVolumeFace.TypeMask |= 32
                    }
                }
                this.VolumeFaces.add(primVolumeFace)
            }
            for (PrimVolumeFace create : this.VolumeFaces) {
                create.create(this)
            }
        }
    }

    private Boolean generate() {
        Int i = (Int) (this.Detail * 0.66f)
        if (this.volumeParams.PathParams.CurveType == 16 && (!(this.volumeParams.PathParams.ScaleX == 1.0f && this.volumeParams.PathParams.ScaleY == 1.0f) && (this.volumeParams.ProfileParams.CurveType == 1 || this.volumeParams.ProfileParams.CurveType == 2 || this.volumeParams.ProfileParams.CurveType == 3 || this.volumeParams.ProfileParams.CurveType == 4))) {
            i = 0
        }
        this.LODScaleBias.set(0.5f, 0.5f, 0.5f)
        Float f = this.Detail
        Float f2 = this.Detail
        Byte b = this.volumeParams.PathParams.CurveType
        Byte b2 = this.volumeParams.ProfileParams.CurveType
        if (b == 16 && b2 == 0) {
            this.LODScaleBias.set(0.6f, 0.6f, 0.0f)
        } else if (b == 32) {
            this.LODScaleBias.set(0.6f, 0.6f, 0.6f)
        }
        Boolean generate = this.Path.generate(this.volumeParams.PathParams, f2, this.volumeParams.isFlexible() ? this.volumeParams.FlexiParams.NumFlexiSections - 2 : i, false, 0)
        Boolean generate2 = this.Profile.generate(this.volumeParams.ProfileParams, this.Path.Open, f, i, false, 0)
        if (!generate && !generate2) {
            return false
        }
        Int size = this.Path.Path.size()
        Int size2 = this.Profile.Profile.size()
        this.Mesh = Vector3Array(size2 * size)
        for (Int i2 = 0; i2 < size; i2++) {
            LLVector2 lLVector2 = this.Path.Path.get(i2).scale
            LLQuaternion lLQuaternion = this.Path.Path.get(i2).rot
            for (Int i3 = 0; i3 < size2; i3++) {
                Int i4 = (i2 * size2) + i3
                this.Mesh.set(i4, lLVector2.x * this.Profile.Profile.get(i3).x, this.Profile.Profile.get(i3).y * lLVector2.y, 0.0f)
                this.Mesh.mul(i4, lLQuaternion)
                this.Mesh.add(i4, this.Path.Path.get(i2).pos)
            }
        }
        for (PrimProfile.Face face : this.Profile.Faces) {
            this.FaceMask = face.FaceID | this.FaceMask
        }
        return true
    }

    private Int getNumFaces() {
        return this.Profile.Faces.size()
    }

    private Boolean sculpt(Int i, Int i2, Int i3, GLTexture gLTexture, Int i4) {
        Byte b = this.volumeParams.SculptType
        if (i == 0 || i2 == 0 || i3 < 3 || gLTexture == null) {
            i4 = -1
            z = true
        } else {
            z = false
        }
        sculpt_calc_mesh_resolution(i, i2, this.Detail)
        this.Path.generate(this.volumeParams.PathParams, this.Detail, 0, true, this.sculptRequestedS)
        this.Profile.generate(this.volumeParams.ProfileParams, this.Path.Open, this.Detail, 0, true, this.sculptRequestedT)
        Int size = this.Path.Path.size()
        Int size2 = this.Profile.Profile.size()
        if (size == 0 || size2 == 0) {
            return false
        }
        this.Mesh = Vector3Array(size * size2)
        if (z) {
            return false
        }
        try {
            sculptGenerateMapVertices(i, i2, i3, gLTexture, b)
            Int i5 = 0
            while (true) {
                Int i6 = i5
                if (i6 < this.Profile.Faces.size()) {
                    this.FaceMask = this.Profile.Faces.get(i6).FaceID | this.FaceMask
                    i5 = i6 + 1
                } else {
                    this.SculptLevel = i4
                    this.VolumeFaces.clear()
                    createVolumeFaces()
                    return true
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false
        }
    }

    private Unit sculptGenerateMapVertices(Int i, Int i2, Int i3, GLTexture gLTexture, Byte b) {
        Byte b2 = (Byte) (b & 7)
        Boolean z = (b & 64) != 0
        Boolean z2 = (b & Byte.MIN_VALUE) != 0
        Boolean z3 = z ? !z2 : z2
        Int size = this.Path.Path.size()
        Int size2 = this.Profile.Profile.size()
        Int i4 = 0
        Int i5 = 0
        while (i4 < size) {
            for (Int i6 = 0; i6 < size2; i6++) {
                Int i7 = i6 + i5
                Int i8 = (Int) ((((Float) (z3 ? (size2 - i6) - 1 : i6)) / ((Float) (size2 - 1))) * (i.toFloat()))
                Int i9 = (Int) (((i4.toFloat()) / ((Float) (size - 1))) * (i2.toFloat()))
                if (i9 == 0 && b2 == 1) {
                    i8 = i / 2
                }
                if (i9 == i2) {
                    i9 = b2 == 2 ? 0 : i2 - 1
                    if (b2 == 1) {
                        i8 = i / 2
                    }
                }
                if (i8 == i) {
                    i8 = (b2 == 1 || b2 == 2 || b2 == 4) ? 0 : i - 1
                }
                if (i8 <= 0) {
                    i8 = 0
                }
                if (i8 >= i) {
                    i8 = i - 1
                }
                if (i9 <= 0) {
                    i9 = 0
                }
                if (i9 >= i2) {
                    i9 = i2 - 1
                }
                Int rgb = gLTexture.getRGB(((i9 * i) + i8) * i3)
                Float f = (((Float) ((rgb >> 16) & 255)) / 255.0f) - 0.5f
                Float f2 = (((Float) ((rgb >> 8) & 255)) / 255.0f) - 0.5f
                Float f3 = (((Float) (rgb & 255)) / 255.0f) - 0.5f
                if (z2) {
                    f *= -1.0f
                }
                this.Mesh.set(i7, f, f2, f3)
            }
            i4++
            i5 += size2
        }
    }

    private Unit sculpt_calc_mesh_resolution(Int i, Int i2, Float f) {
        Int pow = Math.toInt().pow(sculpt_sides.toDouble()(f), 2.0d)
        Int i3 = (i * i2) / 4
        Int min = i3 > 0 ? Math.min(pow, i3) : pow
        Int max = Math.max(min / Math.max(Math.toInt().sqrt((Double) ((min.toFloat()) / ((i == 0 || i2 == 0) ? 1.0f : (i.toFloat()) / (i2.toFloat())))), 4), 4)
        this.sculptRequestedS = min / max
        this.sculptRequestedT = max
    }

    private Int sculpt_sides(Float f) {
        if ((f.toDouble()) <= 1.0d) {
            return 6
        }
        if ((f.toDouble()) <= 2.0d) {
            return 8
        }
        return (f.toDouble()) <= 3.0d ? 16 : 32
    }

    /* access modifiers changed from: package-private */
    public Byte getPathType() {
        return this.volumeParams.PathParams.CurveType
    }

    /* access modifiers changed from: package-private */
    public Byte getProfileType() {
        return this.volumeParams.ProfileParams.CurveType
    }
}
