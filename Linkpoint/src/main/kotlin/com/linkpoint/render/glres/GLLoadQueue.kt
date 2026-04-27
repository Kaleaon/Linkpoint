package com.linkpoint.render.glres

import com.linkpoint.render.RenderContext
import com.linkpoint.res.collections.WeakQueue
import javax.annotation.Nonnull

abstract class GLLoadQueue {
    final WeakQueue<GLLoadable> loadQueue = WeakQueue<>()

    interface GLLoadHandler {
        fun GLResourceLoaded(gLLoadable: GLLoadable)
    }

    interface GLLoadable {
        fun GLCompleteLoad()

        fun GLGetLoadSize(): Int

        fun GLLoad(renderContext: RenderContext, gLLoadHandler: GLLoadHandler): Int
    }

    public abstract Unit RunLoadQueue(RenderContext renderContext)

    fun StopLoadQueue() {
        this.loadQueue.clear()
    }

    fun add(gLLoadable: GLLoadable) {
        this.loadQueue.offer(gLLoadable)
    }

    fun remove(gLLoadable: GLLoadable) {
        this.loadQueue.remove(gLLoadable)
    }
}
