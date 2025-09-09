package com.lumiyaviewer.lumiya.base64;

import com.google.common.base.Ascii;
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
        int i;
        int i2 = 0;
        for (i = 0; i < length; i++) {
            if (IA[str.charAt(i)] < 0) {
                i2++;
            }
        }
        if ((length - i2) % 4 != 0) {
            return null;
        }
        i = length;
        int i3 = 0;
        while (i > 1) {
            i--;
            if (IA[str.charAt(i)] > 0) {
                break;
            } else if (str.charAt(i) == '=') {
                i3++;
            }
        }
        int i4 = (((length - i2) * 6) >> 3) - i3;
        byte[] bArr = new byte[i4];
        int i5 = 0;
        length = 0;
        while (i5 < i4) {
            i2 = 0;
            i3 = length;
            length = 0;
            while (length < 4) {
                i = i3 + 1;
                i3 = IA[str.charAt(i3)];
                if (i3 < 0) {
                    length--;
                } else {
                    i2 |= i3 << (18 - (length * 6));
                }
                length++;
                i3 = i;
            }
            length = i5 + 1;
            bArr[i5] = (byte) ((byte) (i2 >> 16));
            if (length < i4) {
                i = length + 1;
                bArr[length] = (byte) ((byte) (i2 >> 8));
                if (i >= i4) {
                    length = i;
                } else {
                    length = i + 1;
                    bArr[i] = (byte) ((byte) i2);
                }
            }
            i5 = length;
            length = i3;
        }
        return bArr;
    }

    public static final byte[] decode(byte[] bArr) {
        int i;
        int i2;
        int i3 = 0;
        for (byte b : bArr) {
            if (IA[b & 255] < 0) {
                i3++;
            }
        }
        if ((i2 - i3) % 4 != 0) {
            return null;
        }
        i = i2;
        int i4 = 0;
        while (i > 1) {
            i--;
            if (IA[bArr[i] & 255] > 0) {
                break;
            } else if (bArr[i] == (byte) 61) {
                i4++;
            }
        }
        int i5 = (((i2 - i3) * 6) >> 3) - i4;
        byte[] bArr2 = new byte[i5];
        int i6 = 0;
        i3 = 0;
        while (i6 < i5) {
            i = 0;
            i4 = i3;
            i3 = 0;
            while (i3 < 4) {
                i2 = i4 + 1;
                i4 = IA[bArr[i4] & 255];
                if (i4 < 0) {
                    i3--;
                } else {
                    i |= i4 << (18 - (i3 * 6));
                }
                i3++;
                i4 = i2;
            }
            i3 = i6 + 1;
            bArr2[i6] = (byte) ((byte) (i >> 16));
            if (i3 < i5) {
                i2 = i3 + 1;
                bArr2[i3] = (byte) ((byte) (i >> 8));
                if (i2 >= i5) {
                    i3 = i2;
                } else {
                    i3 = i2 + 1;
                    bArr2[i2] = (byte) ((byte) i);
                }
            }
            i6 = i3;
            i3 = i4;
        }
        return bArr2;
    }

    public static final byte[] decode(char[] cArr) {
        int length = cArr == null ? 0 : cArr.length;
        if (length == 0) {
            return new byte[0];
        }
        int i;
        int i2 = 0;
        for (i = 0; i < length; i++) {
            if (IA[cArr[i]] < 0) {
                i2++;
            }
        }
        if ((length - i2) % 4 != 0) {
            return null;
        }
        i = length;
        int i3 = 0;
        while (i > 1) {
            i--;
            if (IA[cArr[i]] > 0) {
                break;
            } else if (cArr[i] == '=') {
                i3++;
            }
        }
        int i4 = (((length - i2) * 6) >> 3) - i3;
        byte[] bArr = new byte[i4];
        int i5 = 0;
        length = 0;
        while (i5 < i4) {
            i2 = 0;
            i3 = length;
            length = 0;
            while (length < 4) {
                i = i3 + 1;
                i3 = IA[cArr[i3]];
                if (i3 < 0) {
                    length--;
                } else {
                    i2 |= i3 << (18 - (length * 6));
                }
                length++;
                i3 = i;
            }
            length = i5 + 1;
            bArr[i5] = (byte) ((byte) (i2 >> 16));
            if (length < i4) {
                i = length + 1;
                bArr[length] = (byte) ((byte) (i2 >> 8));
                if (i >= i4) {
                    length = i;
                } else {
                    length = i + 1;
                    bArr[i] = (byte) ((byte) i2);
                }
            }
            i5 = length;
            length = i3;
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
        i2 = str.charAt(i4) != '=' ? 0 : str.charAt(i4 + -1) != '=' ? 1 : 2;
        int i5 = (i4 - i3) + 1;
        if (length <= 76) {
            length = 0;
        } else {
            length = (str.charAt(76) != 13 ? 0 : i5 / 78) << 1;
        }
        int i6 = (((i5 - length) * 6) >> 3) - i2;
        byte[] bArr = new byte[i6];
        int i7 = (i6 / 3) * 3;
        i5 = 0;
        int i8 = 0;
        while (i8 < i7) {
            int i9 = i3 + 1;
            int i10 = i9 + 1;
            i3 = (IA[str.charAt(i3)] << 18) | (IA[str.charAt(i9)] << 12);
            i9 = i10 + 1;
            int i11 = (IA[str.charAt(i10)] << 6) | i3;
            i3 = i9 + 1;
            i11 |= IA[str.charAt(i9)];
            i9 = i8 + 1;
            bArr[i8] = (byte) ((byte) (i11 >> 16));
            i10 = i9 + 1;
            bArr[i9] = (byte) ((byte) (i11 >> 8));
            i8 = i10 + 1;
            bArr[i10] = (byte) ((byte) i11);
            if (length > 0) {
                i5++;
                if (i5 == 19) {
                    i3 += 2;
                    i5 = 0;
                }
            }
        }
        if (i8 < i6) {
            i5 = 0;
            while (true) {
                length = i3;
                if (length > i4 - i2) {
                    break;
                }
                i3 = length + 1;
                i++;
                i5 = (IA[str.charAt(length)] << (18 - (i * 6))) | i5;
            }
            i2 = 16;
            i = i8;
            while (i < i6) {
                length = i + 1;
                bArr[i] = (byte) ((byte) (i5 >> i2));
                i2 -= 8;
                i = length;
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
        while (i3 < i2 && IA[bArr[i3] & 255] < 0) {
            i3++;
        }
        int i4 = i2;
        while (i4 > 0 && IA[bArr[i4] & 255] < 0) {
            i4--;
        }
        i2 = bArr[i4] != (byte) 61 ? 0 : bArr[i4 + -1] != (byte) 61 ? 1 : 2;
        int i5 = (i4 - i3) + 1;
        if (length <= 76) {
            length = 0;
        } else {
            length = (bArr[76] != Ascii.CR ? 0 : i5 / 78) << 1;
        }
        int i6 = (((i5 - length) * 6) >> 3) - i2;
        byte[] bArr2 = new byte[i6];
        int i7 = (i6 / 3) * 3;
        i5 = 0;
        int i8 = 0;
        while (i8 < i7) {
            int i9 = i3 + 1;
            int i10 = i9 + 1;
            i3 = (IA[bArr[i3]] << 18) | (IA[bArr[i9]] << 12);
            i9 = i10 + 1;
            int i11 = (IA[bArr[i10]] << 6) | i3;
            i3 = i9 + 1;
            i11 |= IA[bArr[i9]];
            i9 = i8 + 1;
            bArr2[i8] = (byte) ((byte) (i11 >> 16));
            i10 = i9 + 1;
            bArr2[i9] = (byte) ((byte) (i11 >> 8));
            i8 = i10 + 1;
            bArr2[i10] = (byte) ((byte) i11);
            if (length > 0) {
                i5++;
                if (i5 == 19) {
                    i3 += 2;
                    i5 = 0;
                }
            }
        }
        if (i8 < i6) {
            i5 = 0;
            while (true) {
                length = i3;
                if (length > i4 - i2) {
                    break;
                }
                i3 = length + 1;
                i++;
                i5 = (IA[bArr[length]] << (18 - (i * 6))) | i5;
            }
            i2 = 16;
            i = i8;
            while (i < i6) {
                length = i + 1;
                bArr2[i] = (byte) ((byte) (i5 >> i2));
                i2 -= 8;
                i = length;
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
        i2 = cArr[i4] != '=' ? 0 : cArr[i4 + -1] != '=' ? 1 : 2;
        int i5 = (i4 - i3) + 1;
        if (length <= 76) {
            length = 0;
        } else {
            length = (cArr[76] != 13 ? 0 : i5 / 78) << 1;
        }
        int i6 = (((i5 - length) * 6) >> 3) - i2;
        byte[] bArr = new byte[i6];
        int i7 = (i6 / 3) * 3;
        i5 = 0;
        int i8 = 0;
        while (i8 < i7) {
            int i9 = i3 + 1;
            int i10 = i9 + 1;
            i3 = (IA[cArr[i3]] << 18) | (IA[cArr[i9]] << 12);
            i9 = i10 + 1;
            int i11 = (IA[cArr[i10]] << 6) | i3;
            i3 = i9 + 1;
            i11 |= IA[cArr[i9]];
            i9 = i8 + 1;
            bArr[i8] = (byte) ((byte) (i11 >> 16));
            i10 = i9 + 1;
            bArr[i9] = (byte) ((byte) (i11 >> 8));
            i8 = i10 + 1;
            bArr[i10] = (byte) ((byte) i11);
            if (length > 0) {
                i5++;
                if (i5 == 19) {
                    i3 += 2;
                    i5 = 0;
                }
            }
        }
        if (i8 < i6) {
            i5 = 0;
            while (true) {
                length = i3;
                if (length > i4 - i2) {
                    break;
                }
                i3 = length + 1;
                i++;
                i5 = (IA[cArr[length]] << (18 - (i * 6))) | i5;
            }
            i2 = 16;
            i = i8;
            while (i < i6) {
                length = i + 1;
                bArr[i] = (byte) ((byte) (i5 >> i2));
                i2 -= 8;
                i = length;
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
        i3 = 0;
        int i6 = 0;
        while (i6 < i2) {
            int i7 = i6 + 1;
            int i8 = i7 + 1;
            i7 = ((bArr[i7] & 255) << 8) | ((bArr[i6] & 255) << 16);
            i6 = i8 + 1;
            i7 |= bArr[i8] & 255;
            i8 = i3 + 1;
            bArr2[i3] = (byte) ((byte) CA[(i7 >>> 18) & 63]);
            i3 = i8 + 1;
            bArr2[i8] = (byte) ((byte) CA[(i7 >>> 12) & 63]);
            i8 = i3 + 1;
            bArr2[i3] = (byte) ((byte) CA[(i7 >>> 6) & 63]);
            i3 = i8 + 1;
            bArr2[i8] = (byte) ((byte) CA[i7 & 63]);
            if (z) {
                i5++;
                if (i5 == 19 && i3 < i4 - 2) {
                    i7 = i3 + 1;
                    bArr2[i3] = Ascii.CR;
                    i5 = i7 + 1;
                    bArr2[i7] = (byte) 10;
                    i3 = i5;
                    i5 = 0;
                }
            }
        }
        i5 = length - i2;
        if (i5 > 0) {
            i3 = (bArr[i2] & 255) << 10;
            if (i5 == 2) {
                i = (bArr[length - 1] & 255) << 2;
            }
            length = i3 | i;
            bArr2[i4 - 4] = (byte) ((byte) CA[length >> 12]);
            bArr2[i4 - 3] = (byte) ((byte) CA[(length >>> 6) & 63]);
            bArr2[i4 - 2] = (byte) (i5 != 2 ? 61 : (byte) CA[length & 63]);
            bArr2[i4 - 1] = (byte) 61;
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
        i3 = 0;
        int i6 = 0;
        while (i6 < i2) {
            int i7 = i6 + 1;
            int i8 = i7 + 1;
            i7 = ((bArr[i7] & 255) << 8) | ((bArr[i6] & 255) << 16);
            i6 = i8 + 1;
            i7 |= bArr[i8] & 255;
            i8 = i3 + 1;
            cArr[i3] = (char) CA[(i7 >>> 18) & 63];
            i3 = i8 + 1;
            cArr[i8] = (char) CA[(i7 >>> 12) & 63];
            i8 = i3 + 1;
            cArr[i3] = (char) CA[(i7 >>> 6) & 63];
            i3 = i8 + 1;
            cArr[i8] = (char) CA[i7 & 63];
            if (z) {
                i5++;
                if (i5 == 19 && i3 < i4 - 2) {
                    i7 = i3 + 1;
                    cArr[i3] = 13;
                    i5 = i7 + 1;
                    cArr[i7] = 10;
                    i3 = i5;
                    i5 = 0;
                }
            }
        }
        i5 = length - i2;
        if (i5 > 0) {
            i3 = (bArr[i2] & 255) << 10;
            if (i5 == 2) {
                i = (bArr[length - 1] & 255) << 2;
            }
            length = i3 | i;
            cArr[i4 - 4] = (char) CA[length >> 12];
            cArr[i4 - 3] = (char) CA[(length >>> 6) & 63];
            cArr[i4 - 2] = (char) (i5 != 2 ? 61 : CA[length & 63]);
            cArr[i4 - 1] = '=';
        }
        return cArr;
    }

    public static final String encodeToString(byte[] bArr, boolean z) {
        return new String(encodeToChar(bArr, z));
    }
}
