package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelAccessListReply : SLMessage() {
    public Data Data_Field
    public ArrayList<List> List_Fields = ArrayList<>()

    @JvmStatic
    class Data {
        public UUID AgentID
        public Int Flags
        public Int LocalID
        public Int SequenceID
    }

    @JvmStatic
    class List {
        public Int Flags
        public UUID ID
        public Int Time
    }

    public ParcelAccessListReply() {
        this.zeroCoded = true
        this.Data_Field = Data()
    }

    public fun CalcPayloadSize(): Int {
        return (this.List_Fields.size() * 24) + 33
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelAccessListReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -40)
        packUUID(byteBuffer, this.Data_Field.AgentID)
        packInt(byteBuffer, this.Data_Field.SequenceID)
        packInt(byteBuffer, this.Data_Field.Flags)
        packInt(byteBuffer, this.Data_Field.LocalID)
        byteBuffer.put((Byte) this.List_Fields.size())
        for (List list : this.List_Fields) {
            packUUID(byteBuffer, list.ID)
            packInt(byteBuffer, list.Time)
            packInt(byteBuffer, list.Flags)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.Data_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.SequenceID = unpackInt(byteBuffer)
        this.Data_Field.Flags = unpackInt(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val list: List = List()
            list.ID = unpackUUID(byteBuffer)
            list.Time = unpackInt(byteBuffer)
            list.Flags = unpackInt(byteBuffer)
            this.List_Fields.add(list)
        }
    }
}
