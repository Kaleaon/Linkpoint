package com.lumiyaviewer.lumiya.slproto.https;

import android.support.v4.os.EnvironmentCompat;
import com.google.common.net.HttpHeaders;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LLSDStreamingXMLRequest {
    private static final MediaType MEDIA_TYPE_LLSD_XML = MediaType.parse("application/llsd+xml");
    private final AtomicReference<Call> callRef = new AtomicReference<>((Object) null);

    public void InterruptRequest() {
        Call call = this.callRef.get();
        if (call != null) {
            try {
                call.cancel();
            } catch (Exception e) {
                Debug.Warning(e);
            }
        }
    }

    public void PerformRequest(String str, LLSDNode lLSDNode, LLSDStreamingParser.LLSDContentHandler lLSDContentHandler) throws IOException, LLSDXMLException {
        Response execute;
        Request.Builder header = new Request.Builder().url(str).header(HttpHeaders.ACCEPT, "application/llsd+binary;q=0.5,application/llsd+xml;q=0.1");
        if (lLSDNode != null) {
            header.post(RequestBody.create(MEDIA_TYPE_LLSD_XML, lLSDNode.serializeToXML()));
        }
        Call newCall = SLHTTPSConnection.getOkHttpClient().newCall(header.build());
        this.callRef.set(newCall);
        try {
            execute = newCall.execute();
            if (execute == null) {
                throw new IOException("Null response");
            } else if (!execute.isSuccessful()) {
                throw new IOException("Error response: " + execute.code());
            } else {
                LLSDStreamingParser.parseAny(execute.body().byteStream(), execute.header(HttpHeaders.CONTENT_TYPE, EnvironmentCompat.MEDIA_UNKNOWN), lLSDContentHandler);
                execute.close();
                this.callRef.set((Object) null);
            }
        } catch (IOException e) {
            this.callRef.set((Object) null);
            throw e;
        } catch (LLSDXMLException e2) {
            this.callRef.set((Object) null);
            throw e2;
        } catch (Exception e3) {
            this.callRef.set((Object) null);
            throw new IOException(e3.getMessage(), e3);
        } catch (Throwable th) {
            execute.close();
            this.callRef.set((Object) null);
            throw th;
        }
    }
}
