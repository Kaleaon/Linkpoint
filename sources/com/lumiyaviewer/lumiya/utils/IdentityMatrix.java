package com.lumiyaviewer.lumiya.utils;

import android.opengl.Matrix;

public class IdentityMatrix {
    private static final float[] matrix = new float[16];

    static {
        Matrix.setIdentityM(matrix, 0);
    }

    public static float[] getMatrix() {
        return matrix;
    }
}
