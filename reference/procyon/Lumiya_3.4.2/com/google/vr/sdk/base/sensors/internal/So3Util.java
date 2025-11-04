// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors.internal;

public class So3Util
{
    private static final double M_SQRT1_2 = 0.7071067811865476;
    private static final double ONE_20TH = 0.1666666716337204;
    private static final double ONE_6TH = 0.1666666716337204;
    private static Vector3d muFromSO3R2;
    private static Vector3d rotationPiAboutAxisTemp;
    private static Matrix3x3d sO3FromTwoVec33R1;
    private static Matrix3x3d sO3FromTwoVec33R2;
    private static Vector3d sO3FromTwoVecA;
    private static Vector3d sO3FromTwoVecB;
    private static Vector3d sO3FromTwoVecN;
    private static Vector3d sO3FromTwoVecRotationAxis;
    private static Vector3d temp31;
    
    static {
        So3Util.temp31 = new Vector3d();
        So3Util.sO3FromTwoVecN = new Vector3d();
        So3Util.sO3FromTwoVecA = new Vector3d();
        So3Util.sO3FromTwoVecB = new Vector3d();
        So3Util.sO3FromTwoVecRotationAxis = new Vector3d();
        So3Util.sO3FromTwoVec33R1 = new Matrix3x3d();
        So3Util.sO3FromTwoVec33R2 = new Matrix3x3d();
        So3Util.muFromSO3R2 = new Vector3d();
        So3Util.rotationPiAboutAxisTemp = new Vector3d();
    }
    
    public static void generatorField(final int n, final Matrix3x3d matrix3x3d, final Matrix3x3d matrix3x3d2) {
        matrix3x3d2.set(n, 0, 0.0);
        matrix3x3d2.set((n + 1) % 3, 0, -matrix3x3d.get((n + 2) % 3, 0));
        matrix3x3d2.set((n + 2) % 3, 0, matrix3x3d.get((n + 1) % 3, 0));
    }
    
    public static void muFromSO3(final Matrix3x3d matrix3x3d, final Vector3d vector3d) {
        final double a = (matrix3x3d.get(0, 0) + matrix3x3d.get(1, 1) + matrix3x3d.get(2, 2) - 1.0) * 0.5;
        vector3d.set((matrix3x3d.get(2, 1) - matrix3x3d.get(1, 2)) / 2.0, (matrix3x3d.get(0, 2) - matrix3x3d.get(2, 0)) / 2.0, (matrix3x3d.get(1, 0) - matrix3x3d.get(0, 1)) / 2.0);
        final double length = vector3d.length();
        if (a > 0.7071067811865476) {
            if (length > 0.0) {
                vector3d.scale(Math.asin(length) / length);
            }
        }
        else if (a > -0.7071067811865476) {
            vector3d.scale(Math.acos(a) / length);
        }
        else {
            final double asin = Math.asin(length);
            final double n = matrix3x3d.get(0, 0) - a;
            final double n2 = matrix3x3d.get(1, 1) - a;
            final double n3 = matrix3x3d.get(2, 2) - a;
            final Vector3d muFromSO3R2 = So3Util.muFromSO3R2;
            if (n * n > n2 * n2 && n * n > n3 * n3) {
                muFromSO3R2.set(n, (matrix3x3d.get(1, 0) + matrix3x3d.get(0, 1)) / 2.0, (matrix3x3d.get(0, 2) + matrix3x3d.get(2, 0)) / 2.0);
            }
            else if (n2 * n2 > n3 * n3) {
                muFromSO3R2.set((matrix3x3d.get(1, 0) + matrix3x3d.get(0, 1)) / 2.0, n2, (matrix3x3d.get(2, 1) + matrix3x3d.get(1, 2)) / 2.0);
            }
            else {
                muFromSO3R2.set((matrix3x3d.get(0, 2) + matrix3x3d.get(2, 0)) / 2.0, (matrix3x3d.get(2, 1) + matrix3x3d.get(1, 2)) / 2.0, n3);
            }
            if (Vector3d.dot(muFromSO3R2, vector3d) < 0.0) {
                muFromSO3R2.scale(-1.0);
            }
            muFromSO3R2.normalize();
            muFromSO3R2.scale(3.141592653589793 - asin);
            vector3d.set(muFromSO3R2);
        }
    }
    
