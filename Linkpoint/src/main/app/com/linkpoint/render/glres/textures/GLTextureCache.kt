package com.linkpoint.render.glres.textures

import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLLoadQueue
import com.linkpoint.render.glres.GLResourceCache
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.textures.TextureCache

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
