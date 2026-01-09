package com.linkpoint.ui

import android.content.Intent
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
                    statusText.text = "Login failed: ${result.message}"
                    Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                statusText.text = "Error: ${e.message}"
                Toast.makeText(this@LoginActivity, "Connection error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
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
}
