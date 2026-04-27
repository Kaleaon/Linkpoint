package com.linkpoint.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape

@Immutable
data class LinkpointShapeTokens(
    val materialShapes: Shapes,
    val chipShape: Shape,
    val cardShape: Shape
)

val LocalLinkpointShapes = staticCompositionLocalOf {
    linkpointShapesFor(
        cornerStyle = ThemePack.CornerStyleProfile.STANDARD,
        designTokens = linkpointDesignTokensFor(ThemePack.DensityMode.BALANCED)
    )
}

fun linkpointShapesFor(
    cornerStyle: ThemePack.CornerStyleProfile,
    designTokens: LinkpointDesignTokens
): LinkpointShapeTokens {
    val radii = designTokens.radii
    val (small, medium, large) = when (cornerStyle) {
        ThemePack.CornerStyleProfile.SHARP -> Triple(
            RoundedCornerShape(0),
            RoundedCornerShape(radii.sm),
            RoundedCornerShape(radii.md)
        )
        ThemePack.CornerStyleProfile.ORGANIC -> Triple(
            RoundedCornerShape(radii.md),
            RoundedCornerShape(radii.lg),
            RoundedCornerShape(radii.xl)
        )
        ThemePack.CornerStyleProfile.STANDARD -> Triple(
            RoundedCornerShape(radii.sm),
            RoundedCornerShape(radii.md),
            RoundedCornerShape(radii.lg)
        )
    }

    return LinkpointShapeTokens(
        materialShapes = Shapes(small = small, medium = medium, large = large),
        chipShape = RoundedCornerShape(radii.pill),
        cardShape = medium
    )
}
