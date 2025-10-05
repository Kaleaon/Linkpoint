package com.linkpoint.slproto.https

import com.linkpoint.Debug
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDXMLException
import java.io.IOException
import kotlin.concurrent.thread

class LLSDXMLAsyncRequest(
    url: String,
    request: LLSDNode,
    resultListener: LLSDXMLResultListener
) {
    interface LLSDXMLResultListener {
        fun onLLSDXMLResult(result: LLSDNode?)
    }

    init {
        thread {
            val result = try {
                LLSDXMLRequest().PerformRequest(url, request)
            } catch (e: LLSDXMLException) {
                Debug.Warning(e)
                null
            } catch (e: IOException) {
                Debug.Warning(e)
                null
            }
            resultListener.onLLSDXMLResult(result)
        }
    }
}