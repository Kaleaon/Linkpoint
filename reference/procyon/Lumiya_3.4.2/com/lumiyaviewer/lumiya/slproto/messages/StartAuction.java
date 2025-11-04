// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class StartAuction extends SLMessage
{
    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;
    
    public StartAuction() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ParcelData_Field.Name.length + 33 + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleStartAuction(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-27));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.ParcelData_Field.ParcelID);
        this.packUUID(byteBuffer, this.ParcelData_Field.SnapshotID);
        this.packVariable(byteBuffer, this.ParcelData_Field.Name, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.ParcelID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.SnapshotID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.Name = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class ParcelData
    {
        public byte[] Name;
        public UUID ParcelID;
        public UUID SnapshotID;
    }
}
