// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.tls;

import java.security.PublicKey;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.security.cert.TrustAnchor;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public abstract class TrustRootIndex
{
    public static TrustRootIndex get(final X509TrustManager x509TrustManager) {
        try {
            final Method declaredMethod = x509TrustManager.getClass().getDeclaredMethod("findTrustAnchorByIssuerAndSignature", X509Certificate.class);
            declaredMethod.setAccessible(true);
            return new AndroidTrustRootIndex(x509TrustManager, declaredMethod);
        }
        catch (final NoSuchMethodException ex) {
            return get(x509TrustManager.getAcceptedIssuers());
        }
    }
    
    public static TrustRootIndex get(final X509Certificate... array) {
        return new BasicTrustRootIndex(array);
    }
    
    public abstract X509Certificate findByIssuerAndSignature(final X509Certificate p0);
    
    static final class AndroidTrustRootIndex extends TrustRootIndex
    {
        private final Method findByIssuerAndSignatureMethod;
        private final X509TrustManager trustManager;
        
        AndroidTrustRootIndex(final X509TrustManager trustManager, final Method findByIssuerAndSignatureMethod) {
            this.findByIssuerAndSignatureMethod = findByIssuerAndSignatureMethod;
            this.trustManager = trustManager;
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = true;
            if (o == this) {
                return true;
            }
            if (o instanceof AndroidTrustRootIndex) {
                final AndroidTrustRootIndex androidTrustRootIndex = (AndroidTrustRootIndex)o;
                if (!this.trustManager.equals(androidTrustRootIndex.trustManager) || !this.findByIssuerAndSignatureMethod.equals(androidTrustRootIndex.findByIssuerAndSignatureMethod)) {
                    b = false;
                }
                return b;
            }
            return false;
        }
        
        @Override
        public X509Certificate findByIssuerAndSignature(X509Certificate trustedCert) {
            try {
                final TrustAnchor trustAnchor = (TrustAnchor)this.findByIssuerAndSignatureMethod.invoke(this.trustManager, trustedCert);
                if (trustAnchor == null) {
                    trustedCert = null;
                }
                else {
                    trustedCert = trustAnchor.getTrustedCert();
                }
                return trustedCert;
            }
            catch (final IllegalAccessException ex) {
                throw new AssertionError();
            }
            catch (final InvocationTargetException ex2) {
                return null;
            }
        }
        
        @Override
        public int hashCode() {
            return this.trustManager.hashCode() + this.findByIssuerAndSignatureMethod.hashCode() * 31;
        }
    }
    
    static final class BasicTrustRootIndex extends TrustRootIndex
    {
        private final Map<X500Principal, Set<X509Certificate>> subjectToCaCerts;
        
        public BasicTrustRootIndex(final X509Certificate... array) {
            this.subjectToCaCerts = new LinkedHashMap<X500Principal, Set<X509Certificate>>();
            for (final X509Certificate x509Certificate : array) {
                final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
                Set set = this.subjectToCaCerts.get(subjectX500Principal);
                if (set == null) {
                    set = new LinkedHashSet(1);
                    this.subjectToCaCerts.put(subjectX500Principal, set);
                }
                set.add(x509Certificate);
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = true;
            if (o != this) {
                if (!(o instanceof BasicTrustRootIndex) || !((BasicTrustRootIndex)o).subjectToCaCerts.equals(this.subjectToCaCerts)) {
                    b = false;
                }
                return b;
            }
            return true;
        }
        
        @Override
        public X509Certificate findByIssuerAndSignature(final X509Certificate x509Certificate) {
            final Set set = this.subjectToCaCerts.get(x509Certificate.getIssuerX500Principal());
            if (set != null) {
                for (final X509Certificate x509Certificate2 : set) {
                    final PublicKey publicKey = x509Certificate2.getPublicKey();
                    try {
                        x509Certificate.verify(publicKey);
                        return x509Certificate2;
                    }
                    catch (final Exception ex) {}
                }
                return null;
            }
            return null;
        }
        
        @Override
        public int hashCode() {
            return this.subjectToCaCerts.hashCode();
        }
    }
}
