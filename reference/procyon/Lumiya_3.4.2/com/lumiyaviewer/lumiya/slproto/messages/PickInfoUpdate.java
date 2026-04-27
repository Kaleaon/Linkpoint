// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class PickInfoUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    
    public PickInfoUpdate() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.Name.length + 50 + 2 + this.Data_Field.Desc.length + 16 + 24 + 4 + 1 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandlePickInfoUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-71));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.Data_Field.PickID);
        this.packUUID(byteBuffer, this.Data_Field.CreatorID);
        this.packBoolean(byteBuffer, this.Data_Field.TopPick);
        this.packUUID(byteBuffer, this.Data_Field.ParcelID);
        this.packVariable(byteBuffer, this.Data_Field.Name, 1);
        this.packVariable(byteBuffer, this.Data_Field.Desc, 2);
        this.packUUID(byteBuffer, this.Data_Field.SnapshotID);
        this.packLLVector3d(byteBuffer, this.Data_Field.PosGlobal);
        this.packInt(byteBuffer, this.Data_Field.SortOrder);
        this.packBoolean(byteBuffer, this.Data_Field.Enabled);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Data_Field.PickID = this.unpackUUID(byteBuffer);
        this.Data_Field.CreatorID = this.unpackUUID(byteBuffer);
        this.Data_Field.TopPick = this.unpackBoolean(byteBuffer);
        this.Data_Field.ParcelID = this.unpackUUID(byteBuffer);
        this.Data_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.Desc = this.unpackVariable(byteBuffer, 2);
        this.Data_Field.SnapshotID = this.unpackUUID(byteBuffer);
        this.Data_Field.PosGlobal = this.unpackLLVector3d(byteBuffer);
        this.Data_Field.SortOrder = this.unpackInt(byteBuffer);
        this.Data_Field.Enabled = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Data
    {
        public UUID CreatorID;
        public byte[] Desc;
        public boolean Enabled;
        public byte[] Name;
        public UUID ParcelID;
        public UUID PickID;
        public LLVector3d PosGlobal;
        public UUID SnapshotID;
        public int SortOrder;
        public boolean TopPick;
    }
}
