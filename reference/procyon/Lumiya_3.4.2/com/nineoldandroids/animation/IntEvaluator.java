// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

public class IntEvaluator implements TypeEvaluator<Integer>
{
    @Override
    public Integer evaluate(final float n, final Integer n2, final Integer n3) {
        final int intValue = n2;
        return (int)((n3 - intValue) * n + intValue);
    }
}
