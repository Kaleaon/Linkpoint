package com.linkpoint.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UploadDataProviders
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

/**
 * Chromium Cronet wrapper for asset traffic — primary path for textures
 * and meshes, with the existing OkHttp+Conscrypt client as fallback.
 *
 * **Why Cronet:** Cronet gives us a single client that speaks HTTP/2
 * reliably (Chromium's BoringSSL handles ALPN where some Android-platform
 * SSL stacks fail) plus HTTP/3 *if and when the server advertises it via
 * Alt-Svc*. Today (2026-04 check) `login.agni.lindenlab.com`,
 * `asset-cdn.glb.agni.lindenlab.com`, and the simhost capability hosts
 * return no `alt-svc` header, so QUIC negotiation never engages and we
 * stay on H2 over TCP. Leaving QUIC enabled is still worthwhile: when
 * LL flips the switch (or for OpenSim grids that already serve H3),
 * connection-migration kicks in for free — a QUIC connection is keyed by
 * connection ID, not the IP/port tuple, so the cellular CGNAT rebinds
 * we keep seeing in pcap captures wouldn't tear the connection down.
 * Until then the cellular UDP-rebind problem stays a UDP-protocol
 * problem (see `LinkpointConstants.CELLULAR_KEEPALIVE_INTERVAL_MS`).
 *
 * **Default-on with graceful fallback:** [isAvailable] reflects whether
 * the engine actually built. If Cronet's native lib is missing, blocked
 * by SELinux, or the constructor throws for any other reason, the flag
 * stays false and callers route through their existing OkHttp path.
 * Per-request fallback is the caller's responsibility — see the wrapper
 * pattern in `TextureManager.downloadTextureViaCronet`.
 *
 * **Embedded variant:** the dependency is `cronet-embedded`, which
 * bundles native libs per ABI (~5-8 MB each). This avoids a Play
 * Services dependency so the app works on de-googled / Asian-market
 * devices, at the cost of APK size.
 */
