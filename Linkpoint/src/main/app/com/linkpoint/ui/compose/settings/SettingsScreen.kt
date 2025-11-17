package com.linkpoint.ui.compose.settings

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
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.compose.components.*
import com.linkpoint.ui.compose.theme.ThemeMode
import com.linkpoint.ui.compose.theme.rememberThemeState

/**
 * Settings Screen
 * 
 * Complete settings interface for Linkpoint with:
 * - Appearance settings
 * - Account settings
 * - Notification settings
 * - Privacy settings
 * - About section
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val themeState = rememberThemeState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance Section
            item {
                SettingsSection(title = "Appearance") {
                    // Theme Mode Setting
                    ThemeModeSetting(
                        currentMode = themeState.themeMode,
                        onModeChange = { themeState.setThemeMode(it) }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Dynamic Color Setting
                    SwitchSetting(
                        title = "Dynamic Colors",
                        description = "Use colors from your wallpaper",
                        icon = Icons.Default.Refresh,
                        checked = themeState.useDynamicColor,
                        onCheckedChange = { themeState.setDynamicColor(it) }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // High Contrast Setting
                    SwitchSetting(
                        title = "High Contrast",
                        description = "Increase contrast for better visibility",
                        icon = Icons.Default.Star,
                        checked = themeState.useHighContrast,
                        onCheckedChange = { themeState.setHighContrast(it) }
                    )
                }
            }
            
            // Account Section
            item {
                SettingsSection(title = "Account") {
                    SettingsItem(
                        title = "Profile",
                        description = "View and edit your profile",
                        icon = Icons.Default.Person,
                        onClick = onNavigateToProfile
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingsItem(
                        title = "Grid Settings",
                        description = "Manage grid connections",
                        icon = Icons.Default.Settings,
                        onClick = { /* TODO */ }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingsItem(
                        title = "Sign Out",
                        description = "Sign out of your account",
                        icon = Icons.Default.ExitToApp,
                        onClick = { /* TODO */ }
                    )
                }
            }
            
            // Notifications Section
            item {
                SettingsSection(title = "Notifications") {
                    var chatNotifications by remember { mutableStateOf(true) }
                    var groupNotifications by remember { mutableStateOf(true) }
                    var friendNotifications by remember { mutableStateOf(true) }
                    
                    SwitchSetting(
                        title = "Chat Messages",
                        description = "Receive notifications for chat messages",
                        icon = Icons.Default.Email,
                        checked = chatNotifications,
                        onCheckedChange = { chatNotifications = it }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SwitchSetting(
                        title = "Group Messages",
                        description = "Receive notifications for group messages",
                        icon = Icons.Default.Email,
                        checked = groupNotifications,
                        onCheckedChange = { groupNotifications = it }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SwitchSetting(
                        title = "Friend Requests",
                        description = "Receive notifications for friend requests",
                        icon = Icons.Default.Person,
                        checked = friendNotifications,
                        onCheckedChange = { friendNotifications = it }
                    )
                }
            }
            
            // Privacy Section
            item {
                SettingsSection(title = "Privacy") {
                    var showOnlineStatus by remember { mutableStateOf(true) }
                    var allowIMs by remember { mutableStateOf(true) }
                    
                    SwitchSetting(
                        title = "Show Online Status",
                        description = "Let others see when you're online",
                        icon = Icons.Default.Face,
                        checked = showOnlineStatus,
                        onCheckedChange = { showOnlineStatus = it }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SwitchSetting(
                        title = "Allow Instant Messages",
                        description = "Allow others to send you instant messages",
                        icon = Icons.Default.Email,
                        checked = allowIMs,
                        onCheckedChange = { allowIMs = it }
                    )
                }
            }
            
            // Performance Section
            item {
                SettingsSection(title = "Performance") {
                    var highQualityGraphics by remember { mutableStateOf(true) }
                    var enableShadows by remember { mutableStateOf(true) }
                    
                    SwitchSetting(
                        title = "High Quality Graphics",
                        description = "Enable high quality rendering",
                        icon = Icons.Default.Star,
                        checked = highQualityGraphics,
                        onCheckedChange = { highQualityGraphics = it }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SwitchSetting(
                        title = "Shadows",
                        description = "Enable shadow rendering",
                        icon = Icons.Default.Star,
                        checked = enableShadows,
                        onCheckedChange = { enableShadows = it }
                    )
                }
            }
            
            // About Section
            item {
                SettingsSection(title = "About") {
                    SettingsItem(
                        title = "Version",
                        description = "1.0.0 (Beta)",
                        icon = Icons.Default.Info,
                        onClick = onNavigateToAbout
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingsItem(
                        title = "Privacy Policy",
                        description = "View our privacy policy",
                        icon = Icons.Default.Info,
                        onClick = { /* TODO */ }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingsItem(
                        title = "Terms of Service",
                        description = "View terms of service",
                        icon = Icons.Default.Info,
                        onClick = { /* TODO */ }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingsItem(
                        title = "Open Source Licenses",
                        description = "View open source licenses",
                        icon = Icons.Default.Info,
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

/**
 * Settings Section
 * 
 * Container for a group of related settings.
 */
@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    LinkpointCard(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        content()
    }
}

/**
 * Settings Item
 * 
 * Individual clickable setting item.
 */
@Composable
private fun SettingsItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Switch Setting
 * 
 * Setting with a switch toggle.
 */
@Composable
private fun SwitchSetting(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Theme Mode Setting
 * 
 * Special setting for theme mode selection.
 */
@Composable
private fun ThemeModeSetting(
    currentMode: ThemeMode,
    onModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Choose your preferred theme",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeModeChip(
                text = "Light",
                selected = currentMode == ThemeMode.LIGHT,
                onClick = { onModeChange(ThemeMode.LIGHT) },
                modifier = Modifier.weight(1f)
            )
            
            ThemeModeChip(
                text = "Dark",
                selected = currentMode == ThemeMode.DARK,
                onClick = { onModeChange(ThemeMode.DARK) },
                modifier = Modifier.weight(1f)
            )
            
            ThemeModeChip(
                text = "System",
                selected = currentMode == ThemeMode.SYSTEM,
                onClick = { onModeChange(ThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Theme Mode Chip
 * 
 * Chip for theme mode selection.
 */
@Composable
private fun ThemeModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else {
            null
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}