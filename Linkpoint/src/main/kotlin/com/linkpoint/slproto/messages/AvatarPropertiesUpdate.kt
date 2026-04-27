package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesUpdate : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public PropertiesData PropertiesData_Field = PropertiesData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class PropertiesData {
        public ByteArray AboutText
        public Boolean AllowPublish
        public ByteArray FLAboutText
        public UUID FLImageID
        public UUID ImageID
        public Boolean MaturePublish
        public ByteArray ProfileURL
    }

    public AvatarPropertiesUpdate() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return this.PropertiesData_Field.AboutText.length + 34 + 1 + this.PropertiesData_Field.FLAboutText.length + 1 + 1 + 1 + this.PropertiesData_Field.ProfileURL.length + 36
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -82)
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

    fun UnpackPayload(byteBuffer: ByteBuffer) {
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
