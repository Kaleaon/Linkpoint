package com.linkpoint.render.glres

import android.opengl.GLES10
import android.os.SystemClock
import com.linkpoint.Debug
import com.linkpoint.render.RenderContext
import com.linkpoint.render.TextureMemoryTracker
import com.linkpoint.render.avatar.AnimationSequenceInfo
import com.linkpoint.render.glres.GLLoadQueue
import com.linkpoint.res.collections.WeakQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

class GLAsyncLoadQueue : GLLoadQueue(), GLLoadQueue.GLLoadHandler {
    /* access modifiers changed from: private */
    public volatile Boolean contextFailed = true
    /* access modifiers changed from: private */
    public volatile Boolean contextReady = false
    /* access modifiers changed from: private */
    val Object contextReadyLock = Object()
    /* access modifiers changed from: private */
    val EGL10 egl10
    /* access modifiers changed from: private */
    val EGLContext eglBaseContext
    /* access modifiers changed from: private */
    val EGLConfig eglConfig
    /* access modifiers changed from: private */
    val EGLDisplay eglDisplay
    /* access modifiers changed from: private */
    val WeakQueue<GLLoadQueue.GLLoadable> loadedQueue = WeakQueue<>()
    /* access modifiers changed from: private */
    val AtomicBoolean mustExit = AtomicBoolean(false)
    /* access modifiers changed from: private */
    val Boolean requestGL30
    private val Thread thread

    private class EGLLoadThread : Runnable {
        private EGLSurface eglSurface
        private val AtomicReference<RenderContext> renderContext

        private EGLLoadThread(RenderContext renderContext2) {
            this.renderContext = AtomicReference<>(renderContext2)
        }

        /* synthetic */ EGLLoadThread(GLAsyncLoadQueue gLAsyncLoadQueue, RenderContext renderContext2, EGLLoadThread eGLLoadThread) {
            this(renderContext2)
        }

