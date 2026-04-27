package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelObjectOwnersReply : SLMessage() {
    public ArrayList<Data> Data_Fields = ArrayList<>()

    @JvmStatic
    class Data {
        public Int Count
        public Boolean IsGroupOwned
        public Boolean OnlineStatus
        public UUID OwnerID
    }

    public ParcelObjectOwnersReply() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return (this.Data_Fields.size() * 22) + 5
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelObjectOwnersReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 57)
        byteBuffer.put((Byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.OwnerID)
            packBoolean(byteBuffer, data.IsGroupOwned)
            packInt(byteBuffer, data.Count)
            packBoolean(byteBuffer, data.OnlineStatus)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val data: Data = Data()
            data.OwnerID = unpackUUID(byteBuffer)
            data.IsGroupOwned = unpackBoolean(byteBuffer)
            data.Count = unpackInt(byteBuffer)
            data.OnlineStatus = unpackBoolean(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
