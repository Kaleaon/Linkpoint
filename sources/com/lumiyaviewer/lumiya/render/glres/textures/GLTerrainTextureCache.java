// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.terrain.TerrainTextureCache;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres.textures:
//            GLLoadedTexture

public class GLTerrainTextureCache extends GLResourceCache
{

    private final TerrainTextureCache terrainTextureCache;

    public GLTerrainTextureCache(GLLoadQueue glloadqueue, TerrainTextureCache terraintexturecache)
    {
        super(glloadqueue);
        terrainTextureCache = terraintexturecache;
    }

    protected void CancelRawResource(ResourceConsumer resourceconsumer)
    {
        terrainTextureCache.CancelRequest(resourceconsumer);
    }

    protected int GetResourceSize(OpenJPEG openjpeg)
    {
        return openjpeg.getLoadedSize();
    }

    protected volatile int GetResourceSize(Object obj)
    {
        return GetResourceSize((OpenJPEG)obj);
    }

    protected volatile GLSizedResource LoadResource(Object obj, Object obj1, RenderContext rendercontext)
    {
        return LoadResource((TerrainPatchInfo)obj, (OpenJPEG)obj1, rendercontext);
    }

    protected GLLoadedTexture LoadResource(TerrainPatchInfo terrainpatchinfo, OpenJPEG openjpeg, RenderContext rendercontext)
    {
        Debug.Printf("Terrain: Loading baked texture into GL", new Object[0]);
        return new GLLoadedTexture(rendercontext, openjpeg);
    }

    protected void RequestRawResource(TerrainPatchInfo terrainpatchinfo, ResourceConsumer resourceconsumer)
    {
        terrainTextureCache.RequestResource(terrainpatchinfo, resourceconsumer);
    }

    protected volatile void RequestRawResource(Object obj, ResourceConsumer resourceconsumer)
    {
        RequestRawResource((TerrainPatchInfo)obj, resourceconsumer);
    }
}
