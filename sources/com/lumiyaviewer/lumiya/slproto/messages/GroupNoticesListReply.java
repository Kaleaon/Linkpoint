package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class GroupNoticesListReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
    }

    public static class Data {
        public int AssetType;
        public byte[] FromName;
        public boolean HasAttachment;
        public UUID NoticeID;
        public byte[] Subject;
        public int Timestamp;
    }

    public GroupNoticesListReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int i = 37;
        Iterator<T> it = this.Data_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            Data data = (Data) it.next();
            i = data.Subject.length + data.FromName.length + 22 + 2 + 1 + 1 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupNoticesListReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 59);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.NoticeID);
            packInt(byteBuffer, data.Timestamp);
            packVariable(byteBuffer, data.FromName, 2);
            packVariable(byteBuffer, data.Subject, 2);
            packBoolean(byteBuffer, data.HasAttachment);
            packByte(byteBuffer, (byte) data.AssetType);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Data data = new Data();
            data.NoticeID = unpackUUID(byteBuffer);
            data.Timestamp = unpackInt(byteBuffer);
            data.FromName = unpackVariable(byteBuffer, 2);
            data.Subject = unpackVariable(byteBuffer, 2);
            data.HasAttachment = unpackBoolean(byteBuffer);
            data.AssetType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.Data_Fields.add(data);
        }
    }
}
