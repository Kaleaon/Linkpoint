// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelBuy extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    public ParcelData ParcelData_Field;
    
    public ParcelBuy() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 67;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelBuy(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-43));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.Data_Field.GroupID);
        this.packBoolean(byteBuffer, this.Data_Field.IsGroupOwned);
        this.packBoolean(byteBuffer, this.Data_Field.RemoveContribution);
        this.packInt(byteBuffer, this.Data_Field.LocalID);
        this.packBoolean(byteBuffer, this.Data_Field.Final);
        this.packInt(byteBuffer, this.ParcelData_Field.Price);
        this.packInt(byteBuffer, this.ParcelData_Field.Area);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Data_Field.GroupID = this.unpackUUID(byteBuffer);
        this.Data_Field.IsGroupOwned = this.unpackBoolean(byteBuffer);
        this.Data_Field.RemoveContribution = this.unpackBoolean(byteBuffer);
        this.Data_Field.LocalID = this.unpackInt(byteBuffer);
        this.Data_Field.Final = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.Price = this.unpackInt(byteBuffer);
        this.ParcelData_Field.Area = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Data
    {
        public boolean Final;
        public UUID GroupID;
        public boolean IsGroupOwned;
        public int LocalID;
        public boolean RemoveContribution;
    }
    
    public static class ParcelData
    {
        public int Area;
        public int Price;
    }
}
