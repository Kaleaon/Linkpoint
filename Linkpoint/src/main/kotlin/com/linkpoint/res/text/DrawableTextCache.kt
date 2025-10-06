package com.linkpoint.res.text

import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceMemoryCache
import com.linkpoint.res.ResourceRequest
import com.linkpoint.res.executors.PrimComputeExecutor

class DrawableTextCache : ResourceMemoryCache()<DrawableTextParams, DrawableTextBitmap> {
    private val Int fontSize

    @JvmStatic
private class TextGenRequest : ResourceRequest()<DrawableTextParams, DrawableTextBitmap> : Runnable {
        private val Int fontSize

        TextGenRequest(DrawableTextParams drawableTextParams, Int i, ResourceManager<DrawableTextParams, DrawableTextBitmap> resourceManager) {
            super(drawableTextParams, resourceManager)
            this.fontSize = i
        }

        fun execute() {
            PrimComputeExecutor.getInstance().execute(this)
        }

        fun run() {
            completeRequest(DrawableTextBitmap((DrawableTextParams) getParams(), this.fontSize))
        }
    }

    public DrawableTextCache(Int i) {
        this.fontSize = i
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<DrawableTextParams, DrawableTextBitmap> CreateNewRequest(DrawableTextParams drawableTextParams, ResourceManager<DrawableTextParams, DrawableTextBitmap> resourceManager) {
        return TextGenRequest(drawableTextParams, this.fontSize, resourceManager)
    }
}
