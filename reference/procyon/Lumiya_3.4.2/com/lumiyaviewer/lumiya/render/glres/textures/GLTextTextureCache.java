// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.res.text.DrawableTextCache;
import com.lumiyaviewer.lumiya.res.text.DrawableTextBitmap;
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;

public class GLTextTextureCache extends GLResourceCache<DrawableTextParams, DrawableTextBitmap, GLLoadedTextTexture>
{
    private final DrawableTextCache drawableTextCache;
    
    public GLTextTextureCache(final GLLoadQueue glLoadQueue, final DrawableTextCache drawableTextCache) {
        super(glLoadQueue);
        this.drawableTextCache = drawableTextCache;
    }
    
    @Override
    protected void CancelRawResource(final ResourceConsumer resourceConsumer) {
        this.drawableTextCache.CancelRequest(resourceConsumer);
    }
    
    @Override
    protected int GetResourceSize(final DrawableTextBitmap drawableTextBitmap) {
        return 0;
    }
    
    @Override
    protected GLLoadedTextTexture LoadResource(final DrawableTextParams drawableTextParams, final DrawableTextBitmap drawableTextBitmap, final RenderContext renderContext) {
        return new GLLoadedTextTexture(renderContext, drawableTextBitmap.getBitmap(), drawableTextBitmap.getBaselineOffset());
    }
    
    @Override
    protected void RequestRawResource(final DrawableTextParams drawableTextParams, final ResourceConsumer resourceConsumer) {
        ((ResourceMemoryCache<DrawableTextParams, ResourceType>)this.drawableTextCache).RequestResource(drawableTextParams, resourceConsumer);
    }
}
