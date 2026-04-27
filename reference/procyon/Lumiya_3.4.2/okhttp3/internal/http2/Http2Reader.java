// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http2;

import okio.Timeout;
import okio.Buffer;
import okhttp3.internal.Util;
import java.util.logging.Level;
import java.util.List;
import okio.ByteString;
import java.io.IOException;
import okio.Source;
import okio.BufferedSource;
import java.util.logging.Logger;
import java.io.Closeable;

final class Http2Reader implements Closeable
{
    static final Logger logger;
    private final boolean client;
    private final ContinuationSource continuation;
    final Hpack.Reader hpackReader;
    private final BufferedSource source;
    
    static {
        logger = Logger.getLogger(Http2.class.getName());
    }
    
    public Http2Reader(final BufferedSource source, final boolean client) {
        this.source = source;
        this.client = client;
        this.continuation = new ContinuationSource(this.source);
        this.hpackReader = new Hpack.Reader(4096, this.continuation);
    }
    
    static int lengthWithoutPadding(int i, final byte b, final short s) throws IOException {
        if ((b & 0x8) != 0x0) {
            --i;
        }
        if (s <= i) {
            return (short)(i - s);
        }
        throw Http2.ioException("PROTOCOL_ERROR padding %s > remaining length %s", s, i);
    }
    
    private void readData(final Handler handler, int lengthWithoutPadding, final byte b, final int n) throws IOException {
        boolean b2 = true;
        final short n2 = 0;
        final boolean b3 = (b & 0x1) != 0x0;
        if ((b & 0x20) == 0x0) {
            b2 = false;
        }
        if (!b2) {
            short n3;
            if ((b & 0x8) == 0x0) {
                n3 = n2;
            }
            else {
                n3 = (short)(this.source.readByte() & 0xFF);
            }
            lengthWithoutPadding = lengthWithoutPadding(lengthWithoutPadding, b, n3);
            handler.data(b3, n, this.source, lengthWithoutPadding);
            this.source.skip(n3);
            return;
        }
        throw Http2.ioException("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA", new Object[0]);
    }
    
    private void readGoAway(final Handler handler, int i, final byte b, int int1) throws IOException {
        if (i < 8) {
            throw Http2.ioException("TYPE_GOAWAY length < 8: %s", i);
        }
        if (int1 != 0) {
            throw Http2.ioException("TYPE_GOAWAY streamId != 0", new Object[0]);
        }
        final int int2 = this.source.readInt();
        int1 = this.source.readInt();
        i -= 8;
        final ErrorCode fromHttp2 = ErrorCode.fromHttp2(int1);
        if (fromHttp2 != null) {
            ByteString byteString = ByteString.EMPTY;
            if (i > 0) {
                byteString = this.source.readByteString(i);
            }
            handler.goAway(int2, fromHttp2, byteString);
            return;
        }
        throw Http2.ioException("TYPE_GOAWAY unexpected error code: %d", int1);
    }
    
    private List<Header> readHeaderBlock(final int n, final short n2, final byte b, final int streamId) throws IOException {
        final ContinuationSource continuation = this.continuation;
        this.continuation.left = n;
        continuation.length = n;
        this.continuation.padding = n2;
        this.continuation.flags = b;
        this.continuation.streamId = streamId;
        this.hpackReader.readHeaders();
        return this.hpackReader.getAndResetHeaderList();
    }
    
    private void readHeaders(final Handler handler, int n, final byte b, final int n2) throws IOException {
        final short n3 = 0;
        if (n2 != 0) {
            final boolean b2 = (b & 0x1) != 0x0;
            short n4;
            if ((b & 0x8) == 0x0) {
                n4 = n3;
            }
            else {
                n4 = (short)(this.source.readByte() & 0xFF);
            }
            if ((b & 0x20) != 0x0) {
                this.readPriority(handler, n2);
                n -= 5;
            }
            handler.headers(b2, n2, -1, this.readHeaderBlock(lengthWithoutPadding(n, b, n4), n4, b, n2));
            return;
        }
        throw Http2.ioException("PROTOCOL_ERROR: TYPE_HEADERS streamId == 0", new Object[0]);
    }
    
    static int readMedium(final BufferedSource bufferedSource) throws IOException {
        return (bufferedSource.readByte() & 0xFF) << 16 | (bufferedSource.readByte() & 0xFF) << 8 | (bufferedSource.readByte() & 0xFF);
    }
    
    private void readPing(final Handler handler, int int1, final byte b, int int2) throws IOException {
        boolean b2 = false;
        if (int1 != 8) {
            throw Http2.ioException("TYPE_PING length != 8: %s", int1);
        }
        if (int2 == 0) {
            int1 = this.source.readInt();
            int2 = this.source.readInt();
            if ((b & 0x1) != 0x0) {
                b2 = true;
            }
            handler.ping(b2, int1, int2);
            return;
        }
        throw Http2.ioException("TYPE_PING streamId != 0", new Object[0]);
    }
    
