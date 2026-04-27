package com.linkpoint.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.theme.DensitySettingsStore
import com.linkpoint.ui.theme.LinkpointTokens
import com.linkpoint.ui.theme.ThemePack
import kotlinx.coroutines.launch

/**
 * Settings state
 */
data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val soundVolume: Float = 0.8f,
    val showOnlineStatus: Boolean = true,
    val autoAcceptFriends: Boolean = false,
    val renderDistance: Float = 64f,
    val brightness: Float = 1.0f,
    val rlvEnabled: Boolean = false,
    val showHud: Boolean = true,
    val showJoysticks: Boolean = true,
    val showActionButtons: Boolean = true,
    val showMovementButtons: Boolean = true
)

/**
 * Compose version of SettingsActivity.
 * 
 * Features:
 * - Account settings
 * - Notifications toggle
 * - Sound settings with volume slider
 * - Privacy settings
 * - Graphics settings
 * - Theme selection
 * - About section
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: SettingsState,
    appVersion: String = "1.0.0",
    onSettingsChange: (SettingsState) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenThemePicker: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenLayoutEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSettings by remember { mutableStateOf(settings) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val densityStore = remember(context) { DensitySettingsStore(context) }
    val densityMode by densityStore.densityMode.collectAsState(initial = ThemePack.DensityMode.BALANCED)
    val spacing = LinkpointTokens.design.spacing
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            // Account Section
            SettingsSection(title = "Account") {
                SettingsNavigationItem(
                    icon = Icons.Default.Person,
                    title = "Account Settings",
                    subtitle = "Manage your account",
                    onClick = onOpenAccount
                )
            }
            
            // Appearance Section
            SettingsSection(title = "Appearance") {
                SettingsNavigationItem(
                    icon = Icons.Default.ColorLens,
                    title = "Theme",
                    subtitle = "Choose your color theme",
                    onClick = onOpenThemePicker
                )
                
                HorizontalDivider()

                DensityModeSetting(
                    selectedDensity = densityMode,
                    onDensitySelected = { selected ->
                        scope.launch {
                            densityStore.setDensityMode(selected)
                        }
                    }
                )

                HorizontalDivider()
                
                SettingsSliderItem(
                    icon = Icons.Default.BrightnessMedium,
                    title = "Brightness",
                    value = currentSettings.brightness,
                    onValueChange = { 
                        currentSettings = currentSettings.copy(brightness = it)
                        onSettingsChange(currentSettings)
                    }
                )
            }
            
            // Notifications Section
            SettingsSection(title = "Notifications") {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Enable Notifications",
                    subtitle = "Receive alerts for messages and events",
                    checked = currentSettings.notificationsEnabled,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(notificationsEnabled = it)
                        onSettingsChange(currentSettings)
                    }
                )
            }

            // Interface Section
            SettingsSection(title = "Interface") {
                SettingsSwitchItem(
                    icon = Icons.Default.Tune,
                    title = "Show HUD",
                    subtitle = "Toggle HUD overlays",
                    checked = currentSettings.showHud,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(showHud = it)
                        onSettingsChange(currentSettings)
                    }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Default.Tune,
                    title = "Show Joysticks",
                    subtitle = "Toggle movement and camera joysticks",
                    checked = currentSettings.showJoysticks,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(showJoysticks = it)
                        onSettingsChange(currentSettings)
                    }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Default.Tune,
                    title = "Show Action Buttons",
                    subtitle = "Toggle gesture and social buttons",
                    checked = currentSettings.showActionButtons,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(showActionButtons = it)
                        onSettingsChange(currentSettings)
                    }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Default.Tune,
                    title = "Show Movement Buttons",
                    subtitle = "Toggle fly, run, jump, and sit buttons",
                    checked = currentSettings.showMovementButtons,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(showMovementButtons = it)
                        onSettingsChange(currentSettings)
                    }
                )

                HorizontalDivider()

                SettingsNavigationItem(
                    icon = Icons.Default.Tune,
                    title = "Customize Layout",
                    subtitle = "Reposition on-screen controls",
                    onClick = onOpenLayoutEditor
                )
            }
            
            // Sound Section
            SettingsSection(title = "Sound") {
                SettingsSwitchItem(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    title = "Sound Effects",
                    subtitle = "Play sounds for notifications",
                    checked = currentSettings.soundEnabled,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(soundEnabled = it)
                        onSettingsChange(currentSettings)
                    }
                )
                
                if (currentSettings.soundEnabled) {
                    HorizontalDivider()
                    
                    SettingsSliderItem(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = "Volume",
                        value = currentSettings.soundVolume,
                        onValueChange = {
                            currentSettings = currentSettings.copy(soundVolume = it)
                            onSettingsChange(currentSettings)
                        }
                    )
                }
            }
            
            // Privacy Section
            SettingsSection(title = "Privacy") {
                SettingsSwitchItem(
                    icon = Icons.Default.Person,
                    title = "Show Online Status",
                    subtitle = "Let friends see when you're online",
                    checked = currentSettings.showOnlineStatus,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(showOnlineStatus = it)
                        onSettingsChange(currentSettings)
                    }
                )
                
                HorizontalDivider()
                
                SettingsNavigationItem(
                    icon = Icons.Default.Security,
                    title = "Privacy Settings",
                    subtitle = "Manage permissions and data",
                    onClick = onOpenPrivacy
                )
            }
            
            // RLV Section
            SettingsSection(title = "RLV (Restrained Love Viewer)") {
                SettingsSwitchItem(
                    icon = Icons.Default.Security,
                    title = "Enable RLV",
                    subtitle = "Enable Restrained Love Viewer functions",
                    checked = currentSettings.rlvEnabled,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(rlvEnabled = it)
                        onSettingsChange(currentSettings)
                    }
                )
            }
            
            // Graphics Section
            SettingsSection(title = "Graphics") {
                SettingsSliderItem(
                    icon = Icons.Default.Tune,
                    title = "Render Distance",
                    subtitle = "${currentSettings.renderDistance.toInt()}m",
                    value = currentSettings.renderDistance / 256f,
                    onValueChange = {
                        currentSettings = currentSettings.copy(renderDistance = it * 256f)
                        onSettingsChange(currentSettings)
                    }
                )
            }
            
            // About Section
            SettingsSection(title = "About") {
                SettingsNavigationItem(
                    icon = Icons.Default.Info,
                    title = "About Linkpoint",
                    subtitle = "Version $appVersion",
                    onClick = onOpenAbout
                )
            }
        }
    }
}

@Composable
private fun DensityModeSetting(
    selectedDensity: ThemePack.DensityMode,
    onDensitySelected: (ThemePack.DensityMode) -> Unit
) {
    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Density", style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "Controls global spacing and component compactness",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemePack.DensityMode.entries.forEach { mode ->
                FilterChip(
                    selected = selectedDensity == mode,
                    onClick = { onDensitySelected(mode) },
                    label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }
    }
}

/**
 * Settings section card
 */
@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

/**
 * Settings item with switch
 */
@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Settings item with navigation arrow
 */
@Composable
fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Settings item with slider
 */
@Composable
fun SettingsSliderItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.padding(start = 40.dp)
        )
    }
}
