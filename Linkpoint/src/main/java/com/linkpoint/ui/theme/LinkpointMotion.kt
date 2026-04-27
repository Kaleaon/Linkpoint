package com.linkpoint.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class LinkpointMotion(
    val fastMillis: Int,
    val normalMillis: Int,
    val slowMillis: Int
)

enum class MotionProfile {
    REDUCED,
    STANDARD,
    EXPRESSIVE
}

fun ThemePack.resolvedMotionProfile(): MotionProfile = motionProfile ?: MotionProfile.STANDARD

fun ThemePack.toMotion(): LinkpointMotion {
    return when (resolvedMotionProfile()) {
        MotionProfile.REDUCED -> LinkpointMotion(
            fastMillis = 80,
            normalMillis = 140,
            slowMillis = 200
        )

        MotionProfile.STANDARD -> LinkpointMotion(
            fastMillis = 120,
            normalMillis = 220,
            slowMillis = 320
        )

        MotionProfile.EXPRESSIVE -> LinkpointMotion(
            fastMillis = 180,
            normalMillis = 300,
            slowMillis = 420
        )
    }
}

val LocalLinkpointMotion = staticCompositionLocalOf { BuiltInThemes.LINKPOINT_DEFAULT.toMotion() }
