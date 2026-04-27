// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.geometry;

import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;

// Referenced classes of package com.lumiyaviewer.lumiya.res.geometry:
//            GeometryCache

private static class  extends ResourceRequest
    implements Runnable
{

    public void cancelRequest()
    {
        PrimComputeExecutor.getInstance().remove(this);
        super.cancelRequest();
    }

    public void execute()
    {
        PrimComputeExecutor.getInstance().execute(this);
    }

    public void run()
    {
        try
        {
            completeRequest(new DrawableGeometry((PrimVolumeParams)getParams(), null));
            return;
        }
        catch (Exception exception)
        {
            completeRequest(null);
        }
    }

    public (PrimVolumeParams primvolumeparams, ResourceManager resourcemanager)
    {
        super(primvolumeparams, resourcemanager);
    }
}
