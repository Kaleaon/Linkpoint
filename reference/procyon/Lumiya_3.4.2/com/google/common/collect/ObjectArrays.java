// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collection;
import java.lang.reflect.Array;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class ObjectArrays
{
    static final Object[] EMPTY_ARRAY;
    
    static {
        EMPTY_ARRAY = new Object[0];
    }
    
    private ObjectArrays() {
    }
    
    static <T> T[] arraysCopyOf(final T[] array, final int b) {
        final Object[] array2 = newArray((Object[])array, b);
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, b));
        return (T[])array2;
    }
    
    static Object checkElementNotNull(final Object o, final int i) {
        if (o != null) {
            return o;
        }
        throw new NullPointerException("at index " + i);
    }
    
    static Object[] checkElementsNotNull(final Object... array) {
        return checkElementsNotNull(array, array.length);
    }
    
    static Object[] checkElementsNotNull(final Object[] array, final int n) {
        for (int i = 0; i < n; ++i) {
            checkElementNotNull(array[i], i);
        }
        return array;
    }
    
    public static <T> T[] concat(@Nullable final T t, final T[] array) {
        final T[] array2 = newArray(array, array.length + 1);
        array2[0] = t;
        System.arraycopy(array, 0, array2, 1, array.length);
        return array2;
    }
    
    public static <T> T[] concat(final T[] array, @Nullable final T t) {
        final T[] arraysCopy = arraysCopyOf(array, array.length + 1);
        arraysCopy[array.length] = t;
        return arraysCopy;
    }
    
    @GwtIncompatible("Array.newInstance(Class, int)")
    public static <T> T[] concat(final T[] array, final T[] array2, final Class<T> clazz) {
        final T[] array3 = newArray(clazz, array.length + array2.length);
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    static Object[] copyAsObjectArray(final Object[] array, final int n, final int n2) {
        Preconditions.checkPositionIndexes(n, n + n2, array.length);
        if (n2 != 0) {
            final Object[] array2 = new Object[n2];
            System.arraycopy(array, n, array2, 0, n2);
            return array2;
        }
        return ObjectArrays.EMPTY_ARRAY;
    }
    
    private static Object[] fillArray(final Iterable<?> iterable, final Object[] array) {
        int n = 0;
        final Iterator<?> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            array[n] = iterator.next();
            ++n;
        }
        return array;
    }
    
    @GwtIncompatible("Array.newInstance(Class, int)")
    public static <T> T[] newArray(final Class<T> componentType, final int length) {
        return (T[])Array.newInstance(componentType, length);
    }
    
    public static <T> T[] newArray(final T[] array, final int n) {
        return Platform.newArray(array, n);
    }
    
    static void swap(final Object[] array, final int n, final int n2) {
        final Object o = array[n];
        array[n] = array[n2];
        array[n2] = o;
    }
    
    static Object[] toArrayImpl(final Collection<?> collection) {
        return fillArray(collection, new Object[collection.size()]);
    }
    
    static <T> T[] toArrayImpl(final Collection<?> collection, T[] array) {
        final int size = collection.size();
        if (array.length < size) {
            array = newArray(array, size);
        }
        fillArray(collection, array);
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }
    
    static <T> T[] toArrayImpl(final Object[] array, final int n, final int n2, T[] array2) {
        Preconditions.checkPositionIndexes(n, n + n2, array.length);
        if (array2.length >= n2) {
            if (array2.length > n2) {
                array2[n2] = null;
            }
        }
        else {
            array2 = newArray(array2, n2);
        }
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
}
