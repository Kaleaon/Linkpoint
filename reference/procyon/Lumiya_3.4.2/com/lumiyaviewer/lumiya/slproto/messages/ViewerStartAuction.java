// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ViewerStartAuction extends SLMessage
{
    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;
    
    public ViewerStartAuction() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 56;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleViewerStartAuction(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-28));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.ParcelData_Field.LocalID);
        this.packUUID(byteBuffer, this.ParcelData_Field.SnapshotID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.LocalID = this.unpackInt(byteBuffer);
        this.ParcelData_Field.SnapshotID = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ParcelData
    {
        public int LocalID;
        public UUID SnapshotID;
    }
}
