// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.security.cert.X509Certificate;
import java.security.Principal;
import okhttp3.internal.Util;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.util.Collections;
import javax.net.ssl.SSLSession;
import java.security.cert.Certificate;
import java.util.List;

public final class Handshake
{
    private final CipherSuite cipherSuite;
    private final List<Certificate> localCertificates;
    private final List<Certificate> peerCertificates;
    private final TlsVersion tlsVersion;
    
    private Handshake(final TlsVersion tlsVersion, final CipherSuite cipherSuite, final List<Certificate> peerCertificates, final List<Certificate> localCertificates) {
        this.tlsVersion = tlsVersion;
        this.cipherSuite = cipherSuite;
        this.peerCertificates = peerCertificates;
        this.localCertificates = localCertificates;
    }
    
    public static Handshake get(SSLSession sslSession) {
        Object o = null;
        final String cipherSuite = sslSession.getCipherSuite();
        Label_0080: {
            if (cipherSuite == null) {
                break Label_0080;
            }
            final CipherSuite forJavaName = CipherSuite.forJavaName(cipherSuite);
            final String protocol = sslSession.getProtocol();
            Label_0090: {
                if (protocol == null) {
                    break Label_0090;
                }
                TlsVersion forJavaName2;
                Label_0052_Outer:Label_0067_Outer:
                while (true) {
                    forJavaName2 = TlsVersion.forJavaName(protocol);
                    while (true) {
                    Label_0112:
                        while (true) {
                        Label_0104:
                            while (true) {
                                try {
                                    o = sslSession.getPeerCertificates();
                                    if (o != null) {
                                        break Label_0104;
                                    }
                                    o = Collections.emptyList();
                                    sslSession = (SSLSession)(Object)sslSession.getLocalCertificates();
                                    if (sslSession == null) {
                                        sslSession = (SSLSession)Collections.emptyList();
                                        return new Handshake(forJavaName2, forJavaName, (List<Certificate>)o, (List<Certificate>)sslSession);
                                    }
                                    break Label_0112;
                                    throw new IllegalStateException("cipherSuite == null");
                                    throw new IllegalStateException("tlsVersion == null");
                                }
                                catch (final SSLPeerUnverifiedException ex) {
                                    continue Label_0052_Outer;
                                }
                                break;
                            }
                            o = Util.immutableList((Certificate[])o);
                            continue Label_0067_Outer;
                        }
                        sslSession = (SSLSession)Util.immutableList((Object[])(Object)sslSession);
                        continue;
                    }
                }
            }
        }
    }
    
    public static Handshake get(final TlsVersion tlsVersion, final CipherSuite cipherSuite, final List<Certificate> list, final List<Certificate> list2) {
        if (cipherSuite != null) {
            return new Handshake(tlsVersion, cipherSuite, Util.immutableList(list), Util.immutableList(list2));
        }
        throw new NullPointerException("cipherSuite == null");
    }
    
    public CipherSuite cipherSuite() {
        return this.cipherSuite;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (o instanceof Handshake) {
            final Handshake handshake = (Handshake)o;
            boolean b2;
            if (!Util.equal(this.cipherSuite, handshake.cipherSuite)) {
                b2 = b;
            }
            else {
                b2 = b;
                if (this.cipherSuite.equals(handshake.cipherSuite)) {
                    b2 = b;
                    if (this.peerCertificates.equals(handshake.peerCertificates)) {
                        b2 = b;
                        if (this.localCertificates.equals(handshake.localCertificates)) {
                            b2 = true;
                        }
                    }
                }
            }
            return b2;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode;
        if (this.tlsVersion == null) {
            hashCode = 0;
        }
        else {
            hashCode = this.tlsVersion.hashCode();
        }
        return (((hashCode + 527) * 31 + this.cipherSuite.hashCode()) * 31 + this.peerCertificates.hashCode()) * 31 + this.localCertificates.hashCode();
    }
    
    public List<Certificate> localCertificates() {
        return this.localCertificates;
    }
    
    public Principal localPrincipal() {
        Principal subjectX500Principal;
        if (this.localCertificates.isEmpty()) {
            subjectX500Principal = null;
        }
        else {
            subjectX500Principal = this.localCertificates.get(0).getSubjectX500Principal();
        }
        return subjectX500Principal;
    }
    
    public List<Certificate> peerCertificates() {
        return this.peerCertificates;
    }
    
    public Principal peerPrincipal() {
        Principal subjectX500Principal;
        if (this.peerCertificates.isEmpty()) {
            subjectX500Principal = null;
        }
        else {
            subjectX500Principal = this.peerCertificates.get(0).getSubjectX500Principal();
        }
        return subjectX500Principal;
    }
    
    public TlsVersion tlsVersion() {
        return this.tlsVersion;
    }
}
