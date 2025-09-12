// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.base64;

import java.util.Arrays;

public class Base64
{

    private static final char CA[];
    private static final int IA[];

    public Base64()
    {
    }

    public static final byte[] decode(String s)
    {
        byte abyte0[];
        int i;
        int j;
        int k;
        int i1;
        int l1;
        if (s == null)
        {
            i = 0;
        } else
        {
            i = s.length();
        }
        if (i == 0) goto _L2; else goto _L1
_L1:
        k = 0;
        j = 0;
_L10:
        if (k < i) goto _L4; else goto _L3
_L3:
        if ((i - j) % 4 != 0) goto _L6; else goto _L5
_L5:
        k = i;
        i1 = 0;
_L12:
        if (k > 1) goto _L8; else goto _L7
_L7:
        l1 = ((i - j) * 6 >> 3) - i1;
        abyte0 = new byte[l1];
        k = 0;
        i = 0;
          goto _L9
_L2:
        return new byte[0];
_L4:
        if (IA[s.charAt(k)] < 0)
        {
            j++;
        }
        k++;
          goto _L10
_L6:
        return null;
_L8:
        abyte0 = IA;
        j1 = k - 1;
        if (abyte0[s.charAt(j1)] > 0) goto _L7; else goto _L11
_L11:
        k = j1;
        if (s.charAt(j1) == '=')
        {
            i1++;
            k = j1;
        }
          goto _L12
_L9:
        int j1;
        if (k >= l1)
        {
            return abyte0;
        }
        i1 = 0;
        j1 = 0;
        j = i;
        i = j1;
_L14:
label0:
        {
            if (i < 4)
            {
                break label0;
            }
            i = k + 1;
            abyte0[k] = (byte)(byte)(i1 >> 16);
            int k1;
            if (i < l1)
            {
                int l = i + 1;
                abyte0[i] = (byte)(byte)(i1 >> 8);
                if (l >= l1)
                {
                    i = l;
                } else
                {
                    i = l + 1;
                    abyte0[l] = (byte)(byte)i1;
                }
            }
            k = i;
            i = j;
        }
        if (true) goto _L9; else goto _L13
_L13:
        k1 = IA[s.charAt(j)];
        if (k1 < 0)
        {
            i--;
        } else
        {
            i1 |= k1 << 18 - i * 6;
        }
        i++;
        j++;
          goto _L14
    }

    public static final byte[] decode(byte abyte0[])
    {
        int i;
        int j;
        int i1;
        i1 = abyte0.length;
        j = 0;
        i = 0;
_L8:
        if (j < i1) goto _L2; else goto _L1
_L1:
        if ((i1 - i) % 4 != 0) goto _L4; else goto _L3
_L3:
        int k;
        j = i1;
        k = 0;
_L10:
        if (j > 1) goto _L6; else goto _L5
_L5:
        byte abyte1[];
        int l1;
        l1 = ((i1 - i) * 6 >> 3) - k;
        abyte1 = new byte[l1];
        k = 0;
        i = 0;
          goto _L7
_L2:
        if (IA[abyte0[j] & 0xff] < 0)
        {
            i++;
        }
        j++;
          goto _L8
_L4:
        return null;
_L6:
        abyte1 = IA;
        j1 = j - 1;
        if (abyte1[abyte0[j1] & 0xff] > 0) goto _L5; else goto _L9
_L9:
        j = j1;
        if (abyte0[j1] == 61)
        {
            k++;
            j = j1;
        }
          goto _L10
_L7:
        int j1;
        if (k >= l1)
        {
            return abyte1;
        }
        i1 = 0;
        j1 = 0;
        j = i;
        i = j1;
_L12:
label0:
        {
            if (i < 4)
            {
                break label0;
            }
            i = k + 1;
            abyte1[k] = (byte)(byte)(i1 >> 16);
            int k1;
            if (i < l1)
            {
                int l = i + 1;
                abyte1[i] = (byte)(byte)(i1 >> 8);
                if (l >= l1)
                {
                    i = l;
                } else
                {
                    i = l + 1;
                    abyte1[l] = (byte)(byte)i1;
                }
            }
            k = i;
            i = j;
        }
        if (true) goto _L7; else goto _L11
_L11:
        k1 = IA[abyte0[j] & 0xff];
        if (k1 < 0)
        {
            i--;
        } else
        {
            i1 |= k1 << 18 - i * 6;
        }
        i++;
        j++;
          goto _L12
    }

