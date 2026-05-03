package com.linkpoint.ui.system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SettingsScreenState(
    val title: String = "Settings",
    val avatarName: String = "",
    val pushNotificationsEnabled: Boolean = true,
    val autoAcceptTeleportEnabled: Boolean = false,
    val cameraDistanceMeters: String = "4.0"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDestination(
    state: SettingsScreenState = SettingsScreenState(),
    onAvatarNameChanged: (String) -> Unit = {},
    onPushNotificationsChanged: (Boolean) -> Unit = {},
    onAutoAcceptTeleportChanged: (Boolean) -> Unit = {},
    onCameraDistanceChanged: (String) -> Unit = {},
    onSave: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = { TopAppBar(title = { Text(state.title) }) }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.avatarName,
                onValueChange = onAvatarNameChanged,
                label = { Text("Avatar display name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.cameraDistanceMeters,
                onValueChange = onCameraDistanceChanged,
                label = { Text("Camera distance (m)") },
                modifier = Modifier.fillMaxWidth()
            )
            SettingToggleRow("Push notifications", state.pushNotificationsEnabled, onPushNotificationsChanged)
            SettingToggleRow("Auto-accept teleports", state.autoAcceptTeleportEnabled, onAutoAcceptTeleportChanged)
            Button(onClick = onSave, modifier = Modifier.align(Alignment.End)) { Text("Save") }
        }
    }
}

data class TosScreenState(
    val title: String = "Terms of Service",
    val termsVersion: String = "2026.05",
    val accepted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TosDestination(
    state: TosScreenState = TosScreenState(),
    onAcceptedChanged: (Boolean) -> Unit = {},
    onContinue: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = { TopAppBar(title = { Text(state.title) }) }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Version ${state.termsVersion}")
            Text("By continuing, you agree to the virtual world code of conduct, privacy policy, and moderation rules.")
            SettingToggleRow("I agree", state.accepted, onAcceptedChanged)
            Button(onClick = onContinue, enabled = state.accepted, modifier = Modifier.align(Alignment.End)) {
                Text("Continue")
            }
        }
    }
}

data class ThemePickerScreenState(
    val title: String = "Theme Picker",
    val selectedTheme: ThemeOption = ThemeOption.SYSTEM
)

enum class ThemeOption(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerDestination(
    state: ThemePickerScreenState = ThemePickerScreenState(),
    onThemeSelected: (ThemeOption) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = { TopAppBar(title = { Text(state.title) }) }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Choose app theme")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeOption.entries.forEach { option ->
                    FilterChip(
                        selected = state.selectedTheme == option,
                        onClick = { onThemeSelected(option) },
                        label = { Text(option.label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    enabled: Boolean,
    onChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = enabled, onCheckedChange = onChanged)
    }
}
