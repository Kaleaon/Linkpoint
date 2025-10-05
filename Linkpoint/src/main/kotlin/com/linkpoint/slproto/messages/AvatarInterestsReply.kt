package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarInterestsReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public PropertiesData PropertiesData_Field = PropertiesData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID AvatarID
    }

    @JvmStatic
    class PropertiesData {
        public Byte[] LanguagesText
        public Int SkillsMask
        public Byte[] SkillsText
        public Int WantToMask
        public Byte[] WantToText
    }

    public AvatarInterestsReply() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.PropertiesData_Field.WantToText.length + 5 + 4 + 1 + this.PropertiesData_Field.SkillsText.length + 1 + this.PropertiesData_Field.LanguagesText.length + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarInterestsReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -84)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.AvatarID)
        packInt(byteBuffer, this.PropertiesData_Field.WantToMask)
        packVariable(byteBuffer, this.PropertiesData_Field.WantToText, 1)
        packInt(byteBuffer, this.PropertiesData_Field.SkillsMask)
        packVariable(byteBuffer, this.PropertiesData_Field.SkillsText, 1)
        packVariable(byteBuffer, this.PropertiesData_Field.LanguagesText, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.WantToMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.WantToText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.SkillsMask = unpackInt(byteBuffer)
        this.PropertiesData_Field.SkillsText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.LanguagesText = unpackVariable(byteBuffer, 1)
    }
}
