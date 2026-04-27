// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentGroupDataUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<GroupData> GroupData_Fields;
    
    public AgentGroupDataUpdate() {
        this.GroupData_Fields = new ArrayList<GroupData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.GroupData_Fields.iterator();
        int n = 21;
        while (iterator.hasNext()) {
            n += iterator.next().GroupName.length + 46;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentGroupDataUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-123));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        byteBuffer.put((byte)this.GroupData_Fields.size());
        for (final GroupData groupData : this.GroupData_Fields) {
            this.packUUID(byteBuffer, groupData.GroupID);
            this.packLong(byteBuffer, groupData.GroupPowers);
            this.packBoolean(byteBuffer, groupData.AcceptNotices);
            this.packUUID(byteBuffer, groupData.GroupInsigniaID);
            this.packInt(byteBuffer, groupData.Contribution);
            this.packVariable(byteBuffer, groupData.GroupName, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final GroupData e = new GroupData();
            e.GroupID = this.unpackUUID(byteBuffer);
            e.GroupPowers = this.unpackLong(byteBuffer);
            e.AcceptNotices = this.unpackBoolean(byteBuffer);
            e.GroupInsigniaID = this.unpackUUID(byteBuffer);
            e.Contribution = this.unpackInt(byteBuffer);
            e.GroupName = this.unpackVariable(byteBuffer, 1);
            this.GroupData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class GroupData
    {
        public boolean AcceptNotices;
        public int Contribution;
        public UUID GroupID;
        public UUID GroupInsigniaID;
        public byte[] GroupName;
        public long GroupPowers;
    }
}
