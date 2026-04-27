// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.primitives;

import java.io.Serializable;
import java.util.RandomAccess;
import java.util.AbstractList;
import javax.annotation.Nullable;
import javax.annotation.CheckForNull;
import java.util.Collection;
import com.google.common.annotations.Beta;
import com.google.common.base.Converter;
import java.util.Comparator;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible(emulated = true)
public final class Ints
{
    public static final int BYTES = 4;
    public static final int MAX_POWER_OF_TWO = 1073741824;
    
    private Ints() {
    }
    
    public static List<Integer> asList(final int... array) {
        if (array.length != 0) {
            return new IntArrayAsList(array);
        }
        return Collections.emptyList();
    }
    
    public static int checkedCast(final long lng) {
        final int n = (int)lng;
        if (n != lng) {
            throw new IllegalArgumentException("Out of range: " + lng);
        }
        return n;
    }
    
    public static int compare(int n, final int n2) {
        if (n >= n2) {
            if (n <= n2) {
                n = 0;
            }
            else {
                n = 1;
            }
        }
        else {
            n = -1;
        }
        return n;
    }
    
    public static int[] concat(final int[]... array) {
        final int length = array.length;
        int i = 0;
        int n = 0;
        while (i < length) {
            n += array[i].length;
            ++i;
        }
        final int[] array2 = new int[n];
        final int length2 = array.length;
        int j = 0;
        int n2 = 0;
        while (j < length2) {
            final int[] array3 = array[j];
            System.arraycopy(array3, 0, array2, n2, array3.length);
            n2 += array3.length;
            ++j;
        }
        return array2;
    }
    
