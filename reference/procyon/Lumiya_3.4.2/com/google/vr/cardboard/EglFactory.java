// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.os.Build$VERSION;
import javax.microedition.khronos.egl.EGLSurface;
import android.util.Log;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGL10;
import android.opengl.GLSurfaceView$EGLWindowSurfaceFactory;
import android.opengl.GLSurfaceView$EGLContextFactory;

public class EglFactory implements GLSurfaceView$EGLContextFactory, GLSurfaceView$EGLWindowSurfaceFactory
{
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EGL_CONTEXT_PRIORITY_HIGH = 12545;
    private static final int EGL_CONTEXT_PRIORITY_LEVEL = 12544;
    private static final int EGL_PROTECTED_CONTENT_EXT = 12992;
    private static final int MIN_REQUIRED_CONTEXT_CLIENT_VERSION = 2;
    private static final String TAG = "GvrEglFactory";
    private int eglContextClientVersion;
    private boolean usePriority;
    private boolean useProtected;
    
    public EglFactory() {
        this.usePriority = false;
        this.useProtected = false;
        this.eglContextClientVersion = 2;
    }
    
    private boolean supportsProtectedContent(final EGL10 egl10, final EGLDisplay eglDisplay) {
        return egl10.eglQueryString(eglDisplay, 12373).contains("EGL_EXT_protected_content");
    }
    
    public EGLContext createContext(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLConfig eglConfig) {
        final IntBuffer allocate = IntBuffer.allocate(8);
        allocate.put(12440);
        allocate.put(this.eglContextClientVersion);
        if (this.usePriority) {
            allocate.put(12544);
            allocate.put(12545);
        }
        if (this.useProtected && this.supportsProtectedContent(egl10, eglDisplay)) {
            allocate.put(12992);
            allocate.put(1);
        }
        while (allocate.hasRemaining()) {
            allocate.put(12344);
        }
        EGLContext eglContext = egl10.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, allocate.array());
        if (eglContext == null && this.eglContextClientVersion > 2) {
            Log.w("GvrEglFactory", new StringBuilder(75).append("Failed to create EGL context with version ").append(this.eglContextClientVersion).append(", will try 2").toString());
            allocate.array()[1] = 2;
            eglContext = egl10.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, allocate.array());
        }
        return eglContext;
    }
    
    public EGLSurface createWindowSurface(final EGL10 p0, final EGLDisplay p1, final EGLConfig p2, final Object p3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore          5
        //     3: aload_0        
        //     4: getfield        com/google/vr/cardboard/EglFactory.useProtected:Z
        //     7: ifne            28
        //    10: aconst_null    
        //    11: astore          6
        //    13: aload_1        
        //    14: aload_2        
        //    15: aload_3        
        //    16: aload           4
        //    18: aload           6
        //    20: invokeinterface javax/microedition/khronos/egl/EGL10.eglCreateWindowSurface:(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLConfig;Ljava/lang/Object;[I)Ljavax/microedition/khronos/egl/EGLSurface;
        //    25: astore_1       
        //    26: aload_1        
        //    27: areturn        
        //    28: aload_0        
        //    29: aload_1        
        //    30: aload_2        
        //    31: invokespecial   com/google/vr/cardboard/EglFactory.supportsProtectedContent:(Ljavax/microedition/khronos/egl/EGL10;Ljavax/microedition/khronos/egl/EGLDisplay;)Z
        //    34: ifeq            10
        //    37: iconst_3       
        //    38: newarray        I
        //    40: astore          6
        //    42: aload           6
        //    44: iconst_0       
        //    45: sipush          12992
        //    48: iastore        
        //    49: aload           6
        //    51: iconst_1       
        //    52: iconst_1       
        //    53: iastore        
        //    54: aload           6
        //    56: iconst_2       
        //    57: sipush          12344
        //    60: iastore        
        //    61: goto            13
        //    64: astore_1       
        //    65: ldc             "GvrEglFactory"
        //    67: ldc             "eglCreateWindowSurface"
        //    69: aload_1        
        //    70: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //    73: pop            
        //    74: aload           5
        //    76: astore_1       
        //    77: goto            26
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                
        //  -----  -----  -----  -----  ------------------------------------
        //  13     26     64     80     Ljava/lang/IllegalArgumentException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokespecial:boolean(EglFactory::supportsProtectedContent, this:EglFactory, p0:EGL10, p1:EGLDisplay))
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
    
    public void destroyContext(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLContext eglContext) {
        egl10.eglDestroyContext(eglDisplay, eglContext);
    }
    
    public void destroySurface(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLSurface eglSurface) {
        egl10.eglDestroySurface(eglDisplay, eglSurface);
    }
    
    public void setEGLContextClientVersion(final int eglContextClientVersion) {
        this.eglContextClientVersion = eglContextClientVersion;
    }
    
    public void setUsePriorityContext(final boolean usePriority) {
        this.usePriority = usePriority;
    }
    
    public void setUseProtectedBuffers(final boolean useProtected) {
        if (useProtected && Build$VERSION.SDK_INT < 17) {
            throw new RuntimeException("Protected buffer support requires EGL 1.4, available only on Jelly Bean MR1 and later.");
        }
        this.useProtected = useProtected;
    }
}
