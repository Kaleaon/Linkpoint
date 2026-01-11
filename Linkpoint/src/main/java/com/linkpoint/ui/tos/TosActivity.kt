package com.linkpoint.ui.tos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.linkpoint.R

/**
 * Terms of Service Activity
 * 
 * Based on Lumiya's approach: Users must accept Second Life's
 * Terms of Service before their first login.
 * 
 * This is similar to how Lumiya handles ToS acceptance - it shows
 * the ToS inline and requires explicit acceptance before proceeding.
 */
class TosActivity : AppCompatActivity() {
    
    companion object {
        private const val PREFS_NAME = "tos_prefs"
        private const val KEY_TOS_ACCEPTED = "tos_accepted"
        private const val KEY_TOS_VERSION = "tos_version"
        private const val KEY_TOS_ACCEPT_DATE = "tos_accept_date"
        
        // Current ToS version - increment when ToS changes require re-acceptance
        private const val CURRENT_TOS_VERSION = 1
        
        // Second Life ToS URL
        private const val TOS_URL = "https://www.lindenlab.com/tos"
        private const val COMMUNITY_STANDARDS_URL = "https://www.lindenlab.com/legal/community-standards"
        
        /**
         * Check if user has accepted current ToS version
         */
        fun hasAcceptedTos(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val acceptedVersion = prefs.getInt(KEY_TOS_VERSION, 0)
            return prefs.getBoolean(KEY_TOS_ACCEPTED, false) && acceptedVersion >= CURRENT_TOS_VERSION
        }
        
        /**
         * Record ToS acceptance
         * Uses commit() instead of apply() to ensure synchronous persistence
         * for this critical data - prevents loss if app is killed immediately after
         */
        fun recordTosAcceptance(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean(KEY_TOS_ACCEPTED, true)
                .putInt(KEY_TOS_VERSION, CURRENT_TOS_VERSION)
                .putLong(KEY_TOS_ACCEPT_DATE, System.currentTimeMillis())
                .commit() // Use commit() for critical ToS data to ensure synchronous write
        }
        
        /**
         * Get ToS acceptance date
         */
        fun getTosAcceptanceDate(context: Context): Long {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getLong(KEY_TOS_ACCEPT_DATE, 0)
        }
        
        /**
         * Create intent to launch ToS activity
         */
        fun createIntent(context: Context, requireAcceptance: Boolean = true): Intent {
            return Intent(context, TosActivity::class.java).apply {
                putExtra("require_acceptance", requireAcceptance)
            }
        }
    }
    
    private lateinit var webView: WebView
    private lateinit var acceptButton: Button
    private lateinit var declineButton: Button
    
