package com.linkpoint.network.core

/**
 * HTTP request options modeled after Firestorm's HttpOptions class.
 * 
 * These options can be shared across requests and provide fine-grained
 * control over HTTP behavior including timeouts, retries, and SSL settings.
 * 
 * Key features from Firestorm's implementation:
 * - Configurable retry counts and backoff timing
 * - Retry-After header support
 * - DNS cache timeout control
 * - SSL verification options
 * - Request tracing levels
 * 
 * @see https://github.com/FirestormViewer/phoenix-firestorm/blob/master/indra/llcorehttp/httpoptions.h
 */
data class HttpRequestOptions(
    /**
     * Whether to include headers in the response.
     * Default: false
     */
    val wantHeaders: Boolean = false,
    
    /**
     * Tracing level for debugging.
     * 0 = Off, 1 = Basic, 2 = Verbose, 3 = Payload
     * Default: 0
     */
    val traceLevel: Int = HTTP_TRACE_OFF,
    
    /**
     * Connection timeout in seconds.
     * This is the time to establish a connection.
     * Default: 30 seconds
     */
    val timeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS,
    
    /**
     * Transfer timeout in seconds.
     * This is the time for the entire transfer to complete.
     * 0 means no transfer timeout (use connection timeout only).
     * Default: 0 (no separate transfer timeout)
     */
    val transferTimeoutSeconds: Long = DEFAULT_TRANSFER_TIMEOUT_SECONDS,
    
    /**
     * Number of retry attempts on failure.
     * Default: 5 (matching Firestorm)
     */
    val retries: Int = DEFAULT_RETRY_COUNT,
    
    /**
     * Minimum delay before retry in milliseconds.
     * Default: 1000ms (1 second)
     */
    val minRetryBackoffMs: Long = DEFAULT_MIN_RETRY_BACKOFF_MS,
    
    /**
     * Maximum delay before retry in milliseconds.
     * Default: 5000ms (5 seconds)
     */
    val maxRetryBackoffMs: Long = DEFAULT_MAX_RETRY_BACKOFF_MS,
    
    /**
     * Whether to respect server's Retry-After header.
     * When true, uses the server's suggested delay if present.
     * Default: true
     */
    val useRetryAfter: Boolean = DEFAULT_USE_RETRY_AFTER,
    
    /**
     * Maximum value to accept from Retry-After header in seconds.
     * Values larger than this are capped to prevent indefinite waits.
     * Default: 30 seconds (matching Firestorm)
     */
    val maxRetryAfterSeconds: Int = MAX_RETRY_AFTER_SECONDS,
    
    /**
     * Whether to follow HTTP redirects.
     * Default: true
     */
    val followRedirects: Boolean = true,
    
    /**
     * Whether to verify SSL peer certificate.
     * Should be true in production for security.
     * Default: true
     */
    val sslVerifyPeer: Boolean = true,
    
    /**
     * Whether to verify SSL hostname matches certificate.
     * Default: true
     */
    val sslVerifyHost: Boolean = true,
    
    /**
     * DNS cache timeout in seconds.
     * -1 = Never timeout (cache forever)
     * 0 = Disable caching
     * >0 = Cache for specified duration
     * Default: -1 (matching Firestorm's default for stability)
     */
    val dnsCacheTimeoutSeconds: Int = DEFAULT_DNS_CACHE_TIMEOUT,
    
    /**
     * Request priority. Higher values are processed first.
     * Default: PRIORITY_NORMAL
     */
    val priority: Int = PRIORITY_NORMAL,
    
    /**
     * Policy class for request classification.
     * Different policy classes can have different connection limits
     * and throttling settings.
     */
    val policyClass: PolicyClass = PolicyClass.DEFAULT
) {
    companion object {
        // Trace levels
        const val HTTP_TRACE_OFF = 0
        const val HTTP_TRACE_BASIC = 1
        const val HTTP_TRACE_VERBOSE = 2
        const val HTTP_TRACE_PAYLOAD = 3
        
        // Default values (based on Firestorm's httpinternal.h)
        const val DEFAULT_TIMEOUT_SECONDS = 30L
        const val DEFAULT_TRANSFER_TIMEOUT_SECONDS = 0L
        const val DEFAULT_RETRY_COUNT = 5
        const val DEFAULT_MIN_RETRY_BACKOFF_MS = 1000L  // 1 second
        const val DEFAULT_MAX_RETRY_BACKOFF_MS = 5000L  // 5 seconds
        const val DEFAULT_USE_RETRY_AFTER = true
        const val MAX_RETRY_AFTER_SECONDS = 30
        const val DEFAULT_DNS_CACHE_TIMEOUT = -1  // Never timeout
        
        // Priority levels
        const val PRIORITY_LOW = 0
        const val PRIORITY_NORMAL = 100
        const val PRIORITY_HIGH = 200
        const val PRIORITY_CRITICAL = 300
        
        /**
         * Options optimized for login requests.
         * More patient with longer timeouts and more retries.
         */
        fun forLogin(): HttpRequestOptions = HttpRequestOptions(
            timeoutSeconds = 60,
            transferTimeoutSeconds = 120,
            retries = 5,
            minRetryBackoffMs = 1500L,
            maxRetryBackoffMs = 10000L,
            useRetryAfter = true,
            dnsCacheTimeoutSeconds = -1,
            priority = PRIORITY_CRITICAL,
            policyClass = PolicyClass.LOGIN
        )
        
        /**
         * Options optimized for inventory operations.
         * Moderate patience with good retry behavior.
         */
        fun forInventory(): HttpRequestOptions = HttpRequestOptions(
            timeoutSeconds = 45,
            transferTimeoutSeconds = 90,
            retries = 4,
            minRetryBackoffMs = 1000L,
            maxRetryBackoffMs = 8000L,
            useRetryAfter = true,
            dnsCacheTimeoutSeconds = 60,  // Cache DNS for 1 minute
            priority = PRIORITY_HIGH,
            policyClass = PolicyClass.INVENTORY
        )
        
        /**
         * Options optimized for texture downloads.
         * Can be more aggressive with retries since textures are less critical.
         */
        fun forTextures(): HttpRequestOptions = HttpRequestOptions(
            timeoutSeconds = 30,
            transferTimeoutSeconds = 60,
            retries = 3,
            minRetryBackoffMs = 500L,
            maxRetryBackoffMs = 5000L,
            useRetryAfter = true,
            dnsCacheTimeoutSeconds = 300,  // Cache DNS for 5 minutes
            priority = PRIORITY_LOW,
            policyClass = PolicyClass.ASSET
        )
        
        /**
         * Options optimized for mesh downloads.
         */
        fun forMesh(): HttpRequestOptions = HttpRequestOptions(
            timeoutSeconds = 45,
            transferTimeoutSeconds = 90,
            retries = 3,
            minRetryBackoffMs = 500L,
            maxRetryBackoffMs = 5000L,
            useRetryAfter = true,
            dnsCacheTimeoutSeconds = 300,
            priority = PRIORITY_NORMAL,
            policyClass = PolicyClass.ASSET
        )
        
        /**
         * Options for event queue long-polling.
         * Very long timeout since event queue can wait for events.
         */
        fun forEventQueue(): HttpRequestOptions = HttpRequestOptions(
            timeoutSeconds = 30,
            transferTimeoutSeconds = 300,  // 5 minutes for long poll
            retries = 10,  // Many retries since this is critical
            minRetryBackoffMs = 500L,
            maxRetryBackoffMs = 3000L,
            useRetryAfter = true,
            dnsCacheTimeoutSeconds = -1,
            priority = PRIORITY_CRITICAL,
            policyClass = PolicyClass.EVENT_QUEUE
        )
        
        /**
         * Options for mobile networks (LTE, etc).
         * More patient timeouts and retries.
         */
        fun forMobileNetwork(): HttpRequestOptions = HttpRequestOptions(
            timeoutSeconds = 60,
            transferTimeoutSeconds = 120,
            retries = 6,
            minRetryBackoffMs = 1500L,
            maxRetryBackoffMs = 15000L,
            useRetryAfter = true,
            dnsCacheTimeoutSeconds = 120,
            priority = PRIORITY_NORMAL,
            policyClass = PolicyClass.DEFAULT
        )
    }
    
    /**
     * Calculate retry delay for a given attempt, optionally using Retry-After.
     * 
     * @param attempt Current retry attempt (0-based)
     * @param retryAfterSeconds Server-provided Retry-After value (or null)
     * @return Delay in milliseconds before next retry
     */
    fun calculateRetryDelay(attempt: Int, retryAfterSeconds: Int? = null): Long {
        // If server provided Retry-After and we're configured to use it
        if (useRetryAfter && retryAfterSeconds != null && retryAfterSeconds > 0) {
            // Cap the retry-after value and convert to milliseconds
            val cappedRetryAfter = minOf(retryAfterSeconds, maxRetryAfterSeconds)
            return cappedRetryAfter * 1000L
        }
        
        // Otherwise use exponential backoff
        // Formula: min(maxBackoff, minBackoff * 2^attempt)
        val factor = 1L shl minOf(attempt, 10)  // Cap at 2^10 = 1024
        val delay = minOf(minRetryBackoffMs * factor, maxRetryBackoffMs)
        
        return delay
    }
    
    /**
     * Check if we should retry based on attempt count.
     */
    fun shouldRetry(attempt: Int): Boolean = attempt < retries
}

