package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesUpdate : SLMessage {
    AgentData AgentData_Field = AgentData()
    PropertiesData PropertiesData_Field = PropertiesData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class PropertiesData {
        ByteArray AboutText
        Boolean AllowPublish
        ByteArray FLAboutText
        UUID FLImageID
        UUID ImageID
        Boolean MaturePublish
        ByteArray ProfileURL
    }

    AvatarPropertiesUpdate() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.PropertiesData_Field.AboutText.size + 34 + 1 + this.PropertiesData_Field.FLAboutText.size + 1 + 1 + 1 + this.PropertiesData_Field.ProfileURL.size + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAvatarPropertiesUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -82)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.PropertiesData_Field.ImageID)
        packUUID(byteBuffer, this.PropertiesData_Field.FLImageID)
        packVariable(byteBuffer, this.PropertiesData_Field.AboutText, 2)
        packVariable(byteBuffer, this.PropertiesData_Field.FLAboutText, 1)
        packBoolean(byteBuffer, this.PropertiesData_Field.AllowPublish)
        packBoolean(byteBuffer, this.PropertiesData_Field.MaturePublish)
        packVariable(byteBuffer, this.PropertiesData_Field.ProfileURL, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.ImageID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.FLImageID = unpackUUID(byteBuffer)
        this.PropertiesData_Field.AboutText = unpackVariable(byteBuffer, 2)
        this.PropertiesData_Field.FLAboutText = unpackVariable(byteBuffer, 1)
        this.PropertiesData_Field.AllowPublish = unpackBoolean(byteBuffer)
        this.PropertiesData_Field.MaturePublish = unpackBoolean(byteBuffer)
        this.PropertiesData_Field.ProfileURL = unpackVariable(byteBuffer, 1)
    }
}
