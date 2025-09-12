// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BitBuffer
{

    private static final int MAX_BITS = 8;
    private int bitPos;
    private ByteBuffer buf;
    private int bytePos;
    private byte output[];

    public BitBuffer(byte abyte0[])
    {
        output = new byte[4];
        buf = ByteBuffer.wrap(abyte0);
        bytePos = 0;
        bitPos = 0;
    }

    public int getBits(int i)
    {
        Arrays.fill(output, (byte)0);
        boolean flag = false;
        int l = 0;
        int k = i;
        i = ((flag) ? 1 : 0);
        int j;
label0:
        for (; k > 0; k = j)
        {
            if (k > 8)
            {
                j = k - 8;
                k = 8;
            } else
            {
                j = 0;
            }
            do
            {
                if (k <= 0)
                {
                    continue label0;
                }
                byte abyte0[] = output;
                abyte0[l] = (byte)(abyte0[l] << 1);
                if ((buf.get(bytePos) & 128 >> bitPos) != 0)
                {
                    byte abyte1[] = output;
                    abyte1[l] = (byte)(abyte1[l] | 1);
                }
                bitPos = bitPos + 1;
                int i1 = k - 1;
                int j1 = i + 1;
                if (bitPos >= 8)
                {
                    bitPos = 0;
                    bytePos = bytePos + 1;
                }
                i = j1;
                k = i1;
                if (j1 >= 8)
                {
                    l++;
                    i = 0;
                    k = i1;
                }
            } while (true);
        }

        return output[0] & 0xff | output[1] << 8 & 0xff00 | output[2] << 16 & 0xff0000 | output[3] << 24 & 0xff000000;
    }

    public float getFloat()
    {
        return Float.intBitsToFloat(getBits(32));
    }

    public boolean isEOF()
    {
        return bytePos >= buf.limit();
    }
}
