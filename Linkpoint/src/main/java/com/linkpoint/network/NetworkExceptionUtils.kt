package com.linkpoint.network

import java.io.EOFException

/**
 * Shared utilities for network exception handling.
 * 
 * This object provides common constants and helper functions used across
 * SecondLifeProtocol and SecondLifeConnection for consistent error handling.
 */
object NetworkExceptionUtils {
    
    /**
     * Extra delay in milliseconds before retrying after an EOF error.
     * EOF errors often indicate server-side issues, so a brief delay
     * gives the server time to recover.
     */
    const val EOF_EXTRA_DELAY_MS = 300L
    
    /**
     * Message indicators that suggest an EOF-related error.
     * Used to detect EOF errors even when wrapped in other exception types.
     */
    private val EOF_MESSAGE_INDICATORS = listOf(
        "EOF",
        "unexpected end",
        "stream ended",
        "connection closed unexpectedly",
        "closed before response"
    )
    
    /**
     * Check if the exception is an EOFException or has EOFException in its cause chain.
     * 
     * This handles cases where:
     * - The exception is directly an EOFException or EOFIOException
     * - The exception's cause is an EOFException
     * - The EOFException is nested deeper in the cause chain (e.g., wrapped by OkHttp)
     * - The message contains EOF-related keywords (for wrapped exceptions)
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
}
