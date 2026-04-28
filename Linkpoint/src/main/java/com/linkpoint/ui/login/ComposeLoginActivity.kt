package com.linkpoint.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.network.LoginResult
import com.linkpoint.ui.navigation.WorldHomeHostActivity
import com.linkpoint.ui.settings.SettingsActivity
import com.linkpoint.ui.theme.LinkpointTheme
import kotlinx.coroutines.launch

/**
 * Linkpoint 2.0 Compose-first login entry point.
 *
 * Drives the same `app.protocol.login(...)` flow as [LoginActivity] but
 * renders the modern aurora + glass form via [LoginScreen]. Use this in
 * place of LoginActivity when launching from the app shell:
 * `setLauncherActivity = ComposeLoginActivity::class.java`.
 */
class ComposeLoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LinkpointTheme {
                ComposeLoginRoot(
                    onLoginSuccess = {
                        startActivity(Intent(this, WorldHomeHostActivity::class.java))
                        finish()
                    },
                    onOpenSettings = {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    },
                    onPerformLogin = ::performLogin,
                )
            }
        }
    }

    private suspend fun performLogin(
        credentials: LoginCredentials,
        statusState: MutableState<String>,
        loadingState: MutableState<Boolean>,
        errorState: MutableState<Boolean>,
    ): Boolean {
        val app = LinkpointApp.getInstance()
        loadingState.value = true
        errorState.value = false
        statusState.value = "Logging in to ${app.gridManager.getSelectedGrid().name}…"

        val grid = app.gridManager.getAvailableGrids().getOrNull(credentials.selectedGridIndex)
            ?: app.gridManager.getSelectedGrid()
        val result = app.protocol.login(
            firstName = credentials.firstName.trim(),
            lastName = credentials.lastName.trim().ifBlank { "Resident" },
            password = credentials.password,
            loginUri = grid.loginUri,
            startLocation = credentials.startLocation.lowercase().replace(' ', '_'),
        )

        loadingState.value = false
        return when (result) {
            is LoginResult.Success -> {
                statusState.value = "Welcome to ${grid.name}"
                true
            }
            is LoginResult.MFARequired -> {
                statusState.value = "MFA required — open the app for the full prompt."
                errorState.value = true
                false
            }
            is LoginResult.Failure -> {
                statusState.value = result.message
                errorState.value = true
                false
            }
        }
    }
}

@Composable
private fun ComposeLoginRoot(
    onLoginSuccess: () -> Unit,
    onOpenSettings: () -> Unit,
    onPerformLogin: suspend (
        LoginCredentials,
        MutableState<String>,
        MutableState<Boolean>,
        MutableState<Boolean>,
    ) -> Boolean,
) {
    val app = LinkpointApp.getInstance()
    val grids = remember {
        app.gridManager.getAvailableGrids().map { GridDisplayInfo(it.id, it.name) }
    }
    val statusState = remember { mutableStateOf("") }
    val loadingState = remember { mutableStateOf(false) }
    val errorState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val isConnected by app.sessionManager.connectionState
        .collectAsState(initial = app.sessionManager.connectionState.value)

    LaunchedEffect(isConnected) {
        if (app.sessionManager.isConnected()) onLoginSuccess()
    }

    LoginScreen(
        grids = grids,
        statusMessage = statusState.value,
        isLoading = loadingState.value,
        isError = errorState.value,
        onLogin = { credentials ->
            scope.launch {
                if (onPerformLogin(credentials, statusState, loadingState, errorState)) {
                    onLoginSuccess()
                }
            }
        },
        onOpenSettings = onOpenSettings,
    )
}
