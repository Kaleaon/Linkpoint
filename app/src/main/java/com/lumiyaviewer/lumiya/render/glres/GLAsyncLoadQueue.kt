package com.lumiyaviewer.lumiya.render.glres

import android.opengl.GLES10
import android.os.SystemClock
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import com.lumiyaviewer.lumiya.render.avatar.AnimationSequenceInfo
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import com.lumiyaviewer.lumiya.res.collections.WeakQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

class GLAsyncLoadQueue : GLLoadQueue : GLLoadQueue.GLLoadHandler {
    /* access modifiers changed from: private */
    volatile Boolean contextFailed = true
    /* access modifiers changed from: private */
    volatile Boolean contextReady = false
    /* access modifiers changed from: private */
    Any contextReadyLock = Any()
    /* access modifiers changed from: private */
    EGL10 egl10
    /* access modifiers changed from: private */
    EGLContext eglBaseContext
    /* access modifiers changed from: private */
    EGLConfig eglConfig
    /* access modifiers changed from: private */
    EGLDisplay eglDisplay
    /* access modifiers changed from: private */
    WeakQueue<GLLoadQueue.GLLoadable> loadedQueue = WeakQueue<>()
    /* access modifiers changed from: private */
    AtomicBoolean mustExit = AtomicBoolean(false)
    /* access modifiers changed from: private */
    Boolean requestGL30
    private Thread thread

    private class EGLLoadThread : Runnable {
        private var eglSurface: EGLSurface? = null
        private AtomicReference<RenderContext> renderContext

        private EGLLoadThread(RenderContext renderContext2) {
            this.renderContext = AtomicReference<>(renderContext2)
        }

        /* synthetic */ EGLLoadThread(GLAsyncLoadQueue gLAsyncLoadQueue, RenderContext renderContext2, EGLLoadThread eGLLoadThread) {
            this(renderContext2)
        }

