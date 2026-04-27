package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class StartLure : SLMessage() {
    public AgentData AgentData_Field
    public Info Info_Field
    public ArrayList<TargetData> TargetData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Info {
        public Int LureType
        public ByteArray Message
    }

    @JvmStatic
    class TargetData {
        public UUID TargetID
    }

    public StartLure() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.Info_Field = Info()
    }

    public fun CalcPayloadSize(): Int {
        return this.Info_Field.Message.length + 2 + 36 + 1 + (this.TargetData_Fields.size() * 16)
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleStartLure(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 70)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (Byte) this.Info_Field.LureType)
        packVariable(byteBuffer, this.Info_Field.Message, 1)
        byteBuffer.put((Byte) this.TargetData_Fields.size())
        for (TargetData targetData : this.TargetData_Fields) {
            packUUID(byteBuffer, targetData.TargetID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Info_Field.LureType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.Info_Field.Message = unpackVariable(byteBuffer, 1)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val targetData: TargetData = TargetData()
            targetData.TargetID = unpackUUID(byteBuffer)
            this.TargetData_Fields.add(targetData)
        }
    }
}
