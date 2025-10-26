package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import androidx.annotation.NonNull

class GLSyncLoadQueue : GLLoadQueue : GLLoadQueue.GLLoadHandler {
    private Int MAX_LOADS_PER_FRAME = 16
    private Int MAX_SIZE_PER_FRAME = 4194304
    private Int WAIT_FRAMES_AFTER_LOAD = 3
    private var framesWait: Int = 0

    fun GLResourceLoaded(gLLoadable: GLLoadQueue.GLLoadable): Unit {
        gLLoadable.GLCompleteLoad()
    }

    fun RunLoadQueue(renderContext: RenderContext): Unit {
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
            Debug.Printf("waitForMemory: loadedCount %d, size %d", Int.valueOf(i2), Int.valueOf(i))
        }
    }
}
