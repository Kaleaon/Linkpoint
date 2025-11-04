// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DirPlacesQuery extends SLMessage
{
    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    
    public DirPlacesQuery() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.QueryData_Field = new QueryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.QueryData_Field.QueryText.length + 17 + 4 + 1 + 1 + this.QueryData_Field.SimName.length + 4 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDirPlacesQuery(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)33);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        this.packVariable(byteBuffer, this.QueryData_Field.QueryText, 1);
        this.packInt(byteBuffer, this.QueryData_Field.QueryFlags);
        this.packByte(byteBuffer, (byte)this.QueryData_Field.Category);
        this.packVariable(byteBuffer, this.QueryData_Field.SimName, 1);
        this.packInt(byteBuffer, this.QueryData_Field.QueryStart);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryText = this.unpackVariable(byteBuffer, 1);
        this.QueryData_Field.QueryFlags = this.unpackInt(byteBuffer);
        this.QueryData_Field.Category = this.unpackByte(byteBuffer);
        this.QueryData_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.QueryData_Field.QueryStart = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class QueryData
    {
        public int Category;
        public int QueryFlags;
        public UUID QueryID;
        public int QueryStart;
        public byte[] QueryText;
        public byte[] SimName;
    }
}
