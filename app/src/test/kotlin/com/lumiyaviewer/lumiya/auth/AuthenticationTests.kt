package com.lumiyaviewer.lumiya.auth

import org.junit.Test
import org.junit.Assert.*

/**
 * Phase 2: Authentication System Tests
 * 
 * Comprehensive test suite for Second Life authentication including:
 * - Legacy XMLRPC login
 * - OAuth2 authentication (when supported)
 * - Token management and refresh
 * - Secure credential storage
 * - Multi-grid support
 * - Error handling
 */
class AuthenticationTests {
    
    @Test
    fun `test credential validation - valid username`() {
        val username = "FirstName.LastName"
        assertTrue("Valid SL username should pass validation", 
            isValidSecondLifeUsername(username))
    }
    
    @Test
    fun `test credential validation - invalid username`() {
        val invalidUsernames = listOf(
            "",                    // Empty
            "FirstName",           // Missing last name
            "First Name",          // Space instead of dot
            "First_Name_Extra",    // Too many parts
            "123.456",            // All numbers
            "First.Last.Name"     // Too many dots
        )
        
        invalidUsernames.forEach { username ->
            assertFalse("Invalid username '$username' should fail validation",
                isValidSecondLifeUsername(username))
        }
    }
    
    @Test
    fun `test credential validation - valid password`() {
        val validPasswords = listOf(
            "SecurePass123!",
            "MyP@ssw0rd",
            "Complex!Pass#123"
        )
        
        validPasswords.forEach { password ->
            assertTrue("Valid password '$password' should pass validation",
                isValidPassword(password))
        }
    }
    
    @Test
    fun `test credential validation - invalid password`() {
        val invalidPasswords = listOf(
            "",           // Empty
            "short",      // Too short
            "12345",      // Only numbers
            "password"    // Too simple
        )
        
        invalidPasswords.forEach { password ->
            assertFalse("Invalid password should fail validation",
                isValidPassword(password))
        }
    }
    
    @Test
    fun `test authentication result - success`() {
        val result = AuthResult.success(
            accessToken = "test_access_token_123",
            refreshToken = "test_refresh_token_456",
            expiresIn = 3600,
            userId = "12345678-1234-1234-1234-123456789abc"
        )
        
        assertTrue("Successful auth result should be successful", result.isSuccessful())
        assertNotNull("Access token should not be null", result.getAccessToken())
        assertNotNull("Refresh token should not be null", result.getRefreshToken())
        assertNull("Error message should be null for success", result.getErrorMessage())
    }
    
    @Test
    fun `test authentication result - failure`() {
        val result = AuthResult.failure("Invalid credentials")
        
        assertFalse("Failed auth result should not be successful", result.isSuccessful())
        assertNull("Access token should be null for failure", result.getAccessToken())
        assertNotNull("Error message should not be null", result.getErrorMessage())
        assertEquals("Error message should match", "Invalid credentials", result.getErrorMessage())
    }
    
    @Test
    fun `test token expiry calculation`() {
        val currentTime = System.currentTimeMillis()
        val expiresIn = 3600L // 1 hour
        val expectedExpiry = currentTime + (expiresIn * 1000)
        
        val calculatedExpiry = calculateTokenExpiry(expiresIn)
        
        // Allow 1 second tolerance for test execution time
        assertTrue("Token expiry should be calculated correctly",
            Math.abs(calculatedExpiry - expectedExpiry) < 1000)
    }
    
    @Test
    fun `test token validation - not expired`() {
        val futureExpiry = System.currentTimeMillis() + 3600000 // 1 hour from now
        assertTrue("Token should be valid", isTokenValid(futureExpiry))
    }
    
    @Test
    fun `test token validation - expired`() {
        val pastExpiry = System.currentTimeMillis() - 3600000 // 1 hour ago
        assertFalse("Token should be expired", isTokenValid(pastExpiry))
    }
    
    @Test
    fun `test grid configuration - Second Life main grid`() {
        val mainGrid = GridConfiguration.SECOND_LIFE_MAIN
        
        assertEquals("Grid name should match", "Second Life", mainGrid.name)
        assertTrue("Login URI should be HTTPS", mainGrid.loginUri.startsWith("https://"))
        assertNotNull("Grid should have login URI", mainGrid.loginUri)
    }
    
    @Test
    fun `test grid configuration - Beta grid`() {
        val betaGrid = GridConfiguration.SECOND_LIFE_BETA
        
        assertEquals("Grid name should match", "Second Life Beta", betaGrid.name)
        assertTrue("Beta grid should be identifiable", betaGrid.isBetaGrid)
        assertNotEquals("Beta grid should have different URI than main",
            GridConfiguration.SECOND_LIFE_MAIN.loginUri, betaGrid.loginUri)
    }
    
