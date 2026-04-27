// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser;
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

public class LLSDStreamingXMLRequest
{

    private static final MediaType MEDIA_TYPE_LLSD_XML = MediaType.parse("application/llsd+xml");
    private final AtomicReference callRef = new AtomicReference(null);

    public LLSDStreamingXMLRequest()
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

    public void PerformRequest(String s, LLSDNode llsdnode, com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler llsdcontenthandler)
        throws IOException, LLSDXMLException
    {
        s = (new okhttp3.Request.Builder()).url(s).header("Accept", "application/llsd+binary;q=0.5,application/llsd+xml;q=0.1");
        if (llsdnode != null)
        {
            s.post(RequestBody.create(MEDIA_TYPE_LLSD_XML, llsdnode.serializeToXML()));
        }
        s = SLHTTPSConnection.getOkHttpClient().newCall(s.build());
        callRef.set(s);
        s = s.execute();
        if (s == null)
        {
            try
            {
                throw new IOException("Null response");
            }
            // Misplaced declaration of an exception variable
            catch (String s)
            {
                callRef.set(null);
                throw s;
            }
            // Misplaced declaration of an exception variable
            catch (String s)
            {
                callRef.set(null);
                throw s;
            }
            // Misplaced declaration of an exception variable
            catch (String s)
            {
                callRef.set(null);
            }
            break MISSING_BLOCK_LABEL_195;
        }
        if (!s.isSuccessful())
        {
            throw new IOException((new StringBuilder()).append("Error response: ").append(s.code()).toString());
        }
        break MISSING_BLOCK_LABEL_152;
        llsdnode;
        s.close();
        callRef.set(null);
        throw llsdnode;
        llsdnode = s.header("Content-Type", "unknown");
        LLSDStreamingParser.parseAny(s.body().byteStream(), llsdnode, llsdcontenthandler);
        s.close();
        callRef.set(null);
        return;
        throw new IOException(s.getMessage(), s);
    }

}
