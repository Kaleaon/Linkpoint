// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MapNameRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public NameData NameData_Field;
    
    public MapNameRequest() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.NameData_Field = new NameData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.NameData_Field.Name.length + 1 + 45;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMapNameRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-104));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.Flags);
        this.packInt(byteBuffer, this.AgentData_Field.EstateID);
        this.packBoolean(byteBuffer, this.AgentData_Field.Godlike);
        this.packVariable(byteBuffer, this.NameData_Field.Name, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = this.unpackInt(byteBuffer);
        this.AgentData_Field.EstateID = this.unpackInt(byteBuffer);
        this.AgentData_Field.Godlike = this.unpackBoolean(byteBuffer);
        this.NameData_Field.Name = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int EstateID;
        public int Flags;
        public boolean Godlike;
        public UUID SessionID;
    }
    
    public static class NameData
    {
        public byte[] Name;
    }
}
