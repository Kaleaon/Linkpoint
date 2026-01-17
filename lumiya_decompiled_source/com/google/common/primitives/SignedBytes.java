package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import javax.annotation.CheckReturnValue;
@GwtCompatible
@CheckReturnValue
/* loaded from: classes.dex */
public final class SignedBytes {
    public static final byte MAX_POWER_OF_TWO = 64;

    /* loaded from: classes.dex */
    private enum LexicographicalComparator implements Comparator<byte[]> {
        INSTANCE;

        @Override // java.util.Comparator
        public int compare(byte[] bArr, byte[] bArr2) {
            int min = Math.min(bArr.length, bArr2.length);
            for (int i = 0; i < min; i++) {
                int compare = SignedBytes.compare(bArr[i], bArr2[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return bArr.length - bArr2.length;
        }
    }

    private SignedBytes() {
    }

    public static byte checkedCast(long j) {
        byte b = (byte) j;
        if (b != j) {
            throw new IllegalArgumentException("Out of range: " + j);
        }
        return b;
    }

    public static int compare(byte b, byte b2) {
        return b - b2;
    }

    public static String join(String str, byte... bArr) {
        Preconditions.checkNotNull(str);
        if (bArr.length != 0) {
            StringBuilder sb = new StringBuilder(bArr.length * 5);
            sb.append((int) bArr[0]);
            for (int i = 1; i < bArr.length; i++) {
                sb.append(str).append((int) bArr[i]);
            }
            return sb.toString();
        }
        return "";
    }

    public static Comparator<byte[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static byte max(byte... bArr) {
        Preconditions.checkArgument(bArr.length > 0);
        byte b = bArr[0];
        for (int i = 1; i < bArr.length; i++) {
            if (bArr[i] > b) {
                b = bArr[i];
            }
        }
        return b;
    }

    public static byte min(byte... bArr) {
        Preconditions.checkArgument(bArr.length > 0);
        byte b = bArr[0];
        for (int i = 1; i < bArr.length; i++) {
            if (bArr[i] < b) {
                b = bArr[i];
            }
        }
        return b;
    }

    public static byte saturatedCast(long j) {
        if (j <= 127) {
            if (j >= -128) {
                return (byte) j;
            }
            return Byte.MIN_VALUE;
        }
        return Ascii.DEL;
    }
}
