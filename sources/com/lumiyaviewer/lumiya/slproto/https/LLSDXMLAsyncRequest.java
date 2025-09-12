// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.https:
//            LLSDXMLRequest

public class LLSDXMLAsyncRequest
{
    public static interface LLSDXMLResultListener
    {

        public abstract void onLLSDXMLResult(LLSDNode llsdnode);
    }


    public LLSDXMLAsyncRequest(final String url, final LLSDNode request, final LLSDXMLResultListener resultListener)
    {
        (new Thread(new Runnable() {

            final LLSDXMLAsyncRequest this$0;
            final LLSDNode val$request;
            final LLSDXMLResultListener val$resultListener;
            final String val$url;

            public void run()
            {
                Object obj = new LLSDXMLRequest();
                try
                {
                    obj = ((LLSDXMLRequest) (obj)).PerformRequest(url, request);
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
                resultListener.onLLSDXMLResult(((LLSDNode) (obj)));
            }

            
            {
                this$0 = LLSDXMLAsyncRequest.this;
                url = s;
                request = llsdnode;
                resultListener = llsdxmlresultlistener;
                super();
            }
        })).start();
    }
}
