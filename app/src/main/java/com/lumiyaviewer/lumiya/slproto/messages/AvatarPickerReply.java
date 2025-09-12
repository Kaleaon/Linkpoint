package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class AvatarPickerReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID QueryID;
    }

    public static class Data {
        public UUID AvatarID;
        public byte[] FirstName;
        public byte[] LastName;
    }

    public AvatarPickerReply() {
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
            i = data.LastName.length + data.FirstName.length + 17 + 1 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPickerReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.FS);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.QueryID);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.AvatarID);
            packVariable(byteBuffer, data.FirstName, 1);
            packVariable(byteBuffer, data.LastName, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Data data = new Data();
            data.AvatarID = unpackUUID(byteBuffer);
            data.FirstName = unpackVariable(byteBuffer, 1);
            data.LastName = unpackVariable(byteBuffer, 1);
            this.Data_Fields.add(data);
        }
    }
}
