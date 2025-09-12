// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            SpatialObjectIndex, DrawListObjectEntry, SpatialTree

private class <init>
    implements Runnable
{

    final SpatialObjectIndex this$0;

    public void run()
    {
        if (!SpatialObjectIndex._2D_get6(SpatialObjectIndex.this) || !(SpatialObjectIndex._2D_get5(SpatialObjectIndex.this) ^ true))
        {
            break MISSING_BLOCK_LABEL_286;
        }
        Object obj1 = SpatialObjectIndex._2D_get7(SpatialObjectIndex.this);
        obj1;
        JVM INSTR monitorenter ;
        Object obj;
        DrawListObjectEntry adrawlistobjectentry[];
        adrawlistobjectentry = (DrawListObjectEntry[])SpatialObjectIndex._2D_get9(SpatialObjectIndex.this).toArray(new DrawListObjectEntry[SpatialObjectIndex._2D_get9(SpatialObjectIndex.this).size()]);
        obj = (DrawListObjectEntry[])SpatialObjectIndex._2D_get8(SpatialObjectIndex.this).toArray(new DrawListObjectEntry[SpatialObjectIndex._2D_get8(SpatialObjectIndex.this).size()]);
        SpatialObjectIndex._2D_get9(SpatialObjectIndex.this).clear();
        SpatialObjectIndex._2D_get8(SpatialObjectIndex.this).clear();
        obj1;
        JVM INSTR monitorexit ;
        boolean flag;
        int k = adrawlistobjectentry.length;
        int j = 0;
        flag = false;
        while (j < k) 
        {
            obj1 = adrawlistobjectentry[j];
            if (!((DrawListObjectEntry) (obj1)).getObjectInfo().isDead)
            {
                flag |= SpatialObjectIndex._2D_wrap1(SpatialObjectIndex.this, ((DrawListObjectEntry) (obj1)));
            } else
            {
                flag |= SpatialObjectIndex._2D_wrap0(SpatialObjectIndex.this, ((DrawListObjectEntry) (obj1)));
            }
            j++;
        }
        break MISSING_BLOCK_LABEL_195;
        obj;
        throw obj;
        int l = obj.length;
        boolean flag2 = false;
        boolean flag1 = flag;
        for (int i = ((flag2) ? 1 : 0); i < l; i++)
        {
            DrawListObjectEntry drawlistobjectentry = obj[i];
            flag1 |= SpatialObjectIndex._2D_wrap0(SpatialObjectIndex.this, drawlistobjectentry);
        }

        if (flag1 || SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).isDrawListChanged() || SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).isTreeWalkNeeded())
        {
            SpatialObjectIndex._2D_get1(SpatialObjectIndex.this).set(true);
        }
    }

    private Q()
    {
        this$0 = SpatialObjectIndex.this;
        super();
    }

    this._cls0(this._cls0 _pcls0)
    {
        this();
    }
}