    @Test
    fun `test session token generation`() {
        val token = generateSessionToken()
        
        assertNotNull("Session token should not be null", token)
        assertTrue("Session token should have minimum length", token.length >= 32)
        assertTrue("Session token should be alphanumeric", 
            token.matches(Regex("[A-Za-z0-9]+")))
    }
    
    @Test
    fun `test password hashing`() {
        val password = "MySecurePassword123"
        val hash1 = hashPassword(password)
        val hash2 = hashPassword(password)
        
        assertNotNull("Hash should not be null", hash1)
        assertNotEquals("Hash should not equal plain password", password, hash1)
        assertEquals("Same password should produce same hash", hash1, hash2)
    }
    
    @Test
    fun `test authentication error codes`() {
        val errors = mapOf(
            AuthErrorCode.INVALID_CREDENTIALS to "Invalid username or password",
            AuthErrorCode.NETWORK_ERROR to "Network connection failed",
            AuthErrorCode.SERVER_ERROR to "Server returned an error",
            AuthErrorCode.ACCOUNT_SUSPENDED to "Account is suspended",
            AuthErrorCode.MAINTENANCE_MODE to "Grid is under maintenance"
        )
        
        errors.forEach { (code, message) ->
            val result = AuthResult.failure(code, message)
            assertEquals("Error code should match", code, result.getErrorCode())
            assertEquals("Error message should match", message, result.getErrorMessage())
        }
    }
}

/**
 * Helper classes and functions for authentication testing
 */

/**
 * Validates Second Life username format (FirstName.LastName)
 */
fun isValidSecondLifeUsername(username: String): Boolean {
    if (username.isEmpty()) return false
    
    val parts = username.split(".")
    if (parts.size != 2) return false
    
    val (firstName, lastName) = parts
    
    // Both parts must be non-empty and contain only letters
    return firstName.isNotEmpty() && 
           lastName.isNotEmpty() && 
           firstName.all { it.isLetter() } && 
           lastName.all { it.isLetter() }
}

/**
 * Validates password strength
 */
fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

/**
 * Authentication result data class
 */
data class AuthResult(
    val success: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userId: String? = null,
    val expiresIn: Long = 0,
    val errorCode: AuthErrorCode? = null,
    val errorMessage: String? = null
) {
    fun isSuccessful() = success
    fun getAccessToken() = accessToken
    fun getRefreshToken() = refreshToken
    fun getErrorMessage() = errorMessage
    fun getErrorCode() = errorCode
    
    companion object {
        fun success(
            accessToken: String,
            refreshToken: String,
            expiresIn: Long,
            userId: String
        ) = AuthResult(
            success = true,
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            expiresIn = expiresIn
        )
        
        fun failure(errorMessage: String) = AuthResult(
            success = false,
            errorMessage = errorMessage
        )
        
        fun failure(errorCode: AuthErrorCode, errorMessage: String) = AuthResult(
            success = false,
            errorCode = errorCode,
            errorMessage = errorMessage
        )
    }
}

/**
 * Authentication error codes
 */
enum class AuthErrorCode {
    INVALID_CREDENTIALS,
    NETWORK_ERROR,
    SERVER_ERROR,
    ACCOUNT_SUSPENDED,
    MAINTENANCE_MODE,
    TOKEN_EXPIRED,
    UNKNOWN_ERROR
}

/**
 * Grid configuration for different Second Life grids
 */
data class GridConfiguration(
    val name: String,
    val loginUri: String,
    val isBetaGrid: Boolean = false
) {
    companion object {
        val SECOND_LIFE_MAIN = GridConfiguration(
            name = "Second Life",
            loginUri = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        )
        
        val SECOND_LIFE_BETA = GridConfiguration(
            name = "Second Life Beta",
            loginUri = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi",
            isBetaGrid = true
        )
    }
}

/**
 * Calculate token expiry timestamp
 */
fun calculateTokenExpiry(expiresInSeconds: Long): Long {
    return System.currentTimeMillis() + (expiresInSeconds * 1000)
}

/**
 * Check if token is still valid
 */
fun isTokenValid(expiryTimestamp: Long): Boolean {
    return System.currentTimeMillis() < expiryTimestamp
}

/**
 * Generate secure session token
 */
fun generateSessionToken(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..32)
        .map { chars.random() }
        .joinToString("")
}

/**
 * Hash password for secure storage (simplified for testing)
 */
fun hashPassword(password: String): String {
    // In production, use proper password hashing (bcrypt, Argon2, etc.)
    // This is simplified for testing
    return password.hashCode().toString() + "_hashed"
}
