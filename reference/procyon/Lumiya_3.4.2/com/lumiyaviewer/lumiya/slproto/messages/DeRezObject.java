// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DeRezObject extends SLMessage
{
    public AgentBlock AgentBlock_Field;
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields;
    
    public DeRezObject() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.AgentBlock_Field = new AgentBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Fields.size() * 4 + 88;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDeRezObject(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)35);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentBlock_Field.GroupID);
        this.packByte(byteBuffer, (byte)this.AgentBlock_Field.Destination);
        this.packUUID(byteBuffer, this.AgentBlock_Field.DestinationID);
        this.packUUID(byteBuffer, this.AgentBlock_Field.TransactionID);
        this.packByte(byteBuffer, (byte)this.AgentBlock_Field.PacketCount);
        this.packByte(byteBuffer, (byte)this.AgentBlock_Field.PacketNumber);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        final Iterator<Object> iterator = this.ObjectData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packInt(byteBuffer, iterator.next().ObjectLocalID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentBlock_Field.GroupID = this.unpackUUID(byteBuffer);
        this.AgentBlock_Field.Destination = (this.unpackByte(byteBuffer) & 0xFF);
        this.AgentBlock_Field.DestinationID = this.unpackUUID(byteBuffer);
        this.AgentBlock_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.AgentBlock_Field.PacketCount = (this.unpackByte(byteBuffer) & 0xFF);
        this.AgentBlock_Field.PacketNumber = (this.unpackByte(byteBuffer) & 0xFF);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ObjectLocalID = this.unpackInt(byteBuffer);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class AgentBlock
    {
        public int Destination;
        public UUID DestinationID;
        public UUID GroupID;
        public int PacketCount;
        public int PacketNumber;
        public UUID TransactionID;
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ObjectData
    {
        public int ObjectLocalID;
    }
}
