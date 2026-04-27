package com.linkpoint.modern.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import java.io.IOException
import java.net.InetAddress
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Modern connection diagnostics for Second Life grid connectivity.
 * Provides comprehensive testing and troubleshooting capabilities.
 */
class ConnectionDiagnostics {
    private const val TAG: String = "ConnectionDiagnostics"
    
    // Second Life endpoints for testing
    private const val Array<String> SL_LOGIN_ENDPOINTS = {
        "https://login.agni.lindenlab.com/cgi-bin/login.cgi",  // Main grid
        "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"   // Beta grid
    }
    
    private const val Array<String> SL_TEST_DOMAINS = {
        "login.agni.lindenlab.com",
        "login.aditi.lindenlab.com",
        "secondlife.com",
        "lindenlab.com"
    }

    private val Context context
    private val OkHttpClient httpClient
    
    public ConnectionDiagnostics(Context context) {
        this.context = context
        this.httpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Comprehensive connection diagnosis for Second Life services.
     */
    public CompletableFuture<DiagnosticResult> diagnoseAsync() {
        return CompletableFuture.supplyAsync(() -> {
            Log.i(TAG, "Starting comprehensive Second Life connection diagnosis")
            
            val result: DiagnosticResult = DiagnosticResult()
            
            // Test 1: Network availability
            result.networkAvailable = isNetworkAvailable()
            if (!result.networkAvailable) {
                result.addIssue("No network connection available")
                return result
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
            
            Log.i(TAG, "Diagnosis complete. Overall health: " + result.getOverallHealth())
            return result
        })
    }

     private fun isNetworkAvailable(): Boolean {
        try {
            val cm: ConnectivityManager = (ConnectivityManager) 
                context.getSystemService(Context.CONNECTIVITY_SERVICE)
            
            if (cm == null) return false
            
            val activeNetwork: NetworkInfo = cm.getActiveNetworkInfo()
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting()
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability", e)
            return false
        }
    }

     private fun testDNSResolution(): Boolean {
        for (String domain : SL_TEST_DOMAINS) {
            try {
                val addresses: Array<InetAddress> = InetAddress.getAllByName(domain)
                if (addresses.length > 0) {
                    Log.d(TAG, "DNS resolution successful for " + domain + 
                        " -> " + addresses[0].getHostAddress())
                    return true; // At least one domain resolves
                }
            } catch (Exception e) {
                Log.w(TAG, "DNS resolution failed for " + domain, e)
            }
        }
        return false
    }

     private fun testHTTPSConnectivity(): Boolean {
        try {
            val request: Request = Request.Builder()
                .url("https://secondlife.com")
                .head() // Use HEAD to minimize data transfer
                .build()
                
            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful()
            }
        } catch (Exception e) {
            Log.w(TAG, "HTTPS connectivity test failed", e)
            return false
        }
    }

     private fun testLoginServerAccess(): Boolean {
        for (String endpoint : SL_LOGIN_ENDPOINTS) {
            try {
                val request: Request = Request.Builder()
                    .url(endpoint)
                    .head()
                    .build()
                    
                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful() || response.code() == 405) { // 405 Method Not Allowed is OK for login endpoint
                        Log.d(TAG, "Login server accessible: " + endpoint)
                        return true
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Login server test failed for " + endpoint, e)
            }
        }
        return false
    }

     private fun detectProxyOrFirewall(): Boolean {
        // Simple heuristic: if we can reach general internet but not SL-specific endpoints
        try {
            // Test general connectivity
            val googleTest: Request = Request.Builder()
                .url("https://www.google.com")
                .head()
                .build()
                
            Boolean canReachInternet
            try (Response response = httpClient.newCall(googleTest).execute()) {
                canReachInternet = response.isSuccessful()
            }
            
            if (canReachInternet && !testLoginServerAccess()) {
                return true; // Can reach internet but not SL servers - likely proxy/firewall
            }
        } catch (Exception e) {
            Log.d(TAG, "Proxy detection test error", e)
        }
        return false
    }

    /**
     * Result of connection diagnostics
     */
    @JvmStatic
    class DiagnosticResult {
        public Boolean networkAvailable = false
        public Boolean dnsWorking = false
        public Boolean httpsWorking = false
        public Boolean loginServerWorking = false
        public Boolean proxyDetected = false
        
        private StringBuilder issues = StringBuilder()
        
        fun addIssue(issue: String) {
            if (issues.length() > 0) {
                issues.append("; ")
            }
            issues.append(issue)
        }
        
         public fun getIssues(): String {
            return issues.toString()
        }
        
         public fun getOverallHealth(): HealthLevel {
            if (loginServerWorking) {
                return HealthLevel.EXCELLENT
            } else if (httpsWorking) {
                return HealthLevel.GOOD
            } else if (dnsWorking) {
                return HealthLevel.POOR
            } else if (networkAvailable) {
                return HealthLevel.CRITICAL
            } else {
                return HealthLevel.NO_CONNECTIVITY
            }
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