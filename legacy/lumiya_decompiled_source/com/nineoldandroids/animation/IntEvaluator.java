package com.nineoldandroids.animation;
/* loaded from: classes.dex */
public class IntEvaluator implements TypeEvaluator<Integer> {
    @Override // com.nineoldandroids.animation.TypeEvaluator
    public Integer evaluate(float f, Integer num, Integer num2) {
        int intValue = num.intValue();
        return Integer.valueOf((int) (((num2.intValue() - intValue) * f) + intValue));
    }
}
