package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.nio.charset.Charsets
import java.util.*

/**
 * Agent update message - sent to update agent position and state
 */
class AgentUpdateMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var bodyRotation: LLQuaternion = LLQuaternion()
    var headRotation: LLQuaternion = LLQuaternion()
    var state: Byte = 0
    var cameraCenter: LLVector3 = LLVector3()
    var cameraAtAxis: LLVector3 = LLVector3()
    var cameraLeftAxis: LLVector3 = LLVector3()
    var cameraUpAxis: LLVector3 = LLVector3()
    var far: Float = 0f
    var controlFlags: Int = 0
    var flags: Byte = 0

    override fun packPayload(buffer: ByteBuffer) {
        // Agent data block
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)

        bodyRotation.pack(buffer)
        headRotation.pack(buffer)

        buffer.put(state)

        cameraCenter.pack(buffer)
        cameraAtAxis.pack(buffer)
        cameraLeftAxis.pack(buffer)
        cameraUpAxis.pack(buffer)

        buffer.putFloat(far)
        buffer.putInt(controlFlags)
        buffer.put(flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        // Agent data block
        val agentMsb = buffer.getLong()
        val agentLsb = buffer.getLong()
        agentId = UUID(agentMsb, agentLsb)

        val sessionMsb = buffer.getLong()
        val sessionLsb = buffer.getLong()
        sessionId = UUID(sessionMsb, sessionLsb)

        bodyRotation = LLQuaternion.unpack(buffer)
        headRotation = LLQuaternion.unpack(buffer)

        state = buffer.get()

        cameraCenter = LLVector3.unpack(buffer)
        cameraAtAxis = LLVector3.unpack(buffer)
        cameraLeftAxis = LLVector3.unpack(buffer)
        cameraUpAxis = LLVector3.unpack(buffer)

        far = buffer.getFloat()
        controlFlags = buffer.getInt()
        flags = buffer.get()
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_UPDATE

    override fun getMessageName(): String = "AgentUpdate"
}

/**
 * Agent animation message
 */
class AgentAnimationMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()

    data class AnimationBlock(
        var animId: UUID = UUID.randomUUID(),
        var startAnim: Boolean = true,
    )

    val animationList = mutableListOf<AnimationBlock>()

    override fun packPayload(buffer: ByteBuffer) {
        // Agent data
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)

        // Animation list
        buffer.put(animationList.size.toByte())
        for (anim in animationList) {
            buffer.putLong(anim.animId.mostSignificantBits)
            buffer.putLong(anim.animId.leastSignificantBits)
            buffer.put(if (anim.startAnim) 1 else 0)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        // Agent data
        val agentMsb = buffer.getLong()
        val agentLsb = buffer.getLong()
        agentId = UUID(agentMsb, agentLsb)

        val sessionMsb = buffer.getLong()
        val sessionLsb = buffer.getLong()
        sessionId = UUID(sessionMsb, sessionLsb)

        // Animation list
        val count = buffer.get().toInt() and 0xFF
        animationList.clear()
        for (i in 0 until count) {
            val animMsb = buffer.getLong()
            val animLsb = buffer.getLong()
            val animId = UUID(animMsb, animLsb)
            val startAnim = buffer.get() != 0.toByte()
            animationList.add(AnimationBlock(animId, startAnim))
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_ANIMATION

    override fun getMessageName(): String = "AgentAnimation"
}

/**
 * Agent wearables request (0x7D)
 */
class AgentWearablesRequestMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_WEARABLES_REQUEST

    override fun getMessageName(): String = "AgentWearablesRequest"
}

/**
 * Agent wearables update (0x7E)
 */
class AgentWearablesUpdateMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var serialNum: Int = 0

    data class WearableData(
        var itemId: UUID = UUID.randomUUID(),
        var assetId: UUID = UUID.randomUUID(),
        var wearableType: Int = 0,
    )

    val wearables: MutableList<WearableData> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, serialNum)
        require(wearables.size <= 0xFF) { "Wearable list too large (${wearables.size})" }
        packByte(buffer, wearables.size)
        wearables.forEach { block ->
            packUUID(buffer, block.itemId)
            packUUID(buffer, block.assetId)
            packByte(buffer, block.wearableType and 0xFF)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        serialNum = unpackInt(buffer)
        val count = unpackByte(buffer)
        wearables.clear()
        repeat(count) {
            wearables += WearableData(
                itemId = unpackUUID(buffer),
                assetId = unpackUUID(buffer),
                wearableType = unpackByte(buffer),
            )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_WEARABLES_UPDATE

    override fun getMessageName(): String = "AgentWearablesUpdate"
}

/**
 * Agent cached texture request (0x80)
 */
class AgentCachedTextureMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var serialNum: Int = 0

    data class WearableData(
        var id: UUID = UUID.randomUUID(),
        var textureIndex: Int = 0,
    )

    val textures: MutableList<WearableData> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, serialNum)
        require(textures.size <= 0xFF) { "Texture list too large (${textures.size})" }
        packByte(buffer, textures.size)
        textures.forEach { entry ->
            packUUID(buffer, entry.id)
            packByte(buffer, entry.textureIndex and 0xFF)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        serialNum = unpackInt(buffer)
        val count = unpackByte(buffer)
        textures.clear()
        repeat(count) {
            textures += WearableData(
                id = unpackUUID(buffer),
                textureIndex = unpackByte(buffer),
            )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_CACHED_TEXTURE

    override fun getMessageName(): String = "AgentCachedTexture"
}

/**
 * Agent cached texture response (0x81)
 */
class AgentCachedTextureResponseMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var serialNum: Int = 0

    data class WearableData(
        var textureId: UUID = UUID.randomUUID(),
        var textureIndex: Int = 0,
        var hostName: String = "",
    )

    val textures: MutableList<WearableData> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, serialNum)
        require(textures.size <= 0xFF) { "Texture response list too large (${textures.size})" }
        packByte(buffer, textures.size)
        textures.forEach { entry ->
            packUUID(buffer, entry.textureId)
            packByte(buffer, entry.textureIndex and 0xFF)
            packVariable(buffer, entry.hostName.toByteArray(Charsets.UTF_8), 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        serialNum = unpackInt(buffer)
        val count = unpackByte(buffer)
        textures.clear()
        repeat(count) {
            val textureId = unpackUUID(buffer)
            val textureIndex = unpackByte(buffer)
            val host = String(unpackVariable(buffer, 1), Charsets.UTF_8)
            textures += WearableData(textureId, textureIndex, host)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_CACHED_TEXTURE_RESPONSE

    override fun getMessageName(): String = "AgentCachedTextureResponse"
}

/**
 * Agent group data update (0x85)
 */
class AgentGroupDataUpdateMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()

    data class GroupData(
        var groupId: UUID = UUID.randomUUID(),
        var groupPowers: Long = 0L,
        var acceptNotices: Boolean = false,
        var insigniaId: UUID = UUID.randomUUID(),
        var contribution: Int = 0,
        var groupName: String = "",
    )

    val groups: MutableList<GroupData> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        require(groups.size <= 0xFF) { "Group list too large (${groups.size})" }
        packByte(buffer, groups.size)
        groups.forEach { entry ->
            packUUID(buffer, entry.groupId)
            packLong(buffer, entry.groupPowers)
            packBoolean(buffer, entry.acceptNotices)
            packUUID(buffer, entry.insigniaId)
            packInt(buffer, entry.contribution)
            packVariable(buffer, entry.groupName.toByteArray(Charsets.UTF_8), 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        val count = unpackByte(buffer)
        groups.clear()
        repeat(count) {
            val groupId = unpackUUID(buffer)
            val powers = unpackLong(buffer)
            val notices = unpackBoolean(buffer)
            val insignia = unpackUUID(buffer)
            val contribution = unpackInt(buffer)
            val name = String(unpackVariable(buffer, 1), Charsets.UTF_8)
            groups += GroupData(groupId, powers, notices, insignia, contribution, name)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_GROUP_DATA_UPDATE

    override fun getMessageName(): String = "AgentGroupDataUpdate"
}

/**
 * Agent height/width (0x53)
 */
class AgentHeightWidthMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var circuitCode: Int = 0
    var genCounter: Int = 0
    var height: Int = 0
    var width: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, circuitCode)
        packInt(buffer, genCounter)
        require(height in 0..0xFFFF) { "Height out of range ($height)" }
        require(width in 0..0xFFFF) { "Width out of range ($width)" }
        packUInt16(buffer, height)
        packUInt16(buffer, width)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        circuitCode = unpackInt(buffer)
        genCounter = unpackInt(buffer)
        height = unpackUInt16(buffer)
        width = unpackUInt16(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_HEIGHT_WIDTH

    override fun getMessageName(): String = "AgentHeightWidth"
}

/**
 * Agent is now wearing (0x7F)
 */
class AgentIsNowWearingMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()

    data class WearableData(
        var itemId: UUID = UUID.randomUUID(),
        var wearableType: Int = 0,
    )

    val wearables: MutableList<WearableData> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(wearables.size <= 0xFF) { "Wearable data list too large (${wearables.size})" }
        packByte(buffer, wearables.size)
        wearables.forEach { entry ->
            packUUID(buffer, entry.itemId)
            packByte(buffer, entry.wearableType and 0xFF)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        val count = unpackByte(buffer)
        wearables.clear()
        repeat(count) {
            wearables += WearableData(
                itemId = unpackUUID(buffer),
                wearableType = unpackByte(buffer),
            )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_IS_NOW_WEARING

    override fun getMessageName(): String = "AgentIsNowWearing"
}

/**
 * Agent movement complete (0xFA)
 */
class AgentMovementCompleteMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var position: LLVector3 = LLVector3()
    var lookAt: LLVector3 = LLVector3()
    var regionHandle: Long = 0L
    var timestamp: Int = 0
    var channelVersion: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        position.pack(buffer)
        lookAt.pack(buffer)
        packLong(buffer, regionHandle)
        packInt(buffer, timestamp)
        packVariable(buffer, channelVersion, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        position = LLVector3.unpack(buffer)
        lookAt = LLVector3.unpack(buffer)
        regionHandle = unpackLong(buffer)
        timestamp = unpackInt(buffer)
        channelVersion = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_MOVEMENT_COMPLETE

    override fun getMessageName(): String = "AgentMovementComplete"
}

/**
 * Agent throttle (0x51)
 */
class AgentThrottleMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var circuitCode: Int = 0
    var genCounter: Int = 0
    var throttles: ByteArray = ByteArray(0)

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, circuitCode)
        packInt(buffer, genCounter)
        packVariable(buffer, throttles, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        circuitCode = unpackInt(buffer)
        genCounter = unpackInt(buffer)
        throttles = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_THROTTLE

    override fun getMessageName(): String = "AgentThrottle"
}

/**
 * Agent data update (0x83)
 */
class AgentDataUpdateMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var activeGroupId: UUID = UUID.randomUUID()
    var groupPowers: Long = 0L
    var firstName: String = ""
    var lastName: String = ""
    var groupTitle: String = ""
    var groupName: String = ""

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packVariable(buffer, firstName.toByteArray(Charsets.UTF_8), 1)
        packVariable(buffer, lastName.toByteArray(Charsets.UTF_8), 1)
        packVariable(buffer, groupTitle.toByteArray(Charsets.UTF_8), 1)
        packUUID(buffer, activeGroupId)
        packLong(buffer, groupPowers)
        packVariable(buffer, groupName.toByteArray(Charsets.UTF_8), 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        firstName = String(unpackVariable(buffer, 1), Charsets.UTF_8)
        lastName = String(unpackVariable(buffer, 1), Charsets.UTF_8)
        groupTitle = String(unpackVariable(buffer, 1), Charsets.UTF_8)
        activeGroupId = unpackUUID(buffer)
        groupPowers = unpackLong(buffer)
        groupName = String(unpackVariable(buffer, 1), Charsets.UTF_8)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_DATA_UPDATE

    override fun getMessageName(): String = "AgentDataUpdate"
}

/**
 * Agent data update request (0x82)
 */
class AgentDataUpdateRequestMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_DATA_UPDATE_REQUEST

    override fun getMessageName(): String = "AgentDataUpdateRequest"
}

/**
 * Agent drop group (0x86)
 */
class AgentDropGroupMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var groupId: UUID = UUID.randomUUID()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_DROP_GROUP

    override fun getMessageName(): String = "AgentDropGroup"
}

/**
 * Agent alert (0x87)
 */
class AgentAlertMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var modal: Boolean = false
    var message: String = ""

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packBoolean(buffer, modal)
        packVariable(buffer, message.toByteArray(Charsets.UTF_8), 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        modal = unpackBoolean(buffer)
        message = String(unpackVariable(buffer, 1), Charsets.UTF_8)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_ALERT

    override fun getMessageName(): String = "AgentAlert"
}

/**
 * Agent field of view (0x52)
 */
class AgentFovMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var circuitCode: Int = 0
    var genCounter: Int = 0
    var verticalAngle: Float = 0f

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, circuitCode)
        packInt(buffer, genCounter)
        packFloat(buffer, verticalAngle)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        circuitCode = unpackInt(buffer)
        genCounter = unpackInt(buffer)
        verticalAngle = unpackFloat(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_FOV

    override fun getMessageName(): String = "AgentFOV"
}

