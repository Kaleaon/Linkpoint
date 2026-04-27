// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.base64;

import java.util.Arrays;

public class Base64
{
    private static final char[] CA;
    private static final int[] IA;
    
    static {
        CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        Arrays.fill(IA = new int[256], -1);
        for (int length = Base64.CA.length, i = 0; i < length; ++i) {
            Base64.IA[Base64.CA[i]] = i;
        }
        Base64.IA[61] = 0;
    }
    
    public static final byte[] decode(final String s) {
        int length;
        if (s == null) {
            length = 0;
        }
        else {
            length = s.length();
        }
        if (length == 0) {
            return new byte[0];
        }
        int i = 0;
        int n = 0;
        while (i < length) {
            if (Base64.IA[s.charAt(i)] < 0) {
                ++n;
            }
            ++i;
        }
        if ((length - n) % 4 == 0) {
            int j = length;
            int n2 = 0;
            while (j > 1) {
                final int[] ia = Base64.IA;
                final int n3 = j - 1;
                if (ia[s.charAt(n3)] > 0) {
                    break;
                }
                j = n3;
                if (s.charAt(n3) != '=') {
                    continue;
                }
                ++n2;
                j = n3;
            }
            final int n4 = ((length - n) * 6 >> 3) - n2;
            final byte[] array = new byte[n4];
            int k = 0;
            int n5 = 0;
            while (k < n4) {
                int n6 = 0;
                final int n7 = 0;
                int index = n5;
                for (int l = n7; l < 4; ++l, ++index) {
                    final int n8 = Base64.IA[s.charAt(index)];
                    if (n8 < 0) {
                        --l;
                    }
                    else {
                        n6 |= n8 << 18 - l * 6;
                    }
                }
                int n9 = k + 1;
                array[k] = (byte)(n6 >> 16);
                if (n9 < n4) {
                    final int n10 = n9 + 1;
                    array[n9] = (byte)(n6 >> 8);
                    if (n10 >= n4) {
                        n9 = n10;
                    }
                    else {
                        n9 = n10 + 1;
                        array[n10] = (byte)n6;
                    }
                }
                k = n9;
                n5 = index;
            }
            return array;
        }
        return null;
    }
    
    public static final byte[] decode(final byte[] array) {
        final int length = array.length;
        int i = 0;
        int n = 0;
        while (i < length) {
            if (Base64.IA[array[i] & 0xFF] < 0) {
                ++n;
            }
            ++i;
        }
        if ((length - n) % 4 == 0) {
            int j = length;
            int n2 = 0;
            while (j > 1) {
                final int[] ia = Base64.IA;
                final int n3 = j - 1;
                if (ia[array[n3] & 0xFF] > 0) {
                    break;
                }
                j = n3;
                if (array[n3] != 61) {
                    continue;
                }
                ++n2;
                j = n3;
            }
            final int n4 = ((length - n) * 6 >> 3) - n2;
            final byte[] array2 = new byte[n4];
            int k = 0;
            int n5 = 0;
            while (k < n4) {
                int n6 = 0;
                final int n7 = 0;
                int n8 = n5;
                for (int l = n7; l < 4; ++l, ++n8) {
                    final int n9 = Base64.IA[array[n8] & 0xFF];
                    if (n9 < 0) {
                        --l;
                    }
                    else {
                        n6 |= n9 << 18 - l * 6;
                    }
                }
                int n10 = k + 1;
                array2[k] = (byte)(n6 >> 16);
                if (n10 < n4) {
                    final int n11 = n10 + 1;
                    array2[n10] = (byte)(n6 >> 8);
                    if (n11 >= n4) {
                        n10 = n11;
                    }
                    else {
                        n10 = n11 + 1;
                        array2[n11] = (byte)n6;
                    }
                }
                k = n10;
                n5 = n8;
            }
            return array2;
        }
        return null;
    }
    
