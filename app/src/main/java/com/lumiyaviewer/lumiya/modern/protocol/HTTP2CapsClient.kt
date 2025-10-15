package com.lumiyaviewer.lumiya.modern.protocol

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
    private String TAG = "HTTP2CapsClient"
    private MediaType JSON = MediaType.get("application/json; charset=utf-8")
    private MediaType LLSD_XML = MediaType.get("application/llsd+xml; charset=utf-8")
    
    private OkHttpClient client
    private String authToken
    private Map<String, String> capabilities = new ConcurrentHashMap<>()
    
    HTTP2CapsClient() {
        this.client = new OkHttpClient.Builder()
            .protocols(java.util.Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(new AuthenticationInterceptor())
            .build()
    }
    
    void setAuthToken(String token) {
        this.authToken = token
    }
    
    /**
     * Configure capability URLs from seed capability response
     */
    void configureCapabilities(Map<String, String> capabilitiesMap) {
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
    String getCapabilityUrl(String capabilityName) {
        return capabilities.get(capabilityName)
    }
    
    /**
     * Send async CAPS request with modern error handling
     */
    CompletableFuture<String> sendAsync(String capUrl, String llsdData) {
        CompletableFuture<String> future = new CompletableFuture<>()
        
        RequestBody body = RequestBody.create(llsdData, LLSD_XML)
        Request request = new Request.Builder()
            .url(capUrl)
            .post(body)
            .addHeader("User-Agent", "Lumiya/3.4.3 (Android)")
            .addHeader("Accept", "application/llsd+xml")
            .build()
            
        client.newCall(request).enqueue(new Callback() {
            @Override
            void onFailure(Call call, IOException e) {
                Log.e(TAG, "CAPS request failed for " + capUrl, e)
                future.completeExceptionally(e)
            }
            
            @Override
            void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String result = responseBody.string()
                        Log.d(TAG, "CAPS response received: " + result.length() + " bytes")
                        future.complete(result)
                    } else {
                        future.completeExceptionally(new IOException(
                            "CAPS request failed: " + response.code() + " " + response.message()))
                    }
                }
            }
        
        return future
    }
    
    /**
     * Asset upload with progress monitoring
     */
    CompletableFuture<String> uploadAssetAsync(String uploadUrl, byte[] assetData, 
                                                     String contentType, ProgressListener progressListener) {
        CompletableFuture<String> future = new CompletableFuture<>()
        
        RequestBody body = new ProgressRequestBody(
            RequestBody.create(assetData, MediaType.get(contentType)),
            progressListener
        )
        
        Request request = new Request.Builder()
            .url(uploadUrl)
            .post(body)
            .addHeader("User-Agent", "Lumiya/3.4.3 (Android)")
            .build()
            
        client.newCall(request).enqueue(new Callback() {
            @Override
            void onFailure(Call call, IOException e) {
                Log.e(TAG, "Asset upload failed", e)
                future.completeExceptionally(e)
            }
            
            @Override
            void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        future.complete(responseBody.string())
                    } else {
                        future.completeExceptionally(new IOException(
                            "Asset upload failed: " + response.code()))
                    }
                }
            }
        
        return future
    }
    
    /**
     * Authentication interceptor for CAPS requests
     */
    private class AuthenticationInterceptor implements Interceptor {
        @Override
        Response intercept(Chain chain) throws IOException {
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
    private class ProgressRequestBody extends RequestBody {
        private RequestBody delegate
        private ProgressListener listener
        
        ProgressRequestBody(RequestBody delegate, ProgressListener listener) {
            this.delegate = delegate
            this.listener = listener
        }
        
        @Override
        MediaType contentType() {
            return delegate.contentType()
        }
        
        @Override
        long contentLength() throws IOException {
            return delegate.contentLength()
        }
        
        @Override
        void writeTo(okio.BufferedSink sink) throws IOException {
            okio.ForwardingSink forwardingSink = new okio.ForwardingSink(sink) {
                long bytesWritten = 0L
                long contentLength = 0L
                
                @Override
                void write(okio.Buffer source, long byteCount) throws IOException {
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
        fun onProgress(bytesWritten: Long, contentLength: Long): Unit
    }
    
    void shutdown() {
        client.dispatcher().executorService().shutdown()
        client.connectionPool().evictAll()
    }
}
