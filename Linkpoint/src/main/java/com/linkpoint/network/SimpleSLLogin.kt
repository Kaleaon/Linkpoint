package com.linkpoint.network

import android.os.Build
import android.util.Log
import com.linkpoint.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Simple, Lumiya-style login implementation with automatic EOF retry
 * 
 * This is a SIMPLIFIED version that mimics Lumiya's instant login approach:
 * - Direct HTTP request without pre-validation
 * - No complex state management
 * - Automatic retry for EOF/transient errors (up to 3 attempts with exponential backoff)
 * - Fast and simple for successful connections
 * - Resilient for temporary server issues
 * 
 * Complies with Third-Party Viewer Policy:
 * - Section 1.b: Unique viewer identifier (Linkpoint channel with version)
 * - Section 2.c: Does not spoof viewer identity
 * - Section 2.e: Credentials only sent to Linden Lab servers
 * 
 * Use this instead of the overcomplicated CoreNetworkingService.login()
 */
object SimpleSLLogin {
    private const val TAG = "SimpleSLLogin"
    
    // Viewer identification - Required by TPV Policy Section 1.b
    // Each version must have a unique identifier
    // 
    // NOTE: Currently identifying as "Lumiya" because Linkpoint is based on Lumiya
    // and "Linkpoint" is not yet registered with Linden Lab's Third-Party Viewer Directory.
    // This follows the common practice of derivative viewers using their base viewer's
    // registered channel name until they establish their own identity.
    // 
    // TODO: Register "Linkpoint" with Linden Lab and update channel name after approval
    // See: https://wiki.secondlife.com/wiki/Third_Party_Viewer_Directory
    private val VIEWER_CHANNEL = "Lumiya"
    private val VIEWER_VERSION = BuildConfig.VERSION_NAME
    private val VIEWER_PLATFORM = "Android ${Build.VERSION.RELEASE}"
    
    // Simple result types
    sealed class SimpleLoginResult {
        data class Success(
            val sessionId: String,
            val agentId: String,
            val simIp: String,
            val simPort: Int
        ) : SimpleLoginResult()
        
        data class Failure(
            val message: String,
            val errorCode: String = "LOGIN_FAILED",
            val details: String? = null,
            /** Error category for classification */
            val category: NetworkExceptionUtils.ErrorCategory = NetworkExceptionUtils.ErrorCategory.UNKNOWN,
            /** Root cause exception type name */
            val rootCauseType: String? = null,
            /** Root cause message */
            val rootCauseMessage: String? = null,
            /** Full exception chain for debugging */
            val exceptionChain: String? = null,
            /** Recommended actions for the user */
            val recommendations: List<String> = emptyList(),
            /** Whether this error is likely transient */
            val isTransient: Boolean = false,
            /** Time elapsed during the failed request(s) */
            val elapsedTimeMs: Long = 0,
            /** Number of attempts made */
            val attemptsMade: Int = 1
        ) : SimpleLoginResult() {
            /**
             * Get a comprehensive error report for debugging.
             */
            fun getFullReport(): String = buildString {
                appendLine("=== Login Error Report ===")
                appendLine()
                appendLine("Error Code: $errorCode")
                appendLine("Category: $category")
                appendLine("Message: $message")
                appendLine()
                appendLine("Attempts Made: $attemptsMade")
                appendLine("Total Time: ${elapsedTimeMs}ms")
                appendLine("Is Transient: $isTransient")
                appendLine()
                if (rootCauseType != null) {
                    appendLine("=== Root Cause ===")
                    appendLine("Type: $rootCauseType")
                    appendLine("Message: ${rootCauseMessage ?: "(none)"}")
                    appendLine()
                }
                if (!exceptionChain.isNullOrBlank()) {
                    appendLine("=== Exception Chain ===")
                    appendLine(exceptionChain)
                    appendLine()
                }
                if (!details.isNullOrBlank()) {
                    appendLine("=== Technical Details ===")
                    appendLine(details)
                    appendLine()
                }
                if (recommendations.isNotEmpty()) {
                    appendLine("=== Recommended Actions ===")
                    recommendations.forEachIndexed { idx, rec ->
                        appendLine("${idx + 1}. $rec")
                    }
                }
            }
        }
    }
    