    private void readPriority(final Handler handler, final int n) throws IOException {
        boolean b = false;
        final int int1 = this.source.readInt();
        if ((Integer.MIN_VALUE & int1) != 0x0) {
            b = true;
        }
        handler.priority(n, int1 & Integer.MAX_VALUE, (this.source.readByte() & 0xFF) + 1, b);
    }
    
    private void readPriority(final Handler handler, final int i, final byte b, final int n) throws IOException {
        if (i != 5) {
            throw Http2.ioException("TYPE_PRIORITY length: %d != 5", i);
        }
        if (n != 0) {
            this.readPriority(handler, n);
            return;
        }
        throw Http2.ioException("TYPE_PRIORITY streamId == 0", new Object[0]);
    }
    
    private void readPushPromise(final Handler handler, final int n, final byte b, final int n2) throws IOException {
        final short n3 = 0;
        if (n2 != 0) {
            short n4;
            if ((b & 0x8) == 0x0) {
                n4 = n3;
            }
            else {
                n4 = (short)(this.source.readByte() & 0xFF);
            }
            handler.pushPromise(n2, this.source.readInt() & Integer.MAX_VALUE, this.readHeaderBlock(lengthWithoutPadding(n - 4, b, n4), n4, b, n2));
            return;
        }
        throw Http2.ioException("PROTOCOL_ERROR: TYPE_PUSH_PROMISE streamId == 0", new Object[0]);
    }
    
    private void readRstStream(final Handler handler, int int1, final byte b, final int n) throws IOException {
        if (int1 != 4) {
            throw Http2.ioException("TYPE_RST_STREAM length: %d != 4", int1);
        }
        if (n == 0) {
            throw Http2.ioException("TYPE_RST_STREAM streamId == 0", new Object[0]);
        }
        int1 = this.source.readInt();
        final ErrorCode fromHttp2 = ErrorCode.fromHttp2(int1);
        if (fromHttp2 != null) {
            handler.rstStream(n, fromHttp2);
            return;
        }
        throw Http2.ioException("TYPE_RST_STREAM unexpected error code: %d", int1);
    }
    
    private void readSettings(final Handler handler, final int i, final byte b, int j) throws IOException {
        if (j != 0) {
            throw Http2.ioException("TYPE_SETTINGS streamId != 0", new Object[0]);
        }
        if ((b & 0x1) == 0x0) {
            if (i % 6 == 0) {
                final Settings settings = new Settings();
                j = 0;
            Label_0163_Outer:
                while (j < i) {
                    final short short1 = this.source.readShort();
                    final int int1 = this.source.readInt();
                    int n = short1;
                    while (true) {
                        switch (short1) {
                            default: {
                                n = short1;
                                break Label_0163;
                            }
                            case 3: {
                                n = 4;
                            }
                            case 1:
                            case 6: {
                                settings.set(n, int1);
                                j += 6;
                                continue Label_0163_Outer;
                            }
                            case 2: {
                                n = short1;
                                if (int1 == 0) {
                                    continue;
                                }
                                n = short1;
                                if (int1 != 1) {
                                    throw Http2.ioException("PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1", new Object[0]);
                                }
                                continue;
                            }
                            case 4: {
                                n = 7;
                                if (int1 < 0) {
                                    throw Http2.ioException("PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1", new Object[0]);
                                }
                                continue;
                            }
                            case 5: {
                                if (int1 >= 16384 && int1 <= 16777215) {
                                    n = short1;
                                    continue;
                                }
                                throw Http2.ioException("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: %s", int1);
                            }
                        }
                        break;
                    }
                }
                handler.settings(false, settings);
                return;
            }
            throw Http2.ioException("TYPE_SETTINGS length %% 6 != 0: %s", i);
        }
        else {
            if (i == 0) {
                handler.ackSettings();
                return;
            }
            throw Http2.ioException("FRAME_SIZE_ERROR ack frame should be empty!", new Object[0]);
        }
    }
    
    private void readWindowUpdate(final Handler handler, final int i, final byte b, final int n) throws IOException {
        if (i != 4) {
            throw Http2.ioException("TYPE_WINDOW_UPDATE length !=4: %s", i);
        }
        final long l = (long)this.source.readInt() & 0x7FFFFFFFL;
        if (l == 0L) {
            throw Http2.ioException("windowSizeIncrement was 0", l);
        }
        handler.windowUpdate(n, l);
    }
    
    @Override
    public void close() throws IOException {
        this.source.close();
    }
    
