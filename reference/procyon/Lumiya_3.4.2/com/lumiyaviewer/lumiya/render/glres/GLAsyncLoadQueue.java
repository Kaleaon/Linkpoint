// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import javax.microedition.khronos.egl.EGLSurface;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import java.util.concurrent.atomic.AtomicBoolean;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGL10;

public class GLAsyncLoadQueue extends GLLoadQueue implements GLLoadHandler
{
    private volatile boolean contextFailed;
    private volatile boolean contextReady;
    private final Object contextReadyLock;
    private final EGL10 egl10;
    private final EGLContext eglBaseContext;
    private final EGLConfig eglConfig;
    private final EGLDisplay eglDisplay;
    private final WeakQueue<GLLoadable> loadedQueue;
    private final AtomicBoolean mustExit;
    private final boolean requestGL30;
    private final Thread thread;
    
    public GLAsyncLoadQueue(final RenderContext renderContext, final EGL10 egl10, final EGLDisplay eglDisplay, final EGLConfig eglConfig, final boolean requestGL30) throws InstantiationException {
        this.mustExit = new AtomicBoolean(false);
        this.contextReadyLock = new Object();
        this.contextReady = false;
        this.contextFailed = true;
        this.loadedQueue = new WeakQueue<GLLoadable>();
        this.egl10 = egl10;
        this.eglDisplay = eglDisplay;
        this.eglConfig = eglConfig;
        this.requestGL30 = requestGL30;
        this.eglBaseContext = egl10.eglGetCurrentContext();
        if (this.eglBaseContext == null || this.eglBaseContext == EGL10.EGL_NO_CONTEXT) {
            throw new InstantiationException("TexLoad: current context was null");
        }
        (this.thread = new Thread(new EGLLoadThread(renderContext, (EGLLoadThread)null), "EGLLoader")).setPriority(4);
        this.thread.start();
        try {
            Debug.Printf("TexLoad: Waiting for thread to create context", new Object[0]);
            synchronized (this.contextReadyLock) {
                while (!this.contextReady) {
                    this.contextReadyLock.wait();
                }
            }
        }
        catch (final InterruptedException ex) {
            throw new InstantiationException("Interrupted: " + ex.getMessage());
        }
        monitorexit(egl10);
        Debug.Printf("TexLoad: Context created, failed = %b", this.contextFailed);
        if (this.contextFailed) {
            throw new InstantiationException("TexLoad: failed to create context");
        }
    }
    
    @Override
    public void GLResourceLoaded(final GLLoadable glLoadable) {
        this.loadedQueue.offer(glLoadable);
    }
    
    @Override
    public void RunLoadQueue(@Nonnull final RenderContext renderContext) {
        while (true) {
            final GLLoadable glLoadable = this.loadedQueue.poll();
            if (glLoadable == null) {
                break;
            }
            glLoadable.GLCompleteLoad();
        }
    }
    
    @Override
    public void StopLoadQueue() {
        Debug.Printf("TexLoad: StopLoadQueue called.", new Object[0]);
        this.mustExit.set(true);
        this.thread.interrupt();
        while (true) {
            try {
                this.thread.join();
                super.StopLoadQueue();
                Debug.Printf("TexLoad: StopLoadQueue exiting.", new Object[0]);
            }
            catch (final InterruptedException ex) {
                continue;
            }
            break;
        }
    }
    
    @Override
    public void remove(@Nonnull final GLLoadable glLoadable) {
        this.loadedQueue.remove(glLoadable);
        super.remove(glLoadable);
    }
    
    private class EGLLoadThread implements Runnable
    {
        private EGLSurface eglSurface;
        private final AtomicReference<RenderContext> renderContext;
        
        private EGLLoadThread(final RenderContext initialValue) {
            this.renderContext = new AtomicReference<RenderContext>(initialValue);
        }
        
