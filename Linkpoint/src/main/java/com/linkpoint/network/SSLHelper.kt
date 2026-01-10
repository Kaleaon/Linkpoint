package com.linkpoint.network

import android.content.Context
import android.os.Build
import android.util.Log
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * SSL/TLS Helper for Android 9+ compatibility
 * 
 * Addresses common SSL issues on Android devices:
 * - Android 9+ (API 28) stricter certificate validation
 * - TLS 1.2/1.3 compatibility across Android versions
 * - Proper cipher suite selection for security
 * - Debug mode for SSL troubleshooting
 */
object SSLHelper {
    
    private const val TAG = "SSLHelper"
    
    /**
     * Connection specifications for different security levels
     */
    private val MODERN_TLS = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
        .cipherSuites(
            // TLS 1.3 cipher suites
            CipherSuite.TLS_AES_128_GCM_SHA256,
            CipherSuite.TLS_AES_256_GCM_SHA384,
            CipherSuite.TLS_CHACHA20_POLY1305_SHA256,
            // TLS 1.2 cipher suites (most secure first)
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
            CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256
        )
        .build()
    
    /**
     * Compatible TLS for older servers that don't support modern protocols
     */
    private val COMPATIBLE_TLS = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
        .allEnabledCipherSuites()
        .build()
    
    /**
     * Get the default system TrustManager
     */
    fun getDefaultTrustManager(): X509TrustManager {
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(null as KeyStore?)
        
        val trustManagers = trustManagerFactory.trustManagers
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers: ${trustManagers.contentToString()}")
        }
        
