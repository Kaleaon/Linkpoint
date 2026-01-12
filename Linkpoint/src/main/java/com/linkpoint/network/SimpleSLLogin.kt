package com.linkpoint.network

import android.os.Build
import android.util.Log
import com.linkpoint.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.EOFException
import java.io.IOException
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
            val simPort: Int,
            /** MFA hash returned by server for future logins */
            val mfaHash: String? = null
        ) : SimpleLoginResult()
        
        /**
         * MFA (Multi-Factor Authentication) challenge required.
         * The user must provide a TOTP code from their authenticator app.
         * After obtaining the code, call login() again with the token parameter.
         */
        data class MFARequired(
            val message: String,
            val agentId: String? = null
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
     * @param maxRetries Maximum number of retry attempts for EOF errors (1-10, default 4)
     * @param mfaToken TOTP code from authenticator app (required after MFARequired result)
     * @param mfaHash Cached MFA hash from previous successful login (allows skipping MFA)
     * @return Login result (Success, MFARequired, or Failure)
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String,
        loginUri: String,
        startLocation: String = "last",
        maxRetries: Int = 4,
        mfaToken: String = "",
        mfaHash: String = ""
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
                startLocation = startLocation,
                mfaToken = mfaToken,
                mfaHash = mfaHash
            )
            
            // Create HTTP client configured for Second Life login protocol
            // Key configuration based on https://wiki.secondlife.com/wiki/Current_login_protocols:
            // - Force HTTP/1.1 only: SL login server uses XML-RPC over HTTP/1.1
            // - Using HTTP/2 can cause EOF errors during ALPN negotiation or protocol mismatch
            // - retryOnConnectionFailure: Handle transient connection issues
            // - ChunkedResponseInterceptor: Pre-buffers response to handle EOF gracefully
            val client = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_1_1))  // Force HTTP/1.1 for XML-RPC compatibility
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)  // Auto-retry on connection failures
                .addNetworkInterceptor(ChunkedResponseInterceptor())  // Handle EOF during chunked reads
                .build()
            
            // Critical headers to reduce EOF issues with streaming responses:
            // - Connection: close - Prevents connection reuse
            // - Accept-Encoding: identity - Disables response body compression (gzip/deflate)
            //   Disabling compression makes the server more likely to send a simple,
            //   uncompressed body, which can help avoid or simplify chunked/EOF problems
            //   seen in ChunkedSource.readChunkSize() when the server closes the connection early
            //
            // IMPORTANT: Use ByteArray.toRequestBody() instead of String.toRequestBody()
            // because OkHttp automatically adds "charset=utf-8" to the Content-Type header
            // when using String.toRequestBody(). Second Life's login server returns HTTP 400
            // if the Content-Type includes a charset parameter - it requires exactly "text/xml".
            val xmlBytes = xmlRequest.toByteArray(Charsets.UTF_8)
            val request = Request.Builder()
                .url(loginUri)
                .post(xmlBytes.toRequestBody("text/xml".toMediaType()))
                .header("Content-Type", "text/xml")
                .header("Accept-Encoding", "identity")  // Disable compression to simplify stream handling
                .header("User-Agent", "$VIEWER_CHANNEL/$VIEWER_VERSION ($VIEWER_PLATFORM)")
                .header("Connection", "close")  // Prevent connection reuse issues
                .build()
            
            Log.d(TAG, "Sending login request...")
            val startTime = System.currentTimeMillis()
            
            // Execute request
            val response = client.newCall(request).execute()
            
            // Use try-finally to ensure response is always closed properly in all paths
            try {
                // Read response body with resilient EOF handling
                // Defense-in-depth: The ChunkedResponseInterceptor handles EOF at the network layer,
                // but readBodyResilient() provides backup handling in case the interceptor misses
                // edge cases or if EOF occurs after the interceptor's processing.
                // If we hit EOF during reading and have partial but usable data, we try to use it.
                val (responseBody, hitEof) = readBodyResilient(response)
                
                // If we hit EOF but the response seems incomplete, retry
                if (hitEof && !isResponseUsable(responseBody)) {
                    Log.w(TAG, "EOF during body reading with incomplete response, will retry")
                    throw EOFException("Incomplete response after EOF")
                }
                
                if (hitEof) {
                    Log.i(TAG, "Successfully recovered from EOF with usable response (${responseBody.length} chars)")
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
            } finally {
                // Always close response to avoid resource leaks
                response.close()
            }
                
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
            } catch (e: EOFException) {
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
     * Build login XML request (Lumiya-compatible with MFA support)
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     * @param password User's password
     * @param startLocation Start location ("last", "home", or specific)
     * @param mfaToken TOTP code from authenticator app (empty if not responding to MFA challenge)
     * @param mfaHash Cached MFA hash from previous successful login (empty if not available)
     */
    private fun buildLoginXml(
        firstName: String,
        lastName: String,
        password: String,
        startLocation: String,
        mfaToken: String = "",
        mfaHash: String = ""
    ): String {
        // Create password hash (truncate to 16 chars, then MD5 like Lumiya)
        val truncatedPassword = password.trim().take(16)
        val passwordHash = "\$1\$${md5Hash(truncatedPassword)}"
        
        // Escape XML characters
        val safeFirst = escapeXml(firstName)
        val safeLast = escapeXml(lastName)
        val safePass = escapeXml(passwordHash)
        val safeStart = escapeXml(startLocation)
        val safeToken = escapeXml(mfaToken)
        val safeMfaHash = escapeXml(mfaHash)
        
        // Generate unique identifiers
        val macAddress = generateMacAddress()
        val id0 = java.util.UUID.randomUUID().toString()
        // Generate viewer_digest - required by Second Life login server
        val viewerDigest = java.util.UUID.randomUUID().toString()
        
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
            
            // MFA fields - required by Second Life MFA login flow
            // See: https://wiki.secondlife.com/wiki/User:Brad_Linden/Login_MFA
            // - token: TOTP code from authenticator app (empty string if not responding to challenge)
            // - mfa_hash: Cached hash from previous successful MFA (allows skipping token entry)
            append("<member><name>token</name><value><string>$safeToken</string></value></member>")
            append("<member><name>mfa_hash</name><value><string>$safeMfaHash</string></value></member>")
            
            // Viewer info - Required by TPV Policy Section 1.b
            // Uses unique channel and version to identify this viewer
            append("<member><name>channel</name><value><string>$VIEWER_CHANNEL</string></value></member>")
            append("<member><name>version</name><value><string>$VIEWER_CHANNEL $VIEWER_VERSION</string></value></member>")
            append("<member><name>platform</name><value><string>$VIEWER_PLATFORM</string></value></member>")
            append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
            append("<member><name>id0</name><value><string>$id0</string></value></member>")
            // viewer_digest is required by the Second Life login server
            append("<member><name>viewer_digest</name><value><string>$viewerDigest</string></value></member>")
            
            // Agreements
            append("<member><name>agree_to_tos</name><value><string>true</string></value></member>")
            append("<member><name>read_critical</name><value><string>true</string></value></member>")
            
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
     * Parse login response XML (simple extraction like Lumiya with MFA support)
     * 
     * Handles the following cases:
     * 1. Successful login (login=true with session_id)
     * 2. Login failure (login=false)
     * 3. MFA challenge (login=false with reason=mfa_challenge)
     * 4. Missing session_id with agent_id (typically MFA-related)
     */
    private fun parseLoginResponse(xml: String): SimpleLoginResult {
        try {
            // Check for login failure - use regex to handle whitespace variations
            val loginValueRegex = Regex("<name>login</name>\\s*<value>\\s*<string>([^<]*)</string>")
            val loginMatch = loginValueRegex.find(xml)
            val loginValue = loginMatch?.groupValues?.get(1)
            
            // Extract the reason field to check for MFA challenge
            val reason = extractXmlValue(xml, "reason")
            
            if (loginValue == "false") {
                // Check if this is an MFA challenge
                if (reason == "mfa_challenge") {
                    val message = extractXmlValue(xml, "message") 
                        ?: "Multi-factor authentication required. Please enter your authenticator code."
                    val agentId = extractXmlValue(xml, "agent_id")
                    
                    Log.i(TAG, "MFA challenge received for agent: ${agentId?.take(8) ?: "unknown"}...")
                    return SimpleLoginResult.MFARequired(
                        message = message,
                        agentId = agentId
                    )
                }
                
                // Regular login failure
                val messageMatch = Regex("<name>message</name>\\s*<value>\\s*<string>([^<]+)</string>").find(xml)
                val message = messageMatch?.groupValues?.get(1) ?: "Login failed"
                
                Log.w(TAG, "Login failed: $message (reason: $reason)")
                return SimpleLoginResult.Failure(
                    message = message,
                    errorCode = mapReasonToErrorCode(reason),
                    details = "Server rejected login. Reason: ${reason ?: "unknown"}"
                )
            }
            
            // Extract session_id - can be <string> or <uuid> type
            val sessionId = extractXmlValue(xml, "session_id")
            
            // Extract agent_id - can be <string> or <uuid> type
            val agentId = extractXmlValue(xml, "agent_id")
            
            // Extract sim_ip and sim_port
            val simIp = extractXmlValue(xml, "sim_ip")
            val simPort = extractXmlValue(xml, "sim_port")?.toIntOrNull()
            
            // Extract mfa_hash for future logins (to skip MFA)
            val mfaHash = extractXmlValue(xml, "mfa_hash")
            
            // Check for MFA challenge scenario: agent_id present but session_id missing
            // This can happen when:
            // 1. MFA is required but response doesn't explicitly set login=false
            // 2. Server returned a partial response requiring additional authentication
            if (sessionId.isNullOrBlank() && !agentId.isNullOrBlank()) {
                // Check if this is actually an MFA challenge
                if (reason == "mfa_challenge" || xml.contains("mfa_challenge") || 
                    xml.contains("authenticator") || xml.contains("multi-factor")) {
                    val message = extractXmlValue(xml, "message") 
                        ?: "Multi-factor authentication required. Please enter your authenticator code."
                    
                    Log.i(TAG, "MFA challenge detected (session_id missing). Agent: ${agentId.take(8)}...")
                    return SimpleLoginResult.MFARequired(
                        message = message,
                        agentId = agentId
                    )
                }
                
                // Unknown state: agent_id present but no session_id and not MFA
                Log.e(TAG, "Unexpected login state: agent_id present but session_id missing. Reason: $reason")
                Log.d(TAG, "Response length: ${xml.length} chars")
                return SimpleLoginResult.Failure(
                    message = extractXmlValue(xml, "message") 
                        ?: "Login incomplete - additional authentication may be required",
                    errorCode = "INCOMPLETE_LOGIN",
                    details = buildString {
                        appendLine("Server returned agent_id but no session_id.")
                        appendLine("This typically indicates additional authentication is required.")
                        appendLine("Reason: ${reason ?: "not specified"}")
                        appendLine("agent_id: ${agentId.take(8)}...")
                        appendLine("Response length: ${xml.length} chars")
                        appendLine("Response preview: ${xml.take(500)}")
                    },
                    recommendations = listOf(
                        "Check if Multi-Factor Authentication (MFA) is enabled on your account",
                        "Try logging in through the Second Life website first",
                        "Contact Second Life support if the issue persists"
                    )
                )
            }
            
            // Validate we got essential fields for a successful login
            if (sessionId.isNullOrBlank() || agentId.isNullOrBlank()) {
                Log.e(TAG, "Missing essential fields in login response. sessionId present: ${!sessionId.isNullOrBlank()}, agentId present: ${!agentId.isNullOrBlank()}")
                Log.d(TAG, "Response length: ${xml.length} chars")
                return SimpleLoginResult.Failure(
                    message = "Invalid server response - missing session or agent ID",
                    errorCode = "INVALID_RESPONSE",
                    details = buildString {
                        appendLine("Response did not contain required fields.")
                        appendLine("session_id found: ${!sessionId.isNullOrBlank()}")
                        appendLine("agent_id found: ${!agentId.isNullOrBlank()}")
                        appendLine("Response length: ${xml.length} chars")
                        appendLine("Response preview: ${xml.take(300)}")
                    }
                )
            }
            
            Log.i(TAG, "Login successful! Session: ${sessionId.take(8)}..., Agent: ${agentId.take(8)}...")
            if (!mfaHash.isNullOrBlank()) {
                Log.d(TAG, "MFA hash received for future logins")
            }
            
            return SimpleLoginResult.Success(
                sessionId = sessionId,
                agentId = agentId,
                simIp = simIp ?: "127.0.0.1",
                simPort = simPort ?: 13000,
                mfaHash = mfaHash
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
    
    /**
     * Map login failure reason to a user-friendly error code.
     */
    private fun mapReasonToErrorCode(reason: String?): String {
        return when (reason?.lowercase()) {
            "key" -> "INVALID_CREDENTIALS"
            "presence" -> "ALREADY_LOGGED_IN"
            "update" -> "UPDATE_REQUIRED"
            "tos" -> "TOS_NOT_ACCEPTED"
            "mfa_challenge" -> "MFA_REQUIRED"
            "critical" -> "CRITICAL_MESSAGE"
            "disabled" -> "ACCOUNT_DISABLED"
            "ban" -> "ACCOUNT_BANNED"
            null -> "LOGIN_REJECTED"
            else -> "LOGIN_REJECTED"
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
    
    /**
     * Extract a value from XML-RPC response by name.
     * Handles multiple XML value types: <string>, <uuid>, <i4>, <int>.
     * Second Life login responses may use <uuid> for session_id and agent_id.
     * 
     * @param xml The XML response string
     * @param name The member name to extract
     * @return The extracted value or null if not found
     */
    private fun extractXmlValue(xml: String, name: String): String? {
        // Try <string> type first (most common)
        val stringRegex = Regex("<name>$name</name>\\s*<value>\\s*<string>([^<]*)</string>")
        stringRegex.find(xml)?.groupValues?.get(1)?.let { return it }
        
        // Try <uuid> type (used for session_id, agent_id)
        val uuidRegex = Regex("<name>$name</name>\\s*<value>\\s*<uuid>([^<]*)</uuid>")
        uuidRegex.find(xml)?.groupValues?.get(1)?.let { return it }
        
        // Try <i4> type (used for integers like sim_port)
        val i4Regex = Regex("<name>$name</name>\\s*<value>\\s*<i4>([^<]*)</i4>")
        i4Regex.find(xml)?.groupValues?.get(1)?.let { return it }
        
        // Try <int> type (alternative integer format)
        val intRegex = Regex("<name>$name</name>\\s*<value>\\s*<int>([^<]*)</int>")
        intRegex.find(xml)?.groupValues?.get(1)?.let { return it }
        
        return null
    }
    
    private fun generateMacAddress(): String {
        val random = java.util.Random()
        return (0..5).joinToString(":") { 
            String.format("%02X", random.nextInt(256)) 
        }
    }
    
    /**
     * Reads response body resiliently, handling EOF exceptions gracefully.
     * 
     * This is critical for handling chunked transfer encoding where the server
     * may close the connection before sending the final zero-length chunk.
     * In such cases, we attempt to recover whatever partial data was received.
     * 
     * For XML-RPC login responses, a partial response that contains the complete
     * XML structure (closing tags) can still be parsed successfully.
     * 
     * @param response The OkHttp response to read
     * @return Pair of (body string, whether EOF was encountered)
     * @throws IOException if reading fails for non-EOF reasons
     */
    private fun readBodyResilient(response: Response): Pair<String, Boolean> {
        val source = response.body?.source() ?: return Pair("", false)
        val buffer = Buffer()
        var hitEof = false
        
        try {
            // Buffer the response in chunks
            // Use a reasonable buffer size for XML-RPC responses
            val bufferSize = 8192L
            
            while (true) {
                val bytesRead = try {
                    source.read(buffer, bufferSize)
                } catch (e: EOFException) {
                    // EOF during read - this is expected when server closes connection early
                    Log.w(TAG, "EOF encountered while reading response body, using partial data")
                    hitEof = true
                    break
                } catch (e: IOException) {
                    // Check if this is an EOF-related error
                    if (NetworkExceptionUtils.isEOFException(e)) {
                        Log.w(TAG, "EOF-like error while reading response body: ${e.message}")
                        hitEof = true
                        break
                    }
                    throw e
                }
                
                if (bytesRead == -1L) {
                    // Normal end of stream
                    break
                }
            }
            
            val bodyString = buffer.readUtf8()
            
            if (hitEof && bodyString.isNotEmpty()) {
                Log.d(TAG, "Recovered ${bodyString.length} chars from partial response")
            }
            
            return Pair(bodyString, hitEof)
            
        } finally {
            try {
                source.close()
            } catch (e: Exception) {
                // Ignore close errors
            }
        }
    }
    
    /**
     * Validates if a partial response contains usable XML data.
     * 
     * For login responses, we need at least the key response elements.
     * A partial response that contains the closing </methodResponse> tag
     * is likely complete enough to parse.
     * 
     * @param body The response body string (potentially partial)
     * @return true if the body appears to contain complete/usable XML
     */
    private fun isResponseUsable(body: String): Boolean {
        if (body.isBlank()) return false
        
        // Check for complete XML-RPC response structure
        // The login response should have methodResponse closing tag
        val hasMethodResponse = body.contains("</methodResponse>")
        val hasStruct = body.contains("<struct>") && body.contains("</struct>")
        
        // If we have the closing methodResponse tag, the response is likely complete
        if (hasMethodResponse) {
            return true
        }
        
        // Check for login-specific markers that indicate a complete response
        val hasLoginResult = body.contains("<name>login</name>") && 
            (body.contains("<string>true</string>") || body.contains("<string>false</string>"))
        
        // Even without methodResponse, if we have key login data with struct, try to use it
        if (hasLoginResult && hasStruct) {
            Log.d(TAG, "Partial response contains login result, attempting to use it")
            return true
        }
        
        return false
    }
    
    /**
     * Network interceptor that pre-buffers response bodies to handle EOF gracefully.
     * 
     * This interceptor reads the entire response body into memory before returning
     * the response to the caller. This allows us to:
     * 1. Catch EOF exceptions during the buffering phase
     * 2. Return a complete (or best-effort partial) response body
     * 3. Avoid EOF exceptions during the caller's response.body.string() call
     * 
     * For login responses (which are typically small XML-RPC payloads), this
     * buffering approach is safe and provides better resilience.
     */
    private class ChunkedResponseInterceptor : Interceptor {
        companion object {
            private const val TAG = "ChunkedResponseInterceptor"
            private const val MAX_BUFFER_SIZE = 1024 * 1024L // 1MB max for login responses
        }
        
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
            
            // Only buffer responses with bodies
            val body = response.body ?: return response
            val contentType = body.contentType()
            
            // Buffer the response body, handling EOF gracefully
            val buffer = Buffer()
            var hitEof = false
            var totalRead = 0L
            
            try {
                val source = body.source()
                
                while (totalRead < MAX_BUFFER_SIZE) {
                    val bytesRead = try {
                        source.read(buffer, 8192L)
                    } catch (e: EOFException) {
                        Log.w(TAG, "EOF during response buffering, using ${buffer.size} bytes")
                        hitEof = true
                        break
                    } catch (e: IOException) {
                        if (NetworkExceptionUtils.isEOFException(e)) {
                            Log.w(TAG, "EOF-like error during buffering: ${e.message}")
                            hitEof = true
                            break
                        }
                        throw e
                    }
                    
                    if (bytesRead == -1L) break
                    totalRead += bytesRead
                }
                
            } finally {
                try {
                    body.close()
                } catch (e: Exception) {
                    // Ignore close errors
                }
            }
            
            // If we hit EOF but have data, create a new response with the buffered data
            val bufferedBody = buffer.readByteArray()
            
            if (hitEof && bufferedBody.isEmpty()) {
                // No data recovered at the network layer - this is a hard failure
                // that should trigger a retry. Unlike readBodyResilient() which runs
                // after buffering, here we're at the raw network layer and getting
                // zero bytes with EOF means the connection was dropped immediately.
                throw EOFException("Server closed connection with no data")
            }
            
            if (hitEof) {
                Log.d(TAG, "Recovered ${bufferedBody.size} bytes from chunked response with EOF")
            }
            
            // Return a new response with the buffered body
            return response.newBuilder()
                .body(bufferedBody.toResponseBody(contentType))
                .build()
        }
    }
}