    public static final byte[] decode(char ac[])
    {
        byte abyte0[];
        int i;
        int j;
        int k;
        int i1;
        int l1;
        if (ac == null)
        {
            i = 0;
        } else
        {
            i = ac.length;
        }
        if (i == 0) goto _L2; else goto _L1
_L1:
        k = 0;
        j = 0;
_L10:
        if (k < i) goto _L4; else goto _L3
_L3:
        if ((i - j) % 4 != 0) goto _L6; else goto _L5
_L5:
        k = i;
        i1 = 0;
_L12:
        if (k > 1) goto _L8; else goto _L7
_L7:
        l1 = ((i - j) * 6 >> 3) - i1;
        abyte0 = new byte[l1];
        k = 0;
        i = 0;
          goto _L9
_L2:
        return new byte[0];
_L4:
        if (IA[ac[k]] < 0)
        {
            j++;
        }
        k++;
          goto _L10
_L6:
        return null;
_L8:
        abyte0 = IA;
        j1 = k - 1;
        if (abyte0[ac[j1]] > 0) goto _L7; else goto _L11
_L11:
        k = j1;
        if (ac[j1] == '=')
        {
            i1++;
            k = j1;
        }
          goto _L12
_L9:
        int j1;
        if (k >= l1)
        {
            return abyte0;
        }
        i1 = 0;
        j1 = 0;
        j = i;
        i = j1;
_L14:
label0:
        {
            if (i < 4)
            {
                break label0;
            }
            i = k + 1;
            abyte0[k] = (byte)(byte)(i1 >> 16);
            int k1;
            if (i < l1)
            {
                int l = i + 1;
                abyte0[i] = (byte)(byte)(i1 >> 8);
                if (l >= l1)
                {
                    i = l;
                } else
                {
                    i = l + 1;
                    abyte0[l] = (byte)(byte)i1;
                }
            }
            k = i;
            i = j;
        }
        if (true) goto _L9; else goto _L13
_L13:
        k1 = IA[ac[j]];
        if (k1 < 0)
        {
            i--;
        } else
        {
            i1 |= k1 << 18 - i * 6;
        }
        i++;
        j++;
          goto _L14
    }

    public static final byte[] decodeFast(String s)
    {
        int k;
        int k1;
        k1 = 0;
        k = s.length();
        if (k == 0) goto _L2; else goto _L1
_L1:
        byte abyte0[];
        int i;
        int j;
        int l;
        int i1;
        int j1;
        int i3;
        i1 = k - 1;
        i = 0;
        int j3;
        while (i < i1 && IA[s.charAt(i) & 0xff] < 0) 
        {
            i++;
        }
        while (i1 > 0 && IA[s.charAt(i1) & 0xff] < 0) 
        {
            i1--;
        }
        if (s.charAt(i1) != '=')
        {
            j = 0;
        } else
        if (s.charAt(i1 - 1) != '=')
        {
            j = 1;
        } else
        {
            j = 2;
        }
        l = (i1 - i) + 1;
        if (k <= 76)
        {
            j1 = 0;
        } else
        {
            if (s.charAt(76) != '\r')
            {
                k = 0;
            } else
            {
                k = l / 78;
            }
            j1 = k << 1;
        }
        i3 = ((l - j1) * 6 >> 3) - j;
        abyte0 = new byte[i3];
        j3 = i3 / 3;
        l = 0;
        k = 0;
_L7:
        if (k < j3 * 3) goto _L4; else goto _L3
_L3:
        if (k < i3) goto _L6; else goto _L5
_L5:
        return abyte0;
_L2:
        return new byte[0];
_L4:
        int ai[] = IA;
        int j2 = i + 1;
        i = ai[s.charAt(i)];
        ai = IA;
        int l1 = j2 + 1;
        j2 = ai[s.charAt(j2)];
        ai = IA;
        int k2 = l1 + 1;
        int k3 = ai[s.charAt(l1)];
        ai = IA;
        l1 = k2 + 1;
        i = k3 << 6 | (i << 18 | j2 << 12) | ai[s.charAt(k2)];
        j2 = k + 1;
        abyte0[k] = (byte)(i >> 16);
        k = j2 + 1;
        abyte0[j2] = (byte)(i >> 8);
        j2 = k + 1;
        abyte0[k] = (byte)i;
        i = l1;
        k = j2;
        if (j1 > 0)
        {
            int l2 = l + 1;
            l = l2;
            i = l1;
            k = j2;
            if (l2 == 19)
            {
                i = l1 + 2;
                l = 0;
                k = j2;
            }
        }
          goto _L7
_L6:
        l = 0;
        j1 = i;
        i = k1;
_L8:
label0:
        {
            k1 = i;
            if (j1 <= i1 - j)
            {
                break label0;
            }
            j = 16;
            i = k;
            while (i < i3) 
            {
                abyte0[i] = (byte)(l >> j);
                j -= 8;
                i++;
            }
        }
          goto _L5
        int i2 = IA[s.charAt(j1)];
        i = k1 + 1;
        l = i2 << 18 - k1 * 6 | l;
        j1++;
          goto _L8
    }

