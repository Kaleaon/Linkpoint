package com.linkpoint.render.glres

import com.linkpoint.Debug
import com.linkpoint.render.RenderContext
import com.linkpoint.render.TextureMemoryTracker
import com.linkpoint.render.glres.GLLoadQueue
import javax.annotation.Nonnull

class GLSyncLoadQueue : GLLoadQueue() : GLLoadQueue.GLLoadHandler {
    private const val Int MAX_LOADS_PER_FRAME = 16
    private const val Int MAX_SIZE_PER_FRAME = 4194304
    private const val Int WAIT_FRAMES_AFTER_LOAD = 3
    private Int framesWait = 0

    public Unit GLResourceLoaded(GLLoadQueue.GLLoadable gLLoadable) {
        gLLoadable.GLCompleteLoad()
    }

    public Unit RunLoadQueue(RenderContext renderContext) {
        GLLoadQueue.GLLoadable gLLoadable
        if (this.framesWait != 0) {
            this.framesWait--
            return
        }
        Int i = 0
        Int i2 = 0
        while (true) {
            if (!TextureMemoryTracker.canAllocateMemory(0) || (gLLoadable = (GLLoadQueue.GLLoadable) this.loadQueue.poll()) == null) {
                break
            } else if (!TextureMemoryTracker.canAllocateMemory(gLLoadable.GLGetLoadSize())) {
                TextureMemoryTracker.stall()
                this.loadQueue.add(gLLoadable)
                break
            } else {
                Int GLLoad = gLLoadable.GLLoad(renderContext, this) + i
                this.framesWait = 3
                Int i3 = i2 + 1
                if (i3 >= 16 || GLLoad >= 4194304) {
                    i2 = i3
                    i = GLLoad
                } else {
                    i2 = i3
                    i = GLLoad
                }
            }
        }
        if (i2 != 0) {
            Debug.Printf("waitForMemory: loadedCount %d, size %d", Integer.valueOf(i2), Integer.valueOf(i))
        }
    }
}
