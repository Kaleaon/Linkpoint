// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            SpatialObjectIndex, SpatialTree, FrustrumInfo

private class <init>
    implements Runnable, com.lumiyaviewer.lumiya.res.collections.init>
{

    final SpatialObjectIndex this$0;

    public void run()
    {
        if (SpatialObjectIndex._2D_get6(SpatialObjectIndex.this) && SpatialObjectIndex._2D_get5(SpatialObjectIndex.this) ^ true)
        {
            boolean flag;
            if (!SpatialObjectIndex._2D_get2(SpatialObjectIndex.this).getAndSet(false))
            {
                flag = SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).isTreeWalkNeeded();
            } else
            {
                flag = true;
            }
            if (flag)
            {
                FrustrumPlanes frustrumplanes = SpatialObjectIndex._2D_get4(SpatialObjectIndex.this);
                FrustrumInfo frustruminfo = SpatialObjectIndex._2D_get3(SpatialObjectIndex.this);
                if (frustrumplanes != null && frustruminfo != null)
                {
                    SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).walkTree(frustrumplanes, frustruminfo.viewDistance);
                }
            }
            if (SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).isDrawListChanged())
            {
                SpatialObjectIndex._2D_set0(SpatialObjectIndex.this, SpatialObjectIndex._2D_wrap2(SpatialObjectIndex.this, SpatialObjectIndex._2D_get0(SpatialObjectIndex.this)));
            }
        }
    }

    private ()
    {
        this$0 = SpatialObjectIndex.this;
        super();
    }

    this._cls0(this._cls0 _pcls0)
    {
        this();
    }
}
