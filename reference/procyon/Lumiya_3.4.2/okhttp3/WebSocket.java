// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import okio.ByteString;

public interface WebSocket
{
    void cancel();
    
    boolean close(final int p0, final String p1);
    
    long queueSize();
    
    Request request();
    
    boolean send(final String p0);
    
    boolean send(final ByteString p0);
    
    public interface Factory
    {
        WebSocket newWebSocket(final Request p0, final WebSocketListener p1);
    }
}
