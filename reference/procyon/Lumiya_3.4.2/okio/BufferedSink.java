// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.nio.charset.Charset;
import java.io.OutputStream;
import java.io.IOException;

public interface BufferedSink extends Sink
{
    Buffer buffer();
    
    BufferedSink emit() throws IOException;
    
    BufferedSink emitCompleteSegments() throws IOException;
    
    void flush() throws IOException;
    
    OutputStream outputStream();
    
    BufferedSink write(final ByteString p0) throws IOException;
    
    BufferedSink write(final Source p0, final long p1) throws IOException;
    
    BufferedSink write(final byte[] p0) throws IOException;
    
    BufferedSink write(final byte[] p0, final int p1, final int p2) throws IOException;
    
    long writeAll(final Source p0) throws IOException;
    
    BufferedSink writeByte(final int p0) throws IOException;
    
    BufferedSink writeDecimalLong(final long p0) throws IOException;
    
    BufferedSink writeHexadecimalUnsignedLong(final long p0) throws IOException;
    
    BufferedSink writeInt(final int p0) throws IOException;
    
    BufferedSink writeIntLe(final int p0) throws IOException;
    
    BufferedSink writeLong(final long p0) throws IOException;
    
    BufferedSink writeLongLe(final long p0) throws IOException;
    
    BufferedSink writeShort(final int p0) throws IOException;
    
    BufferedSink writeShortLe(final int p0) throws IOException;
    
    BufferedSink writeString(final String p0, final int p1, final int p2, final Charset p3) throws IOException;
    
    BufferedSink writeString(final String p0, final Charset p1) throws IOException;
    
    BufferedSink writeUtf8(final String p0) throws IOException;
    
    BufferedSink writeUtf8(final String p0, final int p1, final int p2) throws IOException;
    
    BufferedSink writeUtf8CodePoint(final int p0) throws IOException;
}
