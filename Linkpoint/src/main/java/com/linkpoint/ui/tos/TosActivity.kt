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
         */
        fun recordTosAcceptance(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean(KEY_TOS_ACCEPTED, true)
                .putInt(KEY_TOS_VERSION, CURRENT_TOS_VERSION)
                .putLong(KEY_TOS_ACCEPT_DATE, System.currentTimeMillis())
                .apply()
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
        
        // Configure WebView
        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
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
                </style>
            </head>
            <body>
                <h1>Second Life Terms of Service</h1>
                
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
                    <h2>Linkpoint Third-Party Viewer Policy</h2>
                    <p>Linkpoint is a third-party viewer that complies with Linden Lab's Third-Party Viewer Policy.</p>
                    <p>By using Linkpoint, you also agree to:</p>
                    <ul>
                        <li>Not use the viewer for malicious purposes</li>
                        <li>Not exploit vulnerabilities or bugs</li>
                        <li>Respect the privacy and rights of other users</li>
                    </ul>
                </div>
                
                <div class="important">
                    <p><strong>Privacy:</strong> Linkpoint stores your login credentials locally on your device if you choose to save them. We do not collect or transmit your password to any third parties.</p>
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
