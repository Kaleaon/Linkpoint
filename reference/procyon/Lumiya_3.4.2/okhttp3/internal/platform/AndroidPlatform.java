// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.platform;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
import java.lang.reflect.Method;
import javax.net.ssl.SSLSocketFactory;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import okhttp3.internal.Util;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import okhttp3.Protocol;
import java.util.List;
import javax.net.ssl.SSLSocket;
import java.security.cert.X509Certificate;
import okhttp3.internal.tls.CertificateChainCleaner;
import javax.net.ssl.X509TrustManager;
import java.net.Socket;

class AndroidPlatform extends Platform
{
    private static final int MAX_LOG_LENGTH = 4000;
    private final CloseGuard closeGuard;
    private final OptionalMethod<Socket> getAlpnSelectedProtocol;
    private final OptionalMethod<Socket> setAlpnProtocols;
    private final OptionalMethod<Socket> setHostname;
    private final OptionalMethod<Socket> setUseSessionTickets;
    private final Class<?> sslParametersClass;
    
    public AndroidPlatform(final Class<?> sslParametersClass, final OptionalMethod<Socket> setUseSessionTickets, final OptionalMethod<Socket> setHostname, final OptionalMethod<Socket> getAlpnSelectedProtocol, final OptionalMethod<Socket> setAlpnProtocols) {
        this.closeGuard = CloseGuard.get();
        this.sslParametersClass = sslParametersClass;
        this.setUseSessionTickets = setUseSessionTickets;
        this.setHostname = setHostname;
        this.getAlpnSelectedProtocol = getAlpnSelectedProtocol;
        this.setAlpnProtocols = setAlpnProtocols;
    }
    
    public static Platform buildIfSupported() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: invokestatic    java/lang/Class.forName:(Ljava/lang/String;)Ljava/lang/Class;
        //     5: astore_0       
        //     6: new             Lokhttp3/internal/platform/OptionalMethod;
        //     9: astore_1       
        //    10: aload_1        
        //    11: aconst_null    
        //    12: ldc             "setUseSessionTickets"
        //    14: iconst_1       
        //    15: anewarray       Ljava/lang/Class;
        //    18: dup            
        //    19: iconst_0       
        //    20: getstatic       java/lang/Boolean.TYPE:Ljava/lang/Class;
        //    23: aastore        
        //    24: invokespecial   okhttp3/internal/platform/OptionalMethod.<init>:(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)V
        //    27: new             Lokhttp3/internal/platform/OptionalMethod;
        //    30: astore_2       
        //    31: aload_2        
        //    32: aconst_null    
        //    33: ldc             "setHostname"
        //    35: iconst_1       
        //    36: anewarray       Ljava/lang/Class;
        //    39: dup            
        //    40: iconst_0       
        //    41: ldc             Ljava/lang/String;.class
        //    43: aastore        
        //    44: invokespecial   okhttp3/internal/platform/OptionalMethod.<init>:(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)V
        //    47: ldc             "android.net.Network"
        //    49: invokestatic    java/lang/Class.forName:(Ljava/lang/String;)Ljava/lang/Class;
        //    52: pop            
        //    53: new             Lokhttp3/internal/platform/OptionalMethod;
        //    56: astore_3       
        //    57: aload_3        
        //    58: ldc             [B.class
        //    60: ldc             "getAlpnSelectedProtocol"
        //    62: iconst_0       
        //    63: anewarray       Ljava/lang/Class;
        //    66: invokespecial   okhttp3/internal/platform/OptionalMethod.<init>:(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)V
        //    69: new             Lokhttp3/internal/platform/OptionalMethod;
        //    72: astore          4
        //    74: aload           4
        //    76: aconst_null    
        //    77: ldc             "setAlpnProtocols"
        //    79: iconst_1       
        //    80: anewarray       Ljava/lang/Class;
        //    83: dup            
        //    84: iconst_0       
        //    85: ldc             [B.class
        //    87: aastore        
        //    88: invokespecial   okhttp3/internal/platform/OptionalMethod.<init>:(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)V
        //    91: aload_3        
        //    92: astore          5
        //    94: aload           4
        //    96: astore_3       
        //    97: new             Lokhttp3/internal/platform/AndroidPlatform;
        //   100: dup            
        //   101: aload_0        
        //   102: aload_1        
        //   103: aload_2        
        //   104: aload           5
        //   106: aload_3        
        //   107: invokespecial   okhttp3/internal/platform/AndroidPlatform.<init>:(Ljava/lang/Class;Lokhttp3/internal/platform/OptionalMethod;Lokhttp3/internal/platform/OptionalMethod;Lokhttp3/internal/platform/OptionalMethod;Lokhttp3/internal/platform/OptionalMethod;)V
        //   110: areturn        
        //   111: astore_3       
        //   112: ldc             "org.apache.harmony.xnet.provider.jsse.SSLParametersImpl"
        //   114: invokestatic    java/lang/Class.forName:(Ljava/lang/String;)Ljava/lang/Class;
        //   117: astore_0       
        //   118: goto            6
        //   121: astore_3       
        //   122: aconst_null    
        //   123: astore_3       
        //   124: aconst_null    
        //   125: astore          4
        //   127: aload_3        
        //   128: astore          5
        //   130: aload           4
        //   132: astore_3       
        //   133: goto            97
        //   136: astore_3       
        //   137: aconst_null    
        //   138: areturn        
        //   139: astore          5
        //   141: goto            124
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                              
        //  -----  -----  -----  -----  ----------------------------------
        //  0      6      111    121    Ljava/lang/ClassNotFoundException;
        //  0      6      136    139    Ljava/lang/ClassNotFoundException;
        //  6      47     136    139    Ljava/lang/ClassNotFoundException;
        //  47     69     121    124    Ljava/lang/ClassNotFoundException;
        //  47     69     136    139    Ljava/lang/ClassNotFoundException;
        //  69     91     139    144    Ljava/lang/ClassNotFoundException;
        //  69     91     136    139    Ljava/lang/ClassNotFoundException;
        //  97     111    136    139    Ljava/lang/ClassNotFoundException;
        //  112    118    136    139    Ljava/lang/ClassNotFoundException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 83 out of bounds for length 83
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public CertificateChainCleaner buildCertificateChainCleaner(final X509TrustManager x509TrustManager) {
        try {
            final Class<?> forName = Class.forName("android.net.http.X509TrustManagerExtensions");
            return new AndroidCertificateChainCleaner(forName.getConstructor(X509TrustManager.class).newInstance(x509TrustManager), forName.getMethod("checkServerTrusted", X509Certificate[].class, String.class, String.class));
        }
        catch (final Exception ex) {
            return super.buildCertificateChainCleaner(x509TrustManager);
        }
    }
    
