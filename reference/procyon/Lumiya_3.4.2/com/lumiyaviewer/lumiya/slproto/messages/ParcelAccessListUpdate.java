// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelAccessListUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    public ArrayList<List> List_Fields;
    
    public ParcelAccessListUpdate() {
        this.List_Fields = new ArrayList<List>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.List_Fields.size() * 24 + 69;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelAccessListUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-39));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.Data_Field.Flags);
        this.packInt(byteBuffer, this.Data_Field.LocalID);
        this.packUUID(byteBuffer, this.Data_Field.TransactionID);
        this.packInt(byteBuffer, this.Data_Field.SequenceID);
        this.packInt(byteBuffer, this.Data_Field.Sections);
        byteBuffer.put((byte)this.List_Fields.size());
        for (final List list : this.List_Fields) {
            this.packUUID(byteBuffer, list.ID);
            this.packInt(byteBuffer, list.Time);
            this.packInt(byteBuffer, list.Flags);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Data_Field.Flags = this.unpackInt(byteBuffer);
        this.Data_Field.LocalID = this.unpackInt(byteBuffer);
        this.Data_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.Data_Field.SequenceID = this.unpackInt(byteBuffer);
        this.Data_Field.Sections = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final List e = new List();
            e.ID = this.unpackUUID(byteBuffer);
            e.Time = this.unpackInt(byteBuffer);
            e.Flags = this.unpackInt(byteBuffer);
            this.List_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Data
    {
        public int Flags;
        public int LocalID;
        public int Sections;
        public int SequenceID;
        public UUID TransactionID;
    }
    
    public static class List
    {
        public int Flags;
        public UUID ID;
        public int Time;
    }
}
