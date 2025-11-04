// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.DataInputStream;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.EOFException;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import com.google.common.annotations.Beta;
import java.io.DataInput;
import java.io.FilterInputStream;

@Beta
public final class LittleEndianDataInputStream extends FilterInputStream implements DataInput
{
    public LittleEndianDataInputStream(final InputStream inputStream) {
        super(Preconditions.checkNotNull(inputStream));
    }
    
    private byte readAndCheckByte() throws IOException, EOFException {
        final int read = this.in.read();
        if (-1 != read) {
            return (byte)read;
        }
        throw new EOFException();
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        boolean b = false;
        if (this.readUnsignedByte() != 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public byte readByte() throws IOException {
        return (byte)this.readUnsignedByte();
    }
    
    @Override
    public char readChar() throws IOException {
        return (char)this.readUnsignedShort();
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
    public void readFully(final byte[] array) throws IOException {
        ByteStreams.readFully(this, array);
    }
    
    @Override
    public void readFully(final byte[] array, final int n, final int n2) throws IOException {
        ByteStreams.readFully(this, array, n, n2);
    }
    
    @Override
    public int readInt() throws IOException {
        return Ints.fromBytes(this.readAndCheckByte(), this.readAndCheckByte(), this.readAndCheckByte(), this.readAndCheckByte());
    }
    
    @Override
    public String readLine() {
        throw new UnsupportedOperationException("readLine is not supported");
    }
    
    @Override
    public long readLong() throws IOException {
        return Longs.fromBytes(this.readAndCheckByte(), this.readAndCheckByte(), this.readAndCheckByte(), this.readAndCheckByte(), this.readAndCheckByte(), this.readAndCheckByte(), this.readAndCheckByte(), this.readAndCheckByte());
    }
    
    @Override
    public short readShort() throws IOException {
        return (short)this.readUnsignedShort();
    }
    
    @Override
    public String readUTF() throws IOException {
        return new DataInputStream(this.in).readUTF();
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        final int read = this.in.read();
        if (read >= 0) {
            return read;
        }
        throw new EOFException();
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        return Ints.fromBytes((byte)0, (byte)0, this.readAndCheckByte(), this.readAndCheckByte());
    }
    
    @Override
    public int skipBytes(final int n) throws IOException {
        return (int)this.in.skip(n);
    }
}
