package com.linkpoint.render.glres.textures

import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLLoadQueue
import com.linkpoint.render.glres.GLResourceCache
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.textures.TextureCache

class GLTextureCache : GLResourceCache()<DrawableTextureParams, OpenJPEG, GLLoadedTexture> {
    public GLTextureCache(GLLoadQueue gLLoadQueue) {
        super(gLLoadQueue)
    }

    /* access modifiers changed from: protected */
    fun CancelRawResource(ResourceConsumer resourceConsumer) {
        TextureCache.getInstance().CancelRequest(resourceConsumer)
    }

    /* access modifiers changed from: protected */
    public Int GetResourceSize(OpenJPEG openJPEG) {
        return openJPEG.getLoadedSize()
    }

    /* access modifiers changed from: protected */
    public GLLoadedTexture LoadResource(DrawableTextureParams drawableTextureParams, OpenJPEG openJPEG, RenderContext renderContext) {
        return GLLoadedTexture(renderContext, openJPEG)
    }

    /* access modifiers changed from: protected */
    fun RequestRawResource(DrawableTextureParams drawableTextureParams, ResourceConsumer resourceConsumer) {
        TextureCache.getInstance().RequestResource(drawableTextureParams, resourceConsumer)
    }
}
