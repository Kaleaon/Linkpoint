// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.util.ArrayList;
import java.util.UUID;
import java.io.IOException;
import okio.Buffer;
import okio.BufferedSink;
import okhttp3.internal.Util;
import java.util.List;
import okio.ByteString;

public final class MultipartBody extends RequestBody
{
    public static final MediaType ALTERNATIVE;
    private static final byte[] COLONSPACE;
    private static final byte[] CRLF;
    private static final byte[] DASHDASH;
    public static final MediaType DIGEST;
    public static final MediaType FORM;
    public static final MediaType MIXED;
    public static final MediaType PARALLEL;
    private final ByteString boundary;
    private long contentLength;
    private final MediaType contentType;
    private final MediaType originalType;
    private final List<Part> parts;
    
    static {
        MIXED = MediaType.parse("multipart/mixed");
        ALTERNATIVE = MediaType.parse("multipart/alternative");
        DIGEST = MediaType.parse("multipart/digest");
        PARALLEL = MediaType.parse("multipart/parallel");
        FORM = MediaType.parse("multipart/form-data");
        COLONSPACE = new byte[] { 58, 32 };
        CRLF = new byte[] { 13, 10 };
        DASHDASH = new byte[] { 45, 45 };
    }
    
    MultipartBody(final ByteString boundary, final MediaType mediaType, final List<Part> list) {
        this.contentLength = -1L;
        this.boundary = boundary;
        this.originalType = mediaType;
        this.contentType = MediaType.parse(mediaType + "; boundary=" + boundary.utf8());
        this.parts = Util.immutableList(list);
    }
    
