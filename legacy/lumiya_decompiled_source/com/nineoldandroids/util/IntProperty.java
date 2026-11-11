package com.nineoldandroids.util;
/* loaded from: classes.dex */
public abstract class IntProperty<T> extends Property<T, Integer> {
    public IntProperty(String str) {
        super(Integer.class, str);
    }

    /* renamed from: set  reason: avoid collision after fix types in other method */
    public final void set2(T t, Integer num) {
        set2((IntProperty<T>) t, Integer.valueOf(num.intValue()));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.nineoldandroids.util.Property
    public /* bridge */ /* synthetic */ void set(Object obj, Integer num) {
        set2((IntProperty<T>) obj, num);
    }

    public abstract void setValue(T t, int i);
}
