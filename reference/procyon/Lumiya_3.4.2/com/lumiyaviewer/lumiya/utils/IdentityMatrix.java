// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import android.opengl.Matrix;

public class IdentityMatrix
{
    private static final float[] matrix;
    
    static {
        Matrix.setIdentityM(matrix = new float[16], 0);
    }
    
    public static float[] getMatrix() {
        return IdentityMatrix.matrix;
    }
}
