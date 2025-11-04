// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MapBlockReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields;
    public ArrayList<Size> Size_Fields;
    
    public MapBlockReply() {
        this.Data_Fields = new ArrayList<Data>();
        this.Size_Fields = new ArrayList<Size>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.Data_Fields.iterator();
        int n = 25;
        while (iterator.hasNext()) {
            n += iterator.next().Name.length + 5 + 1 + 4 + 1 + 1 + 16;
        }
        return n + 1 + this.Size_Fields.size() * 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMapBlockReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-103));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packInt(byteBuffer, this.AgentData_Field.Flags);
        byteBuffer.put((byte)this.Data_Fields.size());
        for (final Data data : this.Data_Fields) {
            this.packShort(byteBuffer, (short)data.X);
            this.packShort(byteBuffer, (short)data.Y);
            this.packVariable(byteBuffer, data.Name, 1);
            this.packByte(byteBuffer, (byte)data.Access);
            this.packInt(byteBuffer, data.RegionFlags);
            this.packByte(byteBuffer, (byte)data.WaterHeight);
            this.packByte(byteBuffer, (byte)data.Agents);
            this.packUUID(byteBuffer, data.MapImageID);
        }
        byteBuffer.put((byte)this.Size_Fields.size());
        for (final Size size : this.Size_Fields) {
            this.packShort(byteBuffer, (short)size.SizeX);
            this.packShort(byteBuffer, (short)size.SizeY);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Data e = new Data();
            e.X = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.Y = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.Name = this.unpackVariable(byteBuffer, 1);
            e.Access = (this.unpackByte(byteBuffer) & 0xFF);
            e.RegionFlags = this.unpackInt(byteBuffer);
            e.WaterHeight = (this.unpackByte(byteBuffer) & 0xFF);
            e.Agents = (this.unpackByte(byteBuffer) & 0xFF);
            e.MapImageID = this.unpackUUID(byteBuffer);
            this.Data_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final Size e2 = new Size();
            e2.SizeX = (this.unpackShort(byteBuffer) & 0xFFFF);
            e2.SizeY = (this.unpackShort(byteBuffer) & 0xFFFF);
            this.Size_Fields.add(e2);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int Flags;
    }
    
    public static class Data
    {
        public int Access;
        public int Agents;
        public UUID MapImageID;
        public byte[] Name;
        public int RegionFlags;
        public int WaterHeight;
        public int X;
        public int Y;
    }
    
    public static class Size
    {
        public int SizeX;
        public int SizeY;
    }
}