    static StringBuilder appendQuotedString(final StringBuilder sb, final String s) {
        sb.append('\"');
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                default: {
                    sb.append(char1);
                    break;
                }
                case 10: {
                    sb.append("%0A");
                    break;
                }
                case 13: {
                    sb.append("%0D");
                    break;
                }
                case 34: {
                    sb.append("%22");
                    break;
                }
            }
        }
        sb.append('\"');
        return sb;
    }
    
    private long writeOrCountBytes(final BufferedSink bufferedSink, final boolean b) throws IOException {
        long n = 0L;
        BufferedSink bufferedSink2;
        Buffer buffer2;
        if (!b) {
            final Buffer buffer = null;
            bufferedSink2 = bufferedSink;
            buffer2 = buffer;
        }
        else {
            bufferedSink2 = (buffer2 = new Buffer());
        }
        for (int size = this.parts.size(), i = 0; i < size; ++i) {
            final Part part = this.parts.get(i);
            final Headers headers = part.headers;
            final RequestBody body = part.body;
            bufferedSink2.write(MultipartBody.DASHDASH);
            bufferedSink2.write(this.boundary);
            bufferedSink2.write(MultipartBody.CRLF);
            if (headers != null) {
                for (int j = 0; j < headers.size(); ++j) {
                    bufferedSink2.writeUtf8(headers.name(j)).write(MultipartBody.COLONSPACE).writeUtf8(headers.value(j)).write(MultipartBody.CRLF);
                }
            }
            final MediaType contentType = body.contentType();
            if (contentType != null) {
                bufferedSink2.writeUtf8("Content-Type: ").writeUtf8(contentType.toString()).write(MultipartBody.CRLF);
            }
            final long contentLength = body.contentLength();
            if (contentLength != -1L) {
                bufferedSink2.writeUtf8("Content-Length: ").writeDecimalLong(contentLength).write(MultipartBody.CRLF);
            }
            else if (b) {
                buffer2.clear();
                return -1L;
            }
            bufferedSink2.write(MultipartBody.CRLF);
            if (!b) {
                body.writeTo(bufferedSink2);
            }
            else {
                n += contentLength;
            }
            bufferedSink2.write(MultipartBody.CRLF);
        }
        bufferedSink2.write(MultipartBody.DASHDASH);
        bufferedSink2.write(this.boundary);
        bufferedSink2.write(MultipartBody.DASHDASH);
        bufferedSink2.write(MultipartBody.CRLF);
        if (b) {
            n += buffer2.size();
            buffer2.clear();
        }
        return n;
    }
    
    public String boundary() {
        return this.boundary.utf8();
    }
    
    @Override
    public long contentLength() throws IOException {
        final long contentLength = this.contentLength;
        if (contentLength != -1L) {
            return contentLength;
        }
        return this.contentLength = this.writeOrCountBytes(null, true);
    }
    
    @Override
    public MediaType contentType() {
        return this.contentType;
    }
    
    public Part part(final int n) {
        return this.parts.get(n);
    }
    
    public List<Part> parts() {
        return this.parts;
    }
    
    public int size() {
        return this.parts.size();
    }
    
    public MediaType type() {
        return this.originalType;
    }
    
    @Override
    public void writeTo(final BufferedSink bufferedSink) throws IOException {
        this.writeOrCountBytes(bufferedSink, false);
    }
    
    public static final class Builder
    {
        private final ByteString boundary;
        private final List<Part> parts;
        private MediaType type;
        
        public Builder() {
            this(UUID.randomUUID().toString());
        }
        
        public Builder(final String s) {
            this.type = MultipartBody.MIXED;
            this.parts = new ArrayList<Part>();
            this.boundary = ByteString.encodeUtf8(s);
        }
        
        public Builder addFormDataPart(final String s, final String s2) {
            return this.addPart(Part.createFormData(s, s2));
        }
        
        public Builder addFormDataPart(final String s, final String s2, final RequestBody requestBody) {
            return this.addPart(Part.createFormData(s, s2, requestBody));
        }
        
        public Builder addPart(final Headers headers, final RequestBody requestBody) {
            return this.addPart(Part.create(headers, requestBody));
        }
        
        public Builder addPart(final Part part) {
            if (part != null) {
                this.parts.add(part);
                return this;
            }
            throw new NullPointerException("part == null");
        }
        
        public Builder addPart(final RequestBody requestBody) {
            return this.addPart(Part.create(requestBody));
        }
        
        public MultipartBody build() {
            if (!this.parts.isEmpty()) {
                return new MultipartBody(this.boundary, this.type, this.parts);
            }
            throw new IllegalStateException("Multipart body must have at least one part.");
        }
        
        public Builder setType(final MediaType mediaType) {
            if (mediaType == null) {
                throw new NullPointerException("type == null");
            }
            if (mediaType.type().equals("multipart")) {
                this.type = mediaType;
                return this;
            }
            throw new IllegalArgumentException("multipart != " + mediaType);
        }
    }
    
    public static final class Part
    {
        final RequestBody body;
        final Headers headers;
        
        private Part(final Headers headers, final RequestBody body) {
            this.headers = headers;
            this.body = body;
        }
        
        public static Part create(final Headers headers, final RequestBody requestBody) {
            if (requestBody == null) {
                throw new NullPointerException("body == null");
            }
            if (headers != null && headers.get("Content-Type") != null) {
                throw new IllegalArgumentException("Unexpected header: Content-Type");
            }
            if (headers != null && headers.get("Content-Length") != null) {
                throw new IllegalArgumentException("Unexpected header: Content-Length");
            }
            return new Part(headers, requestBody);
        }
        
        public static Part create(final RequestBody requestBody) {
            return create(null, requestBody);
        }
        
        public static Part createFormData(final String s, final String s2) {
            return createFormData(s, null, RequestBody.create(null, s2));
        }
        
        public static Part createFormData(final String s, final String s2, final RequestBody requestBody) {
            if (s != null) {
                final StringBuilder sb = new StringBuilder("form-data; name=");
                MultipartBody.appendQuotedString(sb, s);
                if (s2 != null) {
                    sb.append("; filename=");
                    MultipartBody.appendQuotedString(sb, s2);
                }
                return create(Headers.of("Content-Disposition", sb.toString()), requestBody);
            }
            throw new NullPointerException("name == null");
        }
        
        public RequestBody body() {
            return this.body;
        }
        
        public Headers headers() {
            return this.headers;
        }
    }
}
