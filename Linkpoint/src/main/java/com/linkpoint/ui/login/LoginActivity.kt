package com.linkpoint.ui.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.core.GridInfo
import com.linkpoint.core.StartLocationOption
import com.linkpoint.core.StartLocationType
import com.linkpoint.network.LoginResult
import com.linkpoint.network.NetworkDiagnostics
import com.linkpoint.network.NetworkExceptionUtils.ErrorCategory
import com.linkpoint.network.NetworkLogger
import com.linkpoint.network.SSLHelper
import com.linkpoint.ui.settings.SettingsActivity
import com.linkpoint.ui.tos.TosActivity
import com.linkpoint.ui.world.WorldViewActivity
import com.linkpoint.utils.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyStore
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Login Activity - Entry point for the app
 * 
 * Based on Lumiya's LoginActivity structure with enhancements:
 * - Terms of Service acceptance check before first login
 * - Secure password storage with encryption (like Lumiya)
 * - Quick login with saved credentials
 * - Better network error handling
 * - Start location selection with cached landmarks and themed destinations
 * - Avatar selection options
 */
class LoginActivity : AppCompatActivity(), StartLocationDialog.StartLocationListener {
    
    companion object {
        private const val TAG = "LoginActivity"
        private const val PREFS_NAME = "login_prefs"
        private const val KEY_LAST_FIRST = "last_first"
        private const val KEY_LAST_LAST = "last_last"
        private const val KEY_LAST_GRID = "last_grid"
        private const val KEY_SAVE_PASSWORD = "save_password"
        private const val KEY_ENCRYPTED_PASSWORD = "encrypted_password"
        private const val KEY_PASSWORD_IV = "password_iv"
        private const val KEY_STORAGE_PERMISSION_REQUESTED = "storage_permission_requested"
        private const val KEY_ALL_PERMISSIONS_REQUESTED = "all_permissions_requested"
        
        // Android Keystore alias for password encryption
        private const val KEYSTORE_ALIAS = "LinkpointLoginKey"
        
        // MFA TOTP code length (standard is 6 digits)
        private const val MFA_CODE_LENGTH = 6
    }
    
    private lateinit var firstNameEdit: EditText
    private lateinit var lastNameEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var gridSpinner: Spinner
    private lateinit var startLocationSpinner: Spinner
    private lateinit var startLocationButton: Button
    private lateinit var savePasswordCheck: CheckBox
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var btnSettings: ImageButton
    
    // Current start location display text
    private var currentStartLocationText: String = "Last Location"
    
    private val app by lazy { LinkpointApp.getInstance() }
    
    // Permission manager for handling runtime permissions
    private lateinit var permissionManager: PermissionManager
    
