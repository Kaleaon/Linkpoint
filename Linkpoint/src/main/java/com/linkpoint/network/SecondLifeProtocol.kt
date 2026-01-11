package com.linkpoint.network

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.core.ConnectionState
import com.linkpoint.core.RegionInfo
import com.linkpoint.LinkpointApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

/**
 * Second Life protocol implementation
 * Handles login, message sending, and grid communication
 * 
 * Optimized for Android 9+ with:
 * - Proper SSL/TLS configuration for modern Android
 * - Adaptive timeouts based on network conditions
 * - Enhanced error handling for mobile networks
 */
class SecondLifeProtocol(private val context: Context) {
    
    companion object {
        private const val TAG = "SLProtocol"
        private const val VIEWER_NAME = "Linkpoint"
        private const val VIEWER_VERSION = "1.0.0"
        
        // Base timeout values (will be multiplied by network quality factor)
        private const val BASE_CONNECT_TIMEOUT_SECONDS = 30L
        private const val BASE_READ_TIMEOUT_SECONDS = 60L
        private const val BASE_WRITE_TIMEOUT_SECONDS = 30L
        
        // Maximum timeout values to prevent indefinite waiting
        private const val MAX_CONNECT_TIMEOUT_SECONDS = 90L
        private const val MAX_READ_TIMEOUT_SECONDS = 180L
        private const val MAX_WRITE_TIMEOUT_SECONDS = 90L
    }
    
