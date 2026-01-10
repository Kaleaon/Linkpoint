package com.linkpoint.render.drawable

import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLCleanable
import com.linkpoint.render.glres.textures.GLLoadedTexture
import com.linkpoint.render.glres.textures.GLTextureCache
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.res.ResourceConsumer

class DrawableFaceTexture : ResourceConsumer, GLCleanable {
    private DrawableTextureParams drawableTextureParams
    private var glTextureCache: GLTextureCache = null
    private volatile Boolean hasAlphaLayer = false
    private volatile GLLoadedTexture loadedTexture = null
    private var textureRequested: Boolean = false

    constructor(drawableTextureParams2: DrawableTextureParams) {
        this.drawableTextureParams = drawableTextureParams2
    }

    fun GLCleanup()  {
        if (this.glTextureCache != null) {
            this.glTextureCache.CancelRequest(this)
        }
        this.textureRequested = false
        this.loadedTexture = null
        this.hasAlphaLayer = false
    }

    fun GLDraw(RenderContext renderContext): Boolean {
        GLLoadedTexture gLLoadedTexture = this.loadedTexture
        gLLoadedTexture?.GLDraw()
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

    fun OnResourceReady(obj: Any, z: Boolean)  {
        if (obj is GLLoadedTexture) {
            GLLoadedTexture gLLoadedTexture = (GLLoadedTexture) obj
            this.loadedTexture = gLLoadedTexture
            this.hasAlphaLayer = gLLoadedTexture.hasAlphaLayer()
        } else if (obj == null) {
            this.loadedTexture = null
            this.hasAlphaLayer = false
        }
    }

    /* access modifiers changed from: package-private */
    fun hasAlphaLayer(): Boolean {
        return this.hasAlphaLayer
    }
}
