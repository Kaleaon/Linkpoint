package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class MapBlockReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields = new ArrayList<>();
    public ArrayList<Size> Size_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public int Flags;
    }

    public static class Data {
        public int Access;
        public int Agents;
        public UUID MapImageID;
        public byte[] Name;
        public int RegionFlags;
        public int WaterHeight;
        public int X;
        public int Y;
    }

    public static class Size {
        public int SizeX;
        public int SizeY;
    }

    public MapBlockReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int i = 25;
        Iterator<T> it = this.Data_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2 + 1 + (this.Size_Fields.size() * 4);
            }
            i = ((Data) it.next()).Name.length + 5 + 1 + 4 + 1 + 1 + 16 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapBlockReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -103);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packInt(byteBuffer, this.AgentData_Field.Flags);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packShort(byteBuffer, (short) data.X);
            packShort(byteBuffer, (short) data.Y);
            packVariable(byteBuffer, data.Name, 1);
            packByte(byteBuffer, (byte) data.Access);
            packInt(byteBuffer, data.RegionFlags);
            packByte(byteBuffer, (byte) data.WaterHeight);
            packByte(byteBuffer, (byte) data.Agents);
            packUUID(byteBuffer, data.MapImageID);
        }
        byteBuffer.put((byte) this.Size_Fields.size());
        for (Size size : this.Size_Fields) {
            packShort(byteBuffer, (short) size.SizeX);
            packShort(byteBuffer, (short) size.SizeY);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Data data = new Data();
            data.X = unpackShort(byteBuffer) & 65535;
            data.Y = unpackShort(byteBuffer) & 65535;
            data.Name = unpackVariable(byteBuffer, 1);
            data.Access = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            data.RegionFlags = unpackInt(byteBuffer);
            data.WaterHeight = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            data.Agents = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            data.MapImageID = unpackUUID(byteBuffer);
            this.Data_Fields.add(data);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            Size size = new Size();
            size.SizeX = unpackShort(byteBuffer) & 65535;
            size.SizeY = unpackShort(byteBuffer) & 65535;
            this.Size_Fields.add(size);
        }
    }
}
