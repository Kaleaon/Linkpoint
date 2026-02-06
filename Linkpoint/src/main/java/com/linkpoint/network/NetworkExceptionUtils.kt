package com.linkpoint.network

import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLProtocolException

/**
 * Shared utilities for network exception handling.
 * 
 * This object provides common constants and helper functions used across
 * SecondLifeProtocol and SecondLifeConnection for consistent error handling.
 * 
 * Enhanced with detailed error classification and diagnostic information
 * for comprehensive error reporting.
 */
object NetworkExceptionUtils {
    
    /**
     * Extra delay in milliseconds before retrying after an EOF error.
     * EOF errors often indicate server-side issues (e.g., load balancer resets,
     * server overload), so a longer delay gives the server time to recover.
     * 
     * Increased from 300ms to 500ms based on mobile network testing where
     * shorter delays led to repeated EOF errors.
     */
    const val EOF_EXTRA_DELAY_MS = 500L
    
    /**
     * Detailed error classification for network exceptions.
     * Provides comprehensive categorization for debugging and user feedback.
     */
    enum class ErrorCategory {
        /** Connection closed unexpectedly (EOF, reset, etc.) */
        CONNECTION_CLOSED,
        /** SSL/TLS handshake or protocol errors */
        SSL_ERROR,
        /** Connection timeout */
        TIMEOUT,
        /** DNS resolution failure */
        DNS_ERROR,
        /** Connection refused or unreachable */
        CONNECTION_REFUSED,
        /** Socket-level errors */
        SOCKET_ERROR,
        /** Generic I/O errors */
        IO_ERROR,
        /** Unknown or unclassified errors */
        UNKNOWN
    }
    
    /**
     * Comprehensive error details for debugging and user feedback.
     * Contains both user-friendly messages and technical diagnostics.
     */
    data class DetailedErrorInfo(
        /** Error category for classification */
        val category: ErrorCategory,
        /** User-friendly error message */
        val userMessage: String,
        /** Technical error code */
        val errorCode: String,
        /** Detailed technical description */
        val technicalDetails: String,
        /** Root cause exception class name */
        val rootCauseType: String,
        /** Root cause message */
        val rootCauseMessage: String?,
        /** Full exception chain for debugging */
        val exceptionChain: String,
        /** Recommended actions for the user */
        val recommendations: List<String>,
        /** Whether this error is likely transient and worth retrying */
        val isTransient: Boolean,
        /** Suggested retry delay in milliseconds */
        val suggestedRetryDelayMs: Long
    )
    
    /**
     * Message indicators that suggest an EOF-related error.
     * Used to detect EOF errors even when wrapped in other exception types.
     * 
     * These patterns cover various ways EOF errors manifest across
     * different HTTP clients, SSL layers, and network stacks.
     */
    private val EOF_MESSAGE_INDICATORS = listOf(
        "EOF",
        "unexpected end",
        "stream ended",
        "connection closed unexpectedly",
        "closed before response",
        "connection was reset",
        "peer reset",
        "connection reset by peer",
        "ECONNRESET",
        "broken pipe",
        "stream was reset",
        "stream closed",
        "socket closed",
        "connection abort",
        "connection terminated"
    )
    
