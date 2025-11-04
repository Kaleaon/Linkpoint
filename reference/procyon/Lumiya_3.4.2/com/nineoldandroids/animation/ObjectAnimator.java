// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import android.view.View;
import com.nineoldandroids.view.animation.AnimatorProxy;
import java.util.HashMap;
import com.nineoldandroids.util.Property;
import java.util.Map;

public final class ObjectAnimator extends ValueAnimator
{
    private static final boolean DBG = false;
    private static final Map<String, Property> PROXY_PROPERTIES;
    private Property mProperty;
    private String mPropertyName;
    private Object mTarget;
    
    static {
        (PROXY_PROPERTIES = new HashMap<String, Property>()).put("alpha", PreHoneycombCompat.ALPHA);
        ObjectAnimator.PROXY_PROPERTIES.put("pivotX", PreHoneycombCompat.PIVOT_X);
        ObjectAnimator.PROXY_PROPERTIES.put("pivotY", PreHoneycombCompat.PIVOT_Y);
        ObjectAnimator.PROXY_PROPERTIES.put("translationX", PreHoneycombCompat.TRANSLATION_X);
        ObjectAnimator.PROXY_PROPERTIES.put("translationY", PreHoneycombCompat.TRANSLATION_Y);
        ObjectAnimator.PROXY_PROPERTIES.put("rotation", PreHoneycombCompat.ROTATION);
        ObjectAnimator.PROXY_PROPERTIES.put("rotationX", PreHoneycombCompat.ROTATION_X);
        ObjectAnimator.PROXY_PROPERTIES.put("rotationY", PreHoneycombCompat.ROTATION_Y);
        ObjectAnimator.PROXY_PROPERTIES.put("scaleX", PreHoneycombCompat.SCALE_X);
        ObjectAnimator.PROXY_PROPERTIES.put("scaleY", PreHoneycombCompat.SCALE_Y);
        ObjectAnimator.PROXY_PROPERTIES.put("scrollX", PreHoneycombCompat.SCROLL_X);
        ObjectAnimator.PROXY_PROPERTIES.put("scrollY", PreHoneycombCompat.SCROLL_Y);
        ObjectAnimator.PROXY_PROPERTIES.put("x", PreHoneycombCompat.X);
        ObjectAnimator.PROXY_PROPERTIES.put("y", PreHoneycombCompat.Y);
    }
    
    public ObjectAnimator() {
    }
    
    private <T> ObjectAnimator(final T mTarget, final Property<T, ?> property) {
        this.mTarget = mTarget;
        this.setProperty(property);
    }
    
    private ObjectAnimator(final Object mTarget, final String propertyName) {
        this.mTarget = mTarget;
        this.setPropertyName(propertyName);
    }
    
    public static <T> ObjectAnimator ofFloat(final T t, final Property<T, Float> property, final float... floatValues) {
        final ObjectAnimator objectAnimator = new ObjectAnimator((T)t, (Property<T, ?>)property);
        objectAnimator.setFloatValues(floatValues);
        return objectAnimator;
    }
    
    public static ObjectAnimator ofFloat(final Object o, final String s, final float... floatValues) {
        final ObjectAnimator objectAnimator = new ObjectAnimator(o, s);
        objectAnimator.setFloatValues(floatValues);
        return objectAnimator;
    }
    
    public static <T> ObjectAnimator ofInt(final T t, final Property<T, Integer> property, final int... intValues) {
        final ObjectAnimator objectAnimator = new ObjectAnimator((T)t, (Property<T, ?>)property);
        objectAnimator.setIntValues(intValues);
        return objectAnimator;
    }
    
    public static ObjectAnimator ofInt(final Object o, final String s, final int... intValues) {
        final ObjectAnimator objectAnimator = new ObjectAnimator(o, s);
        objectAnimator.setIntValues(intValues);
        return objectAnimator;
    }
    
    public static <T, V> ObjectAnimator ofObject(final T t, final Property<T, V> property, final TypeEvaluator<V> evaluator, final V... objectValues) {
        final ObjectAnimator objectAnimator = new ObjectAnimator((T)t, (Property<T, ?>)property);
        objectAnimator.setObjectValues((Object[])objectValues);
        objectAnimator.setEvaluator(evaluator);
        return objectAnimator;
    }
    
    public static ObjectAnimator ofObject(final Object o, final String s, final TypeEvaluator evaluator, final Object... objectValues) {
        final ObjectAnimator objectAnimator = new ObjectAnimator(o, s);
        objectAnimator.setObjectValues(objectValues);
        objectAnimator.setEvaluator(evaluator);
        return objectAnimator;
    }
    
    public static ObjectAnimator ofPropertyValuesHolder(final Object mTarget, final PropertyValuesHolder... values) {
        final ObjectAnimator objectAnimator = new ObjectAnimator();
        objectAnimator.mTarget = mTarget;
        objectAnimator.setValues(values);
        return objectAnimator;
    }
    
