// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.drawable;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;

public class DrawableFaceTexture implements ResourceConsumer, GLCleanable
{
    private final DrawableTextureParams drawableTextureParams;
    private GLTextureCache glTextureCache;
    private volatile boolean hasAlphaLayer;
    private volatile GLLoadedTexture loadedTexture;
    private boolean textureRequested;
    
    public DrawableFaceTexture(final DrawableTextureParams drawableTextureParams) {
        this.glTextureCache = null;
        this.textureRequested = false;
        this.loadedTexture = null;
        this.hasAlphaLayer = false;
        this.drawableTextureParams = drawableTextureParams;
    }
    
    @Override
    public void GLCleanup() {
        if (this.glTextureCache != null) {
            this.glTextureCache.CancelRequest(this);
        }
        this.textureRequested = false;
        this.loadedTexture = null;
        this.hasAlphaLayer = false;
    }
    
    public final boolean GLDraw(final RenderContext renderContext) {
        final GLLoadedTexture loadedTexture = this.loadedTexture;
        if (loadedTexture != null) {
            loadedTexture.GLDraw();
            return true;
        }
        if (!this.textureRequested) {
            this.textureRequested = true;
            this.glTextureCache = renderContext.drawableStore.glTextureCache;
            renderContext.glResourceManager.addCleanable(this);
            ((ResourceMemoryCache<ResourceParams, ResourceType>)this.glTextureCache).RequestResource((ResourceParams)this.drawableTextureParams, this);
        }
        return false;
    }
    
    @Override
    public void OnResourceReady(final Object o, final boolean b) {
        if (o instanceof GLLoadedTexture) {
            final GLLoadedTexture loadedTexture = (GLLoadedTexture)o;
            this.loadedTexture = loadedTexture;
            this.hasAlphaLayer = loadedTexture.hasAlphaLayer();
        }
        else if (o == null) {
            this.loadedTexture = null;
            this.hasAlphaLayer = false;
        }
    }
    
    boolean hasAlphaLayer() {
        return this.hasAlphaLayer;
    }
}
