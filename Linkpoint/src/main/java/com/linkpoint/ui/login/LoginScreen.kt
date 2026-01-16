package com.linkpoint.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow

/**
 * Login credentials state
 */
data class LoginCredentials(
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val savePassword: Boolean = false,
    val selectedGridIndex: Int = 0,
    val startLocation: String = "Last Location"
)

/**
 * Grid info for display
 */
data class GridDisplayInfo(
    val id: String,
    val name: String
)

/**
 * Compose version of LoginActivity.
 * 
 * Features:
 * - First/Last name and password input
 * - Grid selection dropdown
 * - Start location selection
 * - Save password option
 * - Login progress indicator
 * - Settings access
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    grids: List<GridDisplayInfo>,
    startLocations: List<String> = listOf("Last Location", "Home"),
    statusMessage: String = "",
    isLoading: Boolean = false,
    onLogin: (LoginCredentials) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var credentials by remember { mutableStateOf(LoginCredentials()) }
    var passwordVisible by remember { mutableStateOf(false) }
    var gridExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Linkpoint") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App title/logo area
                Text(
                    text = "Welcome to Linkpoint",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
                
                // First Name
                OutlinedTextField(
                    value = credentials.firstName,
                    onValueChange = { credentials = credentials.copy(firstName = it) },
                    label = { Text("First Name") },
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Last Name
                OutlinedTextField(
                    value = credentials.lastName,
                    onValueChange = { credentials = credentials.copy(lastName = it) },
                    label = { Text("Last Name") },
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Password
                OutlinedTextField(
                    value = credentials.password,
                    onValueChange = { credentials = credentials.copy(password = it) },
                    label = { Text("Password") },
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Grid Selection
                ExposedDropdownMenuBox(
                    expanded = gridExpanded,
                    onExpandedChange = { if (!isLoading) gridExpanded = it }
                ) {
                    OutlinedTextField(
                        value = grids.getOrNull(credentials.selectedGridIndex)?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isLoading,
                        label = { Text("Grid") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gridExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = gridExpanded,
                        onDismissRequest = { gridExpanded = false }
                    ) {
                        grids.forEachIndexed { index, grid ->
                            DropdownMenuItem(
                                text = { Text(grid.name) },
                                onClick = {
                                    credentials = credentials.copy(selectedGridIndex = index)
                                    gridExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Start Location
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { if (!isLoading) locationExpanded = it }
                ) {
                    OutlinedTextField(
                        value = credentials.startLocation,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isLoading,
                        label = { Text("Start Location") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        startLocations.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location) },
                                onClick = {
                                    credentials = credentials.copy(startLocation = location)
                                    locationExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Save Password Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = credentials.savePassword,
                        onCheckedChange = { credentials = credentials.copy(savePassword = it) },
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Password")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Login Button
                Button(
                    onClick = { onLogin(credentials) },
                    enabled = !isLoading && 
                        credentials.firstName.isNotBlank() && 
                        credentials.lastName.isNotBlank() && 
                        credentials.password.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }
                
                // Status Message
                if (statusMessage.isNotBlank()) {
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
