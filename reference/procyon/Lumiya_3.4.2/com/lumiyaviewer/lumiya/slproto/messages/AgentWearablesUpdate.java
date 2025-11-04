// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentWearablesUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<WearableData> WearableData_Fields;
    
    public AgentWearablesUpdate() {
        this.WearableData_Fields = new ArrayList<WearableData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.WearableData_Fields.size() * 33 + 41;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentWearablesUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)126);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.SerialNum);
        byteBuffer.put((byte)this.WearableData_Fields.size());
        for (final WearableData wearableData : this.WearableData_Fields) {
            this.packUUID(byteBuffer, wearableData.ItemID);
            this.packUUID(byteBuffer, wearableData.AssetID);
            this.packByte(byteBuffer, (byte)wearableData.WearableType);
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
            e.ItemID = this.unpackUUID(byteBuffer);
            e.AssetID = this.unpackUUID(byteBuffer);
            e.WearableType = (this.unpackByte(byteBuffer) & 0xFF);
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
        public UUID AssetID;
        public UUID ItemID;
        public int WearableType;
    }
}
