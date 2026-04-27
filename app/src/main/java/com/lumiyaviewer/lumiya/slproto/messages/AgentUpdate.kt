package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AgentUpdate : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
        LLQuaternion BodyRotation
        LLVector3 CameraAtAxis
        LLVector3 CameraCenter
        LLVector3 CameraLeftAxis
        LLVector3 CameraUpAxis
        Int ControlFlags
        float Far
        Int Flags
        LLQuaternion HeadRotation
        UUID SessionID
        Int State
    }

    AgentUpdate() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return 115
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 4)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packLLQuaternion(byteBuffer, this.AgentData_Field.BodyRotation)
        packLLQuaternion(byteBuffer, this.AgentData_Field.HeadRotation)
        packByte(byteBuffer, (byte) this.AgentData_Field.State)
        packLLVector3(byteBuffer, this.AgentData_Field.CameraCenter)
        packLLVector3(byteBuffer, this.AgentData_Field.CameraAtAxis)
        packLLVector3(byteBuffer, this.AgentData_Field.CameraLeftAxis)
        packLLVector3(byteBuffer, this.AgentData_Field.CameraUpAxis)
        packFloat(byteBuffer, this.AgentData_Field.Far)
        packInt(byteBuffer, this.AgentData_Field.ControlFlags)
        packByte(byteBuffer, (byte) this.AgentData_Field.Flags)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.BodyRotation = unpackLLQuaternion(byteBuffer)
        this.AgentData_Field.HeadRotation = unpackLLQuaternion(byteBuffer)
        this.AgentData_Field.State = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentData_Field.CameraCenter = unpackLLVector3(byteBuffer)
        this.AgentData_Field.CameraAtAxis = unpackLLVector3(byteBuffer)
        this.AgentData_Field.CameraLeftAxis = unpackLLVector3(byteBuffer)
        this.AgentData_Field.CameraUpAxis = unpackLLVector3(byteBuffer)
        this.AgentData_Field.Far = unpackFloat(byteBuffer)
        this.AgentData_Field.ControlFlags = unpackInt(byteBuffer)
        this.AgentData_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
