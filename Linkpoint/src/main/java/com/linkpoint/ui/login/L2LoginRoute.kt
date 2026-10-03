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
import com.linkpoint.core.ConnectionState
import com.linkpoint.ui.auth.WebAuthInterceptorDialog
import com.linkpoint.ui.auth.InterceptedToken
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
    val gridListFlow = remember { app.gridManager.getAvailableGridsFlow() }
    val gridList by gridListFlow.collectAsState(initial = app.gridManager.getAvailableGrids())
    val grids = gridList.map {
        GridDisplayInfo(
            id = it.id,
            name = it.name,
            status = it.status,
            logoUrl = it.logoUrl,
            loginUri = it.loginUri
        )
    }
    var status by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var showWebAuthDialog by remember { mutableStateOf(false) }
    var webAuthUrl by remember { mutableStateOf("https://id.secondlife.com/openid/login") }
    var pendingCredentials by remember { mutableStateOf<LoginCredentials?>(null) }
    val savedAccountRepo = remember { app.savedAccountRepository }
    var savedAccounts by remember { mutableStateOf<List<com.linkpoint.auth.SavedAccount>>(emptyList()) }
    val connectionState by app.sessionManager.connectionState.collectAsState()
    val isConnected = connectionState == ConnectionState.CONNECTED

    LaunchedEffect(Unit) {
        savedAccounts = savedAccountRepo.getSavedAccounts()
    }

    LaunchedEffect(isConnected) {
        if (isConnected) onLoginSuccess()
    }

    if (showWebAuthDialog) {
        val selectedGrid = app.gridManager.getSelectedGrid()
        WebAuthInterceptorDialog(
            initialUrl = webAuthUrl,
            gridUri = selectedGrid.loginUri,
            onTokenIntercepted = { interceptedToken ->
                showWebAuthDialog = false
                loading = true
                error = false
                status = "Web token captured. Authenticating grid session…"
                app.applicationScope.launch {
                    val creds = pendingCredentials ?: LoginCredentials()
                    val startLocation = when (creds.startLocation.trim().lowercase()) {
                        "last location", "last" -> "last"
                        "home" -> "home"
                        else -> app.startLocationManager.getStartLocationForLogin()
                    }
                    val username = if (creds.lastName.isBlank() || creds.lastName.equals("Resident", ignoreCase = true)) {
                        creds.firstName.trim()
                    } else {
                        "${creds.firstName.trim()} ${creds.lastName.trim()}"
                    }
                    val storedMfaHash = app.protocol.getStoredMfaHash(username) ?: ""
                    val mfaHashToUse = interceptedToken.mfaHash ?: storedMfaHash
                    val result = app.protocol.login(
                        firstName = creds.firstName.trim(),
                        lastName = creds.lastName.trim().ifBlank { "Resident" },
                        password = creds.password,
                        loginUri = selectedGrid.loginUri,
                        startLocation = startLocation,
                        mfaToken = interceptedToken.token,
                        mfaHash = mfaHashToUse,
                        webAuthToken = interceptedToken.token
                    )
                    withContext(Dispatchers.Main) {
                        loading = false
                        when (result) {
                            is LoginResult.Success -> {
                                status = "Welcome to ${selectedGrid.name}"
                                if (creds.savePassword) {
                                    savedAccountRepo.saveAccount(
                                        firstName = creds.firstName.trim(),
                                        lastName = creds.lastName.trim().ifBlank { "Resident" },
                                        gridId = selectedGrid.id,
                                        password = creds.password
                                    )
                                }
                                if (!result.mfaHash.isNullOrBlank()) {
                                    app.protocol.storeMfaHash(username, result.mfaHash)
                                }
                                savedAccounts = savedAccountRepo.getSavedAccounts()
                            }
                            is LoginResult.MFARequired -> {
                                status = "Additional MFA verification required."
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
            onDismiss = {
                showWebAuthDialog = false
                loading = false
                status = "Web verification cancelled."
            }
        )
    }

    LoginScreen(
        grids = grids,
        statusMessage = status,
        isLoading = loading,
        isError = error,
        savedAccounts = savedAccounts,
        onSelectSavedAccount = { account ->
            // Update status or prepare selected profile
        },
        onDeleteSavedAccount = { account ->
            app.applicationScope.launch {
                savedAccountRepo.deleteAccount(account)
                savedAccounts = savedAccountRepo.getSavedAccounts()
            }
        },
        onAddAccount = {
            status = ""
            error = false
        },
        onWebAuthRequested = {
            webAuthUrl = "https://id.secondlife.com/openid/login"
            showWebAuthDialog = true
        },
        onLogin = { credentials ->
            pendingCredentials = credentials
            loading = true
            error = false
            val grid = app.gridManager.getAvailableGrids()
                .getOrNull(credentials.selectedGridIndex)
                ?: app.gridManager.getSelectedGrid()
            app.gridManager.selectGrid(grid.id)
            status = "Resolving grid & logging in to ${grid.name}…"
            app.applicationScope.launch {
                val startLocation = when (credentials.startLocation.trim().lowercase()) {
                    "last location", "last" -> "last"
                    "home" -> "home"
                    else -> app.startLocationManager.getStartLocationForLogin()
                }
                val username = if (credentials.lastName.isBlank() || credentials.lastName.equals("Resident", ignoreCase = true)) {
                    credentials.firstName.trim()
                } else {
                    "${credentials.firstName.trim()} ${credentials.lastName.trim()}"
                }
                val storedMfaHash = app.protocol.getStoredMfaHash(username) ?: ""
                val result = app.protocol.login(
                    firstName = credentials.firstName.trim(),
                    lastName = credentials.lastName.trim().ifBlank { "Resident" },
                    password = credentials.password,
                    loginUri = grid.loginUri,
                    startLocation = startLocation,
                    mfaHash = storedMfaHash,
                )
                withContext(Dispatchers.Main) {
                    loading = false
                    when (result) {
                        is LoginResult.Success -> {
                            status = "Welcome to ${grid.name}"
                            if (credentials.savePassword) {
                                savedAccountRepo.saveAccount(
                                    firstName = credentials.firstName.trim(),
                                    lastName = credentials.lastName.trim().ifBlank { "Resident" },
                                    gridId = grid.id,
                                    password = credentials.password
                                )
                            }
                            if (!result.mfaHash.isNullOrBlank()) {
                                app.protocol.storeMfaHash(username, result.mfaHash)
                            }
                            savedAccounts = savedAccountRepo.getSavedAccounts()
                        }
                        is LoginResult.MFARequired -> {
                            status = "2FA verification required. Opening verification portal…"
                            error = false
                            webAuthUrl = "https://id.secondlife.com/openid/login"
                            showWebAuthDialog = true
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
