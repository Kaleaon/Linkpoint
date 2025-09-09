package com.google.vr.ndk.base;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.GLDebugHelper;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

public class GvrSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {
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
    /* access modifiers changed from: private */
    public int mDebugFlags;
    private boolean mDetached;
    /* access modifiers changed from: private */
    public GLSurfaceView.EGLConfigChooser mEGLConfigChooser;
    /* access modifiers changed from: private */
    public int mEGLContextClientVersion;
    /* access modifiers changed from: private */
    public GLSurfaceView.EGLContextFactory mEGLContextFactory;
    /* access modifiers changed from: private */
    public GLSurfaceView.EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLThread mGLThread;
    /* access modifiers changed from: private */
    public GLWrapper mGLWrapper;
    /* access modifiers changed from: private */
    public boolean mPreserveEGLContextOnPause;
    /* access modifiers changed from: private */
    public GLSurfaceView.Renderer mRenderer;
    private final WeakReference<GvrSurfaceView> mThisWeakRef = new WeakReference<>(this);

    private abstract class BaseConfigChooser implements GLSurfaceView.EGLConfigChooser {
        protected int[] mConfigSpec;

        public BaseConfigChooser(int[] iArr) {
            this.mConfigSpec = filterConfigSpec(iArr);
        }

        private int[] filterConfigSpec(int[] iArr) {
            if (GvrSurfaceView.this.mEGLContextClientVersion != 2 && GvrSurfaceView.this.mEGLContextClientVersion != 3) {
                return iArr;
            }
            int length = iArr.length;
            int[] iArr2 = new int[(length + 2)];
            System.arraycopy(iArr, 0, iArr2, 0, length - 1);
            iArr2[length - 1] = 12352;
            if (GvrSurfaceView.this.mEGLContextClientVersion != 2) {
                iArr2[length] = 64;
            } else {
                iArr2[length] = 4;
            }
            iArr2[length + 1] = 12344;
            return iArr2;
        }

        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay) {
            int[] iArr = new int[1];
            if (egl10.eglChooseConfig(eGLDisplay, this.mConfigSpec, (EGLConfig[]) null, 0, iArr)) {
                int i = iArr[0];
                if (i > 0) {
                    EGLConfig[] eGLConfigArr = new EGLConfig[i];
                    if (egl10.eglChooseConfig(eGLDisplay, this.mConfigSpec, eGLConfigArr, i, iArr)) {
                        EGLConfig chooseConfig = chooseConfig(egl10, eGLDisplay, eGLConfigArr);
                        if (chooseConfig != null) {
                            return chooseConfig;
                        }
                        throw new IllegalArgumentException("No config chosen");
                    }
                    throw new IllegalArgumentException("eglChooseConfig#2 failed");
                }
                throw new IllegalArgumentException("No configs match configSpec");
            }
            throw new IllegalArgumentException("eglChooseConfig failed");
        }

