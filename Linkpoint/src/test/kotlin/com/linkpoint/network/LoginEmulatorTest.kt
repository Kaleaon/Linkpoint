package com.linkpoint.network

import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.util.concurrent.TimeUnit

/**
 * Login Emulator Test - Fully emulates and tests app login with credentials.
 * 
 * This test harness provides comprehensive login testing with debugging capabilities:
 * - Full login flow emulation using SimpleSLLogin
 * - Detailed logging of all request/response data
 * - MFA handling support
 * - Error categorization and recommendations
 * - Performance metrics (timing, retries)
 * 
 * USAGE:
 * Run with environment variables:
 * ```
 * SL_TEST_FIRST_NAME="YourFirstName" \
 * SL_TEST_LAST_NAME="Resident" \
 * SL_TEST_PASSWORD="YourPassword" \
 * ./gradlew :Linkpoint:test --tests "com.linkpoint.network.LoginEmulatorTest"
 * ```
 * 
 * For MFA accounts, also set:
 * ```
 * SL_TEST_MFA_TOKEN="123456"
 * ```
 */
@Ignore("Login emulator tests require real credentials - run manually")
class LoginEmulatorTest {
    
    companion object {
        private const val TAG = "LoginEmulator"
        
        // Login endpoints
        const val MAIN_GRID_LOGIN = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        const val BETA_GRID_LOGIN = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
        
        // Test timeouts
        const val MAX_RETRIES = 4
        const val TEST_TIMEOUT_MS = 120_000L
    }
    
    private var firstName: String = ""
    private var lastName: String = ""
    private var password: String = ""
    private var mfaToken: String = ""
    private var useBetaGrid: Boolean = false
    
    @Before
    fun setup() {
        firstName = System.getenv("SL_TEST_FIRST_NAME") ?: ""
        lastName = System.getenv("SL_TEST_LAST_NAME") ?: ""
        password = System.getenv("SL_TEST_PASSWORD") ?: ""
        mfaToken = System.getenv("SL_TEST_MFA_TOKEN") ?: ""
        useBetaGrid = System.getenv("SL_USE_BETA_GRID")?.lowercase() == "true"
        
        if (firstName.isBlank() || lastName.isBlank() || password.isBlank()) {
            printHeader("MISSING CREDENTIALS")
            println("""
                |
                | To run this test, set the following environment variables:
                |   SL_TEST_FIRST_NAME  - Your Second Life first name
                |   SL_TEST_LAST_NAME   - Your Second Life last name (e.g., "Resident")
                |   SL_TEST_PASSWORD    - Your account password
                |   SL_TEST_MFA_TOKEN   - (Optional) TOTP code if MFA is enabled
                |   SL_USE_BETA_GRID    - (Optional) Set to "true" for Aditi/beta grid
                |
                | Example:
                |   SL_TEST_FIRST_NAME="John" SL_TEST_LAST_NAME="Resident" SL_TEST_PASSWORD="pass123" \
                |     ./gradlew :Linkpoint:test --tests "*.LoginEmulatorTest"
                |
            """.trimMargin())
            org.junit.Assume.assumeTrue("Credentials required", false)
        }
        
        printHeader("LOGIN EMULATOR CONFIGURATION")
        println("""
            | Avatar: $firstName $lastName
            | Grid: ${if (useBetaGrid) "Beta (Aditi)" else "Main (Agni)"}
            | MFA Token: ${if (mfaToken.isNotBlank()) "Provided" else "Not provided"}
            | Max Retries: $MAX_RETRIES
        """.trimMargin())
    }
    
