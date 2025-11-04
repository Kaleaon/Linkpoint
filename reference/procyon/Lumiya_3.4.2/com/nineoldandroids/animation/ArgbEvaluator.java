// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

public class ArgbEvaluator implements TypeEvaluator
{
    @Override
    public Object evaluate(final float n, final Object o, final Object o2) {
        final int intValue = (int)o;
        final int n2 = intValue >> 24;
        final int n3 = intValue >> 16 & 0xFF;
        final int n4 = intValue >> 8 & 0xFF;
        final int n5 = intValue & 0xFF;
        final int intValue2 = (int)o2;
        return n5 + (int)(((intValue2 & 0xFF) - n5) * n) | (n2 + (int)(((intValue2 >> 24) - n2) * n) << 24 | n3 + (int)(((intValue2 >> 16 & 0xFF) - n3) * n) << 16 | (int)(((intValue2 >> 8 & 0xFF) - n4) * n) + n4 << 8);
    }
}
