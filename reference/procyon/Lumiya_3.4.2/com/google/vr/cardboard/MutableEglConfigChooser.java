// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGL10;
import android.opengl.GLSurfaceView$EGLConfigChooser;

public class MutableEglConfigChooser implements GLSurfaceView$EGLConfigChooser
{
    private static final int EGL_MUTABLE_RENDER_BUFFER_BIT = 4096;
    private static final int EGL_OPENGL_ES3_BIT_KHR = 64;
    private boolean forceMutableBuffer;
    
    public MutableEglConfigChooser() {
        this.forceMutableBuffer = true;
    }
    
    public MutableEglConfigChooser(final boolean forceMutableBuffer) {
        this.forceMutableBuffer = true;
        this.forceMutableBuffer = forceMutableBuffer;
    }
    
    private static EGLConfig chooseConfig(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLConfig[] array, final boolean b) {
        for (final EGLConfig eglConfig : array) {
            final int configAttrib = findConfigAttrib(egl10, eglDisplay, eglConfig, 12325, 0);
            final int configAttrib2 = findConfigAttrib(egl10, eglDisplay, eglConfig, 12326, 0);
            final int configAttrib3 = findConfigAttrib(egl10, eglDisplay, eglConfig, 12324, 0);
            final int configAttrib4 = findConfigAttrib(egl10, eglDisplay, eglConfig, 12323, 0);
            final int configAttrib5 = findConfigAttrib(egl10, eglDisplay, eglConfig, 12322, 0);
            final int configAttrib6 = findConfigAttrib(egl10, eglDisplay, eglConfig, 12339, 0);
            if (configAttrib3 == 8 && configAttrib4 == 8 && configAttrib5 == 8 && configAttrib == 0 && configAttrib2 == 0 && (!b || (configAttrib6 & 0x1000) != 0x0)) {
                return eglConfig;
            }
        }
        return null;
    }
    
    private static int findConfigAttrib(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLConfig eglConfig, final int n, final int n2) {
        final int[] array = { 0 };
        if (!egl10.eglGetConfigAttrib(eglDisplay, eglConfig, n, array)) {
            return n2;
        }
        return array[0];
    }
    
    public EGLConfig chooseConfig(final EGL10 egl10, final EGLDisplay eglDisplay) {
        final int[] array = { 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12352, 64, 12339, 4100, 12344 };
        final int[] array2 = { 0 };
        if (!egl10.eglChooseConfig(eglDisplay, array, (EGLConfig[])null, 0, array2) && this.forceMutableBuffer) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        array[15] = 4;
        if (!egl10.eglChooseConfig(eglDisplay, array, (EGLConfig[])null, 0, array2)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        final int n = array2[0];
        if (n <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }
        final EGLConfig[] array3 = new EGLConfig[n];
        if (!egl10.eglChooseConfig(eglDisplay, array, array3, n, array2)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        final EGLConfig chooseConfig = chooseConfig(egl10, eglDisplay, array3, this.forceMutableBuffer);
        if (chooseConfig != null) {
            return chooseConfig;
        }
        throw new IllegalArgumentException("No config chosen");
    }
}
