package com.linkpoint.modern.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.util.concurrent.TimeUnit

/**
 * Modern connection diagnostics for Second Life grid connectivity.
 * Provides comprehensive testing and troubleshooting capabilities.
 * 
 * Fixed from original broken syntax to proper Kotlin.
 */
class ConnectionDiagnostics(private val context: Context) {
    
    companion object {
        private const val TAG = "ConnectionDiagnostics"
        
        // Second Life endpoints for testing
        private val SL_LOGIN_ENDPOINTS = arrayOf(
            "https://login.agni.lindenlab.com/cgi-bin/login.cgi",  // Main grid
            "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"   // Beta grid
        )
        
        private val SL_TEST_DOMAINS = arrayOf(
            "login.agni.lindenlab.com",
            "login.aditi.lindenlab.com",
            "secondlife.com",
            "lindenlab.com"
        )
    }
    
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Comprehensive connection diagnosis for Second Life services.
     */
    suspend fun diagnoseAsync(): DiagnosticResult = withContext(Dispatchers.IO) {
        Log.i(TAG, "Starting comprehensive Second Life connection diagnosis")
        
        val result = DiagnosticResult()
        
        // Test 1: Network availability
        result.networkAvailable = isNetworkAvailable()
        if (!result.networkAvailable) {
            result.addIssue("No network connection available")
            return@withContext result
        }
        
        // Test 2: DNS resolution
        result.dnsWorking = testDNSResolution()
        if (!result.dnsWorking) {
            result.addIssue("DNS resolution failed for Second Life domains")
        }
        
        // Test 3: HTTPS connectivity
        result.httpsWorking = testHTTPSConnectivity()
        if (!result.httpsWorking) {
            result.addIssue("HTTPS connectivity issues detected")
        }
        
        // Test 4: Second Life login server accessibility
        result.loginServerWorking = testLoginServerAccess()
        if (!result.loginServerWorking) {
            result.addIssue("Cannot reach Second Life login servers")
        }
        
        // Test 5: Firewall/proxy detection
        result.proxyDetected = detectProxyOrFirewall()
        if (result.proxyDetected) {
            result.addIssue("Proxy or firewall detected - may impact connectivity")
        }
        
        Log.i(TAG, "Diagnosis complete. Overall health: ${result.getOverallHealth()}")
        result
    }
    
    private fun isNetworkAvailable(): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork ?: return false
                val capabilities = cm.getNetworkCapabilities(network) ?: return false
                
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            } else {
                @Suppress("DEPRECATION")
                val activeNetwork = cm.activeNetworkInfo
                activeNetwork != null && activeNetwork.isConnectedOrConnecting
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking network availability", e)
            false
        }
    }
    
    private fun testDNSResolution(): Boolean {
        for (domain in SL_TEST_DOMAINS) {
            try {
                val addresses = InetAddress.getAllByName(domain)
                if (addresses.isNotEmpty()) {
                    Log.d(TAG, "DNS resolution successful for $domain -> ${addresses[0].hostAddress}")
                    return true
                }
            } catch (e: Exception) {
                Log.w(TAG, "DNS resolution failed for $domain", e)
            }
        }
        return false
    }
    
    private fun testHTTPSConnectivity(): Boolean {
        return try {
            val request = Request.Builder()
                .url("https://secondlife.com")
                .head()
                .build()
            
            httpClient.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.w(TAG, "HTTPS connectivity test failed", e)
            false
        }
    }
    
    private fun testLoginServerAccess(): Boolean {
        for (endpoint in SL_LOGIN_ENDPOINTS) {
            try {
                val request = Request.Builder()
                    .url(endpoint)
                    .head()
                    .build()
                
                httpClient.newCall(request).execute().use { response ->
                    // 405 Method Not Allowed is OK for login endpoint
                    if (response.isSuccessful || response.code == 405) {
                        Log.d(TAG, "Login server accessible: $endpoint")
                        return true
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Login server test failed for $endpoint", e)
            }
        }
        return false
    }
    
    private fun detectProxyOrFirewall(): Boolean {
        return try {
            // Test general connectivity
            val googleTest = Request.Builder()
                .url("https://www.google.com")
                .head()
                .build()
            
            val canReachInternet = httpClient.newCall(googleTest).execute().use { response ->
                response.isSuccessful
            }
            
            // Can reach internet but not SL servers - likely proxy/firewall
            canReachInternet && !testLoginServerAccess()
        } catch (e: Exception) {
            Log.d(TAG, "Proxy detection test error", e)
            false
        }
    }
    
    /**
     * Result of connection diagnostics
     */
    data class DiagnosticResult(
        var networkAvailable: Boolean = false,
        var dnsWorking: Boolean = false,
        var httpsWorking: Boolean = false,
        var loginServerWorking: Boolean = false,
        var proxyDetected: Boolean = false
    ) {
        private val issues = StringBuilder()
        
        fun addIssue(issue: String) {
            if (issues.isNotEmpty()) {
                issues.append("; ")
            }
            issues.append(issue)
        }
        
        fun getIssues(): String = issues.toString()
        
        fun getOverallHealth(): HealthLevel = when {
            loginServerWorking -> HealthLevel.EXCELLENT
            httpsWorking -> HealthLevel.GOOD
            dnsWorking -> HealthLevel.POOR
            networkAvailable -> HealthLevel.CRITICAL
            else -> HealthLevel.NO_CONNECTIVITY
        }
        
        enum class HealthLevel {
            EXCELLENT,
            GOOD,
            POOR,
            CRITICAL,
            NO_CONNECTIVITY
        }
    }
}
