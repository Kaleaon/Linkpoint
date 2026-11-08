package com.lumiyaviewer.lumiya.render.glres;

import android.opengl.GLES10;
import android.os.SystemClock;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.avatar.AnimationSequenceInfo;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
/* loaded from: classes.dex */
public class GLAsyncLoadQueue extends GLLoadQueue implements GLLoadQueue.GLLoadHandler {
    private final EGL10 egl10;
    private final EGLContext eglBaseContext;
    private final EGLConfig eglConfig;
    private final EGLDisplay eglDisplay;
    private final boolean requestGL30;
    private final Thread thread;
    private final AtomicBoolean mustExit = new AtomicBoolean(false);
    private final Object contextReadyLock = new Object();
    private volatile boolean contextReady = false;
    private volatile boolean contextFailed = true;
    private final WeakQueue<GLLoadQueue.GLLoadable> loadedQueue = new WeakQueue<>();

    /* loaded from: classes.dex */
    private class EGLLoadThread implements Runnable {
        private EGLSurface eglSurface;
        private final AtomicReference<RenderContext> renderContext;

        private EGLLoadThread(RenderContext renderContext) {
            this.renderContext = new AtomicReference<>(renderContext);
        }

        /* synthetic */ EGLLoadThread(GLAsyncLoadQueue gLAsyncLoadQueue, RenderContext renderContext, EGLLoadThread eGLLoadThread) {
            this(renderContext);
        }

