// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.GLTexture;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.prims:
//            PrimVolumeParams, PrimPath, PrimProfile, PrimVolumeFace, 
//            PrimProfileParams, PrimPathParams, PrimFlexibleParams

public class PrimVolume
{

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
    public ArrayList VolumeFaces;
    private int sculptRequestedS;
    private int sculptRequestedT;
    PrimVolumeParams volumeParams;

    public PrimVolume()
    {
        VolumeFaces = new ArrayList();
    }

    public static PrimVolume create(PrimVolumeParams primvolumeparams, float f, boolean flag, boolean flag1, GLTexture gltexture)
    {
        if (primvolumeparams.isSculpt())
        {
            if (gltexture == null)
            {
                return null;
            }
            Debug.Log((new StringBuilder()).append("Sculpt: using sculpt texture ").append(primvolumeparams.SculptID).toString());
        }
        PrimVolume primvolume = new PrimVolume();
        primvolume.volumeParams = primvolumeparams;
        primvolume.Detail = f;
        primvolume.GenerateSingleFace = flag;
        primvolume.Unique = flag1;
        primvolume.Path = new PrimPath();
        primvolume.Profile = new PrimProfile();
        primvolume.FaceMask = 0;
        primvolume.LODScaleBias = new LLVector3(1.0F, 1.0F, 1.0F);
        if (!primvolumeparams.isSculpt())
        {
            primvolume.generate();
            primvolume.createVolumeFaces();
            return primvolume;
        }
        if (!primvolume.sculpt(gltexture.getWidth(), gltexture.getHeight(), gltexture.getNumComponents(), gltexture, 1))
        {
            return null;
        } else
        {
            return primvolume;
        }
    }

    private void createVolumeFaces()
    {
        if (GenerateSingleFace)
        {
            return;
        }
        int j = getNumFaces();
        VolumeFaces.ensureCapacity(j);
        int i = 0;
        while (i < j) 
        {
            PrimVolumeFace primvolumeface = new PrimVolumeFace();
            PrimProfile.Face face = (PrimProfile.Face)Profile.Faces.get(i);
            primvolumeface.BeginS = face.Index;
            primvolumeface.NumS = face.Count;
            primvolumeface.BeginT = 0;
            primvolumeface.NumT = Path.Path.size();
            primvolumeface.ID = i;
            if (volumeParams.ProfileParams.Hollow > 0.0F)
            {
                primvolumeface.TypeMask = primvolumeface.TypeMask | 0x40;
            }
            if (Profile.Open)
            {
                primvolumeface.TypeMask = primvolumeface.TypeMask | 0x80;
            }
            if (face.Cap)
            {
                primvolumeface.TypeMask = primvolumeface.TypeMask | 2;
                if (face.FaceID == 1)
                {
                    primvolumeface.TypeMask = primvolumeface.TypeMask | 0x200;
                } else
                {
                    primvolumeface.TypeMask = primvolumeface.TypeMask | 0x400;
                }
            } else
            if ((face.FaceID & 0x18) != 0)
            {
                primvolumeface.TypeMask = primvolumeface.TypeMask | 0x104;
            } else
            {
                primvolumeface.TypeMask = primvolumeface.TypeMask | 8;
                if (face.Flat)
                {
                    primvolumeface.TypeMask = primvolumeface.TypeMask | 0x100;
                }
                if ((face.FaceID & 4) != 0)
                {
                    primvolumeface.TypeMask = primvolumeface.TypeMask | 0x10;
                    if (face.Flat && primvolumeface.NumS > 2)
                    {
                        primvolumeface.NumS = primvolumeface.NumS * 2;
                    }
                } else
                {
                    primvolumeface.TypeMask = primvolumeface.TypeMask | 0x20;
                }
            }
            VolumeFaces.add(primvolumeface);
            i++;
        }
        for (Iterator iterator = VolumeFaces.iterator(); iterator.hasNext(); ((PrimVolumeFace)iterator.next()).create(this)) { }
    }