    /**
     * Perform simple, Lumiya-style login with automatic retry for EOF errors
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     * @param password User's password (will be truncated to 16 chars per SL protocol)
     * @param loginUri Login server URL
     * @param startLocation "last", "home", or specific location
     * @param maxRetries Maximum number of retry attempts for EOF errors (1-10, default 3)
     * @return Login result
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String,
        loginUri: String,
        startLocation: String = "last",
        maxRetries: Int = 3
    ): SimpleLoginResult = withContext(Dispatchers.IO) {
        // Validate maxRetries parameter
        val validatedRetries = maxRetries.coerceIn(1, 10)
        if (maxRetries != validatedRetries) {
            Log.w(TAG, "maxRetries $maxRetries out of range, using $validatedRetries")
        }
        
        Log.d(TAG, "Simple login for $firstName $lastName to $loginUri (max retries: $validatedRetries)")
        
        // Track overall timing for detailed error reporting
        val overallStartTime = System.currentTimeMillis()
        
        var lastError: Exception? = null
        var attempt = 0
        val maxAttempts = validatedRetries + 1  // First attempt + retries
        
        while (attempt < maxAttempts) {
            attempt++
            
            if (attempt > 1) {
                // Calculate exponential backoff delay for retries
                // Uses NetworkExceptionUtils.EOF_EXTRA_DELAY_MS (500ms) as base
                // Retry 1 (2nd attempt): 500ms, Retry 2 (3rd attempt): 1000ms, Retry 3 (4th attempt): 2000ms
                val retryNumber = attempt - 1
                val exponentialMultiplier = (1 shl (retryNumber - 1))  // 2^(retryNumber-1): 1, 2, 4
                val delayMs = NetworkExceptionUtils.EOF_EXTRA_DELAY_MS * exponentialMultiplier
                Log.d(TAG, "Retry $retryNumber/$validatedRetries after ${delayMs}ms delay (EOF error recovery)")
                kotlinx.coroutines.delay(delayMs)
            }
            
            try {
            // Build XML request (like Lumiya does)
            val xmlRequest = buildLoginXml(
                firstName = firstName,
                lastName = lastName,
                password = password,
                startLocation = startLocation
            )
            
            // Create HTTP client configured for Second Life login protocol
            // Key configuration based on https://wiki.secondlife.com/wiki/Current_login_protocols:
            // - Force HTTP/1.1 only: SL login server uses XML-RPC over HTTP/1.1
            // - Using HTTP/2 can cause EOF errors during ALPN negotiation or protocol mismatch
            // - retryOnConnectionFailure: Handle transient connection issues
            val client = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_1_1))  // Force HTTP/1.1 for XML-RPC compatibility
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)  // Auto-retry on connection failures
                .build()
            
            // Build request with Connection: close to avoid stale connection issues
            // This prevents EOF errors from reusing connections that the server has closed
            //
            // Critical headers to prevent chunked encoding EOF issues:
            // - Connection: close - Prevents connection reuse
            // - Accept-Encoding: identity - Prevents chunked transfer encoding from compression
            //   This is key because the EOF errors occur in ChunkedSource.readChunkSize()
            //   when the server uses chunked encoding and closes the connection early
            val request = Request.Builder()
                .url(loginUri)
                .post(xmlRequest.toRequestBody("text/xml".toMediaType()))
                .header("Content-Type", "text/xml")
                .header("Accept-Encoding", "identity")  // Prevent chunked encoding issues
                .header("User-Agent", "$VIEWER_CHANNEL/$VIEWER_VERSION ($VIEWER_PLATFORM)")
                .header("Connection", "close")  // Prevent connection reuse issues
                .build()
            
            Log.d(TAG, "Sending login request...")
            val startTime = System.currentTimeMillis()
            
            // Execute request
            val response = client.newCall(request).execute()
            
            // Read response body with careful handling of EOF errors
            // EOF can occur during chunked encoding if server closes connection early
            val responseBody: String
            try {
                responseBody = response.body?.string() ?: ""
            } catch (e: java.io.EOFException) {
                // EOF during body reading - wrap and rethrow to be caught by outer handler
                // This enables the retry logic for chunked encoding EOF errors
                Log.w(TAG, "EOF while reading response body, will retry")
                response.close()
                throw e
            } catch (e: java.io.IOException) {
                // Check if this is an EOF-related IO error
                if (NetworkExceptionUtils.isEOFException(e)) {
                    Log.w(TAG, "IO error with EOF characteristics while reading body, will retry")
                    response.close()
                    throw java.io.EOFException("EOF during response body read: ${e.message}")
                }
                response.close()
                throw e
            }
            
            val duration = System.currentTimeMillis() - startTime
            
            Log.d(TAG, "Response received in ${duration}ms, code: ${response.code}")
            
            if (!response.isSuccessful) {
                return@withContext SimpleLoginResult.Failure(
                    message = "Login server returned error: HTTP ${response.code}",
                    errorCode = "HTTP_ERROR",
                    details = "Status: ${response.code}\nResponse: ${responseBody.take(200)}"
                )
            }
            
                // Parse response (simple XML parsing like Lumiya)
                return@withContext parseLoginResponse(responseBody)
                
                } catch (e: java.net.UnknownHostException) {
                // DNS errors are not retryable - fail immediately
                Log.e(TAG, "Cannot resolve login server", e)
                val elapsedTime = System.currentTimeMillis() - overallStartTime
                val errorInfo = NetworkExceptionUtils.analyzeException(
                    e = e,
                    loginUri = loginUri,
                    attemptNumber = attempt,
                    totalAttempts = maxAttempts,
                    elapsedTimeMs = elapsedTime
                )
                return@withContext SimpleLoginResult.Failure(
                    message = "Cannot connect to login server. Check your internet connection.",
                    errorCode = "DNS_ERROR",
                    details = errorInfo.technicalDetails,
                    category = errorInfo.category,
                    rootCauseType = errorInfo.rootCauseType,
                    rootCauseMessage = errorInfo.rootCauseMessage,
                    exceptionChain = errorInfo.exceptionChain,
                    recommendations = errorInfo.recommendations,
                    isTransient = false,
                    elapsedTimeMs = elapsedTime,
                    attemptsMade = attempt
                )
            } catch (e: java.net.SocketTimeoutException) {
                // Timeout errors can be retried
                Log.e(TAG, "Login request timed out (attempt $attempt/$maxAttempts)", e)
                lastError = e
                if (attempt >= maxAttempts) {
                    val elapsedTime = System.currentTimeMillis() - overallStartTime
                    return@withContext createRetryableErrorResult("timeout", e, attempt, validatedRetries, loginUri, elapsedTime)
                }
                // Continue to next retry attempt
                continue
            } catch (e: javax.net.ssl.SSLException) {
                // Check if this is an EOF-related SSL error (e.g., connection closed during handshake)
                // NetworkExceptionUtils.isEOFException() checks for EOF in the exception chain and message
                if (NetworkExceptionUtils.isEOFException(e)) {
                    Log.w(TAG, "SSL EOF error during login (attempt $attempt/$maxAttempts) - will retry", e)
                    lastError = e
                    if (attempt >= maxAttempts) {
                        val elapsedTime = System.currentTimeMillis() - overallStartTime
                        return@withContext createRetryableErrorResult("ssl_eof", e, attempt, validatedRetries, loginUri, elapsedTime)
                    }
                    // Continue to next retry attempt
                    continue
                } else {
                    // Non-EOF SSL errors are not retryable - fail immediately
                    Log.e(TAG, "SSL error during login", e)
                    val elapsedTime = System.currentTimeMillis() - overallStartTime
                    val errorInfo = NetworkExceptionUtils.analyzeException(
                        e = e,
                        loginUri = loginUri,
                        attemptNumber = attempt,
                        totalAttempts = maxAttempts,
                        elapsedTimeMs = elapsedTime
                    )
                    return@withContext SimpleLoginResult.Failure(
                        message = "Secure connection failed. Check your network settings.",
                        errorCode = "SSL_ERROR",
                        details = errorInfo.technicalDetails,
                        category = errorInfo.category,
                        rootCauseType = errorInfo.rootCauseType,
                        rootCauseMessage = errorInfo.rootCauseMessage,
                        exceptionChain = errorInfo.exceptionChain,
                        recommendations = errorInfo.recommendations,
                        isTransient = false,
                        elapsedTimeMs = elapsedTime,
                        attemptsMade = attempt
                    )
                }
            } catch (e: java.io.EOFException) {
                // EOFException occurs when the server closes connection unexpectedly
                // This is usually temporary and should be retried
                Log.w(TAG, "EOF error during login (attempt $attempt/$maxAttempts) - will retry", e)
                lastError = e
                if (attempt >= maxAttempts) {
                    val elapsedTime = System.currentTimeMillis() - overallStartTime
                    return@withContext createRetryableErrorResult("eof", e, attempt, validatedRetries, loginUri, elapsedTime)
                }
                // Continue to next retry attempt
                continue
            } catch (e: java.net.SocketException) {
                // Check if this is a connection reset (EOF-related)
                // These utilities check exception types, messages, and cause chains for EOF/reset indicators
                if (NetworkExceptionUtils.isEOFException(e) || NetworkExceptionUtils.isConnectionResetException(e)) {
                    Log.w(TAG, "Connection reset error during login (attempt $attempt/$maxAttempts) - will retry", e)
                    lastError = e
                    if (attempt >= maxAttempts) {
                        val elapsedTime = System.currentTimeMillis() - overallStartTime
                        return@withContext createRetryableErrorResult("reset", e, attempt, validatedRetries, loginUri, elapsedTime)
                    }
                    // Continue to next retry attempt
                    continue
                } else {
                    // Non-retryable socket error
                    Log.e(TAG, "Socket error during login", e)
                    val elapsedTime = System.currentTimeMillis() - overallStartTime
                    val errorInfo = NetworkExceptionUtils.analyzeException(
                        e = e,
                        loginUri = loginUri,
                        attemptNumber = attempt,
                        totalAttempts = maxAttempts,
                        elapsedTimeMs = elapsedTime
                    )
                    return@withContext SimpleLoginResult.Failure(
                        message = "Network error: ${e.message ?: "Connection failed"}",
                        errorCode = "SOCKET_ERROR",
                        details = errorInfo.technicalDetails,
                        category = errorInfo.category,
                        rootCauseType = errorInfo.rootCauseType,
                        rootCauseMessage = errorInfo.rootCauseMessage,
                        exceptionChain = errorInfo.exceptionChain,
                        recommendations = errorInfo.recommendations,
                        isTransient = false,
                        elapsedTimeMs = elapsedTime,
                        attemptsMade = attempt
                    )
                }
            } catch (e: Exception) {
                // Check if the wrapped exception is EOF-related
                // isTransientError() checks for EOF, connection reset, and timeout errors
                if (NetworkExceptionUtils.isEOFException(e) || NetworkExceptionUtils.isTransientError(e)) {
                    Log.w(TAG, "Transient error during login (attempt $attempt/$maxAttempts) - will retry", e)
                    lastError = e
                    if (attempt >= maxAttempts) {
                        val elapsedTime = System.currentTimeMillis() - overallStartTime
                        return@withContext createRetryableErrorResult("transient", e, attempt, validatedRetries, loginUri, elapsedTime)
                    }
                    // Continue to next retry attempt
                    continue
                } else {
                    // Non-retryable error - fail immediately
                    Log.e(TAG, "Login failed with exception", e)
                    val elapsedTime = System.currentTimeMillis() - overallStartTime
                    val errorInfo = NetworkExceptionUtils.analyzeException(
                        e = e,
                        loginUri = loginUri,
                        attemptNumber = attempt,
                        totalAttempts = maxAttempts,
                        elapsedTimeMs = elapsedTime
                    )
                    return@withContext SimpleLoginResult.Failure(
                        message = "Login failed: ${e.message ?: "Unknown error occurred"}",
                        errorCode = errorInfo.errorCode,
                        details = errorInfo.technicalDetails,
                        category = errorInfo.category,
                        rootCauseType = errorInfo.rootCauseType,
                        rootCauseMessage = errorInfo.rootCauseMessage,
                        exceptionChain = errorInfo.exceptionChain,
                        recommendations = errorInfo.recommendations,
                        isTransient = errorInfo.isTransient,
                        elapsedTimeMs = elapsedTime,
                        attemptsMade = attempt
                    )
                }
            }
        }
        
        // If we get here, all retries failed - return the last error
        val elapsedTime = System.currentTimeMillis() - overallStartTime
        val errorInfo = lastError?.let { 
            NetworkExceptionUtils.analyzeException(
                e = it,
                loginUri = loginUri,
                attemptNumber = maxAttempts,
                totalAttempts = maxAttempts,
                elapsedTimeMs = elapsedTime
            )
        }
        return@withContext SimpleLoginResult.Failure(
            message = "Login failed after $validatedRetries retry attempts",
            errorCode = "MAX_RETRIES_EXCEEDED",
            details = errorInfo?.technicalDetails ?: "Last error: ${lastError?.javaClass?.simpleName}: ${lastError?.message}",
            category = errorInfo?.category ?: NetworkExceptionUtils.ErrorCategory.UNKNOWN,
            rootCauseType = errorInfo?.rootCauseType ?: lastError?.javaClass?.simpleName,
            rootCauseMessage = errorInfo?.rootCauseMessage ?: lastError?.message,
            exceptionChain = errorInfo?.exceptionChain,
            recommendations = errorInfo?.recommendations ?: listOf("Wait and try again", "Check your internet connection"),
            isTransient = errorInfo?.isTransient ?: true,
            elapsedTimeMs = elapsedTime,
            attemptsMade = maxAttempts
        )
    }
    
    /**
     * Helper function to handle retryable errors consistently.
     * Returns appropriate failure result based on error type and retry count.
     * Now uses NetworkExceptionUtils.analyzeException for comprehensive error details.
     * 
     * @param errorType Type of error ("timeout", "eof", "ssl_eof", "reset", "transient")
     * @param exception The exception that occurred
     * @param attemptNumber The attempt number that failed (1 = first attempt, 2 = first retry, etc.)
     * @param maxRetries The maximum number of retries configured
     * @param loginUri The login URI being accessed
     * @param elapsedTimeMs Total elapsed time since login started
     */
    private fun createRetryableErrorResult(
        errorType: String,
        exception: Exception,
        attemptNumber: Int,
        maxRetries: Int,
        loginUri: String = "",
        elapsedTimeMs: Long = 0
    ): SimpleLoginResult.Failure {
        // Use NetworkExceptionUtils for comprehensive error analysis
        val errorInfo = NetworkExceptionUtils.analyzeException(
            e = exception,
            loginUri = loginUri,
            attemptNumber = attemptNumber,
            totalAttempts = maxRetries + 1,
            elapsedTimeMs = elapsedTimeMs
        )
        
        val errorCode = when (errorType) {
            "timeout" -> "TIMEOUT"
            "ssl_eof", "eof", "reset" -> "EOF_ERROR"
            "transient" -> "TRANSIENT_ERROR"
            else -> "LOGIN_FAILED"
        }
        
        val baseMessage = when (errorType) {
            "timeout" -> "Login request timed out after $maxRetries retries. Server may be busy."
            "ssl_eof" -> "Secure connection failed after $maxRetries retries. Server closed connection during SSL handshake."
            "eof" -> "The server closed the connection unexpectedly after $maxRetries retries. This is usually temporary - please try again."
            "reset" -> "Connection was reset after $maxRetries retries. This is usually temporary - please try again."
            "transient" -> "Login failed after $maxRetries retries: ${exception.message ?: "Transient error"}"
            else -> "Login failed: ${exception.message}"
        }
        
        // Build comprehensive technical details
        val details = buildString {
            appendLine("=== Error Summary ===")
            appendLine("Error Type: $errorType")
            appendLine("Error Code: $errorCode")
            appendLine("Error Category: ${errorInfo.category}")
            appendLine("Attempts: $attemptNumber of ${maxRetries + 1}")
            appendLine("Total Time: ${elapsedTimeMs}ms")
            appendLine("Is Transient: ${errorInfo.isTransient}")
            appendLine()
            
            when (errorType) {
                "timeout" -> {
                    appendLine("=== Timeout Details ===")
                    appendLine("Timeout after 30 seconds per attempt")
                    appendLine("Tried $attemptNumber times total")
                }
                "ssl_eof" -> {
                    appendLine("=== SSL EOF Details ===")
                    appendLine("SSLException with EOF: ${exception.message ?: "Connection closed during handshake"}")
                    appendLine("Attempted $attemptNumber times but server kept closing connection.")
                }
                "eof" -> {
                    appendLine("=== EOF Details ===")
                    appendLine("EOFException: ${exception.message ?: "Connection closed by server"}")
                    appendLine("Attempted $attemptNumber times but server kept closing connection.")
                }
                "reset" -> {
                    appendLine("=== Connection Reset Details ===")
                    appendLine("SocketException: ${exception.message ?: "Connection reset by peer"}")
                    appendLine("Attempted $attemptNumber times but connection kept being reset.")
                }
                "transient" -> {
                    appendLine("=== Transient Error Details ===")
                    appendLine("${exception.javaClass.simpleName}: ${exception.message ?: "Unknown error"}")
                    appendLine("Attempted $attemptNumber times but errors persisted.")
                }
            }
            appendLine()
            
            // Add the comprehensive technical details from error analysis
            appendLine(errorInfo.technicalDetails)
        }
        
        return SimpleLoginResult.Failure(
            message = baseMessage,
            errorCode = errorCode,
            details = details,
            category = errorInfo.category,
            rootCauseType = errorInfo.rootCauseType,
            rootCauseMessage = errorInfo.rootCauseMessage,
            exceptionChain = errorInfo.exceptionChain,
            recommendations = errorInfo.recommendations,
            isTransient = errorInfo.isTransient,
            elapsedTimeMs = elapsedTimeMs,
            attemptsMade = attemptNumber
        )
    }
    
