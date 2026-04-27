package com.google.vr.sdk.base;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Distortion {
    private static final float[] CARDBOARD_V1_COEFFICIENTS = {0.441f, 0.156f};
    private static final float[] CARDBOARD_V2_2_COEFFICIENTS = {0.34f, 0.55f};
    private float[] coefficients;

    public Distortion() {
        this.coefficients = (float[]) CARDBOARD_V2_2_COEFFICIENTS.clone();
    }

    public Distortion(Distortion distortion) {
        setCoefficients(distortion.coefficients);
    }

    public static Distortion cardboardV1Distortion() {
        Distortion distortion = new Distortion();
        distortion.coefficients = (float[]) CARDBOARD_V1_COEFFICIENTS.clone();
        return distortion;
    }

    public static Distortion parseFromProtobuf(float[] fArr) {
        Distortion distortion = new Distortion();
        distortion.setCoefficients(fArr);
        return distortion;
    }

    private static double[] solveLeastSquares(double[][] dArr, double[] dArr2) {
        int length = dArr.length;
        int length2 = dArr[0].length;
        double[][] dArr3 = (double[][]) Array.newInstance(Double.TYPE, new int[]{length2, length2});
        for (int i = 0; i < length2; i++) {
            for (int i2 = 0; i2 < length2; i2++) {
                double d = 0.0d;
                for (int i3 = 0; i3 < length; i3++) {
                    d += dArr[i3][i2] * dArr[i3][i];
                }
                dArr3[i2][i] = d;
            }
        }
        double[] dArr4 = new double[length2];
        for (int i4 = 0; i4 < length2; i4++) {
            double d2 = 0.0d;
            for (int i5 = 0; i5 < length; i5++) {
                d2 += dArr[i5][i4] * dArr2[i5];
            }
            dArr4[i4] = d2;
        }
        return solveLinear(dArr3, dArr4);
    }

    private static double[] solveLinear(double[][] dArr, double[] dArr2) {
        int i = 0;
        int length = dArr[0].length;
        while (true) {
            int i2 = i;
            if (i2 >= length - 1) {
                break;
            }
            for (int i3 = i2 + 1; i3 < length; i3++) {
                double d = dArr[i3][i2] / dArr[i2][i2];
                for (int i4 = i2 + 1; i4 < length; i4++) {
                    double[] dArr3 = dArr[i3];
                    dArr3[i4] = dArr3[i4] - (dArr[i2][i4] * d);
                }
                dArr2[i3] = dArr2[i3] - (d * dArr2[i2]);
            }
            i = i2 + 1;
        }
        double[] dArr4 = new double[length];
        for (int i5 = length - 1; i5 >= 0; i5--) {
            double d2 = dArr2[i5];
            for (int i6 = i5 + 1; i6 < length; i6++) {
                d2 -= dArr[i5][i6] * dArr4[i6];
            }
            dArr4[i5] = d2 / dArr[i5][i5];
        }
        return dArr4;
    }

    public float distort(float f) {
        return distortionFactor(f) * f;
    }

    public float distortInverse(float f) {
        float f2 = f / 0.9f;
        float distort = f - distort(f2);
        float f3 = f2;
        float f4 = f * 0.9f;
        while (((double) Math.abs(f4 - f3)) > 1.0E-4d) {
            float distort2 = f - distort(f4);
            f3 = f4;
            f4 -= ((f4 - f3) / (distort2 - distort)) * distort2;
            distort = distort2;
        }
        return f4;
    }

    public float distortionFactor(float f) {
        float f2 = 1.0f;
        float f3 = f * f;
        float f4 = 1.0f;
        for (float f5 : this.coefficients) {
            f2 *= f3;
            f4 += f5 * f2;
        }
        return f4;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Distortion) {
            return Arrays.equals(this.coefficients, ((Distortion) obj).coefficients);
        }
        return false;
    }

    @Deprecated
    public Distortion getApproximateInverseDistortion(float f) {
        return getApproximateInverseDistortion(f, 2);
    }

    public Distortion getApproximateInverseDistortion(float f, int i) {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{100, i});
        double[] dArr2 = new double[100];
        for (int i2 = 0; i2 < 100; i2++) {
            float f2 = (((float) (i2 + 1)) * f) / 100.0f;
            double distort = (double) distort(f2);
            double d = distort;
            for (int i3 = 0; i3 < i; i3++) {
                d *= distort * distort;
                dArr[i2][i3] = d;
            }
            dArr2[i2] = ((double) f2) - distort;
        }
        double[] solveLeastSquares = solveLeastSquares(dArr, dArr2);
        float[] fArr = new float[solveLeastSquares.length];
        for (int i4 = 0; i4 < solveLeastSquares.length; i4++) {
            fArr[i4] = (float) solveLeastSquares[i4];
        }
        Distortion distortion = new Distortion();
        distortion.setCoefficients(fArr);
        return distortion;
    }

    public float[] getCoefficients() {
        return this.coefficients;
    }

    public void setCoefficients(float[] fArr) {
        this.coefficients = fArr == null ? new float[0] : (float[]) fArr.clone();
    }

    public float[] toProtobuf() {
        return (float[]) this.coefficients.clone();
    }

    public String toString() {
        StringBuilder append = new StringBuilder().append("{\n").append("  coefficients: [");
        for (int i = 0; i < this.coefficients.length; i++) {
            append.append(Float.toString(this.coefficients[i]));
            if (i < this.coefficients.length - 1) {
                append.append(", ");
            }
        }
        append.append("],\n}");
        return append.toString();
    }
}
