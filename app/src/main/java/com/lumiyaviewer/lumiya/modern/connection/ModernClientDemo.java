package com.lumiyaviewer.lumiya.modern.examples;

import android.content.Context;
import android.util.Log;
import com.lumiyaviewer.lumiya.modern.ModernLinkpointClient;
import com.lumiyaviewer.lumiya.modern.features.ModernSecondLifeFeatures;
import java.util.concurrent.CompletableFuture;

/**
 * Example demonstrating how to use the modern Linkpoint Second Life client
 * with all new features: improved connection reliability, modern authentication,
 * hybrid protocol support, and enhanced Second Life features.
 */
public class ModernClientDemo {
    private static final String TAG = "ModernClientDemo";
    
    /**
     * Simple connection test without full login
     */
    public static CompletableFuture<Boolean> testConnectionOnly(Context context) {
        Log.i(TAG, "=== TESTING CONNECTION ONLY ===");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                com.lumiyaviewer.lumiya.modern.connection.ModernConnectionManager connectionManager = 
                    new com.lumiyaviewer.lumiya.modern.connection.ModernConnectionManager(context);
                
                Log.i(TAG, "Connection manager created, state: " + connectionManager.getState());
                
                // Test basic connectivity without full authentication
                com.lumiyaviewer.lumiya.modern.connection.ConnectionDiagnostics diagnostics = 
                    new com.lumiyaviewer.lumiya.modern.connection.ConnectionDiagnostics(context);
                
                var diagnosticResult = diagnostics.diagnoseAsync().get();
                
                boolean connectionHealthy = diagnosticResult.networkAvailable && 
                    (diagnosticResult.dnsWorking || diagnosticResult.httpsWorking);
                
                Log.i(TAG, "Connection test result: " + (connectionHealthy ? "✅ HEALTHY" : "❌ ISSUES DETECTED"));
                
                return connectionHealthy;
                
            } catch (Exception e) {
                Log.e(TAG, "Connection test failed", e);
                return false;
            }
        });
    }
}