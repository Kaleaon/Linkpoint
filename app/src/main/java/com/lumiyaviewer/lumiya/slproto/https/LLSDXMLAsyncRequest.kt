package com.lumiyaviewer.lumiya.slproto.https
import java.util.*

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
import java.io.IOException

class LLSDXMLAsyncRequest {

    interface LLSDXMLResultListener {
        Unit onLLSDXMLResult(LLSDNode lLSDNode)
    }

    LLSDXMLAsyncRequest(String str, LLSDNode lLSDNode, LLSDXMLResultListener lLSDXMLResultListener) {
        Thread(Runnable() {
            Unit run() {
                LLSDNode lLSDNode
                try {
                    lLSDNode = LLSDXMLRequest().PerformRequest(str, lLSDNode)
                } catch (LLSDXMLException e) {
                    Debug.Warning(e)
                    lLSDNode = null
                } catch (IOException e2) {
                    Debug.Warning(e2)
                    lLSDNode = null
                }
                lLSDXMLResultListener.onLLSDXMLResult(lLSDNode)
            }
        }).start()
    }
}
