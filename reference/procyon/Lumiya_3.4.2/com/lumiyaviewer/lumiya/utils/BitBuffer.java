// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import java.util.Arrays;
import java.nio.ByteBuffer;

public class BitBuffer
{
    private static final int MAX_BITS = 8;
    private int bitPos;
    private ByteBuffer buf;
    private int bytePos;
    private byte[] output;
    
    public BitBuffer(final byte[] array) {
        this.output = new byte[4];
        this.buf = ByteBuffer.wrap(array);
        this.bytePos = 0;
        this.bitPos = 0;
    }
    
    public int getBits(int n) {
        Arrays.fill(this.output, (byte)0);
        int n2 = 0;
        int n3 = 0;
        for (int i = n; i > 0; i = n) {
            if (i > 8) {
                n = i - 8;
                i = 8;
            }
            else {
                n = 0;
            }
            while (i > 0) {
                final byte[] output = this.output;
                output[n3] <<= 1;
                if ((this.buf.get(this.bytePos) & 128 >> this.bitPos) != 0x0) {
                    final byte[] output2 = this.output;
                    output2[n3] |= 0x1;
                }
                ++this.bitPos;
                final int n4 = i - 1;
                final int n5 = n2 + 1;
                if (this.bitPos >= 8) {
                    this.bitPos = 0;
                    ++this.bytePos;
                }
                n2 = n5;
                i = n4;
                if (n5 >= 8) {
                    ++n3;
                    n2 = 0;
                    i = n4;
                }
            }
        }
        return (this.output[0] & 0xFF) | (this.output[1] << 8 & 0xFF00) | (this.output[2] << 16 & 0xFF0000) | (this.output[3] << 24 & 0xFF000000);
    }
    
    public float getFloat() {
        return Float.intBitsToFloat(this.getBits(32));
    }
    
    public boolean isEOF() {
        return this.bytePos >= this.buf.limit();
    }
}
