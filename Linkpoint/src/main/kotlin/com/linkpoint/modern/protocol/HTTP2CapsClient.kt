package com.linkpoint.modern.protocol

import android.util.Log
import okhttp3.*
import okhttp3.MediaType
import okhttp3.RequestBody

import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.Map
import java.util.concurrent.ConcurrentHashMap

/**
 * Modern HTTP/2 CAPS client for Second Life protocol
 * Based on LibreMetaverse patterns with mobile optimization
 */
class HTTP2CapsClient {
    private const val String TAG = "HTTP2CapsClient"
    private const val MediaType JSON = MediaType.get("application/json; charset=utf-8")
    private const val MediaType LLSD_XML = MediaType.get("application/llsd+xml; charset=utf-8")
    
    private val OkHttpClient client
    private String authToken
    private val Map<String, String> capabilities = ConcurrentHashMap<>()
    
    public HTTP2CapsClient() {
        this.client = OkHttpClient.Builder()
            .protocols(java.util.Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(AuthenticationInterceptor())
            .build()
    }
    
    public Unit setAuthToken(String token) {
        this.authToken = token
    }
    
    /**
     * Configure capability URLs from seed capability response
     */
    public Unit configureCapabilities(Map<String, String> capabilitiesMap) {
        capabilities.clear()
        capabilities.putAll(capabilitiesMap)
        Log.i(TAG, "Configured " + capabilities.size() + " capabilities")
        
        // Log available capabilities for debugging
        for (Map.Entry<String, String> entry : capabilities.entrySet()) {
            Log.d(TAG, "Capability: " + entry.getKey() + " -> " + entry.getValue())
        }
    }
    
    /**
     * Get capability URL by name
     */
    public String getCapabilityUrl(String capabilityName) {
        return capabilities.get(capabilityName)
    }
    
    /**
     * Send async CAPS request with modern error handling
     */
    public CompletableFuture<String> sendAsync(String capUrl, String llsdData) {
        CompletableFuture<String> future = CompletableFuture<>()
        
        RequestBody body = RequestBody.create(llsdData, LLSD_XML)
        Request request = Request.Builder()
            .url(capUrl)
            .post(body)
            .addHeader("User-Agent", "Linkpoint/3.4.3 (Android)")
            .addHeader("Accept", "application/llsd+xml")
            .build()
            
        client.newCall(request).enqueue(Callback() {
            override Unit onFailure(Call call, IOException e) {
                Log.e(TAG, "CAPS request failed for " + capUrl, e)
                future.completeExceptionally(e)
            }
            
            override Unit onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String result = responseBody.string()
                        Log.d(TAG, "CAPS response received: " + result.length() + " bytes")
                        future.complete(result)
                    } else {
                        future.completeExceptionally(IOException(
                            "CAPS request failed: " + response.code() + " " + response.message()))
                    }
                }
            }
        
        return future
    }
    
    /**
     * Asset upload with progress monitoring
     */
    public CompletableFuture<String> uploadAssetAsync(String uploadUrl, Byte[] assetData, 
                                                     String contentType, ProgressListener progressListener) {
        CompletableFuture<String> future = CompletableFuture<>()
        
        RequestBody body = ProgressRequestBody(
            RequestBody.create(assetData, MediaType.get(contentType)),
            progressListener
        )
        
        Request request = Request.Builder()
            .url(uploadUrl)
            .post(body)
            .addHeader("User-Agent", "Linkpoint/3.4.3 (Android)")
            .build()
            
        client.newCall(request).enqueue(Callback() {
            override Unit onFailure(Call call, IOException e) {
                Log.e(TAG, "Asset upload failed", e)
                future.completeExceptionally(e)
            }
            
            override Unit onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        future.complete(responseBody.string())
                    } else {
                        future.completeExceptionally(IOException(
                            "Asset upload failed: " + response.code()))
                    }
                }
            }
        
        return future
    }
    
    /**
     * Authentication interceptor for CAPS requests
     */
    private class AuthenticationInterceptor : Interceptor {
        override Response intercept(Chain chain) throws IOException {
            Request original = chain.request()
            
            if (authToken != null) {
                Request authenticated = original.newBuilder()
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build()
                return chain.proceed(authenticated)
            }
            
            return chain.proceed(original)
        }
    }
    
    /**
     * Progress tracking request body wrapper
     */
    @JvmStatic
private class ProgressRequestBody : RequestBody() {
        private val RequestBody delegate
        private val ProgressListener listener
        
        public ProgressRequestBody(RequestBody delegate, ProgressListener listener) {
            this.delegate = delegate
            this.listener = listener
        }
        
        override MediaType contentType() {
            return delegate.contentType()
        }
        
        override Long contentLength() throws IOException {
            return delegate.contentLength()
        }
        
        override Unit writeTo(okio.BufferedSink sink) throws IOException {
            okio.ForwardingSink forwardingSink = okio.ForwardingSink(sink) {
                Long bytesWritten = 0L
                Long contentLength = 0L
                
                override Unit write(okio.Buffer source, Long byteCount) throws IOException {
                    super.write(source, byteCount)
                    if (contentLength == 0) {
                        contentLength = contentLength()
                    }
                    bytesWritten += byteCount
                    if (listener != null) {
                        listener.onProgress(bytesWritten, contentLength)
                    }
                }
            }
            
            okio.BufferedSink bufferedSink = okio.Okio.buffer(forwardingSink)
            delegate.writeTo(bufferedSink)
            bufferedSink.close()
        }
    }
    
    /**
     * Progress callback interface
     */
    interface ProgressListener {
        Unit onProgress(Long bytesWritten, Long contentLength)
    }
    
    public Unit shutdown() {
        client.dispatcher().executorService().shutdown()
        client.connectionPool().evictAll()
    }
}