/**
 * Policy classes for request categorization.
 * Different classes can have different connection pool sizes and throttling.
 * 
 * Based on Firestorm's HTTP_POLICY_CLASS pattern.
 * 
 * Note: OkHttp's connection pool manages idle connections at the pool level.
 * For per-host request limiting, configure OkHttp's Dispatcher.maxRequestsPerHost.
 */
enum class PolicyClass(
    /**
     * Maximum number of idle connections to keep in the pool.
     * This is a pool-wide limit, not per-host.
     */
    val maxIdleConnections: Int,
    
    /**
     * Maximum concurrent requests per host.
     * Used to configure OkHttp's Dispatcher.maxRequestsPerHost.
     */
    val maxRequestsPerHost: Int,
    
    /**
     * Rate limit for requests per second.
     * 0 means no throttling.
     */
    val throttleRatePerSecond: Int
) {
    /**
     * Default policy for general requests.
     */
    DEFAULT(
        maxIdleConnections = 8,
        maxRequestsPerHost = 4,
        throttleRatePerSecond = 0  // No throttling
    ),
    
    /**
     * Login operations - single connection, no throttling.
     */
    LOGIN(
        maxIdleConnections = 2,
        maxRequestsPerHost = 1,
        throttleRatePerSecond = 0
    ),
    
    /**
     * Inventory operations - moderate parallelism.
     */
    INVENTORY(
        maxIdleConnections = 4,
        maxRequestsPerHost = 2,
        throttleRatePerSecond = 10  // Max 10 requests/second
    ),
    
    /**
     * Asset downloads (textures, mesh) - high parallelism.
     */
    ASSET(
        maxIdleConnections = 12,
        maxRequestsPerHost = 6,
        throttleRatePerSecond = 0  // No throttling for assets
    ),
    
    /**
     * Event queue - dedicated connection.
     */
    EVENT_QUEUE(
        maxIdleConnections = 1,
        maxRequestsPerHost = 1,
        throttleRatePerSecond = 0
    )
}
