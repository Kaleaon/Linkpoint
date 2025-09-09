package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class AgentCachedTextureResponse extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<WearableData> WearableData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public int SerialNum;
        public UUID SessionID;
    }

    public static class WearableData {
        public byte[] HostName;
        public UUID TextureID;
        public int TextureIndex;
    }

    public AgentCachedTextureResponse() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int i = 41;
        Iterator<T> it = this.WearableData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((WearableData) it.next()).HostName.length + 18 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentCachedTextureResponse(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -127);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.SerialNum);
        byteBuffer.put((byte) this.WearableData_Fields.size());
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.TextureID);
            packByte(byteBuffer, (byte) wearableData.TextureIndex);
            packVariable(byteBuffer, wearableData.HostName, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.SerialNum = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            WearableData wearableData = new WearableData();
            wearableData.TextureID = unpackUUID(byteBuffer);
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            wearableData.HostName = unpackVariable(byteBuffer, 1);
            this.WearableData_Fields.add(wearableData);
        }
    }
}
