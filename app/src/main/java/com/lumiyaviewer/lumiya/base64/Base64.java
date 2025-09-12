package com.lumiyaviewer.lumiya.base64;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import java.util.Arrays;

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
            } else if (str.charAt(i3) == '=') {
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
            bArr[i6] = (byte) ((byte) (i8 >> 16));
            if (i13 < i5) {
                int i14 = i13 + 1;
                bArr[i13] = (byte) ((byte) (i8 >> 8));
                if (i14 >= i5) {
                    i13 = i14;
                } else {
                    i13 = i14 + 1;
                    bArr[i14] = (byte) ((byte) i8);
                }
            }
            i6 = i13;
            i7 = i9;
        }
        return bArr;
    }

    public static final byte[] decode(byte[] bArr) {
        int i = 0;
        for (byte b : bArr) {
            if (IA[b & UnsignedBytes.MAX_VALUE] < 0) {
                i++;
            }
        }
        if ((r2 - i) % 4 != 0) {
            return null;
        }
        int i2 = r2;
        int i3 = 0;
        while (i2 > 1) {
            i2--;
            if (IA[bArr[i2] & UnsignedBytes.MAX_VALUE] > 0) {
                break;
            } else if (bArr[i2] == 61) {
                i3++;
            }
        }
        int i4 = (((r2 - i) * 6) >> 3) - i3;
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
            bArr2[i5] = (byte) ((byte) (i7 >> 16));
            if (i12 < i4) {
                int i13 = i12 + 1;
                bArr2[i12] = (byte) ((byte) (i7 >> 8));
                if (i13 >= i4) {
                    i12 = i13;
                } else {
                    i12 = i13 + 1;
                    bArr2[i13] = (byte) ((byte) i7);
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
            } else if (cArr[i3] == '=') {
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
            bArr[i6] = (byte) ((byte) (i8 >> 16));
            if (i13 < i5) {
                int i14 = i13 + 1;
                bArr[i13] = (byte) ((byte) (i8 >> 8));
                if (i14 >= i5) {
                    i13 = i14;
                } else {
                    i13 = i14 + 1;
                    bArr[i14] = (byte) ((byte) i8);
                }
            }
            i6 = i13;
            i7 = i9;
        }
        return bArr;
    }

    public static final byte[] decodeFast(String str) {
        int i;
        int i2;
        int i3 = 0;
        int length = str.length();
        if (length == 0) {
            return new byte[0];
        }
        int i4 = length - 1;
        int i5 = 0;
        while (i < i4 && IA[str.charAt(i) & 255] < 0) {
            i5 = i + 1;
        }
        int i6 = i4;
        while (i6 > 0 && IA[str.charAt(i6) & 255] < 0) {
            i6--;
        }
        int i7 = str.charAt(i6) != '=' ? 0 : str.charAt(i6 + -1) != '=' ? 1 : 2;
        int i8 = (i6 - i) + 1;
        if (length <= 76) {
            i2 = 0;
        } else {
            i2 = (str.charAt(76) != 13 ? 0 : i8 / 78) << 1;
        }
        int i9 = (((i8 - i2) * 6) >> 3) - i7;
        byte[] bArr = new byte[i9];
        int i10 = (i9 / 3) * 3;
        int i11 = 0;
        int i12 = 0;
        while (i12 < i10) {
            int i13 = i + 1;
            int i14 = i13 + 1;
            int i15 = (IA[str.charAt(i)] << 18) | (IA[str.charAt(i13)] << 12);
            int i16 = i14 + 1;
            int i17 = (IA[str.charAt(i14)] << 6) | i15;
            i = i16 + 1;
            int i18 = i17 | IA[str.charAt(i16)];
            int i19 = i12 + 1;
            bArr[i12] = (byte) ((byte) (i18 >> 16));
            int i20 = i19 + 1;
            bArr[i19] = (byte) ((byte) (i18 >> 8));
            i12 = i20 + 1;
            bArr[i20] = (byte) ((byte) i18);
            if (i2 > 0 && (i11 = i11 + 1) == 19) {
                i += 2;
                i11 = 0;
            }
        }
        if (i12 < i9) {
            int i21 = 0;
            while (true) {
                int i22 = i;
                if (i22 > i6 - i7) {
                    break;
                }
                i = i22 + 1;
                i3++;
                i21 = (IA[str.charAt(i22)] << (18 - (i3 * 6))) | i21;
            }
            int i23 = 16;
            for (int i24 = i12; i24 < i9; i24++) {
                bArr[i24] = (byte) ((byte) (i21 >> i23));
                i23 -= 8;
            }
        }
        return bArr;
    }

    public static final byte[] decodeFast(byte[] bArr) {
        int i;
        int i2;
        int i3 = 0;
        int length = bArr.length;
        if (length == 0) {
            return new byte[0];
        }
        int i4 = length - 1;
        int i5 = 0;
        while (i < i4 && IA[bArr[i] & UnsignedBytes.MAX_VALUE] < 0) {
            i5 = i + 1;
        }
        int i6 = i4;
        while (i6 > 0 && IA[bArr[i6] & UnsignedBytes.MAX_VALUE] < 0) {
            i6--;
        }
        int i7 = bArr[i6] != 61 ? 0 : bArr[i6 + -1] != 61 ? 1 : 2;
        int i8 = (i6 - i) + 1;
        if (length <= 76) {
            i2 = 0;
        } else {
            i2 = (bArr[76] != 13 ? 0 : i8 / 78) << 1;
        }
        int i9 = (((i8 - i2) * 6) >> 3) - i7;
        byte[] bArr2 = new byte[i9];
        int i10 = (i9 / 3) * 3;
        int i11 = 0;
        int i12 = 0;
        while (i12 < i10) {
            int i13 = i + 1;
            int i14 = i13 + 1;
            int i15 = (IA[bArr[i]] << 18) | (IA[bArr[i13]] << 12);
            int i16 = i14 + 1;
            int i17 = (IA[bArr[i14]] << 6) | i15;
            i = i16 + 1;
            int i18 = i17 | IA[bArr[i16]];
            int i19 = i12 + 1;
            bArr2[i12] = (byte) ((byte) (i18 >> 16));
            int i20 = i19 + 1;
            bArr2[i19] = (byte) ((byte) (i18 >> 8));
            i12 = i20 + 1;
            bArr2[i20] = (byte) ((byte) i18);
            if (i2 > 0 && (i11 = i11 + 1) == 19) {
                i += 2;
                i11 = 0;
            }
        }
        if (i12 < i9) {
            int i21 = 0;
            while (true) {
                int i22 = i;
                if (i22 > i6 - i7) {
                    break;
                }
                i = i22 + 1;
                i3++;
                i21 = (IA[bArr[i22]] << (18 - (i3 * 6))) | i21;
            }
            int i23 = 16;
            for (int i24 = i12; i24 < i9; i24++) {
                bArr2[i24] = (byte) ((byte) (i21 >> i23));
                i23 -= 8;
            }
        }
        return bArr2;
    }

    public static final byte[] decodeFast(char[] cArr) {
        int i;
        int i2;
        int i3 = 0;
        int length = cArr.length;
        if (length == 0) {
            return new byte[0];
        }
        int i4 = length - 1;
        int i5 = 0;
        while (i < i4 && IA[cArr[i]] < 0) {
            i5 = i + 1;
        }
        int i6 = i4;
        while (i6 > 0 && IA[cArr[i6]] < 0) {
            i6--;
        }
        int i7 = cArr[i6] != '=' ? 0 : cArr[i6 + -1] != '=' ? 1 : 2;
        int i8 = (i6 - i) + 1;
        if (length <= 76) {
            i2 = 0;
        } else {
            i2 = (cArr[76] != 13 ? 0 : i8 / 78) << 1;
        }
        int i9 = (((i8 - i2) * 6) >> 3) - i7;
        byte[] bArr = new byte[i9];
        int i10 = (i9 / 3) * 3;
        int i11 = 0;
        int i12 = 0;
        while (i12 < i10) {
            int i13 = i + 1;
            int i14 = i13 + 1;
            int i15 = (IA[cArr[i]] << 18) | (IA[cArr[i13]] << 12);
            int i16 = i14 + 1;
            int i17 = (IA[cArr[i14]] << 6) | i15;
            i = i16 + 1;
            int i18 = i17 | IA[cArr[i16]];
            int i19 = i12 + 1;
            bArr[i12] = (byte) ((byte) (i18 >> 16));
            int i20 = i19 + 1;
            bArr[i19] = (byte) ((byte) (i18 >> 8));
            i12 = i20 + 1;
            bArr[i20] = (byte) ((byte) i18);
            if (i2 > 0 && (i11 = i11 + 1) == 19) {
                i += 2;
                i11 = 0;
            }
        }
        if (i12 < i9) {
            int i21 = 0;
            while (true) {
                int i22 = i;
                if (i22 > i6 - i7) {
                    break;
                }
                i = i22 + 1;
                i3++;
                i21 = (IA[cArr[i22]] << (18 - (i3 * 6))) | i21;
            }
            int i23 = 16;
            for (int i24 = i12; i24 < i9; i24++) {
                bArr[i24] = (byte) ((byte) (i21 >> i23));
                i23 -= 8;
            }
        }
        return bArr;
    }

    public static final byte[] encodeToByte(byte[] bArr, boolean z) {
        int i = 0;
        int length = bArr == null ? 0 : bArr.length;
        if (length == 0) {
            return new byte[0];
        }
        int i2 = (length / 3) * 3;
        int i3 = (((length - 1) / 3) + 1) << 2;
        int i4 = i3 + (!z ? 0 : ((i3 - 1) / 76) << 1);
        byte[] bArr2 = new byte[i4];
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        while (i7 < i2) {
            int i8 = i7 + 1;
            int i9 = i8 + 1;
            byte b = ((bArr[i8] & UnsignedBytes.MAX_VALUE) << 8) | ((bArr[i7] & UnsignedBytes.MAX_VALUE) << 16);
            i7 = i9 + 1;
            byte b2 = b | (bArr[i9] & UnsignedBytes.MAX_VALUE);
            int i10 = i6 + 1;
            bArr2[i6] = (byte) ((byte) CA[(b2 >>> Ascii.DC2) & 63]);
            int i11 = i10 + 1;
            bArr2[i10] = (byte) ((byte) CA[(b2 >>> Ascii.FF) & 63]);
            int i12 = i11 + 1;
            bArr2[i11] = (byte) ((byte) CA[(b2 >>> 6) & 63]);
            i6 = i12 + 1;
            bArr2[i12] = (byte) ((byte) CA[b2 & 63]);
            if (z && (i5 = i5 + 1) == 19 && i6 < i4 - 2) {
                int i13 = i6 + 1;
                bArr2[i6] = Ascii.CR;
                bArr2[i13] = 10;
                i6 = i13 + 1;
                i5 = 0;
            }
        }
        int i14 = length - i2;
        if (i14 > 0) {
            int i15 = (bArr[i2] & UnsignedBytes.MAX_VALUE) << 10;
            if (i14 == 2) {
                i = (bArr[length - 1] & UnsignedBytes.MAX_VALUE) << 2;
            }
            int i16 = i15 | i;
            bArr2[i4 - 4] = (byte) ((byte) CA[i16 >> 12]);
            bArr2[i4 - 3] = (byte) ((byte) CA[(i16 >>> 6) & 63]);
            bArr2[i4 - 2] = (byte) (i14 != 2 ? 61 : (byte) CA[i16 & 63]);
            bArr2[i4 - 1] = 61;
        }
        return bArr2;
    }

    public static final char[] encodeToChar(byte[] bArr, boolean z) {
        int i = 0;
        int length = bArr == null ? 0 : bArr.length;
        if (length == 0) {
            return new char[0];
        }
        int i2 = (length / 3) * 3;
        int i3 = (((length - 1) / 3) + 1) << 2;
        int i4 = i3 + (!z ? 0 : ((i3 - 1) / 76) << 1);
        char[] cArr = new char[i4];
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        while (i7 < i2) {
            int i8 = i7 + 1;
            int i9 = i8 + 1;
            byte b = ((bArr[i8] & UnsignedBytes.MAX_VALUE) << 8) | ((bArr[i7] & UnsignedBytes.MAX_VALUE) << 16);
            i7 = i9 + 1;
            byte b2 = b | (bArr[i9] & UnsignedBytes.MAX_VALUE);
            int i10 = i6 + 1;
            cArr[i6] = (char) CA[(b2 >>> Ascii.DC2) & 63];
            int i11 = i10 + 1;
            cArr[i10] = (char) CA[(b2 >>> Ascii.FF) & 63];
            int i12 = i11 + 1;
            cArr[i11] = (char) CA[(b2 >>> 6) & 63];
            i6 = i12 + 1;
            cArr[i12] = (char) CA[b2 & 63];
            if (z && (i5 = i5 + 1) == 19 && i6 < i4 - 2) {
                int i13 = i6 + 1;
                cArr[i6] = 13;
                cArr[i13] = 10;
                i6 = i13 + 1;
                i5 = 0;
            }
        }
        int i14 = length - i2;
        if (i14 > 0) {
            int i15 = (bArr[i2] & UnsignedBytes.MAX_VALUE) << 10;
            if (i14 == 2) {
                i = (bArr[length - 1] & UnsignedBytes.MAX_VALUE) << 2;
            }
            int i16 = i15 | i;
            cArr[i4 - 4] = (char) CA[i16 >> 12];
            cArr[i4 - 3] = (char) CA[(i16 >>> 6) & 63];
            cArr[i4 - 2] = (char) (i14 != 2 ? '=' : CA[i16 & 63]);
            cArr[i4 - 1] = '=';
        }
        return cArr;
    }

    public static final String encodeToString(byte[] bArr, boolean z) {
        return new String(encodeToChar(bArr, z));
    }
}
