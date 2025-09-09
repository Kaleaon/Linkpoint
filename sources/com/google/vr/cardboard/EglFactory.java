package com.google.vr.cardboard;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EglFactory implements GLSurfaceView.EGLContextFactory, GLSurfaceView.EGLWindowSurfaceFactory {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EGL_CONTEXT_PRIORITY_HIGH = 12545;
    private static final int EGL_CONTEXT_PRIORITY_LEVEL = 12544;
    private static final int EGL_PROTECTED_CONTENT_EXT = 12992;
    private static final int MIN_REQUIRED_CONTEXT_CLIENT_VERSION = 2;
    private static final String TAG = "GvrEglFactory";
    private int eglContextClientVersion = 2;
    private boolean usePriority = false;
    private boolean useProtected = false;

    private boolean supportsProtectedContent(EGL10 egl10, EGLDisplay eGLDisplay) {
        return egl10.eglQueryString(eGLDisplay, 12373).contains("EGL_EXT_protected_content");
    }

    public EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
        IntBuffer allocate = IntBuffer.allocate(8);
        allocate.put(EGL_CONTEXT_CLIENT_VERSION);
        allocate.put(this.eglContextClientVersion);
        if (this.usePriority) {
            allocate.put(EGL_CONTEXT_PRIORITY_LEVEL);
            allocate.put(EGL_CONTEXT_PRIORITY_HIGH);
        }
        if (this.useProtected && supportsProtectedContent(egl10, eGLDisplay)) {
            allocate.put(EGL_PROTECTED_CONTENT_EXT);
            allocate.put(1);
        }
        while (allocate.hasRemaining()) {
            allocate.put(12344);
        }
        EGLContext eglCreateContext = egl10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, allocate.array());
        if (eglCreateContext != null || this.eglContextClientVersion <= 2) {
            return eglCreateContext;
        }
        Log.w(TAG, new StringBuilder(75).append("Failed to create EGL context with version ").append(this.eglContextClientVersion).append(", will try 2").toString());
        allocate.array()[1] = 2;
        return egl10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, allocate.array());
    }

    public EGLSurface createWindowSurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj) {
        try {
            return egl10.eglCreateWindowSurface(eGLDisplay, eGLConfig, obj, (this.useProtected && supportsProtectedContent(egl10, eGLDisplay)) ? new int[]{EGL_PROTECTED_CONTENT_EXT, 1, 12344} : null);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "eglCreateWindowSurface", e);
            return null;
        }
    }

    public void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
        egl10.eglDestroyContext(eGLDisplay, eGLContext);
    }

    public void destroySurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface) {
        egl10.eglDestroySurface(eGLDisplay, eGLSurface);
    }

    public void setEGLContextClientVersion(int i) {
        this.eglContextClientVersion = i;
    }

    public void setUsePriorityContext(boolean z) {
        this.usePriority = z;
    }

    public void setUseProtectedBuffers(boolean z) {
        if (z && Build.VERSION.SDK_INT < 17) {
            throw new RuntimeException("Protected buffer support requires EGL 1.4, available only on Jelly Bean MR1 and later.");
        }
        this.useProtected = z;
    }
}
