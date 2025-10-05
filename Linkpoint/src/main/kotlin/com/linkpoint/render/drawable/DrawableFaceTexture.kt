package com.linkpoint.render.drawable

import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLCleanable
import com.linkpoint.render.glres.textures.GLLoadedTexture
import com.linkpoint.render.glres.textures.GLTextureCache
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.res.ResourceConsumer

class DrawableFaceTexture : ResourceConsumer, GLCleanable {
    private val DrawableTextureParams drawableTextureParams
    private GLTextureCache glTextureCache = null
    private volatile Boolean hasAlphaLayer = false
    private volatile GLLoadedTexture loadedTexture = null
    private Boolean textureRequested = false

    public DrawableFaceTexture(DrawableTextureParams drawableTextureParams2) {
        this.drawableTextureParams = drawableTextureParams2
    }

    fun GLCleanup() {
        if (this.glTextureCache != null) {
            this.glTextureCache.CancelRequest(this)
        }
        this.textureRequested = false
        this.loadedTexture = null
        this.hasAlphaLayer = false
    }

    val Boolean GLDraw(RenderContext renderContext) {
        GLLoadedTexture gLLoadedTexture = this.loadedTexture
        if (gLLoadedTexture != null) {
            gLLoadedTexture.GLDraw()
            return true
        } else if (this.textureRequested) {
            return false
        } else {
            this.textureRequested = true
            this.glTextureCache = renderContext.drawableStore.glTextureCache
            renderContext.glResourceManager.addCleanable(this)
            this.glTextureCache.RequestResource(this.drawableTextureParams, this)
            return false
        }
    }

    fun OnResourceReady(Object obj, Boolean z) {
        if (obj instanceof GLLoadedTexture) {
            GLLoadedTexture gLLoadedTexture = (GLLoadedTexture) obj
            this.loadedTexture = gLLoadedTexture
            this.hasAlphaLayer = gLLoadedTexture.hasAlphaLayer()
        } else if (obj == null) {
            this.loadedTexture = null
            this.hasAlphaLayer = false
        }
    }

    /* access modifiers changed from: package-private */
    public Boolean hasAlphaLayer() {
        return this.hasAlphaLayer
    }
}
