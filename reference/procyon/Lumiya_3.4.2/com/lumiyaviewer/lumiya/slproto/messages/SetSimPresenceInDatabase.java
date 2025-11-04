// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SetSimPresenceInDatabase extends SLMessage
{
    public SimData SimData_Field;
    
    public SetSimPresenceInDatabase() {
        this.zeroCoded = false;
        this.SimData_Field = new SimData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.SimData_Field.HostName.length + 17 + 4 + 4 + 4 + 4 + 4 + 1 + this.SimData_Field.Status.length + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSetSimPresenceInDatabase(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)23);
        this.packUUID(byteBuffer, this.SimData_Field.RegionID);
        this.packVariable(byteBuffer, this.SimData_Field.HostName, 1);
        this.packInt(byteBuffer, this.SimData_Field.GridX);
        this.packInt(byteBuffer, this.SimData_Field.GridY);
        this.packInt(byteBuffer, this.SimData_Field.PID);
        this.packInt(byteBuffer, this.SimData_Field.AgentCount);
        this.packInt(byteBuffer, this.SimData_Field.TimeToLive);
        this.packVariable(byteBuffer, this.SimData_Field.Status, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.SimData_Field.RegionID = this.unpackUUID(byteBuffer);
        this.SimData_Field.HostName = this.unpackVariable(byteBuffer, 1);
        this.SimData_Field.GridX = this.unpackInt(byteBuffer);
        this.SimData_Field.GridY = this.unpackInt(byteBuffer);
        this.SimData_Field.PID = this.unpackInt(byteBuffer);
        this.SimData_Field.AgentCount = this.unpackInt(byteBuffer);
        this.SimData_Field.TimeToLive = this.unpackInt(byteBuffer);
        this.SimData_Field.Status = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class SimData
    {
        public int AgentCount;
        public int GridX;
        public int GridY;
        public byte[] HostName;
        public int PID;
        public UUID RegionID;
        public byte[] Status;
        public int TimeToLive;
    }
}