    public static final byte[] decodeFast(byte abyte0[])
    {
        int k;
        int k1;
        k1 = 0;
        k = abyte0.length;
        if (k == 0) goto _L2; else goto _L1
_L1:
        byte abyte1[];
        int i;
        int j;
        int l;
        int i1;
        int j1;
        int i3;
        i1 = k - 1;
        i = 0;
        int j3;
        while (i < i1 && IA[abyte0[i] & 0xff] < 0) 
        {
            i++;
        }
        while (i1 > 0 && IA[abyte0[i1] & 0xff] < 0) 
        {
            i1--;
        }
        if (abyte0[i1] != 61)
        {
            j = 0;
        } else
        if (abyte0[i1 - 1] != 61)
        {
            j = 1;
        } else
        {
            j = 2;
        }
        l = (i1 - i) + 1;
        if (k <= 76)
        {
            j1 = 0;
        } else
        {
            if (abyte0[76] != 13)
            {
                k = 0;
            } else
            {
                k = l / 78;
            }
            j1 = k << 1;
        }
        i3 = ((l - j1) * 6 >> 3) - j;
        abyte1 = new byte[i3];
        j3 = i3 / 3;
        l = 0;
        k = 0;
_L7:
        if (k < j3 * 3) goto _L4; else goto _L3
_L3:
        if (k < i3) goto _L6; else goto _L5
_L5:
        return abyte1;
_L2:
        return new byte[0];
_L4:
        int ai[] = IA;
        int j2 = i + 1;
        i = ai[abyte0[i]];
        ai = IA;
        int l1 = j2 + 1;
        j2 = ai[abyte0[j2]];
        ai = IA;
        int k2 = l1 + 1;
        int k3 = ai[abyte0[l1]];
        ai = IA;
        l1 = k2 + 1;
        i = k3 << 6 | (i << 18 | j2 << 12) | ai[abyte0[k2]];
        j2 = k + 1;
        abyte1[k] = (byte)(i >> 16);
        k = j2 + 1;
        abyte1[j2] = (byte)(i >> 8);
        j2 = k + 1;
        abyte1[k] = (byte)i;
        i = l1;
        k = j2;
        if (j1 > 0)
        {
            int l2 = l + 1;
            l = l2;
            i = l1;
            k = j2;
            if (l2 == 19)
            {
                i = l1 + 2;
                l = 0;
                k = j2;
            }
        }
          goto _L7
_L6:
        l = 0;
        j1 = i;
        i = k1;
_L8:
label0:
        {
            k1 = i;
            if (j1 <= i1 - j)
            {
                break label0;
            }
            j = 16;
            i = k;
            while (i < i3) 
            {
                abyte1[i] = (byte)(l >> j);
                j -= 8;
                i++;
            }
        }
          goto _L5
        int i2 = IA[abyte0[j1]];
        i = k1 + 1;
        l = i2 << 18 - k1 * 6 | l;
        j1++;
          goto _L8
    }

