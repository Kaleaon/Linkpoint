// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MapItemReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields;
    public RequestData RequestData_Field;
    
    public MapItemReply() {
        this.Data_Fields = new ArrayList<Data>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.RequestData_Field = new RequestData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.Data_Fields.iterator();
        int n = 29;
        while (iterator.hasNext()) {
            n += iterator.next().Name.length + 33;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMapItemReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-101));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packInt(byteBuffer, this.AgentData_Field.Flags);
        this.packInt(byteBuffer, this.RequestData_Field.ItemType);
        byteBuffer.put((byte)this.Data_Fields.size());
        for (final Data data : this.Data_Fields) {
            this.packInt(byteBuffer, data.X);
            this.packInt(byteBuffer, data.Y);
            this.packUUID(byteBuffer, data.ID);
            this.packInt(byteBuffer, data.Extra);
            this.packInt(byteBuffer, data.Extra2);
            this.packVariable(byteBuffer, data.Name, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = this.unpackInt(byteBuffer);
        this.RequestData_Field.ItemType = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Data e = new Data();
            e.X = this.unpackInt(byteBuffer);
            e.Y = this.unpackInt(byteBuffer);
            e.ID = this.unpackUUID(byteBuffer);
            e.Extra = this.unpackInt(byteBuffer);
            e.Extra2 = this.unpackInt(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            this.Data_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int Flags;
    }
    
    public static class Data
    {
        public int Extra;
        public int Extra2;
        public UUID ID;
        public byte[] Name;
        public int X;
        public int Y;
    }
    
    public static class RequestData
    {
        public int ItemType;
    }
}
