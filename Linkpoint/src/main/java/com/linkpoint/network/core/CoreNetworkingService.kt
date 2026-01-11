package com.linkpoint.network.core

import android.content.Context
import android.util.Log
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.SSLException
import com.linkpoint.network.EOFIOException
import com.linkpoint.network.NetworkExceptionUtils

/**
 * Core networking service with comprehensive connection management.
 * Based on patterns from the official Second Life app's CoreNetworkingService.
 * 
 * Features:
 * - Automatic reconnection with exponential backoff
 * - Connection quality monitoring
 * - Error tracking and thresholds
 * - gRPC channel management
 * - HTTP client for XMLRPC login
 * - Network diagnostics logging
 * - Force reconnect capability
 * - Connection death detection and restoration
 */
class CoreNetworkingService(private val context: Context) {
    
    companion object {
        private const val TAG = "CoreNetworking"
        
        // Reconnection configuration
        private const val MAX_RECONNECT_TIME_MS = 120_000L  // 2 minutes max
        private const val INITIAL_RECONNECT_DELAY_MS = 1000L
        private const val ENSURE_CONNECTED_RETRY_DELAY_MS = 500L
        
        // Timeout for connection checks
        private const val CONNECTION_CHECK_TIMEOUT_MS = 10_000L
        
        // Maximum concurrent web requests
        private const val MAX_CONCURRENT_REQUESTS = 500
    }
    
    // Components
    val qualityManager = ConnectionQualityManager(context)
    val stateManager = NetworkStateManager()
    private val channelFactory = GrpcChannelFactory(context, qualityManager)
    
    // Retry policies
    private val loginRetryPolicy = RetryPolicy.forLogin()
    private val streamRetryPolicy = RetryPolicy.forMobileNetwork()
    
    // Coroutine scope for background operations
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Current connection
    private var currentChannel: ManagedChannel? = null
    private var currentHttpClient: OkHttpClient? = null
    
    // Connection lock
    private val connectionLock = Any()
    private val isConnecting = AtomicBoolean(false)
    
    // Events
    private val _connectionEvents = MutableSharedFlow<ConnectionEvent>()
    val connectionEvents: SharedFlow<ConnectionEvent> = _connectionEvents.asSharedFlow()
    
    /**
     * Connection events for observers
     */
    sealed class ConnectionEvent {
        object Connecting : ConnectionEvent()
        object Connected : ConnectionEvent()
        object Disconnected : ConnectionEvent()
        data class Reconnecting(val attempt: Int, val delayMs: Long) : ConnectionEvent()
        data class Error(val message: String, val code: String, val recoverable: Boolean) : ConnectionEvent()
        object ConnectionReset : ConnectionEvent()
    }
    
    /**
     * Result of a login operation
     */
    sealed class LoginResult {
        data class Success(
            val sessionId: String,
            val agentId: String,
            val simIp: String,
            val simPort: Int,
            val responseXml: String
        ) : LoginResult()
        
        data class Failure(
            val message: String,
            val errorCode: String,
            val technicalDetails: String? = null,
            val shouldRetry: Boolean = false
        ) : LoginResult()
    }
    
    init {
        // Generate connection instance ID
        stateManager.setConnectionInstanceId(UUID.randomUUID().toString().take(8))
        
        Log.d(TAG, "CoreNetworkingService initialized")
    }
    
    /**
     * Ensure we have an active connection.
     * Creates a new connection if needed.
     */
    suspend fun ensureConnected(): Boolean = withContext(Dispatchers.IO) {
        if (stateManager.isLogoutInProgress()) {
            Log.d(TAG, "EnsureConnected: Logout in progress, not connecting")
            return@withContext false
        }
        
        val currentStatus = stateManager.connectionStatus.value
        if (currentStatus == NetworkStateManager.ConnectionStatus.CONNECTED) {
            return@withContext true
        }
        
        if (isConnecting.get()) {
            Log.d(TAG, "EnsureConnected: Already connecting, waiting...")
            // Wait for connection to complete
            delay(ENSURE_CONNECTED_RETRY_DELAY_MS)
            return@withContext stateManager.connectionStatus.value == 
                NetworkStateManager.ConnectionStatus.CONNECTED
        }
        
        return@withContext connectOrReconnect()
    }
    
