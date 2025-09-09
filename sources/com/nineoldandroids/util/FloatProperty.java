package com.nineoldandroids.util;

public abstract class FloatProperty<T> extends Property<T, Float> {
    public FloatProperty(String str) {
        super(Float.class, str);
    }

    public final void set(T t, Float f) {
        setValue(t, f.floatValue());
    }

    public abstract void setValue(T t, float f);
}
