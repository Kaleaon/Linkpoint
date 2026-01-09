package com.linkpoint.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Modern network state monitor using NetworkCallback API.
 * Provides real-time network connectivity monitoring for both WiFi and mobile networks.
 * 
 * Fixes connectivity issues by:
 * 1. Using modern NetworkCallback API instead of deprecated NetworkInfo
 * 2. Properly detecting network type (WiFi vs Mobile)
 * 3. Providing reactive state updates for UI/connection managers
 * 4. Handling network transitions gracefully
 */
class NetworkStateMonitor(private val context: Context) {
    
    companion object {
        private const val TAG = "NetworkStateMonitor"
        
        @Volatile
        private var instance: NetworkStateMonitor? = null
        
        fun getInstance(context: Context): NetworkStateMonitor {
            return instance ?: synchronized(this) {
                instance ?: NetworkStateMonitor(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    /**
     * Represents the current network state
     */
    data class NetworkState(
        val isConnected: Boolean = false,
        val networkType: NetworkType = NetworkType.NONE,
        val isValidated: Boolean = false,
        val hasInternet: Boolean = false,
        val signalStrength: Int = -1, // -1 means unknown
        val errorMessage: String? = null
    ) {
        val isWifi: Boolean get() = networkType == NetworkType.WIFI
        val isMobile: Boolean get() = networkType == NetworkType.MOBILE
        val isUsable: Boolean get() = isConnected && hasInternet
    }
    
    enum class NetworkType {
        NONE,
        WIFI,
        MOBILE,
        ETHERNET,
        VPN,
        OTHER
    }
    
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    
    private val _networkState = MutableStateFlow(NetworkState())
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()
    
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isMonitoring = false
    
    /**
     * Start monitoring network changes
     */
    fun startMonitoring() {
        if (isMonitoring) {
            Log.d(TAG, "Already monitoring network state")
            return
        }
        
        Log.i(TAG, "Starting network state monitoring")
        
        // Get initial state
        updateNetworkState()
        
        // Register callback for network changes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerNetworkCallback()
        } else {
            registerLegacyNetworkCallback()
        }
        
        isMonitoring = true
    }
    
    /**
     * Stop monitoring network changes
     */
    fun stopMonitoring() {
        if (!isMonitoring) return
        
        Log.i(TAG, "Stopping network state monitoring")
        
        networkCallback?.let { callback ->
            try {
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (e: Exception) {
                Log.w(TAG, "Error unregistering network callback", e)
            }
        }
        networkCallback = null
        isMonitoring = false
    }
    
    /**
     * Force refresh the current network state
     */
    fun refreshState() {
        updateNetworkState()
    }
    
    /**
     * Get current network state synchronously
     */
    fun getCurrentState(): NetworkState = _networkState.value
    
    /**
     * Check if network is currently available for Second Life connection
     */
    fun isNetworkAvailableForSL(): Boolean {
        val state = getCurrentState()
        return state.isConnected && state.hasInternet
    }
    
    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network available: $network")
                updateNetworkState(network)
            }
            
            override fun onLost(network: Network) {
                Log.d(TAG, "Network lost: $network")
                updateNetworkState(null)
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.d(TAG, "Network capabilities changed for: $network")
                updateNetworkStateFromCapabilities(network, networkCapabilities)
            }
            
            override fun onUnavailable() {
                Log.d(TAG, "Network unavailable")
                _networkState.value = NetworkState(
                    isConnected = false,
                    networkType = NetworkType.NONE,
                    errorMessage = "No network available"
                )
            }
        }
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback!!)
            } else {
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register network callback", e)
            _networkState.value = NetworkState(
                errorMessage = "Failed to monitor network: ${e.message}"
            )
        }
    }
    
    @Suppress("DEPRECATION")
    private fun registerLegacyNetworkCallback() {
        // Fallback for older Android versions
        updateNetworkState()
    }
    
    private fun updateNetworkState(network: Network? = null) {
        try {
            val activeNetwork = network ?: connectivityManager.activeNetwork
            
            if (activeNetwork == null) {
                _networkState.value = NetworkState(
                    isConnected = false,
                    networkType = NetworkType.NONE,
                    errorMessage = "No active network"
                )
                return
            }
            
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (capabilities != null) {
                updateNetworkStateFromCapabilities(activeNetwork, capabilities)
            } else {
                _networkState.value = NetworkState(
                    isConnected = false,
                    networkType = NetworkType.NONE,
                    errorMessage = "Cannot determine network capabilities"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating network state", e)
            _networkState.value = NetworkState(
                errorMessage = "Error checking network: ${e.message}"
            )
        }
    }
    
    private fun updateNetworkStateFromCapabilities(
        network: Network,
        capabilities: NetworkCapabilities
    ) {
        val networkType = when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.MOBILE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NetworkType.VPN
            else -> NetworkType.OTHER
        }
        
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        
        // Get signal strength if available (API 29+)
        val signalStrength = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            capabilities.signalStrength
        } else {
            -1
        }
        
        val newState = NetworkState(
            isConnected = true,
            networkType = networkType,
            isValidated = isValidated,
            hasInternet = hasInternet,
            signalStrength = signalStrength,
            errorMessage = null
        )
        
        Log.i(TAG, "Network state updated: connected=${newState.isConnected}, " +
                "type=${newState.networkType}, validated=${newState.isValidated}, " +
                "hasInternet=${newState.hasInternet}")
        
        _networkState.value = newState
    }
    
    /**
     * Perform a basic connectivity test to Second Life servers
     */
    suspend fun testSecondLifeConnectivity(): ConnectivityTestResult {
        val state = getCurrentState()
        
        if (!state.isConnected) {
            return ConnectivityTestResult(
                success = false,
                networkAvailable = false,
                message = "No network connection available"
            )
        }
        
        if (!state.hasInternet) {
            return ConnectivityTestResult(
                success = false,
                networkAvailable = true,
                message = "Network connected but no internet access"
            )
        }
        
        // Try to reach Second Life login servers
        return try {
            val url = java.net.URL("https://login.agni.lindenlab.com")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.requestMethod = "HEAD"
            
            val responseCode = connection.responseCode
            connection.disconnect()
            
            if (responseCode in 200..499) {
                ConnectivityTestResult(
                    success = true,
                    networkAvailable = true,
                    secondLifeReachable = true,
                    message = "Second Life servers are reachable"
                )
            } else {
                ConnectivityTestResult(
                    success = false,
                    networkAvailable = true,
                    secondLifeReachable = false,
                    message = "Second Life servers returned unexpected response: $responseCode"
                )
            }
        } catch (e: java.net.UnknownHostException) {
            ConnectivityTestResult(
                success = false,
                networkAvailable = true,
                secondLifeReachable = false,
                message = "DNS resolution failed - cannot resolve Second Life servers"
            )
        } catch (e: java.net.SocketTimeoutException) {
            ConnectivityTestResult(
                success = false,
                networkAvailable = true,
                secondLifeReachable = false,
                message = "Connection timed out - Second Life servers may be blocked or unreachable"
            )
        } catch (e: Exception) {
            ConnectivityTestResult(
                success = false,
                networkAvailable = true,
                secondLifeReachable = false,
                message = "Connection test failed: ${e.message}"
            )
        }
    }
    
    data class ConnectivityTestResult(
        val success: Boolean,
        val networkAvailable: Boolean,
        val secondLifeReachable: Boolean = false,
        val latencyMs: Long = -1,
        val message: String
    )
}