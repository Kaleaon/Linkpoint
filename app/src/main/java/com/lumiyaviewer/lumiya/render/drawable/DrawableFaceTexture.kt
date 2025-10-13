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
    private var textureRequested = false
    
    @Volatile
    private var loadedTexture: GLLoadedTexture? = null
    
    @Volatile
    private var hasAlphaLayer = false

    fun GLDraw(renderContext: RenderContext): Boolean {
        val texture = loadedTexture
        if (texture != null) {
            texture.GLDraw()
            return true
        }

        if (textureRequested) {
            return false
        }

        textureRequested = true
        glTextureCache = renderContext.drawableStore.glTextureCache
        renderContext.glResourceManager.addCleanable(this)
        glTextureCache?.RequestResource(drawableTextureParams, this)
        return false
    }

    override fun GLCleanup() {
        glTextureCache?.CancelRequest(this)
        textureRequested = false
        loadedTexture = null
        hasAlphaLayer = false
    }

    override fun OnResourceReady(resource: Any?, priority: Boolean) {
        when (resource) {
            is GLLoadedTexture -> {
                loadedTexture = resource
                hasAlphaLayer = resource.hasAlphaLayer()
            }
            null -> {
                loadedTexture = null
                hasAlphaLayer = false
            }
        }
    }

    internal fun hasAlphaLayer(): Boolean = hasAlphaLayer
}