    /**
     * Check if the exception is an EOFException or has EOFException in its cause chain.
     * 
     * This handles cases where:
     * - The exception is directly an EOFException or EOFIOException
     * - The exception's cause is an EOFException
     * - The EOFException is nested deeper in the cause chain (e.g., wrapped by OkHttp)
     * - The message contains EOF-related keywords (for wrapped exceptions)
     * - SSLException wrapping connection reset (common with TLS connections)
     * - SocketException with connection reset
     * 
     * @param e The throwable to check
     * @return true if the exception is or contains an EOF-related error
     */
    fun isEOFException(e: Throwable): Boolean {
        var current: Throwable? = e
        var depth = 0
        
        while (current != null && depth < 10) {
            // Check if this is an EOF exception type
            if (current is EOFException || current is EOFIOException) {
                return true
            }
            
            // Check for SocketException with connection reset
            if (current is SocketException) {
                val msg = current.message ?: ""
                if (msg.contains("reset", ignoreCase = true) ||
                    msg.contains("closed", ignoreCase = true) ||
                    msg.contains("broken", ignoreCase = true)) {
                    return true
                }
            }
            
            // Check for SSLException wrapping connection issues
            if (current is SSLException) {
                val msg = current.message ?: ""
                if (msg.contains("reset", ignoreCase = true) ||
                    msg.contains("closed", ignoreCase = true) ||
                    msg.contains("EOF", ignoreCase = true)) {
                    return true
                }
            }
            
            // Check the message for EOF indicators
            val message = current.message ?: ""
            if (EOF_MESSAGE_INDICATORS.any { indicator -> 
                message.contains(indicator, ignoreCase = true) 
            }) {
                return true
            }
            
            current = current.cause
            depth++
        }
        
        return false
    }
    
    /**
     * Check if the exception is a connection reset error.
     * Connection resets are often related to EOF issues (server closed connection).
     */
    fun isConnectionResetException(e: Throwable): Boolean {
        var current: Throwable? = e
        var depth = 0
        
        while (current != null && depth < 10) {
            val message = current.message ?: ""
            if (message.contains("Connection reset", ignoreCase = true) ||
                message.contains("ECONNRESET", ignoreCase = true) ||
                message.contains("peer reset", ignoreCase = true)) {
                return true
            }
            current = current.cause
            depth++
        }
        
        return false
    }
    
    /**
     * Get a user-friendly description of an EOF error.
     */
    fun getEOFErrorDescription(): String {
        return "The server closed the connection unexpectedly. " +
            "This is usually temporary and often caused by server load or network conditions. " +
            "Please try again."
    }
    
    /**
     * Determine if the error is likely transient and worth retrying.
     */
    fun isTransientError(e: Throwable): Boolean {
        return isEOFException(e) || 
            isConnectionResetException(e) ||
            e is java.net.SocketTimeoutException ||
            (e is IOException && e.message?.contains("timeout", ignoreCase = true) == true)
    }
    
