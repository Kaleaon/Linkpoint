// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import com.google.common.primitives.Longs;
import java.io.IOException;
import java.io.DataOutputStream;
import com.google.common.base.Preconditions;
import java.io.OutputStream;
import com.google.common.annotations.Beta;
import java.io.DataOutput;
import java.io.FilterOutputStream;

@Beta
public class LittleEndianDataOutputStream extends FilterOutputStream implements DataOutput
{
    public LittleEndianDataOutputStream(final OutputStream outputStream) {
        super(new DataOutputStream(Preconditions.checkNotNull(outputStream)));
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
    }
    
    @Override
    public void writeBoolean(final boolean v) throws IOException {
        ((DataOutputStream)this.out).writeBoolean(v);
    }
    
    @Override
    public void writeByte(final int v) throws IOException {
        ((DataOutputStream)this.out).writeByte(v);
    }
    
    @Deprecated
    @Override
    public void writeBytes(final String s) throws IOException {
        ((DataOutputStream)this.out).writeBytes(s);
    }
    
    @Override
    public void writeChar(final int n) throws IOException {
        this.writeShort(n);
    }
    
    @Override
    public void writeChars(final String s) throws IOException {
        for (int i = 0; i < s.length(); ++i) {
            this.writeChar(s.charAt(i));
        }
    }
    
    @Override
    public void writeDouble(final double value) throws IOException {
        this.writeLong(Double.doubleToLongBits(value));
    }
    
    @Override
    public void writeFloat(final float value) throws IOException {
        this.writeInt(Float.floatToIntBits(value));
    }
    
    @Override
    public void writeInt(final int n) throws IOException {
        this.out.write(n & 0xFF);
        this.out.write(n >> 8 & 0xFF);
        this.out.write(n >> 16 & 0xFF);
        this.out.write(n >> 24 & 0xFF);
    }
    
    @Override
    public void writeLong(final long i) throws IOException {
        final byte[] byteArray = Longs.toByteArray(Long.reverseBytes(i));
        this.write(byteArray, 0, byteArray.length);
    }
    
    @Override
    public void writeShort(final int n) throws IOException {
        this.out.write(n & 0xFF);
        this.out.write(n >> 8 & 0xFF);
    }
    
    @Override
    public void writeUTF(final String str) throws IOException {
        ((DataOutputStream)this.out).writeUTF(str);
    }
}
