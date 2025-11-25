package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AvatarInterestsUpdate : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var PropertiesData_Field: PropertiesData = PropertiesData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class PropertiesData {
        var LanguagesText: ByteArray = ByteArray(0)
        var SkillsMask: Int = 0
        var SkillsText: ByteArray = ByteArray(0)
        var WantToMask: Int = 0
        var WantToText: ByteArray = ByteArray(0)
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return PropertiesData_Field.WantToText.size + 5 + 4 + 1 + PropertiesData_Field.SkillsText.size + 1 + PropertiesData_Field.LanguagesText.size + 36
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarInterestsUpdate(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-81).toByte())
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.SessionID!!)
        packInt(byteBuffer, PropertiesData_Field.WantToMask)
        packVariable(byteBuffer, PropertiesData_Field.WantToText, 1)
        packInt(byteBuffer, PropertiesData_Field.SkillsMask)
        packVariable(byteBuffer, PropertiesData_Field.SkillsText, 1)
        packVariable(byteBuffer, PropertiesData_Field.LanguagesText, 1)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.SessionID = unpackUUID(byteBuffer)
        PropertiesData_Field.WantToMask = unpackInt(byteBuffer)
        PropertiesData_Field.WantToText = unpackVariable(byteBuffer, 1)
        PropertiesData_Field.SkillsMask = unpackInt(byteBuffer)
        PropertiesData_Field.SkillsText = unpackVariable(byteBuffer, 1)
        PropertiesData_Field.LanguagesText = unpackVariable(byteBuffer, 1)
    }
}
