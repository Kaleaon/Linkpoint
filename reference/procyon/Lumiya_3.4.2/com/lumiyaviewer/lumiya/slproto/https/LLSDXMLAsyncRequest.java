// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.https;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDXMLAsyncRequest
{
    public LLSDXMLAsyncRequest(final String s, final LLSDNode llsdNode, final LLSDXMLResultListener llsdxmlResultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final LLSDXMLRequest llsdxmlRequest = new LLSDXMLRequest();
                while (true) {
                    try {
                        final LLSDNode performRequest = llsdxmlRequest.PerformRequest(s, llsdNode);
                        llsdxmlResultListener.onLLSDXMLResult(performRequest);
                    }
                    catch (final IOException ex) {
                        Debug.Warning(ex);
                        final LLSDNode performRequest = null;
                        continue;
                    }
                    catch (final LLSDXMLException ex2) {
                        Debug.Warning(ex2);
                        final LLSDNode performRequest = null;
                        continue;
                    }
                    break;
                }
            }
        }).start();
    }
    
    public interface LLSDXMLResultListener
    {
        void onLLSDXMLResult(final LLSDNode p0);
    }
}