    /**
     * Connect or reconnect with exponential backoff
     */
    suspend fun connectOrReconnect(): Boolean = withContext(Dispatchers.IO) {
        if (!isConnecting.compareAndSet(false, true)) {
            Log.d(TAG, "ConnectOrReconnect: Already in progress")
            return@withContext false
        }
        
        try {
            stateManager.setStatus(NetworkStateManager.ConnectionStatus.CONNECTING)
            emitEvent(ConnectionEvent.Connecting)
            
            val startTime = System.currentTimeMillis()
            var attempt = 0
            
            while (System.currentTimeMillis() - startTime < MAX_RECONNECT_TIME_MS) {
                if (stateManager.isLogoutInProgress()) {
                    Log.d(TAG, "ConnectOrReconnect: Logout initiated, stopping")
                    return@withContext false
                }
                
                attempt++
                Log.d(TAG, "ConnectOrReconnect: Attempt $attempt")
                
                try {
                    // Create HTTP client for login operations
                    synchronized(connectionLock) {
                        currentHttpClient = channelFactory.createHttpClient()
                    }
                    
                    // Connection successful
                    stateManager.setStatus(NetworkStateManager.ConnectionStatus.CONNECTED)
                    stateManager.clearConnectionReset()
                    streamRetryPolicy.onSuccess()
                    emitEvent(ConnectionEvent.Connected)
                    
                    Log.d(TAG, "ConnectOrReconnect: Connected successfully after $attempt attempts")
                    return@withContext true
                    
                } catch (e: Exception) {
                    val decision = streamRetryPolicy.onError(e, isRecoverableError(e))
                    qualityManager.recordRequestResult(false)
                    
                    when (decision) {
                        is RetryPolicy.RetryDecision.Retry -> {
                            Log.d(TAG, "ConnectOrReconnect: Retrying in ${decision.delayMs}ms")
                            emitEvent(ConnectionEvent.Reconnecting(decision.attempt, decision.delayMs))
                            delay(decision.delayMs)
                        }
                        is RetryPolicy.RetryDecision.GiveUp -> {
                            Log.e(TAG, "ConnectOrReconnect: Giving up - ${decision.reason}")
                            if (decision.shouldResetConnection) {
                                stateManager.requestConnectionReset()
                                emitEvent(ConnectionEvent.ConnectionReset)
                            }
                            stateManager.setStatus(NetworkStateManager.ConnectionStatus.ERROR)
                            return@withContext false
                        }
                        is RetryPolicy.RetryDecision.Continue -> {
                            // Continue with degraded state
                            Log.w(TAG, "ConnectOrReconnect: Continuing with degraded connection")
                        }
                    }
                }
            }
            
            Log.e(TAG, "ConnectOrReconnect: Max reconnect time exceeded")
            stateManager.setStatus(NetworkStateManager.ConnectionStatus.ERROR)
            return@withContext false
            
        } finally {
            isConnecting.set(false)
        }
    }
    
    /**
     * Perform login with comprehensive retry handling.
     * Uses XMLRPC for Second Life login (required by the protocol).
     */
    suspend fun login(
        loginUri: String,
        xmlRequest: String
    ): LoginResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Login request to: $loginUri")
        
        // Double-check network connectivity before attempting login
        // The network callback may not have updated yet, so do a fresh check
        val networkConnected = validateNetworkConnection()
        
        if (!networkConnected) {
            Log.w(TAG, "Network check failed - no connectivity detected")
            return@withContext LoginResult.Failure(
                message = "No network connection available. Please check your internet connection.",
                errorCode = "NO_NETWORK",
                technicalDetails = buildNetworkDiagnosticsString()
            )
        }
        
        stateManager.setStatus(NetworkStateManager.ConnectionStatus.CONNECTING)
        emitEvent(ConnectionEvent.Connecting)
        loginRetryPolicy.reset()
        
