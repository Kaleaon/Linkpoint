// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MapBlockRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public PositionData PositionData_Field;
    
    public MapBlockRequest() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.PositionData_Field = new PositionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 53;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMapBlockRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-105));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.Flags);
        this.packInt(byteBuffer, this.AgentData_Field.EstateID);
        this.packBoolean(byteBuffer, this.AgentData_Field.Godlike);
        this.packShort(byteBuffer, (short)this.PositionData_Field.MinX);
        this.packShort(byteBuffer, (short)this.PositionData_Field.MaxX);
        this.packShort(byteBuffer, (short)this.PositionData_Field.MinY);
        this.packShort(byteBuffer, (short)this.PositionData_Field.MaxY);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = this.unpackInt(byteBuffer);
        this.AgentData_Field.EstateID = this.unpackInt(byteBuffer);
        this.AgentData_Field.Godlike = this.unpackBoolean(byteBuffer);
        this.PositionData_Field.MinX = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.PositionData_Field.MaxX = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.PositionData_Field.MinY = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.PositionData_Field.MaxY = (this.unpackShort(byteBuffer) & 0xFFFF);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int EstateID;
        public int Flags;
        public boolean Godlike;
        public UUID SessionID;
    }
    
    public static class PositionData
    {
        public int MaxX;
        public int MaxY;
        public int MinX;
        public int MinY;
    }
}