    /**
     * Full login emulation test with detailed output.
     */
    @Test
    fun `emulate full login flow`() = runBlocking {
        printHeader("STARTING LOGIN EMULATION")
        
        val loginUri = if (useBetaGrid) BETA_GRID_LOGIN else MAIN_GRID_LOGIN
        val startLocation = "last"
        
        println("| Login URI: $loginUri")
        println("| Start Location: $startLocation")
        println("|")
        
        val startTime = System.currentTimeMillis()
        
        // Phase 1: Initial login attempt
        printPhase(1, "Initial Login Request")
        
        var result = SimpleSLLogin.login(
            firstName = firstName,
            lastName = lastName,
            password = password,
            loginUri = loginUri,
            startLocation = startLocation,
            maxRetries = MAX_RETRIES,
            mfaToken = mfaToken
        )
        
        val initialDuration = System.currentTimeMillis() - startTime
        println("| Duration: ${initialDuration}ms")
        
        // Handle result
        when (result) {
            is SimpleSLLogin.SimpleLoginResult.Success -> {
                printSuccess(result, initialDuration)
                return@runBlocking
            }
            
            is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                printMfaRequired(result)
                
                // If MFA token was provided, retry with it
                if (mfaToken.isNotBlank()) {
                    printPhase(2, "Retrying with MFA Token")
                    
                    val mfaStartTime = System.currentTimeMillis()
                    result = SimpleSLLogin.login(
                        firstName = firstName,
                        lastName = lastName,
                        password = password,
                        loginUri = loginUri,
                        startLocation = startLocation,
                        maxRetries = MAX_RETRIES,
                        mfaToken = mfaToken
                    )
                    val mfaDuration = System.currentTimeMillis() - mfaStartTime
                    
                    when (result) {
                        is SimpleSLLogin.SimpleLoginResult.Success -> {
                            printSuccess(result, mfaDuration)
                            return@runBlocking
                        }
                        is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                            printError("MFA still required - token may be invalid or expired")
                        }
                        is SimpleSLLogin.SimpleLoginResult.Failure -> {
                            printFailure(result)
                        }
                    }
                } else {
                    println("| ACTION: Set SL_TEST_MFA_TOKEN and re-run to complete login")
                }
            }
            
