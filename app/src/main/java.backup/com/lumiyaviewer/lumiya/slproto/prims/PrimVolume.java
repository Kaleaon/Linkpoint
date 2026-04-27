package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.GLTexture;
import com.lumiyaviewer.lumiya.slproto.prims.PrimProfile;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import java.util.ArrayList;

public class PrimVolume {
    private static final int FLEXI_PATH_REZ = 16;
    private static final int SCULPT_REZ_1 = 6;
    private static final int SCULPT_REZ_2 = 8;
    private static final int SCULPT_REZ_3 = 16;
    private static final int SCULPT_REZ_4 = 32;
    private float Detail;
    private int FaceMask;
    private boolean GenerateSingleFace;
    private LLVector3 LODScaleBias;
    Vector3Array Mesh;
    PrimPath Path;
    PrimProfile Profile;
    private int SculptLevel;
    private boolean Unique;
    public ArrayList<PrimVolumeFace> VolumeFaces = new ArrayList<>();
    private int sculptRequestedS;
    private int sculptRequestedT;
    PrimVolumeParams volumeParams;

    public static PrimVolume create(PrimVolumeParams primVolumeParams, float f, boolean z, boolean z2, GLTexture gLTexture) {
        if (primVolumeParams.isSculpt()) {
            if (gLTexture == null) {
                return null;
            }
            Debug.Log("Sculpt: using sculpt texture " + primVolumeParams.SculptID);
        }
        PrimVolume primVolume = new PrimVolume();
        primVolume.volumeParams = primVolumeParams;
        primVolume.Detail = f;
        primVolume.GenerateSingleFace = z;
        primVolume.Unique = z2;
        primVolume.Path = new PrimPath();
        primVolume.Profile = new PrimProfile();
        primVolume.FaceMask = 0;
        primVolume.LODScaleBias = new LLVector3(1.0f, 1.0f, 1.0f);
        if (!primVolumeParams.isSculpt()) {
            primVolume.generate();
            primVolume.createVolumeFaces();
            return primVolume;
        } else if (!primVolume.sculpt(gLTexture.getWidth(), gLTexture.getHeight(), gLTexture.getNumComponents(), gLTexture, 1)) {
            return null;
        } else {
            return primVolume;
        }
    }

    private void createVolumeFaces() {
        if (!this.GenerateSingleFace) {
            int numFaces = getNumFaces();
            this.VolumeFaces.ensureCapacity(numFaces);
            for (int i = 0; i < numFaces; i++) {
                PrimVolumeFace primVolumeFace = new PrimVolumeFace();
                PrimProfile.Face face = this.Profile.Faces.get(i);
                primVolumeFace.BeginS = face.Index;
                primVolumeFace.NumS = face.Count;
                primVolumeFace.BeginT = 0;
                primVolumeFace.NumT = this.Path.Path.size();
                primVolumeFace.ID = i;
                if (this.volumeParams.ProfileParams.Hollow > 0.0f) {
                    primVolumeFace.TypeMask |= 64;
                }
                if (this.Profile.Open) {
                    primVolumeFace.TypeMask |= 128;
                }
                if (face.Cap) {
                    primVolumeFace.TypeMask |= 2;
                    if (face.FaceID == 1) {
                        primVolumeFace.TypeMask |= 512;
                    } else {
                        primVolumeFace.TypeMask |= 1024;
                    }
                } else if ((face.FaceID & 24) != 0) {
                    primVolumeFace.TypeMask |= 260;
                } else {
                    primVolumeFace.TypeMask |= 8;
                    if (face.Flat) {
                        primVolumeFace.TypeMask |= 256;
                    }
                    if ((face.FaceID & 4) != 0) {
                        primVolumeFace.TypeMask |= 16;
                        if (face.Flat && primVolumeFace.NumS > 2) {
                            primVolumeFace.NumS *= 2;
                        }
                    } else {
                        primVolumeFace.TypeMask |= 32;
                    }
                }
                this.VolumeFaces.add(primVolumeFace);
            }
            for (PrimVolumeFace create : this.VolumeFaces) {
                create.create(this);
            }
        }
    }

    private boolean generate() {
        int i = (int) (this.Detail * 0.66f);
        if (this.volumeParams.PathParams.CurveType == 16 && (!(this.volumeParams.PathParams.ScaleX == 1.0f && this.volumeParams.PathParams.ScaleY == 1.0f) && (this.volumeParams.ProfileParams.CurveType == 1 || this.volumeParams.ProfileParams.CurveType == 2 || this.volumeParams.ProfileParams.CurveType == 3 || this.volumeParams.ProfileParams.CurveType == 4))) {
            i = 0;
        }
        this.LODScaleBias.set(0.5f, 0.5f, 0.5f);
        float f = this.Detail;
        float f2 = this.Detail;
        byte b = this.volumeParams.PathParams.CurveType;
        byte b2 = this.volumeParams.ProfileParams.CurveType;
        if (b == 16 && b2 == 0) {
            this.LODScaleBias.set(0.6f, 0.6f, 0.0f);
        } else if (b == 32) {
            this.LODScaleBias.set(0.6f, 0.6f, 0.6f);
        }
        boolean generate = this.Path.generate(this.volumeParams.PathParams, f2, this.volumeParams.isFlexible() ? this.volumeParams.FlexiParams.NumFlexiSections - 2 : i, false, 0);
        boolean generate2 = this.Profile.generate(this.volumeParams.ProfileParams, this.Path.Open, f, i, false, 0);
        if (!generate && !generate2) {
            return false;
        }
        int size = this.Path.Path.size();
        int size2 = this.Profile.Profile.size();
        this.Mesh = new Vector3Array(size2 * size);
        for (int i2 = 0; i2 < size; i2++) {
            LLVector2 lLVector2 = this.Path.Path.get(i2).scale;
            LLQuaternion lLQuaternion = this.Path.Path.get(i2).rot;
            for (int i3 = 0; i3 < size2; i3++) {
                int i4 = (i2 * size2) + i3;
                this.Mesh.set(i4, lLVector2.x * this.Profile.Profile.get(i3).x, this.Profile.Profile.get(i3).y * lLVector2.y, 0.0f);
                this.Mesh.mul(i4, lLQuaternion);
                this.Mesh.add(i4, this.Path.Path.get(i2).pos);
            }
        }
        for (PrimProfile.Face face : this.Profile.Faces) {
            this.FaceMask = face.FaceID | this.FaceMask;
        }
        return true;
    }