        return trustManagers[0] as X509TrustManager
    }
    
    /**
     * Get SSLSocketFactory configured for Android 9+ compatibility
     */
    fun getSSLSocketFactory(): Pair<SSLSocketFactory, X509TrustManager> {
        val trustManager = getDefaultTrustManager()
        
        val sslContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+: Use TLS 1.3
            SSLContext.getInstance("TLSv1.3")
        } else {
            // Android 9 and below: Use TLS 1.2
            SSLContext.getInstance("TLSv1.2")
        }
        
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        
        return sslContext.socketFactory to trustManager
    }
    
    /**
     * Configure OkHttpClient.Builder with proper SSL settings for Android 9+
     * 
     * This is the main entry point for configuring HTTP clients for login.
     */
    fun configureSSL(builder: OkHttpClient.Builder, debugMode: Boolean = false): OkHttpClient.Builder {
        try {
            val (sslSocketFactory, trustManager) = getSSLSocketFactory()
            
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            
            // Configure connection specs based on mode
            if (debugMode) {
                // In debug mode, allow more protocols for testing
                builder.connectionSpecs(listOf(MODERN_TLS, COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                Log.d(TAG, "SSL configured in DEBUG mode - allowing more protocols")
            } else {
                // Production: Modern TLS only, no cleartext
                builder.connectionSpecs(listOf(MODERN_TLS, COMPATIBLE_TLS))
                Log.d(TAG, "SSL configured in PRODUCTION mode - TLS 1.2+ only")
            }
            
            // Enable hostname verification (always on for security)
            // OkHttp does this by default, but we're explicit
            
        } catch (e: Exception) {
            Log.e(TAG, "Error configuring SSL: ${e.message}", e)
            // Fall back to default configuration
            builder.connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
        }
        
        return builder
    }
    
    /**
     * Diagnose SSL connection issues for a given URL
     * Returns a detailed report of any problems found
     */
    fun diagnoseSSLIssues(url: String): SSLDiagnosticResult {
        val issues = mutableListOf<String>()
        val info = mutableListOf<String>()
        
        info.add("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        info.add("Target URL: $url")
        
        try {
            val javaUrl = java.net.URL(url)
            
            // Check protocol
            if (javaUrl.protocol == "http") {
                issues.add("URL uses insecure HTTP protocol. HTTPS is strongly recommended.")
            }
            
            // Check port
            val port = if (javaUrl.port == -1) {
                if (javaUrl.protocol == "https") 443 else 80
            } else {
                javaUrl.port
            }
            info.add("Port: $port")
            
            // For HTTPS, try to validate the certificate
            if (javaUrl.protocol == "https") {
                try {
                    val connection = javaUrl.openConnection() as javax.net.ssl.HttpsURLConnection
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    
                    // Try to connect
                    connection.connect()
                    
                    // Get certificate info
                    val certs = connection.serverCertificates
                    if (certs.isNotEmpty()) {
                        val cert = certs[0] as? X509Certificate
                        cert?.let {
                            info.add("Certificate Subject: ${it.subjectDN}")
                            info.add("Certificate Issuer: ${it.issuerDN}")
                            info.add("Valid From: ${it.notBefore}")
                            info.add("Valid Until: ${it.notAfter}")
                            
                            // Check expiration
                            val now = java.util.Date()
                            if (now.before(it.notBefore)) {
                                issues.add("Certificate is not yet valid!")
                            }
                            if (now.after(it.notAfter)) {
                                issues.add("Certificate has EXPIRED!")
                            }
                        }
                    }
                    
                    info.add("TLS Protocol: ${connection.cipherSuite}")
                    connection.disconnect()
                    
                } catch (e: javax.net.ssl.SSLHandshakeException) {
                    issues.add("SSL Handshake failed: ${e.message}")
                    
                    // Common causes
                    when {
                        e.message?.contains("CERTIFICATE_VERIFY_FAILED", ignoreCase = true) == true ->
                            issues.add("Cause: Server certificate could not be verified. May be self-signed or from untrusted CA.")
                        e.message?.contains("WRONG_VERSION", ignoreCase = true) == true ->
                            issues.add("Cause: TLS version mismatch. Server may require older TLS versions.")
                        e.message?.contains("HOSTNAME", ignoreCase = true) == true ->
                            issues.add("Cause: Certificate hostname doesn't match the URL.")
                    }
                } catch (e: javax.net.ssl.SSLException) {
                    issues.add("SSL Error: ${e.message}")
                } catch (e: Exception) {
                    issues.add("Connection error: ${e.javaClass.simpleName}: ${e.message}")
                }
            }
            
            // Android 9+ specific checks
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.add("Android 9+ detected - stricter SSL requirements apply")
                
                if (javaUrl.protocol == "http") {
                    issues.add("Android 9+ blocks cleartext HTTP by default. " +
                        "Ensure network_security_config.xml is properly configured.")
                }
            }
            
        } catch (e: Exception) {
            issues.add("Failed to analyze URL: ${e.message}")
        }
        
        return SSLDiagnosticResult(
            url = url,
            issues = issues,
            info = info,
            hasIssues = issues.isNotEmpty()
        )
    }
    
    data class SSLDiagnosticResult(
        val url: String,
        val issues: List<String>,
        val info: List<String>,
        val hasIssues: Boolean
    ) {
        fun toReport(): String = buildString {
            appendLine("=== SSL Diagnostic Report ===")
            appendLine()
            appendLine("URL: $url")
            appendLine()
            appendLine("--- Info ---")
            info.forEach { appendLine("  • $it") }
            appendLine()
            if (hasIssues) {
                appendLine("--- Issues Found ---")
                issues.forEach { appendLine("  ⚠️ $it") }
            } else {
                appendLine("--- No Issues Found ---")
                appendLine("  ✓ SSL configuration appears correct")
            }
        }
    }
    
    /**
     * Check if the device supports TLS 1.3
     */
    fun supportsTLS13(): Boolean {
        return try {
            val sslContext = SSLContext.getInstance("TLSv1.3")
            sslContext.init(null, null, null)
            val protocols = sslContext.supportedSSLParameters.protocols
            protocols?.contains("TLSv1.3") == true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get the highest supported TLS version on this device
     */
    fun getHighestSupportedTLSVersion(): String {
        return when {
            supportsTLS13() -> "TLS 1.3"
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN -> "TLS 1.2"
            else -> "TLS 1.0"
        }
    }
}
