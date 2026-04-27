// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.geometry;

import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.res.mesh.MeshCache;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;

// Referenced classes of package com.lumiyaviewer.lumiya.res.geometry:
//            GeometryCache

private static class meshCache extends ResourceRequest
    implements Runnable, ResourceConsumer
{

    private final MeshCache meshCache;
    private volatile MeshData meshData;

    public void OnResourceReady(Object obj, boolean flag)
    {
        if (obj instanceof MeshData)
        {
            meshData = (MeshData)obj;
            PrimComputeExecutor.getInstance().execute(this);
            return;
        } else
        {
            completeRequest(null);
            return;
        }
    }

    public void cancelRequest()
    {
        PrimComputeExecutor.getInstance().remove(this);
        super.cancelRequest();
    }

    public void execute()
    {
        meshCache.RequestResource(((PrimVolumeParams)getParams()).SculptID, this);
    }

    public void run()
    {
        try
        {
            completeRequest(new DrawableGeometry(meshData));
            return;
        }
        catch (Exception exception)
        {
            completeRequest(null);
        }
    }

    public (MeshCache meshcache, PrimVolumeParams primvolumeparams, ResourceManager resourcemanager)
    {
        super(primvolumeparams, resourcemanager);
        meshCache = meshcache;
    }
}
