package com.lumiyaviewer.lumiya.slproto.https;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;

/* loaded from: classes.dex */
public class LLSDXMLAsyncRequest {

    /* loaded from: classes.dex */
    public interface LLSDXMLResultListener {
        void onLLSDXMLResult(LLSDNode lLSDNode);
    }

    public LLSDXMLAsyncRequest(final String str, final LLSDNode lLSDNode, final LLSDXMLResultListener lLSDXMLResultListener) {
        new Thread(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest.1
            @Override // java.lang.Runnable
            public void run() {
                LLSDNode lLSDNode2;
                try {
                    lLSDNode2 = new LLSDXMLRequest().PerformRequest(str, lLSDNode);
                } catch (LLSDXMLException e) {
                    Debug.Warning(e);
                    lLSDNode2 = null;
                } catch (IOException e2) {
                    Debug.Warning(e2);
                    lLSDNode2 = null;
                }
                lLSDXMLResultListener.onLLSDXMLResult(lLSDNode2);
            }
        }).start();
    }
}
