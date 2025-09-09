package com.lumiyaviewer.lumiya.utils;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInputStream implements DataInput {
    private byte[] buf = new byte[8];
    private InputStream inputStream;

    public LittleEndianDataInputStream(InputStream inputStream2) {
        this.inputStream = inputStream2;
    }

    public boolean readBoolean() throws IOException {
        int read = this.inputStream.read();
        if (read != -1) {
            return read != 0;
        }
        throw new EOFException("End of stream");
    }

    public byte readByte() throws IOException {
        int read = this.inputStream.read();
        if (read != -1) {
            return (byte) (read & 255);
        }
        throw new EOFException("End of stream");
    }

    public char readChar() throws IOException {
        int read = this.inputStream.read();
        if (read != -1) {
            return (char) (read & 255);
        }
        throw new EOFException("End of stream");
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public void readFully(byte[] bArr) throws IOException {
        if (this.inputStream.read(bArr, 0, bArr.length) != bArr.length) {
            throw new EOFException("End of stream");
        }
    }

    public void readFully(byte[] bArr, int i, int i2) throws IOException {
        if (this.inputStream.read(bArr, i, i2) != i2) {
            throw new EOFException("End of stream");
        }
    }

    public int readInt() throws IOException {
        if (this.inputStream.read(this.buf, 0, 4) == 4) {
            return (this.buf[3] << Ascii.CAN) | ((this.buf[2] & UnsignedBytes.MAX_VALUE) << 16) | ((this.buf[1] & UnsignedBytes.MAX_VALUE) << 8) | (this.buf[0] & UnsignedBytes.MAX_VALUE);
        }
        throw new EOFException("End of stream");
    }

    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char readChar = readChar();
            if (readChar != 10 && readChar != 13) {
                sb.append(readChar);
            }
        }
        return sb.toString();
    }

    public long readLong() throws IOException {
        if (this.inputStream.read(this.buf, 0, 8) == 8) {
            return (((long) this.buf[7]) << 56) | (((long) (this.buf[6] & UnsignedBytes.MAX_VALUE)) << 48) | (((long) (this.buf[5] & UnsignedBytes.MAX_VALUE)) << 40) | (((long) (this.buf[4] & UnsignedBytes.MAX_VALUE)) << 32) | (((long) (this.buf[3] & UnsignedBytes.MAX_VALUE)) << 24) | (((long) (this.buf[2] & UnsignedBytes.MAX_VALUE)) << 16) | (((long) (this.buf[1] & UnsignedBytes.MAX_VALUE)) << 8) | ((long) (this.buf[0] & UnsignedBytes.MAX_VALUE));
        }
        throw new EOFException("End of stream");
    }

    public short readShort() throws IOException {
        if (this.inputStream.read(this.buf, 0, 2) == 2) {
            return (short) (((this.buf[1] & UnsignedBytes.MAX_VALUE) << 8) | (this.buf[0] & UnsignedBytes.MAX_VALUE));
        }
        throw new EOFException("End of stream");
    }

    public String readUTF() throws IOException {
        return readLine();
    }

    public int readUnsignedByte() throws IOException {
        int read = this.inputStream.read();
        if (read != -1) {
            return read & 255;
        }
        throw new EOFException("End of stream");
    }

    public int readUnsignedShort() throws IOException {
        if (this.inputStream.read(this.buf, 0, 2) == 2) {
            return ((this.buf[1] & UnsignedBytes.MAX_VALUE) << 8) | (this.buf[0] & UnsignedBytes.MAX_VALUE);
        }
        throw new EOFException("End of stream");
    }

    public LLVector3 readVector3() throws IOException {
        return new LLVector3(readFloat(), readFloat(), readFloat());
    }

    public String readZeroTerminatedString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char readChar = readChar();
            if (readChar == 0) {
                return sb.toString();
            }
            sb.append(readChar);
        }
    }

    public int skipBytes(int i) throws IOException {
        return (int) this.inputStream.skip((long) i);
    }
}
