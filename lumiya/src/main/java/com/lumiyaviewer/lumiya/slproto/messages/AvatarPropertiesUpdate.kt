package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        byte[] AboutText
        Boolean AllowPublish
        byte[] FLAboutText
        UUID FLImageID
        UUID ImageID
        Boolean MaturePublish
        byte[] ProfileURL
    }

    AvatarPropertiesUpdate() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.PropertiesData_Field.AboutText.length + 34 + 1 + this.PropertiesData_Field.FLAboutText.length + 1 + 1 + 1 + this.PropertiesData_Field.ProfileURL.length + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
