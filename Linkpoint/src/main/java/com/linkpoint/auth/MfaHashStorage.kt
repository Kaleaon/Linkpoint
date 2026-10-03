package com.linkpoint.auth

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure storage for MFA (Multi-Factor Authentication) hashes.
 * 
 * When a user successfully completes MFA verification, the server returns
 * an `mfa_hash` that can be used on subsequent logins to skip the MFA prompt.
 * This class stores those hashes securely.
 * 
 * The hash is device-specific - it only works on the device where MFA was
 * originally verified.
 * 
 * @see <a href="https://wiki.secondlife.com/wiki/User:Brad_Linden/Login_MFA">SL MFA Documentation</a>
 */
class MfaHashStorage(context: Context) {
    
    companion object {
        private const val TAG = "MfaHashStorage"
        private const val PREFS_NAME = "linkpoint_mfa_storage"
        private const val KEY_PREFIX = "mfa_hash_"
    }
    
    private val prefs by lazy {
        try {
            // Use encrypted preferences for security
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.w(TAG, "Could not create encrypted prefs, falling back to regular prefs: ${e.message}")
            // Fallback to regular SharedPreferences if encryption fails
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }
    
    /**
     * Store MFA hash for a user.
     * 
     * @param username The user's login name (first.last or username)
     * @param mfaHash The hash returned by the server after successful MFA
     */
    fun saveMfaHash(username: String, mfaHash: String) {
        val key = makeKey(username)
        prefs.edit().putString(key, mfaHash).apply()
        Log.d(TAG, "Saved MFA hash for user: ${username.take(3)}***")
    }
    
    /**
     * Get stored MFA hash for a user.
     * 
     * @param username The user's login name
     * @return The stored MFA hash, or null if none exists
     */
    fun getMfaHash(username: String): String? {
        val key = makeKey(username)
        return prefs.getString(key, null)
    }
    
    /**
     * Check if we have a stored MFA hash for a user.
     * 
     * @param username The user's login name
     * @return true if an MFA hash is stored
     */
    fun hasMfaHash(username: String): Boolean {
        return getMfaHash(username) != null
    }
    
    /**
     * Clear MFA hash for a user.
     * 
     * Call this if the stored hash is rejected by the server,
     * or when the user explicitly wants to re-verify MFA.
     * 
     * @param username The user's login name
     */
    fun clearMfaHash(username: String) {
        val key = makeKey(username)
        prefs.edit().remove(key).apply()
        Log.d(TAG, "Cleared MFA hash for user: ${username.take(3)}***")
    }
    
    /**
     * Clear all stored MFA hashes.
     * 
     * Use with caution - all users will need to re-verify MFA.
     */
    fun clearAllMfaHashes() {
        prefs.edit().clear().apply()
        Log.i(TAG, "Cleared all MFA hashes")
    }
    
    /**
     * Get list of usernames that have stored MFA hashes.
     * 
     * @return List of usernames (partially masked for privacy in logs)
     */
    fun getStoredUsers(): List<String> {
        return prefs.all.keys
            .filter { it.startsWith(KEY_PREFIX) }
            .map { it.removePrefix(KEY_PREFIX) }
    }
    
    private fun makeKey(username: String): String {
        // Normalize username to lowercase for consistent lookup
        return KEY_PREFIX + username.lowercase().trim()
    }
}
