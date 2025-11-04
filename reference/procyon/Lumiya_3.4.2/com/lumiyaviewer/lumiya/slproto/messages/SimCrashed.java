// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimCrashed extends SLMessage
{
    public Data Data_Field;
    public ArrayList<Users> Users_Fields;
    
    public SimCrashed() {
        this.Users_Fields = new ArrayList<Users>();
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Users_Fields.size() * 16 + 13;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSimCrashed(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)72);
        this.packInt(byteBuffer, this.Data_Field.RegionX);
        this.packInt(byteBuffer, this.Data_Field.RegionY);
        byteBuffer.put((byte)this.Users_Fields.size());
        final Iterator<Object> iterator = this.Users_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().AgentID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.RegionX = this.unpackInt(byteBuffer);
        this.Data_Field.RegionY = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Users e = new Users();
            e.AgentID = this.unpackUUID(byteBuffer);
            this.Users_Fields.add(e);
        }
    }
    
    public static class Data
    {
        public int RegionX;
        public int RegionY;
    }
    
    public static class Users
    {
        public UUID AgentID;
    }
}
