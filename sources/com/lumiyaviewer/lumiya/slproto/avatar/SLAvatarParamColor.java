// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import java.util.Arrays;

public class SLAvatarParamColor
{
    public static final class ColorOperation extends Enum
    {

        private static final ColorOperation $VALUES[];
        public static final ColorOperation Blend;
        public static final ColorOperation Default;
        public static final ColorOperation Multiply;

        public static ColorOperation valueOf(String s)
        {
            return (ColorOperation)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamColor$ColorOperation, s);
        }

        public static ColorOperation[] values()
        {
            return $VALUES;
        }

        static 
        {
            Default = new ColorOperation("Default", 0);
            Blend = new ColorOperation("Blend", 1);
            Multiply = new ColorOperation("Multiply", 2);
            $VALUES = (new ColorOperation[] {
                Default, Blend, Multiply
            });
        }

        private ColorOperation(String s, int i)
        {
            super(s, i);
        }
    }


    public final ColorOperation colorOperation;
    private final int colorValues[];

    SLAvatarParamColor(ColorOperation coloroperation, int ai[])
    {
        colorOperation = coloroperation;
        colorValues = ai;
    }

    public static int colorAdd(int i, int j)
    {
        int l = 255;
        int k1 = (i & 0xff) + (j & 0xff);
        int k = (i >> 8 & 0xff) + (j >> 8 & 0xff);
        int i1 = (j >> 16 & 0xff) + (i >> 16 & 0xff);
        int j1 = (i >> 24 & 0xff) + (j >> 24 & 0xff);
        i = k1;
        if (k1 > 255)
        {
            i = 255;
        }
        j = k;
        if (k > 255)
        {
            j = 255;
        }
        k = i1;
        if (i1 > 255)
        {
            k = 255;
        }
        if (j1 <= 255)
        {
            l = j1;
        }
        return l << 24 | k << 16 | j << 8 | i;
    }

    public static int colorLerp(int i, int j, float f)
    {
        int l = 0;
        float f1 = i & 0xff;
        float f2 = i >> 8 & 0xff;
        float f3 = i >> 16 & 0xff;
        float f4 = i >> 24 & 0xff;
        float f5 = j & 0xff;
        float f6 = j >> 8 & 0xff;
        float f7 = j >> 16 & 0xff;
        float f8 = j >> 24 & 0xff;
        float f9 = 1.0F - f;
        j = Math.round(f1 * f9 + f5 * f);
        int k = Math.round(f9 * f2 + f * f6);
        int j1 = Math.round(f9 * f3 + f * f7);
        int i1 = Math.round(f9 * f4 + f * f8);
        if (j < 0)
        {
            i = 0;
        } else
        {
            i = j;
            if (j > 255)
            {
                i = 255;
            }
        }
        if (k < 0)
        {
            j = 0;
        } else
        {
            j = k;
            if (k > 255)
            {
                j = 255;
            }
        }
        if (j1 < 0)
        {
            k = 0;
        } else
        {
            k = j1;
            if (j1 > 255)
            {
                k = 255;
            }
        }
        if (i1 >= 0)
        {
            if (i1 > 255)
            {
                l = 255;
            } else
            {
                l = i1;
            }
        }
        return l << 24 | k << 16 | j << 8 | i;
    }

    public static int colorMult(int i, int j)
    {
        int l = 255;
        int k1 = ((i & 0xff) * (j & 0xff)) / 255;
        int k = ((i >> 8 & 0xff) * (j >> 8 & 0xff)) / 255;
        int i1 = ((i >> 16 & 0xff) * (j >> 16 & 0xff)) / 255;
        int j1 = ((i >> 24 & 0xff) * (j >> 24 & 0xff)) / 255;
        i = k1;
        if (k1 > 255)
        {
            i = 255;
        }
        j = k;
        if (k > 255)
        {
            j = 255;
        }
        k = i1;
        if (i1 > 255)
        {
            k = 255;
        }
        if (j1 <= 255)
        {
            l = j1;
        }
        return l << 24 | k << 16 | j << 8 | i;
    }

    public boolean equals(Object obj)
    {
        boolean flag = false;
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        obj = (SLAvatarParamColor)obj;
        if (colorOperation == ((SLAvatarParamColor) (obj)).colorOperation)
        {
            flag = Arrays.equals(colorValues, ((SLAvatarParamColor) (obj)).colorValues);
        }
        return flag;
    }

    public int getColor(float f)
    {
        if (colorValues.length == 0)
        {
            return 0;
        }
        if (colorValues.length == 1)
        {
            return colorValues[0];
        }
        int i = colorValues.length - 1;
        f = (float)i * f;
        int j = (int)f;
        if (j >= i)
        {
            return colorValues[i];
        } else
        {
            float f1 = j;
            return colorLerp(colorValues[j], colorValues[j + 1], f - f1);
        }
    }

    public int hashCode()
    {
        return colorOperation.hashCode() * 31 + Arrays.hashCode(colorValues);
    }
}
