package com.lumiyaviewer.lumiya.render.glres.textures

import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.text.DrawableTextBitmap
import com.lumiyaviewer.lumiya.res.text.DrawableTextCache
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams

class GLTextTextureCache(
    glLoadQueue: GLLoadQueue,
    private val drawableTextCache: DrawableTextCache
) : GLResourceCache<DrawableTextParams, DrawableTextBitmap, GLLoadedTextTexture>(glLoadQueue) {

    override fun CancelRawResource(resourceConsumer: ResourceConsumer) {
        drawableTextCache.CancelRequest(resourceConsumer)
    }

    override fun GetResourceSize(rawResource: DrawableTextBitmap): Int {
        return 0
    }

    override fun LoadResource(
        resourceId: DrawableTextParams,
        rawResource: DrawableTextBitmap,
        renderContext: RenderContext
    ): GLLoadedTextTexture {
        return GLLoadedTextTexture(
            renderContext,
            rawResource.bitmap,
            rawResource.baselineOffset
        )
    }

    override fun RequestRawResource(resourceId: DrawableTextParams, resourceConsumer: ResourceConsumer) {
        drawableTextCache.RequestResource(resourceId, resourceConsumer)
    }
}
