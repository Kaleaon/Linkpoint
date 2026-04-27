package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class AvatarTextureUpdate extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<TextureData> TextureData_Fields = new ArrayList<>();
    public ArrayList<WearableData> WearableData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public boolean TexturesChanged;
    }

    public static class TextureData {
        public UUID TextureID;
    }

    public static class WearableData {
        public UUID CacheID;
        public byte[] HostName;
        public int TextureIndex;
    }

    public AvatarTextureUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int i = 22;
        Iterator<T> it = this.WearableData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2 + 1 + (this.TextureData_Fields.size() * 16);
            }
            i = ((WearableData) it.next()).HostName.length + 18 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarTextureUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 4);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packBoolean(byteBuffer, this.AgentData_Field.TexturesChanged);
        byteBuffer.put((byte) this.WearableData_Fields.size());
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.CacheID);
            packByte(byteBuffer, (byte) wearableData.TextureIndex);
            packVariable(byteBuffer, wearableData.HostName, 1);
        }
        byteBuffer.put((byte) this.TextureData_Fields.size());
        for (TextureData textureData : this.TextureData_Fields) {
            packUUID(byteBuffer, textureData.TextureID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.TexturesChanged = unpackBoolean(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            WearableData wearableData = new WearableData();
            wearableData.CacheID = unpackUUID(byteBuffer);
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            wearableData.HostName = unpackVariable(byteBuffer, 1);
            this.WearableData_Fields.add(wearableData);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            TextureData textureData = new TextureData();
            textureData.TextureID = unpackUUID(byteBuffer);
            this.TextureData_Fields.add(textureData);
        }
    }
}
