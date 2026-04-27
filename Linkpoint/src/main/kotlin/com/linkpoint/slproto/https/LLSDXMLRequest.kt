package com.linkpoint.slproto.https

import androidx.core.os.EnvironmentCompat
import com.google.common.net.HttpHeaders
import com.linkpoint.Debug
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDXMLException
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class LLSDXMLRequest {
    private const val MediaType MEDIA_TYPE_LLSD_XML = MediaType.parse("application/llsd+xml")
    private val AtomicReference<Call> callRef = AtomicReference<>((Object) null)

    fun InterruptRequest() {
        val call: Call = this.callRef.get()
        if (call != null) {
            try {
                call.cancel()
            } catch (Exception e) {
                Debug.Warning(e)
            }
        }
    }

    public fun PerformRequest(str: String, lLSDNode: LLSDNode): LLSDNode throws IOException, LLSDXMLException {
        Response execute
        Request.Builder url = Request.Builder().url(str)
        if (lLSDNode != null) {
            url.post(RequestBody.create(MEDIA_TYPE_LLSD_XML, lLSDNode.serializeToXML()))
        }
        url.header(HttpHeaders.ACCEPT, "application/llsd+binary;q=0.5,application/llsd+xml;q=0.1")
        val newCall: Call = SLHTTPSConnection.getOkHttpClient().newCall(url.build())
        this.callRef.set(newCall)
        try {
            execute = newCall.execute()
            if (execute == null) {
                throw IOException("Null response")
            } else if (!execute.isSuccessful()) {
                throw IOException("Unexpected code " + execute.code())
            } else {
                val fromAny: LLSDNode = LLSDNode.fromAny(execute.body().byteStream(), execute.header(HttpHeaders.CONTENT_TYPE, EnvironmentCompat.MEDIA_UNKNOWN))
                execute.close()
                this.callRef.set((Object) null)
                return fromAny
            }
        } catch (Throwable th) {
            this.callRef.set((Object) null)
            throw th
        }
    }
}