    private static void rodriguesSo3Exp(final Vector3d vector3d, double n, double n2, final Matrix3x3d matrix3x3d) {
        final double n3 = vector3d.x * vector3d.x;
        final double n4 = vector3d.y * vector3d.y;
        final double n5 = vector3d.z * vector3d.z;
        matrix3x3d.set(0, 0, 1.0 - (n4 + n5) * n2);
        matrix3x3d.set(1, 1, 1.0 - (n5 + n3) * n2);
        matrix3x3d.set(2, 2, 1.0 - (n3 + n4) * n2);
        final double n6 = vector3d.z * n;
        final double n7 = vector3d.x * vector3d.y * n2;
        matrix3x3d.set(0, 1, n7 - n6);
        matrix3x3d.set(1, 0, n6 + n7);
        final double n8 = vector3d.y * n;
        final double n9 = vector3d.x * vector3d.z * n2;
        matrix3x3d.set(0, 2, n9 + n8);
        matrix3x3d.set(2, 0, n9 - n8);
        n *= vector3d.x;
        n2 *= vector3d.y * vector3d.z;
        matrix3x3d.set(1, 2, n2 - n);
        matrix3x3d.set(2, 1, n + n2);
    }
    
    private static void rotationPiAboutAxis(final Vector3d vector3d, final Matrix3x3d matrix3x3d) {
        So3Util.rotationPiAboutAxisTemp.set(vector3d);
        So3Util.rotationPiAboutAxisTemp.scale(3.141592653589793 / So3Util.rotationPiAboutAxisTemp.length());
        rodriguesSo3Exp(So3Util.rotationPiAboutAxisTemp, 0.0, 0.20264236728467558, matrix3x3d);
    }
    
    public static void sO3FromMu(final Vector3d vector3d, final Matrix3x3d matrix3x3d) {
        double n = 0.5;
        final double dot = Vector3d.dot(vector3d, vector3d);
        final double sqrt = Math.sqrt(dot);
        double n2;
        if (dot < 1.0E-8) {
            n2 = 1.0 - dot * 0.1666666716337204;
        }
        else if (dot < 1.0E-6) {
            n = 0.5 - 0.0416666679084301 * dot;
            n2 = 1.0 - (1.0 - dot * 0.1666666716337204) * (dot * 0.1666666716337204);
        }
        else {
            final double n3 = 1.0 / sqrt;
            n2 = Math.sin(sqrt) * n3;
            n = (1.0 - Math.cos(sqrt)) * (n3 * n3);
        }
        rodriguesSo3Exp(vector3d, n2, n, matrix3x3d);
    }
    
    public static void sO3FromTwoVec(final Vector3d vector3d, final Vector3d vector3d2, final Matrix3x3d matrix3x3d) {
        Vector3d.cross(vector3d, vector3d2, So3Util.sO3FromTwoVecN);
        if (So3Util.sO3FromTwoVecN.length() == 0.0) {
            if (Vector3d.dot(vector3d, vector3d2) >= 0.0) {
                matrix3x3d.setIdentity();
            }
            else {
                Vector3d.ortho(vector3d, So3Util.sO3FromTwoVecRotationAxis);
                rotationPiAboutAxis(So3Util.sO3FromTwoVecRotationAxis, matrix3x3d);
            }
            return;
        }
        So3Util.sO3FromTwoVecA.set(vector3d);
        So3Util.sO3FromTwoVecB.set(vector3d2);
        So3Util.sO3FromTwoVecN.normalize();
        So3Util.sO3FromTwoVecA.normalize();
        So3Util.sO3FromTwoVecB.normalize();
        final Matrix3x3d so3FromTwoVec33R1 = So3Util.sO3FromTwoVec33R1;
        so3FromTwoVec33R1.setColumn(0, So3Util.sO3FromTwoVecA);
        so3FromTwoVec33R1.setColumn(1, So3Util.sO3FromTwoVecN);
        Vector3d.cross(So3Util.sO3FromTwoVecN, So3Util.sO3FromTwoVecA, So3Util.temp31);
        so3FromTwoVec33R1.setColumn(2, So3Util.temp31);
        final Matrix3x3d so3FromTwoVec33R2 = So3Util.sO3FromTwoVec33R2;
        so3FromTwoVec33R2.setColumn(0, So3Util.sO3FromTwoVecB);
        so3FromTwoVec33R2.setColumn(1, So3Util.sO3FromTwoVecN);
        Vector3d.cross(So3Util.sO3FromTwoVecN, So3Util.sO3FromTwoVecB, So3Util.temp31);
        so3FromTwoVec33R2.setColumn(2, So3Util.temp31);
        so3FromTwoVec33R1.transpose();
        Matrix3x3d.mult(so3FromTwoVec33R2, so3FromTwoVec33R1, matrix3x3d);
    }
}