    @Override
    public void configureTlsExtensions(final SSLSocket sslSocket, final String s, final List<Protocol> list) {
        if (s != null) {
            this.setUseSessionTickets.invokeOptionalWithoutCheckedException(sslSocket, true);
            this.setHostname.invokeOptionalWithoutCheckedException(sslSocket, s);
        }
        if (this.setAlpnProtocols != null && this.setAlpnProtocols.isSupported(sslSocket)) {
            this.setAlpnProtocols.invokeWithoutCheckedException(sslSocket, Platform.concatLengthPrefixed(list));
        }
    }
    
    @Override
    public void connectSocket(final Socket socket, final InetSocketAddress endpoint, final int timeout) throws IOException {
        try {
            socket.connect(endpoint, timeout);
        }
        catch (final AssertionError cause) {
            if (!Util.isAndroidGetsocknameError(cause)) {
                throw cause;
            }
            throw new IOException(cause);
        }
        catch (final SecurityException cause2) {
            final IOException ex = new IOException("Exception in connect");
            ex.initCause(cause2);
            throw ex;
        }
    }
    
    @Override
    public String getSelectedProtocol(final SSLSocket sslSocket) {
        if (this.getAlpnSelectedProtocol == null) {
            return null;
        }
        if (this.getAlpnSelectedProtocol.isSupported(sslSocket)) {
            final byte[] bytes = (byte[])this.getAlpnSelectedProtocol.invokeWithoutCheckedException(sslSocket, new Object[0]);
            String s;
            if (bytes == null) {
                s = null;
            }
            else {
                s = new String(bytes, Util.UTF_8);
            }
            return s;
        }
        return null;
    }
    
    @Override
    public Object getStackTraceForCloseable(final String s) {
        return this.closeGuard.createAndOpen(s);
    }
    
    @Override
    public boolean isCleartextTrafficPermitted(final String s) {
        try {
            final Class<?> forName = Class.forName("android.security.NetworkSecurityPolicy");
            return (boolean)forName.getMethod("isCleartextTrafficPermitted", String.class).invoke(forName.getMethod("getInstance", (Class[])new Class[0]).invoke(null, new Object[0]), s);
        }
        catch (final ClassNotFoundException | NoSuchMethodException ex) {
            return super.isCleartextTrafficPermitted(s);
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex2) {
            throw new AssertionError();
        }
    }
    