    /**
     * Analyze an exception and return comprehensive error details.
     * This is the primary method for detailed error reporting.
     * 
     * @param e The exception to analyze
     * @param loginUri The login URI being accessed (for context)
     * @param attemptNumber The current attempt number (1-based)
     * @param totalAttempts Total number of attempts configured
     * @param elapsedTimeMs Time elapsed since the request started
     * @return Comprehensive error details for debugging and user feedback
     */
    fun analyzeException(
        e: Throwable,
        loginUri: String? = null,
        attemptNumber: Int = 1,
        totalAttempts: Int = 1,
        elapsedTimeMs: Long = 0
    ): DetailedErrorInfo {
        val rootCause = getRootCause(e)
        val category = classifyException(e)
        val isTransient = isTransientError(e)
        
        val (userMessage, errorCode) = when (category) {
            ErrorCategory.CONNECTION_CLOSED -> {
                val rootType = rootCause.javaClass.simpleName
                val specificMessage = when {
                    rootCause is EOFException -> 
                        "Server closed connection before sending complete response"
                    rootCause is EOFIOException ->
                        "Server closed connection unexpectedly during data transfer"
                    rootCause.message?.contains("reset", ignoreCase = true) == true ->
                        "Connection was forcibly reset by the server"
                    rootCause.message?.contains("broken pipe", ignoreCase = true) == true ->
                        "Connection broken while sending data to server"
                    else -> "The server closed the connection unexpectedly"
                }
                Pair(specificMessage, "EOF_ERROR")
            }
            ErrorCategory.SSL_ERROR -> {
                val specificMessage = when (rootCause) {
                    is SSLHandshakeException -> 
                        "Secure connection handshake failed - certificate or protocol issue"
                    is SSLProtocolException ->
                        "SSL/TLS protocol error during secure communication"
                    else -> {
                        val msg = rootCause.message ?: ""
                        when {
                            msg.contains("handshake", ignoreCase = true) ->
                                "SSL handshake failed - possible certificate or compatibility issue"
                            msg.contains("certificate", ignoreCase = true) ->
                                "SSL certificate validation failed"
                            msg.contains("protocol", ignoreCase = true) ->
                                "SSL protocol negotiation failed"
                            else -> "Secure connection failed"
                        }
                    }
                }
                Pair(specificMessage, "SSL_ERROR")
            }
            ErrorCategory.TIMEOUT -> {
                Pair("Connection timed out - server may be slow or unreachable", "TIMEOUT")
            }
            ErrorCategory.DNS_ERROR -> {
                val host = loginUri?.substringAfter("://")?.substringBefore("/") ?: "server"
                Pair("Cannot resolve '$host' - check your internet connection", "DNS_ERROR")
            }
            ErrorCategory.CONNECTION_REFUSED -> {
                Pair("Connection refused - server may be down or blocking connections", "CONNECTION_REFUSED")
            }
            ErrorCategory.SOCKET_ERROR -> {
                val msg = rootCause.message ?: ""
                val specificMessage = when {
                    msg.contains("closed", ignoreCase = true) -> "Socket was closed unexpectedly"
                    msg.contains("reset", ignoreCase = true) -> "Socket connection was reset"
                    msg.contains("refused", ignoreCase = true) -> "Socket connection refused"
                    else -> "Socket error: ${msg.take(50)}"
                }
                Pair(specificMessage, "SOCKET_ERROR")
            }
            ErrorCategory.IO_ERROR -> {
                Pair("Network I/O error: ${rootCause.message?.take(100) ?: "Unknown"}", "IO_ERROR")
            }
            ErrorCategory.UNKNOWN -> {
                Pair("Unexpected error: ${rootCause.message?.take(100) ?: rootCause.javaClass.simpleName}", "UNKNOWN_ERROR")
            }
        }
        
        val technicalDetails = buildDetailedTechnicalInfo(
            e = e,
            rootCause = rootCause,
            category = category,
            loginUri = loginUri,
            attemptNumber = attemptNumber,
            totalAttempts = totalAttempts,
            elapsedTimeMs = elapsedTimeMs
        )
        
        val recommendations = getRecommendations(category, isTransient, attemptNumber, totalAttempts)
        
        val suggestedDelay = when (category) {
            ErrorCategory.CONNECTION_CLOSED -> EOF_EXTRA_DELAY_MS * (1 shl (attemptNumber - 1).coerceAtMost(3))
            ErrorCategory.TIMEOUT -> 1000L * attemptNumber
            ErrorCategory.SSL_ERROR -> 500L
            else -> 500L
        }
        
        return DetailedErrorInfo(
            category = category,
            userMessage = userMessage,
            errorCode = errorCode,
            technicalDetails = technicalDetails,
            rootCauseType = rootCause.javaClass.simpleName,
            rootCauseMessage = rootCause.message,
            exceptionChain = buildExceptionChain(e),
            recommendations = recommendations,
            isTransient = isTransient,
            suggestedRetryDelayMs = suggestedDelay
        )
    }
    
    /**
     * Classify an exception into an error category.
     */
    fun classifyException(e: Throwable): ErrorCategory {
        val rootCause = getRootCause(e)
        
        return when {
            isEOFException(e) -> ErrorCategory.CONNECTION_CLOSED
            isConnectionResetException(e) -> ErrorCategory.CONNECTION_CLOSED
            rootCause is SSLException || e is SSLException -> ErrorCategory.SSL_ERROR
            rootCause is SocketTimeoutException || e is SocketTimeoutException -> ErrorCategory.TIMEOUT
            rootCause is UnknownHostException || e is UnknownHostException -> ErrorCategory.DNS_ERROR
            rootCause is ConnectException || e is ConnectException -> {
                val msg = (rootCause.message ?: "") + (e.message ?: "")
                if (msg.contains("refused", ignoreCase = true)) {
                    ErrorCategory.CONNECTION_REFUSED
                } else {
                    ErrorCategory.CONNECTION_REFUSED
                }
            }
            rootCause is SocketException || e is SocketException -> ErrorCategory.SOCKET_ERROR
            rootCause is IOException || e is IOException -> ErrorCategory.IO_ERROR
            else -> ErrorCategory.UNKNOWN
        }
    }
    
