// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SetSimStatusInDatabase extends SLMessage
{
    public Data Data_Field;
    
    public SetSimStatusInDatabase() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.HostName.length + 17 + 4 + 4 + 4 + 4 + 4 + 1 + this.Data_Field.Status.length + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSetSimStatusInDatabase(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)22);
        this.packUUID(byteBuffer, this.Data_Field.RegionID);
        this.packVariable(byteBuffer, this.Data_Field.HostName, 1);
        this.packInt(byteBuffer, this.Data_Field.X);
        this.packInt(byteBuffer, this.Data_Field.Y);
        this.packInt(byteBuffer, this.Data_Field.PID);
        this.packInt(byteBuffer, this.Data_Field.AgentCount);
        this.packInt(byteBuffer, this.Data_Field.TimeToLive);
        this.packVariable(byteBuffer, this.Data_Field.Status, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.RegionID = this.unpackUUID(byteBuffer);
        this.Data_Field.HostName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.X = this.unpackInt(byteBuffer);
        this.Data_Field.Y = this.unpackInt(byteBuffer);
        this.Data_Field.PID = this.unpackInt(byteBuffer);
        this.Data_Field.AgentCount = this.unpackInt(byteBuffer);
        this.Data_Field.TimeToLive = this.unpackInt(byteBuffer);
        this.Data_Field.Status = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class Data
    {
        public int AgentCount;
        public byte[] HostName;
        public int PID;
        public UUID RegionID;
        public byte[] Status;
        public int TimeToLive;
        public int X;
        public int Y;
    }
}
