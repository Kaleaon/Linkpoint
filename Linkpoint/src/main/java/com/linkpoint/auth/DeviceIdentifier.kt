package com.linkpoint.auth

import android.content.Context
import android.provider.Settings
import android.util.Log
import java.net.NetworkInterface
import java.security.MessageDigest
import java.util.UUID

/**
 * Manages persistent device identification for Second Life login.
 * 
 * Matches behavior of official viewers (Firestorm, LibreMetaverse):
 * - MAC address: Hashed for privacy, persistent across sessions
 * - ID0: Hardware identifier, persistent across sessions
 * - Viewer digest: Application integrity check
 * 
 * These identifiers are required by the Second Life login protocol and
 * must remain consistent between login sessions for the same device.
 * 
 * @see <a href="https://wiki.secondlife.com/wiki/Current_login_protocols">SL Login Protocol</a>
 */
class DeviceIdentifier(private val context: Context) {
    
    companion object {
        private const val TAG = "DeviceIdentifier"
        private const val PREFS_NAME = "linkpoint_device_identity"
        private const val KEY_MAC_ADDRESS = "mac_address"
        private const val KEY_ID0 = "id0"
        private const val KEY_VIEWER_DIGEST = "viewer_digest"
    }
    
    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Get persistent, hashed MAC address.
     * 
     * Official viewers hash the first valid NIC MAC or use a persistent fallback.
     * The hash is MD5 of the MAC address, returned as a 32-character hex string.
     * 
     * @return Hashed MAC address (32-char hex string)
     */
    fun getMacAddress(): String {
        return prefs.getString(KEY_MAC_ADDRESS, null) ?: generateAndStoreMac()
    }
    
    /**
     * Get persistent device ID (ID0).
     * 
     * This should be consistent across app sessions and is typically
     * the same as or derived from the MAC hash.
     * 
     * @return Device ID0 (32-char hex string)
     */
    fun getId0(): String {
        return prefs.getString(KEY_ID0, null) ?: generateAndStoreId0()
    }
    
    /**
     * Get viewer digest for integrity verification.
     * 
     * Official desktop viewers use MD5 of the viewer executable.
     * For mobile, we use a persistent UUID that represents the installation.
     * 
     * @return Viewer digest UUID string
     */
    fun getViewerDigest(): String {
        return prefs.getString(KEY_VIEWER_DIGEST, null) ?: generateAndStoreViewerDigest()
    }
    
    /**
     * Force regeneration of all device identifiers.
     * Use with caution - this will change your device identity on the grid.
     */
    fun regenerateIdentifiers() {
        prefs.edit()
            .remove(KEY_MAC_ADDRESS)
            .remove(KEY_ID0)
            .remove(KEY_VIEWER_DIGEST)
            .apply()
        
        // Generate new values
        getMacAddress()
        getId0()
        getViewerDigest()
        
        Log.i(TAG, "Device identifiers regenerated")
    }
    
    private fun generateAndStoreMac(): String {
        val mac = try {
            // Try to get actual device MAC
            val interfaces = NetworkInterface.getNetworkInterfaces()
            var foundMac: String? = null
            
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val hwAddr = iface.hardwareAddress
                
                if (hwAddr != null && hwAddr.size == 6) {
                    val macStr = hwAddr.joinToString("") { String.format("%02X", it) }
                    if (macStr != "000000000000") {
                        // Hash for privacy (like official viewers)
                        foundMac = md5Hash(macStr)
                        Log.d(TAG, "Found valid MAC from interface: ${iface.name}")
                        break
                    }
                }
            }
            
            foundMac
        } catch (e: Exception) {
            Log.w(TAG, "Could not get hardware MAC: ${e.message}")
            null
        } ?: run {
            // Fallback: Use Android ID for consistent identification
            val androidId = try {
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            } catch (e: Exception) {
                Log.w(TAG, "Could not get Android ID: ${e.message}")
                null
            }
            
            md5Hash(androidId ?: UUID.randomUUID().toString())
        }
        
        prefs.edit().putString(KEY_MAC_ADDRESS, mac).apply()
        Log.d(TAG, "Generated and stored MAC hash")
        return mac
    }
    
    private fun generateAndStoreId0(): String {
        // ID0 is typically derived from the same source as MAC
        // Some viewers use MD5 of Android ID directly
        val id0 = try {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            md5Hash(androidId ?: UUID.randomUUID().toString())
        } catch (e: Exception) {
            Log.w(TAG, "Could not generate ID0 from Android ID: ${e.message}")
            md5Hash(UUID.randomUUID().toString())
        }
        
        prefs.edit().putString(KEY_ID0, id0).apply()
        Log.d(TAG, "Generated and stored ID0")
        return id0
    }
    
    private fun generateAndStoreViewerDigest(): String {
        // For mobile, use a persistent UUID representing this installation
        val digest = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_VIEWER_DIGEST, digest).apply()
        Log.d(TAG, "Generated and stored viewer digest")
        return digest
    }
    
    /**
     * Compute MD5 hash of input string.
     * 
     * @param input String to hash
     * @return 32-character lowercase hex string
     */
    private fun md5Hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
