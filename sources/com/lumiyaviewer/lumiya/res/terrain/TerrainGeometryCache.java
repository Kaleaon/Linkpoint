// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.terrain.TerrainPatchGeometry;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap;

public class TerrainGeometryCache extends ResourceMemoryCache
{
    private static class TerrainGeometryRequest extends ResourceRequest
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
                completeRequest(new TerrainPatchGeometry((TerrainPatchHeightMap)getParams()));
                return;
            }
            catch (Exception exception)
            {
                Debug.Warning(exception);
            }
            completeRequest(null);
        }

        public TerrainGeometryRequest(TerrainPatchHeightMap terrainpatchheightmap, ResourceManager resourcemanager)
        {
            super(terrainpatchheightmap, resourcemanager);
        }
    }


    public TerrainGeometryCache()
    {
    }

    protected ResourceRequest CreateNewRequest(TerrainPatchHeightMap terrainpatchheightmap, ResourceManager resourcemanager)
    {
        return new TerrainGeometryRequest(terrainpatchheightmap, resourcemanager);
    }

    protected volatile ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        return CreateNewRequest((TerrainPatchHeightMap)obj, resourcemanager);
    }
}
