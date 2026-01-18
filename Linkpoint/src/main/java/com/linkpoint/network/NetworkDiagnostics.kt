package com.linkpoint.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log

/**
 * Network diagnostics utility for debugging and optimizing login on Android devices.
 * 
 * Provides detailed network type detection, connection quality estimation,
 * and diagnostic information for troubleshooting login issues, especially
 * on mobile/LTE networks where conditions vary significantly.
 */
object NetworkDiagnostics {
    
    private const val TAG = "NetworkDiagnostics"
    
    /**
     * Represents the detected network type with detailed information
     */
    data class NetworkInfo(
        val isConnected: Boolean,
        val type: NetworkType,
        val subType: String,
        val isMetered: Boolean,
        val estimatedBandwidthKbps: Int,
        val estimatedLatencyMs: Int,
        val supportsIpv6: Boolean,
        val details: String
    ) {
        /**
         * Recommended timeout multiplier based on network conditions
         */
        val timeoutMultiplier: Float
            get() = when {
                type == NetworkType.WIFI && !isMetered -> 1.0f
                type == NetworkType.ETHERNET -> 1.0f
                type == NetworkType.CELLULAR_5G -> 1.2f
                type == NetworkType.CELLULAR_LTE -> 1.5f
                type == NetworkType.CELLULAR_4G -> 1.5f
                type == NetworkType.CELLULAR_3G -> 2.0f
                type == NetworkType.CELLULAR_2G -> 3.0f
                type == NetworkType.VPN -> 1.3f
                type == NetworkType.UNKNOWN -> 2.0f
                else -> 1.5f
            }
        
        /**
         * Recommended number of retry attempts based on network type.
         * 
         * Mobile networks, especially LTE, can experience transient connection
         * issues (EOF, resets) more frequently than Wi-Fi. Increased retry
         * counts help ensure successful connections on unstable networks.
         */
        val recommendedRetries: Int
            get() = when (type) {
                NetworkType.WIFI, NetworkType.ETHERNET -> 2
                NetworkType.CELLULAR_5G -> 3
                NetworkType.CELLULAR_LTE, NetworkType.CELLULAR_4G -> 4  // Increased from 3 for LTE
                NetworkType.CELLULAR_3G -> 5  // Increased from 4
                NetworkType.CELLULAR_2G -> 6  // Increased from 5
                else -> 4  // Default increased from 3
            }
        
        /**
         * Human-readable description for UI display
         */
        val displayName: String
            get() = when (type) {
                NetworkType.WIFI -> "Wi-Fi"
                NetworkType.ETHERNET -> "Ethernet"
                NetworkType.CELLULAR_5G -> "5G Mobile"
                NetworkType.CELLULAR_LTE -> "LTE"
                NetworkType.CELLULAR_4G -> "4G"
                NetworkType.CELLULAR_3G -> "3G"
                NetworkType.CELLULAR_2G -> "2G"
                NetworkType.VPN -> "VPN"
                NetworkType.BLUETOOTH -> "Bluetooth"
                NetworkType.UNKNOWN -> "Unknown"
                NetworkType.NONE -> "No Connection"
            }
    }
    
    enum class NetworkType {
        NONE,
        WIFI,
        ETHERNET,
        CELLULAR_5G,
        CELLULAR_LTE,
        CELLULAR_4G,
        CELLULAR_3G,
        CELLULAR_2G,
        VPN,
        BLUETOOTH,
        UNKNOWN
    }
    
