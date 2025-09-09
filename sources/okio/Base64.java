package okio;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import java.io.UnsupportedEncodingException;

final class Base64 {
    private static final byte[] MAP = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] URL_MAP = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};

    private Base64() {
    }

    public static byte[] decode(String str) {
        int i;
        int i2;
        int i3;
        byte b;
        int length = str.length();
        while (length > 0) {
            char charAt = str.charAt(length - 1);
            if (charAt != '=' && charAt != 10 && charAt != 13 && charAt != ' ' && charAt != 9) {
                break;
            }
            length--;
        }
        byte[] bArr = new byte[((int) ((((long) length) * 6) / 8))];
        int i4 = 0;
        byte b2 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i4 < length) {
            char charAt2 = str.charAt(i4);
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
            } else if (charAt2 == 10 || charAt2 == 13 || charAt2 == ' ') {
                b = b2;
                i3 = i5;
                i2 = i6;
                i4++;
                i6 = i2;
                i5 = i3;
                b2 = b;
            } else if (charAt2 != 9) {
                return null;
            } else {
                b = b2;
                i3 = i5;
                i2 = i6;
                i4++;
                i6 = i2;
                i5 = i3;
                b2 = b;
            }
            b = ((byte) i) | (b2 << 6);
            i3 = i5 + 1;
            if (i3 % 4 != 0) {
                i2 = i6;
            } else {
                int i7 = i6 + 1;
                bArr[i6] = (byte) ((byte) (b >> 16));
                int i8 = i7 + 1;
                bArr[i7] = (byte) ((byte) (b >> 8));
                i2 = i8 + 1;
                bArr[i8] = (byte) ((byte) b);
            }
            i4++;
            i6 = i2;
            i5 = i3;
            b2 = b;
        }
        int i9 = i5 % 4;
        if (i9 == 1) {
            return null;
        }
        if (i9 == 2) {
            bArr[i6] = (byte) ((byte) ((b2 << Ascii.FF) >> 16));
            i6++;
        } else if (i9 == 3) {
            int i10 = b2 << 6;
            int i11 = i6 + 1;
            bArr[i6] = (byte) ((byte) (i10 >> 16));
            i6 = i11 + 1;
            bArr[i11] = (byte) ((byte) (i10 >> 8));
        }
        if (i6 == bArr.length) {
            return bArr;
        }
        byte[] bArr2 = new byte[i6];
        System.arraycopy(bArr, 0, bArr2, 0, i6);
        return bArr2;
    }

    public static String encode(byte[] bArr) {
        return encode(bArr, MAP);
    }

    private static String encode(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[(((bArr.length + 2) / 3) * 4)];
        int length = bArr.length - (bArr.length % 3);
        int i = 0;
        for (int i2 = 0; i2 < length; i2 += 3) {
            int i3 = i + 1;
            bArr3[i] = (byte) bArr2[(bArr[i2] & UnsignedBytes.MAX_VALUE) >> 2];
            int i4 = i3 + 1;
            bArr3[i3] = (byte) bArr2[((bArr[i2] & 3) << 4) | ((bArr[i2 + 1] & UnsignedBytes.MAX_VALUE) >> 4)];
            int i5 = i4 + 1;
            bArr3[i4] = (byte) bArr2[((bArr[i2 + 1] & 15) << 2) | ((bArr[i2 + 2] & UnsignedBytes.MAX_VALUE) >> 6)];
            i = i5 + 1;
            bArr3[i5] = (byte) bArr2[bArr[i2 + 2] & 63];
        }
        switch (bArr.length % 3) {
            case 1:
                int i6 = i + 1;
                bArr3[i] = (byte) bArr2[(bArr[length] & UnsignedBytes.MAX_VALUE) >> 2];
                int i7 = i6 + 1;
                bArr3[i6] = (byte) bArr2[(bArr[length] & 3) << 4];
                bArr3[i7] = 61;
                bArr3[i7 + 1] = 61;
                break;
            case 2:
                int i8 = i + 1;
                bArr3[i] = (byte) bArr2[(bArr[length] & UnsignedBytes.MAX_VALUE) >> 2];
                int i9 = i8 + 1;
                bArr3[i8] = (byte) bArr2[((bArr[length] & 3) << 4) | ((bArr[length + 1] & UnsignedBytes.MAX_VALUE) >> 4)];
                bArr3[i9] = (byte) bArr2[(bArr[length + 1] & 15) << 2];
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
