package com.lumiyaviewer.lumiya.render.glres.textures

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.textures.TextureCache

class GLTextureCache : GLResourceCache<DrawableTextureParams, OpenJPEG, GLLoadedTexture> {
    constructor(gLLoadQueue: GLLoadQueue) {
        super(gLLoadQueue)
    }

    // access modifiers changed from: protected
    fun CancelRawResource(resourceConsumer: ResourceConsumer) {
        TextureCache.getInstance().CancelRequest(resourceConsumer)
    }

    // access modifiers changed from: protected
    fun GetResourceSize(openJPEG: OpenJPEG): Int {
        return openJPEG.getLoadedSize()
    }

    // access modifiers changed from: protected
    fun LoadResource(
        drawableTextureParams: DrawableTextureParams,
        openJPEG: OpenJPEG,
        renderContext: RenderContext,
    ): GLLoadedTexture {
        return GLLoadedTexture(renderContext, openJPEG)
    }

    // access modifiers changed from: protected
    fun RequestRawResource(
        drawableTextureParams: DrawableTextureParams,
        resourceConsumer: ResourceConsumer,
    ) {
        TextureCache.getInstance().RequestResource(drawableTextureParams, resourceConsumer)
    }
}
