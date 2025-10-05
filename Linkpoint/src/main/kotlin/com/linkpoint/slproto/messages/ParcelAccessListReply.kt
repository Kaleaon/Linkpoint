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

    public Int CalcPayloadSize() {
        return (this.List_Fields.size() * 24) + 33
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelAccessListReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.SequenceID = unpackInt(byteBuffer)
        this.Data_Field.Flags = unpackInt(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            List list = List()
            list.ID = unpackUUID(byteBuffer)
            list.Time = unpackInt(byteBuffer)
            list.Flags = unpackInt(byteBuffer)
            this.List_Fields.add(list)
        }
    }
}
