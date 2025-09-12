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
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.res.mesh.MeshCache;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;

public class GeometryCache extends ResourceMemoryCache
{
    private static class MeshGeometryRequest extends ResourceRequest
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

        public MeshGeometryRequest(MeshCache meshcache, PrimVolumeParams primvolumeparams, ResourceManager resourcemanager)
        {
            super(primvolumeparams, resourcemanager);
            meshCache = meshcache;
        }
    }

    private static class SculptGeometryRequest extends ResourceRequest
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

        public SculptGeometryRequest(PrimVolumeParams primvolumeparams, ResourceManager resourcemanager)
        {
            super(primvolumeparams, resourcemanager);
            sculptTextureParams = DrawableTextureParams.create(((PrimVolumeParams)getParams()).SculptID, TextureClass.Sculpt);
        }
    }

    private static class SimpleGeometryRequest extends ResourceRequest
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

        public SimpleGeometryRequest(PrimVolumeParams primvolumeparams, ResourceManager resourcemanager)
        {
            super(primvolumeparams, resourcemanager);
        }
    }


    private final MeshCache meshCache;

    public GeometryCache(MeshCache meshcache)
    {
        meshCache = meshcache;
    }

    protected ResourceRequest CreateNewRequest(PrimVolumeParams primvolumeparams, ResourceManager resourcemanager)
    {
        if (primvolumeparams.isMesh())
        {
            return new MeshGeometryRequest(meshCache, primvolumeparams, resourcemanager);
        }
        if (primvolumeparams.isSculpt())
        {
            return new SculptGeometryRequest(primvolumeparams, resourcemanager);
        } else
        {
            return new SimpleGeometryRequest(primvolumeparams, resourcemanager);
        }
    }

    protected volatile ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        return CreateNewRequest((PrimVolumeParams)obj, resourcemanager);
    }
}
