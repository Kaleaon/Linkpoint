package com.lumiyaviewer.lumiya.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.lumiyaviewer.lumiya.LumiyaApp
import com.lumiyaviewer.lumiya.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Small, modernised login activity so the rebuilt APK has a stable entry point.
 * The real grid authentication workflow will be reintroduced once the
 * supporting protocol code is ready; for now we simulate a successful login
 * and provide basic input validation and status feedback.
 */
class CleanLoginActivity : AppCompatActivity() {

    private lateinit var firstNameField: EditText
    private lateinit var lastNameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView

    private var loginJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clean_login)

        firstNameField = findViewById(R.id.edit_first_name)
        lastNameField = findViewById(R.id.edit_last_name)
        passwordField = findViewById(R.id.edit_password)
        loginButton = findViewById(R.id.button_login)
        progressBar = findViewById(R.id.progress_login)
        statusText = findViewById(R.id.text_status)

        statusText.text = getString(
            R.string.clean_login_status_ready,
            getString(R.string.app_name),
            LumiyaApp.getAppVersion()
        )

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                loginButton.isEnabled = areInputsValid()
            }
        }

        firstNameField.addTextChangedListener(watcher)
        lastNameField.addTextChangedListener(watcher)
        passwordField.addTextChangedListener(watcher)

        loginButton.isEnabled = areInputsValid()
        loginButton.setOnClickListener { attemptLogin() }
    }

    override fun onDestroy() {
        loginJob?.cancel()
        super.onDestroy()
    }

    private fun attemptLogin() {
        val first = firstNameField.text?.toString()?.trim().orEmpty()
        val last = lastNameField.text?.toString()?.trim().orEmpty()
        val password = passwordField.text?.toString().orEmpty()

        if (first.isBlank() || last.isBlank() || password.isBlank()) {
            showError(getString(R.string.clean_login_validation_error))
            return
        }

        Log.i(TAG, "Authenticating user $first $last (demo mode)")
        setInProgress(true, getString(R.string.clean_login_status_authenticating))

        loginJob?.cancel()
        loginJob = lifecycleScope.launch {
            delay(DEMO_LOGIN_DELAY_MS)
            Log.i(TAG, "Demo login complete; continuing to main experience placeholder")
            setInProgress(false, getString(R.string.clean_login_status_success, first, last))
            Toast.makeText(
                this@CleanLoginActivity,
                R.string.clean_login_toast_success,
                Toast.LENGTH_LONG
            ).show()

            // TODO: Launch the real viewer experience once the modernised
            // navigation stack is ready. For now we stay on this screen.
        }
    }

    private fun setInProgress(inProgress: Boolean, statusMessage: String? = null) {
        loginButton.isEnabled = !inProgress && areInputsValid()
        progressBar.isVisible = inProgress
        firstNameField.isEnabled = !inProgress
        lastNameField.isEnabled = !inProgress
        passwordField.isEnabled = !inProgress
        statusMessage?.let { statusText.text = it }
    }

    private fun showError(message: String) {
        setInProgress(false, getString(R.string.clean_login_status_error, message))
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun areInputsValid(): Boolean {
        val first = firstNameField.text?.toString()?.trim().orEmpty()
        val last = lastNameField.text?.toString()?.trim().orEmpty()
        val password = passwordField.text?.toString().orEmpty()
        return first.isNotEmpty() && last.isNotEmpty() && password.isNotEmpty()
    }

    companion object {
        private const val TAG = "CleanLoginActivity"
        private const val DEMO_LOGIN_DELAY_MS = 1500L
    }
}
