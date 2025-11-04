// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import android.view.animation.Interpolator;

public abstract class Keyframe implements Cloneable
{
    float mFraction;
    boolean mHasValue;
    private Interpolator mInterpolator;
    Class mValueType;
    
    public Keyframe() {
        this.mInterpolator = null;
        this.mHasValue = false;
    }
    
    public static Keyframe ofFloat(final float n) {
        return new FloatKeyframe(n);
    }
    
    public static Keyframe ofFloat(final float n, final float n2) {
        return new FloatKeyframe(n, n2);
    }
    
    public static Keyframe ofInt(final float n) {
        return new IntKeyframe(n);
    }
    
    public static Keyframe ofInt(final float n, final int n2) {
        return new IntKeyframe(n, n2);
    }
    
    public static Keyframe ofObject(final float n) {
        return new ObjectKeyframe(n, null);
    }
    
    public static Keyframe ofObject(final float n, final Object o) {
        return new ObjectKeyframe(n, o);
    }
    
    public abstract Keyframe clone();
    
    public float getFraction() {
        return this.mFraction;
    }
    
    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }
    
    public Class getType() {
        return this.mValueType;
    }
    
    public abstract Object getValue();
    
    public boolean hasValue() {
        return this.mHasValue;
    }
    
    public void setFraction(final float mFraction) {
        this.mFraction = mFraction;
    }
    
    public void setInterpolator(final Interpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
    }
    
    public abstract void setValue(final Object p0);
    
    static class FloatKeyframe extends Keyframe
    {
        float mValue;
        
        FloatKeyframe(final float mFraction) {
            this.mFraction = mFraction;
            this.mValueType = Float.TYPE;
        }
        
        FloatKeyframe(final float mFraction, final float mValue) {
            this.mFraction = mFraction;
            this.mValue = mValue;
            this.mValueType = Float.TYPE;
            this.mHasValue = true;
        }
        
        @Override
        public FloatKeyframe clone() {
            final FloatKeyframe floatKeyframe = new FloatKeyframe(this.getFraction(), this.mValue);
            floatKeyframe.setInterpolator(this.getInterpolator());
            return floatKeyframe;
        }
        
        public float getFloatValue() {
            return this.mValue;
        }
        
        @Override
        public Object getValue() {
            return this.mValue;
        }
        
        @Override
        public void setValue(final Object o) {
            if (o != null && o.getClass() == Float.class) {
                this.mValue = (float)o;
                this.mHasValue = true;
            }
        }
    }
    
    static class IntKeyframe extends Keyframe
    {
        int mValue;
        
        IntKeyframe(final float mFraction) {
            this.mFraction = mFraction;
            this.mValueType = Integer.TYPE;
        }
        
        IntKeyframe(final float mFraction, final int mValue) {
            this.mFraction = mFraction;
            this.mValue = mValue;
            this.mValueType = Integer.TYPE;
            this.mHasValue = true;
        }
        
        @Override
        public IntKeyframe clone() {
            final IntKeyframe intKeyframe = new IntKeyframe(this.getFraction(), this.mValue);
            intKeyframe.setInterpolator(this.getInterpolator());
            return intKeyframe;
        }
        
        public int getIntValue() {
            return this.mValue;
        }
        
        @Override
        public Object getValue() {
            return this.mValue;
        }
        
        @Override
        public void setValue(final Object o) {
            if (o != null && o.getClass() == Integer.class) {
                this.mValue = (int)o;
                this.mHasValue = true;
            }
        }
    }
    
    static class ObjectKeyframe extends Keyframe
    {
        Object mValue;
        
        ObjectKeyframe(final float mFraction, final Object mValue) {
            boolean mHasValue = false;
            this.mFraction = mFraction;
            this.mValue = mValue;
            if (mValue != null) {
                mHasValue = true;
            }
            Class<?> class1;
            if (!(this.mHasValue = mHasValue)) {
                class1 = Object.class;
            }
            else {
                class1 = mValue.getClass();
            }
            this.mValueType = class1;
        }
        
        @Override
        public ObjectKeyframe clone() {
            final ObjectKeyframe objectKeyframe = new ObjectKeyframe(this.getFraction(), this.mValue);
            objectKeyframe.setInterpolator(this.getInterpolator());
            return objectKeyframe;
        }
        
        @Override
        public Object getValue() {
            return this.mValue;
        }
        
        @Override
        public void setValue(final Object mValue) {
            this.mValue = mValue;
            this.mHasValue = (mValue != null);
        }
    }
}