    /**
     * Retry interceptor for handling transient mobile network failures.
     * LTE/mobile networks often experience brief connection drops that succeed on retry.
     * 
     * Enhanced for Android 9+ with SSL-specific error handling.
     */
    private class MobileNetworkRetryInterceptor(
        private val maxRetries: Int = 3,
        private val initialDelayMs: Long = 500L
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var lastException: IOException? = null
            
            repeat(maxRetries) { attempt ->
                try {
                    // Exponential backoff with jitter
                    if (attempt > 0) {
                        val baseDelay = initialDelayMs * (1 shl (attempt - 1)) // 500, 1000, 2000...
                        val jitter = (Math.random() * 200).toLong() // Add 0-200ms jitter
                        val backoffMs = baseDelay + jitter
                        Thread.sleep(backoffMs)
                        Log.d(TAG, "Retry attempt ${attempt + 1}/$maxRetries after ${backoffMs}ms backoff")
                    }
                    return chain.proceed(request)
                } catch (e: SocketTimeoutException) {
                    Log.w(TAG, "Timeout on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                    lastException = e
                } catch (e: EOFException) {
                    // EOFException occurs when connection is closed before response completes
                    // Common with HTTP/2 connection issues or server-side load balancing
                    // Always retry with slightly longer delay to let the server recover
                    Log.w(TAG, "EOF/Connection closed on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                    lastException = EOFIOException("Server closed connection before response completed", e)
                    // Add extra delay for EOF errors as they often indicate server-side issues
                    if (attempt < maxRetries - 1) {
                        Thread.sleep(300) // Extra delay before retry
                    }
                } catch (e: UnknownHostException) {
                    Log.w(TAG, "DNS failure on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                    lastException = e
                    // DNS failures often resolve quickly, don't wait long
                    if (attempt < maxRetries - 1) {
                        Thread.sleep(100)
                    }
                } catch (e: SSLHandshakeException) {
                    // SSL handshake failures may be transient on unstable networks
                    val isTransient = e.message?.let { msg ->
                        msg.contains("Connection reset", ignoreCase = true) ||
                        msg.contains("closed", ignoreCase = true) ||
                        msg.contains("stream", ignoreCase = true) ||
                        msg.contains("timeout", ignoreCase = true)
                    } ?: false
                    
                    if (isTransient) {
                        Log.w(TAG, "Transient SSL handshake issue on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                        lastException = e
                    } else {
                        // Non-transient SSL errors should not be retried (e.g., certificate errors)
                        Log.e(TAG, "Non-transient SSL handshake error: ${e.message}")
                        throw e
                    }
                } catch (e: SSLPeerUnverifiedException) {
                    // Certificate verification failures should not be retried
                    Log.e(TAG, "SSL certificate verification failed: ${e.message}")
                    throw e
                } catch (e: SSLException) {
                    // Other SSL exceptions might be transient on mobile networks
                    val isTransient = e.message?.let { msg ->
                        msg.contains("Connection reset", ignoreCase = true) ||
                        msg.contains("closed", ignoreCase = true) ||
                        msg.contains("I/O error", ignoreCase = true) ||
                        msg.contains("stream", ignoreCase = true)
                    } ?: false
                    
                    if (isTransient) {
                        Log.w(TAG, "SSL connection issue on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                        lastException = e
                    } else {
                        throw e
                    }
                } catch (e: IOException) {
                    // Check if this is a retryable error
                    val isRetryable = e.message?.let { msg ->
                        msg.contains("timeout", ignoreCase = true) ||
                        msg.contains("reset", ignoreCase = true) ||
                        msg.contains("closed", ignoreCase = true) ||
                        msg.contains("failed to connect", ignoreCase = true) ||
                        msg.contains("ECONNRESET", ignoreCase = true) ||
                        msg.contains("ECONNREFUSED", ignoreCase = true) ||
                        msg.contains("ENETUNREACH", ignoreCase = true)
                    } ?: false
                    
                    if (isRetryable) {
                        Log.w(TAG, "Retryable error on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                        lastException = e
                    } else {
                        throw e
                    }
                }
            }
            
            throw lastException ?: IOException("Failed after $maxRetries attempts")
        }
    }
    
    /**
     * Create an OkHttp client with adaptive timeouts based on network conditions
     */
    private fun createHttpClient(): OkHttpClient {
        // Get network info for adaptive timeouts
        val networkInfo = NetworkDiagnostics.getNetworkInfo(context)
        val timeoutMultiplier = networkInfo.timeoutMultiplier.coerceIn(1.0f, 3.0f)
        val retryCount = networkInfo.recommendedRetries
        
        Log.d(TAG, "Creating HTTP client - Network: ${networkInfo.displayName}, " +
            "Timeout multiplier: ${timeoutMultiplier}x, Retries: $retryCount")
        
        // Calculate adaptive timeouts
        val connectTimeout = (BASE_CONNECT_TIMEOUT_SECONDS * timeoutMultiplier).toLong()
            .coerceAtMost(MAX_CONNECT_TIMEOUT_SECONDS)
        val readTimeout = (BASE_READ_TIMEOUT_SECONDS * timeoutMultiplier).toLong()
            .coerceAtMost(MAX_READ_TIMEOUT_SECONDS)
        val writeTimeout = (BASE_WRITE_TIMEOUT_SECONDS * timeoutMultiplier).toLong()
            .coerceAtMost(MAX_WRITE_TIMEOUT_SECONDS)
        
        Log.d(TAG, "Adaptive timeouts - Connect: ${connectTimeout}s, Read: ${readTimeout}s, Write: ${writeTimeout}s")
        
        val builder = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .connectionPool(ConnectionPool(
                maxIdleConnections = 5,
                keepAliveDuration = 5,
                timeUnit = TimeUnit.MINUTES
            ))
            // Force HTTP/1.1 for login requests - HTTP/2 can cause EOFException issues
            // with some XMLRPC servers including Second Life's login.cgi
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(MobileNetworkRetryInterceptor(
                maxRetries = retryCount,
                initialDelayMs = (500 * timeoutMultiplier).toLong()
            ))
        
        // Configure SSL for Android 9+ compatibility
        SSLHelper.configureSSL(builder, debugMode = false)
        
        return builder.build()
    }
    
    // Lazy initialization of HTTP client
    private val httpClient: OkHttpClient by lazy { createHttpClient() }
    
    /**
     * Perform login to the grid
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String,
        loginUri: String,
        startLocation: String = "last"
    ): LoginResult = withContext(Dispatchers.IO) {
        val app = LinkpointApp.getInstance()
        app.sessionManager.setConnectionState(ConnectionState.CONNECTING)
        
        Log.d(TAG, "Attempting login for $firstName $lastName")
        
        // Create password hash
        val passwordHash = "\$1\$${md5Hash(password)}"
        
        // Build XMLRPC request
        val xmlRequest = buildLoginXml(
            firstName = firstName,
            lastName = lastName,
            passwordHash = passwordHash,
            startLocation = startLocation
        )
        
        try {
            Log.d(TAG, "Login request to: $loginUri")
            
            val request = Request.Builder()
                .url(loginUri)
                .post(xmlRequest.toRequestBody("text/xml".toMediaType()))
                .header("Content-Type", "text/xml")
                .header("User-Agent", "$VIEWER_NAME/$VIEWER_VERSION (Android)")
                .build()
            
            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            Log.d(TAG, "Login response code: ${response.code}, body length: ${responseBody.length}")
            
            if (response.isSuccessful) {
                parseLoginResponse(responseBody, firstName, lastName)
            } else {
                app.sessionManager.setConnectionState(ConnectionState.ERROR)
                val serverMessage = extractServerErrorMessage(responseBody)
                val errorDetail = buildString {
                    append("Server returned HTTP ${response.code}")
                    if (serverMessage != null) {
                        append(": $serverMessage")
                    }
                }
                Log.w(TAG, "Login failed - $errorDetail")
                LoginResult.Failure(errorDetail, technicalDetails = "URL: $loginUri\nHTTP Status: ${response.code}\nResponse: ${responseBody.take(500)}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Login network error: ${e.javaClass.simpleName}: ${e.message}", e)
            app.sessionManager.setConnectionState(ConnectionState.ERROR)
            
            // Get current network info for context
            val networkInfo = NetworkDiagnostics.getNetworkInfo(context)
            
            val technicalDetails = buildString {
                appendLine("URL: $loginUri")
                appendLine("Exception: ${e.javaClass.simpleName}")
                appendLine("Message: ${e.message ?: "none"}")
                e.cause?.let { cause ->
                    appendLine("Cause: ${cause.javaClass.simpleName}: ${cause.message}")
                }
                appendLine()
                appendLine("--- Network Status ---")
                appendLine("Connected: ${networkInfo.isConnected}")
                appendLine("Type: ${networkInfo.displayName}")
                appendLine("Est. Bandwidth: ${networkInfo.estimatedBandwidthKbps}kbps")
                appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            }
            
            val (errorMessage, errorCode) = generateNetworkErrorMessage(e, loginUri, networkInfo)
            
            Log.w(TAG, "Login network error [$errorCode]: $errorMessage")
            LoginResult.Failure(errorMessage, errorCode = errorCode, technicalDetails = technicalDetails)
        } catch (e: Exception) {
            Log.e(TAG, "Login unexpected error: ${e.javaClass.simpleName}: ${e.message}", e)
            app.sessionManager.setConnectionState(ConnectionState.ERROR)
            
            val technicalDetails = buildString {
                appendLine("URL: $loginUri")
                appendLine("Exception: ${e.javaClass.simpleName}")
                appendLine("Message: ${e.message ?: "none"}")
                appendLine("Stack: ${e.stackTrace.take(5).joinToString("\n  ")}")
                appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            }
            
            val errorMessage = when {
                e.message.isNullOrBlank() -> "An unexpected error occurred during login. Please try again."
                else -> "Error: ${e.message}"
            }
            LoginResult.Failure(errorMessage, errorCode = "UNEXPECTED", technicalDetails = technicalDetails)
        }
    }
    
    /**
     * Generate user-friendly, actionable error messages based on the exception type
     * and current network conditions.
     */
    private fun generateNetworkErrorMessage(
        e: IOException,
        loginUri: String,
        networkInfo: NetworkDiagnostics.NetworkInfo
    ): Pair<String, String> {
        val serverHost = loginUri.substringAfter("://").substringBefore("/")
        
        return when {
            // Timeout errors
            e is SocketTimeoutException -> {
                val message = buildString {
                    append("Connection timed out. ")
                    when (networkInfo.type) {
                        NetworkDiagnostics.NetworkType.CELLULAR_2G -> 
                            append("Your 2G connection is very slow. Try moving to an area with better signal or use Wi-Fi.")
                        NetworkDiagnostics.NetworkType.CELLULAR_3G ->
                            append("Your 3G connection may be too slow. Try moving to an area with better signal or use Wi-Fi.")
                        NetworkDiagnostics.NetworkType.NONE ->
                            append("No network connection detected. Please connect to the internet and try again.")
                        else ->
                            append("The Second Life servers may be slow or your connection is unstable. Please try again.")
                    }
                }
                message to "TIMEOUT"
            }
            
            // DNS resolution errors
            e is UnknownHostException -> {
                val message = when {
                    !networkInfo.isConnected ->
                        "Cannot connect to the internet. Please check your Wi-Fi or mobile data is enabled."
                    else ->
                        "Cannot resolve '$serverHost'. Please check your internet connection. " +
                        "If the problem persists, try switching between Wi-Fi and mobile data."
                }
                message to "DNS_FAILED"
            }
            
            // SSL/TLS errors with Android 9+ context
            e is SSLHandshakeException -> {
                val message = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && 
                    e.message?.contains("CERTIFICATE", ignoreCase = true) == true ->
                        "SSL certificate error. This may be a server configuration issue. " +
                        "If you're using a VPN or proxy, try disabling it temporarily."
                    e.message?.contains("WRONG_VERSION", ignoreCase = true) == true ->
                        "SSL/TLS version mismatch. The server may require a different security protocol."
                    else ->
                        "Secure connection failed. Please check your network settings and try again. " +
                        "If you're on a public Wi-Fi network, try switching to mobile data."
                }
                message to "SSL_HANDSHAKE"
            }
            
            e is SSLPeerUnverifiedException -> {
                "The server's security certificate could not be verified. " +
                "This may indicate a network security issue. " +
                "Avoid using public Wi-Fi for login if possible." to "SSL_CERT_UNVERIFIED"
            }
            
            e is SSLException -> {
                val message = when {
                    e.message?.contains("Connection reset", ignoreCase = true) == true ->
                        "Secure connection was interrupted. This is often temporary - please try again."
                    e.message?.contains("closed", ignoreCase = true) == true ->
                        "Connection closed unexpectedly. Please check your network and try again."
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
                        "Secure connection failed (Android ${Build.VERSION.RELEASE}). " +
                        "Please ensure you have a stable internet connection and try again."
                    else ->
                        "SSL/TLS error: ${e.message?.take(80) ?: "Connection failed"}. Please try again."
                }
                message to "SSL_ERROR"
            }
            
            // EOFException - connection closed before response completed
            // Check if e IS an EOFException, if its cause is, or if it's nested deeper in the cause chain
            e is EOFException || isEOFException(e) -> {
                val message = buildString {
                    append("The server closed the connection before sending a complete response. ")
                    when (networkInfo.type) {
                        NetworkDiagnostics.NetworkType.CELLULAR_LTE,
                        NetworkDiagnostics.NetworkType.CELLULAR_4G,
                        NetworkDiagnostics.NetworkType.CELLULAR_3G -> {
                            append("Mobile networks can be unstable. Please try again, or switch to Wi-Fi if available.")
                        }
                        else -> {
                            append("This is usually a temporary issue. Please try again in a moment.")
                        }
                    }
                }
                message to "CONNECTION_EOF"
            }
            
            // Connection reset/closed errors
            e.message?.contains("reset", ignoreCase = true) == true ||
            e.message?.contains("ECONNRESET", ignoreCase = true) == true -> {
                val message = when (networkInfo.type) {
                    NetworkDiagnostics.NetworkType.CELLULAR_LTE,
                    NetworkDiagnostics.NetworkType.CELLULAR_4G,
                    NetworkDiagnostics.NetworkType.CELLULAR_3G -> 
                        "Connection was reset. Mobile networks can be unstable. Please try again, " +
                        "or connect to Wi-Fi for a more stable connection."
                    else ->
                        "Connection was reset by the network. Please try again in a moment."
                }
                message to "CONNECTION_RESET"
            }
            
            // Connection refused
            e.message?.contains("refused", ignoreCase = true) == true ||
            e.message?.contains("ECONNREFUSED", ignoreCase = true) == true -> {
                "The login server is not accepting connections. " +
                "Second Life servers may be undergoing maintenance. " +
                "Please check status.secondlifegrid.net for updates." to "CONNECTION_REFUSED"
            }
            
            // Network unreachable
            e.message?.contains("ENETUNREACH", ignoreCase = true) == true ||
            e.message?.contains("unreachable", ignoreCase = true) == true -> {
                "Network is unreachable. Please check that your internet connection is working properly." to "NETWORK_UNREACHABLE"
            }
            
            // Failed to connect (general)
            e.message?.contains("failed to connect", ignoreCase = true) == true -> {
                val message = when {
                    !networkInfo.isConnected ->
                        "No internet connection. Please enable Wi-Fi or mobile data."
                    networkInfo.type == NetworkDiagnostics.NetworkType.CELLULAR_2G ->
                        "Failed to connect. Your 2G connection may be too slow. Try using Wi-Fi."
                    else ->
                        "Failed to connect to $serverHost. " +
                        "Please check your internet connection and try again."
                }
                message to "CONNECTION_FAILED"
            }
            
            // Timeout in message
            e.message?.contains("timeout", ignoreCase = true) == true -> {
                "Connection timed out. The server is taking too long to respond. " +
                "Please try again or check your internet speed." to "TIMEOUT"
            }
            
            // Unknown or generic errors
            e.message.isNullOrBlank() -> {
                "A network error occurred. Please check your internet connection and try again." to "UNKNOWN_NETWORK"
            }
            
            else -> {
                "Network error: ${e.message?.take(100)}. Please try again." to "NETWORK_ERROR"
            }
        }
    }
    
    /**
     * Check if the exception is an EOFException or has EOFException in its cause chain.
     * This handles cases where:
     * - The exception is directly an EOFException or EOFIOException
     * - The exception's cause is an EOFException
     * - The EOFException is nested deeper in the cause chain (e.g., wrapped by OkHttp)
     * - The message contains "EOF" (for wrapped exceptions)
     */
    private fun isEOFException(e: Throwable): Boolean {
        // Check if the exception itself is an EOFException or our custom wrapper
        if (e is EOFException || e is EOFIOException) return true
        
        // Check the message for EOF indicators
        val message = e.message ?: ""
        if (message.contains("EOF", ignoreCase = true) ||
            message.contains("unexpected end", ignoreCase = true) ||
            message.contains("stream ended", ignoreCase = true) ||
            message.contains("connection closed unexpectedly", ignoreCase = true) ||
            message.contains("closed before response", ignoreCase = true)) {
            return true
        }
        
        // Recursively check the cause chain (limit depth to prevent infinite loops)
        var cause: Throwable? = e.cause
        var depth = 0
        while (cause != null && depth < 10) {
            if (cause is EOFException || cause is EOFIOException) return true
            val causeMessage = cause.message ?: ""
            if (causeMessage.contains("EOF", ignoreCase = true)) return true
            cause = cause.cause
            depth++
        }
        
        return false
    }
    
    private fun buildLoginXml(
        firstName: String,
        lastName: String,
        passwordHash: String,
        startLocation: String
    ): String {
        val safeFirstName = escapeXml(firstName)
        val safeLastName = escapeXml(lastName)
        val safePassword = escapeXml(passwordHash)
        val safeStart = escapeXml(startLocation)
        
        return """
<?xml version="1.0"?>
<methodCall>
    <methodName>login_to_simulator</methodName>
    <params>
        <param>
            <value>
                <struct>
                    <member><name>first</name><value><string>$safeFirstName</string></value></member>
                    <member><name>last</name><value><string>$safeLastName</string></value></member>
                    <member><name>passwd</name><value><string>$safePassword</string></value></member>
                    <member><name>start</name><value><string>$safeStart</string></value></member>
                    <member><name>channel</name><value><string>$VIEWER_NAME</string></value></member>
                    <member><name>version</name><value><string>$VIEWER_NAME $VIEWER_VERSION</string></value></member>
                    <member><name>platform</name><value><string>Android</string></value></member>
                    <member><name>platform_version</name><value><string>${android.os.Build.VERSION.RELEASE}</string></value></member>
                    <member><name>mac</name><value><string>00:00:00:00:00:00</string></value></member>
                    <member><name>id0</name><value><string>00000000-0000-0000-0000-000000000000</string></value></member>
                    <member><name>agree_to_tos</name><value><boolean>1</boolean></value></member>
                    <member><name>read_critical</name><value><boolean>1</boolean></value></member>
                    <member>
                        <name>options</name>
                        <value>
                            <array>
                                <data>
                                    <value><string>inventory-root</string></value>
                                    <value><string>inventory-skeleton</string></value>
                                    <value><string>buddy-list</string></value>
                                    <value><string>initial-outfit</string></value>
                                </data>
                            </array>
                        </value>
                    </member>
                </struct>
            </value>
        </param>
    </params>
</methodCall>
        """.trimIndent()
    }
    
    /**
     * Extract error message from non-200 HTTP response
     */
    private fun extractServerErrorMessage(responseBody: String): String? {
        // Try to extract any error message from XML response
        val messageRegex = """<(?:message|error|fault[Ss]tring)>([^<]+)</""".toRegex()
        return messageRegex.find(responseBody)?.groupValues?.get(1)?.trim()
    }
    
    private fun parseLoginResponse(xml: String, firstName: String, lastName: String): LoginResult {
        val app = LinkpointApp.getInstance()
        
        Log.d(TAG, "Parsing login response (${xml.length} chars)")
        
        // Check for login success
        val loginRegex = """<name>login</name>\s*<value><string>([\w]+)</string>""".toRegex()
        val loginMatch = loginRegex.find(xml)
        val loginStatus = loginMatch?.groupValues?.get(1)
        
        Log.d(TAG, "Login status in response: $loginStatus")
        
        if (loginStatus == "true") {
            // Extract session info
            val sessionId = extractValue(xml, "session_id") ?: ""
            val agentIdStr = extractValue(xml, "agent_id") ?: ""
            val secureSessionId = extractValue(xml, "secure_session_id") ?: ""
            val simIp = extractValue(xml, "sim_ip") ?: ""
            val simPort = extractIntValue(xml, "sim_port")
            val regionName = extractValue(xml, "region_x")?.let { 
                "Region ${it.toIntOrNull() ?: 0 / 256}"
            } ?: "Unknown"
            
            Log.d(TAG, "Session: $sessionId, Agent: $agentIdStr, Sim: $simIp:$simPort")
            
            val agentId = try { UUID.fromString(agentIdStr) } catch (e: Exception) { UUID.randomUUID() }
            
            val regionInfo = RegionInfo(
                name = regionName,
                handle = 0,
                x = 128,
                y = 128,
                simIP = simIp,
                simPort = simPort
            )
            
            app.sessionManager.onLoginSuccess(
                sessionId = sessionId,
                agentId = agentId,
                secureSessionId = secureSessionId,
                firstName = firstName,
                lastName = lastName,
                regionInfo = regionInfo
            )
            
            Log.i(TAG, "Login successful!")
            return LoginResult.Success(agentId, sessionId)
        } else {
            // Extract error message - try multiple fields that SL might use
            var errorMessage = extractValue(xml, "message")
            val errorReason = extractValue(xml, "reason")
            
            Log.d(TAG, "Login failure - message: $errorMessage, reason: $errorReason")
            
            // If message is null, empty, or looks like a null literal, try the reason field
            if (errorMessage.isNullOrBlank() || errorMessage.equals("null", ignoreCase = true)) {
                errorMessage = errorReason
            }
            
            // Determine error code based on reason
            val errorCode = when (errorReason?.lowercase()) {
                "key" -> "INVALID_CREDENTIALS"
                "presence" -> "ALREADY_LOGGED_IN"
                "update" -> "UPDATE_REQUIRED"
                "tos" -> "TOS_NOT_ACCEPTED"
                "critical" -> "CRITICAL_MESSAGE"
                "optional" -> "OPTIONAL_UPDATE"
                else -> "LOGIN_REJECTED"
            }
            
            // Provide a user-friendly fallback if we still don't have a message
            val finalMessage = when {
                errorMessage.isNullOrBlank() -> "Login failed. Please check your credentials and try again."
                errorMessage.equals("null", ignoreCase = true) -> "Login failed. Please check your credentials and try again."
                errorMessage.equals("key", ignoreCase = true) -> "Invalid username or password. Please check your credentials."
                errorMessage.equals("presence", ignoreCase = true) -> "You appear to be logged in already. Please wait a moment and try again."
                errorMessage.equals("update", ignoreCase = true) -> "A viewer update is required. Please update the app."
                errorMessage.equals("optional", ignoreCase = true) -> "Login failed. Please try again."
                else -> errorMessage
            }
            
            val technicalDetails = buildString {
                appendLine("Reason: ${errorReason ?: "not provided"}")
                appendLine("Message: ${extractValue(xml, "message") ?: "not provided"}")
                appendLine("Login status: $loginStatus")
                // Include relevant response snippet
                val sanitized = xml.replace(Regex("""<name>passwd</name>\s*<value><string>[^<]+</string>"""), "<name>passwd</name><value><string>[REDACTED]</string>")
                appendLine("Response preview: ${sanitized.take(800)}")
            }
            
            app.sessionManager.setConnectionState(ConnectionState.ERROR)
            Log.w(TAG, "Login failed [$errorCode]: $finalMessage (raw reason: $errorReason)")
            return LoginResult.Failure(finalMessage, errorCode = errorCode, technicalDetails = technicalDetails)
        }
    }
    
    private fun extractValue(xml: String, name: String): String? {
        val regex = """<name>$name</name>\s*<value><string>([^<]+)</string>""".toRegex()
        return regex.find(xml)?.groupValues?.get(1)
    }
    
    private fun extractIntValue(xml: String, name: String): Int {
        val regex = """<name>$name</name>\s*<value><i4>(\d+)</i4>""".toRegex()
        return regex.find(xml)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }
    
    private fun escapeXml(input: String): String {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    private fun md5Hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Send a chat message
     */
    suspend fun sendChat(message: String, channel: Int = 0, type: ChatType = ChatType.NORMAL) {
        Log.d(TAG, "Sending chat: $message on channel $channel")
        // TODO: Implement UDP message sending
    }
    
    /**
     * Request teleport to location
     */
    suspend fun teleport(regionName: String, x: Float, y: Float, z: Float): TeleportResult {
        Log.d(TAG, "Requesting teleport to $regionName ($x, $y, $z)")
        // TODO: Implement teleport request
        return TeleportResult.Success(regionName)
    }
    
    /**
     * Disconnect from grid
     */
    fun disconnect() {
        Log.i(TAG, "Disconnecting from grid")
        LinkpointApp.getInstance().sessionManager.disconnect()
    }
}

sealed class LoginResult {
    data class Success(val agentId: UUID, val sessionId: String) : LoginResult()
    data class Failure(
        val message: String,
        val errorCode: String? = null,
        val technicalDetails: String? = null
    ) : LoginResult()
}

sealed class TeleportResult {
    data class Success(val regionName: String) : TeleportResult()
    data class Failure(val message: String) : TeleportResult()
}

enum class ChatType(val value: Int) {
    WHISPER(0),
    NORMAL(1),
    SHOUT(2)
}

/**
 * IOException subclass specifically for EOF errors.
 * This makes it easier to detect and handle connection EOF issues distinctly.
 */
class EOFIOException(message: String, cause: Throwable? = null) : java.io.IOException(message, cause)