    /**
     * Get the root cause of an exception chain.
     * 
     * Traverses the exception chain to find the original cause of the error.
     * Limits traversal to 20 levels to prevent infinite loops in malformed
     * exception chains.
     * 
     * @param e The exception to analyze
     * @return The root cause of the exception chain
     */
    fun getRootCause(e: Throwable): Throwable {
        var current: Throwable = e
        var depth = 0
        while (depth < 20) {
            val cause = current.cause ?: break
            current = cause
            depth++
        }
        return current
    }
    
    /**
     * Build a detailed technical information string for debugging.
     */
    private fun buildDetailedTechnicalInfo(
        e: Throwable,
        rootCause: Throwable,
        category: ErrorCategory,
        loginUri: String?,
        attemptNumber: Int,
        totalAttempts: Int,
        elapsedTimeMs: Long
    ): String = buildString {
        appendLine("=== Exception Details ===")
        appendLine("Top-level Exception: ${e.javaClass.name}")
        appendLine("Top-level Message: ${e.message ?: "(no message)"}")
        appendLine()
        
        if (e !== rootCause) {
            appendLine("=== Root Cause ===")
            appendLine("Root Cause Type: ${rootCause.javaClass.name}")
            appendLine("Root Cause Message: ${rootCause.message ?: "(no message)"}")
            appendLine()
        }
        
        appendLine("=== Request Context ===")
        if (loginUri != null) {
            appendLine("Login URI: $loginUri")
        }
        appendLine("Attempt: $attemptNumber of $totalAttempts")
        appendLine("Elapsed Time: ${elapsedTimeMs}ms")
        appendLine("Error Category: $category")
        appendLine()
        
        appendLine("=== Exception Chain ===")
        append(buildExceptionChain(e))
        appendLine()
        
        // Add category-specific details
        when (category) {
            ErrorCategory.CONNECTION_CLOSED -> {
                appendLine()
                appendLine("=== EOF/Connection Reset Analysis ===")
                appendLine("This error typically occurs when:")
                appendLine("• The login server is experiencing high load")
                appendLine("• A load balancer reset the connection")
                appendLine("• Network interruption during data transfer")
                appendLine("• HTTP/2 protocol negotiation issue")
                appendLine("• Server-side rate limiting or timeout")
            }
            ErrorCategory.SSL_ERROR -> {
                appendLine()
                appendLine("=== SSL/TLS Analysis ===")
                appendLine("This error may indicate:")
                appendLine("• Certificate validation failure")
                appendLine("• TLS version mismatch")
                appendLine("• VPN or proxy interference")
                appendLine("• Network security policy blocking")
            }
            ErrorCategory.TIMEOUT -> {
                appendLine()
                appendLine("=== Timeout Analysis ===")
                appendLine("The connection timed out, which may indicate:")
                appendLine("• Server is overloaded or unresponsive")
                appendLine("• Network congestion")
                appendLine("• Firewall blocking the connection")
                appendLine("• Mobile network instability")
            }
            ErrorCategory.DNS_ERROR -> {
                appendLine()
                appendLine("=== DNS Analysis ===")
                appendLine("DNS resolution failed, which may indicate:")
                appendLine("• No internet connection")
                appendLine("• DNS server issues")
                appendLine("• Invalid hostname")
                appendLine("• Network security policy blocking")
            }
            else -> {}
        }
        
        // Add stack trace for root cause (first 5 frames)
        appendLine()
        appendLine("=== Stack Trace (Root Cause) ===")
        rootCause.stackTrace.take(5).forEachIndexed { idx, frame ->
            appendLine("  ${idx + 1}. ${frame.className}.${frame.methodName}(${frame.fileName}:${frame.lineNumber})")
        }
        if (rootCause.stackTrace.size > 5) {
            appendLine("  ... ${rootCause.stackTrace.size - 5} more frames")
        }
    }
    
