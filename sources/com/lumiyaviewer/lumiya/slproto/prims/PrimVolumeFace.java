// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.Vector2Array;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import com.lumiyaviewer.lumiya.slproto.types.VertexArray;
import java.util.ArrayList;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.prims:
//            PrimVolume, PrimVolumeParams, PrimPathParams, PrimProfile, 
//            PrimPath

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
    public int Edge[];
    public LLVector3 Extents[] = {
        new LLVector3(), new LLVector3()
    };
    public int ID;
    public short Indices[];
    public Vector3Array Normals;
    public int NumIndices;
    public int NumS;
    public int NumT;
    public int NumVertices;
    public Vector3Array Positions;
    public LLVector2 TexCoordExtents[] = {
        new LLVector2(), new LLVector2()
    };
    public Vector2Array TexCoords;
    public int TypeMask;
    public VertexArray vertexArray;

    public PrimVolumeFace()
    {
    }

    private boolean createCap(PrimVolume primvolume)
    {
        if ((TypeMask & 0x40) == 0 && (TypeMask & 0x80) == 0 && primvolume.volumeParams.PathParams.Begin == 0.0F && primvolume.volumeParams.PathParams.End == 1.0F && primvolume.getProfileType() == 1 && primvolume.getPathType() == 16)
        {
            return createUnCutCubeCap(primvolume);
        }
        Object obj = primvolume.Mesh;
        ArrayList arraylist = primvolume.Profile.Profile;
        int i1 = arraylist.size();
        int i = (arraylist.size() - 2) * 3;
        Vector2Array vector2array;
        Vector3Array vector3array;
        Vector3Array vector3array1;
        Object obj1;
        LLVector3 llvector3_6;
        LLVector3 llvector3_7;
        int j;
        if ((TypeMask & 0x40) == 0 && (TypeMask & 0x80) == 0)
        {
            resizeVertices(i1 + 1);
            resizeIndices(i + 3);
        } else
        {
            resizeVertices(i1);
            resizeIndices(i);
        }
        i = primvolume.Profile.Total;
        j = primvolume.Path.Path.size();
        if ((TypeMask & 0x200) != 0)
        {
            i *= j - 1;
        } else
        {
            i = BeginS;
        }
        primvolume = new LLVector2();
        obj1 = new LLVector2();
        llvector3_6 = Extents[0];
        llvector3_7 = Extents[1];
        vector2array = TexCoords;
        vector3array = Positions;
        vector3array1 = Normals;
        j = 0;
        while (j < i1) 
        {
            if ((TypeMask & 0x200) != 0)
            {
                vector2array.set(j, 0.5F + ((LLVector3)arraylist.get(j)).x, ((LLVector3)arraylist.get(j)).y + 0.5F);
            } else
            {
                vector2array.set(j, 0.5F + ((LLVector3)arraylist.get(j)).x, 0.5F - ((LLVector3)arraylist.get(j)).y);
            }
            vector3array.set(j, ((Vector3Array) (obj)), j + i);
            if (j == 0)
            {
                vector3array.get(j, llvector3_6);
                vector3array.get(j, llvector3_7);
                vector2array.get(j, primvolume);
                vector2array.get(j, ((LLVector2) (obj1)));
            } else
            {
                vector3array.minMaxVector(j, llvector3_6, llvector3_7);
                vector2array.minMaxVector(j, primvolume, ((LLVector2) (obj1)));
            }
            j++;
        }
        Center = new LLVector3(llvector3_6);
        Center.add(llvector3_7);
        Center.mul(0.5F);
        obj = LLVector2.sum(primvolume, ((LLVector2) (obj1)));
        ((LLVector2) (obj)).mul(0.5F);
        primvolume = new LLVector3(Center);
        obj1 = new LLVector3(Center);
        vector3array.subFromVector(primvolume, 0);
        vector3array.subFromVector(((LLVector3) (obj1)), 1);
        if ((TypeMask & 0x200) != 0)
        {
            primvolume = LLVector3.cross(primvolume, ((LLVector3) (obj1)));
        } else
        {
            primvolume = LLVector3.cross(((LLVector3) (obj1)), primvolume);
        }
        primvolume.normVec();
        if ((TypeMask & 0x40) == 0 && (TypeMask & 0x80) == 0)
        {
            vector3array.set(i1, Center);
            vector2array.set(i1, ((LLVector2) (obj)).x, ((LLVector2) (obj)).y);
            i = i1 + 1;
        } else
        {
            i = i1;
        }
        vector3array1.fill(0, i, primvolume);
        if ((TypeMask & 0x40) != 0)
        {
            if ((TypeMask & 0x200) != 0)
            {
                int k = 0;
                i1 = i - 1;
                int k1 = 0;
                while (i1 - k > 1) 
                {
                    LLVector3 llvector3_2 = new LLVector3((LLVector3)arraylist.get(k));
                    primvolume = new LLVector3((LLVector3)arraylist.get(i1));
                    LLVector3 llvector3_4 = new LLVector3((LLVector3)arraylist.get(k + 1));
                    LLVector3 llvector3 = new LLVector3((LLVector3)arraylist.get(i1 - 1));
                    llvector3_2.z = 0.0F;
                    primvolume.z = 0.0F;
                    llvector3_4.z = 0.0F;
                    llvector3.z = 0.0F;
                    float f = llvector3_2.x;
                    float f2 = llvector3_4.y;
                    float f4 = llvector3_4.x;
                    float f6 = llvector3_2.y;
                    float f8 = llvector3_4.x;
                    float f10 = ((LLVector3) (primvolume)).y;
                    float f12 = ((LLVector3) (primvolume)).x;
                    float f14 = llvector3_4.y;
                    float f16 = ((LLVector3) (primvolume)).x;
                    float f18 = llvector3_2.y;
                    float f20 = llvector3_2.x;
                    float f22 = ((LLVector3) (primvolume)).y;
                    float f24 = llvector3_2.x;
                    float f26 = llvector3.y;
                    float f28 = llvector3.x;
                    float f30 = llvector3_2.y;
                    float f32 = llvector3.x;
                    float f34 = llvector3_4.y;
                    float f36 = llvector3_4.x;
                    float f38 = llvector3.y;
                    float f40 = llvector3_4.x;
                    float f42 = llvector3_2.y;
                    float f44 = llvector3_2.x;
                    float f46 = llvector3_4.y;
                    float f48 = ((LLVector3) (primvolume)).x;
                    float f50 = llvector3_2.y;
                    float f52 = llvector3_2.x;
                    float f54 = ((LLVector3) (primvolume)).y;
                    float f56 = llvector3_2.x;
                    float f58 = llvector3.y;
                    float f60 = llvector3.x;
                    float f62 = llvector3_2.y;
                    float f64 = llvector3.x;
                    float f66 = ((LLVector3) (primvolume)).y;
                    float f68 = ((LLVector3) (primvolume)).x;
                    float f70 = llvector3.y;
                    float f72 = ((LLVector3) (primvolume)).x;
                    float f74 = llvector3_4.y;
                    float f76 = llvector3_4.x;
                    float f78 = ((LLVector3) (primvolume)).y;
                    float f80 = llvector3_4.x;
                    float f82 = llvector3.y;
                    float f84 = llvector3.x;
                    float f86 = llvector3_4.y;
                    float f88 = llvector3.x;
                    float f90 = ((LLVector3) (primvolume)).y;
                    float f92 = ((LLVector3) (primvolume)).x;
                    float f94 = llvector3.y;
                    i = 1;
                    boolean flag = true;
                    if ((f * f2 - f4 * f6) + (f8 * f10 - f12 * f14) + (f16 * f18 - f20 * f22) < 0.0F)
                    {
                        i = 0;
                    }
                    int j2 = i;
                    if ((f72 * f74 - f76 * f78) + (f80 * f82 - f84 * f86) + (f88 * f90 - f92 * f94) < 0.0F)
                    {
                        j2 = 0;
                    }
                    i = ((flag) ? 1 : 0);
                    if ((f48 * f50 - f52 * f54) + (f56 * f58 - f60 * f62) + (f64 * f66 - f68 * f70) < 0.0F)
                    {
                        i = 0;
                    }
                    if ((f24 * f26 - f28 * f30) + (f32 * f34 - f36 * f38) + (f40 * f42 - f44 * f46) < 0.0F)
                    {
                        i = 0;
                    }
                    if (j2 == 0)
                    {
                        i = 0;
                    } else
                    if (i == 0)
                    {
                        i = 1;
                    } else
                    {
                        llvector3_2 = LLVector3.sub(llvector3_2, llvector3_4);
                        primvolume = LLVector3.sub(primvolume, llvector3);
                        if (llvector3_2.magVecSquared() < primvolume.magVecSquared())
                        {
                            i = 1;
                        } else
                        {
                            i = 0;
                        }
                    }
                    if (i != 0)
                    {
                        primvolume = Indices;
                        i = k1 + 1;
                        primvolume[k1] = (short)k;
                        primvolume = Indices;
                        k1 = i + 1;
                        primvolume[i] = (short)(k + 1);
                        primvolume = Indices;
                        i = k1 + 1;
                        primvolume[k1] = (short)i1;
                        k++;
                    } else
                    {
                        primvolume = Indices;
                        i = k1 + 1;
                        primvolume[k1] = (short)k;
                        primvolume = Indices;
                        k1 = i + 1;
                        primvolume[i] = (short)(i1 - 1);
                        primvolume = Indices;
                        i = k1 + 1;
                        primvolume[k1] = (short)i1;
                        i1--;
                    }
                    k1 = i;
                }
            } else
            {
                int l = 0;
                int j1 = i - 1;
                int l1 = 0;
                while (j1 - l > 1) 
                {
                    LLVector3 llvector3_3 = new LLVector3((LLVector3)arraylist.get(l));
                    primvolume = new LLVector3((LLVector3)arraylist.get(j1));
                    LLVector3 llvector3_5 = new LLVector3((LLVector3)arraylist.get(l + 1));
                    LLVector3 llvector3_1 = new LLVector3((LLVector3)arraylist.get(j1 - 1));
                    llvector3_3.z = 0.0F;
                    primvolume.z = 0.0F;
                    llvector3_5.z = 0.0F;
                    llvector3_1.z = 0.0F;
                    float f1 = llvector3_3.x;
                    float f3 = llvector3_5.y;
                    float f5 = llvector3_5.x;
                    float f7 = llvector3_3.y;
                    float f9 = llvector3_5.x;
                    float f11 = ((LLVector3) (primvolume)).y;
                    float f13 = ((LLVector3) (primvolume)).x;
                    float f15 = llvector3_5.y;
                    float f17 = ((LLVector3) (primvolume)).x;
                    float f19 = llvector3_3.y;
                    float f21 = llvector3_3.x;
                    float f23 = ((LLVector3) (primvolume)).y;
                    float f25 = llvector3_3.x;
                    float f27 = llvector3_1.y;
                    float f29 = llvector3_1.x;
                    float f31 = llvector3_3.y;
                    float f33 = llvector3_1.x;
                    float f35 = llvector3_5.y;
                    float f37 = llvector3_5.x;
                    float f39 = llvector3_1.y;
                    float f41 = llvector3_5.x;
                    float f43 = llvector3_3.y;
                    float f45 = llvector3_3.x;
                    float f47 = llvector3_5.y;
                    float f49 = ((LLVector3) (primvolume)).x;
                    float f51 = llvector3_3.y;
                    float f53 = llvector3_3.x;
                    float f55 = ((LLVector3) (primvolume)).y;
                    float f57 = llvector3_3.x;
                    float f59 = llvector3_1.y;
                    float f61 = llvector3_1.x;
                    float f63 = llvector3_3.y;
                    float f65 = llvector3_1.x;
                    float f67 = ((LLVector3) (primvolume)).y;
                    float f69 = ((LLVector3) (primvolume)).x;
                    float f71 = llvector3_1.y;
                    float f73 = ((LLVector3) (primvolume)).x;
                    float f75 = llvector3_5.y;
                    float f77 = llvector3_5.x;
                    float f79 = ((LLVector3) (primvolume)).y;
                    float f81 = llvector3_5.x;
                    float f83 = llvector3_1.y;
                    float f85 = llvector3_1.x;
                    float f87 = llvector3_5.y;
                    float f89 = llvector3_1.x;
                    float f91 = ((LLVector3) (primvolume)).y;
                    float f93 = ((LLVector3) (primvolume)).x;
                    float f95 = llvector3_1.y;
                    i = 1;
                    boolean flag1 = true;
                    if ((f1 * f3 - f5 * f7) + (f9 * f11 - f13 * f15) + (f17 * f19 - f21 * f23) < 0.0F)
                    {
                        i = 0;
                    }
                    int k2 = i;
                    if ((f73 * f75 - f77 * f79) + (f81 * f83 - f85 * f87) + (f89 * f91 - f93 * f95) < 0.0F)
                    {
                        k2 = 0;
                    }
                    i = ((flag1) ? 1 : 0);
                    if ((f49 * f51 - f53 * f55) + (f57 * f59 - f61 * f63) + (f65 * f67 - f69 * f71) < 0.0F)
                    {
                        i = 0;
                    }
                    if ((f25 * f27 - f29 * f31) + (f33 * f35 - f37 * f39) + (f41 * f43 - f45 * f47) < 0.0F)
                    {
                        i = 0;
                    }
                    if (k2 == 0)
                    {
                        i = 0;
                    } else
                    if (i == 0)
                    {
                        i = 1;
                    } else
                    {
                        llvector3_3 = LLVector3.sub(llvector3_3, llvector3_5);
                        primvolume = LLVector3.sub(primvolume, llvector3_1);
                        if (llvector3_3.magVecSquared() < primvolume.magVecSquared())
                        {
                            i = 1;
                        } else
                        {
                            i = 0;
                        }
                    }
                    if (i != 0)
                    {
                        primvolume = Indices;
                        i = l1 + 1;
                        primvolume[l1] = (short)l;
                        primvolume = Indices;
                        l1 = i + 1;
                        primvolume[i] = (short)j1;
                        primvolume = Indices;
                        i = l1 + 1;
                        primvolume[l1] = (short)(l + 1);
                        l++;
                    } else
                    {
                        primvolume = Indices;
                        i = l1 + 1;
                        primvolume[l1] = (short)l;
                        primvolume = Indices;
                        l1 = i + 1;
                        primvolume[i] = (short)j1;
                        primvolume = Indices;
                        i = l1 + 1;
                        primvolume[l1] = (short)(j1 - 1);
                        j1--;
                    }
                    l1 = i;
                }
            }
        } else
        {
            byte byte1 = 2;
            byte byte0 = 1;
            if ((TypeMask & 0x200) != 0)
            {
                byte1 = 1;
                byte0 = 2;
            }
            for (int i2 = 0; i2 < i - 2; i2++)
            {
                Indices[i2 * 3] = (short)(i - 1);
                Indices[i2 * 3 + byte1] = (short)i2;
                Indices[i2 * 3 + byte0] = (short)(i2 + 1);
            }

        }
        return true;
    }

    private boolean createSide(PrimVolume primvolume)
    {
        Vector3Array vector3array;
        Vector3Array vector3array1;
        Object obj;
        Object obj1;
        Object obj2;
        Object obj3;
        int i;
        boolean flag1;
        int i1;
        int j1;
        int l1;
        byte byte0;
        int j4;
        int k4;
        if ((TypeMask & 0x100) != 0)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        i = primvolume.volumeParams.SculptType;
        byte0 = (byte)(i & 7);
        if ((i & 0x40) != 0)
        {
            i1 = 1;
        } else
        {
            i1 = 0;
        }
        if ((i & 0xffffff80) != 0)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        if (i1 != 0)
        {
            i1 = i ^ 1;
        } else
        {
            i1 = i;
        }
        obj = primvolume.Mesh;
        obj1 = primvolume.Profile.Profile;
        obj2 = primvolume.Path.Path;
        j4 = primvolume.Profile.Total;
        i = NumS;
        j1 = NumT;
        l1 = (NumS - 1) * (NumT - 1) * 6;
        resizeVertices(i * j1);
        resizeIndices(l1);
        Edge = new int[l1];
        vector3array1 = Positions;
        vector3array = Normals;
        obj3 = TexCoords;
        k4 = (int)Math.floor(((LLVector3)((ArrayList) (obj1)).get(BeginS)).z);
        if ((TypeMask & 0x10) != 0 && (TypeMask & 0x100) != 0 && NumS > 2)
        {
            j1 = NumS / 2;
        } else
        {
            j1 = NumS;
        }
        l1 = BeginT;
        i = 0;
        while (l1 < BeginT + NumT) 
        {
            float f3 = ((PrimPath.PathPoint)((ArrayList) (obj2)).get(l1)).TexT;
            int i2 = 0;
            while (i2 < j1) 
            {
                float f;
                float f2;
                int l3;
                if ((TypeMask & 4) != 0 || BeginS + i2 >= ((ArrayList) (obj1)).size())
                {
                    if (i2 != 0)
                    {
                        f = 1.0F;
                    } else
                    {
                        f = 0.0F;
                    }
                } else
                if (!flag1)
                {
                    f = ((LLVector3)((ArrayList) (obj1)).get(BeginS + i2)).z;
                } else
                {
                    f = ((LLVector3)((ArrayList) (obj1)).get(BeginS + i2)).z - (float)k4;
                }
                f2 = f;
                if (i1 != 0)
                {
                    f2 = 1.0F - f;
                }
                if (BeginS + i2 >= j4)
                {
                    l3 = BeginS + i2 + (l1 - 1) * j4;
                } else
                {
                    l3 = BeginS + i2 + j4 * l1;
                }
                vector3array1.set(i, ((Vector3Array) (obj)), l3);
                ((Vector2Array) (obj3)).set(i, f2, f3);
                i++;
                if ((TypeMask & 0x10) != 0 && (TypeMask & 0x100) != 0 && NumS > 2 && i2 > 0)
                {
                    vector3array1.set(i, ((Vector3Array) (obj)), l3);
                    ((Vector2Array) (obj3)).set(i, f2, f3);
                    i++;
                }
                i2++;
            }
            if ((TypeMask & 0x10) != 0 && (TypeMask & 0x100) != 0 && NumS > 2)
            {
                float f1;
                int j2;
                int i4;
                if ((TypeMask & 0x80) != 0)
                {
                    j2 = j1 - 1;
                } else
                {
                    j2 = 0;
                }
                i4 = BeginS;
                if (BeginS + j2 < ((ArrayList) (obj1)).size())
                {
                    f1 = ((LLVector3)((ArrayList) (obj1)).get(j2 + BeginS)).z - (float)k4;
                } else
                if (j2 != 0)
                {
                    f1 = 1.0F;
                } else
                {
                    f1 = 0.0F;
                }
                vector3array1.set(i, ((Vector3Array) (obj)), i4 + j2 + j4 * l1);
                ((Vector2Array) (obj3)).set(i, f1, f3);
                i++;
            }
            l1++;
        }
        obj = Extents[0];
        obj1 = Extents[1];
        vector3array1.get(0, ((LLVector3) (obj)));
        vector3array1.get(0, ((LLVector3) (obj1)));
        vector3array1.minMaxVector(((LLVector3) (obj)), ((LLVector3) (obj1)));
        Center = new LLVector3(((LLVector3) (obj)));
        Center.add(((LLVector3) (obj1)));
        Center.mul(0.5F);
        l1 = 0;
        i = 0;
        if ((TypeMask & 0x100) != 0)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        for (i1 = 0; i1 < NumT - 1; i1++)
        {
            int k1 = 0;
            while (k1 < NumS - 1) 
            {
                int ai[] = Indices;
                int k2 = l1 + 1;
                ai[l1] = (short)(NumS * i1 + k1);
                ai = Indices;
                l1 = k2 + 1;
                ai[k2] = (short)(k1 + 1 + NumS * (i1 + 1));
                ai = Indices;
                k2 = l1 + 1;
                ai[l1] = (short)(NumS * (i1 + 1) + k1);
                ai = Indices;
                l1 = k2 + 1;
                ai[k2] = (short)(NumS * i1 + k1);
                ai = Indices;
                k2 = l1 + 1;
                ai[l1] = (short)(k1 + 1 + NumS * i1);
                ai = Indices;
                l1 = k2 + 1;
                ai[k2] = (short)(k1 + 1 + NumS * (i1 + 1));
                ai = Edge;
                k2 = i + 1;
                ai[i] = (NumS - 1) * 2 * i1 + k1 * 2 + 1;
                if (i1 < NumT - 2)
                {
                    Edge[k2] = (NumS - 1) * 2 * (i1 + 1) + k1 * 2 + 1;
                    i = k2 + 1;
                } else
                if (NumT <= 3 || primvolume.Path.Open)
                {
                    Edge[k2] = -1;
                    i = k2 + 1;
                } else
                {
                    Edge[k2] = k1 * 2 + 1;
                    i = k2 + 1;
                }
                if (k1 > 0)
                {
                    Edge[i] = ((NumS - 1) * 2 * i1 + k1 * 2) - 1;
                    i++;
                } else
                if (flag1 || primvolume.Path.Open)
                {
                    Edge[i] = -1;
                    i++;
                } else
                {
                    Edge[i] = (NumS - 1) * 2 * i1 + (NumS - 2) * 2 + 1;
                    i++;
                }
                if (i1 > 0)
                {
                    ai = Edge;
                    k2 = i + 1;
                    ai[i] = (NumS - 1) * 2 * (i1 - 1) + k1 * 2;
                    i = k2;
                } else
                if (NumT <= 3 || primvolume.Path.Open)
                {
                    int ai1[] = Edge;
                    int l2 = i + 1;
                    ai1[i] = -1;
                    i = l2;
                } else
                {
                    int ai2[] = Edge;
                    int i3 = i + 1;
                    ai2[i] = (NumS - 1) * 2 * (NumT - 2) + k1 * 2;
                    i = i3;
                }
                if (k1 < NumS - 2)
                {
                    ai = Edge;
                    k2 = i + 1;
                    ai[i] = (NumS - 1) * 2 * i1 + (k1 + 1) * 2;
                    i = k2;
                } else
                if (flag1 || primvolume.Path.Open)
                {
                    int ai3[] = Edge;
                    int j3 = i + 1;
                    ai3[i] = -1;
                    i = j3;
                } else
                {
                    int ai4[] = Edge;
                    int k3 = i + 1;
                    ai4[i] = (NumS - 1) * 2 * i1;
                    i = k3;
                }
                ai = Edge;
                k2 = i + 1;
                ai[i] = (NumS - 1) * 2 * i1 + k1 * 2;
                k1++;
                i = k2;
            }
        }

        Normals.clear();
        LLVector3 allvector3[] = new LLVector3[3];
        short aword0[] = new short[3];
        for (i = 0; i < 3; i++)
        {
            allvector3[i] = new LLVector3();
        }

        obj2 = new LLVector3();
        obj3 = new LLVector3();
        for (i = 0; i < NumIndices / 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                aword0[j] = Indices[i * 3 + j];
                vector3array1.get(aword0[j], allvector3[j]);
            }

            ((LLVector3) (obj2)).setSub(allvector3[0], allvector3[1]);
            ((LLVector3) (obj3)).setSub(allvector3[0], allvector3[2]);
            ((LLVector3) (obj2)).setCross(((LLVector3) (obj3)));
            for (int k = 0; k < 3; k++)
            {
                Normals.add(aword0[k], ((LLVector3) (obj2)));
            }

            Normals.add(aword0[(i & 1) + 1], ((LLVector3) (obj2)));
        }

        allvector3 = new LLVector3();
        aword0 = new LLVector3();
        obj2 = new LLVector3();
        vector3array1.get(0, aword0);
        vector3array1.get(NumS * (NumT - 2), ((LLVector3) (obj2)));
        allvector3.setSub(aword0, ((LLVector3) (obj2)));
        boolean flag;
        if (allvector3.dot(allvector3) < 1E-06F)
        {
            k = 1;
        } else
        {
            k = 0;
        }
        vector3array1.get(NumS - 1, aword0);
        vector3array1.get((NumS * (NumT - 2) + NumS) - 1, ((LLVector3) (obj2)));
        allvector3.setSub(aword0, ((LLVector3) (obj2)));
        if (allvector3.dot(allvector3) < 1E-06F)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (byte0 == 0)
        {
            if (!primvolume.Path.Open)
            {
                for (i1 = 0; i1 < NumS; i1++)
                {
                    vector3array.setAdd(i1, NumS * (NumT - 1) + i1);
                }

            }
            if (!primvolume.Path.Open && k ^ true)
            {
                for (i1 = 0; i1 < NumT; i1++)
                {
                    vector3array.setAdd(NumS * i1, (NumS * i1 + NumS) - 1);
                }

            }
            if (primvolume.getPathType() == 32 && (primvolume.getProfileType() & 0xf) == 5)
            {
                if (k)
                {
                    for (int l = 0; l < NumT; l++)
                    {
                        vector3array.set(NumS * l, 1.0F, 0.0F, 0.0F);
                    }

                }
                if (flag)
                {
                    for (flag = false; flag < NumT; flag++)
                    {
                        vector3array.set((NumS * flag + NumS) - 1, -1F, 0.0F, 0.0F);
                    }

                }
            }
            break MISSING_BLOCK_LABEL_2487;
        }
        if (byte0 == 1)
        {
            i1 = 1;
        } else
        {
            i1 = 0;
        }
        break MISSING_BLOCK_LABEL_2192;
        if (byte0 == 1 || byte0 == 2 || byte0 == 4)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (byte0 == 2)
        {
            l = 1;
        } else
        {
            l = 0;
        }
        if (i1 == 0)
        {
            break MISSING_BLOCK_LABEL_2388;
        }
        primvolume = new LLVector3();
        for (i1 = 0; i1 < NumS; i1++)
        {
            vector3array.addToVector(i1, primvolume);
        }

        break MISSING_BLOCK_LABEL_2270;
        for (i1 = 0; i1 < NumS; i1++)
        {
            vector3array.set(i1, primvolume);
        }

        primvolume.set(0.0F, 0.0F, 0.0F);
        for (i1 = 0; i1 < NumS; i1++)
        {
            vector3array.addToVector(NumS * (NumT - 1) + i1, primvolume);
        }

        for (i1 = 0; i1 < NumS; i1++)
        {
            vector3array.set(NumS * (NumT - 1) + i1, primvolume);
        }

        if (flag)
        {
            for (flag = false; flag < NumT; flag++)
            {
                vector3array.setAdd(NumS * flag, (NumS * flag + NumS) - 1);
            }

        }
        if (l)
        {
            for (flag = false; flag < NumS; flag++)
            {
                vector3array.setAdd(flag, NumS * (NumT - 1) + flag);
            }

        }
        return true;
    }

    private boolean createUnCutCubeCap(PrimVolume primvolume)
    {
        Object obj = primvolume.Mesh;
        Object obj1 = primvolume.Profile.Profile;
        int i = primvolume.Profile.Total;
        int j = primvolume.Path.Path.size();
        int j2 = (((ArrayList) (obj1)).size() - 1) / 4;
        primvolume = Extents[0];
        LLVector3 llvector3 = Extents[1];
        VertexArray vertexarray;
        LLVector3 llvector3_1;
        Object obj2;
        Object obj3;
        if ((TypeMask & 0x200) != 0)
        {
            i *= j - 1;
        } else
        {
            i = BeginS;
        }
        vertexarray = new VertexArray(4);
        llvector3_1 = new LLVector3();
        obj2 = vertexarray.getVertices();
        obj3 = vertexarray.getTexCoords();
        for (j = 0; j < 4; j++)
        {
            ((Vector3Array) (obj2)).set(j, ((Vector3Array) (obj)), j2 * j + i);
            ((Vector2Array) (obj3)).set(j, 0.5F + ((LLVector3)((ArrayList) (obj1)).get(j2 * j)).x, 0.5F - ((LLVector3)((ArrayList) (obj1)).get(j2 * j)).y);
        }

        obj = new LLVector3();
        ((Vector3Array) (obj2)).getSub(1, 0, llvector3_1);
        ((Vector3Array) (obj2)).getSub(2, 1, ((LLVector3) (obj)));
        llvector3_1.setCross(((LLVector3) (obj)));
        llvector3_1.normVec();
        LLVector2 llvector2;
        if ((TypeMask & 0x200) == 0)
        {
            llvector3_1.mul(-1F);
        } else
        {
            ((Vector2Array) (obj3)).swap(0, 3);
            ((Vector2Array) (obj3)).swap(1, 2);
        }
        resizeVertices((j2 + 1) * (j2 + 1));
        obj = Positions;
        j = 0;
        obj1 = new LLVector3();
        obj2 = new LLVector3();
        obj3 = new LLVector2();
        llvector2 = new LLVector2();
        for (i = 0; i < j2 + 1; i++)
        {
            int i1 = 0;
            while (i1 < j2 + 1) 
            {
                vertexArray.LerpPlanarVertex(j, vertexarray, 0, vertexarray, 1, vertexarray, 3, (float)i / (float)j2, (float)i1 / (float)j2, ((LLVector3) (obj1)), ((LLVector3) (obj2)), ((LLVector2) (obj3)), llvector2);
                vertexArray.getNormals().set(j, llvector3_1);
                if (i == 0 && i1 == 0)
                {
                    ((Vector3Array) (obj)).get(j, primvolume);
                    ((Vector3Array) (obj)).get(j, llvector3);
                } else
                {
                    ((Vector3Array) (obj)).minMaxVector(j, primvolume, llvector3);
                }
                j++;
                i1++;
            }
        }

        Center = new LLVector3(primvolume);
        Center.add(llvector3);
        Center.mul(0.5F);
        resizeIndices(j2 * j2 * 6);
        primvolume = Indices;
        short aword0[] = new short[6];
        aword0[0] = 0;
        aword0[1] = 1;
        aword0[2] = (short)(j2 + 1 + 1);
        aword0[3] = (short)(j2 + 1 + 1);
        aword0[4] = (short)(j2 + 1);
        aword0[5] = 0;
        i = 0;
        for (int j1 = 0; j1 < j2; j1++)
        {
label0:
            for (int k1 = 0; k1 < j2; k1++)
            {
                if ((TypeMask & 0x200) != 0)
                {
                    int l1 = 5;
                    int k = i;
                    do
                    {
                        i = k;
                        if (l1 < 0)
                        {
                            continue label0;
                        }
                        primvolume[k] = (short)((j2 + 1) * k1 + j1 + aword0[l1]);
                        l1--;
                        k++;
                    } while (true);
                }
                int i2 = 0;
                int l = i;
                do
                {
                    i = l;
                    if (i2 >= 6)
                    {
                        continue label0;
                    }
                    primvolume[l] = (short)((j2 + 1) * k1 + j1 + aword0[i2]);
                    i2++;
                    l++;
                } while (true);
            }

        }

        return true;
    }

    private void resizeIndices(int i)
    {
        if (i != NumIndices)
        {
            if (i != 0)
            {
                Indices = new short[i];
            } else
            {
                Indices = null;
            }
            NumIndices = i;
        }
    }

    private void resizeVertices(int i)
    {
        if (NumVertices != i)
        {
            if (i != 0)
            {
                vertexArray = new VertexArray(i);
                Positions = vertexArray.getVertices();
                Normals = vertexArray.getNormals();
                TexCoords = vertexArray.getTexCoords();
            } else
            {
                Positions = null;
                Normals = null;
                TexCoords = null;
                vertexArray = null;
            }
            NumVertices = i;
        }
    }

    public boolean create(PrimVolume primvolume)
    {
        boolean flag;
        if ((TypeMask & 2) != 0)
        {
            flag = createCap(primvolume);
        } else
        if ((TypeMask & 4) != 0 || (TypeMask & 8) != 0)
        {
            flag = createSide(primvolume);
        } else
        {
            flag = false;
        }
        if (flag)
        {
            TexCoordExtents[0] = new LLVector2(1.0F, 1.0F);
            TexCoordExtents[1] = new LLVector2(0.0F, 0.0F);
            TexCoords.minMaxVector(TexCoordExtents[0], TexCoordExtents[1]);
            TexCoordExtents[0].x = Math.max(0.0F, TexCoordExtents[0].x);
            TexCoordExtents[0].y = Math.max(0.0F, TexCoordExtents[0].y);
            TexCoordExtents[1].x = Math.min(1.0F, TexCoordExtents[1].x);
            TexCoordExtents[1].y = Math.min(1.0F, TexCoordExtents[1].y);
        }
        return flag;
    }
}
