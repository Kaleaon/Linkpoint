// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.connection;

import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLHandshakeException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.io.IOException;
import java.net.UnknownServiceException;
import java.util.Arrays;
import okhttp3.internal.Internal;
import javax.net.ssl.SSLSocket;
import okhttp3.ConnectionSpec;
import java.util.List;

public final class ConnectionSpecSelector
{
    private final List<ConnectionSpec> connectionSpecs;
    private boolean isFallback;
    private boolean isFallbackPossible;
    private int nextModeIndex;
    
    public ConnectionSpecSelector(final List<ConnectionSpec> connectionSpecs) {
        this.nextModeIndex = 0;
        this.connectionSpecs = connectionSpecs;
    }
    
    private boolean isFallbackPossible(final SSLSocket sslSocket) {
        for (int i = this.nextModeIndex; i < this.connectionSpecs.size(); ++i) {
            if (this.connectionSpecs.get(i).isCompatible(sslSocket)) {
                return true;
            }
        }
        return false;
    }
    
    public ConnectionSpec configureSecureSocket(final SSLSocket sslSocket) throws IOException {
        int i = this.nextModeIndex;
        while (true) {
            while (i < this.connectionSpecs.size()) {
                final ConnectionSpec connectionSpec = this.connectionSpecs.get(i);
                if (!connectionSpec.isCompatible(sslSocket)) {
                    ++i;
                }
                else {
                    this.nextModeIndex = i + 1;
                    if (connectionSpec != null) {
                        this.isFallbackPossible = this.isFallbackPossible(sslSocket);
                        Internal.instance.apply(connectionSpec, sslSocket, this.isFallback);
                        return connectionSpec;
                    }
                    throw new UnknownServiceException("Unable to find acceptable protocols. isFallback=" + this.isFallback + ", modes=" + this.connectionSpecs + ", supported protocols=" + Arrays.toString(sslSocket.getEnabledProtocols()));
                }
            }
            final ConnectionSpec connectionSpec = null;
            continue;
        }
    }
    
    public boolean connectionFailed(final IOException ex) {
        boolean b = false;
        this.isFallback = true;
        if (!this.isFallbackPossible) {
            return false;
        }
        if (ex instanceof ProtocolException) {
            return false;
        }
        if (ex instanceof InterruptedIOException) {
            return false;
        }
        if (ex instanceof SSLHandshakeException && ex.getCause() instanceof CertificateException) {
            return false;
        }
        if (!(ex instanceof SSLPeerUnverifiedException)) {
            if (ex instanceof SSLHandshakeException || ex instanceof SSLProtocolException) {
                b = true;
            }
            return b;
        }
        return false;
    }
}