    public static final byte[] decode(final char[] array) {
        int length;
        if (array == null) {
            length = 0;
        }
        else {
            length = array.length;
        }
        if (length == 0) {
            return new byte[0];
        }
        int i = 0;
        int n = 0;
        while (i < length) {
            if (Base64.IA[array[i]] < 0) {
                ++n;
            }
            ++i;
        }
        if ((length - n) % 4 == 0) {
            int j = length;
            int n2 = 0;
            while (j > 1) {
                final int[] ia = Base64.IA;
                final int n3 = j - 1;
                if (ia[array[n3]] > 0) {
                    break;
                }
                j = n3;
                if (array[n3] != '=') {
                    continue;
                }
                ++n2;
                j = n3;
            }
            final int n4 = ((length - n) * 6 >> 3) - n2;
            final byte[] array2 = new byte[n4];
            int k = 0;
            int n5 = 0;
            while (k < n4) {
                int n6 = 0;
                final int n7 = 0;
                int n8 = n5;
                for (int l = n7; l < 4; ++l, ++n8) {
                    final int n9 = Base64.IA[array[n8]];
                    if (n9 < 0) {
                        --l;
                    }
                    else {
                        n6 |= n9 << 18 - l * 6;
                    }
                }
                int n10 = k + 1;
                array2[k] = (byte)(n6 >> 16);
                if (n10 < n4) {
                    final int n11 = n10 + 1;
                    array2[n10] = (byte)(n6 >> 8);
                    if (n11 >= n4) {
                        n10 = n11;
                    }
                    else {
                        n10 = n11 + 1;
                        array2[n11] = (byte)n6;
                    }
                }
                k = n10;
                n5 = n8;
            }
            return array2;
        }
        return null;
    }
    
    public static final byte[] decodeFast(final String s) {
        final int n = 0;
        final int length = s.length();
        if (length != 0) {
            int n2;
            int n3;
            for (n2 = length - 1, n3 = 0; n3 < n2 && Base64.IA[s.charAt(n3) & '\u00ff'] < 0; ++n3) {}
            while (n2 > 0 && Base64.IA[s.charAt(n2) & '\u00ff'] < 0) {
                --n2;
            }
            int n4;
            if (s.charAt(n2) != '=') {
                n4 = 0;
            }
            else if (s.charAt(n2 - 1) != '=') {
                n4 = 1;
            }
            else {
                n4 = 2;
            }
            final int n5 = n2 - n3 + 1;
            int n6;
            if (length <= 76) {
                n6 = 0;
            }
            else {
                int n7;
                if (s.charAt(76) != '\r') {
                    n7 = 0;
                }
                else {
                    n7 = n5 / 78;
                }
                n6 = n7 << 1;
            }
            final int n8 = ((n5 - n6) * 6 >> 3) - n4;
            final byte[] array = new byte[n8];
            final int n9 = n8 / 3;
            int n10 = 0;
            int i = 0;
            while (i < n9 * 3) {
                final int[] ia = Base64.IA;
                final int index = n3 + 1;
                final int n11 = ia[s.charAt(n3)];
                final int[] ia2 = Base64.IA;
                final int index2 = index + 1;
                final int n12 = ia2[s.charAt(index)];
                final int[] ia3 = Base64.IA;
                final int index3 = index2 + 1;
                final int n13 = ia3[s.charAt(index2)];
                final int[] ia4 = Base64.IA;
                final int n14 = index3 + 1;
                final int n15 = n13 << 6 | (n11 << 18 | n12 << 12) | ia4[s.charAt(index3)];
                final int n16 = i + 1;
                array[i] = (byte)(n15 >> 16);
                final int n17 = n16 + 1;
                array[n16] = (byte)(n15 >> 8);
                final int n18 = n17 + 1;
                array[n17] = (byte)n15;
                n3 = n14;
                i = n18;
                if (n6 > 0) {
                    ++n10;
                    n3 = n14;
                    i = n18;
                    if (n10 != 19) {
                        continue;
                    }
                    n3 = n14 + 2;
                    n10 = 0;
                    i = n18;
                }
            }
            if (i < n8) {
                int n19 = 0;
                int index4 = n3;
                int n20 = n;
                while (true) {
                    final int n21 = n20;
                    if (index4 > n2 - n4) {
                        break;
                    }
                    final int n22 = Base64.IA[s.charAt(index4)];
                    n20 = n21 + 1;
                    n19 |= n22 << 18 - n21 * 6;
                    ++index4;
                }
                int n23 = 16;
                for (int j = i; j < n8; ++j) {
                    array[j] = (byte)(n19 >> n23);
                    n23 -= 8;
                }
            }
            return array;
        }
        return new byte[0];
    }
    