    @Override
    public void log(int i, String string, final Throwable t) {
        int n = 5;
        if (i != 5) {
            n = 3;
        }
        if (t != null) {
            string = string + '\n' + Log.getStackTraceString(t);
        }
        i = 0;
        for (int length = string.length(); i < length; ++i) {
            int index;
            final int n2 = index = string.indexOf(10, i);
            int beginIndex = i;
            if (n2 == -1) {
                index = length;
                beginIndex = i;
            }
            while (true) {
                i = Math.min(index, beginIndex + 4000);
                Log.println(n, "OkHttp", string.substring(beginIndex, i));
                if (i >= index) {
                    break;
                }
                beginIndex = i;
            }
        }
    }
    
    @Override
    public void logCloseableLeak(final String s, final Object o) {
        if (!this.closeGuard.warnIfOpen(o)) {
            this.log(5, s, null);
        }
    }
    
    @Override
    public X509TrustManager trustManager(SSLSocketFactory fieldOrNull) {
        final Object fieldOrNull2 = Platform.readFieldOrNull(fieldOrNull, this.sslParametersClass, "sslParameters");
        Label_0018: {
            if (fieldOrNull2 == null) {
                try {
                    fieldOrNull = Platform.readFieldOrNull(fieldOrNull, Class.forName("com.google.android.gms.org.conscrypt.SSLParametersImpl", false, fieldOrNull.getClass().getClassLoader()), "sslParameters");
                    break Label_0018;
                }
                catch (final ClassNotFoundException ex) {
                    return super.trustManager((SSLSocketFactory)fieldOrNull);
                }
                return;
            }
            fieldOrNull = fieldOrNull2;
        }
        final X509TrustManager x509TrustManager = Platform.readFieldOrNull(fieldOrNull, X509TrustManager.class, "x509TrustManager");
        if (x509TrustManager == null) {
            return Platform.readFieldOrNull(fieldOrNull, X509TrustManager.class, "trustManager");
        }
        return x509TrustManager;
    }
    
    static final class AndroidCertificateChainCleaner extends CertificateChainCleaner
    {
        private final Method checkServerTrusted;
        private final Object x509TrustManagerExtensions;
        
        AndroidCertificateChainCleaner(final Object x509TrustManagerExtensions, final Method checkServerTrusted) {
            this.x509TrustManagerExtensions = x509TrustManagerExtensions;
            this.checkServerTrusted = checkServerTrusted;
        }
        
        @Override
        public List<Certificate> clean(final List<Certificate> list, final String s) throws SSLPeerUnverifiedException {
            try {
                return (List)this.checkServerTrusted.invoke(this.x509TrustManagerExtensions, list.toArray(new X509Certificate[list.size()]), "RSA", s);
            }
            catch (final InvocationTargetException cause) {
                final SSLPeerUnverifiedException ex = new SSLPeerUnverifiedException(cause.getMessage());
                ex.initCause(cause);
                throw ex;
            }
            catch (final IllegalAccessException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof AndroidCertificateChainCleaner;
        }
        
        @Override
        public int hashCode() {
            return 0;
        }
    }
    
    static final class CloseGuard
    {
        private final Method getMethod;
        private final Method openMethod;
        private final Method warnIfOpenMethod;
        
        CloseGuard(final Method getMethod, final Method openMethod, final Method warnIfOpenMethod) {
            this.getMethod = getMethod;
            this.openMethod = openMethod;
            this.warnIfOpenMethod = warnIfOpenMethod;
        }
        
        static CloseGuard get() {
            Method method = null;
            try {
                final Class<?> forName = Class.forName("dalvik.system.CloseGuard");
                final Method method2 = forName.getMethod("get", (Class[])new Class[0]);
                final Method method3 = forName.getMethod("open", String.class);
                method = forName.getMethod("warnIfOpen", (Class[])new Class[0]);
                return new CloseGuard(method2, method3, method);
            }
            catch (final Exception ex) {
                final Method method3 = null;
                final Method method2 = null;
                return new CloseGuard(method2, method3, method);
            }
        }
        
        Object createAndOpen(final String s) {
            if (this.getMethod != null) {
                try {
                    final Object invoke = this.getMethod.invoke(null, new Object[0]);
                    this.openMethod.invoke(invoke, s);
                    return invoke;
                }
                catch (final Exception ex) {}
            }
            return null;
        }
        
        boolean warnIfOpen(final Object obj) {
            boolean b = false;
            if (obj != null) {
                try {
                    this.warnIfOpenMethod.invoke(obj, new Object[0]);
                    b = true;
                }
                catch (final Exception ex) {}
            }
            return b;
        }
    }
}
