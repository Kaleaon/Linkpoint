package com.linkpoint.network.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import com.linkpoint.network.NetworkDiagnostics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max
import kotlin.math.min

/**
 * Manages connection quality detection and monitoring.
 * Based on patterns from the official Second Life app.
 * 
 * Features:
 * - Real-time network quality assessment
 * - Latency tracking and averaging
 * - Bandwidth estimation
 * - Network change detection
 * - Quality-based timeout recommendations
 */
class ConnectionQualityManager(private val context: Context) {
    
    companion object {
        private const val TAG = "ConnectionQuality"
        
        // Quality thresholds
        private const val EXCELLENT_LATENCY_MS = 50
        private const val GOOD_LATENCY_MS = 100
        private const val FAIR_LATENCY_MS = 200
        private const val POOR_LATENCY_MS = 500
        
        private const val EXCELLENT_BANDWIDTH_KBPS = 50000  // 50 Mbps
        private const val GOOD_BANDWIDTH_KBPS = 10000       // 10 Mbps
        private const val FAIR_BANDWIDTH_KBPS = 2000        // 2 Mbps
        
        // Sample window
        private const val MAX_LATENCY_SAMPLES = 10
        private const val MAX_ERROR_SAMPLES = 20
        
        // Timeout configuration based on official app patterns
        const val CONNECT_TIMEOUT_MS = 60_000L        // 60 seconds for connection
        const val SUBSCRIBE_TIMEOUT_MS = 30_000L      // 30 seconds for subscriptions
        const val PUSH_TIMEOUT_MS = 15_000L           // 15 seconds for push operations
        const val READ_TIMEOUT_MS = 60_000L           // 60 seconds for reads
        const val WRITE_TIMEOUT_MS = 30_000L          // 30 seconds for writes
    }
    
    /**
     * Connection quality levels
     */
    enum class Quality {
        EXCELLENT,
        GOOD,
        FAIR,
        POOR,
        UNKNOWN
    }
    
    // State flows
    private val _quality = MutableStateFlow(Quality.UNKNOWN)
    val quality: StateFlow<Quality> = _quality.asStateFlow()
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private val _networkType = MutableStateFlow(NetworkDiagnostics.NetworkType.UNKNOWN)
    val networkType: StateFlow<NetworkDiagnostics.NetworkType> = _networkType.asStateFlow()
    
    // Latency tracking
    private val latencySamples = ConcurrentLinkedQueue<Long>()
    private var averageLatencyMs: Long = 0
    
    // Error rate tracking
    private val errorSamples = ConcurrentLinkedQueue<Boolean>()  // true = error, false = success
    private var errorRate: Float = 0f
    
    // Bandwidth estimation
    private var estimatedBandwidthKbps: Int = 0
    
    // Network callback
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    // Network change listeners (for DNS cache clearing, etc.)
    private val networkChangeListeners = mutableListOf<() -> Unit>()
    
    init {
        startMonitoring()
        updateNetworkInfo()
    }
    