    public static final byte[] decodeFast(final byte[] array) {
        final int n = 0;
        final int length = array.length;
        if (length != 0) {
            int n2;
            int n3;
            for (n2 = length - 1, n3 = 0; n3 < n2 && Base64.IA[array[n3] & 0xFF] < 0; ++n3) {}
            while (n2 > 0 && Base64.IA[array[n2] & 0xFF] < 0) {
                --n2;
            }
            int n4;
            if (array[n2] != 61) {
                n4 = 0;
            }
            else if (array[n2 - 1] != 61) {
                n4 = 1;
            }
            else {
                n4 = 2;
            }
            final int n5 = n2 - n3 + 1;
            int n6;
            if (length <= 76) {
                n6 = 0;
            }
            else {
                int n7;
                if (array[76] != 13) {
                    n7 = 0;
                }
                else {
                    n7 = n5 / 78;
                }
                n6 = n7 << 1;
            }
            final int n8 = ((n5 - n6) * 6 >> 3) - n4;
            final byte[] array2 = new byte[n8];
            final int n9 = n8 / 3;
            int n10 = 0;
            int i = 0;
            while (i < n9 * 3) {
                final int[] ia = Base64.IA;
                final int n11 = n3 + 1;
                final int n12 = ia[array[n3]];
                final int[] ia2 = Base64.IA;
                final int n13 = n11 + 1;
                final int n14 = ia2[array[n11]];
                final int[] ia3 = Base64.IA;
                final int n15 = n13 + 1;
                final int n16 = ia3[array[n13]];
                final int[] ia4 = Base64.IA;
                final int n17 = n15 + 1;
                final int n18 = n16 << 6 | (n12 << 18 | n14 << 12) | ia4[array[n15]];
                final int n19 = i + 1;
                array2[i] = (byte)(n18 >> 16);
                final int n20 = n19 + 1;
                array2[n19] = (byte)(n18 >> 8);
                final int n21 = n20 + 1;
                array2[n20] = (byte)n18;
                n3 = n17;
                i = n21;
                if (n6 > 0) {
                    ++n10;
                    n3 = n17;
                    i = n21;
                    if (n10 != 19) {
                        continue;
                    }
                    n3 = n17 + 2;
                    n10 = 0;
                    i = n21;
                }
            }
            if (i < n8) {
                int n22 = 0;
                int n23 = n3;
                int n24 = n;
                while (true) {
                    final int n25 = n24;
                    if (n23 > n2 - n4) {
                        break;
                    }
                    final int n26 = Base64.IA[array[n23]];
                    n24 = n25 + 1;
                    n22 |= n26 << 18 - n25 * 6;
                    ++n23;
                }
                int n27 = 16;
                for (int j = i; j < n8; ++j) {
                    array2[j] = (byte)(n22 >> n27);
                    n27 -= 8;
                }
            }
            return array2;
        }
        return new byte[0];
    }
    
