package com.linkpoint.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

enum class CornerProfile {
    SHARP,
    ROUNDED,
    PILLED
}

fun ThemePack.resolvedCornerProfile(): CornerProfile = cornerProfile ?: CornerProfile.ROUNDED

fun ThemePack.toMaterialShapes(): Shapes {
    return when (resolvedCornerProfile()) {
        CornerProfile.SHARP -> Shapes(
            extraSmall = RoundedCornerShape(0.dp),
            small = RoundedCornerShape(2.dp),
            medium = RoundedCornerShape(4.dp),
            large = RoundedCornerShape(6.dp),
            extraLarge = RoundedCornerShape(8.dp)
        )

        CornerProfile.ROUNDED -> Shapes(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp),
            extraLarge = RoundedCornerShape(20.dp)
        )

        CornerProfile.PILLED -> Shapes(
            extraSmall = RoundedCornerShape(8.dp),
            small = RoundedCornerShape(14.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(28.dp),
            extraLarge = RoundedCornerShape(36.dp)
        )
    }
}

val LocalLinkpointShapes = staticCompositionLocalOf { BuiltInThemes.LINKPOINT_DEFAULT.toMaterialShapes() }
