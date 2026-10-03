package com.linkpoint.ui.auth

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Data class representing an intercepted authentication token.
 */
data class InterceptedToken(
    val token: String,
    val mfaHash: String? = null
)

/**
 * Helper object to extract tokens from redirect URIs and validate allowed domains.
 */
object TokenExtractor {
    private val ALLOWED_DOMAINS = listOf(
        "secondlife.com",
        "lindenlab.com",
        "agni.lindenlab.com",
        "id.secondlife.com",
        "login.secondlife.com"
    )

    /**
     * Checks if a target URL is an OAuth / MFA callback or redirect containing a token.
     */
    fun isCallbackUrl(url: String): Boolean {
        if (url.isBlank()) return false

        val uri = try { Uri.parse(url) } catch (e: Exception) { return false }
        val scheme = uri.scheme?.lowercase() ?: ""

        // Custom scheme interception
        if (scheme == "secondlife" || scheme == "linkpoint") {
            return true
        }

        // Callback path / query param checks
        val path = uri.path?.lowercase() ?: ""
        val query = uri.query?.lowercase() ?: ""

        val isCallbackPath = path.contains("/callback") || path.contains("oauth") || path.contains("redirect")
        val hasTokenParam = query.contains("token=") ||
                query.contains("auth_token=") ||
                query.contains("mfa_token=") ||
                query.contains("session_token=") ||
                query.contains("code=")

        return isCallbackPath && hasTokenParam
    }

    /**
     * Extracts token and optional mfa_hash from a callback URI.
     */
    fun extractToken(url: String): InterceptedToken? {
        if (url.isBlank()) return null

        val uri = try { Uri.parse(url) } catch (e: Exception) { return null }

        // Standard token parameter keys in order of precedence
        val tokenKeys = listOf("token", "auth_token", "mfa_token", "session_token", "code", "access_token")
        var extractedToken: String? = null

        for (key in tokenKeys) {
            val param = uri.getQueryParameter(key)
            if (!param.isNullOrBlank()) {
                extractedToken = param
                break
            }
        }

        if (extractedToken == null && (uri.scheme == "secondlife" || uri.scheme == "linkpoint")) {
            // Handle scheme authority/host as token if param missing e.g. secondlife://token_12345
            val host = uri.host
            if (!host.isNullOrBlank() && host != "auth" && host != "callback") {
                extractedToken = host
            }
        }

        if (extractedToken.isNullOrBlank()) {
            return null
        }

        val mfaHash = uri.getQueryParameter("mfa_hash")
        return InterceptedToken(token = extractedToken, mfaHash = mfaHash)
    }

    /**
     * Restricts navigation to allowed Second Life / Linden Lab domains or grid host.
     */
    fun isDomainAllowed(url: String, gridUri: String? = null): Boolean {
        if (url.isBlank()) return false

        val uri = try { Uri.parse(url) } catch (e: Exception) { return false }
        val scheme = uri.scheme?.lowercase() ?: ""

        if (scheme == "secondlife" || scheme == "linkpoint" || scheme == "about") {
            return true
        }

        val host = uri.host?.lowercase() ?: return false

        // Check allowed domain whitelist
        for (domain in ALLOWED_DOMAINS) {
            if (host == domain || host.endsWith(".$domain")) {
                return true
            }
        }

        // Check custom grid host if provided
        if (!gridUri.isNullOrBlank()) {
            val gridHost = try { Uri.parse(gridUri).host?.lowercase() } catch (e: Exception) { null }
            if (gridHost != null && (host == gridHost || host.endsWith(".$gridHost"))) {
                return true
            }
        }

        return false
    }

    /**
     * Clears CookieManager cookie state for secure logout.
     */
    fun clearCookies() {
        try {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()
        } catch (e: Exception) {
            // Ignore if WebView engine is not initialized in process
        }
    }
}

/**
 * Embedded Android WebView dialog for multi-factor authentication challenges and web verification.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebAuthInterceptorDialog(
    initialUrl: String,
    gridUri: String? = null,
    onTokenIntercepted: (InterceptedToken) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Hardware back button navigation handling
    BackHandler(enabled = true) {
        val webView = webViewRef
        if (webView != null && webView.canGoBack()) {
            webView.goBack()
        } else {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Second Life Web Verification",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close web verification",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Loading Indicator Bar
                if (isLoading && !hasError) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Main Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasError) {
                        // Error View with Retry button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Network Connection Error",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (errorMessage.isNotBlank()) errorMessage else "Unable to load authentication portal. Please check your network connection and try again.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    hasError = false
                                    isLoading = true
                                    webViewRef?.reload()
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Retry")
                            }
                        }
                    } else {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    // Guardrails & Security Configuration
                                    settings.javaScriptEnabled = true
                                    settings.allowFileAccess = false
                                    settings.allowContentAccess = false
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        @Suppress("DEPRECATION")
                                        settings.allowFileAccessFromFileURLs = false
                                        @Suppress("DEPRECATION")
                                        settings.allowUniversalAccessFromFileURLs = false
                                    }
                                    settings.domStorageEnabled = true
                                    settings.useWideViewPort = true
                                    settings.loadWithOverviewMode = true

                                    webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(
                                            view: WebView?,
                                            request: WebResourceRequest?
                                        ): Boolean {
                                            val url = request?.url?.toString() ?: return false
                                            return handleUrlNavigation(url, gridUri, onTokenIntercepted)
                                        }

                                        @Suppress("DEPRECATION")
                                        override fun shouldOverrideUrlLoading(
                                            view: WebView?,
                                            url: String?
                                        ): Boolean {
                                            if (url == null) return false
                                            return handleUrlNavigation(url, gridUri, onTokenIntercepted)
                                        }

                                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                            super.onPageStarted(view, url, favicon)
                                            isLoading = true
                                            if (url != null && TokenExtractor.isCallbackUrl(url)) {
                                                val token = TokenExtractor.extractToken(url)
                                                if (token != null) {
                                                    onTokenIntercepted(token)
                                                }
                                            }
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            isLoading = false
                                        }

                                        override fun onReceivedError(
                                            view: WebView?,
                                            request: WebResourceRequest?,
                                            error: WebResourceError?
                                        ) {
                                            super.onReceivedError(view, request, error)
                                            // Only display full error view if main page frame failed
                                            if (request?.isForMainFrame == true) {
                                                hasError = true
                                                isLoading = false
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    errorMessage = error?.description?.toString() ?: ""
                                                }
                                            }
                                        }
                                    }

                                    loadUrl(initialUrl)
                                    webViewRef = this
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.destroy()
            webViewRef = null
        }
    }
}

private fun handleUrlNavigation(
    url: String,
    gridUri: String?,
    onTokenIntercepted: (InterceptedToken) -> Unit
): Boolean {
    // 1. Check if token callback
    if (TokenExtractor.isCallbackUrl(url)) {
        val token = TokenExtractor.extractToken(url)
        if (token != null) {
            onTokenIntercepted(token)
            return true
        }
    }

    // 2. Enforce domain restrictions
    if (!TokenExtractor.isDomainAllowed(url, gridUri)) {
        // Block external navigation outside allowed SL/grid domains
        return true
    }

    return false
}
