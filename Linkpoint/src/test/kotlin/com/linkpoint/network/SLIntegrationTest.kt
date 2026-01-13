package com.linkpoint.network

import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.MessageDigest

/**
 * Integration tests for Second Life login functionality.
 * 
 * IMPORTANT: These tests connect to REAL Second Life servers and require valid credentials.
 * They are marked @Ignore by default and should only be run manually by an agent.
 * 
 * To run these tests:
 * 1. Set environment variables SL_TEST_FIRST_NAME, SL_TEST_LAST_NAME, and SL_TEST_PASSWORD
 * 2. Remove the @Ignore annotation or run with -DrunIntegrationTests=true
 * 3. Run the specific test class
 * 
 * Example:
 * ```
 * SL_TEST_FIRST_NAME="TestUser" SL_TEST_LAST_NAME="Resident" SL_TEST_PASSWORD="password123" \
 *   ./gradlew :Linkpoint:test --tests "com.linkpoint.network.SLIntegrationTest"
 * ```
 * 
 * The tests will output detailed results including:
 * - Login success/failure status
 * - Session and agent IDs (truncated for security)
 * - Sim connection details
 * - Any error messages or MFA challenges
 * 
 * @author Linkpoint Development Team
 */
@Ignore("Integration tests require real credentials - run manually via agent")
class SLIntegrationTest {
    
    companion object {
        // Second Life login endpoints
        const val SL_MAIN_GRID_LOGIN = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        const val SL_BETA_GRID_LOGIN = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
        
        // Test configuration
        const val DEFAULT_START_LOCATION = "last"
        
        // Timeouts
        const val LOGIN_TIMEOUT_SECONDS = 60L
    }
    
    private var testFirstName: String = ""
    private var testLastName: String = ""
    private var testPassword: String = ""
    private var useMainGrid: Boolean = true
    
    @Before
    fun setup() {
        // Read credentials from environment variables
        testFirstName = System.getenv("SL_TEST_FIRST_NAME") ?: ""
        testLastName = System.getenv("SL_TEST_LAST_NAME") ?: ""
        testPassword = System.getenv("SL_TEST_PASSWORD") ?: ""
        useMainGrid = System.getenv("SL_USE_BETA_GRID")?.lowercase() != "true"
        
        // Skip if credentials not provided
        if (testFirstName.isBlank() || testLastName.isBlank() || testPassword.isBlank()) {
            println("""
                |
                |=======================================================================
                | INTEGRATION TEST SKIPPED - No credentials provided
                |=======================================================================
                | 
                | To run integration tests, set the following environment variables:
                |   SL_TEST_FIRST_NAME  - Avatar first name
                |   SL_TEST_LAST_NAME   - Avatar last name (e.g., "Resident")
                |   SL_TEST_PASSWORD    - Account password
                |   SL_USE_BETA_GRID    - Optional: set to "true" for Aditi grid
                |
                | Example:
                |   SL_TEST_FIRST_NAME="Test" SL_TEST_LAST_NAME="User" SL_TEST_PASSWORD="pass" \
                |     ./gradlew :Linkpoint:test --tests "*.SLIntegrationTest"
                |
                |=======================================================================
            """.trimMargin())
            org.junit.Assume.assumeTrue("Credentials not provided", false)
        }
        
        val gridName = if (useMainGrid) "Main Grid (Agni)" else "Beta Grid (Aditi)"
        println("""
            |
            |=======================================================================
            | INTEGRATION TEST CONFIGURATION
            |=======================================================================
            | Avatar: $testFirstName $testLastName
            | Grid: $gridName
            | Password: ${"*".repeat(testPassword.length)}
            |=======================================================================
        """.trimMargin())
    }
    