        val startTime = System.currentTimeMillis()
        
        while (true) {
            try {
                val result = executeLoginRequest(loginUri, xmlRequest)
                
                // Record success
                loginRetryPolicy.onSuccess()
                qualityManager.recordRequestResult(true)
                qualityManager.recordLatency(System.currentTimeMillis() - startTime)
                
                stateManager.setStatus(NetworkStateManager.ConnectionStatus.CONNECTED)
                emitEvent(ConnectionEvent.Connected)
                
                return@withContext result
                
            } catch (e: Exception) {
                Log.e(TAG, "Login error: ${e.javaClass.simpleName}: ${e.message}")
                qualityManager.recordRequestResult(false)
                
                val isRecoverable = isRecoverableError(e)
                val decision = loginRetryPolicy.onError(e, isRecoverable)
                
                when (decision) {
                    is RetryPolicy.RetryDecision.Retry -> {
                        Log.d(TAG, "Login: Retrying in ${decision.delayMs}ms (attempt ${decision.attempt})")
                        emitEvent(ConnectionEvent.Reconnecting(decision.attempt, decision.delayMs))
                        delay(decision.delayMs)
                        
                        // Create fresh HTTP client for retry
                        synchronized(connectionLock) {
                            currentHttpClient = channelFactory.createHttpClient()
                        }
                    }
                    is RetryPolicy.RetryDecision.GiveUp -> {
                        Log.e(TAG, "Login: Giving up - ${decision.reason}")
                        stateManager.setStatus(NetworkStateManager.ConnectionStatus.ERROR)
                        emitEvent(ConnectionEvent.Error(
                            message = decision.reason,
                            code = getErrorCode(e),
                            recoverable = false
                        ))
                        
                        return@withContext LoginResult.Failure(
                            message = getUserFriendlyMessage(e),
                            errorCode = getErrorCode(e),
                            technicalDetails = buildTechnicalDetails(e, loginUri),
                            shouldRetry = false
                        )
                    }
                    is RetryPolicy.RetryDecision.Continue -> {
                        // This shouldn't happen for login, return failure
                        Log.w(TAG, "Login: Unexpected Continue decision")
                        return@withContext LoginResult.Failure(
                            message = "Unexpected login state",
                            errorCode = "INTERNAL_ERROR",
                            shouldRetry = true
                        )
                    }
                }
            }
        }
        