         private fun createContext(): EGLContext {
            val i: Int = 3
            Debug.Printf("TexLoad: create[1]: eglGetError = %d", Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()))
            val r2: EGL10 = GLAsyncLoadQueue.this.egl10
            val r3: EGLDisplay = GLAsyncLoadQueue.this.eglDisplay
            val r4: EGLConfig = GLAsyncLoadQueue.this.eglConfig
            val r5: EGLContext = GLAsyncLoadQueue.this.eglBaseContext
            val iArr: IntArray = Int[3]
            iArr[0] = 12440
            if (!GLAsyncLoadQueue.this.requestGL30) {
                i = 2
            }
            iArr[1] = i
            iArr[2] = 12344
            val eglCreateContext: EGLContext = r2.eglCreateContext(r3, r4, r5, iArr)
            Debug.Printf("TexLoad: create[2]: eglGetError = %d", Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()))
            val eglCreatePbufferSurface: EGLSurface = GLAsyncLoadQueue.this.egl10.eglCreatePbufferSurface(GLAsyncLoadQueue.this.eglDisplay, GLAsyncLoadQueue.this.eglConfig, IntArray{12374, 128, 12375, 128, 12344})
            Debug.Printf("TexLoad: create[3]: eglGetError = %d", Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()))
            if (eglCreateContext == null || eglCreateContext == EGL10.EGL_NO_CONTEXT) {
                Debug.Printf("TexLoad: Failed to create loader context", Object[0])
                GLAsyncLoadQueue.this.egl10.eglDestroySurface(GLAsyncLoadQueue.this.eglDisplay, eglCreatePbufferSurface)
                return null
            }
            Debug.Printf("TexLoad: texture loader context created (%s)", eglCreateContext)
            this.eglSurface = eglCreatePbufferSurface
            return eglCreateContext
        }

        fun run() {
            val andSet: RenderContext = this.renderContext.getAndSet((Object) null)
            val createContext: EGLContext = createContext()
            val i: Int = 0
            val j: Long = 0
            Debug.Printf("TexLoad: Signaling context readiness.", Object[0])
            synchronized (GLAsyncLoadQueue.this.contextReadyLock) {
                val unused: Boolean = GLAsyncLoadQueue.this.contextFailed = createContext == null
                val unused2: Boolean = GLAsyncLoadQueue.this.contextReady = true
                GLAsyncLoadQueue.this.contextReadyLock.notifyAll()
            }
            if (createContext != null) {
                Debug.Printf("TexLoad: thread init: eglGetError = %d", Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()))
                Debug.Printf("TexLoad: thread init: rc = %b, eglGetError = %d", Boolean.valueOf(GLAsyncLoadQueue.this.egl10.eglMakeCurrent(GLAsyncLoadQueue.this.eglDisplay, this.eglSurface, this.eglSurface, createContext)), Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()))
                while (true) {
                    val i2: Int = i
                    if (GLAsyncLoadQueue.this.mustExit.get()) {
                        break
                    }
                    try {
                        GLLoadQueue.GLLoadable gLLoadable = (GLLoadQueue.GLLoadable) GLAsyncLoadQueue.this.loadQueue.take()
                        if (!TextureMemoryTracker.canAllocateMemory(gLLoadable.GLGetLoadSize())) {
                            GLAsyncLoadQueue.this.loadQueue.offer(gLLoadable)
                            Thread.sleep(1000)
                            i = i2 + 1
                            if (i >= 10) {
                                val uptimeMillis: Long = SystemClock.uptimeMillis()
                                if (uptimeMillis - j >= AnimationSequenceInfo.MAX_ANIMATION_LENGTH) {
                                    Debug.Printf("TexLoad: invoking GC.", Object[0])
                                    System.gc()
                                    i = 0
                                    j = uptimeMillis
                                }
                            }
                        } else {
                            gLLoadable.GLLoad(andSet, GLAsyncLoadQueue.this)
                            GLES10.glFinish()
                            i = 0
                        }
                    } catch (InterruptedException e) {
                    }
                }
                GLAsyncLoadQueue.this.loadedQueue.clear()
                Debug.Printf("TexLoad: Working thread exiting.", Object[0])
                GLAsyncLoadQueue.this.egl10.eglMakeCurrent(GLAsyncLoadQueue.this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
                GLAsyncLoadQueue.this.egl10.eglDestroyContext(GLAsyncLoadQueue.this.eglDisplay, createContext)
                GLAsyncLoadQueue.this.egl10.eglDestroySurface(GLAsyncLoadQueue.this.eglDisplay, this.eglSurface)
                this.eglSurface = null
            }
        }
    }

    public GLAsyncLoadQueue(RenderContext renderContext, EGL10 egl102, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Boolean z) throws InstantiationException {
        this.egl10 = egl102
        this.eglDisplay = eGLDisplay
        this.eglConfig = eGLConfig
        this.requestGL30 = z
        this.eglBaseContext = egl102.eglGetCurrentContext()
        if (this.eglBaseContext == null || this.eglBaseContext == EGL10.EGL_NO_CONTEXT) {
            throw InstantiationException("TexLoad: current context was null")
        }
        this.thread = Thread(EGLLoadThread(this, renderContext, (EGLLoadThread) null), "EGLLoader")
        this.thread.setPriority(4)
        this.thread.start()
        try {
            Debug.Printf("TexLoad: Waiting for thread to create context", Object[0])
            synchronized (this.contextReadyLock) {
                while (!this.contextReady) {
                    this.contextReadyLock.wait()
                }
            }
            Debug.Printf("TexLoad: Context created, failed = %b", Boolean.valueOf(this.contextFailed))
            if (this.contextFailed) {
                throw InstantiationException("TexLoad: failed to create context")
            }
        } catch (InterruptedException e) {
            throw InstantiationException("Interrupted: " + e.getMessage())
        }
    }

    fun GLResourceLoaded(GLLoadQueue.GLLoadable gLLoadable) {
        this.loadedQueue.offer(gLLoadable)
    }

    fun RunLoadQueue(renderContext: RenderContext) {
        while (true) {
            GLLoadQueue.GLLoadable poll = this.loadedQueue.poll()
            if (poll != null) {
                poll.GLCompleteLoad()
            } else {
                return
            }
        }
    }

    fun StopLoadQueue() {
        Debug.Printf("TexLoad: StopLoadQueue called.", Object[0])
        this.mustExit.set(true)
        this.thread.interrupt()
        try {
            this.thread.join()
        } catch (InterruptedException e) {
        }
        super.StopLoadQueue()
        Debug.Printf("TexLoad: StopLoadQueue exiting.", Object[0])
    }

    fun remove(GLLoadQueue.GLLoadable gLLoadable) {
        this.loadedQueue.remove(gLLoadable)
        super.remove(gLLoadable)
    }
}
