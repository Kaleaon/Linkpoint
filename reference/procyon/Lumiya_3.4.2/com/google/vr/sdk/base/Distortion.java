// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import java.util.Arrays;

public class Distortion
{
    private static final float[] CARDBOARD_V1_COEFFICIENTS;
    private static final float[] CARDBOARD_V2_2_COEFFICIENTS;
    private float[] coefficients;
    
    static {
        CARDBOARD_V2_2_COEFFICIENTS = new float[] { 0.34f, 0.55f };
        CARDBOARD_V1_COEFFICIENTS = new float[] { 0.441f, 0.156f };
    }
    
    public Distortion() {
        this.coefficients = Distortion.CARDBOARD_V2_2_COEFFICIENTS.clone();
    }
    
    public Distortion(final Distortion distortion) {
        this.setCoefficients(distortion.coefficients);
    }
    
    public static Distortion cardboardV1Distortion() {
        final Distortion distortion = new Distortion();
        distortion.coefficients = Distortion.CARDBOARD_V1_COEFFICIENTS.clone();
        return distortion;
    }
    
    public static Distortion parseFromProtobuf(final float[] coefficients) {
        final Distortion distortion = new Distortion();
        distortion.setCoefficients(coefficients);
        return distortion;
    }
    
    private static double[] solveLeastSquares(final double[][] array, final double[] array2) {
        final int length = array.length;
        final int length2 = array[0].length;
        final double[][] array3 = new double[length2][length2];
        for (int i = 0; i < length2; ++i) {
            for (int j = 0; j < length2; ++j) {
                double n = 0.0;
                for (int k = 0; k < length; ++k) {
                    n += array[k][j] * array[k][i];
                }
                array3[j][i] = n;
            }
        }
        final double[] array4 = new double[length2];
        for (int l = 0; l < length2; ++l) {
            double n2 = 0.0;
            for (int n3 = 0; n3 < length; ++n3) {
                n2 += array[n3][l] * array2[n3];
            }
            array4[l] = n2;
        }
        return solveLinear(array3, array4);
    }
    
    private static double[] solveLinear(final double[][] array, final double[] array2) {
        final int length = array[0].length;
        for (int i = 0; i < length - 1; ++i) {
            for (int j = i + 1; j < length; ++j) {
                final double n = array[j][i] / array[i][i];
                for (int k = i + 1; k < length; ++k) {
                    final double[] array3 = array[j];
                    array3[k] -= array[i][k] * n;
                }
                array2[j] -= n * array2[i];
            }
        }
        final double[] array4 = new double[length];
        for (int l = length - 1; l >= 0; --l) {
            double n2 = array2[l];
            for (int n3 = l + 1; n3 < length; ++n3) {
                n2 -= array[l][n3] * array4[n3];
            }
            array4[l] = n2 / array[l][l];
        }
        return array4;
    }
    
    public float distort(final float n) {
        return this.distortionFactor(n) * n;
    }
    
    public float distortInverse(final float n) {
        float n2 = n / 0.9f;
        float n3 = n - this.distort(n2);
        float n4;
        float n5;
        float n6;
        for (n4 = n * 0.9f; Math.abs(n4 - n2) > 1.0E-4; n2 = n4, n4 -= n6 * n5, n3 = n5) {
            n5 = n - this.distort(n4);
            n6 = (n4 - n2) / (n5 - n3);
        }
        return n4;
    }
    
    public float distortionFactor(final float n) {
        float n2 = 1.0f;
        final float[] coefficients = this.coefficients;
        final int length = coefficients.length;
        int i = 0;
        float n3 = 1.0f;
        while (i < length) {
            final float n4 = coefficients[i];
            n2 *= n * n;
            n3 += n4 * n2;
            ++i;
        }
        return n3;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (o == this || (o instanceof Distortion && Arrays.equals(this.coefficients, ((Distortion)o).coefficients)));
    }
    
    @Deprecated
    public Distortion getApproximateInverseDistortion(final float n) {
        return this.getApproximateInverseDistortion(n, 2);
    }
    
    public Distortion getApproximateInverseDistortion(final float n, int i) {
        final double[][] array = new double[100][i];
        final double[] array2 = new double[100];
        for (int j = 0; j < 100; ++j) {
            final float n2 = (j + 1) * n / 100.0f;
            final double n3 = this.distort(n2);
            int k = 0;
            double n4 = n3;
            while (k < i) {
                n4 *= n3 * n3;
                array[j][k] = n4;
                ++k;
            }
            array2[j] = n2 - n3;
        }
        final double[] solveLeastSquares = solveLeastSquares(array, array2);
        final float[] coefficients = new float[solveLeastSquares.length];
        for (i = 0; i < solveLeastSquares.length; ++i) {
            coefficients[i] = (float)solveLeastSquares[i];
        }
        final Distortion distortion = new Distortion();
        distortion.setCoefficients(coefficients);
        return distortion;
    }
    
    public float[] getCoefficients() {
        return this.coefficients;
    }
    
    public void setCoefficients(float[] coefficients) {
        if (coefficients == null) {
            coefficients = new float[0];
        }
        else {
            coefficients = coefficients.clone();
        }
        this.coefficients = coefficients;
    }
    
    public float[] toProtobuf() {
        return this.coefficients.clone();
    }
    
    @Override
    public String toString() {
        final StringBuilder append = new StringBuilder().append("{\n").append("  coefficients: [");
        for (int i = 0; i < this.coefficients.length; ++i) {
            append.append(Float.toString(this.coefficients[i]));
            if (i < this.coefficients.length - 1) {
                append.append(", ");
            }
        }
        append.append("],\n}");
        return append.toString();
    }
}
