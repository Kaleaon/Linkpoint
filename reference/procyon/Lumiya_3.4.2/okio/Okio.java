// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.util.logging.Level;
import java.net.SocketTimeoutException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.net.Socket;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.logging.Logger;

public final class Okio
{
    static final Logger logger;
    
    static {
        logger = Logger.getLogger(Okio.class.getName());
    }
    
    private Okio() {
    }
    
    public static Sink appendingSink(final File file) throws FileNotFoundException {
        if (file != null) {
            return sink(new FileOutputStream(file, true));
        }
        throw new IllegalArgumentException("file == null");
    }
    
    public static Sink blackhole() {
        return new Sink() {
            @Override
            public void close() throws IOException {
            }
            
            @Override
            public void flush() throws IOException {
            }
            
            @Override
            public Timeout timeout() {
                return Timeout.NONE;
            }
            
            @Override
            public void write(final Buffer buffer, final long n) throws IOException {
                buffer.skip(n);
            }
        };
    }
    
    public static BufferedSink buffer(final Sink sink) {
        return new RealBufferedSink(sink);
    }
    
    public static BufferedSource buffer(final Source source) {
        return new RealBufferedSource(source);
    }
    
    static boolean isAndroidGetsocknameError(final AssertionError assertionError) {
        final boolean b = false;
        boolean b2;
        if (assertionError.getCause() == null) {
            b2 = b;
        }
        else {
            b2 = b;
            if (assertionError.getMessage() != null) {
                b2 = b;
                if (assertionError.getMessage().contains("getsockname failed")) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public static Sink sink(final File file) throws FileNotFoundException {
        if (file != null) {
            return sink(new FileOutputStream(file));
        }
        throw new IllegalArgumentException("file == null");
    }
    
    public static Sink sink(final OutputStream outputStream) {
        return sink(outputStream, new Timeout());
    }
    
    private static Sink sink(final OutputStream outputStream, final Timeout timeout) {
        if (outputStream == null) {
            throw new IllegalArgumentException("out == null");
        }
        if (timeout != null) {
            return new Sink() {
                @Override
                public void close() throws IOException {
                    outputStream.close();
                }
                
                @Override
                public void flush() throws IOException {
                    outputStream.flush();
                }
                
                @Override
                public Timeout timeout() {
                    return timeout;
                }
                
                @Override
                public String toString() {
                    return "sink(" + outputStream + ")";
                }
                
                @Override
                public void write(final Buffer buffer, long a) throws IOException {
                    Util.checkOffsetAndCount(buffer.size, 0L, a);
                    while (true) {
                        int n;
                        if (a <= 0L) {
                            n = 1;
                        }
                        else {
                            n = 0;
                        }
                        if (n != 0) {
                            break;
                        }
                        timeout.throwIfReached();
                        final Segment head = buffer.head;
                        final int len = (int)Math.min(a, head.limit - head.pos);
                        outputStream.write(head.data, head.pos, len);
                        head.pos += len;
                        final long n2 = a - len;
                        buffer.size -= len;
                        a = n2;
                        if (head.pos != head.limit) {
                            continue;
                        }
                        buffer.head = head.pop();
                        SegmentPool.recycle(head);
                        a = n2;
                    }
                }
            };
        }
        throw new IllegalArgumentException("timeout == null");
    }
    
    public static Sink sink(final Socket socket) throws IOException {
        if (socket != null) {
            final AsyncTimeout timeout = timeout(socket);
            return timeout.sink(sink(socket.getOutputStream(), timeout));
        }
        throw new IllegalArgumentException("socket == null");
    }
    
    @IgnoreJRERequirement
    public static Sink sink(final Path path, final OpenOption... options) throws IOException {
        if (path != null) {
            return sink(Files.newOutputStream(path, options));
        }
        throw new IllegalArgumentException("path == null");
    }
    
    public static Source source(final File file) throws FileNotFoundException {
        if (file != null) {
            return source(new FileInputStream(file));
        }
        throw new IllegalArgumentException("file == null");
    }
    
    public static Source source(final InputStream inputStream) {
        return source(inputStream, new Timeout());
    }
    
    private static Source source(final InputStream inputStream, final Timeout timeout) {
        if (inputStream == null) {
            throw new IllegalArgumentException("in == null");
        }
        if (timeout != null) {
            return new Source() {
                @Override
                public void close() throws IOException {
                    inputStream.close();
                }
                
                @Override
                public long read(final Buffer buffer, final long n) throws IOException {
                    int n2 = 1;
                    if (n < 0L) {
                        n2 = 0;
                    }
                    if (n2 == 0) {
                        throw new IllegalArgumentException("byteCount < 0: " + n);
                    }
                    if (n == 0L) {
                        return 0L;
                    }
                    try {
                        timeout.throwIfReached();
                        final Segment writableSegment = buffer.writableSegment(1);
                        final int read = inputStream.read(writableSegment.data, writableSegment.limit, (int)Math.min(n, 8192 - writableSegment.limit));
                        if (read != -1) {
                            writableSegment.limit += read;
                            buffer.size += read;
                            return read;
                        }
                        return -1L;
                    }
                    catch (final AssertionError cause) {
                        if (!Okio.isAndroidGetsocknameError(cause)) {
                            throw cause;
                        }
                        throw new IOException(cause);
                    }
                }
                
                @Override
                public Timeout timeout() {
                    return timeout;
                }
                
                @Override
                public String toString() {
                    return "source(" + inputStream + ")";
                }
            };
        }
        throw new IllegalArgumentException("timeout == null");
    }
    
    public static Source source(final Socket socket) throws IOException {
        if (socket != null) {
            final AsyncTimeout timeout = timeout(socket);
            return timeout.source(source(socket.getInputStream(), timeout));
        }
        throw new IllegalArgumentException("socket == null");
    }
    
    @IgnoreJRERequirement
    public static Source source(final Path path, final OpenOption... options) throws IOException {
        if (path != null) {
            return source(Files.newInputStream(path, options));
        }
        throw new IllegalArgumentException("path == null");
    }
    
    private static AsyncTimeout timeout(final Socket socket) {
        return new AsyncTimeout() {
            @Override
            protected IOException newTimeoutException(final IOException cause) {
                final SocketTimeoutException ex = new SocketTimeoutException("timeout");
                if (cause != null) {
                    ex.initCause(cause);
                }
                return ex;
            }
            
            @Override
            protected void timedOut() {
                try {
                    socket.close();
                }
                catch (final Exception thrown) {
                    Okio.logger.log(Level.WARNING, "Failed to close timed out socket " + socket, thrown);
                }
                catch (final AssertionError thrown2) {
                    if (!Okio.isAndroidGetsocknameError(thrown2)) {
                        throw thrown2;
                    }
                    Okio.logger.log(Level.WARNING, "Failed to close timed out socket " + socket, thrown2);
                }
            }
        };
    }
}
