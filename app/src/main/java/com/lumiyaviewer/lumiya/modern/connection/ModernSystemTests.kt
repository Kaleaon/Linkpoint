package com.lumiyaviewer.lumiya.modern.tests

import android.util.Log
import com.lumiyaviewer.lumiya.modern.connection.ConnectionDiagnostics
import com.lumiyaviewer.lumiya.modern.connection.ModernConnectionManager
import com.lumiyaviewer.lumiya.modern.auth.ModernAuthManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Simple test class to validate modern connection and authentication systems.
 * This provides a way to test the functionality without full build system.
 */
class ModernSystemTests {
    private String TAG = "ModernSystemTests";
    
    /**
     * Test connection diagnostics functionality
     */
    fun testConnectionDiagnostics(context: android.content.Context): CompletableFuture<Boolean> {
        Log.i(TAG, "Testing connection diagnostics...");
        
        ConnectionDiagnostics diagnostics = ConnectionDiagnostics(context)
        
        return diagnostics.diagnoseAsync()
            .thenApply(result -> {
                Log.i(TAG, "Diagnostics completed:");
                Log.i(TAG, "  Network Available: " + result.networkAvailable);
                Log.i(TAG, "  DNS Working: " + result.dnsWorking);
                Log.i(TAG, "  HTTPS Working: " + result.httpsWorking);
                Log.i(TAG, "  Login Server Working: " + result.loginServerWorking);
                Log.i(TAG, "  Proxy Detected: " + result.proxyDetected);
                Log.i(TAG, "  Overall Health: " + result.getOverallHealth());
                
                if (!result.getIssues().isEmpty()) {
                    Log.w(TAG, "  Issues: " + result.getIssues());
                }
                
                // Test passes if we get any level of connectivity
                Boolean testPassed = result.networkAvailable
                Log.i(TAG, "Connection diagnostics test " + (testPassed ? "PASSED" : "FAILED"));
                
                return testPassed
            })
            .exceptionally(throwable -> {
                Log.e(TAG, "Connection diagnostics test FAILED with exception", throwable);
                return false
            })
    }
    
    /**
     * Test authentication manager functionality
     */
    fun testAuthenticationManager(context: android.content.Context): CompletableFuture<Boolean> {
        Log.i(TAG, "Testing authentication manager...");
        
        ModernAuthManager authManager = ModernAuthManager(context)
        
        return authManager.authenticateAsync("testuser", "testpass")
            .thenApply(result -> {
                Log.i(TAG, "Authentication completed:");
                Log.i(TAG, "  Successful: " + result.isSuccessful());
                Log.i(TAG, "  Valid: " + result.isValid());
                
                if (result.isSuccessful()) {
                    Log.i(TAG, "  Access Token: " + (result.getAccessToken() != null ? "Present" : "Missing"));
                    Log.i(TAG, "  Refresh Token: " + (result.getRefreshToken() != null ? "Present" : "Missing"));
                    Log.i(TAG, "  Expiry: " + result.getExpiryTime());
                } else {
                    Log.w(TAG, "  Error: " + result.getErrorMessage());
                }
                
                Boolean testPassed = result.isSuccessful() && result.isValid()
                Log.i(TAG, "Authentication manager test " + (testPassed ? "PASSED" : "FAILED"));
                
                return testPassed
            })
            .exceptionally(throwable -> {
                Log.e(TAG, "Authentication manager test FAILED with exception", throwable);
                return false
            })
    }
    
    /**
     * Generate a diagnostic report for troubleshooting
     */
    fun generateDiagnosticReport(context: android.content.Context): CompletableFuture<String> {
        ConnectionDiagnostics diagnostics = ConnectionDiagnostics(context)
        
        return diagnostics.diagnoseAsync()
            .thenApply(result -> {
                StringBuilder report = StringBuilder()
                report.append("=== LINKPOINT SECOND LIFE CONNECTION DIAGNOSTIC REPORT ===\n");
                report.append("Generated: ").append(java.util.Date()).append("\n\n");
                
                report.append("CONNECTIVITY TESTS:\n");
                report.append("  Network Available: ").append(result.networkAvailable ? "✅ YES" : "❌ NO").append("\n");
                report.append("  DNS Resolution: ").append(result.dnsWorking ? "✅ WORKING" : "❌ FAILED").append("\n");
                report.append("  HTTPS Connectivity: ").append(result.httpsWorking ? "✅ WORKING" : "❌ FAILED").append("\n");
                report.append("  SL Login Servers: ").append(result.loginServerWorking ? "✅ ACCESSIBLE" : "❌ BLOCKED").append("\n");
                report.append("  Proxy/Firewall: ").append(result.proxyDetected ? "⚠️ DETECTED" : "✅ CLEAR").append("\n");
                
                report.append("\nOVERALL HEALTH: ").append(result.getOverallHealth()).append("\n");
                
                if (!result.getIssues().isEmpty()) {
                    report.append("\nISSUES FOUND:\n");
                    report.append("  ").append(result.getIssues()).append("\n");
                }
                
                report.append("\nRECOMMENDATIONS:\n");
                switch (result.getOverallHealth()) {
                    case EXCELLENT:
                        report.append("  ✅ Your connection is excellent! Second Life login should work perfectly.\n");
                        break
                    case GOOD:
                        report.append("  ✅ Your connection is good. If login fails, the Second Life servers may be temporarily down.\n");
                        break
                    case POOR:
                        report.append("  ⚠️ Limited connectivity detected. Check your firewall and proxy settings.\n");
                        report.append("  Consider switching to a different network or contacting your network administrator.\n");
                        break
                    case CRITICAL:
                        report.append("  ❌ Critical network issues detected. Check your internet connection.\n");
                        report.append("  Try connecting to a different WiFi network or enable mobile data.\n");
                        break
                    case NO_CONNECTIVITY:
                        report.append("  ❌ No network connectivity detected. Enable WiFi or mobile data and try again.\n");
                        break
                }
                
                return report.toString()
            })
    }
}
