// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.io.IOException;

public enum Protocol
{
    HTTP_1_0("http/1.0"), 
    HTTP_1_1("http/1.1"), 
    HTTP_2("h2"), 
    SPDY_3("spdy/3.1");
    
    private final String protocol;
    
    private Protocol(final String protocol) {
        this.protocol = protocol;
    }
    
    public static Protocol get(final String str) throws IOException {
        if (str.equals(Protocol.HTTP_1_0.protocol)) {
            return Protocol.HTTP_1_0;
        }
        if (str.equals(Protocol.HTTP_1_1.protocol)) {
            return Protocol.HTTP_1_1;
        }
        if (str.equals(Protocol.HTTP_2.protocol)) {
            return Protocol.HTTP_2;
        }
        if (!str.equals(Protocol.SPDY_3.protocol)) {
            throw new IOException("Unexpected protocol: " + str);
        }
        return Protocol.SPDY_3;
    }
    
    @Override
    public String toString() {
        return this.protocol;
    }
}
