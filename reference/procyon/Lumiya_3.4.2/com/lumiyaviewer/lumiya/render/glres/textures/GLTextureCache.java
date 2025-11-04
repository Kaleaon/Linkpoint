// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;

public class GLTextureCache extends GLResourceCache<DrawableTextureParams, OpenJPEG, GLLoadedTexture>
{
    public GLTextureCache(final GLLoadQueue glLoadQueue) {
        super(glLoadQueue);
    }
    
    @Override
    protected void CancelRawResource(final ResourceConsumer resourceConsumer) {
        TextureCache.getInstance().CancelRequest(resourceConsumer);
    }
    
    @Override
    protected int GetResourceSize(final OpenJPEG openJPEG) {
        return openJPEG.getLoadedSize();
    }
    
    @Override
    protected GLLoadedTexture LoadResource(final DrawableTextureParams drawableTextureParams, final OpenJPEG openJPEG, final RenderContext renderContext) {
        return new GLLoadedTexture(renderContext, openJPEG);
    }
    
    @Override
    protected void RequestRawResource(final DrawableTextureParams drawableTextureParams, final ResourceConsumer resourceConsumer) {
        ((ResourceMemoryCache<DrawableTextureParams, ResourceType>)TextureCache.getInstance()).RequestResource(drawableTextureParams, resourceConsumer);
    }
}
