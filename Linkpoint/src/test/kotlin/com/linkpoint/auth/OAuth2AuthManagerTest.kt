package com.linkpoint.auth

import android.net.Uri
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OAuth2AuthManagerTest {

    private lateinit var server: MockWebServer
    private lateinit var authManager: OAuth2AuthManager

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        authManager = OAuth2AuthManager()
    }

    @After
    fun tearDown() {
        server.shutdown()
        authManager.clearPkceState()
    }

    @Test
    fun testPkcePairGeneration() {
        val pkce = authManager.generatePkcePair()
        assertNotNull(pkce.codeVerifier)
        assertNotNull(pkce.codeChallenge)
        assertEquals("S256", pkce.codeChallengeMethod)

        // Verifier should be url-safe without padding
        assertFalse(pkce.codeVerifier.contains("="))
        assertFalse(pkce.codeVerifier.contains("+"))
        assertFalse(pkce.codeVerifier.contains("/"))
        assertTrue(pkce.codeVerifier.length >= 43)

        // Challenge should also be url-safe without padding
        assertFalse(pkce.codeChallenge.contains("="))
    }

    @Test
    fun testStartAuthSessionStoresStateAndVerifier() {
        val tokenUrl = server.url("/oauth2/token").toString()
        val (authUrl, state) = authManager.startAuthSession(
            tokenEndpoint = tokenUrl,
            redirectUri = "slviewer://auth-callback"
        )

        assertTrue(authManager.hasPendingSession())
        assertTrue(authUrl.contains("response_type=code"))
        assertTrue(authUrl.contains("redirect_uri=slviewer%3A%2F%2Fauth-callback"))
        assertTrue(authUrl.contains("code_challenge_method=S256"))
        assertTrue(authUrl.contains("state=$state"))
    }

    @Test
    fun testStateValidationPreventsReplayAttack() = kotlinx.coroutines.runBlocking {
        val tokenUrl = server.url("/oauth2/token").toString()
        authManager.startAuthSession(
            tokenEndpoint = tokenUrl,
            redirectUri = "slviewer://auth-callback"
        )

        // Attempt callback with mismatched state
        val maliciousUri = Uri.parse("slviewer://auth-callback?code=stolen_code_123&state=FORGED_STATE")
        val result = authManager.handleRedirectUri(maliciousUri)

        assertTrue(result is OAuth2Result.Error)
        val errorResult = result as OAuth2Result.Error
        assertTrue(errorResult.message.contains("State validation failed"))
        assertFalse("PKCE state must be cleared on mismatch", authManager.hasPendingSession())
    }

    @Test
    fun testSuccessfulRedirectExchange() = kotlinx.coroutines.runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                        "access_token": "valid_oauth2_access_token_999",
                        "token_type": "Bearer",
                        "mfa_hash": "MFA_SAVED_HASH_ABC123"
                    }
                    """.trimIndent()
                )
        )

        val tokenUrl = server.url("/oauth2/token").toString()
        val (_, state) = authManager.startAuthSession(
            tokenEndpoint = tokenUrl,
            redirectUri = "slviewer://auth-callback"
        )

        val callbackUri = Uri.parse("slviewer://auth-callback?code=auth_code_777&state=$state")
        val result = authManager.handleRedirectUri(callbackUri)

        assertTrue(result is OAuth2Result.Success)
        val success = result as OAuth2Result.Success
        assertEquals("valid_oauth2_access_token_999", success.accessToken)
        assertEquals("MFA_SAVED_HASH_ABC123", success.mfaHash)

        // PKCE verifier and state must be discarded after token exchange
        assertFalse("PKCE verifier must be cleared after exchange", authManager.hasPendingSession())
    }

    @Test
    fun testCancelledAuthorization() = kotlinx.coroutines.runBlocking {
        val tokenUrl = server.url("/oauth2/token").toString()
        authManager.startAuthSession(
            tokenEndpoint = tokenUrl,
            redirectUri = "slviewer://auth-callback"
        )

        val cancelUri = Uri.parse("slviewer://auth-callback?error=access_denied&error_description=User+cancelled")
        val result = authManager.handleRedirectUri(cancelUri)

        assertTrue(result is OAuth2Result.Cancelled)
        val cancelled = result as OAuth2Result.Cancelled
        assertEquals("User cancelled", cancelled.message)
        assertFalse(authManager.hasPendingSession())
    }
}
