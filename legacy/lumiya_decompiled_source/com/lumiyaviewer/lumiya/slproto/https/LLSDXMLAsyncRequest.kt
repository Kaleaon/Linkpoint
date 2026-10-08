package com.lumiyaviewer.lumiya.slproto.https

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
import java.io.IOException

class LLSDXMLAsyncRequest(
    url: String,
    node: LLSDNode?,
    listener: LLSDXMLResultListener
) {
    interface LLSDXMLResultListener {
        fun onLLSDXMLResult(node: LLSDNode?)
    }

    init {
        Thread {
            val result = try {
                LLSDXMLRequest().PerformRequest(url, node)
            } catch (e: LLSDXMLException) {
                Debug.Warning(e)
                null
            } catch (e: IOException) {
                Debug.Warning(e)
                null
            }
            listener.onLLSDXMLResult(result)
        }.start()
    }
}
