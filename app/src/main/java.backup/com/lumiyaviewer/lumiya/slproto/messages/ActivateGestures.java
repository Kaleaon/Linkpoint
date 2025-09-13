package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ActivateGestures extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public int Flags;
        public UUID SessionID;
    }

    public static class Data {
        public UUID AssetID;
        public int GestureFlags;
        public UUID ItemID;
    }

    public ActivateGestures() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        return (this.Data_Fields.size() * 36) + 41;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleActivateGestures(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 60);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.Flags);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.ItemID);
            packUUID(byteBuffer, data.AssetID);
            packInt(byteBuffer, data.GestureFlags);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Data data = new Data();
            data.ItemID = unpackUUID(byteBuffer);
            data.AssetID = unpackUUID(byteBuffer);
            data.GestureFlags = unpackInt(byteBuffer);
            this.Data_Fields.add(data);
        }
    }
}
