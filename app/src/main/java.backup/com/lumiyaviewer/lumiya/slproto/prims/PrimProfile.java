package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.ArrayList;

public class PrimProfile {
    public static final int MIN_DETAIL_FACES = 6;
    private static final float[] tableScale = {1.0f, 1.0f, 1.0f, 0.5f, 0.707107f, 0.53f, 0.525f, 0.5f};
    public boolean Concave = false;
    public boolean Dirty = true;
    public LLVector3[] EdgeCenters;
    public LLVector3[] EdgeNormals;
    public ArrayList<Face> Faces = new ArrayList<>();
    public LLVector2[] Normals;
    public boolean Open = false;
    public ArrayList<LLVector3> Profile = new ArrayList<>();
    public int Total = 2;
    public int TotalOut = 0;

    public static class Face {
        public static final short LL_FACE_INNER_SIDE = 4;
        public static final short LL_FACE_OUTER_SIDE_0 = 32;
        public static final short LL_FACE_OUTER_SIDE_1 = 64;
        public static final short LL_FACE_OUTER_SIDE_2 = 128;
        public static final short LL_FACE_OUTER_SIDE_3 = 256;
        public static final short LL_FACE_PATH_BEGIN = 1;
        public static final short LL_FACE_PATH_END = 2;
        public static final short LL_FACE_PROFILE_BEGIN = 8;
        public static final short LL_FACE_PROFILE_END = 16;
        public boolean Cap;
        public int Count;
        public short FaceID;
        public boolean Flat;
        public int Index;
        public float ScaleU;
    }

    private Face addCap(short s) {
        Face face = new Face();
        face.Index = 0;
        face.Count = this.Total;
        face.ScaleU = 1.0f;
        face.Cap = true;
        face.FaceID = s;
        this.Faces.add(face);
        return face;
    }

    private Face addFace(int i, int i2, float f, short s, boolean z) {
        Face face = new Face();
        face.Index = i;
        face.Count = i2;
        face.ScaleU = f;
        face.Flat = z;
        face.Cap = false;
        face.FaceID = s;
        this.Faces.add(face);
        return face;
    }

    private Face addHole(PrimProfileParams primProfileParams, boolean z, float f, float f2, float f3, float f4, int i) {
        this.TotalOut = this.Total;
        genNGon(primProfileParams, (int) Math.floor((double) f), f2, -1.0f, f4, i);
        Face addFace = addFace(this.TotalOut, this.Total - this.TotalOut, 0.0f, 4, z);
        LLVector3[] lLVector3Arr = new LLVector3[this.Total];
        int i2 = this.TotalOut;
        while (true) {
            int i3 = i2;
            if (i3 >= this.Total) {
                break;
            }
            lLVector3Arr[i3] = new LLVector3(this.Profile.get(i3));
            lLVector3Arr[i3].mul(f3);
            i2 = i3 + 1;
        }
        int i4 = this.Total - 1;
        int i5 = this.TotalOut;
        while (i5 < this.Total) {
            this.Profile.set(i5, lLVector3Arr[i4]);
            i5++;
            i4--;
        }
        int i6 = 0;
        while (true) {
            int i7 = i6;
            if (i7 >= this.Faces.size()) {
                return addFace;
            }
            if (this.Faces.get(i7).Cap) {
                this.Faces.get(i7).Count *= 2;
            }
            i6 = i7 + 1;
        }
    }

