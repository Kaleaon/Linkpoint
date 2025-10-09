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
    @Volatile
    private var hasAlphaLayer = false
    @Volatile
    private var loadedTexture: GLLoadedTexture? = null
    private var textureRequested = false

    override fun GLCleanup() {
        glTextureCache?.CancelRequest(this)
        textureRequested = false
        loadedTexture = null
        hasAlphaLayer = false
    }

    fun GLDraw(renderContext: RenderContext): Boolean {
        val texture = loadedTexture
        
        return if (texture != null) {
            texture.GLDraw()
            true
        } else if (textureRequested) {
            false
        } else {
            textureRequested = true
            glTextureCache = renderContext.drawableStore.glTextureCache
            renderContext.glResourceManager.addCleanable(this)
            glTextureCache!!.RequestResource(drawableTextureParams, this)
            false
        }
    }

    override fun OnResourceReady(obj: Any?, z: Boolean) {
        when (obj) {
            is GLLoadedTexture -> {
                loadedTexture = obj
                hasAlphaLayer = obj.hasAlphaLayer()
            }
            null -> {
                loadedTexture = null
                hasAlphaLayer = false
            }
        }
    }

    internal fun hasAlphaLayer(): Boolean {
        return hasAlphaLayer
    }
}
