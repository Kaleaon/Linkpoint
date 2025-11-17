package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    PropertiesData PropertiesData_Field = PropertiesData()

    class AgentData {
        UUID AgentID
        UUID AvatarID
    }

    class PropertiesData {
        byte[] AboutText
        byte[] BornOn
        byte[] CharterMember
        byte[] FLAboutText
        UUID FLImageID
        Int Flags
        UUID ImageID
        UUID PartnerID
        byte[] ProfileURL
    }

    AvatarPropertiesReply() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.PropertiesData_Field.AboutText.length + 50 + 1 + this.PropertiesData_Field.FLAboutText.length + 1 + this.PropertiesData_Field.BornOn.length + 1 + this.PropertiesData_Field.ProfileURL.length + 1 + this.PropertiesData_Field.CharterMember.length + 4 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -85)
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