        @Nullable
        private EGLContext createContext() {
            int n = 3;
            Debug.Printf("TexLoad: create[1]: eglGetError = %d", GLAsyncLoadQueue.this.egl10.eglGetError());
            final EGL10 -get1 = GLAsyncLoadQueue.this.egl10;
            final EGLDisplay -get2 = GLAsyncLoadQueue.this.eglDisplay;
            final EGLConfig -get3 = GLAsyncLoadQueue.this.eglConfig;
            final EGLContext -get4 = GLAsyncLoadQueue.this.eglBaseContext;
            if (!GLAsyncLoadQueue.this.requestGL30) {
                n = 2;
            }
            final EGLContext eglCreateContext = -get1.eglCreateContext(-get2, -get3, -get4, new int[] { 12440, n, 12344 });
            Debug.Printf("TexLoad: create[2]: eglGetError = %d", GLAsyncLoadQueue.this.egl10.eglGetError());
            final EGLSurface eglCreatePbufferSurface = GLAsyncLoadQueue.this.egl10.eglCreatePbufferSurface(GLAsyncLoadQueue.this.eglDisplay, GLAsyncLoadQueue.this.eglConfig, new int[] { 12374, 128, 12375, 128, 12344 });
            Debug.Printf("TexLoad: create[3]: eglGetError = %d", GLAsyncLoadQueue.this.egl10.eglGetError());
            if (eglCreateContext != null && eglCreateContext != EGL10.EGL_NO_CONTEXT) {
                Debug.Printf("TexLoad: texture loader context created (%s)", eglCreateContext);
                this.eglSurface = eglCreatePbufferSurface;
                return eglCreateContext;
            }
            Debug.Printf("TexLoad: Failed to create loader context", new Object[0]);
            GLAsyncLoadQueue.this.egl10.eglDestroySurface(GLAsyncLoadQueue.this.eglDisplay, eglCreatePbufferSurface);
            return null;
        }
        
