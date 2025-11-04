// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.platform;

import javax.net.ssl.SSLSocketFactory;
import java.util.logging.Level;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import okhttp3.internal.tls.BasicCertificateChainCleaner;
import okhttp3.internal.tls.TrustRootIndex;
import okhttp3.internal.tls.CertificateChainCleaner;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSocket;
import java.lang.reflect.Field;
import okio.Buffer;
import java.util.ArrayList;
import okhttp3.Protocol;
import java.util.List;
import okhttp3.OkHttpClient;
import java.util.logging.Logger;

public class Platform
{
    public static final int INFO = 4;
    private static final Platform PLATFORM;
    public static final int WARN = 5;
    private static final Logger logger;
    
    static {
        PLATFORM = findPlatform();
        logger = Logger.getLogger(OkHttpClient.class.getName());
    }
    
    public static List<String> alpnProtocolNames(final List<Protocol> list) {
        final ArrayList list2 = new ArrayList(list.size());
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Protocol protocol = list.get(i);
            if (protocol != Protocol.HTTP_1_0) {
                list2.add(protocol.toString());
            }
        }
        return list2;
    }
    
    static byte[] concatLengthPrefixed(final List<Protocol> list) {
        final Buffer buffer = new Buffer();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Protocol protocol = list.get(i);
            if (protocol != Protocol.HTTP_1_0) {
                buffer.writeByte(protocol.toString().length());
                buffer.writeUtf8(protocol.toString());
            }
        }
        return buffer.readByteArray();
    }
    
    private static Platform findPlatform() {
        final Platform buildIfSupported = AndroidPlatform.buildIfSupported();
        if (buildIfSupported != null) {
            return buildIfSupported;
        }
        final Jdk9Platform buildIfSupported2 = Jdk9Platform.buildIfSupported();
        if (buildIfSupported2 != null) {
            return buildIfSupported2;
        }
        final Platform buildIfSupported3 = JdkWithJettyBootPlatform.buildIfSupported();
        if (buildIfSupported3 == null) {
            return new Platform();
        }
        return buildIfSupported3;
    }
    
    public static Platform get() {
        return Platform.PLATFORM;
    }
    
    static <T> T readFieldOrNull(Object fieldOrNull, final Class<T> clazz, final String name) {
        Class<?> clazz2 = fieldOrNull.getClass();
        while (true) {
            while (clazz2 != Object.class) {
                try {
                    final Field declaredField = clazz2.getDeclaredField(name);
                    declaredField.setAccessible(true);
                    final Object value = declaredField.get(fieldOrNull);
                    if (value != null && clazz.isInstance(value)) {
                        return (T)clazz.cast(value);
                    }
                    return null;
                }
                catch (final IllegalAccessException ex) {
                    throw new AssertionError();
                }
                catch (final NoSuchFieldException ex2) {
                    clazz2 = clazz2.getSuperclass();
                    continue;
                }
                fieldOrNull = readFieldOrNull(fieldOrNull, Object.class, "delegate");
                if (fieldOrNull != null) {
                    return (T)readFieldOrNull(fieldOrNull, (Class<Object>)clazz, name);
                }
                return null;
            }
            if (name.equals("delegate")) {
                return null;
            }
            continue;
        }
    }
    
    public void afterHandshake(final SSLSocket sslSocket) {
    }
    
    public CertificateChainCleaner buildCertificateChainCleaner(final X509TrustManager x509TrustManager) {
        return new BasicCertificateChainCleaner(TrustRootIndex.get(x509TrustManager));
    }
    
    public void configureTlsExtensions(final SSLSocket sslSocket, final String s, final List<Protocol> list) {
    }
    
    public void connectSocket(final Socket socket, final InetSocketAddress endpoint, final int timeout) throws IOException {
        socket.connect(endpoint, timeout);
    }
    
    public String getPrefix() {
        return "OkHttp";
    }
    
    public String getSelectedProtocol(final SSLSocket sslSocket) {
        return null;
    }
    
    public Object getStackTraceForCloseable(final String message) {
        if (!Platform.logger.isLoggable(Level.FINE)) {
            return null;
        }
        return new Throwable(message);
    }
    
    public boolean isCleartextTrafficPermitted(final String s) {
        return true;
    }
    
    public void log(final int n, final String msg, final Throwable thrown) {
        Level level;
        if (n != 5) {
            level = Level.INFO;
        }
        else {
            level = Level.WARNING;
        }
        Platform.logger.log(level, msg, thrown);
    }
    
    public void logCloseableLeak(String string, final Object o) {
        if (o == null) {
            string += " To see where this was allocated, set the OkHttpClient logger level to FINE: Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);";
        }
        this.log(5, string, (Throwable)o);
    }
    
    public X509TrustManager trustManager(final SSLSocketFactory sslSocketFactory) {
        try {
            final Object fieldOrNull = readFieldOrNull(sslSocketFactory, Class.forName("sun.security.ssl.SSLContextImpl"), "context");
            if (fieldOrNull != null) {
                return readFieldOrNull(fieldOrNull, X509TrustManager.class, "trustManager");
            }
            return null;
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
    }
}
