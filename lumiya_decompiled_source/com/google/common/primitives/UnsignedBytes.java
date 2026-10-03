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
/* loaded from: classes.dex */
public final class UnsignedBytes {
    public static final byte MAX_POWER_OF_TWO = Byte.MIN_VALUE;
    public static final byte MAX_VALUE = -1;
    private static final int UNSIGNED_MASK = 255;

    @VisibleForTesting
    /* loaded from: classes.dex */
    static class LexicographicalComparatorHolder {
        static final String UNSAFE_COMPARATOR_NAME = LexicographicalComparatorHolder.class.getName() + "$UnsafeComparator";
        static final Comparator<byte[]> BEST_COMPARATOR = getBestComparator();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public enum PureJavaComparator implements Comparator<byte[]> {
            INSTANCE;

            @Override // java.util.Comparator
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
        /* loaded from: classes.dex */
        enum UnsafeComparator implements Comparator<byte[]> {
            INSTANCE;
            
            static final boolean BIG_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
            static final Unsafe theUnsafe = getUnsafe();
            static final int BYTE_ARRAY_BASE_OFFSET = theUnsafe.arrayBaseOffset(byte[].class);

            static {
                if (theUnsafe.arrayIndexScale(byte[].class) != 1) {
                    throw new AssertionError();
                }
            }

            private static Unsafe getUnsafe() {
                try {
                    return Unsafe.getUnsafe();
                } catch (SecurityException e) {
                    try {
                        return (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() { // from class: com.google.common.primitives.UnsignedBytes.LexicographicalComparatorHolder.UnsafeComparator.1
                            @Override // java.security.PrivilegedExceptionAction
                            public Unsafe run() throws Exception {
                                Field[] declaredFields;
                                for (Field field : Unsafe.class.getDeclaredFields()) {
                                    field.setAccessible(true);
                                    Object obj = field.get(null);
                                    if (Unsafe.class.isInstance(obj)) {
                                        return (Unsafe) Unsafe.class.cast(obj);
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

            @Override // java.util.Comparator
            public int compare(byte[] bArr, byte[] bArr2) {
                int min = Math.min(bArr.length, bArr2.length);
                int i = min / 8;
                for (int i2 = 0; i2 < i * 8; i2 += 8) {
                    long j = theUnsafe.getLong(bArr, BYTE_ARRAY_BASE_OFFSET + i2);
                    long j2 = theUnsafe.getLong(bArr2, BYTE_ARRAY_BASE_OFFSET + i2);
                    if (j != j2) {
                        if (BIG_ENDIAN) {
                            return UnsignedLongs.compare(j, j2);
                        }
                        int numberOfTrailingZeros = Long.numberOfTrailingZeros(j ^ j2) & (-8);
                        return (int) (((j >>> numberOfTrailingZeros) & 255) - ((j2 >>> numberOfTrailingZeros) & 255));
                    }
                }
                for (int i3 = i * 8; i3 < min; i3++) {
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

        /* JADX WARN: Multi-variable type inference failed */
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
        if ((j >> 8) != 0) {
            throw new IllegalArgumentException("Out of range: " + j);
        }
        return (byte) j;
    }

    @CheckReturnValue
    public static int compare(byte b, byte b2) {
        return toInt(b) - toInt(b2);
    }

    @CheckReturnValue
    public static String join(String str, byte... bArr) {
        Preconditions.checkNotNull(str);
        if (bArr.length != 0) {
            StringBuilder sb = new StringBuilder(bArr.length * (str.length() + 3));
            sb.append(toInt(bArr[0]));
            for (int i = 1; i < bArr.length; i++) {
                sb.append(str).append(toString(bArr[i]));
            }
            return sb.toString();
        }
        return "";
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
        if ((parseInt >> 8) != 0) {
            throw new NumberFormatException("out of range: " + parseInt);
        }
        return (byte) parseInt;
    }

    public static byte saturatedCast(long j) {
        if (j <= ((long) toInt((byte) -1))) {
            if (j >= 0) {
                return (byte) j;
            }
            return (byte) 0;
        }
        return (byte) -1;
    }

    @CheckReturnValue
    public static int toInt(byte b) {
        return b & MAX_VALUE;
    }

    @Beta
    @CheckReturnValue
    public static String toString(byte b) {
        return toString(b, 10);
    }

    @Beta
    @CheckReturnValue
    public static String toString(byte b, int i) {
        Preconditions.checkArgument(i >= 2 && i <= 36, "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", Integer.valueOf(i));
        return Integer.toString(toInt(b), i);
    }
}
