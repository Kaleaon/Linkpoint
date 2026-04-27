// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MapItemRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public RequestData RequestData_Field;
    
    public MapItemRequest() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.RequestData_Field = new RequestData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 57;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMapItemRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-102));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.Flags);
        this.packInt(byteBuffer, this.AgentData_Field.EstateID);
        this.packBoolean(byteBuffer, this.AgentData_Field.Godlike);
        this.packInt(byteBuffer, this.RequestData_Field.ItemType);
        this.packLong(byteBuffer, this.RequestData_Field.RegionHandle);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = this.unpackInt(byteBuffer);
        this.AgentData_Field.EstateID = this.unpackInt(byteBuffer);
        this.AgentData_Field.Godlike = this.unpackBoolean(byteBuffer);
        this.RequestData_Field.ItemType = this.unpackInt(byteBuffer);
        this.RequestData_Field.RegionHandle = this.unpackLong(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int EstateID;
        public int Flags;
        public boolean Godlike;
        public UUID SessionID;
    }
    
    public static class RequestData
    {
        public int ItemType;
        public long RegionHandle;
    }
}