/**
 * Agent pause (0x4E)
 */
class AgentPauseMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var serialNum: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, serialNum)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        serialNum = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_PAUSE

    override fun getMessageName(): String = "AgentPause"
}

/**
 * Agent resume (0x4F)
 */
class AgentResumeMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var serialNum: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, serialNum)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        serialNum = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_RESUME

    override fun getMessageName(): String = "AgentResume"
}

/**
 * Agent quit copy (0x55)
 */
class AgentQuitCopyMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var viewerCircuitCode: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, viewerCircuitCode)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        viewerCircuitCode = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_QUIT_COPY

    override fun getMessageName(): String = "AgentQuitCopy"
}

/**
 * Agent set appearance (0x54)
 */
class AgentSetAppearanceMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var serialNum: Int = 0
    var size: LLVector3 = LLVector3()
    val wearables: MutableList<WearableData> = mutableListOf()
    var textureEntry: ByteArray = ByteArray(0)
    val visualParams: MutableList<Int> = mutableListOf()

    data class WearableData(
        var cacheId: UUID = UUID.randomUUID(),
        var textureIndex: Int = 0,
    )

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, serialNum)
        size.pack(buffer)
        require(wearables.size <= 0xFF) { "Wearable block too large (${wearables.size})" }
        packByte(buffer, wearables.size)
        wearables.forEach { entry ->
            packUUID(buffer, entry.cacheId)
            packByte(buffer, entry.textureIndex and 0xFF)
        }
        packVariable(buffer, textureEntry, 2)
        require(visualParams.size <= 0xFF) { "Visual param list too large (${visualParams.size})" }
        packByte(buffer, visualParams.size)
        visualParams.forEach { value ->
            require(value in 0..0xFF) { "Visual param out of range ($value)" }
            packByte(buffer, value)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        serialNum = unpackInt(buffer)
        size = LLVector3.unpack(buffer)
        val wearableCount = unpackByte(buffer)
        wearables.clear()
        repeat(wearableCount) {
            wearables += WearableData(
                cacheId = unpackUUID(buffer),
                textureIndex = unpackByte(buffer),
            )
        }
        textureEntry = unpackVariable(buffer, 2)
        val paramCount = unpackByte(buffer)
        visualParams.clear()
        repeat(paramCount) {
            visualParams += unpackByte(buffer)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_SET_APPEARANCE

    override fun getMessageName(): String = "AgentSetAppearance"
}

/**
 * Agent request sit (0x1E)
 */
class AgentRequestSitMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var targetId: UUID = UUID.randomUUID()
    var offset: LLVector3 = LLVector3()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, targetId)
        offset.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        targetId = unpackUUID(buffer)
        offset = LLVector3.unpack(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_REQUEST_SIT

    override fun getMessageName(): String = "AgentRequestSit"
}

