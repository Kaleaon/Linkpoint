package com.linkpoint.slproto.avatar

import com.linkpoint.slproto.types.LLVector3
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * Converted from Java to Kotlin
 * SLAvatarAppearance - Manages avatar appearance, wearables, and attachments
 */
class SLAvatarAppearance(private val avatarUUID: UUID) {

    companion object {
        // Wearable types
        const val WEARABLE_SHAPE = 0
        const val WEARABLE_SKIN = 1
        const val WEARABLE_HAIR = 2
        const val WEARABLE_EYES = 3
        const val WEARABLE_SHIRT = 4
        const val WEARABLE_PANTS = 5
        const val WEARABLE_SHOES = 6
        const val WEARABLE_SOCKS = 7
        const val WEARABLE_JACKET = 8
        const val WEARABLE_GLOVES = 9
        const val WEARABLE_UNDERSHIRT = 10
        const val WEARABLE_UNDERPANTS = 11
        const val WEARABLE_SKIRT = 12
        const val WEARABLE_ALPHA = 13
        const val WEARABLE_TATTOO = 14
        const val WEARABLE_PHYSICS = 15
        const val WEARABLE_COUNT = 16

        // Attachment points
        const val ATTACH_CHEST = 1
        const val ATTACH_HEAD = 2
        const val ATTACH_LSHOULDER = 3
        const val ATTACH_RSHOULDER = 4
        const val ATTACH_LHAND = 5
        const val ATTACH_RHAND = 6
        const val ATTACH_LFOOT = 7
        const val ATTACH_RFOOT = 8
        const val ATTACH_BACK = 9
        const val ATTACH_PELVIS = 10
        const val ATTACH_MOUTH = 11
        const val ATTACH_CHIN = 12
        const val ATTACH_LEAR = 13
        const val ATTACH_REAR = 14
        const val ATTACH_LEYE = 15
        const val ATTACH_REYE = 16
        const val ATTACH_NOSE = 17
        const val ATTACH_RUARM = 18
        const val ATTACH_RLARM = 19
        const val ATTACH_LUARM = 20
        const val ATTACH_LLARM = 21
        const val ATTACH_RHIP = 22
        const val ATTACH_RULEG = 23
        const val ATTACH_RLLEG = 24
        const val ATTACH_LHIP = 25
        const val ATTACH_LULEG = 26
        const val ATTACH_LLLEG = 27
        const val ATTACH_BELLY = 28
        const val ATTACH_RPEC = 29
        const val ATTACH_LPEC = 30
    }

    data class WearableItem(
        val itemUUID: UUID,
        val assetUUID: UUID,
        val type: Int,
        val name: String
    )

    data class AttachmentItem(
        val itemUUID: UUID,
        val objectUUID: UUID,
        val attachmentPoint: Int,
        val name: String,
        val position: LLVector3? = null,
        val rotation: LLVector3? = null
    )

    data class VisualParam(
        val paramId: Int,
        val weight: Float
    )

    private val wearables = mutableMapOf<Int, WearableItem>()
    private val attachments = mutableMapOf<Int, MutableList<AttachmentItem>>()
    private val visualParams = mutableMapOf<Int, VisualParam>()
    private val textures = mutableMapOf<Int, UUID>()

    fun addWearable(wearable: WearableItem) {
        wearables[wearable.type] = wearable
    }

    fun removeWearable(type: Int) {
        wearables.remove(type)
    }

    fun getWearable(type: Int): WearableItem? {
        return wearables[type]
    }

    fun getAllWearables(): List<WearableItem> {
        return wearables.values.toList()
    }

    fun addAttachment(attachment: AttachmentItem) {
        val list = attachments.getOrPut(attachment.attachmentPoint) { mutableListOf() }
        list.add(attachment)
    }

    fun removeAttachment(attachmentPoint: Int, itemUUID: UUID) {
        attachments[attachmentPoint]?.removeIf { it.itemUUID == itemUUID }
    }

    fun getAttachments(attachmentPoint: Int): List<AttachmentItem>? {
        return attachments[attachmentPoint]
    }

    fun getAllAttachments(): List<AttachmentItem> {
        return attachments.values.flatten()
    }

    fun setVisualParam(paramId: Int, weight: Float) {
        visualParams[paramId] = VisualParam(paramId, weight)
    }

    fun getVisualParam(paramId: Int): VisualParam? {
        return visualParams[paramId]
    }

    fun getAllVisualParams(): List<VisualParam> {
        return visualParams.values.toList()
    }

    fun setTexture(textureIndex: Int, textureUUID: UUID) {
        textures[textureIndex] = textureUUID
    }

    fun getTexture(textureIndex: Int): UUID? {
        return textures[textureIndex]
    }

    fun getAllTextures(): Map<Int, UUID> {
        return textures.toMap()
    }

    fun isItemWorn(itemUUID: UUID): Boolean {
        return wearables.values.any { it.itemUUID == itemUUID } ||
               attachments.values.flatten().any { it.itemUUID == itemUUID }
    }

    fun clear() {
        wearables.clear()
        attachments.clear()
        visualParams.clear()
        textures.clear()
    }

    fun getAvatarHeight(): Float {
        // Calculate avatar height based on visual parameters
        val heightParam = visualParams[33] // Avatar height param
        return heightParam?.let { 1.6f + (it.weight * 0.4f) } ?: 1.8f
    }

    fun rebake() {
        // Trigger avatar rebake
    }
}