    private var requireAcceptance = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tos)
        
        requireAcceptance = intent.getBooleanExtra("require_acceptance", true)
        
        setupViews()
        loadTos()
    }
    
    private fun setupViews() {
        webView = findViewById(R.id.tosWebView)
        acceptButton = findViewById(R.id.acceptButton)
        declineButton = findViewById(R.id.declineButton)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Terms of Service"
        
        // Configure WebView with security restrictions
        // ToS content is static HTML so we can disable JavaScript and file access
        webView.apply {
            settings.javaScriptEnabled = false  // Disable JS for security - not needed for static ToS
            settings.domStorageEnabled = false
            settings.allowFileAccess = false    // Prevent file system access
            settings.allowContentAccess = false // Prevent content provider access
            @Suppress("DEPRECATION")
            settings.allowFileAccessFromFileURLs = false
            @Suppress("DEPRECATION")
            settings.allowUniversalAccessFromFileURLs = false
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // Enable accept button once page loads
                    acceptButton.isEnabled = true
                }
            }
        }
        
        // Setup buttons
        if (requireAcceptance) {
            acceptButton.isEnabled = false // Disabled until ToS loads
            acceptButton.setOnClickListener {
                acceptTos()
            }
            declineButton.setOnClickListener {
                declineTos()
            }
        } else {
            // Just viewing ToS, not requiring acceptance
            acceptButton.text = "Close"
            acceptButton.isEnabled = true
            acceptButton.setOnClickListener {
                finish()
            }
            declineButton.visibility = android.view.View.GONE
        }
    }
    
    private fun loadTos() {
        // Load the local ToS content first, fallback to online
        try {
            val tosHtml = buildTosHtml()
            webView.loadDataWithBaseURL(null, tosHtml, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            android.util.Log.e("TosActivity", "Failed to build local ToS HTML, using fallback", e)
            // Fallback to online ToS
            webView.loadUrl(TOS_URL)
        }
    }
    
    private fun buildTosHtml(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        padding: 16px;
                        line-height: 1.6;
                        color: #333;
                        background: #fafafa;
                    }
                    h1 { color: #1a73e8; font-size: 24px; }
                    h2 { color: #333; font-size: 18px; margin-top: 24px; }
                    a { color: #1a73e8; }
                    .section { background: white; padding: 16px; margin: 16px 0; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
                    .important { background: #fff3e0; padding: 12px; border-left: 4px solid #ff9800; margin: 16px 0; }
                    .disclaimer { background: #e3f2fd; padding: 12px; border-left: 4px solid #2196f3; margin: 16px 0; }
                </style>
            </head>
            <body>
                <h1>Terms of Service &amp; Disclosures</h1>
                
                <div class="disclaimer">
                    <strong>Important Disclaimer:</strong> Linkpoint is <strong>not provided or supported by Linden Lab</strong>, the makers of Second Life. This is an independent, community-developed third-party viewer.
                </div>
                
                <div class="section">
                    <h2>About Linkpoint</h2>
                    <p><strong>Viewer Name:</strong> Linkpoint</p>
                    <p><strong>Type:</strong> Third-Party Viewer for Second Life (Android)</p>
                    <p><strong>Customer Support:</strong> Community support only via <a href="https://github.com/Kaleaon/Linkpoint/issues">GitHub Issues</a>. No official support is provided.</p>
                    <p><strong>Privacy Policy:</strong> <a href="https://github.com/Kaleaon/Linkpoint/blob/main/PRIVACY_POLICY.md">View Privacy Policy</a></p>
                </div>
                
                <div class="section">
                    <h2>Mobile Limitations</h2>
                    <p>As a mobile viewer, Linkpoint has certain limitations compared to desktop viewers:</p>
                    <ul>
                        <li>Limited building and editing functionality</li>
                        <li>Simplified graphics optimized for mobile devices</li>
                        <li>Touch-based controls instead of keyboard/mouse</li>
                        <li>Voice chat via WebRTC implementation</li>
                    </ul>
                </div>
                
                <div class="important">
                    <strong>Important:</strong> By using Linkpoint to access Second Life, you agree to Linden Lab's Terms of Service and Community Standards.
                </div>
                
                <div class="section">
                    <h2>Linden Lab Terms of Service</h2>
                    <p>The complete Terms of Service can be found at:</p>
                    <p><a href="$TOS_URL">$TOS_URL</a></p>
                    <p>Key points include:</p>
                    <ul>
                        <li>You must be at least 16 years old (18 for adult content)</li>
                        <li>You are responsible for your account security</li>
                        <li>Virtual currency (L$) has no real-world value</li>
                        <li>Linden Lab may modify or terminate services</li>
                        <li>User-created content remains yours, with license to Linden Lab</li>
                    </ul>
                </div>
                
                <div class="section">
                    <h2>Community Standards</h2>
                    <p>The Community Standards can be found at:</p>
                    <p><a href="$COMMUNITY_STANDARDS_URL">$COMMUNITY_STANDARDS_URL</a></p>
                    <p>The "Big Six" rules:</p>
                    <ul>
                        <li><strong>Intolerance:</strong> No harassment based on race, ethnicity, gender, religion, or sexual orientation</li>
                        <li><strong>Harassment:</strong> No stalking, bullying, or intimidation</li>
                        <li><strong>Assault:</strong> No pushing, shooting, or other scripted attacks</li>
                        <li><strong>Disclosure:</strong> No sharing of private information</li>
                        <li><strong>Indecency:</strong> Adult content only in designated areas</li>
                        <li><strong>Disturbing the Peace:</strong> No excessive noise, spam, or griefing</li>
                    </ul>
                </div>
                
                <div class="section">
                    <h2>Third-Party Viewer Policy</h2>
                    <p>Linkpoint complies with <a href="https://secondlife.com/corporate/third-party-viewers">Linden Lab's Third-Party Viewer Policy</a>.</p>
                    <p>By using Linkpoint, you also agree to:</p>
                    <ul>
                        <li>Not use the viewer for malicious purposes</li>
                        <li>Not exploit vulnerabilities or bugs</li>
                        <li>Respect the privacy and rights of other users</li>
                        <li>Not circumvent the Second Life permissions system</li>
                        <li>Not use export features to copy content you don't own</li>
                    </ul>
                </div>
                
                <div class="section">
                    <h2>Privacy</h2>
                    <p>Linkpoint stores your login credentials locally on your device if you choose to save them.</p>
                    <p><strong>What we do NOT do:</strong></p>
                    <ul>
                        <li>We do NOT collect analytics or telemetry</li>
                        <li>We do NOT transmit your credentials to third parties</li>
                        <li>We do NOT sell or share any user data</li>
                    </ul>
                    <p>Your password is encrypted using Android Keystore and stored only on your device.</p>
                    <p>For full details, see our <a href="https://github.com/Kaleaon/Linkpoint/blob/main/PRIVACY_POLICY.md">Privacy Policy</a>.</p>
                </div>
                
                <div class="disclaimer">
                    <p><strong>Trademarks:</strong> Second Life is a trademark of Linden Lab. Linkpoint is not affiliated with or endorsed by Linden Lab.</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
    
    private fun acceptTos() {
        recordTosAcceptance(this)
        Toast.makeText(this, "Terms of Service accepted", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }
    
    private fun declineTos() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Decline Terms of Service?")
            .setMessage("You must accept the Terms of Service to use Linkpoint and connect to Second Life.\n\nIf you decline, you will not be able to log in.")
            .setPositiveButton("Go Back") { _, _ ->
                // Do nothing, stay on ToS screen
            }
            .setNegativeButton("Decline and Exit") { _, _ ->
                setResult(RESULT_CANCELED)
                finishAffinity() // Close the entire app
            }
            .show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        if (requireAcceptance) {
            declineTos()
            return false
        }
        finish()
        return true
    }
    
    override fun onBackPressed() {
        if (requireAcceptance) {
            declineTos()
        } else {
            super.onBackPressed()
        }
    }
}