    /**
     * Get detailed network information for the current connection
     */
    fun getNetworkInfo(context: Context): NetworkInfo {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        
        if (network == null) {
            Log.d(TAG, "No active network")
            return NetworkInfo(
                isConnected = false,
                type = NetworkType.NONE,
                subType = "None",
                isMetered = false,
                estimatedBandwidthKbps = 0,
                estimatedLatencyMs = -1,
                supportsIpv6 = false,
                details = "No active network connection"
            )
        }
        
        val capabilities = cm.getNetworkCapabilities(network)
        if (capabilities == null) {
            Log.d(TAG, "No network capabilities available")
            return NetworkInfo(
                isConnected = false,
                type = NetworkType.UNKNOWN,
                subType = "Unknown",
                isMetered = true,
                estimatedBandwidthKbps = 0,
                estimatedLatencyMs = -1,
                supportsIpv6 = false,
                details = "Network capabilities unavailable"
            )
        }
        
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        
        // Detect network type
        val (networkType, subType) = detectNetworkType(context, capabilities)
        
        // Estimate bandwidth and latency
        val bandwidth = estimateBandwidth(capabilities, networkType)
        val latency = estimateLatency(networkType)
        
        // Check IPv6 support
        val supportsIpv6 = checkIpv6Support(cm, network)
        
        val details = buildString {
            appendLine("Transport: $subType")
            appendLine("Has Internet: $hasInternet")
            appendLine("Validated: $hasValidated")
            appendLine("Metered: $isMetered")
            appendLine("Est. Bandwidth: ${bandwidth}kbps")
            appendLine("Est. Latency: ${latency}ms")
            appendLine("IPv6: $supportsIpv6")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appendLine("Link Downstream: ${capabilities.linkDownstreamBandwidthKbps}kbps")
                appendLine("Link Upstream: ${capabilities.linkUpstreamBandwidthKbps}kbps")
            }
        }
        
        Log.d(TAG, "Network info: $networkType ($subType), metered=$isMetered, bandwidth=$bandwidth, validated=$hasValidated")
        
