package com.linkpoint.network

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.net.wifi.WifiManager
import android.util.Log

/**
 * Utility class for network-related operations.
 * Provides helper methods for checking network conditions and configurations.
 */
object NetworkUtils {
    
    private const val TAG = "NetworkUtils"
    
    /**
     * Get a human-readable description of the mobile network type
     */
    fun getMobileNetworkTypeName(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            ?: return "Unknown"
        
        return try {
            when (telephonyManager.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
                
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
                
                TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
                
                TelephonyManager.NETWORK_TYPE_NR -> "5G"
                
                else -> "Mobile"
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Permission denied for getting network type", e)
            "Mobile"
        }
    }
    
    /**
     * Get WiFi signal strength as a percentage (0-100)
     */
    @Suppress("DEPRECATION")
    fun getWifiSignalStrength(context: Context): Int {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return -1
        
        return try {
            val wifiInfo = wifiManager.connectionInfo
            val rssi = wifiInfo.rssi
            // Convert RSSI to percentage (typically -100 to -50 dBm)
            WifiManager.calculateSignalLevel(rssi, 100)
        } catch (e: Exception) {
            Log.w(TAG, "Error getting WiFi signal strength", e)
            -1
        }
    }
    
    /**
     * Get WiFi SSID (network name) if connected
     */
    @Suppress("DEPRECATION")
    fun getWifiSSID(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return null
        
        return try {
            val wifiInfo = wifiManager.connectionInfo
            wifiInfo.ssid?.replace("\"", "")?.takeIf { it != "<unknown ssid>" }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting WiFi SSID", e)
            null
        }
    }
    
    /**
     * Check if the device is on a metered network (like mobile data)
     */
    fun isMeteredNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as? android.net.ConnectivityManager ?: return false
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return true
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return true
            !capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.isActiveNetworkMetered
        }
    }
    
    /**
     * Get recommended connection settings based on current network
     */
    fun getRecommendedSettings(context: Context): NetworkRecommendations {
        val networkMonitor = NetworkStateMonitor.getInstance(context)
        val state = networkMonitor.getCurrentState()
        
        return when {
            state.isWifi -> NetworkRecommendations(
                maxTextureDownloads = 8,
                highQualityTextures = true,
                compressedTextures = false,
                description = "WiFi connected - Full quality enabled"
            )
            state.isMobile -> {
                val networkType = getMobileNetworkTypeName(context)
                when (networkType) {
                    "5G" -> NetworkRecommendations(
                        maxTextureDownloads = 6,
                        highQualityTextures = true,
                        compressedTextures = true,
                        description = "5G connected - High quality with compression"
                    )
                    "4G LTE" -> NetworkRecommendations(
                        maxTextureDownloads = 4,
                        highQualityTextures = false,
                        compressedTextures = true,
                        description = "4G LTE connected - Balanced quality"
                    )
                    "3G" -> NetworkRecommendations(
                        maxTextureDownloads = 2,
                        highQualityTextures = false,
                        compressedTextures = true,
                        description = "3G connected - Reduced quality for stability"
                    )
                    else -> NetworkRecommendations(
                        maxTextureDownloads = 1,
                        highQualityTextures = false,
                        compressedTextures = true,
                        description = "Slow mobile connection - Minimum quality"
                    )
                }
            }
            else -> NetworkRecommendations(
                maxTextureDownloads = 2,
                highQualityTextures = false,
                compressedTextures = true,
                description = "Unknown network - Default settings"
            )
        }
    }
    
    data class NetworkRecommendations(
        val maxTextureDownloads: Int,
        val highQualityTextures: Boolean,
        val compressedTextures: Boolean,
        val description: String
    )
}