        // Unreachable - satisfies compiler's return type requirement
        @Suppress("UNREACHABLE_CODE")
        LoginResult.Failure(
            message = "Unreachable code path",
            errorCode = "UNREACHABLE"
        )
    }
    
    /**
     * Execute a single login request.
     * 
     * Always creates a fresh HTTP client to avoid stale connection issues.
     * The Second Life login server can be sensitive to connection reuse.
     */
    private fun executeLoginRequest(loginUri: String, xmlRequest: String): LoginResult {
        // Always create a fresh client for login to avoid stale connections
        // This is critical for avoiding EOF errors from reused connections
        val client = channelFactory.createHttpClient()
        
        // Update the cached client reference
        synchronized(connectionLock) {
            currentHttpClient = client
        }
        
        val request = Request.Builder()
            .url(loginUri)
            .post(xmlRequest.toRequestBody("text/xml".toMediaType()))
            .header("Content-Type", "text/xml")
            .header("Accept", "text/xml, application/xml")
            .header("User-Agent", "Linkpoint/1.0.0 (Android)")
            // Connection: close ensures we don't reuse a potentially stale connection
            // The GrpcChannelFactory also adds this via interceptor, but explicit is better
            .header("Connection", "close")
            .build()
        
        Log.d(TAG, "Executing login request to: $loginUri")
        
        // Execute with body retry handling
        val (response, responseBody) = executeWithBodyRetry(client, request)
        
        if (!response.isSuccessful) {
            val errorDetails = buildString {
                appendLine("URL: $loginUri")
                appendLine("HTTP Status: ${response.code}")
                appendLine("Response: ${responseBody.take(500)}")
            }
            
            // Check for specific HTTP error codes
            return when (response.code) {
                503 -> LoginResult.Failure(
                    message = "Login service temporarily unavailable. Please try again in a few minutes.",
                    errorCode = "SERVICE_UNAVAILABLE",
                    technicalDetails = errorDetails,
                    shouldRetry = true
                )
                502, 504 -> LoginResult.Failure(
                    message = "Login gateway error. The server may be overloaded.",
                    errorCode = "GATEWAY_ERROR",
                    technicalDetails = errorDetails,
                    shouldRetry = true
                )
                else -> LoginResult.Failure(
                    message = "Server returned HTTP ${response.code}",
                    errorCode = "HTTP_${response.code}",
                    technicalDetails = errorDetails
                )
            }
        }
        
        // Parse the login response
        return parseLoginResponse(responseBody)
    }
    
    /**
     * Execute request with retry for EOF during body reading.
     * 
     * EOF errors typically occur when:
     * - Server closes connection before sending complete response
     * - Load balancer timeout or reset
     * - Network interruption during data transfer
     * - Server-side rate limiting or overload
     * 
     * This method implements a robust retry strategy with:
     * - Fresh client creation on each retry (avoids stale connections)
     * - Exponential backoff with extra delay for EOF errors
     * - Proper resource cleanup on all code paths
     */
    private fun executeWithBodyRetry(
        client: OkHttpClient,
        request: Request,
        maxRetries: Int = 3  // Increased from 2 to handle flaky connections
    ): Pair<Response, String> {
        var lastException: IOException? = null
        var currentClient = client
        
        repeat(maxRetries + 1) { attempt ->
            var response: Response? = null
            try {
                if (attempt > 0) {
                    // Exponential backoff: 1s, 2s, 4s, ...
                    val baseDelayMs = 1000L * (1 shl (attempt - 1))
                    // Add extra delay for EOF errors - server may need time to recover
                    val eofExtraDelay = NetworkExceptionUtils.EOF_EXTRA_DELAY_MS
                    val totalDelayMs = baseDelayMs + eofExtraDelay
                    
                    Log.d(TAG, "Body read retry $attempt/$maxRetries after ${totalDelayMs}ms delay " +
                        "(base: ${baseDelayMs}ms, EOF extra: ${eofExtraDelay}ms)")
                    Thread.sleep(totalDelayMs)
                    
                    // Create fresh client to avoid reusing potentially stale connections
                    currentClient = channelFactory.createHttpClient()
                }
                
                response = currentClient.newCall(request).execute()
                
                // Check if we got a response at all
                if (response.body == null) {
                    response.close()
                    throw EOFIOException("Server returned empty response body")
                }
                
                val responseBody = try {
                    response.body!!.string()
                } catch (e: EOFException) {
                    throw EOFIOException("EOF while reading response body", e)
                } catch (e: IOException) {
                    if (NetworkExceptionUtils.isEOFException(e)) {
                        throw EOFIOException("EOF while reading response body: ${e.message}", e)
                    }
                    throw e
                }
                
                // Verify we got a non-empty response for login requests
                if (responseBody.isEmpty() && request.url.toString().contains("login")) {
                    throw EOFIOException("Server returned empty login response")
                }
                
                // Success - close response and return
                // Note: response.body?.string() already consumes and closes the body,
                // but we call close() on the response for completeness
                response.close()
                
                return response to responseBody
                
            } catch (e: IOException) {
                // Ensure response is closed on any error
                response?.close()
                
                if (NetworkExceptionUtils.isEOFException(e) || e is EOFIOException) {
                    Log.w(TAG, "EOF during request/body read (attempt ${attempt + 1}/${maxRetries + 1}): ${e.message}")
                    lastException = if (e is EOFIOException) e else EOFIOException(e.message ?: "EOF error", e)
                    // Continue to retry
                } else if (isConnectionResetError(e)) {
                    // Treat connection reset as EOF-like - server closed the connection
                    Log.w(TAG, "Connection reset during request (attempt ${attempt + 1}/${maxRetries + 1}): ${e.message}")
                    lastException = EOFIOException("Connection reset: ${e.message}", e)
                    // Continue to retry
                } else {
                    // Non-EOF error - propagate immediately
                    throw e
                }
            }
        }
        
        throw lastException ?: EOFIOException("Failed after ${maxRetries + 1} attempts")
    }
    
    /**
     * Check if the exception indicates a connection reset error
     */
    private fun isConnectionResetError(e: IOException): Boolean {
        val message = e.message ?: return false
        return message.contains("Connection reset", ignoreCase = true) ||
            message.contains("ECONNRESET", ignoreCase = true) ||
            message.contains("connection was reset", ignoreCase = true) ||
            message.contains("peer reset", ignoreCase = true)
    }
    
    /**
     * Parse login XML response
     */
    private fun parseLoginResponse(xml: String): LoginResult {
        val loginRegex = """<name>login</name>\s*<value><string>([\w]+)</string>""".toRegex()
        val loginMatch = loginRegex.find(xml)
        val loginStatus = loginMatch?.groupValues?.get(1)
        
        if (loginStatus == "true") {
            val sessionId = extractXmlValue(xml, "session_id") ?: ""
            val agentId = extractXmlValue(xml, "agent_id") ?: ""
            val simIp = extractXmlValue(xml, "sim_ip") ?: ""
            val simPort = extractXmlIntValue(xml, "sim_port")
            
            Log.d(TAG, "Login successful: session=$sessionId, agent=$agentId")
            
            return LoginResult.Success(
                sessionId = sessionId,
                agentId = agentId,
                simIp = simIp,
                simPort = simPort,
                responseXml = xml
            )
        } else {
            val errorMessage = extractXmlValue(xml, "message") 
                ?: extractXmlValue(xml, "reason")
                ?: "Login failed"
            val errorReason = extractXmlValue(xml, "reason") ?: "unknown"
            
            Log.w(TAG, "Login failed: $errorMessage (reason: $errorReason)")
            
            return LoginResult.Failure(
                message = errorMessage,
                errorCode = mapErrorReason(errorReason),
                technicalDetails = "Reason: $errorReason\nResponse preview: ${xml.take(500)}"
            )
        }
    }
    
    private fun extractXmlValue(xml: String, name: String): String? {
        val regex = """<name>$name</name>\s*<value><string>([^<]+)</string>""".toRegex()
        return regex.find(xml)?.groupValues?.get(1)
    }
    
    private fun extractXmlIntValue(xml: String, name: String): Int {
        val regex = """<name>$name</name>\s*<value><i4>(\d+)</i4>""".toRegex()
        return regex.find(xml)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }
    
    private fun mapErrorReason(reason: String): String {
        return when (reason.lowercase()) {
            "key" -> "INVALID_CREDENTIALS"
            "presence" -> "ALREADY_LOGGED_IN"
            "update" -> "UPDATE_REQUIRED"
            "tos" -> "TOS_NOT_ACCEPTED"
            else -> "LOGIN_REJECTED"
        }
    }
    
    /**
     * Check if an error is recoverable (transient)
     */
    private fun isRecoverableError(e: Throwable): Boolean {
        return when {
            e is SocketTimeoutException -> true
            e is UnknownHostException -> true  // DNS might resolve on retry
            e is EOFException -> true
            e is EOFIOException -> true
            NetworkExceptionUtils.isEOFException(e) -> true
            e is SSLException && isTransientSslError(e) -> true
            e is IOException && isTransientIoError(e) -> true
            e is StatusException -> isRecoverableGrpcStatus(e.status)
            e is StatusRuntimeException -> isRecoverableGrpcStatus(e.status)
            else -> false
        }
    }
    
    private fun isTransientSslError(e: SSLException): Boolean {
        val msg = e.message ?: return false
        return msg.contains("Connection reset", ignoreCase = true) ||
            msg.contains("closed", ignoreCase = true) ||
            msg.contains("timeout", ignoreCase = true)
    }
    
    private fun isTransientIoError(e: IOException): Boolean {
        val msg = e.message ?: return false
        return msg.contains("timeout", ignoreCase = true) ||
            msg.contains("reset", ignoreCase = true) ||
            msg.contains("closed", ignoreCase = true) ||
            msg.contains("ECONNRESET", ignoreCase = true)
    }
    
    private fun isRecoverableGrpcStatus(status: Status): Boolean {
        return when (status.code) {
            Status.Code.UNAVAILABLE,
            Status.Code.DEADLINE_EXCEEDED,
            Status.Code.ABORTED,
            Status.Code.RESOURCE_EXHAUSTED -> true
            else -> false
        }
    }
    
    /**
     * Get error code for an exception
     */
    private fun getErrorCode(e: Throwable): String {
        return when {
            e is SocketTimeoutException -> "TIMEOUT"
            e is UnknownHostException -> "DNS_FAILED"
            NetworkExceptionUtils.isEOFException(e) -> "CONNECTION_EOF"
            e is SSLException -> "SSL_ERROR"
            e is StatusException -> "GRPC_${e.status.code.name}"
            e is StatusRuntimeException -> "GRPC_${e.status.code.name}"
            else -> "NETWORK_ERROR"
        }
    }
    
    /**
     * Get user-friendly error message
     */
    private fun getUserFriendlyMessage(e: Throwable): String {
        val networkType = qualityManager.networkType.value
        
        return when {
            e is SocketTimeoutException -> 
                "Connection timed out. Please try again."
            e is UnknownHostException -> 
                "Cannot resolve server address. Check your internet connection."
            NetworkExceptionUtils.isEOFException(e) -> 
                "The server closed the connection. This is usually temporary - please try again."
            e is SSLException -> 
                "Secure connection failed. Check your network settings."
            e is StatusException -> getGrpcErrorMessage(e.status)
            e is StatusRuntimeException -> getGrpcErrorMessage(e.status)
            else -> "Network error: ${e.message ?: "Unknown error"}"
        }
    }
    
    private fun getGrpcErrorMessage(status: Status): String {
        return when (status.code) {
            Status.Code.UNAVAILABLE -> 
                "Service unavailable. The server may be down or unreachable."
            Status.Code.DEADLINE_EXCEEDED -> 
                "Request timeout. The operation took too long."
            Status.Code.UNAUTHENTICATED -> 
                "Authentication required. Please log in again."
            Status.Code.PERMISSION_DENIED -> 
                "Permission denied. You don't have access to this resource."
            Status.Code.NOT_FOUND -> 
                "Resource not found."
            Status.Code.INTERNAL -> 
                "Internal server error. Please try again later."
            else -> 
                "gRPC error: ${status.code.name}"
        }
    }
    
    private fun buildTechnicalDetails(e: Throwable, url: String): String {
        return buildString {
            appendLine("URL: $url")
            appendLine("Exception: ${e.javaClass.simpleName}")
            appendLine("Message: ${e.message ?: "none"}")
            e.cause?.let { cause ->
                appendLine("Cause: ${cause.javaClass.simpleName}: ${cause.message}")
            }
            appendLine()
            appendLine("Network Status:")
            appendLine("  Connected: ${qualityManager.isConnected.value}")
            appendLine("  Type: ${qualityManager.networkType.value}")
            appendLine("  Quality: ${qualityManager.quality.value}")
        }
    }
    
    /**
     * Signal connection death and wait for restoration
     */
    suspend fun signalConnectionDeathWaitForRestore() {
        Log.d(TAG, "Connection death signaled, attempting restoration...")
        
        stateManager.setStatus(NetworkStateManager.ConnectionStatus.RECONNECTING)
        emitEvent(ConnectionEvent.Reconnecting(0, 0))
        
        // Reset retry policies
        streamRetryPolicy.reset()
        
        // Attempt reconnection
        val success = connectOrReconnect()
        
        if (!success) {
            Log.e(TAG, "Failed to restore connection")
            stateManager.setStatus(NetworkStateManager.ConnectionStatus.ERROR)
        }
    }
    
    /**
     * Force a reconnection
     */
    suspend fun forceReconnect() {
        Log.d(TAG, "Force reconnect requested")
        stateManager.setForceReconnect(true)
        
        // Disconnect current connection
        disconnect()
        
        // Reset state
        streamRetryPolicy.reset()
        loginRetryPolicy.reset()
        
        // Reconnect
        connectOrReconnect()
    }
    
    /**
     * Disconnect from the network
     */
    fun disconnect() {
        Log.d(TAG, "Disconnecting...")
        stateManager.setLogoutInProgress(true)
        
        synchronized(connectionLock) {
            currentChannel?.let { channelFactory.shutdownChannel(it) }
            currentChannel = null
            currentHttpClient = null
        }
        
        stateManager.setStatus(NetworkStateManager.ConnectionStatus.DISCONNECTED)
        stateManager.setLogoutInProgress(false)
        emitEvent(ConnectionEvent.Disconnected)
    }
    
    /**
     * Get the current gRPC channel (creates one if needed)
     */
    fun getChannel(host: String, port: Int, useTls: Boolean = true): ManagedChannel {
        synchronized(connectionLock) {
            return currentChannel ?: channelFactory.createChannel(host, port, useTls).also {
                currentChannel = it
            }
        }
    }
    
    /**
     * Log network diagnostics
     */
    fun logNetworkDiagnostics() {
        Log.d(TAG, "=== NETWORK DIAGNOSTICS ===")
        qualityManager.logNetworkDiagnostics()
        stateManager.logConnectionDetails()
        
        val loginStats = loginRetryPolicy.getStats()
        val streamStats = streamRetryPolicy.getStats()
        
        Log.d(TAG, "=== RETRY STATS ===")
        Log.d(TAG, "  Login Policy - Failures: ${loginStats.failuresInARow}, " +
            "Total: ${loginStats.totalErrors}, InError: ${loginStats.isInErrorState}")
        Log.d(TAG, "  Stream Policy - Failures: ${streamStats.failuresInARow}, " +
            "Total: ${streamStats.totalErrors}, InError: ${streamStats.isInErrorState}")
    }
    
    /**
     * Validate network connection with a fresh check.
     * This is more reliable than just checking the cached isConnected value,
     * as the network callback may not have fired yet.
     */
    private fun validateNetworkConnection(): Boolean {
        // First check the cached value
        if (qualityManager.isConnected.value) {
            return true
        }
        
        // If cached value is false, do a fresh network check
        // This handles cases where the network callback hasn't updated yet
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? android.net.ConnectivityManager ?: return false
            
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            
            val hasInternet = capabilities.hasCapability(
                android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
            val isValidated = capabilities.hasCapability(
                android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
            
            val isConnected = hasInternet && isValidated
            
            if (isConnected) {
                Log.d(TAG, "Fresh network check: connected (cached value was false)")
            }
            
            return isConnected
        } catch (e: Exception) {
            Log.e(TAG, "Error checking network connectivity: ${e.message}")
            // Fall back to cached value on error
            return qualityManager.isConnected.value
        }
    }
    
    /**
     * Build a diagnostics string for error reporting
     */
    private fun buildNetworkDiagnosticsString(): String {
        val report = qualityManager.getQualityReport()
        return buildString {
            appendLine("Network Status:")
            appendLine("  Connected: ${report.isConnected}")
            appendLine("  Type: ${report.networkType}")
            appendLine("  Quality: ${report.quality}")
            appendLine("  Bandwidth: ${report.estimatedBandwidthKbps}kbps")
            appendLine("  Error Rate: ${(report.errorRate * 100).toInt()}%")
        }
    }
    
    private fun emitEvent(event: ConnectionEvent) {
        scope.launch {
            _connectionEvents.emit(event)
        }
    }
    
    /**
     * Clean up resources
     */
    fun shutdown() {
        Log.d(TAG, "Shutting down CoreNetworkingService")
        scope.cancel()
        channelFactory.shutdownAll()
        qualityManager.stopMonitoring()
        stateManager.reset()
    }
}
