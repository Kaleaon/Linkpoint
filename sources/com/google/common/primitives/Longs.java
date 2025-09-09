package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible
@CheckReturnValue
public final class Longs {
    public static final int BYTES = 8;
    public static final long MAX_POWER_OF_TWO = 4611686018427387904L;
    private static final byte[] asciiDigits = createAsciiDigits();

    private enum LexicographicalComparator implements Comparator<long[]> {
        INSTANCE;

        public int compare(long[] jArr, long[] jArr2) {
            int min = Math.min(jArr.length, jArr2.length);
            for (int i = 0; i < min; i++) {
                int compare = Longs.compare(jArr[i], jArr2[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return jArr.length - jArr2.length;
        }
    }

    @GwtCompatible
    private static class LongArrayAsList extends AbstractList<Long> implements RandomAccess, Serializable {
        private static final long serialVersionUID = 0;
        final long[] array;
        final int end;
        final int start;

        LongArrayAsList(long[] jArr) {
            this(jArr, 0, jArr.length);
        }

        LongArrayAsList(long[] jArr, int i, int i2) {
            this.array = jArr;
            this.start = i;
            this.end = i2;
        }

        public boolean contains(Object obj) {
            return (obj instanceof Long) && Longs.indexOf(this.array, ((Long) obj).longValue(), this.start, this.end) != -1;
        }

        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof LongArrayAsList)) {
                return super.equals(obj);
            }
            LongArrayAsList longArrayAsList = (LongArrayAsList) obj;
            int size = size();
            if (longArrayAsList.size() != size) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (this.array[this.start + i] != longArrayAsList.array[longArrayAsList.start + i]) {
                    return false;
                }
            }
            return true;
        }

        public Long get(int i) {
            Preconditions.checkElementIndex(i, size());
            return Long.valueOf(this.array[this.start + i]);
        }

        public int hashCode() {
            int i = 1;
            for (int i2 = this.start; i2 < this.end; i2++) {
                i = (i * 31) + Longs.hashCode(this.array[i2]);
            }
            return i;
        }

        public int indexOf(Object obj) {
            int access$000;
            if ((obj instanceof Long) && (access$000 = Longs.indexOf(this.array, ((Long) obj).longValue(), this.start, this.end)) >= 0) {
                return access$000 - this.start;
            }
            return -1;
        }

        public boolean isEmpty() {
            return false;
        }

        public int lastIndexOf(Object obj) {
            int access$100;
            if ((obj instanceof Long) && (access$100 = Longs.lastIndexOf(this.array, ((Long) obj).longValue(), this.start, this.end)) >= 0) {
                return access$100 - this.start;
            }
            return -1;
        }

        public Long set(int i, Long l) {
            Preconditions.checkElementIndex(i, size());
            long j = this.array[this.start + i];
            this.array[this.start + i] = ((Long) Preconditions.checkNotNull(l)).longValue();
            return Long.valueOf(j);
        }

        public int size() {
            return this.end - this.start;
        }

        public List<Long> subList(int i, int i2) {
            Preconditions.checkPositionIndexes(i, i2, size());
            return i != i2 ? new LongArrayAsList(this.array, this.start + i, this.start + i2) : Collections.emptyList();
        }

