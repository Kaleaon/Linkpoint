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
        ByteArray AboutText
        ByteArray BornOn
        ByteArray CharterMember
        ByteArray FLAboutText
        UUID FLImageID
        Int Flags
        UUID ImageID
        UUID PartnerID
        ByteArray ProfileURL
    }

    AvatarPropertiesReply() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.PropertiesData_Field.AboutText.size + 50 + 1 + this.PropertiesData_Field.FLAboutText.size + 1 + this.PropertiesData_Field.BornOn.size + 1 + this.PropertiesData_Field.ProfileURL.size + 1 + this.PropertiesData_Field.CharterMember.size + 4 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAvatarPropertiesReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
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

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
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