    // ToS acceptance result handler
    private val tosLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // ToS accepted, proceed with login
            statusText.text = "Terms accepted. Ready to login."
        } else {
            // ToS declined, close app
            finishAffinity()
        }
    }
    
    // Storage permission launcher for log file saving
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            Log.i(TAG, "Storage permissions granted - logs will be saved to Documents/Linkpoint Logs/")
            Toast.makeText(
                this,
                "Network logs will be saved to Documents/Linkpoint Logs/",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Log.w(TAG, "Storage permissions denied - logs will only be in logcat")
            Toast.makeText(
                this,
                "Log saving disabled - grant storage permission in settings to enable",
                Toast.LENGTH_LONG
            ).show()
        }
        
        // Mark that we've requested permission
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
            .putBoolean(KEY_STORAGE_PERMISSION_REQUESTED, true)
            .apply()
    }
    
    // All permissions launcher for startup
    private val allPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handleAllPermissionsResult(permissions)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        initViews()
        
        // Initialize permission manager
        permissionManager = PermissionManager(this)
        permissionManager.registerPermissionLauncher(allPermissionsLauncher)
        
        // Check ToS acceptance first (like Lumiya does)
        if (!TosActivity.hasAcceptedTos(this)) {
            // Show ToS activity
            tosLauncher.launch(TosActivity.createIntent(this, requireAcceptance = true))
        } else {
            // ToS already accepted, load credentials
            loadSavedCredentials()
        }
        
        setupGridSpinner()
        setupListeners()
        
        // Request all essential permissions on startup after UI is fully initialized
        requestAllStartupPermissions()
    }
    
    /**
     * Handle results from all permissions request
     */
    private fun handleAllPermissionsResult(permissions: Map<String, Boolean>) {
        val granted = permissions.filter { it.value }.keys
        val denied = permissions.filter { !it.value }.keys
        
        if (granted.isNotEmpty()) {
            Log.i(TAG, "Permissions granted: ${granted.joinToString()}")
        }
        
        if (denied.isNotEmpty()) {
            Log.w(TAG, "Permissions denied: ${denied.joinToString()}")
            
            // Show a user-friendly message about denied permissions
            val deniedNames = denied.map { permissionManager.getPermissionDisplayName(it) }.distinct()
            if (deniedNames.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Some features may be limited. Denied: ${deniedNames.joinToString(", ")}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        
        // Mark that we've requested permissions
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
            .putBoolean(KEY_ALL_PERMISSIONS_REQUESTED, true)
            .apply()
    }
    
    /**
     * Request all essential permissions on startup.
     * This includes storage (for logs/cache), notifications, microphone, and Bluetooth.
     */
    private fun requestAllStartupPermissions() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ALL_PERMISSIONS_REQUESTED, false)) {
            Log.i(TAG, "Startup permissions already requested")
            return
        }
        
        // Use PermissionManager to get all permissions we need
        val allPermissions = PermissionManager.getAllPermissions()
        
        // Filter out already granted permissions
        val permissionsToRequest = allPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isEmpty()) {
            Log.i(TAG, "All startup permissions already granted")
            prefs.edit().putBoolean(KEY_ALL_PERMISSIONS_REQUESTED, true).apply()
            return
        }
        
        Log.i(TAG, "Requesting startup permissions: ${permissionsToRequest.joinToString()}")
        allPermissionsLauncher.launch(permissionsToRequest)
    }
    
    /**
     * Request storage permissions for log file saving.
     * Only requests once per install to avoid annoying users.
     */
    private fun requestStoragePermissionsIfNeeded() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (prefs.getBoolean(KEY_STORAGE_PERMISSION_REQUESTED, false)) {
            return // Already requested
        }
        
        // For Android 13+ (API 33+), we don't need WRITE_EXTERNAL_STORAGE
        // We use app-specific directory which doesn't require permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.i(TAG, "Android 13+: Using app-specific directory, no permissions needed")
            Toast.makeText(
                this,
                "Network logs will be saved to Documents/Linkpoint Logs/",
                Toast.LENGTH_SHORT
            ).show()
            prefs.edit().putBoolean(KEY_STORAGE_PERMISSION_REQUESTED, true).apply()
            return
        }
        
        // For Android 10-12 (API 29-32)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // No permission needed for app-specific directories
            Log.i(TAG, "Android 10-12: Using app-specific directory, no permissions needed")
            Toast.makeText(
                this,
                "Network logs will be saved to Documents/Linkpoint Logs/",
                Toast.LENGTH_SHORT
            ).show()
            prefs.edit().putBoolean(KEY_STORAGE_PERMISSION_REQUESTED, true).apply()
            return
        }
        
        // For Android 9 and below, request WRITE_EXTERNAL_STORAGE
        val permissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            emptyArray()
        }
        
        if (permissions.isNotEmpty()) {
            val needsRequest = permissions.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            
            if (needsRequest) {
                Log.i(TAG, "Requesting storage permissions for log file saving")
                storagePermissionLauncher.launch(permissions)
            } else {
                Log.i(TAG, "Storage permissions already granted")
                prefs.edit().putBoolean(KEY_STORAGE_PERMISSION_REQUESTED, true).apply()
            }
        }
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
        
        // Settings button - accessible before login
        btnSettings = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener {
            openSettings()
        }
    }
    
    /**
     * Open Settings activity. Can be accessed before login.
     */
    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
    
    private fun loadSavedCredentials() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        firstNameEdit.setText(prefs.getString(KEY_LAST_FIRST, ""))
        lastNameEdit.setText(prefs.getString(KEY_LAST_LAST, ""))
        savePasswordCheck.isChecked = prefs.getBoolean(KEY_SAVE_PASSWORD, false)
        
        // Load saved password if user opted to save it
        if (savePasswordCheck.isChecked) {
            loadSavedPassword()?.let { password ->
                passwordEdit.setText(password)
                statusText.text = "Credentials loaded. Tap Login to connect."
            }
        }
    }
    
    /**
     * Save credentials including encrypted password (like Lumiya)
     */
    private fun saveCredentials() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        prefs.putString(KEY_LAST_FIRST, firstNameEdit.text.toString())
        prefs.putString(KEY_LAST_LAST, lastNameEdit.text.toString())
        prefs.putString(KEY_LAST_GRID, app.gridManager.getSelectedGrid().id)
        prefs.putBoolean(KEY_SAVE_PASSWORD, savePasswordCheck.isChecked)
        
        // Save encrypted password if user opted to save it
        if (savePasswordCheck.isChecked) {
            savePasswordSecurely(passwordEdit.text.toString())
        } else {
            // Clear saved password if user unchecked save option
            clearSavedPassword()
        }
        
        prefs.apply()
    }
    
    /**
     * Encrypt and save password using Android Keystore
     * This is how Lumiya securely stores passwords
     */
    private fun savePasswordSecurely(password: String) {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            
            // Generate or get existing key
            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore"
                )
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(
                        KEYSTORE_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
                keyGenerator.generateKey()
            }
            
            val secretKey = keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val encryptedBytes = cipher.doFinal(password.toByteArray(Charsets.UTF_8))
            val iv = cipher.iv
            
            // Store encrypted password and IV
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
            prefs.putString(KEY_ENCRYPTED_PASSWORD, Base64.encodeToString(encryptedBytes, Base64.DEFAULT))
            prefs.putString(KEY_PASSWORD_IV, Base64.encodeToString(iv, Base64.DEFAULT))
            prefs.apply()
            
            Log.d(TAG, "Password saved securely")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save password securely: ${e.message}")
            // Notify user that password wasn't saved
            android.widget.Toast.makeText(
                this,
                "Could not save password securely. You'll need to enter it next time.",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    /**
     * Load and decrypt saved password
     */
    private fun loadSavedPassword(): String? {
        return try {
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val encryptedBase64 = prefs.getString(KEY_ENCRYPTED_PASSWORD, null) ?: return null
            val ivBase64 = prefs.getString(KEY_PASSWORD_IV, null) ?: return null
            
            val encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT)
            val iv = Base64.decode(ivBase64, Base64.DEFAULT)
            
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            
            val secretKey = keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load saved password: ${e.message}")
            null
        }
    }
    
    /**
     * Clear saved password
     */
    private fun clearSavedPassword() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        prefs.remove(KEY_ENCRYPTED_PASSWORD)
        prefs.remove(KEY_PASSWORD_IV)
        prefs.apply()
        
        // Optionally remove the key from keystore
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
                keyStore.deleteEntry(KEYSTORE_ALIAS)
            }
        } catch (e: Exception) {
            // Ignore - key cleanup is not critical
        }
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
        
        // Restore last used grid
        val lastGrid = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_LAST_GRID, null)
        if (lastGrid != null) {
            val gridIndex = grids.indexOfFirst { it.id == lastGrid }
            if (gridIndex >= 0) {
                gridSpinner.setSelection(gridIndex)
            }
        }
        
        // Setup start location with enhanced selection
        setupStartLocationSelection()
    }
    
    /**
     * Setup enhanced start location selection.
     * Uses StartLocationManager for cached landmarks, favorites, and destinations.
     */
    private fun setupStartLocationSelection() {
        // Create list of basic options + cached landmarks for quick selection
        val options = mutableListOf<String>()
        options.add("Last Location")
        options.add("Home")
        
        // Add cached landmarks (if any)
        val cachedLandmarks = app.startLocationManager.cachedLandmarks.value
        if (cachedLandmarks.isNotEmpty()) {
            cachedLandmarks.take(3).forEach { lm ->
                options.add(lm.name)
            }
        }
        
        // Add "More..." option to open full dialog
        options.add("More Locations...")
        
        val locationAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            options
        )
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startLocationSpinner.adapter = locationAdapter
        
        // Restore last selected option
        when (app.startLocationManager.selectedOption.value) {
            StartLocationType.LAST_LOCATION -> startLocationSpinner.setSelection(0)
            StartLocationType.HOME -> startLocationSpinner.setSelection(1)
            StartLocationType.LANDMARK -> {
                val selectedLandmark = app.startLocationManager.selectedLandmark.value
                if (selectedLandmark != null) {
                    val index = cachedLandmarks.indexOfFirst { it.inventoryItemId == selectedLandmark.inventoryItemId }
                    if (index >= 0 && index < 3) {
                        startLocationSpinner.setSelection(index + 2)
                    }
                }
            }
            else -> startLocationSpinner.setSelection(0)
        }
        
        startLocationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when {
                    position == 0 -> {
                        // Last Location
                        app.startLocationManager.selectOption(StartLocationOption(
                            type = StartLocationType.LAST_LOCATION,
                            displayName = "Last Location",
                            description = "Return to where you logged off",
                            iconType = com.linkpoint.core.StartLocationIconType.HISTORY
                        ))
                        currentStartLocationText = "Last Location"
                    }
                    position == 1 -> {
                        // Home
                        app.startLocationManager.selectOption(StartLocationOption(
                            type = StartLocationType.HOME,
                            displayName = "Home",
                            description = "Teleport to your home location",
                            iconType = com.linkpoint.core.StartLocationIconType.HOME
                        ))
                        currentStartLocationText = "Home"
                    }
                    position == options.size - 1 -> {
                        // "More Locations..." - show dialog
                        showStartLocationDialog()
                        // Reset to previous selection
                        startLocationSpinner.setSelection(0)
                    }
                    else -> {
                        // Cached landmark
                        val landmarkIndex = position - 2
                        if (landmarkIndex in cachedLandmarks.indices) {
                            val landmark = cachedLandmarks[landmarkIndex]
                            app.startLocationManager.selectOption(StartLocationOption(
                                type = StartLocationType.LANDMARK,
                                displayName = landmark.name,
                                description = landmark.regionName,
                                iconType = com.linkpoint.core.StartLocationIconType.LANDMARK,
                                slurl = landmark.toSLURL(),
                                landmarkId = landmark.inventoryItemId
                            ))
                            currentStartLocationText = landmark.name
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Observe changes to update UI
        lifecycleScope.launch {
            app.startLocationManager.selectedOption.collectLatest { type ->
                Log.d(TAG, "Start location type changed to: $type")
            }
        }
    }
    
    /**
     * Show the full start location selection dialog.
     */
    private fun showStartLocationDialog() {
        StartLocationDialog.newInstance().show(supportFragmentManager, StartLocationDialog.TAG)
    }
    
    // StartLocationDialog.StartLocationListener implementation
    override fun onStartLocationSelected(option: StartLocationOption) {
        app.startLocationManager.selectOption(option)
        currentStartLocationText = option.displayName
        
        // Update spinner to show selected (or reset to "Last Location" if not in quick list)
        when (option.type) {
            StartLocationType.LAST_LOCATION -> startLocationSpinner.setSelection(0)
            StartLocationType.HOME -> startLocationSpinner.setSelection(1)
            else -> {
                // For other types, we show in status instead
                statusText.text = getString(R.string.start_at, option.displayName)
            }
        }
    }
    
    override fun onCustomSLURLEntered(slurl: String) {
        app.startLocationManager.setCustomSLURL(slurl)
        currentStartLocationText = "Custom: $slurl"
        statusText.text = getString(R.string.start_at, slurl)
    }
    
    private fun setupListeners() {
        loginButton.setOnClickListener {
            // Check ToS before login
            if (!TosActivity.hasAcceptedTos(this)) {
                tosLauncher.launch(TosActivity.createIntent(this, requireAcceptance = true))
                return@setOnClickListener
            }
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
        
        // Get start location from StartLocationManager
        val startLocation = app.startLocationManager.getStartLocationForLogin()
        Log.d(TAG, "Start location for login: $startLocation")
        
        // Save credentials
        saveCredentials()
        
        // Show progress
        setLoginInProgress(true)
        
        val grid = app.gridManager.getSelectedGrid()
        statusText.text = "Checking network..."
        
        // Perform network check and login
        lifecycleScope.launch {
            // Quick network connectivity check (doesn't block on slow networks)
            val networkStatus = checkNetworkConnectivity()
            if (!networkStatus.isConnected) {
                handleLoginResult(LoginResult.Failure(
                    message = networkStatus.message,
                    errorCode = "NO_NETWORK",
                    technicalDetails = networkStatus.details
                ))
                return@launch
            }
            
            // OPTIMIZATION: Skip server reachability test - go straight to login
            // The old code did a HEAD request before POST login, which added latency.
            // Lumiya goes straight to login, which is why it's instant.
            // The login request itself will fail with appropriate errors if server is down.
            statusText.text = "Logging into ${grid.name}..."
            Log.d(TAG, "Starting login to ${grid.loginUri} via ${networkStatus.networkType}")
            
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
                
                // Mark first login complete for landmark caching
                if (!app.startLocationManager.isFirstLoginComplete()) {
                    app.startLocationManager.markFirstLoginComplete()
                    Log.i(TAG, "First login complete - landmark caching enabled")
                }
                
                // Navigate to world view
                val intent = Intent(this, WorldViewActivity::class.java)
                startActivity(intent)
                finish()
            }
            is LoginResult.MFARequired -> {
                statusText.text = "MFA Required"
                showMFADialog(result)
            }
            is LoginResult.Failure -> {
                statusText.text = "Login failed"
                showLoginErrorDialog(result)
            }
        }
    }
    
    /**
     * Show dialog for Multi-Factor Authentication code entry.
     */
    private fun showMFADialog(mfaResult: LoginResult.MFARequired) {
        val editText = android.widget.EditText(this).apply {
            hint = "Enter authenticator code"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            maxLines = 1
            filters = arrayOf(android.text.InputFilter.LengthFilter(MFA_CODE_LENGTH))
        }
        
        val builder = AlertDialog.Builder(this)
            .setTitle("Two-Factor Authentication")
            .setMessage(mfaResult.message)
            .setView(editText)
            .setPositiveButton("Submit") { _, _ ->
                val code = editText.text.toString().trim()
                if (code.length == MFA_CODE_LENGTH) {
                    attemptLoginWithMFA(code)
                } else {
                    Toast.makeText(this, "Please enter a $MFA_CODE_LENGTH-digit code", Toast.LENGTH_SHORT).show()
                    showMFADialog(mfaResult) // Show again
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                statusText.text = "Login cancelled"
            }
        
        builder.show()
    }
    
    /**
     * Retry login with MFA token.
     */
    private fun attemptLoginWithMFA(mfaToken: String) {
        setLoginInProgress(true)
        statusText.text = "Verifying authentication code..."
        
        lifecycleScope.launch {
            try {
                val firstName = firstNameEdit.text.toString().trim()
                val lastName = lastNameEdit.text.toString().trim()
                val password = passwordEdit.text.toString()
                val grid = app.gridManager.getSelectedGrid()
                val startLocation = when (startLocationSpinner.selectedItemPosition) {
                    0 -> "last"
                    1 -> "home"
                    else -> "last"
                }
                
                val result = app.protocol.login(
                    firstName = firstName,
                    lastName = lastName,
                    password = password,
                    loginUri = grid.loginUri,
                    startLocation = startLocation,
                    mfaToken = mfaToken
                )
                
                handleLoginResult(result)
            } catch (e: Exception) {
                handleLoginResult(LoginResult.Failure(
                    message = "MFA verification failed: ${e.message}",
                    errorCode = "MFA_ERROR"
                ))
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
        
        val grid = app.gridManager.getSelectedGrid()
        val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"))
        } else {
            @Suppress("SimpleDateFormat")
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", java.util.Locale.US).format(java.util.Date())
        }
        
        // Build display details (shown in dialog)
        val displayDetails = buildString {
            appendLine("=== Error Information ===")
            appendLine("Error Code: ${failure.errorCode ?: "UNKNOWN"}")
            appendLine("Category: ${failure.category}")
            appendLine("Message: ${failure.message}")
            appendLine("Is Transient: ${failure.isTransient}")
            appendLine()
            
            // Show attempt and timing information
            appendLine("=== Request Statistics ===")
            appendLine("Attempts Made: ${failure.attemptsMade}")
            appendLine("Total Time: ${failure.elapsedTimeMs}ms")
            appendLine()
            
            // Show root cause information if available
            if (failure.rootCauseType != null) {
                appendLine("=== Root Cause ===")
                appendLine("Type: ${failure.rootCauseType}")
                appendLine("Message: ${failure.rootCauseMessage ?: "(no message)"}")
                appendLine()
            }
            
            // Show exception chain if available
            if (!failure.exceptionChain.isNullOrBlank()) {
                appendLine("=== Exception Chain ===")
                appendLine(failure.exceptionChain)
                appendLine()
            }
            
            appendLine("=== Technical Details ===")
            appendLine(failure.technicalDetails ?: "No additional details available")
            appendLine()
            appendLine("=== Device Information ===")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Grid: ${grid.name}")
            appendLine("Login URI: ${grid.loginUri}")
            appendLine()
            appendLine("=== SSL/TLS Information ===")
            append(sslInfo)
            appendLine()
            append(networkDiagReport)
            appendLine()
            
            // Show dynamic recommendations if available, otherwise use defaults
            appendLine("=== Recommended Actions ===")
            if (failure.recommendations.isNotEmpty()) {
                failure.recommendations.forEachIndexed { idx, rec ->
                    appendLine("${idx + 1}. $rec")
                }
            } else {
                appendLine("1. Check your internet connection is stable")
                appendLine("2. Try switching between Wi-Fi and mobile data")
                appendLine("3. Disable VPN/Proxy if enabled")
                appendLine("4. Check status.secondlifegrid.net for server status")
                appendLine("5. Restart the app and try again")
            }
        }
        
        // Build comprehensive copy report (more detailed for support/debugging)
        val fullCopyReport = buildComprehensiveErrorReport(failure, grid, networkDiagReport, sslInfo, timestamp)
        
        AlertDialog.Builder(this)
            .setTitle("Technical Details")
            .setMessage(displayDetails)
            .setPositiveButton("OK", null)
            .setNeutralButton("Copy Full Report") { _, _ ->
                val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Linkpoint Login Error Report", fullCopyReport)
                clipboard.setPrimaryClip(clip)
                android.widget.Toast.makeText(this, "Full error report copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
            }
            .show()
    }
    
    /**
     * Build a comprehensive error report for copying/sharing.
     * This includes everything that happened and what went wrong with the connection.
     */
    private fun buildComprehensiveErrorReport(
        failure: LoginResult.Failure,
        grid: GridInfo,
        networkDiagReport: String,
        sslInfo: String,
        timestamp: String
    ): String = buildString {
        appendLine("╔══════════════════════════════════════════════════════════════════╗")
        appendLine("║           LINKPOINT LOGIN ERROR REPORT                           ║")
        appendLine("╚══════════════════════════════════════════════════════════════════╝")
        appendLine()
        appendLine("Report Generated: $timestamp")
        appendLine("App Version: ${com.linkpoint.BuildConfig.VERSION_NAME} (${com.linkpoint.BuildConfig.VERSION_CODE})")
        appendLine()
        
        appendLine("┌──────────────────────────────────────────────────────────────────┐")
        appendLine("│ WHAT HAPPENED - CONNECTION TIMELINE                              │")
        appendLine("└──────────────────────────────────────────────────────────────────┘")
        appendLine()
        appendLine("1. Login attempt started")
        appendLine("   → Target: ${grid.name}")
        appendLine("   → URI: ${grid.loginUri}")
        appendLine()
        appendLine("2. Connection attempts: ${failure.attemptsMade}")
        appendLine("   → Total time spent: ${failure.elapsedTimeMs}ms (${failure.elapsedTimeMs / 1000.0}s)")
        if (failure.attemptsMade > 1) {
            appendLine("   → Multiple retry attempts were made with exponential backoff")
        }
        appendLine()
        appendLine("3. Final result: FAILED")
        appendLine("   → Error Category: ${failure.category}")
        appendLine("   → Error Code: ${failure.errorCode ?: "UNKNOWN"}")
        appendLine("   → Is Transient (temporary): ${failure.isTransient}")
        appendLine()
        
        appendLine("┌──────────────────────────────────────────────────────────────────┐")
        appendLine("│ WHAT WENT WRONG - ERROR DETAILS                                  │")
        appendLine("└──────────────────────────────────────────────────────────────────┘")
        appendLine()
        appendLine("Error Message:")
        appendLine("  ${failure.message}")
        appendLine()
        
        if (failure.rootCauseType != null) {
            appendLine("Root Cause Exception:")
            appendLine("  Type: ${failure.rootCauseType}")
            appendLine("  Message: ${failure.rootCauseMessage ?: "(no message)"}")
            appendLine()
        }
        
        if (!failure.exceptionChain.isNullOrBlank()) {
            appendLine("Full Exception Chain (from outermost to root cause):")
            failure.exceptionChain.lines().forEach { line ->
                appendLine("  $line")
            }
            appendLine()
        }
        
        appendLine("Error Category Explanation:")
        when (failure.category) {
            ErrorCategory.CONNECTION_CLOSED -> {
                appendLine("  The server closed the connection before completing the response.")
                appendLine("  This typically indicates:")
                appendLine("    • Server is overloaded and dropping connections")
                appendLine("    • Load balancer timeout or reset")
                appendLine("    • Network interruption during data transfer")
                appendLine("    • SSL/TLS handshake was interrupted")
                appendLine("    • HTTP/2 protocol negotiation failed")
            }
            ErrorCategory.SSL_ERROR -> {
                appendLine("  A secure connection (SSL/TLS) could not be established.")
                appendLine("  This typically indicates:")
                appendLine("    • Certificate validation failed")
                appendLine("    • TLS version mismatch between client and server")
                appendLine("    • VPN or proxy interfering with secure connection")
                appendLine("    • Network security policy blocking the connection")
            }
            ErrorCategory.TIMEOUT -> {
                appendLine("  The connection or request timed out.")
                appendLine("  This typically indicates:")
                appendLine("    • Server is not responding")
                appendLine("    • Network is too slow or congested")
                appendLine("    • Firewall is blocking the connection")
                appendLine("    • Server is under heavy load")
            }
            ErrorCategory.DNS_ERROR -> {
                appendLine("  Could not resolve the server hostname.")
                appendLine("  This typically indicates:")
                appendLine("    • No internet connection")
                appendLine("    • DNS server is not responding")
                appendLine("    • The hostname is incorrect")
                appendLine("    • DNS is being blocked by network policy")
            }
            ErrorCategory.CONNECTION_REFUSED -> {
                appendLine("  The server refused the connection.")
                appendLine("  This typically indicates:")
                appendLine("    • Server is down or not accepting connections")
                appendLine("    • Wrong port or address")
                appendLine("    • Firewall blocking the connection")
                appendLine("    • Server maintenance in progress")
            }
            ErrorCategory.SOCKET_ERROR -> {
                appendLine("  A low-level socket error occurred.")
                appendLine("  This typically indicates:")
                appendLine("    • Network interface issues")
                appendLine("    • Connection was forcibly closed")
                appendLine("    • Resource exhaustion")
            }
            ErrorCategory.IO_ERROR -> {
                appendLine("  An I/O error occurred during communication.")
                appendLine("  This typically indicates:")
                appendLine("    • Data corruption during transfer")
                appendLine("    • Unexpected response format")
                appendLine("    • Stream was interrupted")
            }
            ErrorCategory.UNKNOWN -> {
                appendLine("  An unexpected error occurred.")
                appendLine("  The error could not be classified into a known category.")
            }
        }
        appendLine()
        
        if (!failure.technicalDetails.isNullOrBlank()) {
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ DETAILED TECHNICAL INFORMATION                                   │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            failure.technicalDetails.lines().forEach { line ->
                appendLine(line)
            }
            appendLine()
        }
        
        appendLine("┌──────────────────────────────────────────────────────────────────┐")
        appendLine("│ DEVICE & ENVIRONMENT                                             │")
        appendLine("└──────────────────────────────────────────────────────────────────┘")
        appendLine()
        appendLine("Device Information:")
        appendLine("  Manufacturer: ${Build.MANUFACTURER}")
        appendLine("  Model: ${Build.MODEL}")
        appendLine("  Product: ${Build.PRODUCT}")
        appendLine("  Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        appendLine("  Build: ${Build.DISPLAY}")
        appendLine()
        appendLine("Target Grid:")
        appendLine("  Name: ${grid.name}")
        appendLine("  Login URI: ${grid.loginUri}")
        appendLine()
        appendLine("SSL/TLS Capabilities:")
        sslInfo.lines().forEach { line ->
            if (line.isNotBlank()) appendLine("  $line")
        }
        appendLine()
        
        appendLine("┌──────────────────────────────────────────────────────────────────┐")
        appendLine("│ NETWORK DIAGNOSTICS AT TIME OF ERROR                             │")
        appendLine("└──────────────────────────────────────────────────────────────────┘")
        appendLine()
        networkDiagReport.lines().forEach { line ->
            appendLine(line)
        }
        appendLine()
        
        appendLine("┌──────────────────────────────────────────────────────────────────┐")
        appendLine("│ RECOMMENDED ACTIONS                                              │")
        appendLine("└──────────────────────────────────────────────────────────────────┘")
        appendLine()
        if (failure.recommendations.isNotEmpty()) {
            failure.recommendations.forEachIndexed { idx, rec ->
                appendLine("${idx + 1}. $rec")
            }
        } else {
            appendLine("1. Check your internet connection is stable")
            appendLine("2. Try switching between Wi-Fi and mobile data")
            appendLine("3. Disable VPN/Proxy if enabled")
            appendLine("4. Check status.secondlifegrid.net for server status")
            appendLine("5. Restart the app and try again")
        }
        appendLine()
        
        if (failure.isTransient) {
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ NOTE: TRANSIENT ERROR                                            │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            appendLine("This error appears to be temporary. The connection issue may resolve")
            appendLine("on its own. Please wait a moment and try again.")
            appendLine()
        }
        
        appendLine("═══════════════════════════════════════════════════════════════════")
        appendLine("End of Linkpoint Login Error Report")
        appendLine("═══════════════════════════════════════════════════════════════════")
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
