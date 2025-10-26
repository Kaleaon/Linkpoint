package com.linkpoint.render.glres

import com.linkpoint.Debug
import com.linkpoint.render.RenderContext
import com.linkpoint.render.TextureMemoryTracker
import com.linkpoint.render.glres.GLLoadQueue
import javax.annotation.Nonnull

class GLSyncLoadQueue : GLLoadQueue(), GLLoadQueue.GLLoadHandler {
    private const val MAX_LOADS_PER_FRAME: Int = 16
    private const val MAX_SIZE_PER_FRAME: Int = 4194304
    private const val WAIT_FRAMES_AFTER_LOAD: Int = 3
    private Int framesWait = 0

    fun GLResourceLoaded(GLLoadQueue.GLLoadable gLLoadable) {
        gLLoadable.GLCompleteLoad()
    }

    fun RunLoadQueue(renderContext: RenderContext) {
        GLLoadQueue.GLLoadable gLLoadable
        if (this.framesWait != 0) {
            this.framesWait--
            return
        }
        val i: Int = 0
        val i2: Int = 0
        while (true) {
            if (!TextureMemoryTracker.canAllocateMemory(0) || (gLLoadable = (GLLoadQueue.GLLoadable) this.loadQueue.poll()) == null) {
                break
            } else if (!TextureMemoryTracker.canAllocateMemory(gLLoadable.GLGetLoadSize())) {
                TextureMemoryTracker.stall()
                this.loadQueue.add(gLLoadable)
                break
            } else {
                val GLLoad: Int = gLLoadable.GLLoad(renderContext, this) + i
                this.framesWait = 3
                val i3: Int = i2 + 1
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
