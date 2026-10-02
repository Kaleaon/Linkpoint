package com.linkpoint.network

import android.content.Context
import android.util.Log
import com.linkpoint.inventory.AisHttpRequest
import com.linkpoint.inventory.AisHttpResponse
import com.linkpoint.inventory.AisTransport
import com.linkpoint.inventory.OkHttpAisTransport
import java.io.IOException

/**
 * Interface defining modern HTTP transport adapters for network communication.
 */
interface HttpTransportAdapter : AisTransport {
    /** True if the Cronet engine is initialized and available for requests. */
    val isCronetAvailable: Boolean
}

/**
 * Modern Cronet engine transport adapter replacing legacy Apache HttpClient adapters.
 *
 * Dispatches HTTP, HTTP/2, and HTTP/3 (QUIC) requests through Chromium Cronet's
 * asynchronous [org.chromium.net.UrlRequest] callback interfaces without blocking threads.
 *
 * Provides dynamic fallback to standard HTTP transport ([OkHttpAisTransport] / standard Java HTTP connection)
 * if the Play Services Cronet provider or embedded Cronet engine is uninitialized or unavailable.
 */
class CronetTransportAdapter(
    private val cronetClient: CronetHttpClient,
    private val fallbackTransport: AisTransport = OkHttpAisTransport()
) : HttpTransportAdapter {

    override val isCronetAvailable: Boolean
        get() = cronetClient.isAvailable

    override suspend fun execute(request: AisHttpRequest): AisHttpResponse {
        if (cronetClient.isAvailable) {
            try {
                val contentType = request.headers["Content-Type"] ?: "application/json"
                val bodyBytes = request.body?.toByteArray(Charsets.UTF_8)
                val result = cronetClient.execute(
                    method = request.method,
                    url = request.url,
                    headers = request.headers,
                    body = bodyBytes,
                    contentType = if (bodyBytes != null) contentType else null
                )

                when (result) {
                    is CronetResult.Success -> {
                        Log.d(TAG, "✓ Dispatched ${request.method} ${request.url} via Cronet/${result.protocol} (code=${result.code})")
                        return AisHttpResponse(
                            code = result.code,
                            body = result.body.toString(Charsets.UTF_8)
                        )
                    }
                    is CronetResult.Failure -> {
                        Log.w(TAG, "Cronet request failed for ${request.url}: ${result.message}. Falling back to standard HTTP transport.")
                    }
                    is CronetResult.Cancelled -> {
                        Log.w(TAG, "Cronet request cancelled for ${request.url}. Falling back to standard HTTP transport.")
                    }
                    is CronetResult.EngineUnavailable -> {
                        Log.i(TAG, "Cronet engine unavailable. Routing request through standard HTTP transport fallback.")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during Cronet transport execution: ${e.message}. Falling back to standard HTTP transport.", e)
            }
        } else {
            Log.d(TAG, "Cronet engine uninitialized/unavailable. Executing via standard HTTP transport fallback.")
        }

        // Dynamic fallback to standard HTTP transport
        return fallbackTransport.execute(request)
    }

    companion object {
        private const val TAG = "CronetTransportAdapter"

        /**
         * Convenience factory method using application context.
         */
        fun create(context: Context, fallbackTransport: AisTransport = OkHttpAisTransport()): CronetTransportAdapter {
            val cronet = CronetHttpClient.getOrCreate(context)
            return CronetTransportAdapter(cronet, fallbackTransport)
        }
    }
}
