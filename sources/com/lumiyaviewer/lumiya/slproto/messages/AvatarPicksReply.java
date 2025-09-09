package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class AvatarPicksReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID TargetID;
    }

    public static class Data {
        public UUID PickID;
        public byte[] PickName;
    }

    public AvatarPicksReply() {
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
            i = ((Data) it.next()).PickName.length + 17 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPicksReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -78);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.TargetID);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.PickID);
            packVariable(byteBuffer, data.PickName, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.TargetID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Data data = new Data();
            data.PickID = unpackUUID(byteBuffer);
            data.PickName = unpackVariable(byteBuffer, 1);
            this.Data_Fields.add(data);
        }
    }
}
