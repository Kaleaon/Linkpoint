// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import okhttp3.internal.Util;
import java.util.Arrays;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.util.List;
import okio.ByteString;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.util.Set;
import okhttp3.internal.tls.CertificateChainCleaner;

public final class CertificatePinner
{
    public static final CertificatePinner DEFAULT;
    private final CertificateChainCleaner certificateChainCleaner;
    private final Set<Pin> pins;
    
    static {
        DEFAULT = new Builder().build();
    }
    
    CertificatePinner(final Set<Pin> pins, final CertificateChainCleaner certificateChainCleaner) {
        this.pins = pins;
        this.certificateChainCleaner = certificateChainCleaner;
    }
    
    public static String pin(final Certificate certificate) {
        if (certificate instanceof X509Certificate) {
            return "sha256/" + sha256((X509Certificate)certificate).base64();
        }
        throw new IllegalArgumentException("Certificate pinning requires X509 certificates");
    }
    
    static ByteString sha1(final X509Certificate x509Certificate) {
        return ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha1();
    }
    
    static ByteString sha256(final X509Certificate x509Certificate) {
        return ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha256();
    }
    
    public void check(final String str, final List<Certificate> list) throws SSLPeerUnverifiedException {
        final List<Pin> matchingPins = this.findMatchingPins(str);
        if (!matchingPins.isEmpty()) {
            List<Certificate> clean;
            if (this.certificateChainCleaner == null) {
                clean = list;
            }
            else {
                clean = this.certificateChainCleaner.clean(list, str);
            }
            for (int size = clean.size(), i = 0; i < size; ++i) {
                final X509Certificate x509Certificate = clean.get(i);
                final int size2 = matchingPins.size();
                int j = 0;
                Object sha256 = null;
                ByteString sha257 = null;
                while (j < size2) {
                    final Pin pin = matchingPins.get(j);
                    ByteString byteString;
                    if (!pin.hashAlgorithm.equals("sha256/")) {
                        if (!pin.hashAlgorithm.equals("sha1/")) {
                            throw new AssertionError();
                        }
                        if (sha257 == null) {
                            sha257 = sha1(x509Certificate);
                        }
                        byteString = sha257;
                        if (pin.hash.equals(sha257)) {
                            return;
                        }
                    }
                    else {
                        if (sha256 == null) {
                            sha256 = sha256(x509Certificate);
                        }
                        if (pin.hash.equals(sha256)) {
                            return;
                        }
                        byteString = sha257;
                    }
                    ++j;
                    sha257 = byteString;
                }
            }
            final StringBuilder append = new StringBuilder().append("Certificate pinning failure!").append("\n  Peer certificate chain:");
            for (int size3 = clean.size(), k = 0; k < size3; ++k) {
                final X509Certificate x509Certificate2 = clean.get(k);
                append.append("\n    ").append(pin(x509Certificate2)).append(": ").append(x509Certificate2.getSubjectDN().getName());
            }
            append.append("\n  Pinned certificates for ").append(str).append(":");
            for (int size4 = matchingPins.size(), l = 0; l < size4; ++l) {
                append.append("\n    ").append(matchingPins.get(l));
            }
            throw new SSLPeerUnverifiedException(append.toString());
        }
    }
    
    public void check(final String s, final Certificate... a) throws SSLPeerUnverifiedException {
        this.check(s, Arrays.asList(a));
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof CertificatePinner && Util.equal(this.certificateChainCleaner, ((CertificatePinner)o).certificateChainCleaner) && this.pins.equals(((CertificatePinner)o).pins));
    }
    
    List<Pin> findMatchingPins(final String s) {
        List<Object> emptyList = Collections.emptyList();
        for (final Pin pin : this.pins) {
            if (!pin.matches(s)) {
                continue;
            }
            if (emptyList.isEmpty()) {
                emptyList = new ArrayList<Object>();
            }
            emptyList.add(pin);
        }
        return (List<Pin>)emptyList;
    }
    
    @Override
    public int hashCode() {
        int hashCode;
        if (this.certificateChainCleaner == null) {
            hashCode = 0;
        }
        else {
            hashCode = this.certificateChainCleaner.hashCode();
        }
        return hashCode * 31 + this.pins.hashCode();
    }
    
    CertificatePinner withCertificateChainCleaner(final CertificateChainCleaner certificateChainCleaner) {
        CertificatePinner certificatePinner = this;
        if (!Util.equal(this.certificateChainCleaner, certificateChainCleaner)) {
            certificatePinner = new CertificatePinner(this.pins, certificateChainCleaner);
        }
        return certificatePinner;
    }
    
    public static final class Builder
    {
        private final List<Pin> pins;
        
        public Builder() {
            this.pins = new ArrayList<Pin>();
        }
        
        public Builder add(final String s, final String... array) {
            if (s != null) {
                for (int length = array.length, i = 0; i < length; ++i) {
                    this.pins.add(new Pin(s, array[i]));
                }
                return this;
            }
            throw new NullPointerException("pattern == null");
        }
        
        public CertificatePinner build() {
            return new CertificatePinner(new LinkedHashSet<Pin>((Collection<? extends Pin>)this.pins), null);
        }
    }
    
    static final class Pin
    {
        private static final String WILDCARD = "*.";
        final String canonicalHostname;
        final ByteString hash;
        final String hashAlgorithm;
        final String pattern;
        
        Pin(String canonicalHostname, final String s) {
            this.pattern = canonicalHostname;
            if (!canonicalHostname.startsWith("*.")) {
                canonicalHostname = HttpUrl.parse("http://" + canonicalHostname).host();
            }
            else {
                canonicalHostname = HttpUrl.parse("http://" + canonicalHostname.substring("*.".length())).host();
            }
            this.canonicalHostname = canonicalHostname;
            if (!s.startsWith("sha1/")) {
                if (!s.startsWith("sha256/")) {
                    throw new IllegalArgumentException("pins must start with 'sha256/' or 'sha1/': " + s);
                }
                this.hashAlgorithm = "sha256/";
                this.hash = ByteString.decodeBase64(s.substring("sha256/".length()));
            }
            else {
                this.hashAlgorithm = "sha1/";
                this.hash = ByteString.decodeBase64(s.substring("sha1/".length()));
            }
            if (this.hash != null) {
                return;
            }
            throw new IllegalArgumentException("pins must be base64: " + s);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Pin && this.pattern.equals(((Pin)o).pattern) && this.hashAlgorithm.equals(((Pin)o).hashAlgorithm) && this.hash.equals(((Pin)o).hash);
        }
        
        @Override
        public int hashCode() {
            return ((this.pattern.hashCode() + 527) * 31 + this.hashAlgorithm.hashCode()) * 31 + this.hash.hashCode();
        }
        
        boolean matches(final String s) {
            if (!this.pattern.startsWith("*.")) {
                return s.equals(this.canonicalHostname);
            }
            return s.regionMatches(false, s.indexOf(46) + 1, this.canonicalHostname, 0, this.canonicalHostname.length());
        }
        
        @Override
        public String toString() {
            return this.hashAlgorithm + this.hash.base64();
        }
    }
}
