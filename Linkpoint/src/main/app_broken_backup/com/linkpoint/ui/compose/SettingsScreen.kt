package com.linkpoint.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.theme.LinkpointTheme
import com.linkpoint.ui.theme.ThemeManager

/**
 * Settings screen implementation using Jetpack Compose
 * 
 * Features:
 * - Theme selection (Light/Dark/System)
 * - Graphics quality settings
 * - Network settings
 * - Audio settings
 * - Privacy settings
 * - About information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val themeManager = remember { ThemeManager.getInstance(context) }
    var currentTheme by remember { mutableStateOf(themeManager.getThemeMode()) }
    var showThemeDialog by remember { mutableStateOf(false) }

    LinkpointTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Appearance Section
                item {
                    SettingsSectionHeader("Appearance")
                }
                
                item {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = themeManager.getThemeModeName(),
                        onClick = { showThemeDialog = true }
                    )
                }
                
                // Graphics Section
                item {
                    SettingsSectionHeader("Graphics")
                }
                
                items(graphicsSettings) { setting ->
                    SettingsItem(
                        icon = setting.icon,
                        title = setting.title,
                        subtitle = setting.subtitle,
                        onClick = setting.onClick
                    )
                }
                
                // Network Section
                item {
                    SettingsSectionHeader("Network")
                }
                
                items(networkSettings) { setting ->
                    SettingsItem(
                        icon = setting.icon,
                        title = setting.title,
                        subtitle = setting.subtitle,
                        onClick = setting.onClick
                    )
                }
                
                // Audio Section
                item {
                    SettingsSectionHeader("Audio")
                }
                
                items(audioSettings) { setting ->
                    SettingsItem(
                        icon = setting.icon,
                        title = setting.title,
                        subtitle = setting.subtitle,
                        onClick = setting.onClick
                    )
                }
                
                // Privacy Section
                item {
                    SettingsSectionHeader("Privacy")
                }
                
                items(privacySettings) { setting ->
                    SettingsItem(
                        icon = setting.icon,
                        title = setting.title,
                        subtitle = setting.subtitle,
                        onClick = setting.onClick
                    )
                }
                
                // About Section
                item {
                    SettingsSectionHeader("About")
                }
                
                item {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "About Linkpoint",
                        subtitle = "Version 1.0.0 Beta",
                        onClick = { /* Show about dialog */ }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Theme selection dialog
        if (showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = currentTheme,
                onThemeSelected = { theme ->
                    themeManager.setThemeMode(theme)
                    currentTheme = theme
                    showThemeDialog = false
                },
                onDismiss = { showThemeDialog = false }
            )
        }
    }
}

/**
 * Settings section header
 */
@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

/**
 * Individual settings item
 */
@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Theme selection dialog
 */
@Composable
private fun ThemeSelectionDialog(
    currentTheme: Int,
    onThemeSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Theme") },
        text = {
            Column {
                ThemeOption(
                    title = "Light",
                    selected = currentTheme == ThemeManager.THEME_LIGHT,
                    onClick = { onThemeSelected(ThemeManager.THEME_LIGHT) }
                )
                
                ThemeOption(
                    title = "Dark",
                    selected = currentTheme == ThemeManager.THEME_DARK,
                    onClick = { onThemeSelected(ThemeManager.THEME_DARK) }
                )
                
                ThemeOption(
                    title = "System Default",
                    selected = currentTheme == ThemeManager.THEME_SYSTEM,
                    onClick = { onThemeSelected(ThemeManager.THEME_SYSTEM) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * Theme option in dialog
 */
@Composable
private fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title)
    }
}

/**
 * Data class for settings items
 */
private data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit = {}
)

/**
 * Graphics settings list
 */
private val graphicsSettings = listOf(
    SettingItem(
        icon = Icons.Default.Settings,
        title = "Graphics Quality",
        subtitle = "High (PBR enabled)"
    ),
    SettingItem(
        icon = Icons.Default.AspectRatio,
        title = "Render Distance",
        subtitle = "256 meters"
    ),
    SettingItem(
        icon = Icons.Default.ViewInAr,
        title = "Avatar Complexity",
        subtitle = "Maximum 350,000 triangles"
    )
)

/**
 * Network settings list
 */
private val networkSettings = listOf(
    SettingItem(
        icon = Icons.Default.CloudSync,
        title = "Bandwidth",
        subtitle = "1500 kbps"
    ),
    SettingItem(
        icon = Icons.Default.Speed,
        title = "Cache Size",
        subtitle = "1024 MB"
    )
)

/**
 * Audio settings list
 */
private val audioSettings = listOf(
    SettingItem(
        icon = Icons.Default.VolumeUp,
        title = "Master Volume",
        subtitle = "80%"
    ),
    SettingItem(
        icon = Icons.Default.Mic,
        title = "Voice Chat",
        subtitle = "Enabled"
    )
)

/**
 * Privacy settings list
 */
private val privacySettings = listOf(
    SettingItem(
        icon = Icons.Default.Visibility,
        title = "Online Status",
        subtitle = "Visible to friends"
    ),
    SettingItem(
        icon = Icons.Default.LocationOn,
        title = "Location Sharing",
        subtitle = "Enabled"
    )
)
