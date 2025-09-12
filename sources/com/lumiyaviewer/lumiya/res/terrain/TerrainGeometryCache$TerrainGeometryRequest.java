// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.terrain.TerrainPatchGeometry;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap;

// Referenced classes of package com.lumiyaviewer.lumiya.res.terrain:
//            TerrainGeometryCache

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
            completeRequest(new TerrainPatchGeometry((TerrainPatchHeightMap)getParams()));
            return;
        }
        catch (Exception exception)
        {
            Debug.Warning(exception);
        }
        completeRequest(null);
    }

    public (TerrainPatchHeightMap terrainpatchheightmap, ResourceManager resourcemanager)
    {
        super(terrainpatchheightmap, resourcemanager);
    }
}
