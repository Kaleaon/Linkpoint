package okio;

import com.google.common.primitives.UnsignedBytes;
import java.io.UnsupportedEncodingException;
/* loaded from: classes.dex */
final class Base64 {
    private static final byte[] MAP = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] URL_MAP = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};

    private Base64() {
    }

    public static byte[] decode(String str) {
        int i;
        int i2;
        int i3;
        int i4;
        int length = str.length();
        while (length > 0) {
            char charAt = str.charAt(length - 1);
            if (charAt != '=' && charAt != '\n' && charAt != '\r' && charAt != ' ' && charAt != '\t') {
                break;
            }
            length--;
        }
        byte[] bArr = new byte[(int) ((length * 6) / 8)];
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        while (i5 < length) {
            char charAt2 = str.charAt(i5);
            if (charAt2 >= 'A' && charAt2 <= 'Z') {
                i = charAt2 - 'A';
            } else if (charAt2 >= 'a' && charAt2 <= 'z') {
                i = charAt2 - 'G';
            } else if (charAt2 >= '0' && charAt2 <= '9') {
                i = charAt2 + 4;
            } else if (charAt2 == '+' || charAt2 == '-') {
                i = 62;
            } else if (charAt2 == '/' || charAt2 == '_') {
                i = 63;
            } else {
                if (charAt2 == '\n' || charAt2 == '\r' || charAt2 == ' ') {
                    i4 = i6;
                    i3 = i7;
                    i2 = i8;
                } else if (charAt2 != '\t') {
                    return null;
                } else {
                    i4 = i6;
                    i3 = i7;
                    i2 = i8;
                }
                i5++;
                i8 = i2;
                i7 = i3;
                i6 = i4;
            }
            i4 = ((byte) i) | (i6 << 6);
            i3 = i7 + 1;
            if (i3 % 4 != 0) {
                i2 = i8;
            } else {
                int i9 = i8 + 1;
                bArr[i8] = (byte) (i4 >> 16);
                int i10 = i9 + 1;
                bArr[i9] = (byte) (i4 >> 8);
                i2 = i10 + 1;
                bArr[i10] = (byte) i4;
            }
            i5++;
            i8 = i2;
            i7 = i3;
            i6 = i4;
        }
        int i11 = i7 % 4;
        if (i11 != 1) {
            if (i11 == 2) {
                bArr[i8] = (byte) ((i6 << 12) >> 16);
                i8++;
            } else if (i11 == 3) {
                int i12 = i6 << 6;
                int i13 = i8 + 1;
                bArr[i8] = (byte) (i12 >> 16);
                i8 = i13 + 1;
                bArr[i13] = (byte) (i12 >> 8);
            }
            if (i8 != bArr.length) {
                byte[] bArr2 = new byte[i8];
                System.arraycopy(bArr, 0, bArr2, 0, i8);
                return bArr2;
            }
            return bArr;
        }
        return null;
    }

    public static String encode(byte[] bArr) {
        return encode(bArr, MAP);
    }

    private static String encode(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[((bArr.length + 2) / 3) * 4];
        int length = bArr.length - (bArr.length % 3);
        int i = 0;
        for (int i2 = 0; i2 < length; i2 += 3) {
            int i3 = i + 1;
            bArr3[i] = bArr2[(bArr[i2] & UnsignedBytes.MAX_VALUE) >> 2];
            int i4 = i3 + 1;
            bArr3[i3] = bArr2[((bArr[i2] & 3) << 4) | ((bArr[i2 + 1] & UnsignedBytes.MAX_VALUE) >> 4)];
            int i5 = i4 + 1;
            bArr3[i4] = bArr2[((bArr[i2 + 1] & 15) << 2) | ((bArr[i2 + 2] & UnsignedBytes.MAX_VALUE) >> 6)];
            i = i5 + 1;
            bArr3[i5] = bArr2[bArr[i2 + 2] & 63];
        }
        switch (bArr.length % 3) {
            case 1:
                int i6 = i + 1;
                bArr3[i] = bArr2[(bArr[length] & UnsignedBytes.MAX_VALUE) >> 2];
                int i7 = i6 + 1;
                bArr3[i6] = bArr2[(bArr[length] & 3) << 4];
                bArr3[i7] = 61;
                bArr3[i7 + 1] = 61;
                break;
            case 2:
                int i8 = i + 1;
                bArr3[i] = bArr2[(bArr[length] & UnsignedBytes.MAX_VALUE) >> 2];
                int i9 = i8 + 1;
                bArr3[i8] = bArr2[((bArr[length] & 3) << 4) | ((bArr[length + 1] & UnsignedBytes.MAX_VALUE) >> 4)];
                bArr3[i9] = bArr2[(bArr[length + 1] & 15) << 2];
                bArr3[i9 + 1] = 61;
                break;
        }
        try {
            return new String(bArr3, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static String encodeUrl(byte[] bArr) {
        return encode(bArr, URL_MAP);
    }
}
