package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AgentUpdate : SLMessage {
    var AgentData_Field: AgentData? = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var BodyRotation: LLQuaternion? = LLQuaternion()
        var CameraAtAxis: LLVector3? = LLVector3()
        var CameraCenter: LLVector3? = LLVector3()
        var CameraLeftAxis: LLVector3? = LLVector3()
        var CameraUpAxis: LLVector3? = LLVector3()
        var ControlFlags: Int = 0
        var Far: Float = 0f
        var Flags: Byte = 0
        var HeadRotation: LLQuaternion? = LLQuaternion()
        var SessionID: UUID? = null
        var State: Byte = 0
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 118
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentUpdate(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(4.toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID!!)
        packUUID(byteBuffer, AgentData_Field?.SessionID!!)
        packLLQuaternion(byteBuffer, AgentData_Field?.BodyRotation!!)
        packLLQuaternion(byteBuffer, AgentData_Field?.HeadRotation!!)
        packByte(byteBuffer, AgentData_Field?.State ?: 0)
        packLLVector3(byteBuffer, AgentData_Field?.CameraCenter!!)
        packLLVector3(byteBuffer, AgentData_Field?.CameraAtAxis!!)
        packLLVector3(byteBuffer, AgentData_Field?.CameraLeftAxis!!)
        packLLVector3(byteBuffer, AgentData_Field?.CameraUpAxis!!)
        packFloat(byteBuffer, AgentData_Field?.Far ?: 0f)
        packInt(byteBuffer, AgentData_Field?.ControlFlags ?: 0)
        packByte(byteBuffer, AgentData_Field?.Flags ?: 0)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        agentData.BodyRotation = unpackLLQuaternion(byteBuffer)
        agentData.HeadRotation = unpackLLQuaternion(byteBuffer)
        agentData.State = unpackByte(byteBuffer)
        agentData.CameraCenter = unpackLLVector3(byteBuffer)
        agentData.CameraAtAxis = unpackLLVector3(byteBuffer)
        agentData.CameraLeftAxis = unpackLLVector3(byteBuffer)
        agentData.CameraUpAxis = unpackLLVector3(byteBuffer)
        agentData.Far = unpackFloat(byteBuffer)
        agentData.ControlFlags = unpackInt(byteBuffer)
        agentData.Flags = unpackByte(byteBuffer)
        this.AgentData_Field = agentData
    }
}
