// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

import android.text.TextUtils;
import android.support.annotation.NonNull;
import java.util.Iterator;
import java.util.Collection;
import android.support.annotation.IntRange;
import java.util.Locale;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class Preconditions
{
    public static void checkArgument(final boolean b) {
        if (b) {
            return;
        }
        throw new IllegalArgumentException();
    }
    
    public static void checkArgument(final boolean b, final Object obj) {
        if (b) {
            return;
        }
        throw new IllegalArgumentException(String.valueOf(obj));
    }
    
    public static float checkArgumentFinite(final float n, final String s) {
        if (Float.isNaN(n)) {
            throw new IllegalArgumentException(s + " must not be NaN");
        }
        if (!Float.isInfinite(n)) {
            return n;
        }
        throw new IllegalArgumentException(s + " must not be infinite");
    }
    
    public static float checkArgumentInRange(final float v, final float n, final float n2, final String str) {
        if (Float.isNaN(v)) {
            throw new IllegalArgumentException(str + " must not be NaN");
        }
        if (v < n) {
            throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%f, %f] (too low)", str, n, n2));
        }
        if (v > n2) {
            throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%f, %f] (too high)", str, n, n2));
        }
        return v;
    }
    
    public static int checkArgumentInRange(final int n, final int n2, final int n3, final String s) {
        if (n < n2) {
            throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too low)", s, n2, n3));
        }
        if (n <= n3) {
            return n;
        }
        throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too high)", s, n2, n3));
    }
    
    public static long checkArgumentInRange(final long n, final long n2, final long n3, final String s) {
        int n4;
        if (n >= n2) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too low)", s, n2, n3));
        }
        int n5;
        if (n <= n3) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        if (n5 == 0) {
            throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too high)", s, n2, n3));
        }
        return n;
    }
    
    @IntRange(from = 0L)
    public static int checkArgumentNonnegative(final int n) {
        if (n >= 0) {
            return n;
        }
        throw new IllegalArgumentException();
    }
    
    @IntRange(from = 0L)
    public static int checkArgumentNonnegative(final int n, final String s) {
        if (n >= 0) {
            return n;
        }
        throw new IllegalArgumentException(s);
    }
    
    public static long checkArgumentNonnegative(final long n) {
        int n2;
        if (n >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException();
        }
        return n;
    }
    
    public static long checkArgumentNonnegative(final long n, final String s) {
        int n2;
        if (n >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException(s);
        }
        return n;
    }
    
    public static int checkArgumentPositive(final int n, final String s) {
        if (n > 0) {
            return n;
        }
        throw new IllegalArgumentException(s);
    }
    
    public static float[] checkArrayElementsInRange(final float[] array, final float n, final float n2, final String s) {
        checkNotNull(array, s + " must not be null");
        for (int i = 0; i < array.length; ++i) {
            final float v = array[i];
            if (Float.isNaN(v)) {
                throw new IllegalArgumentException(s + "[" + i + "] must not be NaN");
            }
            if (v < n) {
                throw new IllegalArgumentException(String.format(Locale.US, "%s[%d] is out of range of [%f, %f] (too low)", s, i, n, n2));
            }
            if (v > n2) {
                throw new IllegalArgumentException(String.format(Locale.US, "%s[%d] is out of range of [%f, %f] (too high)", s, i, n, n2));
            }
        }
        return array;
    }
    
    public static <T> T[] checkArrayElementsNotNull(final T[] array, final String str) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new NullPointerException(String.format(Locale.US, "%s[%d] must not be null", str, i));
                }
            }
            return array;
        }
        throw new NullPointerException(str + " must not be null");
    }
    
    @NonNull
    public static <C extends Collection<T>, T> C checkCollectionElementsNotNull(final C c, final String str) {
        if (c != null) {
            long l = 0L;
            final Iterator<T> iterator = c.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == null) {
                    throw new NullPointerException(String.format(Locale.US, "%s[%d] must not be null", str, l));
                }
                ++l;
            }
            return c;
        }
        throw new NullPointerException(str + " must not be null");
    }
    
    public static <T> Collection<T> checkCollectionNotEmpty(final Collection<T> collection, final String s) {
        if (collection == null) {
            throw new NullPointerException(s + " must not be null");
        }
        if (!collection.isEmpty()) {
            return collection;
        }
        throw new IllegalArgumentException(s + " is empty");
    }
    
    public static int checkFlagsArgument(final int i, final int j) {
        if ((i & j) == i) {
            return i;
        }
        throw new IllegalArgumentException("Requested flags 0x" + Integer.toHexString(i) + ", but only 0x" + Integer.toHexString(j) + " are allowed");
    }
    
    @NonNull
    public static <T> T checkNotNull(final T t) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException();
    }
    
    @NonNull
    public static <T> T checkNotNull(final T t, final Object obj) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(String.valueOf(obj));
    }
    
    public static void checkState(final boolean b) {
        checkState(b, null);
    }
    
    public static void checkState(final boolean b, final String s) {
        if (b) {
            return;
        }
        throw new IllegalStateException(s);
    }
    
    @NonNull
    public static <T extends CharSequence> T checkStringNotEmpty(final T t) {
        if (!TextUtils.isEmpty((CharSequence)t)) {
            return t;
        }
        throw new IllegalArgumentException();
    }
    
    @NonNull
    public static <T extends CharSequence> T checkStringNotEmpty(final T t, final Object obj) {
        if (!TextUtils.isEmpty((CharSequence)t)) {
            return t;
        }
        throw new IllegalArgumentException(String.valueOf(obj));
    }
}
