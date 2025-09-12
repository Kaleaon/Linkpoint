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

public class GLAsyncLoadQueue extends GLLoadQueue implements GLLoadQueue.GLLoadHandler {
    /* access modifiers changed from: private */
    public volatile boolean contextFailed = true;
    /* access modifiers changed from: private */
    public volatile boolean contextReady = false;
    /* access modifiers changed from: private */
    public final Object contextReadyLock = new Object();
    /* access modifiers changed from: private */
    public final EGL10 egl10;
    /* access modifiers changed from: private */
    public final EGLContext eglBaseContext;
    /* access modifiers changed from: private */
    public final EGLConfig eglConfig;
    /* access modifiers changed from: private */
    public final EGLDisplay eglDisplay;
    /* access modifiers changed from: private */
    public final WeakQueue<GLLoadQueue.GLLoadable> loadedQueue = new WeakQueue<>();
    /* access modifiers changed from: private */
    public final AtomicBoolean mustExit = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public final boolean requestGL30;
    private final Thread thread;

    private class EGLLoadThread implements Runnable {
        private EGLSurface eglSurface;
        private final AtomicReference<RenderContext> renderContext;

        private EGLLoadThread(RenderContext renderContext2) {
            this.renderContext = new AtomicReference<>(renderContext2);
        }

        /* synthetic */ EGLLoadThread(GLAsyncLoadQueue gLAsyncLoadQueue, RenderContext renderContext2, EGLLoadThread eGLLoadThread) {
            this(renderContext2);
        }

        @Nullable
        private EGLContext createContext() {
            int i = 3;
            Debug.Printf("TexLoad: create[1]: eglGetError = %d", Integer.valueOf(GLAsyncLoadQueue.this.egl10.eglGetError()));
            EGL10 r2 = GLAsyncLoadQueue.this.egl10;
            EGLDisplay r3 = GLAsyncLoadQueue.this.eglDisplay;
            EGLConfig r4 = GLAsyncLoadQueue.this.eglConfig;
            EGLContext r5 = GLAsyncLoadQueue.this.eglBaseContext;
            int[] iArr = new int[3];
            iArr[0] = 12440;
            if (!GLAsyncLoadQueue.this.requestGL30) {
                i = 2;
            }
            iArr[1] = i;
            iArr[2] = 12344;
            EGLContext eglCreateContext = r2.eglCreateContext(r3, r4, r5, iArr);
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

        public void run() {
            RenderContext andSet = this.renderContext.getAndSet((Object) null);
            EGLContext createContext = createContext();
            int i = 0;
            long j = 0;
            Debug.Printf("TexLoad: Signaling context readiness.", new Object[0]);
            synchronized (GLAsyncLoadQueue.this.contextReadyLock) {
                boolean unused = GLAsyncLoadQueue.this.contextFailed = createContext == null;
                boolean unused2 = GLAsyncLoadQueue.this.contextReady = true;
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
                        GLLoadQueue.GLLoadable gLLoadable = (GLLoadQueue.GLLoadable) GLAsyncLoadQueue.this.loadQueue.take();
                        if (!TextureMemoryTracker.canAllocateMemory(gLLoadable.GLGetLoadSize())) {
                            GLAsyncLoadQueue.this.loadQueue.offer(gLLoadable);
                            Thread.sleep(1000);
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
                        } else {
                            gLLoadable.GLLoad(andSet, GLAsyncLoadQueue.this);
                            GLES10.glFinish();
                            i = 0;
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

    public GLAsyncLoadQueue(RenderContext renderContext, EGL10 egl102, EGLDisplay eGLDisplay, EGLConfig eGLConfig, boolean z) throws InstantiationException {
        this.egl10 = egl102;
        this.eglDisplay = eGLDisplay;
        this.eglConfig = eGLConfig;
        this.requestGL30 = z;
        this.eglBaseContext = egl102.eglGetCurrentContext();
        if (this.eglBaseContext == null || this.eglBaseContext == EGL10.EGL_NO_CONTEXT) {
            throw new InstantiationException("TexLoad: current context was null");
        }
        this.thread = new Thread(new EGLLoadThread(this, renderContext, (EGLLoadThread) null), "EGLLoader");
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

    public void GLResourceLoaded(GLLoadQueue.GLLoadable gLLoadable) {
        this.loadedQueue.offer(gLLoadable);
    }

    public void RunLoadQueue(@Nonnull RenderContext renderContext) {
        while (true) {
            GLLoadQueue.GLLoadable poll = this.loadedQueue.poll();
            if (poll != null) {
                poll.GLCompleteLoad();
            } else {
                return;
            }
        }
    }

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

    public void remove(@Nonnull GLLoadQueue.GLLoadable gLLoadable) {
        this.loadedQueue.remove(gLLoadable);
        super.remove(gLLoadable);
    }
}
