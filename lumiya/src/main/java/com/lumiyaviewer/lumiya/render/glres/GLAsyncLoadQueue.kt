package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.render.RenderContext

class GLAsyncLoadQueue(
    renderContext: RenderContext,
    egl10: Any?, // Stubbing EGL types for now if javax.microedition not avail
    eglDisplay: Any?,
    eglConfig: Any?,
    requestGL30: Boolean
) : GLLoadQueue, GLLoadQueue.GLLoadHandler {

    override fun add(loadable: GLLoadQueue.GLLoadable) {
        // Stub
    }

    override fun remove(loadable: GLLoadQueue.GLLoadable) {
        // Stub
    }

    override fun RunLoadQueue(renderContext: RenderContext) {
        // Stub
    }

    override fun StopLoadQueue() {
        // Stub
    }

    override fun GLResourceLoaded(loadable: GLLoadQueue.GLLoadable) {
        // Stub
    }
}
