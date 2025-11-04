// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.net.InetAddress;
import java.util.List;

public interface Dns
{
    public static final Dns SYSTEM = new Dns() {
        @Override
        public List<InetAddress> lookup(final String host) throws UnknownHostException {
            if (host != null) {
                return Arrays.asList(InetAddress.getAllByName(host));
            }
            throw new UnknownHostException("hostname == null");
        }
    };
    
    List<InetAddress> lookup(final String p0) throws UnknownHostException;
}
