// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.tls;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import okhttp3.internal.Util;
import java.util.Locale;
import java.util.Iterator;
import java.security.cert.CertificateParsingException;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;

public final class OkHostnameVerifier implements HostnameVerifier
{
    private static final int ALT_DNS_NAME = 2;
    private static final int ALT_IPA_NAME = 7;
    public static final OkHostnameVerifier INSTANCE;
    
    static {
        INSTANCE = new OkHostnameVerifier();
    }
    
    private OkHostnameVerifier() {
    }
    
    public static List<String> allSubjectAltNames(final X509Certificate x509Certificate) {
        final List<String> subjectAltNames = getSubjectAltNames(x509Certificate, 7);
        final List<String> subjectAltNames2 = getSubjectAltNames(x509Certificate, 2);
        final ArrayList list = new ArrayList(subjectAltNames.size() + subjectAltNames2.size());
        list.addAll((Collection)subjectAltNames);
        list.addAll((Collection)subjectAltNames2);
        return (List<String>)list;
    }
    
    private static List<String> getSubjectAltNames(final X509Certificate x509Certificate, final int n) {
        final ArrayList list = new ArrayList();
        try {
            final Collection<List<?>> subjectAlternativeNames = x509Certificate.getSubjectAlternativeNames();
            if (subjectAlternativeNames != null) {
                for (final List list2 : subjectAlternativeNames) {
                    if (list2 != null && list2.size() >= 2) {
                        final Integer n2 = (Integer)list2.get(0);
                        if (n2 == null || n2 != n) {
                            continue;
                        }
                        final String s = (String)list2.get(1);
                        if (s == null) {
                            continue;
                        }
                        list.add(s);
                    }
                }
                return list;
            }
            return Collections.emptyList();
        }
        catch (final CertificateParsingException ex) {
            return Collections.emptyList();
        }
    }
    
    private boolean verifyHostname(String string, String s) {
        if (string == null || string.length() == 0 || string.startsWith(".") || string.endsWith("..")) {
            return false;
        }
        if (s == null || s.length() == 0 || s.startsWith(".") || s.endsWith("..")) {
            return false;
        }
        if (!string.endsWith(".")) {
            string += '.';
        }
        if (!s.endsWith(".")) {
            s += '.';
        }
        s = s.toLowerCase(Locale.US);
        if (!s.contains("*")) {
            return string.equals(s);
        }
        if (!s.startsWith("*.") || s.indexOf(42, 1) != -1) {
            return false;
        }
        if (string.length() < s.length()) {
            return false;
        }
        if ("*.".equals(s)) {
            return false;
        }
        s = s.substring(1);
        if (string.endsWith(s)) {
            final int n = string.length() - s.length();
            return n <= 0 || string.lastIndexOf(46, n - 1) == -1;
        }
        return false;
    }
    
    private boolean verifyHostname(String lowerCase, final X509Certificate x509Certificate) {
        lowerCase = lowerCase.toLowerCase(Locale.US);
        final List<String> subjectAltNames = getSubjectAltNames(x509Certificate, 2);
        final int size = subjectAltNames.size();
        int i = 0;
        int n = 0;
        while (i < size) {
            if (this.verifyHostname(lowerCase, (String)subjectAltNames.get(i))) {
                return true;
            }
            ++i;
            n = 1;
        }
        if (n == 0) {
            final String mostSpecific = new DistinguishedNameParser(x509Certificate.getSubjectX500Principal()).findMostSpecific("cn");
            if (mostSpecific != null) {
                return this.verifyHostname(lowerCase, mostSpecific);
            }
        }
        return false;
    }
    
    private boolean verifyIpAddress(final String s, final X509Certificate x509Certificate) {
        final List<String> subjectAltNames = getSubjectAltNames(x509Certificate, 7);
        for (int size = subjectAltNames.size(), i = 0; i < size; ++i) {
            if (s.equalsIgnoreCase((String)subjectAltNames.get(i))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean verify(final String s, final X509Certificate x509Certificate) {
        boolean b;
        if (!Util.verifyAsIpAddress(s)) {
            b = this.verifyHostname(s, x509Certificate);
        }
        else {
            b = this.verifyIpAddress(s, x509Certificate);
        }
        return b;
    }
    
    @Override
    public boolean verify(final String s, final SSLSession sslSession) {
        try {
            return this.verify(s, (X509Certificate)sslSession.getPeerCertificates()[0]);
        }
        catch (final SSLException ex) {
            return false;
        }
    }
}
