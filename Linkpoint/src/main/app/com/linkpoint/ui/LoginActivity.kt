package com.linkpoint.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.linkpoint.R
import com.linkpoint.network.SecondLifeConnection
import com.linkpoint.ui.tos.TosActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Login activity for Second Life authentication
 * 
 * Features (similar to Lumiya):
 * - Terms of Service acceptance check before first login
 * - Secure password storage with Android Keystore encryption
 * - Quick login with saved credentials
 * - Better network error handling
 */
class LoginActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "LoginActivity"
        private const val PREFS_NAME = "login_prefs"
        private const val KEY_LAST_FIRST = "last_first"
        private const val KEY_LAST_LAST = "last_last"
        private const val KEY_LAST_GRID = "last_grid"
        private const val KEY_SAVE_PASSWORD = "save_password"
        private const val KEY_ENCRYPTED_PASSWORD = "encrypted_password"
        private const val KEY_PASSWORD_IV = "password_iv"
        private const val KEYSTORE_ALIAS = "LinkpointLoginKey"
    }
    
    private lateinit var firstNameInput: TextInputEditText
    private lateinit var lastNameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var gridSpinner: TextView
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private var savePasswordCheck: CheckBox? = null
    
    private val connection = SecondLifeConnection()
    
    // ToS acceptance result handler
    private val tosLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // ToS accepted, proceed
            statusText.text = "Terms accepted. Ready to login."
            loadSavedCredentials()
        } else {
            // ToS declined, close app
            finishAffinity()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        initViews()
        
        // Check ToS acceptance first (like Lumiya does)
        if (!TosActivity.hasAcceptedTos(this)) {
            tosLauncher.launch(TosActivity.createIntent(this, requireAcceptance = true))
        } else {
            loadSavedCredentials()
        }
        
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
        savePasswordCheck = findViewById(R.id.savePasswordCheck) // May be null if not in layout
        
        // Set default grid
        gridSpinner.text = "Second Life"
    }
    
    private fun loadSavedCredentials() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        firstNameInput.setText(prefs.getString(KEY_LAST_FIRST, ""))
        lastNameInput.setText(prefs.getString(KEY_LAST_LAST, ""))
        
        val savedGrid = prefs.getString(KEY_LAST_GRID, "Second Life") ?: "Second Life"
        gridSpinner.text = savedGrid
        
        val shouldSavePassword = prefs.getBoolean(KEY_SAVE_PASSWORD, false)
        savePasswordCheck?.isChecked = shouldSavePassword
        
        // Load saved password if user opted to save it
        if (shouldSavePassword) {
            loadSavedPassword()?.let { password ->
                passwordInput.setText(password)
                statusText.text = "Credentials loaded. Tap Login to connect."
            }
        }
    }
    
    private fun saveCredentials() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        prefs.putString(KEY_LAST_FIRST, firstNameInput.text.toString())
        prefs.putString(KEY_LAST_LAST, lastNameInput.text.toString())
        prefs.putString(KEY_LAST_GRID, gridSpinner.text.toString())
        
        val shouldSavePassword = savePasswordCheck?.isChecked ?: false
        prefs.putBoolean(KEY_SAVE_PASSWORD, shouldSavePassword)
        
        if (shouldSavePassword) {
            savePasswordSecurely(passwordInput.text.toString())
        } else {
            clearSavedPassword()
        }
        
        prefs.apply()
    }
    
    /**
     * Encrypt and save password using Android Keystore (like Lumiya)
     */
    private fun savePasswordSecurely(password: String) {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            
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
            
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
            prefs.putString(KEY_ENCRYPTED_PASSWORD, Base64.encodeToString(encryptedBytes, Base64.DEFAULT))
            prefs.putString(KEY_PASSWORD_IV, Base64.encodeToString(iv, Base64.DEFAULT))
            prefs.apply()
            
            Log.d(TAG, "Password saved securely")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save password securely: ${e.message}")
            // Notify user that password wasn't saved
            Toast.makeText(
                this,
                "Could not save password securely. You'll need to enter it next time.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
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
    
    private fun clearSavedPassword() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        prefs.remove(KEY_ENCRYPTED_PASSWORD)
        prefs.remove(KEY_PASSWORD_IV)
        prefs.apply()
        
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
                keyStore.deleteEntry(KEYSTORE_ALIAS)
            }
        } catch (e: Exception) {
            // Key cleanup not critical
        }
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
        
        // Save credentials before login attempt
        saveCredentials()
        
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
