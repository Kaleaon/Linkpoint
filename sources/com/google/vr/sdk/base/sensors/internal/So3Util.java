package com.google.vr.sdk.base.sensors.internal;

public class So3Util {
    private static final double M_SQRT1_2 = 0.7071067811865476d;
    private static final double ONE_20TH = 0.1666666716337204d;
    private static final double ONE_6TH = 0.1666666716337204d;
    private static Vector3d muFromSO3R2 = new Vector3d();
    private static Vector3d rotationPiAboutAxisTemp = new Vector3d();
    private static Matrix3x3d sO3FromTwoVec33R1 = new Matrix3x3d();
    private static Matrix3x3d sO3FromTwoVec33R2 = new Matrix3x3d();
    private static Vector3d sO3FromTwoVecA = new Vector3d();
    private static Vector3d sO3FromTwoVecB = new Vector3d();
    private static Vector3d sO3FromTwoVecN = new Vector3d();
    private static Vector3d sO3FromTwoVecRotationAxis = new Vector3d();
    private static Vector3d temp31 = new Vector3d();

    public static void generatorField(int i, Matrix3x3d matrix3x3d, Matrix3x3d matrix3x3d2) {
        matrix3x3d2.set(i, 0, 0.0d);
        matrix3x3d2.set((i + 1) % 3, 0, -matrix3x3d.get((i + 2) % 3, 0));
        matrix3x3d2.set((i + 2) % 3, 0, matrix3x3d.get((i + 1) % 3, 0));
    }

    public static void muFromSO3(Matrix3x3d matrix3x3d, Vector3d vector3d) {
        double d = (((matrix3x3d.get(0, 0) + matrix3x3d.get(1, 1)) + matrix3x3d.get(2, 2)) - 1.0d) * 0.5d;
        vector3d.set((matrix3x3d.get(2, 1) - matrix3x3d.get(1, 2)) / 2.0d, (matrix3x3d.get(0, 2) - matrix3x3d.get(2, 0)) / 2.0d, (matrix3x3d.get(1, 0) - matrix3x3d.get(0, 1)) / 2.0d);
        double length = vector3d.length();
        if (d > M_SQRT1_2) {
            if (length > 0.0d) {
                vector3d.scale(Math.asin(length) / length);
            }
        } else if (d > -0.7071067811865476d) {
            vector3d.scale(Math.acos(d) / length);
        } else {
            double asin = 3.141592653589793d - Math.asin(length);
            double d2 = matrix3x3d.get(0, 0) - d;
            double d3 = matrix3x3d.get(1, 1) - d;
            double d4 = matrix3x3d.get(2, 2) - d;
            Vector3d vector3d2 = muFromSO3R2;
            if (d2 * d2 > d3 * d3 && d2 * d2 > d4 * d4) {
                vector3d2.set(d2, (matrix3x3d.get(1, 0) + matrix3x3d.get(0, 1)) / 2.0d, (matrix3x3d.get(0, 2) + matrix3x3d.get(2, 0)) / 2.0d);
            } else if (d3 * d3 > d4 * d4) {
                vector3d2.set((matrix3x3d.get(1, 0) + matrix3x3d.get(0, 1)) / 2.0d, d3, (matrix3x3d.get(2, 1) + matrix3x3d.get(1, 2)) / 2.0d);
            } else {
                vector3d2.set((matrix3x3d.get(0, 2) + matrix3x3d.get(2, 0)) / 2.0d, (matrix3x3d.get(2, 1) + matrix3x3d.get(1, 2)) / 2.0d, d4);
            }
            if (Vector3d.dot(vector3d2, vector3d) < 0.0d) {
                vector3d2.scale(-1.0d);
            }
            vector3d2.normalize();
            vector3d2.scale(asin);
            vector3d.set(vector3d2);
        }
    }

