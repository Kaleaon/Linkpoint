// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelClaim extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    public ArrayList<ParcelData> ParcelData_Fields;
    
    public ParcelClaim() {
        this.ParcelData_Fields = new ArrayList<ParcelData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ParcelData_Fields.size() * 16 + 55;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelClaim(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-47));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.Data_Field.GroupID);
        this.packBoolean(byteBuffer, this.Data_Field.IsGroupOwned);
        this.packBoolean(byteBuffer, this.Data_Field.Final);
        byteBuffer.put((byte)this.ParcelData_Fields.size());
        for (final ParcelData parcelData : this.ParcelData_Fields) {
            this.packFloat(byteBuffer, parcelData.West);
            this.packFloat(byteBuffer, parcelData.South);
            this.packFloat(byteBuffer, parcelData.East);
            this.packFloat(byteBuffer, parcelData.North);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Data_Field.GroupID = this.unpackUUID(byteBuffer);
        this.Data_Field.IsGroupOwned = this.unpackBoolean(byteBuffer);
        this.Data_Field.Final = this.unpackBoolean(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ParcelData e = new ParcelData();
            e.West = this.unpackFloat(byteBuffer);
            e.South = this.unpackFloat(byteBuffer);
            e.East = this.unpackFloat(byteBuffer);
            e.North = this.unpackFloat(byteBuffer);
            this.ParcelData_Fields.add(e);
        }
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
    }
    
    public static class ParcelData
    {
        public float East;
        public float North;
        public float South;
        public float West;
    }
}
