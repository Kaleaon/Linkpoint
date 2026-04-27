// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.prims:
//            PrimVolumeParams

public class PrimDrawParams
{

    private final SLTextureEntry textures;
    private final PrimVolumeParams volumeParams;

    public PrimDrawParams(PrimVolumeParams primvolumeparams, SLTextureEntry sltextureentry)
    {
        volumeParams = primvolumeparams;
        textures = sltextureentry;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof PrimDrawParams))
        {
            return false;
        }
        obj = (PrimDrawParams)obj;
        boolean flag;
        boolean flag1;
        if (volumeParams == null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (((PrimDrawParams) (obj)).volumeParams == null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (flag != flag1)
        {
            return false;
        }
        if (volumeParams != null && !volumeParams.equals(((PrimDrawParams) (obj)).volumeParams))
        {
            return false;
        }
        if (textures == null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (((PrimDrawParams) (obj)).textures == null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (flag != flag1)
        {
            return false;
        }
        return textures == null || textures.equals(((PrimDrawParams) (obj)).textures);
    }

    public final SLTextureEntry getTextures()
    {
        return textures;
    }

    public final PrimVolumeParams getVolumeParams()
    {
        return volumeParams;
    }

    public int hashCode()
    {
        int i = 0;
        if (volumeParams != null)
        {
            i = volumeParams.hashCode() + 0;
        }
        int j = i;
        if (textures != null)
        {
            j = i + textures.hashCode();
        }
        return j;
    }
}