    public static final byte[] decodeFast(final char[] array) {
        final int n = 0;
        final int length = array.length;
        if (length != 0) {
            int n2;
            int n3;
            for (n2 = length - 1, n3 = 0; n3 < n2 && Base64.IA[array[n3]] < 0; ++n3) {}
            while (n2 > 0 && Base64.IA[array[n2]] < 0) {
                --n2;
            }
            int n4;
            if (array[n2] != '=') {
                n4 = 0;
            }
            else if (array[n2 - 1] != '=') {
                n4 = 1;
            }
            else {
                n4 = 2;
            }
            final int n5 = n2 - n3 + 1;
            int n6;
            if (length <= 76) {
                n6 = 0;
            }
            else {
                int n7;
                if (array[76] != '\r') {
                    n7 = 0;
                }
                else {
                    n7 = n5 / 78;
                }
                n6 = n7 << 1;
            }
            final int n8 = ((n5 - n6) * 6 >> 3) - n4;
            final byte[] array2 = new byte[n8];
            final int n9 = n8 / 3;
            int n10 = 0;
            int i = 0;
            while (i < n9 * 3) {
                final int[] ia = Base64.IA;
                final int n11 = n3 + 1;
                final int n12 = ia[array[n3]];
                final int[] ia2 = Base64.IA;
                final int n13 = n11 + 1;
                final int n14 = ia2[array[n11]];
                final int[] ia3 = Base64.IA;
                final int n15 = n13 + 1;
                final int n16 = ia3[array[n13]];
                final int[] ia4 = Base64.IA;
                final int n17 = n15 + 1;
                final int n18 = n16 << 6 | (n12 << 18 | n14 << 12) | ia4[array[n15]];
                final int n19 = i + 1;
                array2[i] = (byte)(n18 >> 16);
                final int n20 = n19 + 1;
                array2[n19] = (byte)(n18 >> 8);
                final int n21 = n20 + 1;
                array2[n20] = (byte)n18;
                n3 = n17;
                i = n21;
                if (n6 > 0) {
                    ++n10;
                    n3 = n17;
                    i = n21;
                    if (n10 != 19) {
                        continue;
                    }
                    n3 = n17 + 2;
                    n10 = 0;
                    i = n21;
                }
            }
            if (i < n8) {
                int n22 = 0;
                int n23 = n3;
                int n24 = n;
                while (true) {
                    final int n25 = n24;
                    if (n23 > n2 - n4) {
                        break;
                    }
                    final int n26 = Base64.IA[array[n23]];
                    n24 = n25 + 1;
                    n22 |= n26 << 18 - n25 * 6;
                    ++n23;
                }
                int n27 = 16;
                for (int j = i; j < n8; ++j) {
                    array2[j] = (byte)(n22 >> n27);
                    n27 -= 8;
                }
            }
            return array2;
        }
        return new byte[0];
    }
    
    public static final byte[] encodeToByte(final byte[] array, final boolean b) {
        final int n = 0;
        int length;
        if (array == null) {
            length = 0;
        }
        else {
            length = array.length;
        }
        if (length != 0) {
            final int n2 = length / 3 * 3;
            final int n3 = (length - 1) / 3 + 1 << 2;
            int n4;
            if (!b) {
                n4 = 0;
            }
            else {
                n4 = (n3 - 1) / 76 << 1;
            }
            final int n5 = n3 + n4;
            final byte[] array2 = new byte[n5];
            int n6 = 0;
            int n7 = 0;
            int i = 0;
            while (i < n2) {
                final int n8 = i + 1;
                final byte b2 = array[i];
                final int n9 = n8 + 1;
                final byte b3 = array[n8];
                final int n10 = n9 + 1;
                final int n11 = (b3 & 0xFF) << 8 | (b2 & 0xFF) << 16 | (array[n9] & 0xFF);
                final int n12 = n7 + 1;
                array2[n7] = (byte)Base64.CA[n11 >>> 18 & 0x3F];
                final int n13 = n12 + 1;
                array2[n12] = (byte)Base64.CA[n11 >>> 12 & 0x3F];
                final int n14 = n13 + 1;
                array2[n13] = (byte)Base64.CA[n11 >>> 6 & 0x3F];
                final int n15 = n14 + 1;
                array2[n14] = (byte)Base64.CA[n11 & 0x3F];
                n7 = n15;
                i = n10;
                if (b) {
                    final int n16 = ++n6;
                    n7 = n15;
                    i = n10;
                    if (n16 != 19) {
                        continue;
                    }
                    n6 = n16;
                    n7 = n15;
                    i = n10;
                    if (n15 >= n5 - 2) {
                        continue;
                    }
                    final int n17 = n15 + 1;
                    array2[n15] = 13;
                    array2[n17] = 10;
                    n7 = n17 + 1;
                    n6 = 0;
                    i = n10;
                }
            }
            final int n18 = length - n2;
            if (n18 > 0) {
                final byte b4 = array[n2];
                int n19;
                if (n18 != 2) {
                    n19 = n;
                }
                else {
                    n19 = (array[length - 1] & 0xFF) << 2;
                }
                final int n20 = (b4 & 0xFF) << 10 | n19;
                array2[n5 - 4] = (byte)Base64.CA[n20 >> 12];
                array2[n5 - 3] = (byte)Base64.CA[n20 >>> 6 & 0x3F];
                int n21;
                if (n18 != 2) {
                    n21 = 61;
                }
                else {
                    n21 = (byte)Base64.CA[n20 & 0x3F];
                }
                array2[n5 - 2] = (byte)n21;
                array2[n5 - 1] = 61;
            }
            return array2;
        }
        return new byte[0];
    }
    
