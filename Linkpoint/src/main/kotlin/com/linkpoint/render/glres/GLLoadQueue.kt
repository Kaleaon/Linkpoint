package com.linkpoint.render.glres

import com.linkpoint.render.RenderContext
import com.linkpoint.res.collections.WeakQueue
import javax.annotation.Nonnull

abstract class GLLoadQueue {
    final WeakQueue<GLLoadable> loadQueue = WeakQueue<>()

    interface GLLoadHandler {
        Unit GLResourceLoaded(GLLoadable gLLoadable)
    }

    interface GLLoadable {
        Unit GLCompleteLoad()

        Int GLGetLoadSize()

        Int GLLoad(RenderContext renderContext, GLLoadHandler gLLoadHandler)
    }

    public abstract Unit RunLoadQueue(RenderContext renderContext)

    fun StopLoadQueue() {
        this.loadQueue.clear()
    }

    fun add(GLLoadable gLLoadable) {
        this.loadQueue.offer(gLLoadable)
    }

    fun remove(GLLoadable gLLoadable) {
        this.loadQueue.remove(gLLoadable)
    }
}
