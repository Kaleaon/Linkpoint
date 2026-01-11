package com.linkpoint.ui.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.core.GridInfo
import com.linkpoint.network.LoginResult
import com.linkpoint.network.NetworkDiagnostics
import com.linkpoint.network.SSLHelper
import com.linkpoint.ui.world.WorldViewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Login Activity - Entry point for the app
 * Based on Lumiya's LoginActivity structure
 */
class LoginActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "LoginActivity"
        private const val PREFS_NAME = "login_prefs"
        private const val KEY_LAST_FIRST = "last_first"
        private const val KEY_LAST_LAST = "last_last"
        private const val KEY_LAST_GRID = "last_grid"
        private const val KEY_SAVE_PASSWORD = "save_password"
    }
    
    private lateinit var firstNameEdit: EditText
    private lateinit var lastNameEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var gridSpinner: Spinner
    private lateinit var startLocationSpinner: Spinner
    private lateinit var savePasswordCheck: CheckBox
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    
    private val app by lazy { LinkpointApp.getInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        initViews()
        loadSavedCredentials()
        setupGridSpinner()
        setupListeners()
    }
    
    private fun initViews() {
        firstNameEdit = findViewById(R.id.editFirstName)
        lastNameEdit = findViewById(R.id.editLastName)
        passwordEdit = findViewById(R.id.editPassword)
        gridSpinner = findViewById(R.id.spinnerGrid)
        startLocationSpinner = findViewById(R.id.spinnerStartLocation)
        savePasswordCheck = findViewById(R.id.checkSavePassword)
        loginButton = findViewById(R.id.buttonLogin)
        progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.textStatus)
    }
    
    private fun loadSavedCredentials() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        firstNameEdit.setText(prefs.getString(KEY_LAST_FIRST, ""))
        lastNameEdit.setText(prefs.getString(KEY_LAST_LAST, ""))
        savePasswordCheck.isChecked = prefs.getBoolean(KEY_SAVE_PASSWORD, false)
    }
    
    private fun saveCredentials() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        prefs.putString(KEY_LAST_FIRST, firstNameEdit.text.toString())
        prefs.putString(KEY_LAST_LAST, lastNameEdit.text.toString())
        prefs.putString(KEY_LAST_GRID, app.gridManager.getSelectedGrid().id)
        prefs.putBoolean(KEY_SAVE_PASSWORD, savePasswordCheck.isChecked)
        prefs.apply()
    }
    
    private fun setupGridSpinner() {
        val grids = app.gridManager.getAvailableGrids()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            grids.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gridSpinner.adapter = adapter
        
        gridSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                app.gridManager.selectGrid(grids[position].id)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Setup start location
        val locations = listOf("Last Location", "Home", "Custom...")
        val locationAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            locations
        )
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startLocationSpinner.adapter = locationAdapter
    }
    
    private fun setupListeners() {
        loginButton.setOnClickListener {
            attemptLogin()
        }
    }
    
    private fun attemptLogin() {
        val firstName = firstNameEdit.text.toString().trim()
        val lastName = lastNameEdit.text.toString().trim()
        val password = passwordEdit.text.toString()
        
        // Validation
        if (firstName.isEmpty()) {
            firstNameEdit.error = "First name required"
            return
        }
        if (lastName.isEmpty()) {
            lastNameEdit.error = "Last name required"
            return
        }
        if (password.isEmpty()) {
            passwordEdit.error = "Password required"
            return
        }
        
        // Get start location
        val startLocation = when (startLocationSpinner.selectedItemPosition) {
            0 -> "last"
            1 -> "home"
            else -> "last"
        }
        
        // Save credentials
        saveCredentials()
        
        // Show progress
        setLoginInProgress(true)
        
        val grid = app.gridManager.getSelectedGrid()
        statusText.text = "Checking network..."
        
        // Perform network check and login
        lifecycleScope.launch {
            // Pre-flight network connectivity check
            val networkStatus = checkNetworkConnectivity()
            if (!networkStatus.isConnected) {
                handleLoginResult(LoginResult.Failure(
                    message = networkStatus.message,
                    errorCode = "NO_NETWORK",
                    technicalDetails = networkStatus.details
                ))
                return@launch
            }
            
            statusText.text = "Connecting to ${grid.name}..."
            Log.d(TAG, "Starting login to ${grid.loginUri}")
            
            // Test if we can reach the login server
            val serverReachable = testServerReachability(grid.loginUri)
            if (!serverReachable.success) {
                handleLoginResult(LoginResult.Failure(
                    message = serverReachable.message,
                    errorCode = "SERVER_UNREACHABLE",
                    technicalDetails = serverReachable.details
                ))
                return@launch
            }
            
            statusText.text = "Authenticating with ${grid.name}..."
            
            val result = app.protocol.login(
                firstName = firstName,
                lastName = lastName,
                password = password,
                loginUri = grid.loginUri,
                startLocation = startLocation
            )
            
            handleLoginResult(result)
        }
    }
    
    private data class NetworkStatus(
        val isConnected: Boolean,
        val message: String,
        val details: String,
        val networkType: NetworkDiagnostics.NetworkType = NetworkDiagnostics.NetworkType.UNKNOWN
    )
    
    private data class ServerTestResult(
        val success: Boolean,
        val message: String,
        val details: String
    )
    
    /**
     * Enhanced network connectivity check using NetworkDiagnostics
     * Provides detailed network information for better debugging
     */
    private fun checkNetworkConnectivity(): NetworkStatus {
        val networkInfo = NetworkDiagnostics.getNetworkInfo(this)
        
        if (!networkInfo.isConnected) {
            val message = when (networkInfo.type) {
                NetworkDiagnostics.NetworkType.NONE ->
                    "No network connection. Please connect to Wi-Fi or enable mobile data."
                NetworkDiagnostics.NetworkType.WIFI ->
                    "Connected to Wi-Fi but no internet access. Check your router connection."
                NetworkDiagnostics.NetworkType.CELLULAR_2G,
                NetworkDiagnostics.NetworkType.CELLULAR_3G,
                NetworkDiagnostics.NetworkType.CELLULAR_4G,
                NetworkDiagnostics.NetworkType.CELLULAR_LTE,
                NetworkDiagnostics.NetworkType.CELLULAR_5G ->
                    "Connected to ${networkInfo.displayName} but no internet access. Check your mobile data settings."
                else ->
                    "Network connection unavailable. Please check your network settings."
            }
            
            return NetworkStatus(
                isConnected = false,
                message = message,
                details = networkInfo.details,
                networkType = networkInfo.type
            )
        }
        
        // Warn about slow network connections
        val warningMessage = when (networkInfo.type) {
            NetworkDiagnostics.NetworkType.CELLULAR_2G ->
                "⚠️ Using 2G connection - login may be very slow. Wi-Fi recommended."
            NetworkDiagnostics.NetworkType.CELLULAR_3G ->
                "ℹ️ Using 3G connection - login may take longer than usual."
            else -> null
        }
        
        val statusMessage = buildString {
            append("Connected via ${networkInfo.displayName}")
            if (networkInfo.isMetered) {
                append(" (metered)")
            }
            warningMessage?.let { append("\n$it") }
        }
        
        Log.d(TAG, "Network check passed: ${networkInfo.displayName}, " +
            "bandwidth: ${networkInfo.estimatedBandwidthKbps}kbps, " +
            "timeout multiplier: ${networkInfo.timeoutMultiplier}x")
        
        return NetworkStatus(
            isConnected = true,
            message = statusMessage,
            details = networkInfo.details,
            networkType = networkInfo.type
        )
    }
    
    private suspend fun testServerReachability(loginUri: String): ServerTestResult = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(loginUri)
            
            // Get network info for adaptive timeout
            val networkInfo = NetworkDiagnostics.getNetworkInfo(this@LoginActivity)
            val timeoutMs = (10000 * networkInfo.timeoutMultiplier).toInt()
            
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = timeoutMs
            connection.readTimeout = timeoutMs
            connection.instanceFollowRedirects = true
            
            val startTime = System.currentTimeMillis()
            val responseCode = connection.responseCode
            val elapsed = System.currentTimeMillis() - startTime
            connection.disconnect()
            
            val details = buildString {
                appendLine("URL: $loginUri")
                appendLine("Response Code: $responseCode")
                appendLine("Response Time: ${elapsed}ms")
                appendLine("Network: ${networkInfo.displayName}")
                appendLine("Timeout Used: ${timeoutMs}ms")
            }
            
            // Accept any response - even 400/405 means server is reachable
            // The actual login will use POST which is what the server expects
            Log.d(TAG, "Server reachability test: $responseCode in ${elapsed}ms via ${networkInfo.displayName}")
            ServerTestResult(
                success = true,
                message = "Server reachable (${elapsed}ms)",
                details = details
            )
        } catch (e: java.net.UnknownHostException) {
            val serverHost = loginUri.substringAfter("://").substringBefore("/")
            ServerTestResult(
                success = false,
                message = "Cannot resolve '$serverHost'. Please check your internet connection.",
                details = "UnknownHostException: ${e.message}\n\nThis usually means:\n• No internet connection\n• DNS server issues\n• Firewall blocking DNS"
            )
        } catch (e: java.net.SocketTimeoutException) {
            val networkInfo = NetworkDiagnostics.getNetworkInfo(this@LoginActivity)
            val suggestion = when (networkInfo.type) {
                NetworkDiagnostics.NetworkType.CELLULAR_2G, 
                NetworkDiagnostics.NetworkType.CELLULAR_3G ->
                    "Your mobile connection is slow. Try using Wi-Fi."
                else ->
                    "The server may be busy. Please try again in a moment."
            }
            ServerTestResult(
                success = false,
                message = "Server is not responding. $suggestion",
                details = "SocketTimeoutException: ${e.message}\nURL: $loginUri\nNetwork: ${networkInfo.displayName}"
            )
        } catch (e: javax.net.ssl.SSLHandshakeException) {
            val sslDiag = SSLHelper.diagnoseSSLIssues(loginUri)
            val message = buildString {
                append("SSL handshake failed. ")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    append("On Android ${Build.VERSION.RELEASE}, this may require proper network security configuration. ")
                }
                append("If using VPN or proxy, try disabling them.")
            }
            ServerTestResult(
                success = false,
                message = message,
                details = buildString {
                    appendLine("SSLHandshakeException: ${e.message}")
                    appendLine()
                    append(sslDiag.toReport())
                }
            )
        } catch (e: javax.net.ssl.SSLException) {
            ServerTestResult(
                success = false,
                message = "Secure connection failed. Please check your network and try again.",
                details = "SSLException: ${e.message}\nURL: $loginUri\n\nPossible causes:\n• Unstable network\n• VPN/Proxy interference\n• Certificate issues"
            )
        } catch (e: java.net.ConnectException) {
            ServerTestResult(
                success = false,
                message = "Cannot connect to login server. The server may be down for maintenance.",
                details = "ConnectException: ${e.message}\nURL: $loginUri\n\nCheck status.secondlifegrid.net for server status."
            )
        } catch (e: java.io.EOFException) {
            // EOF can happen if server closes connection during handshake
            ServerTestResult(
                success = false,
                message = "Server closed connection unexpectedly. This is usually temporary - please try again.",
                details = "EOFException: ${e.message}\nURL: $loginUri\n\nThis can happen when:\n• Server is busy\n• Network interruption\n• HTTP/2 connection issues"
            )
        } catch (e: Exception) {
            ServerTestResult(
                success = false,
                message = "Failed to reach login server: ${e.message?.take(100) ?: "Unknown error"}",
                details = "${e.javaClass.simpleName}: ${e.message}\nURL: $loginUri"
            )
        }
    }
    
    private fun handleLoginResult(result: LoginResult) {
        setLoginInProgress(false)
        
        when (result) {
            is LoginResult.Success -> {
                statusText.text = "Login successful!"
                
                // Navigate to world view
                val intent = Intent(this, WorldViewActivity::class.java)
                startActivity(intent)
                finish()
            }
            is LoginResult.Failure -> {
                statusText.text = "Login failed"
                showLoginErrorDialog(result)
            }
        }
    }
    
    private fun showLoginErrorDialog(failure: LoginResult.Failure) {
        val errorCodeDisplay = failure.errorCode?.let { " [$it]" } ?: ""
        
        val builder = AlertDialog.Builder(this)
            .setTitle("Login Failed$errorCodeDisplay")
            .setMessage(failure.message)
            .setPositiveButton("OK", null)
        
        // Add "Details" button if technical details are available
        if (!failure.technicalDetails.isNullOrBlank()) {
            builder.setNeutralButton("View Details") { _, _ ->
                showTechnicalDetailsDialog(failure)
            }
        }
        
        // Add retry button for network errors
        if (failure.errorCode?.contains("NETWORK", ignoreCase = true) == true ||
            failure.errorCode?.contains("TIMEOUT", ignoreCase = true) == true ||
            failure.errorCode?.contains("CONNECTION", ignoreCase = true) == true ||
            failure.errorCode?.contains("EOF", ignoreCase = true) == true ||
            failure.errorCode?.contains("DNS", ignoreCase = true) == true ||
            failure.errorCode?.contains("SSL", ignoreCase = true) == true) {
            builder.setNegativeButton("Retry") { _, _ ->
                attemptLogin()
            }
        }
        
        builder.show()
    }
    
    private fun showTechnicalDetailsDialog(failure: LoginResult.Failure) {
        // Generate comprehensive diagnostic report
        val networkDiagReport = NetworkDiagnostics.generateDiagnosticReport(this)
        val sslInfo = buildString {
            appendLine("TLS Support: ${SSLHelper.getHighestSupportedTLSVersion()}")
            appendLine("TLS 1.3: ${if (SSLHelper.supportsTLS13()) "Supported" else "Not Supported"}")
        }
        
        val details = buildString {
            appendLine("=== Error Information ===")
            appendLine("Error Code: ${failure.errorCode ?: "UNKNOWN"}")
            appendLine("Message: ${failure.message}")
            appendLine()
            appendLine("=== Technical Details ===")
            appendLine(failure.technicalDetails ?: "No additional details available")
            appendLine()
            appendLine("=== Device Information ===")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Grid: ${app.gridManager.getSelectedGrid().name}")
            appendLine("Login URI: ${app.gridManager.getSelectedGrid().loginUri}")
            appendLine()
            appendLine("=== SSL/TLS Information ===")
            append(sslInfo)
            appendLine()
            append(networkDiagReport)
            appendLine()
            appendLine("=== Troubleshooting Tips ===")
            appendLine("1. Check your internet connection is stable")
            appendLine("2. Try switching between Wi-Fi and mobile data")
            appendLine("3. Disable VPN/Proxy if enabled")
            appendLine("4. Check status.secondlifegrid.net for server status")
            appendLine("5. Restart the app and try again")
        }
        
        AlertDialog.Builder(this)
            .setTitle("Technical Details")
            .setMessage(details)
            .setPositiveButton("OK", null)
            .setNeutralButton("Copy") { _, _ ->
                val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Login Error Details", details)
                clipboard.setPrimaryClip(clip)
                android.widget.Toast.makeText(this, "Details copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
            }
            .show()
    }
    
    private fun setLoginInProgress(inProgress: Boolean) {
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        loginButton.isEnabled = !inProgress
        firstNameEdit.isEnabled = !inProgress
        lastNameEdit.isEnabled = !inProgress
        passwordEdit.isEnabled = !inProgress
        gridSpinner.isEnabled = !inProgress
        startLocationSpinner.isEnabled = !inProgress
    }
}
