// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.IOException;
import java.io.Flushable;
import java.io.Closeable;

public interface Sink extends Closeable, Flushable
{
    void close() throws IOException;
    
    void flush() throws IOException;
    
    Timeout timeout();
    
    void write(final Buffer p0, final long p1) throws IOException;
}
