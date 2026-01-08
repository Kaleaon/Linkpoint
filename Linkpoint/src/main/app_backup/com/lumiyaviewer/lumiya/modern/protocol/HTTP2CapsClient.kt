package com.lumiyaviewer.lumiya.modern.protocol
import java.util.concurrent.CompletableFuture

class HTTP2CapsClient {
    fun setAuthToken(token: String) {}
    fun configureCapabilities(caps: Map<String, String>) {}
    fun sendAsync(url: String, body: String): CompletableFuture<String> {
        return CompletableFuture.completedFuture("")
    }
    fun uploadAssetAsync(url: String, data: ByteArray, contentType: String, listener: Any?): CompletableFuture<String> {
        return CompletableFuture.completedFuture("")
    }
    fun getCapability(name: String): String? = null
    fun shutdown() {}
    
    interface ProgressListener {
        fun onProgress(uploaded: Long, total: Long)
    }
}
