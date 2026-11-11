package com.linkpoint.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ktheme.models.LayoutStructure

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

@Preview(showBackground = true, name = "Density Preview - Compact")
@Composable
private fun DensityPreviewCompact() {
    DensityPreviewWrapper(densityProfile = DensityProfile.COMPACT)
}

@Preview(showBackground = true, name = "Density Preview - Standard")
@Composable
private fun DensityPreviewStandard() {
    DensityPreviewWrapper(densityProfile = DensityProfile.STANDARD)
}

@Preview(showBackground = true, name = "Density Preview - Comfortable")
@Composable
private fun DensityPreviewComfortable() {
    DensityPreviewWrapper(densityProfile = DensityProfile.COMFORTABLE)
}

@Preview(showBackground = true, name = "Layout Mode Preview - Grid")
@Composable
private fun LayoutModePreviewGrid() {
    LayoutModePreviewWrapper(layoutStructure = LayoutStructure.MATERIAL3)
}

@Preview(showBackground = true, name = "Layout Mode Preview - List")
@Composable
private fun LayoutModePreviewList() {
    LayoutModePreviewWrapper(layoutStructure = LayoutStructure.TERMINAL)
}

@Preview(showBackground = true, name = "Layout Mode Preview - Rail")
@Composable
private fun LayoutModePreviewRail() {
    LayoutModePreviewWrapper(layoutStructure = LayoutStructure.ART_DECO)
}

@Preview(showBackground = true, name = "Layout Mode Preview - Split")
@Composable
private fun LayoutModePreviewSplit() {
    LayoutModePreviewWrapper(layoutStructure = LayoutStructure.MODERN_GLASS)
}

@Composable
fun DensityPreviewWrapper(
    densityProfile: DensityProfile,
    themePack: ThemePack = BuiltInThemes.LINKPOINT_DEFAULT
) {
    val configuredPack = themePack.copy(densityProfile = densityProfile)
    LinkpointTheme(themePack = configuredPack, darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(LinkpointTheme.spacing.md)
        ) {
            Text(
                text = "Density Profile: ${densityProfile.name}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            ThemeTokenPreviewContent()
        }
    }
}

@Composable
fun LayoutModePreviewWrapper(
    layoutStructure: LayoutStructure,
    themePack: ThemePack = BuiltInThemes.LINKPOINT_DEFAULT
) {
    val configuredPack = themePack.copy(layoutStructure = layoutStructure)
    LinkpointTheme(themePack = configuredPack, darkTheme = true) {
        val spacing = LinkpointTheme.spacing
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(spacing.md)
        ) {
            Text(
                text = "Layout Structure: ${layoutStructure.name}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            when (layoutStructure) {
                LayoutStructure.ART_DECO -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(120.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clip(MaterialTheme.shapes.small)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .clip(MaterialTheme.shapes.medium)
                                .padding(spacing.sm)
                        ) {
                            Text("Rail Navigation Layout Content", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                LayoutStructure.MODERN_GLASS -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clip(MaterialTheme.shapes.small)
                                .padding(spacing.xs)
                        ) {
                            Text("List Pane", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(120.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .clip(MaterialTheme.shapes.medium)
                                .padding(spacing.xs)
                        ) {
                            Text("Detail Pane Content", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                else -> {
                    ThemeTokenPreviewContent()
                }
            }
        }
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