        /* access modifiers changed from: package-private */
        public long[] toLongArray() {
            int size = size();
            long[] jArr = new long[size];
            System.arraycopy(this.array, this.start, jArr, 0, size);
            return jArr;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(size() * 10);
            sb.append('[').append(this.array[this.start]);
            int i = this.start;
            while (true) {
                i++;
                if (i >= this.end) {
                    return sb.append(']').toString();
                }
                sb.append(", ").append(this.array[i]);
            }
        }
    }

    private static final class LongConverter extends Converter<String, Long> implements Serializable {
        static final LongConverter INSTANCE = new LongConverter();
        private static final long serialVersionUID = 1;

        private LongConverter() {
        }

        private Object readResolve() {
            return INSTANCE;
        }

        /* access modifiers changed from: protected */
        public String doBackward(Long l) {
            return l.toString();
        }

        /* access modifiers changed from: protected */
        public Long doForward(String str) {
            return Long.decode(str);
        }

        public String toString() {
            return "Longs.stringConverter()";
        }
    }

    private Longs() {
    }

    public static List<Long> asList(long... jArr) {
        return jArr.length != 0 ? new LongArrayAsList(jArr) : Collections.emptyList();
    }

    public static int compare(long j, long j2) {
        if (!(j >= j2)) {
            return -1;
        }
        return (j > j2 ? 1 : (j == j2 ? 0 : -1)) <= 0 ? 0 : 1;
    }

    public static long[] concat(long[]... jArr) {
        int i = 0;
        for (long[] length : jArr) {
            i += length.length;
        }
        long[] jArr2 = new long[i];
        int i2 = 0;
        for (long[] jArr3 : jArr) {
            System.arraycopy(jArr3, 0, jArr2, i2, jArr3.length);
            i2 += jArr3.length;
        }
        return jArr2;
    }

    public static boolean contains(long[] jArr, long j) {
        for (long j2 : jArr) {
            if (j2 == j) {
                return true;
            }
        }
        return false;
    }

    private static long[] copyOf(long[] jArr, int i) {
        long[] jArr2 = new long[i];
        System.arraycopy(jArr, 0, jArr2, 0, Math.min(jArr.length, i));
        return jArr2;
    }

    private static byte[] createAsciiDigits() {
        byte[] bArr = new byte[128];
        Arrays.fill(bArr, (byte) -1);
        for (int i = 0; i <= 9; i++) {
            bArr[i + 48] = (byte) ((byte) i);
        }
        for (int i2 = 0; i2 <= 26; i2++) {
            bArr[i2 + 65] = (byte) ((byte) (i2 + 10));
            bArr[i2 + 97] = (byte) ((byte) (i2 + 10));
        }
        return bArr;
    }

    private static int digit(char c) {
        if (c >= 128) {
            return -1;
        }
        return asciiDigits[c];
    }

    public static long[] ensureCapacity(long[] jArr, int i, int i2) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", Integer.valueOf(i));
        Preconditions.checkArgument(i2 >= 0, "Invalid padding: %s", Integer.valueOf(i2));
        return jArr.length >= i ? jArr : copyOf(jArr, i + i2);
    }

    public static long fromByteArray(byte[] bArr) {
        Preconditions.checkArgument(bArr.length >= 8, "array too small: %s < %s", Integer.valueOf(bArr.length), 8);
        return fromBytes(bArr[0], bArr[1], bArr[2], bArr[3], bArr[4], bArr[5], bArr[6], bArr[7]);
    }

    public static long fromBytes(byte b, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
        return ((((long) b) & 255) << 56) | ((((long) b2) & 255) << 48) | ((((long) b3) & 255) << 40) | ((((long) b4) & 255) << 32) | ((((long) b5) & 255) << 24) | ((((long) b6) & 255) << 16) | ((((long) b7) & 255) << 8) | (((long) b8) & 255);
    }

    public static int hashCode(long j) {
        return (int) ((j >>> 32) ^ j);
    }

    public static int indexOf(long[] jArr, long j) {
        return indexOf(jArr, j, 0, jArr.length);
    }

    /* access modifiers changed from: private */
    public static int indexOf(long[] jArr, long j, int i, int i2) {
        while (i < i2) {
            if (jArr[i] == j) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int indexOf(long[] jArr, long[] jArr2) {
        Preconditions.checkNotNull(jArr, "array");
        Preconditions.checkNotNull(jArr2, "target");
        if (jArr2.length == 0) {
            return 0;
        }
        int i = 0;
        while (i < (jArr.length - jArr2.length) + 1) {
            int i2 = 0;
            while (i2 < jArr2.length) {
                if (jArr[i + i2] != jArr2[i2]) {
                    i++;
                } else {
                    i2++;
                }
            }
            return i;
        }
        return -1;
    }

    public static String join(String str, long... jArr) {
        Preconditions.checkNotNull(str);
        if (jArr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(jArr.length * 10);
        sb.append(jArr[0]);
        for (int i = 1; i < jArr.length; i++) {
            sb.append(str).append(jArr[i]);
        }
        return sb.toString();
    }

    public static int lastIndexOf(long[] jArr, long j) {
        return lastIndexOf(jArr, j, 0, jArr.length);
    }

    /* access modifiers changed from: private */
    public static int lastIndexOf(long[] jArr, long j, int i, int i2) {
        for (int i3 = i2 - 1; i3 >= i; i3--) {
            if (jArr[i3] == j) {
                return i3;
            }
        }
        return -1;
    }

    public static Comparator<long[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static long max(long... jArr) {
        Preconditions.checkArgument(jArr.length > 0);
        long j = jArr[0];
        for (int i = 1; i < jArr.length; i++) {
            if (!(jArr[i] <= j)) {
                j = jArr[i];
            }
        }
        return j;
    }

    public static long min(long... jArr) {
        Preconditions.checkArgument(jArr.length > 0);
        long j = jArr[0];
        for (int i = 1; i < jArr.length; i++) {
            if (!(jArr[i] >= j)) {
                j = jArr[i];
            }
        }
        return j;
    }

    @Beta
    public static Converter<String, Long> stringConverter() {
        return LongConverter.INSTANCE;
    }

    public static long[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof LongArrayAsList) {
            return ((LongArrayAsList) collection).toLongArray();
        }
        Object[] array = collection.toArray();
        int length = array.length;
        long[] jArr = new long[length];
        for (int i = 0; i < length; i++) {
            jArr[i] = ((Number) Preconditions.checkNotNull(array[i])).longValue();
        }
        return jArr;
    }

    public static byte[] toByteArray(long j) {
        byte[] bArr = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bArr[i] = (byte) ((byte) ((int) (255 & j)));
            j >>= 8;
        }
        return bArr;
    }

    @CheckForNull
    @Nullable
    @Beta
    public static Long tryParse(String str) {
        return tryParse(str, 10);
    }

    @CheckForNull
    @Nullable
    @Beta
    public static Long tryParse(String str, int i) {
        if (((String) Preconditions.checkNotNull(str)).isEmpty()) {
            return null;
        }
        if (i >= 2 && i <= 36) {
            boolean z = str.charAt(0) == '-';
            int i2 = !z ? 0 : 1;
            if (i2 == str.length()) {
                return null;
            }
            int i3 = i2 + 1;
            int digit = digit(str.charAt(i2));
            if (digit < 0 || digit >= i) {
                return null;
            }
            long j = (long) (-digit);
            long j2 = Long.MIN_VALUE / ((long) i);
            while (true) {
                int i4 = i3;
                if (i4 < str.length()) {
                    i3 = i4 + 1;
                    int digit2 = digit(str.charAt(i4));
                    if (digit2 < 0 || digit2 >= i) {
                        return null;
                    }
                    if (!(j >= j2)) {
                        return null;
                    }
                    long j3 = j * ((long) i);
                    if (!(j3 >= ((long) digit2) + Long.MIN_VALUE)) {
                        return null;
                    }
                    j = j3 - ((long) digit2);
                } else if (z) {
                    return Long.valueOf(j);
                } else {
                    if (j == Long.MIN_VALUE) {
                        return null;
                    }
                    return Long.valueOf(-j);
                }
            }
        } else {
            throw new IllegalArgumentException("radix must be between MIN_RADIX and MAX_RADIX but was " + i);
        }
    }
}