    public static final byte[] decodeFast(char ac[])
    {
        int k;
        int k1;
        k1 = 0;
        k = ac.length;
        if (k == 0) goto _L2; else goto _L1
_L1:
        byte abyte0[];
        int i;
        int j;
        int l;
        int i1;
        int j1;
        int i3;
        i1 = k - 1;
        i = 0;
        int j3;
        while (i < i1 && IA[ac[i]] < 0) 
        {
            i++;
        }
        while (i1 > 0 && IA[ac[i1]] < 0) 
        {
            i1--;
        }
        if (ac[i1] != '=')
        {
            j = 0;
        } else
        if (ac[i1 - 1] != '=')
        {
            j = 1;
        } else
        {
            j = 2;
        }
        l = (i1 - i) + 1;
        if (k <= 76)
        {
            j1 = 0;
        } else
        {
            if (ac[76] != '\r')
            {
                k = 0;
            } else
            {
                k = l / 78;
            }
            j1 = k << 1;
        }
        i3 = ((l - j1) * 6 >> 3) - j;
        abyte0 = new byte[i3];
        j3 = i3 / 3;
        l = 0;
        k = 0;
_L7:
        if (k < j3 * 3) goto _L4; else goto _L3
_L3:
        if (k < i3) goto _L6; else goto _L5
_L5:
        return abyte0;
_L2:
        return new byte[0];
_L4:
        int ai[] = IA;
        int j2 = i + 1;
        i = ai[ac[i]];
        ai = IA;
        int l1 = j2 + 1;
        j2 = ai[ac[j2]];
        ai = IA;
        int k2 = l1 + 1;
        int k3 = ai[ac[l1]];
        ai = IA;
        l1 = k2 + 1;
        i = k3 << 6 | (i << 18 | j2 << 12) | ai[ac[k2]];
        j2 = k + 1;
        abyte0[k] = (byte)(i >> 16);
        k = j2 + 1;
        abyte0[j2] = (byte)(i >> 8);
        j2 = k + 1;
        abyte0[k] = (byte)i;
        i = l1;
        k = j2;
        if (j1 > 0)
        {
            int l2 = l + 1;
            l = l2;
            i = l1;
            k = j2;
            if (l2 == 19)
            {
                i = l1 + 2;
                l = 0;
                k = j2;
            }
        }
          goto _L7
_L6:
        l = 0;
        j1 = i;
        i = k1;
_L8:
label0:
        {
            k1 = i;
            if (j1 <= i1 - j)
            {
                break label0;
            }
            j = 16;
            i = k;
            while (i < i3) 
            {
                abyte0[i] = (byte)(l >> j);
                j -= 8;
                i++;
            }
        }
          goto _L5
        int i2 = IA[ac[j1]];
        i = k1 + 1;
        l = i2 << 18 - k1 * 6 | l;
        j1++;
          goto _L8
    }

    public static final byte[] encodeToByte(byte abyte0[], boolean flag)
    {
        byte abyte1[];
        int i;
        int j;
        int k;
        int l;
        boolean flag1;
        int i2;
        int j2;
        flag1 = false;
        if (abyte0 == null)
        {
            k = 0;
        } else
        {
            k = abyte0.length;
        }
        if (k == 0) goto _L2; else goto _L1
_L1:
        j2 = (k / 3) * 3;
        j = (k - 1) / 3 + 1 << 2;
        if (!flag)
        {
            i = 0;
        } else
        {
            i = (j - 1) / 76 << 1;
        }
        i2 = j + i;
        abyte1 = new byte[i2];
        i = 0;
        j = 0;
        l = 0;
_L4:
        if (l >= j2)
        {
            j = k - j2;
            if (j <= 0)
            {
                return abyte1;
            }
            break; /* Loop/switch isn't completed */
        }
        int i1 = l + 1;
        l = abyte0[l];
        int j1 = i1 + 1;
        int k1 = abyte0[i1];
        i1 = j1 + 1;
        l = (k1 & 0xff) << 8 | (l & 0xff) << 16 | abyte0[j1] & 0xff;
        j1 = j + 1;
        abyte1[j] = (byte)CA[l >>> 18 & 0x3f];
        j = j1 + 1;
        abyte1[j1] = (byte)CA[l >>> 12 & 0x3f];
        k1 = j + 1;
        abyte1[j] = (byte)CA[l >>> 6 & 0x3f];
        j1 = k1 + 1;
        abyte1[k1] = (byte)CA[l & 0x3f];
        j = j1;
        l = i1;
        if (flag)
        {
            int l1 = i + 1;
            i = l1;
            j = j1;
            l = i1;
            if (l1 == 19)
            {
                i = l1;
                j = j1;
                l = i1;
                if (j1 < i2 - 2)
                {
                    i = j1 + 1;
                    abyte1[j1] = 13;
                    abyte1[i] = 10;
                    j = i + 1;
                    i = 0;
                    l = i1;
                }
            }
        }
        continue; /* Loop/switch isn't completed */
_L2:
        return new byte[0];
        if (true) goto _L4; else goto _L3
_L3:
        byte byte1 = abyte0[j2];
        byte byte0;
        if (j != 2)
        {
            byte0 = flag1;
        } else
        {
            byte0 = (abyte0[k - 1] & 0xff) << 2;
        }
        byte0 = (byte1 & 0xff) << 10 | byte0;
        abyte1[i2 - 4] = (byte)CA[byte0 >> 12];
        abyte1[i2 - 3] = (byte)CA[byte0 >>> 6 & 0x3f];
        if (j != 2)
        {
            byte0 = 61;
        } else
        {
            byte0 = (byte)CA[byte0 & 0x3f];
        }
        abyte1[i2 - 2] = (byte)byte0;
        abyte1[i2 - 1] = 61;
        return abyte1;
    }