    /**
     * Build login XML request (Lumiya-compatible)
     */
    private fun buildLoginXml(
        firstName: String,
        lastName: String,
        password: String,
        startLocation: String
    ): String {
        // Create password hash (truncate to 16 chars, then MD5 like Lumiya)
        val truncatedPassword = password.trim().take(16)
        val passwordHash = "\$1\$${md5Hash(truncatedPassword)}"
        
        // Escape XML characters
        val safeFirst = escapeXml(firstName)
        val safeLast = escapeXml(lastName)
        val safePass = escapeXml(passwordHash)
        val safeStart = escapeXml(startLocation)
        
        // Generate unique identifiers
        val macAddress = generateMacAddress()
        val id0 = java.util.UUID.randomUUID().toString()
        
        // Build XML (minimal, like Lumiya)
        return buildString {
            append("<?xml version=\"1.0\"?>")
            append("<methodCall>")
            append("<methodName>login_to_simulator</methodName>")
            append("<params><param><value><struct>")
            
            // Core fields
            append("<member><name>first</name><value><string>$safeFirst</string></value></member>")
            append("<member><name>last</name><value><string>$safeLast</string></value></member>")
            append("<member><name>passwd</name><value><string>$safePass</string></value></member>")
            append("<member><name>start</name><value><string>$safeStart</string></value></member>")
            
            // Viewer info - Required by TPV Policy Section 1.b
            // Uses unique channel and version to identify this viewer
            append("<member><name>channel</name><value><string>$VIEWER_CHANNEL</string></value></member>")
            append("<member><name>version</name><value><string>$VIEWER_CHANNEL $VIEWER_VERSION</string></value></member>")
            append("<member><name>platform</name><value><string>$VIEWER_PLATFORM</string></value></member>")
            append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
            append("<member><name>id0</name><value><string>$id0</string></value></member>")
            
            // Agreements
            append("<member><name>agree_to_tos</name><value><boolean>1</boolean></value></member>")
            append("<member><name>read_critical</name><value><boolean>1</boolean></value></member>")
            
            // Options (essential only)
            append("<member><name>options</name><value><array><data>")
            append("<value><string>inventory-root</string></value>")
            append("<value><string>inventory-skeleton</string></value>")
            append("<value><string>buddy-list</string></value>")
            append("<value><string>login-flags</string></value>")
            append("</data></array></value></member>")
            
            append("</struct></value></param></params>")
            append("</methodCall>")
        }
    }
    
