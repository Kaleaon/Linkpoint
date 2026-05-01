package com.linkpoint.network

import android.util.Log
import java.security.Security

/**
 * Records the outcome of TLS-stack initialisation so the debug report can
 * surface "why is HTTP/2 failing" without us having to fish through the
 * logcat. Populated once from [LinkpointApp.onCreate] (Conscrypt install
 * attempt) and once from [CronetHttpClient.getOrCreate] (Cronet engine
 * build attempt).
 *
 * The 2026-04-29 + 2026-05-01 Athanasia captures both showed 0% HTTP/2
 * for capability + texture traffic. With this object plumbed through the
 * debug report we can tell at a glance whether:
 *   - Conscrypt failed to install (UnsatisfiedLinkError on an OEM SoC)
 *   - Conscrypt installed but the JCA registry has it at the wrong
 *     position
 *   - Cronet's native engine failed to build
 *   - Both are healthy and the server is genuinely H1.1-only
 *     (some `simhost-*.agni.secondlife.com` capability hosts are
 *     suspected of this — yet to be confirmed with a wireshark capture
 *     of an actual cap response)
 */
object TlsDiagnostics {

    enum class State { UNKNOWN, OK, FAILED }

    @Volatile var conscryptState: State = State.UNKNOWN
        private set
    @Volatile var conscryptError: String? = null
        private set
    @Volatile var conscryptVersion: String? = null
        private set

    @Volatile var cronetState: State = State.UNKNOWN
        private set
    @Volatile var cronetError: String? = null
        private set
    @Volatile var cronetVersion: String? = null
        private set

    fun recordConscryptOk(version: String?) {
        conscryptState = State.OK
        conscryptError = null
        conscryptVersion = version
        Log.i("TlsDiagnostics", "Conscrypt OK${version?.let { " (v$it)" } ?: ""}")
    }

    fun recordConscryptFailure(error: Throwable) {
        conscryptState = State.FAILED
        conscryptError = "${error.javaClass.simpleName}: ${error.message ?: "(no message)"}"
        Log.w("TlsDiagnostics", "Conscrypt FAILED: $conscryptError")
    }

    fun recordCronetOk(version: String?) {
        cronetState = State.OK
        cronetError = null
        cronetVersion = version
        Log.i("TlsDiagnostics", "Cronet OK${version?.let { " (v$it)" } ?: ""}")
    }

    fun recordCronetFailure(error: Throwable) {
        cronetState = State.FAILED
        cronetError = "${error.javaClass.simpleName}: ${error.message ?: "(no message)"}"
        Log.w("TlsDiagnostics", "Cronet FAILED: $cronetError")
    }

    /**
     * One-line summary of the JCA provider order so we can tell whether
     * Conscrypt actually landed at position 1. The 2026-05-01 Athanasia
     * capture is the kind of investigation this directly answers — if
     * Conscrypt is at position 5 instead of 1, OkHttp's `SSLContext.
     * getInstance("TLS")` will pick the platform engine instead.
     */
    fun jcaProviderOrder(): String = try {
        Security.getProviders()
            .mapIndexed { idx, p -> "${idx + 1}:${p.name}" }
            .joinToString(",")
    } catch (e: Exception) {
        "(error: ${e.message})"
    }

    /**
     * Block of text suitable for dropping into the debug report.
     */
    fun renderDebugSection(): String = buildString {
        appendLine("TLS Stack Diagnostics:")
        appendLine("  Conscrypt: ${stateLabel(conscryptState)}${conscryptVersion?.let { " (v$it)" } ?: ""}")
        conscryptError?.let { appendLine("    Error: $it") }
        appendLine("  Cronet engine: ${stateLabel(cronetState)}${cronetVersion?.let { " (v$it)" } ?: ""}")
        cronetError?.let { appendLine("    Error: $it") }
        append("  JCA provider order: ${jcaProviderOrder()}")
    }

    private fun stateLabel(s: State): String = when (s) {
        State.OK -> "✓ OK"
        State.FAILED -> "✗ FAILED"
        State.UNKNOWN -> "? not initialised"
    }

    /**
     * Build an SSLSocketFactory that is *guaranteed* to use Conscrypt
     * when Conscrypt installed successfully. Inserting Conscrypt into
     * the JCA registry (LinkpointApp.onCreate) is necessary but on some
     * Android devices not sufficient — `SSLContext.getInstance("TLS")`
     * still resolves to the platform engine, which can have flaky ALPN.
     * Passing `Conscrypt.newProvider()` explicitly to `SSLContext.
     * getInstance` is the bulletproof path: we go through BoringSSL,
     * which does ALPN reliably and gives us HTTP/2.
     *
     * Returns null if Conscrypt isn't available; callers should leave
     * OkHttp's default SSL config untouched in that case.
     */
    fun buildConscryptSslSocketFactory(): Pair<javax.net.ssl.SSLSocketFactory, javax.net.ssl.X509TrustManager>? {
        if (conscryptState != State.OK) return null
        return try {
            val provider = org.conscrypt.Conscrypt.newProvider()
            val tmf = javax.net.ssl.TrustManagerFactory.getInstance(
                javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm()
            ).apply { init(null as java.security.KeyStore?) }
            val trustManagers = tmf.trustManagers
            val tm = trustManagers.firstOrNull { it is javax.net.ssl.X509TrustManager }
                as? javax.net.ssl.X509TrustManager ?: return null
            val sslContext = javax.net.ssl.SSLContext.getInstance("TLS", provider)
            sslContext.init(null, arrayOf<javax.net.ssl.TrustManager>(tm), null)
            sslContext.socketFactory to tm
        } catch (e: Throwable) {
            Log.w("TlsDiagnostics", "Could not build Conscrypt SSL socket factory: ${e.message}", e)
            null
        }
    }
}
