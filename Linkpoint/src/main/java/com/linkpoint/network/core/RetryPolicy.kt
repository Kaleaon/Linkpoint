package com.linkpoint.network.core

import android.util.Log
import kotlin.math.min
import kotlin.math.pow
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
            maxRetryAttempts = 4,
            startingRetryDelayMs = 1000L,
            maxRetryDelayMs = 60_000L,
            errorCountLimit = 4,
            errorTimeoutLimitMs = 120_000L  // 2 minutes
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
        isInErrorState = false
    }
    
    /**
     * Calculate the next retry delay using exponential backoff with jitter
     */
    private fun calculateNextRetryDelay(): Long {
        // Exponential backoff: delay = startingDelay * 2^attempt
        val exponentialDelay = startingRetryDelayMs * 2.0.pow(currentRetryAttempt.toDouble())
        
        // Cap at maximum delay
        val cappedDelay = min(exponentialDelay, maxRetryDelayMs.toDouble()).toLong()
        
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
