// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http2;

import java.util.List;
import okhttp3.internal.Util;
import java.util.logging.Level;
import java.io.IOException;
import okio.BufferedSink;
import okio.Buffer;
import java.util.logging.Logger;
import java.io.Closeable;

final class Http2Writer implements Closeable
{
    private static final Logger logger;
    private final boolean client;
    private boolean closed;
    private final Buffer hpackBuffer;
    final Hpack.Writer hpackWriter;
    private int maxFrameSize;
    private final BufferedSink sink;
    
    static {
        logger = Logger.getLogger(Http2.class.getName());
    }
    
    public Http2Writer(final BufferedSink sink, final boolean client) {
        this.sink = sink;
        this.client = client;
        this.hpackBuffer = new Buffer();
        this.hpackWriter = new Hpack.Writer(this.hpackBuffer);
        this.maxFrameSize = 16384;
    }
    
    private void writeContinuationFrames(final int n, long b) throws IOException {
        while (true) {
            int n2;
            if (b <= 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 != 0) {
                break;
            }
            final int n3 = (int)Math.min(this.maxFrameSize, b);
            b -= n3;
            byte b2;
            if (b == 0L) {
                b2 = 4;
            }
            else {
                b2 = 0;
            }
            this.frameHeader(n, n3, (byte)9, b2);
            this.sink.write(this.hpackBuffer, n3);
        }
    }
    
    private static void writeMedium(final BufferedSink bufferedSink, final int n) throws IOException {
        bufferedSink.writeByte(n >>> 16 & 0xFF);
        bufferedSink.writeByte(n >>> 8 & 0xFF);
        bufferedSink.writeByte(n & 0xFF);
    }
    
