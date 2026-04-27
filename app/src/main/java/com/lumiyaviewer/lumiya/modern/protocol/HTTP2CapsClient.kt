package com.lumiyaviewer.lumiya.modern.protocol

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.buffer
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Modern HTTP/2 CAPS client for Second Life protocol
 * Based on LibreMetaverse patterns with mobile optimization
 */
class HTTP2CapsClient {
    
    private val TAG = "HTTP2CapsClient"
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val LLSD_XML = "application/llsd+xml; charset=utf-8".toMediaType()
    
    private val client: OkHttpClient
    private var authToken: String? = null
    private val capabilities = ConcurrentHashMap<String, String>()
    
    init {
        client = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(AuthenticationInterceptor())
            .build()
    }
    
    /**
     * Set authentication token
     */
    fun setAuthToken(token: String) {
        authToken = token
    }
    
    /**
     * Configure capability URLs from seed capability response
     */
    fun configureCapabilities(capabilitiesMap: Map<String, String>) {
        capabilities.clear()
        capabilities.putAll(capabilitiesMap)
        Log.i(TAG, "Configured ${capabilities.size} capabilities")
        
        // Log available capabilities for debugging
        capabilities.forEach { (name, url) ->
            Log.d(TAG, "Capability: $name -> $url")
        }
    }
    
    /**
     * Get capability URL by name
     */
    fun getCapabilityUrl(capabilityName: String): String? {
        return capabilities[capabilityName]
    }
    
    /**
     * Send async CAPS request with modern error handling
     */
    fun sendAsync(capUrl: String, llsdData: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        
        val body = llsdData.toRequestBody(LLSD_XML)
        val request = Request.Builder()
            .url(capUrl)
            .post(body)
            .addHeader("User-Agent", "Lumiya/3.4.3 (Android)")
            .addHeader("Accept", "application/llsd+xml")
            .build()
            
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "CAPS request failed for $capUrl", e)
                future.completeExceptionally(e)
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.body?.use { responseBody ->
                    if (response.isSuccessful) {
                        val result = responseBody.string()
                        Log.d(TAG, "CAPS response received: ${result.length} bytes")
                        future.complete(result)
                    } else {
                        future.completeExceptionally(
                            IOException("CAPS request failed: ${response.code} ${response.message}")
                        )
                    }
                } ?: future.completeExceptionally(IOException("Empty response body"))
            }
        })
        
        return future
    }
    
    /**
     * Asset upload with progress monitoring
     */
    fun uploadAssetAsync(
        uploadUrl: String,
        assetData: ByteArray,
        contentType: String,
        progressListener: ProgressListener?
    ): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        
        val baseBody = assetData.toRequestBody(contentType.toMediaType())
        val body = progressListener?.let { 
            ProgressRequestBody(baseBody, it)
        } ?: baseBody
        
        val request = Request.Builder()
            .url(uploadUrl)
            .post(body)
            .addHeader("User-Agent", "Lumiya/3.4.3 (Android)")
            .build()
            
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Asset upload failed", e)
                future.completeExceptionally(e)
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.body?.use { responseBody ->
                    if (response.isSuccessful) {
                        future.complete(responseBody.string())
                    } else {
                        future.completeExceptionally(
                            IOException("Asset upload failed: ${response.code}")
                        )
                    }
                } ?: future.completeExceptionally(IOException("Empty response body"))
            }
        })
        
        return future
    }
    
    /**
     * Authentication interceptor for CAPS requests
     */
    private inner class AuthenticationInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            
            val authenticated = authToken?.let { token ->
                original.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } ?: original
            
            return chain.proceed(authenticated)
        }
    }
    
    /**
     * Progress tracking request body wrapper
     */
    private class ProgressRequestBody(
        private val delegate: RequestBody,
        private val listener: ProgressListener
    ) : RequestBody() {
        
        override fun contentType(): MediaType? = delegate.contentType()
        
        override fun contentLength(): Long = try {
            delegate.contentLength()
        } catch (e: IOException) {
            -1
        }
        
        override fun writeTo(sink: BufferedSink) {
            val forwardingSink = object : ForwardingSink(sink) {
                var bytesWritten = 0L
                var contentLength = 0L
                
                override fun write(source: Buffer, byteCount: Long) {
                    super.write(source, byteCount)
                    
                    if (contentLength == 0L) {
                        contentLength = contentLength()
                    }
                    
                    bytesWritten += byteCount
                    listener.onProgress(bytesWritten, contentLength)
                }
            }
            
            val bufferedSink = forwardingSink.buffer()
            delegate.writeTo(bufferedSink)
            bufferedSink.close()
        }
    }
    
    /**
     * Progress callback interface
     */
    fun interface ProgressListener {
        fun onProgress(bytesWritten: Long, contentLength: Long)
    }
    
    /**
     * Shutdown the client and cleanup resources
     */
    fun shutdown() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}
