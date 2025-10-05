package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public PropertiesData PropertiesData_Field = PropertiesData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID AvatarID
    }

    @JvmStatic
    class PropertiesData {
        public Byte[] AboutText
        public Byte[] BornOn
        public Byte[] CharterMember
        public Byte[] FLAboutText
        public UUID FLImageID
        public Int Flags
        public UUID ImageID
        public UUID PartnerID
        public Byte[] ProfileURL
    }

    public AvatarPropertiesReply() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.PropertiesData_Field.AboutText.length + 50 + 1 + this.PropertiesData_Field.FLAboutText.length + 1 + this.PropertiesData_Field.BornOn.length + 1 + this.PropertiesData_Field.ProfileURL.length + 1 + this.PropertiesData_Field.CharterMember.length + 4 + 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -85)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.AvatarID)
        packUUID(byteBuffer, this.PropertiesData_Field.ImageID)
        packUUID(byteBuffer, this.PropertiesData_Field.FLImageID)
        packUUID(byteBuffer, this.PropertiesData_Field.PartnerID)
        packVariable(byteBuffer, this.PropertiesData_Field.AboutText, 2)
        packVariable(byteBuffer, this.PropertiesData_Field.FLAboutText, 1)
        packVariable(byteBuffer, this.PropertiesData_Field.BornOn, 1)
        packVariable(byteBuffer, this.PropertiesData_Field.ProfileURL, 1)
        packVariable(byteBuffer, this.PropertiesData_Field.CharterMember, 1)
        packInt(byteBuffer, this.PropertiesData_Field.Flags)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.ImageID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.FLImageID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.PartnerID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.AboutText = unpackVariable(byteBuffer, 2)
        this.PropertiesData_Field.FLAboutText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.BornOn = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.ProfileURL = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.CharterMember = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.Flags = unpackInt(byteBuffer)
    }
}
