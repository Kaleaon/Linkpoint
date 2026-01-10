package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarInterestsReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    PropertiesData PropertiesData_Field = PropertiesData()

    class AgentData {
        UUID AgentID
        UUID AvatarID
    }

    class PropertiesData {
        ByteArray LanguagesText
        Int SkillsMask
        ByteArray SkillsText
        Int WantToMask
        ByteArray WantToText
    }

    AvatarInterestsReply() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.PropertiesData_Field.WantToText.size + 5 + 4 + 1 + this.PropertiesData_Field.SkillsText.size + 1 + this.PropertiesData_Field.LanguagesText.size + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleAvatarInterestsReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -84)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.AvatarID)
        packInt(byteBuffer, this.PropertiesData_Field.WantToMask)
        packVariable(byteBuffer, this.PropertiesData_Field.WantToText, 1)
        packInt(byteBuffer, this.PropertiesData_Field.SkillsMask)
        packVariable(byteBuffer, this.PropertiesData_Field.SkillsText, 1)
        packVariable(byteBuffer, this.PropertiesData_Field.LanguagesText, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.WantToMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.WantToText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.SkillsMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.SkillsText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.LanguagesText = unpackVariable(byteBuffer, 1)
    }
}
