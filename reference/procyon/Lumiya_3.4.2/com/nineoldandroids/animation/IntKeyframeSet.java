// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import android.view.animation.Interpolator;
import java.util.ArrayList;

class IntKeyframeSet extends KeyframeSet
{
    private int deltaValue;
    private boolean firstTime;
    private int firstValue;
    private int lastValue;
    
    public IntKeyframeSet(final Keyframe.IntKeyframe... array) {
        super((Keyframe[])array);
        this.firstTime = true;
    }
    
    @Override
    public IntKeyframeSet clone() {
        final ArrayList<Keyframe> mKeyframes = this.mKeyframes;
        final int size = this.mKeyframes.size();
        final Keyframe.IntKeyframe[] array = new Keyframe.IntKeyframe[size];
        for (int i = 0; i < size; ++i) {
            array[i] = (Keyframe.IntKeyframe)mKeyframes.get(i).clone();
        }
        return new IntKeyframeSet(array);
    }
    
    public int getIntValue(float n) {
        int i = 1;
        if (this.mNumKeyframes != 2) {
            if (n <= 0.0f) {
                final Keyframe.IntKeyframe intKeyframe = this.mKeyframes.get(0);
                final Keyframe.IntKeyframe intKeyframe2 = this.mKeyframes.get(1);
                final int intValue = intKeyframe.getIntValue();
                final int intValue2 = intKeyframe2.getIntValue();
                final float fraction = intKeyframe.getFraction();
                final float fraction2 = intKeyframe2.getFraction();
                final Interpolator interpolator = intKeyframe2.getInterpolator();
                if (interpolator != null) {
                    n = interpolator.getInterpolation(n);
                }
                n = (n - fraction) / (fraction2 - fraction);
                int intValue3;
                if (this.mEvaluator != null) {
                    intValue3 = this.mEvaluator.evaluate(n, intValue, intValue2).intValue();
                }
                else {
                    intValue3 = (int)(n * (intValue2 - intValue)) + intValue;
                }
                return intValue3;
            }
            if (n >= 1.0f) {
                final Keyframe.IntKeyframe intKeyframe3 = this.mKeyframes.get(this.mNumKeyframes - 2);
                final Keyframe.IntKeyframe intKeyframe4 = this.mKeyframes.get(this.mNumKeyframes - 1);
                final int intValue4 = intKeyframe3.getIntValue();
                final int intValue5 = intKeyframe4.getIntValue();
                final float fraction3 = intKeyframe3.getFraction();
                final float fraction4 = intKeyframe4.getFraction();
                final Interpolator interpolator2 = intKeyframe4.getInterpolator();
                if (interpolator2 != null) {
                    n = interpolator2.getInterpolation(n);
                }
                n = (n - fraction3) / (fraction4 - fraction3);
                int intValue6;
                if (this.mEvaluator != null) {
                    intValue6 = this.mEvaluator.evaluate(n, intValue4, intValue5).intValue();
                }
                else {
                    intValue6 = (int)(n * (intValue5 - intValue4)) + intValue4;
                }
                return intValue6;
            }
            Keyframe keyframe = this.mKeyframes.get(0);
            while (i < this.mNumKeyframes) {
                final Keyframe.IntKeyframe intKeyframe5 = this.mKeyframes.get(i);
                if (n < intKeyframe5.getFraction()) {
                    final Interpolator interpolator3 = intKeyframe5.getInterpolator();
                    if (interpolator3 != null) {
                        n = interpolator3.getInterpolation(n);
                    }
                    n = (n - keyframe.getFraction()) / (intKeyframe5.getFraction() - keyframe.getFraction());
                    final int intValue7 = ((Keyframe.IntKeyframe)keyframe).getIntValue();
                    final int intValue8 = intKeyframe5.getIntValue();
                    int intValue9;
                    if (this.mEvaluator != null) {
                        intValue9 = this.mEvaluator.evaluate(n, intValue7, intValue8).intValue();
                    }
                    else {
                        intValue9 = (int)((intValue8 - intValue7) * n) + intValue7;
                    }
                    return intValue9;
                }
                ++i;
                keyframe = intKeyframe5;
            }
            return ((Number)this.mKeyframes.get(this.mNumKeyframes - 1).getValue()).intValue();
        }
        else {
            if (this.firstTime) {
                this.firstTime = false;
                this.firstValue = ((Keyframe.IntKeyframe)this.mKeyframes.get(0)).getIntValue();
                this.lastValue = ((Keyframe.IntKeyframe)this.mKeyframes.get(1)).getIntValue();
                this.deltaValue = this.lastValue - this.firstValue;
            }
            if (this.mInterpolator != null) {
                n = this.mInterpolator.getInterpolation(n);
            }
            if (this.mEvaluator != null) {
                return this.mEvaluator.evaluate(n, this.firstValue, this.lastValue).intValue();
            }
            return this.firstValue + (int)(this.deltaValue * n);
        }
    }
    
    @Override
    public Object getValue(final float n) {
        return this.getIntValue(n);
    }
}