    private int getNumFaces() {
        return this.Profile.Faces.size();
    }

    private boolean sculpt(int i, int i2, int i3, GLTexture gLTexture, int i4) {
        boolean z;
        byte b = this.volumeParams.SculptType;
        if (i == 0 || i2 == 0 || i3 < 3 || gLTexture == null) {
            i4 = -1;
            z = true;
        } else {
            z = false;
        }
        sculpt_calc_mesh_resolution(i, i2, this.Detail);
        this.Path.generate(this.volumeParams.PathParams, this.Detail, 0, true, this.sculptRequestedS);
        this.Profile.generate(this.volumeParams.ProfileParams, this.Path.Open, this.Detail, 0, true, this.sculptRequestedT);
        int size = this.Path.Path.size();
        int size2 = this.Profile.Profile.size();
        if (size == 0 || size2 == 0) {
            return false;
        }
        this.Mesh = new Vector3Array(size * size2);
        if (z) {
            return false;
        }
        try {
            sculptGenerateMapVertices(i, i2, i3, gLTexture, b);
            int i5 = 0;
            while (true) {
                int i6 = i5;
                if (i6 < this.Profile.Faces.size()) {
                    this.FaceMask = this.Profile.Faces.get(i6).FaceID | this.FaceMask;
                    i5 = i6 + 1;
                } else {
                    this.SculptLevel = i4;
                    this.VolumeFaces.clear();
                    createVolumeFaces();
                    return true;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    private void sculptGenerateMapVertices(int i, int i2, int i3, GLTexture gLTexture, byte b) {
        byte b2 = (byte) (b & 7);
        boolean z = (b & 64) != 0;
        boolean z2 = (b & Byte.MIN_VALUE) != 0;
        boolean z3 = z ? !z2 : z2;
        int size = this.Path.Path.size();
        int size2 = this.Profile.Profile.size();
        int i4 = 0;
        int i5 = 0;
        while (i4 < size) {
            for (int i6 = 0; i6 < size2; i6++) {
                int i7 = i6 + i5;
                int i8 = (int) ((((float) (z3 ? (size2 - i6) - 1 : i6)) / ((float) (size2 - 1))) * ((float) i));
                int i9 = (int) ((((float) i4) / ((float) (size - 1))) * ((float) i2));
                if (i9 == 0 && b2 == 1) {
                    i8 = i / 2;
                }
                if (i9 == i2) {
                    i9 = b2 == 2 ? 0 : i2 - 1;
                    if (b2 == 1) {
                        i8 = i / 2;
                    }
                }
                if (i8 == i) {
                    i8 = (b2 == 1 || b2 == 2 || b2 == 4) ? 0 : i - 1;
                }
                if (i8 <= 0) {
                    i8 = 0;
                }
                if (i8 >= i) {
                    i8 = i - 1;
                }
                if (i9 <= 0) {
                    i9 = 0;
                }
                if (i9 >= i2) {
                    i9 = i2 - 1;
                }
                int rgb = gLTexture.getRGB(((i9 * i) + i8) * i3);
                float f = (((float) ((rgb >> 16) & 255)) / 255.0f) - 0.5f;
                float f2 = (((float) ((rgb >> 8) & 255)) / 255.0f) - 0.5f;
                float f3 = (((float) (rgb & 255)) / 255.0f) - 0.5f;
                if (z2) {
                    f *= -1.0f;
                }
                this.Mesh.set(i7, f, f2, f3);
            }
            i4++;
            i5 += size2;
        }
    }

    private void sculpt_calc_mesh_resolution(int i, int i2, float f) {
        int pow = (int) Math.pow((double) sculpt_sides(f), 2.0d);
        int i3 = (i * i2) / 4;
        int min = i3 > 0 ? Math.min(pow, i3) : pow;
        int max = Math.max(min / Math.max((int) Math.sqrt((double) (((float) min) / ((i == 0 || i2 == 0) ? 1.0f : ((float) i) / ((float) i2)))), 4), 4);
        this.sculptRequestedS = min / max;
        this.sculptRequestedT = max;
    }

    private int sculpt_sides(float f) {
        if (((double) f) <= 1.0d) {
            return 6;
        }
        if (((double) f) <= 2.0d) {
            return 8;
        }
        return ((double) f) <= 3.0d ? 16 : 32;
    }

    /* access modifiers changed from: package-private */
    public byte getPathType() {
        return this.volumeParams.PathParams.CurveType;
    }

    /* access modifiers changed from: package-private */
    public byte getProfileType() {
        return this.volumeParams.ProfileParams.CurveType;
    }
}
