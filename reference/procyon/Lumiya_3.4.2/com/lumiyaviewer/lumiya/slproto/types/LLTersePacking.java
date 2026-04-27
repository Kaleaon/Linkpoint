// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

public final class LLTersePacking
{
    public static final float U16_to_float(final int n, final float n2, final float n3) {
        return int_dequantize(1.5259022E-5f, n, n2, n3);
    }
    
    public static final float U8_to_float(final int n, final float n2, final float n3) {
        return int_dequantize(0.003921569f, n, n2, n3);
    }
    
    public static final int getSignedByte(int n) {
        final int n2 = n &= 0xFF;
        if (n2 >= 128) {
            n = n2 - 256;
        }
        return n;
    }
    
    private static final float int_dequantize(final float n, final int n2, float n3, float a) {
        final float n4 = (float)n2;
        final float n5 = a - n3;
        a = (n3 += n4 * n * n5);
        if (Math.abs(a) < n5 * n) {
            n3 = 0.0f;
        }
        return n3;
    }
}
