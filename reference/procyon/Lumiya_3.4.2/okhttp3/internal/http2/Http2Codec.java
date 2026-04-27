// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http2;

import okio.ForwardingSource;
import java.util.concurrent.TimeUnit;
import okhttp3.internal.http.RealResponseBody;
import okio.Source;
import okio.Okio;
import okhttp3.ResponseBody;
import okio.Sink;
import java.io.IOException;
import java.net.ProtocolException;
import okhttp3.Protocol;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.Internal;
import okhttp3.Response;
import okhttp3.Headers;
import java.util.Locale;
import okhttp3.internal.http.RequestLine;
import java.util.ArrayList;
import okhttp3.Request;
import okhttp3.internal.Util;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.OkHttpClient;
import java.util.List;
import okio.ByteString;
import okhttp3.internal.http.HttpCodec;

public final class Http2Codec implements HttpCodec
{
    private static final ByteString CONNECTION;
    private static final ByteString ENCODING;
    private static final ByteString HOST;
    private static final List<ByteString> HTTP_2_SKIPPED_REQUEST_HEADERS;
    private static final List<ByteString> HTTP_2_SKIPPED_RESPONSE_HEADERS;
    private static final ByteString KEEP_ALIVE;
    private static final ByteString PROXY_CONNECTION;
    private static final ByteString TE;
    private static final ByteString TRANSFER_ENCODING;
    private static final ByteString UPGRADE;
    private final OkHttpClient client;
    private final Http2Connection connection;
    private Http2Stream stream;
    final StreamAllocation streamAllocation;
    
    static {
        CONNECTION = ByteString.encodeUtf8("connection");
        HOST = ByteString.encodeUtf8("host");
        KEEP_ALIVE = ByteString.encodeUtf8("keep-alive");
        PROXY_CONNECTION = ByteString.encodeUtf8("proxy-connection");
        TRANSFER_ENCODING = ByteString.encodeUtf8("transfer-encoding");
        TE = ByteString.encodeUtf8("te");
        ENCODING = ByteString.encodeUtf8("encoding");
        UPGRADE = ByteString.encodeUtf8("upgrade");
        HTTP_2_SKIPPED_REQUEST_HEADERS = Util.immutableList(Http2Codec.CONNECTION, Http2Codec.HOST, Http2Codec.KEEP_ALIVE, Http2Codec.PROXY_CONNECTION, Http2Codec.TE, Http2Codec.TRANSFER_ENCODING, Http2Codec.ENCODING, Http2Codec.UPGRADE, Header.TARGET_METHOD, Header.TARGET_PATH, Header.TARGET_SCHEME, Header.TARGET_AUTHORITY);
        HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableList(Http2Codec.CONNECTION, Http2Codec.HOST, Http2Codec.KEEP_ALIVE, Http2Codec.PROXY_CONNECTION, Http2Codec.TE, Http2Codec.TRANSFER_ENCODING, Http2Codec.ENCODING, Http2Codec.UPGRADE);
    }
    
    public Http2Codec(final OkHttpClient client, final StreamAllocation streamAllocation, final Http2Connection connection) {
        this.client = client;
        this.streamAllocation = streamAllocation;
        this.connection = connection;
    }
    
    public static List<Header> http2HeadersList(final Request request) {
        int i = 0;
        final Headers headers = request.headers();
        final ArrayList list = new ArrayList(headers.size() + 4);
        list.add((Object)new Header(Header.TARGET_METHOD, request.method()));
        list.add((Object)new Header(Header.TARGET_PATH, RequestLine.requestPath(request.url())));
        list.add((Object)new Header(Header.TARGET_AUTHORITY, Util.hostHeader(request.url(), false)));
        list.add((Object)new Header(Header.TARGET_SCHEME, request.url().scheme()));
        while (i < headers.size()) {
            final ByteString encodeUtf8 = ByteString.encodeUtf8(headers.name(i).toLowerCase(Locale.US));
            if (!Http2Codec.HTTP_2_SKIPPED_REQUEST_HEADERS.contains(encodeUtf8)) {
                list.add((Object)new Header(encodeUtf8, headers.value(i)));
            }
            ++i;
        }
        return (List<Header>)list;
    }
    
    public static Response.Builder readHttp2HeadersList(final List<Header> list) throws IOException {
        String str = null;
        final Headers.Builder builder = new Headers.Builder();
        String utf8;
        for (int size = list.size(), i = 0; i < size; ++i, str = utf8) {
            final ByteString name = list.get(i).name;
            utf8 = list.get(i).value.utf8();
            if (!name.equals(Header.RESPONSE_STATUS)) {
                if (Http2Codec.HTTP_2_SKIPPED_RESPONSE_HEADERS.contains(name)) {
                    utf8 = str;
                }
                else {
                    Internal.instance.addLenient(builder, name.utf8(), utf8);
                    utf8 = str;
                }
            }
        }
        if (str != null) {
            final StatusLine parse = StatusLine.parse("HTTP/1.1 " + str);
            return new Response.Builder().protocol(Protocol.HTTP_2).code(parse.code).message(parse.message).headers(builder.build());
        }
        throw new ProtocolException("Expected ':status' header not present");
    }
    
    @Override
    public void cancel() {
        if (this.stream != null) {
            this.stream.closeLater(ErrorCode.CANCEL);
        }
    }
    
    @Override
    public Sink createRequestBody(final Request request, final long n) {
        return this.stream.getSink();
    }
    
    @Override
    public void finishRequest() throws IOException {
        this.stream.getSink().close();
    }
    
    @Override
    public ResponseBody openResponseBody(final Response response) throws IOException {
        return new RealResponseBody(response.headers(), Okio.buffer(new StreamFinishingSource(this.stream.getSource())));
    }
    
    @Override
    public Response.Builder readResponseHeaders() throws IOException {
        return readHttp2HeadersList(this.stream.getResponseHeaders());
    }
    
    @Override
    public void writeRequestHeaders(final Request request) throws IOException {
        if (this.stream == null) {
            this.stream = this.connection.newStream(http2HeadersList(request), request.body() != null);
            this.stream.readTimeout().timeout(this.client.readTimeoutMillis(), TimeUnit.MILLISECONDS);
            this.stream.writeTimeout().timeout(this.client.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
    }
    
    class StreamFinishingSource extends ForwardingSource
    {
        public StreamFinishingSource(final Source source) {
            super(source);
        }
        
        @Override
        public void close() throws IOException {
            Http2Codec.this.streamAllocation.streamFinished(false, Http2Codec.this);
            super.close();
        }
    }
}
