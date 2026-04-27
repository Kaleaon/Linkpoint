// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SetStartLocationRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public StartLocationData StartLocationData_Field;
    
    public SetStartLocationRequest() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.StartLocationData_Field = new StartLocationData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.StartLocationData_Field.SimName.length + 1 + 4 + 12 + 12 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSetStartLocationRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)68);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packVariable(byteBuffer, this.StartLocationData_Field.SimName, 1);
        this.packInt(byteBuffer, this.StartLocationData_Field.LocationID);
        this.packLLVector3(byteBuffer, this.StartLocationData_Field.LocationPos);
        this.packLLVector3(byteBuffer, this.StartLocationData_Field.LocationLookAt);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.StartLocationData_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.StartLocationData_Field.LocationID = this.unpackInt(byteBuffer);
        this.StartLocationData_Field.LocationPos = this.unpackLLVector3(byteBuffer);
        this.StartLocationData_Field.LocationLookAt = this.unpackLLVector3(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class StartLocationData
    {
        public int LocationID;
        public LLVector3 LocationLookAt;
        public LLVector3 LocationPos;
        public byte[] SimName;
    }
}