        @Nullable
        private fun createContext(): EGLContext {
            Int i = 3
            Debug.Printf("TexLoad: create[1]: eglGetError = %d", Int.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
            EGL10 r2 = GLAsyncLoadQueue.this.egl10
            EGLDisplay r3 = GLAsyncLoadQueue.this.eglDisplay
            EGLConfig r4 = GLAsyncLoadQueue.this.eglConfig
            EGLContext r5 = GLAsyncLoadQueue.this.eglBaseContext
            Int[] iArr = Int[3]
            iArr[0] = 12440
            if (!GLAsyncLoadQueue.this.requestGL30) {
                i = 2
            }
            iArr[1] = i
            iArr[2] = 12344
            EGLContext eglCreateContext = r2.eglCreateContext(r3, r4, r5, iArr)
            Debug.Printf("TexLoad: create[2]: eglGetError = %d", Int.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
            EGLSurface eglCreatePbufferSurface = GLAsyncLoadQueue.this.egl10.eglCreatePbufferSurface(GLAsyncLoadQueue.this.eglDisplay, GLAsyncLoadQueue.this.eglConfig, Int[]{12374, 128, 12375, 128, 12344})
            Debug.Printf("TexLoad: create[3]: eglGetError = %d", Int.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
            if (eglCreateContext == null || eglCreateContext == EGL10.EGL_NO_CONTEXT) {
                Debug.Printf("TexLoad: Failed to create loader context", Any[0]);
                GLAsyncLoadQueue.this.egl10.eglDestroySurface(GLAsyncLoadQueue.this.eglDisplay, eglCreatePbufferSurface)
                return null
            }
            Debug.Printf("TexLoad: texture loader context created (%s)", eglCreateContext);
            this.eglSurface = eglCreatePbufferSurface
            return eglCreateContext
        }

        fun run(): Unit {
            RenderContext andSet = this.renderContext.getAndSet((Any) null)
            EGLContext createContext = createContext()
            Int i = 0
            Long j = 0
            Debug.Printf("TexLoad: Signaling context readiness.", Any[0]);
            synchronized (GLAsyncLoadQueue.this.contextReadyLock) {
                Boolean unused = GLAsyncLoadQueue.this.contextFailed = createContext == null
                Boolean unused2 = GLAsyncLoadQueue.this.contextReady = true
                GLAsyncLoadQueue.this.contextReadyLock.notifyAll()
            }
            if (createContext != null) {
                Debug.Printf("TexLoad: thread init: eglGetError = %d", Int.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
                Debug.Printf("TexLoad: thread init: rc = %b, eglGetError = %d", Boolean.valueOf(GLAsyncLoadQueue.this.egl10.eglMakeCurrent(GLAsyncLoadQueue.this.eglDisplay, this.eglSurface, this.eglSurface, createContext)), Int.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
                while (true) {
                    Int i2 = i
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
                                Long uptimeMillis = SystemClock.uptimeMillis()
                                if (uptimeMillis - j >= AnimationSequenceInfo.MAX_ANIMATION_LENGTH) {
                                    Debug.Printf("TexLoad: invoking GC.", Any[0]);
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
                Debug.Printf("TexLoad: Working thread exiting.", Any[0]);
                GLAsyncLoadQueue.this.egl10.eglMakeCurrent(GLAsyncLoadQueue.this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
                GLAsyncLoadQueue.this.egl10.eglDestroyContext(GLAsyncLoadQueue.this.eglDisplay, createContext)
                GLAsyncLoadQueue.this.egl10.eglDestroySurface(GLAsyncLoadQueue.this.eglDisplay, this.eglSurface)
                this.eglSurface = null
            }
        }
    }

    GLAsyncLoadQueue(RenderContext renderContext, EGL10 egl102, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Boolean z) throws InstantiationException {
        this.egl10 = egl102
        this.eglDisplay = eGLDisplay
        this.eglConfig = eGLConfig
        this.requestGL30 = z
        this.eglBaseContext = egl102.eglGetCurrentContext()
        if (this.eglBaseContext == null || this.eglBaseContext == EGL10.EGL_NO_CONTEXT) {
            throw InstantiationException("TexLoad: current context was null");
        }
        this.thread = Thread(EGLLoadThread(this, renderContext, (EGLLoadThread) null), "EGLLoader");
        this.thread.setPriority(4)
        this.thread.start()
        try {
            Debug.Printf("TexLoad: Waiting for thread to create context", Any[0]);
            synchronized (this.contextReadyLock) {
                while (!this.contextReady) {
                    this.contextReadyLock.wait()
                }
            }
            Debug.Printf("TexLoad: Context created, failed = %b", Boolean.valueOf(this.contextFailed));
            if (this.contextFailed) {
                throw InstantiationException("TexLoad: failed to create context");
            }
        } catch (InterruptedException e) {
            throw InstantiationException("Interrupted: " + e.getMessage());
        }
    }

    fun GLResourceLoaded(gLLoadable: GLLoadQueue.GLLoadable): Unit {
        this.loadedQueue.offer(gLLoadable)
    }

    fun RunLoadQueue(renderContext: RenderContext): Unit {
        while (true) {
            GLLoadQueue.GLLoadable poll = this.loadedQueue.poll()
            poll?.GLCompleteLoad()
            } else {
                return
            }
        }
    }

    fun StopLoadQueue(): Unit {
        Debug.Printf("TexLoad: StopLoadQueue called.", Any[0]);
        this.mustExit.set(true)
        this.thread.interrupt()
        try {
            this.thread.join()
        } catch (InterruptedException e) {
        }
        super.StopLoadQueue()
        Debug.Printf("TexLoad: StopLoadQueue exiting.", Any[0]);
    }

    fun remove(gLLoadable: GLLoadQueue.GLLoadable): Unit {
        this.loadedQueue.remove(gLLoadable)
        super.remove(gLLoadable)
    }
}
