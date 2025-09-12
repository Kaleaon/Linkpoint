// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.geometry;

import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry;
import com.lumiyaviewer.lumiya.render.drawable.DrawablePrim;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;

// Referenced classes of package com.lumiyaviewer.lumiya.res.geometry:
//            GeometryCache

public class PrimCache extends ResourceMemoryCache
{
    private static class PrimRequest extends ResourceRequest
        implements Runnable, ResourceConsumer
    {

        private volatile DrawableGeometry geometry;
        private final GeometryCache geometryCache;
        private final GLTextureCache glTextureCache;

        public void OnResourceReady(Object obj, boolean flag)
        {
            if (obj instanceof DrawableGeometry)
            {
                geometry = (DrawableGeometry)obj;
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
            geometryCache.CancelRequest(this);
            super.cancelRequest();
        }

        public void execute()
        {
            geometryCache.RequestResource(((PrimDrawParams)getParams()).getVolumeParams(), this);
        }

        public void run()
        {
            try
            {
                completeRequest(new DrawablePrim((PrimDrawParams)getParams(), geometry));
                return;
            }
            catch (Exception exception)
            {
                completeRequest(null);
            }
        }

        public PrimRequest(GLTextureCache gltexturecache, GeometryCache geometrycache, PrimDrawParams primdrawparams, ResourceManager resourcemanager)
        {
            super(primdrawparams, resourcemanager);
            glTextureCache = gltexturecache;
            geometryCache = geometrycache;
        }
    }


    private final GeometryCache geometryCache;
    private final GLTextureCache textureCache;

    public PrimCache(GLTextureCache gltexturecache, GeometryCache geometrycache)
    {
        textureCache = gltexturecache;
        geometryCache = geometrycache;
    }

    protected ResourceRequest CreateNewRequest(PrimDrawParams primdrawparams, ResourceManager resourcemanager)
    {
        return new PrimRequest(textureCache, geometryCache, primdrawparams, resourcemanager);
    }

    protected volatile ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        return CreateNewRequest((PrimDrawParams)obj, resourcemanager);
    }
}