    public static final char[] encodeToChar(byte abyte0[], boolean flag)
    {
        char ac[];
        int i;
        int k;
        int l;
        int i1;
        boolean flag1;
        int j2;
        int k2;
        flag1 = false;
        if (abyte0 == null)
        {
            l = 0;
        } else
        {
            l = abyte0.length;
        }
        if (l == 0) goto _L2; else goto _L1
_L1:
        k2 = (l / 3) * 3;
        k = (l - 1) / 3 + 1 << 2;
        if (!flag)
        {
            i = 0;
        } else
        {
            i = (k - 1) / 76 << 1;
        }
        j2 = k + i;
        ac = new char[j2];
        i = 0;
        k = 0;
        i1 = 0;
_L4:
        if (i1 >= k2)
        {
            k = l - k2;
            if (k <= 0)
            {
                return ac;
            }
            break; /* Loop/switch isn't completed */
        }
        int j1 = i1 + 1;
        i1 = abyte0[i1];
        int k1 = j1 + 1;
        int l1 = abyte0[j1];
        j1 = k1 + 1;
        i1 = (l1 & 0xff) << 8 | (i1 & 0xff) << 16 | abyte0[k1] & 0xff;
        k1 = k + 1;
        ac[k] = CA[i1 >>> 18 & 0x3f];
        k = k1 + 1;
        ac[k1] = CA[i1 >>> 12 & 0x3f];
        l1 = k + 1;
        ac[k] = CA[i1 >>> 6 & 0x3f];
        k1 = l1 + 1;
        ac[l1] = CA[i1 & 0x3f];
        k = k1;
        i1 = j1;
        if (flag)
        {
            int i2 = i + 1;
            i = i2;
            k = k1;
            i1 = j1;
            if (i2 == 19)
            {
                i = i2;
                k = k1;
                i1 = j1;
                if (k1 < j2 - 2)
                {
                    i = k1 + 1;
                    ac[k1] = '\r';
                    ac[i] = '\n';
                    k = i + 1;
                    i = 0;
                    i1 = j1;
                }
            }
        }
        continue; /* Loop/switch isn't completed */
_L2:
        return new char[0];
        if (true) goto _L4; else goto _L3
_L3:
        byte byte0 = abyte0[k2];
        int j;
        if (k != 2)
        {
            j = ((flag1) ? 1 : 0);
        } else
        {
            j = (abyte0[l - 1] & 0xff) << 2;
        }
        j = (byte0 & 0xff) << 10 | j;
        ac[j2 - 4] = CA[j >> 12];
        ac[j2 - 3] = CA[j >>> 6 & 0x3f];
        if (k != 2)
        {
            j = 61;
        } else
        {
            j = CA[j & 0x3f];
        }
        ac[j2 - 2] = (char)j;
        ac[j2 - 1] = '=';
        return ac;
    }

    public static final String encodeToString(byte abyte0[], boolean flag)
    {
        return new String(encodeToChar(abyte0, flag));
    }

    static 
    {
        CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        IA = new int[256];
        Arrays.fill(IA, -1);
        int j = CA.length;
        int i = 0;
        do
        {
            if (i >= j)
            {
                IA[61] = 0;
                return;
            }
            IA[CA[i]] = i;
            i++;
        } while (true);
    }
}
