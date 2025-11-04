// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import java.util.Arrays;
import javax.annotation.Nonnull;

public class SLAvatarParamColor
{
    @Nonnull
    public final ColorOperation colorOperation;
    @Nonnull
    private final int[] colorValues;
    
    SLAvatarParamColor(@Nonnull final ColorOperation colorOperation, @Nonnull final int[] colorValues) {
        this.colorOperation = colorOperation;
        this.colorValues = colorValues;
    }
    
    public static int colorAdd(int n, int n2) {
        int n3 = 255;
        final int n4 = (n & 0xFF) + (n2 & 0xFF);
        final int n5 = (n >> 8 & 0xFF) + (n2 >> 8 & 0xFF);
        final int n6 = (n2 >> 16 & 0xFF) + (n >> 16 & 0xFF);
        final int n7 = (n >> 24 & 0xFF) + (n2 >> 24 & 0xFF);
        n = n4;
        if (n4 > 255) {
            n = 255;
        }
        if ((n2 = n5) > 255) {
            n2 = 255;
        }
        int n8;
        if ((n8 = n6) > 255) {
            n8 = 255;
        }
        if (n7 <= 255) {
            n3 = n7;
        }
        return n3 << 24 | n8 << 16 | n2 << 8 | n;
    }
    
    public static int colorLerp(int n, int round, final float n2) {
        int n3 = 0;
        final float n4 = (float)(n & 0xFF);
        final float n5 = (float)(n >> 8 & 0xFF);
        final float n6 = (float)(n >> 16 & 0xFF);
        final float n7 = (float)(n >> 24 & 0xFF);
        final float n8 = (float)(round & 0xFF);
        final float n9 = (float)(round >> 8 & 0xFF);
        final float n10 = (float)(round >> 16 & 0xFF);
        final float n11 = (float)(round >> 24 & 0xFF);
        final float n12 = 1.0f - n2;
        round = Math.round(n4 * n12 + n8 * n2);
        final int round2 = Math.round(n12 * n5 + n2 * n9);
        final int round3 = Math.round(n12 * n6 + n2 * n10);
        final int round4 = Math.round(n12 * n7 + n2 * n11);
        if (round < 0) {
            n = 0;
        }
        else if ((n = round) > 255) {
            n = 255;
        }
        if (round2 < 0) {
            round = 0;
        }
        else if ((round = round2) > 255) {
            round = 255;
        }
        int n13;
        if (round3 < 0) {
            n13 = 0;
        }
        else if ((n13 = round3) > 255) {
            n13 = 255;
        }
        if (round4 >= 0) {
            if (round4 > 255) {
                n3 = 255;
            }
            else {
                n3 = round4;
            }
        }
        return n3 << 24 | n13 << 16 | round << 8 | n;
    }
    
    public static int colorMult(int n, int n2) {
        int n3 = 255;
        final int n4 = (n & 0xFF) * (n2 & 0xFF) / 255;
        final int n5 = (n >> 8 & 0xFF) * (n2 >> 8 & 0xFF) / 255;
        final int n6 = (n >> 16 & 0xFF) * (n2 >> 16 & 0xFF) / 255;
        final int n7 = (n >> 24 & 0xFF) * (n2 >> 24 & 0xFF) / 255;
        n = n4;
        if (n4 > 255) {
            n = 255;
        }
        if ((n2 = n5) > 255) {
            n2 = 255;
        }
        int n8;
        if ((n8 = n6) > 255) {
            n8 = 255;
        }
        if (n7 <= 255) {
            n3 = n7;
        }
        return n3 << 24 | n8 << 16 | n2 << 8 | n;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = false;
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SLAvatarParamColor slAvatarParamColor = (SLAvatarParamColor)o;
        if (this.colorOperation == slAvatarParamColor.colorOperation) {
            equals = Arrays.equals(this.colorValues, slAvatarParamColor.colorValues);
        }
        return equals;
    }
    
    public int getColor(float n) {
        if (this.colorValues.length == 0) {
            return 0;
        }
        if (this.colorValues.length == 1) {
            return this.colorValues[0];
        }
        final int n2 = this.colorValues.length - 1;
        final float n3 = n2 * n;
        final int n4 = (int)n3;
        if (n4 >= n2) {
            return this.colorValues[n2];
        }
        n = (float)n4;
        return colorLerp(this.colorValues[n4], this.colorValues[n4 + 1], n3 - n);
    }
    
    @Override
    public int hashCode() {
        return this.colorOperation.hashCode() * 31 + Arrays.hashCode(this.colorValues);
    }
    
    public enum ColorOperation
    {
        Blend("Blend", 1), 
        Default("Default", 0), 
        Multiply("Multiply", 2);
        
        private ColorOperation(final String name, final int ordinal) {
        }
    }
}
