package com.nineoldandroids.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
class ReflectiveProperty<T, V> extends Property<T, V> {
    private static final String PREFIX_GET = "get";
    private static final String PREFIX_IS = "is";
    private static final String PREFIX_SET = "set";
    private Field mField;
    private Method mGetter;
    private Method mSetter;

    public ReflectiveProperty(Class<T> cls, Class<V> cls2, String str) {
        super(cls2, str);
        String str2 = Character.toUpperCase(str.charAt(0)) + str.substring(1);
        String str3 = PREFIX_GET + str2;
        try {
            this.mGetter = cls.getMethod(str3, null);
        } catch (NoSuchMethodException e) {
            try {
                this.mGetter = cls.getDeclaredMethod(str3, null);
                this.mGetter.setAccessible(true);
            } catch (NoSuchMethodException e2) {
                String str4 = PREFIX_IS + str2;
                try {
                    this.mGetter = cls.getMethod(str4, null);
                } catch (NoSuchMethodException e3) {
                    try {
                        this.mGetter = cls.getDeclaredMethod(str4, null);
                        this.mGetter.setAccessible(true);
                    } catch (NoSuchMethodException e4) {
                        try {
                            this.mField = cls.getField(str);
                            Class<?> type = this.mField.getType();
                            if (typesMatch(cls2, type)) {
                                return;
                            }
                            throw new NoSuchPropertyException("Underlying type (" + type + ") does not match Property type (" + cls2 + ")");
                        } catch (NoSuchFieldException e5) {
                            throw new NoSuchPropertyException("No accessor method or field found for property with name " + str);
                        }
                    }
                }
            }
        }
        Class<?> returnType = this.mGetter.getReturnType();
        if (!typesMatch(cls2, returnType)) {
            throw new NoSuchPropertyException("Underlying type (" + returnType + ") does not match Property type (" + cls2 + ")");
        }
        try {
            this.mSetter = cls.getDeclaredMethod(PREFIX_SET + str2, returnType);
            this.mSetter.setAccessible(true);
        } catch (NoSuchMethodException e6) {
        }
    }

    private boolean typesMatch(Class<V> cls, Class cls2) {
        if (cls2 == cls) {
            return true;
        }
        if (cls2.isPrimitive()) {
            return (cls2 == Float.TYPE && cls == Float.class) || (cls2 == Integer.TYPE && cls == Integer.class) || ((cls2 == Boolean.TYPE && cls == Boolean.class) || ((cls2 == Long.TYPE && cls == Long.class) || ((cls2 == Double.TYPE && cls == Double.class) || ((cls2 == Short.TYPE && cls == Short.class) || ((cls2 == Byte.TYPE && cls == Byte.class) || (cls2 == Character.TYPE && cls == Character.class))))));
        }
        return false;
    }

    @Override // com.nineoldandroids.util.Property
    public V get(T t) {
        if (this.mGetter == null) {
            if (this.mField == null) {
                throw new AssertionError();
            }
            try {
                return (V) this.mField.get(t);
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }
        try {
            return (V) this.mGetter.invoke(t, null);
        } catch (IllegalAccessException e2) {
            throw new AssertionError();
        } catch (InvocationTargetException e3) {
            throw new RuntimeException(e3.getCause());
        }
    }

    @Override // com.nineoldandroids.util.Property
    public boolean isReadOnly() {
        return this.mSetter == null && this.mField == null;
    }

    @Override // com.nineoldandroids.util.Property
    public void set(T t, V v) {
        if (this.mSetter == null) {
            if (this.mField == null) {
                throw new IllegalStateException("Property " + getName() + " is read-only");
            }
            try {
                this.mField.set(t, v);
                return;
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }
        try {
            this.mSetter.invoke(t, v);
        } catch (IllegalAccessException e2) {
            throw new AssertionError();
        } catch (InvocationTargetException e3) {
            throw new RuntimeException(e3.getCause());
        }
    }
}
