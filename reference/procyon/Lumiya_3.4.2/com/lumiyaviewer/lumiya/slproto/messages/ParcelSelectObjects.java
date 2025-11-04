// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelSelectObjects extends SLMessage
{
    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;
    public ArrayList<ReturnIDs> ReturnIDs_Fields;
    
    public ParcelSelectObjects() {
        this.ReturnIDs_Fields = new ArrayList<ReturnIDs>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ReturnIDs_Fields.size() * 16 + 45;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelSelectObjects(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-54));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.ParcelData_Field.LocalID);
        this.packInt(byteBuffer, this.ParcelData_Field.ReturnType);
        byteBuffer.put((byte)this.ReturnIDs_Fields.size());
        final Iterator<Object> iterator = this.ReturnIDs_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().ReturnID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.LocalID = this.unpackInt(byteBuffer);
        this.ParcelData_Field.ReturnType = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ReturnIDs e = new ReturnIDs();
            e.ReturnID = this.unpackUUID(byteBuffer);
            this.ReturnIDs_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ParcelData
    {
        public int LocalID;
        public int ReturnType;
    }
    
    public static class ReturnIDs
    {
        public UUID ReturnID;
    }
}