    private void genNGon(PrimProfileParams primProfileParams, int i, float f, float f2, float f3, int i2) {
        float f4;
        float f5;
        float f6 = primProfileParams.Begin;
        float f7 = primProfileParams.End;
        float f8 = 1.0f / ((float) i);
        float f9 = 6.2831855f * f8 * f3;
        int round = Math.round(((float) i) / f3);
        float f10 = round < 8 ? tableScale[round] : 0.5f;
        float floor = (float) (Math.floor((double) (((float) i) * f6)) / ((double) ((float) i)));
        float f11 = 6.2831855f * ((floor * f3) + f);
        LLVector3 lLVector3 = new LLVector3(((float) Math.cos((double) f11)) * f10, ((float) Math.sin((double) f11)) * f10, floor);
        float f12 = floor + f8;
        float f13 = f11 + f9;
        LLVector3 lLVector32 = new LLVector3(((float) Math.cos((double) f13)) * f10, ((float) Math.sin((double) f13)) * f10, f12);
        float f14 = (f6 - floor) * ((float) i);
        if (f14 < 0.9999f) {
            this.Profile.add(LLVector3.lerp(lLVector3, lLVector32, f14));
            f5 = f13;
            f4 = f12;
        } else {
            f5 = f13;
            f4 = f12;
        }
        while (f4 < f7) {
            LLVector3 lLVector33 = new LLVector3(((float) Math.cos((double) f5)) * f10, ((float) Math.sin((double) f5)) * f10, f4);
            if (this.Profile.size() > 0) {
                LLVector3 lLVector34 = this.Profile.get(this.Profile.size() - 1);
                for (int i3 = 0; i3 < i2; i3++) {
                    this.Profile.add(LLVector3.lerp(lLVector34, lLVector33, (1.0f / ((float) (i2 + 1))) * ((float) (i3 + 1))));
                }
            }
            this.Profile.add(lLVector33);
            f5 += f9;
            f4 += f8;
            lLVector3 = lLVector33;
        }
        LLVector3 lLVector35 = new LLVector3(((float) Math.cos((double) f5)) * f10, f10 * ((float) Math.sin((double) f5)), f4);
        float f15 = (f7 - (f4 - f8)) * ((float) i);
        if (f15 > 1.0E-4f) {
            LLVector3 lerp = LLVector3.lerp(lLVector3, lLVector35, f15);
            if (this.Profile.size() > 0) {
                LLVector3 lLVector36 = this.Profile.get(this.Profile.size() - 1);
                for (int i4 = 0; i4 < i2; i4++) {
                    this.Profile.add(LLVector3.lerp(lLVector36, lerp, (1.0f / ((float) (i2 + 1))) * ((float) (i4 + 1))));
                }
            }
            this.Profile.add(lerp);
        }
        if ((f7 - f6) * f3 < 0.99f) {
            this.Open = true;
            this.Concave = (f7 - f6) * f3 > 0.5f;
            if (primProfileParams.Hollow <= 0.0f) {
                this.Profile.add(new LLVector3(0.0f, 0.0f, 0.0f));
            }
        } else {
            this.Open = false;
            this.Concave = false;
        }
        this.Total = this.Profile.size();
    }

    private static int getNumNGonPoints(PrimProfileParams primProfileParams, int i, float f, float f2, float f3, int i2) {
        float f4;
        int i3;
        float f5 = primProfileParams.Begin;
        float f6 = primProfileParams.End;
        float f7 = 1.0f / ((float) i);
        float floor = (float) (Math.floor((double) (((float) i) * f5)) / ((double) ((float) i)));
        float f8 = floor + f7;
        if ((f5 - floor) * ((float) i) < 0.9999f) {
            float f9 = f8;
            i3 = 1;
            f4 = f9;
        } else {
            float f10 = f8;
            i3 = 0;
            f4 = f10;
        }
        while (f4 < f6) {
            f4 += f7;
            i3++;
        }
        if ((f6 - (f4 - f7)) * ((float) i) > 1.0E-4f) {
            i3++;
        }
        return ((f6 - f5) * f3 >= 0.99f || primProfileParams.Hollow > 0.0f) ? i3 : i3 + 1;
    }