    public void applyAndAckSettings(final Settings settings) throws IOException {
        while (true) {
            while (true) {
                synchronized (this) {
                    if (this.closed) {
                        throw new IOException("closed");
                    }
                    this.maxFrameSize = settings.getMaxFrameSize(this.maxFrameSize);
                    if (settings.getHeaderTableSize() == -1) {
                        this.frameHeader(0, 0, (byte)4, (byte)1);
                        this.sink.flush();
                        return;
                    }
                }
                final Settings settings2;
                this.hpackWriter.setHeaderTableSizeSetting(settings2.getHeaderTableSize());
                continue;
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this) {
            this.closed = true;
            this.sink.close();
        }
    }
    
    public void connectionPreface() throws IOException {
        while (true) {
            while (true) {
                Label_0076: {
                    synchronized (this) {
                        if (this.closed) {
                            throw new IOException("closed");
                        }
                        if (this.client) {
                            if (!Http2Writer.logger.isLoggable(Level.FINE)) {
                                this.sink.write(Http2.CONNECTION_PREFACE.toByteArray());
                                this.sink.flush();
                                return;
                            }
                            break Label_0076;
                        }
                    }
                    break;
                }
                Http2Writer.logger.fine(Util.format(">> CONNECTION %s", Http2.CONNECTION_PREFACE.hex()));
                continue;
            }
        }
        monitorexit(this);
    }
    
    public void data(final boolean b, final int n, final Buffer buffer, final int n2) throws IOException {
        while (true) {
            final byte b2 = 0;
            while (true) {
                synchronized (this) {
                    if (this.closed) {
                        throw new IOException("closed");
                    }
                    if (!b) {
                        final byte b3 = b2;
                        this.dataFrame(n, b3, buffer, n2);
                        return;
                    }
                }
                final byte b3 = 1;
                continue;
            }
        }
    }
    
    void dataFrame(final int n, final byte b, final Buffer buffer, final int n2) throws IOException {
        this.frameHeader(n, n2, (byte)0, b);
        if (n2 > 0) {
            this.sink.write(buffer, n2);
        }
    }
    
    public void flush() throws IOException {
        synchronized (this) {
            if (!this.closed) {
                this.sink.flush();
                return;
            }
            throw new IOException("closed");
        }
    }
    
    public void frameHeader(final int i, final int j, final byte b, final byte b2) throws IOException {
        if (Http2Writer.logger.isLoggable(Level.FINE)) {
            Http2Writer.logger.fine(Http2.frameLog(false, i, j, b, b2));
        }
        if (j > this.maxFrameSize) {
            throw Http2.illegalArgument("FRAME_SIZE_ERROR length > %d: %d", this.maxFrameSize, j);
        }
        if ((Integer.MIN_VALUE & i) == 0x0) {
            writeMedium(this.sink, j);
            this.sink.writeByte(b & 0xFF);
            this.sink.writeByte(b2 & 0xFF);
            this.sink.writeInt(Integer.MAX_VALUE & i);
            return;
        }
        throw Http2.illegalArgument("reserved bit set: %s", i);
    }
    
    public void goAway(final int n, final ErrorCode errorCode, final byte[] array) throws IOException {
        while (true) {
            while (true) {
                Label_0099: {
                    synchronized (this) {
                        if (this.closed) {
                            throw new IOException("closed");
                        }
                        if (errorCode.httpCode != -1) {
                            this.frameHeader(0, array.length + 8, (byte)7, (byte)0);
                            this.sink.writeInt(n);
                            this.sink.writeInt(errorCode.httpCode);
                            if (array.length <= 0) {
                                this.sink.flush();
                                return;
                            }
                            break Label_0099;
                        }
                    }
                    break;
                }
                this.sink.write(array);
                continue;
            }
        }
        throw Http2.illegalArgument("errorCode.httpCode == -1", new Object[0]);
    }
    
    public void headers(final int n, final List<Header> list) throws IOException {
        synchronized (this) {
            if (!this.closed) {
                this.headers(false, n, list);
                return;
            }
            throw new IOException("closed");
        }
    }
    
    void headers(final boolean b, final int n, final List<Header> list) throws IOException {
        final int n2 = 0;
        if (!this.closed) {
            this.hpackWriter.writeHeaders(list);
            final long size = this.hpackBuffer.size();
            final int n3 = (int)Math.min(this.maxFrameSize, size);
            byte b2;
            if (size == n3) {
                b2 = 4;
            }
            else {
                b2 = 0;
            }
            byte b3;
            if (!b) {
                b3 = b2;
            }
            else {
                b3 = (byte)(b2 | 0x1);
            }
            this.frameHeader(n, n3, (byte)1, b3);
            this.sink.write(this.hpackBuffer, n3);
            int n4 = n2;
            if (size <= n3) {
                n4 = 1;
            }
            if (n4 == 0) {
                this.writeContinuationFrames(n, size - n3);
            }
            return;
        }
        throw new IOException("closed");
    }
    
    public int maxDataLength() {
        return this.maxFrameSize;
    }
    
    public void ping(final boolean b, final int n, final int n2) throws IOException {
        while (true) {
            final byte b2 = 0;
            while (true) {
                synchronized (this) {
                    if (this.closed) {
                        throw new IOException("closed");
                    }
                    if (!b) {
                        final byte b3 = b2;
                        this.frameHeader(0, 8, (byte)6, b3);
                        this.sink.writeInt(n);
                        this.sink.writeInt(n2);
                        this.sink.flush();
                        return;
                    }
                }
                final byte b3 = 1;
                continue;
            }
        }
    }
    
    public void pushPromise(final int n, int n2, final List<Header> list) throws IOException {
        while (true) {
            final int n3 = 0;
            while (true) {
                synchronized (this) {
                    if (this.closed) {
                        throw new IOException("closed");
                    }
                    this.hpackWriter.writeHeaders(list);
                    final long size = this.hpackBuffer.size();
                    final int n4 = (int)Math.min(this.maxFrameSize - 4, size);
                    if (size == n4) {
                        final byte b = 4;
                        this.frameHeader(n, n4 + 4, (byte)5, b);
                        this.sink.writeInt(Integer.MAX_VALUE & n2);
                        this.sink.write(this.hpackBuffer, n4);
                        n2 = n3;
                        if (size <= n4) {
                            n2 = 1;
                        }
                        if (n2 == 0) {
                            this.writeContinuationFrames(n, size - n4);
                        }
                        return;
                    }
                }
                final byte b = 0;
                continue;
            }
        }
    }
    
    public void rstStream(final int n, final ErrorCode errorCode) throws IOException {
        synchronized (this) {
            if (this.closed) {
                throw new IOException("closed");
            }
            if (errorCode.httpCode != -1) {
                this.frameHeader(n, 4, (byte)3, (byte)0);
                this.sink.writeInt(errorCode.httpCode);
                this.sink.flush();
                monitorexit(this);
                return;
            }
        }
        final IllegalArgumentException ex = new IllegalArgumentException();
    }
    
    public void settings(final Settings settings) throws IOException {
        while (true) {
            int n = 0;
            while (true) {
                synchronized (this) {
                    if (this.closed) {
                        throw new IOException("closed");
                    }
                    this.frameHeader(0, settings.size() * 6, (byte)4, (byte)0);
                    if (n >= 10) {
                        this.sink.flush();
                        return;
                    }
                }
                final Settings settings2;
                if (settings2.isSet(n)) {
                    int n2;
                    if (n != 4) {
                        if (n != 7) {
                            n2 = n;
                        }
                        else {
                            n2 = 4;
                        }
                    }
                    else {
                        n2 = 3;
                    }
                    this.sink.writeShort(n2);
                    this.sink.writeInt(settings2.get(n));
                }
                ++n;
                continue;
            }
        }
    }
    
    public void synReply(final boolean b, final int n, final List<Header> list) throws IOException {
        synchronized (this) {
            if (!this.closed) {
                this.headers(b, n, list);
                return;
            }
            throw new IOException("closed");
        }
    }
    
    public void synStream(final boolean b, final int n, final int n2, final List<Header> list) throws IOException {
        synchronized (this) {
            if (!this.closed) {
                this.headers(b, n, list);
                return;
            }
            throw new IOException("closed");
        }
    }
    
    public void windowUpdate(final int n, final long l) throws IOException {
        while (true) {
            int n2 = 1;
        Label_0076:
            while (true) {
                Label_0070: {
                    synchronized (this) {
                        if (!this.closed) {
                            if (l != 0L) {
                                if (l > 2147483647L) {
                                    break Label_0070;
                                }
                                if (n2 != 0) {
                                    break Label_0076;
                                }
                            }
                            throw Http2.illegalArgument("windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: %s", l);
                        }
                    }
                    throw new IOException("closed");
                }
                n2 = 0;
                continue;
            }
            this.frameHeader(n, 4, (byte)8, (byte)0);
            this.sink.writeInt((int)l);
            this.sink.flush();
            monitorexit(this);
        }
    }
}
