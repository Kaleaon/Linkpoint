// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.utils.InternPool;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.prims:
//            PrimDrawParams, PrimPathParams, PrimProfileParams, PrimVolumeParams

public class PrimParamsPool
{

    public static InternPool drawParamsPool = new InternPool();
    public static InternPool pathParamsPool = new InternPool();
    public static InternPool profileParamsPool = new InternPool();
    public static InternPool volumeParamsPool = new InternPool();

    public PrimParamsPool()
    {
    }

    public static PrimDrawParams get(PrimDrawParams primdrawparams)
    {
        return (PrimDrawParams)drawParamsPool.intern(primdrawparams);
    }

    public static PrimPathParams get(PrimPathParams primpathparams)
    {
        return (PrimPathParams)pathParamsPool.intern(primpathparams);
    }

    public static PrimProfileParams get(PrimProfileParams primprofileparams)
    {
        return (PrimProfileParams)profileParamsPool.intern(primprofileparams);
    }

    public static PrimVolumeParams get(PrimVolumeParams primvolumeparams)
    {
        return (PrimVolumeParams)volumeParamsPool.intern(primvolumeparams);
    }

}
