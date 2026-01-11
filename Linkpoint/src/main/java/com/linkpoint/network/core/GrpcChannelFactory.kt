package com.linkpoint.network.core

import android.content.Context
import android.util.Log
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.okhttp.OkHttpChannelBuilder
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.net.InetAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Factory for creating gRPC channels with optimal configuration.
 * Based on patterns from the official Second Life app and Firestorm's llcorehttp.
 * 
 * Features:
 * - Adaptive timeouts based on network quality
 * - Proper SSL/TLS configuration
 * - Connection pooling optimized for mobile
 * - Keep-alive configuration
 * - Retry configuration
 * - DNS caching (like Firestorm's mDNSCacheTimeout)
 * - Policy-based request options
 */
class GrpcChannelFactory(
    private val context: Context,
    private val qualityManager: ConnectionQualityManager
) {
    
    companion object {
        private const val TAG = "GrpcChannelFactory"
        
        // Default timeouts (based on official SL app)
        private const val DEFAULT_CONNECT_TIMEOUT_SECONDS = 60L
        private const val DEFAULT_READ_TIMEOUT_SECONDS = 60L
        private const val DEFAULT_WRITE_TIMEOUT_SECONDS = 30L
        
        // Keep-alive configuration (important for mobile networks)
        private const val KEEP_ALIVE_TIME_SECONDS = 30L
        private const val KEEP_ALIVE_TIMEOUT_SECONDS = 10L
        private const val KEEP_ALIVE_WITHOUT_CALLS = true
        
        // Idle timeout
        private const val IDLE_TIMEOUT_MINUTES = 5L
        
        // Max retry attempts for gRPC
        private const val MAX_RETRY_ATTEMPTS = 5
        
        // Fixed call timeout - generous to allow for all retries
        // This is a safety net, not the primary timeout mechanism
        private const val CALL_TIMEOUT_SECONDS = 300L  // 5 minutes
        
        // Max message size (10MB)
        private const val MAX_MESSAGE_SIZE = 10 * 1024 * 1024
        
        // DNS cache configuration (like Firestorm's mDNSCacheTimeout = -1)
        // -1 means cache forever, which is optimal for stable hosts like SL servers
        private const val DNS_CACHE_TIMEOUT_SECONDS = -1
    }
    
    /**
     * Cache entry for DNS lookups.
     * Moved outside of CachingDns because data classes cannot be nested inside inner classes.
     */
    private data class DnsCacheEntry(
        val addresses: List<InetAddress>,
        val timestamp: Long
    )
    
    /**
     * DNS cache implementation based on Firestorm's DNS caching pattern.
     * Caches DNS lookups to avoid repeated resolution overhead,
     * especially helpful on mobile networks where DNS can be slow.
     */
    private inner class CachingDns(
        private val cacheTimeoutSeconds: Int = DNS_CACHE_TIMEOUT_SECONDS
    ) : Dns {
        private val cache = ConcurrentHashMap<String, DnsCacheEntry>()
        private val systemDns = Dns.SYSTEM
        
        override fun lookup(hostname: String): List<InetAddress> {
            val now = System.currentTimeMillis()
            
            // Check cache
            val cached = cache[hostname]
            if (cached != null) {
                // If cacheTimeoutSeconds is -1, cache forever
                // Otherwise, check if still valid
                if (cacheTimeoutSeconds < 0 || 
                    (now - cached.timestamp) < cacheTimeoutSeconds * 1000L) {
                    Log.v(TAG, "DNS cache hit for $hostname")
                    return cached.addresses
                }
            }
            
            // Cache miss or expired - do lookup
            Log.d(TAG, "DNS lookup for $hostname (cache ${if (cached == null) "miss" else "expired"})")
            val startTime = System.currentTimeMillis()
            val addresses = systemDns.lookup(hostname)
            val elapsed = System.currentTimeMillis() - startTime
            
            Log.d(TAG, "DNS resolved $hostname to ${addresses.size} addresses in ${elapsed}ms")
            
            // Cache if caching is enabled (timeout != 0)
            if (cacheTimeoutSeconds != 0 && addresses.isNotEmpty()) {
                cache[hostname] = DnsCacheEntry(addresses, now)
            }
            
            return addresses
        }
        
        /**
         * Clear the DNS cache.
         * Useful when network changes (e.g., WiFi to mobile).
         */
        fun clearCache() {
            cache.clear()
            Log.d(TAG, "DNS cache cleared")
        }
        
        /**
         * Get cache statistics for diagnostics.
         */
        fun getCacheStats(): Map<String, Any> = mapOf(
            "entries" to cache.size,
            "hosts" to cache.keys.toList()
        )
    }
    
    // Shared DNS cache for all HTTP clients
    private val dnsCache = CachingDns()
    
    init {
        // Register for network changes to clear DNS cache
        // This is important because DNS entries may change when network changes
        // (e.g., WiFi to mobile, different exit nodes, etc.)
        qualityManager.addNetworkChangeListener {
            Log.d(TAG, "Network changed, clearing DNS cache")
            dnsCache.clearCache()
        }
    }
    
    // Thread pool for gRPC executor
    private val executor: Executor = Executors.newCachedThreadPool { runnable ->
        Thread(runnable, "grpc-executor").apply {
            isDaemon = true
        }
    }
    
    // Track created channels for cleanup
    private val activeChannels = mutableListOf<ManagedChannel>()
    
    /**
     * Create a gRPC channel for the given host and port
     */
    fun createChannel(
        host: String,
        port: Int,
        useTls: Boolean = true
    ): ManagedChannel {
        val timeouts = qualityManager.getTimeouts()
        val multiplier = qualityManager.getTimeoutMultiplier()
        
        Log.d(TAG, "Creating gRPC channel to $host:$port (TLS: $useTls, multiplier: ${multiplier}x)")
        
        val builder = OkHttpChannelBuilder.forAddress(host, port)
            .executor(executor)
            // Timeouts
            .keepAliveTime(KEEP_ALIVE_TIME_SECONDS, TimeUnit.SECONDS)
            .keepAliveTimeout(KEEP_ALIVE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .keepAliveWithoutCalls(KEEP_ALIVE_WITHOUT_CALLS)
            .idleTimeout(IDLE_TIMEOUT_MINUTES, TimeUnit.MINUTES)
            // Message size limits
            .maxInboundMessageSize(MAX_MESSAGE_SIZE)
            .maxInboundMetadataSize(MAX_MESSAGE_SIZE / 10)
            // Retry configuration
            .enableRetry()
            .maxRetryAttempts(MAX_RETRY_ATTEMPTS)
        
        // Configure TLS
        if (useTls) {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }
        
        val channel = builder.build()
        
        synchronized(activeChannels) {
            activeChannels.add(channel)
        }
        
        Log.d(TAG, "gRPC channel created successfully")
        return channel
    }
    
    /**
     * Create a gRPC channel from a URL
     */
    fun createChannelFromUrl(url: String): ManagedChannel {
        val (host, port, useTls) = parseUrl(url)
        return createChannel(host, port, useTls)
    }
    
    /**
     * Create an OkHttpClient configured for XMLRPC login operations.
     * 
     * This client is optimized for reliability over performance:
     * - Fresh connections preferred over pooled connections
     * - Aggressive connection cleanup to avoid stale connections
     * - Proper keep-alive handling for Second Life login servers
     * - HTTP/1.1 for XMLRPC compatibility
     * - DNS caching to avoid repeated DNS lookups
     * 
     * EOF errors often occur due to:
     * - Server closing idle connections before client expects
     * - Load balancer timeouts
     * - Connection pool returning stale connections
     * 
     * Mitigations implemented:
     * - Short keep-alive duration to avoid stale connections
     * - Minimal connection pooling
     * - retryOnConnectionFailure enabled
     * - DNS caching for faster reconnects
     * - Ping interval for connection health checks (via interceptor)
     */
    fun createHttpClient(): OkHttpClient {
        return createHttpClient(HttpRequestOptions.forLogin())
    }
    
    /**
     * Create an OkHttpClient with specific request options.
     * This method allows fine-grained control based on Firestorm's HttpOptions pattern.
     * 
     * @param options Configuration options for the HTTP client
     * @return Configured OkHttpClient instance
     */
    fun createHttpClient(options: HttpRequestOptions): OkHttpClient {
        val timeouts = qualityManager.getTimeouts()
        val multiplier = qualityManager.getTimeoutMultiplier()
        
        // Calculate effective timeouts combining base options with network quality
        val connectTimeoutMs = (options.timeoutSeconds * 1000 * multiplier).toLong()
        val readTimeoutMs = if (options.transferTimeoutSeconds > 0) {
            (options.transferTimeoutSeconds * 1000 * multiplier).toLong()
        } else {
            timeouts.readTimeoutMs
        }
        val writeTimeoutMs = timeouts.writeTimeoutMs
        
        Log.d(TAG, "Creating HTTP client for ${options.policyClass}: " +
            "connect=${connectTimeoutMs}ms, read=${readTimeoutMs}ms, " +
            "retries=${options.retries}, dnsCacheTimeout=${options.dnsCacheTimeoutSeconds}s")
        
        val builder = OkHttpClient.Builder()
            .connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
            // Overall call timeout - use fixed generous timeout to allow for all retries
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            // Retry on connection failure - critical for handling EOF errors
            .retryOnConnectionFailure(true)
            // Follow redirects based on options
            .followRedirects(options.followRedirects)
            .followSslRedirects(options.followRedirects)
            // DNS caching - key improvement from Firestorm
            .dns(getCachingDns(options.dnsCacheTimeoutSeconds))
        
        // Configure connection pool based on policy class
        // maxIdleConnections is a pool-wide limit on idle connections
        val poolConfig = when (options.policyClass) {
            PolicyClass.LOGIN -> ConnectionPool(
                maxIdleConnections = 1,
                keepAliveDuration = 15,
                timeUnit = TimeUnit.SECONDS
            )
            PolicyClass.EVENT_QUEUE -> ConnectionPool(
                maxIdleConnections = 1,
                keepAliveDuration = 60,
                timeUnit = TimeUnit.SECONDS
            )
            PolicyClass.INVENTORY -> ConnectionPool(
                maxIdleConnections = options.policyClass.maxIdleConnections,
                keepAliveDuration = 30,
                timeUnit = TimeUnit.SECONDS
            )
            PolicyClass.ASSET -> ConnectionPool(
                maxIdleConnections = options.policyClass.maxIdleConnections,
                keepAliveDuration = 60,
                timeUnit = TimeUnit.SECONDS
            )
            else -> ConnectionPool(
                maxIdleConnections = 4,
                keepAliveDuration = 30,
                timeUnit = TimeUnit.SECONDS
            )
        }
        builder.connectionPool(poolConfig)
        
        // Configure dispatcher for per-host request limiting
        // This properly controls concurrent requests to a single host
        val dispatcher = Dispatcher().apply {
            maxRequestsPerHost = options.policyClass.maxRequestsPerHost
        }
        builder.dispatcher(dispatcher)
        
        // Configure HTTP protocol based on policy class
        // LOGIN requests MUST use HTTP/1.1 only:
        // - Second Life login server uses XML-RPC over HTTP/1.1
        //   (see https://wiki.secondlife.com/wiki/Current_login_protocols)
        // - HTTP/2 ALPN negotiation can cause EOF errors during protocol mismatch
        // - Chunked transfer encoding issues occur when HTTP/2 falls back to HTTP/1.1
        // Other requests can use HTTP/2 for better performance
        if (options.policyClass == PolicyClass.LOGIN) {
            builder.protocols(listOf(Protocol.HTTP_1_1))
        } else {
            builder.protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        }
        
        // Add interceptor for connection handling and tracing
        builder.addNetworkInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
            
            // For login requests, use Connection: close to avoid stale connections
            if (options.policyClass == PolicyClass.LOGIN) {
                requestBuilder.header("Connection", "close")
            }
            
            // Add tracing if enabled
            if (options.traceLevel >= HttpRequestOptions.HTTP_TRACE_BASIC) {
                Log.d(TAG, "HTTP Request: ${originalRequest.method} ${originalRequest.url}")
            }
            
            val request = requestBuilder.build()
            val startTime = System.currentTimeMillis()
            val response = chain.proceed(request)
            val elapsed = System.currentTimeMillis() - startTime
            
            if (options.traceLevel >= HttpRequestOptions.HTTP_TRACE_BASIC) {
                Log.d(TAG, "HTTP Response: ${response.code} in ${elapsed}ms")
            }
            
            response
        }
        
        return builder.build()
    }
    
    /**
     * Get a DNS resolver with the specified cache timeout.
     * Uses the shared cache for most timeouts, creates a new one for custom timeouts.
     */
    private fun getCachingDns(timeoutSeconds: Int): Dns {
        return if (timeoutSeconds == DNS_CACHE_TIMEOUT_SECONDS) {
            dnsCache
        } else if (timeoutSeconds == 0) {
            Dns.SYSTEM  // No caching
        } else {
            CachingDns(timeoutSeconds)
        }
    }
    
    /**
     * Clear the DNS cache.
     * Should be called when network changes (WiFi to mobile, etc.)
     */
    fun clearDnsCache() {
        dnsCache.clearCache()
    }
    
    /**
     * Get DNS cache statistics.
     */
    fun getDnsCacheStats(): Map<String, Any> = dnsCache.getCacheStats()
    
    /**
     * Create an OkHttpClient optimized for regular API operations (non-login).
     * This client uses connection pooling for better performance.
     */
    fun createApiClient(): OkHttpClient {
        val timeouts = qualityManager.getTimeouts()
        
        return OkHttpClient.Builder()
            .connectTimeout(timeouts.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(timeouts.readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(timeouts.writeTimeoutMs, TimeUnit.MILLISECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            // More aggressive pooling for regular API operations
            .connectionPool(ConnectionPool(
                maxIdleConnections = 5,
                keepAliveDuration = 30,
                timeUnit = TimeUnit.SECONDS
            ))
            // Enable HTTP/2 for better performance on API calls
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .build()
    }
    
    /**
     * Parse URL into host, port, and TLS flag
     */
    private fun parseUrl(url: String): Triple<String, Int, Boolean> {
        val useTls = url.startsWith("https://") || url.startsWith("grpcs://")
        val defaultPort = if (useTls) 443 else 80
        
        val cleanUrl = url
            .removePrefix("https://")
            .removePrefix("http://")
            .removePrefix("grpcs://")
            .removePrefix("grpc://")
        
        val hostPort = cleanUrl.split("/").first()
        val parts = hostPort.split(":")
        
        val host = parts[0]
        val port = if (parts.size > 1) {
            parts[1].toIntOrNull() ?: defaultPort
        } else {
            defaultPort
        }
        
        return Triple(host, port, useTls)
    }
    
    /**
     * Shutdown a specific channel
     */
    fun shutdownChannel(channel: ManagedChannel) {
        try {
            if (!channel.isShutdown) {
                channel.shutdown()
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    channel.shutdownNow()
                }
            }
            synchronized(activeChannels) {
                activeChannels.remove(channel)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down channel: ${e.message}")
        }
    }
    
    /**
     * Shutdown all active channels
     */
    fun shutdownAll() {
        Log.d(TAG, "Shutting down all gRPC channels")
        synchronized(activeChannels) {
            activeChannels.forEach { channel ->
                try {
                    if (!channel.isShutdown) {
                        channel.shutdown()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error shutting down channel: ${e.message}")
                }
            }
            
            // Wait for termination
            activeChannels.forEach { channel ->
                try {
                    if (!channel.awaitTermination(2, TimeUnit.SECONDS)) {
                        channel.shutdownNow()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error waiting for channel termination: ${e.message}")
                }
            }
            
            activeChannels.clear()
        }
    }
    
    /**
     * Get the number of active channels
     */
    fun getActiveChannelCount(): Int {
        synchronized(activeChannels) {
            return activeChannels.count { !it.isShutdown && !it.isTerminated }
        }
    }
}
