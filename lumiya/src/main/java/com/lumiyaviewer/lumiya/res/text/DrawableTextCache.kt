package com.lumiyaviewer.lumiya.res.text

import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor
import com.lumiyaviewer.lumiya.memory.MemoryManager

class DrawableTextCache(private val fontSize: Int) : ResourceMemoryCache<DrawableTextParams, DrawableTextBitmap>(MemoryManager()) {

    private inner class TextGenRequest(
        params: DrawableTextParams,
        private val fontSize: Int,
        manager: ResourceManager<DrawableTextParams, DrawableTextBitmap>
    ) : ResourceRequest<DrawableTextParams, DrawableTextBitmap>(params, manager), Runnable {

        override fun execute() {
            PrimComputeExecutor.getInstance().execute(this)
        }

        override fun run() {
            completeRequest(DrawableTextBitmap(getParams(), this.fontSize))
        }
    }

    override fun CreateNewRequest(
        params: DrawableTextParams,
        manager: ResourceManager<DrawableTextParams, DrawableTextBitmap>
    ): ResourceRequest<DrawableTextParams, DrawableTextBitmap> {
        return TextGenRequest(params, this.fontSize, manager)
    }
}
