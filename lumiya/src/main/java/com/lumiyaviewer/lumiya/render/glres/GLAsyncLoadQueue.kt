package com.lumiyaviewer.lumiya.render.glres

import android.opengl.GLES10
import android.os.SystemClock
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import com.lumiyaviewer.lumiya.render.avatar.AnimationSequenceInfo
import com.lumiyaviewer.lumiya.res.collections.WeakQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

class GLAsyncLoadQueue(
    renderContext: RenderContext,
    private val egl10: EGL10,
    private val eglDisplay: EGLDisplay,
    private val eglConfig: EGLConfig,
    private val requestGL30: Boolean
) : GLLoadQueue(), GLLoadQueue.GLLoadHandler {

    @Volatile private var contextFailed = true
    @Volatile private var contextReady = false
    private val contextReadyLock = Any()
    
    private val eglBaseContext: EGLContext
    private val loadedQueue = WeakQueue<GLLoadable>()
    private val mustExit = AtomicBoolean(false)
    private val thread: Thread

    private class EGLLoadThread(
        private val loadQueue: GLAsyncLoadQueue, // Pass outer class instance
        renderContext: RenderContext
    ) : Runnable {
        private var eglSurface: EGLSurface? = null
        private val renderContextRef = AtomicReference(renderContext)

        private fun createContext(): EGLContext? {
            var version = 3
            Debug.Printf("TexLoad: create[1]: eglGetError = %d", loadQueue.egl10.eglGetError())
            
            val attribs = intArrayOf(
                12440, // EGL_CONTEXT_CLIENT_VERSION
                3,
                12344 // EGL_NONE
            )
            
            if (!loadQueue.requestGL30) {
                attribs[1] = 2
            }
            
            val eglCreateContext = loadQueue.egl10.eglCreateContext(
                loadQueue.eglDisplay, 
                loadQueue.eglConfig, 
                loadQueue.eglBaseContext, 
                attribs
            )
            
            Debug.Printf("TexLoad: create[2]: eglGetError = %d", loadQueue.egl10.eglGetError())
            
            val pbufferAttribs = intArrayOf(
                12374, 128, // EGL_WIDTH
                12375, 128, // EGL_HEIGHT
                12344 // EGL_NONE
            )
            
            val eglCreatePbufferSurface = loadQueue.egl10.eglCreatePbufferSurface(
                loadQueue.eglDisplay, 
                loadQueue.eglConfig, 
                pbufferAttribs
            )
            
            Debug.Printf("TexLoad: create[3]: eglGetError = %d", loadQueue.egl10.eglGetError())
            
            if (eglCreateContext == null || eglCreateContext == EGL10.EGL_NO_CONTEXT) {
                Debug.Printf("TexLoad: Failed to create loader context")
                loadQueue.egl10.eglDestroySurface(loadQueue.eglDisplay, eglCreatePbufferSurface)
                return null
            }
            
            Debug.Printf("TexLoad: texture loader context created (%s)", eglCreateContext)
            this.eglSurface = eglCreatePbufferSurface
            return eglCreateContext
        }

        override fun run() {
            val context = renderContextRef.getAndSet(null)
            val eglContext = createContext()
            var consecutiveFails = 0
            var lastGcTime = 0L
            
            Debug.Printf("TexLoad: Signaling context readiness.")
            synchronized(loadQueue.contextReadyLock) {
                loadQueue.contextFailed = eglContext == null
                loadQueue.contextReady = true
                (loadQueue.contextReadyLock as Object).notifyAll()
            }
            
            if (eglContext != null) {
                Debug.Printf("TexLoad: thread init: eglGetError = %d", loadQueue.egl10.eglGetError())
                Debug.Printf("TexLoad: thread init: rc = %b, eglGetError = %d", 
                    loadQueue.egl10.eglMakeCurrent(loadQueue.eglDisplay, eglSurface, eglSurface, eglContext), 
                    loadQueue.egl10.eglGetError()
                )
                
                while (!loadQueue.mustExit.get()) {
                    try {
                        val loadable = loadQueue.loadQueue.take()
                        if (!TextureMemoryTracker.canAllocateMemory(loadable.GLGetLoadSize())) {
                            loadQueue.loadQueue.offer(loadable)
                            Thread.sleep(1000)
                            consecutiveFails++
                            if (consecutiveFails >= 10) {
                                val uptimeMillis = SystemClock.uptimeMillis()
                                if (uptimeMillis - lastGcTime >= AnimationSequenceInfo.MAX_ANIMATION_LENGTH) {
                                    Debug.Printf("TexLoad: invoking GC.")
                                    System.gc()
                                    consecutiveFails = 0
                                    lastGcTime = uptimeMillis
                                }
                            }
                        } else {
                            loadable.GLLoad(context, loadQueue)
                            GLES10.glFinish()
                            consecutiveFails = 0
                        }
                    } catch (e: InterruptedException) {
                        // Loop will check mustExit
                    }
                }
                
                loadQueue.loadedQueue.clear()
                Debug.Printf("TexLoad: Working thread exiting.")
                loadQueue.egl10.eglMakeCurrent(loadQueue.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
                loadQueue.egl10.eglDestroyContext(loadQueue.eglDisplay, eglContext)
                loadQueue.egl10.eglDestroySurface(loadQueue.eglDisplay, eglSurface)
                eglSurface = null
            }
        }
    }

    init {
        eglBaseContext = egl10.eglGetCurrentContext()
        if (eglBaseContext == null || eglBaseContext == EGL10.EGL_NO_CONTEXT) {
            throw InstantiationException("TexLoad: current context was null")
        }
        
        thread = Thread(EGLLoadThread(this, renderContext), "EGLLoader")
        thread.priority = 4
        thread.start()
        
        try {
            Debug.Printf("TexLoad: Waiting for thread to create context")
            synchronized(contextReadyLock) {
                while (!contextReady) {
                    (contextReadyLock as Object).wait()
                }
            }
            Debug.Printf("TexLoad: Context created, failed = %b", contextFailed)
            if (contextFailed) {
                throw InstantiationException("TexLoad: failed to create context")
            }
        } catch (e: InterruptedException) {
            throw InstantiationException("Interrupted: " + e.message)
        }
    }

    override fun GLResourceLoaded(gLLoadable: GLLoadable) {
        loadedQueue.offer(gLLoadable)
    }

    override fun RunLoadQueue(renderContext: RenderContext) {
        while (true) {
            val poll = loadedQueue.poll() ?: return
            poll.GLCompleteLoad()
        }
    }

    override fun StopLoadQueue() {
        Debug.Printf("TexLoad: StopLoadQueue called.")
        mustExit.set(true)
        thread.interrupt()
        try {
            thread.join()
        } catch (e: InterruptedException) {
        }
        super.StopLoadQueue()
        Debug.Printf("TexLoad: StopLoadQueue exiting.")
    }

    override fun remove(gLLoadable: GLLoadable) {
        loadedQueue.remove(gLLoadable)
        super.remove(gLLoadable)
    }
}
