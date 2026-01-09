package com.linkpoint.res.text

import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceMemoryCache
import com.linkpoint.res.ResourceRequest
import com.linkpoint.res.executors.PrimComputeExecutor

class DrawableTextCache : ResourceMemoryCache<DrawableTextParams, DrawableTextBitmap> {
    private Int fontSize

    private class TextGenRequest : ResourceRequest<DrawableTextParams, DrawableTextBitmap> : Runnable {
        private Int fontSize

        TextGenRequest(DrawableTextParams drawableTextParams, Int i, ResourceManager<DrawableTextParams, DrawableTextBitmap> resourceManager) {
            super(drawableTextParams, resourceManager)
            this.fontSize = i
        }

        fun execute()  {
            PrimComputeExecutor.getInstance().execute(this)
        }

        fun run()  {
            completeRequest(DrawableTextBitmap((DrawableTextParams) getParams(), this.fontSize))
        }
    }

    constructor(i: Int) {
        this.fontSize = i
    }

    /* access modifiers changed from: protected */
    fun CreateNewRequest(drawableTextParams: DrawableTextParams, resourceManager: DrawableTextBitmap>): ResourceRequest<DrawableTextParams, DrawableTextBitmap> {
        return TextGenRequest(drawableTextParams, this.fontSize, resourceManager)
    }
}
