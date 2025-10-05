package com.linkpoint.modern.examples

import android.content.Context
import android.util.Log
import com.linkpoint.modern.ModernLinkpointClient
import com.linkpoint.modern.features.ModernSecondLifeFeatures
import java.util.concurrent.CompletableFuture

/**
 * Example demonstrating how to use the modern Linkpoint Second Life client
 * with all features: improved connection reliability, modern authentication,
 * hybrid protocol support, and enhanced Second Life features.
 */
class ModernClientDemo {
    private const val TAG: String = "ModernClientDemo"
    
    /**
     * Simple connection test without full login
     */
    @JvmStatic
    CompletableFuture<Boolean> testConnectionOnly(Context context) {
        Log.i(TAG, "=== TESTING CONNECTION ONLY ===")
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                com.lumiyaviewer.lumiya.modern.connection.ModernConnectionManager connectionManager = 
                    com.lumiyaviewer.lumiya.modern.connection.ModernConnectionManager(context)
                
                Log.i(TAG, "Connection manager created, state: " + connectionManager.getState())
                
                // Test basic connectivity without full authentication
                com.lumiyaviewer.lumiya.modern.connection.ConnectionDiagnostics diagnostics = 
                    com.lumiyaviewer.lumiya.modern.connection.ConnectionDiagnostics(context)
                
                var diagnosticResult = diagnostics.diagnoseAsync().get()
                
                Boolean connectionHealthy = diagnosticResult.networkAvailable && 
                    (diagnosticResult.dnsWorking || diagnosticResult.httpsWorking)
                
                Log.i(TAG, "Connection test result: " + (connectionHealthy ? "✅ HEALTHY" : "❌ ISSUES DETECTED"))
                
                return connectionHealthy
                
            } catch (Exception e) {
                Log.e(TAG, "Connection test failed", e)
                return false
            }
        })
    }
}