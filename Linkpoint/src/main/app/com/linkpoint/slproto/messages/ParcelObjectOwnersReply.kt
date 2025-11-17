package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelObjectOwnersReply : SLMessage {
    ArrayList<Data> Data_Fields = ArrayList<>()

    class Data {
        Int Count
        Boolean IsGroupOwned
        Boolean OnlineStatus
        UUID OwnerID
    }

    ParcelObjectOwnersReply() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return (this.Data_Fields.size() * 22) + 5
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelObjectOwnersReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Data data = Data()
            data.OwnerID = unpackUUID(byteBuffer)
            data.IsGroupOwned = unpackBoolean(byteBuffer)
            data.Count = unpackInt(byteBuffer)
            data.OnlineStatus = unpackBoolean(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
