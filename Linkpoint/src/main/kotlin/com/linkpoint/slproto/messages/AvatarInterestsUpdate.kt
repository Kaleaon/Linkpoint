package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarInterestsUpdate : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public PropertiesData PropertiesData_Field = PropertiesData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class PropertiesData {
        public ByteArray LanguagesText
        public Int SkillsMask
        public ByteArray SkillsText
        public Int WantToMask
        public ByteArray WantToText
    }

    public AvatarInterestsUpdate() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return this.PropertiesData_Field.WantToText.length + 5 + 4 + 1 + this.PropertiesData_Field.SkillsText.length + 1 + this.PropertiesData_Field.LanguagesText.length + 36
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarInterestsUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -81)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.PropertiesData_Field.WantToMask)
        packVariable(byteBuffer, this.PropertiesData_Field.WantToText, 1)
        packInt(byteBuffer, this.PropertiesData_Field.SkillsMask)
        packVariable(byteBuffer, this.PropertiesData_Field.SkillsText, 1)
        packVariable(byteBuffer, this.PropertiesData_Field.LanguagesText, 1)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.WantToMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.WantToText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.SkillsMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.SkillsText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.LanguagesText = unpackVariable(byteBuffer, 1)
    }
}
