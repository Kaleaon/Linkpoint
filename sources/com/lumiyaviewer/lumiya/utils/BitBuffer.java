package com.lumiyaviewer.lumiya.utils;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class BitBuffer {
    private static final int MAX_BITS = 8;
    private int bitPos;
    private ByteBuffer buf;
    private int bytePos;
    private byte[] output = new byte[4];

    public BitBuffer(byte[] bArr) {
        this.buf = ByteBuffer.wrap(bArr);
        this.bytePos = 0;
        this.bitPos = 0;
    }

    public int getBits(int i) {
        int i2;
        Arrays.fill(this.output, (byte) 0);
        int i3 = 0;
        int i4 = 0;
        int i5 = i;
        while (i5 > 0) {
            if (i5 > 8) {
                i2 = i5 - 8;
                i5 = 8;
            } else {
                i2 = 0;
            }
            while (i5 > 0) {
                byte[] bArr = this.output;
                bArr[i4] = (byte) (bArr[i4] << 1);
                if ((this.buf.get(this.bytePos) & (128 >> this.bitPos)) != 0) {
                    byte[] bArr2 = this.output;
                    bArr2[i4] = (byte) (bArr2[i4] | 1);
                }
                this.bitPos++;
                i5--;
                i3++;
                if (this.bitPos >= 8) {
                    this.bitPos = 0;
                    this.bytePos++;
                }
                if (i3 >= 8) {
                    i4++;
                    i3 = 0;
                }
            }
            i5 = i2;
        }
        return (this.output[0] & UnsignedBytes.MAX_VALUE) | ((this.output[1] << 8) & 65280) | ((this.output[2] << 16) & 16711680) | ((this.output[3] << Ascii.CAN) & -16777216);
    }

    public float getFloat() {
        return Float.intBitsToFloat(getBits(32));
    }

    public boolean isEOF() {
        return this.bytePos >= this.buf.limit();
    }
}
