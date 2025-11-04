// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.DataInput;

public interface ByteArrayDataInput extends DataInput
{
    boolean readBoolean();
    
    byte readByte();
    
    char readChar();
    
    double readDouble();
    
    float readFloat();
    
    void readFully(final byte[] p0);
    
    void readFully(final byte[] p0, final int p1, final int p2);
    
    int readInt();
    
    String readLine();
    
    long readLong();
    
    short readShort();
    
    String readUTF();
    
    int readUnsignedByte();
    
    int readUnsignedShort();
    
    int skipBytes(final int p0);
}