    public static int getNumPoints(PrimProfileParams primProfileParams, boolean z, float f, int i, boolean z2, int i2) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        float f2 = primProfileParams.Hollow;
        switch (primProfileParams.CurveType & 15) {
            case 0:
                float f3 = 6.0f * f;
                if (f2 != 0.0f && (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) == 32) {
                    f3 = (float) (Math.ceil((double) (f3 / 4.0f)) * 4.0d);
                }
                int i3 = (int) f3;
                if (z2) {
                    i3 = i2;
                }
                int numNGonPoints = getNumNGonPoints(primProfileParams, i3, 0.0f, 0.0f, 1.0f, 0);
                return f2 != 0.0f ? numNGonPoints * 2 : numNGonPoints;
            case 1:
                int numNGonPoints2 = getNumNGonPoints(primProfileParams, 4, -0.375f, 0.0f, 1.0f, i);
                return f2 != 0.0f ? numNGonPoints2 * 2 : numNGonPoints2;
            case 2:
            case 3:
            case 4:
                int numNGonPoints3 = getNumNGonPoints(primProfileParams, 3, 0.0f, 0.0f, 1.0f, i);
                return f2 != 0.0f ? numNGonPoints3 * 2 : numNGonPoints3;
            case 5:
                float f4 = 6.0f * f * 0.5f;
                if (f2 != 0.0f && (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) == 32) {
                    f4 = (float) (Math.ceil((double) (f4 / 2.0f)) * 2.0d);
                }
                int numNGonPoints4 = getNumNGonPoints(primProfileParams, (int) Math.floor((double) f4), 0.5f, 0.0f, 0.5f, 0);
                if (f2 != 0.0f) {
                    numNGonPoints4 *= 2;
                }
                return ((((primProfileParams.End - primProfileParams.Begin) > 1.0f ? 1 : ((primProfileParams.End - primProfileParams.Begin) == 1.0f ? 0 : -1)) < 0) || f2 != 0.0f) ? numNGonPoints4 : numNGonPoints4 + 1;
            default:
                return 0;
        }
    }

    /* access modifiers changed from: protected */
    public void genNormals(PrimProfileParams primProfileParams) {
        LLVector3 lLVector3;
        int size = this.Profile.size();
        int i = this.TotalOut != 0 ? this.TotalOut : this.Total / 2;
        this.EdgeNormals = new LLVector3[(size * 2)];
        this.EdgeCenters = new LLVector3[(size * 2)];
        this.Normals = new LLVector2[size];
        boolean z = primProfileParams.Hollow > 0.0f;
        for (int i2 = 0; i2 < size; i2++) {
            this.Normals[i2] = new LLVector2(this.Profile.get(i2).x, this.Profile.get(i2).y);
            if (z && i2 >= i) {
                this.Normals[i2].mul(-1.0f);
            }
            if (((double) this.Normals[i2].magVec()) < 0.001d) {
                int i3 = i2 + -1 >= 0 ? i2 - 1 : size - 1;
                int i4 = i3 + -1 >= 0 ? i3 - 1 : size - 1;
                int i5 = i2 + 1 < size ? i2 + 1 : 0;
                int i6 = i5 + 1 < size ? i5 + 1 : 0;
                this.Normals[i2] = LLVector2.sum(new LLVector2((this.Profile.get(i3).x + this.Profile.get(i3).x) - this.Profile.get(i4).x, (this.Profile.get(i3).y + this.Profile.get(i3).y) - this.Profile.get(i4).y), new LLVector2((this.Profile.get(i5).x + this.Profile.get(i5).x) - this.Profile.get(i6).x, (this.Profile.get(i5).y + this.Profile.get(i5).y) - this.Profile.get(i6).y));
                this.Normals[i2].mul(0.5f);
            }
            this.Normals[i2].normVec();
        }
        int i7 = this.Concave ? 2 : 1;
        for (int i8 = 0; i8 < i7; i8++) {
            int i9 = 0;
            while (true) {
                int i10 = i9;
                if (i10 >= this.Total) {
                    break;
                }
                LLVector3 lLVector32 = new LLVector3(this.Profile.get(i10));
                lLVector32.z = 0.0f;
                if (this.Concave && i8 == 0 && i10 == (this.Total - 1) / 2) {
                    lLVector3 = this.Profile.get(this.Total - 1);
                } else if (!this.Concave || i8 != 1 || i10 != this.Total - 1) {
                    LLVector3 lLVector33 = new LLVector3();
                    LLVector3 lLVector34 = new LLVector3();
                    lLVector3 = lLVector33;
                    int i11 = (i10 + 1) % this.Total;
                    while (lLVector34.magVecSquared() < 1.0E-4f) {
                        lLVector3 = this.Profile.get(i11);
                        lLVector34.setSub(lLVector3, lLVector32);
                        i11 = (i11 + 1) % this.Total;
                        if (i11 == i10) {
                            break;
                        }
                    }
                } else {
                    lLVector3 = this.Profile.get((this.Total - 1) / 2);
                }
                lLVector3.z = 0.0f;
                LLVector3 sub = LLVector3.sub(lLVector3, lLVector32);
                sub.setCross(LLVector3.z_axis);
                sub.normVec();
                this.EdgeNormals[(i8 * size) + i10] = sub;
                this.EdgeCenters[(i8 * size) + i10] = LLVector3.lerp(lLVector32, lLVector3, 0.5f);
                i9 = i10 + 1;
            }
        }
    }

    public boolean generate(PrimProfileParams primProfileParams, boolean z, float f, int i, boolean z2, int i2) {
        float f2;
        byte b;
        float f3;
        byte b2;
        if (!this.Dirty && (!z2)) {
            return false;
        }
        this.Dirty = false;
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.Profile.clear();
        this.Faces.clear();
        float f4 = primProfileParams.Begin;
        float f5 = primProfileParams.End;
        float f6 = primProfileParams.Hollow;
        if (f4 > f5 - 0.01f) {
            return false;
        }
        int i3 = 0;
        switch (primProfileParams.CurveType & 15) {
            case 0:
                float f7 = 6.0f * f;
                if (f6 != 0.0f) {
                    byte b3 = (byte) (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK);
                    if (b3 == 32) {
                        f3 = (float) (Math.ceil((double) (f7 / 4.0f)) * 4.0d);
                        b2 = b3;
                    } else {
                        f3 = f7;
                        b2 = b3;
                    }
                } else {
                    f3 = f7;
                    b2 = 0;
                }
                int i4 = (int) f3;
                if (z2) {
                    i4 = i2;
                }
                genNGon(primProfileParams, i4, 0.0f, 0.0f, 1.0f, 0);
                if (z) {
                    addCap(1);
                }
                if (!this.Open || f6 != 0.0f) {
                    addFace(0, this.Total, 0.0f, 32, false);
                } else {
                    addFace(0, this.Total - 1, 0.0f, 32, false);
                }
                if (f6 != 0.0f) {
                    switch (b2) {
                        case 32:
                            addHole(primProfileParams, true, 4.0f, 0.0f, f6, 1.0f, i);
                            break;
                        case 48:
                            addHole(primProfileParams, true, 3.0f, 0.0f, f6, 1.0f, i);
                            break;
                        default:
                            addHole(primProfileParams, false, f3, 0.0f, f6, 1.0f, 0);
                            break;
                    }
                }
                break;
            case 1:
                genNGon(primProfileParams, 4, -0.375f, 0.0f, 1.0f, i);
                if (z) {
                    addCap(1);
                }
                int floor = (int) Math.floor((double) (4.0f * f4));
                while (true) {
                    int i5 = floor;
                    int i6 = i3;
                    if (i5 >= ((int) Math.floor((double) ((4.0f * f5) + 0.999f)))) {
                        int i7 = 0;
                        while (true) {
                            int i8 = i7;
                            if (i8 >= this.Profile.size()) {
                                if (f6 != 0.0f) {
                                    switch (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) {
                                        case 16:
                                            addHole(primProfileParams, false, 6.0f * f, -0.375f, f6, 1.0f, 0);
                                            break;
                                        case 48:
                                            addHole(primProfileParams, true, 3.0f, -0.375f, f6, 1.0f, i);
                                            break;
                                        default:
                                            addHole(primProfileParams, true, 4.0f, -0.375f, f6, 1.0f, i);
                                            break;
                                    }
                                }
                                if (z) {
                                    this.Faces.get(0).Count = this.Total;
                                    break;
                                }
                            } else {
                                this.Profile.get(i8).z *= 4.0f;
                                i7 = i8 + 1;
                            }
                        }
                    } else {
                        i3 = i6 + 1;
                        addFace((i + 1) * i6, i + 2, 1.0f, (short) (32 << i5), true);
                        floor = i5 + 1;
                    }
                }
                break;
            case 2:
            case 3:
            case 4:
                genNGon(primProfileParams, 3, 0.0f, 0.0f, 1.0f, i);
                int i9 = 0;
                while (true) {
                    int i10 = i9;
                    if (i10 >= this.Profile.size()) {
                        if (z) {
                            addCap(1);
                        }
                        int floor2 = (int) Math.floor((double) (3.0f * f4));
                        while (floor2 < ((int) Math.floor((double) ((3.0f * f5) + 0.999f)))) {
                            addFace(i3 * (i + 1), i + 2, 1.0f, (short) (32 << floor2), true);
                            floor2++;
                            i3++;
                        }
                        if (f6 != 0.0f) {
                            float f8 = f6 / 2.0f;
                            switch (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) {
                                case 16:
                                    addHole(primProfileParams, false, 6.0f * f, 0.0f, f8, 1.0f, 0);
                                    break;
                                case 32:
                                    addHole(primProfileParams, true, 4.0f, 0.0f, f8, 1.0f, i);
                                    break;
                                default:
                                    addHole(primProfileParams, true, 3.0f, 0.0f, f8, 1.0f, i);
                                    break;
                            }
                        }
                    } else {
                        this.Profile.get(i10).z *= 3.0f;
                        i9 = i10 + 1;
                    }
                }
                break;
            case 5:
                float f9 = 6.0f * f * 0.5f;
                if (f6 != 0.0f) {
                    byte b4 = (byte) (primProfileParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK);
                    if (b4 == 32) {
                        f2 = (float) (Math.ceil((double) (f9 / 2.0f)) * 2.0d);
                        b = b4;
                    } else {
                        f2 = f9;
                        b = b4;
                    }
                } else {
                    f2 = f9;
                    b = 0;
                }
                genNGon(primProfileParams, (int) Math.floor((double) f2), 0.5f, 0.0f, 0.5f, 0);
                if (z) {
                    addCap(1);
                }
                if (!this.Open || f6 != 0.0f) {
                    addFace(0, this.Total, 0.0f, 32, false);
                } else {
                    addFace(0, this.Total - 1, 0.0f, 32, false);
                }
                if (f6 != 0.0f) {
                    switch (b) {
                        case 32:
                            addHole(primProfileParams, true, 2.0f, 0.5f, f6, 0.5f, i);
                            break;
                        case 48:
                            addHole(primProfileParams, true, 3.0f, 0.5f, f6, 0.5f, i);
                            break;
                        default:
                            addHole(primProfileParams, false, f2, 0.5f, f6, 0.5f, 0);
                            break;
                    }
                }
                if (f5 - f4 >= 1.0f) {
                    if (f6 == 0.0f) {
                        this.Open = false;
                        this.Profile.add(new LLVector3(this.Profile.get(0)));
                        this.Total++;
                        break;
                    }
                } else {
                    this.Open = true;
                    break;
                }
                break;
        }
        if (z) {
            addCap(2);
        }
        if (!this.Open) {
            return true;
        }
        addFace(this.Total - 1, 2, 0.5f, 8, true);
        if (f6 != 0.0f) {
            addFace(this.TotalOut - 1, 2, 0.5f, 16, true);
            return true;
        }
        addFace(this.Total - 2, 2, 0.5f, 16, true);
        return true;
    }
}