/**
 * Agent sit (0x1F)
 */
class AgentSitMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_SIT

    override fun getMessageName(): String = "AgentSit"
}

/**
 * Child agent position update (0x1B)
 */
class ChildAgentPositionUpdateMessage : SLMessage() {
    var regionHandle: Long = 0L
    var viewerCircuitCode: Int = 0
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var agentPosition: LLVector3 = LLVector3()
    var agentVelocity: LLVector3 = LLVector3()
    var cameraCenter: LLVector3 = LLVector3()
    var agentSize: LLVector3 = LLVector3()
    var atAxis: LLVector3 = LLVector3()
    var leftAxis: LLVector3 = LLVector3()
    var upAxis: LLVector3 = LLVector3()
    var changedGrid: Boolean = false

    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, regionHandle)
        packInt(buffer, viewerCircuitCode)
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        agentPosition.pack(buffer)
        agentVelocity.pack(buffer)
        cameraCenter.pack(buffer)
        agentSize.pack(buffer)
        atAxis.pack(buffer)
        leftAxis.pack(buffer)
        upAxis.pack(buffer)
        packBoolean(buffer, changedGrid)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = unpackLong(buffer)
        viewerCircuitCode = unpackInt(buffer)
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        agentPosition = LLVector3.unpack(buffer)
        agentVelocity = LLVector3.unpack(buffer)
        cameraCenter = LLVector3.unpack(buffer)
        agentSize = LLVector3.unpack(buffer)
        atAxis = LLVector3.unpack(buffer)
        leftAxis = LLVector3.unpack(buffer)
        upAxis = LLVector3.unpack(buffer)
        changedGrid = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CHILD_AGENT_POSITION_UPDATE

    override fun getMessageName(): String = "ChildAgentPositionUpdate"
}
