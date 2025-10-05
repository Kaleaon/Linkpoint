package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class MapBlockReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<Data> Data_Fields = ArrayList<>()
    public ArrayList<Size> Size_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int Flags
    }

    @JvmStatic
    class Data {
        public Int Access
        public Int Agents
        public UUID MapImageID
        public Byte[] Name
        public Int RegionFlags
        public Int WaterHeight
        public Int X
        public Int Y
    }

    @JvmStatic
    class Size {
        public Int SizeX
        public Int SizeY
    }

    public MapBlockReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        Int i = 25
        Iterator<T> it = this.Data_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2 + 1 + (this.Size_Fields.size() * 4)
            }
            i = ((Data) it.next()).Name.length + 5 + 1 + 4 + 1 + 1 + 16 + i2
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapBlockReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -103)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        byteBuffer.put((Byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packShort(byteBuffer, (Short) data.X)
            packShort(byteBuffer, (Short) data.Y)
            packVariable(byteBuffer, data.Name, 1)
            packByte(byteBuffer, (Byte) data.Access)
            packInt(byteBuffer, data.RegionFlags)
            packByte(byteBuffer, (Byte) data.WaterHeight)
            packByte(byteBuffer, (Byte) data.Agents)
            packUUID(byteBuffer, data.MapImageID)
        }
        byteBuffer.put((Byte) this.Size_Fields.size())
        for (Size size : this.Size_Fields) {
            packShort(byteBuffer, (Short) size.SizeX)
            packShort(byteBuffer, (Short) size.SizeY)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Data data = Data()
            data.X = unpackShort(byteBuffer) & 65535
            data.Y = unpackShort(byteBuffer) & 65535
            data.Name = unpackVariable(byteBuffer, 1)
            data.Access = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            data.RegionFlags = unpackInt(byteBuffer)
            data.WaterHeight = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            data.Agents = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            data.MapImageID = unpackUUID(byteBuffer)
            this.Data_Fields.add(data)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            Size size = Size()
            size.SizeX = unpackShort(byteBuffer) & 65535
            size.SizeY = unpackShort(byteBuffer) & 65535
            this.Size_Fields.add(size)
        }
    }
}
