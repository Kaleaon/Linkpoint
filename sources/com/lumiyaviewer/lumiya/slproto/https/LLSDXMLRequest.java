package com.lumiyaviewer.lumiya.slproto.https;

import android.support.v4.os.EnvironmentCompat;
import com.google.common.net.HttpHeaders;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LLSDXMLRequest {
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

    public LLSDNode PerformRequest(String str, LLSDNode lLSDNode) throws IOException, LLSDXMLException {
        Response execute;
        Request.Builder url = new Request.Builder().url(str);
        if (lLSDNode != null) {
            url.post(RequestBody.create(MEDIA_TYPE_LLSD_XML, lLSDNode.serializeToXML()));
        }
        url.header(HttpHeaders.ACCEPT, "application/llsd+binary;q=0.5,application/llsd+xml;q=0.1");
        Call newCall = SLHTTPSConnection.getOkHttpClient().newCall(url.build());
        this.callRef.set(newCall);
        try {
            execute = newCall.execute();
            if (execute == null) {
                throw new IOException("Null response");
            } else if (!execute.isSuccessful()) {
                throw new IOException("Unexpected code " + execute.code());
            } else {
                LLSDNode fromAny = LLSDNode.fromAny(execute.body().byteStream(), execute.header(HttpHeaders.CONTENT_TYPE, EnvironmentCompat.MEDIA_UNKNOWN));
                execute.close();
                this.callRef.set((Object) null);
                return fromAny;
            }
        } catch (Throwable th) {
            this.callRef.set((Object) null);
            throw th;
        }
    }
}
