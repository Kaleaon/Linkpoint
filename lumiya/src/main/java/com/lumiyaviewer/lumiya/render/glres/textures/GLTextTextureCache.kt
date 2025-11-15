package com.lumiyaviewer.lumiya.render.glres.textures

import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.text.DrawableTextBitmap
import com.lumiyaviewer.lumiya.res.text.DrawableTextCache
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams

class GLTextTextureCache : GLResourceCache<DrawableTextParams, DrawableTextBitmap, GLLoadedTextTexture> {
    private DrawableTextCache drawableTextCache

    constructor(gLLoadQueue: GLLoadQueue, drawableTextCache2: DrawableTextCache) {
        super(gLLoadQueue)
        this.drawableTextCache = drawableTextCache2
    }

    /* access modifiers changed from: protected */
    fun CancelRawResource(resourceConsumer: ResourceConsumer): Unit {
        this.drawableTextCache.CancelRequest(resourceConsumer)
    }

    /* access modifiers changed from: protected */
    fun GetResourceSize(drawableTextBitmap: DrawableTextBitmap): Int {
        return 0
    }

    /* access modifiers changed from: protected */
    fun LoadResource(drawableTextParams: DrawableTextParams, drawableTextBitmap: DrawableTextBitmap, renderContext: RenderContext): GLLoadedTextTexture {
        return GLLoadedTextTexture(renderContext, drawableTextBitmap.getBitmap(), drawableTextBitmap.getBaselineOffset())
    }

    /* access modifiers changed from: protected */
    fun RequestRawResource(drawableTextParams: DrawableTextParams, resourceConsumer: ResourceConsumer): Unit {
        this.drawableTextCache.RequestResource(drawableTextParams, resourceConsumer)
    }
}
