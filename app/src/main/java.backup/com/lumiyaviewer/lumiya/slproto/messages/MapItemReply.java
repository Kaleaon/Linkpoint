package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class MapItemReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields = new ArrayList<>();
    public RequestData RequestData_Field;

    public static class AgentData {
        public UUID AgentID;
        public int Flags;
    }

    public static class Data {
        public int Extra;
        public int Extra2;
        public UUID ID;
        public byte[] Name;
        public int X;
        public int Y;
    }

    public static class RequestData {
        public int ItemType;
    }

    public MapItemReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.RequestData_Field = new RequestData();
    }

    public int CalcPayloadSize() {
        int i = 29;
        Iterator<T> it = this.Data_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((Data) it.next()).Name.length + 33 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapItemReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -101);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packInt(byteBuffer, this.AgentData_Field.Flags);
        packInt(byteBuffer, this.RequestData_Field.ItemType);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packInt(byteBuffer, data.X);
            packInt(byteBuffer, data.Y);
            packUUID(byteBuffer, data.ID);
            packInt(byteBuffer, data.Extra);
            packInt(byteBuffer, data.Extra2);
            packVariable(byteBuffer, data.Name, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = unpackInt(byteBuffer);
        this.RequestData_Field.ItemType = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Data data = new Data();
            data.X = unpackInt(byteBuffer);
            data.Y = unpackInt(byteBuffer);
            data.ID = unpackUUID(byteBuffer);
            data.Extra = unpackInt(byteBuffer);
            data.Extra2 = unpackInt(byteBuffer);
            data.Name = unpackVariable(byteBuffer, 1);
            this.Data_Fields.add(data);
        }
    }
}
