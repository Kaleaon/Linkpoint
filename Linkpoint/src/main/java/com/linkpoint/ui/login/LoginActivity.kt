package com.linkpoint.ui.login

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.launch

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
        statusText.text = "Connecting to ${app.gridManager.getSelectedGrid().name}..."
        
        // Perform login
        lifecycleScope.launch {
            val result = app.protocol.login(
                firstName = firstName,
                lastName = lastName,
                password = password,
                loginUri = app.gridManager.getSelectedGrid().loginUri,
                startLocation = startLocation
            )
            
            handleLoginResult(result)
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
                
                AlertDialog.Builder(this)
                    .setTitle("Login Failed")
                    .setMessage(result.message)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
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
