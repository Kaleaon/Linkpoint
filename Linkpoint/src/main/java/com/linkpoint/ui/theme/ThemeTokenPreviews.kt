package com.linkpoint.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, name = "Theme Tokens - Default")
@Composable
private fun ThemeTokenPreviewDefault() {
    LinkpointTheme(themePack = BuiltInThemes.LINKPOINT_DEFAULT, darkTheme = true) {
        ThemeTokenPreviewContent()
    }
}

@Preview(showBackground = true, name = "Theme Tokens - Firestorm")
@Composable
private fun ThemeTokenPreviewFirestorm() {
    LinkpointTheme(themePack = BuiltInThemes.FIRESTORM, darkTheme = true) {
        ThemeTokenPreviewContent()
    }
}

@Composable
private fun ThemeTokenPreviewContent() {
    val spacing = LinkpointTheme.spacing
    val motion = LinkpointTheme.motion

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        Text("Typography", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        Text("Body Large", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        Text("Body Medium", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)

        Text("Shapes", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Card(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.xxl)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
            }
            Card(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.xxl)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                )
            }
        }

        Text(
            "Motion: fast=${motion.fastMillis}ms, normal=${motion.normalMillis}ms, slow=${motion.slowMillis}ms",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
