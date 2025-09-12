// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.https:
//            SLHTTPSConnection

public class LLSDXMLRequest
{

    private static final MediaType MEDIA_TYPE_LLSD_XML = MediaType.parse("application/llsd+xml");
    private final AtomicReference callRef = new AtomicReference(null);

    public LLSDXMLRequest()
    {
    }

    public void InterruptRequest()
    {
        Call call;
        call = (Call)callRef.get();
        if (call == null)
        {
            break MISSING_BLOCK_LABEL_21;
        }
        call.cancel();
        return;
        Exception exception;
        exception;
        Debug.Warning(exception);
        return;
    }

    public LLSDNode PerformRequest(String s, LLSDNode llsdnode)
        throws IOException, LLSDXMLException
    {
        s = (new okhttp3.Request.Builder()).url(s);
        if (llsdnode != null)
        {
            s.post(RequestBody.create(MEDIA_TYPE_LLSD_XML, llsdnode.serializeToXML()));
        }
        s.header("Accept", "application/llsd+binary;q=0.5,application/llsd+xml;q=0.1");
        s = SLHTTPSConnection.getOkHttpClient().newCall(s.build());
        callRef.set(s);
        s = s.execute();
        if (s != null)
        {
            break MISSING_BLOCK_LABEL_91;
        }
        throw new IOException("Null response");
        s;
        callRef.set(null);
        throw s;
        if (!s.isSuccessful())
        {
            throw new IOException((new StringBuilder()).append("Unexpected code ").append(s.code()).toString());
        }
        break MISSING_BLOCK_LABEL_135;
        llsdnode;
        s.close();
        throw llsdnode;
        llsdnode = s.header("Content-Type", "unknown");
        llsdnode = LLSDNode.fromAny(s.body().byteStream(), llsdnode);
        s.close();
        callRef.set(null);
        return llsdnode;
    }

}