    public boolean nextFrame(final Handler handler) throws IOException {
        int medium;
        while (true) {
            while (true) {
                try {
                    this.source.require(9L);
                    medium = readMedium(this.source);
                    if (medium < 0) {
                        throw Http2.ioException("FRAME_SIZE_ERROR: %s", medium);
                    }
                }
                catch (final IOException ex) {
                    return false;
                }
                if (medium <= 16384) {
                    break;
                }
                continue;
            }
        }
        final byte b = (byte)(this.source.readByte() & 0xFF);
        final byte b2 = (byte)(this.source.readByte() & 0xFF);
        final int n = this.source.readInt() & Integer.MAX_VALUE;
        if (Http2Reader.logger.isLoggable(Level.FINE)) {
            Http2Reader.logger.fine(Http2.frameLog(true, n, medium, b, b2));
        }
        switch (b) {
            default: {
                this.source.skip(medium);
                break;
            }
            case 0: {
                this.readData(handler, medium, b2, n);
                break;
            }
            case 1: {
                this.readHeaders(handler, medium, b2, n);
                break;
            }
            case 2: {
                this.readPriority(handler, medium, b2, n);
                break;
            }
            case 3: {
                this.readRstStream(handler, medium, b2, n);
                break;
            }
            case 4: {
                this.readSettings(handler, medium, b2, n);
                break;
            }
            case 5: {
                this.readPushPromise(handler, medium, b2, n);
                break;
            }
            case 6: {
                this.readPing(handler, medium, b2, n);
                break;
            }
            case 7: {
                this.readGoAway(handler, medium, b2, n);
                break;
            }
            case 8: {
                this.readWindowUpdate(handler, medium, b2, n);
                break;
            }
        }
        return true;
    }
    
    public void readConnectionPreface() throws IOException {
        if (this.client) {
            return;
        }
        final ByteString byteString = this.source.readByteString(Http2.CONNECTION_PREFACE.size());
        if (Http2Reader.logger.isLoggable(Level.FINE)) {
            Http2Reader.logger.fine(Util.format("<< CONNECTION %s", byteString.hex()));
        }
        if (Http2.CONNECTION_PREFACE.equals(byteString)) {
            return;
        }
        throw Http2.ioException("Expected a connection header but was %s", byteString.utf8());
    }
    
    static final class ContinuationSource implements Source
    {
        byte flags;
        int left;
        int length;
        short padding;
        private final BufferedSource source;
        int streamId;
        
        public ContinuationSource(final BufferedSource source) {
            this.source = source;
        }
        
        private void readContinuationHeader() throws IOException {
            final int streamId = this.streamId;
            final int medium = Http2Reader.readMedium(this.source);
            this.left = medium;
            this.length = medium;
            final byte b = (byte)(this.source.readByte() & 0xFF);
            this.flags = (byte)(this.source.readByte() & 0xFF);
            if (Http2Reader.logger.isLoggable(Level.FINE)) {
                Http2Reader.logger.fine(Http2.frameLog(true, this.streamId, this.length, b, this.flags));
            }
            this.streamId = (this.source.readInt() & Integer.MAX_VALUE);
            if (b != 9) {
                throw Http2.ioException("%s != TYPE_CONTINUATION", b);
            }
            if (this.streamId == streamId) {
                return;
            }
            throw Http2.ioException("TYPE_CONTINUATION streamId changed", new Object[0]);
        }
        
        @Override
        public void close() throws IOException {
        }
        
        @Override
        public long read(final Buffer buffer, long read) throws IOException {
            while (this.left == 0) {
                this.source.skip(this.padding);
                this.padding = 0;
                if ((this.flags & 0x4) != 0x0) {
                    return -1L;
                }
                this.readContinuationHeader();
            }
            read = this.source.read(buffer, Math.min(read, this.left));
            if (read == -1L) {
                return -1L;
            }
            this.left -= (int)read;
            return read;
        }
        
        @Override
        public Timeout timeout() {
            return this.source.timeout();
        }
    }
    
    interface Handler
    {
        void ackSettings();
        
        void alternateService(final int p0, final String p1, final ByteString p2, final String p3, final int p4, final long p5);
        
        void data(final boolean p0, final int p1, final BufferedSource p2, final int p3) throws IOException;
        
        void goAway(final int p0, final ErrorCode p1, final ByteString p2);
        
        void headers(final boolean p0, final int p1, final int p2, final List<Header> p3);
        
        void ping(final boolean p0, final int p1, final int p2);
        
        void priority(final int p0, final int p1, final int p2, final boolean p3);
        
        void pushPromise(final int p0, final int p1, final List<Header> p2) throws IOException;
        
        void rstStream(final int p0, final ErrorCode p1);
        
        void settings(final boolean p0, final Settings p1);
        
        void windowUpdate(final int p0, final long p1);
    }
}
