package okhttp3;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.internal.Util;
import okhttp3.internal.tls.CertificateChainCleaner;
import okio.ByteString;
/* loaded from: classes.dex */
public final class CertificatePinner {
    public static final CertificatePinner DEFAULT = new Builder().build();
    private final CertificateChainCleaner certificateChainCleaner;
    private final Set<Pin> pins;

    /* loaded from: classes.dex */
    public static final class Builder {
        private final List<Pin> pins = new ArrayList();

        public Builder add(String str, String... strArr) {
            if (str != null) {
                for (String str2 : strArr) {
                    this.pins.add(new Pin(str, str2));
                }
                return this;
            }
            throw new NullPointerException("pattern == null");
        }

        public CertificatePinner build() {
            return new CertificatePinner(new LinkedHashSet(this.pins), null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Pin {
        private static final String WILDCARD = "*.";
        final String canonicalHostname;
        final ByteString hash;
        final String hashAlgorithm;
        final String pattern;

        Pin(String str, String str2) {
            this.pattern = str;
            this.canonicalHostname = !str.startsWith(WILDCARD) ? HttpUrl.parse("http://" + str).host() : HttpUrl.parse("http://" + str.substring(WILDCARD.length())).host();
            if (str2.startsWith("sha1/")) {
                this.hashAlgorithm = "sha1/";
                this.hash = ByteString.decodeBase64(str2.substring("sha1/".length()));
            } else if (!str2.startsWith("sha256/")) {
                throw new IllegalArgumentException("pins must start with 'sha256/' or 'sha1/': " + str2);
            } else {
                this.hashAlgorithm = "sha256/";
                this.hash = ByteString.decodeBase64(str2.substring("sha256/".length()));
            }
            if (this.hash == null) {
                throw new IllegalArgumentException("pins must be base64: " + str2);
            }
        }

        public boolean equals(Object obj) {
            return (obj instanceof Pin) && this.pattern.equals(((Pin) obj).pattern) && this.hashAlgorithm.equals(((Pin) obj).hashAlgorithm) && this.hash.equals(((Pin) obj).hash);
        }

        public int hashCode() {
            return ((((this.pattern.hashCode() + 527) * 31) + this.hashAlgorithm.hashCode()) * 31) + this.hash.hashCode();
        }

        boolean matches(String str) {
            return !this.pattern.startsWith(WILDCARD) ? str.equals(this.canonicalHostname) : str.regionMatches(false, str.indexOf(46) + 1, this.canonicalHostname, 0, this.canonicalHostname.length());
        }

        public String toString() {
            return this.hashAlgorithm + this.hash.base64();
        }
    }

    CertificatePinner(Set<Pin> set, CertificateChainCleaner certificateChainCleaner) {
        this.pins = set;
        this.certificateChainCleaner = certificateChainCleaner;
    }

    public static String pin(Certificate certificate) {
        if (certificate instanceof X509Certificate) {
            return "sha256/" + sha256((X509Certificate) certificate).base64();
        }
        throw new IllegalArgumentException("Certificate pinning requires X509 certificates");
    }

    static ByteString sha1(X509Certificate x509Certificate) {
        return ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha1();
    }

    static ByteString sha256(X509Certificate x509Certificate) {
        return ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha256();
    }

    public void check(String str, List<Certificate> list) throws SSLPeerUnverifiedException {
        List<Pin> findMatchingPins = findMatchingPins(str);
        if (findMatchingPins.isEmpty()) {
            return;
        }
        if (this.certificateChainCleaner != null) {
            list = this.certificateChainCleaner.clean(list, str);
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            X509Certificate x509Certificate = (X509Certificate) list.get(i);
            int size2 = findMatchingPins.size();
            ByteString byteString = null;
            ByteString byteString2 = null;
            for (int i2 = 0; i2 < size2; i2++) {
                Pin pin = findMatchingPins.get(i2);
                if (pin.hashAlgorithm.equals("sha256/")) {
                    if (byteString == null) {
                        byteString = sha256(x509Certificate);
                    }
                    if (pin.hash.equals(byteString)) {
                        return;
                    }
                } else if (!pin.hashAlgorithm.equals("sha1/")) {
                    throw new AssertionError();
                } else {
                    if (byteString2 == null) {
                        byteString2 = sha1(x509Certificate);
                    }
                    if (pin.hash.equals(byteString2)) {
                        return;
                    }
                }
            }
        }
        StringBuilder append = new StringBuilder().append("Certificate pinning failure!").append("\n  Peer certificate chain:");
        int size3 = list.size();
        for (int i3 = 0; i3 < size3; i3++) {
            X509Certificate x509Certificate2 = (X509Certificate) list.get(i3);
            append.append("\n    ").append(pin(x509Certificate2)).append(": ").append(x509Certificate2.getSubjectDN().getName());
        }
        append.append("\n  Pinned certificates for ").append(str).append(":");
        int size4 = findMatchingPins.size();
        for (int i4 = 0; i4 < size4; i4++) {
            append.append("\n    ").append(findMatchingPins.get(i4));
        }
        throw new SSLPeerUnverifiedException(append.toString());
    }

    public void check(String str, Certificate... certificateArr) throws SSLPeerUnverifiedException {
        check(str, Arrays.asList(certificateArr));
    }

    public boolean equals(Object obj) {
        if (obj != this) {
            return (obj instanceof CertificatePinner) && Util.equal(this.certificateChainCleaner, ((CertificatePinner) obj).certificateChainCleaner) && this.pins.equals(((CertificatePinner) obj).pins);
        }
        return true;
    }

    List<Pin> findMatchingPins(String str) {
        List emptyList = Collections.emptyList();
        Iterator<Pin> it = this.pins.iterator();
        while (true) {
            List list = emptyList;
            if (!it.hasNext()) {
                return list;
            }
            Pin next = it.next();
            if (next.matches(str)) {
                if (list.isEmpty()) {
                    list = new ArrayList();
                }
                list.add(next);
            }
            emptyList = list;
        }
    }

    public int hashCode() {
        return ((this.certificateChainCleaner == null ? 0 : this.certificateChainCleaner.hashCode()) * 31) + this.pins.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CertificatePinner withCertificateChainCleaner(CertificateChainCleaner certificateChainCleaner) {
        return !Util.equal(this.certificateChainCleaner, certificateChainCleaner) ? new CertificatePinner(this.pins, certificateChainCleaner) : this;
    }
}
