// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLSocket;
import okhttp3.internal.Util;

public final class ConnectionSpec
{
    private static final CipherSuite[] APPROVED_CIPHER_SUITES;
    public static final ConnectionSpec CLEARTEXT;
    public static final ConnectionSpec COMPATIBLE_TLS;
    public static final ConnectionSpec MODERN_TLS;
    final String[] cipherSuites;
    final boolean supportsTlsExtensions;
    final boolean tls;
    final String[] tlsVersions;
    
    static {
        APPROVED_CIPHER_SUITES = new CipherSuite[] { CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA };
        MODERN_TLS = new Builder(true).cipherSuites(ConnectionSpec.APPROVED_CIPHER_SUITES).tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0).supportsTlsExtensions(true).build();
        COMPATIBLE_TLS = new Builder(ConnectionSpec.MODERN_TLS).tlsVersions(TlsVersion.TLS_1_0).supportsTlsExtensions(true).build();
        CLEARTEXT = new Builder(false).build();
    }
    
    ConnectionSpec(final Builder builder) {
        this.tls = builder.tls;
        this.cipherSuites = builder.cipherSuites;
        this.tlsVersions = builder.tlsVersions;
        this.supportsTlsExtensions = builder.supportsTlsExtensions;
    }
    
    private static boolean nonEmptyIntersection(final String[] array, final String[] array2) {
        if (array != null && array2 != null && array.length != 0 && array2.length != 0) {
            for (int length = array.length, i = 0; i < length; ++i) {
                if (Util.indexOf(array2, array[i]) != -1) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    private ConnectionSpec supportedSpec(final SSLSocket sslSocket, final boolean b) {
        String[] enabledCipherSuites;
        if (this.cipherSuites == null) {
            enabledCipherSuites = sslSocket.getEnabledCipherSuites();
        }
        else {
            enabledCipherSuites = Util.intersect(String.class, this.cipherSuites, sslSocket.getEnabledCipherSuites());
        }
        String[] enabledProtocols;
        if (this.tlsVersions == null) {
            enabledProtocols = sslSocket.getEnabledProtocols();
        }
        else {
            enabledProtocols = Util.intersect(String.class, this.tlsVersions, sslSocket.getEnabledProtocols());
        }
        String[] concat;
        if (!b) {
            concat = enabledCipherSuites;
        }
        else {
            concat = enabledCipherSuites;
            if (Util.indexOf(sslSocket.getSupportedCipherSuites(), "TLS_FALLBACK_SCSV") != -1) {
                concat = Util.concat(enabledCipherSuites, "TLS_FALLBACK_SCSV");
            }
        }
        return new Builder(this).cipherSuites(concat).tlsVersions(enabledProtocols).build();
    }
    
    void apply(final SSLSocket sslSocket, final boolean b) {
        final ConnectionSpec supportedSpec = this.supportedSpec(sslSocket, b);
        if (supportedSpec.tlsVersions != null) {
            sslSocket.setEnabledProtocols(supportedSpec.tlsVersions);
        }
        if (supportedSpec.cipherSuites != null) {
            sslSocket.setEnabledCipherSuites(supportedSpec.cipherSuites);
        }
    }
    
    public List<CipherSuite> cipherSuites() {
        if (this.cipherSuites != null) {
            final ArrayList list = new ArrayList(this.cipherSuites.length);
            final String[] cipherSuites = this.cipherSuites;
            for (int length = cipherSuites.length, i = 0; i < length; ++i) {
                list.add(CipherSuite.forJavaName(cipherSuites[i]));
            }
            return (List<CipherSuite>)Collections.unmodifiableList((List<?>)list);
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ConnectionSpec)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        final ConnectionSpec connectionSpec = (ConnectionSpec)o;
        if (this.tls == connectionSpec.tls) {
            if (this.tls) {
                if (!Arrays.equals(this.cipherSuites, connectionSpec.cipherSuites)) {
                    return false;
                }
                if (!Arrays.equals(this.tlsVersions, connectionSpec.tlsVersions)) {
                    return false;
                }
                if (this.supportsTlsExtensions != connectionSpec.supportsTlsExtensions) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        int n2;
        if (!this.tls) {
            n2 = 17;
        }
        else {
            final int hashCode = Arrays.hashCode(this.cipherSuites);
            final int hashCode2 = Arrays.hashCode(this.tlsVersions);
            if (!this.supportsTlsExtensions) {
                n = 1;
            }
            n2 = n + ((hashCode + 527) * 31 + hashCode2) * 31;
        }
        return n2;
    }
    
    public boolean isCompatible(final SSLSocket sslSocket) {
        return this.tls && (this.tlsVersions == null || nonEmptyIntersection(this.tlsVersions, sslSocket.getEnabledProtocols())) && (this.cipherSuites == null || nonEmptyIntersection(this.cipherSuites, sslSocket.getEnabledCipherSuites()));
    }
    
    public boolean isTls() {
        return this.tls;
    }
    
    public boolean supportsTlsExtensions() {
        return this.supportsTlsExtensions;
    }
    
    public List<TlsVersion> tlsVersions() {
        if (this.tlsVersions != null) {
            final ArrayList list = new ArrayList(this.tlsVersions.length);
            final String[] tlsVersions = this.tlsVersions;
            for (int length = tlsVersions.length, i = 0; i < length; ++i) {
                list.add(TlsVersion.forJavaName(tlsVersions[i]));
            }
            return (List<TlsVersion>)Collections.unmodifiableList((List<?>)list);
        }
        return null;
    }
    
    @Override
    public String toString() {
        if (this.tls) {
            String string;
            if (this.cipherSuites == null) {
                string = "[all enabled]";
            }
            else {
                string = this.cipherSuites().toString();
            }
            String string2;
            if (this.tlsVersions == null) {
                string2 = "[all enabled]";
            }
            else {
                string2 = this.tlsVersions().toString();
            }
            return "ConnectionSpec(cipherSuites=" + string + ", tlsVersions=" + string2 + ", supportsTlsExtensions=" + this.supportsTlsExtensions + ")";
        }
        return "ConnectionSpec()";
    }
    
    public static final class Builder
    {
        String[] cipherSuites;
        boolean supportsTlsExtensions;
        boolean tls;
        String[] tlsVersions;
        
        public Builder(final ConnectionSpec connectionSpec) {
            this.tls = connectionSpec.tls;
            this.cipherSuites = connectionSpec.cipherSuites;
            this.tlsVersions = connectionSpec.tlsVersions;
            this.supportsTlsExtensions = connectionSpec.supportsTlsExtensions;
        }
        
        Builder(final boolean tls) {
            this.tls = tls;
        }
        
        public Builder allEnabledCipherSuites() {
            if (this.tls) {
                this.cipherSuites = null;
                return this;
            }
            throw new IllegalStateException("no cipher suites for cleartext connections");
        }
        
        public Builder allEnabledTlsVersions() {
            if (this.tls) {
                this.tlsVersions = null;
                return this;
            }
            throw new IllegalStateException("no TLS versions for cleartext connections");
        }
        
        public ConnectionSpec build() {
            return new ConnectionSpec(this);
        }
        
        public Builder cipherSuites(final String... array) {
            if (!this.tls) {
                throw new IllegalStateException("no cipher suites for cleartext connections");
            }
            if (array.length != 0) {
                this.cipherSuites = array.clone();
                return this;
            }
            throw new IllegalArgumentException("At least one cipher suite is required");
        }
        
        public Builder cipherSuites(final CipherSuite... array) {
            int i = 0;
            if (this.tls) {
                final String[] array2 = new String[array.length];
                while (i < array.length) {
                    array2[i] = array[i].javaName;
                    ++i;
                }
                return this.cipherSuites(array2);
            }
            throw new IllegalStateException("no cipher suites for cleartext connections");
        }
        
        public Builder supportsTlsExtensions(final boolean supportsTlsExtensions) {
            if (this.tls) {
                this.supportsTlsExtensions = supportsTlsExtensions;
                return this;
            }
            throw new IllegalStateException("no TLS extensions for cleartext connections");
        }
        
        public Builder tlsVersions(final String... array) {
            if (!this.tls) {
                throw new IllegalStateException("no TLS versions for cleartext connections");
            }
            if (array.length != 0) {
                this.tlsVersions = array.clone();
                return this;
            }
            throw new IllegalArgumentException("At least one TLS version is required");
        }
        
        public Builder tlsVersions(final TlsVersion... array) {
            int i = 0;
            if (this.tls) {
                final String[] array2 = new String[array.length];
                while (i < array.length) {
                    array2[i] = array[i].javaName;
                    ++i;
                }
                return this.tlsVersions(array2);
            }
            throw new IllegalStateException("no TLS versions for cleartext connections");
        }
    }
}