    /**
     * Start monitoring network changes
     */
    private fun startMonitoring() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network available")
                _isConnected.value = true
                updateNetworkInfo()
                // Notify listeners of network change (for DNS cache clearing, etc.)
                notifyNetworkChange()
            }
            
            override fun onLost(network: Network) {
                Log.d(TAG, "Network lost")
                _isConnected.value = false
                _quality.value = Quality.UNKNOWN
                // Notify listeners of network change
                notifyNetworkChange()
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                updateFromCapabilities(capabilities)
            }
        }
        
        try {
            connectivityManager.registerNetworkCallback(request, networkCallback!!)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register network callback: ${e.message}")
        }
    }
    
    /**
     * Stop monitoring network changes
     */
    fun stopMonitoring() {
        networkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to unregister network callback: ${e.message}")
            }
        }
        networkCallback = null
        networkChangeListeners.clear()
    }
    
    /**
     * Register a listener for network changes.
     * Used by GrpcChannelFactory to clear DNS cache when network changes.
     */
    fun addNetworkChangeListener(listener: () -> Unit) {
        synchronized(networkChangeListeners) {
            networkChangeListeners.add(listener)
        }
    }
    
    /**
     * Remove a network change listener.
     */
    fun removeNetworkChangeListener(listener: () -> Unit) {
        synchronized(networkChangeListeners) {
            networkChangeListeners.remove(listener)
        }
    }
    
    /**
     * Notify all listeners of network change.
     * Creates a snapshot of listeners to avoid holding the lock during callback execution,
     * which could cause deadlocks if a listener tries to acquire another lock.
     */
    private fun notifyNetworkChange() {
        val listeners = synchronized(networkChangeListeners) {
            networkChangeListeners.toList() // Create a snapshot
        }
        listeners.forEach { listener ->
            try {
                listener()
            } catch (e: Exception) {
                Log.e(TAG, "Error notifying network change listener", e)
            }
        }
    }
    
    /**
     * Update network info from NetworkDiagnostics
     */
    private fun updateNetworkInfo() {
        try {
            val info = NetworkDiagnostics.getNetworkInfo(context)
            _isConnected.value = info.isConnected
            _networkType.value = info.type
            estimatedBandwidthKbps = info.estimatedBandwidthKbps
            
            // Update quality based on network type and bandwidth
            determineQuality()
            
            Log.d(TAG, "Network updated: ${info.displayName}, " +
                "bandwidth: ${info.estimatedBandwidthKbps}kbps, " +
                "quality: ${_quality.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating network info: ${e.message}")
        }
    }
    
    /**
     * Update from network capabilities
     * 
     * IMPORTANT: We only require NET_CAPABILITY_INTERNET, NOT NET_CAPABILITY_VALIDATED.
     * 
     * NET_CAPABILITY_VALIDATED is too strict and fails intermittently on mobile networks:
     * - LTE networks where validation is slow or fails temporarily  
     * - Networks behind captive portals
     * - Networks where Google's connectivity check is blocked
     * 
     * The actual HTTP request will determine if connectivity works.
     * This matches Lumiya's behavior (which logs in instantly on the same networks).
     */
    private fun updateFromCapabilities(capabilities: NetworkCapabilities) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val downstream = capabilities.linkDownstreamBandwidthKbps
            if (downstream > 0) {
                estimatedBandwidthKbps = downstream
            }
        }
        
        // Only require internet capability - NOT validated
        // Validated check is too strict for many mobile networks
        _isConnected.value = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        
        determineQuality()
    }
    
    /**
     * Record a latency sample
     */
    fun recordLatency(latencyMs: Long) {
        // Add to samples
        latencySamples.add(latencyMs)
        
        // Keep only recent samples
        while (latencySamples.size > MAX_LATENCY_SAMPLES) {
            latencySamples.poll()
        }
        
        // Calculate average
        val samples = latencySamples.toList()
        averageLatencyMs = if (samples.isNotEmpty()) {
            samples.sum() / samples.size
        } else {
            0
        }
        
        determineQuality()
    }
    
    /**
     * Record a request result (success or error)
     */
    fun recordRequestResult(success: Boolean) {
        errorSamples.add(!success)
        
        while (errorSamples.size > MAX_ERROR_SAMPLES) {
            errorSamples.poll()
        }
        
        val samples = errorSamples.toList()
        errorRate = if (samples.isNotEmpty()) {
            samples.count { it }.toFloat() / samples.size
        } else {
            0f
        }
        
        determineQuality()
    }
    
    /**
     * Determine connection quality based on all factors
     */
    private fun determineQuality() {
        val quality = when {
            !_isConnected.value -> Quality.UNKNOWN
            
            // Check error rate first
            errorRate > 0.5f -> Quality.POOR
            errorRate > 0.25f -> Quality.FAIR
            
            // Check latency
            averageLatencyMs > 0 -> when {
                averageLatencyMs <= EXCELLENT_LATENCY_MS -> Quality.EXCELLENT
                averageLatencyMs <= GOOD_LATENCY_MS -> Quality.GOOD
                averageLatencyMs <= FAIR_LATENCY_MS -> Quality.FAIR
                else -> Quality.POOR
            }
            
            // Fall back to bandwidth-based estimation
            estimatedBandwidthKbps >= EXCELLENT_BANDWIDTH_KBPS -> Quality.EXCELLENT
            estimatedBandwidthKbps >= GOOD_BANDWIDTH_KBPS -> Quality.GOOD
            estimatedBandwidthKbps >= FAIR_BANDWIDTH_KBPS -> Quality.FAIR
            estimatedBandwidthKbps > 0 -> Quality.POOR
            
            else -> Quality.UNKNOWN
        }
        
        if (_quality.value != quality) {
            Log.d(TAG, "Quality changed: ${_quality.value} -> $quality " +
                "(latency: ${averageLatencyMs}ms, bandwidth: ${estimatedBandwidthKbps}kbps, errorRate: $errorRate)")
            _quality.value = quality
        }
    }
    
    /**
     * Get recommended timeout multiplier based on quality
     */
    fun getTimeoutMultiplier(): Float {
        return when (_quality.value) {
            Quality.EXCELLENT -> 1.0f
            Quality.GOOD -> 1.2f
            Quality.FAIR -> 1.5f
            Quality.POOR -> 2.0f
            Quality.UNKNOWN -> 1.5f
        }
    }
    
    /**
     * Get recommended retry policy based on quality
     */
    fun getRetryPolicy(): RetryPolicy {
        return when (_quality.value) {
            Quality.EXCELLENT, Quality.GOOD -> RetryPolicy.forWifi()
            Quality.FAIR, Quality.POOR, Quality.UNKNOWN -> RetryPolicy.forMobileNetwork()
        }
    }
    
    /**
     * Get adaptive timeout configuration
     */
    fun getTimeouts(): TimeoutConfig {
        val multiplier = getTimeoutMultiplier()
        return TimeoutConfig(
            connectTimeoutMs = (CONNECT_TIMEOUT_MS * multiplier).toLong(),
            subscribeTimeoutMs = (SUBSCRIBE_TIMEOUT_MS * multiplier).toLong(),
            pushTimeoutMs = (PUSH_TIMEOUT_MS * multiplier).toLong(),
            readTimeoutMs = (READ_TIMEOUT_MS * multiplier).toLong(),
            writeTimeoutMs = (WRITE_TIMEOUT_MS * multiplier).toLong()
        )
    }
    
    /**
     * Get quality report for diagnostics
     */
    fun getQualityReport(): QualityReport {
        return QualityReport(
            quality = _quality.value,
            isConnected = _isConnected.value,
            networkType = _networkType.value,
            averageLatencyMs = averageLatencyMs,
            estimatedBandwidthKbps = estimatedBandwidthKbps,
            errorRate = errorRate,
            sampleCount = latencySamples.size,
            timeoutMultiplier = getTimeoutMultiplier()
        )
    }
    
    /**
     * Log network diagnostics
     */
    fun logNetworkDiagnostics() {
        val report = getQualityReport()
        Log.d(TAG, "=== NETWORK DIAGNOSTICS ===")
        Log.d(TAG, "  Quality: ${report.quality}")
        Log.d(TAG, "  Connected: ${report.isConnected}")
        Log.d(TAG, "  Network Type: ${report.networkType}")
        Log.d(TAG, "  Average Latency: ${report.averageLatencyMs}ms")
        Log.d(TAG, "  Est. Bandwidth: ${report.estimatedBandwidthKbps}kbps")
        Log.d(TAG, "  Error Rate: ${(report.errorRate * 100).toInt()}%")
        Log.d(TAG, "  Timeout Multiplier: ${report.timeoutMultiplier}x")
    }
    
    data class TimeoutConfig(
        val connectTimeoutMs: Long,
        val subscribeTimeoutMs: Long,
        val pushTimeoutMs: Long,
        val readTimeoutMs: Long,
        val writeTimeoutMs: Long
    )
    
    data class QualityReport(
        val quality: Quality,
        val isConnected: Boolean,
        val networkType: NetworkDiagnostics.NetworkType,
        val averageLatencyMs: Long,
        val estimatedBandwidthKbps: Int,
        val errorRate: Float,
        val sampleCount: Int,
        val timeoutMultiplier: Float
    )
}
