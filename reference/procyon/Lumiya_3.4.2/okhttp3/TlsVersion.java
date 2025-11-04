// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

public enum TlsVersion
{
    SSL_3_0("SSLv3"), 
    TLS_1_0("TLSv1"), 
    TLS_1_1("TLSv1.1"), 
    TLS_1_2("TLSv1.2"), 
    TLS_1_3("TLSv1.3");
    
    final String javaName;
    
    private TlsVersion(final String javaName) {
        this.javaName = javaName;
    }
    
    public static TlsVersion forJavaName(final String str) {
        switch (str) {
            default: {
                throw new IllegalArgumentException("Unexpected TLS version: " + str);
            }
            case "TLSv1.3": {
                return TlsVersion.TLS_1_3;
            }
            case "TLSv1.2": {
                return TlsVersion.TLS_1_2;
            }
            case "TLSv1.1": {
                return TlsVersion.TLS_1_1;
            }
            case "TLSv1": {
                return TlsVersion.TLS_1_0;
            }
            case "SSLv3": {
                return TlsVersion.SSL_3_0;
            }
        }
    }
    
    public String javaName() {
        return this.javaName;
    }
}
