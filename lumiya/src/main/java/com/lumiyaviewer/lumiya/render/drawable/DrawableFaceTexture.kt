package com.lumiyaviewer.lumiya.render.drawable

import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLCleanable
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.res.ResourceConsumer

class DrawableFaceTexture(
    private val drawableTextureParams: DrawableTextureParams
) : ResourceConsumer, GLCleanable {

    private var glTextureCache: GLTextureCache? = null
    @Volatile private var hasAlphaLayer: Boolean = false
    @Volatile private var loadedTexture: GLLoadedTexture? = null
    private var textureRequested: Boolean = false

    override fun GLCleanup() {
        glTextureCache?.CancelRequest(this)
        textureRequested = false
        loadedTexture = null
        hasAlphaLayer = false
    }

    fun GLDraw(renderContext: RenderContext): Boolean {
        val currentTexture = loadedTexture
        if (currentTexture != null) {
            currentTexture.GLDraw()
            return true
        } else if (textureRequested) {
            return false
        } else {
            textureRequested = true
            val cache = renderContext.drawableStore?.glTextureCache
            if (cache != null) {
                glTextureCache = cache
                renderContext.glResourceManager.addCleanable(this)
                cache.RequestResource(drawableTextureParams, this)
            }
            return false
        }
    }

    override fun OnResourceReady(resource: Any?, isIntermediate: Boolean) {
        if (resource is GLLoadedTexture) {
            loadedTexture = resource
            hasAlphaLayer = resource.hasAlphaLayer()
        } else if (resource == null) {
            loadedTexture = null
            hasAlphaLayer = false
        }
    }

    fun hasAlphaLayer(): Boolean {
        return hasAlphaLayer
    }
}
