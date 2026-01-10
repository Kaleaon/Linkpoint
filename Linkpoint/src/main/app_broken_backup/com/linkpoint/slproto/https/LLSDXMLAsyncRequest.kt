package com.linkpoint.slproto.https
import java.util.*

import com.linkpoint.Debug
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDXMLException
import java.io.IOException

class LLSDXMLAsyncRequest {

    interface LLSDXMLResultListener {
        fun onLLSDXMLResult(LLSDNode lLSDNode)
    }

    LLSDXMLAsyncRequest(String str, LLSDNode lLSDNode, LLSDXMLResultListener lLSDXMLResultListener) {
        Thread(Runnable() {
            fun run()  {
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
