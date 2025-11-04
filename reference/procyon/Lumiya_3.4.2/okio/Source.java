// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.IOException;
import java.io.Closeable;

public interface Source extends Closeable
{
    void close() throws IOException;
    
    long read(final Buffer p0, final long p1) throws IOException;
    
    Timeout timeout();
}
