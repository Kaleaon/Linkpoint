package com.lumiyaviewer.lumiya.slproto.https

import android.support.v4.os.EnvironmentCompat
import com.google.common.net.HttpHeaders
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class LLSDXMLRequest {
    private val callRef: AtomicReference<Call?> = AtomicReference(null)

    fun InterruptRequest() {
        val call = callRef.get()
        if (call != null) {
            try {
                call.cancel()
            } catch (e: Exception) {
                Debug.Warning(e)
            }
        }
    }

    @Throws(IOException::class, LLSDXMLException::class)
    fun PerformRequest(url: String, node: LLSDNode?): LLSDNode {
        val requestBuilder = Request.Builder().url(url)
        if (node != null) {
            requestBuilder.post(RequestBody.create(MEDIA_TYPE_LLSD_XML, node.serializeToXML()))
        }
        requestBuilder.header(HttpHeaders.ACCEPT, "application/llsd+binary;q=0.5,application/llsd+xml;q=0.1")
        val call = SLHTTPSConnection.getOkHttpClient().newCall(requestBuilder.build())
        callRef.set(call)
        try {
            val response: Response = call.execute()
            if (response == null) {
                throw IOException("Null response")
            }
            if (response.isSuccessful) {
                val result = LLSDNode.fromAny(
                    response.body().byteStream(),
                    response.header(HttpHeaders.CONTENT_TYPE, EnvironmentCompat.MEDIA_UNKNOWN)
                )
                response.close()
                return result
            }
            throw IOException("Unexpected code ${response.code()}")
        } finally {
            callRef.set(null)
        }
    }

    companion object {
        private val MEDIA_TYPE_LLSD_XML: MediaType? = MediaType.parse("application/llsd+xml")
    }
}
