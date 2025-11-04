// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.util.ArrayList;
import java.io.IOException;
import okio.Buffer;
import okio.BufferedSink;
import okhttp3.internal.Util;
import java.util.List;

public final class FormBody extends RequestBody
{
    private static final MediaType CONTENT_TYPE;
    private final List<String> encodedNames;
    private final List<String> encodedValues;
    
    static {
        CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");
    }
    
    FormBody(final List<String> list, final List<String> list2) {
        this.encodedNames = Util.immutableList(list);
        this.encodedValues = Util.immutableList(list2);
    }
    
    private long writeOrCountBytes(final BufferedSink bufferedSink, final boolean b) {
        int i = 0;
        Buffer buffer;
        if (!b) {
            buffer = bufferedSink.buffer();
        }
        else {
            buffer = new Buffer();
        }
        while (i < this.encodedNames.size()) {
            if (i > 0) {
                buffer.writeByte(38);
            }
            buffer.writeUtf8((String)this.encodedNames.get(i));
            buffer.writeByte(61);
            buffer.writeUtf8((String)this.encodedValues.get(i));
            ++i;
        }
        long size;
        if (!b) {
            size = 0L;
        }
        else {
            size = buffer.size();
            buffer.clear();
        }
        return size;
    }
    
    @Override
    public long contentLength() {
        return this.writeOrCountBytes(null, true);
    }
    
    @Override
    public MediaType contentType() {
        return FormBody.CONTENT_TYPE;
    }
    
    public String encodedName(final int n) {
        return this.encodedNames.get(n);
    }
    
    public String encodedValue(final int n) {
        return this.encodedValues.get(n);
    }
    
    public String name(final int n) {
        return HttpUrl.percentDecode(this.encodedName(n), true);
    }
    
    public int size() {
        return this.encodedNames.size();
    }
    
    public String value(final int n) {
        return HttpUrl.percentDecode(this.encodedValue(n), true);
    }
    
    @Override
    public void writeTo(final BufferedSink bufferedSink) throws IOException {
        this.writeOrCountBytes(bufferedSink, false);
    }
    
    public static final class Builder
    {
        private final List<String> names;
        private final List<String> values;
        
        public Builder() {
            this.names = new ArrayList<String>();
            this.values = new ArrayList<String>();
        }
        
        public Builder add(final String s, final String s2) {
            this.names.add(HttpUrl.canonicalize(s, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, true));
            this.values.add(HttpUrl.canonicalize(s2, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, true));
            return this;
        }
        
        public Builder addEncoded(final String s, final String s2) {
            this.names.add(HttpUrl.canonicalize(s, " \"':;<=>@[]^`{}|/\\?#&!$(),~", true, false, true, true));
            this.values.add(HttpUrl.canonicalize(s2, " \"':;<=>@[]^`{}|/\\?#&!$(),~", true, false, true, true));
            return this;
        }
        
        public FormBody build() {
            return new FormBody(this.names, this.values);
        }
    }
}