    @Override
    void animateValue(final float n) {
        super.animateValue(n);
        for (int length = this.mValues.length, i = 0; i < length; ++i) {
            this.mValues[i].setAnimatedValue(this.mTarget);
        }
    }
    
    @Override
    public ObjectAnimator clone() {
        return (ObjectAnimator)super.clone();
    }
    
    public String getPropertyName() {
        return this.mPropertyName;
    }
    
    public Object getTarget() {
        return this.mTarget;
    }
    
    @Override
    void initAnimation() {
        if (!this.mInitialized) {
            if (this.mProperty == null && AnimatorProxy.NEEDS_PROXY && this.mTarget instanceof View && ObjectAnimator.PROXY_PROPERTIES.containsKey(this.mPropertyName)) {
                this.setProperty(ObjectAnimator.PROXY_PROPERTIES.get(this.mPropertyName));
            }
            for (int length = this.mValues.length, i = 0; i < length; ++i) {
                this.mValues[i].setupSetterAndGetter(this.mTarget);
            }
            super.initAnimation();
        }
    }
    
    @Override
    public ObjectAnimator setDuration(final long duration) {
        super.setDuration(duration);
        return this;
    }
    
    @Override
    public void setFloatValues(final float... floatValues) {
        if (this.mValues != null && this.mValues.length != 0) {
            super.setFloatValues(floatValues);
        }
        else if (this.mProperty == null) {
            this.setValues(PropertyValuesHolder.ofFloat(this.mPropertyName, floatValues));
        }
        else {
            this.setValues(PropertyValuesHolder.ofFloat(this.mProperty, floatValues));
        }
    }
    
    @Override
    public void setIntValues(final int... intValues) {
        if (this.mValues != null && this.mValues.length != 0) {
            super.setIntValues(intValues);
        }
        else if (this.mProperty == null) {
            this.setValues(PropertyValuesHolder.ofInt(this.mPropertyName, intValues));
        }
        else {
            this.setValues(PropertyValuesHolder.ofInt(this.mProperty, intValues));
        }
    }
    
    @Override
    public void setObjectValues(final Object... objectValues) {
        if (this.mValues != null && this.mValues.length != 0) {
            super.setObjectValues(objectValues);
        }
        else if (this.mProperty == null) {
            this.setValues(PropertyValuesHolder.ofObject(this.mPropertyName, null, objectValues));
        }
        else {
            this.setValues(PropertyValuesHolder.ofObject(this.mProperty, null, objectValues));
        }
    }
    
    public void setProperty(final Property property) {
        if (this.mValues != null) {
            final PropertyValuesHolder value = this.mValues[0];
            final String propertyName = value.getPropertyName();
            value.setProperty(property);
            this.mValuesMap.remove(propertyName);
            this.mValuesMap.put(this.mPropertyName, value);
        }
        if (this.mProperty != null) {
            this.mPropertyName = property.getName();
        }
        this.mProperty = property;
        this.mInitialized = false;
    }
    
    public void setPropertyName(final String mPropertyName) {
        if (this.mValues != null) {
            final PropertyValuesHolder value = this.mValues[0];
            final String propertyName = value.getPropertyName();
            value.setPropertyName(mPropertyName);
            this.mValuesMap.remove(propertyName);
            this.mValuesMap.put(mPropertyName, value);
        }
        this.mPropertyName = mPropertyName;
        this.mInitialized = false;
    }
    
    @Override
    public void setTarget(final Object mTarget) {
        if (this.mTarget != mTarget) {
            final Object mTarget2 = this.mTarget;
            this.mTarget = mTarget;
            if (mTarget2 != null && mTarget != null && mTarget2.getClass() == mTarget.getClass()) {
                return;
            }
            this.mInitialized = false;
        }
    }
    
    @Override
    public void setupEndValues() {
        this.initAnimation();
        for (int length = this.mValues.length, i = 0; i < length; ++i) {
            this.mValues[i].setupEndValue(this.mTarget);
        }
    }
    
    @Override
    public void setupStartValues() {
        this.initAnimation();
        for (int length = this.mValues.length, i = 0; i < length; ++i) {
            this.mValues[i].setupStartValue(this.mTarget);
        }
    }
    
    @Override
    public void start() {
        super.start();
    }
    
    @Override
    public String toString() {
        String str = "ObjectAnimator@" + Integer.toHexString(this.hashCode()) + ", target " + this.mTarget;
        String s;
        if (this.mValues == null) {
            s = str;
        }
        else {
            int n = 0;
            while (true) {
                s = str;
                if (n >= this.mValues.length) {
                    break;
                }
                str = str + "\n    " + this.mValues[n].toString();
                ++n;
            }
        }
        return s;
    }
}
