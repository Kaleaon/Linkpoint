// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.geometry;

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;

// Referenced classes of package com.lumiyaviewer.lumiya.res.geometry:
//            GeometryCache

private static class getParams extends ResourceRequest
    implements Runnable, ResourceConsumer
{

    private volatile OpenJPEG sculptData;
    private final DrawableTextureParams sculptTextureParams;

    public void OnResourceReady(Object obj, boolean flag)
    {
        if (obj instanceof OpenJPEG)
        {
            sculptData = (OpenJPEG)obj;
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
        TextureCache.getInstance().CancelRequest(this);
        super.cancelRequest();
    }

    public void execute()
    {
        TextureCache.getInstance().RequestResource(sculptTextureParams, this);
    }

    public void run()
    {
        try
        {
            completeRequest(new DrawableGeometry((PrimVolumeParams)getParams(), sculptData));
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
        sculptTextureParams = DrawableTextureParams.create(((PrimVolumeParams)getParams()).SculptID, TextureClass.Sculpt);
    }
}
