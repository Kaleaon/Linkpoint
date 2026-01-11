package com.linkpoint.network.core

import android.util.Log
import kotlin.math.min
import kotlin.random.Random

/**
 * Retry policy with exponential backoff and error tracking.
 * Based on patterns from the official Second Life app.
 * 
 * Features:
 * - Exponential backoff with jitter
 * - Error count tracking with thresholds
 * - Error timeout tracking
 * - Distinction between recoverable and non-recoverable errors
 * - Maximum retry limits
 */
class RetryPolicy(
    private val maxRetryAttempts: Int = DEFAULT_MAX_RETRY_ATTEMPTS,
    private val startingRetryDelayMs: Long = DEFAULT_STARTING_RETRY_DELAY_MS,
    private val maxRetryDelayMs: Long = DEFAULT_MAX_RETRY_DELAY_MS,
    private val errorCountLimit: Int = DEFAULT_ERROR_COUNT_LIMIT,
    private val errorTimeoutLimitMs: Long = DEFAULT_ERROR_TIMEOUT_LIMIT_MS,
    private val jitterFactor: Float = DEFAULT_JITTER_FACTOR
) {
    companion object {
        private const val TAG = "RetryPolicy"
        
        // Default values based on official SL app patterns
        const val DEFAULT_MAX_RETRY_ATTEMPTS = 5
        const val DEFAULT_STARTING_RETRY_DELAY_MS = 500L
        const val DEFAULT_MAX_RETRY_DELAY_MS = 30_000L  // 30 seconds max
        const val DEFAULT_ERROR_COUNT_LIMIT = 5
        const val DEFAULT_ERROR_TIMEOUT_LIMIT_MS = 60_000L  // 1 minute
        const val DEFAULT_JITTER_FACTOR = 0.2f
        
        /**
         * Create a policy optimized for LTE/mobile networks
         */
        fun forMobileNetwork(): RetryPolicy = RetryPolicy(
            maxRetryAttempts = 6,
            startingRetryDelayMs = 750L,
            maxRetryDelayMs = 45_000L,
            errorCountLimit = 6,
            errorTimeoutLimitMs = 90_000L
        )
        
        /**
         * Create a policy optimized for WiFi
         */
        fun forWifi(): RetryPolicy = RetryPolicy(
            maxRetryAttempts = 3,
            startingRetryDelayMs = 300L,
            maxRetryDelayMs = 15_000L,
            errorCountLimit = 3,
            errorTimeoutLimitMs = 30_000L
        )
        
        /**
         * Create a policy for login operations (more patient)
         */
        fun forLogin(): RetryPolicy = RetryPolicy(
            maxRetryAttempts = 5,  // Match Firestorm's HTTP_RETRY_COUNT_DEFAULT
            startingRetryDelayMs = 1000L,
            maxRetryDelayMs = 60_000L,
            errorCountLimit = 5,
            errorTimeoutLimitMs = 120_000L  // 2 minutes
        )
        
        /**
         * Create a policy for inventory operations
         */
        fun forInventory(): RetryPolicy = RetryPolicy(
            maxRetryAttempts = 4,
            startingRetryDelayMs = 1000L,
            maxRetryDelayMs = 30_000L,
            errorCountLimit = 4,
            errorTimeoutLimitMs = 90_000L
        )
        
        /**
         * Create a policy for event queue (very patient, many retries)
         */
        fun forEventQueue(): RetryPolicy = RetryPolicy(
            maxRetryAttempts = 10,
            startingRetryDelayMs = 500L,
            maxRetryDelayMs = 10_000L,
            errorCountLimit = 10,
            errorTimeoutLimitMs = 300_000L  // 5 minutes
        )
    }
    
    // Error tracking state
    private var failuresInARow = 0
    private var firstErrorTimestamp: Long = 0L
    private var lastErrorTimestamp: Long = 0L
    private var totalErrors = 0
    private var isInErrorState = false
    
    // Backoff state
    private var currentRetryAttempt = 0
    private var nextRetryDelayMs = startingRetryDelayMs
    
    // Retry-After support (Firestorm's mReplyRetryAfter pattern)
    private var lastRetryAfterMs: Long = 0
    
    /**
     * Result of a retry decision
     */
    sealed class RetryDecision {
        data class Retry(
            val delayMs: Long,
            val attempt: Int,
            val reason: String
        ) : RetryDecision()
        
        data class GiveUp(
            val reason: String,
            val shouldResetConnection: Boolean
        ) : RetryDecision()
        
        data class Continue(
            val degraded: Boolean,
            val message: String
        ) : RetryDecision()
    }
    
    /**
     * Record a Retry-After hint from the server.
     * Call this before onError() when a Retry-After header is present.
     * 
     * Based on Firestorm's handling in _httppolicy.cpp:
     * if (op->mReplyRetryAfter > 0 && op->mReplyRetryAfter < 30)
     *     delta = op->mReplyRetryAfter * U64L(1000000);
     * 
     * @param retryAfterSeconds Value from Retry-After header (capped at 30)
     */
    @Synchronized
    fun setRetryAfterHint(retryAfterSeconds: Int) {
        // Cap at 30 seconds like Firestorm does
        val capped = minOf(retryAfterSeconds, 30).coerceAtLeast(0)
        lastRetryAfterMs = capped * 1000L
        Log.d(TAG, "Retry-After hint set: ${capped}s")
    }
    
    /**
     * Record an error and decide whether to retry
     */
    @Synchronized
    fun onError(error: Throwable, isRecoverable: Boolean = true): RetryDecision {
        val now = System.currentTimeMillis()
        totalErrors++
        failuresInARow++
        lastErrorTimestamp = now
        
        if (firstErrorTimestamp == 0L) {
            firstErrorTimestamp = now
        }
        
        val errorDuration = now - firstErrorTimestamp
        
        Log.w(TAG, "Error recorded: ${error.javaClass.simpleName}: ${error.message} " +
            "(failures: $failuresInARow/$errorCountLimit, duration: ${errorDuration}ms)")
        
        // Non-recoverable errors should trigger immediate connection reset
        if (!isRecoverable) {
            Log.e(TAG, "Non-recoverable error detected. Triggering connection reset.")
            isInErrorState = true
            return RetryDecision.GiveUp(
                reason = "Non-recoverable error: ${error.message}",
                shouldResetConnection = true
            )
        }
        
        // Check error count limit
        if (failuresInARow >= errorCountLimit) {
            Log.e(TAG, "Hit error count limit ($failuresInARow/$errorCountLimit). Triggering connection reset.")
            isInErrorState = true
            return RetryDecision.GiveUp(
                reason = "Error count limit exceeded ($failuresInARow failures)",
                shouldResetConnection = true
            )
        }
        
        // Check error timeout limit
        if (errorDuration >= errorTimeoutLimitMs) {
            Log.e(TAG, "Hit error timeout limit (${errorDuration}ms >= ${errorTimeoutLimitMs}ms). Triggering connection reset.")
            isInErrorState = true
            return RetryDecision.GiveUp(
                reason = "Error timeout limit exceeded (${errorDuration}ms)",
                shouldResetConnection = true
            )
        }
        
        // Check retry attempts
        if (currentRetryAttempt >= maxRetryAttempts) {
            Log.w(TAG, "Max retry attempts reached ($currentRetryAttempt/$maxRetryAttempts)")
            return RetryDecision.GiveUp(
                reason = "Maximum retry attempts exceeded",
                shouldResetConnection = false
            )
        }
        
        // Calculate next retry delay with exponential backoff and jitter
        val delay = calculateNextRetryDelay()
        currentRetryAttempt++
        
        Log.d(TAG, "Within error limit ($failuresInARow/$errorCountLimit). " +
            "Retrying in ${delay}ms (attempt $currentRetryAttempt/$maxRetryAttempts)")
        
        return RetryDecision.Retry(
            delayMs = delay,
            attempt = currentRetryAttempt,
            reason = "Transient error, retrying..."
        )
    }
    
    /**
     * Record a successful operation
     */
    @Synchronized
    fun onSuccess() {
        if (failuresInARow > 0) {
            Log.d(TAG, "Success after $failuresInARow failures. Resetting error state.")
        }
        reset()
    }
    
    /**
     * Reset the retry policy state
     */
    @Synchronized
    fun reset() {
        failuresInARow = 0
        firstErrorTimestamp = 0L
        lastErrorTimestamp = 0L
        currentRetryAttempt = 0
        nextRetryDelayMs = startingRetryDelayMs
        lastRetryAfterMs = 0
        isInErrorState = false
    }
    
    /**
     * Calculate the next retry delay using exponential backoff with jitter.
     * 
     * If a Retry-After hint was provided by the server, uses that instead
     * of calculated backoff (Firestorm pattern: external_delta flag).
     */
    private fun calculateNextRetryDelay(): Long {
        // If server provided Retry-After, use it (external delta in Firestorm terms)
        if (lastRetryAfterMs > 0) {
            val serverDelay = lastRetryAfterMs
            lastRetryAfterMs = 0  // Clear after using
            Log.d(TAG, "Using server-provided Retry-After delay: ${serverDelay}ms")
            nextRetryDelayMs = serverDelay
            return serverDelay
        }
        
        // Otherwise use exponential backoff (internal delta)
        // Formula: delay = startingDelay * 2^attempt (capped)
        // Based on Firestorm's: delta_min * delta_factor where delta_factor = 1 << retries
        // Using bit-shifting for consistency with HttpRequestOptions.calculateRetryDelay
        val exponentialDelay = startingRetryDelayMs * (1L shl min(currentRetryAttempt, 10))
        
        // Cap at maximum delay
        val cappedDelay = min(exponentialDelay, maxRetryDelayMs)
        
        // Add jitter to prevent thundering herd
        val jitter = (cappedDelay * jitterFactor * Random.nextFloat()).toLong()
        val finalDelay = cappedDelay + jitter
        
        nextRetryDelayMs = finalDelay
        return finalDelay
    }
    
    /**
     * Check if we're currently in an error state
     */
    fun isInErrorState(): Boolean = isInErrorState
    
    /**
     * Get current retry statistics
     */
    fun getStats(): RetryStats = RetryStats(
        failuresInARow = failuresInARow,
        totalErrors = totalErrors,
        currentRetryAttempt = currentRetryAttempt,
        nextRetryDelayMs = nextRetryDelayMs,
        isInErrorState = isInErrorState,
        errorDurationMs = if (firstErrorTimestamp > 0) 
            System.currentTimeMillis() - firstErrorTimestamp else 0
    )
    
    data class RetryStats(
        val failuresInARow: Int,
        val totalErrors: Int,
        val currentRetryAttempt: Int,
        val nextRetryDelayMs: Long,
        val isInErrorState: Boolean,
        val errorDurationMs: Long
    )
}
