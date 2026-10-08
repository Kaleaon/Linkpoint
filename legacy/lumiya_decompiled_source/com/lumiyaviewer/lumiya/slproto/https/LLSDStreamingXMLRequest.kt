package com.lumiyaviewer.lumiya.slproto.https

import android.support.v4.os.EnvironmentCompat
import com.google.common.net.HttpHeaders
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class LLSDStreamingXMLRequest {
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
    fun PerformRequest(url: String, node: LLSDNode?, handler: LLSDStreamingParser.LLSDContentHandler) {
        val requestBuilder = Request.Builder()
            .url(url)
            .header(HttpHeaders.ACCEPT, "application/llsd+binary;q=0.5,application/llsd+xml;q=0.1")
        if (node != null) {
            requestBuilder.post(RequestBody.create(MEDIA_TYPE_LLSD_XML, node.serializeToXML()))
        }
        val call = SLHTTPSConnection.getOkHttpClient().newCall(requestBuilder.build())
        callRef.set(call)
        try {
            val response: Response = call.execute()
            if (response == null) {
                throw IOException("Null response")
            }
            try {
                if (!response.isSuccessful) {
                    throw IOException("Error response: ${response.code()}")
                }
                LLSDStreamingParser.parseAny(
                    response.body().byteStream(),
                    response.header(HttpHeaders.CONTENT_TYPE, EnvironmentCompat.MEDIA_UNKNOWN),
                    handler
                )
            } finally {
                response.close()
                callRef.set(null)
            }
        } catch (e: LLSDXMLException) {
            callRef.set(null)
            throw e
        } catch (e: IOException) {
            callRef.set(null)
            throw e
        } catch (e: Exception) {
            callRef.set(null)
            throw IOException(e.message, e)
        }
    }

    companion object {
        private val MEDIA_TYPE_LLSD_XML: MediaType? = MediaType.parse("application/llsd+xml")
    }
}
