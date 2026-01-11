package com.linkpoint.network.core

import android.content.Context
import android.util.Log
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.okhttp.OkHttpChannelBuilder
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Factory for creating gRPC channels with optimal configuration.
 * Based on patterns from the official Second Life app.
 * 
 * Features:
 * - Adaptive timeouts based on network quality
 * - Proper SSL/TLS configuration
 * - Connection pooling optimized for mobile
 * - Keep-alive configuration
 * - Retry configuration
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
        
        // Max message size (10MB)
        private const val MAX_MESSAGE_SIZE = 10 * 1024 * 1024
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
     * Create an OkHttpClient configured for gRPC-like operations
     * (for XMLRPC login which doesn't use gRPC)
     */
    fun createHttpClient(): OkHttpClient {
        val timeouts = qualityManager.getTimeouts()
        
        Log.d(TAG, "Creating HTTP client with adaptive timeouts: " +
            "connect=${timeouts.connectTimeoutMs}ms, " +
            "read=${timeouts.readTimeoutMs}ms, " +
            "write=${timeouts.writeTimeoutMs}ms")
        
        return OkHttpClient.Builder()
            .connectTimeout(timeouts.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(timeouts.readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(timeouts.writeTimeoutMs, TimeUnit.MILLISECONDS)
            // Overall call timeout
            .callTimeout(
                (timeouts.connectTimeoutMs + timeouts.readTimeoutMs + timeouts.writeTimeoutMs),
                TimeUnit.MILLISECONDS
            )
            // Retry on connection failure
            .retryOnConnectionFailure(true)
            // Minimal connection pooling for mobile
            .connectionPool(ConnectionPool(
                maxIdleConnections = 2,
                keepAliveDuration = 30,
                timeUnit = TimeUnit.SECONDS
            ))
            // Force HTTP/1.1 for XMLRPC compatibility
            .protocols(listOf(Protocol.HTTP_1_1))
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
