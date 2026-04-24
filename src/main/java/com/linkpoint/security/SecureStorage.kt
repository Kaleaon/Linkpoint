package com.linkpoint.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import android.util.Log
import android.content.SharedPreferences

/**
 * Secure Storage Manager - Provides encrypted storage for sensitive data.
 * 
 * Uses Android Keystore for key management and EncryptedSharedPreferences
 * for secure data storage.
 * 
 * Thread-safe: All operations are thread-safe
 * 
 * @property context Application context
 */
class SecureStorage(private val context: Context) {
    
    companion object {
        private const val TAG = "SecureStorage"
        private const val PREFS_FILE_NAME = "linkpoint_secure_prefs"
        
        // Keys for common stored values
        const val KEY_USERNAME = "stored_username"
        const val KEY_PASSWORD = "stored_password"
        const val KEY_GRID_ID = "stored_grid_id"
        const val KEY_SESSION_TOKEN = "session_token"
        const val KEY_START_LOCATION = "start_location"
        const val KEY_AVATAR_ID = "avatar_id"
    }
    
    private val masterKey: MasterKey
    private val encryptedPrefs: SharedPreferences
    
    init {
        try {
            // Create or retrieve master key from Android Keystore
            masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .setKeyGenParameterSpec(
                    KeyGenParameterSpec.Builder(
                        MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setUserAuthenticationRequired(false)
                        .build()
                )
                .build()
            
            // Create encrypted SharedPreferences
            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            
            Log.d(TAG, "SecureStorage initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SecureStorage", e)
            throw SecurityException("Cannot initialize secure storage", e)
        }
    }
    
    /**
     * Save a string value securely
     */
    fun saveString(key: String, value: String?) {
        try {
            if (value == null) {
                encryptedPrefs.edit().remove(key).apply()
            } else {
                encryptedPrefs.edit().putString(key, value).apply()
            }
            Log.d(TAG, "Saved value for key: $key")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save value for key: $key", e)
        }
    }
    
    /**
     * Retrieve a string value securely
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return try {
            encryptedPrefs.getString(key, defaultValue)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve value for key: $key", e)
            defaultValue
        }
    }
    
    /**
     * Save an integer value securely
     */
    fun saveInt(key: String, value: Int) {
        try {
            encryptedPrefs.edit().putInt(key, value).apply()
            Log.d(TAG, "Saved int value for key: $key")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save int value for key: $key", e)
        }
    }
    
    /**
     * Retrieve an integer value securely
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return try {
            encryptedPrefs.getInt(key, defaultValue)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve int value for key: $key", e)
            defaultValue
        }
    }
    
    /**
     * Save a long value securely
     */
    fun saveLong(key: String, value: Long) {
        try {
            encryptedPrefs.edit().putLong(key, value).apply()
            Log.d(TAG, "Saved long value for key: $key")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save long value for key: $key", e)
        }
    }
    
    /**
     * Retrieve a long value securely
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return try {
            encryptedPrefs.getLong(key, defaultValue)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve long value for key: $key", e)
            defaultValue
        }
    }
    
    /**
     * Save a boolean value securely
     */
    fun saveBoolean(key: String, value: Boolean) {
        try {
            encryptedPrefs.edit().putBoolean(key, value).apply()
            Log.d(TAG, "Saved boolean value for key: $key")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save boolean value for key: $key", e)
        }
    }
    
    /**
     * Retrieve a boolean value securely
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return try {
            encryptedPrefs.getBoolean(key, defaultValue)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve boolean value for key: $key", e)
            defaultValue
        }
    }
    
    /**
     * Remove a specific key
     */
    fun remove(key: String) {
        try {
            encryptedPrefs.edit().remove(key).apply()
            Log.d(TAG, "Removed key: $key")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove key: $key", e)
        }
    }
    
    /**
     * Clear all stored data
     */
    fun clearAll() {
        try {
            encryptedPrefs.edit().clear().apply()
            Log.d(TAG, "Cleared all stored data")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear all data", e)
        }
    }
    
    /**
     * Check if a key exists
     */
    fun contains(key: String): Boolean {
        return try {
            encryptedPrefs.contains(key)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check key existence: $key", e)
            false
        }
    }
    
    /**
     * Get all keys
     */
    fun getAllKeys(): Set<String> {
        return try {
            encryptedPrefs.all.keys
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get all keys", e)
            emptySet()
        }
    }
}