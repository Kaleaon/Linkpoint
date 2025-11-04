package com.lumiyaviewer.lumiya.base64;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import java.util.Arrays;

/* loaded from: classes.dex */
public class Base64 {
    private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final int[] IA = new int[256];

    static {
        Arrays.fill(IA, -1);
        int length = CA.length;
        for (int i = 0; i < length; i++) {
            IA[CA[i]] = i;
        }
        IA[61] = 0;
    }

    public static final byte[] decode(String str) {
        int length = str == null ? 0 : str.length();
        if (length == 0) {
            return new byte[0];
        }
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            if (IA[str.charAt(i2)] < 0) {
                i++;
            }
        }
        if ((length - i) % 4 != 0) {
            return null;
        }
        int i3 = length;
        int i4 = 0;
        while (i3 > 1) {
            i3--;
            if (IA[str.charAt(i3)] > 0) {
                break;
            }
            if (str.charAt(i3) == '=') {
                i4++;
            }
        }
        int i5 = (((length - i) * 6) >> 3) - i4;
        byte[] bArr = new byte[i5];
        int i6 = 0;
        int i7 = 0;
        while (i6 < i5) {
            int i8 = 0;
            int i9 = i7;
            int i10 = 0;
            while (i10 < 4) {
                int i11 = i9 + 1;
                int i12 = IA[str.charAt(i9)];
                if (i12 < 0) {
                    i10--;
                } else {
                    i8 |= i12 << (18 - (i10 * 6));
                }
                i10++;
                i9 = i11;
            }
            int i13 = i6 + 1;
            bArr[i6] = (byte) (i8 >> 16);
            if (i13 < i5) {
                int i14 = i13 + 1;
                bArr[i13] = (byte) (i8 >> 8);
                if (i14 >= i5) {
                    i13 = i14;
                } else {
                    i13 = i14 + 1;
                    bArr[i14] = (byte) i8;
                }
            }
            i6 = i13;
            i7 = i9;
        }
        return bArr;
    }

    public static final byte[] decode(byte[] bArr) {
        int length = bArr.length;
        int i = 0;
        for (byte b : bArr) {
            if (IA[b & UnsignedBytes.MAX_VALUE] < 0) {
                i++;
            }
        }
        if ((length - i) % 4 != 0) {
            return null;
        }
        int i2 = length;
        int i3 = 0;
        while (i2 > 1) {
            i2--;
            if (IA[bArr[i2] & UnsignedBytes.MAX_VALUE] > 0) {
                break;
            }
            if (bArr[i2] == 61) {
                i3++;
            }
        }
        int i4 = (((length - i) * 6) >> 3) - i3;
        byte[] bArr2 = new byte[i4];
        int i5 = 0;
        int i6 = 0;
        while (i5 < i4) {
            int i7 = 0;
            int i8 = i6;
            int i9 = 0;
            while (i9 < 4) {
                int i10 = i8 + 1;
                int i11 = IA[bArr[i8] & UnsignedBytes.MAX_VALUE];
                if (i11 < 0) {
                    i9--;
                } else {
                    i7 |= i11 << (18 - (i9 * 6));
                }
                i9++;
                i8 = i10;
            }
            int i12 = i5 + 1;
            bArr2[i5] = (byte) (i7 >> 16);
            if (i12 < i4) {
                int i13 = i12 + 1;
                bArr2[i12] = (byte) (i7 >> 8);
                if (i13 >= i4) {
                    i12 = i13;
                } else {
                    i12 = i13 + 1;
                    bArr2[i13] = (byte) i7;
                }
            }
            i5 = i12;
            i6 = i8;
        }
        return bArr2;
    }

    public static final byte[] decode(char[] cArr) {
        int length = cArr == null ? 0 : cArr.length;
        if (length == 0) {
            return new byte[0];
        }
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            if (IA[cArr[i2]] < 0) {
                i++;
            }
        }
        if ((length - i) % 4 != 0) {
            return null;
        }
        int i3 = length;
        int i4 = 0;
        while (i3 > 1) {
            i3--;
            if (IA[cArr[i3]] > 0) {
                break;
            }
            if (cArr[i3] == '=') {
                i4++;
            }
        }
        int i5 = (((length - i) * 6) >> 3) - i4;
        byte[] bArr = new byte[i5];
        int i6 = 0;
        int i7 = 0;
        while (i6 < i5) {
            int i8 = 0;
            int i9 = i7;
            int i10 = 0;
            while (i10 < 4) {
                int i11 = i9 + 1;
                int i12 = IA[cArr[i9]];
                if (i12 < 0) {
                    i10--;
                } else {
                    i8 |= i12 << (18 - (i10 * 6));
                }
                i10++;
                i9 = i11;
            }
            int i13 = i6 + 1;
            bArr[i6] = (byte) (i8 >> 16);
            if (i13 < i5) {
                int i14 = i13 + 1;
                bArr[i13] = (byte) (i8 >> 8);
                if (i14 >= i5) {
                    i13 = i14;
                } else {
                    i13 = i14 + 1;
                    bArr[i14] = (byte) i8;
                }
            }
            i6 = i13;
            i7 = i9;
        }
        return bArr;
    }

    public static final byte[] decodeFast(String str) {
        int i = 0;
        int length = str.length();
        if (length == 0) {
            return new byte[0];
        }
        int i2 = length - 1;
        int i3 = 0;
        while (i3 < i2 && IA[str.charAt(i3) & 255] < 0) {
            i3++;
        }
        int i4 = i2;
        while (i4 > 0 && IA[str.charAt(i4) & 255] < 0) {
            i4--;
        }
        int i5 = str.charAt(i4) != '=' ? 0 : str.charAt(i4 + (-1)) != '=' ? 1 : 2;
        int i6 = (i4 - i3) + 1;
        int i7 = length <= 76 ? 0 : (str.charAt(76) != '\r' ? 0 : i6 / 78) << 1;
        int i8 = (((i6 - i7) * 6) >> 3) - i5;
        byte[] bArr = new byte[i8];
        int i9 = (i8 / 3) * 3;
        int i10 = 0;
        int i11 = 0;
        while (i11 < i9) {
            int i12 = i3 + 1;
            int i13 = i12 + 1;
            int i14 = (IA[str.charAt(i3)] << 18) | (IA[str.charAt(i12)] << 12);
            int i15 = i13 + 1;
            int i16 = (IA[str.charAt(i13)] << 6) | i14;
            i3 = i15 + 1;
            int i17 = i16 | IA[str.charAt(i15)];
            int i18 = i11 + 1;
            bArr[i11] = (byte) (i17 >> 16);
            int i19 = i18 + 1;
            bArr[i18] = (byte) (i17 >> 8);
            i11 = i19 + 1;
            bArr[i19] = (byte) i17;
            if (i7 > 0 && (i10 = i10 + 1) == 19) {
                i3 += 2;
                i10 = 0;
            }
        }
        if (i11 < i8) {
            int i20 = 0;
            while (true) {
                int i21 = i3;
                if (i21 > i4 - i5) {
                    break;
                }
                i3 = i21 + 1;
                int i22 = (IA[str.charAt(i21)] << (18 - (i * 6))) | i20;
                i++;
                i20 = i22;
            }
            int i23 = 16;
            for (int i24 = i11; i24 < i8; i24++) {
                bArr[i24] = (byte) (i20 >> i23);
                i23 -= 8;
            }
        }
        return bArr;
    }

    public static final byte[] decodeFast(byte[] bArr) {
        int i = 0;
        int length = bArr.length;
        if (length == 0) {
            return new byte[0];
        }
        int i2 = length - 1;
        int i3 = 0;
        while (i3 < i2 && IA[bArr[i3] & UnsignedBytes.MAX_VALUE] < 0) {
            i3++;
        }
        int i4 = i2;
        while (i4 > 0 && IA[bArr[i4] & UnsignedBytes.MAX_VALUE] < 0) {
            i4--;
        }
        int i5 = bArr[i4] != 61 ? 0 : bArr[i4 + (-1)] != 61 ? 1 : 2;
        int i6 = (i4 - i3) + 1;
        int i7 = length <= 76 ? 0 : (bArr[76] != 13 ? 0 : i6 / 78) << 1;
        int i8 = (((i6 - i7) * 6) >> 3) - i5;
        byte[] bArr2 = new byte[i8];
        int i9 = (i8 / 3) * 3;
        int i10 = 0;
        int i11 = 0;
        while (i11 < i9) {
            int i12 = i3 + 1;
            int i13 = i12 + 1;
            int i14 = (IA[bArr[i3]] << 18) | (IA[bArr[i12]] << 12);
            int i15 = i13 + 1;
            int i16 = (IA[bArr[i13]] << 6) | i14;
            i3 = i15 + 1;
            int i17 = i16 | IA[bArr[i15]];
            int i18 = i11 + 1;
            bArr2[i11] = (byte) (i17 >> 16);
            int i19 = i18 + 1;
            bArr2[i18] = (byte) (i17 >> 8);
            i11 = i19 + 1;
            bArr2[i19] = (byte) i17;
            if (i7 > 0 && (i10 = i10 + 1) == 19) {
                i3 += 2;
                i10 = 0;
            }
        }
        if (i11 < i8) {
            int i20 = 0;
            while (true) {
                int i21 = i3;
                if (i21 > i4 - i5) {
                    break;
                }
                i3 = i21 + 1;
                int i22 = (IA[bArr[i21]] << (18 - (i * 6))) | i20;
                i++;
                i20 = i22;
            }
            int i23 = 16;
            for (int i24 = i11; i24 < i8; i24++) {
                bArr2[i24] = (byte) (i20 >> i23);
                i23 -= 8;
            }
        }
        return bArr2;
    }

    public static final byte[] decodeFast(char[] cArr) {
        int i = 0;
        int length = cArr.length;
        if (length == 0) {
            return new byte[0];
        }
        int i2 = length - 1;
        int i3 = 0;
        while (i3 < i2 && IA[cArr[i3]] < 0) {
            i3++;
        }
        int i4 = i2;
        while (i4 > 0 && IA[cArr[i4]] < 0) {
            i4--;
        }
        int i5 = cArr[i4] != '=' ? 0 : cArr[i4 + (-1)] != '=' ? 1 : 2;
        int i6 = (i4 - i3) + 1;
        int i7 = length <= 76 ? 0 : (cArr[76] != '\r' ? 0 : i6 / 78) << 1;
        int i8 = (((i6 - i7) * 6) >> 3) - i5;
        byte[] bArr = new byte[i8];
        int i9 = (i8 / 3) * 3;
        int i10 = 0;
        int i11 = 0;
        while (i11 < i9) {
            int i12 = i3 + 1;
            int i13 = i12 + 1;
            int i14 = (IA[cArr[i3]] << 18) | (IA[cArr[i12]] << 12);
            int i15 = i13 + 1;
            int i16 = (IA[cArr[i13]] << 6) | i14;
            i3 = i15 + 1;
            int i17 = i16 | IA[cArr[i15]];
            int i18 = i11 + 1;
            bArr[i11] = (byte) (i17 >> 16);
            int i19 = i18 + 1;
            bArr[i18] = (byte) (i17 >> 8);
            i11 = i19 + 1;
            bArr[i19] = (byte) i17;
            if (i7 > 0 && (i10 = i10 + 1) == 19) {
                i3 += 2;
                i10 = 0;
            }
        }
        if (i11 < i8) {
            int i20 = 0;
            while (true) {
                int i21 = i3;
                if (i21 > i4 - i5) {
                    break;
                }
                i3 = i21 + 1;
                int i22 = (IA[cArr[i21]] << (18 - (i * 6))) | i20;
                i++;
                i20 = i22;
            }
            int i23 = 16;
            for (int i24 = i11; i24 < i8; i24++) {
                bArr[i24] = (byte) (i20 >> i23);
                i23 -= 8;
            }
        }
        return bArr;
    }

    public static final byte[] encodeToByte(byte[] bArr, boolean z) {
        int length = bArr == null ? 0 : bArr.length;
        if (length == 0) {
            return new byte[0];
        }
        int i = (length / 3) * 3;
        int i2 = (((length - 1) / 3) + 1) << 2;
        int i3 = i2 + (!z ? 0 : ((i2 - 1) / 76) << 1);
        byte[] bArr2 = new byte[i3];
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i6 < i) {
            int i7 = i6 + 1;
            int i8 = i7 + 1;
            int i9 = ((bArr[i7] & UnsignedBytes.MAX_VALUE) << 8) | ((bArr[i6] & UnsignedBytes.MAX_VALUE) << 16);
            i6 = i8 + 1;
            int i10 = i9 | (bArr[i8] & UnsignedBytes.MAX_VALUE);
            int i11 = i5 + 1;
            bArr2[i5] = (byte) CA[(i10 >>> 18) & 63];
            int i12 = i11 + 1;
            bArr2[i11] = (byte) CA[(i10 >>> 12) & 63];
            int i13 = i12 + 1;
            bArr2[i12] = (byte) CA[(i10 >>> 6) & 63];
            i5 = i13 + 1;
            bArr2[i13] = (byte) CA[i10 & 63];
            if (z && (i4 = i4 + 1) == 19 && i5 < i3 - 2) {
                int i14 = i5 + 1;
                bArr2[i5] = Ascii.CR;
                bArr2[i14] = 10;
                i5 = i14 + 1;
                i4 = 0;
            }
        }
        int i15 = length - i;
        if (i15 > 0) {
            int i16 = ((bArr[i] & UnsignedBytes.MAX_VALUE) << 10) | (i15 == 2 ? (bArr[length - 1] & UnsignedBytes.MAX_VALUE) << 2 : 0);
            bArr2[i3 - 4] = (byte) CA[i16 >> 12];
            bArr2[i3 - 3] = (byte) CA[(i16 >>> 6) & 63];
            bArr2[i3 - 2] = i15 != 2 ? (byte) 61 : (byte) CA[i16 & 63];
            bArr2[i3 - 1] = 61;
        }
        return bArr2;
    }

    public static final char[] encodeToChar(byte[] bArr, boolean z) {
        int length = bArr == null ? 0 : bArr.length;
        if (length == 0) {
            return new char[0];
        }
        int i = (length / 3) * 3;
        int i2 = (((length - 1) / 3) + 1) << 2;
        int i3 = i2 + (!z ? 0 : ((i2 - 1) / 76) << 1);
        char[] cArr = new char[i3];
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i6 < i) {
            int i7 = i6 + 1;
            int i8 = i7 + 1;
            int i9 = ((bArr[i7] & UnsignedBytes.MAX_VALUE) << 8) | ((bArr[i6] & UnsignedBytes.MAX_VALUE) << 16);
            i6 = i8 + 1;
            int i10 = i9 | (bArr[i8] & UnsignedBytes.MAX_VALUE);
            int i11 = i5 + 1;
            cArr[i5] = CA[(i10 >>> 18) & 63];
            int i12 = i11 + 1;
            cArr[i11] = CA[(i10 >>> 12) & 63];
            int i13 = i12 + 1;
            cArr[i12] = CA[(i10 >>> 6) & 63];
            i5 = i13 + 1;
            cArr[i13] = CA[i10 & 63];
            if (z && (i4 = i4 + 1) == 19 && i5 < i3 - 2) {
                int i14 = i5 + 1;
                cArr[i5] = '\r';
                cArr[i14] = '\n';
                i5 = i14 + 1;
                i4 = 0;
            }
        }
        int i15 = length - i;
        if (i15 > 0) {
            int i16 = ((bArr[i] & UnsignedBytes.MAX_VALUE) << 10) | (i15 == 2 ? (bArr[length - 1] & UnsignedBytes.MAX_VALUE) << 2 : 0);
            cArr[i3 - 4] = CA[i16 >> 12];
            cArr[i3 - 3] = CA[(i16 >>> 6) & 63];
            cArr[i3 - 2] = i15 != 2 ? '=' : CA[i16 & 63];
            cArr[i3 - 1] = '=';
        }
        return cArr;
    }

    public static final String encodeToString(byte[] bArr, boolean z) {
        return new String(encodeToChar(bArr, z));
    }
}
