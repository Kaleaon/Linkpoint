package com.linkpoint.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ThemePreviewHarness(
    themePack: ThemePack,
    densityMode: ThemePack.DensityMode,
    modifier: Modifier = Modifier
) {
    LinkpointTheme(themePack = themePack, densityMode = densityMode, darkTheme = true) {
        val spacing = LinkpointTokens.design.spacing
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text("${themePack.name} • ${densityMode.name}", style = LocalLinkpointTypography.current.coordinateText)

            Button(onClick = {}) {
                Text("Primary Button")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                FilterChip(selected = true, onClick = {}, label = { Text("Online") })
                FilterChip(selected = false, onClick = {}, label = { Text("Busy") })
                FilterChip(selected = false, onClick = {}, label = { Text("L$ 1250") })
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("Parcel: Sandbox 84, 128, 23", style = LocalLinkpointTypography.current.coordinateText)
                    Text("Balance: L$ 4,980", style = LocalLinkpointTypography.current.lindenDollarText)
                    Text("Chat, inventory, and quick actions should reflect active shape + spacing tokens.")
                }
            }

            OutlinedTextField(
                value = "SLURL: secondlife://Sandbox/128/128/25",
                onValueChange = {},
                label = { Text("Teleport Target") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Default / Balanced", showBackground = true)
@Composable
private fun ThemePreviewHarnessDefaultBalanced() {
    ThemePreviewHarness(BuiltInThemes.LINKPOINT_DEFAULT, ThemePack.DensityMode.BALANCED)
}

@Preview(name = "Firestorm / Compact", showBackground = true)
@Composable
private fun ThemePreviewHarnessFirestormCompact() {
    ThemePreviewHarness(BuiltInThemes.FIRESTORM, ThemePack.DensityMode.COMPACT)
}

@Preview(name = "Royal / Spacious", showBackground = true)
@Composable
private fun ThemePreviewHarnessRoyalSpacious() {
    ThemePreviewHarness(BuiltInThemes.ROYAL_SILVER, ThemePack.DensityMode.SPACIOUS)
}
