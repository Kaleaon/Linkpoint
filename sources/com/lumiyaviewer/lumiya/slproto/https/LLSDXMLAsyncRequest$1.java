// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.https:
//            LLSDXMLAsyncRequest, LLSDXMLRequest

class SDXMLResultListener
    implements Runnable
{

    final LLSDXMLAsyncRequest this$0;
    final LLSDNode val$request;
    final SDXMLResultListener val$resultListener;
    final String val$url;

    public void run()
    {
        Object obj = new LLSDXMLRequest();
        try
        {
            obj = ((LLSDXMLRequest) (obj)).PerformRequest(val$url, val$request);
        }
        catch (LLSDXMLException llsdxmlexception)
        {
            Debug.Warning(llsdxmlexception);
            llsdxmlexception = null;
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            Debug.Warning(((Throwable) (obj)));
            obj = null;
        }
        val$resultListener.onLLSDXMLResult(((LLSDNode) (obj)));
    }

    SDXMLResultListener()
    {
        this$0 = final_llsdxmlasyncrequest;
        val$url = s;
        val$request = llsdnode;
        val$resultListener = SDXMLResultListener.this;
        super();
    }
}
