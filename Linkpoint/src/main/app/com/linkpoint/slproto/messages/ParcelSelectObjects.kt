package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelSelectObjects : SLMessage {
    AgentData AgentData_Field
    ParcelData ParcelData_Field
    ArrayList<ReturnIDs> ReturnIDs_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ParcelData {
        Int LocalID
        Int ReturnType
    }

    class ReturnIDs {
        UUID ReturnID
    }

    ParcelSelectObjects() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ParcelData_Field = ParcelData()
    }

    Int CalcPayloadSize() {
        return (this.ReturnIDs_Fields.size() * 16) + 45
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelSelectObjects(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
        this.ParcelData_Field.ReturnType = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ReturnIDs returnIDs = ReturnIDs()
            returnIDs.ReturnID = unpackUUID(byteBuffer)
            this.ReturnIDs_Fields.add(returnIDs)
        }
    }
}
