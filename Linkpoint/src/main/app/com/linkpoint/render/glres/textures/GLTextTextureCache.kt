package com.linkpoint.render.glres.textures

import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLLoadQueue
import com.linkpoint.render.glres.GLResourceCache
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.text.DrawableTextBitmap
import com.linkpoint.res.text.DrawableTextCache
import com.linkpoint.res.text.DrawableTextParams

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
