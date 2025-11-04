// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import android.view.animation.Interpolator;
import java.util.ArrayList;

class FloatKeyframeSet extends KeyframeSet
{
    private float deltaValue;
    private boolean firstTime;
    private float firstValue;
    private float lastValue;
    
    public FloatKeyframeSet(final Keyframe.FloatKeyframe... array) {
        super((Keyframe[])array);
        this.firstTime = true;
    }
    
    @Override
    public FloatKeyframeSet clone() {
        final ArrayList<Keyframe> mKeyframes = this.mKeyframes;
        final int size = this.mKeyframes.size();
        final Keyframe.FloatKeyframe[] array = new Keyframe.FloatKeyframe[size];
        for (int i = 0; i < size; ++i) {
            array[i] = (Keyframe.FloatKeyframe)mKeyframes.get(i).clone();
        }
        return new FloatKeyframeSet(array);
    }
    
    public float getFloatValue(float f) {
        int i = 1;
        if (this.mNumKeyframes != 2) {
            if (f <= 0.0f) {
                final Keyframe.FloatKeyframe floatKeyframe = this.mKeyframes.get(0);
                final Keyframe.FloatKeyframe floatKeyframe2 = this.mKeyframes.get(1);
                final float floatValue = floatKeyframe.getFloatValue();
                final float floatValue2 = floatKeyframe2.getFloatValue();
                final float fraction = floatKeyframe.getFraction();
                final float fraction2 = floatKeyframe2.getFraction();
                final Interpolator interpolator = floatKeyframe2.getInterpolator();
                if (interpolator != null) {
                    f = interpolator.getInterpolation(f);
                }
                f = (f - fraction) / (fraction2 - fraction);
                if (this.mEvaluator != null) {
                    f = this.mEvaluator.evaluate(f, floatValue, floatValue2).floatValue();
                }
                else {
                    f = f * (floatValue2 - floatValue) + floatValue;
                }
                return f;
            }
            if (f >= 1.0f) {
                final Keyframe.FloatKeyframe floatKeyframe3 = this.mKeyframes.get(this.mNumKeyframes - 2);
                final Keyframe.FloatKeyframe floatKeyframe4 = this.mKeyframes.get(this.mNumKeyframes - 1);
                final float floatValue3 = floatKeyframe3.getFloatValue();
                final float floatValue4 = floatKeyframe4.getFloatValue();
                final float fraction3 = floatKeyframe3.getFraction();
                final float fraction4 = floatKeyframe4.getFraction();
                final Interpolator interpolator2 = floatKeyframe4.getInterpolator();
                if (interpolator2 != null) {
                    f = interpolator2.getInterpolation(f);
                }
                f = (f - fraction3) / (fraction4 - fraction3);
                if (this.mEvaluator != null) {
                    f = this.mEvaluator.evaluate(f, floatValue3, floatValue4).floatValue();
                }
                else {
                    f = f * (floatValue4 - floatValue3) + floatValue3;
                }
                return f;
            }
            Keyframe keyframe = this.mKeyframes.get(0);
            while (i < this.mNumKeyframes) {
                final Keyframe.FloatKeyframe floatKeyframe5 = this.mKeyframes.get(i);
                if (f < floatKeyframe5.getFraction()) {
                    final Interpolator interpolator3 = floatKeyframe5.getInterpolator();
                    if (interpolator3 != null) {
                        f = interpolator3.getInterpolation(f);
                    }
                    final float n = (f - keyframe.getFraction()) / (floatKeyframe5.getFraction() - keyframe.getFraction());
                    f = ((Keyframe.FloatKeyframe)keyframe).getFloatValue();
                    final float floatValue5 = floatKeyframe5.getFloatValue();
                    if (this.mEvaluator != null) {
                        f = this.mEvaluator.evaluate(n, f, floatValue5).floatValue();
                    }
                    else {
                        f += (floatValue5 - f) * n;
                    }
                    return f;
                }
                ++i;
                keyframe = floatKeyframe5;
            }
            return ((Number)this.mKeyframes.get(this.mNumKeyframes - 1).getValue()).floatValue();
        }
        else {
            if (this.firstTime) {
                this.firstTime = false;
                this.firstValue = ((Keyframe.FloatKeyframe)this.mKeyframes.get(0)).getFloatValue();
                this.lastValue = ((Keyframe.FloatKeyframe)this.mKeyframes.get(1)).getFloatValue();
                this.deltaValue = this.lastValue - this.firstValue;
            }
            if (this.mInterpolator != null) {
                f = this.mInterpolator.getInterpolation(f);
            }
            if (this.mEvaluator != null) {
                return this.mEvaluator.evaluate(f, this.firstValue, this.lastValue).floatValue();
            }
            return this.firstValue + this.deltaValue * f;
        }
    }
    
    @Override
    public Object getValue(final float n) {
        return this.getFloatValue(n);
    }
}
