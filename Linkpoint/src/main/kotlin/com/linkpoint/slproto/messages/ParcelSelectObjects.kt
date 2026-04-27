package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelSelectObjects : SLMessage() {
    public AgentData AgentData_Field
    public ParcelData ParcelData_Field
    public ArrayList<ReturnIDs> ReturnIDs_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ParcelData {
        public Int LocalID
        public Int ReturnType
    }

    @JvmStatic
    class ReturnIDs {
        public UUID ReturnID
    }

    public ParcelSelectObjects() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ParcelData_Field = ParcelData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.ReturnIDs_Fields.size() * 16) + 45
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelSelectObjects(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -54)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ParcelData_Field.LocalID)
        packInt(byteBuffer, this.ParcelData_Field.ReturnType)
        byteBuffer.put((Byte) this.ReturnIDs_Fields.size())
        for (ReturnIDs returnIDs : this.ReturnIDs_Fields) {
            packUUID(byteBuffer, returnIDs.ReturnID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
        this.ParcelData_Field.ReturnType = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val returnIDs: ReturnIDs = ReturnIDs()
            returnIDs.ReturnID = unpackUUID(byteBuffer)
            this.ReturnIDs_Fields.add(returnIDs)
        }
    }
}
