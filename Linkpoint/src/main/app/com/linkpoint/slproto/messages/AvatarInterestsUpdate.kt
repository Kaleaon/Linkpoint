package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarInterestsUpdate : SLMessage {
    AgentData AgentData_Field = AgentData()
    PropertiesData PropertiesData_Field = PropertiesData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class PropertiesData {
        ByteArray LanguagesText
        Int SkillsMask
        ByteArray SkillsText
        Int WantToMask
        ByteArray WantToText
    }

    AvatarInterestsUpdate() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.PropertiesData_Field.WantToText.size + 5 + 4 + 1 + this.PropertiesData_Field.SkillsText.size + 1 + this.PropertiesData_Field.LanguagesText.size + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAvatarInterestsUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -81)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.PropertiesData_Field.WantToMask)
        packVariable(byteBuffer, this.PropertiesData_Field.WantToText, 1)
        packInt(byteBuffer, this.PropertiesData_Field.SkillsMask)
        packVariable(byteBuffer, this.PropertiesData_Field.SkillsText, 1)
        packVariable(byteBuffer, this.PropertiesData_Field.LanguagesText, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.WantToMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.WantToText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.SkillsMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.SkillsText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.LanguagesText = unpackVariable(byteBuffer, 1)
    }
}
