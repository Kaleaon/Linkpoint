// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import java.util.ArrayList;
import android.opengl.EGL14;
import java.io.Writer;
import android.opengl.GLDebugHelper;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGL10;
import android.view.SurfaceHolder;
import android.util.Log;
import android.os.Build$VERSION;
import android.view.SurfaceHolder$Callback;
import android.util.AttributeSet;
import android.content.Context;
import java.lang.ref.WeakReference;
import android.opengl.GLSurfaceView$Renderer;
import android.opengl.GLSurfaceView$EGLWindowSurfaceFactory;
import android.opengl.GLSurfaceView$EGLContextFactory;
import android.opengl.GLSurfaceView$EGLConfigChooser;
import android.view.SurfaceHolder$Callback2;
import android.view.SurfaceView;

public class GvrSurfaceView extends SurfaceView implements SurfaceHolder$Callback2
{
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;
    private static final boolean LOG_ATTACH_DETACH = false;
    private static final boolean LOG_EGL = false;
    private static final boolean LOG_PAUSE_RESUME = false;
    private static final boolean LOG_RENDERER = false;
    private static final boolean LOG_RENDERER_DRAW_FRAME = false;
    private static final boolean LOG_SURFACE = false;
    private static final boolean LOG_THREADS = false;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    public static final int SWAPMODE_MANUAL = 2;
    public static final int SWAPMODE_QUEUED = 0;
    public static final int SWAPMODE_SINGLE = 1;
    private static final String TAG = "GvrSurfaceView";
    private int mDebugFlags;
    private boolean mDetached;
    private GLSurfaceView$EGLConfigChooser mEGLConfigChooser;
    private int mEGLContextClientVersion;
    private GLSurfaceView$EGLContextFactory mEGLContextFactory;
    private GLSurfaceView$EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLThread mGLThread;
    private GLWrapper mGLWrapper;
    private boolean mPreserveEGLContextOnPause;
    private GLSurfaceView$Renderer mRenderer;
    private final WeakReference<GvrSurfaceView> mThisWeakRef;
    
    public GvrSurfaceView(final Context context) {
        super(context);
        this.mThisWeakRef = new WeakReference<GvrSurfaceView>(this);
        this.init();
    }
    
