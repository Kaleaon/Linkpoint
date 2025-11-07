package com.linkpoint.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.linkpoint.BuildConfig
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.slproto.auth.SLAuth
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.ui.render.WorldViewActivity

import java.io.IOException
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Clean implementation of LoginActivity without decompilation artifacts
 * Implements real grid authentication using SLAuth
 */
class CleanLoginActivity : AppCompatActivity() {
    
    private lateinit var firstNameEdit: EditText
    private lateinit var lastNameEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var loginButton: Button
    private lateinit var loginProgress: ProgressBar
    private lateinit var statusText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.i("CleanLoginActivity", "Starting CleanLoginActivity")
        
        try {
            setContentView(R.layout.activity_clean_login)
            
            initializeViews()
            setupLoginButton()
            
            Log.i("CleanLoginActivity", "CleanLoginActivity initialized successfully")
        } catch (e: Exception) {
            Log.e("CleanLoginActivity", "Error during activity initialization", e)
            
            // Create a basic error layout if the normal layout fails
            createFallbackLayout(e)
        }
    }
    
    /**
     * Create a basic fallback layout when normal initialization fails
     */
    private fun createFallbackLayout(originalError: Exception) {
        try {
            Log.w("CleanLoginActivity", "Creating fallback layout due to: ${originalError.message}")
            
            // Create a simple error display
            setTitle("Linkpoint - Second Life Viewer")
            
            // Since we can't rely on the XML layout, create a simple text view
            val errorText = TextView(this).apply {
                text = "Linkpoint Second Life Viewer\n\n" +
                        "The app is starting in safe mode.\n" +
                        "Some features may be limited.\n\n" +
                        "Error: ${originalError.message}"
                setPadding(32, 32, 32, 32)
                textSize = 16f
            }
            setContentView(errorText)
            
            Log.i("CleanLoginActivity", "Fallback layout created successfully")
        } catch (e: Exception) {
            Log.e("CleanLoginActivity", "Failed to create fallback layout", e)
            // Last resort - just finish the activity
            finish()
        }
    }
    
    private fun initializeViews() {
        try {
            firstNameEdit = findViewById(R.id.edit_first_name)
            lastNameEdit = findViewById(R.id.edit_last_name)
            passwordEdit = findViewById(R.id.edit_password)
            loginButton = findViewById(R.id.button_login)
            loginProgress = findViewById(R.id.progress_login)
            statusText = findViewById(R.id.text_status)
            
            // Initialize with default values for testing
            firstNameEdit.setText("Test")
            lastNameEdit.setText("User")
            
            // Show app status in the status text for debugging
            val appStatus = LinkpointApp.getStartupStatus()
            statusText.text = "Ready to login to Second Life\n\n$appStatus"
            
            // Add debug log upload button for debug builds
            addDebugLogUploadButton()
            
            Log.i("CleanLoginActivity", "All views initialized successfully")
            Log.i("CleanLoginActivity", appStatus)
        } catch (e: Exception) {
            Log.e("CleanLoginActivity", "Error initializing views", e)
            throw e // Re-throw to trigger fallback layout
        }
    }
    
    private fun setupLoginButton() {
        loginButton.setOnClickListener {
            performLogin()
        }
    }
    
    private fun performLogin() {
        val firstName = firstNameEdit.text.toString().trim()
        val lastName = lastNameEdit.text.toString().trim()
        val password = passwordEdit.text.toString()
        
        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields")
            return
        }
        
        // Show progress
        setLoginInProgress(true)
        statusText.text = "Authenticating with Second Life..."
        
        // Perform real authentication using SLAuth
        CompletableFuture.runAsync {
            try {
                // Build login parameters
                val loginName = "$firstName $lastName"
                val passwordHash = SLAuth.getPasswordHash(password)
                val clientId = UUID.randomUUID()
                val startLocation = "last"
                val loginUrl = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
                val gridName = "Second Life"
                
                val authParams = SLAuthParams(
                    loginName = loginName,
                    passwordHash = passwordHash,
                    clientId = clientId,
                    startLocation = startLocation,
                    loginUrl = loginUrl,
                    gridName = gridName
                )
                
                Log.i("CleanLoginActivity", "Attempting login for: $loginName")
                
                // Perform actual authentication
                val slAuth = SLAuth()
                val authReply = slAuth.login(authParams)
                
                if (authReply.success) {
                    Log.i("CleanLoginActivity", "Login successful! Agent ID: ${authReply.agentId}")
                    
                    runOnUiThread {
                        setLoginInProgress(false)
                        statusText.text = "Login successful! Starting Second Life..."
                        
                        // Launch WorldViewActivity
                        launchWorldView(authParams, authReply)
                    }
                } else {
                    Log.e("CleanLoginActivity", "Login failed: ${authReply.message}")
                    
                    runOnUiThread {
                        setLoginInProgress(false)
                        showError("Login failed: ${authReply.message}")
                    }
                }
                
            } catch (e: IOException) {
                Log.e("CleanLoginActivity", "Network error during login", e)
                runOnUiThread {
                    setLoginInProgress(false)
                    showError("Network error: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("CleanLoginActivity", "Login error", e)
                runOnUiThread {
                    setLoginInProgress(false)
                    showError("Login failed: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Launch the WorldViewActivity after successful login
     */
    private fun launchWorldView(authParams: SLAuthParams, authReply: SLAuthReply) {
        try {
            val intent = Intent(this, WorldViewActivity::class.java)
            
            // Pass authentication parameters via intent extras
            intent.putExtra("login_name", authParams.loginName)
            intent.putExtra("client_id", authParams.clientId.toString())
            intent.putExtra("grid_name", authParams.gridName)
            intent.putExtra("login_url", authParams.loginUrl)
            
            // Pass authentication reply data (only non-null values)
            authReply.agentId?.let { intent.putExtra("agent_id", it.toString()) }
            authReply.sessionId?.let { intent.putExtra("session_id", it.toString()) }
            authReply.secureSessionId?.let { intent.putExtra("secure_session_id", it.toString()) }
            intent.putExtra("circuit_code", authReply.circuitCode)
            authReply.simAddress?.let { intent.putExtra("sim_address", it) }
            intent.putExtra("sim_port", authReply.simPort)
            authReply.seedCapability?.let { intent.putExtra("seed_capability", it) }
            
            Log.i("CleanLoginActivity", "Launching WorldViewActivity")
            
            // Start the world view
            startActivity(intent)
            
            // Show success message
            Toast.makeText(this, "Welcome to Second Life!", Toast.LENGTH_LONG).show()
            
            // Finish this activity so back button doesn't return to login
            finish()
            
        } catch (e: Exception) {
            Log.e("CleanLoginActivity", "Error launching WorldViewActivity", e)
            showError("Error starting world view: ${e.message}")
        }
    }
    
    private fun setLoginInProgress(inProgress: Boolean) {
        loginButton.isEnabled = !inProgress
        loginProgress.visibility = if (inProgress) View.VISIBLE else View.GONE
        firstNameEdit.isEnabled = !inProgress
        lastNameEdit.isEnabled = !inProgress
        passwordEdit.isEnabled = !inProgress
    }
    
    private fun showError(message: String) {
        statusText.text = "Error: $message"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Add debug log upload button for debug builds
     */
    private fun addDebugLogUploadButton() {
        // Only add for debug builds
        if (!BuildConfig.DEBUG) {
            return
        }
        
        try {
            // Find the parent layout
            val parentLayout = statusText.parent as? ViewGroup ?: return
            
            // Create debug log upload button
            val debugUploadButton = Button(this).apply {
                text = "📤 Upload Debug Logs"
                textSize = 12f
            }
            
            // Set layout parameters
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 0)
            }
            debugUploadButton.layoutParams = params
            
            // Set click listener
            debugUploadButton.setOnClickListener {
                debugUploadButton.isEnabled = false
                debugUploadButton.text = "📤 Uploading..."
                
                LinkpointApp.uploadDebugLogsNow("Manual upload from login screen")
                
                // Re-enable button after a delay
                debugUploadButton.postDelayed({
                    debugUploadButton.isEnabled = true
                    debugUploadButton.text = "📤 Upload Debug Logs"
                    Toast.makeText(this, "Debug logs upload initiated - check logcat for results", Toast.LENGTH_LONG).show()
                }, 2000)
            }
            
            // Add to parent layout
            parentLayout.addView(debugUploadButton)
            
            Log.i("CleanLoginActivity", "Debug log upload button added")
            
        } catch (e: Exception) {
            Log.e("CleanLoginActivity", "Failed to add debug upload button", e)
        }
    }
}
