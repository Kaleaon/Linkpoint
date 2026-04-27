package com.google.vr.sdk.base.testing;

import com.google.vr.sdk.base.sensors.internal.Matrix3x3d;
import com.google.vr.sdk.base.sensors.internal.Vector3d;

public final class MathUtil {
    private MathUtil() {
    }

    public static double matrixDiffNorm(Matrix3x3d matrix3x3d, Matrix3x3d matrix3x3d2) {
        Matrix3x3d matrix3x3d3 = new Matrix3x3d(matrix3x3d);
        matrix3x3d3.minusEquals(matrix3x3d2);
        return matrix3x3d3.maxNorm();
    }

    public static double vectorDiffNorm(Vector3d vector3d, Vector3d vector3d2) {
        Vector3d vector3d3 = new Vector3d();
        Vector3d.sub(vector3d, vector3d2, vector3d3);
        return vector3d3.maxNorm();
    }

    public static Vector3d vectorFromScalar(double d) {
        return new Vector3d(d, -0.5d * d, 0.8d * d);
    }
}
