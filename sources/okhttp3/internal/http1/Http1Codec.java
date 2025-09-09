package okhttp3.internal.http1;

import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.internal.http.RequestLine;
import okhttp3.internal.http.StatusLine;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingTimeout;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class Http1Codec implements HttpCodec {
    private static final int STATE_CLOSED = 6;
    private static final int STATE_IDLE = 0;
    private static final int STATE_OPEN_REQUEST_BODY = 1;
    private static final int STATE_OPEN_RESPONSE_BODY = 4;
    private static final int STATE_READING_RESPONSE_BODY = 5;
    private static final int STATE_READ_RESPONSE_HEADERS = 3;
    private static final int STATE_WRITING_REQUEST_BODY = 2;
    final OkHttpClient client;
    final BufferedSink sink;
    final BufferedSource source;
    int state = 0;
    final StreamAllocation streamAllocation;

    private abstract class AbstractSource implements Source {
        protected boolean closed;
        protected final ForwardingTimeout timeout;

        private AbstractSource() {
            this.timeout = new ForwardingTimeout(Http1Codec.this.source.timeout());
        }

        /* access modifiers changed from: protected */
        public final void endOfInput(boolean z) throws IOException {
            boolean z2 = false;
            if (Http1Codec.this.state == 6) {
                return;
            }
            if (Http1Codec.this.state == 5) {
                Http1Codec.this.detachTimeout(this.timeout);
                Http1Codec.this.state = 6;
                if (Http1Codec.this.streamAllocation != null) {
                    StreamAllocation streamAllocation = Http1Codec.this.streamAllocation;
                    if (!z) {
                        z2 = true;
                    }
                    streamAllocation.streamFinished(z2, Http1Codec.this);
                    return;
                }
                return;
            }
            throw new IllegalStateException("state: " + Http1Codec.this.state);
        }

        public Timeout timeout() {
            return this.timeout;
        }
    }

    private final class ChunkedSink implements Sink {
        private boolean closed;
        private final ForwardingTimeout timeout = new ForwardingTimeout(Http1Codec.this.sink.timeout());

        ChunkedSink() {
        }

        public synchronized void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                Http1Codec.this.sink.writeUtf8("0\r\n\r\n");
                Http1Codec.this.detachTimeout(this.timeout);
                Http1Codec.this.state = 3;
            }
        }

        public synchronized void flush() throws IOException {
            if (!this.closed) {
                Http1Codec.this.sink.flush();
            }
        }

        public Timeout timeout() {
            return this.timeout;
        }

        public void write(Buffer buffer, long j) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (j != 0) {
                Http1Codec.this.sink.writeHexadecimalUnsignedLong(j);
                Http1Codec.this.sink.writeUtf8("\r\n");
                Http1Codec.this.sink.write(buffer, j);
                Http1Codec.this.sink.writeUtf8("\r\n");
            }
        }
    }

    private class ChunkedSource extends AbstractSource {
        private static final long NO_CHUNK_YET = -1;
        private long bytesRemainingInChunk = -1;
        private boolean hasMoreChunks = true;
        private final HttpUrl url;

        ChunkedSource(HttpUrl httpUrl) {
            super();
            this.url = httpUrl;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0060, code lost:
            if (r3.startsWith(";") == false) goto L_0x0062;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void readChunkSize() throws java.io.IOException {
            /*
                r10 = this;
                r8 = 0
                r1 = 1
                r2 = 0
                long r4 = r10.bytesRemainingInChunk
                r6 = -1
                int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r0 == 0) goto L_0x0013
                okhttp3.internal.http1.Http1Codec r0 = okhttp3.internal.http1.Http1Codec.this
                okio.BufferedSource r0 = r0.source
                r0.readUtf8LineStrict()
            L_0x0013:
                okhttp3.internal.http1.Http1Codec r0 = okhttp3.internal.http1.Http1Codec.this     // Catch:{ NumberFormatException -> 0x0089 }
                okio.BufferedSource r0 = r0.source     // Catch:{ NumberFormatException -> 0x0089 }
                long r4 = r0.readHexadecimalUnsignedLong()     // Catch:{ NumberFormatException -> 0x0089 }
                r10.bytesRemainingInChunk = r4     // Catch:{ NumberFormatException -> 0x0089 }
                okhttp3.internal.http1.Http1Codec r0 = okhttp3.internal.http1.Http1Codec.this     // Catch:{ NumberFormatException -> 0x0089 }
                okio.BufferedSource r0 = r0.source     // Catch:{ NumberFormatException -> 0x0089 }
                java.lang.String r0 = r0.readUtf8LineStrict()     // Catch:{ NumberFormatException -> 0x0089 }
                java.lang.String r3 = r0.trim()     // Catch:{ NumberFormatException -> 0x0089 }
                long r4 = r10.bytesRemainingInChunk     // Catch:{ NumberFormatException -> 0x0089 }
                int r0 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
                if (r0 >= 0) goto L_0x0057
                r0 = r1
            L_0x0030:
                if (r0 != 0) goto L_0x0062
                boolean r0 = r3.isEmpty()     // Catch:{ NumberFormatException -> 0x0089 }
                if (r0 == 0) goto L_0x0059
            L_0x0038:
                long r4 = r10.bytesRemainingInChunk
                int r0 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
                if (r0 != 0) goto L_0x0056
                r10.hasMoreChunks = r2
                okhttp3.internal.http1.Http1Codec r0 = okhttp3.internal.http1.Http1Codec.this
                okhttp3.OkHttpClient r0 = r0.client
                okhttp3.CookieJar r0 = r0.cookieJar()
                okhttp3.HttpUrl r2 = r10.url
                okhttp3.internal.http1.Http1Codec r3 = okhttp3.internal.http1.Http1Codec.this
                okhttp3.Headers r3 = r3.readHeaders()
                okhttp3.internal.http.HttpHeaders.receiveHeaders(r0, r2, r3)
                r10.endOfInput(r1)
            L_0x0056:
                return
            L_0x0057:
                r0 = r2
                goto L_0x0030
            L_0x0059:
                java.lang.String r0 = ";"
                boolean r0 = r3.startsWith(r0)     // Catch:{ NumberFormatException -> 0x0089 }
                if (r0 != 0) goto L_0x0038
            L_0x0062:
                java.net.ProtocolException r0 = new java.net.ProtocolException     // Catch:{ NumberFormatException -> 0x0089 }
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x0089 }
                r1.<init>()     // Catch:{ NumberFormatException -> 0x0089 }
                java.lang.String r2 = "expected chunk size and optional extensions but was \""
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ NumberFormatException -> 0x0089 }
                long r4 = r10.bytesRemainingInChunk     // Catch:{ NumberFormatException -> 0x0089 }
                java.lang.StringBuilder r1 = r1.append(r4)     // Catch:{ NumberFormatException -> 0x0089 }
                java.lang.StringBuilder r1 = r1.append(r3)     // Catch:{ NumberFormatException -> 0x0089 }
                java.lang.String r2 = "\""
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ NumberFormatException -> 0x0089 }
                java.lang.String r1 = r1.toString()     // Catch:{ NumberFormatException -> 0x0089 }
                r0.<init>(r1)     // Catch:{ NumberFormatException -> 0x0089 }
                throw r0     // Catch:{ NumberFormatException -> 0x0089 }
            L_0x0089:
                r0 = move-exception
                java.net.ProtocolException r1 = new java.net.ProtocolException
                java.lang.String r0 = r0.getMessage()
                r1.<init>(r0)
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http1.Http1Codec.ChunkedSource.readChunkSize():void");
        }

        public void close() throws IOException {
            if (!this.closed) {
                if (this.hasMoreChunks && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                    endOfInput(false);
                }
                this.closed = true;
            }
        }

        public long read(Buffer buffer, long j) throws IOException {
            if (!(j >= 0)) {
                throw new IllegalArgumentException("byteCount < 0: " + j);
            } else if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (!this.hasMoreChunks) {
                return -1;
            } else {
                if (this.bytesRemainingInChunk == 0 || this.bytesRemainingInChunk == -1) {
                    readChunkSize();
                    if (!this.hasMoreChunks) {
                        return -1;
                    }
                }
                long read = Http1Codec.this.source.read(buffer, Math.min(j, this.bytesRemainingInChunk));
                if (read == -1) {
                    endOfInput(false);
                    throw new ProtocolException("unexpected end of stream");
                }
                this.bytesRemainingInChunk -= read;
                return read;
            }
        }
    }

    private final class FixedLengthSink implements Sink {
        private long bytesRemaining;
        private boolean closed;
        private final ForwardingTimeout timeout = new ForwardingTimeout(Http1Codec.this.sink.timeout());

        FixedLengthSink(long j) {
            this.bytesRemaining = j;
        }

        public void close() throws IOException {
            boolean z = true;
            if (!this.closed) {
                this.closed = true;
                if (this.bytesRemaining > 0) {
                    z = false;
                }
                if (!z) {
                    throw new ProtocolException("unexpected end of stream");
                }
                Http1Codec.this.detachTimeout(this.timeout);
                Http1Codec.this.state = 3;
            }
        }

        public void flush() throws IOException {
            if (!this.closed) {
                Http1Codec.this.sink.flush();
            }
        }

        public Timeout timeout() {
            return this.timeout;
        }

        public void write(Buffer buffer, long j) throws IOException {
            if (!this.closed) {
                Util.checkOffsetAndCount(buffer.size(), 0, j);
                if (!(j <= this.bytesRemaining)) {
                    throw new ProtocolException("expected " + this.bytesRemaining + " bytes but received " + j);
                }
                Http1Codec.this.sink.write(buffer, j);
                this.bytesRemaining -= j;
                return;
            }
            throw new IllegalStateException("closed");
        }
    }

    private class FixedLengthSource extends AbstractSource {
        private long bytesRemaining;

        public FixedLengthSource(long j) throws IOException {
            super();
            this.bytesRemaining = j;
            if (this.bytesRemaining == 0) {
                endOfInput(true);
            }
        }

        public void close() throws IOException {
            if (!this.closed) {
                if (this.bytesRemaining != 0 && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                    endOfInput(false);
                }
                this.closed = true;
            }
        }

        public long read(Buffer buffer, long j) throws IOException {
            if (!(j >= 0)) {
                throw new IllegalArgumentException("byteCount < 0: " + j);
            } else if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (this.bytesRemaining == 0) {
                return -1;
            } else {
                long read = Http1Codec.this.source.read(buffer, Math.min(this.bytesRemaining, j));
                if (read == -1) {
                    endOfInput(false);
                    throw new ProtocolException("unexpected end of stream");
                }
                this.bytesRemaining -= read;
                if (this.bytesRemaining == 0) {
                    endOfInput(true);
                }
                return read;
            }
        }
    }

    private class UnknownLengthSource extends AbstractSource {
        private boolean inputExhausted;

        UnknownLengthSource() {
            super();
        }

        public void close() throws IOException {
            if (!this.closed) {
                if (!this.inputExhausted) {
                    endOfInput(false);
                }
                this.closed = true;
            }
        }

        public long read(Buffer buffer, long j) throws IOException {
            boolean z = false;
            if (j >= 0) {
                z = true;
            }
            if (!z) {
                throw new IllegalArgumentException("byteCount < 0: " + j);
            } else if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (this.inputExhausted) {
                return -1;
            } else {
                long read = Http1Codec.this.source.read(buffer, j);
                if (read != -1) {
                    return read;
                }
                this.inputExhausted = true;
                endOfInput(true);
                return -1;
            }
        }
    }

    public Http1Codec(OkHttpClient okHttpClient, StreamAllocation streamAllocation2, BufferedSource bufferedSource, BufferedSink bufferedSink) {
        this.client = okHttpClient;
        this.streamAllocation = streamAllocation2;
        this.source = bufferedSource;
        this.sink = bufferedSink;
    }

    private Source getTransferStream(Response response) throws IOException {
        if (!HttpHeaders.hasBody(response)) {
            return newFixedLengthSource(0);
        }
        if ("chunked".equalsIgnoreCase(response.header(com.google.common.net.HttpHeaders.TRANSFER_ENCODING))) {
            return newChunkedSource(response.request().url());
        }
        long contentLength = HttpHeaders.contentLength(response);
        return contentLength != -1 ? newFixedLengthSource(contentLength) : newUnknownLengthSource();
    }

    public void cancel() {
        RealConnection connection = this.streamAllocation.connection();
        if (connection != null) {
            connection.cancel();
        }
    }

    public Sink createRequestBody(Request request, long j) {
        if ("chunked".equalsIgnoreCase(request.header(com.google.common.net.HttpHeaders.TRANSFER_ENCODING))) {
            return newChunkedSink();
        }
        if (j != -1) {
            return newFixedLengthSink(j);
        }
        throw new IllegalStateException("Cannot stream a request body without chunked encoding or a known content length!");
    }

    /* access modifiers changed from: package-private */
    public void detachTimeout(ForwardingTimeout forwardingTimeout) {
        Timeout delegate = forwardingTimeout.delegate();
        forwardingTimeout.setDelegate(Timeout.NONE);
        delegate.clearDeadline();
        delegate.clearTimeout();
    }

    public void finishRequest() throws IOException {
        this.sink.flush();
    }

    public boolean isClosed() {
        return this.state == 6;
    }

    public Sink newChunkedSink() {
        if (this.state == 1) {
            this.state = 2;
            return new ChunkedSink();
        }
        throw new IllegalStateException("state: " + this.state);
    }

    public Source newChunkedSource(HttpUrl httpUrl) throws IOException {
        if (this.state == 4) {
            this.state = 5;
            return new ChunkedSource(httpUrl);
        }
        throw new IllegalStateException("state: " + this.state);
    }

    public Sink newFixedLengthSink(long j) {
        if (this.state == 1) {
            this.state = 2;
            return new FixedLengthSink(j);
        }
        throw new IllegalStateException("state: " + this.state);
    }

    public Source newFixedLengthSource(long j) throws IOException {
        if (this.state == 4) {
            this.state = 5;
            return new FixedLengthSource(j);
        }
        throw new IllegalStateException("state: " + this.state);
    }

    public Source newUnknownLengthSource() throws IOException {
        if (this.state != 4) {
            throw new IllegalStateException("state: " + this.state);
        } else if (this.streamAllocation != null) {
            this.state = 5;
            this.streamAllocation.noNewStreams();
            return new UnknownLengthSource();
        } else {
            throw new IllegalStateException("streamAllocation == null");
        }
    }

    public ResponseBody openResponseBody(Response response) throws IOException {
        return new RealResponseBody(response.headers(), Okio.buffer(getTransferStream(response)));
    }

    public Headers readHeaders() throws IOException {
        Headers.Builder builder = new Headers.Builder();
        while (true) {
            String readUtf8LineStrict = this.source.readUtf8LineStrict();
            if (readUtf8LineStrict.length() == 0) {
                return builder.build();
            }
            Internal.instance.addLenient(builder, readUtf8LineStrict);
        }
    }

    public Response.Builder readResponse() throws IOException {
        StatusLine parse;
        Response.Builder headers;
        if (this.state == 1 || this.state == 3) {
            do {
                try {
                    parse = StatusLine.parse(this.source.readUtf8LineStrict());
                    headers = new Response.Builder().protocol(parse.protocol).code(parse.code).message(parse.message).headers(readHeaders());
                } catch (EOFException e) {
                    IOException iOException = new IOException("unexpected end of stream on " + this.streamAllocation);
                    iOException.initCause(e);
                    throw iOException;
                }
            } while (parse.code == 100);
            this.state = 4;
            return headers;
        }
        throw new IllegalStateException("state: " + this.state);
    }

    public Response.Builder readResponseHeaders() throws IOException {
        return readResponse();
    }

    public void writeRequest(Headers headers, String str) throws IOException {
        if (this.state == 0) {
            this.sink.writeUtf8(str).writeUtf8("\r\n");
            int size = headers.size();
            for (int i = 0; i < size; i++) {
                this.sink.writeUtf8(headers.name(i)).writeUtf8(": ").writeUtf8(headers.value(i)).writeUtf8("\r\n");
            }
            this.sink.writeUtf8("\r\n");
            this.state = 1;
            return;
        }
        throw new IllegalStateException("state: " + this.state);
    }

    public void writeRequestHeaders(Request request) throws IOException {
        writeRequest(request.headers(), RequestLine.get(request, this.streamAllocation.connection().route().proxy().type()));
    }
}
