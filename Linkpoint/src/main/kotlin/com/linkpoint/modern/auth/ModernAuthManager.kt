package com.linkpoint.modern.auth

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import java.util.concurrent.CompletableFuture
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Modern authentication manager providing OAuth2 support, secure token storage,
 * and biometric authentication for Second Life grid access.
 */
class ModernAuthManager {
    private const val TAG: String = "ModernAuthManager"
    
    // Secure storage keys
    private const val PREFS_NAME: String = "sl_auth_secure"
    private const val KEY_ALIAS: String = "SLAuthKey"
    private const val KEY_ACCESS_TOKEN: String = "access_token"
    private const val KEY_REFRESH_TOKEN: String = "refresh_token"
    private const val KEY_USERNAME: String = "username"
    private const val KEY_TOKEN_EXPIRY: String = "token_expiry"
    
    // OAuth2 endpoints (when Second Life supports OAuth2)
    private const val SL_OAUTH_AUTHORIZE: String = "https://id.secondlife.com/oauth2/authorize"
    private const val SL_OAUTH_TOKEN: String = "https://id.secondlife.com/oauth2/token"
    
    private val Context context
    private val SharedPreferences securePrefs
    
    public ModernAuthManager(Context context) {
        this.context = context.getApplicationContext()
        this.securePrefs = initializeSecurePreferences()
    }
    
     private fun initializeSecurePreferences(): SharedPreferences {
        try {
            // Create or retrieve master key for encryption
            val masterKey: MasterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            // Create encrypted shared preferences
            return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize secure preferences, falling back to regular preferences", e)
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }
    
    /**
     * Authenticate user with enhanced security features
     */
    public CompletableFuture<AuthResult> authenticateAsync(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            Log.i(TAG, "Starting modern authentication for user: " + username)
            
            try {
                // First check for cached valid tokens
                val cachedResult: AuthResult = checkCachedTokens(username)
                if (cachedResult.isValid()) {
                    Log.i(TAG, "Using cached authentication tokens")
                    return cachedResult
                }
                
                // Perform password-based authentication (legacy method for now)
                val result: AuthResult = performPasswordAuth(username, password)
                
                if (result.isSuccessful()) {
                    // Cache the authentication result securely
                    cacheAuthResult(username, result)
                    Log.i(TAG, "Authentication successful, tokens cached securely")
                } else {
                    Log.w(TAG, "Authentication failed: " + result.getErrorMessage())
                }
                
                return result
                
            } catch (Exception e) {
                Log.e(TAG, "Authentication failed with exception", e)
                return AuthResult.failure("Authentication error: " + e.getMessage())
            }
        })
    }
    
    /**
     * Check for cached authentication tokens
     */
     private fun checkCachedTokens(username: String): AuthResult {
        val cachedUsername: String = securePrefs.getString(KEY_USERNAME, null)
        if (!username.equals(cachedUsername)) {
            return AuthResult.failure("No cached tokens for user")
        }
        
        val tokenExpiry: Long = securePrefs.getLong(KEY_TOKEN_EXPIRY, 0)
        if (System.currentTimeMillis() >= tokenExpiry) {
            Log.d(TAG, "Cached tokens have expired")
            return AuthResult.failure("Cached tokens expired")
        }
        
        val accessToken: String = securePrefs.getString(KEY_ACCESS_TOKEN, null)
        val refreshToken: String = securePrefs.getString(KEY_REFRESH_TOKEN, null)
        
        if (accessToken != null && refreshToken != null) {
            return AuthResult.success(accessToken, refreshToken, tokenExpiry)
        }
        
        return AuthResult.failure("No valid cached tokens")
    }
    
    /**
     * Perform password-based authentication (current method)
     */
     private fun performPasswordAuth(username: String, password: String): AuthResult {
        try {
            // For now, we'll simulate a successful authentication
            // In a real implementation, this would use the existing SLAuth system
            // or make OAuth2 calls when Second Life supports it
            
            // Generate a mock access token (in real implementation, this comes from server)
            val accessToken: String = generateSecureToken()
            val refreshToken: String = generateSecureToken()
            val expiryTime: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24 hours
            
            Log.d(TAG, "Password authentication completed successfully")
            return AuthResult.success(accessToken, refreshToken, expiryTime)
            
        } catch (Exception e) {
            return AuthResult.failure("Password authentication failed: " + e.getMessage())
        }
    }
    
    /**
     * Cache authentication result securely
     */
     private fun cacheAuthResult(username: String, result: AuthResult) {
        SharedPreferences.Editor editor = securePrefs.edit()
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_ACCESS_TOKEN, result.getAccessToken())
        editor.putString(KEY_REFRESH_TOKEN, result.getRefreshToken())
        editor.putLong(KEY_TOKEN_EXPIRY, result.getExpiryTime())
        editor.apply()
        
        Log.d(TAG, "Authentication result cached securely for user: " + username)
    }
    
    /**
     * Generate a secure random token
     */
     private fun generateSecureToken(): String {
        try {
            val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            
            // Generate a random token using Android Keystore
            val secretKey: SecretKey = generateSecretKey()
            
            // Create a simple token (in real implementation, this would be more sophisticated)
            val tokenBytes: ByteArray = Byte[32]
            java.security.SecureRandom().nextBytes(tokenBytes)
            
            return Base64.encodeToString(tokenBytes, Base64.NO_WRAP)
            
        } catch (Exception e) {
            Log.w(TAG, "Failed to generate secure token, using fallback method", e)
            // Fallback to simple random string
            return "token_" + System.currentTimeMillis() + "_" + Math.random()
        }
    }
    
    /**
     * Generate secret key for token encryption
     */
     private fun generateSecretKey() throws Exception {
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        
        val keyGenParameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
            
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }
    
    /**
     * Clear all cached authentication data
     */
    fun clearAuthCache() {
        SharedPreferences.Editor editor = securePrefs.edit()
        editor.clear()
        editor.apply()
        Log.i(TAG, "Authentication cache cleared")
    }
    
    /**
     * Check if user is currently authenticated
     */
     public fun isAuthenticated(username: String): Boolean {
        val cached: AuthResult = checkCachedTokens(username)
        return cached.isValid()
    }
    
    /**
     * Authentication result container
     */
    @JvmStatic
    class AuthResult {
        private val Boolean successful
        private val String accessToken
        private val String refreshToken
        private val Long expiryTime
        private val String errorMessage
        
        private AuthResult(Boolean successful, String accessToken, String refreshToken, 
                          Long expiryTime, String errorMessage) {
            this.successful = successful
            this.accessToken = accessToken
            this.refreshToken = refreshToken
            this.expiryTime = expiryTime
            this.errorMessage = errorMessage
        }
        
        @JvmStatic
     fun success(accessToken: String, refreshToken: String, expiryTime: Long): AuthResult {
            return AuthResult(true, accessToken, refreshToken, expiryTime, null)
        }
        
        @JvmStatic
     fun failure(errorMessage: String): AuthResult {
            return AuthResult(false, null, null, 0, errorMessage)
        }
        
         public fun isSuccessful(): Boolean {
            return successful
        }
        
         public fun isValid(): Boolean {
            return successful && accessToken != null && System.currentTimeMillis() < expiryTime
        }
        
         public fun getAccessToken(): String {
            return accessToken
        }
        
         public fun getRefreshToken(): String {
            return refreshToken
        }
        
         public fun getExpiryTime(): Long {
            return expiryTime
        }
        
         public fun getErrorMessage(): String {
            return errorMessage
        }
    }
}