package com.lumiyaviewer.lumiya.render.drawable

import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLCleanable
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.res.ResourceConsumer

class DrawableFaceTexture : ResourceConsumer, GLCleanable {
    private DrawableTextureParams drawableTextureParams
    private var glTextureCache: GLTextureCache = null
    private volatile Boolean hasAlphaLayer = false
    private volatile GLLoadedTexture loadedTexture = null
    private var textureRequested: Boolean = false

    constructor(drawableTextureParams2: DrawableTextureParams) {
        this.drawableTextureParams = drawableTextureParams2
    }

    fun GLCleanup(): Unit {
        if (this.glTextureCache != null) {
            this.glTextureCache.CancelRequest(this)
        }
        this.textureRequested = false
        this.loadedTexture = null
        this.hasAlphaLayer = false
    }

    Boolean GLDraw(RenderContext renderContext) {
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

    fun OnResourceReady(obj: Any, z: Boolean): Unit {
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
    fun hasAlphaLayer(): Boolean {
        return this.hasAlphaLayer
    }
}
