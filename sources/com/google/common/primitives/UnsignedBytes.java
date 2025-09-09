package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Comparator;
import javax.annotation.CheckReturnValue;
import sun.misc.Unsafe;

public final class UnsignedBytes {
    public static final byte MAX_POWER_OF_TWO = Byte.MIN_VALUE;
    public static final byte MAX_VALUE = -1;
    private static final int UNSIGNED_MASK = 255;

    @VisibleForTesting
    static class LexicographicalComparatorHolder {
        static final Comparator<byte[]> BEST_COMPARATOR = getBestComparator();
        static final String UNSAFE_COMPARATOR_NAME = (LexicographicalComparatorHolder.class.getName() + "$UnsafeComparator");

        enum PureJavaComparator implements Comparator<byte[]> {
            INSTANCE;

            public int compare(byte[] bArr, byte[] bArr2) {
                int min = Math.min(bArr.length, bArr2.length);
                for (int i = 0; i < min; i++) {
                    int compare = UnsignedBytes.compare(bArr[i], bArr2[i]);
                    if (compare != 0) {
                        return compare;
                    }
                }
                return bArr.length - bArr2.length;
            }
        }

        @VisibleForTesting
        enum UnsafeComparator implements Comparator<byte[]> {
            INSTANCE;
            
            static final boolean BIG_ENDIAN = false;
            static final int BYTE_ARRAY_BASE_OFFSET = 0;
            static final Unsafe theUnsafe = null;

            static {
                BIG_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
                theUnsafe = getUnsafe();
                BYTE_ARRAY_BASE_OFFSET = theUnsafe.arrayBaseOffset(byte[].class);
                if (theUnsafe.arrayIndexScale(byte[].class) != 1) {
                    throw new AssertionError();
                }
            }

            private static Unsafe getUnsafe() {
                try {
                    return Unsafe.getUnsafe();
                } catch (SecurityException e) {
                    try {
                        return (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                            public Unsafe run() throws Exception {
                                Class<Unsafe> cls = Unsafe.class;
                                for (Field field : cls.getDeclaredFields()) {
                                    field.setAccessible(true);
                                    Object obj = field.get((Object) null);
                                    if (cls.isInstance(obj)) {
                                        return cls.cast(obj);
                                    }
                                }
                                throw new NoSuchFieldError("the Unsafe");
                            }
                        });
                    } catch (PrivilegedActionException e2) {
                        throw new RuntimeException("Could not initialize intrinsics", e2.getCause());
                    }
                }
            }

            public int compare(byte[] bArr, byte[] bArr2) {
                int i = 0;
                int min = Math.min(bArr.length, bArr2.length);
                int i2 = min / 8;
                while (i < i2 * 8) {
                    long j = theUnsafe.getLong(bArr, ((long) BYTE_ARRAY_BASE_OFFSET) + ((long) i));
                    long j2 = theUnsafe.getLong(bArr2, ((long) BYTE_ARRAY_BASE_OFFSET) + ((long) i));
                    if (j == j2) {
                        i += 8;
                    } else if (BIG_ENDIAN) {
                        return UnsignedLongs.compare(j, j2);
                    } else {
                        int numberOfTrailingZeros = Long.numberOfTrailingZeros(j ^ j2) & -8;
                        return (int) (((j >>> numberOfTrailingZeros) & 255) - ((j2 >>> numberOfTrailingZeros) & 255));
                    }
                }
                for (int i3 = i2 * 8; i3 < min; i3++) {
                    int compare = UnsignedBytes.compare(bArr[i3], bArr2[i3]);
                    if (compare != 0) {
                        return compare;
                    }
                }
                return bArr.length - bArr2.length;
            }
        }

        LexicographicalComparatorHolder() {
        }

        static Comparator<byte[]> getBestComparator() {
            try {
                return (Comparator) Class.forName(UNSAFE_COMPARATOR_NAME).getEnumConstants()[0];
            } catch (Throwable th) {
                return UnsignedBytes.lexicographicalComparatorJavaImpl();
            }
        }
    }

    private UnsignedBytes() {
    }

    public static byte checkedCast(long j) {
        if ((j >> 8) == 0) {
            return (byte) ((int) j);
        }
        throw new IllegalArgumentException("Out of range: " + j);
    }

    @CheckReturnValue
    public static int compare(byte b, byte b2) {
        return toInt(b) - toInt(b2);
    }

    @CheckReturnValue
    public static String join(String str, byte... bArr) {
        Preconditions.checkNotNull(str);
        if (bArr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArr.length * (str.length() + 3));
        sb.append(toInt(bArr[0]));
        for (int i = 1; i < bArr.length; i++) {
            sb.append(str).append(toString(bArr[i]));
        }
        return sb.toString();
    }

    @CheckReturnValue
    public static Comparator<byte[]> lexicographicalComparator() {
        return LexicographicalComparatorHolder.BEST_COMPARATOR;
    }

    @VisibleForTesting
    static Comparator<byte[]> lexicographicalComparatorJavaImpl() {
        return LexicographicalComparatorHolder.PureJavaComparator.INSTANCE;
    }

    @CheckReturnValue
    public static byte max(byte... bArr) {
        Preconditions.checkArgument(bArr.length > 0);
        int i = toInt(bArr[0]);
        for (int i2 = 1; i2 < bArr.length; i2++) {
            int i3 = toInt(bArr[i2]);
            if (i3 > i) {
                i = i3;
            }
        }
        return (byte) i;
    }

    @CheckReturnValue
    public static byte min(byte... bArr) {
        Preconditions.checkArgument(bArr.length > 0);
        int i = toInt(bArr[0]);
        for (int i2 = 1; i2 < bArr.length; i2++) {
            int i3 = toInt(bArr[i2]);
            if (i3 < i) {
                i = i3;
            }
        }
        return (byte) i;
    }

    @Beta
    public static byte parseUnsignedByte(String str) {
        return parseUnsignedByte(str, 10);
    }

    @Beta
    public static byte parseUnsignedByte(String str, int i) {
        int parseInt = Integer.parseInt((String) Preconditions.checkNotNull(str), i);
        if ((parseInt >> 8) == 0) {
            return (byte) parseInt;
        }
        throw new NumberFormatException("out of range: " + parseInt);
    }

    public static byte saturatedCast(long j) {
        boolean z = true;
        if (!(j <= ((long) toInt((byte) -1)))) {
            return -1;
        }
        if (j < 0) {
            z = false;
        }
        if (!z) {
            return 0;
        }
        return (byte) ((int) j);
    }

    @CheckReturnValue
    public static int toInt(byte b) {
        return b & MAX_VALUE;
    }

    @CheckReturnValue
    @Beta
    public static String toString(byte b) {
        return toString(b, 10);
    }

    @CheckReturnValue
    @Beta
    public static String toString(byte b, int i) {
        Preconditions.checkArgument(i >= 2 && i <= 36, "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", Integer.valueOf(i));
        return Integer.toString(toInt(b), i);
    }
}
