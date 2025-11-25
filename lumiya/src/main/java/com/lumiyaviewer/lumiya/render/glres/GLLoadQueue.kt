package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.render.RenderContext

interface GLLoadQueue {
    interface GLLoadable {
        fun GLCompleteLoad()
        fun GLGetLoadSize(): Int
        fun GLLoad(renderContext: RenderContext, loadHandler: GLLoadHandler): Int
    }

    interface GLLoadHandler {
        fun GLResourceLoaded(loadable: GLLoadable)
    }
    
    fun add(loadable: GLLoadable)
    fun remove(loadable: GLLoadable)
    fun RunLoadQueue(renderContext: RenderContext)
    fun StopLoadQueue()
}
