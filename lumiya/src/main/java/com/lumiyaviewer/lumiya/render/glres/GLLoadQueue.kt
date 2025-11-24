package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.res.collections.WeakQueue
import androidx.annotation.NonNull

abstract class GLLoadQueue {
    protected val loadQueue = WeakQueue<GLLoadable>()

    interface GLLoadHandler {
        fun GLResourceLoaded(gLLoadable: GLLoadable)
    }

    interface GLLoadable {
        fun GLCompleteLoad()
        fun GLGetLoadSize(): Int
        fun GLLoad(renderContext: RenderContext, gLLoadHandler: GLLoadHandler): Int
    }

    abstract fun RunLoadQueue(renderContext: RenderContext)

    open fun StopLoadQueue() {
        loadQueue.clear()
    }

    open fun add(@NonNull gLLoadable: GLLoadable) {
        loadQueue.offer(gLLoadable)
    }

    open fun remove(@NonNull gLLoadable: GLLoadable) {
        loadQueue.remove(gLLoadable)
    }
}
