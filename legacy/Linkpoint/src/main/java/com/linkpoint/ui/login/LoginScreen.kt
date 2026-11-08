package com.linkpoint.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linkpoint.R
import com.linkpoint.auth.SavedAccount
import com.linkpoint.ui.components.linkpoint2.fx.AuroraBackdrop
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2FilledButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GhostButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2OutlinedTextField
import com.linkpoint.ui.components.linkpoint2.tokens.GeneratedTokens
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Login credentials state passed to the host.
 */
data class LoginCredentials(
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val savePassword: Boolean = false,
    val selectedGridIndex: Int = 0,
    val startLocation: String = "Last Location",
)

data class GridDisplayInfo(
    val id: String,
    val name: String,
    val status: String = "online",
    val logoUrl: String? = null,
    val loginUri: String = ""
)

/**
 * Linkpoint 2.0 login screen — aurora wash backdrop, glass form card,
 * pill inputs and pill primary CTA. Matches the design in
 * design/screens-1.jsx → LoginScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    grids: List<GridDisplayInfo>,
    startLocations: List<String> = listOf("Last Location", "Home"),
    statusMessage: String = "",
    isLoading: Boolean = false,
    isError: Boolean = false,
    savedAccounts: List<SavedAccount> = emptyList(),
    onSelectSavedAccount: ((SavedAccount) -> Unit)? = null,
    onDeleteSavedAccount: ((SavedAccount) -> Unit)? = null,
    onAddAccount: (() -> Unit)? = null,
    onWebAuthRequested: (() -> Unit)? = null,
    onLogin: (LoginCredentials) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var credentials by remember { mutableStateOf(LoginCredentials()) }
    var passwordVisible by remember { mutableStateOf(false) }
    var gridExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }
    var showSavedSheet by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()
    val tokens = Linkpoint2.tokens

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        AuroraBackdrop()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(scroll),
        ) {
            // Logo + headline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                L2GlassSurface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.login_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.onSurfaceDim,
                modifier = Modifier.padding(top = 6.dp),
            )

            Spacer(Modifier.height(28.dp))

            // Sign-in glass card
            L2GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp),
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.login_sign_in),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                        ),
                        color = tokens.onSurfaceDim,
                    )
                    Spacer(Modifier.height(12.dp))

                    L2OutlinedTextField(
                        value = credentials.firstName,
                        onValueChange = { credentials = credentials.copy(firstName = it) },
                        label = stringResource(R.string.first_name),
                        enabled = !isLoading,
                        leading = {
                            Icon(Icons.Default.AccountCircle, contentDescription = null)
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                    L2OutlinedTextField(
                        value = credentials.lastName,
                        onValueChange = { credentials = credentials.copy(lastName = it) },
                        label = stringResource(R.string.last_name_placeholder),
                        enabled = !isLoading,
                    )
                    Spacer(Modifier.height(10.dp))
                    L2OutlinedTextField(
                        value = credentials.password,
                        onValueChange = { credentials = credentials.copy(password = it) },
                        label = stringResource(R.string.password),
                        enabled = !isLoading,
                        isError = isError,
                        leading = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailing = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password),
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    )

                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.grid),
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.onSurfaceDim,
                        )
                        Spacer(Modifier.weight(1f))
                        Box {
                            val selectedGridInfo = grids.getOrNull(credentials.selectedGridIndex)
                            L2Chip(
                                label = (selectedGridInfo?.name ?: "Second Life") + " ▾",
                                variant = L2ChipVariant.Primary,
                                onClick = { if (!isLoading) gridExpanded = true },
                            )
                            DropdownMenu(
                                expanded = gridExpanded,
                                onDismissRequest = { gridExpanded = false },
                            ) {
                                grids.forEachIndexed { index, grid ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                GridStatusDot(status = grid.status)
                                                Spacer(Modifier.width(8.dp))
                                                Text(grid.name)
                                            }
                                        },
                                        onClick = {
                                            credentials = credentials.copy(selectedGridIndex = index)
                                            gridExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.start_location),
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.onSurfaceDim,
                        )
                        Spacer(Modifier.weight(1f))
                        Box {
                            L2Chip(
                                label = credentials.startLocation + " ▾",
                                onClick = { if (!isLoading) locationExpanded = true },
                            )
                            DropdownMenu(
                                expanded = locationExpanded,
                                onDismissRequest = { locationExpanded = false },
                            ) {
                                startLocations.forEach { location ->
                                    DropdownMenuItem(
                                        text = { Text(location) },
                                        onClick = {
                                            credentials = credentials.copy(startLocation = location)
                                            locationExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.remember_me), style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        Switch(
                            checked = credentials.savePassword,
                            onCheckedChange = { credentials = credentials.copy(savePassword = it) },
                            enabled = !isLoading,
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    L2FilledButton(
                        onClick = { onLogin(credentials) },
                        enabled = !isLoading &&
                            credentials.firstName.isNotBlank() &&
                            credentials.password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        height = 48.dp,
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(stringResource(R.string.enter_world), fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        L2GhostButton(
                            onClick = { showSavedSheet = true },
                            modifier = Modifier.weight(1f),
                            height = 40.dp,
                        ) { Text(stringResource(R.string.login_saved)) }
                        L2GhostButton(
                            onClick = {
                                credentials = LoginCredentials(selectedGridIndex = credentials.selectedGridIndex)
                                onAddAccount?.invoke()
                            },
                            modifier = Modifier.weight(1f),
                            height = 40.dp,
                        ) { Text(stringResource(R.string.login_add_account)) }
                        if (onWebAuthRequested != null) {
                            L2GhostButton(
                                onClick = onWebAuthRequested,
                                modifier = Modifier.weight(1f),
                                height = 40.dp,
                            ) { Text(stringResource(R.string.login_web_2fa)) }
                        }
                    }

                    if (isError && statusMessage.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        L2ErrorBanner(message = statusMessage)
                    } else if (statusMessage.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = statusMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.onSurfaceDim,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    stringResource(R.string.login_version_build),
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.onSurfaceDim,
                )
                Text(
                    stringResource(R.string.forgot_password),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        if (showSavedSheet) {
            SavedAccountsBottomSheet(
                accounts = savedAccounts,
                grids = grids,
                onSelectAccount = { account ->
                    val matchedIndex = grids.indexOfFirst { it.id.equals(account.gridId, ignoreCase = true) }
                    credentials = credentials.copy(
                        firstName = account.firstName,
                        lastName = account.lastName,
                        password = account.encryptedPassword,
                        selectedGridIndex = if (matchedIndex >= 0) matchedIndex else credentials.selectedGridIndex,
                        savePassword = true,
                    )
                    onSelectSavedAccount?.invoke(account)
                    showSavedSheet = false
                },
                onDeleteAccount = { account ->
                    onDeleteSavedAccount?.invoke(account)
                },
                onDismissRequest = { showSavedSheet = false }
            )
        }
    }
}

@Composable
private fun L2ErrorBanner(message: String) {
    val cs = MaterialTheme.colorScheme
    val tokens = Linkpoint2.tokens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(tokens.radii.md))
            .padding(0.dp),
    ) {
        L2GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            backgroundOverride = cs.error.copy(alpha = 0.18f),
            shape = RoundedCornerShape(tokens.radii.md),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.login_failed_prefix, message),
                style = MaterialTheme.typography.bodySmall,
                color = cs.error,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun GridStatusDot(status: String, modifier: Modifier = Modifier) {
    val color = when (status.lowercase()) {
        "online" -> GeneratedTokens.Color.Status.Online
        "offline" -> GeneratedTokens.Color.Status.Offline
        "degraded" -> GeneratedTokens.Color.Status.Degraded
        else -> GeneratedTokens.Color.Status.Unknown
    }
    androidx.compose.foundation.Canvas(modifier = modifier.size(8.dp)) {
        drawCircle(color = color)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedAccountsBottomSheet(
    accounts: List<SavedAccount>,
    grids: List<GridDisplayInfo>,
    onSelectAccount: (SavedAccount) -> Unit,
    onDeleteAccount: (SavedAccount) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val tokens = Linkpoint2.tokens
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(topStart = tokens.radii.lg, topEnd = tokens.radii.lg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "SAVED ACCOUNTS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                    ),
                    color = tokens.onSurfaceDim,
                )
                L2GhostButton(
                    onClick = onDismissRequest,
                    height = 32.dp,
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    Text("Close", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (accounts.isEmpty()) {
                L2GlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(20.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No saved accounts found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.onSurfaceDim,
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(accounts) { account ->
                        val gridName = grids.find { it.id.equals(account.gridId, ignoreCase = true) }?.name ?: account.gridId
                        L2GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectAccount(account)
                                },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = account.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        L2Chip(
                                            label = gridName,
                                            variant = L2ChipVariant.Primary,
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onDeleteAccount(account) },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete account",
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

