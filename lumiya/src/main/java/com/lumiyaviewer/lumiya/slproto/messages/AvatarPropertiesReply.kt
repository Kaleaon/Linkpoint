package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var PropertiesData_Field: PropertiesData = PropertiesData()

    class AgentData {
        var AgentID: UUID? = null
        var AvatarID: UUID? = null
    }

    class PropertiesData {
        var AboutText: ByteArray = ByteArray(0)
        var BornOn: ByteArray = ByteArray(0)
        var CharterMember: ByteArray = ByteArray(0)
        var FLAboutText: ByteArray = ByteArray(0)
        var FLImageID: UUID? = null
        var Flags: Int = 0
        var ImageID: UUID? = null
        var PartnerID: UUID? = null
        var ProfileURL: ByteArray = ByteArray(0)
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return PropertiesData_Field.AboutText.size + 50 + 1 + PropertiesData_Field.FLAboutText.size + 1 + PropertiesData_Field.BornOn.size + 1 + PropertiesData_Field.ProfileURL.size + 1 + PropertiesData_Field.CharterMember.size + 4 + 36
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-85).toByte())
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.AvatarID!!)
        packUUID(byteBuffer, PropertiesData_Field.ImageID!!)
        packUUID(byteBuffer, PropertiesData_Field.FLImageID!!)
        packUUID(byteBuffer, PropertiesData_Field.PartnerID!!)
        packVariable(byteBuffer, PropertiesData_Field.AboutText, 2)
        packVariable(byteBuffer, PropertiesData_Field.FLAboutText, 1)
        packVariable(byteBuffer, PropertiesData_Field.BornOn, 1)
        packVariable(byteBuffer, PropertiesData_Field.ProfileURL, 1)
        packVariable(byteBuffer, PropertiesData_Field.CharterMember, 1)
        packInt(byteBuffer, PropertiesData_Field.Flags)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        PropertiesData_Field.ImageID = unpackUUID(byteBuffer)
        PropertiesData_Field.FLImageID = unpackUUID(byteBuffer)
        PropertiesData_Field.PartnerID = unpackUUID(byteBuffer)
        PropertiesData_Field.AboutText = unpackVariable(byteBuffer, 2)
        PropertiesData_Field.FLAboutText = unpackVariable(byteBuffer, 1)
        PropertiesData_Field.BornOn = unpackVariable(byteBuffer, 1)
        PropertiesData_Field.ProfileURL = unpackVariable(byteBuffer, 1)
        PropertiesData_Field.CharterMember = unpackVariable(byteBuffer, 1)
        PropertiesData_Field.Flags = unpackInt(byteBuffer)
    }
}
