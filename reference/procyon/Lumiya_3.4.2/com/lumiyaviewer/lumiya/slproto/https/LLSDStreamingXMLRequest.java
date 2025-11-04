// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.https;

import okhttp3.Response;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;
import okhttp3.RequestBody;
import okhttp3.Request;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.Debug;
import okhttp3.Call;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.MediaType;

public class LLSDStreamingXMLRequest
{
    private static final MediaType MEDIA_TYPE_LLSD_XML;
    private final AtomicReference<Call> callRef;
    
    static {
        MEDIA_TYPE_LLSD_XML = MediaType.parse("application/llsd+xml");
    }
    
    public LLSDStreamingXMLRequest() {
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
    
    public void PerformRequest(String execute, final LLSDNode llsdNode, LLSDStreamingParser.LLSDContentHandler llsdContentHandler) throws IOException, LLSDXMLException {
        final Request.Builder header = new Request.Builder().url(execute).header("Accept", "application/llsd+binary;q=0.5,application/llsd+xml;q=0.1");
        if (llsdNode != null) {
            header.post(RequestBody.create(LLSDStreamingXMLRequest.MEDIA_TYPE_LLSD_XML, llsdNode.serializeToXML()));
        }
        final Call call = SLHTTPSConnection.getOkHttpClient().newCall(header.build());
        this.callRef.set(call);
        try {
            execute = (String)call.execute();
            if (execute == null) {
                throw new IOException("Null response");
            }
        }
        catch (final IOException ex) {
            this.callRef.set(null);
            throw ex;
        }
        catch (final LLSDXMLException ex2) {
            this.callRef.set(null);
            throw ex2;
        }
        catch (final Exception cause) {
            this.callRef.set(null);
            throw new IOException(cause.getMessage(), cause);
        }
        try {
            if (!((Response)execute).isSuccessful()) {
                llsdContentHandler = (LLSDStreamingParser.LLSDContentHandler)new StringBuilder();
                throw new IOException(((StringBuilder)llsdContentHandler).append("Error response: ").append(((Response)execute).code()).toString());
            }
            goto Label_0158;
        }
        finally {}
    }
}
