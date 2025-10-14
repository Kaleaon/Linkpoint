package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        byte[] LanguagesText
        Int SkillsMask
        byte[] SkillsText
        Int WantToMask
        byte[] WantToText
    }

    AvatarInterestsReply() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.PropertiesData_Field.WantToText.length + 5 + 4 + 1 + this.PropertiesData_Field.SkillsText.length + 1 + this.PropertiesData_Field.LanguagesText.length + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarInterestsReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.WantToMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.WantToText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.SkillsMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.SkillsText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.LanguagesText = unpackVariable(byteBuffer, 1)
    }
}
