// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentSetAppearance extends SLMessage
{
    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;
    public ArrayList<VisualParam> VisualParam_Fields;
    public ArrayList<WearableData> WearableData_Fields;
    
    public AgentSetAppearance() {
        this.WearableData_Fields = new ArrayList<WearableData>();
        this.VisualParam_Fields = new ArrayList<VisualParam>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.WearableData_Fields.size() * 17 + 53 + (this.ObjectData_Field.TextureEntry.length + 2) + 1 + this.VisualParam_Fields.size() * 1;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentSetAppearance(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)84);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.SerialNum);
        this.packLLVector3(byteBuffer, this.AgentData_Field.Size);
        byteBuffer.put((byte)this.WearableData_Fields.size());
        for (final WearableData wearableData : this.WearableData_Fields) {
            this.packUUID(byteBuffer, wearableData.CacheID);
            this.packByte(byteBuffer, (byte)wearableData.TextureIndex);
        }
        this.packVariable(byteBuffer, this.ObjectData_Field.TextureEntry, 2);
        byteBuffer.put((byte)this.VisualParam_Fields.size());
        final Iterator<Object> iterator2 = this.VisualParam_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packByte(byteBuffer, (byte)iterator2.next().ParamValue);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SerialNum = this.unpackInt(byteBuffer);
        this.AgentData_Field.Size = this.unpackLLVector3(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final WearableData e = new WearableData();
            e.CacheID = this.unpackUUID(byteBuffer);
            e.TextureIndex = (this.unpackByte(byteBuffer) & 0xFF);
            this.WearableData_Fields.add(e);
        }
        this.ObjectData_Field.TextureEntry = this.unpackVariable(byteBuffer, 2);
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final VisualParam e2 = new VisualParam();
            e2.ParamValue = (this.unpackByte(byteBuffer) & 0xFF);
            this.VisualParam_Fields.add(e2);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int SerialNum;
        public UUID SessionID;
        public LLVector3 Size;
    }
    
    public static class ObjectData
    {
        public byte[] TextureEntry;
    }
    
    public static class VisualParam
    {
        public int ParamValue;
    }
    
    public static class WearableData
    {
        public UUID CacheID;
        public int TextureIndex;
    }
}
