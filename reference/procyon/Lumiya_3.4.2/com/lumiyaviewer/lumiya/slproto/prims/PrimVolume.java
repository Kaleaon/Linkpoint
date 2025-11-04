// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.GLTexture;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

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
    public ArrayList<PrimVolumeFace> VolumeFaces;
    private int sculptRequestedS;
    private int sculptRequestedT;
    PrimVolumeParams volumeParams;
    
    public PrimVolume() {
        this.VolumeFaces = new ArrayList<PrimVolumeFace>();
    }
    
    public static PrimVolume create(final PrimVolumeParams volumeParams, final float detail, final boolean generateSingleFace, final boolean unique, final GLTexture glTexture) {
        if (volumeParams.isSculpt()) {
            if (glTexture == null) {
                return null;
            }
            Debug.Log("Sculpt: using sculpt texture " + volumeParams.SculptID);
        }
        final PrimVolume primVolume = new PrimVolume();
        primVolume.volumeParams = volumeParams;
        primVolume.Detail = detail;
        primVolume.GenerateSingleFace = generateSingleFace;
        primVolume.Unique = unique;
        primVolume.Path = new PrimPath();
        primVolume.Profile = new PrimProfile();
        primVolume.FaceMask = 0;
        primVolume.LODScaleBias = new LLVector3(1.0f, 1.0f, 1.0f);
        if (!volumeParams.isSculpt()) {
            primVolume.generate();
            primVolume.createVolumeFaces();
            return primVolume;
        }
        if (!primVolume.sculpt(glTexture.getWidth(), glTexture.getHeight(), glTexture.getNumComponents(), glTexture, 1)) {
            return null;
        }
        return primVolume;
    }
    
    private void createVolumeFaces() {
        if (this.GenerateSingleFace) {
            return;
        }
        final int numFaces = this.getNumFaces();
        this.VolumeFaces.ensureCapacity(numFaces);
        for (int i = 0; i < numFaces; ++i) {
            final PrimVolumeFace e = new PrimVolumeFace();
            final PrimProfile.Face face = this.Profile.Faces.get(i);
            e.BeginS = face.Index;
            e.NumS = face.Count;
            e.BeginT = 0;
            e.NumT = this.Path.Path.size();
            e.ID = i;
            if (this.volumeParams.ProfileParams.Hollow > 0.0f) {
                e.TypeMask |= 0x40;
            }
            if (this.Profile.Open) {
                e.TypeMask |= 0x80;
            }
            if (face.Cap) {
                e.TypeMask |= 0x2;
                if (face.FaceID == 1) {
                    e.TypeMask |= 0x200;
                }
                else {
                    e.TypeMask |= 0x400;
                }
            }
            else if ((face.FaceID & 0x18) != 0x0) {
                e.TypeMask |= 0x104;
            }
            else {
                e.TypeMask |= 0x8;
                if (face.Flat) {
                    e.TypeMask |= 0x100;
                }
                if ((face.FaceID & 0x4) != 0x0) {
                    e.TypeMask |= 0x10;
                    if (face.Flat && e.NumS > 2) {
                        e.NumS *= 2;
                    }
                }
                else {
                    e.TypeMask |= 0x20;
                }
            }
            this.VolumeFaces.add(e);
        }
        final Iterator<Object> iterator = this.VolumeFaces.iterator();
        while (iterator.hasNext()) {
            iterator.next().create(this);
        }
    }
    
    private boolean generate() {
        int n2;
        final int n = n2 = (int)(this.Detail * 0.66f);
        Label_0088: {
            if (this.volumeParams.PathParams.CurveType == 16) {
                if (this.volumeParams.PathParams.ScaleX == 1.0f) {
                    n2 = n;
                    if (this.volumeParams.PathParams.ScaleY == 1.0f) {
                        break Label_0088;
                    }
                }
                if (this.volumeParams.ProfileParams.CurveType != 1 && this.volumeParams.ProfileParams.CurveType != 2 && this.volumeParams.ProfileParams.CurveType != 3) {
                    n2 = n;
                    if (this.volumeParams.ProfileParams.CurveType != 4) {
                        break Label_0088;
                    }
                }
                n2 = 0;
            }
        }
        this.LODScaleBias.set(0.5f, 0.5f, 0.5f);
        final float detail = this.Detail;
        final float detail2 = this.Detail;
        final byte curveType = this.volumeParams.PathParams.CurveType;
        final byte curveType2 = this.volumeParams.ProfileParams.CurveType;
        if (curveType == 16 && curveType2 == 0) {
            this.LODScaleBias.set(0.6f, 0.6f, 0.0f);
        }
        else if (curveType == 32) {
            this.LODScaleBias.set(0.6f, 0.6f, 0.6f);
        }
        int n3;
        if (this.volumeParams.isFlexible()) {
            n3 = this.volumeParams.FlexiParams.NumFlexiSections - 2;
        }
        else {
            n3 = n2;
        }
        final boolean generate = this.Path.generate(this.volumeParams.PathParams, detail2, n3, false, 0);
        final boolean generate2 = this.Profile.generate(this.volumeParams.ProfileParams, this.Path.Open, detail, n2, false, 0);
        if (generate || generate2) {
            final int size = this.Path.Path.size();
            final int size2 = this.Profile.Profile.size();
            this.Mesh = new Vector3Array(size2 * size);
            for (int i = 0; i < size; ++i) {
                final LLVector2 scale = ((PrimPath.PathPoint)this.Path.Path.get(i)).scale;
                final LLQuaternion rot = ((PrimPath.PathPoint)this.Path.Path.get(i)).rot;
                for (int j = 0; j < size2; ++j) {
                    final int n4 = i * size2 + j;
                    this.Mesh.set(n4, scale.x * this.Profile.Profile.get(j).x, this.Profile.Profile.get(j).y * scale.y, 0.0f);
                    this.Mesh.mul(n4, rot);
                    this.Mesh.add(n4, ((PrimPath.PathPoint)this.Path.Path.get(i)).pos);
                }
            }
            final Iterator<Object> iterator = this.Profile.Faces.iterator();
            while (iterator.hasNext()) {
                this.FaceMask |= iterator.next().FaceID;
            }
            return true;
        }
        return false;
    }
    
    private int getNumFaces() {
        return this.Profile.Faces.size();
    }
    
    private boolean sculpt(int i, int faceMask, final int n, final GLTexture glTexture, int sculptLevel) {
        final byte sculptType = this.volumeParams.SculptType;
        boolean b;
        if (i != 0 && faceMask != 0 && n >= 3 && glTexture != null) {
            b = false;
        }
        else {
            sculptLevel = -1;
            b = true;
        }
        this.sculpt_calc_mesh_resolution(i, faceMask, this.Detail);
        this.Path.generate(this.volumeParams.PathParams, this.Detail, 0, true, this.sculptRequestedS);
        this.Profile.generate(this.volumeParams.ProfileParams, this.Path.Open, this.Detail, 0, true, this.sculptRequestedT);
        final int size = this.Path.Path.size();
        final int size2 = this.Profile.Profile.size();
        if (size == 0 || size2 == 0) {
            return false;
        }
        this.Mesh = new Vector3Array(size * size2);
        if (!b) {
            try {
                this.sculptGenerateMapVertices(i, faceMask, n, glTexture, sculptType);
                for (i = 0; i < this.Profile.Faces.size(); ++i) {
                    faceMask = this.FaceMask;
                    this.FaceMask = (((PrimProfile.Face)this.Profile.Faces.get(i)).FaceID | faceMask);
                }
            }
            catch (final IndexOutOfBoundsException ex) {
                return false;
            }
            this.SculptLevel = sculptLevel;
            this.VolumeFaces.clear();
            this.createVolumeFaces();
            return true;
        }
        return false;
    }
    
    private void sculptGenerateMapVertices(final int n, final int n2, final int n3, final GLTexture glTexture, final byte b) {
        final byte b2 = (byte)(b & 0x7);
        int n4;
        if ((b & 0x40) != 0x0) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        boolean b3;
        if ((b & 0xFFFFFF80) != 0x0) {
            b3 = true;
        }
        else {
            b3 = false;
        }
        boolean b4;
        if (n4 != 0) {
            b4 = (b3 ^ true);
        }
        else {
            b4 = b3;
        }
        for (int size = this.Path.Path.size(), size2 = this.Profile.Profile.size(), i = 0, n5 = 0; i < size; ++i, n5 += size2) {
            for (int j = 0; j < size2; ++j) {
                int n6;
                if (b4) {
                    n6 = size2 - j - 1;
                }
                else {
                    n6 = j;
                }
                final int n7 = (int)(n6 / (float)(size2 - 1) * n);
                final int n8 = (int)(i / (float)(size - 1) * n2);
                int n9 = n7;
                if (n8 == 0) {
                    n9 = n7;
                    if (b2 == 1) {
                        n9 = n / 2;
                    }
                }
                int n10 = n8;
                int n11 = n9;
                if (n8 == n2) {
                    int n12;
                    if (b2 == 2) {
                        n12 = 0;
                    }
                    else {
                        n12 = n2 - 1;
                    }
                    n10 = n12;
                    n11 = n9;
                    if (b2 == 1) {
                        n11 = n / 2;
                        n10 = n12;
                    }
                }
                int n13;
                if ((n13 = n11) == n) {
                    if (b2 != 1 && b2 != 2 && b2 != 4) {
                        n13 = n - 1;
                    }
                    else {
                        n13 = 0;
                    }
                }
                int n14;
                if ((n14 = n13) <= 0) {
                    n14 = 0;
                }
                int n15;
                if ((n15 = n14) >= n) {
                    n15 = n - 1;
                }
                int n16;
                if ((n16 = n10) <= 0) {
                    n16 = 0;
                }
                int n17;
                if ((n17 = n16) >= n2) {
                    n17 = n2 - 1;
                }
                final int rgb = glTexture.getRGB((n17 * n + n15) * n3);
                final float n18 = (rgb >> 16 & 0xFF) / 255.0f - 0.5f;
                final float n19 = (rgb >> 8 & 0xFF) / 255.0f;
                final float n20 = (rgb & 0xFF) / 255.0f;
                float n21 = n18;
                if (b3) {
                    n21 = n18 * -1.0f;
                }
                this.Mesh.set(j + n5, n21, n19 - 0.5f, n20 - 0.5f);
            }
        }
    }
    
    private void sculpt_calc_mesh_resolution(int max, final int n, float n2) {
        int min = (int)Math.pow(this.sculpt_sides(n2), 2.0);
        final int b = max * n / 4;
        if (b > 0) {
            min = Math.min(min, b);
        }
        if (max == 0 || n == 0) {
            n2 = 1.0f;
        }
        else {
            n2 = max / (float)n;
        }
        max = Math.max(min / Math.max((int)Math.sqrt(min / n2), 4), 4);
        this.sculptRequestedS = min / max;
        this.sculptRequestedT = max;
    }
    
    private int sculpt_sides(final float n) {
        if (n <= 1.0) {
            return 6;
        }
        if (n <= 2.0) {
            return 8;
        }
        if (n <= 3.0) {
            return 16;
        }
        return 32;
    }
    
    byte getPathType() {
        return this.volumeParams.PathParams.CurveType;
    }
    
    byte getProfileType() {
        return this.volumeParams.ProfileParams.CurveType;
    }
}
