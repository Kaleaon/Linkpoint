// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentCachedTextureResponse extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<WearableData> WearableData_Fields;
    
    public AgentCachedTextureResponse() {
        this.WearableData_Fields = new ArrayList<WearableData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.WearableData_Fields.iterator();
        int n = 41;
        while (iterator.hasNext()) {
            n += iterator.next().HostName.length + 18;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentCachedTextureResponse(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-127));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.SerialNum);
        byteBuffer.put((byte)this.WearableData_Fields.size());
        for (final WearableData wearableData : this.WearableData_Fields) {
            this.packUUID(byteBuffer, wearableData.TextureID);
            this.packByte(byteBuffer, (byte)wearableData.TextureIndex);
            this.packVariable(byteBuffer, wearableData.HostName, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SerialNum = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final WearableData e = new WearableData();
            e.TextureID = this.unpackUUID(byteBuffer);
            e.TextureIndex = (this.unpackByte(byteBuffer) & 0xFF);
            e.HostName = this.unpackVariable(byteBuffer, 1);
            this.WearableData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int SerialNum;
        public UUID SessionID;
    }
    
    public static class WearableData
    {
        public byte[] HostName;
        public UUID TextureID;
        public int TextureIndex;
    }
}
