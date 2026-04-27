// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelDisableObjects extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<OwnerIDs> OwnerIDs_Fields;
    public ParcelData ParcelData_Field;
    public ArrayList<TaskIDs> TaskIDs_Fields;
    
    public ParcelDisableObjects() {
        this.TaskIDs_Fields = new ArrayList<TaskIDs>();
        this.OwnerIDs_Fields = new ArrayList<OwnerIDs>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.TaskIDs_Fields.size() * 16 + 45 + 1 + this.OwnerIDs_Fields.size() * 16;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelDisableObjects(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-55));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.ParcelData_Field.LocalID);
        this.packInt(byteBuffer, this.ParcelData_Field.ReturnType);
        byteBuffer.put((byte)this.TaskIDs_Fields.size());
        final Iterator<Object> iterator = this.TaskIDs_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().TaskID);
        }
        byteBuffer.put((byte)this.OwnerIDs_Fields.size());
        final Iterator<Object> iterator2 = this.OwnerIDs_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packUUID(byteBuffer, iterator2.next().OwnerID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.LocalID = this.unpackInt(byteBuffer);
        this.ParcelData_Field.ReturnType = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final TaskIDs e = new TaskIDs();
            e.TaskID = this.unpackUUID(byteBuffer);
            this.TaskIDs_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final OwnerIDs e2 = new OwnerIDs();
            e2.OwnerID = this.unpackUUID(byteBuffer);
            this.OwnerIDs_Fields.add(e2);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class OwnerIDs
    {
        public UUID OwnerID;
    }
    
    public static class ParcelData
    {
        public int LocalID;
        public int ReturnType;
    }
    
    public static class TaskIDs
    {
        public UUID TaskID;
    }
}
