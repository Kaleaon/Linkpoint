package com.linkpoint.ui.login

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linkpoint.ui.components.linkpoint2.fx.AuroraBackdrop
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2FilledButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GhostButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2OutlinedTextField
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
    onLogin: (LoginCredentials) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var credentials by remember { mutableStateOf(LoginCredentials()) }
    var passwordVisible by remember { mutableStateOf(false) }
    var gridExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }
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
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .let { it.then(Modifier) }
                        .padding(0.dp),
                ) {
                    L2GlassSurface(
                        modifier = Modifier.fillMaxSize(),
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
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "Linkpoint",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Step into your second world. Faster, lighter,\nbeautifully on the go.",
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
                        text = "SIGN IN",
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
                        label = "First name",
                        enabled = !isLoading,
                        leading = {
                            Icon(Icons.Default.AccountCircle, contentDescription = null)
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                    L2OutlinedTextField(
                        value = credentials.lastName,
                        onValueChange = { credentials = credentials.copy(lastName = it) },
                        label = "Last name (or 'Resident')",
                        enabled = !isLoading,
                    )
                    Spacer(Modifier.height(10.dp))
                    L2OutlinedTextField(
                        value = credentials.password,
                        onValueChange = { credentials = credentials.copy(password = it) },
                        label = "Password",
                        enabled = !isLoading,
                        isError = isError,
                        leading = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailing = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
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
                            text = "Grid",
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.onSurfaceDim,
                        )
                        Spacer(Modifier.weight(1f))
                        Box {
                            L2Chip(
                                label = (grids.getOrNull(credentials.selectedGridIndex)?.name
                                    ?: "Second Life · Agni") + " ▾",
                                variant = L2ChipVariant.Primary,
                                onClick = { if (!isLoading) gridExpanded = true },
                            )
                            DropdownMenu(
                                expanded = gridExpanded,
                                onDismissRequest = { gridExpanded = false },
                            ) {
                                grids.forEachIndexed { index, grid ->
                                    DropdownMenuItem(
                                        text = { Text(grid.name) },
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
                            text = "Start location",
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
                        Text("Remember me", style = MaterialTheme.typography.bodyMedium)
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
                            Text("Enter world", fontWeight = FontWeight.SemiBold)
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
                            onClick = { /* saved accounts */ },
                            modifier = Modifier.weight(1f),
                            height = 40.dp,
                        ) { Text("Saved") }
                        L2GhostButton(
                            onClick = { /* add account */ },
                            modifier = Modifier.weight(1f),
                            height = 40.dp,
                        ) { Text("Add account") }
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
                    "v2.0 · build 4831",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.onSurfaceDim,
                )
                Text(
                    "Forgot password?",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
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
                text = "Login failed · $message",
                style = MaterialTheme.typography.bodySmall,
                color = cs.error,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