    /**
     * Test basic login to Second Life servers.
     * 
     * This test verifies:
     * - Connection to SL login server
     * - XML-RPC login request/response
     * - Successful session creation (for valid credentials)
     * - Proper error handling (for invalid credentials)
     */
    @Test
    fun `test login to Second Life`() = runBlocking {
        println("\n--- Starting Login Test ---\n")
        
        val loginUri = if (useMainGrid) SL_MAIN_GRID_LOGIN else SL_BETA_GRID_LOGIN
        println("Login URI: $loginUri")
        
        val startTime = System.currentTimeMillis()
        
        try {
            val result = SimpleSLLogin.login(
                firstName = testFirstName,
                lastName = testLastName,
                password = testPassword,
                loginUri = loginUri,
                startLocation = DEFAULT_START_LOCATION
            )
            
            val duration = System.currentTimeMillis() - startTime
            
            println("\n--- Login Result ---")
            println("Duration: ${duration}ms")
            
            when (result) {
                is SimpleSLLogin.SimpleLoginResult.Success -> {
                    println("Status: SUCCESS ✓")
                    println("Session ID: ${result.sessionId.take(8)}...${result.sessionId.takeLast(4)}")
                    println("Agent ID: ${result.agentId.take(8)}...${result.agentId.takeLast(4)}")
                    println("Sim IP: ${result.simIp}")
                    println("Sim Port: ${result.simPort}")
                    if (result.mfaHash != null) {
                        println("MFA Hash: (present, ${result.mfaHash!!.length} chars)")
                    }
                    
                    // Verify we got valid data
                    assertFalse("Session ID should not be blank", result.sessionId.isBlank())
                    assertFalse("Agent ID should not be blank", result.agentId.isBlank())
                    assertFalse("Sim IP should not be default", result.simIp == "127.0.0.1")
                    assertTrue("Sim port should be valid", result.simPort > 0)
                }
                
                is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                    println("Status: MFA REQUIRED")
                    println("Message: ${result.message}")
                    if (result.agentId != null) {
                        println("Agent ID: ${result.agentId!!.take(8)}...${result.agentId!!.takeLast(4)}")
                    }
                    
                    // MFA being required is a valid response for accounts with MFA enabled
                    println("\n[INFO] Account has MFA enabled - this is expected behavior")
                    println("[INFO] To complete login, provide TOTP code via mfaToken parameter")
                }
                
                is SimpleSLLogin.SimpleLoginResult.Failure -> {
                    println("Status: FAILURE ✗")
                    println("Error Code: ${result.errorCode}")
                    println("Message: ${result.message}")
                    println("Category: ${result.category}")
                    println("Is Transient: ${result.isTransient}")
                    println("Attempts Made: ${result.attemptsMade}")
                    println("Elapsed Time: ${result.elapsedTimeMs}ms")
                    
                    if (result.rootCauseType != null) {
                        println("Root Cause: ${result.rootCauseType}")
                        println("Root Message: ${result.rootCauseMessage}")
                    }
                    
                    if (!result.recommendations.isNullOrEmpty()) {
                        println("\nRecommendations:")
                        result.recommendations.forEachIndexed { i, rec ->
                            println("  ${i+1}. $rec")
                        }
                    }
                    
                    if (!result.details.isNullOrBlank()) {
                        println("\nTechnical Details:")
                        println(result.details!!.lines().take(20).joinToString("\n") { "  $it" })
                        if (result.details!!.lines().size > 20) {
                            println("  ... (${result.details!!.lines().size - 20} more lines)")
                        }
                    }
                    
                    // Check if this is an expected failure (e.g., invalid credentials)
                    if (result.errorCode == "INVALID_CREDENTIALS") {
                        println("\n[INFO] Invalid credentials - check username/password")
                    }
                }
            }
            
            println("\n--- End Login Test ---\n")
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            println("\n--- Login Test Exception ---")
            println("Duration: ${duration}ms")
            println("Exception: ${e.javaClass.simpleName}")
            println("Message: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    /**
     * Test login with MFA token.
     * 
     * This test is for accounts with MFA enabled. It requires:
     * - SL_TEST_MFA_TOKEN environment variable with current TOTP code
     */
    @Test
    fun `test login with MFA token`() = runBlocking {
        val mfaToken = System.getenv("SL_TEST_MFA_TOKEN") ?: ""
        
        if (mfaToken.isBlank()) {
            println("\n[SKIP] MFA token test - SL_TEST_MFA_TOKEN not provided")
            org.junit.Assume.assumeTrue("MFA token not provided", false)
            return@runBlocking
        }
        
        println("\n--- Starting MFA Login Test ---\n")
        
        val loginUri = if (useMainGrid) SL_MAIN_GRID_LOGIN else SL_BETA_GRID_LOGIN
        println("Login URI: $loginUri")
        println("MFA Token: [PROVIDED]")
        
        val result = SimpleSLLogin.login(
            firstName = testFirstName,
            lastName = testLastName,
            password = testPassword,
            loginUri = loginUri,
            startLocation = DEFAULT_START_LOCATION,
            mfaToken = mfaToken
        )
        
        println("\n--- MFA Login Result ---")
        when (result) {
            is SimpleSLLogin.SimpleLoginResult.Success -> {
                println("Status: SUCCESS ✓")
                println("MFA authentication successful!")
                println("Session ID: ${result.sessionId.take(8)}...")
            }
            is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                println("Status: MFA STILL REQUIRED")
                println("Message: ${result.message}")
                println("[WARNING] MFA token may be invalid or expired")
            }
            is SimpleSLLogin.SimpleLoginResult.Failure -> {
                println("Status: FAILURE")
                println("Error: ${result.message}")
            }
        }
        
        println("\n--- End MFA Login Test ---\n")
    }
    
    /**
     * Test login to Beta Grid (Aditi).
     * 
     * Useful for testing without affecting main grid account status.
     */
    @Test
    fun `test login to beta grid`() = runBlocking {
        println("\n--- Starting Beta Grid Login Test ---\n")
        
        val loginUri = SL_BETA_GRID_LOGIN
        println("Login URI: $loginUri (Beta Grid)")
        
        val result = SimpleSLLogin.login(
            firstName = testFirstName,
            lastName = testLastName,
            password = testPassword,
            loginUri = loginUri,
            startLocation = DEFAULT_START_LOCATION
        )
        
        println("\n--- Beta Grid Login Result ---")
        when (result) {
            is SimpleSLLogin.SimpleLoginResult.Success -> {
                println("Status: SUCCESS ✓")
                println("Beta Grid session established!")
            }
            is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                println("Status: MFA REQUIRED")
                println("Beta Grid requires MFA for this account")
            }
            is SimpleSLLogin.SimpleLoginResult.Failure -> {
                println("Status: FAILURE")
                println("Error: ${result.message}")
                // Beta grid may have different account status
                if (result.message.contains("not recognized", ignoreCase = true)) {
                    println("[INFO] Account may not exist on Beta Grid - this is normal for some accounts")
                }
            }
        }
        
        println("\n--- End Beta Grid Login Test ---\n")
    }
    
