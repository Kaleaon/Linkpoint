// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DeactivateGestures extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields;
    
    public DeactivateGestures() {
        this.Data_Fields = new ArrayList<Data>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Fields.size() * 20 + 41;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDeactivateGestures(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)61);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.Flags);
        byteBuffer.put((byte)this.Data_Fields.size());
        for (final Data data : this.Data_Fields) {
            this.packUUID(byteBuffer, data.ItemID);
            this.packInt(byteBuffer, data.GestureFlags);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Data e = new Data();
            e.ItemID = this.unpackUUID(byteBuffer);
            e.GestureFlags = this.unpackInt(byteBuffer);
            this.Data_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int Flags;
        public UUID SessionID;
    }
    
    public static class Data
    {
        public int GestureFlags;
        public UUID ItemID;
    }
}
