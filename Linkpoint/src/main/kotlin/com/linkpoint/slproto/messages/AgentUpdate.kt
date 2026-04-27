package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AgentUpdate : SLMessage() {
    public AgentData AgentData_Field = AgentData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public LLQuaternion BodyRotation
        public LLVector3 CameraAtAxis
        public LLVector3 CameraCenter
        public LLVector3 CameraLeftAxis
        public LLVector3 CameraUpAxis
        public Int ControlFlags
        public Float Far
        public Int Flags
        public LLQuaternion HeadRotation
        public UUID SessionID
        public Int State
    }

    public AgentUpdate() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return 115
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put((Byte) 4)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packLLQuaternion(byteBuffer, this.AgentData_Field.BodyRotation)
        packLLQuaternion(byteBuffer, this.AgentData_Field.HeadRotation)
        packByte(byteBuffer, (Byte) this.AgentData_Field.State)
        packLLVector3(byteBuffer, this.AgentData_Field.CameraCenter)
        packLLVector3(byteBuffer, this.AgentData_Field.CameraAtAxis)
        packLLVector3(byteBuffer, this.AgentData_Field.CameraLeftAxis)
        packLLVector3(byteBuffer, this.AgentData_Field.CameraUpAxis)
        packFloat(byteBuffer, this.AgentData_Field.Far)
        packInt(byteBuffer, this.AgentData_Field.ControlFlags)
        packByte(byteBuffer, (Byte) this.AgentData_Field.Flags)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
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
