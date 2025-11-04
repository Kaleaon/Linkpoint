// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.primitives;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.RandomAccess;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible(emulated = true)
public final class Chars
{
    public static final int BYTES = 2;
    
    private Chars() {
    }
    
    public static List<Character> asList(final char... array) {
        if (array.length != 0) {
            return new CharArrayAsList(array);
        }
        return Collections.emptyList();
    }
    
    public static char checkedCast(final long lng) {
        final char c = (char)lng;
        if (c != lng) {
            throw new IllegalArgumentException("Out of range: " + lng);
        }
        return c;
    }
    
    public static int compare(final char c, final char c2) {
        return c - c2;
    }
    
    public static char[] concat(final char[]... array) {
        final int length = array.length;
        int i = 0;
        int n = 0;
        while (i < length) {
            n += array[i].length;
            ++i;
        }
        final char[] array2 = new char[n];
        final int length2 = array.length;
        int j = 0;
        int n2 = 0;
        while (j < length2) {
            final char[] array3 = array[j];
            System.arraycopy(array3, 0, array2, n2, array3.length);
            n2 += array3.length;
            ++j;
        }
        return array2;
    }
    
    public static boolean contains(final char[] array, final char c) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] == c) {
                return true;
            }
        }
        return false;
    }
    
    private static char[] copyOf(final char[] array, final int b) {
        final char[] array2 = new char[b];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, b));
        return array2;
    }
    
    public static char[] ensureCapacity(char[] copy, final int i, final int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        if (copy.length < i) {
            copy = copyOf(copy, i + j);
        }
        return copy;
    }
    
    @GwtIncompatible("doesn't work")
    public static char fromByteArray(final byte[] array) {
        Preconditions.checkArgument(array.length >= 2, "array too small: %s < %s", array.length, 2);
        return fromBytes(array[0], array[1]);
    }
    
    @GwtIncompatible("doesn't work")
    public static char fromBytes(final byte b, final byte b2) {
        return (char)(b << 8 | (b2 & 0xFF));
    }
    
    public static int hashCode(final char c) {
        return c;
    }
    
    public static int indexOf(final char[] array, final char c) {
        return indexOf(array, c, 0, array.length);
    }
    
    private static int indexOf(final char[] array, final char c, int i, final int n) {
        while (i < n) {
            if (array[i] == c) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public static int indexOf(final char[] array, final char[] array2) {
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
    
    public static String join(final String str, final char... array) {
        Preconditions.checkNotNull(str);
        final int length = array.length;
        if (length != 0) {
            final StringBuilder sb = new StringBuilder(str.length() * (length - 1) + length);
            sb.append(array[0]);
            for (int i = 1; i < length; ++i) {
                sb.append(str).append(array[i]);
            }
            return sb.toString();
        }
        return "";
    }
    
    public static int lastIndexOf(final char[] array, final char c) {
        return lastIndexOf(array, c, 0, array.length);
    }
    
    private static int lastIndexOf(final char[] array, final char c, final int n, int i) {
        --i;
        while (i >= n) {
            if (array[i] == c) {
                return i;
            }
            --i;
        }
        return -1;
    }
    
    public static Comparator<char[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }
    
    public static char max(final char... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        char c = array[0];
        while (i < array.length) {
            char c2;
            if (array[i] <= c) {
                c2 = c;
            }
            else {
                c2 = array[i];
            }
            ++i;
            c = c2;
        }
        return c;
    }
    
    public static char min(final char... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        char c = array[0];
        while (i < array.length) {
            char c2;
            if (array[i] >= c) {
                c2 = c;
            }
            else {
                c2 = array[i];
            }
            ++i;
            c = c2;
        }
        return c;
    }
    
    public static char saturatedCast(final long n) {
        final int n2 = 1;
        int n3;
        if (n <= 65535L) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        if (n3 == 0) {
            return '\uffff';
        }
        int n4;
        if (n >= 0L) {
            n4 = n2;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            return '\0';
        }
        return (char)n;
    }
    
    public static char[] toArray(final Collection<Character> collection) {
        if (!(collection instanceof CharArrayAsList)) {
            final Object[] array = collection.toArray();
            final int length = array.length;
            final char[] array2 = new char[length];
            for (int i = 0; i < length; ++i) {
                array2[i] = Preconditions.checkNotNull(array[i]);
            }
            return array2;
        }
        return ((CharArrayAsList)collection).toCharArray();
    }
    
    @GwtIncompatible("doesn't work")
    public static byte[] toByteArray(final char c) {
        return new byte[] { (byte)(c >> 8), (byte)c };
    }
    
    @GwtCompatible
    private static class CharArrayAsList extends AbstractList<Character> implements RandomAccess, Serializable
    {
        private static final long serialVersionUID = 0L;
        final char[] array;
        final int end;
        final int start;
        
        CharArrayAsList(final char[] array) {
            this(array, 0, array.length);
        }
        
        CharArrayAsList(final char[] array, final int start, final int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public boolean contains(final Object o) {
            boolean b = false;
            if (o instanceof Character && indexOf(this.array, (char)o, this.start, this.end) != -1) {
                b = true;
            }
            return b;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CharArrayAsList)) {
                return super.equals(o);
            }
            final CharArrayAsList list = (CharArrayAsList)o;
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
        public Character get(final int n) {
            Preconditions.checkElementIndex(n, this.size());
            return this.array[this.start + n];
        }
        
        @Override
        public int hashCode() {
            int n = 1;
            for (int i = this.start; i < this.end; ++i) {
                n = n * 31 + Chars.hashCode(this.array[i]);
            }
            return n;
        }
        
        @Override
        public int indexOf(final Object o) {
            if (o instanceof Character) {
                final int access$000 = indexOf(this.array, (char)o, this.start, this.end);
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
            if (o instanceof Character) {
                final int access$100 = lastIndexOf(this.array, (char)o, this.start, this.end);
                if (access$100 >= 0) {
                    return access$100 - this.start;
                }
            }
            return -1;
        }
        
        @Override
        public Character set(final int n, final Character c) {
            Preconditions.checkElementIndex(n, this.size());
            final char c2 = this.array[this.start + n];
            this.array[this.start + n] = Preconditions.checkNotNull(c);
            return c2;
        }
        
        @Override
        public int size() {
            return this.end - this.start;
        }
        
        @Override
        public List<Character> subList(final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, this.size());
            if (n != n2) {
                return new CharArrayAsList(this.array, this.start + n, this.start + n2);
            }
            return Collections.emptyList();
        }
        
        char[] toCharArray() {
            final int size = this.size();
            final char[] array = new char[size];
            System.arraycopy(this.array, this.start, array, 0, size);
            return array;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(this.size() * 3);
            sb.append('[').append(this.array[this.start]);
            int start = this.start;
            while (++start < this.end) {
                sb.append(", ").append(this.array[start]);
            }
            return sb.append(']').toString();
        }
    }
    
    private enum LexicographicalComparator implements Comparator<char[]>
    {
        INSTANCE;
        
        @Override
        public int compare(final char[] array, final char[] array2) {
            for (int i = 0; i < Math.min(array.length, array2.length); ++i) {
                final int compare = Chars.compare(array[i], array2[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return array.length - array2.length;
        }
    }
}
