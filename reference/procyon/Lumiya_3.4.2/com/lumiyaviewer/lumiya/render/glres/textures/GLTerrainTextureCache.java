// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.res.terrain.TerrainTextureCache;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;

public class GLTerrainTextureCache extends GLResourceCache<TerrainPatchInfo, OpenJPEG, GLLoadedTexture>
{
    private final TerrainTextureCache terrainTextureCache;
    
    public GLTerrainTextureCache(final GLLoadQueue glLoadQueue, final TerrainTextureCache terrainTextureCache) {
        super(glLoadQueue);
        this.terrainTextureCache = terrainTextureCache;
    }
    
    @Override
    protected void CancelRawResource(final ResourceConsumer resourceConsumer) {
        this.terrainTextureCache.CancelRequest(resourceConsumer);
    }
    
    @Override
    protected int GetResourceSize(final OpenJPEG openJPEG) {
        return openJPEG.getLoadedSize();
    }
    
    @Override
    protected GLLoadedTexture LoadResource(final TerrainPatchInfo terrainPatchInfo, final OpenJPEG openJPEG, final RenderContext renderContext) {
        Debug.Printf("Terrain: Loading baked texture into GL", new Object[0]);
        return new GLLoadedTexture(renderContext, openJPEG);
    }
    
    @Override
    protected void RequestRawResource(final TerrainPatchInfo terrainPatchInfo, final ResourceConsumer resourceConsumer) {
        ((ResourceMemoryCache<TerrainPatchInfo, ResourceType>)this.terrainTextureCache).RequestResource(terrainPatchInfo, resourceConsumer);
    }
}
