package com.linkpoint.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.linkpoint.R
import com.linkpoint.network.SecondLifeConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Login activity for Second Life authentication
 */
class LoginActivity : AppCompatActivity() {
    
    private lateinit var firstNameInput: TextInputEditText
    private lateinit var lastNameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var gridSpinner: TextView
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    
    private val connection = SecondLifeConnection()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        initViews()
        setupListeners()
    }
    
    private fun initViews() {
        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        passwordInput = findViewById(R.id.passwordInput)
        gridSpinner = findViewById(R.id.gridSelector)
        loginButton = findViewById(R.id.loginButton)
        progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.statusText)
        
        // Set default grid
        gridSpinner.text = "Second Life"
    }
    
    private fun setupListeners() {
        loginButton.setOnClickListener {
            attemptLogin()
        }
        
        gridSpinner.setOnClickListener {
            showGridSelector()
        }
    }
    
    private fun attemptLogin() {
        val firstName = firstNameInput.text?.toString()?.trim() ?: ""
        val lastName = lastNameInput.text?.toString()?.trim() ?: ""
        val password = passwordInput.text?.toString() ?: ""
        
        // Validate input
        if (firstName.isEmpty()) {
            firstNameInput.error = "First name required"
            return
        }
        if (lastName.isEmpty()) {
            lastNameInput.error = "Last name required"
            return
        }
        if (password.isEmpty()) {
            passwordInput.error = "Password required"
            return
        }
        
        // Check network connectivity before attempting login
        if (!isNetworkAvailable()) {
            statusText.text = "No internet connection"
            Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_LONG).show()
            return
        }
        
        // Start login process
        setLoading(true)
        statusText.text = "Connecting..."
        
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    connection.login(firstName, lastName, password)
                }
                
                if (result.success) {
                    statusText.text = "Login successful!"
                    navigateToMain()
                } else {
                    statusText.text = "Login failed"
                    showLoginErrorDialog(result)
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unexpected error occurred"
                statusText.text = "Login failed"
                showLoginErrorDialog(SecondLifeConnection.LoginResult(
                    success = false,
                    message = "Network error: $errorMessage",
                    errorCode = "EXCEPTION",
                    technicalDetails = "${e.javaClass.simpleName}: ${e.message}\n${e.stackTrace.take(5).joinToString("\n")}"
                ))
            } finally {
                setLoading(false)
            }
        }
    }
    
    /**
     * Check if network is available and has internet capability.
     * 
     * Note: We only require NET_CAPABILITY_INTERNET, not NET_CAPABILITY_VALIDATED,
     * because validation can be slow or fail on some mobile networks even when
     * connectivity is actually available. The actual HTTP request will determine
     * if the connection works.
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        // Only require internet capability - validation check is too strict for some LTE networks
        // where validation may be slow or fail temporarily even though connectivity works
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        loginButton.isEnabled = !loading
        firstNameInput.isEnabled = !loading
        lastNameInput.isEnabled = !loading
        passwordInput.isEnabled = !loading
    }
    
    private fun showGridSelector() {
        // Show grid selection dialog using grids from SecondLifeConnection
        val grids = SecondLifeConnection.GRIDS.keys.toTypedArray()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Select Grid")
            .setItems(grids) { _, which ->
                gridSpinner.text = grids[which]
            }
            .show()
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun showLoginErrorDialog(result: SecondLifeConnection.LoginResult) {
        val errorCodeDisplay = result.errorCode?.let { " [$it]" } ?: ""
        
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Login Failed$errorCodeDisplay")
            .setMessage(result.message)
            .setPositiveButton("OK", null)
        
        // Add "Details" button if technical details are available
        if (!result.technicalDetails.isNullOrBlank()) {
            builder.setNeutralButton("View Details") { _, _ ->
                showTechnicalDetailsDialog(result)
            }
        }
        
        // Add retry button for network errors
        if (result.errorCode?.contains("NETWORK", ignoreCase = true) == true ||
            result.errorCode?.contains("TIMEOUT", ignoreCase = true) == true ||
            result.errorCode?.contains("CONNECTION", ignoreCase = true) == true ||
            result.errorCode?.contains("DNS", ignoreCase = true) == true ||
            result.errorCode?.contains("SSL", ignoreCase = true) == true) {
            builder.setNegativeButton("Retry") { _, _ ->
                attemptLogin()
            }
        }
        
        builder.show()
    }
    
    private fun showTechnicalDetailsDialog(result: SecondLifeConnection.LoginResult) {
        val gridName = gridSpinner.text.toString()
        val loginUrl = SecondLifeConnection.GRIDS[gridName] ?: "Unknown"
        
        val details = buildString {
            appendLine("Error Code: ${result.errorCode ?: "UNKNOWN"}")
            appendLine()
            appendLine("Message: ${result.message}")
            appendLine()
            appendLine("--- Technical Details ---")
            appendLine(result.technicalDetails ?: "No additional details available")
            appendLine()
            appendLine("--- Device Info ---")
            appendLine("Android: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
            appendLine("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
            appendLine("Grid: $gridName")
            appendLine("Login URI: $loginUrl")
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Technical Details")
            .setMessage(details)
            .setPositiveButton("OK", null)
            .setNeutralButton("Copy") { _, _ ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Login Error Details", details)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Details copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