class CronetHttpClient private constructor(
    private val engine: CronetEngine?,
    private val executor: Executor
) {

    /** True when [engine] is non-null and ready to serve requests. */
    val isAvailable: Boolean get() = engine != null

    /**
     * GET a URL, returning the body bytes on 2xx or null on any failure
     * (caller falls back to OkHttp). Writes a structured log line so we
     * can A/B Cronet vs OkHttp performance from the debug report.
     */
    suspend fun get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long = 15_000L
    ): CronetResult = execute("GET", url, headers, body = null, contentType = null, timeoutMs = timeoutMs)

    /**
     * POST a request with an in-memory byte body. Used by capability
     * traffic — the LLSD-XML payload comes from `LLSDXmlUtils.wrap` and
     * is small enough that an UploadDataProvider over a single ByteBuffer
     * is the right fit (no streaming). Returns [CronetResult] in the same
     * shape as [get].
     */
    suspend fun post(
        url: String,
        body: ByteArray,
        contentType: String,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long = 15_000L
    ): CronetResult = execute("POST", url, headers, body = body, contentType = contentType, timeoutMs = timeoutMs)

    private suspend fun execute(
        method: String,
        url: String,
        headers: Map<String, String>,
        body: ByteArray?,
        contentType: String?,
        timeoutMs: Long
    ): CronetResult {
        val pinnedEngine = engine ?: return CronetResult.EngineUnavailable
        return suspendCancellableCoroutine { cont ->
            val callback = AccumulatingCallback(cont, url)
            val builder = pinnedEngine.newUrlRequestBuilder(url, callback, executor)
                .setHttpMethod(method)
            headers.forEach { (k, v) -> builder.addHeader(k, v) }
            if (body != null) {
                if (contentType != null) builder.addHeader("Content-Type", contentType)
                builder.setUploadDataProvider(
                    UploadDataProviders.create(ByteBuffer.wrap(body)),
                    executor
                )
            }
            val request = builder.build()
            cont.invokeOnCancellation {
                try { request.cancel() } catch (_: Throwable) {}
            }
            request.start()
        }
    }

    private class AccumulatingCallback(
        private val cont: kotlinx.coroutines.CancellableContinuation<CronetResult>,
        private val url: String
    ) : UrlRequest.Callback() {

        private val sink = java.io.ByteArrayOutputStream()
        private val readBuffer: ByteBuffer = ByteBuffer.allocateDirect(64 * 1024)
        @Volatile private var resumed = AtomicBoolean(false)

        override fun onRedirectReceived(
            request: UrlRequest, info: UrlResponseInfo, newLocationUrl: String
        ) {
            request.followRedirect()
        }

        override fun onResponseStarted(request: UrlRequest, info: UrlResponseInfo) {
            request.read(readBuffer)
        }

        override fun onReadCompleted(
            request: UrlRequest, info: UrlResponseInfo, byteBuffer: ByteBuffer
        ) {
            byteBuffer.flip()
            val arr = ByteArray(byteBuffer.remaining())
            byteBuffer.get(arr)
            sink.write(arr)
            byteBuffer.clear()
            request.read(byteBuffer)
        }

        override fun onSucceeded(request: UrlRequest, info: UrlResponseInfo) {
            if (!resumed.compareAndSet(false, true)) return
            cont.resume(
                CronetResult.Success(
                    code = info.httpStatusCode,
                    body = sink.toByteArray(),
                    protocol = info.negotiatedProtocol ?: "unknown",
                    proxy = info.proxyServer
                )
            )
        }

        override fun onFailed(
            request: UrlRequest, info: UrlResponseInfo?, error: CronetException
        ) {
            if (!resumed.compareAndSet(false, true)) return
            Log.w(TAG, "Cronet failed for $url: ${error.message}")
            cont.resume(CronetResult.Failure(error.message ?: "Cronet error", info?.httpStatusCode))
        }

        override fun onCanceled(request: UrlRequest, info: UrlResponseInfo?) {
            if (!resumed.compareAndSet(false, true)) return
            cont.resume(CronetResult.Cancelled)
        }
    }

    companion object {
        private const val TAG = "CronetHttpClient"

        @Volatile private var instance: CronetHttpClient? = null

        /**
         * Build (or return cached) singleton. Safe to call from anywhere;
         * the engine constructor is invoked at most once per process. If
         * construction fails (no native lib, SELinux denial, OOM, etc.)
         * the returned client has [isAvailable] = false and all calls
         * short-circuit to [CronetResult.EngineUnavailable].
         *
         * QUIC hints below cover the SL agni / aditi asset hostnames so
         * Cronet attempts H3 from the very first request rather than
         * waiting for an Alt-Svc response. Add new hints here as new SL
         * CDN hosts come online.
         */
        fun getOrCreate(context: Context): CronetHttpClient {
            instance?.let { return it }
            synchronized(this) {
                instance?.let { return it }
                val executor = Executors.newSingleThreadExecutor { r ->
                    Thread(r, "CronetCallback").apply { isDaemon = true }
                }
                val engine = try {
                    CronetEngine.Builder(context.applicationContext)
                        .enableHttp2(true)
                        .enableQuic(true)
                        .enableBrotli(true)
                        .setUserAgent("Linkpoint/1.0 (Android; Cronet)")
                        .apply {
                            // Pre-seed QUIC for known SL asset CDN hostnames so the
                            // first texture fetch goes straight to H3 rather than
                            // negotiating from H2 via Alt-Svc.
                            addQuicHint("asset-cdn.glb.agni.lindenlab.com", 443, 443)
                            addQuicHint("asset-cdn.glb.aditi.lindenlab.com", 443, 443)
                        }
                        .build()
                        .also {
                            // H3 is *enabled* but only used when the server returns
                            // Alt-Svc; agni hosts do not as of 2026-04. Don't claim
                            // otherwise in the log.
                            Log.i(TAG, "✓ Cronet engine ready (HTTP/2 + Brotli; HTTP/3 enabled, awaits server Alt-Svc)")
                        }
                } catch (e: Throwable) {
                    Log.e(TAG, "Cronet engine init failed — falling back to OkHttp permanently: ${e.message}", e)
                    null
                }
                val client = CronetHttpClient(engine, executor)
                instance = client
                return client
            }
        }
    }
}

sealed class CronetResult {
    data class Success(
        val code: Int,
        val body: ByteArray,
        val protocol: String,
        val proxy: String?
    ) : CronetResult()
    data class Failure(val message: String, val httpCode: Int?) : CronetResult()
    object Cancelled : CronetResult()
    object EngineUnavailable : CronetResult()
}
