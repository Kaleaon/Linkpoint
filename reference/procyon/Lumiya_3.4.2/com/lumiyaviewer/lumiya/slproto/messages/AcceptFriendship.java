// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AcceptFriendship extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<FolderData> FolderData_Fields;
    public TransactionBlock TransactionBlock_Field;
    
    public AcceptFriendship() {
        this.FolderData_Fields = new ArrayList<FolderData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.TransactionBlock_Field = new TransactionBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.FolderData_Fields.size() * 16 + 53;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAcceptFriendship(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)41);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.TransactionBlock_Field.TransactionID);
        byteBuffer.put((byte)this.FolderData_Fields.size());
        final Iterator<Object> iterator = this.FolderData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().FolderID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.TransactionBlock_Field.TransactionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final FolderData e = new FolderData();
            e.FolderID = this.unpackUUID(byteBuffer);
            this.FolderData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class FolderData
    {
        public UUID FolderID;
    }
    
    public static class TransactionBlock
    {
        public UUID TransactionID;
    }
}