    private boolean generate()
    {
        int i;
        int j;
        j = (int)(Detail * 0.66F);
        i = j;
        if (volumeParams.PathParams.CurveType != 16) goto _L2; else goto _L1
_L1:
        if (volumeParams.PathParams.ScaleX != 1.0F) goto _L4; else goto _L3
_L3:
        i = j;
        if (volumeParams.PathParams.ScaleY == 1.0F) goto _L2; else goto _L4
_L6:
        i = 0;
          goto _L2
_L4:
        if (volumeParams.ProfileParams.CurveType == 1 || volumeParams.ProfileParams.CurveType == 2 || volumeParams.ProfileParams.CurveType == 3) goto _L6; else goto _L5
_L5:
        i = j;
        if (volumeParams.ProfileParams.CurveType != 4) goto _L2; else goto _L6
_L2:
        LODScaleBias.set(0.5F, 0.5F, 0.5F);
        float f = Detail;
        float f2 = Detail;
        j = volumeParams.PathParams.CurveType;
        int k = volumeParams.ProfileParams.CurveType;
        int l;
        boolean flag;
        boolean flag1;
        if (j == 16 && k == 0)
        {
            LODScaleBias.set(0.6F, 0.6F, 0.0F);
        } else
        if (j == 32)
        {
            LODScaleBias.set(0.6F, 0.6F, 0.6F);
        }
        Iterator iterator;
        PrimProfile.Face face;
        if (volumeParams.isFlexible())
        {
            j = volumeParams.FlexiParams.NumFlexiSections - 2;
        } else
        {
            j = i;
        }
        flag = Path.generate(volumeParams.PathParams, f2, j, false, 0);
        flag1 = Profile.generate(volumeParams.ProfileParams, Path.Open, f, i, false, 0);
        if (!flag && !flag1)
        {
            break MISSING_BLOCK_LABEL_589;
        }
        k = Path.Path.size();
        l = Profile.Profile.size();
        Mesh = new Vector3Array(l * k);
        i = 0;
        do
        {
label0:
            {
                if (i >= k)
                {
                    break label0;
                }
                LLVector2 llvector2 = ((PrimPath.PathPoint)Path.Path.get(i)).scale;
                com.lumiyaviewer.lumiya.slproto.types.LLQuaternion llquaternion = ((PrimPath.PathPoint)Path.Path.get(i)).rot;
                for (j = 0; j < l; j++)
                {
                    int i1 = i * l + j;
                    Vector3Array vector3array = Mesh;
                    float f1 = ((LLVector3)Profile.Profile.get(j)).x;
                    vector3array.set(i1, llvector2.x * f1, ((LLVector3)Profile.Profile.get(j)).y * llvector2.y, 0.0F);
                    Mesh.mul(i1, llquaternion);
                    Mesh.add(i1, ((PrimPath.PathPoint)Path.Path.get(i)).pos);
                }

                i++;
            }
        } while (true);
        for (iterator = Profile.Faces.iterator(); iterator.hasNext();)
        {
            face = (PrimProfile.Face)iterator.next();
            i = FaceMask;
            FaceMask = face.FaceID | i;
        }

        return true;
        return false;
    }

    private int getNumFaces()
    {
        return Profile.Faces.size();
    }

    private boolean sculpt(int i, int j, int k, GLTexture gltexture, int l)
    {
        byte byte0;
        byte0 = volumeParams.SculptType;
        break MISSING_BLOCK_LABEL_9;
        boolean flag;
        int i1;
        int j1;
        if (i == 0 || j == 0 || k < 3 || gltexture == null)
        {
            l = -1;
            flag = true;
        } else
        {
            flag = false;
        }
        sculpt_calc_mesh_resolution(i, j, Detail);
        Path.generate(volumeParams.PathParams, Detail, 0, true, sculptRequestedS);
        Profile.generate(volumeParams.ProfileParams, Path.Open, Detail, 0, true, sculptRequestedT);
        i1 = Path.Path.size();
        j1 = Profile.Profile.size();
        if (i1 == 0 || j1 == 0)
        {
            return false;
        }
        break MISSING_BLOCK_LABEL_142;
        Mesh = new Vector3Array(i1 * j1);
        if (!flag)
        {
            try
            {
                sculptGenerateMapVertices(i, j, k, gltexture, byte0);
            }
            // Misplaced declaration of an exception variable
            catch (GLTexture gltexture)
            {
                return false;
            }
            for (i = 0; i < Profile.Faces.size(); i++)
            {
                j = FaceMask;
                FaceMask = ((PrimProfile.Face)Profile.Faces.get(i)).FaceID | j;
            }

            SculptLevel = l;
            VolumeFaces.clear();
            createVolumeFaces();
            return true;
        } else
        {
            return false;
        }
    }