    /**
     * Build a string representation of the full exception chain.
     */
    fun buildExceptionChain(e: Throwable): String = buildString {
        var current: Throwable? = e
        var depth = 0
        
        while (current != null && depth < 10) {
            val indent = "  ".repeat(depth)
            appendLine("${indent}${if (depth == 0) "→" else "└─"} ${current.javaClass.simpleName}: ${current.message ?: "(no message)"}")
            current = current.cause
            depth++
        }
        
        if (depth >= 10) {
            appendLine("  ... (chain continues)")
        }
    }
    
    /**
     * Get recommended actions based on error category.
     */
    private fun getRecommendations(
        category: ErrorCategory,
        isTransient: Boolean,
        attemptNumber: Int,
        totalAttempts: Int
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        // Common first recommendation for transient errors
        if (isTransient && attemptNumber < totalAttempts) {
            recommendations.add("Wait a moment and try again (attempt ${attemptNumber + 1} of $totalAttempts)")
        } else if (isTransient) {
            recommendations.add("Wait 30-60 seconds and try again")
        }
        
        when (category) {
            ErrorCategory.CONNECTION_CLOSED -> {
                recommendations.add("Check status.secondlifegrid.net for server status")
                recommendations.add("If on mobile data, try switching to Wi-Fi")
                recommendations.add("If using VPN, try disabling it temporarily")
                recommendations.add("Try closing and reopening the app")
            }
            ErrorCategory.SSL_ERROR -> {
                recommendations.add("Ensure your device date/time is correct")
                recommendations.add("Disable VPN or proxy if enabled")
                recommendations.add("Check if your network blocks secure connections")
                recommendations.add("Try switching between Wi-Fi and mobile data")
            }
            ErrorCategory.TIMEOUT -> {
                recommendations.add("Check your internet connection stability")
                recommendations.add("Move to an area with better signal")
                recommendations.add("If on mobile data, try switching to Wi-Fi")
                recommendations.add("Close other apps using the network")
            }
            ErrorCategory.DNS_ERROR -> {
                recommendations.add("Check your internet connection")
                recommendations.add("Try switching between Wi-Fi and mobile data")
                recommendations.add("Restart your router/modem")
                recommendations.add("Try using a different DNS server")
            }
            ErrorCategory.CONNECTION_REFUSED -> {
                recommendations.add("Check status.secondlifegrid.net for server status")
                recommendations.add("The server may be undergoing maintenance")
                recommendations.add("Try again in a few minutes")
            }
            ErrorCategory.SOCKET_ERROR, ErrorCategory.IO_ERROR -> {
                recommendations.add("Check your internet connection")
                recommendations.add("Try switching between Wi-Fi and mobile data")
                recommendations.add("Restart the app and try again")
            }
            ErrorCategory.UNKNOWN -> {
                recommendations.add("Try restarting the app")
                recommendations.add("Check your internet connection")
                recommendations.add("If problem persists, report this error")
            }
        }
        
        return recommendations
    }
    
    /**
     * Format error details for display in an error dialog.
     * Returns a user-friendly summary with expandable technical details.
     */
    fun formatForErrorDialog(info: DetailedErrorInfo): Pair<String, String> {
        val summary = buildString {
            appendLine(info.userMessage)
            if (info.isTransient) {
                appendLine()
                appendLine("This is usually a temporary issue.")
            }
        }
        
        val details = buildString {
            appendLine("=== Error Information ===")
            appendLine("Error Code: ${info.errorCode}")
            appendLine("Category: ${info.category}")
            appendLine("Is Transient: ${info.isTransient}")
            appendLine()
            appendLine("=== Root Cause ===")
            appendLine("Type: ${info.rootCauseType}")
            appendLine("Message: ${info.rootCauseMessage ?: "(none)"}")
            appendLine()
            append(info.technicalDetails)
            appendLine()
            appendLine("=== Recommended Actions ===")
            info.recommendations.forEachIndexed { idx, rec ->
                appendLine("${idx + 1}. $rec")
            }
        }
        
        return Pair(summary.trim(), details.trim())
    }
}
