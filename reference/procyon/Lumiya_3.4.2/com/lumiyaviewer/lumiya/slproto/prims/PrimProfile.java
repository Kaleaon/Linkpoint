// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class PrimProfile
{
    public static final int MIN_DETAIL_FACES = 6;
    private static final float[] tableScale;
    public boolean Concave;
    public boolean Dirty;
    public LLVector3[] EdgeCenters;
    public LLVector3[] EdgeNormals;
    public ArrayList<Face> Faces;
    public LLVector2[] Normals;
    public boolean Open;
    public ArrayList<LLVector3> Profile;
    public int Total;
    public int TotalOut;
    
    static {
        tableScale = new float[] { 1.0f, 1.0f, 1.0f, 0.5f, 0.707107f, 0.53f, 0.525f, 0.5f };
    }
    
    public PrimProfile() {
        this.Open = false;
        this.Concave = false;
        this.Dirty = true;
        this.TotalOut = 0;
        this.Total = 2;
        this.Profile = new ArrayList<LLVector3>();
        this.Faces = new ArrayList<Face>();
    }
    
    private Face addCap(final short n) {
        final Face e = new Face();
        e.Index = 0;
        e.Count = this.Total;
        e.ScaleU = 1.0f;
        e.Cap = true;
        e.FaceID = n;
        this.Faces.add(e);
        return e;
    }
    
    private Face addFace(final int index, final int count, final float scaleU, final short n, final boolean flat) {
        final Face e = new Face();
        e.Index = index;
        e.Count = count;
        e.ScaleU = scaleU;
        e.Flat = flat;
        e.Cap = false;
        e.FaceID = n;
        this.Faces.add(e);
        return e;
    }
    
    private Face addHole(final PrimProfileParams primProfileParams, final boolean b, final float n, final float n2, final float n3, final float n4, int i) {
        this.TotalOut = this.Total;
        this.genNGon(primProfileParams, (int)Math.floor(n), n2, -1.0f, n4, i);
        final Face addFace = this.addFace(this.TotalOut, this.Total - this.TotalOut, 0.0f, (short)4, b);
        final LLVector3[] array = new LLVector3[this.Total];
        for (i = this.TotalOut; i < this.Total; ++i) {
            (array[i] = new LLVector3(this.Profile.get(i))).mul(n3);
        }
        i = this.Total - 1;
        for (int j = this.TotalOut; j < this.Total; ++j, --i) {
            this.Profile.set(j, array[i]);
        }
        Face face;
        for (i = 0; i < this.Faces.size(); ++i) {
            if (this.Faces.get(i).Cap) {
                face = this.Faces.get(i);
                face.Count *= 2;
            }
        }
        return addFace;
    }
    
    private void genNGon(final PrimProfileParams primProfileParams, int i, float n, float n2, final float n3, final int n4) {
        final float begin = primProfileParams.Begin;
        final float end = primProfileParams.End;
        final float n5 = 1.0f / i;
        final float n6 = 6.2831855f * n5 * n3;
        final int round = Math.round(i / n3);
        float n7;
        if (round < 8) {
            n7 = PrimProfile.tableScale[round];
        }
        else {
            n7 = 0.5f;
        }
        final float n8 = (float)(Math.floor(i * begin) / (float)i);
        n2 = 6.2831855f * (n8 * n3 + n);
        LLVector3 e = new LLVector3((float)Math.cos(n2) * n7, (float)Math.sin(n2) * n7, n8);
        n = n8 + n5;
        n2 += n6;
        final LLVector3 llVector3 = new LLVector3((float)Math.cos(n2) * n7, (float)Math.sin(n2) * n7, n);
        final float n9 = (begin - n8) * i;
        if (n9 < 0.9999f) {
            this.Profile.add(LLVector3.lerp(e, llVector3, n9));
            final float n10 = n2;
            n2 = n;
            n = n10;
        }
        else {
            final float n11 = n;
            n = n2;
            n2 = n11;
        }
        while (n2 < end) {
            e = new LLVector3((float)Math.cos(n) * n7, (float)Math.sin(n) * n7, n2);
            if (this.Profile.size() > 0) {
                final LLVector3 llVector4 = this.Profile.get(this.Profile.size() - 1);
                for (int j = 0; j < n4; ++j) {
                    this.Profile.add(LLVector3.lerp(llVector4, e, 1.0f / (n4 + 1) * (j + 1)));
                }
            }
            this.Profile.add(e);
            n += n6;
            n2 += n5;
        }
        final LLVector3 llVector5 = new LLVector3((float)Math.cos(n) * n7, n7 * (float)Math.sin(n), n2);
        n = (end - (n2 - n5)) * i;
        if (n > 1.0E-4f) {
            final LLVector3 lerp = LLVector3.lerp(e, llVector5, n);
            if (this.Profile.size() > 0) {
                final LLVector3 llVector6 = this.Profile.get(this.Profile.size() - 1);
                for (i = 0; i < n4; ++i) {
                    this.Profile.add(LLVector3.lerp(llVector6, lerp, 1.0f / (n4 + 1) * (i + 1)));
                }
            }
            this.Profile.add(lerp);
        }
        if ((end - begin) * n3 < 0.99f) {
            this.Open = true;
            this.Concave = ((end - begin) * n3 > 0.5f);
            if (primProfileParams.Hollow <= 0.0f) {
                this.Profile.add(new LLVector3(0.0f, 0.0f, 0.0f));
            }
        }
        else {
            this.Open = false;
            this.Concave = false;
        }
        this.Total = this.Profile.size();
    }
    
    private static int getNumNGonPoints(final PrimProfileParams primProfileParams, int n, float n2, float end, final float n3, int n4) {
        final float begin = primProfileParams.Begin;
        end = primProfileParams.End;
        final float n5 = 1.0f / n;
        final float n6 = (float)(Math.floor(n * begin) / (float)n);
        n2 = n6 + n5;
        if ((begin - n6) * n < 0.9999f) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        while (n2 < end) {
            n2 += n5;
            ++n4;
        }
        int n7 = n4;
        if ((end - (n2 - n5)) * n > 1.0E-4f) {
            n7 = n4 + 1;
        }
        n = n7;
        if ((end - begin) * n3 < 0.99f) {
            n = n7;
            if (primProfileParams.Hollow <= 0.0f) {
                n = n7 + 1;
            }
        }
        return n;
    }
    
    public static int getNumPoints(final PrimProfileParams primProfileParams, final boolean b, float n, int n2, final boolean b2, int numNGonPoints) {
        float n3 = n;
        if (n < 0.0f) {
            n3 = 0.0f;
        }
        final float hollow = primProfileParams.Hollow;
        final int n4 = 0;
        switch (primProfileParams.CurveType & 0xF) {
            default: {
                n2 = n4;
                break;
            }
            case 1: {
                numNGonPoints = (n2 = getNumNGonPoints(primProfileParams, 4, -0.375f, 0.0f, 1.0f, n2));
                if (hollow != 0.0f) {
                    n2 = numNGonPoints * 2;
                    break;
                }
                break;
            }
            case 2:
            case 3:
            case 4: {
                numNGonPoints = (n2 = getNumNGonPoints(primProfileParams, 3, 0.0f, 0.0f, 1.0f, n2));
                if (hollow != 0.0f) {
                    n2 = numNGonPoints * 2;
                    break;
                }
                break;
            }
            case 0: {
                final float n5 = n = 6.0f * n3;
                if (hollow != 0.0f) {
                    n = n5;
                    if ((primProfileParams.CurveType & 0xFFFFFFF0) == 0x20) {
                        n = (float)(Math.ceil(n5 / 4.0f) * 4.0);
                    }
                }
                n2 = (int)n;
                if (b2) {
                    n2 = numNGonPoints;
                }
                numNGonPoints = (n2 = getNumNGonPoints(primProfileParams, n2, 0.0f, 0.0f, 1.0f, 0));
                if (hollow != 0.0f) {
                    n2 = numNGonPoints * 2;
                    break;
                }
                break;
            }
            case 5: {
                final float n6 = n = 6.0f * n3 * 0.5f;
                if (hollow != 0.0f) {
                    n = n6;
                    if ((primProfileParams.CurveType & 0xFFFFFFF0) == 0x20) {
                        n = (float)(Math.ceil(n6 / 2.0f) * 2.0);
                    }
                }
                n2 = (numNGonPoints = getNumNGonPoints(primProfileParams, (int)Math.floor(n), 0.5f, 0.0f, 0.5f, 0));
                if (hollow != 0.0f) {
                    numNGonPoints = n2 * 2;
                }
                int n7;
                if (primProfileParams.End - primProfileParams.Begin < 1.0f) {
                    n7 = 1;
                }
                else {
                    n7 = 0;
                }
                n2 = numNGonPoints;
                if (n7 != 0) {
                    break;
                }
                n2 = numNGonPoints;
                if (hollow == 0.0f) {
                    n2 = numNGonPoints + 1;
                    break;
                }
                break;
            }
        }
        return n2;
    }
    
    protected void genNormals(final PrimProfileParams primProfileParams) {
        final int size = this.Profile.size();
        int totalOut;
        if (this.TotalOut != 0) {
            totalOut = this.TotalOut;
        }
        else {
            totalOut = this.Total / 2;
        }
        this.EdgeNormals = new LLVector3[size * 2];
        this.EdgeCenters = new LLVector3[size * 2];
        this.Normals = new LLVector2[size];
        boolean b;
        if (primProfileParams.Hollow > 0.0f) {
            b = true;
        }
        else {
            b = false;
        }
        for (int i = 0; i < size; ++i) {
            this.Normals[i] = new LLVector2(this.Profile.get(i).x, this.Profile.get(i).y);
            if (b && i >= totalOut) {
                this.Normals[i].mul(-1.0f);
            }
            if (this.Normals[i].magVec() < 0.001) {
                int n;
                if (i - 1 >= 0) {
                    n = i - 1;
                }
                else {
                    n = size - 1;
                }
                int n2;
                if (n - 1 >= 0) {
                    n2 = n - 1;
                }
                else {
                    n2 = size - 1;
                }
                int n3;
                if (i + 1 < size) {
                    n3 = i + 1;
                }
                else {
                    n3 = 0;
                }
                int n4;
                if (n3 + 1 < size) {
                    n4 = n3 + 1;
                }
                else {
                    n4 = 0;
                }
                (this.Normals[i] = LLVector2.sum(new LLVector2(this.Profile.get(n).x + this.Profile.get(n).x - this.Profile.get(n2).x, this.Profile.get(n).y + this.Profile.get(n).y - this.Profile.get(n2).y), new LLVector2(this.Profile.get(n3).x + this.Profile.get(n3).x - this.Profile.get(n4).x, this.Profile.get(n3).y + this.Profile.get(n3).y - this.Profile.get(n4).y))).mul(0.5f);
            }
            this.Normals[i].normVec();
        }
        int n5;
        if (this.Concave) {
            n5 = 2;
        }
        else {
            n5 = 1;
        }
        for (int j = 0; j < n5; ++j) {
            for (int k = 0; k < this.Total; ++k) {
                final LLVector3 llVector3 = new LLVector3(this.Profile.get(k));
                llVector3.z = 0.0f;
                LLVector3 llVector4;
                if (this.Concave && j == 0 && k == (this.Total - 1) / 2) {
                    llVector4 = this.Profile.get(this.Total - 1);
                }
                else if (this.Concave && j == 1 && k == this.Total - 1) {
                    llVector4 = this.Profile.get((this.Total - 1) / 2);
                }
                else {
                    llVector4 = new LLVector3();
                    final LLVector3 llVector5 = new LLVector3();
                    int index = (k + 1) % this.Total;
                    while (llVector5.magVecSquared() < 1.0E-4f) {
                        final LLVector3 llVector6 = this.Profile.get(index);
                        llVector5.setSub(llVector6, llVector3);
                        final int n6 = (index + 1) % this.Total;
                        llVector4 = llVector6;
                        if ((index = n6) == k) {
                            llVector4 = llVector6;
                            break;
                        }
                    }
                }
                llVector4.z = 0.0f;
                final LLVector3 sub = LLVector3.sub(llVector4, llVector3);
                sub.setCross(LLVector3.z_axis);
                sub.normVec();
                this.EdgeNormals[j * size + k] = sub;
                this.EdgeCenters[j * size + k] = LLVector3.lerp(llVector3, llVector4, 0.5f);
            }
        }
    }
    
    public boolean generate(final PrimProfileParams primProfileParams, final boolean b, float n, final int n2, final boolean b2, int i) {
        if (!this.Dirty && (b2 ^ true)) {
            return false;
        }
        this.Dirty = false;
        float n3 = n;
        if (n < 0.0f) {
            n3 = 0.0f;
        }
        this.Profile.clear();
        this.Faces.clear();
        final float begin = primProfileParams.Begin;
        final float end = primProfileParams.End;
        final float hollow = primProfileParams.Hollow;
        if (begin > end - 0.01f) {
            return false;
        }
        final int n4 = 0;
        Label_0128: {
            switch (primProfileParams.CurveType & 0xF) {
                case 1: {
                    this.genNGon(primProfileParams, 4, -0.375f, 0.0f, 1.0f, n2);
                    if (b) {
                        this.addCap((short)1);
                    }
                    int j;
                    for (j = (int)Math.floor(4.0f * begin), i = 0; j < (int)Math.floor(4.0f * end + 0.999f); ++j, ++i) {
                        this.addFace((n2 + 1) * i, n2 + 2, 1.0f, (short)(32 << j), true);
                    }
                    LLVector3 llVector3;
                    for (i = 0; i < this.Profile.size(); ++i) {
                        llVector3 = this.Profile.get(i);
                        llVector3.z *= 4.0f;
                    }
                    if (hollow != 0.0f) {
                        switch (primProfileParams.CurveType & 0xFFFFFFF0) {
                            default: {
                                this.addHole(primProfileParams, true, 4.0f, -0.375f, hollow, 1.0f, n2);
                                break;
                            }
                            case 48: {
                                this.addHole(primProfileParams, true, 3.0f, -0.375f, hollow, 1.0f, n2);
                                break;
                            }
                            case 16: {
                                this.addHole(primProfileParams, false, 6.0f * n3, -0.375f, hollow, 1.0f, 0);
                                break;
                            }
                        }
                    }
                    if (b) {
                        this.Faces.get(0).Count = this.Total;
                        break;
                    }
                    break;
                }
                case 2:
                case 3:
                case 4: {
                    this.genNGon(primProfileParams, 3, 0.0f, 0.0f, 1.0f, n2);
                    LLVector3 llVector4;
                    for (i = 0; i < this.Profile.size(); ++i) {
                        llVector4 = this.Profile.get(i);
                        llVector4.z *= 3.0f;
                    }
                    if (b) {
                        this.addCap((short)1);
                    }
                    final int n5 = (int)Math.floor(3.0f * begin);
                    i = n4;
                    for (int k = n5; k < (int)Math.floor(3.0f * end + 0.999f); ++k, ++i) {
                        this.addFace(i * (n2 + 1), n2 + 2, 1.0f, (short)(32 << k), true);
                    }
                    if (hollow == 0.0f) {
                        break;
                    }
                    n = hollow / 2.0f;
                    switch (primProfileParams.CurveType & 0xFFFFFFF0) {
                        default: {
                            this.addHole(primProfileParams, true, 3.0f, 0.0f, n, 1.0f, n2);
                            break Label_0128;
                        }
                        case 16: {
                            this.addHole(primProfileParams, false, 6.0f * n3, 0.0f, n, 1.0f, 0);
                            break Label_0128;
                        }
                        case 32: {
                            this.addHole(primProfileParams, true, 4.0f, 0.0f, n, 1.0f, n2);
                            break Label_0128;
                        }
                    }
                    break;
                }
                case 0: {
                    n = 6.0f * n3;
                    int n6;
                    if (hollow != 0.0f) {
                        n6 = (byte)(primProfileParams.CurveType & 0xFFFFFFF0);
                        if (n6 == 32) {
                            n = (float)(Math.ceil(n / 4.0f) * 4.0);
                        }
                    }
                    else {
                        n6 = 0;
                    }
                    int n7 = (int)n;
                    if (b2) {
                        n7 = i;
                    }
                    this.genNGon(primProfileParams, n7, 0.0f, 0.0f, 1.0f, 0);
                    if (b) {
                        this.addCap((short)1);
                    }
                    if (this.Open && hollow == 0.0f) {
                        this.addFace(0, this.Total - 1, 0.0f, (short)32, false);
                    }
                    else {
                        this.addFace(0, this.Total, 0.0f, (short)32, false);
                    }
                    if (hollow == 0.0f) {
                        break;
                    }
                    switch (n6) {
                        default: {
                            this.addHole(primProfileParams, false, n, 0.0f, hollow, 1.0f, 0);
                            break Label_0128;
                        }
                        case 32: {
                            this.addHole(primProfileParams, true, 4.0f, 0.0f, hollow, 1.0f, n2);
                            break Label_0128;
                        }
                        case 48: {
                            this.addHole(primProfileParams, true, 3.0f, 0.0f, hollow, 1.0f, n2);
                            break Label_0128;
                        }
                    }
                    break;
                }
                case 5: {
                    n = 6.0f * n3 * 0.5f;
                    if (hollow != 0.0f) {
                        i = (byte)(primProfileParams.CurveType & 0xFFFFFFF0);
                        if (i == 32) {
                            n = (float)(Math.ceil(n / 2.0f) * 2.0);
                        }
                    }
                    else {
                        i = 0;
                    }
                    this.genNGon(primProfileParams, (int)Math.floor(n), 0.5f, 0.0f, 0.5f, 0);
                    if (b) {
                        this.addCap((short)1);
                    }
                    if (this.Open && hollow == 0.0f) {
                        this.addFace(0, this.Total - 1, 0.0f, (short)32, false);
                    }
                    else {
                        this.addFace(0, this.Total, 0.0f, (short)32, false);
                    }
                    if (hollow != 0.0f) {
                        switch (i) {
                            default: {
                                this.addHole(primProfileParams, false, n, 0.5f, hollow, 0.5f, 0);
                                break;
                            }
                            case 32: {
                                this.addHole(primProfileParams, true, 2.0f, 0.5f, hollow, 0.5f, n2);
                                break;
                            }
                            case 48: {
                                this.addHole(primProfileParams, true, 3.0f, 0.5f, hollow, 0.5f, n2);
                                break;
                            }
                        }
                    }
                    if (end - begin < 1.0f) {
                        this.Open = true;
                        break;
                    }
                    if (hollow == 0.0f) {
                        this.Open = false;
                        this.Profile.add(new LLVector3(this.Profile.get(0)));
                        ++this.Total;
                        break;
                    }
                    break;
                }
            }
        }
        if (b) {
            this.addCap((short)2);
        }
        if (this.Open) {
            this.addFace(this.Total - 1, 2, 0.5f, (short)8, true);
            if (hollow != 0.0f) {
                this.addFace(this.TotalOut - 1, 2, 0.5f, (short)16, true);
            }
            else {
                this.addFace(this.Total - 2, 2, 0.5f, (short)16, true);
            }
        }
        return true;
    }
    
    public static class Face
    {
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
}
