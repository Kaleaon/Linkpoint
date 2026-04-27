// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.ArrayList;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.prims:
//            PrimProfileParams

public class PrimProfile
{
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

        public Face()
        {
        }
    }


    public static final int MIN_DETAIL_FACES = 6;
    private static final float tableScale[] = {
        1.0F, 1.0F, 1.0F, 0.5F, 0.707107F, 0.53F, 0.525F, 0.5F
    };
    public boolean Concave;
    public boolean Dirty;
    public LLVector3 EdgeCenters[];
    public LLVector3 EdgeNormals[];
    public ArrayList Faces;
    public LLVector2 Normals[];
    public boolean Open;
    public ArrayList Profile;
    public int Total;
    public int TotalOut;

    public PrimProfile()
    {
        Open = false;
        Concave = false;
        Dirty = true;
        TotalOut = 0;
        Total = 2;
        Profile = new ArrayList();
        Faces = new ArrayList();
    }

    private Face addCap(short word0)
    {
        Face face = new Face();
        face.Index = 0;
        face.Count = Total;
        face.ScaleU = 1.0F;
        face.Cap = true;
        face.FaceID = word0;
        Faces.add(face);
        return face;
    }

    private Face addFace(int i, int j, float f, short word0, boolean flag)
    {
        Face face = new Face();
        face.Index = i;
        face.Count = j;
        face.ScaleU = f;
        face.Flat = flag;
        face.Cap = false;
        face.FaceID = word0;
        Faces.add(face);
        return face;
    }

    private Face addHole(PrimProfileParams primprofileparams, boolean flag, float f, float f1, float f2, float f3, int i)
    {
        TotalOut = Total;
        genNGon(primprofileparams, (int)Math.floor(f), f1, -1F, f3, i);
        primprofileparams = addFace(TotalOut, Total - TotalOut, 0.0F, (short)4, flag);
        LLVector3 allvector3[] = new LLVector3[Total];
        for (i = TotalOut; i < Total; i++)
        {
            allvector3[i] = new LLVector3((LLVector3)Profile.get(i));
            allvector3[i].mul(f2);
        }

        i = Total - 1;
        for (int j = TotalOut; j < Total;)
        {
            Profile.set(j, allvector3[i]);
            j++;
            i--;
        }

        for (i = 0; i < Faces.size(); i++)
        {
            if (((Face)Faces.get(i)).Cap)
            {
                Face face = (Face)Faces.get(i);
                face.Count = face.Count * 2;
            }
        }

        return primprofileparams;
    }

    private void genNGon(PrimProfileParams primprofileparams, int i, float f, float f1, float f2, int j)
    {
        float f6 = primprofileparams.Begin;
        float f7 = primprofileparams.End;
        float f8 = 1.0F / (float)i;
        float f9 = 6.283185F * f8 * f2;
        int k = Math.round((float)i / f2);
        float f3;
        float f4;
        LLVector3 llvector3;
        LLVector3 llvector3_1;
        if (k < 8)
        {
            f3 = tableScale[k];
        } else
        {
            f3 = 0.5F;
        }
        f4 = (float)(Math.floor((float)i * f6) / (double)(float)i);
        f1 = 6.283185F * (f4 * f2 + f);
        llvector3 = new LLVector3((float)Math.cos(f1) * f3, (float)Math.sin(f1) * f3, f4);
        f = f4 + f8;
        f1 += f9;
        llvector3_1 = new LLVector3((float)Math.cos(f1) * f3, (float)Math.sin(f1) * f3, f);
        f4 = (f6 - f4) * (float)i;
        if (f4 < 0.9999F)
        {
            Profile.add(LLVector3.lerp(llvector3, llvector3_1, f4));
            f4 = f1;
            f1 = f;
            f = f4;
        } else
        {
            float f5 = f;
            f = f1;
            f1 = f5;
        }
        for (; f1 < f7; f1 += f8)
        {
            llvector3 = new LLVector3((float)Math.cos(f) * f3, (float)Math.sin(f) * f3, f1);
            if (Profile.size() > 0)
            {
                llvector3_1 = (LLVector3)Profile.get(Profile.size() - 1);
                for (int l = 0; l < j; l++)
                {
                    Profile.add(LLVector3.lerp(llvector3_1, llvector3, (1.0F / (float)(j + 1)) * (float)(l + 1)));
                }

            }
            Profile.add(llvector3);
            f += f9;
        }

        llvector3_1 = new LLVector3((float)Math.cos(f) * f3, f3 * (float)Math.sin(f), f1);
        f = (f7 - (f1 - f8)) * (float)i;
        if (f > 1E-04F)
        {
            llvector3 = LLVector3.lerp(llvector3, llvector3_1, f);
            if (Profile.size() > 0)
            {
                LLVector3 llvector3_2 = (LLVector3)Profile.get(Profile.size() - 1);
                for (i = 0; i < j; i++)
                {
                    Profile.add(LLVector3.lerp(llvector3_2, llvector3, (1.0F / (float)(j + 1)) * (float)(i + 1)));
                }

            }
            Profile.add(llvector3);
        }
        if ((f7 - f6) * f2 < 0.99F)
        {
            Open = true;
            boolean flag;
            if ((f7 - f6) * f2 > 0.5F)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            Concave = flag;
            if (primprofileparams.Hollow <= 0.0F)
            {
                Profile.add(new LLVector3(0.0F, 0.0F, 0.0F));
            }
        } else
        {
            Open = false;
            Concave = false;
        }
        Total = Profile.size();
    }

    private static int getNumNGonPoints(PrimProfileParams primprofileparams, int i, float f, float f1, float f2, int j)
    {
        f1 = primprofileparams.Begin;
        float f3 = primprofileparams.End;
        float f4 = 1.0F / (float)i;
        float f5 = (float)(Math.floor((float)i * f1) / (double)(float)i);
        f = f5 + f4;
        int k;
        if ((f1 - f5) * (float)i < 0.9999F)
        {
            j = 1;
        } else
        {
            j = 0;
        }
        while (f < f3) 
        {
            f += f4;
            j++;
        }
        k = j;
        if ((f3 - (f - f4)) * (float)i > 1E-04F)
        {
            k = j + 1;
        }
        i = k;
        if ((f3 - f1) * f2 < 0.99F)
        {
            i = k;
            if (primprofileparams.Hollow <= 0.0F)
            {
                i = k + 1;
            }
        }
        return i;
    }

    public static int getNumPoints(PrimProfileParams primprofileparams, boolean flag, float f, int i, boolean flag1, int j)
    {
        float f1;
        float f2;
        boolean flag2;
        f1 = f;
        if (f < 0.0F)
        {
            f1 = 0.0F;
        }
        f2 = primprofileparams.Hollow;
        flag2 = false;
        primprofileparams.CurveType & 0xf;
        JVM INSTR tableswitch 0 5: default 68
    //                   0 126
    //                   1 73
    //                   2 100
    //                   3 100
    //                   4 100
    //                   5 210;
           goto _L1 _L2 _L3 _L4 _L4 _L4 _L5
_L1:
        i = ((flag2) ? 1 : 0);
_L7:
        return i;
_L3:
        j = getNumNGonPoints(primprofileparams, 4, -0.375F, 0.0F, 1.0F, i);
        i = j;
        if (f2 != 0.0F)
        {
            return j * 2;
        }
        continue; /* Loop/switch isn't completed */
_L4:
        j = getNumNGonPoints(primprofileparams, 3, 0.0F, 0.0F, 1.0F, i);
        i = j;
        if (f2 != 0.0F)
        {
            return j * 2;
        }
        continue; /* Loop/switch isn't completed */
_L2:
        f1 = 6F * f1;
        f = f1;
        if (f2 != 0.0F)
        {
            f = f1;
            if ((primprofileparams.CurveType & 0xfffffff0) == 32)
            {
                f = (float)(Math.ceil(f1 / 4F) * 4D);
            }
        }
        i = (int)f;
        if (flag1)
        {
            i = j;
        }
        j = getNumNGonPoints(primprofileparams, i, 0.0F, 0.0F, 1.0F, 0);
        i = j;
        if (f2 != 0.0F)
        {
            return j * 2;
        }
        continue; /* Loop/switch isn't completed */
_L5:
        f1 = 6F * f1 * 0.5F;
        f = f1;
        if (f2 != 0.0F)
        {
            f = f1;
            if ((primprofileparams.CurveType & 0xfffffff0) == 32)
            {
                f = (float)(Math.ceil(f1 / 2.0F) * 2D);
            }
        }
        i = getNumNGonPoints(primprofileparams, (int)Math.floor(f), 0.5F, 0.0F, 0.5F, 0);
        j = i;
        if (f2 != 0.0F)
        {
            j = i * 2;
        }
        boolean flag3;
        if (primprofileparams.End - primprofileparams.Begin < 1.0F)
        {
            flag3 = true;
        } else
        {
            flag3 = false;
        }
        i = j;
        if (!flag3)
        {
            i = j;
            if (f2 == 0.0F)
            {
                return j + 1;
            }
        }
        if (true) goto _L7; else goto _L6
_L6:
    }

    protected void genNormals(PrimProfileParams primprofileparams)
    {
        LLVector3 llvector3_2;
        int j;
        int k;
        int j2 = Profile.size();
        int i;
        if (TotalOut != 0)
        {
            i = TotalOut;
        } else
        {
            i = Total / 2;
        }
        EdgeNormals = new LLVector3[j2 * 2];
        EdgeCenters = new LLVector3[j2 * 2];
        Normals = new LLVector2[j2];
        if (primprofileparams.Hollow > 0.0F)
        {
            j = 1;
        } else
        {
            j = 0;
        }
        k = 0;
        while (k < j2) 
        {
            Normals[k] = new LLVector2(((LLVector3)Profile.get(k)).x, ((LLVector3)Profile.get(k)).y);
            if (j && k >= i)
            {
                Normals[k].mul(-1F);
            }
            if ((double)Normals[k].magVec() < 0.001D)
            {
                LLVector2 llvector2;
                int l;
                int j1;
                int l1;
                int i2;
                if (k - 1 >= 0)
                {
                    l = k - 1;
                } else
                {
                    l = j2 - 1;
                }
                if (l - 1 >= 0)
                {
                    j1 = l - 1;
                } else
                {
                    j1 = j2 - 1;
                }
                if (k + 1 < j2)
                {
                    l1 = k + 1;
                } else
                {
                    l1 = 0;
                }
                if (l1 + 1 < j2)
                {
                    i2 = l1 + 1;
                } else
                {
                    i2 = 0;
                }
                primprofileparams = new LLVector2((((LLVector3)Profile.get(l)).x + ((LLVector3)Profile.get(l)).x) - ((LLVector3)Profile.get(j1)).x, (((LLVector3)Profile.get(l)).y + ((LLVector3)Profile.get(l)).y) - ((LLVector3)Profile.get(j1)).y);
                llvector2 = new LLVector2((((LLVector3)Profile.get(l1)).x + ((LLVector3)Profile.get(l1)).x) - ((LLVector3)Profile.get(i2)).x, (((LLVector3)Profile.get(l1)).y + ((LLVector3)Profile.get(l1)).y) - ((LLVector3)Profile.get(i2)).y);
                Normals[k] = LLVector2.sum(primprofileparams, llvector2);
                Normals[k].mul(0.5F);
            }
            Normals[k].normVec();
            k++;
        }
        LLVector3 llvector3;
        if (Concave)
        {
            i = 2;
        } else
        {
            i = 1;
        }
        j = 0;
_L8:
        if (j >= i)
        {
            break; /* Loop/switch isn't completed */
        }
        k = 0;
_L2:
        if (k >= Total)
        {
            break MISSING_BLOCK_LABEL_821;
        }
        llvector3_2 = new LLVector3((LLVector3)Profile.get(k));
        llvector3_2.z = 0.0F;
        if (!Concave || j != 0 || k != (Total - 1) / 2)
        {
            break; /* Loop/switch isn't completed */
        }
        primprofileparams = (LLVector3)Profile.get(Total - 1);
_L3:
        primprofileparams.z = 0.0F;
        llvector3 = LLVector3.sub(primprofileparams, llvector3_2);
        llvector3.setCross(LLVector3.z_axis);
        llvector3.normVec();
        EdgeNormals[j * j2 + k] = llvector3;
        EdgeCenters[j * j2 + k] = LLVector3.lerp(llvector3_2, primprofileparams, 0.5F);
        k++;
        if (true) goto _L2; else goto _L1
_L1:
label0:
        {
            if (!Concave || j != 1 || k != Total - 1)
            {
                break label0;
            }
            primprofileparams = (LLVector3)Profile.get((Total - 1) / 2);
        }
          goto _L3
        LLVector3 llvector3_3;
        int i1;
        primprofileparams = new LLVector3();
        llvector3_3 = new LLVector3();
        i1 = Total;
        i1 = (k + 1) % i1;
_L6:
        if (llvector3_3.magVecSquared() >= 1E-04F) goto _L3; else goto _L4
_L4:
        LLVector3 llvector3_1;
        int k1;
        llvector3_1 = (LLVector3)Profile.get(i1);
        llvector3_3.setSub(llvector3_1, llvector3_2);
        k1 = (i1 + 1) % Total;
        primprofileparams = llvector3_1;
        i1 = k1;
        if (k1 != k) goto _L6; else goto _L5
_L5:
        primprofileparams = llvector3_1;
          goto _L3
        j++;
        if (true) goto _L8; else goto _L7
_L7:
    }

    public boolean generate(PrimProfileParams primprofileparams, boolean flag, float f, int i, boolean flag1, int j)
    {
        float f1;
        float f2;
        float f3;
        float f4;
        int k;
        if (!Dirty && flag1 ^ true)
        {
            return false;
        }
        Dirty = false;
        f1 = f;
        if (f < 0.0F)
        {
            f1 = 0.0F;
        }
        Profile.clear();
        Faces.clear();
        f3 = primprofileparams.Begin;
        f4 = primprofileparams.End;
        f2 = primprofileparams.Hollow;
        if (f3 > f4 - 0.01F)
        {
            return false;
        }
        k = 0;
        primprofileparams.CurveType & 0xf;
        JVM INSTR tableswitch 0 5: default 128
    //                   0 701
    //                   1 188
    //                   2 453
    //                   3 453
    //                   4 453
    //                   5 913;
           goto _L1 _L2 _L3 _L4 _L4 _L4 _L5
_L12:
        if (flag)
        {
            addCap((short)2);
        }
        if (Open)
        {
            addFace(Total - 1, 2, 0.5F, (short)8, true);
            LLVector3 llvector3;
            int l;
            if (f2 != 0.0F)
            {
                addFace(TotalOut - 1, 2, 0.5F, (short)16, true);
            } else
            {
                addFace(Total - 2, 2, 0.5F, (short)16, true);
            }
        }
        return true;
_L3:
        genNGon(primprofileparams, 4, -0.375F, 0.0F, 1.0F, i);
        if (flag)
        {
            addCap((short)1);
        }
        k = (int)Math.floor(4F * f3);
        for (j = 0; k < (int)Math.floor(4F * f4 + 0.999F); j++)
        {
            addFace((i + 1) * j, i + 2, 1.0F, (short)(32 << k), true);
            k++;
        }

        for (j = 0; j < Profile.size(); j++)
        {
            llvector3 = (LLVector3)Profile.get(j);
            llvector3.z = llvector3.z * 4F;
        }

        if (f2 == 0.0F) goto _L7; else goto _L6
_L6:
        primprofileparams.CurveType & 0xfffffff0;
        JVM INSTR lookupswitch 2: default 372
    //                   16: 432
    //                   48: 413;
           goto _L8 _L9 _L10
_L8:
        addHole(primprofileparams, true, 4F, -0.375F, f2, 1.0F, i);
_L7:
        if (flag)
        {
            ((Face)Faces.get(0)).Count = Total;
        }
          goto _L1
_L10:
        addHole(primprofileparams, true, 3F, -0.375F, f2, 1.0F, i);
          goto _L7
_L9:
        addHole(primprofileparams, false, 6F * f1, -0.375F, f2, 1.0F, 0);
          goto _L7
_L4:
        genNGon(primprofileparams, 3, 0.0F, 0.0F, 1.0F, i);
        for (j = 0; j < Profile.size(); j++)
        {
            llvector3 = (LLVector3)Profile.get(j);
            llvector3.z = llvector3.z * 3F;
        }

        if (flag)
        {
            addCap((short)1);
        }
        l = (int)Math.floor(3F * f3);
        j = k;
        for (k = l; k < (int)Math.floor(3F * f4 + 0.999F);)
        {
            addFace(j * (i + 1), i + 2, 1.0F, (short)(32 << k), true);
            k++;
            j++;
        }

        if (f2 != 0.0F)
        {
            f = f2 / 2.0F;
            switch (primprofileparams.CurveType & 0xfffffff0)
            {
            default:
                addHole(primprofileparams, true, 3F, 0.0F, f, 1.0F, i);
                break;

            case 16: // '\020'
                addHole(primprofileparams, false, 6F * f1, 0.0F, f, 1.0F, 0);
                break;

            case 32: // ' '
                addHole(primprofileparams, true, 4F, 0.0F, f, 1.0F, i);
                break;
            }
        }
_L1:
        if (true) goto _L12; else goto _L11
_L11:
_L2:
        f = 6F * f1;
        byte byte0;
        if (f2 != 0.0F)
        {
            byte0 = (byte)(primprofileparams.CurveType & 0xfffffff0);
            if (byte0 == 32)
            {
                f = (float)(Math.ceil(f / 4F) * 4D);
            }
        } else
        {
            byte0 = 0;
        }
        l = (int)f;
        if (flag1)
        {
            l = j;
        }
        genNGon(primprofileparams, l, 0.0F, 0.0F, 1.0F, 0);
        if (flag)
        {
            addCap((short)1);
        }
        if (Open && f2 == 0.0F)
        {
            addFace(0, Total - 1, 0.0F, (short)32, false);
        } else
        {
            addFace(0, Total, 0.0F, (short)32, false);
        }
        if (f2 == 0.0F) goto _L12; else goto _L13
_L13:
        switch (byte0)
        {
        default:
            addHole(primprofileparams, false, f, 0.0F, f2, 1.0F, 0);
            break;

        case 32: // ' '
            addHole(primprofileparams, true, 4F, 0.0F, f2, 1.0F, i);
            break;

        case 48: // '0'
            addHole(primprofileparams, true, 3F, 0.0F, f2, 1.0F, i);
            break;
        }
        if (true) goto _L12; else goto _L14
_L14:
_L5:
        f = 6F * f1 * 0.5F;
        if (f2 != 0.0F)
        {
            j = (byte)(primprofileparams.CurveType & 0xfffffff0);
            if (j == 32)
            {
                f = (float)(Math.ceil(f / 2.0F) * 2D);
            }
        } else
        {
            j = 0;
        }
        genNGon(primprofileparams, (int)Math.floor(f), 0.5F, 0.0F, 0.5F, 0);
        if (flag)
        {
            addCap((short)1);
        }
        if (Open && f2 == 0.0F)
        {
            addFace(0, Total - 1, 0.0F, (short)32, false);
        } else
        {
            addFace(0, Total, 0.0F, (short)32, false);
        }
        if (f2 == 0.0F) goto _L16; else goto _L15
_L15:
        j;
        JVM INSTR lookupswitch 2: default 1052
    //                   32: 1102
    //                   48: 1121;
           goto _L17 _L18 _L19
_L17:
        addHole(primprofileparams, false, f, 0.5F, f2, 0.5F, 0);
_L16:
        if (f4 - f3 < 1.0F)
        {
            Open = true;
        } else
        if (f2 == 0.0F)
        {
            Open = false;
            Profile.add(new LLVector3((LLVector3)Profile.get(0)));
            Total = Total + 1;
        }
          goto _L12
_L18:
        addHole(primprofileparams, true, 2.0F, 0.5F, f2, 0.5F, i);
          goto _L16
_L19:
        addHole(primprofileparams, true, 3F, 0.5F, f2, 0.5F, i);
          goto _L16
    }

}
