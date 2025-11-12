package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * AvatarAppearance message (0x9E).
 */
class AvatarAppearanceMessage : SLMessage() {
    var senderId: UUID = UUID.randomUUID()
    var isTrial: Boolean = false
    var textureEntry: ByteArray = ByteArray(0)

    data class VisualParam(var value: Int = 0)

    data class AppearanceData(
        var appearanceVersion: Int = 0,
        var cofVersion: Int = 0,
        var flags: Int = 0,
    )

    val visualParams: MutableList<VisualParam> = mutableListOf()
    val appearanceData: MutableList<AppearanceData> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, senderId)
        packBoolean(buffer, isTrial)
        packVariable(buffer, textureEntry, 2)

        require(visualParams.size <= 0xFF) { "Visual param list too large (${visualParams.size})" }
        packByte(buffer, visualParams.size)
        visualParams.forEach { param ->
            packByte(buffer, param.value and 0xFF)
        }

        require(appearanceData.size <= 0xFF) { "Appearance data list too large (${appearanceData.size})" }
        packByte(buffer, appearanceData.size)
        appearanceData.forEach { entry ->
            packByte(buffer, entry.appearanceVersion and 0xFF)
            packInt(buffer, entry.cofVersion)
            packInt(buffer, entry.flags)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        senderId = unpackUUID(buffer)
        isTrial = unpackBoolean(buffer)
        textureEntry = unpackVariable(buffer, 2)

        val visualCount = unpackByte(buffer)
        visualParams.clear()
        repeat(visualCount) {
            visualParams += VisualParam(unpackByte(buffer))
        }

        val appearanceCount = unpackByte(buffer)
        appearanceData.clear()
        repeat(appearanceCount) {
            val appearanceVersion = unpackByte(buffer)
            val cofVersion = unpackInt(buffer)
            val flags = unpackInt(buffer)
            appearanceData += AppearanceData(appearanceVersion, cofVersion, flags)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AVATAR_APPEARANCE

    override fun getMessageName(): String = "AvatarAppearance"
}

/**
 * AvatarPropertiesRequest message (0xA0).
 */
class AvatarPropertiesRequestMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var avatarId: UUID = UUID.randomUUID()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, avatarId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        avatarId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AVATAR_PROPERTIES_REQUEST

    override fun getMessageName(): String = "AvatarPropertiesRequest"
}

/**
 * AvatarPropertiesReply message (0xA1).
 */
class AvatarPropertiesReplyMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var avatarId: UUID = UUID.randomUUID()

    data class PropertiesData(
        var imageId: UUID = UUID.randomUUID(),
        var flImageId: UUID = UUID.randomUUID(),
        var partnerId: UUID = UUID.randomUUID(),
        var aboutText: String = "",
        var flAboutText: String = "",
        var bornOn: String = "",
        var profileUrl: String = "",
        var charterMember: String = "",
        var flags: Int = 0,
    )

    var properties: PropertiesData = PropertiesData()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, avatarId)
        packUUID(buffer, properties.imageId)
        packUUID(buffer, properties.flImageId)
        packUUID(buffer, properties.partnerId)
        packVariable(buffer, properties.aboutText.toByteArray(StandardCharsets.UTF_8), 2)
        packVariable(buffer, properties.flAboutText.toByteArray(StandardCharsets.UTF_8), 1)
        packVariable(buffer, properties.bornOn.toByteArray(StandardCharsets.UTF_8), 1)
        packVariable(buffer, properties.profileUrl.toByteArray(StandardCharsets.UTF_8), 1)
        packVariable(buffer, properties.charterMember.toByteArray(StandardCharsets.UTF_8), 1)
        packInt(buffer, properties.flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        avatarId = unpackUUID(buffer)
        val imageId = unpackUUID(buffer)
        val flImageId = unpackUUID(buffer)
        val partnerId = unpackUUID(buffer)
        val about = String(unpackVariable(buffer, 2), StandardCharsets.UTF_8)
        val flAbout = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
        val bornOn = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
        val profileUrl = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
        val charterMember = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
        val flags = unpackInt(buffer)

        properties =
            PropertiesData(
                imageId = imageId,
                flImageId = flImageId,
                partnerId = partnerId,
                aboutText = about,
                flAboutText = flAbout,
                bornOn = bornOn,
                profileUrl = profileUrl,
                charterMember = charterMember,
                flags = flags,
            )
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AVATAR_PROPERTIES_REPLY

    override fun getMessageName(): String = "AvatarPropertiesReply"
}
