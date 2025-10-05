package com.linkpoint.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.linkpoint.auth.LinkpointAuthManager
import com.linkpoint.core.LinkpointApplication
import com.linkpoint.voice.LinkpointVoiceManager
import kotlinx.coroutines.*

/**
 * Linkpoint Main Activity
 * Modern Material Design 3 UI for Second Life viewer
 */
class LinkpointMainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "LinkpointMain"
        private const val PERMISSION_REQUEST_CODE = 1001
    }
    
    private lateinit var authManager: LinkpointAuthManager
    private lateinit var voiceManager: LinkpointVoiceManager
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // UI Components
    private lateinit var firstNameEdit: EditText
    private lateinit var lastNameEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var loginButton: Button
    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar
    
    // Voice UI
    private lateinit var voiceButton: Button
    private lateinit var muteButton: Button
    private lateinit var voiceStatus: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get managers from application
        val app = application as LinkpointApplication
        authManager = app.authManager
        voiceManager = app.voiceManager
        
        // Create modern UI programmatically
        createModernUI()
        
        // Request permissions
        checkAndRequestPermissions()
    }
    
    private fun createModernUI() {
        // Create root layout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Title
        val titleText = TextView(this).apply {
            text = "🔗 Linkpoint"
            textSize = 32f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }
        rootLayout.addView(titleText)
        
        val subtitleText = TextView(this).apply {
            text = "Modern Second Life Viewer"
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 8, 0, 32)
        }
        rootLayout.addView(subtitleText)
        
        // Login form
        firstNameEdit = EditText(this).apply {
            hint = "First Name"
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        }
        rootLayout.addView(firstNameEdit)
        
        lastNameEdit = EditText(this).apply {
            hint = "Last Name"
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        }
        rootLayout.addView(lastNameEdit)
        
        passwordEdit = EditText(this).apply {
            hint = "Password"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or 
                       android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        rootLayout.addView(passwordEdit)
        
        loginButton = Button(this).apply {
            text = "Login to Second Life"
            setOnClickListener { performLogin() }
        }
        rootLayout.addView(loginButton)
        
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            visibility = View.GONE
        }
        rootLayout.addView(progressBar)
        
        statusText = TextView(this).apply {
            text = "Ready to connect"
            gravity = android.view.Gravity.CENTER
            setPadding(0, 16, 0, 16)
        }
        rootLayout.addView(statusText)
        
        // Voice controls
        val voiceLabel = TextView(this).apply {
            text = "Voice Chat"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 32, 0, 8)
        }
        rootLayout.addView(voiceLabel)
        
        voiceButton = Button(this).apply {
            text = "Enable Voice"
            setOnClickListener { toggleVoice() }
            isEnabled = false
        }
        rootLayout.addView(voiceButton)
        
        muteButton = Button(this).apply {
            text = "Mute Microphone"
            setOnClickListener { toggleMute() }
            isEnabled = false
        }
        rootLayout.addView(muteButton)
        
        voiceStatus = TextView(this).apply {
            text = "Voice: Disabled"
            gravity = android.view.Gravity.CENTER
        }
        rootLayout.addView(voiceStatus)
        
        setContentView(rootLayout)
    }
    
    private fun performLogin() {
        val firstName = firstNameEdit.text.toString().trim()
        val lastName = lastNameEdit.text.toString().trim()
        val password = passwordEdit.text.toString()
        
        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            statusText.text = "Please fill in all fields"
            return
        }
        
        loginButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
        
        scope.launch {
            val success = authManager.authenticateWithSecondLife(
                firstName = firstName,
                lastName = lastName,
                password = password,
                callback = createAuthCallback()
            )
            
            progressBar.visibility = View.GONE
            loginButton.isEnabled = true
            
            if (success) {
                voiceButton.isEnabled = true
            }
        }
    }
    
    private fun createAuthCallback() = object : LinkpointAuthManager.AuthCallback {
        override fun onAuthSuccess(agentId: String, sessionId: String) {
            scope.launch {
                statusText.text = "✓ Connected to Second Life"
            }
        }
        
        override fun onAuthFailure(error: String) {
            scope.launch {
                statusText.text = "✗ Login failed: $error"
            }
        }
        
        override fun onAuthProgress(message: String) {
            scope.launch {
                statusText.text = message
            }
        }
    }
    
    private fun toggleVoice() {
        scope.launch {
            if (!voiceManager.isConnected()) {
                val initialized = voiceManager.initialize()
                if (initialized) {
                    val connected = voiceManager.connectToVoiceChannel(
                        channelUri = "sip:confctl-1@bhr.vivox.com",
                        authToken = "demo-token"
                    )
                    if (connected) {
                        voiceButton.text = "Disable Voice"
                        muteButton.isEnabled = true
                        voiceStatus.text = "Voice: Connected"
                    }
                }
            } else {
                voiceManager.leaveVoiceChannel("sip:confctl-1@bhr.vivox.com")
                voiceButton.text = "Enable Voice"
                muteButton.isEnabled = false
                voiceStatus.text = "Voice: Disabled"
            }
        }
    }
    
    private fun toggleMute() {
        val currentlyMuted = voiceManager.isMuted()
        voiceManager.setMicrophoneMuted(!currentlyMuted)
        muteButton.text = if (!currentlyMuted) "Unmute Microphone" else "Mute Microphone"
    }
    
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                statusText.text = "Permissions granted - Ready to connect"
            } else {
                statusText.text = "Some permissions denied - Voice may not work"
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}