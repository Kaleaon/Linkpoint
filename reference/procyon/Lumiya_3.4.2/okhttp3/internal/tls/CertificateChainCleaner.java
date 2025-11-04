// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.tls;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
import java.util.List;
import java.security.cert.X509Certificate;
import okhttp3.internal.platform.Platform;
import javax.net.ssl.X509TrustManager;

public abstract class CertificateChainCleaner
{
    public static CertificateChainCleaner get(final X509TrustManager x509TrustManager) {
        return Platform.get().buildCertificateChainCleaner(x509TrustManager);
    }
    
    public static CertificateChainCleaner get(final X509Certificate... array) {
        return new BasicCertificateChainCleaner(TrustRootIndex.get(array));
    }
    
    public abstract List<Certificate> clean(final List<Certificate> p0, final String p1) throws SSLPeerUnverifiedException;
}
