package com.linkpoint.network

import java.io.EOFException
import java.io.IOException
import java.net.SocketException
import javax.net.ssl.SSLException

/**
 * Shared utilities for network exception handling.
 * 
 * This object provides common constants and helper functions used across
 * SecondLifeProtocol and SecondLifeConnection for consistent error handling.
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
}