    public static boolean contains(final int[] array, final int n) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    private static int[] copyOf(final int[] array, final int b) {
        final int[] array2 = new int[b];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, b));
        return array2;
    }
    
    public static int[] ensureCapacity(int[] copy, final int i, final int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        if (copy.length < i) {
            copy = copyOf(copy, i + j);
        }
        return copy;
    }
    
    @GwtIncompatible("doesn't work")
    public static int fromByteArray(final byte[] array) {
        Preconditions.checkArgument(array.length >= 4, "array too small: %s < %s", array.length, 4);
        return fromBytes(array[0], array[1], array[2], array[3]);
    }
    
    @GwtIncompatible("doesn't work")
    public static int fromBytes(final byte b, final byte b2, final byte b3, final byte b4) {
        return b << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);
    }
    
    public static int hashCode(final int n) {
        return n;
    }
    
    public static int indexOf(final int[] array, final int n) {
        return indexOf(array, n, 0, array.length);
    }
    
    private static int indexOf(final int[] array, final int n, int i, final int n2) {
        while (i < n2) {
            if (array[i] == n) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public static int indexOf(final int[] array, final int[] array2) {
        Preconditions.checkNotNull(array, (Object)"array");
        Preconditions.checkNotNull(array2, (Object)"target");
        if (array2.length != 0) {
            int i = 0;
        Label_0021:
            while (i < array.length - array2.length + 1) {
                for (int j = 0; j < array2.length; ++j) {
                    if (array[i + j] != array2[j]) {
                        ++i;
                        continue Label_0021;
                    }
                }
                return i;
            }
            return -1;
        }
        return 0;
    }
    
    public static String join(final String str, final int... array) {
        Preconditions.checkNotNull(str);
        if (array.length != 0) {
            final StringBuilder sb = new StringBuilder(array.length * 5);
            sb.append(array[0]);
            for (int i = 1; i < array.length; ++i) {
                sb.append(str).append(array[i]);
            }
            return sb.toString();
        }
        return "";
    }
    
    public static int lastIndexOf(final int[] array, final int n) {
        return lastIndexOf(array, n, 0, array.length);
    }
    
    private static int lastIndexOf(final int[] array, final int n, final int n2, int i) {
        --i;
        while (i >= n2) {
            if (array[i] == n) {
                return i;
            }
            --i;
        }
        return -1;
    }
    
    public static Comparator<int[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }
    
    public static int max(final int... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        int n = array[0];
        while (i < array.length) {
            if (array[i] > n) {
                n = array[i];
            }
            ++i;
        }
        return n;
    }
    
    public static int min(final int... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        int n = array[0];
        while (i < array.length) {
            if (array[i] < n) {
                n = array[i];
            }
            ++i;
        }
        return n;
    }
    
    public static int saturatedCast(final long n) {
        final int n2 = 1;
        int n3;
        if (n <= 2147483647L) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        if (n3 == 0) {
            return Integer.MAX_VALUE;
        }
        int n4;
        if (n >= -2147483648L) {
            n4 = n2;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            return Integer.MIN_VALUE;
        }
        return (int)n;
    }
    
    @Beta
    public static Converter<String, Integer> stringConverter() {
        return IntConverter.INSTANCE;
    }
    
    public static int[] toArray(final Collection<? extends Number> collection) {
        if (!(collection instanceof IntArrayAsList)) {
            final Object[] array = collection.toArray();
            final int length = array.length;
            final int[] array2 = new int[length];
            for (int i = 0; i < length; ++i) {
                array2[i] = Preconditions.checkNotNull(array[i]).intValue();
            }
            return array2;
        }
        return ((IntArrayAsList)collection).toIntArray();
    }
    
    @GwtIncompatible("doesn't work")
    public static byte[] toByteArray(final int n) {
        return new byte[] { (byte)(n >> 24), (byte)(n >> 16), (byte)(n >> 8), (byte)n };
    }
    
    @CheckForNull
    @Nullable
    @Beta
    public static Integer tryParse(final String s) {
        return tryParse(s, 10);
    }
    
    @CheckForNull
    @Nullable
    @Beta
    public static Integer tryParse(final String s, final int n) {
        final Long tryParse = Longs.tryParse(s, n);
        if (tryParse != null && tryParse == tryParse.intValue()) {
            return tryParse.intValue();
        }
        return null;
    }
    
    @GwtCompatible
    private static class IntArrayAsList extends AbstractList<Integer> implements RandomAccess, Serializable
    {
        private static final long serialVersionUID = 0L;
        final int[] array;
        final int end;
        final int start;
        
        IntArrayAsList(final int[] array) {
            this(array, 0, array.length);
        }
        
        IntArrayAsList(final int[] array, final int start, final int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public boolean contains(final Object o) {
            boolean b = false;
            if (o instanceof Integer && indexOf(this.array, (int)o, this.start, this.end) != -1) {
                b = true;
            }
            return b;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof IntArrayAsList)) {
                return super.equals(o);
            }
            final IntArrayAsList list = (IntArrayAsList)o;
            final int size = this.size();
            if (list.size() == size) {
                for (int i = 0; i < size; ++i) {
                    if (this.array[this.start + i] != list.array[list.start + i]) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        
        @Override
        public Integer get(final int n) {
            Preconditions.checkElementIndex(n, this.size());
            return this.array[this.start + n];
        }
        
        @Override
        public int hashCode() {
            int n = 1;
            for (int i = this.start; i < this.end; ++i) {
                n = n * 31 + Ints.hashCode(this.array[i]);
            }
            return n;
        }
        
        @Override
        public int indexOf(final Object o) {
            if (o instanceof Integer) {
                final int access$000 = indexOf(this.array, (int)o, this.start, this.end);
                if (access$000 >= 0) {
                    return access$000 - this.start;
                }
            }
            return -1;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public int lastIndexOf(final Object o) {
            if (o instanceof Integer) {
                final int access$100 = lastIndexOf(this.array, (int)o, this.start, this.end);
                if (access$100 >= 0) {
                    return access$100 - this.start;
                }
            }
            return -1;
        }
        
        @Override
        public Integer set(final int n, final Integer n2) {
            Preconditions.checkElementIndex(n, this.size());
            final int i = this.array[this.start + n];
            this.array[this.start + n] = Preconditions.checkNotNull(n2);
            return i;
        }
        
        @Override
        public int size() {
            return this.end - this.start;
        }
        
        @Override
        public List<Integer> subList(final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, this.size());
            if (n != n2) {
                return new IntArrayAsList(this.array, this.start + n, this.start + n2);
            }
            return Collections.emptyList();
        }
        
        int[] toIntArray() {
            final int size = this.size();
            final int[] array = new int[size];
            System.arraycopy(this.array, this.start, array, 0, size);
            return array;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(this.size() * 5);
            sb.append('[').append(this.array[this.start]);
            int start = this.start;
            while (++start < this.end) {
                sb.append(", ").append(this.array[start]);
            }
            return sb.append(']').toString();
        }
    }
    
    private static final class IntConverter extends Converter<String, Integer> implements Serializable
    {
        static final IntConverter INSTANCE;
        private static final long serialVersionUID = 1L;
        
        static {
            INSTANCE = new IntConverter();
        }
        
        private Object readResolve() {
            return IntConverter.INSTANCE;
        }
        
        @Override
        protected String doBackward(final Integer n) {
            return n.toString();
        }
        
        @Override
        protected Integer doForward(final String nm) {
            return Integer.decode(nm);
        }
        
        @Override
        public String toString() {
            return "Ints.stringConverter()";
        }
    }
    
    private enum LexicographicalComparator implements Comparator<int[]>
    {
        INSTANCE;
        
        @Override
        public int compare(final int[] array, final int[] array2) {
            for (int i = 0; i < Math.min(array.length, array2.length); ++i) {
                final int compare = Ints.compare(array[i], array2[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return array.length - array2.length;
        }
    }
}