        @Nullable
        private EGLContext createContext() {
            Debug.Printf("TexLoad: create[1]: eglGetError = %d", Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
            EGL10 egl10 = GLAsyncLoadQueue.this.egl10;
            EGLDisplay eGLDisplay = GLAsyncLoadQueue.this.eglDisplay;
            EGLConfig eGLConfig = GLAsyncLoadQueue.this.eglConfig;
            EGLContext eGLContext = GLAsyncLoadQueue.this.eglBaseContext;
            int[] iArr = new int[3];
            iArr[0] = 12440;
            iArr[1] = GLAsyncLoadQueue.this.requestGL30 ? 3 : 2;
            iArr[2] = 12344;
            EGLContext eglCreateContext = egl10.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
            Debug.Printf("TexLoad: create[2]: eglGetError = %d", Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
            EGLSurface eglCreatePbufferSurface = GLAsyncLoadQueue.this.egl10.eglCreatePbufferSurface(GLAsyncLoadQueue.this.eglDisplay, GLAsyncLoadQueue.this.eglConfig, new int[]{12374, 128, 12375, 128, 12344});
            Debug.Printf("TexLoad: create[3]: eglGetError = %d", Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
            if (eglCreateContext == null || eglCreateContext == EGL10.EGL_NO_CONTEXT) {
                Debug.Printf("TexLoad: Failed to create loader context", new Object[0]);
                GLAsyncLoadQueue.this.egl10.eglDestroySurface(GLAsyncLoadQueue.this.eglDisplay, eglCreatePbufferSurface);
                return null;
            }
            Debug.Printf("TexLoad: texture loader context created (%s)", eglCreateContext);
            this.eglSurface = eglCreatePbufferSurface;
            return eglCreateContext;
        }

        @Override // java.lang.Runnable
        public void run() {
            RenderContext andSet = this.renderContext.getAndSet(null);
            EGLContext createContext = createContext();
            int i = 0;
            long j = 0;
            Debug.Printf("TexLoad: Signaling context readiness.", new Object[0]);
            synchronized (GLAsyncLoadQueue.this.contextReadyLock) {
                GLAsyncLoadQueue.this.contextFailed = createContext == null;
                GLAsyncLoadQueue.this.contextReady = true;
                GLAsyncLoadQueue.this.contextReadyLock.notifyAll();
            }
            if (createContext != null) {
                Debug.Printf("TexLoad: thread init: eglGetError = %d", Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
                Debug.Printf("TexLoad: thread init: rc = %b, eglGetError = %d", Boolean.valueOf(GLAsyncLoadQueue.this.egl10.eglMakeCurrent(GLAsyncLoadQueue.this.eglDisplay, this.eglSurface, this.eglSurface, createContext)), Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
                while (true) {
                    int i2 = i;
                    if (GLAsyncLoadQueue.this.mustExit.get()) {
                        break;
                    }
                    try {
                        GLLoadQueue.GLLoadable take = GLAsyncLoadQueue.this.loadQueue.take();
                        if (TextureMemoryTracker.canAllocateMemory(take.GLGetLoadSize())) {
                            take.GLLoad(andSet, GLAsyncLoadQueue.this);
                            GLES10.glFinish();
                            i = 0;
                        } else {
                            GLAsyncLoadQueue.this.loadQueue.offer(take);
                            Thread.sleep(1000L);
                            i = i2 + 1;
                            if (i >= 10) {
                                long uptimeMillis = SystemClock.uptimeMillis();
                                if (uptimeMillis - j >= AnimationSequenceInfo.MAX_ANIMATION_LENGTH) {
                                    Debug.Printf("TexLoad: invoking GC.", new Object[0]);
                                    System.gc();
                                    i = 0;
                                    j = uptimeMillis;
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                    }
                }
                GLAsyncLoadQueue.this.loadedQueue.clear();
                Debug.Printf("TexLoad: Working thread exiting.", new Object[0]);
                GLAsyncLoadQueue.this.egl10.eglMakeCurrent(GLAsyncLoadQueue.this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GLAsyncLoadQueue.this.egl10.eglDestroyContext(GLAsyncLoadQueue.this.eglDisplay, createContext);
                GLAsyncLoadQueue.this.egl10.eglDestroySurface(GLAsyncLoadQueue.this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
        }
    }

    public GLAsyncLoadQueue(RenderContext renderContext, EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, boolean z) throws InstantiationException {
        this.egl10 = egl10;
        this.eglDisplay = eGLDisplay;
        this.eglConfig = eGLConfig;
        this.requestGL30 = z;
        this.eglBaseContext = egl10.eglGetCurrentContext();
        if (this.eglBaseContext == null || this.eglBaseContext == EGL10.EGL_NO_CONTEXT) {
            throw new InstantiationException("TexLoad: current context was null");
        }
        this.thread = new Thread(new EGLLoadThread(this, renderContext, null), "EGLLoader");
        this.thread.setPriority(4);
        this.thread.start();
        try {
            Debug.Printf("TexLoad: Waiting for thread to create context", new Object[0]);
            synchronized (this.contextReadyLock) {
                while (!this.contextReady) {
                    this.contextReadyLock.wait();
                }
            }
            Debug.Printf("TexLoad: Context created, failed = %b", Boolean.valueOf(this.contextFailed));
            if (this.contextFailed) {
                throw new InstantiationException("TexLoad: failed to create context");
            }
        } catch (InterruptedException e) {
            throw new InstantiationException("Interrupted: " + e.getMessage());
        }
    }

    @Override // com.lumiyaviewer.lumiya.render.glres.GLLoadQueue.GLLoadHandler
    public void GLResourceLoaded(GLLoadQueue.GLLoadable gLLoadable) {
        this.loadedQueue.offer(gLLoadable);
    }

    @Override // com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
    public void RunLoadQueue(@Nonnull RenderContext renderContext) {
        while (true) {
            GLLoadQueue.GLLoadable poll = this.loadedQueue.poll();
            if (poll == null) {
                return;
            }
            poll.GLCompleteLoad();
        }
    }

    @Override // com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
    public void StopLoadQueue() {
        Debug.Printf("TexLoad: StopLoadQueue called.", new Object[0]);
        this.mustExit.set(true);
        this.thread.interrupt();
        try {
            this.thread.join();
        } catch (InterruptedException e) {
        }
        super.StopLoadQueue();
        Debug.Printf("TexLoad: StopLoadQueue exiting.", new Object[0]);
    }

    @Override // com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
    public void remove(@Nonnull GLLoadQueue.GLLoadable gLLoadable) {
        this.loadedQueue.remove(gLLoadable);
        super.remove(gLLoadable);
    }
}
