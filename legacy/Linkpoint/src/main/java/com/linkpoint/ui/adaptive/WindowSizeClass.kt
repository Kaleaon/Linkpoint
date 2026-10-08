package com.linkpoint.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom breakpoint-driven window size classification.
 *
 * Thresholds:
 *  - Compact:  < 600 dp (Phone portrait, small foldables)
 *  - Medium:   600 dp .. 840 dp (Foldables unfolded, small tablets)
 *  - Expanded: > 840 dp (Tablets, desktop windows)
 */
enum class WindowSizeClass {
    Compact,
    Medium,
    Expanded;

    val isCompact: Boolean get() = this == Compact
    val isMedium: Boolean get() = this == Medium
    val isExpanded: Boolean get() = this == Expanded
    val isAtLeastMedium: Boolean get() = this != Compact
}

/**
 * Classifies screen width in DP into a [WindowSizeClass].
 */
fun calculateWindowSizeClass(widthDp: Int): WindowSizeClass {
    return when {
        widthDp < 600 -> WindowSizeClass.Compact
        widthDp <= 840 -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }
}

/**
 * Classifies screen width in [Dp] into a [WindowSizeClass].
 */
fun calculateWindowSizeClass(widthDp: Dp): WindowSizeClass {
    return calculateWindowSizeClass(widthDp.value.toInt())
}

/**
 * CompositionLocal providing the current [WindowSizeClass].
 * Defaults to [WindowSizeClass.Compact].
 */
val LocalWindowSizeClass = staticCompositionLocalOf { WindowSizeClass.Compact }

/**
 * Provider component that resolves screen size from [LocalConfiguration]
 * and provides [LocalWindowSizeClass] to downstream composables.
 */
@Composable
fun ProvideWindowSizeClass(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val windowSizeClass = calculateWindowSizeClass(configuration.screenWidthDp)
    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        content()
    }
}
