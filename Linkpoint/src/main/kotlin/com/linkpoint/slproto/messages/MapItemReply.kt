package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class MapItemReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<Data> Data_Fields = ArrayList<>()
    public RequestData RequestData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int Flags
    }

    @JvmStatic
    class Data {
        public Int Extra
        public Int Extra2
        public UUID ID
        public Byte[] Name
        public Int X
        public Int Y
    }

    @JvmStatic
    class RequestData {
        public Int ItemType
    }

    public MapItemReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.RequestData_Field = RequestData()
    }

    public Int CalcPayloadSize() {
        Int i = 29
        Iterator<T> it = this.Data_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((Data) it.next()).Name.length + 33 + i2
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapItemReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -101)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        packInt(byteBuffer, this.RequestData_Field.ItemType)
        byteBuffer.put((Byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packInt(byteBuffer, data.X)
            packInt(byteBuffer, data.Y)
            packUUID(byteBuffer, data.ID)
            packInt(byteBuffer, data.Extra)
            packInt(byteBuffer, data.Extra2)
            packVariable(byteBuffer, data.Name, 1)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        this.RequestData_Field.ItemType = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Data data = Data()
            data.X = unpackInt(byteBuffer)
            data.Y = unpackInt(byteBuffer)
            data.ID = unpackUUID(byteBuffer)
            data.Extra = unpackInt(byteBuffer)
            data.Extra2 = unpackInt(byteBuffer)
            data.Name = unpackVariable(byteBuffer, 1)
            this.Data_Fields.add(data)
        }
    }
}
