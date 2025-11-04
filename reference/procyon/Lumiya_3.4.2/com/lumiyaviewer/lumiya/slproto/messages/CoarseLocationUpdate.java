// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CoarseLocationUpdate extends SLMessage
{
    public ArrayList<AgentData> AgentData_Fields;
    public Index Index_Field;
    public ArrayList<Location> Location_Fields;
    
    public CoarseLocationUpdate() {
        this.Location_Fields = new ArrayList<Location>();
        this.AgentData_Fields = new ArrayList<AgentData>();
        this.zeroCoded = false;
        this.Index_Field = new Index();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Location_Fields.size() * 3 + 3 + 4 + 1 + this.AgentData_Fields.size() * 16;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCoarseLocationUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)6);
        byteBuffer.put((byte)this.Location_Fields.size());
        for (final Location location : this.Location_Fields) {
            this.packByte(byteBuffer, (byte)location.X);
            this.packByte(byteBuffer, (byte)location.Y);
            this.packByte(byteBuffer, (byte)location.Z);
        }
        this.packShort(byteBuffer, (short)this.Index_Field.You);
        this.packShort(byteBuffer, (short)this.Index_Field.Prey);
        byteBuffer.put((byte)this.AgentData_Fields.size());
        final Iterator<Object> iterator2 = this.AgentData_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packUUID(byteBuffer, iterator2.next().AgentID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Location e = new Location();
            e.X = (this.unpackByte(byteBuffer) & 0xFF);
            e.Y = (this.unpackByte(byteBuffer) & 0xFF);
            e.Z = (this.unpackByte(byteBuffer) & 0xFF);
            this.Location_Fields.add(e);
        }
        this.Index_Field.You = this.unpackShort(byteBuffer);
        this.Index_Field.Prey = this.unpackShort(byteBuffer);
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final AgentData e2 = new AgentData();
            e2.AgentID = this.unpackUUID(byteBuffer);
            this.AgentData_Fields.add(e2);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class Index
    {
        public int Prey;
        public int You;
    }
    
    public static class Location
    {
        public int X;
        public int Y;
        public int Z;
    }
}
