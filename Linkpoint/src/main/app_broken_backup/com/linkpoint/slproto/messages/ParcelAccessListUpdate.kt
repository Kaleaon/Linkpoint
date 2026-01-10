package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelAccessListUpdate : SLMessage {
    AgentData AgentData_Field
    Data Data_Field
    ArrayList<List> List_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        Int Flags
        Int LocalID
        Int Sections
        Int SequenceID
        UUID TransactionID
    }

    class List {
        Int Flags
        UUID ID
        Int Time
    }

    ParcelAccessListUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.Data_Field = Data()
    }

    fun CalcPayloadSize(): Int {
        return (this.List_Fields.size() * 24) + 69
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelAccessListUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -39)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.Data_Field.Flags)
        packInt(byteBuffer, this.Data_Field.LocalID)
        packUUID(byteBuffer, this.Data_Field.TransactionID)
        packInt(byteBuffer, this.Data_Field.SequenceID)
        packInt(byteBuffer, this.Data_Field.Sections)
        byteBuffer.put((this as Byte).List_Fields.size())
        for (List list : this.List_Fields) {
            packUUID(byteBuffer, list.ID)
            packInt(byteBuffer, list.Time)
            packInt(byteBuffer, list.Flags)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.Flags = unpackInt(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
        this.Data_Field.TransactionID = unpackUUID(byteBuffer)
        this.Data_Field.SequenceID = unpackInt(byteBuffer)
        this.Data_Field.Sections = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            List list = List()
            list.ID = unpackUUID(byteBuffer)
            list.Time = unpackInt(byteBuffer)
            list.Flags = unpackInt(byteBuffer)
            this.List_Fields.add(list)
        }
    }
}