    public GvrSurfaceView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mThisWeakRef = new WeakReference<GvrSurfaceView>(this);
        this.init();
    }
    
    private void checkRenderThreadState() {
        if (this.mGLThread == null) {
            return;
        }
        throw new IllegalStateException("setRenderer has already been called for this instance.");
    }
    
    private void init() {
        this.getHolder().addCallback((SurfaceHolder$Callback)this);
    }
    
    protected void finalize() throws Throwable {
        try {
            if (this.mGLThread != null) {
                this.mGLThread.requestExitAndWait();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    public int getDebugFlags() {
        return this.mDebugFlags;
    }
    
    public boolean getPreserveEGLContextOnPause() {
        return this.mPreserveEGLContextOnPause;
    }
    
    public int getRenderMode() {
        return this.mGLThread.getRenderMode();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mDetached && this.mRenderer != null) {
            int swapMode;
            int renderMode;
            if (this.mGLThread == null) {
                swapMode = 0;
                renderMode = 1;
            }
            else {
                renderMode = this.mGLThread.getRenderMode();
                swapMode = this.mGLThread.getSwapMode();
            }
            this.mGLThread = new GLThread(this.mThisWeakRef);
            if (renderMode != 1) {
                this.mGLThread.setRenderMode(renderMode);
            }
            if (swapMode != 0) {
                this.mGLThread.setSwapMode(swapMode);
            }
            this.mGLThread.start();
        }
        this.mDetached = false;
    }
    
    protected void onDetachedFromWindow() {
        if (this.mGLThread != null) {
            this.mGLThread.requestExitAndWait();
        }
        this.mDetached = true;
        super.onDetachedFromWindow();
    }
    
    public void onPause() {
        this.mGLThread.onPause();
    }
    
    public void onResume() {
        this.mGLThread.onResume();
    }
    
    public void queueEvent(final Runnable runnable) {
        this.mGLThread.queueEvent(runnable);
    }
    
    public void requestRender() {
        this.mGLThread.requestRender();
    }
    
    public void setDebugFlags(final int mDebugFlags) {
        this.mDebugFlags = mDebugFlags;
    }
    
    public void setEGLConfigChooser(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.setEGLConfigChooser((GLSurfaceView$EGLConfigChooser)new ComponentSizeChooser(n, n2, n3, n4, n5, n6));
    }
    
    public void setEGLConfigChooser(final GLSurfaceView$EGLConfigChooser meglConfigChooser) {
        this.checkRenderThreadState();
        this.mEGLConfigChooser = meglConfigChooser;
    }
    
    public void setEGLConfigChooser(final boolean b) {
        this.setEGLConfigChooser((GLSurfaceView$EGLConfigChooser)new SimpleEGLConfigChooser(b));
    }
    
    public void setEGLContextClientVersion(final int meglContextClientVersion) {
        this.checkRenderThreadState();
        this.mEGLContextClientVersion = meglContextClientVersion;
    }
    
    public void setEGLContextFactory(final GLSurfaceView$EGLContextFactory meglContextFactory) {
        this.checkRenderThreadState();
        this.mEGLContextFactory = meglContextFactory;
    }
    
    public void setEGLWindowSurfaceFactory(final GLSurfaceView$EGLWindowSurfaceFactory meglWindowSurfaceFactory) {
        this.checkRenderThreadState();
        this.mEGLWindowSurfaceFactory = meglWindowSurfaceFactory;
    }
    
    public void setGLWrapper(final GLWrapper mglWrapper) {
        this.mGLWrapper = mglWrapper;
    }
    
    public void setPreserveEGLContextOnPause(final boolean mPreserveEGLContextOnPause) {
        this.mPreserveEGLContextOnPause = mPreserveEGLContextOnPause;
    }
    
    public void setRenderMode(final int renderMode) {
        this.mGLThread.setRenderMode(renderMode);
    }
    
    public void setRenderer(final GLSurfaceView$Renderer mRenderer) {
        this.checkRenderThreadState();
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = (GLSurfaceView$EGLConfigChooser)new SimpleEGLConfigChooser(true);
        }
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = (GLSurfaceView$EGLContextFactory)new DefaultContextFactory();
        }
        if (this.mEGLWindowSurfaceFactory == null) {
            this.mEGLWindowSurfaceFactory = (GLSurfaceView$EGLWindowSurfaceFactory)new DefaultWindowSurfaceFactory();
        }
        this.mRenderer = mRenderer;
        (this.mGLThread = new GLThread(this.mThisWeakRef)).start();
    }
    
    public void setSwapMode(final int swapMode) {
        if (swapMode == 1 && Build$VERSION.SDK_INT < 17) {
            Log.e("GvrSurfaceView", "setSwapMode(SWAPMODE_SINGLE) requires Jellybean MR1 (EGL14 dependency)");
            return;
        }
        this.mGLThread.setSwapMode(swapMode);
    }
    
    public void surfaceChanged(final SurfaceHolder surfaceHolder, final int n, final int n2, final int n3) {
        this.mGLThread.onWindowResize(n2, n3);
    }
    
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        this.mGLThread.surfaceCreated();
    }
    
    public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
        this.mGLThread.surfaceDestroyed();
    }
    
    public void surfaceRedrawNeeded(final SurfaceHolder surfaceHolder) {
        this.mGLThread.requestRenderAndWait();
    }
    
    private abstract class BaseConfigChooser implements GLSurfaceView$EGLConfigChooser
    {
        protected int[] mConfigSpec;
        
        public BaseConfigChooser(final int[] array) {
            this.mConfigSpec = this.filterConfigSpec(array);
        }
        
        private int[] filterConfigSpec(final int[] array) {
            if (GvrSurfaceView.this.mEGLContextClientVersion != 2 && GvrSurfaceView.this.mEGLContextClientVersion != 3) {
                return array;
            }
            final int length = array.length;
            final int[] array2 = new int[length + 2];
            System.arraycopy(array, 0, array2, 0, length - 1);
            array2[length - 1] = 12352;
            if (GvrSurfaceView.this.mEGLContextClientVersion != 2) {
                array2[length] = 64;
            }
            else {
                array2[length] = 4;
            }
            array2[length + 1] = 12344;
            return array2;
        }
        
        public EGLConfig chooseConfig(final EGL10 egl10, final EGLDisplay eglDisplay) {
            final int[] array = { 0 };
            if (!egl10.eglChooseConfig(eglDisplay, this.mConfigSpec, (EGLConfig[])null, 0, array)) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            final int n = array[0];
            if (n <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            final EGLConfig[] array2 = new EGLConfig[n];
            if (!egl10.eglChooseConfig(eglDisplay, this.mConfigSpec, array2, n, array)) {
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            final EGLConfig chooseConfig = this.chooseConfig(egl10, eglDisplay, array2);
            if (chooseConfig != null) {
                return chooseConfig;
            }
            throw new IllegalArgumentException("No config chosen");
        }
        
        abstract EGLConfig chooseConfig(final EGL10 p0, final EGLDisplay p1, final EGLConfig[] p2);
    }
    
    private class ComponentSizeChooser extends BaseConfigChooser
    {
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue;
        
        public ComponentSizeChooser(final GvrSurfaceView gvrSurfaceView, final int mRedSize, final int mGreenSize, final int mBlueSize, final int mAlphaSize, final int mDepthSize, final int mStencilSize) {
            gvrSurfaceView.super(new int[] { 12324, mRedSize, 12323, mGreenSize, 12322, mBlueSize, 12321, mAlphaSize, 12325, mDepthSize, 12326, mStencilSize, 12344 });
            this.mValue = new int[1];
            this.mRedSize = mRedSize;
            this.mGreenSize = mGreenSize;
            this.mBlueSize = mBlueSize;
            this.mAlphaSize = mAlphaSize;
            this.mDepthSize = mDepthSize;
            this.mStencilSize = mStencilSize;
        }
        
        private int findConfigAttrib(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLConfig eglConfig, final int n, final int n2) {
            if (!egl10.eglGetConfigAttrib(eglDisplay, eglConfig, n, this.mValue)) {
                return n2;
            }
            return this.mValue[0];
        }
        
        public EGLConfig chooseConfig(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLConfig[] array) {
            for (final EGLConfig eglConfig : array) {
                final int configAttrib = this.findConfigAttrib(egl10, eglDisplay, eglConfig, 12325, 0);
                final int configAttrib2 = this.findConfigAttrib(egl10, eglDisplay, eglConfig, 12326, 0);
                if (configAttrib >= this.mDepthSize && configAttrib2 >= this.mStencilSize) {
                    final int configAttrib3 = this.findConfigAttrib(egl10, eglDisplay, eglConfig, 12324, 0);
                    final int configAttrib4 = this.findConfigAttrib(egl10, eglDisplay, eglConfig, 12323, 0);
                    final int configAttrib5 = this.findConfigAttrib(egl10, eglDisplay, eglConfig, 12322, 0);
                    final int configAttrib6 = this.findConfigAttrib(egl10, eglDisplay, eglConfig, 12321, 0);
                    if (configAttrib3 == this.mRedSize && configAttrib4 == this.mGreenSize && configAttrib5 == this.mBlueSize && configAttrib6 == this.mAlphaSize) {
                        return eglConfig;
                    }
                }
            }
            return null;
        }
    }
    
    private class DefaultContextFactory implements GLSurfaceView$EGLContextFactory
    {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        
        public EGLContext createContext(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLConfig eglConfig) {
            int[] array = { 12440, GvrSurfaceView.this.mEGLContextClientVersion, 12344 };
            final EGLContext egl_NO_CONTEXT = EGL10.EGL_NO_CONTEXT;
            if (GvrSurfaceView.this.mEGLContextClientVersion == 0) {
                array = null;
            }
            return egl10.eglCreateContext(eglDisplay, eglConfig, egl_NO_CONTEXT, array);
        }
        
        public void destroyContext(final EGL10 egl10, final EGLDisplay obj, final EGLContext obj2) {
            if (!egl10.eglDestroyContext(obj, obj2)) {
                final String value = String.valueOf(obj);
                final String value2 = String.valueOf(obj2);
                Log.e("DefaultContextFactory", new StringBuilder(String.valueOf(value).length() + 18 + String.valueOf(value2).length()).append("display:").append(value).append(" context: ").append(value2).toString());
                EglHelper.throwEglException("eglDestroyContex", egl10.eglGetError());
            }
        }
    }
    
    private static class DefaultWindowSurfaceFactory implements GLSurfaceView$EGLWindowSurfaceFactory
    {
        public EGLSurface createWindowSurface(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLConfig eglConfig, final Object o) {
            final EGLSurface eglSurface = null;
            try {
                return egl10.eglCreateWindowSurface(eglDisplay, eglConfig, o, (int[])null);
            }
            catch (final IllegalArgumentException ex) {
                Log.e("GvrSurfaceView", "eglCreateWindowSurface", (Throwable)ex);
                return eglSurface;
            }
        }
        
        public void destroySurface(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLSurface eglSurface) {
            egl10.eglDestroySurface(eglDisplay, eglSurface);
        }
    }
    
    private static class EglHelper
    {
        public static final int EGL_FRONT_BUFFER_AUTO_REFRESH = 12620;
        EGL10 mEgl;
        EGLConfig mEglConfig;
        EGLContext mEglContext;
        EGLDisplay mEglDisplay;
        EGLSurface mEglSurface;
        private WeakReference<GvrSurfaceView> mGvrSurfaceViewWeakRef;
        
        public EglHelper(final WeakReference<GvrSurfaceView> mGvrSurfaceViewWeakRef) {
            this.mGvrSurfaceViewWeakRef = mGvrSurfaceViewWeakRef;
        }
        
        private void destroySurfaceImp() {
            if (this.mEglSurface != null && this.mEglSurface != EGL10.EGL_NO_SURFACE) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                final GvrSurfaceView gvrSurfaceView = this.mGvrSurfaceViewWeakRef.get();
                if (gvrSurfaceView != null) {
                    gvrSurfaceView.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
                }
                this.mEglSurface = null;
            }
        }
        
        public static String formatEglError(final String s, final int n) {
            final String value = String.valueOf(getErrorString(n));
            return new StringBuilder(String.valueOf(s).length() + 9 + String.valueOf(value).length()).append(s).append(" failed: ").append(value).toString();
        }
        
        private static String getErrorString(final int n) {
            switch (n) {
                default: {
                    return getHex(n);
                }
                case 12288: {
                    return "EGL_SUCCESS";
                }
                case 12289: {
                    return "EGL_NOT_INITIALIZED";
                }
                case 12290: {
                    return "EGL_BAD_ACCESS";
                }
                case 12291: {
                    return "EGL_BAD_ALLOC";
                }
                case 12292: {
                    return "EGL_BAD_ATTRIBUTE";
                }
                case 12293: {
                    return "EGL_BAD_CONFIG";
                }
                case 12294: {
                    return "EGL_BAD_CONTEXT";
                }
                case 12295: {
                    return "EGL_BAD_CURRENT_SURFACE";
                }
                case 12296: {
                    return "EGL_BAD_DISPLAY";
                }
                case 12297: {
                    return "EGL_BAD_MATCH";
                }
                case 12298: {
                    return "EGL_BAD_NATIVE_PIXMAP";
                }
                case 12299: {
                    return "EGL_BAD_NATIVE_WINDOW";
                }
                case 12300: {
                    return "EGL_BAD_PARAMETER";
                }
                case 12301: {
                    return "EGL_BAD_SURFACE";
                }
                case 12302: {
                    return "EGL_CONTEXT_LOST";
                }
            }
        }
        
        private static String getHex(final int i) {
            final String value = String.valueOf(Integer.toHexString(i));
            if (value.length() == 0) {
                return new String("0x");
            }
            return "0x".concat(value);
        }
        
        public static void logEglErrorAsWarning(final String s, final String s2, final int n) {
            Log.w(s, formatEglError(s2, n));
        }
        
        private void throwEglException(final String s) {
            throwEglException(s, this.mEgl.eglGetError());
        }
        
        public static void throwEglException(final String s, final int n) {
            throw new RuntimeException(formatEglError(s, n));
        }
        
        GL createGL() {
            int n = 0;
            GL gl = this.mEglContext.getGL();
            final GvrSurfaceView gvrSurfaceView = this.mGvrSurfaceViewWeakRef.get();
            GL wrap;
            if (gvrSurfaceView == null) {
                wrap = gl;
            }
            else {
                if (gvrSurfaceView.mGLWrapper != null) {
                    gl = gvrSurfaceView.mGLWrapper.wrap(gl);
                }
                wrap = gl;
                if ((gvrSurfaceView.mDebugFlags & 0x3) != 0x0) {
                    if ((gvrSurfaceView.mDebugFlags & 0x1) != 0x0) {
                        n = 1;
                    }
                    Writer writer;
                    if ((gvrSurfaceView.mDebugFlags & 0x2) == 0x0) {
                        writer = null;
                    }
                    else {
                        writer = new LogWriter();
                    }
                    wrap = GLDebugHelper.wrap(gl, n, writer);
                }
            }
            return wrap;
        }
        
        public boolean createSurface() {
            if (this.mEgl == null) {
                throw new RuntimeException("egl not initialized");
            }
            if (this.mEglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            }
            if (this.mEglConfig == null) {
                throw new RuntimeException("mEglConfig not initialized");
            }
            this.destroySurfaceImp();
            final GvrSurfaceView gvrSurfaceView = this.mGvrSurfaceViewWeakRef.get();
            if (gvrSurfaceView == null) {
                this.mEglSurface = null;
            }
            else {
                this.mEglSurface = gvrSurfaceView.mEGLWindowSurfaceFactory.createWindowSurface(this.mEgl, this.mEglDisplay, this.mEglConfig, (Object)gvrSurfaceView.getHolder());
            }
            if (this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE) {
                if (this.mEgl.eglGetError() == 12299) {
                    Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                }
                return false;
            }
            if (this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
                return true;
            }
            logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", this.mEgl.eglGetError());
            return false;
        }
        
        public void destroySurface() {
            this.destroySurfaceImp();
        }
        
        public void finish() {
            if (this.mEglContext != null) {
                final GvrSurfaceView gvrSurfaceView = this.mGvrSurfaceViewWeakRef.get();
                if (gvrSurfaceView != null) {
                    gvrSurfaceView.mEGLContextFactory.destroyContext(this.mEgl, this.mEglDisplay, this.mEglContext);
                }
                this.mEglContext = null;
            }
            if (this.mEglDisplay != null) {
                this.mEgl.eglTerminate(this.mEglDisplay);
                this.mEglDisplay = null;
            }
        }
        
        public void setEglSurfaceAttrib(final int i, final int j) {
            if (Build$VERSION.SDK_INT >= 17) {
                if (!EGL14.eglSurfaceAttrib(EGL14.eglGetCurrentDisplay(), EGL14.eglGetCurrentSurface(12377), i, j)) {
                    Log.e("EglHelper", new StringBuilder(66).append("eglSurfaceAttrib() failed. attribute=").append(i).append(" value=").append(j).toString());
                }
                return;
            }
            Log.e("EglHelper", "Cannot call eglSurfaceAttrib. API version is too low.");
        }
        
        public void start() {
            this.mEgl = (EGL10)EGLContext.getEGL();
            this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.mEglDisplay == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }
            if (this.mEgl.eglInitialize(this.mEglDisplay, new int[2])) {
                final GvrSurfaceView gvrSurfaceView = this.mGvrSurfaceViewWeakRef.get();
                if (gvrSurfaceView != null) {
                    this.mEglConfig = gvrSurfaceView.mEGLConfigChooser.chooseConfig(this.mEgl, this.mEglDisplay);
                    this.mEglContext = gvrSurfaceView.mEGLContextFactory.createContext(this.mEgl, this.mEglDisplay, this.mEglConfig);
                }
                else {
                    this.mEglConfig = null;
                    this.mEglContext = null;
                }
                if (this.mEglContext == null || this.mEglContext == EGL10.EGL_NO_CONTEXT) {
                    this.mEglContext = null;
                    this.throwEglException("createContext");
                }
                this.mEglSurface = null;
                return;
            }
            throw new RuntimeException("eglInitialize failed");
        }
        
        public int swap() {
            if (this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface)) {
                return 12288;
            }
            return this.mEgl.eglGetError();
        }
    }
    
    static class GLThread extends Thread
    {
        private EglHelper mEglHelper;
        private ArrayList<Runnable> mEventQueue;
        private boolean mExited;
        private boolean mFinishedCreatingEglSurface;
        private final GLThreadManager mGLThreadManager;
        private WeakReference<GvrSurfaceView> mGvrSurfaceViewWeakRef;
        private boolean mHasSurface;
        private boolean mHaveEglContext;
        private boolean mHaveEglSurface;
        private int mHeight;
        private boolean mPaused;
        private boolean mRenderComplete;
        private int mRenderMode;
        private boolean mRequestPaused;
        private boolean mRequestRender;
        private int mRequestedSwapMode;
        private boolean mShouldExit;
        private boolean mShouldReleaseEglContext;
        private boolean mSizeChanged;
        private boolean mSurfaceIsBad;
        private boolean mWaitingForSurface;
        private boolean mWantRenderNotification;
        private int mWidth;
        
        GLThread(final WeakReference<GvrSurfaceView> mGvrSurfaceViewWeakRef) {
            this.mEventQueue = new ArrayList<Runnable>();
            this.mSizeChanged = true;
            this.mGLThreadManager = new GLThreadManager();
            this.mWidth = 0;
            this.mHeight = 0;
            this.mRequestRender = true;
            this.mRenderMode = 1;
            this.mRequestedSwapMode = 0;
            this.mWantRenderNotification = false;
            this.mGvrSurfaceViewWeakRef = mGvrSurfaceViewWeakRef;
        }
        
        private void guardedRun() throws InterruptedException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: new             Lcom/google/vr/ndk/base/GvrSurfaceView$EglHelper;
            //     4: dup            
            //     5: aload_0        
            //     6: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGvrSurfaceViewWeakRef:Ljava/lang/ref/WeakReference;
            //     9: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$EglHelper.<init>:(Ljava/lang/ref/WeakReference;)V
            //    12: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEglHelper:Lcom/google/vr/ndk/base/GvrSurfaceView$EglHelper;
            //    15: aload_0        
            //    16: iconst_0       
            //    17: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglContext:Z
            //    20: aload_0        
            //    21: iconst_0       
            //    22: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglSurface:Z
            //    25: aload_0        
            //    26: iconst_0       
            //    27: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mWantRenderNotification:Z
            //    30: aconst_null    
            //    31: astore_1       
            //    32: iconst_0       
            //    33: istore_2       
            //    34: iconst_0       
            //    35: istore_3       
            //    36: iconst_0       
            //    37: istore          4
            //    39: iconst_0       
            //    40: istore          5
            //    42: iconst_0       
            //    43: istore          6
            //    45: iconst_0       
            //    46: istore          7
            //    48: iconst_0       
            //    49: istore          8
            //    51: iconst_0       
            //    52: istore          9
            //    54: iconst_0       
            //    55: istore          10
            //    57: iconst_0       
            //    58: istore          11
            //    60: iconst_0       
            //    61: istore          12
            //    63: iconst_0       
            //    64: istore          13
            //    66: aconst_null    
            //    67: astore          14
            //    69: aload_0        
            //    70: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //    73: astore          15
            //    75: aload           15
            //    77: monitorenter   
            //    78: iload           13
            //    80: istore          16
            //    82: iload           5
            //    84: istore          13
            //    86: aload_0        
            //    87: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mShouldExit:Z
            //    90: ifne            244
            //    93: aload_0        
            //    94: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEventQueue:Ljava/util/ArrayList;
            //    97: invokevirtual   java/util/ArrayList.isEmpty:()Z
            //   100: ifeq            274
            //   103: iconst_0       
            //   104: istore          17
            //   106: aload_0        
            //   107: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mPaused:Z
            //   110: aload_0        
            //   111: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mRequestPaused:Z
            //   114: if_icmpne       512
            //   117: aload_0        
            //   118: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mShouldReleaseEglContext:Z
            //   121: ifne            536
            //   124: iload           16
            //   126: ifne            554
            //   129: iload           17
            //   131: ifne            568
            //   134: iload           17
            //   136: ifne            582
            //   139: aload_0        
            //   140: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHasSurface:Z
            //   143: ifeq            631
            //   146: aload_0        
            //   147: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHasSurface:Z
            //   150: ifne            672
            //   153: iload           10
            //   155: ifne            694
            //   158: iload           10
            //   160: istore          18
            //   162: aload_0        
            //   163: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.readyToDraw:()Z
            //   166: ifne            717
            //   169: iload           11
            //   171: istore          19
            //   173: iload           4
            //   175: istore          20
            //   177: iload           13
            //   179: istore          21
            //   181: iload_3        
            //   182: istore          4
            //   184: aload_0        
            //   185: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   188: invokevirtual   java/lang/Object.wait:()V
            //   191: iload           4
            //   193: istore_3       
            //   194: iload           18
            //   196: istore          10
            //   198: iload           21
            //   200: istore          13
            //   202: iload           20
            //   204: istore          4
            //   206: iload           19
            //   208: istore          11
            //   210: goto            86
            //   213: astore          14
            //   215: aload           15
            //   217: monitorexit    
            //   218: aload           14
            //   220: athrow         
            //   221: astore_1       
            //   222: aload_0        
            //   223: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   226: astore          14
            //   228: aload           14
            //   230: monitorenter   
            //   231: aload_0        
            //   232: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglSurfaceLocked:()V
            //   235: aload_0        
            //   236: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglContextLocked:()V
            //   239: aload           14
            //   241: monitorexit    
            //   242: aload_1        
            //   243: athrow         
            //   244: aload           15
            //   246: monitorexit    
            //   247: aload_0        
            //   248: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   251: astore          14
            //   253: aload           14
            //   255: monitorenter   
            //   256: aload_0        
            //   257: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglSurfaceLocked:()V
            //   260: aload_0        
            //   261: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglContextLocked:()V
            //   264: aload           14
            //   266: monitorexit    
            //   267: return         
            //   268: astore_1       
            //   269: aload           14
            //   271: monitorexit    
            //   272: aload_1        
            //   273: athrow         
            //   274: aload_0        
            //   275: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEventQueue:Ljava/util/ArrayList;
            //   278: iconst_0       
            //   279: invokevirtual   java/util/ArrayList.remove:(I)Ljava/lang/Object;
            //   282: checkcast       Ljava/lang/Runnable;
            //   285: astore          14
            //   287: iload           10
            //   289: istore          5
            //   291: iload           4
            //   293: istore          18
            //   295: iload           6
            //   297: istore          4
            //   299: iload           13
            //   301: istore          6
            //   303: iload           7
            //   305: istore          10
            //   307: iload           12
            //   309: istore          7
            //   311: iload           4
            //   313: istore          12
            //   315: iload           11
            //   317: istore          4
            //   319: iload           8
            //   321: istore          13
            //   323: iload           6
            //   325: istore          11
            //   327: iload           16
            //   329: istore          6
            //   331: iload           18
            //   333: istore          8
            //   335: aload           15
            //   337: monitorexit    
            //   338: aload           14
            //   340: ifnonnull       984
            //   343: iload           4
            //   345: ifne            1045
            //   348: iload           4
            //   350: istore          16
            //   352: iload_2        
            //   353: istore          4
            //   355: iload           16
            //   357: istore_2       
            //   358: iload           8
            //   360: ifne            1183
            //   363: iload           13
            //   365: ifne            1200
            //   368: iload           11
            //   370: ifne            1260
            //   373: iload           7
            //   375: ifne            1317
            //   378: aload_0        
            //   379: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGvrSurfaceViewWeakRef:Ljava/lang/ref/WeakReference;
            //   382: invokevirtual   java/lang/ref/WeakReference.get:()Ljava/lang/Object;
            //   385: checkcast       Lcom/google/vr/ndk/base/GvrSurfaceView;
            //   388: astore          15
            //   390: aload           15
            //   392: ifnonnull       1386
            //   395: iload           7
            //   397: ifeq            1416
            //   400: aload_0        
            //   401: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEglHelper:Lcom/google/vr/ndk/base/GvrSurfaceView$EglHelper;
            //   404: invokevirtual   com/google/vr/ndk/base/GvrSurfaceView$EglHelper.swap:()I
            //   407: istore          16
            //   409: iload           16
            //   411: lookupswitch {
            //            12288: 1424
            //            12302: 1427
            //          default: 436
            //        }
            //   436: ldc             "GLThread"
            //   438: ldc             "eglSwapBuffers"
            //   440: iload           16
            //   442: invokestatic    com/google/vr/ndk/base/GvrSurfaceView$EglHelper.logEglErrorAsWarning:(Ljava/lang/String;Ljava/lang/String;I)V
            //   445: iload           4
            //   447: ifeq            1433
            //   450: iload           10
            //   452: ifne            1468
            //   455: iload_2        
            //   456: istore          16
            //   458: iload           4
            //   460: istore_2       
            //   461: iload           7
            //   463: istore          19
            //   465: iload           6
            //   467: istore          4
            //   469: iload           12
            //   471: istore          6
            //   473: iload           10
            //   475: istore          7
            //   477: iload           13
            //   479: istore          18
            //   481: iload           19
            //   483: istore          12
            //   485: iload           5
            //   487: istore          10
            //   489: iload           11
            //   491: istore          5
            //   493: iload           4
            //   495: istore          13
            //   497: iload           8
            //   499: istore          4
            //   501: iload           16
            //   503: istore          11
            //   505: iload           18
            //   507: istore          8
            //   509: goto            69
            //   512: aload_0        
            //   513: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mRequestPaused:Z
            //   516: istore          17
            //   518: aload_0        
            //   519: aload_0        
            //   520: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mRequestPaused:Z
            //   523: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mPaused:Z
            //   526: aload_0        
            //   527: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   530: invokevirtual   java/lang/Object.notifyAll:()V
            //   533: goto            117
            //   536: aload_0        
            //   537: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglSurfaceLocked:()V
            //   540: aload_0        
            //   541: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglContextLocked:()V
            //   544: aload_0        
            //   545: iconst_0       
            //   546: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mShouldReleaseEglContext:Z
            //   549: iconst_1       
            //   550: istore_3       
            //   551: goto            124
            //   554: aload_0        
            //   555: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglSurfaceLocked:()V
            //   558: aload_0        
            //   559: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglContextLocked:()V
            //   562: iconst_0       
            //   563: istore          16
            //   565: goto            129
            //   568: aload_0        
            //   569: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglSurface:Z
            //   572: ifeq            134
            //   575: aload_0        
            //   576: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglSurfaceLocked:()V
            //   579: goto            134
            //   582: aload_0        
            //   583: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglContext:Z
            //   586: ifeq            139
            //   589: aload_0        
            //   590: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGvrSurfaceViewWeakRef:Ljava/lang/ref/WeakReference;
            //   593: invokevirtual   java/lang/ref/WeakReference.get:()Ljava/lang/Object;
            //   596: checkcast       Lcom/google/vr/ndk/base/GvrSurfaceView;
            //   599: astore          22
            //   601: aload           22
            //   603: ifnull          625
            //   606: aload           22
            //   608: invokestatic    com/google/vr/ndk/base/GvrSurfaceView.access$800:(Lcom/google/vr/ndk/base/GvrSurfaceView;)Z
            //   611: istore          17
            //   613: iload           17
            //   615: ifne            139
            //   618: aload_0        
            //   619: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglContextLocked:()V
            //   622: goto            139
            //   625: iconst_0       
            //   626: istore          17
            //   628: goto            613
            //   631: aload_0        
            //   632: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mWaitingForSurface:Z
            //   635: ifne            146
            //   638: aload_0        
            //   639: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglSurface:Z
            //   642: ifne            665
            //   645: aload_0        
            //   646: iconst_1       
            //   647: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mWaitingForSurface:Z
            //   650: aload_0        
            //   651: iconst_0       
            //   652: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mSurfaceIsBad:Z
            //   655: aload_0        
            //   656: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   659: invokevirtual   java/lang/Object.notifyAll:()V
            //   662: goto            146
            //   665: aload_0        
            //   666: invokespecial   com/google/vr/ndk/base/GvrSurfaceView$GLThread.stopEglSurfaceLocked:()V
            //   669: goto            645
            //   672: aload_0        
            //   673: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mWaitingForSurface:Z
            //   676: ifeq            153
            //   679: aload_0        
            //   680: iconst_0       
            //   681: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mWaitingForSurface:Z
            //   684: aload_0        
            //   685: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   688: invokevirtual   java/lang/Object.notifyAll:()V
            //   691: goto            153
            //   694: aload_0        
            //   695: iconst_0       
            //   696: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mWantRenderNotification:Z
            //   699: iconst_0       
            //   700: istore          18
            //   702: aload_0        
            //   703: iconst_1       
            //   704: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mRenderComplete:Z
            //   707: aload_0        
            //   708: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   711: invokevirtual   java/lang/Object.notifyAll:()V
            //   714: goto            162
            //   717: aload_0        
            //   718: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglContext:Z
            //   721: ifeq            862
            //   724: iload           8
            //   726: istore          5
            //   728: aload_0        
            //   729: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglContext:Z
            //   732: ifne            913
            //   735: iload           4
            //   737: istore          10
            //   739: iload_3        
            //   740: istore          4
            //   742: iload           13
            //   744: istore          21
            //   746: iload           10
            //   748: istore          20
            //   750: iload           11
            //   752: istore          19
            //   754: iload           5
            //   756: istore          8
            //   758: aload_0        
            //   759: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglSurface:Z
            //   762: ifeq            184
            //   765: aload_0        
            //   766: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mSizeChanged:Z
            //   769: ifne            941
            //   772: aload_0        
            //   773: iconst_0       
            //   774: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mRequestRender:Z
            //   777: aload_0        
            //   778: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   781: invokevirtual   java/lang/Object.notifyAll:()V
            //   784: aload_0        
            //   785: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mWantRenderNotification:Z
            //   788: ifne            972
            //   791: iload           7
            //   793: istore          12
            //   795: aload_0        
            //   796: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mRequestedSwapMode:I
            //   799: iload_2        
            //   800: if_icmpne       978
            //   803: iconst_0       
            //   804: istore          7
            //   806: aload_0        
            //   807: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mRequestedSwapMode:I
            //   810: istore_2       
            //   811: iload           6
            //   813: istore          20
            //   815: iload           5
            //   817: istore          4
            //   819: iload           12
            //   821: istore          19
            //   823: iload           11
            //   825: istore          12
            //   827: iload           18
            //   829: istore          5
            //   831: iload           16
            //   833: istore          6
            //   835: iload           10
            //   837: istore          8
            //   839: iload           13
            //   841: istore          11
            //   843: iload           4
            //   845: istore          13
            //   847: iload           12
            //   849: istore          4
            //   851: iload           20
            //   853: istore          12
            //   855: iload           19
            //   857: istore          10
            //   859: goto            335
            //   862: iload_3        
            //   863: ifne            891
            //   866: aload_0        
            //   867: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEglHelper:Lcom/google/vr/ndk/base/GvrSurfaceView$EglHelper;
            //   870: invokevirtual   com/google/vr/ndk/base/GvrSurfaceView$EglHelper.start:()V
            //   873: aload_0        
            //   874: iconst_1       
            //   875: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglContext:Z
            //   878: iconst_1       
            //   879: istore          5
            //   881: aload_0        
            //   882: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   885: invokevirtual   java/lang/Object.notifyAll:()V
            //   888: goto            728
            //   891: iconst_0       
            //   892: istore_3       
            //   893: iload           8
            //   895: istore          5
            //   897: goto            728
            //   900: astore          14
            //   902: aload_0        
            //   903: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //   906: aload_0        
            //   907: invokevirtual   com/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager.releaseEglContextLocked:(Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread;)V
            //   910: aload           14
            //   912: athrow         
            //   913: iload           4
            //   915: istore          10
            //   917: aload_0        
            //   918: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglSurface:Z
            //   921: ifne            739
            //   924: aload_0        
            //   925: iconst_1       
            //   926: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHaveEglSurface:Z
            //   929: iconst_1       
            //   930: istore          11
            //   932: iconst_1       
            //   933: istore          10
            //   935: iconst_1       
            //   936: istore          13
            //   938: goto            739
            //   941: iconst_1       
            //   942: istore          13
            //   944: aload_0        
            //   945: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mWidth:I
            //   948: istore          9
            //   950: aload_0        
            //   951: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mHeight:I
            //   954: istore          6
            //   956: aload_0        
            //   957: iconst_1       
            //   958: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mWantRenderNotification:Z
            //   961: iconst_1       
            //   962: istore          11
            //   964: aload_0        
            //   965: iconst_0       
            //   966: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mSizeChanged:Z
            //   969: goto            772
            //   972: iconst_1       
            //   973: istore          12
            //   975: goto            795
            //   978: iconst_1       
            //   979: istore          7
            //   981: goto            806
            //   984: aload           14
            //   986: invokeinterface java/lang/Runnable.run:()V
            //   991: iload           10
            //   993: istore          18
            //   995: iload           13
            //   997: istore          16
            //   999: iload           5
            //  1001: istore          10
            //  1003: iload           4
            //  1005: istore          19
            //  1007: iload           6
            //  1009: istore          13
            //  1011: aconst_null    
            //  1012: astore          14
            //  1014: iload           12
            //  1016: istore          6
            //  1018: iload           7
            //  1020: istore          12
            //  1022: iload           18
            //  1024: istore          7
            //  1026: iload           11
            //  1028: istore          5
            //  1030: iload           8
            //  1032: istore          4
            //  1034: iload           19
            //  1036: istore          11
            //  1038: iload           16
            //  1040: istore          8
            //  1042: goto            69
            //  1045: aload_0        
            //  1046: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEglHelper:Lcom/google/vr/ndk/base/GvrSurfaceView$EglHelper;
            //  1049: invokevirtual   com/google/vr/ndk/base/GvrSurfaceView$EglHelper.createSurface:()Z
            //  1052: ifne            1135
            //  1055: aload_0        
            //  1056: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //  1059: astore          15
            //  1061: aload           15
            //  1063: monitorenter   
            //  1064: aload_0        
            //  1065: iconst_1       
            //  1066: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mFinishedCreatingEglSurface:Z
            //  1069: aload_0        
            //  1070: iconst_1       
            //  1071: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mSurfaceIsBad:Z
            //  1074: aload_0        
            //  1075: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //  1078: invokevirtual   java/lang/Object.notifyAll:()V
            //  1081: aload           15
            //  1083: monitorexit    
            //  1084: iload           10
            //  1086: istore          18
            //  1088: iload           13
            //  1090: istore          16
            //  1092: iload           5
            //  1094: istore          10
            //  1096: iload           4
            //  1098: istore          19
            //  1100: iload           6
            //  1102: istore          13
            //  1104: iload           12
            //  1106: istore          6
            //  1108: iload           7
            //  1110: istore          12
            //  1112: iload           18
            //  1114: istore          7
            //  1116: iload           11
            //  1118: istore          5
            //  1120: iload           8
            //  1122: istore          4
            //  1124: iload           19
            //  1126: istore          11
            //  1128: iload           16
            //  1130: istore          8
            //  1132: goto            69
            //  1135: aload_0        
            //  1136: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //  1139: astore          15
            //  1141: aload           15
            //  1143: monitorenter   
            //  1144: aload_0        
            //  1145: iconst_1       
            //  1146: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mFinishedCreatingEglSurface:Z
            //  1149: aload_0        
            //  1150: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //  1153: invokevirtual   java/lang/Object.notifyAll:()V
            //  1156: aload           15
            //  1158: monitorexit    
            //  1159: iconst_0       
            //  1160: istore_2       
            //  1161: iconst_0       
            //  1162: istore          4
            //  1164: goto            358
            //  1167: astore          14
            //  1169: aload           15
            //  1171: monitorexit    
            //  1172: aload           14
            //  1174: athrow         
            //  1175: astore          14
            //  1177: aload           15
            //  1179: monitorexit    
            //  1180: aload           14
            //  1182: athrow         
            //  1183: aload_0        
            //  1184: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEglHelper:Lcom/google/vr/ndk/base/GvrSurfaceView$EglHelper;
            //  1187: invokevirtual   com/google/vr/ndk/base/GvrSurfaceView$EglHelper.createGL:()Ljavax/microedition/khronos/opengles/GL;
            //  1190: checkcast       Ljavax/microedition/khronos/opengles/GL10;
            //  1193: astore_1       
            //  1194: iconst_0       
            //  1195: istore          8
            //  1197: goto            363
            //  1200: aload_0        
            //  1201: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGvrSurfaceViewWeakRef:Ljava/lang/ref/WeakReference;
            //  1204: invokevirtual   java/lang/ref/WeakReference.get:()Ljava/lang/Object;
            //  1207: checkcast       Lcom/google/vr/ndk/base/GvrSurfaceView;
            //  1210: astore          15
            //  1212: aload           15
            //  1214: ifnonnull       1223
            //  1217: iconst_0       
            //  1218: istore          13
            //  1220: goto            368
            //  1223: ldc             "onSurfaceCreated"
            //  1225: invokestatic    com/google/vr/ndk/base/TraceCompat.beginSection:(Ljava/lang/String;)V
            //  1228: aload           15
            //  1230: invokestatic    com/google/vr/ndk/base/GvrSurfaceView.access$900:(Lcom/google/vr/ndk/base/GvrSurfaceView;)Landroid/opengl/GLSurfaceView$Renderer;
            //  1233: aload_1        
            //  1234: aload_0        
            //  1235: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEglHelper:Lcom/google/vr/ndk/base/GvrSurfaceView$EglHelper;
            //  1238: getfield        com/google/vr/ndk/base/GvrSurfaceView$EglHelper.mEglConfig:Ljavax/microedition/khronos/egl/EGLConfig;
            //  1241: invokeinterface android/opengl/GLSurfaceView$Renderer.onSurfaceCreated:(Ljavax/microedition/khronos/opengles/GL10;Ljavax/microedition/khronos/egl/EGLConfig;)V
            //  1246: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
            //  1249: goto            1217
            //  1252: astore          14
            //  1254: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
            //  1257: aload           14
            //  1259: athrow         
            //  1260: aload_0        
            //  1261: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGvrSurfaceViewWeakRef:Ljava/lang/ref/WeakReference;
            //  1264: invokevirtual   java/lang/ref/WeakReference.get:()Ljava/lang/Object;
            //  1267: checkcast       Lcom/google/vr/ndk/base/GvrSurfaceView;
            //  1270: astore          15
            //  1272: aload           15
            //  1274: ifnonnull       1283
            //  1277: iconst_0       
            //  1278: istore          11
            //  1280: goto            373
            //  1283: ldc             "onSurfaceChanged"
            //  1285: invokestatic    com/google/vr/ndk/base/TraceCompat.beginSection:(Ljava/lang/String;)V
            //  1288: aload           15
            //  1290: invokestatic    com/google/vr/ndk/base/GvrSurfaceView.access$900:(Lcom/google/vr/ndk/base/GvrSurfaceView;)Landroid/opengl/GLSurfaceView$Renderer;
            //  1293: aload_1        
            //  1294: iload           9
            //  1296: iload           12
            //  1298: invokeinterface android/opengl/GLSurfaceView$Renderer.onSurfaceChanged:(Ljavax/microedition/khronos/opengles/GL10;II)V
            //  1303: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
            //  1306: goto            1277
            //  1309: astore          14
            //  1311: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
            //  1314: aload           14
            //  1316: athrow         
            //  1317: aload_0        
            //  1318: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEglHelper:Lcom/google/vr/ndk/base/GvrSurfaceView$EglHelper;
            //  1321: astore          15
            //  1323: iload           4
            //  1325: iconst_1       
            //  1326: if_icmpeq       1372
            //  1329: sipush          12420
            //  1332: istore          16
            //  1334: aload           15
            //  1336: sipush          12422
            //  1339: iload           16
            //  1341: invokevirtual   com/google/vr/ndk/base/GvrSurfaceView$EglHelper.setEglSurfaceAttrib:(II)V
            //  1344: aload_0        
            //  1345: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mEglHelper:Lcom/google/vr/ndk/base/GvrSurfaceView$EglHelper;
            //  1348: astore          15
            //  1350: iload           4
            //  1352: iconst_1       
            //  1353: if_icmpeq       1380
            //  1356: iconst_0       
            //  1357: istore          16
            //  1359: aload           15
            //  1361: sipush          12620
            //  1364: iload           16
            //  1366: invokevirtual   com/google/vr/ndk/base/GvrSurfaceView$EglHelper.setEglSurfaceAttrib:(II)V
            //  1369: goto            378
            //  1372: sipush          12421
            //  1375: istore          16
            //  1377: goto            1334
            //  1380: iconst_1       
            //  1381: istore          16
            //  1383: goto            1359
            //  1386: ldc             "onDrawFrame"
            //  1388: invokestatic    com/google/vr/ndk/base/TraceCompat.beginSection:(Ljava/lang/String;)V
            //  1391: aload           15
            //  1393: invokestatic    com/google/vr/ndk/base/GvrSurfaceView.access$900:(Lcom/google/vr/ndk/base/GvrSurfaceView;)Landroid/opengl/GLSurfaceView$Renderer;
            //  1396: aload_1        
            //  1397: invokeinterface android/opengl/GLSurfaceView$Renderer.onDrawFrame:(Ljavax/microedition/khronos/opengles/GL10;)V
            //  1402: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
            //  1405: goto            395
            //  1408: astore          14
            //  1410: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
            //  1413: aload           14
            //  1415: athrow         
            //  1416: iload           4
            //  1418: ifeq            400
            //  1421: goto            450
            //  1424: goto            450
            //  1427: iconst_1       
            //  1428: istore          6
            //  1430: goto            450
            //  1433: aload_0        
            //  1434: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //  1437: astore          15
            //  1439: aload           15
            //  1441: monitorenter   
            //  1442: aload_0        
            //  1443: iconst_1       
            //  1444: putfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mSurfaceIsBad:Z
            //  1447: aload_0        
            //  1448: getfield        com/google/vr/ndk/base/GvrSurfaceView$GLThread.mGLThreadManager:Lcom/google/vr/ndk/base/GvrSurfaceView$GLThread$GLThreadManager;
            //  1451: invokevirtual   java/lang/Object.notifyAll:()V
            //  1454: aload           15
            //  1456: monitorexit    
            //  1457: goto            450
            //  1460: astore          14
            //  1462: aload           15
            //  1464: monitorexit    
            //  1465: aload           14
            //  1467: athrow         
            //  1468: iconst_1       
            //  1469: istore          10
            //  1471: iload_2        
            //  1472: istore          16
            //  1474: iload           4
            //  1476: istore_2       
            //  1477: iload           6
            //  1479: istore          4
            //  1481: iload           12
            //  1483: istore          6
            //  1485: iconst_0       
            //  1486: istore          19
            //  1488: iload           13
            //  1490: istore          18
            //  1492: iload           11
            //  1494: istore          5
            //  1496: iload           7
            //  1498: istore          12
            //  1500: iload           19
            //  1502: istore          7
            //  1504: iload           4
            //  1506: istore          13
            //  1508: iload           8
            //  1510: istore          4
            //  1512: iload           16
            //  1514: istore          11
            //  1516: iload           18
            //  1518: istore          8
            //  1520: goto            69
            //  1523: astore_1       
            //  1524: aload           14
            //  1526: monitorexit    
            //  1527: aload_1        
            //  1528: athrow         
            //    Exceptions:
            //  throws java.lang.InterruptedException
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                        
            //  -----  -----  -----  -----  ----------------------------
            //  69     78     221    1529   Any
            //  86     103    213    221    Any
            //  106    117    213    221    Any
            //  117    124    213    221    Any
            //  139    146    213    221    Any
            //  146    153    213    221    Any
            //  162    169    213    221    Any
            //  184    191    213    221    Any
            //  215    218    213    221    Any
            //  218    221    221    1529   Any
            //  231    242    1523   1529   Any
            //  244    247    213    221    Any
            //  256    267    268    274    Any
            //  269    272    268    274    Any
            //  274    287    213    221    Any
            //  335    338    213    221    Any
            //  378    390    221    1529   Any
            //  400    409    221    1529   Any
            //  436    445    221    1529   Any
            //  512    533    213    221    Any
            //  536    549    213    221    Any
            //  554    562    213    221    Any
            //  568    579    213    221    Any
            //  582    601    213    221    Any
            //  606    613    213    221    Any
            //  618    622    213    221    Any
            //  631    645    213    221    Any
            //  645    662    213    221    Any
            //  665    669    213    221    Any
            //  672    691    213    221    Any
            //  694    699    213    221    Any
            //  702    714    213    221    Any
            //  717    724    213    221    Any
            //  728    735    213    221    Any
            //  758    772    213    221    Any
            //  772    791    213    221    Any
            //  795    803    213    221    Any
            //  806    811    213    221    Any
            //  866    873    900    913    Ljava/lang/RuntimeException;
            //  866    873    213    221    Any
            //  873    878    213    221    Any
            //  881    888    213    221    Any
            //  902    913    213    221    Any
            //  917    929    213    221    Any
            //  944    961    213    221    Any
            //  964    969    213    221    Any
            //  984    991    221    1529   Any
            //  1045   1064   221    1529   Any
            //  1064   1084   1175   1183   Any
            //  1135   1144   221    1529   Any
            //  1144   1159   1167   1175   Any
            //  1169   1172   1167   1175   Any
            //  1172   1175   221    1529   Any
            //  1177   1180   1175   1183   Any
            //  1180   1183   221    1529   Any
            //  1183   1194   221    1529   Any
            //  1200   1212   221    1529   Any
            //  1223   1246   1252   1260   Any
            //  1246   1249   221    1529   Any
            //  1254   1260   221    1529   Any
            //  1260   1272   221    1529   Any
            //  1283   1303   1309   1317   Any
            //  1303   1306   221    1529   Any
            //  1311   1317   221    1529   Any
            //  1317   1323   221    1529   Any
            //  1334   1350   221    1529   Any
            //  1359   1369   221    1529   Any
            //  1386   1402   1408   1416   Any
            //  1402   1405   221    1529   Any
            //  1410   1416   221    1529   Any
            //  1433   1442   221    1529   Any
            //  1442   1457   1460   1468   Any
            //  1462   1465   1460   1468   Any
            //  1465   1468   221    1529   Any
            //  1524   1527   1523   1529   Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: getfield:boolean(GLThread::mWaitingForSurface, this:GLThread)
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
            //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
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
        
        private boolean readyToDraw() {
            return !this.mPaused && this.mHasSurface && !this.mSurfaceIsBad && this.mWidth > 0 && this.mHeight > 0 && (this.mRequestRender || this.mRenderMode == 1);
        }
        
        private void stopEglContextLocked() {
            if (this.mHaveEglContext) {
                this.mEglHelper.finish();
                this.mHaveEglContext = false;
                this.mGLThreadManager.releaseEglContextLocked(this);
            }
        }
        
        private void stopEglSurfaceLocked() {
            if (this.mHaveEglSurface) {
                this.mHaveEglSurface = false;
                this.mEglHelper.destroySurface();
            }
        }
        
        public boolean ableToDraw() {
            return this.mHaveEglContext && this.mHaveEglSurface && this.readyToDraw();
        }
        
        public int getRenderMode() {
            synchronized (this.mGLThreadManager) {
                return this.mRenderMode;
            }
        }
        
        public int getSwapMode() {
            synchronized (this.mGLThreadManager) {
                return this.mRequestedSwapMode;
            }
        }
        
        public void onPause() {
            synchronized (this.mGLThreadManager) {
                this.mRequestPaused = true;
                this.mGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused) {
                    try {
                        this.mGLThreadManager.wait();
                    }
                    catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        public void onResume() {
            synchronized (this.mGLThreadManager) {
                this.mRequestPaused = false;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                this.mGLThreadManager.notifyAll();
                while (!this.mExited && this.mPaused && !this.mRenderComplete) {
                    try {
                        this.mGLThreadManager.wait();
                    }
                    catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        public void onWindowResize(final int mWidth, final int mHeight) {
            synchronized (this.mGLThreadManager) {
                this.mWidth = mWidth;
                this.mHeight = mHeight;
                this.mSizeChanged = true;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                if (Thread.currentThread() != this) {
                    this.mGLThreadManager.notifyAll();
                    while (!this.mExited && !this.mPaused && !this.mRenderComplete && this.ableToDraw()) {
                        try {
                            this.mGLThreadManager.wait();
                        }
                        catch (final InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        
        public void queueEvent(final Runnable e) {
            Label_0030: {
                if (e == null) {
                    break Label_0030;
                }
                synchronized (this.mGLThreadManager) {
                    this.mEventQueue.add(e);
                    this.mGLThreadManager.notifyAll();
                    return;
                    throw new IllegalArgumentException("r must not be null");
                }
            }
        }
        
        public void requestExitAndWait() {
            synchronized (this.mGLThreadManager) {
                this.mShouldExit = true;
                this.mGLThreadManager.notifyAll();
                while (!this.mExited) {
                    try {
                        this.mGLThreadManager.wait();
                    }
                    catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        public void requestReleaseEglContextLocked() {
            this.mShouldReleaseEglContext = true;
            this.mGLThreadManager.notifyAll();
        }
        
        public void requestRender() {
            synchronized (this.mGLThreadManager) {
                this.mRequestRender = true;
                this.mGLThreadManager.notifyAll();
            }
        }
        
        public void requestRenderAndWait() {
            synchronized (this.mGLThreadManager) {
                if (Thread.currentThread() != this) {
                    this.mWantRenderNotification = true;
                    this.mRequestRender = true;
                    this.mRenderComplete = false;
                    this.mGLThreadManager.notifyAll();
                    while (!this.mExited && !this.mPaused && !this.mRenderComplete && this.ableToDraw()) {
                        try {
                            this.mGLThreadManager.wait();
                        }
                        catch (final InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        
        @Override
        public void run() {
            this.setName(new StringBuilder(29).append("GLThread ").append(this.getId()).toString());
            try {
                this.guardedRun();
            }
            catch (final InterruptedException ex) {}
            finally {
                this.mGLThreadManager.threadExiting(this);
            }
        }
        
        public void setRenderMode(final int mRenderMode) {
            if (mRenderMode < 0 || mRenderMode > 1) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (this.mGLThreadManager) {
                this.mRenderMode = mRenderMode;
                this.mGLThreadManager.notifyAll();
            }
        }
        
        public void setSwapMode(final int mRequestedSwapMode) {
            if (mRequestedSwapMode < 0 || mRequestedSwapMode > 2) {
                throw new IllegalArgumentException("swapMode");
            }
            synchronized (this.mGLThreadManager) {
                this.mRequestedSwapMode = mRequestedSwapMode;
                this.mGLThreadManager.notifyAll();
            }
        }
        
        public void surfaceCreated() {
            synchronized (this.mGLThreadManager) {
                this.mHasSurface = true;
                this.mFinishedCreatingEglSurface = false;
                this.mGLThreadManager.notifyAll();
                while (this.mWaitingForSurface && !this.mFinishedCreatingEglSurface && !this.mExited) {
                    try {
                        this.mGLThreadManager.wait();
                    }
                    catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        public void surfaceDestroyed() {
            synchronized (this.mGLThreadManager) {
                this.mHasSurface = false;
                this.mGLThreadManager.notifyAll();
                while (!this.mWaitingForSurface && !this.mExited) {
                    try {
                        this.mGLThreadManager.wait();
                    }
                    catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        private static class GLThreadManager
        {
            private static final String TAG = "GLThreadManager";
            
            public void releaseEglContextLocked(final GLThread glThread) {
                this.notifyAll();
            }
            
            public void threadExiting(final GLThread glThread) {
                synchronized (this) {
                    glThread.mExited = true;
                    this.notifyAll();
                }
            }
        }
    }
    
    public interface GLWrapper
    {
        GL wrap(final GL p0);
    }
    
    static class LogWriter extends Writer
    {
        private StringBuilder mBuilder;
        
        LogWriter() {
            this.mBuilder = new StringBuilder();
        }
        
        private void flushBuilder() {
            if (this.mBuilder.length() > 0) {
                Log.v("GvrSurfaceView", this.mBuilder.toString());
                this.mBuilder.delete(0, this.mBuilder.length());
            }
        }
        
        @Override
        public void close() {
            this.flushBuilder();
        }
        
        @Override
        public void flush() {
            this.flushBuilder();
        }
        
        @Override
        public void write(final char[] array, final int n, final int n2) {
            for (int i = 0; i < n2; ++i) {
                final char c = array[n + i];
                if (c != '\n') {
                    this.mBuilder.append(c);
                }
                else {
                    this.flushBuilder();
                }
            }
        }
    }
    
    private class SimpleEGLConfigChooser extends ComponentSizeChooser
    {
        public SimpleEGLConfigChooser(final GvrSurfaceView gvrSurfaceView, final boolean b) {
            int n;
            if (!b) {
                n = 0;
            }
            else {
                n = 16;
            }
            gvrSurfaceView.super(8, 8, 8, 0, n, 0);
        }
    }
}
