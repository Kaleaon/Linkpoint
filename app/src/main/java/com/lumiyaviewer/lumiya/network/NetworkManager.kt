package com.lumiyaviewer.lumiya.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Network Manager for Linkpoint
 * 
 * Manages network operations including:
 * - Network connectivity monitoring
 * - HTTP requests
 * - Network state tracking
 * - Retry logic
 * - Timeout handling
 */
class NetworkManager(private val context: Context) {
    
    companion object {
        private const val TAG = "NetworkManager"
        private const val DEFAULT_TIMEOUT_MS = 30000
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val RETRY_DELAY_MS = 1000L
        
        @Volatile
        private var instance: NetworkManager? = null
        
        fun getInstance(context: Context): NetworkManager {
            return instance ?: synchronized(this) {
                instance ?: NetworkManager(context.applicationContext).also { 
                    instance = it 
                }
            }
        }
    }
    
    // Connectivity manager
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    // Network state
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Unknown)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()
    
    // Network callback
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    
    // Coroutine scope
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Network State
     */
    sealed class NetworkState {
        object Unknown : NetworkState()
        object Available : NetworkState()
        object Unavailable : NetworkState()
        data class Capabilities(
            val hasInternet: Boolean,
            val hasWifi: Boolean,
            val hasCellular: Boolean,
            val isMetered: Boolean
        ) : NetworkState()
    }
    
    /**
     * HTTP Method
     */
    enum class HttpMethod {
        GET, POST, PUT, DELETE, PATCH
    }
    
    /**
     * Network Request
     */
    data class NetworkRequest(
        val url: String,
        val method: HttpMethod = HttpMethod.GET,
        val headers: Map<String, String> = emptyMap(),
        val body: String? = null,
        val timeoutMs: Int = DEFAULT_TIMEOUT_MS
    )
    
    /**
     * Network Response
     */
    data class NetworkResponse(
        val statusCode: Int,
        val body: String,
        val headers: Map<String, List<String>>
    )
    
    /**
     * Network Result
     */
    sealed class NetworkResult {
        data class Success(val response: NetworkResponse) : NetworkResult()
        data class Failure(val message: String, val exception: Exception? = null) : NetworkResult()
    }
    
    init {
        startNetworkMonitoring()
        updateNetworkState()
    }
    
    /**
     * Start monitoring network connectivity
     */
    private fun startNetworkMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network available")
                updateNetworkState()
            }
            
            override fun onLost(network: Network) {
                Log.d(TAG, "Network lost")
                _networkState.value = NetworkState.Unavailable
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.d(TAG, "Network capabilities changed")
                updateNetworkState()
            }
        }
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
        Log.i(TAG, "Network monitoring started")
    }
    
    /**
     * Stop monitoring network connectivity
     */
    private fun stopNetworkMonitoring() {
        networkCallback?.let { callback ->
            try {
                connectivityManager.unregisterNetworkCallback(callback)
                Log.i(TAG, "Network monitoring stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping network monitoring", e)
            }
        }
        networkCallback = null
    }
    
    /**
     * Update network state
     */
    private fun updateNetworkState() {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        
        if (network == null || capabilities == null) {
            _networkState.value = NetworkState.Unavailable
            return
        }
        
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val hasCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        
        _networkState.value = NetworkState.Capabilities(
            hasInternet = hasInternet,
            hasWifi = hasWifi,
            hasCellular = hasCellular,
            isMetered = isMetered
        )
        
        Log.d(TAG, "Network state updated: hasInternet=$hasInternet, hasWifi=$hasWifi, hasCellular=$hasCellular, isMetered=$isMetered")
    }
    
    /**
     * Check if network is available
     */
    fun isNetworkAvailable(): Boolean {
        val state = _networkState.value
        return state is NetworkState.Available || state is NetworkState.Capabilities
    }
    
    /**
     * Check if connected to WiFi
     */
    fun isWifiConnected(): Boolean {
        val state = _networkState.value
        return state is NetworkState.Capabilities && state.hasWifi
    }
    
    /**
     * Check if connection is metered
     */
    fun isMeteredConnection(): Boolean {
        val state = _networkState.value
        return state is NetworkState.Capabilities && state.isMetered
    }
    
    /**
     * Execute HTTP request
     */
    suspend fun executeRequest(request: NetworkRequest): NetworkResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Executing ${request.method} request to ${request.url}")
            
            val connection = URL(request.url).openConnection() as HttpURLConnection
            
            try {
                // Configure connection
                connection.requestMethod = request.method.name
                connection.connectTimeout = request.timeoutMs
                connection.readTimeout = request.timeoutMs
                connection.doInput = true
                
                // Set headers
                request.headers.forEach { (key, value) ->
                    connection.setRequestProperty(key, value)
                }
                
                // Set body for POST/PUT/PATCH
                if (request.body != null && request.method in listOf(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH)) {
                    connection.doOutput = true
                    connection.outputStream.use { output ->
                        output.write(request.body.toByteArray())
                    }
                }
                
                // Get response
                val statusCode = connection.responseCode
                val responseBody = if (statusCode in 200..299) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                }
                
                val responseHeaders = connection.headerFields.filterKeys { it != null }
                    .mapKeys { it.key!! }
                
                Log.d(TAG, "Request completed with status $statusCode")
                
                NetworkResult.Success(
                    NetworkResponse(
                        statusCode = statusCode,
                        body = responseBody,
                        headers = responseHeaders
                    )
                )
                
            } finally {
                connection.disconnect()
            }
            
        } catch (e: IOException) {
            Log.e(TAG, "Network request failed", e)
            NetworkResult.Failure("Network request failed: ${e.message}", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during network request", e)
            NetworkResult.Failure("Unexpected error: ${e.message}", e)
        }
    }
    
    /**
     * Execute request with retry
     */
    suspend fun executeRequestWithRetry(
        request: NetworkRequest,
        maxAttempts: Int = MAX_RETRY_ATTEMPTS
    ): NetworkResult = withContext(Dispatchers.IO) {
        var lastResult: NetworkResult? = null
        
        repeat(maxAttempts) { attempt ->
            Log.d(TAG, "Request attempt ${attempt + 1}/$maxAttempts")
            
            val result = executeRequest(request)
            
            if (result is NetworkResult.Success) {
                return@withContext result
            }
            
            lastResult = result
            
            if (attempt < maxAttempts - 1) {
                delay(RETRY_DELAY_MS * (attempt + 1))
            }
        }
        
        lastResult ?: NetworkResult.Failure("All retry attempts failed")
    }
    
    /**
     * GET request
     */
    suspend fun get(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): NetworkResult {
        return executeRequest(
            NetworkRequest(
                url = url,
                method = HttpMethod.GET,
                headers = headers
            )
        )
    }
    
    /**
     * POST request
     */
    suspend fun post(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap()
    ): NetworkResult {
        return executeRequest(
            NetworkRequest(
                url = url,
                method = HttpMethod.POST,
                body = body,
                headers = headers
            )
        )
    }
    
    /**
     * PUT request
     */
    suspend fun put(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap()
    ): NetworkResult {
        return executeRequest(
            NetworkRequest(
                url = url,
                method = HttpMethod.PUT,
                body = body,
                headers = headers
            )
        )
    }
    
    /**
     * DELETE request
     */
    suspend fun delete(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): NetworkResult {
        return executeRequest(
            NetworkRequest(
                url = url,
                method = HttpMethod.DELETE,
                headers = headers
            )
        )
    }
    
    /**
     * Download file
     */
    suspend fun downloadFile(
        url: String,
        outputPath: String,
        onProgress: ((Float) -> Unit)? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Downloading file from $url")
            
            val connection = URL(url).openConnection() as HttpURLConnection
            val contentLength = connection.contentLength
            
            connection.inputStream.use { input ->
                java.io.FileOutputStream(outputPath).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead = 0L
                    
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        
                        if (contentLength > 0) {
                            val progress = totalBytesRead.toFloat() / contentLength
                            withContext(Dispatchers.Main) {
                                onProgress?.invoke(progress)
                            }
                        }
                    }
                }
            }
            
            Log.i(TAG, "File downloaded successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading file", e)
            false
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        stopNetworkMonitoring()
        scope.cancel()
        Log.i(TAG, "NetworkManager cleaned up")
    }
}

/**
 * Extension functions
 */

/**
 * Check if response is successful
 */
fun NetworkManager.NetworkResponse.isSuccessful(): Boolean {
    return statusCode in 200..299
}

/**
 * Get header value
 */
fun NetworkManager.NetworkResponse.getHeader(name: String): String? {
    return headers[name]?.firstOrNull()
}