        // IMPORTANT: Only require hasInternet, NOT hasValidated
        // NET_CAPABILITY_VALIDATED is too strict and fails on many mobile networks:
        // - LTE networks where validation is slow or fails temporarily
        // - Networks behind captive portals
        // - Networks where Google's connectivity check is blocked
        // 
        // The actual HTTP request will determine if connectivity works.
        // This matches Lumiya's behavior (which logs in instantly on the same networks).
        return NetworkInfo(
            isConnected = hasInternet,  // Don't require validated!
            type = networkType,
            subType = subType,
            isMetered = isMetered,
            estimatedBandwidthKbps = bandwidth,
            estimatedLatencyMs = latency,
            supportsIpv6 = supportsIpv6,
            details = details
        )
    }
    
    private fun detectNetworkType(context: Context, capabilities: NetworkCapabilities): Pair<NetworkType, String> {
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                NetworkType.WIFI to "Wi-Fi"
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                NetworkType.ETHERNET to "Ethernet"
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                detectCellularType(context)
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                NetworkType.VPN to "VPN"
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> {
                NetworkType.BLUETOOTH to "Bluetooth Tethering"
            }
            else -> {
                NetworkType.UNKNOWN to "Unknown"
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun detectCellularType(context: Context): Pair<NetworkType, String> {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            
            // Note: getDataNetworkType requires READ_PHONE_STATE permission
            // We try to get it but fall back gracefully if not available
            val networkType = try {
                telephonyManager.dataNetworkType
            } catch (e: SecurityException) {
                Log.d(TAG, "Cannot access data network type: ${e.message}")
                TelephonyManager.NETWORK_TYPE_UNKNOWN
            }
            
            return when (networkType) {
                // 5G
                TelephonyManager.NETWORK_TYPE_NR -> NetworkType.CELLULAR_5G to "5G NR"
                
                // 4G LTE
                TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.CELLULAR_LTE to "LTE"
                
                // 4G
                TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkType.CELLULAR_4G to "HSPA+"
                
                // 3G
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EHRPD -> NetworkType.CELLULAR_3G to "3G"
                
                // 2G
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN -> NetworkType.CELLULAR_2G to "2G"
                
                else -> NetworkType.CELLULAR_LTE to "Mobile Data" // Assume LTE as fallback
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error detecting cellular type: ${e.message}")
            return NetworkType.CELLULAR_LTE to "Mobile Data"
        }
    }
    
    private fun estimateBandwidth(capabilities: NetworkCapabilities, type: NetworkType): Int {
        // First try to get actual reported bandwidth
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val downstream = capabilities.linkDownstreamBandwidthKbps
            if (downstream > 0) {
                return downstream
            }
        }
        
        // Fall back to estimated bandwidth based on network type
        return when (type) {
            NetworkType.WIFI -> 50000 // 50 Mbps typical
            NetworkType.ETHERNET -> 100000 // 100 Mbps typical
            NetworkType.CELLULAR_5G -> 100000 // 100 Mbps typical
            NetworkType.CELLULAR_LTE -> 25000 // 25 Mbps typical
            NetworkType.CELLULAR_4G -> 10000 // 10 Mbps typical
            NetworkType.CELLULAR_3G -> 2000 // 2 Mbps typical
            NetworkType.CELLULAR_2G -> 100 // 100 kbps typical
            NetworkType.VPN -> 20000 // Depends on underlying connection
            NetworkType.BLUETOOTH -> 1000 // Limited
            else -> 1000
        }
    }
    
    private fun estimateLatency(type: NetworkType): Int {
        // Estimated round-trip latency in milliseconds
        return when (type) {
            NetworkType.WIFI -> 20
            NetworkType.ETHERNET -> 5
            NetworkType.CELLULAR_5G -> 20
            NetworkType.CELLULAR_LTE -> 50
            NetworkType.CELLULAR_4G -> 80
            NetworkType.CELLULAR_3G -> 150
            NetworkType.CELLULAR_2G -> 500
            NetworkType.VPN -> 50
            NetworkType.BLUETOOTH -> 100
            else -> 100
        }
    }
    
    @Suppress("DEPRECATION")
    private fun checkIpv6Support(cm: ConnectivityManager, network: android.net.Network): Boolean {
        return try {
            val linkProperties = cm.getLinkProperties(network)
            linkProperties?.linkAddresses?.any { 
                it.address is java.net.Inet6Address 
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Generate a full diagnostic report for troubleshooting
     */
    fun generateDiagnosticReport(context: Context): String {
        val networkInfo = getNetworkInfo(context)
        
        return buildString {
            appendLine("=== Linkpoint Network Diagnostics ===")
            appendLine()
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine()
            appendLine("=== Network Status ===")
            appendLine("Connected: ${networkInfo.isConnected}")
            appendLine("Type: ${networkInfo.displayName} (${networkInfo.subType})")
            appendLine("Metered: ${networkInfo.isMetered}")
            appendLine("IPv6: ${networkInfo.supportsIpv6}")
            appendLine()
            appendLine("=== Connection Quality ===")
            appendLine("Est. Bandwidth: ${networkInfo.estimatedBandwidthKbps} kbps")
            appendLine("Est. Latency: ${networkInfo.estimatedLatencyMs} ms")
            appendLine("Timeout Multiplier: ${networkInfo.timeoutMultiplier}x")
            appendLine("Recommended Retries: ${networkInfo.recommendedRetries}")
            appendLine()
            appendLine("=== Details ===")
            append(networkInfo.details)
        }
    }
    
    /**
     * Calculate adaptive timeout values based on current network conditions
     */
    fun getAdaptiveTimeouts(context: Context): AdaptiveTimeouts {
        val networkInfo = getNetworkInfo(context)
        val multiplier = networkInfo.timeoutMultiplier
        
        return AdaptiveTimeouts(
            connectTimeoutMs = (30000 * multiplier).toLong(),
            readTimeoutMs = (60000 * multiplier).toLong(),
            writeTimeoutMs = (30000 * multiplier).toLong(),
            retryCount = networkInfo.recommendedRetries,
            initialRetryDelayMs = (500 * multiplier).toLong()
        )
    }
    
    data class AdaptiveTimeouts(
        val connectTimeoutMs: Long,
        val readTimeoutMs: Long,
        val writeTimeoutMs: Long,
        val retryCount: Int,
        val initialRetryDelayMs: Long
    )
}
