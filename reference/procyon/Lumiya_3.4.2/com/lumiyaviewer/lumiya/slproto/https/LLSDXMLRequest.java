// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.https;

import okhttp3.Response;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;
import okhttp3.RequestBody;
import okhttp3.Request;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.Debug;
import okhttp3.Call;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.MediaType;

public class LLSDXMLRequest
{
    private static final MediaType MEDIA_TYPE_LLSD_XML;
    private final AtomicReference<Call> callRef;
    
    static {
        MEDIA_TYPE_LLSD_XML = MediaType.parse("application/llsd+xml");
    }
    
    public LLSDXMLRequest() {
        this.callRef = new AtomicReference<Call>(null);
    }
    
    public void InterruptRequest() {
        final Call call = this.callRef.get();
        if (call == null) {
            return;
        }
        try {
            call.cancel();
        }
        catch (final Exception ex) {
            Debug.Warning(ex);
        }
    }
    
    public LLSDNode PerformRequest(final String s, LLSDNode fromAny) throws IOException, LLSDXMLException {
        final Request.Builder url = new Request.Builder().url(s);
        if (fromAny != null) {
            url.post(RequestBody.create(LLSDXMLRequest.MEDIA_TYPE_LLSD_XML, fromAny.serializeToXML()));
        }
        url.header("Accept", "application/llsd+binary;q=0.5,application/llsd+xml;q=0.1");
        final Call call = SLHTTPSConnection.getOkHttpClient().newCall(url.build());
        this.callRef.set(call);
        try {
            if (call.execute() == null) {
                throw new IOException("Null response");
            }
        }
        finally {
            this.callRef.set(null);
        }
        try {
            if (!((Response)s).isSuccessful()) {
                throw new IOException("Unexpected code " + ((Response)s).code());
            }
        }
        finally {
            ((Response)s).close();
        }
        fromAny = LLSDNode.fromAny(((Response)s).body().byteStream(), ((Response)s).header("Content-Type", "unknown"));
        ((Response)s).close();
        this.callRef.set(null);
        return fromAny;
    }
}
