package com.lumiyaviewer.lumiya.render.drawable;

import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;

public class DrawableFaceTexture implements ResourceConsumer, GLCleanable {
    private final DrawableTextureParams drawableTextureParams;
    private GLTextureCache glTextureCache = null;
    private volatile boolean hasAlphaLayer = false;
    private volatile GLLoadedTexture loadedTexture = null;
    private boolean textureRequested = false;

    public DrawableFaceTexture(DrawableTextureParams drawableTextureParams) {
        this.drawableTextureParams = drawableTextureParams;
    }

    public void GLCleanup() {
        if (this.glTextureCache != null) {
            this.glTextureCache.CancelRequest(this);
        }
        this.textureRequested = false;
        this.loadedTexture = null;
        this.hasAlphaLayer = false;
    }

    public final boolean GLDraw(RenderContext renderContext) {
        GLLoadedTexture gLLoadedTexture = this.loadedTexture;
        if (gLLoadedTexture != null) {
            gLLoadedTexture.GLDraw();
            return true;
        }
        if (!this.textureRequested) {
            this.textureRequested = true;
            this.glTextureCache = renderContext.drawableStore.glTextureCache;
            renderContext.glResourceManager.addCleanable(this);
            this.glTextureCache.RequestResource(this.drawableTextureParams, this);
        }
        return false;
    }

    public void OnResourceReady(Object obj, boolean z) {
        if (obj instanceof GLLoadedTexture) {
            GLLoadedTexture gLLoadedTexture = (GLLoadedTexture) obj;
            this.loadedTexture = gLLoadedTexture;
            this.hasAlphaLayer = gLLoadedTexture.hasAlphaLayer();
        } else if (obj == null) {
            this.loadedTexture = null;
            this.hasAlphaLayer = false;
        }
    }

    boolean hasAlphaLayer() {
        return this.hasAlphaLayer;
    }
}
