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
import java.util.Collections;
import java.util.List;
import com.google.common.annotations.GwtIncompatible;
import java.util.regex.Pattern;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible(emulated = true)
public final class Doubles
{
    public static final int BYTES = 8;
    @GwtIncompatible("regular expressions")
    static final Pattern FLOATING_POINT_PATTERN;
    
    static {
        FLOATING_POINT_PATTERN = fpPattern();
    }
    
    private Doubles() {
    }
    
    public static List<Double> asList(final double... array) {
        if (array.length != 0) {
            return new DoubleArrayAsList(array);
        }
        return Collections.emptyList();
    }
    
    public static int compare(final double d1, final double d2) {
        return Double.compare(d1, d2);
    }
    
    public static double[] concat(final double[]... array) {
        final int length = array.length;
        int i = 0;
        int n = 0;
        while (i < length) {
            n += array[i].length;
            ++i;
        }
        final double[] array2 = new double[n];
        final int length2 = array.length;
        int j = 0;
        int n2 = 0;
        while (j < length2) {
            final double[] array3 = array[j];
            System.arraycopy(array3, 0, array2, n2, array3.length);
            n2 += array3.length;
            ++j;
        }
        return array2;
    }
    
    public static boolean contains(final double[] array, final double n) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    private static double[] copyOf(final double[] array, final int b) {
        final double[] array2 = new double[b];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, b));
        return array2;
    }
    
    public static double[] ensureCapacity(double[] copy, final int i, final int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        if (copy.length < i) {
            copy = copyOf(copy, i + j);
        }
        return copy;
    }
    
    @GwtIncompatible("regular expressions")
    private static Pattern fpPattern() {
        return Pattern.compile("[+-]?(?:NaN|Infinity|" + ("(?:\\d++(?:\\.\\d*+)?|\\.\\d++)" + "(?:[eE][+-]?\\d++)?[fFdD]?") + "|" + ("0[xX]" + "(?:\\p{XDigit}++(?:\\.\\p{XDigit}*+)?|\\.\\p{XDigit}++)" + "[pP][+-]?\\d++[fFdD]?") + ")");
    }
    
    public static int hashCode(final double d) {
        return Double.valueOf(d).hashCode();
    }
    
    public static int indexOf(final double[] array, final double n) {
        return indexOf(array, n, 0, array.length);
    }
    
    private static int indexOf(final double[] array, final double n, int i, final int n2) {
        while (i < n2) {
            if (array[i] == n) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public static int indexOf(final double[] array, final double[] array2) {
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
    
    public static boolean isFinite(final double n) {
        boolean b = true;
        boolean b2;
        if (Double.NEGATIVE_INFINITY < n) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        if (n >= Double.POSITIVE_INFINITY) {
            b = false;
        }
        return b & b2;
    }
    
    public static String join(final String str, final double... array) {
        Preconditions.checkNotNull(str);
        if (array.length != 0) {
            final StringBuilder sb = new StringBuilder(array.length * 12);
            sb.append(array[0]);
            for (int i = 1; i < array.length; ++i) {
                sb.append(str).append(array[i]);
            }
            return sb.toString();
        }
        return "";
    }
    
    public static int lastIndexOf(final double[] array, final double n) {
        return lastIndexOf(array, n, 0, array.length);
    }
    
    private static int lastIndexOf(final double[] array, final double n, final int n2, int i) {
        --i;
        while (i >= n2) {
            if (array[i] == n) {
                return i;
            }
            --i;
        }
        return -1;
    }
    
    public static Comparator<double[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }
    
    public static double max(final double... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        double max = array[0];
        while (i < array.length) {
            max = Math.max(max, array[i]);
            ++i;
        }
        return max;
    }
    
    public static double min(final double... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        double min = array[0];
        while (i < array.length) {
            min = Math.min(min, array[i]);
            ++i;
        }
        return min;
    }
    
    @Beta
    public static Converter<String, Double> stringConverter() {
        return DoubleConverter.INSTANCE;
    }
    
    public static double[] toArray(final Collection<? extends Number> collection) {
        if (!(collection instanceof DoubleArrayAsList)) {
            final Object[] array = collection.toArray();
            final int length = array.length;
            final double[] array2 = new double[length];
            for (int i = 0; i < length; ++i) {
                array2[i] = Preconditions.checkNotNull(array[i]).doubleValue();
            }
            return array2;
        }
        return ((DoubleArrayAsList)collection).toDoubleArray();
    }
    
    @CheckForNull
    @Nullable
    @Beta
    @GwtIncompatible("regular expressions")
    public static Double tryParse(final String s) {
        if (Doubles.FLOATING_POINT_PATTERN.matcher(s).matches()) {
            try {
                return Double.parseDouble(s);
            }
            catch (final NumberFormatException ex) {}
        }
        return null;
    }
    
    @GwtCompatible
    private static class DoubleArrayAsList extends AbstractList<Double> implements RandomAccess, Serializable
    {
        private static final long serialVersionUID = 0L;
        final double[] array;
        final int end;
        final int start;
        
        DoubleArrayAsList(final double[] array) {
            this(array, 0, array.length);
        }
        
        DoubleArrayAsList(final double[] array, final int start, final int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public boolean contains(final Object o) {
            boolean b = false;
            if (o instanceof Double && indexOf(this.array, (double)o, this.start, this.end) != -1) {
                b = true;
            }
            return b;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof DoubleArrayAsList)) {
                return super.equals(o);
            }
            final DoubleArrayAsList list = (DoubleArrayAsList)o;
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
        public Double get(final int n) {
            Preconditions.checkElementIndex(n, this.size());
            return this.array[this.start + n];
        }
        
        @Override
        public int hashCode() {
            int n = 1;
            for (int i = this.start; i < this.end; ++i) {
                n = n * 31 + Doubles.hashCode(this.array[i]);
            }
            return n;
        }
        
        @Override
        public int indexOf(final Object o) {
            if (o instanceof Double) {
                final int access$000 = indexOf(this.array, (double)o, this.start, this.end);
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
            if (o instanceof Double) {
                final int access$100 = lastIndexOf(this.array, (double)o, this.start, this.end);
                if (access$100 >= 0) {
                    return access$100 - this.start;
                }
            }
            return -1;
        }
        
        @Override
        public Double set(final int n, final Double n2) {
            Preconditions.checkElementIndex(n, this.size());
            final double d = this.array[this.start + n];
            this.array[this.start + n] = Preconditions.checkNotNull(n2);
            return d;
        }
        
        @Override
        public int size() {
            return this.end - this.start;
        }
        
        @Override
        public List<Double> subList(final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, this.size());
            if (n != n2) {
                return new DoubleArrayAsList(this.array, this.start + n, this.start + n2);
            }
            return Collections.emptyList();
        }
        
        double[] toDoubleArray() {
            final int size = this.size();
            final double[] array = new double[size];
            System.arraycopy(this.array, this.start, array, 0, size);
            return array;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(this.size() * 12);
            sb.append('[').append(this.array[this.start]);
            int start = this.start;
            while (++start < this.end) {
                sb.append(", ").append(this.array[start]);
            }
            return sb.append(']').toString();
        }
    }
    
    private static final class DoubleConverter extends Converter<String, Double> implements Serializable
    {
        static final DoubleConverter INSTANCE;
        private static final long serialVersionUID = 1L;
        
        static {
            INSTANCE = new DoubleConverter();
        }
        
        private Object readResolve() {
            return DoubleConverter.INSTANCE;
        }
        
        @Override
        protected String doBackward(final Double n) {
            return n.toString();
        }
        
        @Override
        protected Double doForward(final String s) {
            return Double.valueOf(s);
        }
        
        @Override
        public String toString() {
            return "Doubles.stringConverter()";
        }
    }
    
    private enum LexicographicalComparator implements Comparator<double[]>
    {
        INSTANCE;
        
        @Override
        public int compare(final double[] array, final double[] array2) {
            for (int i = 0; i < Math.min(array.length, array2.length); ++i) {
                final int compare = Double.compare(array[i], array2[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return array.length - array2.length;
        }
    }
}
