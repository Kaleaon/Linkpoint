package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesUpdate : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var PropertiesData_Field: PropertiesData = PropertiesData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class PropertiesData {
        var AboutText: ByteArray = ByteArray(0)
        var AllowPublish: Boolean = false
        var FLAboutText: ByteArray = ByteArray(0)
        var FLImageID: UUID? = null
        var ImageID: UUID? = null
        var MaturePublish: Boolean = false
        var ProfileURL: ByteArray = ByteArray(0)
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return PropertiesData_Field.AboutText.size + 34 + 1 + PropertiesData_Field.FLAboutText.size + 1 + 1 + 1 + PropertiesData_Field.ProfileURL.size + 36
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesUpdate(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-82).toByte())
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.SessionID!!)
        packUUID(byteBuffer, PropertiesData_Field.ImageID!!)
        packUUID(byteBuffer, PropertiesData_Field.FLImageID!!)
        packVariable(byteBuffer, PropertiesData_Field.AboutText, 2)
        packVariable(byteBuffer, PropertiesData_Field.FLAboutText, 1)
        packBoolean(byteBuffer, PropertiesData_Field.AllowPublish)
        packBoolean(byteBuffer, PropertiesData_Field.MaturePublish)
        packVariable(byteBuffer, PropertiesData_Field.ProfileURL, 1)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.SessionID = unpackUUID(byteBuffer)
        PropertiesData_Field.ImageID = unpackUUID(byteBuffer)
        PropertiesData_Field.FLImageID = unpackUUID(byteBuffer)
        PropertiesData_Field.AboutText = unpackVariable(byteBuffer, 2)
        PropertiesData_Field.FLAboutText = unpackVariable(byteBuffer, 1)
        PropertiesData_Field.AllowPublish = unpackBoolean(byteBuffer)
        PropertiesData_Field.MaturePublish = unpackBoolean(byteBuffer)
        PropertiesData_Field.ProfileURL = unpackVariable(byteBuffer, 1)
    }
}