        /* access modifiers changed from: package-private */
        public abstract EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);
    }

    private class ComponentSizeChooser extends BaseConfigChooser {
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];

        public ComponentSizeChooser(GvrSurfaceView gvrSurfaceView, int i, int i2, int i3, int i4, int i5, int i6) {
            super(new int[]{12324, i, 12323, i2, 12322, i3, 12321, i4, 12325, i5, 12326, i6, 12344});
            this.mRedSize = i;
            this.mGreenSize = i2;
            this.mBlueSize = i3;
            this.mAlphaSize = i4;
            this.mDepthSize = i5;
            this.mStencilSize = i6;
        }

        private int findConfigAttrib(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2) {
            return !egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.mValue) ? i2 : this.mValue[0];
        }

        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
            for (EGLConfig eGLConfig : eGLConfigArr) {
                int findConfigAttrib = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12325, 0);
                int findConfigAttrib2 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12326, 0);
                if (findConfigAttrib >= this.mDepthSize && findConfigAttrib2 >= this.mStencilSize) {
                    int findConfigAttrib3 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12324, 0);
                    int findConfigAttrib4 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12323, 0);
                    int findConfigAttrib5 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12322, 0);
                    int findConfigAttrib6 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12321, 0);
                    if (findConfigAttrib3 == this.mRedSize && findConfigAttrib4 == this.mGreenSize && findConfigAttrib5 == this.mBlueSize && findConfigAttrib6 == this.mAlphaSize) {
                        return eGLConfig;
                    }
                }
            }
            return null;
        }
    }

    private class DefaultContextFactory implements GLSurfaceView.EGLContextFactory {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;

        private DefaultContextFactory() {
        }

        public EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
            int[] iArr = {EGL_CONTEXT_CLIENT_VERSION, GvrSurfaceView.this.mEGLContextClientVersion, 12344};
            EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
            if (GvrSurfaceView.this.mEGLContextClientVersion == 0) {
                iArr = null;
            }
            return egl10.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
        }

        public void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
            if (!egl10.eglDestroyContext(eGLDisplay, eGLContext)) {
                String valueOf = String.valueOf(eGLDisplay);
                String valueOf2 = String.valueOf(eGLContext);
                Log.e("DefaultContextFactory", new StringBuilder(String.valueOf(valueOf).length() + 18 + String.valueOf(valueOf2).length()).append("display:").append(valueOf).append(" context: ").append(valueOf2).toString());
                EglHelper.throwEglException("eglDestroyContex", egl10.eglGetError());
            }
        }
    }

    private static class DefaultWindowSurfaceFactory implements GLSurfaceView.EGLWindowSurfaceFactory {
        private DefaultWindowSurfaceFactory() {
        }

        public EGLSurface createWindowSurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj) {
            try {
                return egl10.eglCreateWindowSurface(eGLDisplay, eGLConfig, obj, (int[]) null);
            } catch (IllegalArgumentException e) {
                Log.e(GvrSurfaceView.TAG, "eglCreateWindowSurface", e);
                return null;
            }
        }

        public void destroySurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface) {
            egl10.eglDestroySurface(eGLDisplay, eGLSurface);
        }
    }

    private static class EglHelper {
        public static final int EGL_FRONT_BUFFER_AUTO_REFRESH = 12620;
        EGL10 mEgl;
        EGLConfig mEglConfig;
        EGLContext mEglContext;
        EGLDisplay mEglDisplay;
        EGLSurface mEglSurface;
        private WeakReference<GvrSurfaceView> mGvrSurfaceViewWeakRef;

        public EglHelper(WeakReference<GvrSurfaceView> weakReference) {
            this.mGvrSurfaceViewWeakRef = weakReference;
        }

        private void destroySurfaceImp() {
            if (this.mEglSurface != null && this.mEglSurface != EGL10.EGL_NO_SURFACE) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GvrSurfaceView gvrSurfaceView = (GvrSurfaceView) this.mGvrSurfaceViewWeakRef.get();
                if (gvrSurfaceView != null) {
                    gvrSurfaceView.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
                }
                this.mEglSurface = null;
            }
        }

        public static String formatEglError(String str, int i) {
            String valueOf = String.valueOf(getErrorString(i));
            return new StringBuilder(String.valueOf(str).length() + 9 + String.valueOf(valueOf).length()).append(str).append(" failed: ").append(valueOf).toString();
        }

        private static String getErrorString(int i) {
            switch (i) {
                case 12288:
                    return "EGL_SUCCESS";
                case 12289:
                    return "EGL_NOT_INITIALIZED";
                case 12290:
                    return "EGL_BAD_ACCESS";
                case 12291:
                    return "EGL_BAD_ALLOC";
                case 12292:
                    return "EGL_BAD_ATTRIBUTE";
                case 12293:
                    return "EGL_BAD_CONFIG";
                case 12294:
                    return "EGL_BAD_CONTEXT";
                case 12295:
                    return "EGL_BAD_CURRENT_SURFACE";
                case 12296:
                    return "EGL_BAD_DISPLAY";
                case 12297:
                    return "EGL_BAD_MATCH";
                case 12298:
                    return "EGL_BAD_NATIVE_PIXMAP";
                case 12299:
                    return "EGL_BAD_NATIVE_WINDOW";
                case 12300:
                    return "EGL_BAD_PARAMETER";
                case 12301:
                    return "EGL_BAD_SURFACE";
                case 12302:
                    return "EGL_CONTEXT_LOST";
                default:
                    return getHex(i);
            }
        }

        private static String getHex(int i) {
            String valueOf = String.valueOf(Integer.toHexString(i));
            return valueOf.length() == 0 ? new String("0x") : "0x".concat(valueOf);
        }

        public static void logEglErrorAsWarning(String str, String str2, int i) {
            Log.w(str, formatEglError(str2, i));
        }

        private void throwEglException(String str) {
            throwEglException(str, this.mEgl.eglGetError());
        }

        public static void throwEglException(String str, int i) {
            throw new RuntimeException(formatEglError(str, i));
        }

        /* access modifiers changed from: package-private */
        public GL createGL() {
            int i = 0;
            GL gl = this.mEglContext.getGL();
            GvrSurfaceView gvrSurfaceView = (GvrSurfaceView) this.mGvrSurfaceViewWeakRef.get();
            if (gvrSurfaceView == null) {
                return gl;
            }
            if (gvrSurfaceView.mGLWrapper != null) {
                gl = gvrSurfaceView.mGLWrapper.wrap(gl);
            }
            if ((gvrSurfaceView.mDebugFlags & 3) == 0) {
                return gl;
            }
            if ((gvrSurfaceView.mDebugFlags & 1) != 0) {
                i = 1;
            }
            return GLDebugHelper.wrap(gl, i, (gvrSurfaceView.mDebugFlags & 2) == 0 ? null : new LogWriter());
        }

        public boolean createSurface() {
            if (this.mEgl == null) {
                throw new RuntimeException("egl not initialized");
            } else if (this.mEglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            } else if (this.mEglConfig != null) {
                destroySurfaceImp();
                GvrSurfaceView gvrSurfaceView = (GvrSurfaceView) this.mGvrSurfaceViewWeakRef.get();
                if (gvrSurfaceView == null) {
                    this.mEglSurface = null;
                } else {
                    this.mEglSurface = gvrSurfaceView.mEGLWindowSurfaceFactory.createWindowSurface(this.mEgl, this.mEglDisplay, this.mEglConfig, gvrSurfaceView.getHolder());
                }
                if (this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE) {
                    if (this.mEgl.eglGetError() == 12299) {
                        Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                    }
                    return false;
                } else if (this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
                    return true;
                } else {
                    logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", this.mEgl.eglGetError());
                    return false;
                }
            } else {
                throw new RuntimeException("mEglConfig not initialized");
            }
        }

        public void destroySurface() {
            destroySurfaceImp();
        }

        public void finish() {
            if (this.mEglContext != null) {
                GvrSurfaceView gvrSurfaceView = (GvrSurfaceView) this.mGvrSurfaceViewWeakRef.get();
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

        public void setEglSurfaceAttrib(int i, int i2) {
            if (Build.VERSION.SDK_INT < 17) {
                Log.e("EglHelper", "Cannot call eglSurfaceAttrib. API version is too low.");
            } else if (!EGL14.eglSurfaceAttrib(EGL14.eglGetCurrentDisplay(), EGL14.eglGetCurrentSurface(12377), i, i2)) {
                Log.e("EglHelper", new StringBuilder(66).append("eglSurfaceAttrib() failed. attribute=").append(i).append(" value=").append(i2).toString());
            }
        }

        public void start() {
            this.mEgl = (EGL10) EGLContext.getEGL();
            this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.mEglDisplay != EGL10.EGL_NO_DISPLAY) {
                if (this.mEgl.eglInitialize(this.mEglDisplay, new int[2])) {
                    GvrSurfaceView gvrSurfaceView = (GvrSurfaceView) this.mGvrSurfaceViewWeakRef.get();
                    if (gvrSurfaceView != null) {
                        this.mEglConfig = gvrSurfaceView.mEGLConfigChooser.chooseConfig(this.mEgl, this.mEglDisplay);
                        this.mEglContext = gvrSurfaceView.mEGLContextFactory.createContext(this.mEgl, this.mEglDisplay, this.mEglConfig);
                    } else {
                        this.mEglConfig = null;
                        this.mEglContext = null;
                    }
                    if (this.mEglContext == null || this.mEglContext == EGL10.EGL_NO_CONTEXT) {
                        this.mEglContext = null;
                        throwEglException("createContext");
                    }
                    this.mEglSurface = null;
                    return;
                }
                throw new RuntimeException("eglInitialize failed");
            }
            throw new RuntimeException("eglGetDisplay failed");
        }

        public int swap() {
            if (this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface)) {
                return 12288;
            }
            return this.mEgl.eglGetError();
        }
    }

    static class GLThread extends Thread {
        private EglHelper mEglHelper;
        private ArrayList<Runnable> mEventQueue = new ArrayList<>();
        /* access modifiers changed from: private */
        public boolean mExited;
        private boolean mFinishedCreatingEglSurface;
        private final GLThreadManager mGLThreadManager = new GLThreadManager();
        private WeakReference<GvrSurfaceView> mGvrSurfaceViewWeakRef;
        private boolean mHasSurface;
        private boolean mHaveEglContext;
        private boolean mHaveEglSurface;
        private int mHeight = 0;
        private boolean mPaused;
        private boolean mRenderComplete;
        private int mRenderMode = 1;
        private boolean mRequestPaused;
        private boolean mRequestRender = true;
        private int mRequestedSwapMode = 0;
        private boolean mShouldExit;
        private boolean mShouldReleaseEglContext;
        private boolean mSizeChanged = true;
        private boolean mSurfaceIsBad;
        private boolean mWaitingForSurface;
        private boolean mWantRenderNotification = false;
        private int mWidth = 0;

        private static class GLThreadManager {
            private static final String TAG = "GLThreadManager";

            private GLThreadManager() {
            }

            public void releaseEglContextLocked(GLThread gLThread) {
                notifyAll();
            }

            public synchronized void threadExiting(GLThread gLThread) {
                boolean unused = gLThread.mExited = true;
                notifyAll();
            }
        }

        GLThread(WeakReference<GvrSurfaceView> weakReference) {
            this.mGvrSurfaceViewWeakRef = weakReference;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:132:?, code lost:
            r10.run();
            r9 = r14;
            r14 = r2;
            r20 = r12;
            r12 = r4;
            r4 = r11;
            r11 = r16;
            r16 = r5;
            r5 = r20;
            r10 = r15;
            r15 = r8;
            r8 = r13;
            r13 = r3;
            r3 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:134:0x0280, code lost:
            if (r22.mEglHelper.createSurface() != false) goto L_0x02b7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:135:0x0282, code lost:
            r9 = r22.mGLThreadManager;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:136:0x0286, code lost:
            monitor-enter(r9);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:139:?, code lost:
            r22.mFinishedCreatingEglSurface = true;
            r22.mSurfaceIsBad = true;
            r22.mGLThreadManager.notifyAll();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:140:0x02a0, code lost:
            monitor-exit(r9);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:141:0x02a1, code lost:
            r9 = r14;
            r14 = r2;
            r20 = r12;
            r12 = r4;
            r4 = r11;
            r11 = r16;
            r16 = r5;
            r5 = r20;
            r21 = r10;
            r10 = r15;
            r15 = r8;
            r8 = r13;
            r13 = r3;
            r3 = r21;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:144:?, code lost:
            r7 = r22.mGLThreadManager;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:145:0x02bb, code lost:
            monitor-enter(r7);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:148:?, code lost:
            r22.mFinishedCreatingEglSurface = true;
            r22.mGLThreadManager.notifyAll();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:149:0x02c8, code lost:
            monitor-exit(r7);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:150:0x02c9, code lost:
            r9 = false;
            r8 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:160:0x02d5, code lost:
            r6 = false;
            r7 = (javax.microedition.khronos.opengles.GL10) r22.mEglHelper.createGL();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:161:0x02e3, code lost:
            r2 = (com.google.vr.ndk.base.GvrSurfaceView) r22.mGvrSurfaceViewWeakRef.get();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:162:0x02ed, code lost:
            if (r2 != null) goto L_0x02f3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:163:0x02ef, code lost:
            r5 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:166:?, code lost:
            com.google.vr.ndk.base.TraceCompat.beginSection("onSurfaceCreated");
            com.google.vr.ndk.base.GvrSurfaceView.access$900(r2).onSurfaceCreated(r7, r22.mEglHelper.mEglConfig);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:168:?, code lost:
            com.google.vr.ndk.base.TraceCompat.endSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:172:0x030f, code lost:
            r2 = (com.google.vr.ndk.base.GvrSurfaceView) r22.mGvrSurfaceViewWeakRef.get();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:173:0x0319, code lost:
            if (r2 != null) goto L_0x031f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:174:0x031b, code lost:
            r4 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:177:?, code lost:
            com.google.vr.ndk.base.TraceCompat.beginSection("onSurfaceChanged");
            com.google.vr.ndk.base.GvrSurfaceView.access$900(r2).onSurfaceChanged(r7, r12, r11);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:179:?, code lost:
            com.google.vr.ndk.base.TraceCompat.endSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:183:0x0335, code lost:
            r17 = r22.mEglHelper;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:184:0x033e, code lost:
            if (r8 == 1) goto L_0x035e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:185:0x0340, code lost:
            r2 = 12420;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:186:0x0342, code lost:
            r17.setEglSurfaceAttrib(12422, r2);
            r17 = r22.mEglHelper;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:187:0x0352, code lost:
            if (r8 == 1) goto L_0x0361;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:188:0x0354, code lost:
            r2 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:189:0x0355, code lost:
            r17.setEglSurfaceAttrib(com.google.vr.ndk.base.GvrSurfaceView.EglHelper.EGL_FRONT_BUFFER_AUTO_REFRESH, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:190:0x035e, code lost:
            r2 = 12421;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:191:0x0361, code lost:
            r2 = 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:194:?, code lost:
            com.google.vr.ndk.base.TraceCompat.beginSection("onDrawFrame");
            com.google.vr.ndk.base.GvrSurfaceView.access$900(r2).onDrawFrame(r7);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:196:?, code lost:
            com.google.vr.ndk.base.TraceCompat.endSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:200:0x037a, code lost:
            if (r8 == 0) goto L_0x00f0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:201:0x037c, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:202:0x037f, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:203:0x0382, code lost:
            r2 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:204:0x0385, code lost:
            r17 = r22.mGLThreadManager;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:205:0x038b, code lost:
            monitor-enter(r17);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:208:?, code lost:
            r22.mSurfaceIsBad = true;
            r22.mGLThreadManager.notifyAll();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:209:0x0398, code lost:
            monitor-exit(r17);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:210:0x0399, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:215:0x039f, code lost:
            r3 = r10;
            r10 = true;
            r15 = r9;
            r9 = r14;
            r14 = r6;
            r6 = r7;
            r7 = r8;
            r8 = r13;
            r13 = r2;
            r20 = r4;
            r4 = r11;
            r11 = false;
            r16 = r5;
            r5 = r12;
            r12 = r20;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x00d2, code lost:
            if (r10 != null) goto L_0x025e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x00d4, code lost:
            if (r8 != false) goto L_0x0278;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x00d6, code lost:
            r9 = r8;
            r8 = r7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x00d8, code lost:
            if (r2 != false) goto L_0x02d5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x00da, code lost:
            r7 = r6;
            r6 = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x00dc, code lost:
            if (r5 != false) goto L_0x02e3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x00de, code lost:
            if (r4 != false) goto L_0x030f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x00e0, code lost:
            if (r13 != false) goto L_0x0335;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
            r2 = (com.google.vr.ndk.base.GvrSurfaceView) r22.mGvrSurfaceViewWeakRef.get();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x00ec, code lost:
            if (r2 != null) goto L_0x0363;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x00ee, code lost:
            if (r13 == false) goto L_0x037a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:65:0x00f0, code lost:
            r2 = r22.mEglHelper.swap();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:66:0x00f8, code lost:
            switch(r2) {
                case 12288: goto L_0x037f;
                case 12302: goto L_0x0382;
                default: goto L_0x00fb;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:0x00fb, code lost:
            com.google.vr.ndk.base.GvrSurfaceView.EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x0108, code lost:
            if (r8 == 0) goto L_0x0385;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:69:0x010a, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:70:0x010b, code lost:
            if (r16 != false) goto L_0x039f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:71:0x010d, code lost:
            r3 = r10;
            r10 = r15;
            r15 = r9;
            r9 = r14;
            r14 = r6;
            r6 = r7;
            r7 = r8;
            r8 = r13;
            r13 = r2;
            r20 = r4;
            r4 = r11;
            r11 = r16;
            r16 = r5;
            r5 = r12;
            r12 = r20;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void guardedRun() throws java.lang.InterruptedException {
            /*
                r22 = this;
                com.google.vr.ndk.base.GvrSurfaceView$EglHelper r2 = new com.google.vr.ndk.base.GvrSurfaceView$EglHelper
                r0 = r22
                java.lang.ref.WeakReference<com.google.vr.ndk.base.GvrSurfaceView> r3 = r0.mGvrSurfaceViewWeakRef
                r2.<init>(r3)
                r0 = r22
                r0.mEglHelper = r2
                r2 = 0
                r0 = r22
                r0.mHaveEglContext = r2
                r2 = 0
                r0 = r22
                r0.mHaveEglSurface = r2
                r2 = 0
                r0 = r22
                r0.mWantRenderNotification = r2
                r6 = 0
                r5 = 0
                r8 = 0
                r2 = 0
                r3 = 0
                r4 = 0
                r16 = 0
                r15 = 0
                r14 = 0
                r13 = 0
                r7 = 0
                r12 = 0
                r11 = 0
                r10 = 0
                r9 = r14
                r14 = r2
                r20 = r12
                r12 = r4
                r4 = r11
                r11 = r16
                r16 = r5
                r5 = r20
                r21 = r10
                r10 = r15
                r15 = r8
                r8 = r13
                r13 = r3
                r3 = r21
            L_0x003f:
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r0 = r0.mGLThreadManager     // Catch:{ all -> 0x0096 }
                r17 = r0
                monitor-enter(r17)     // Catch:{ all -> 0x0096 }
            L_0x0046:
                r0 = r22
                boolean r2 = r0.mShouldExit     // Catch:{ all -> 0x0093 }
                if (r2 != 0) goto L_0x00a4
                r0 = r22
                java.util.ArrayList<java.lang.Runnable> r2 = r0.mEventQueue     // Catch:{ all -> 0x0093 }
                boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x0093 }
                if (r2 == 0) goto L_0x00b5
                r2 = 0
                r0 = r22
                boolean r0 = r0.mPaused     // Catch:{ all -> 0x0093 }
                r18 = r0
                r0 = r22
                boolean r0 = r0.mRequestPaused     // Catch:{ all -> 0x0093 }
                r19 = r0
                r0 = r18
                r1 = r19
                if (r0 != r1) goto L_0x0122
            L_0x0069:
                r0 = r22
                boolean r0 = r0.mShouldReleaseEglContext     // Catch:{ all -> 0x0093 }
                r18 = r0
                if (r18 != 0) goto L_0x013d
            L_0x0071:
                if (r13 != 0) goto L_0x014b
            L_0x0073:
                if (r2 != 0) goto L_0x0154
            L_0x0075:
                if (r2 != 0) goto L_0x0161
            L_0x0077:
                r0 = r22
                boolean r2 = r0.mHasSurface     // Catch:{ all -> 0x0093 }
                if (r2 == 0) goto L_0x0180
            L_0x007d:
                r0 = r22
                boolean r2 = r0.mHasSurface     // Catch:{ all -> 0x0093 }
                if (r2 != 0) goto L_0x01a3
            L_0x0083:
                if (r10 != 0) goto L_0x01b7
            L_0x0085:
                boolean r2 = r22.readyToDraw()     // Catch:{ all -> 0x0093 }
                if (r2 != 0) goto L_0x01cb
            L_0x008b:
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r2 = r0.mGLThreadManager     // Catch:{ all -> 0x0093 }
                r2.wait()     // Catch:{ all -> 0x0093 }
                goto L_0x0046
            L_0x0093:
                r2 = move-exception
                monitor-exit(r17)     // Catch:{ all -> 0x0093 }
                throw r2     // Catch:{ all -> 0x0096 }
            L_0x0096:
                r2 = move-exception
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r3 = r0.mGLThreadManager
                monitor-enter(r3)
                r22.stopEglSurfaceLocked()     // Catch:{ all -> 0x03b7 }
                r22.stopEglContextLocked()     // Catch:{ all -> 0x03b7 }
                monitor-exit(r3)     // Catch:{ all -> 0x03b7 }
                throw r2
            L_0x00a4:
                monitor-exit(r17)     // Catch:{ all -> 0x0093 }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r3 = r0.mGLThreadManager
                monitor-enter(r3)
                r22.stopEglSurfaceLocked()     // Catch:{ all -> 0x00b2 }
                r22.stopEglContextLocked()     // Catch:{ all -> 0x00b2 }
                monitor-exit(r3)     // Catch:{ all -> 0x00b2 }
                return
            L_0x00b2:
                r2 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x00b2 }
                throw r2
            L_0x00b5:
                r0 = r22
                java.util.ArrayList<java.lang.Runnable> r2 = r0.mEventQueue     // Catch:{ all -> 0x0093 }
                r3 = 0
                java.lang.Object r2 = r2.remove(r3)     // Catch:{ all -> 0x0093 }
                java.lang.Runnable r2 = (java.lang.Runnable) r2     // Catch:{ all -> 0x0093 }
                r3 = r13
                r13 = r8
                r8 = r15
                r15 = r10
                r10 = r2
                r2 = r14
                r14 = r9
                r20 = r12
                r12 = r5
                r5 = r16
                r16 = r11
                r11 = r4
                r4 = r20
            L_0x00d1:
                monitor-exit(r17)     // Catch:{ all -> 0x0093 }
                if (r10 != 0) goto L_0x025e
                if (r8 != 0) goto L_0x0278
                r9 = r8
                r8 = r7
            L_0x00d8:
                if (r2 != 0) goto L_0x02d5
                r7 = r6
                r6 = r2
            L_0x00dc:
                if (r5 != 0) goto L_0x02e3
            L_0x00de:
                if (r4 != 0) goto L_0x030f
            L_0x00e0:
                if (r13 != 0) goto L_0x0335
            L_0x00e2:
                r0 = r22
                java.lang.ref.WeakReference<com.google.vr.ndk.base.GvrSurfaceView> r2 = r0.mGvrSurfaceViewWeakRef     // Catch:{ all -> 0x0096 }
                java.lang.Object r2 = r2.get()     // Catch:{ all -> 0x0096 }
                com.google.vr.ndk.base.GvrSurfaceView r2 = (com.google.vr.ndk.base.GvrSurfaceView) r2     // Catch:{ all -> 0x0096 }
                if (r2 != 0) goto L_0x0363
            L_0x00ee:
                if (r13 == 0) goto L_0x037a
            L_0x00f0:
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$EglHelper r2 = r0.mEglHelper     // Catch:{ all -> 0x0096 }
                int r2 = r2.swap()     // Catch:{ all -> 0x0096 }
                switch(r2) {
                    case 12288: goto L_0x037f;
                    case 12302: goto L_0x0382;
                    default: goto L_0x00fb;
                }     // Catch:{ all -> 0x0096 }
            L_0x00fb:
                java.lang.String r17 = "GLThread"
                java.lang.String r18 = "eglSwapBuffers"
                r0 = r17
                r1 = r18
                com.google.vr.ndk.base.GvrSurfaceView.EglHelper.logEglErrorAsWarning(r0, r1, r2)     // Catch:{ all -> 0x0096 }
                if (r8 == 0) goto L_0x0385
                r2 = r3
            L_0x010b:
                if (r16 != 0) goto L_0x039f
                r3 = r10
                r10 = r15
                r15 = r9
                r9 = r14
                r14 = r6
                r6 = r7
                r7 = r8
                r8 = r13
                r13 = r2
                r20 = r4
                r4 = r11
                r11 = r16
                r16 = r5
                r5 = r12
                r12 = r20
                goto L_0x003f
            L_0x0122:
                r0 = r22
                boolean r2 = r0.mRequestPaused     // Catch:{ all -> 0x0093 }
                r0 = r22
                boolean r0 = r0.mRequestPaused     // Catch:{ all -> 0x0093 }
                r18 = r0
                r0 = r18
                r1 = r22
                r1.mPaused = r0     // Catch:{ all -> 0x0093 }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r0 = r0.mGLThreadManager     // Catch:{ all -> 0x0093 }
                r18 = r0
                r18.notifyAll()     // Catch:{ all -> 0x0093 }
                goto L_0x0069
            L_0x013d:
                r22.stopEglSurfaceLocked()     // Catch:{ all -> 0x0093 }
                r22.stopEglContextLocked()     // Catch:{ all -> 0x0093 }
                r9 = 0
                r0 = r22
                r0.mShouldReleaseEglContext = r9     // Catch:{ all -> 0x0093 }
                r9 = 1
                goto L_0x0071
            L_0x014b:
                r22.stopEglSurfaceLocked()     // Catch:{ all -> 0x0093 }
                r22.stopEglContextLocked()     // Catch:{ all -> 0x0093 }
                r13 = 0
                goto L_0x0073
            L_0x0154:
                r0 = r22
                boolean r0 = r0.mHaveEglSurface     // Catch:{ all -> 0x0093 }
                r18 = r0
                if (r18 == 0) goto L_0x0075
                r22.stopEglSurfaceLocked()     // Catch:{ all -> 0x0093 }
                goto L_0x0075
            L_0x0161:
                r0 = r22
                boolean r2 = r0.mHaveEglContext     // Catch:{ all -> 0x0093 }
                if (r2 == 0) goto L_0x0077
                r0 = r22
                java.lang.ref.WeakReference<com.google.vr.ndk.base.GvrSurfaceView> r2 = r0.mGvrSurfaceViewWeakRef     // Catch:{ all -> 0x0093 }
                java.lang.Object r2 = r2.get()     // Catch:{ all -> 0x0093 }
                com.google.vr.ndk.base.GvrSurfaceView r2 = (com.google.vr.ndk.base.GvrSurfaceView) r2     // Catch:{ all -> 0x0093 }
                if (r2 == 0) goto L_0x017e
                boolean r2 = r2.mPreserveEGLContextOnPause     // Catch:{ all -> 0x0093 }
            L_0x0177:
                if (r2 != 0) goto L_0x0077
                r22.stopEglContextLocked()     // Catch:{ all -> 0x0093 }
                goto L_0x0077
            L_0x017e:
                r2 = 0
                goto L_0x0177
            L_0x0180:
                r0 = r22
                boolean r2 = r0.mWaitingForSurface     // Catch:{ all -> 0x0093 }
                if (r2 != 0) goto L_0x007d
                r0 = r22
                boolean r2 = r0.mHaveEglSurface     // Catch:{ all -> 0x0093 }
                if (r2 != 0) goto L_0x019f
            L_0x018c:
                r2 = 1
                r0 = r22
                r0.mWaitingForSurface = r2     // Catch:{ all -> 0x0093 }
                r2 = 0
                r0 = r22
                r0.mSurfaceIsBad = r2     // Catch:{ all -> 0x0093 }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r2 = r0.mGLThreadManager     // Catch:{ all -> 0x0093 }
                r2.notifyAll()     // Catch:{ all -> 0x0093 }
                goto L_0x007d
            L_0x019f:
                r22.stopEglSurfaceLocked()     // Catch:{ all -> 0x0093 }
                goto L_0x018c
            L_0x01a3:
                r0 = r22
                boolean r2 = r0.mWaitingForSurface     // Catch:{ all -> 0x0093 }
                if (r2 == 0) goto L_0x0083
                r2 = 0
                r0 = r22
                r0.mWaitingForSurface = r2     // Catch:{ all -> 0x0093 }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r2 = r0.mGLThreadManager     // Catch:{ all -> 0x0093 }
                r2.notifyAll()     // Catch:{ all -> 0x0093 }
                goto L_0x0083
            L_0x01b7:
                r2 = 0
                r0 = r22
                r0.mWantRenderNotification = r2     // Catch:{ all -> 0x0093 }
                r10 = 0
                r2 = 1
                r0 = r22
                r0.mRenderComplete = r2     // Catch:{ all -> 0x0093 }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r2 = r0.mGLThreadManager     // Catch:{ all -> 0x0093 }
                r2.notifyAll()     // Catch:{ all -> 0x0093 }
                goto L_0x0085
            L_0x01cb:
                r0 = r22
                boolean r2 = r0.mHaveEglContext     // Catch:{ all -> 0x0093 }
                if (r2 == 0) goto L_0x0211
            L_0x01d1:
                r0 = r22
                boolean r2 = r0.mHaveEglContext     // Catch:{ all -> 0x0093 }
                if (r2 != 0) goto L_0x0236
            L_0x01d7:
                r0 = r22
                boolean r2 = r0.mHaveEglSurface     // Catch:{ all -> 0x0093 }
                if (r2 == 0) goto L_0x008b
                r0 = r22
                boolean r2 = r0.mSizeChanged     // Catch:{ all -> 0x0093 }
                if (r2 != 0) goto L_0x0245
            L_0x01e3:
                r2 = 0
                r0 = r22
                r0.mRequestRender = r2     // Catch:{ all -> 0x0093 }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r2 = r0.mGLThreadManager     // Catch:{ all -> 0x0093 }
                r2.notifyAll()     // Catch:{ all -> 0x0093 }
                r0 = r22
                boolean r2 = r0.mWantRenderNotification     // Catch:{ all -> 0x0093 }
                if (r2 != 0) goto L_0x025a
                r8 = r11
            L_0x01f6:
                r0 = r22
                int r2 = r0.mRequestedSwapMode     // Catch:{ all -> 0x0093 }
                if (r2 != r7) goto L_0x025c
                r2 = 0
            L_0x01fd:
                r0 = r22
                int r7 = r0.mRequestedSwapMode     // Catch:{ all -> 0x0093 }
                r11 = r4
                r4 = r12
                r12 = r5
                r5 = r16
                r16 = r8
                r8 = r15
                r15 = r10
                r10 = r3
                r3 = r13
                r13 = r2
                r2 = r14
                r14 = r9
                goto L_0x00d1
            L_0x0211:
                if (r9 != 0) goto L_0x0229
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$EglHelper r2 = r0.mEglHelper     // Catch:{ RuntimeException -> 0x022b }
                r2.start()     // Catch:{ RuntimeException -> 0x022b }
                r2 = 1
                r0 = r22
                r0.mHaveEglContext = r2     // Catch:{ all -> 0x0093 }
                r16 = 1
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r2 = r0.mGLThreadManager     // Catch:{ all -> 0x0093 }
                r2.notifyAll()     // Catch:{ all -> 0x0093 }
                goto L_0x01d1
            L_0x0229:
                r9 = 0
                goto L_0x01d1
            L_0x022b:
                r2 = move-exception
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r3 = r0.mGLThreadManager     // Catch:{ all -> 0x0093 }
                r0 = r22
                r3.releaseEglContextLocked(r0)     // Catch:{ all -> 0x0093 }
                throw r2     // Catch:{ all -> 0x0093 }
            L_0x0236:
                r0 = r22
                boolean r2 = r0.mHaveEglSurface     // Catch:{ all -> 0x0093 }
                if (r2 != 0) goto L_0x01d7
                r2 = 1
                r0 = r22
                r0.mHaveEglSurface = r2     // Catch:{ all -> 0x0093 }
                r15 = 1
                r14 = 1
                r12 = 1
                goto L_0x01d7
            L_0x0245:
                r12 = 1
                r0 = r22
                int r5 = r0.mWidth     // Catch:{ all -> 0x0093 }
                r0 = r22
                int r4 = r0.mHeight     // Catch:{ all -> 0x0093 }
                r2 = 1
                r0 = r22
                r0.mWantRenderNotification = r2     // Catch:{ all -> 0x0093 }
                r15 = 1
                r2 = 0
                r0 = r22
                r0.mSizeChanged = r2     // Catch:{ all -> 0x0093 }
                goto L_0x01e3
            L_0x025a:
                r8 = 1
                goto L_0x01f6
            L_0x025c:
                r2 = 1
                goto L_0x01fd
            L_0x025e:
                r10.run()     // Catch:{ all -> 0x0096 }
                r10 = 0
                r9 = r14
                r14 = r2
                r20 = r12
                r12 = r4
                r4 = r11
                r11 = r16
                r16 = r5
                r5 = r20
                r21 = r10
                r10 = r15
                r15 = r8
                r8 = r13
                r13 = r3
                r3 = r21
                goto L_0x003f
            L_0x0278:
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$EglHelper r9 = r0.mEglHelper     // Catch:{ all -> 0x0096 }
                boolean r9 = r9.createSurface()     // Catch:{ all -> 0x0096 }
                if (r9 != 0) goto L_0x02b7
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r9 = r0.mGLThreadManager     // Catch:{ all -> 0x0096 }
                monitor-enter(r9)     // Catch:{ all -> 0x0096 }
                r17 = 1
                r0 = r17
                r1 = r22
                r1.mFinishedCreatingEglSurface = r0     // Catch:{ all -> 0x02d2 }
                r17 = 1
                r0 = r17
                r1 = r22
                r1.mSurfaceIsBad = r0     // Catch:{ all -> 0x02d2 }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r0 = r0.mGLThreadManager     // Catch:{ all -> 0x02d2 }
                r17 = r0
                r17.notifyAll()     // Catch:{ all -> 0x02d2 }
                monitor-exit(r9)     // Catch:{ all -> 0x02d2 }
                r9 = r14
                r14 = r2
                r20 = r12
                r12 = r4
                r4 = r11
                r11 = r16
                r16 = r5
                r5 = r20
                r21 = r10
                r10 = r15
                r15 = r8
                r8 = r13
                r13 = r3
                r3 = r21
                goto L_0x003f
            L_0x02b7:
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r7 = r0.mGLThreadManager     // Catch:{ all -> 0x0096 }
                monitor-enter(r7)     // Catch:{ all -> 0x0096 }
                r8 = 1
                r0 = r22
                r0.mFinishedCreatingEglSurface = r8     // Catch:{ all -> 0x02cf }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r8 = r0.mGLThreadManager     // Catch:{ all -> 0x02cf }
                r8.notifyAll()     // Catch:{ all -> 0x02cf }
                monitor-exit(r7)     // Catch:{ all -> 0x02cf }
                r8 = 0
                r7 = 0
                r9 = r8
                r8 = r7
                goto L_0x00d8
            L_0x02cf:
                r2 = move-exception
                monitor-exit(r7)     // Catch:{ all -> 0x02cf }
                throw r2     // Catch:{ all -> 0x0096 }
            L_0x02d2:
                r2 = move-exception
                monitor-exit(r9)     // Catch:{ all -> 0x02d2 }
                throw r2     // Catch:{ all -> 0x0096 }
            L_0x02d5:
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$EglHelper r2 = r0.mEglHelper     // Catch:{ all -> 0x0096 }
                javax.microedition.khronos.opengles.GL r2 = r2.createGL()     // Catch:{ all -> 0x0096 }
                javax.microedition.khronos.opengles.GL10 r2 = (javax.microedition.khronos.opengles.GL10) r2     // Catch:{ all -> 0x0096 }
                r6 = 0
                r7 = r2
                goto L_0x00dc
            L_0x02e3:
                r0 = r22
                java.lang.ref.WeakReference<com.google.vr.ndk.base.GvrSurfaceView> r2 = r0.mGvrSurfaceViewWeakRef     // Catch:{ all -> 0x0096 }
                java.lang.Object r2 = r2.get()     // Catch:{ all -> 0x0096 }
                com.google.vr.ndk.base.GvrSurfaceView r2 = (com.google.vr.ndk.base.GvrSurfaceView) r2     // Catch:{ all -> 0x0096 }
                if (r2 != 0) goto L_0x02f3
            L_0x02ef:
                r2 = 0
                r5 = r2
                goto L_0x00de
            L_0x02f3:
                java.lang.String r5 = "onSurfaceCreated"
                com.google.vr.ndk.base.TraceCompat.beginSection(r5)     // Catch:{ all -> 0x030a }
                android.opengl.GLSurfaceView$Renderer r2 = r2.mRenderer     // Catch:{ all -> 0x030a }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$EglHelper r5 = r0.mEglHelper     // Catch:{ all -> 0x030a }
                javax.microedition.khronos.egl.EGLConfig r5 = r5.mEglConfig     // Catch:{ all -> 0x030a }
                r2.onSurfaceCreated(r7, r5)     // Catch:{ all -> 0x030a }
                com.google.vr.ndk.base.TraceCompat.endSection()     // Catch:{ all -> 0x0096 }
                goto L_0x02ef
            L_0x030a:
                r2 = move-exception
                com.google.vr.ndk.base.TraceCompat.endSection()     // Catch:{ all -> 0x0096 }
                throw r2     // Catch:{ all -> 0x0096 }
            L_0x030f:
                r0 = r22
                java.lang.ref.WeakReference<com.google.vr.ndk.base.GvrSurfaceView> r2 = r0.mGvrSurfaceViewWeakRef     // Catch:{ all -> 0x0096 }
                java.lang.Object r2 = r2.get()     // Catch:{ all -> 0x0096 }
                com.google.vr.ndk.base.GvrSurfaceView r2 = (com.google.vr.ndk.base.GvrSurfaceView) r2     // Catch:{ all -> 0x0096 }
                if (r2 != 0) goto L_0x031f
            L_0x031b:
                r2 = 0
                r4 = r2
                goto L_0x00e0
            L_0x031f:
                java.lang.String r4 = "onSurfaceChanged"
                com.google.vr.ndk.base.TraceCompat.beginSection(r4)     // Catch:{ all -> 0x0330 }
                android.opengl.GLSurfaceView$Renderer r2 = r2.mRenderer     // Catch:{ all -> 0x0330 }
                r2.onSurfaceChanged(r7, r12, r11)     // Catch:{ all -> 0x0330 }
                com.google.vr.ndk.base.TraceCompat.endSection()     // Catch:{ all -> 0x0096 }
                goto L_0x031b
            L_0x0330:
                r2 = move-exception
                com.google.vr.ndk.base.TraceCompat.endSection()     // Catch:{ all -> 0x0096 }
                throw r2     // Catch:{ all -> 0x0096 }
            L_0x0335:
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$EglHelper r0 = r0.mEglHelper     // Catch:{ all -> 0x0096 }
                r17 = r0
                r18 = 12422(0x3086, float:1.7407E-41)
                r2 = 1
                if (r8 == r2) goto L_0x035e
                r2 = 12420(0x3084, float:1.7404E-41)
            L_0x0342:
                r0 = r17
                r1 = r18
                r0.setEglSurfaceAttrib(r1, r2)     // Catch:{ all -> 0x0096 }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$EglHelper r0 = r0.mEglHelper     // Catch:{ all -> 0x0096 }
                r17 = r0
                r18 = 12620(0x314c, float:1.7684E-41)
                r2 = 1
                if (r8 == r2) goto L_0x0361
                r2 = 0
            L_0x0355:
                r0 = r17
                r1 = r18
                r0.setEglSurfaceAttrib(r1, r2)     // Catch:{ all -> 0x0096 }
                goto L_0x00e2
            L_0x035e:
                r2 = 12421(0x3085, float:1.7406E-41)
                goto L_0x0342
            L_0x0361:
                r2 = 1
                goto L_0x0355
            L_0x0363:
                java.lang.String r17 = "onDrawFrame"
                com.google.vr.ndk.base.TraceCompat.beginSection(r17)     // Catch:{ all -> 0x0375 }
                android.opengl.GLSurfaceView$Renderer r2 = r2.mRenderer     // Catch:{ all -> 0x0375 }
                r2.onDrawFrame(r7)     // Catch:{ all -> 0x0375 }
                com.google.vr.ndk.base.TraceCompat.endSection()     // Catch:{ all -> 0x0096 }
                goto L_0x00ee
            L_0x0375:
                r2 = move-exception
                com.google.vr.ndk.base.TraceCompat.endSection()     // Catch:{ all -> 0x0096 }
                throw r2     // Catch:{ all -> 0x0096 }
            L_0x037a:
                if (r8 == 0) goto L_0x00f0
                r2 = r3
                goto L_0x010b
            L_0x037f:
                r2 = r3
                goto L_0x010b
            L_0x0382:
                r2 = 1
                goto L_0x010b
            L_0x0385:
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r0 = r0.mGLThreadManager     // Catch:{ all -> 0x0096 }
                r17 = r0
                monitor-enter(r17)     // Catch:{ all -> 0x0096 }
                r2 = 1
                r0 = r22
                r0.mSurfaceIsBad = r2     // Catch:{ all -> 0x039c }
                r0 = r22
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r2 = r0.mGLThreadManager     // Catch:{ all -> 0x039c }
                r2.notifyAll()     // Catch:{ all -> 0x039c }
                monitor-exit(r17)     // Catch:{ all -> 0x039c }
                r2 = r3
                goto L_0x010b
            L_0x039c:
                r2 = move-exception
                monitor-exit(r17)     // Catch:{ all -> 0x039c }
                throw r2     // Catch:{ all -> 0x0096 }
            L_0x039f:
                r15 = 1
                r16 = 0
                r3 = r10
                r10 = r15
                r15 = r9
                r9 = r14
                r14 = r6
                r6 = r7
                r7 = r8
                r8 = r13
                r13 = r2
                r20 = r4
                r4 = r11
                r11 = r16
                r16 = r5
                r5 = r12
                r12 = r20
                goto L_0x003f
            L_0x03b7:
                r2 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x03b7 }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.vr.ndk.base.GvrSurfaceView.GLThread.guardedRun():void");
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
            return this.mHaveEglContext && this.mHaveEglSurface && readyToDraw();
        }

        public int getRenderMode() {
            int i;
            synchronized (this.mGLThreadManager) {
                i = this.mRenderMode;
            }
            return i;
        }

        public int getSwapMode() {
            int i;
            synchronized (this.mGLThreadManager) {
                i = this.mRequestedSwapMode;
            }
            return i;
        }

        public void onPause() {
            synchronized (this.mGLThreadManager) {
                this.mRequestPaused = true;
                this.mGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused) {
                    try {
                        this.mGLThreadManager.wait();
                    } catch (InterruptedException e) {
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
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onWindowResize(int r3, int r4) {
            /*
                r2 = this;
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r1 = r2.mGLThreadManager
                monitor-enter(r1)
                r2.mWidth = r3     // Catch:{ all -> 0x0040 }
                r2.mHeight = r4     // Catch:{ all -> 0x0040 }
                r0 = 1
                r2.mSizeChanged = r0     // Catch:{ all -> 0x0040 }
                r0 = 1
                r2.mRequestRender = r0     // Catch:{ all -> 0x0040 }
                r0 = 0
                r2.mRenderComplete = r0     // Catch:{ all -> 0x0040 }
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0040 }
                if (r0 == r2) goto L_0x0021
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r0 = r2.mGLThreadManager     // Catch:{ all -> 0x0040 }
                r0.notifyAll()     // Catch:{ all -> 0x0040 }
            L_0x001b:
                boolean r0 = r2.mExited     // Catch:{ all -> 0x0040 }
                if (r0 == 0) goto L_0x0023
            L_0x001f:
                monitor-exit(r1)     // Catch:{ all -> 0x0040 }
                return
            L_0x0021:
                monitor-exit(r1)     // Catch:{ all -> 0x0040 }
                return
            L_0x0023:
                boolean r0 = r2.mPaused     // Catch:{ all -> 0x0040 }
                if (r0 != 0) goto L_0x001f
                boolean r0 = r2.mRenderComplete     // Catch:{ all -> 0x0040 }
                if (r0 != 0) goto L_0x001f
                boolean r0 = r2.ableToDraw()     // Catch:{ all -> 0x0040 }
                if (r0 == 0) goto L_0x001f
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r0 = r2.mGLThreadManager     // Catch:{ InterruptedException -> 0x0037 }
                r0.wait()     // Catch:{ InterruptedException -> 0x0037 }
                goto L_0x001b
            L_0x0037:
                r0 = move-exception
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0040 }
                r0.interrupt()     // Catch:{ all -> 0x0040 }
                goto L_0x001b
            L_0x0040:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0040 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.vr.ndk.base.GvrSurfaceView.GLThread.onWindowResize(int, int):void");
        }

        public void queueEvent(Runnable runnable) {
            if (runnable != null) {
                synchronized (this.mGLThreadManager) {
                    this.mEventQueue.add(runnable);
                    this.mGLThreadManager.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("r must not be null");
        }

        public void requestExitAndWait() {
            synchronized (this.mGLThreadManager) {
                this.mShouldExit = true;
                this.mGLThreadManager.notifyAll();
                while (!this.mExited) {
                    try {
                        this.mGLThreadManager.wait();
                    } catch (InterruptedException e) {
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

        /* JADX WARNING: Code restructure failed: missing block: B:9:0x001c, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void requestRenderAndWait() {
            /*
                r2 = this;
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r1 = r2.mGLThreadManager
                monitor-enter(r1)
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x003c }
                if (r0 == r2) goto L_0x001d
                r0 = 1
                r2.mWantRenderNotification = r0     // Catch:{ all -> 0x003c }
                r0 = 1
                r2.mRequestRender = r0     // Catch:{ all -> 0x003c }
                r0 = 0
                r2.mRenderComplete = r0     // Catch:{ all -> 0x003c }
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r0 = r2.mGLThreadManager     // Catch:{ all -> 0x003c }
                r0.notifyAll()     // Catch:{ all -> 0x003c }
            L_0x0017:
                boolean r0 = r2.mExited     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x001f
            L_0x001b:
                monitor-exit(r1)     // Catch:{ all -> 0x003c }
                return
            L_0x001d:
                monitor-exit(r1)     // Catch:{ all -> 0x003c }
                return
            L_0x001f:
                boolean r0 = r2.mPaused     // Catch:{ all -> 0x003c }
                if (r0 != 0) goto L_0x001b
                boolean r0 = r2.mRenderComplete     // Catch:{ all -> 0x003c }
                if (r0 != 0) goto L_0x001b
                boolean r0 = r2.ableToDraw()     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x001b
                com.google.vr.ndk.base.GvrSurfaceView$GLThread$GLThreadManager r0 = r2.mGLThreadManager     // Catch:{ InterruptedException -> 0x0033 }
                r0.wait()     // Catch:{ InterruptedException -> 0x0033 }
                goto L_0x0017
            L_0x0033:
                r0 = move-exception
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x003c }
                r0.interrupt()     // Catch:{ all -> 0x003c }
                goto L_0x0017
            L_0x003c:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x003c }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.vr.ndk.base.GvrSurfaceView.GLThread.requestRenderAndWait():void");
        }

        public void run() {
            setName(new StringBuilder(29).append("GLThread ").append(getId()).toString());
            try {
                guardedRun();
            } catch (InterruptedException e) {
            } finally {
                this.mGLThreadManager.threadExiting(this);
            }
        }

        public void setRenderMode(int i) {
            if (i >= 0 && i <= 1) {
                synchronized (this.mGLThreadManager) {
                    this.mRenderMode = i;
                    this.mGLThreadManager.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("renderMode");
        }

        public void setSwapMode(int i) {
            if (i >= 0 && i <= 2) {
                synchronized (this.mGLThreadManager) {
                    this.mRequestedSwapMode = i;
                    this.mGLThreadManager.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("swapMode");
        }

        public void surfaceCreated() {
            synchronized (this.mGLThreadManager) {
                this.mHasSurface = true;
                this.mFinishedCreatingEglSurface = false;
                this.mGLThreadManager.notifyAll();
                while (this.mWaitingForSurface && !this.mFinishedCreatingEglSurface && !this.mExited) {
                    try {
                        this.mGLThreadManager.wait();
                    } catch (InterruptedException e) {
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
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public interface GLWrapper {
        GL wrap(GL gl);
    }

    static class LogWriter extends Writer {
        private StringBuilder mBuilder = new StringBuilder();

        LogWriter() {
        }

        private void flushBuilder() {
            if (this.mBuilder.length() > 0) {
                Log.v(GvrSurfaceView.TAG, this.mBuilder.toString());
                this.mBuilder.delete(0, this.mBuilder.length());
            }
        }

        public void close() {
            flushBuilder();
        }

        public void flush() {
            flushBuilder();
        }

        public void write(char[] cArr, int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                char c = cArr[i + i3];
                if (c != 10) {
                    this.mBuilder.append(c);
                } else {
                    flushBuilder();
                }
            }
        }
    }

    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public SimpleEGLConfigChooser(GvrSurfaceView gvrSurfaceView, boolean z) {
            super(gvrSurfaceView, 8, 8, 8, 0, !z ? 0 : 16, 0);
        }
    }

    public GvrSurfaceView(Context context) {
        super(context);
        init();
    }

    public GvrSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void checkRenderThreadState() {
        if (this.mGLThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }

    private void init() {
        getHolder().addCallback(this);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            if (this.mGLThread != null) {
                this.mGLThread.requestExitAndWait();
            }
        } finally {
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

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        int renderMode;
        int swapMode;
        super.onAttachedToWindow();
        if (this.mDetached && this.mRenderer != null) {
            if (this.mGLThread == null) {
                swapMode = 0;
                renderMode = 1;
            } else {
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

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
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

    public void queueEvent(Runnable runnable) {
        this.mGLThread.queueEvent(runnable);
    }

    public void requestRender() {
        this.mGLThread.requestRender();
    }

    public void setDebugFlags(int i) {
        this.mDebugFlags = i;
    }

    public void setEGLConfigChooser(int i, int i2, int i3, int i4, int i5, int i6) {
        setEGLConfigChooser((GLSurfaceView.EGLConfigChooser) new ComponentSizeChooser(this, i, i2, i3, i4, i5, i6));
    }

    public void setEGLConfigChooser(GLSurfaceView.EGLConfigChooser eGLConfigChooser) {
        checkRenderThreadState();
        this.mEGLConfigChooser = eGLConfigChooser;
    }

    public void setEGLConfigChooser(boolean z) {
        setEGLConfigChooser((GLSurfaceView.EGLConfigChooser) new SimpleEGLConfigChooser(this, z));
    }

    public void setEGLContextClientVersion(int i) {
        checkRenderThreadState();
        this.mEGLContextClientVersion = i;
    }

    public void setEGLContextFactory(GLSurfaceView.EGLContextFactory eGLContextFactory) {
        checkRenderThreadState();
        this.mEGLContextFactory = eGLContextFactory;
    }

    public void setEGLWindowSurfaceFactory(GLSurfaceView.EGLWindowSurfaceFactory eGLWindowSurfaceFactory) {
        checkRenderThreadState();
        this.mEGLWindowSurfaceFactory = eGLWindowSurfaceFactory;
    }

    public void setGLWrapper(GLWrapper gLWrapper) {
        this.mGLWrapper = gLWrapper;
    }

    public void setPreserveEGLContextOnPause(boolean z) {
        this.mPreserveEGLContextOnPause = z;
    }

    public void setRenderMode(int i) {
        this.mGLThread.setRenderMode(i);
    }

    public void setRenderer(GLSurfaceView.Renderer renderer) {
        checkRenderThreadState();
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new SimpleEGLConfigChooser(this, true);
        }
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = new DefaultContextFactory();
        }
        if (this.mEGLWindowSurfaceFactory == null) {
            this.mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
        }
        this.mRenderer = renderer;
        this.mGLThread = new GLThread(this.mThisWeakRef);
        this.mGLThread.start();
    }

    public void setSwapMode(int i) {
        if (i == 1 && Build.VERSION.SDK_INT < 17) {
            Log.e(TAG, "setSwapMode(SWAPMODE_SINGLE) requires Jellybean MR1 (EGL14 dependency)");
        } else {
            this.mGLThread.setSwapMode(i);
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        this.mGLThread.onWindowResize(i2, i3);
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mGLThread.surfaceCreated();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mGLThread.surfaceDestroyed();
    }

    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
        this.mGLThread.requestRenderAndWait();
    }
}
