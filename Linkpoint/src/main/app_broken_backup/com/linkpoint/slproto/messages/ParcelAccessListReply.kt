package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelAccessListReply : SLMessage {
    Data Data_Field
    ArrayList<List> List_Fields = ArrayList<>()

    class Data {
        UUID AgentID
        Int Flags
        Int LocalID
        Int SequenceID
    }

    class List {
        Int Flags
        UUID ID
        Int Time
    }

    ParcelAccessListReply() {
        this.zeroCoded = true
        this.Data_Field = Data()
    }

    fun CalcPayloadSize(): Int {
        return (this.List_Fields.size() * 24) + 33
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleParcelAccessListReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -40)
        packUUID(byteBuffer, this.Data_Field.AgentID)
        packInt(byteBuffer, this.Data_Field.SequenceID)
        packInt(byteBuffer, this.Data_Field.Flags)
        packInt(byteBuffer, this.Data_Field.LocalID)
        byteBuffer.put((this as Byte).List_Fields.size())
        for (List list : this.List_Fields) {
            packUUID(byteBuffer, list.ID)
            packInt(byteBuffer, list.Time)
            packInt(byteBuffer, list.Flags)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Data_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.SequenceID = unpackInt(byteBuffer)
        this.Data_Field.Flags = unpackInt(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
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
