package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import java.util.LinkedList
import java.util.Queue

class GLSyncLoadQueue : GLLoadQueue, GLLoadQueue.GLLoadHandler {
    private val MAX_LOADS_PER_FRAME = 16
    private val MAX_SIZE_PER_FRAME = 4194304
    private val WAIT_FRAMES_AFTER_LOAD = 3
    private var framesWait = 0
    
    private val loadQueue: Queue<GLLoadQueue.GLLoadable> = LinkedList()

    override fun add(loadable: GLLoadQueue.GLLoadable) {
        loadQueue.add(loadable)
    }

    override fun remove(loadable: GLLoadQueue.GLLoadable) {
        loadQueue.remove(loadable)
    }

    override fun GLResourceLoaded(loadable: GLLoadQueue.GLLoadable) {
        loadable.GLCompleteLoad()
    }

    override fun RunLoadQueue(renderContext: RenderContext) {
        if (this.framesWait != 0) {
            this.framesWait--
            return
        }
        
        var loadedCount = 0
        var loadedSize = 0
        
        while (true) {
            if (!TextureMemoryTracker.canAllocateMemory(0)) {
                break
            }
            
            val loadable = loadQueue.poll() ?: break
            
            if (!TextureMemoryTracker.canAllocateMemory(loadable.GLGetLoadSize())) {
                TextureMemoryTracker.stall()
                loadQueue.add(loadable)
                break
            }
            
            val size = loadable.GLLoad(renderContext, this)
            loadedSize += size
            loadedCount++
            
            framesWait = WAIT_FRAMES_AFTER_LOAD
            
            if (loadedCount >= MAX_LOADS_PER_FRAME || loadedSize >= MAX_SIZE_PER_FRAME) {
                break
            }
        }
        
        if (loadedCount != 0) {
            Debug.Log("waitForMemory: loadedCount $loadedCount, size $loadedSize")
        }
    }

    override fun StopLoadQueue() {
        // No thread to stop for sync queue
    }
}
