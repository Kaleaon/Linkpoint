// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.testing;

import com.google.vr.sdk.base.sensors.internal.Vector3d;
import com.google.vr.sdk.base.sensors.internal.Matrix3x3d;

public final class MathUtil
{
    private MathUtil() {
    }
    
    public static double matrixDiffNorm(Matrix3x3d matrix3x3d, final Matrix3x3d matrix3x3d2) {
        matrix3x3d = new Matrix3x3d(matrix3x3d);
        matrix3x3d.minusEquals(matrix3x3d2);
        return matrix3x3d.maxNorm();
    }
    
    public static double vectorDiffNorm(final Vector3d vector3d, final Vector3d vector3d2) {
        final Vector3d vector3d3 = new Vector3d();
        Vector3d.sub(vector3d, vector3d2, vector3d3);
        return vector3d3.maxNorm();
    }
    
    public static Vector3d vectorFromScalar(final double n) {
        return new Vector3d(n, -0.5 * n, 0.8 * n);
    }
}
