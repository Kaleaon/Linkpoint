// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.UnsupportedEncodingException;

final class Base64
{
    private static final byte[] MAP;
    private static final byte[] URL_MAP;
    
    static {
        MAP = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        URL_MAP = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
    }
    
    private Base64() {
    }
    
    public static byte[] decode(final String s) {
        int i;
        for (i = s.length(); i > 0; --i) {
            final char char1 = s.charAt(i - 1);
            if (char1 != '=' && char1 != '\n' && char1 != '\r' && char1 != ' ' && char1 != '\t') {
                break;
            }
        }
        final byte[] array = new byte[(int)(i * 6L / 8L)];
        int j = 0;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (j < i) {
            int char2 = s.charAt(j);
            int n4 = 0;
            Label_0199: {
                if (char2 >= 65 && char2 <= 90) {
                    char2 -= 65;
                }
                else if (char2 >= 97 && char2 <= 122) {
                    char2 -= 71;
                }
                else if (char2 >= 48 && char2 <= 57) {
                    char2 += 4;
                }
                else if (char2 != 43 && char2 != 45) {
                    if (char2 != 47 && char2 != 95) {
                        if (char2 == 10 || char2 == 13 || char2 == 32) {
                            n4 = n;
                            break Label_0199;
                        }
                        if (char2 != 9) {
                            return null;
                        }
                        n4 = n;
                        break Label_0199;
                    }
                    else {
                        char2 = 63;
                    }
                }
                else {
                    char2 = 62;
                }
                n4 = ((byte)char2 | n << 6);
                if (++n2 % 4 == 0) {
                    final int n5 = n3 + 1;
                    array[n3] = (byte)(n4 >> 16);
                    final int n6 = n5 + 1;
                    array[n5] = (byte)(n4 >> 8);
                    n3 = n6 + 1;
                    array[n6] = (byte)n4;
                }
            }
            ++j;
            n = n4;
        }
        final int n7 = n2 % 4;
        if (n7 == 1) {
            return null;
        }
        if (n7 != 2) {
            if (n7 == 3) {
                final int n8 = n << 6;
                final int n9 = n3 + 1;
                array[n3] = (byte)(n8 >> 16);
                n3 = n9 + 1;
                array[n9] = (byte)(n8 >> 8);
            }
        }
        else {
            array[n3] = (byte)(n << 12 >> 16);
            ++n3;
        }
        if (n3 != array.length) {
            final byte[] array2 = new byte[n3];
            System.arraycopy(array, 0, array2, 0, n3);
            return array2;
        }
        return array;
    }
    
    public static String encode(final byte[] array) {
        return encode(array, Base64.MAP);
    }
    
    private static String encode(final byte[] array, final byte[] array2) {
        int n = 0;
        final byte[] bytes = new byte[(array.length + 2) / 3 * 4];
        final int n2 = array.length - array.length % 3;
        int n3 = 0;
        while (true) {
            Label_0069: {
                if (n < n2) {
                    break Label_0069;
                }
                Label_0252: {
                    switch (array.length % 3) {
                        case 1: {
                            break Label_0252;
                        }
                        case 2: {
                            break Label_0252;
                        }
                    }
                    try {
                        return new String(bytes, "US-ASCII");
                        final int n4 = n3 + 1;
                        bytes[n3] = array2[(array[n2] & 0xFF) >> 2];
                        final int n5 = n4 + 1;
                        bytes[n4] = array2[(array[n2] & 0x3) << 4];
                        bytes[n5] = 61;
                        bytes[n5 + 1] = 61;
                        return new String(bytes, "US-ASCII");
                        final int n6 = n3 + 1;
                        bytes[n3] = array2[(array[n2] & 0xFF) >> 2];
                        final int n7 = n6 + 1;
                        bytes[n6] = array2[(array[n2] & 0x3) << 4 | (array[n2 + 1] & 0xFF) >> 4];
                        bytes[n7] = array2[(array[n2 + 1] & 0xF) << 2];
                        bytes[n7 + 1] = 61;
                        return new String(bytes, "US-ASCII");
                        final int n8 = n3 + 1;
                        bytes[n3] = array2[(array[n] & 0xFF) >> 2];
                        final int n9 = n8 + 1;
                        bytes[n8] = array2[(array[n] & 0x3) << 4 | (array[n + 1] & 0xFF) >> 4];
                        final int n10 = n9 + 1;
                        bytes[n9] = array2[(array[n + 1] & 0xF) << 2 | (array[n + 2] & 0xFF) >> 6];
                        n3 = n10 + 1;
                        bytes[n10] = array2[array[n + 2] & 0x3F];
                        n += 3;
                    }
                    catch (final UnsupportedEncodingException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
            }
        }
    }
    
    public static String encodeUrl(final byte[] array) {
        return encode(array, Base64.URL_MAP);
    }
}
