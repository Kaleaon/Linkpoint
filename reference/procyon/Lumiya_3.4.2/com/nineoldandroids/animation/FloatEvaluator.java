// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

public class FloatEvaluator implements TypeEvaluator<Number>
{
    @Override
    public Float evaluate(final float n, final Number n2, final Number n3) {
        final float floatValue = n2.floatValue();
        return floatValue + (n3.floatValue() - floatValue) * n;
    }
}
