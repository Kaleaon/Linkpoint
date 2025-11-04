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
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public final class Longs
{
    public static final int BYTES = 8;
    public static final long MAX_POWER_OF_TWO = 4611686018427387904L;
    private static final byte[] asciiDigits;
    
    static {
        asciiDigits = createAsciiDigits();
    }
    
    private Longs() {
    }
    
    public static List<Long> asList(final long... array) {
        if (array.length != 0) {
            return new LongArrayAsList(array);
        }
        return Collections.emptyList();
    }
    
    public static int compare(final long n, final long n2) {
        final int n3 = 1;
        int n4;
        if (n >= n2) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        int n5;
        if (n4 == 0) {
            n5 = -1;
        }
        else {
            int n6;
            if (n <= n2) {
                n6 = 1;
            }
            else {
                n6 = 0;
            }
            n5 = n3;
            if (n6 != 0) {
                n5 = 0;
            }
        }
        return n5;
    }
    
    public static long[] concat(final long[]... array) {
        final int length = array.length;
        int i = 0;
        int n = 0;
        while (i < length) {
            n += array[i].length;
            ++i;
        }
        final long[] array2 = new long[n];
        final int length2 = array.length;
        int j = 0;
        int n2 = 0;
        while (j < length2) {
            final long[] array3 = array[j];
            System.arraycopy(array3, 0, array2, n2, array3.length);
            n2 += array3.length;
            ++j;
        }
        return array2;
    }
    
    public static boolean contains(final long[] array, final long n) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    private static long[] copyOf(final long[] array, final int b) {
        final long[] array2 = new long[b];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, b));
        return array2;
    }
    
    private static byte[] createAsciiDigits() {
        final int n = 0;
        final byte[] a = new byte[128];
        Arrays.fill(a, (byte)(-1));
        for (int i = 0; i <= 9; ++i) {
            a[i + 48] = (byte)i;
        }
        for (int j = n; j <= 26; ++j) {
            a[j + 65] = (byte)(j + 10);
            a[j + 97] = (byte)(j + 10);
        }
        return a;
    }
    
    private static int digit(final char c) {
        int n;
        if (c >= '\u0080') {
            n = -1;
        }
        else {
            n = Longs.asciiDigits[c];
        }
        return n;
    }
    
    public static long[] ensureCapacity(long[] copy, final int i, final int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        if (copy.length < i) {
            copy = copyOf(copy, i + j);
        }
        return copy;
    }
    
    public static long fromByteArray(final byte[] array) {
        Preconditions.checkArgument(array.length >= 8, "array too small: %s < %s", array.length, 8);
        return fromBytes(array[0], array[1], array[2], array[3], array[4], array[5], array[6], array[7]);
    }
    
    public static long fromBytes(final byte b, final byte b2, final byte b3, final byte b4, final byte b5, final byte b6, final byte b7, final byte b8) {
        return ((long)b & 0xFFL) << 56 | ((long)b2 & 0xFFL) << 48 | ((long)b3 & 0xFFL) << 40 | ((long)b4 & 0xFFL) << 32 | ((long)b5 & 0xFFL) << 24 | ((long)b6 & 0xFFL) << 16 | ((long)b7 & 0xFFL) << 8 | ((long)b8 & 0xFFL);
    }
    
    public static int hashCode(final long n) {
        return (int)(n >>> 32 ^ n);
    }
    
    public static int indexOf(final long[] array, final long n) {
        return indexOf(array, n, 0, array.length);
    }
    
    private static int indexOf(final long[] array, final long n, int i, final int n2) {
        while (i < n2) {
            if (array[i] == n) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public static int indexOf(final long[] array, final long[] array2) {
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
    
    public static String join(final String str, final long... array) {
        Preconditions.checkNotNull(str);
        if (array.length != 0) {
            final StringBuilder sb = new StringBuilder(array.length * 10);
            sb.append(array[0]);
            for (int i = 1; i < array.length; ++i) {
                sb.append(str).append(array[i]);
            }
            return sb.toString();
        }
        return "";
    }
    
    public static int lastIndexOf(final long[] array, final long n) {
        return lastIndexOf(array, n, 0, array.length);
    }
    
    private static int lastIndexOf(final long[] array, final long n, final int n2, int i) {
        --i;
        while (i >= n2) {
            if (array[i] == n) {
                return i;
            }
            --i;
        }
        return -1;
    }
    
    public static Comparator<long[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }
    
    public static long max(final long... array) {
        Preconditions.checkArgument(array.length > 0);
        long n = array[0];
        for (int i = 1; i < array.length; ++i) {
            int n2;
            if (array[i] <= n) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                n = array[i];
            }
        }
        return n;
    }
    
    public static long min(final long... array) {
        Preconditions.checkArgument(array.length > 0);
        long n = array[0];
        for (int i = 1; i < array.length; ++i) {
            int n2;
            if (array[i] >= n) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                n = array[i];
            }
        }
        return n;
    }
    
    @Beta
    public static Converter<String, Long> stringConverter() {
        return LongConverter.INSTANCE;
    }
    
    public static long[] toArray(final Collection<? extends Number> collection) {
        if (!(collection instanceof LongArrayAsList)) {
            final Object[] array = collection.toArray();
            final int length = array.length;
            final long[] array2 = new long[length];
            for (int i = 0; i < length; ++i) {
                array2[i] = Preconditions.checkNotNull(array[i]).longValue();
            }
            return array2;
        }
        return ((LongArrayAsList)collection).toLongArray();
    }
    
    public static byte[] toByteArray(long n) {
        final byte[] array = new byte[8];
        for (int i = 7; i >= 0; --i) {
            array[i] = (byte)(0xFFL & n);
            n >>= 8;
        }
        return array;
    }
    
    @CheckForNull
    @Nullable
    @Beta
    public static Long tryParse(final String s) {
        return tryParse(s, 10);
    }
    
    @CheckForNull
    @Nullable
    @Beta
    public static Long tryParse(final String s, final int i) {
        if (Preconditions.checkNotNull(s).isEmpty()) {
            return null;
        }
        if (i < 2 || i > 36) {
            throw new IllegalArgumentException("radix must be between MIN_RADIX and MAX_RADIX but was " + i);
        }
        boolean b;
        if (s.charAt(0) != '-') {
            b = false;
        }
        else {
            b = true;
        }
        int j;
        if (!b) {
            j = 0;
        }
        else {
            j = 1;
        }
        if (j == s.length()) {
            return null;
        }
        final int digit = digit(s.charAt(j));
        if (digit < 0 || digit >= i) {
            return null;
        }
        long l = -digit;
        final long n = Long.MIN_VALUE / i;
        ++j;
        while (j < s.length()) {
            final int digit2 = digit(s.charAt(j));
            if (digit2 >= 0 && digit2 < i) {
                int n2;
                if (l >= n) {
                    n2 = 1;
                }
                else {
                    n2 = 0;
                }
                if (n2 != 0) {
                    final long n3 = l * i;
                    int n4;
                    if (n3 >= digit2 - Long.MIN_VALUE) {
                        n4 = 1;
                    }
                    else {
                        n4 = 0;
                    }
                    if (n4 == 0) {
                        return null;
                    }
                    l = n3 - digit2;
                    ++j;
                    continue;
                }
            }
            return null;
        }
        if (b) {
            return l;
        }
        if (l == Long.MIN_VALUE) {
            return null;
        }
        return -l;
    }
    
    private enum LexicographicalComparator implements Comparator<long[]>
    {
        INSTANCE;
        
        @Override
        public int compare(final long[] array, final long[] array2) {
            for (int i = 0; i < Math.min(array.length, array2.length); ++i) {
                final int compare = Longs.compare(array[i], array2[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return array.length - array2.length;
        }
    }
    
    @GwtCompatible
    private static class LongArrayAsList extends AbstractList<Long> implements RandomAccess, Serializable
    {
        private static final long serialVersionUID = 0L;
        final long[] array;
        final int end;
        final int start;
        
        LongArrayAsList(final long[] array) {
            this(array, 0, array.length);
        }
        
        LongArrayAsList(final long[] array, final int start, final int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public boolean contains(final Object o) {
            boolean b = false;
            if (o instanceof Long && indexOf(this.array, (long)o, this.start, this.end) != -1) {
                b = true;
            }
            return b;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof LongArrayAsList)) {
                return super.equals(o);
            }
            final LongArrayAsList list = (LongArrayAsList)o;
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
        public Long get(final int n) {
            Preconditions.checkElementIndex(n, this.size());
            return this.array[this.start + n];
        }
        
        @Override
        public int hashCode() {
            int n = 1;
            for (int i = this.start; i < this.end; ++i) {
                n = n * 31 + Longs.hashCode(this.array[i]);
            }
            return n;
        }
        
        @Override
        public int indexOf(final Object o) {
            if (o instanceof Long) {
                final int access$000 = indexOf(this.array, (long)o, this.start, this.end);
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
            if (o instanceof Long) {
                final int access$100 = lastIndexOf(this.array, (long)o, this.start, this.end);
                if (access$100 >= 0) {
                    return access$100 - this.start;
                }
            }
            return -1;
        }
        
        @Override
        public Long set(final int n, final Long n2) {
            Preconditions.checkElementIndex(n, this.size());
            final long l = this.array[this.start + n];
            this.array[this.start + n] = Preconditions.checkNotNull(n2);
            return l;
        }
        
        @Override
        public int size() {
            return this.end - this.start;
        }
        
        @Override
        public List<Long> subList(final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, this.size());
            if (n != n2) {
                return new LongArrayAsList(this.array, this.start + n, this.start + n2);
            }
            return Collections.emptyList();
        }
        
        long[] toLongArray() {
            final int size = this.size();
            final long[] array = new long[size];
            System.arraycopy(this.array, this.start, array, 0, size);
            return array;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(this.size() * 10);
            sb.append('[').append(this.array[this.start]);
            int start = this.start;
            while (++start < this.end) {
                sb.append(", ").append(this.array[start]);
            }
            return sb.append(']').toString();
        }
    }
    
    private static final class LongConverter extends Converter<String, Long> implements Serializable
    {
        static final LongConverter INSTANCE;
        private static final long serialVersionUID = 1L;
        
        static {
            INSTANCE = new LongConverter();
        }
        
        private Object readResolve() {
            return LongConverter.INSTANCE;
        }
        
        @Override
        protected String doBackward(final Long n) {
            return n.toString();
        }
        
        @Override
        protected Long doForward(final String nm) {
            return Long.decode(nm);
        }
        
        @Override
        public String toString() {
            return "Longs.stringConverter()";
        }
    }
}
