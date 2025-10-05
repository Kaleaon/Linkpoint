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

    public Unit StopLoadQueue() {
        this.loadQueue.clear()
    }

    public Unit add(GLLoadable gLLoadable) {
        this.loadQueue.offer(gLLoadable)
    }

    public Unit remove(GLLoadable gLLoadable) {
        this.loadQueue.remove(gLLoadable)
    }
}
