package com.linkpoint.eventbus.events

import com.linkpoint.protocol.messages.AdditionalMessageParsers
import java.util.UUID

/**
 * Event emitted when an AVATAR_PROPERTIES_REPLY packet is received.
 */
data class AvatarPropertiesReplyEvent(
    val agentId: UUID,
    val avatarId: UUID,
    val imageId: UUID,
    val firstLifeImageId: UUID,
    val partnerId: UUID,
    val aboutText: String,
    val firstLifeAboutText: String,
    val bornOn: String,
    val profileUrl: String,
    val flags: Int,
    val charterMember: ByteArray = ByteArray(0)
) {
    constructor(data: AdditionalMessageParsers.AvatarPropertiesReplyData) : this(
        agentId = data.agentID,
        avatarId = data.avatarID,
        imageId = data.imageID,
        firstLifeImageId = data.flImageID,
        partnerId = data.partnerID,
        aboutText = data.aboutText,
        firstLifeAboutText = data.flAboutText,
        bornOn = data.bornOn,
        profileUrl = data.profileURL,
        flags = data.flags,
        charterMember = data.charterMember
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AvatarPropertiesReplyEvent
        return agentId == other.agentId && avatarId == other.avatarId && imageId == other.imageId &&
                firstLifeImageId == other.firstLifeImageId && partnerId == other.partnerId &&
                aboutText == other.aboutText && firstLifeAboutText == other.firstLifeAboutText &&
                bornOn == other.bornOn && profileUrl == other.profileUrl && flags == other.flags
    }

    override fun hashCode(): Int {
        var result = agentId.hashCode()
        result = 31 * result + avatarId.hashCode()
        result = 31 * result + imageId.hashCode()
        result = 31 * result + firstLifeImageId.hashCode()
        result = 31 * result + partnerId.hashCode()
        result = 31 * result + aboutText.hashCode()
        result = 31 * result + firstLifeAboutText.hashCode()
        result = 31 * result + bornOn.hashCode()
        result = 31 * result + profileUrl.hashCode()
        result = 31 * result + flags
        return result
    }
}

/**
 * Event emitted when an AVATAR_PROPERTIES_UPDATE packet is received.
 */
data class AvatarPropertiesUpdateEvent(
    val agentId: UUID,
    val sessionId: UUID,
    val imageId: UUID,
    val firstLifeImageId: UUID,
    val aboutText: String,
    val firstLifeAboutText: String,
    val allowPublish: Boolean,
    val maturePublish: Boolean,
    val profileUrl: String
) {
    constructor(data: AdditionalMessageParsers.AvatarPropertiesUpdateData) : this(
        agentId = data.agentID,
        sessionId = data.sessionID,
        imageId = data.imageID,
        firstLifeImageId = data.flImageID,
        aboutText = data.aboutText,
        firstLifeAboutText = data.flAboutText,
        allowPublish = data.allowPublish,
        maturePublish = data.maturePublish,
        profileUrl = data.profileURL
    )

    companion object {
        fun fromPayload(payload: ByteArray): AvatarPropertiesUpdateEvent? {
            val data = AdditionalMessageParsers.parseAvatarPropertiesUpdate(payload)
            return if (data != null) AvatarPropertiesUpdateEvent(data) else null
        }
    }
}
