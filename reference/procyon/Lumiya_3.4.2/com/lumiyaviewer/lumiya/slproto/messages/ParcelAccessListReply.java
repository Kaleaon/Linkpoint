// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelAccessListReply extends SLMessage
{
    public Data Data_Field;
    public ArrayList<List> List_Fields;
    
    public ParcelAccessListReply() {
        this.List_Fields = new ArrayList<List>();
        this.zeroCoded = true;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.List_Fields.size() * 24 + 33;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelAccessListReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-40));
        this.packUUID(byteBuffer, this.Data_Field.AgentID);
        this.packInt(byteBuffer, this.Data_Field.SequenceID);
        this.packInt(byteBuffer, this.Data_Field.Flags);
        this.packInt(byteBuffer, this.Data_Field.LocalID);
        byteBuffer.put((byte)this.List_Fields.size());
        for (final List list : this.List_Fields) {
            this.packUUID(byteBuffer, list.ID);
            this.packInt(byteBuffer, list.Time);
            this.packInt(byteBuffer, list.Flags);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Data_Field.SequenceID = this.unpackInt(byteBuffer);
        this.Data_Field.Flags = this.unpackInt(byteBuffer);
        this.Data_Field.LocalID = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final List e = new List();
            e.ID = this.unpackUUID(byteBuffer);
            e.Time = this.unpackInt(byteBuffer);
            e.Flags = this.unpackInt(byteBuffer);
            this.List_Fields.add(e);
        }
    }
    
    public static class Data
    {
        public UUID AgentID;
        public int Flags;
        public int LocalID;
        public int SequenceID;
    }
    
    public static class List
    {
        public int Flags;
        public UUID ID;
        public int Time;
    }
}
