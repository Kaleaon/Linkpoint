package com.linkpoint.avatar

import java.util.UUID

/**
 * Represents a wearable item (clothing, body part, attachment) in Second Life
 * Based on Lumiya/Firestorm viewer wearable representation
 */
data class Wearable(
    val itemId: UUID,
    val assetId: UUID,
    val name: String,
    val description: String = "",
    val wearableType: WearableType,
    val iconAssetId: UUID? = null,
    val isWorn: Boolean = false,
    val creatorId: UUID? = null,
    val permissions: Int = 0
) {
    companion object {
        /**
         * Create a Wearable from inventory item data
         */
        fun fromInventoryItem(
            itemId: UUID,
            assetId: UUID,
            name: String,
            description: String,
            wearableType: WearableType,
            isWorn: Boolean = false
        ): Wearable {
            return Wearable(
                itemId = itemId,
                assetId = assetId,
                name = name,
                description = description,
                wearableType = wearableType,
                isWorn = isWorn
            )
        }
    }
}

/**
 * Body parts that can be edited in the appearance editor
 */
enum class BodyPart {
    HEAD,
    EYES,
    EARS,
    NOSE,
    MOUTH,
    CHIN,
    TORSO,
    LEGS,
    FEET
}
