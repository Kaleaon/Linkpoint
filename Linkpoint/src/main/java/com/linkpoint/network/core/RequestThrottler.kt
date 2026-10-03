package com.linkpoint.network.core

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Request throttler based on Firestorm's HttpPolicy throttle rate limiting.
 * 
 * Prevents overwhelming the server by limiting the rate of requests.
 * Different policy classes can have different throttle rates.
 * 
 * From Firestorm's _httppolicy.cpp:
 * - mThrottleRate: requests per second limit
 * - mThrottleEnd: time when current throttle window ends
 * - mThrottleLeft: remaining requests in current window
 * 
 * @see https://github.com/FirestormViewer/phoenix-firestorm/blob/master/indra/llcorehttp/_httppolicy.cpp
 */
class RequestThrottler {
    
    companion object {
        private const val TAG = "RequestThrottler"
        
        // Singleton instance for global throttling
        @Volatile
        private var instance: RequestThrottler? = null
        
        fun getInstance(): RequestThrottler {
            return instance ?: synchronized(this) {
                instance ?: RequestThrottler().also { instance = it }
            }
        }
    }
    
    /**
     * Throttle state for a specific policy class.
     */
    private data class ThrottleState(
        val mutex: Mutex = Mutex(),
        var windowStartMs: Long = 0,
        var requestsInWindow: Int = 0
    )
    
    // Per-policy throttle states
    private val throttleStates = ConcurrentHashMap<PolicyClass, ThrottleState>()
    
    // Global request counter for diagnostics
    private val totalRequests = AtomicLong(0)
    private val throttledRequests = AtomicLong(0)
    
    /**
     * Acquire permission to make a request.
     * If the throttle limit is reached, this will delay until a slot is available.
     * 
     * @param policyClass The policy class for the request
     * @return Time spent waiting in milliseconds (0 if no wait)
     */
    suspend fun acquire(policyClass: PolicyClass): Long {
        val rate = policyClass.throttleRatePerSecond
        
        // If no throttling configured, return immediately
        if (rate <= 0) {
            totalRequests.incrementAndGet()
            return 0
        }
        
        val state = throttleStates.getOrPut(policyClass) { ThrottleState() }
        
        return state.mutex.withLock {
            totalRequests.incrementAndGet()
            
            val now = System.currentTimeMillis()
            val windowDurationMs = 1000L  // 1 second window
            
            // Check if we're in a new window
            if (now >= state.windowStartMs + windowDurationMs) {
                // Reset window
                state.windowStartMs = now
                state.requestsInWindow = 0
            }
            
            // Check if we've hit the limit
            if (state.requestsInWindow >= rate) {
                // Calculate how long to wait until next window
                val waitMs = (state.windowStartMs + windowDurationMs) - now
                
                if (waitMs > 0) {
                    throttledRequests.incrementAndGet()
                    Log.d(TAG, "Throttling ${policyClass.name} request, waiting ${waitMs}ms " +
                        "(${state.requestsInWindow}/$rate requests in window)")
                    
                    delay(waitMs)
                    
                    // After waiting, we're in a new window
                    state.windowStartMs = System.currentTimeMillis()
                    state.requestsInWindow = 0
                    
                    state.requestsInWindow++
                    return@withLock waitMs
                }
            }
            
            state.requestsInWindow++
            0L
        }
    }
    
    /**
     * Try to acquire permission without waiting.
     * Returns true if permission granted, false if throttled.
     * 
     * @param policyClass The policy class for the request
     * @return true if request can proceed, false if throttled
     */
    fun tryAcquire(policyClass: PolicyClass): Boolean {
        val rate = policyClass.throttleRatePerSecond
        
        if (rate <= 0) {
            totalRequests.incrementAndGet()
            return true
        }
        
        val state = throttleStates.getOrPut(policyClass) { ThrottleState() }
        
        // Use tryLock to avoid blocking
        if (!state.mutex.tryLock()) {
            return false
        }
        
        try {
            val now = System.currentTimeMillis()
            val windowDurationMs = 1000L
            
            if (now >= state.windowStartMs + windowDurationMs) {
                state.windowStartMs = now
                state.requestsInWindow = 0
            }
            
            if (state.requestsInWindow >= rate) {
                return false
            }
            
            totalRequests.incrementAndGet()
            state.requestsInWindow++
            return true
        } finally {
            state.mutex.unlock()
        }
    }
    
    /**
     * Reset throttle state for a policy class.
     * Useful after network changes or reconnection.
     */
    fun reset(policyClass: PolicyClass) {
        throttleStates.remove(policyClass)
        Log.d(TAG, "Reset throttle state for ${policyClass.name}")
    }
    
    /**
     * Reset all throttle states.
     */
    fun resetAll() {
        throttleStates.clear()
        Log.d(TAG, "Reset all throttle states")
    }
    
    /**
     * Get throttle statistics for diagnostics.
     */
    fun getStats(): ThrottleStats {
        return ThrottleStats(
            totalRequests = totalRequests.get(),
            throttledRequests = throttledRequests.get(),
            policyStats = throttleStates.entries.associate { (policy, state) ->
                policy.name to PolicyThrottleStats(
                    windowStartMs = state.windowStartMs,
                    requestsInWindow = state.requestsInWindow,
                    maxRequestsPerSecond = policy.throttleRatePerSecond
                )
            }
        )
    }
    
    data class ThrottleStats(
        val totalRequests: Long,
        val throttledRequests: Long,
        val policyStats: Map<String, PolicyThrottleStats>
    ) {
        val throttlePercentage: Float
            get() = if (totalRequests > 0) {
                throttledRequests.toFloat() / totalRequests * 100
            } else {
                0f
            }
    }
    
    data class PolicyThrottleStats(
        val windowStartMs: Long,
        val requestsInWindow: Int,
        val maxRequestsPerSecond: Int
    )
}
