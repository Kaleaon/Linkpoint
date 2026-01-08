package com.lumiyaviewer.lumiya.render.glres.textures

import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.res.ResourceConsumer

class GLTextureCache {
    fun CancelRequest(consumer: ResourceConsumer) {}
    fun RequestResource(params: DrawableTextureParams, consumer: ResourceConsumer) {}
}
