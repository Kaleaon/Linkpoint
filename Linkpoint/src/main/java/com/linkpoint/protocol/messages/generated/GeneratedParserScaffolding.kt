package com.linkpoint.protocol.messages.generated

import com.linkpoint.protocol.messages.*

/** Auto-generated parser scaffolding from message templates. */
object GeneratedParserScaffolding {
    const val OBJECTUPDATE_ID: Int = 12
    // domain=object returnType=List<ObjectUpdateData>
    fun parseObjectUpdate(payload: ByteArray): List<ObjectUpdateData> = TODO("Implement ObjectUpdate parser")

    const val AVATARANIMATION_ID: Int = 20
    // domain=avatar returnType=AvatarAnimationData?
    fun parseAvatarAnimation(payload: ByteArray): AvatarAnimationData? = TODO("Implement AvatarAnimation parser")

    const val TELEPORTFINISH_ID: Int = -65467
    // domain=teleport returnType=TeleportFinishData?
    fun parseTeleportFinish(payload: ByteArray): TeleportFinishData? = TODO("Implement TeleportFinish parser")

    const val INVENTORYDESCENDENTS_ID: Int = -65397
    // domain=inventory returnType=AdditionalMessageParsers.InventoryDescendentsData?
    fun parseInventoryDescendents(payload: ByteArray): AdditionalMessageParsers.InventoryDescendentsData? = TODO("Implement InventoryDescendents parser")

}