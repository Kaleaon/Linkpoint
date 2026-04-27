package com.lumiyaviewer.lumiya.slproto.types;

import android.support.v4.view.InputDeviceCompat;

public final class LLTersePacking {
    public static final float U16_to_float(int i, float f, float f2) {
        return int_dequantize(1.5259022E-5f, i, f, f2);
    }

    public static final float U8_to_float(int i, float f, float f2) {
        return int_dequantize(0.003921569f, i, f, f2);
    }

    public static final int getSignedByte(int i) {
        int i2 = i & 255;
        return i2 >= 128 ? i2 + InputDeviceCompat.SOURCE_ANY : i2;
    }

    private static final float int_dequantize(float f, int i, float f2, float f3) {
        float f4 = f3 - f2;
        float f5 = (((float) i) * f * f4) + f2;
        if (Math.abs(f5) < f4 * f) {
            return 0.0f;
        }
        return f5;
    }
}