    private static void rodriguesSo3Exp(Vector3d vector3d, double d, double d2, Matrix3x3d matrix3x3d) {
        double d3 = vector3d.x * vector3d.x;
        double d4 = vector3d.y * vector3d.y;
        double d5 = vector3d.z * vector3d.z;
        matrix3x3d.set(0, 0, 1.0d - ((d4 + d5) * d2));
        matrix3x3d.set(1, 1, 1.0d - ((d5 + d3) * d2));
        matrix3x3d.set(2, 2, 1.0d - ((d3 + d4) * d2));
        double d6 = vector3d.z * d;
        double d7 = vector3d.x * vector3d.y * d2;
        matrix3x3d.set(0, 1, d7 - d6);
        matrix3x3d.set(1, 0, d6 + d7);
        double d8 = vector3d.y * d;
        double d9 = vector3d.x * vector3d.z * d2;
        matrix3x3d.set(0, 2, d9 + d8);
        matrix3x3d.set(2, 0, d9 - d8);
        double d10 = vector3d.x * d;
        double d11 = vector3d.y * vector3d.z * d2;
        matrix3x3d.set(1, 2, d11 - d10);
        matrix3x3d.set(2, 1, d10 + d11);
    }

    private static void rotationPiAboutAxis(Vector3d vector3d, Matrix3x3d matrix3x3d) {
        rotationPiAboutAxisTemp.set(vector3d);
        rotationPiAboutAxisTemp.scale(3.141592653589793d / rotationPiAboutAxisTemp.length());
        rodriguesSo3Exp(rotationPiAboutAxisTemp, 0.0d, 0.20264236728467558d, matrix3x3d);
    }

    public static void sO3FromMu(Vector3d vector3d, Matrix3x3d matrix3x3d) {
        double sin;
        double d = 0.5d;
        double dot = Vector3d.dot(vector3d, vector3d);
        double sqrt = Math.sqrt(dot);
        if (dot < 1.0E-8d) {
            sin = 1.0d - (dot * 0.1666666716337204d);
        } else if (dot < 1.0E-6d) {
            d = 0.5d - (0.0416666679084301d * dot);
            sin = 1.0d - ((1.0d - (dot * 0.1666666716337204d)) * (dot * 0.1666666716337204d));
        } else {
            double d2 = 1.0d / sqrt;
            sin = Math.sin(sqrt) * d2;
            d = (1.0d - Math.cos(sqrt)) * d2 * d2;
        }
        rodriguesSo3Exp(vector3d, sin, d, matrix3x3d);
    }

    public static void sO3FromTwoVec(Vector3d vector3d, Vector3d vector3d2, Matrix3x3d matrix3x3d) {
        Vector3d.cross(vector3d, vector3d2, sO3FromTwoVecN);
        if (sO3FromTwoVecN.length() != 0.0d) {
            sO3FromTwoVecA.set(vector3d);
            sO3FromTwoVecB.set(vector3d2);
            sO3FromTwoVecN.normalize();
            sO3FromTwoVecA.normalize();
            sO3FromTwoVecB.normalize();
            Matrix3x3d matrix3x3d2 = sO3FromTwoVec33R1;
            matrix3x3d2.setColumn(0, sO3FromTwoVecA);
            matrix3x3d2.setColumn(1, sO3FromTwoVecN);
            Vector3d.cross(sO3FromTwoVecN, sO3FromTwoVecA, temp31);
            matrix3x3d2.setColumn(2, temp31);
            Matrix3x3d matrix3x3d3 = sO3FromTwoVec33R2;
            matrix3x3d3.setColumn(0, sO3FromTwoVecB);
            matrix3x3d3.setColumn(1, sO3FromTwoVecN);
            Vector3d.cross(sO3FromTwoVecN, sO3FromTwoVecB, temp31);
            matrix3x3d3.setColumn(2, temp31);
            matrix3x3d2.transpose();
            Matrix3x3d.mult(matrix3x3d3, matrix3x3d2, matrix3x3d);
        } else if (Vector3d.dot(vector3d, vector3d2) >= 0.0d) {
            matrix3x3d.setIdentity();
        } else {
            Vector3d.ortho(vector3d, sO3FromTwoVecRotationAxis);
            rotationPiAboutAxis(sO3FromTwoVecRotationAxis, matrix3x3d);
        }
    }
}
