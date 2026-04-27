// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentMovementComplete extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    public SimData SimData_Field;
    
    public AgentMovementComplete() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
        this.SimData_Field = new SimData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.SimData_Field.ChannelVersion.length + 2 + 72;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentMovementComplete(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-6));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packLLVector3(byteBuffer, this.Data_Field.Position);
        this.packLLVector3(byteBuffer, this.Data_Field.LookAt);
        this.packLong(byteBuffer, this.Data_Field.RegionHandle);
        this.packInt(byteBuffer, this.Data_Field.Timestamp);
        this.packVariable(byteBuffer, this.SimData_Field.ChannelVersion, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Data_Field.Position = this.unpackLLVector3(byteBuffer);
        this.Data_Field.LookAt = this.unpackLLVector3(byteBuffer);
        this.Data_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.Data_Field.Timestamp = this.unpackInt(byteBuffer);
        this.SimData_Field.ChannelVersion = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Data
    {
        public LLVector3 LookAt;
        public LLVector3 Position;
        public long RegionHandle;
        public int Timestamp;
    }
    
    public static class SimData
    {
        public byte[] ChannelVersion;
    }
}
