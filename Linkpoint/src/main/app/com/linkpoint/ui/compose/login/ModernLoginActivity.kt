package com.linkpoint.ui.compose.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.linkpoint.ui.main.ModernMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Modern Login Activity using Jetpack Compose
 * 
 * Features:
 * - Material Design 3 theming
 * - Reactive UI with StateFlow
 * - Hilt dependency injection
 * - Smooth navigation
 */
@AndroidEntryPoint
class ModernLoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LinkpointTheme {
                LoginScreenContainer(
                    viewModel = viewModel,
                    onLoginSuccess = { navigateToMain() },
                    onCreateAccount = { openCreateAccountUrl() }
                )
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, ModernMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun openCreateAccountUrl() {
        // Open Second Life account creation page
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("https://join.secondlife.com/")
        }
        startActivity(intent)
    }
}

@Composable
private fun LoginScreenContainer(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()

    // Handle login result
    LaunchedEffect(loginResult) {
        when (loginResult) {
            is LoginResult.Success -> {
                onLoginSuccess()
                viewModel.clearLoginResult()
            }
            is LoginResult.Error -> {
                // Error is already shown in UI state
                viewModel.clearLoginResult()
            }
            null -> { /* No result yet */ }
        }
    }

    var showGridDialog by remember { mutableStateOf(false) }

    LoginScreen(
        uiState = uiState,
        onFirstNameChange = viewModel::updateFirstName,
        onLastNameChange = viewModel::updateLastName,
        onPasswordChange = viewModel::updatePassword,
        onLoginClick = viewModel::login,
        onGridSelectionClick = { showGridDialog = true },
        onCreateAccountClick = onCreateAccount
    )

    // Grid selection dialog
    if (showGridDialog) {
        GridSelectionDialog(
            currentGrid = uiState.selectedGrid,
            onGridSelected = viewModel::updateSelectedGrid,
            onDismiss = { showGridDialog = false }
        )
    }
}

/**
 * Linkpoint Material Design 3 Theme
 */
@Composable
fun LinkpointTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF6750A4),
            onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            primaryContainer = androidx.compose.ui.graphics.Color(0xFFEADDFF),
            onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF21005D),
            secondary = androidx.compose.ui.graphics.Color(0xFF625B71),
            onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8DEF8),
            onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF1D192B),
            tertiary = androidx.compose.ui.graphics.Color(0xFF7D5260),
            onTertiary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            error = androidx.compose.ui.graphics.Color(0xFFB3261E),
            onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            errorContainer = androidx.compose.ui.graphics.Color(0xFFF9DEDC),
            onErrorContainer = androidx.compose.ui.graphics.Color(0xFF410E0B),
            background = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
            onBackground = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
            surface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
            onSurface = androidx.compose.ui.graphics.Color(0xFFE6E1E5)
        )
    } else {
        lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF6750A4),
            onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            primaryContainer = androidx.compose.ui.graphics.Color(0xFFEADDFF),
            onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF21005D),
            secondary = androidx.compose.ui.graphics.Color(0xFF625B71),
            onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8DEF8),
            onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF1D192B),
            tertiary = androidx.compose.ui.graphics.Color(0xFF7D5260),
            onTertiary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            error = androidx.compose.ui.graphics.Color(0xFFB3261E),
            onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            errorContainer = androidx.compose.ui.graphics.Color(0xFFF9DEDC),
            onErrorContainer = androidx.compose.ui.graphics.Color(0xFF410E0B),
            background = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
            onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
            surface = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
            onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}