    /**
     * Test error handling for invalid credentials.
     * 
     * This test intentionally uses wrong credentials to verify error handling.
     */
    @Test
    fun `test invalid credentials handling`() = runBlocking {
        println("\n--- Starting Invalid Credentials Test ---\n")
        
        val loginUri = if (useMainGrid) SL_MAIN_GRID_LOGIN else SL_BETA_GRID_LOGIN
        
        val result = SimpleSLLogin.login(
            firstName = "NonExistent",
            lastName = "TestUser12345",
            password = "WrongPassword123",
            loginUri = loginUri,
            startLocation = DEFAULT_START_LOCATION
        )
        
        println("\n--- Invalid Credentials Result ---")
        when (result) {
            is SimpleSLLogin.SimpleLoginResult.Success -> {
                println("Status: UNEXPECTED SUCCESS")
                fail("Login should have failed with invalid credentials")
            }
            is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                println("Status: UNEXPECTED MFA REQUIRED")
                fail("Invalid credentials should not trigger MFA")
            }
            is SimpleSLLogin.SimpleLoginResult.Failure -> {
                println("Status: EXPECTED FAILURE ✓")
                println("Error Code: ${result.errorCode}")
                println("Message: ${result.message}")
                
                // Verify we get proper error response, not MFA prompt
                assertNotEquals("Should not get MFA_REQUIRED error", "MFA_REQUIRED", result.errorCode)
                assertTrue("Error should indicate invalid credentials",
                    result.errorCode == "INVALID_CREDENTIALS" || 
                    result.errorCode == "LOGIN_REJECTED" ||
                    result.message.contains("password", ignoreCase = true) ||
                    result.message.contains("credentials", ignoreCase = true) ||
                    result.message.contains("key", ignoreCase = true))
            }
        }
        
