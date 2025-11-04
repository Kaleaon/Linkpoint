// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.types.VertexArray;
import com.lumiyaviewer.lumiya.slproto.types.Vector2Array;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class PrimVolumeFace
{
    public static final int BOTTOM_MASK = 1024;
    public static final int CAP_MASK = 2;
    public static final int END_MASK = 4;
    public static final int FLAT_MASK = 256;
    public static final int HOLLOW_MASK = 64;
    public static final int INNER_MASK = 16;
    public static final int OPEN_MASK = 128;
    public static final int OUTER_MASK = 32;
    public static final int SIDE_MASK = 8;
    public static final int SINGLE_MASK = 1;
    public static final int TOP_MASK = 512;
    public int BeginS;
    public int BeginT;
    public LLVector3 Center;
    public int[] Edge;
    public LLVector3[] Extents;
    public int ID;
    public short[] Indices;
    public Vector3Array Normals;
    public int NumIndices;
    public int NumS;
    public int NumT;
    public int NumVertices;
    public Vector3Array Positions;
    public LLVector2[] TexCoordExtents;
    public Vector2Array TexCoords;
    public int TypeMask;
    public VertexArray vertexArray;
    
    public PrimVolumeFace() {
        this.TexCoordExtents = new LLVector2[] { new LLVector2(), new LLVector2() };
        this.Extents = new LLVector3[] { new LLVector3(), new LLVector3() };
    }
    
    private boolean createCap(final PrimVolume primVolume) {
        if ((this.TypeMask & 0x40) == 0x0 && (this.TypeMask & 0x80) == 0x0 && primVolume.volumeParams.PathParams.Begin == 0.0f && primVolume.volumeParams.PathParams.End == 1.0f && primVolume.getProfileType() == 1 && primVolume.getPathType() == 16) {
            return this.createUnCutCubeCap(primVolume);
        }
        final Vector3Array mesh = primVolume.Mesh;
        final ArrayList<LLVector3> profile = primVolume.Profile.Profile;
        final int size = profile.size();
        final int n = (profile.size() - 2) * 3;
        if ((this.TypeMask & 0x40) == 0x0 && (this.TypeMask & 0x80) == 0x0) {
            this.resizeVertices(size + 1);
            this.resizeIndices(n + 3);
        }
        else {
            this.resizeVertices(size);
            this.resizeIndices(n);
        }
        final int total = primVolume.Profile.Total;
        final int size2 = primVolume.Path.Path.size();
        int beginS;
        if ((this.TypeMask & 0x200) != 0x0) {
            beginS = total * (size2 - 1);
        }
        else {
            beginS = this.BeginS;
        }
        final LLVector2 llVector2 = new LLVector2();
        final LLVector2 llVector3 = new LLVector2();
        final LLVector3 llVector4 = this.Extents[0];
        final LLVector3 llVector5 = this.Extents[1];
        final Vector2Array texCoords = this.TexCoords;
        final Vector3Array positions = this.Positions;
        final Vector3Array normals = this.Normals;
        for (int i = 0; i < size; ++i) {
            if ((this.TypeMask & 0x200) != 0x0) {
                texCoords.set(i, 0.5f + ((LLVector3)profile.get(i)).x, ((LLVector3)profile.get(i)).y + 0.5f);
            }
            else {
                texCoords.set(i, 0.5f + ((LLVector3)profile.get(i)).x, 0.5f - ((LLVector3)profile.get(i)).y);
            }
            positions.set(i, mesh, i + beginS);
            if (i == 0) {
                positions.get(i, llVector4);
                positions.get(i, llVector5);
                texCoords.get(i, llVector2);
                texCoords.get(i, llVector3);
            }
            else {
                positions.minMaxVector(i, llVector4, llVector5);
                texCoords.minMaxVector(i, llVector2, llVector3);
            }
        }
        (this.Center = new LLVector3(llVector4)).add(llVector5);
        this.Center.mul(0.5f);
        final LLVector2 sum = LLVector2.sum(llVector2, llVector3);
        sum.mul(0.5f);
        final LLVector3 llVector6 = new LLVector3(this.Center);
        final LLVector3 llVector7 = new LLVector3(this.Center);
        positions.subFromVector(llVector6, 0);
        positions.subFromVector(llVector7, 1);
        LLVector3 llVector8;
        if ((this.TypeMask & 0x200) != 0x0) {
            llVector8 = LLVector3.cross(llVector6, llVector7);
        }
        else {
            llVector8 = LLVector3.cross(llVector7, llVector6);
        }
        llVector8.normVec();
        int n2;
        if ((this.TypeMask & 0x40) == 0x0 && (this.TypeMask & 0x80) == 0x0) {
            positions.set(size, this.Center);
            texCoords.set(size, sum.x, sum.y);
            n2 = size + 1;
        }
        else {
            n2 = size;
        }
        normals.fill(0, n2, llVector8);
        if ((this.TypeMask & 0x40) != 0x0) {
            if ((this.TypeMask & 0x200) != 0x0) {
                int index = 0;
                int index2 = n2 - 1;
                int n3 = 0;
                while (index2 - index > 1) {
                    final LLVector3 llVector9 = new LLVector3(profile.get(index));
                    final LLVector3 llVector10 = new LLVector3(profile.get(index2));
                    final LLVector3 llVector11 = new LLVector3(profile.get(index + 1));
                    final LLVector3 llVector12 = new LLVector3(profile.get(index2 - 1));
                    llVector9.z = 0.0f;
                    llVector10.z = 0.0f;
                    llVector11.z = 0.0f;
                    llVector12.z = 0.0f;
                    final float x = llVector9.x;
                    final float y = llVector11.y;
                    final float x2 = llVector11.x;
                    final float y2 = llVector9.y;
                    final float x3 = llVector11.x;
                    final float y3 = llVector10.y;
                    final float x4 = llVector10.x;
                    final float y4 = llVector11.y;
                    final float x5 = llVector10.x;
                    final float y5 = llVector9.y;
                    final float x6 = llVector9.x;
                    final float y6 = llVector10.y;
                    final float x7 = llVector9.x;
                    final float y7 = llVector12.y;
                    final float x8 = llVector12.x;
                    final float y8 = llVector9.y;
                    final float x9 = llVector12.x;
                    final float y9 = llVector11.y;
                    final float x10 = llVector11.x;
                    final float y10 = llVector12.y;
                    final float x11 = llVector11.x;
                    final float y11 = llVector9.y;
                    final float x12 = llVector9.x;
                    final float y12 = llVector11.y;
                    final float x13 = llVector10.x;
                    final float y13 = llVector9.y;
                    final float x14 = llVector9.x;
                    final float y14 = llVector10.y;
                    final float x15 = llVector9.x;
                    final float y15 = llVector12.y;
                    final float x16 = llVector12.x;
                    final float y16 = llVector9.y;
                    final float x17 = llVector12.x;
                    final float y17 = llVector10.y;
                    final float x18 = llVector10.x;
                    final float y18 = llVector12.y;
                    final float x19 = llVector10.x;
                    final float y19 = llVector11.y;
                    final float x20 = llVector11.x;
                    final float y20 = llVector10.y;
                    final float x21 = llVector11.x;
                    final float y21 = llVector12.y;
                    final float x22 = llVector12.x;
                    final float y22 = llVector11.y;
                    final float x23 = llVector12.x;
                    final float y23 = llVector10.y;
                    final float x24 = llVector10.x;
                    final float y24 = llVector12.y;
                    boolean b = true;
                    final int n4 = 1;
                    if (x * y - x2 * y2 + (x3 * y3 - x4 * y4) + (x5 * y5 - x6 * y6) < 0.0f) {
                        b = false;
                    }
                    boolean b2 = b;
                    if (x19 * y19 - x20 * y20 + (x21 * y21 - x22 * y22) + (x23 * y23 - x24 * y24) < 0.0f) {
                        b2 = false;
                    }
                    int n5 = n4;
                    if (x13 * y13 - x14 * y14 + (x15 * y15 - x16 * y16) + (x17 * y17 - x18 * y18) < 0.0f) {
                        n5 = 0;
                    }
                    if (x7 * y7 - x8 * y8 + (x9 * y9 - x10 * y10) + (x11 * y11 - x12 * y12) < 0.0f) {
                        n5 = 0;
                    }
                    int n6;
                    if (!b2) {
                        n6 = 0;
                    }
                    else if (n5 == 0) {
                        n6 = 1;
                    }
                    else if (LLVector3.sub(llVector9, llVector11).magVecSquared() < LLVector3.sub(llVector10, llVector12).magVecSquared()) {
                        n6 = 1;
                    }
                    else {
                        n6 = 0;
                    }
                    int n9;
                    if (n6 != 0) {
                        final short[] indices = this.Indices;
                        final int n7 = n3 + 1;
                        indices[n3] = (short)index;
                        final short[] indices2 = this.Indices;
                        final int n8 = n7 + 1;
                        indices2[n7] = (short)(index + 1);
                        final short[] indices3 = this.Indices;
                        n9 = n8 + 1;
                        indices3[n8] = (short)index2;
                        ++index;
                    }
                    else {
                        final short[] indices4 = this.Indices;
                        final int n10 = n3 + 1;
                        indices4[n3] = (short)index;
                        final short[] indices5 = this.Indices;
                        final int n11 = n10 + 1;
                        indices5[n10] = (short)(index2 - 1);
                        final short[] indices6 = this.Indices;
                        n9 = n11 + 1;
                        indices6[n11] = (short)index2;
                        --index2;
                    }
                    n3 = n9;
                }
            }
            else {
                int index3 = 0;
                int index4 = n2 - 1;
                int n12 = 0;
                while (index4 - index3 > 1) {
                    final LLVector3 llVector13 = new LLVector3(profile.get(index3));
                    final LLVector3 llVector14 = new LLVector3(profile.get(index4));
                    final LLVector3 llVector15 = new LLVector3(profile.get(index3 + 1));
                    final LLVector3 llVector16 = new LLVector3(profile.get(index4 - 1));
                    llVector13.z = 0.0f;
                    llVector14.z = 0.0f;
                    llVector15.z = 0.0f;
                    llVector16.z = 0.0f;
                    final float x25 = llVector13.x;
                    final float y25 = llVector15.y;
                    final float x26 = llVector15.x;
                    final float y26 = llVector13.y;
                    final float x27 = llVector15.x;
                    final float y27 = llVector14.y;
                    final float x28 = llVector14.x;
                    final float y28 = llVector15.y;
                    final float x29 = llVector14.x;
                    final float y29 = llVector13.y;
                    final float x30 = llVector13.x;
                    final float y30 = llVector14.y;
                    final float x31 = llVector13.x;
                    final float y31 = llVector16.y;
                    final float x32 = llVector16.x;
                    final float y32 = llVector13.y;
                    final float x33 = llVector16.x;
                    final float y33 = llVector15.y;
                    final float x34 = llVector15.x;
                    final float y34 = llVector16.y;
                    final float x35 = llVector15.x;
                    final float y35 = llVector13.y;
                    final float x36 = llVector13.x;
                    final float y36 = llVector15.y;
                    final float x37 = llVector14.x;
                    final float y37 = llVector13.y;
                    final float x38 = llVector13.x;
                    final float y38 = llVector14.y;
                    final float x39 = llVector13.x;
                    final float y39 = llVector16.y;
                    final float x40 = llVector16.x;
                    final float y40 = llVector13.y;
                    final float x41 = llVector16.x;
                    final float y41 = llVector14.y;
                    final float x42 = llVector14.x;
                    final float y42 = llVector16.y;
                    final float x43 = llVector14.x;
                    final float y43 = llVector15.y;
                    final float x44 = llVector15.x;
                    final float y44 = llVector14.y;
                    final float x45 = llVector15.x;
                    final float y45 = llVector16.y;
                    final float x46 = llVector16.x;
                    final float y46 = llVector15.y;
                    final float x47 = llVector16.x;
                    final float y47 = llVector14.y;
                    final float x48 = llVector14.x;
                    final float y48 = llVector16.y;
                    boolean b3 = true;
                    final int n13 = 1;
                    if (x25 * y25 - x26 * y26 + (x27 * y27 - x28 * y28) + (x29 * y29 - x30 * y30) < 0.0f) {
                        b3 = false;
                    }
                    boolean b4 = b3;
                    if (x43 * y43 - x44 * y44 + (x45 * y45 - x46 * y46) + (x47 * y47 - x48 * y48) < 0.0f) {
                        b4 = false;
                    }
                    int n14 = n13;
                    if (x37 * y37 - x38 * y38 + (x39 * y39 - x40 * y40) + (x41 * y41 - x42 * y42) < 0.0f) {
                        n14 = 0;
                    }
                    if (x31 * y31 - x32 * y32 + (x33 * y33 - x34 * y34) + (x35 * y35 - x36 * y36) < 0.0f) {
                        n14 = 0;
                    }
                    int n15;
                    if (!b4) {
                        n15 = 0;
                    }
                    else if (n14 == 0) {
                        n15 = 1;
                    }
                    else if (LLVector3.sub(llVector13, llVector15).magVecSquared() < LLVector3.sub(llVector14, llVector16).magVecSquared()) {
                        n15 = 1;
                    }
                    else {
                        n15 = 0;
                    }
                    int n18;
                    if (n15 != 0) {
                        final short[] indices7 = this.Indices;
                        final int n16 = n12 + 1;
                        indices7[n12] = (short)index3;
                        final short[] indices8 = this.Indices;
                        final int n17 = n16 + 1;
                        indices8[n16] = (short)index4;
                        final short[] indices9 = this.Indices;
                        n18 = n17 + 1;
                        indices9[n17] = (short)(index3 + 1);
                        ++index3;
                    }
                    else {
                        final short[] indices10 = this.Indices;
                        final int n19 = n12 + 1;
                        indices10[n12] = (short)index3;
                        final short[] indices11 = this.Indices;
                        final int n20 = n19 + 1;
                        indices11[n19] = (short)index4;
                        final short[] indices12 = this.Indices;
                        n18 = n20 + 1;
                        indices12[n20] = (short)(index4 - 1);
                        --index4;
                    }
                    n12 = n18;
                }
            }
        }
        else {
            int n21 = 2;
            int n22 = 1;
            if ((this.TypeMask & 0x200) != 0x0) {
                n21 = 1;
                n22 = 2;
            }
            for (int j = 0; j < n2 - 2; ++j) {
                this.Indices[j * 3] = (short)(n2 - 1);
                this.Indices[j * 3 + n21] = (short)j;
                this.Indices[j * 3 + n22] = (short)(j + 1);
            }
        }
        return true;
    }
    
    private boolean createSide(final PrimVolume primVolume) {
        final boolean b = (this.TypeMask & 0x100) != 0x0;
        final byte sculptType = primVolume.volumeParams.SculptType;
        final byte b2 = (byte)(sculptType & 0x7);
        int n;
        if ((sculptType & 0x40) != 0x0) {
            n = 1;
        }
        else {
            n = 0;
        }
        boolean b3;
        if ((sculptType & 0xFFFFFF80) != 0x0) {
            b3 = true;
        }
        else {
            b3 = false;
        }
        int n2;
        if (n != 0) {
            n2 = ((b3 ? 1 : 0) ^ 0x1);
        }
        else {
            n2 = (b3 ? 1 : 0);
        }
        final Vector3Array mesh = primVolume.Mesh;
        final ArrayList<LLVector3> profile = primVolume.Profile.Profile;
        final ArrayList<PrimPath.PathPoint> path = primVolume.Path.Path;
        final int total = primVolume.Profile.Total;
        final int numS = this.NumS;
        final int numT = this.NumT;
        final int n3 = (this.NumS - 1) * (this.NumT - 1) * 6;
        this.resizeVertices(numS * numT);
        this.resizeIndices(n3);
        this.Edge = new int[n3];
        final Vector3Array positions = this.Positions;
        final Vector3Array normals = this.Normals;
        final Vector2Array texCoords = this.TexCoords;
        final int n4 = (int)Math.floor(profile.get(this.BeginS).z);
        int numS2;
        if ((this.TypeMask & 0x10) != 0x0 && (this.TypeMask & 0x100) != 0x0 && this.NumS > 2) {
            numS2 = this.NumS / 2;
        }
        else {
            numS2 = this.NumS;
        }
        int i = this.BeginT;
        int n5 = 0;
        while (i < this.BeginT + this.NumT) {
            final float texT = ((PrimPath.PathPoint)path.get(i)).TexT;
            for (int j = 0; j < numS2; ++j) {
                float z;
                if ((this.TypeMask & 0x4) != 0x0 || this.BeginS + j >= profile.size()) {
                    if (j != 0) {
                        z = 1.0f;
                    }
                    else {
                        z = 0.0f;
                    }
                }
                else if (!b) {
                    z = profile.get(this.BeginS + j).z;
                }
                else {
                    z = profile.get(this.BeginS + j).z - n4;
                }
                float n6 = z;
                if (n2 != 0) {
                    n6 = 1.0f - z;
                }
                int n7;
                if (this.BeginS + j >= total) {
                    n7 = this.BeginS + j + (i - 1) * total;
                }
                else {
                    n7 = this.BeginS + j + total * i;
                }
                positions.set(n5, mesh, n7);
                texCoords.set(n5, n6, texT);
                ++n5;
                if ((this.TypeMask & 0x10) != 0x0 && (this.TypeMask & 0x100) != 0x0 && this.NumS > 2 && j > 0) {
                    positions.set(n5, mesh, n7);
                    texCoords.set(n5, n6, texT);
                    ++n5;
                }
            }
            if ((this.TypeMask & 0x10) != 0x0 && (this.TypeMask & 0x100) != 0x0 && this.NumS > 2) {
                int n8;
                if ((this.TypeMask & 0x80) != 0x0) {
                    n8 = numS2 - 1;
                }
                else {
                    n8 = 0;
                }
                final int beginS = this.BeginS;
                float n9;
                if (this.BeginS + n8 < profile.size()) {
                    n9 = profile.get(n8 + this.BeginS).z - n4;
                }
                else if (n8 != 0) {
                    n9 = 1.0f;
                }
                else {
                    n9 = 0.0f;
                }
                positions.set(n5, mesh, beginS + n8 + total * i);
                texCoords.set(n5, n9, texT);
                ++n5;
            }
            ++i;
        }
        final LLVector3 llVector3 = this.Extents[0];
        final LLVector3 llVector4 = this.Extents[1];
        positions.get(0, llVector3);
        positions.get(0, llVector4);
        positions.minMaxVector(llVector3, llVector4);
        (this.Center = new LLVector3(llVector3)).add(llVector4);
        this.Center.mul(0.5f);
        int n10 = 0;
        int n11 = 0;
        final boolean b4 = (this.TypeMask & 0x100) != 0x0;
        for (int k = 0; k < this.NumT - 1; ++k) {
            int n27;
            for (int l = 0; l < this.NumS - 1; ++l, n11 = n27) {
                final short[] indices = this.Indices;
                final int n12 = n10 + 1;
                indices[n10] = (short)(this.NumS * k + l);
                final short[] indices2 = this.Indices;
                final int n13 = n12 + 1;
                indices2[n12] = (short)(l + 1 + this.NumS * (k + 1));
                final short[] indices3 = this.Indices;
                final int n14 = n13 + 1;
                indices3[n13] = (short)(this.NumS * (k + 1) + l);
                final short[] indices4 = this.Indices;
                final int n15 = n14 + 1;
                indices4[n14] = (short)(this.NumS * k + l);
                final short[] indices5 = this.Indices;
                final int n16 = n15 + 1;
                indices5[n15] = (short)(l + 1 + this.NumS * k);
                final short[] indices6 = this.Indices;
                n10 = n16 + 1;
                indices6[n16] = (short)(l + 1 + this.NumS * (k + 1));
                final int[] edge = this.Edge;
                final int n17 = n11 + 1;
                edge[n11] = (this.NumS - 1) * 2 * k + l * 2 + 1;
                int n18;
                if (k < this.NumT - 2) {
                    this.Edge[n17] = (this.NumS - 1) * 2 * (k + 1) + l * 2 + 1;
                    n18 = n17 + 1;
                }
                else if (this.NumT <= 3 || primVolume.Path.Open) {
                    this.Edge[n17] = -1;
                    n18 = n17 + 1;
                }
                else {
                    this.Edge[n17] = l * 2 + 1;
                    n18 = n17 + 1;
                }
                if (l > 0) {
                    this.Edge[n18] = (this.NumS - 1) * 2 * k + l * 2 - 1;
                    ++n18;
                }
                else if (b4 || primVolume.Path.Open) {
                    this.Edge[n18] = -1;
                    ++n18;
                }
                else {
                    this.Edge[n18] = (this.NumS - 1) * 2 * k + (this.NumS - 2) * 2 + 1;
                    ++n18;
                }
                int n20;
                if (k > 0) {
                    final int[] edge2 = this.Edge;
                    final int n19 = n18 + 1;
                    edge2[n18] = (this.NumS - 1) * 2 * (k - 1) + l * 2;
                    n20 = n19;
                }
                else if (this.NumT <= 3 || primVolume.Path.Open) {
                    final int[] edge3 = this.Edge;
                    final int n21 = n18 + 1;
                    edge3[n18] = -1;
                    n20 = n21;
                }
                else {
                    final int[] edge4 = this.Edge;
                    final int n22 = n18 + 1;
                    edge4[n18] = (this.NumS - 1) * 2 * (this.NumT - 2) + l * 2;
                    n20 = n22;
                }
                int n24;
                if (l < this.NumS - 2) {
                    final int[] edge5 = this.Edge;
                    final int n23 = n20 + 1;
                    edge5[n20] = (this.NumS - 1) * 2 * k + (l + 1) * 2;
                    n24 = n23;
                }
                else if (b4 || primVolume.Path.Open) {
                    final int[] edge6 = this.Edge;
                    final int n25 = n20 + 1;
                    edge6[n20] = -1;
                    n24 = n25;
                }
                else {
                    final int[] edge7 = this.Edge;
                    final int n26 = n20 + 1;
                    edge7[n20] = (this.NumS - 1) * 2 * k;
                    n24 = n26;
                }
                final int[] edge8 = this.Edge;
                n27 = n24 + 1;
                edge8[n24] = (this.NumS - 1) * 2 * k + l * 2;
            }
        }
        this.Normals.clear();
        final LLVector3[] array = new LLVector3[3];
        final short[] array2 = new short[3];
        for (int n28 = 0; n28 < 3; ++n28) {
            array[n28] = new LLVector3();
        }
        final LLVector3 llVector5 = new LLVector3();
        final LLVector3 cross = new LLVector3();
        for (int n29 = 0; n29 < this.NumIndices / 3; ++n29) {
            for (int n30 = 0; n30 < 3; ++n30) {
                positions.get(array2[n30] = this.Indices[n29 * 3 + n30], array[n30]);
            }
            llVector5.setSub(array[0], array[1]);
            cross.setSub(array[0], array[2]);
            llVector5.setCross(cross);
            for (int n31 = 0; n31 < 3; ++n31) {
                this.Normals.add(array2[n31], llVector5);
            }
            this.Normals.add(array2[(n29 & 0x1) + 1], llVector5);
        }
        final LLVector3 llVector6 = new LLVector3();
        final LLVector3 llVector7 = new LLVector3();
        final LLVector3 llVector8 = new LLVector3();
        positions.get(0, llVector7);
        positions.get(this.NumS * (this.NumT - 2), llVector8);
        llVector6.setSub(llVector7, llVector8);
        final boolean b5 = llVector6.dot(llVector6) < 1.0E-6f;
        positions.get(this.NumS - 1, llVector7);
        positions.get(this.NumS * (this.NumT - 2) + this.NumS - 1, llVector8);
        llVector6.setSub(llVector7, llVector8);
        final boolean b6 = llVector6.dot(llVector6) < 1.0E-6f;
        if (b2 == 0) {
            if (!primVolume.Path.Open) {
                for (int n32 = 0; n32 < this.NumS; ++n32) {
                    normals.setAdd(n32, this.NumS * (this.NumT - 1) + n32);
                }
            }
            if (!primVolume.Path.Open && (b5 ^ true)) {
                for (int n33 = 0; n33 < this.NumT; ++n33) {
                    normals.setAdd(this.NumS * n33, this.NumS * n33 + this.NumS - 1);
                }
            }
            if (primVolume.getPathType() == 32 && (primVolume.getProfileType() & 0xF) == 0x5) {
                if (b5) {
                    for (int n34 = 0; n34 < this.NumT; ++n34) {
                        normals.set(this.NumS * n34, 1.0f, 0.0f, 0.0f);
                    }
                }
                if (b6) {
                    for (int n35 = 0; n35 < this.NumT; ++n35) {
                        normals.set(this.NumS * n35 + this.NumS - 1, -1.0f, 0.0f, 0.0f);
                    }
                }
            }
        }
        else {
            int n36;
            if (b2 == 1) {
                n36 = 1;
            }
            else {
                n36 = 0;
            }
            final boolean b7 = b2 == 1 || b2 == 2 || b2 == 4;
            int n37;
            if (b2 == 2) {
                n37 = 1;
            }
            else {
                n37 = 0;
            }
            if (n36 != 0) {
                final LLVector3 llVector9 = new LLVector3();
                for (int n38 = 0; n38 < this.NumS; ++n38) {
                    normals.addToVector(n38, llVector9);
                }
                for (int n39 = 0; n39 < this.NumS; ++n39) {
                    normals.set(n39, llVector9);
                }
                llVector9.set(0.0f, 0.0f, 0.0f);
                for (int n40 = 0; n40 < this.NumS; ++n40) {
                    normals.addToVector(this.NumS * (this.NumT - 1) + n40, llVector9);
                }
                for (int n41 = 0; n41 < this.NumS; ++n41) {
                    normals.set(this.NumS * (this.NumT - 1) + n41, llVector9);
                }
            }
            if (b7) {
                for (int n42 = 0; n42 < this.NumT; ++n42) {
                    normals.setAdd(this.NumS * n42, this.NumS * n42 + this.NumS - 1);
                }
            }
            if (n37 != 0) {
                for (int n43 = 0; n43 < this.NumS; ++n43) {
                    normals.setAdd(n43, this.NumS * (this.NumT - 1) + n43);
                }
            }
        }
        return true;
    }
    
    private boolean createUnCutCubeCap(final PrimVolume primVolume) {
        final Vector3Array mesh = primVolume.Mesh;
        final ArrayList<LLVector3> profile = primVolume.Profile.Profile;
        final int total = primVolume.Profile.Total;
        final int size = primVolume.Path.Path.size();
        final int n = (profile.size() - 1) / 4;
        final LLVector3 llVector3 = this.Extents[0];
        final LLVector3 llVector4 = this.Extents[1];
        int beginS;
        if ((this.TypeMask & 0x200) != 0x0) {
            beginS = total * (size - 1);
        }
        else {
            beginS = this.BeginS;
        }
        final VertexArray vertexArray = new VertexArray(4);
        final LLVector3 llVector5 = new LLVector3();
        final Vector3Array vertices = vertexArray.getVertices();
        final Vector2Array texCoords = vertexArray.getTexCoords();
        for (int i = 0; i < 4; ++i) {
            vertices.set(i, mesh, n * i + beginS);
            texCoords.set(i, 0.5f + ((LLVector3)profile.get(n * i)).x, 0.5f - ((LLVector3)profile.get(n * i)).y);
        }
        final LLVector3 cross = new LLVector3();
        vertices.getSub(1, 0, llVector5);
        vertices.getSub(2, 1, cross);
        llVector5.setCross(cross);
        llVector5.normVec();
        if ((this.TypeMask & 0x200) == 0x0) {
            llVector5.mul(-1.0f);
        }
        else {
            texCoords.swap(0, 3);
            texCoords.swap(1, 2);
        }
        this.resizeVertices((n + 1) * (n + 1));
        final Vector3Array positions = this.Positions;
        int n2 = 0;
        final LLVector3 llVector6 = new LLVector3();
        final LLVector3 llVector7 = new LLVector3();
        final LLVector2 llVector8 = new LLVector2();
        final LLVector2 llVector9 = new LLVector2();
        for (int j = 0; j < n + 1; ++j) {
            for (int k = 0; k < n + 1; ++k) {
                this.vertexArray.LerpPlanarVertex(n2, vertexArray, 0, vertexArray, 1, vertexArray, 3, j / (float)n, k / (float)n, llVector6, llVector7, llVector8, llVector9);
                this.vertexArray.getNormals().set(n2, llVector5);
                if (j == 0 && k == 0) {
                    positions.get(n2, llVector3);
                    positions.get(n2, llVector4);
                }
                else {
                    positions.minMaxVector(n2, llVector3, llVector4);
                }
                ++n2;
            }
        }
        (this.Center = new LLVector3(llVector3)).add(llVector4);
        this.Center.mul(0.5f);
        this.resizeIndices(n * n * 6);
        final short[] indices = this.Indices;
        final short[] array = { 0, 1, (short)(n + 1 + 1), (short)(n + 1 + 1), (short)(n + 1), 0 };
        int n3 = 0;
        for (int l = 0; l < n; ++l) {
            for (int n4 = 0; n4 < n; ++n4) {
                if ((this.TypeMask & 0x200) != 0x0) {
                    int n5 = 5;
                    int n6 = n3;
                    while (true) {
                        n3 = n6;
                        if (n5 < 0) {
                            break;
                        }
                        indices[n6] = (short)((n + 1) * n4 + l + array[n5]);
                        --n5;
                        ++n6;
                    }
                }
                else {
                    int n7 = 0;
                    int n8 = n3;
                    while (true) {
                        n3 = n8;
                        if (n7 >= 6) {
                            break;
                        }
                        indices[n8] = (short)((n + 1) * n4 + l + array[n7]);
                        ++n7;
                        ++n8;
                    }
                }
            }
        }
        return true;
    }
    
    private void resizeIndices(final int numIndices) {
        if (numIndices != this.NumIndices) {
            if (numIndices != 0) {
                this.Indices = new short[numIndices];
            }
            else {
                this.Indices = null;
            }
            this.NumIndices = numIndices;
        }
    }
    
    private void resizeVertices(final int numVertices) {
        if (this.NumVertices != numVertices) {
            if (numVertices != 0) {
                this.vertexArray = new VertexArray(numVertices);
                this.Positions = this.vertexArray.getVertices();
                this.Normals = this.vertexArray.getNormals();
                this.TexCoords = this.vertexArray.getTexCoords();
            }
            else {
                this.Positions = null;
                this.Normals = null;
                this.TexCoords = null;
                this.vertexArray = null;
            }
            this.NumVertices = numVertices;
        }
    }
    
    public boolean create(final PrimVolume primVolume) {
        boolean cap;
        if ((this.TypeMask & 0x2) != 0x0) {
            cap = this.createCap(primVolume);
        }
        else {
            cap = (((this.TypeMask & 0x4) != 0x0 || (this.TypeMask & 0x8) != 0x0) && this.createSide(primVolume));
        }
        if (cap) {
            this.TexCoordExtents[0] = new LLVector2(1.0f, 1.0f);
            this.TexCoordExtents[1] = new LLVector2(0.0f, 0.0f);
            this.TexCoords.minMaxVector(this.TexCoordExtents[0], this.TexCoordExtents[1]);
            this.TexCoordExtents[0].x = Math.max(0.0f, this.TexCoordExtents[0].x);
            this.TexCoordExtents[0].y = Math.max(0.0f, this.TexCoordExtents[0].y);
            this.TexCoordExtents[1].x = Math.min(1.0f, this.TexCoordExtents[1].x);
            this.TexCoordExtents[1].y = Math.min(1.0f, this.TexCoordExtents[1].y);
        }
        return cap;
    }
}
