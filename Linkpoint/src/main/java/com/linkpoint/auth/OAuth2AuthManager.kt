package com.linkpoint.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

/**
 * Result of an OAuth2 authentication flow.
 */
sealed interface OAuth2Result {
    data class Success(
        val accessToken: String,
        val idToken: String? = null,
        val refreshToken: String? = null,
        val mfaHash: String? = null,
        val gridSessionParams: Map<String, String> = emptyMap()
    ) : OAuth2Result

    data class Cancelled(
        val message: String = "Web authentication was cancelled or closed."
    ) : OAuth2Result

    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : OAuth2Result
}

/**
 * PKCE parameter pair containing the code verifier and code challenge.
 */
data class PkcePair(
    val codeVerifier: String,
    val codeChallenge: String,
    val codeChallengeMethod: String = "S256"
)

/**
 * Modern OAuth2 Authentication Portal Manager using PKCE and Android Custom Tabs.
 *
 * Implements RFC 7636 (Proof Key for Code Exchange) and RFC 8252 (OAuth 2.0 for Native Apps).
 * Replaces direct plain-text password handling with authorization code grant exchange.
 */
class OAuth2AuthManager(
    private val httpClient: OkHttpClient = OkHttpClient()
) {
    companion object {
        private const val TAG = "OAuth2AuthManager"
        const val DEFAULT_REDIRECT_URI = "slviewer://auth-callback"
        const val DEFAULT_CLIENT_ID = "linkpoint-mobile-viewer"
        const val DEFAULT_AUTH_ENDPOINT = "https://id.secondlife.com/oauth2/authorize"
        const val DEFAULT_TOKEN_ENDPOINT = "https://id.secondlife.com/oauth2/token"

        @Volatile
        private var instance: OAuth2AuthManager? = null

        fun getInstance(): OAuth2AuthManager {
            return instance ?: synchronized(this) {
                instance ?: OAuth2AuthManager().also { instance = it }
            }
        }
    }

    // Temporary in-memory storage for PKCE verifier and state parameter.
    // Discarded immediately after token exchange or flow cancellation.
    @Volatile
    private var pendingCodeVerifier: String? = null

    @Volatile
    private var pendingState: String? = null

    @Volatile
    private var pendingRedirectUri: String = DEFAULT_REDIRECT_URI

    @Volatile
    private var pendingTokenEndpoint: String = DEFAULT_TOKEN_ENDPOINT

    /**
     * Generates a cryptographically secure random PKCE code_verifier and code_challenge (S256).
     */
    fun generatePkcePair(): PkcePair {
        val random = SecureRandom()
        val verifierBytes = ByteArray(64)
        random.nextBytes(verifierBytes)
        
        // Base64Url unpadded encoding (RFC 7636 Section 3)
        val codeVerifier = Base64.encodeToString(
            verifierBytes,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )

        val digest = MessageDigest.getInstance("SHA-256")
        val challengeBytes = digest.digest(codeVerifier.toByteArray(StandardCharsets.US_ASCII))
        val codeChallenge = Base64.encodeToString(
            challengeBytes,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )

        return PkcePair(
            codeVerifier = codeVerifier,
            codeChallenge = codeChallenge,
            codeChallengeMethod = "S256"
        )
    }

    /**
     * Generates a cryptographically secure random state parameter to prevent CSRF / token replay.
     */
    fun generateState(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    /**
     * Prepares and starts an OAuth2 PKCE authorization session.
     * Stores PKCE verifier and state in temporary memory.
     */
    fun startAuthSession(
        authEndpoint: String = DEFAULT_AUTH_ENDPOINT,
        tokenEndpoint: String = DEFAULT_TOKEN_ENDPOINT,
        clientId: String = DEFAULT_CLIENT_ID,
        redirectUri: String = DEFAULT_REDIRECT_URI,
        scope: String = "openid profile grid_login"
    ): Pair<String, String> {
        val pkce = generatePkcePair()
        val state = generateState()

        this.pendingCodeVerifier = pkce.codeVerifier
        this.pendingState = state
        this.pendingRedirectUri = redirectUri
        this.pendingTokenEndpoint = tokenEndpoint

        val builder = Uri.parse(authEndpoint).buildUpon()
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("scope", scope)
            .appendQueryParameter("state", state)
            .appendQueryParameter("code_challenge", pkce.codeChallenge)
            .appendQueryParameter("code_challenge_method", pkce.codeChallengeMethod)

        val authUrl = builder.build().toString()
        Log.i(TAG, "OAuth2 PKCE session started. Auth URL built with state=$state")
        return Pair(authUrl, state)
    }

    /**
     * Launches the authentication URL using Android Custom Tabs, falling back to system browser
     * if Custom Tabs are unavailable.
     *
     * @return true if an intent was successfully launched.
     */
    fun launchAuthPortal(
        context: Context,
        authUrl: String
    ): Boolean {
        val uri = Uri.parse(authUrl)

        // 1. Try launching Android Custom Tabs
        try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                .build()

            // Check if Custom Tabs package is available
            val packageName = androidx.browser.customtabs.CustomTabsClient.getPackageName(context, null)
            if (packageName != null) {
                customTabsIntent.intent.setPackage(packageName)
                customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                customTabsIntent.launchUrl(context, uri)
                Log.i(TAG, "Launched OAuth2 web authentication via Custom Tabs ($packageName)")
                return true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to launch Custom Tabs, falling back to system browser", e)
        }

        // 2. Fallback to default system browser intent
        return try {
            val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
            Log.i(TAG, "Launched OAuth2 web authentication via default system browser")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Unable to launch system browser for OAuth2 authentication", e)
            false
        }
    }

    /**
     * Handles an incoming redirect URI (e.g. slviewer://auth-callback?code=...&state=...).
     * Validates state parameter to prevent token replay attacks.
     */
    suspend fun handleRedirectUri(
        uri: Uri,
        clientId: String = DEFAULT_CLIENT_ID
    ): OAuth2Result = withContext(Dispatchers.IO) {
        val receivedState = uri.getQueryParameter("state")
        val code = uri.getQueryParameter("code")
            ?: uri.getQueryParameter("auth_code")
            ?: uri.getQueryParameter("token")
        val error = uri.getQueryParameter("error")
        val errorDescription = uri.getQueryParameter("error_description")

        if (!error.isNullOrBlank()) {
            clearPkceState()
            Log.w(TAG, "OAuth2 authorization failed/cancelled: $error ($errorDescription)")
            return@withContext if (error == "access_denied" || error == "user_cancelled") {
                OAuth2Result.Cancelled(errorDescription ?: "User cancelled web authorization.")
            } else {
                OAuth2Result.Error("Authorization error: $error (${errorDescription ?: ""})")
            }
        }

        val expectedState = pendingState
        val verifier = pendingCodeVerifier

        // State validation (CSRF / Replay protection)
        if (expectedState != null && receivedState != expectedState) {
            clearPkceState()
            Log.e(TAG, "OAuth2 state mismatch! Expected: $expectedState, Got: $receivedState")
            return@withContext OAuth2Result.Error(
                "State validation failed: state mismatch prevents token replay attacks."
            )
        }

        if (code.isNullOrBlank()) {
            clearPkceState()
            Log.e(TAG, "OAuth2 redirect missing authorization code parameter.")
            return@withContext OAuth2Result.Error("Missing authorization code in redirect callback.")
        }

        if (verifier.isNullOrBlank()) {
            clearPkceState()
            Log.e(TAG, "Missing active PKCE code verifier for token exchange.")
            return@withContext OAuth2Result.Error("PKCE verifier expired or missing.")
        }

        // Perform token exchange
        try {
            val tokenResult = exchangeCodeForTokens(
                code = code,
                verifier = verifier,
                tokenEndpoint = pendingTokenEndpoint,
                clientId = clientId,
                redirectUri = pendingRedirectUri
            )
            tokenResult
        } finally {
            // Discard PKCE code verifier and state from temporary memory
            clearPkceState()
        }
    }

    /**
     * Exchanges the authorization code and PKCE code_verifier for session tokens at token endpoint.
     */
    private suspend fun exchangeCodeForTokens(
        code: String,
        verifier: String,
        tokenEndpoint: String,
        clientId: String,
        redirectUri: String
    ): OAuth2Result = withContext(Dispatchers.IO) {
        try {
            val formBody = FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("client_id", clientId)
                .add("redirect_uri", redirectUri)
                .add("code_verifier", verifier)
                .build()

            val request = Request.Builder()
                .url(tokenEndpoint)
                .post(formBody)
                .header("Accept", "application/json")
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBodyStr = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Token exchange HTTP error ${response.code}: $responseBodyStr")
                return@withContext OAuth2Result.Error(
                    "Token exchange failed with status code ${response.code}"
                )
            }

            val json = JSONObject(responseBodyStr)
            val accessToken = json.optString("access_token", code)
            val idToken = if (json.has("id_token") && !json.isNull("id_token")) json.optString("id_token") else null
            val refreshToken = if (json.has("refresh_token") && !json.isNull("refresh_token")) json.optString("refresh_token") else null
            val mfaHash = if (json.has("mfa_hash") && !json.isNull("mfa_hash")) json.optString("mfa_hash") else null

            val sessionParams = mutableMapOf<String, String>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                sessionParams[k] = json.optString(k, "")
            }

            Log.i(TAG, "OAuth2 token exchange successful.")
            OAuth2Result.Success(
                accessToken = accessToken,
                idToken = idToken,
                refreshToken = refreshToken,
                mfaHash = mfaHash,
                gridSessionParams = sessionParams
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to exchange authorization code for tokens", e)
            OAuth2Result.Error("Network exception during token exchange: ${e.message}", e)
        }
    }

    /**
     * Clears temporary in-memory PKCE verifier and state.
     */
    fun clearPkceState() {
        pendingCodeVerifier = null
        pendingState = null
        Log.d(TAG, "Temporary PKCE code verifier and state discarded from memory.")
    }

    fun hasPendingSession(): Boolean = pendingCodeVerifier != null && pendingState != null
}