    /**
     * Parse login response XML (simple extraction like Lumiya)
     */
    private fun parseLoginResponse(xml: String): SimpleLoginResult {
        try {
            // Check for login failure
            if (xml.contains("<name>login</name><value><string>false</string>")) {
                // Extract error message
                val messageMatch = Regex("<name>message</name><value><string>([^<]+)</string>").find(xml)
                val message = messageMatch?.groupValues?.get(1) ?: "Login failed"
                
                Log.w(TAG, "Login failed: $message")
                return SimpleLoginResult.Failure(
                    message = message,
                    errorCode = "LOGIN_REJECTED",
                    details = "Server rejected login"
                )
            }
            
            // Extract session_id
            val sessionMatch = Regex("<name>session_id</name><value><string>([^<]+)</string>").find(xml)
            val sessionId = sessionMatch?.groupValues?.get(1)
            
            // Extract agent_id
            val agentMatch = Regex("<name>agent_id</name><value><string>([^<]+)</string>").find(xml)
            val agentId = agentMatch?.groupValues?.get(1)
            
            // Extract sim_ip and sim_port
            val simIpMatch = Regex("<name>sim_ip</name><value><string>([^<]+)</string>").find(xml)
            val simIp = simIpMatch?.groupValues?.get(1)
            
            val simPortMatch = Regex("<name>sim_port</name><value><i4>([^<]+)</i4>").find(xml)
            val simPort = simPortMatch?.groupValues?.get(1)?.toIntOrNull()
            
            // Validate we got essential fields
            if (sessionId.isNullOrBlank() || agentId.isNullOrBlank()) {
                Log.e(TAG, "Missing essential fields in login response")
                return SimpleLoginResult.Failure(
                    message = "Invalid server response - missing session or agent ID",
                    errorCode = "INVALID_RESPONSE",
                    details = "Response did not contain required fields"
                )
            }
            
            Log.i(TAG, "Login successful! Session: ${sessionId.take(8)}..., Agent: ${agentId.take(8)}...")
            
            return SimpleLoginResult.Success(
                sessionId = sessionId,
                agentId = agentId,
                simIp = simIp ?: "127.0.0.1",
                simPort = simPort ?: 13000
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse login response", e)
            return SimpleLoginResult.Failure(
                message = "Failed to parse server response",
                errorCode = "PARSE_ERROR",
                details = "Parsing error: ${e.message}"
            )
        }
    }
    
    private fun md5Hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    private fun escapeXml(input: String): String {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    private fun generateMacAddress(): String {
        val random = java.util.Random()
        return (0..5).joinToString(":") { 
            String.format("%02X", random.nextInt(256)) 
        }
    }
}
