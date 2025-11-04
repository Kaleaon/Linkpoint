// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.DataInput;

public class LittleEndianDataInputStream implements DataInput
{
    private byte[] buf;
    private InputStream inputStream;
    
    public LittleEndianDataInputStream(final InputStream inputStream) {
        this.buf = new byte[8];
        this.inputStream = inputStream;
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        boolean b = false;
        final int read = this.inputStream.read();
        if (read == -1) {
            throw new EOFException("End of stream");
        }
        if (read != 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public byte readByte() throws IOException {
        final int read = this.inputStream.read();
        if (read == -1) {
            throw new EOFException("End of stream");
        }
        return (byte)(read & 0xFF);
    }
    
    @Override
    public char readChar() throws IOException {
        final int read = this.inputStream.read();
        if (read == -1) {
            throw new EOFException("End of stream");
        }
        return (char)(read & 0xFF);
    }
    
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }
    
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }
    
    @Override
    public void readFully(final byte[] b) throws IOException {
        if (this.inputStream.read(b, 0, b.length) != b.length) {
            throw new EOFException("End of stream");
        }
    }
    
    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        if (this.inputStream.read(b, off, len) != len) {
            throw new EOFException("End of stream");
        }
    }
    
    @Override
    public int readInt() throws IOException {
        if (this.inputStream.read(this.buf, 0, 4) != 4) {
            throw new EOFException("End of stream");
        }
        return this.buf[3] << 24 | (this.buf[2] & 0xFF) << 16 | (this.buf[1] & 0xFF) << 8 | (this.buf[0] & 0xFF);
    }
    
    @Override
    public String readLine() throws IOException {
        final StringBuilder sb = new StringBuilder();
        while (true) {
            final char char1 = this.readChar();
            if (char1 == '\n' || char1 == '\r') {
                break;
            }
            sb.append(char1);
        }
        return sb.toString();
    }
    
    @Override
    public long readLong() throws IOException {
        if (this.inputStream.read(this.buf, 0, 8) != 8) {
            throw new EOFException("End of stream");
        }
        return (long)this.buf[7] << 56 | (long)(this.buf[6] & 0xFF) << 48 | (long)(this.buf[5] & 0xFF) << 40 | (long)(this.buf[4] & 0xFF) << 32 | (long)(this.buf[3] & 0xFF) << 24 | (long)(this.buf[2] & 0xFF) << 16 | (long)(this.buf[1] & 0xFF) << 8 | (long)(this.buf[0] & 0xFF);
    }
    
    @Override
    public short readShort() throws IOException {
        if (this.inputStream.read(this.buf, 0, 2) != 2) {
            throw new EOFException("End of stream");
        }
        return (short)((this.buf[1] & 0xFF) << 8 | (this.buf[0] & 0xFF));
    }
    
    @Override
    public String readUTF() throws IOException {
        return this.readLine();
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        final int read = this.inputStream.read();
        if (read == -1) {
            throw new EOFException("End of stream");
        }
        return read & 0xFF;
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        if (this.inputStream.read(this.buf, 0, 2) != 2) {
            throw new EOFException("End of stream");
        }
        return (this.buf[1] & 0xFF) << 8 | (this.buf[0] & 0xFF);
    }
    
    public LLVector3 readVector3() throws IOException {
        return new LLVector3(this.readFloat(), this.readFloat(), this.readFloat());
    }
    
    public String readZeroTerminatedString() throws IOException {
        final StringBuilder sb = new StringBuilder();
        while (true) {
            final char char1 = this.readChar();
            if (char1 == '\0') {
                break;
            }
            sb.append(char1);
        }
        return sb.toString();
    }
    
    @Override
    public int skipBytes(final int n) throws IOException {
        return (int)this.inputStream.skip(n);
    }
}
