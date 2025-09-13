/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.base64;

import java.util.Arrays;

public class Base64 {
    private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final int[] IA = new int[256];

    static {
        Arrays.fill(IA, -1);
        int n = CA.length;
        for (int i = 0; i < n; ++i) {
            Base64.IA[Base64.CA[i]] = i;
        }
        Base64.IA[61] = 0;
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public static final byte[] decode(String string2) {
        int n;
        int[] nArray;
        int n2;
        int n3;
        void var7_3;
        int n4 = string2 != null ? string2.length() : 0;
        if (n4 == 0) {
            byte[] byArray = new byte[]{};
            return var7_3;
        }
        int n5 = 0;
        for (n3 = 0; n3 < n4; ++n3) {
            n2 = n5;
            if (IA[string2.charAt(n3)] < 0) {
                n2 = n5 + 1;
            }
            n5 = n2;
        }
        if ((n4 - n5) % 4 != 0) {
            return var7_3;
        }
        n2 = 0;
        n3 = n4;
        while (n3 > 1 && (nArray = IA)[string2.charAt(n = n3 - 1)] <= 0) {
            n3 = n;
            if (string2.charAt(n) != '=') continue;
            ++n2;
            n3 = n;
        }
        int n6 = ((n4 - n5) * 6 >> 3) - n2;
        byte[] byArray = new byte[n6];
        n4 = 0;
        n2 = 0;
        while (true) {
            byte[] byArray2 = byArray;
            if (n2 >= n6) return var7_3;
            n3 = 0;
            n = 0;
            n5 = n4;
            for (n4 = n; n4 < 4; ++n4, ++n5) {
                n = IA[string2.charAt(n5)];
                if (n >= 0) {
                    n3 |= n << 18 - n4 * 6;
                    continue;
                }
                --n4;
            }
            n = n2 + 1;
            byArray[n2] = (byte)(n3 >> 16);
            n4 = n;
            if (n < n6) {
                n4 = n + 1;
                byArray[n] = (byte)(n3 >> 8);
                if (n4 < n6) {
                    n2 = n4 + 1;
                    byArray[n4] = (byte)n3;
                    n4 = n2;
                }
            }
            n2 = n4;
            n4 = n5;
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    public static final byte[] decode(byte[] byArray) {
        int n;
        int[] nArray;
        void var7_6;
        int n2;
        int n3;
        int n4 = byArray.length;
        int n5 = 0;
        for (n3 = 0; n3 < n4; ++n3) {
            n2 = n5;
            if (IA[byArray[n3] & 0xFF] < 0) {
                n2 = n5 + 1;
            }
            n5 = n2;
        }
        if ((n4 - n5) % 4 != 0) {
            return var7_6;
        }
        n3 = 0;
        n2 = n4;
        while (n2 > 1 && (nArray = IA)[byArray[n = n2 - 1] & 0xFF] <= 0) {
            n2 = n;
            if (byArray[n] != 61) continue;
            ++n3;
            n2 = n;
        }
        int n6 = ((n4 - n5) * 6 >> 3) - n3;
        byte[] byArray2 = new byte[n6];
        n5 = 0;
        n3 = 0;
        while (true) {
            byte[] byArray3 = byArray2;
            if (n3 >= n6) {
                return var7_6;
            }
            n4 = 0;
            n = 0;
            n2 = n5;
            for (n5 = n; n5 < 4; ++n5, ++n2) {
                n = IA[byArray[n2] & 0xFF];
                if (n >= 0) {
                    n4 |= n << 18 - n5 * 6;
                    continue;
                }
                --n5;
            }
            n = n3 + 1;
            byArray2[n3] = (byte)(n4 >> 16);
            n5 = n;
            if (n < n6) {
                n5 = n + 1;
                byArray2[n] = (byte)(n4 >> 8);
                if (n5 < n6) {
                    n3 = n5 + 1;
                    byArray2[n5] = (byte)n4;
                    n5 = n3;
                }
            }
            n3 = n5;
            n5 = n2;
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public static final byte[] decode(char[] cArray) {
        int n;
        int[] nArray;
        int n2;
        int n3;
        void var7_3;
        int n4 = cArray != null ? cArray.length : 0;
        if (n4 == 0) {
            byte[] byArray = new byte[]{};
            return var7_3;
        }
        int n5 = 0;
        for (n3 = 0; n3 < n4; ++n3) {
            n2 = n5;
            if (IA[cArray[n3]] < 0) {
                n2 = n5 + 1;
            }
            n5 = n2;
        }
        if ((n4 - n5) % 4 != 0) {
            return var7_3;
        }
        n2 = 0;
        n3 = n4;
        while (n3 > 1 && (nArray = IA)[cArray[n = n3 - 1]] <= 0) {
            n3 = n;
            if (cArray[n] != '=') continue;
            ++n2;
            n3 = n;
        }
        int n6 = ((n4 - n5) * 6 >> 3) - n2;
        byte[] byArray = new byte[n6];
        n4 = 0;
        n2 = 0;
        while (true) {
            byte[] byArray2 = byArray;
            if (n2 >= n6) return var7_3;
            n3 = 0;
            n = 0;
            n5 = n4;
            for (n4 = n; n4 < 4; ++n4, ++n5) {
                n = IA[cArray[n5]];
                if (n >= 0) {
                    n3 |= n << 18 - n4 * 6;
                    continue;
                }
                --n4;
            }
            n = n2 + 1;
            byArray[n2] = (byte)(n3 >> 16);
            n4 = n;
            if (n < n6) {
                n4 = n + 1;
                byArray[n] = (byte)(n3 >> 8);
                if (n4 < n6) {
                    n2 = n4 + 1;
                    byArray[n4] = (byte)n3;
                    n4 = n2;
                }
            }
            n2 = n4;
            n4 = n5;
        }
    }

    /*
     * Unable to fully structure code
     */
    public static final byte[] decodeFast(String var0) {
        block17: {
            block18: {
                block15: {
                    block16: {
                        block19: {
                            var4_1 = var0.length();
                            if (var4_1 == 0) {
                                var0 = new byte[0];
lbl4:
                                // 2 sources

                                return var0;
                            }
                            var1_2 = 0;
                            var2_3 = var4_1 - 1;
                            while (true) {
                                var5_4 = var2_3;
                                if (var1_2 >= var2_3) break;
                                var5_4 = var2_3;
                                if (Base64.IA[var0.charAt(var1_2) & 255] >= 0) break;
                                ++var1_2;
                            }
                            while (var5_4 > 0 && Base64.IA[var0.charAt(var5_4) & 255] < 0) {
                                --var5_4;
                            }
                            if (var0.charAt(var5_4) != '=') break block19;
                            if (var0.charAt(var5_4 - 1) == '=') {
                                var3_5 = 2;
lbl21:
                                // 3 sources

                                while (true) {
                                    var7_6 = var5_4 - var1_2 + 1;
                                    if (var4_1 <= 76) break block15;
                                    if (var0.charAt(76) != '\r') break block16;
                                    var2_3 = var7_6 / 78;
lbl26:
                                    // 2 sources

                                    while (true) {
                                        var6_7 = var2_3 << 1;
lbl28:
                                        // 2 sources

                                        while (true) {
                                            var8_8 = ((var7_6 - var6_7) * 6 >> 3) - var3_5;
                                            var13_9 = new byte[var8_8];
                                            var4_1 = 0;
                                            var9_10 = var8_8 / 3;
                                            var7_6 = 0;
                                            var2_3 = var1_2;
                                            var1_2 = var7_6;
                                            block6: while (var1_2 < var9_10 * 3) {
                                                var14_14 = Base64.IA;
                                                var10_11 = var2_3 + 1;
                                                var7_6 = var14_14[var0.charAt(var2_3)];
                                                var14_14 = Base64.IA;
                                                var2_3 = var10_11 + 1;
                                                var10_11 = var14_14[var0.charAt(var10_11)];
                                                var14_14 = Base64.IA;
                                                var11_12 = var2_3 + 1;
                                                var12_13 = var14_14[var0.charAt(var2_3)];
                                                var14_14 = Base64.IA;
                                                var2_3 = var11_12 + 1;
                                                var10_11 = var7_6 << 18 | var10_11 << 12 | var12_13 << 6 | var14_14[var0.charAt(var11_12)];
                                                var11_12 = var1_2 + 1;
                                                var13_9[var1_2] = (byte)(var10_11 >> 16);
                                                var7_6 = var11_12 + 1;
                                                var13_9[var11_12] = (byte)(var10_11 >> 8);
                                                var13_9[var7_6] = (byte)var10_11;
                                                var1_2 = var4_1++;
                                                if (var6_7 > 0) {
                                                    var1_2 = var4_1;
                                                    if (var4_1 == 19) {
                                                        var1_2 = var2_3 + 2;
                                                        var4_1 = 0;
lbl60:
                                                        // 2 sources

                                                        while (true) {
                                                            var2_3 = var1_2;
                                                            var1_2 = ++var7_6;
                                                            continue block6;
                                                            break;
                                                        }
                                                    }
                                                }
                                                break block17;
                                            }
                                            break block18;
                                            break;
                                        }
                                        break;
                                    }
                                    break;
                                }
                            }
                            var3_5 = 1;
                            ** GOTO lbl21
                        }
                        var3_5 = 0;
                        ** while (true)
                    }
                    var2_3 = 0;
                    ** while (true)
                }
                var6_7 = 0;
                ** while (true)
            }
            var6_7 = var1_2;
            var6_7 = var2_3;
            if (var1_2 < var8_8) {
                var4_1 = 0;
                var6_7 = 0;
                while (var2_3 <= var5_4 - var3_5) {
                    var4_1 |= Base64.IA[var0.charAt(var2_3)] << 18 - var6_7 * 6;
                    ++var6_7;
                    ++var2_3;
                }
                var3_5 = 16;
                while (true) {
                    var6_7 = var1_2;
                    var6_7 = var2_3;
                    if (var1_2 >= var8_8) break;
                    var13_9[var1_2] = (byte)(var4_1 >> var3_5);
                    var3_5 -= 8;
                    ++var1_2;
                }
            }
            var0 = var13_9;
            ** while (true)
        }
        var4_1 = var1_2;
        var1_2 = var2_3;
        ** while (true)
    }

    /*
     * Enabled aggressive block sorting
     */
    public static final byte[] decodeFast(byte[] byArray) {
        int n;
        int n2;
        int n3 = byArray.length;
        if (n3 == 0) {
            return new byte[0];
        }
        int n4 = 0;
        int n5 = n3 - 1;
        while (true) {
            n2 = n5;
            if (n4 >= n5) break;
            n2 = n5;
            if (IA[byArray[n4] & 0xFF] >= 0) break;
            ++n4;
        }
        while (n2 > 0 && IA[byArray[n2] & 0xFF] < 0) {
            --n2;
        }
        int n6 = byArray[n2] == 61 ? (byArray[n2 - 1] == 61 ? 2 : 1) : 0;
        int n7 = n2 - n4 + 1;
        if (n3 > 76) {
            n5 = byArray[76] == 13 ? n7 / 78 : 0;
            n = n5 << 1;
        } else {
            n = 0;
        }
        int n8 = ((n7 - n) * 6 >> 3) - n6;
        byte[] byArray2 = new byte[n8];
        n3 = 0;
        int n9 = n8 / 3;
        n7 = 0;
        n5 = n4;
        n4 = n7;
        while (n4 < n9 * 3) {
            int[] nArray = IA;
            int n10 = n5 + 1;
            n7 = nArray[byArray[n5]];
            nArray = IA;
            n5 = n10 + 1;
            int n11 = nArray[byArray[n10]];
            nArray = IA;
            n10 = n5 + 1;
            int n12 = nArray[byArray[n5]];
            nArray = IA;
            n5 = n10 + 1;
            n11 = n7 << 18 | n11 << 12 | n12 << 6 | nArray[byArray[n10]];
            n10 = n4 + 1;
            byArray2[n4] = (byte)(n11 >> 16);
            n7 = n10 + 1;
            byArray2[n10] = (byte)(n11 >> 8);
            byArray2[n7] = (byte)n11;
            n4 = n3++;
            if (n > 0) {
                n4 = n3;
                if (n3 == 19) {
                    n5 += 2;
                    n4 = 0;
                }
            }
            n3 = n4;
            n4 = ++n7;
        }
        n = n4;
        n = n5;
        if (n4 >= n8) return byArray2;
        n3 = 0;
        n = 0;
        while (n5 <= n2 - n6) {
            n3 |= IA[byArray[n5]] << 18 - n * 6;
            ++n;
            ++n5;
        }
        n6 = 16;
        while (true) {
            n = n4;
            n = n5;
            if (n4 >= n8) return byArray2;
            byArray2[n4] = (byte)(n3 >> n6);
            n6 -= 8;
            ++n4;
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    public static final byte[] decodeFast(char[] objectArray) {
        int n;
        int n2;
        void var0_2;
        int n3 = objectArray.length;
        if (n3 == 0) {
            byte[] byArray = new byte[]{};
            return var0_2;
        }
        int n4 = 0;
        int n5 = n3 - 1;
        while (true) {
            n2 = n5;
            if (n4 >= n5) break;
            n2 = n5;
            if (IA[objectArray[n4]] >= 0) break;
            ++n4;
        }
        while (n2 > 0 && IA[objectArray[n2]] < 0) {
            --n2;
        }
        int n6 = objectArray[n2] == '=' ? (objectArray[n2 - 1] == '=' ? 2 : 1) : 0;
        int n7 = n2 - n4 + 1;
        if (n3 > 76) {
            n5 = objectArray[76] == '\r' ? n7 / 78 : 0;
            n = n5 << 1;
        } else {
            n = 0;
        }
        int n8 = ((n7 - n) * 6 >> 3) - n6;
        byte[] byArray = new byte[n8];
        n3 = 0;
        int n9 = n8 / 3;
        n7 = 0;
        n5 = n4;
        n4 = n7;
        while (n4 < n9 * 3) {
            int[] nArray = IA;
            int n10 = n5 + 1;
            n7 = nArray[objectArray[n5]];
            nArray = IA;
            n5 = n10 + 1;
            int n11 = nArray[objectArray[n10]];
            nArray = IA;
            n10 = n5 + 1;
            int n12 = nArray[objectArray[n5]];
            nArray = IA;
            n5 = n10 + 1;
            n11 = n7 << 18 | n11 << 12 | n12 << 6 | nArray[objectArray[n10]];
            n10 = n4 + 1;
            byArray[n4] = (byte)(n11 >> 16);
            n7 = n10 + 1;
            byArray[n10] = (byte)(n11 >> 8);
            byArray[n7] = (byte)n11;
            n4 = n3++;
            if (n > 0) {
                n4 = n3;
                if (n3 == 19) {
                    n5 += 2;
                    n4 = 0;
                }
            }
            n3 = n4;
            n4 = ++n7;
        }
        n = n4;
        n = n5;
        if (n4 < n8) {
            n3 = 0;
            n = 0;
            while (n5 <= n2 - n6) {
                n3 |= IA[objectArray[n5]] << 18 - n * 6;
                ++n;
                ++n5;
            }
            n6 = 16;
            while (true) {
                n = n4;
                n = n5;
                if (n4 >= n8) break;
                byArray[n4] = (byte)(n3 >> n6);
                n6 -= 8;
                ++n4;
            }
        }
        byte[] byArray2 = byArray;
        return var0_2;
    }

    /*
     * Unable to fully structure code
     */
    public static final byte[] encodeToByte(byte[] var0, boolean var1_1) {
        block14: {
            block15: {
                block17: {
                    block16: {
                        if (var0 == null) break block16;
                        var5_2 = var0.length;
lbl3:
                        // 2 sources

                        while (var5_2 == 0) {
                            var12_3 = new byte[]{};
lbl5:
                            // 3 sources

                            return var12_3;
                        }
                        break block17;
                    }
                    var5_2 = 0;
                    ** GOTO lbl3
                }
                var8_4 = var5_2 / 3 * 3;
                var4_5 = (var5_2 - 1) / 3 + 1 << 2;
                if (var1_1) {
                    var3_6 = (var4_5 - 1) / 76 << 1;
lbl16:
                    // 2 sources

                    while (true) {
                        var7_7 = var4_5 + var3_6;
                        var13_8 = new byte[var7_7];
                        var3_6 = 0;
                        var4_5 = 0;
                        var6_9 = 0;
                        block3: while (var6_9 < var8_4) {
                            var10_11 = var6_9 + 1;
                            var6_9 = var0[var6_9];
                            var9_10 = var10_11 + 1;
                            var10_11 = (var6_9 & 255) << 16 | (var0[var10_11] & 255) << 8 | var0[var9_10] & 255;
                            var6_9 = var4_5 + 1;
                            var13_8[var4_5] = (byte)Base64.CA[var10_11 >>> 18 & 63];
                            var4_5 = var6_9 + 1;
                            var13_8[var6_9] = (byte)Base64.CA[var10_11 >>> 12 & 63];
                            var11_12 = var4_5 + 1;
                            var13_8[var4_5] = (byte)Base64.CA[var10_11 >>> 6 & 63];
                            var6_9 = var11_12 + 1;
                            var13_8[var11_12] = (byte)Base64.CA[var10_11 & 63];
                            var4_5 = var3_6++;
                            if (var1_1) {
                                var4_5 = var3_6;
                                if (var3_6 == 19) {
                                    var4_5 = var3_6;
                                    if (var6_9 < var7_7 - 2) {
                                        var4_5 = var6_9 + 1;
                                        var13_8[var6_9] = 13;
                                        var13_8[var4_5] = 10;
                                        var3_6 = 0;
                                        ++var4_5;
lbl46:
                                        // 2 sources

                                        while (true) {
                                            var6_9 = var9_10 + 1;
                                            continue block3;
                                            break;
                                        }
                                    }
                                }
                            }
                            break block14;
                        }
                        break block15;
                        break;
                    }
                }
                var3_6 = 0;
                ** while (true)
            }
            var4_5 = var5_2 - var8_4;
            var12_3 = var13_8;
            if (var4_5 <= 0) ** GOTO lbl5
            var6_9 = var0[var8_4];
            if (var4_5 == 2) {
                var3_6 = (var0[var5_2 - 1] & 255) << 2;
lbl60:
                // 2 sources

                while (true) {
                    var3_6 = (var6_9 & 255) << 10 | var3_6;
                    var13_8[var7_7 - 4] = (byte)Base64.CA[var3_6 >> 12];
                    var13_8[var7_7 - 3] = (byte)Base64.CA[var3_6 >>> 6 & 63];
                    if (var4_5 == 2) {
                        var2_13 = (byte)Base64.CA[var3_6 & 63];
lbl66:
                        // 2 sources

                        while (true) {
                            var13_8[var7_7 - 2] = var2_13;
                            var13_8[var7_7 - 1] = 61;
                            var12_3 = var13_8;
                            ** continue;
                            break;
                        }
                    }
                    break;
                }
            } else {
                var3_6 = 0;
                ** continue;
            }
            var2_13 = 61;
            ** while (true)
        }
        var3_6 = var4_5;
        var4_5 = var6_9;
        ** while (true)
    }

    /*
     * Unable to fully structure code
     */
    public static final char[] encodeToChar(byte[] var0, boolean var1_1) {
        block14: {
            block15: {
                block17: {
                    block16: {
                        if (var0 == null) break block16;
                        var5_2 = var0.length;
lbl3:
                        // 2 sources

                        while (var5_2 == 0) {
                            var12_3 = new char[]{};
lbl5:
                            // 3 sources

                            return var12_3;
                        }
                        break block17;
                    }
                    var5_2 = 0;
                    ** GOTO lbl3
                }
                var8_4 = var5_2 / 3 * 3;
                var4_5 = (var5_2 - 1) / 3 + 1 << 2;
                if (var1_1) {
                    var3_6 = (var4_5 - 1) / 76 << 1;
lbl16:
                    // 2 sources

                    while (true) {
                        var7_7 = var4_5 + var3_6;
                        var13_8 = new char[var7_7];
                        var3_6 = 0;
                        var4_5 = 0;
                        var6_9 = 0;
                        block3: while (var6_9 < var8_4) {
                            var10_11 = var6_9 + 1;
                            var6_9 = var0[var6_9];
                            var9_10 = var10_11 + 1;
                            var10_11 = (var6_9 & 255) << 16 | (var0[var10_11] & 255) << 8 | var0[var9_10] & 255;
                            var11_12 = var4_5 + 1;
                            var13_8[var4_5] = Base64.CA[var10_11 >>> 18 & 63];
                            var6_9 = var11_12 + 1;
                            var13_8[var11_12] = Base64.CA[var10_11 >>> 12 & 63];
                            var4_5 = var6_9 + 1;
                            var13_8[var6_9] = Base64.CA[var10_11 >>> 6 & 63];
                            var6_9 = var4_5 + 1;
                            var13_8[var4_5] = Base64.CA[var10_11 & 63];
                            var4_5 = var3_6++;
                            if (var1_1) {
                                var4_5 = var3_6;
                                if (var3_6 == 19) {
                                    var4_5 = var3_6;
                                    if (var6_9 < var7_7 - 2) {
                                        var4_5 = var6_9 + 1;
                                        var13_8[var6_9] = 13;
                                        var13_8[var4_5] = 10;
                                        var3_6 = 0;
                                        ++var4_5;
lbl46:
                                        // 2 sources

                                        while (true) {
                                            var6_9 = var9_10 + 1;
                                            continue block3;
                                            break;
                                        }
                                    }
                                }
                            }
                            break block14;
                        }
                        break block15;
                        break;
                    }
                }
                var3_6 = 0;
                ** while (true)
            }
            var4_5 = var5_2 - var8_4;
            var12_3 = var13_8;
            if (var4_5 <= 0) ** GOTO lbl5
            var6_9 = var0[var8_4];
            if (var4_5 == 2) {
                var3_6 = (var0[var5_2 - 1] & 255) << 2;
lbl60:
                // 2 sources

                while (true) {
                    var3_6 = (var6_9 & 255) << 10 | var3_6;
                    var13_8[var7_7 - 4] = Base64.CA[var3_6 >> 12];
                    var13_8[var7_7 - 3] = Base64.CA[var3_6 >>> 6 & 63];
                    if (var4_5 == 2) {
                        var2_13 = Base64.CA[var3_6 & 63];
lbl66:
                        // 2 sources

                        while (true) {
                            var13_8[var7_7 - 2] = var2_13;
                            var13_8[var7_7 - 1] = 61;
                            var12_3 = var13_8;
                            ** continue;
                            break;
                        }
                    }
                    break;
                }
            } else {
                var3_6 = 0;
                ** continue;
            }
            var2_13 = 61;
            ** while (true)
        }
        var3_6 = var4_5;
        var4_5 = var6_9;
        ** while (true)
    }

    public static final String encodeToString(byte[] byArray, boolean bl) {
        return new String(Base64.encodeToChar(byArray, bl));
    }
}

