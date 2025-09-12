// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import android.opengl.GLES10;
import android.os.SystemClock;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLLoadQueue

public class GLAsyncLoadQueue extends GLLoadQueue
    implements GLLoadQueue.GLLoadHandler
{
    private class EGLLoadThread
        implements Runnable
    {

        private EGLSurface eglSurface;
        private final AtomicReference renderContext;
        final GLAsyncLoadQueue this$0;

        private EGLContext createContext()
        {
            byte byte0 = 3;
            Debug.Printf("TexLoad: create[1]: eglGetError = %d", new Object[] {
                Integer.valueOf(GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglGetError())
            });
            Object obj = GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this);
            Object obj1 = GLAsyncLoadQueue._2D_get4(GLAsyncLoadQueue.this);
            EGLConfig eglconfig = GLAsyncLoadQueue._2D_get3(GLAsyncLoadQueue.this);
            EGLContext eglcontext = GLAsyncLoadQueue._2D_get2(GLAsyncLoadQueue.this);
            if (!GLAsyncLoadQueue._2D_get7(GLAsyncLoadQueue.this))
            {
                byte0 = 2;
            }
            obj = ((EGL10) (obj)).eglCreateContext(((EGLDisplay) (obj1)), eglconfig, eglcontext, new int[] {
                12440, byte0, 12344
            });
            Debug.Printf("TexLoad: create[2]: eglGetError = %d", new Object[] {
                Integer.valueOf(GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglGetError())
            });
            obj1 = GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglCreatePbufferSurface(GLAsyncLoadQueue._2D_get4(GLAsyncLoadQueue.this), GLAsyncLoadQueue._2D_get3(GLAsyncLoadQueue.this), new int[] {
                12374, 128, 12375, 128, 12344
            });
            Debug.Printf("TexLoad: create[3]: eglGetError = %d", new Object[] {
                Integer.valueOf(GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglGetError())
            });
            if (obj != null && obj != EGL10.EGL_NO_CONTEXT)
            {
                Debug.Printf("TexLoad: texture loader context created (%s)", new Object[] {
                    obj
                });
                eglSurface = ((EGLSurface) (obj1));
                return ((EGLContext) (obj));
            } else
            {
                Debug.Printf("TexLoad: Failed to create loader context", new Object[0]);
                GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglDestroySurface(GLAsyncLoadQueue._2D_get4(GLAsyncLoadQueue.this), ((EGLSurface) (obj1)));
                return null;
            }
        }

        public void run()
        {
            Object obj;
            RenderContext rendercontext;
            long l;
            rendercontext = (RenderContext)renderContext.getAndSet(null);
            obj = createContext();
            l = 0L;
            Debug.Printf("TexLoad: Signaling context readiness.", new Object[0]);
            Object obj1 = GLAsyncLoadQueue._2D_get0(GLAsyncLoadQueue.this);
            obj1;
            JVM INSTR monitorenter ;
            GLAsyncLoadQueue glasyncloadqueue = GLAsyncLoadQueue.this;
            int i;
            int j;
            long l1;
            long l2;
            boolean flag;
            if (obj == null)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            GLAsyncLoadQueue._2D_set0(glasyncloadqueue, flag);
            GLAsyncLoadQueue._2D_set1(GLAsyncLoadQueue.this, true);
            GLAsyncLoadQueue._2D_get0(GLAsyncLoadQueue.this).notifyAll();
            obj1;
            JVM INSTR monitorexit ;
            if (obj == null)
            {
                break MISSING_BLOCK_LABEL_440;
            }
            Debug.Printf("TexLoad: thread init: eglGetError = %d", new Object[] {
                Integer.valueOf(GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglGetError())
            });
            Debug.Printf("TexLoad: thread init: rc = %b, eglGetError = %d", new Object[] {
                Boolean.valueOf(GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglMakeCurrent(GLAsyncLoadQueue._2D_get4(GLAsyncLoadQueue.this), eglSurface, eglSurface, ((EGLContext) (obj)))), Integer.valueOf(GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglGetError())
            });
            i = 0;
_L2:
            if (GLAsyncLoadQueue._2D_get6(GLAsyncLoadQueue.this).get())
            {
                break MISSING_BLOCK_LABEL_342;
            }
            obj1 = (GLLoadQueue.GLLoadable)loadQueue.take();
            if (TextureMemoryTracker.canAllocateMemory(((GLLoadQueue.GLLoadable) (obj1)).GLGetLoadSize()))
            {
                break; /* Loop/switch isn't completed */
            }
            loadQueue.offer(obj1);
            Thread.sleep(1000L);
            j = i + 1;
            i = j;
            l1 = l;
            if (j < 10)
            {
                break MISSING_BLOCK_LABEL_298;
            }
            l2 = SystemClock.uptimeMillis();
            i = j;
            l1 = l;
            if (l2 - l < 60000L)
            {
                break MISSING_BLOCK_LABEL_298;
            }
            Debug.Printf("TexLoad: invoking GC.", new Object[0]);
            System.gc();
            i = 0;
            l1 = l2;
_L3:
            l = l1;
            if (true) goto _L2; else goto _L1
            obj;
            throw obj;
_L1:
            ((GLLoadQueue.GLLoadable) (obj1)).GLLoad(rendercontext, GLAsyncLoadQueue.this);
            GLES10.glFinish();
            i = 0;
            l1 = l;
              goto _L3
            InterruptedException interruptedexception;
            interruptedexception;
            GLAsyncLoadQueue._2D_get5(GLAsyncLoadQueue.this).clear();
            Debug.Printf("TexLoad: Working thread exiting.", new Object[0]);
            GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglMakeCurrent(GLAsyncLoadQueue._2D_get4(GLAsyncLoadQueue.this), EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglDestroyContext(GLAsyncLoadQueue._2D_get4(GLAsyncLoadQueue.this), ((EGLContext) (obj)));
            GLAsyncLoadQueue._2D_get1(GLAsyncLoadQueue.this).eglDestroySurface(GLAsyncLoadQueue._2D_get4(GLAsyncLoadQueue.this), eglSurface);
            eglSurface = null;
        }

        private EGLLoadThread(RenderContext rendercontext)
        {
            this$0 = GLAsyncLoadQueue.this;
            super();
            renderContext = new AtomicReference(rendercontext);
        }

        EGLLoadThread(RenderContext rendercontext, EGLLoadThread eglloadthread)
        {
            this(rendercontext);
        }
    }


    private volatile boolean contextFailed;
    private volatile boolean contextReady;
    private final Object contextReadyLock;
    private final EGL10 egl10;
    private final EGLContext eglBaseContext;
    private final EGLConfig eglConfig;
    private final EGLDisplay eglDisplay;
    private final WeakQueue loadedQueue;
    private final AtomicBoolean mustExit;
    private final boolean requestGL30;
    private final Thread thread;

    static Object _2D_get0(GLAsyncLoadQueue glasyncloadqueue)
    {
        return glasyncloadqueue.contextReadyLock;
    }

    static EGL10 _2D_get1(GLAsyncLoadQueue glasyncloadqueue)
    {
        return glasyncloadqueue.egl10;
    }

    static EGLContext _2D_get2(GLAsyncLoadQueue glasyncloadqueue)
    {
        return glasyncloadqueue.eglBaseContext;
    }

    static EGLConfig _2D_get3(GLAsyncLoadQueue glasyncloadqueue)
    {
        return glasyncloadqueue.eglConfig;
    }

    static EGLDisplay _2D_get4(GLAsyncLoadQueue glasyncloadqueue)
    {
        return glasyncloadqueue.eglDisplay;
    }

    static WeakQueue _2D_get5(GLAsyncLoadQueue glasyncloadqueue)
    {
        return glasyncloadqueue.loadedQueue;
    }

    static AtomicBoolean _2D_get6(GLAsyncLoadQueue glasyncloadqueue)
    {
        return glasyncloadqueue.mustExit;
    }

    static boolean _2D_get7(GLAsyncLoadQueue glasyncloadqueue)
    {
        return glasyncloadqueue.requestGL30;
    }

    static boolean _2D_set0(GLAsyncLoadQueue glasyncloadqueue, boolean flag)
    {
        glasyncloadqueue.contextFailed = flag;
        return flag;
    }

    static boolean _2D_set1(GLAsyncLoadQueue glasyncloadqueue, boolean flag)
    {
        glasyncloadqueue.contextReady = flag;
        return flag;
    }

    public GLAsyncLoadQueue(RenderContext rendercontext, EGL10 egl10_1, EGLDisplay egldisplay, EGLConfig eglconfig, boolean flag)
        throws InstantiationException
    {
        mustExit = new AtomicBoolean(false);
        contextReadyLock = new Object();
        contextReady = false;
        contextFailed = true;
        loadedQueue = new WeakQueue();
        egl10 = egl10_1;
        eglDisplay = egldisplay;
        eglConfig = eglconfig;
        requestGL30 = flag;
        eglBaseContext = egl10_1.eglGetCurrentContext();
        if (eglBaseContext == null || eglBaseContext == EGL10.EGL_NO_CONTEXT)
        {
            break MISSING_BLOCK_LABEL_242;
        }
        thread = new Thread(new EGLLoadThread(rendercontext, null), "EGLLoader");
        thread.setPriority(4);
        thread.start();
        Debug.Printf("TexLoad: Waiting for thread to create context", new Object[0]);
        rendercontext = ((RenderContext) (contextReadyLock));
        rendercontext;
        JVM INSTR monitorenter ;
        while (!contextReady) 
        {
            contextReadyLock.wait();
        }
        break MISSING_BLOCK_LABEL_204;
        egl10_1;
        rendercontext;
        JVM INSTR monitorexit ;
        throw egl10_1;
        rendercontext;
        throw new InstantiationException((new StringBuilder()).append("Interrupted: ").append(rendercontext.getMessage()).toString());
        rendercontext;
        JVM INSTR monitorexit ;
        Debug.Printf("TexLoad: Context created, failed = %b", new Object[] {
            Boolean.valueOf(contextFailed)
        });
        if (contextFailed)
        {
            throw new InstantiationException("TexLoad: failed to create context");
        } else
        {
            return;
        }
        throw new InstantiationException("TexLoad: current context was null");
    }

    public void GLResourceLoaded(GLLoadQueue.GLLoadable glloadable)
    {
        loadedQueue.offer(glloadable);
    }

    public void RunLoadQueue(RenderContext rendercontext)
    {
        do
        {
            rendercontext = (GLLoadQueue.GLLoadable)loadedQueue.poll();
            if (rendercontext != null)
            {
                rendercontext.GLCompleteLoad();
            } else
            {
                return;
            }
        } while (true);
    }

    public void StopLoadQueue()
    {
        Debug.Printf("TexLoad: StopLoadQueue called.", new Object[0]);
        mustExit.set(true);
        thread.interrupt();
        try
        {
            thread.join();
        }
        catch (InterruptedException interruptedexception) { }
        super.StopLoadQueue();
        Debug.Printf("TexLoad: StopLoadQueue exiting.", new Object[0]);
    }

    public void remove(GLLoadQueue.GLLoadable glloadable)
    {
        loadedQueue.remove(glloadable);
        super.remove(glloadable);
    }
}
