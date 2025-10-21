package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ChildAgentPositionUpdate : SLMessage() {
    public AgentData AgentData_Field = AgentData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public LLVector3 AgentPos
        public LLVector3 AgentVel
        public LLVector3 AtAxis
        public LLVector3 Center
        public Boolean ChangedGrid
        public LLVector3 LeftAxis
        public Long RegionHandle
        public UUID SessionID
        public LLVector3 Size
        public LLVector3 UpAxis
        public Int ViewerCircuitCode
    }

    public ChildAgentPositionUpdate() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 130
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChildAgentPositionUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.ESC)
        packLong(byteBuffer, this.AgentData_Field.RegionHandle)
        packInt(byteBuffer, this.AgentData_Field.ViewerCircuitCode)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packLLVector3(byteBuffer, this.AgentData_Field.AgentPos)
        packLLVector3(byteBuffer, this.AgentData_Field.AgentVel)
        packLLVector3(byteBuffer, this.AgentData_Field.Center)
        packLLVector3(byteBuffer, this.AgentData_Field.Size)
        packLLVector3(byteBuffer, this.AgentData_Field.AtAxis)
        packLLVector3(byteBuffer, this.AgentData_Field.LeftAxis)
        packLLVector3(byteBuffer, this.AgentData_Field.UpAxis)
        packBoolean(byteBuffer, this.AgentData_Field.ChangedGrid)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.RegionHandle = unpackLong(byteBuffer)
        this.AgentData_Field.ViewerCircuitCode = unpackInt(byteBuffer)
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.AgentPos = unpackLLVector3(byteBuffer)
        this.AgentData_Field.AgentVel = unpackLLVector3(byteBuffer)
        this.AgentData_Field.Center = unpackLLVector3(byteBuffer)
        this.AgentData_Field.Size = unpackLLVector3(byteBuffer)
        this.AgentData_Field.AtAxis = unpackLLVector3(byteBuffer)
        this.AgentData_Field.LeftAxis = unpackLLVector3(byteBuffer)
        this.AgentData_Field.UpAxis = unpackLLVector3(byteBuffer)
        this.AgentData_Field.ChangedGrid = unpackBoolean(byteBuffer)
    }
}
