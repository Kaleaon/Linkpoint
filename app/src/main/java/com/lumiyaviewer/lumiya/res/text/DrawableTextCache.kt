package com.lumiyaviewer.lumiya.res.text

import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor

class DrawableTextCache : ResourceMemoryCache<DrawableTextParams, DrawableTextBitmap> {
    private Int fontSize

    private class TextGenRequest : ResourceRequest<DrawableTextParams, DrawableTextBitmap> : Runnable {
        private Int fontSize

        TextGenRequest(DrawableTextParams drawableTextParams, Int i, ResourceManager<DrawableTextParams, DrawableTextBitmap> resourceManager) {
            super(drawableTextParams, resourceManager)
            this.fontSize = i
        }

        fun execute(): Unit {
            PrimComputeExecutor.getInstance().execute(this)
        }

        fun run(): Unit {
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
