// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.tls;

import java.util.Iterator;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ArrayDeque;
import java.security.cert.Certificate;
import java.util.List;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public final class BasicCertificateChainCleaner extends CertificateChainCleaner
{
    private static final int MAX_SIGNERS = 9;
    private final TrustRootIndex trustRootIndex;
    
    public BasicCertificateChainCleaner(final TrustRootIndex trustRootIndex) {
        this.trustRootIndex = trustRootIndex;
    }
    
    private boolean verifySignature(final X509Certificate x509Certificate, final X509Certificate x509Certificate2) {
        if (!x509Certificate.getIssuerDN().equals(x509Certificate2.getSubjectDN())) {
            return false;
        }
        try {
            x509Certificate.verify(x509Certificate2.getPublicKey());
            return true;
        }
        catch (final GeneralSecurityException ex) {
            return false;
        }
    }
    
    @Override
    public List<Certificate> clean(final List<Certificate> c, final String s) throws SSLPeerUnverifiedException {
        final ArrayDeque arrayDeque = new ArrayDeque((Collection<? extends E>)c);
        final ArrayList obj = new ArrayList();
        obj.add(arrayDeque.removeFirst());
        int i = 0;
        int n = 0;
        while (i < 9) {
            final X509Certificate obj2 = (X509Certificate)obj.get(obj.size() - 1);
            final X509Certificate byIssuerAndSignature = this.trustRootIndex.findByIssuerAndSignature(obj2);
            Label_0187: {
                if (byIssuerAndSignature == null) {
                    final Iterator iterator = arrayDeque.iterator();
                    while (iterator.hasNext()) {
                        final X509Certificate x509Certificate = (X509Certificate)iterator.next();
                        if (this.verifySignature(obj2, x509Certificate)) {
                            iterator.remove();
                            obj.add(x509Certificate);
                            break Label_0187;
                        }
                    }
                    if (n == 0) {
                        throw new SSLPeerUnverifiedException("Failed to find a trusted cert that signed " + obj2);
                    }
                    return obj;
                }
                else {
                    if (obj.size() > 1 || !obj2.equals(byIssuerAndSignature)) {
                        obj.add(byIssuerAndSignature);
                    }
                    if (this.verifySignature(byIssuerAndSignature, byIssuerAndSignature)) {
                        return obj;
                    }
                    n = 1;
                }
            }
            ++i;
        }
        throw new SSLPeerUnverifiedException("Certificate chain too long: " + obj);
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o != this) {
            if (!(o instanceof BasicCertificateChainCleaner) || !((BasicCertificateChainCleaner)o).trustRootIndex.equals(this.trustRootIndex)) {
                b = false;
            }
            return b;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.trustRootIndex.hashCode();
    }
}
