package com.lumiyaviewer.lumiya.slproto.https;
import java.util.*;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;

public class LLSDXMLAsyncRequest {

    public interface LLSDXMLResultListener {
        void onLLSDXMLResult(LLSDNode lLSDNode);
    }

    public LLSDXMLAsyncRequest(final String str, final LLSDNode lLSDNode, final LLSDXMLResultListener lLSDXMLResultListener) {
        new Thread(new Runnable() {
            public void run() {
                LLSDNode lLSDNode;
                try {
                    lLSDNode = new LLSDXMLRequest().PerformRequest(str, lLSDNode);
                } catch (LLSDXMLException e) {
                    Debug.Warning(e);
                    lLSDNode = null;
                } catch (IOException e2) {
                    Debug.Warning(e2);
                    lLSDNode = null;
                }
                lLSDXMLResultListener.onLLSDXMLResult(lLSDNode);
            }
        }).start();
    }
}
