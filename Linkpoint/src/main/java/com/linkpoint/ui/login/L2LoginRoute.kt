package com.linkpoint.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.linkpoint.LinkpointApp
import com.linkpoint.network.LoginResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Compose-first LOGIN destination wired into the Linkpoint 2.0 nav graph.
 * Drives the same `protocol.login(...)` flow the legacy entry point uses,
 * but renders the modern aurora + glass [LoginScreen] without any Activity
 * bridge.
 */
@Composable
fun L2LoginRoute(
    onLoginSuccess: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstance()
    val grids = remember {
        app.gridManager.getAvailableGrids().map { GridDisplayInfo(it.id, it.name) }
    }
    var status by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    val isConnected by app.sessionManager.connectionState
        .collectAsState(initial = app.sessionManager.connectionState.value)

    LaunchedEffect(isConnected) {
        if (app.sessionManager.isConnected()) onLoginSuccess()
    }

    LoginScreen(
        grids = grids,
        statusMessage = status,
        isLoading = loading,
        isError = error,
        onLogin = { credentials ->
            // The login flow runs HTTP login + capability seed fetch + UDP
            // handshake — easily 5–30s end to end. Launching it on a
            // composition-bound scope (rememberCoroutineScope) means any
            // navigation away from this route mid-login (e.g. opening
            // Settings) tears down the scope and cancels capability
            // initialization with a "coroutine scope left the composition"
            // exception. Use the application-wide SupervisorJob scope
            // instead so the login can complete regardless of UI state;
            // the SessionManager StateFlow above drives navigation.
            loading = true
            error = false
            val grid = app.gridManager.getAvailableGrids()
                .getOrNull(credentials.selectedGridIndex)
                ?: app.gridManager.getSelectedGrid()
            status = "Logging in to ${grid.name}…"
            app.applicationScope.launch {
                val result = app.protocol.login(
                    firstName = credentials.firstName.trim(),
                    lastName = credentials.lastName.trim().ifBlank { "Resident" },
                    password = credentials.password,
                    loginUri = grid.loginUri,
                    startLocation = credentials.startLocation.lowercase().replace(' ', '_'),
                )
                withContext(Dispatchers.Main) {
                    loading = false
                    when (result) {
                        is LoginResult.Success -> {
                            status = "Welcome to ${grid.name}"
                            // SessionManager.connectionState flips to
                            // CONNECTED inside protocol.login on success;
                            // the LaunchedEffect above will route to home.
                            // We still call onLoginSuccess() defensively
                            // in case the route is still composed.
                            onLoginSuccess()
                        }
                        is LoginResult.MFARequired -> {
                            status = "MFA required — open the app for the full prompt."
                            error = true
                        }
                        is LoginResult.Failure -> {
                            status = result.message
                            error = true
                        }
                    }
                }
            }
        },
        onOpenSettings = onOpenSettings,
        modifier = modifier,
    )
}
