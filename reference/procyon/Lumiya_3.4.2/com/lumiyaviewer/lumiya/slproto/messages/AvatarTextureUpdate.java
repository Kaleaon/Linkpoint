// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarTextureUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<TextureData> TextureData_Fields;
    public ArrayList<WearableData> WearableData_Fields;
    
    public AvatarTextureUpdate() {
        this.WearableData_Fields = new ArrayList<WearableData>();
        this.TextureData_Fields = new ArrayList<TextureData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.WearableData_Fields.iterator();
        int n = 22;
        while (iterator.hasNext()) {
            n += iterator.next().HostName.length + 18;
        }
        return n + 1 + this.TextureData_Fields.size() * 16;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarTextureUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)4);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packBoolean(byteBuffer, this.AgentData_Field.TexturesChanged);
        byteBuffer.put((byte)this.WearableData_Fields.size());
        for (final WearableData wearableData : this.WearableData_Fields) {
            this.packUUID(byteBuffer, wearableData.CacheID);
            this.packByte(byteBuffer, (byte)wearableData.TextureIndex);
            this.packVariable(byteBuffer, wearableData.HostName, 1);
        }
        byteBuffer.put((byte)this.TextureData_Fields.size());
        final Iterator<Object> iterator2 = this.TextureData_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packUUID(byteBuffer, iterator2.next().TextureID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.TexturesChanged = this.unpackBoolean(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final WearableData e = new WearableData();
            e.CacheID = this.unpackUUID(byteBuffer);
            e.TextureIndex = (this.unpackByte(byteBuffer) & 0xFF);
            e.HostName = this.unpackVariable(byteBuffer, 1);
            this.WearableData_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final TextureData e2 = new TextureData();
            e2.TextureID = this.unpackUUID(byteBuffer);
            this.TextureData_Fields.add(e2);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public boolean TexturesChanged;
    }
    
    public static class TextureData
    {
        public UUID TextureID;
    }
    
    public static class WearableData
    {
        public UUID CacheID;
        public byte[] HostName;
        public int TextureIndex;
    }
}
