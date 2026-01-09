package com.linkpoint.network

import android.content.Context
import android.util.Log
import com.linkpoint.GlobalOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

/**
 * Applies network-based optimizations to Second Life settings.
 * Automatically adjusts quality settings based on network conditions.
 */
class NetworkOptimizer(private val context: Context) {
    
    companion object {
        private const val TAG = "NetworkOptimizer"
    }
    
    private val networkMonitor = NetworkStateMonitor.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isEnabled = false
    
    /**
     * Enable automatic network optimization
     */
    fun enable() {
        if (isEnabled) return
        
        Log.i(TAG, "Enabling automatic network optimization")
        isEnabled = true
        
        scope.launch {
            networkMonitor.networkState.collectLatest { state ->
                if (isEnabled) {
                    applyOptimizations(state)
                }
            }
        }
    }
    
    /**
     * Disable automatic network optimization
     */
    fun disable() {
        Log.i(TAG, "Disabling automatic network optimization")
        isEnabled = false
    }
    
    /**
     * Manually apply optimizations based on current network state
     */
    fun applyCurrentOptimizations() {
        val state = networkMonitor.getCurrentState()
        applyOptimizations(state)
    }
    
    private fun applyOptimizations(state: NetworkStateMonitor.NetworkState) {
        val recommendations = NetworkUtils.getRecommendedSettings(context)
        
        Log.i(TAG, "Applying network optimizations: ${recommendations.description}")
        
        // Apply recommendations to GlobalOptions
        // Note: In a real implementation, this would update SharedPreferences
        // which would trigger GlobalOptions to update
        
        try {
            val prefs = context.getSharedPreferences(
                "${context.packageName}_preferences",
                Context.MODE_PRIVATE
            )
            
            prefs.edit().apply {
                putString("max_texture_downloads", recommendations.maxTextureDownloads.toString())
                putBoolean("high_quality_textures", recommendations.highQualityTextures)
                putBoolean("compressed_textures", recommendations.compressedTextures)
                apply()
            }
            
            Log.d(TAG, "Applied settings: downloads=${recommendations.maxTextureDownloads}, " +
                    "highQuality=${recommendations.highQualityTextures}, " +
                    "compressed=${recommendations.compressedTextures}")
        } catch (e: Exception) {
            Log.e(TAG, "Error applying network optimizations", e)
        }
    }
    
    /**
     * Shutdown the optimizer
     */
    fun shutdown() {
        isEnabled = false
        scope.cancel()
    }
}