package com.linkpoint.ui.system

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

data class SettingsScreenState(
    val title: String = "Settings",
    val description: String = "Settings destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDestination(
    state: SettingsScreenState = SettingsScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class TosScreenState(
    val title: String = "Terms of Service",
    val description: String = "Terms of service destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TosDestination(
    state: TosScreenState = TosScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class ThemePickerScreenState(
    val title: String = "Theme Picker",
    val description: String = "Theme picker destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerDestination(
    state: ThemePickerScreenState = ThemePickerScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DestinationScaffold(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) })
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = description)
        }
    }
}
