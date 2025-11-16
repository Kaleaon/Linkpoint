package com.linkpoint.bom

import java.util.UUID

enum class BomBakeType {
    HEAD,
    UPPER,
    LOWER,
    EYES,
    SKIRT,
    AUX1,
    AUX2,
    AUX3
}

data class BomTextureLayer(
    val layerId: UUID,
    val bakeType: BomBakeType,
    val textureId: UUID,
    val tint: Int? = null,
    val isAlphaLayer: Boolean = false
)

data class BomBakedTexture(
    val bakeType: BomBakeType,
    val textureId: UUID,
    val bakedAtMillis: Long
)