    private void sculptGenerateMapVertices(int i, int j, int k, GLTexture gltexture, byte byte0)
    {
        byte byte1 = (byte)(byte0 & 7);
        boolean flag;
        int l;
        boolean flag1;
        int i2;
        int j2;
        int k2;
        int l2;
        if ((byte0 & 0x40) != 0)
        {
            l = 1;
        } else
        {
            l = 0;
        }
        if ((byte0 & 0xffffff80) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (l != 0)
        {
            flag1 = flag ^ true;
        } else
        {
            flag1 = flag;
        }
        k2 = Path.Path.size();
        l2 = Profile.Profile.size();
        j2 = 0;
        i2 = 0;
        while (j2 < k2) 
        {
            l = 0;
            while (l < l2) 
            {
                int i1;
                int j1;
                int k1;
                if (flag1)
                {
                    byte0 = l2 - l - 1;
                } else
                {
                    byte0 = l;
                }
                i1 = (int)(((float)byte0 / (float)(l2 - 1)) * (float)i);
                k1 = (int)(((float)j2 / (float)(k2 - 1)) * (float)j);
                byte0 = i1;
                if (k1 == 0)
                {
                    byte0 = i1;
                    if (byte1 == 1)
                    {
                        byte0 = i / 2;
                    }
                }
                i1 = k1;
                j1 = byte0;
                if (k1 == j)
                {
                    float f;
                    float f1;
                    float f2;
                    float f3;
                    int l1;
                    if (byte1 == 2)
                    {
                        l1 = 0;
                    } else
                    {
                        l1 = j - 1;
                    }
                    i1 = l1;
                    j1 = byte0;
                    if (byte1 == 1)
                    {
                        j1 = i / 2;
                        i1 = l1;
                    }
                }
                byte0 = j1;
                if (j1 == i)
                {
                    if (byte1 == 1 || byte1 == 2 || byte1 == 4)
                    {
                        byte0 = 0;
                    } else
                    {
                        byte0 = i - 1;
                    }
                }
                j1 = byte0;
                if (byte0 <= 0)
                {
                    j1 = 0;
                }
                l1 = j1;
                if (j1 >= i)
                {
                    l1 = i - 1;
                }
                byte0 = i1;
                if (i1 <= 0)
                {
                    byte0 = 0;
                }
                i1 = byte0;
                if (byte0 >= j)
                {
                    i1 = j - 1;
                }
                byte0 = gltexture.getRGB((i1 * i + l1) * k);
                f1 = (float)(byte0 >> 16 & 0xff) / 255F - 0.5F;
                f2 = (float)(byte0 >> 8 & 0xff) / 255F;
                f3 = (float)(byte0 & 0xff) / 255F;
                f = f1;
                if (flag)
                {
                    f = f1 * -1F;
                }
                Mesh.set(l + i2, f, f2 - 0.5F, f3 - 0.5F);
                l++;
            }
            j2++;
            i2 += l2;
        }
    }

    private void sculpt_calc_mesh_resolution(int i, int j, float f)
    {
        int k = (int)Math.pow(sculpt_sides(f), 2D);
        int l = (i * j) / 4;
        if (l > 0)
        {
            k = Math.min(k, l);
        }
        if (i == 0 || j == 0)
        {
            f = 1.0F;
        } else
        {
            f = (float)i / (float)j;
        }
        i = Math.max(k / Math.max((int)Math.sqrt((float)k / f), 4), 4);
        sculptRequestedS = k / i;
        sculptRequestedT = i;
    }

    private int sculpt_sides(float f)
    {
        if ((double)f <= 1.0D)
        {
            return 6;
        }
        if ((double)f <= 2D)
        {
            return 8;
        }
        return (double)f > 3D ? 32 : 16;
    }

    byte getPathType()
    {
        return volumeParams.PathParams.CurveType;
    }

    byte getProfileType()
    {
        return volumeParams.ProfileParams.CurveType;
    }
}