    public static final char[] encodeToChar(final byte[] array, final boolean b) {
        final int n = 0;
        int length;
        if (array == null) {
            length = 0;
        }
        else {
            length = array.length;
        }
        if (length != 0) {
            final int n2 = length / 3 * 3;
            final int n3 = (length - 1) / 3 + 1 << 2;
            int n4;
            if (!b) {
                n4 = 0;
            }
            else {
                n4 = (n3 - 1) / 76 << 1;
            }
            final int n5 = n3 + n4;
            final char[] array2 = new char[n5];
            int n6 = 0;
            int n7 = 0;
            int i = 0;
            while (i < n2) {
                final int n8 = i + 1;
                final byte b2 = array[i];
                final int n9 = n8 + 1;
                final byte b3 = array[n8];
                final int n10 = n9 + 1;
                final int n11 = (b3 & 0xFF) << 8 | (b2 & 0xFF) << 16 | (array[n9] & 0xFF);
                final int n12 = n7 + 1;
                array2[n7] = Base64.CA[n11 >>> 18 & 0x3F];
                final int n13 = n12 + 1;
                array2[n12] = Base64.CA[n11 >>> 12 & 0x3F];
                final int n14 = n13 + 1;
                array2[n13] = Base64.CA[n11 >>> 6 & 0x3F];
                final int n15 = n14 + 1;
                array2[n14] = Base64.CA[n11 & 0x3F];
                n7 = n15;
                i = n10;
                if (b) {
                    final int n16 = ++n6;
                    n7 = n15;
                    i = n10;
                    if (n16 != 19) {
                        continue;
                    }
                    n6 = n16;
                    n7 = n15;
                    i = n10;
                    if (n15 >= n5 - 2) {
                        continue;
                    }
                    final int n17 = n15 + 1;
                    array2[n15] = 13;
                    array2[n17] = 10;
                    n7 = n17 + 1;
                    n6 = 0;
                    i = n10;
                }
            }
            final int n18 = length - n2;
            if (n18 > 0) {
                final byte b4 = array[n2];
                int n19;
                if (n18 != 2) {
                    n19 = n;
                }
                else {
                    n19 = (array[length - 1] & 0xFF) << 2;
                }
                final int n20 = (b4 & 0xFF) << 10 | n19;
                array2[n5 - 4] = Base64.CA[n20 >> 12];
                array2[n5 - 3] = Base64.CA[n20 >>> 6 & 0x3F];
                int n21;
                if (n18 != 2) {
                    n21 = 61;
                }
                else {
                    n21 = Base64.CA[n20 & 0x3F];
                }
                array2[n5 - 2] = (char)n21;
                array2[n5 - 1] = 61;
            }
            return array2;
        }
        return new char[0];
    }
    
    public static final String encodeToString(final byte[] array, final boolean b) {
        return new String(encodeToChar(array, b));
    }
}