        println("\n--- End Invalid Credentials Test ---\n")
    }
    
    /**
     * Test network error handling.
     * 
     * Attempts to connect to an invalid login URI to test error handling.
     */
    @Test
    fun `test invalid server handling`() = runBlocking {
        println("\n--- Starting Invalid Server Test ---\n")
        
        val invalidUri = "https://invalid.login.server.example.com/login.cgi"
        println("Invalid URI: $invalidUri")
        
        val result = SimpleSLLogin.login(
            firstName = testFirstName,
            lastName = testLastName,
            password = testPassword,
            loginUri = invalidUri,
            startLocation = DEFAULT_START_LOCATION
        )
        
        println("\n--- Invalid Server Result ---")
        when (result) {
            is SimpleSLLogin.SimpleLoginResult.Failure -> {
                println("Status: EXPECTED FAILURE ✓")
                println("Error Code: ${result.errorCode}")
                println("Category: ${result.category}")
                println("Is Transient: ${result.isTransient}")
                
                // Should be a DNS or connection error, not MFA
                assertTrue("Should be a network error",
                    result.category == NetworkExceptionUtils.ErrorCategory.DNS_ERROR ||
                    result.category == NetworkExceptionUtils.ErrorCategory.CONNECTION_REFUSED ||
                    result.category == NetworkExceptionUtils.ErrorCategory.TIMEOUT)
            }
            else -> {
                fail("Should have failed with network error")
            }
        }
        
        println("\n--- End Invalid Server Test ---\n")
    }
    
    /**
     * Comprehensive test output for analysis.
     * 
     * Runs a full login attempt and outputs all available data for debugging.
     */
    @Test
    fun `test full login with detailed output`() = runBlocking {
        println("\n" + "=".repeat(80))
        println(" COMPREHENSIVE LOGIN TEST - DETAILED OUTPUT")
        println("=".repeat(80))
        
        val loginUri = if (useMainGrid) SL_MAIN_GRID_LOGIN else SL_BETA_GRID_LOGIN
        val gridName = if (useMainGrid) "Main Grid (Agni)" else "Beta Grid (Aditi)"
        
        println("""
            |
            | Test Parameters:
            |   Avatar: $testFirstName $testLastName
            |   Grid: $gridName
            |   Login URI: $loginUri
            |   Start Location: $DEFAULT_START_LOCATION
            |
        """.trimMargin())
        
        val startTime = System.currentTimeMillis()
        
        val result = SimpleSLLogin.login(
            firstName = testFirstName,
            lastName = testLastName,
            password = testPassword,
            loginUri = loginUri,
            startLocation = DEFAULT_START_LOCATION
        )
        
        val duration = System.currentTimeMillis() - startTime
        
        println(" Response received in ${duration}ms")
        println("-".repeat(80))
        
        when (result) {
            is SimpleSLLogin.SimpleLoginResult.Success -> {
                println("""
                    |
                    | ✓ LOGIN SUCCESSFUL
                    |
                    | Session Details:
                    |   Session ID: ${result.sessionId.take(8)}...${result.sessionId.takeLast(4)}
                    |   Agent ID: ${result.agentId.take(8)}...${result.agentId.takeLast(4)}
                    |   Sim IP: ${result.simIp}
                    |   Sim Port: ${result.simPort}
                    |   MFA Hash Present: ${result.mfaHash != null}
                    |
                    | Connection Info:
                    |   Ready to connect to simulator at ${result.simIp}:${result.simPort}
                    |
                """.trimMargin())
            }
            
            is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                println("""
                    |
                    | ⚠ MFA REQUIRED
                    |
                    | Server Response:
                    |   Message: ${result.message}
                    |   Agent ID: ${result.agentId ?: "Not provided"}
                    |
                    | Action Required:
                    |   Provide TOTP code via SL_TEST_MFA_TOKEN environment variable
                    |   and run the 'test login with MFA token' test
                    |
                """.trimMargin())
            }
            
            is SimpleSLLogin.SimpleLoginResult.Failure -> {
                println("""
                    |
                    | ✗ LOGIN FAILED
                    |
                    | Error Details:
                    |   Code: ${result.errorCode}
                    |   Message: ${result.message}
                    |   Category: ${result.category}
                    |   Is Transient: ${result.isTransient}
                    |
                    | Timing:
                    |   Attempts Made: ${result.attemptsMade}
                    |   Elapsed Time: ${result.elapsedTimeMs}ms
                    |
                """.trimMargin())
                
                if (result.rootCauseType != null) {
                    println("""
                        | Root Cause:
                        |   Type: ${result.rootCauseType}
                        |   Message: ${result.rootCauseMessage}
                        |
                    """.trimMargin())
                }
                
                if (!result.recommendations.isNullOrEmpty()) {
                    println(" Recommendations:")
                    result.recommendations.forEach { rec ->
                        println("   - $rec")
                    }
                    println()
                }
                
                if (!result.details.isNullOrBlank()) {
                    println(" Technical Details:")
                    println("-".repeat(40))
                    println(result.details)
                    println("-".repeat(40))
                }
            }
        }
        
        println("=".repeat(80))
        println(" END COMPREHENSIVE LOGIN TEST")
        println("=".repeat(80) + "\n")
    }
}