        @Override
        public void run() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.renderContext:Ljava/util/concurrent/atomic/AtomicReference;
            //     4: aconst_null    
            //     5: invokevirtual   java/util/concurrent/atomic/AtomicReference.getAndSet:(Ljava/lang/Object;)Ljava/lang/Object;
            //     8: checkcast       Lcom/lumiyaviewer/lumiya/render/RenderContext;
            //    11: astore_1       
            //    12: aload_0        
            //    13: invokespecial   com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.createContext:()Ljavax/microedition/khronos/egl/EGLContext;
            //    16: astore_2       
            //    17: lconst_0       
            //    18: lstore_3       
            //    19: ldc             "TexLoad: Signaling context readiness."
            //    21: iconst_0       
            //    22: anewarray       Ljava/lang/Object;
            //    25: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //    28: aload_0        
            //    29: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //    32: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get0:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljava/lang/Object;
            //    35: astore          5
            //    37: aload           5
            //    39: monitorenter   
            //    40: aload_0        
            //    41: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //    44: astore          6
            //    46: aload_2        
            //    47: ifnonnull       306
            //    50: iconst_1       
            //    51: istore          7
            //    53: aload           6
            //    55: iload           7
            //    57: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-set0:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;Z)Z
            //    60: pop            
            //    61: aload_0        
            //    62: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //    65: iconst_1       
            //    66: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-set1:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;Z)Z
            //    69: pop            
            //    70: aload_0        
            //    71: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //    74: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get0:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljava/lang/Object;
            //    77: invokevirtual   java/lang/Object.notifyAll:()V
            //    80: aload           5
            //    82: monitorexit    
            //    83: aload_2        
            //    84: ifnull          442
            //    87: ldc             "TexLoad: thread init: eglGetError = %d"
            //    89: iconst_1       
            //    90: anewarray       Ljava/lang/Object;
            //    93: dup            
            //    94: iconst_0       
            //    95: aload_0        
            //    96: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //    99: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get1:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGL10;
            //   102: invokeinterface javax/microedition/khronos/egl/EGL10.eglGetError:()I
            //   107: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
            //   110: aastore        
            //   111: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //   114: ldc             "TexLoad: thread init: rc = %b, eglGetError = %d"
            //   116: iconst_2       
            //   117: anewarray       Ljava/lang/Object;
            //   120: dup            
            //   121: iconst_0       
            //   122: aload_0        
            //   123: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   126: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get1:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGL10;
            //   129: aload_0        
            //   130: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   133: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get4:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGLDisplay;
            //   136: aload_0        
            //   137: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.eglSurface:Ljavax/microedition/khronos/egl/EGLSurface;
            //   140: aload_0        
            //   141: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.eglSurface:Ljavax/microedition/khronos/egl/EGLSurface;
            //   144: aload_2        
            //   145: invokeinterface javax/microedition/khronos/egl/EGL10.eglMakeCurrent:(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLSurface;Ljavax/microedition/khronos/egl/EGLSurface;Ljavax/microedition/khronos/egl/EGLContext;)Z
            //   150: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
            //   153: aastore        
            //   154: dup            
            //   155: iconst_1       
            //   156: aload_0        
            //   157: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   160: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get1:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGL10;
            //   163: invokeinterface javax/microedition/khronos/egl/EGL10.eglGetError:()I
            //   168: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
            //   171: aastore        
            //   172: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //   175: iconst_0       
            //   176: istore          8
            //   178: aload_0        
            //   179: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   182: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get6:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljava/util/concurrent/atomic/AtomicBoolean;
            //   185: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
            //   188: ifne            344
            //   191: aload_0        
            //   192: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   195: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.loadQueue:Lcom/lumiyaviewer/lumiya/res/collections/WeakQueue;
            //   198: invokevirtual   com/lumiyaviewer/lumiya/res/collections/WeakQueue.take:()Ljava/lang/Object;
            //   201: checkcast       Lcom/lumiyaviewer/lumiya/render/glres/GLLoadQueue$GLLoadable;
            //   204: astore          5
            //   206: aload           5
            //   208: invokeinterface com/lumiyaviewer/lumiya/render/glres/GLLoadQueue$GLLoadable.GLGetLoadSize:()I
            //   213: invokestatic    com/lumiyaviewer/lumiya/render/TextureMemoryTracker.canAllocateMemory:(I)Z
            //   216: ifne            318
            //   219: aload_0        
            //   220: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   223: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.loadQueue:Lcom/lumiyaviewer/lumiya/res/collections/WeakQueue;
            //   226: aload           5
            //   228: invokevirtual   com/lumiyaviewer/lumiya/res/collections/WeakQueue.offer:(Ljava/lang/Object;)Z
            //   231: pop            
            //   232: ldc2_w          1000
            //   235: invokestatic    java/lang/Thread.sleep:(J)V
            //   238: iload           8
            //   240: iconst_1       
            //   241: iadd           
            //   242: istore          9
            //   244: iload           9
            //   246: istore          8
            //   248: lload_3        
            //   249: lstore          10
            //   251: iload           9
            //   253: bipush          10
            //   255: if_icmplt       300
            //   258: invokestatic    android/os/SystemClock.uptimeMillis:()J
            //   261: lstore          12
            //   263: iload           9
            //   265: istore          8
            //   267: lload_3        
            //   268: lstore          10
            //   270: lload           12
            //   272: lload_3        
            //   273: lsub           
            //   274: ldc2_w          60000
            //   277: lcmp           
            //   278: iflt            300
            //   281: ldc             "TexLoad: invoking GC."
            //   283: iconst_0       
            //   284: anewarray       Ljava/lang/Object;
            //   287: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //   290: invokestatic    java/lang/System.gc:()V
            //   293: iconst_0       
            //   294: istore          8
            //   296: lload           12
            //   298: lstore          10
            //   300: lload           10
            //   302: lstore_3       
            //   303: goto            178
            //   306: iconst_0       
            //   307: istore          7
            //   309: goto            53
            //   312: astore_2       
            //   313: aload           5
            //   315: monitorexit    
            //   316: aload_2        
            //   317: athrow         
            //   318: aload           5
            //   320: aload_1        
            //   321: aload_0        
            //   322: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   325: invokeinterface com/lumiyaviewer/lumiya/render/glres/GLLoadQueue$GLLoadable.GLLoad:(Lcom/lumiyaviewer/lumiya/render/RenderContext;Lcom/lumiyaviewer/lumiya/render/glres/GLLoadQueue$GLLoadHandler;)I
            //   330: pop            
            //   331: invokestatic    android/opengl/GLES10.glFinish:()V
            //   334: iconst_0       
            //   335: istore          8
            //   337: lload_3        
            //   338: lstore          10
            //   340: goto            300
            //   343: astore_1       
            //   344: aload_0        
            //   345: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   348: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get5:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Lcom/lumiyaviewer/lumiya/res/collections/WeakQueue;
            //   351: invokevirtual   com/lumiyaviewer/lumiya/res/collections/WeakQueue.clear:()V
            //   354: ldc             "TexLoad: Working thread exiting."
            //   356: iconst_0       
            //   357: anewarray       Ljava/lang/Object;
            //   360: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //   363: aload_0        
            //   364: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   367: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get1:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGL10;
            //   370: aload_0        
            //   371: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   374: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get4:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGLDisplay;
            //   377: getstatic       javax/microedition/khronos/egl/EGL10.EGL_NO_SURFACE:Ljavax/microedition/khronos/egl/EGLSurface;
            //   380: getstatic       javax/microedition/khronos/egl/EGL10.EGL_NO_SURFACE:Ljavax/microedition/khronos/egl/EGLSurface;
            //   383: getstatic       javax/microedition/khronos/egl/EGL10.EGL_NO_CONTEXT:Ljavax/microedition/khronos/egl/EGLContext;
            //   386: invokeinterface javax/microedition/khronos/egl/EGL10.eglMakeCurrent:(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLSurface;Ljavax/microedition/khronos/egl/EGLSurface;Ljavax/microedition/khronos/egl/EGLContext;)Z
            //   391: pop            
            //   392: aload_0        
            //   393: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   396: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get1:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGL10;
            //   399: aload_0        
            //   400: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   403: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get4:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGLDisplay;
            //   406: aload_2        
            //   407: invokeinterface javax/microedition/khronos/egl/EGL10.eglDestroyContext:(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLContext;)Z
            //   412: pop            
            //   413: aload_0        
            //   414: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   417: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get1:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGL10;
            //   420: aload_0        
            //   421: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;
            //   424: invokestatic    com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue.-get4:(Lcom/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue;)Ljavax/microedition/khronos/egl/EGLDisplay;
            //   427: aload_0        
            //   428: getfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.eglSurface:Ljavax/microedition/khronos/egl/EGLSurface;
            //   431: invokeinterface javax/microedition/khronos/egl/EGL10.eglDestroySurface:(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLSurface;)Z
            //   436: pop            
            //   437: aload_0        
            //   438: aconst_null    
            //   439: putfield        com/lumiyaviewer/lumiya/render/glres/GLAsyncLoadQueue$EGLLoadThread.eglSurface:Ljavax/microedition/khronos/egl/EGLSurface;
            //   442: return         
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                            
            //  -----  -----  -----  -----  --------------------------------
            //  40     46     312    318    Any
            //  53     80     312    318    Any
            //  191    238    343    344    Ljava/lang/InterruptedException;
            //  258    263    343    344    Ljava/lang/InterruptedException;
            //  281    293    343    344    Ljava/lang/InterruptedException;
            //  318    334    343    344    Ljava/lang/InterruptedException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0300:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
    }
}
