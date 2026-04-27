package com.nineoldandroids.animation;

public class IntEvaluator implements TypeEvaluator<Integer> {
    public Integer evaluate(float f, Integer num, Integer num2) {
        int intValue = num.intValue();
        return Integer.valueOf((int) ((((float) (num2.intValue() - intValue)) * f) + ((float) intValue)));
    }
}
