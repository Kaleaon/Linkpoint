package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.res.collections.WeakQueue
import androidx.annotation.NonNull

abstract class GLLoadQueue {
    WeakQueue<GLLoadable> loadQueue = new WeakQueue<>()

    interface GLLoadHandler {
        fun GLResourceLoaded(gLLoadable: GLLoadable): Unit
    }

    interface GLLoadable {
        fun GLCompleteLoad(): Unit

        fun GLGetLoadSize(): Int

        fun GLLoad(renderContext: RenderContext, gLLoadHandler: GLLoadHandler): Int
    }

    abstract fun RunLoadQueue(renderContext: RenderContext): Unit

    void StopLoadQueue() {
        this.loadQueue.clear()
    }

    void add(@NonNull GLLoadable gLLoadable) {
        this.loadQueue.offer(gLLoadable)
    }

    void remove(@NonNull GLLoadable gLLoadable) {
        this.loadQueue.remove(gLLoadable)
    }
}
