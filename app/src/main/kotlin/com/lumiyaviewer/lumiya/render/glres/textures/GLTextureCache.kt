package com.lumiyaviewer.lumiya.render.glres.textures

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.textures.TextureCache

class GLTextureCache(
    glLoadQueue: GLLoadQueue
) : GLResourceCache<DrawableTextureParams, OpenJPEG, GLLoadedTexture>(glLoadQueue) {

    override fun CancelRawResource(resourceConsumer: ResourceConsumer) {
        TextureCache.getInstance().CancelRequest(resourceConsumer)
    }

    override fun GetResourceSize(rawResource: OpenJPEG): Int {
        return rawResource.loadedSize
    }

    override fun LoadResource(
        resourceId: DrawableTextureParams,
        rawResource: OpenJPEG,
        renderContext: RenderContext
    ): GLLoadedTexture {
        return GLLoadedTexture(renderContext, rawResource)
    }

    override fun RequestRawResource(resourceId: DrawableTextureParams, resourceConsumer: ResourceConsumer) {
        TextureCache.getInstance().RequestResource(resourceId, resourceConsumer)
    }
}
