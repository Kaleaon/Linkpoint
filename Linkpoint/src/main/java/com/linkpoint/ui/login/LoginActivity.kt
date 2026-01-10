package com.linkpoint.ui.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
        val details: String
    )
    
    private data class ServerTestResult(
        val success: Boolean,
        val message: String,
        val details: String
    )
    
    private fun checkNetworkConnectivity(): NetworkStatus {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        
        if (network == null) {
            return NetworkStatus(
                isConnected = false,
                message = "No network connection. Please connect to Wi-Fi or enable mobile data.",
                details = "ConnectivityManager.activeNetwork returned null"
            )
        }
        
        val capabilities = cm.getNetworkCapabilities(network)
        if (capabilities == null) {
            return NetworkStatus(
                isConnected = false,
                message = "Network connection unavailable. Please check your network settings.",
                details = "NetworkCapabilities is null for active network"
            )
        }
        
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val hasCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val hasEthernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        
        val transportType = when {
            hasWifi -> "Wi-Fi"
            hasCellular -> "Mobile Data"
            hasEthernet -> "Ethernet"
            else -> "Unknown"
        }
        
        val details = buildString {
            appendLine("Transport: $transportType")
            appendLine("Has Internet: $hasInternet")
            appendLine("Has Validated: ${capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)}")
            appendLine("Has Not Metered: ${capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)}")
        }
        
        if (!hasInternet) {
            return NetworkStatus(
                isConnected = false,
                message = "Connected to $transportType but no internet access. Check your network configuration.",
                details = details
            )
        }
        
        Log.d(TAG, "Network check passed: $transportType connected with internet capability")
        return NetworkStatus(isConnected = true, message = "Connected via $transportType", details = details)
    }
    
    private suspend fun testServerReachability(loginUri: String): ServerTestResult = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(loginUri)
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.instanceFollowRedirects = true
            
            val startTime = System.currentTimeMillis()
            val responseCode = connection.responseCode
            val elapsed = System.currentTimeMillis() - startTime
            connection.disconnect()
            
            val details = buildString {
                appendLine("URL: $loginUri")
                appendLine("Response Code: $responseCode")
                appendLine("Response Time: ${elapsed}ms")
            }
            
            // Accept any response - even 400/405 means server is reachable
            // The actual login will use POST which is what the server expects
            Log.d(TAG, "Server reachability test: $responseCode in ${elapsed}ms")
            ServerTestResult(
                success = true,
                message = "Server reachable",
                details = details
            )
        } catch (e: java.net.UnknownHostException) {
            ServerTestResult(
                success = false,
                message = "Cannot resolve server address. DNS lookup failed for ${loginUri.substringAfter("://").substringBefore("/")}",
                details = "UnknownHostException: ${e.message}"
            )
        } catch (e: java.net.SocketTimeoutException) {
            ServerTestResult(
                success = false,
                message = "Server is not responding. Connection timed out.",
                details = "SocketTimeoutException: ${e.message}\nURL: $loginUri"
            )
        } catch (e: javax.net.ssl.SSLException) {
            ServerTestResult(
                success = false,
                message = "SSL/TLS error connecting to server. This may be a certificate or network issue.",
                details = "SSLException: ${e.message}\nURL: $loginUri"
            )
        } catch (e: java.net.ConnectException) {
            ServerTestResult(
                success = false,
                message = "Cannot connect to login server. Connection refused or server may be down.",
                details = "ConnectException: ${e.message}\nURL: $loginUri"
            )
        } catch (e: Exception) {
            ServerTestResult(
                success = false,
                message = "Failed to reach login server: ${e.message}",
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
            failure.errorCode?.contains("DNS", ignoreCase = true) == true ||
            failure.errorCode?.contains("SSL", ignoreCase = true) == true) {
            builder.setNegativeButton("Retry") { _, _ ->
                attemptLogin()
            }
        }
        
        builder.show()
    }
    
    private fun showTechnicalDetailsDialog(failure: LoginResult.Failure) {
        val details = buildString {
            appendLine("Error Code: ${failure.errorCode ?: "UNKNOWN"}")
            appendLine()
            appendLine("Message: ${failure.message}")
            appendLine()
            appendLine("--- Technical Details ---")
            appendLine(failure.technicalDetails ?: "No additional details available")
            appendLine()
            appendLine("--- Device Info ---")
            appendLine("Android: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
            appendLine("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
            appendLine("Grid: ${app.gridManager.getSelectedGrid().name}")
            appendLine("Login URI: ${app.gridManager.getSelectedGrid().loginUri}")
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