            is SimpleSLLogin.SimpleLoginResult.Failure -> {
                printFailure(result)
            }
        }
        
        printHeader("LOGIN EMULATION COMPLETE")
    }
    
    /**
     * Test login with invalid credentials to verify error handling.
     */
    @Test
    fun `test error handling with invalid credentials`() = runBlocking {
        printHeader("TESTING INVALID CREDENTIALS")
        
        val loginUri = if (useBetaGrid) BETA_GRID_LOGIN else MAIN_GRID_LOGIN
        
        val result = SimpleSLLogin.login(
            firstName = "InvalidTestUser_${System.currentTimeMillis()}",
            lastName = "DoesNotExist",
            password = "WrongPassword123!",
            loginUri = loginUri,
            startLocation = "last",
            maxRetries = 1  // Only one attempt for invalid credentials
        )
        
        when (result) {
            is SimpleSLLogin.SimpleLoginResult.Success -> {
                fail("Login should have failed with invalid credentials")
            }
            is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                fail("Invalid credentials should not trigger MFA")
            }
            is SimpleSLLogin.SimpleLoginResult.Failure -> {
                println("| Expected failure received:")
                println("| Error Code: ${result.errorCode}")
                println("| Message: ${result.message}")
                println("| Category: ${result.category}")
                
                // Verify it's a credentials error, not something else
                assertTrue(
                    "Should be a credentials-related error",
                    result.errorCode == "INVALID_CREDENTIALS" ||
                    result.errorCode == "LOGIN_REJECTED" ||
                    result.message.contains("key", ignoreCase = true) ||
                    result.message.contains("password", ignoreCase = true) ||
                    result.message.contains("credentials", ignoreCase = true)
                )
                
                printSuccess("Error handling works correctly")
            }
        }
    }
    
    /**
     * Test network error handling by connecting to invalid endpoint.
     */
    @Test
    fun `test network error handling`() = runBlocking {
        printHeader("TESTING NETWORK ERROR HANDLING")
        
        val invalidUri = "https://invalid.login.server.that.does.not.exist.example.com/login"
        
        val startTime = System.currentTimeMillis()
        val result = SimpleSLLogin.login(
            firstName = firstName,
            lastName = lastName,
            password = password,
            loginUri = invalidUri,
            startLocation = "last",
            maxRetries = 1
        )
        val duration = System.currentTimeMillis() - startTime
        
        when (result) {
            is SimpleSLLogin.SimpleLoginResult.Failure -> {
                println("| Expected network failure received:")
                println("| Duration: ${duration}ms")
                println("| Error Code: ${result.errorCode}")
                println("| Category: ${result.category}")
                println("| Is Transient: ${result.isTransient}")
                
                // Should be a DNS or connection error
                assertTrue(
                    "Should be a network-related error category",
                    result.category == NetworkExceptionUtils.ErrorCategory.DNS_ERROR ||
                    result.category == NetworkExceptionUtils.ErrorCategory.CONNECTION_REFUSED ||
                    result.category == NetworkExceptionUtils.ErrorCategory.TIMEOUT ||
                    result.category == NetworkExceptionUtils.ErrorCategory.SOCKET_ERROR ||
                    result.category == NetworkExceptionUtils.ErrorCategory.IO_ERROR
                )
                
                printSuccess("Network error handling works correctly")
            }
            else -> {
                fail("Should have failed with network error")
            }
        }
    }
    
    /**
     * Performance test - measure login timing under normal conditions.
     */
    @Test
    fun `measure login performance`() = runBlocking {
        printHeader("LOGIN PERFORMANCE TEST")
        
        val loginUri = if (useBetaGrid) BETA_GRID_LOGIN else MAIN_GRID_LOGIN
        val timings = mutableListOf<Long>()
        var successCount = 0
        var failureCount = 0
        
        println("| Running 3 login attempts to measure timing...")
        
        repeat(3) { attempt ->
            val startTime = System.currentTimeMillis()
            
            val result = SimpleSLLogin.login(
                firstName = firstName,
                lastName = lastName,
                password = password,
                loginUri = loginUri,
                startLocation = "last",
                maxRetries = 2,
                mfaToken = mfaToken
            )
            
            val duration = System.currentTimeMillis() - startTime
            timings.add(duration)
            
            when (result) {
                is SimpleSLLogin.SimpleLoginResult.Success -> {
                    successCount++
                    println("| Attempt ${attempt + 1}: SUCCESS in ${duration}ms")
                }
                is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                    println("| Attempt ${attempt + 1}: MFA Required (${duration}ms)")
                    return@runBlocking  // Stop if MFA required
                }
                is SimpleSLLogin.SimpleLoginResult.Failure -> {
                    failureCount++
                    println("| Attempt ${attempt + 1}: FAILED in ${duration}ms - ${result.message}")
                }
            }
            
            // Small delay between attempts
            kotlinx.coroutines.delay(1000)
        }
        
        printHeader("PERFORMANCE RESULTS")
        println("| Successful logins: $successCount")
        println("| Failed logins: $failureCount")
        println("| Min time: ${timings.minOrNull() ?: 0}ms")
        println("| Max time: ${timings.maxOrNull() ?: 0}ms")
        println("| Avg time: ${timings.average().toLong()}ms")
    }
    
    // ===== Helper methods =====
    
    private fun printHeader(title: String) {
        println()
        println("╔${"═".repeat(68)}╗")
        println("║ ${title.padEnd(66)} ║")
        println("╚${"═".repeat(68)}╝")
        println("|")
    }
    
    private fun printPhase(num: Int, description: String) {
        println("├── Phase $num: $description")
        println("|")
    }
    
    private fun printSuccess(result: SimpleSLLogin.SimpleLoginResult.Success, duration: Long) {
        println("|")
        println("| ✓ LOGIN SUCCESSFUL")
        println("|")
        println("| Session Details:")
        println("|   Session ID: ${result.sessionId.take(8)}...${result.sessionId.takeLast(4)}")
        println("|   Agent ID: ${result.agentId.take(8)}...${result.agentId.takeLast(4)}")
        println("|   Sim IP: ${result.simIp}")
        println("|   Sim Port: ${result.simPort}")
        println("|   MFA Hash Present: ${result.mfaHash != null}")
        println("|   Duration: ${duration}ms")
        println("|")
    }
    
    private fun printSuccess(message: String) {
        println("|")
        println("| ✓ $message")
        println("|")
    }
    
    private fun printMfaRequired(result: SimpleSLLogin.SimpleLoginResult.MFARequired) {
        println("|")
        println("| ⚠ MFA REQUIRED")
        println("|")
        println("| Server says: ${result.message}")
        val agentId = result.agentId
        if (agentId != null) {
            println("| Agent ID: ${agentId.take(8)}...")
        }
        println("|")
    }
    
    private fun printFailure(result: SimpleSLLogin.SimpleLoginResult.Failure) {
        println("|")
        println("| ✗ LOGIN FAILED")
        println("|")
        println("| Error Details:")
        println("|   Code: ${result.errorCode}")
        println("|   Category: ${result.category}")
        println("|   Message: ${result.message}")
        println("|   Is Transient: ${result.isTransient}")
        println("|   Attempts Made: ${result.attemptsMade}")
        println("|   Elapsed Time: ${result.elapsedTimeMs}ms")
        println("|")
        
        if (result.rootCauseType != null) {
            println("| Root Cause:")
            println("|   Type: ${result.rootCauseType}")
            println("|   Message: ${result.rootCauseMessage}")
            println("|")
        }
        
        if (result.recommendations.isNotEmpty()) {
            println("| Recommendations:")
            result.recommendations.forEach { rec ->
                println("|   • $rec")
            }
            println("|")
        }
        
        val details = result.details
        if (!details.isNullOrBlank()) {
            println("| Technical Details (first 500 chars):")
            println("|   ${details.take(500).replace("\n", "\n|   ")}")
            println("|")
        }
    }
    
    private fun printError(message: String) {
        println("|")
        println("| ✗ ERROR: $message")
        println("|")
    }
}
