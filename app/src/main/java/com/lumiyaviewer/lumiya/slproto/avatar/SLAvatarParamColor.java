package com.lumiyaviewer.lumiya.slproto.avatar;

import java.util.Arrays;
import javax.annotation.Nonnull;

public class SLAvatarParamColor {
    @Nonnull
    public final ColorOperation colorOperation;
    @Nonnull
    private final int[] colorValues;

    public enum ColorOperation {
        Default,
        Blend,
        Multiply
    }

    SLAvatarParamColor(@Nonnull ColorOperation colorOperation2, @Nonnull int[] iArr) {
        this.colorOperation = colorOperation2;
        this.colorValues = iArr;
    }

    public static int colorAdd(int i, int i2) {
        int i3 = 255;
        int i4 = (i & 255) + (i2 & 255);
        int i5 = ((i >> 8) & 255) + ((i2 >> 8) & 255);
        int i6 = ((i2 >> 16) & 255) + ((i >> 16) & 255);
        int i7 = ((i >> 24) & 255) + ((i2 >> 24) & 255);
        if (i4 > 255) {
            i4 = 255;
        }
        if (i5 > 255) {
            i5 = 255;
        }
        if (i6 > 255) {
            i6 = 255;
        }
        if (i7 <= 255) {
            i3 = i7;
        }
        return (i3 << 24) | (i6 << 16) | (i5 << 8) | i4;
    }

    public static int colorLerp(int i, int i2, float f) {
        int i3 = 0;
        float f2 = 1.0f - f;
        int round = Math.round((((float) (i & 255)) * f2) + (((float) (i2 & 255)) * f));
        int round2 = Math.round((f2 * ((float) ((i >> 8) & 255))) + (f * ((float) ((i2 >> 8) & 255))));
        int round3 = Math.round((f2 * ((float) ((i >> 16) & 255))) + (f * ((float) ((i2 >> 16) & 255))));
        int round4 = Math.round((f2 * ((float) ((i >> 24) & 255))) + (f * ((float) ((i2 >> 24) & 255))));
        if (round < 0) {
            round = 0;
        } else if (round > 255) {
            round = 255;
        }
        if (round2 < 0) {
            round2 = 0;
        } else if (round2 > 255) {
            round2 = 255;
        }
        if (round3 < 0) {
            round3 = 0;
        } else if (round3 > 255) {
            round3 = 255;
        }
        if (round4 >= 0) {
            i3 = round4 > 255 ? 255 : round4;
        }
        return (i3 << 24) | (round3 << 16) | (round2 << 8) | round;
    }

    public static int colorMult(int i, int i2) {
        int i3 = 255;
        int i4 = ((i & 255) * (i2 & 255)) / 255;
        int i5 = (((i >> 8) & 255) * ((i2 >> 8) & 255)) / 255;
        int i6 = (((i >> 16) & 255) * ((i2 >> 16) & 255)) / 255;
        int i7 = (((i >> 24) & 255) * ((i2 >> 24) & 255)) / 255;
        if (i4 > 255) {
            i4 = 255;
        }
        if (i5 > 255) {
            i5 = 255;
        }
        if (i6 > 255) {
            i6 = 255;
        }
        if (i7 <= 255) {
            i3 = i7;
        }
        return (i3 << 24) | (i6 << 16) | (i5 << 8) | i4;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SLAvatarParamColor sLAvatarParamColor = (SLAvatarParamColor) obj;
        if (this.colorOperation == sLAvatarParamColor.colorOperation) {
            return Arrays.equals(this.colorValues, sLAvatarParamColor.colorValues);
        }
        return false;
    }

    public int getColor(float f) {
        if (this.colorValues.length == 0) {
            return 0;
        }
        if (this.colorValues.length == 1) {
            return this.colorValues[0];
        }
        int length = this.colorValues.length - 1;
        float f2 = ((float) length) * f;
        int i = (int) f2;
        int i2 = i + 1;
        if (i >= length) {
            return this.colorValues[length];
        }
        return colorLerp(this.colorValues[i], this.colorValues[i2], f2 - ((float) i));
    }

    public int hashCode() {
        return (this.colorOperation.hashCode() * 31) + Arrays.hashCode(this.colorValues);
    }
}
