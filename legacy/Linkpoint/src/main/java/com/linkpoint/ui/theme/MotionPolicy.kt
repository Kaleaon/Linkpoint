package com.linkpoint.ui.theme

import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Motion policy derived from the system animator duration scale.
 */
@Stable
data class MotionPolicy(
    val animatorDurationScale: Float
) {
    val disableNonEssentialAnimations: Boolean = animatorDurationScale == 0f
}

val LocalMotionPolicy = staticCompositionLocalOf { MotionPolicy(animatorDurationScale = 1f) }

fun readSystemAnimatorDurationScale(context: Context): Float {
    return try {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
    } catch (_: Exception) {
        1f
    }
}

@Composable
@ReadOnlyComposable
fun shouldAnimateNonEssentialMotion(): Boolean =
    !LocalMotionPolicy.current.disableNonEssentialAnimations
