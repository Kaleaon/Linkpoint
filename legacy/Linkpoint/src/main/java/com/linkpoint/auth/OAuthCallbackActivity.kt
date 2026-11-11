package com.linkpoint.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.linkpoint.LinkpointApp
import com.linkpoint.ui.login.ComposeLoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity that receives OAuth2 custom scheme redirects (`slviewer://auth-callback`, `secondlife://auth-callback`, `linkpoint://auth-callback`).
 *
 * Processes the authorization code callback, completes PKCE state validation, and returns the user to the active session.
 */
class OAuthCallbackActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "OAuthCallbackActivity"
        const val EXTRA_OAUTH_TOKEN = "extra_oauth_token"
        const val EXTRA_MFA_HASH = "extra_mfa_hash"
        const val EXTRA_AUTH_ERROR = "extra_auth_error"

        @Volatile
        var latestCallbackResult: OAuth2Result? = null
            private set

        fun clearLatestResult() {
            latestCallbackResult = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = intent?.data
        Log.i(TAG, "OAuthCallbackActivity received intent data URI: $uri")

        if (uri != null) {
            val authManager = OAuth2AuthManager.getInstance()
            CoroutineScope(Dispatchers.IO).launch {
                val result = authManager.handleRedirectUri(uri)
                latestCallbackResult = result

                withContext(Dispatchers.Main) {
                    when (result) {
                        is OAuth2Result.Success -> {
                            Log.i(TAG, "OAuth2 authorization successful, returning to app.")
                            resumeApplicationWithToken(result)
                        }
                        is OAuth2Result.Cancelled -> {
                            Log.w(TAG, "OAuth2 authorization cancelled.")
                            returnToLoginActivityWithError(result.message)
                        }
                        is OAuth2Result.Error -> {
                            Log.e(TAG, "OAuth2 authorization error: ${result.message}")
                            returnToLoginActivityWithError(result.message)
                        }
                    }
                    finish()
                }
            }
        } else {
            Log.w(TAG, "OAuthCallbackActivity launched with null data URI.")
            finish()
        }
    }

    private fun resumeApplicationWithToken(result: OAuth2Result.Success) {
        val launchIntent = Intent(this, ComposeLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_OAUTH_TOKEN, result.accessToken)
            if (!result.mfaHash.isNullOrBlank()) {
                putExtra(EXTRA_MFA_HASH, result.mfaHash)
            }
        }
        startActivity(launchIntent)
    }

    private fun returnToLoginActivityWithError(errorMessage: String) {
        val launchIntent = Intent(this, ComposeLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_AUTH_ERROR, errorMessage)
        }
        startActivity(launchIntent)
    }
}
