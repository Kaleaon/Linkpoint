// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupDataUpdate extends SLMessage
{
    public ArrayList<AgentGroupData> AgentGroupData_Fields;
    
    public GroupDataUpdate() {
        this.AgentGroupData_Fields = new ArrayList<AgentGroupData>();
        this.zeroCoded = true;
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.AgentGroupData_Fields.iterator();
        int n = 5;
        while (iterator.hasNext()) {
            n += iterator.next().GroupTitle.length + 41;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupDataUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-124));
        byteBuffer.put((byte)this.AgentGroupData_Fields.size());
        for (final AgentGroupData agentGroupData : this.AgentGroupData_Fields) {
            this.packUUID(byteBuffer, agentGroupData.AgentID);
            this.packUUID(byteBuffer, agentGroupData.GroupID);
            this.packLong(byteBuffer, agentGroupData.AgentPowers);
            this.packVariable(byteBuffer, agentGroupData.GroupTitle, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final AgentGroupData e = new AgentGroupData();
            e.AgentID = this.unpackUUID(byteBuffer);
            e.GroupID = this.unpackUUID(byteBuffer);
            e.AgentPowers = this.unpackLong(byteBuffer);
            e.GroupTitle = this.unpackVariable(byteBuffer, 1);
            this.AgentGroupData_Fields.add(e);
        }
    }
    
    public static class AgentGroupData
    {
        public UUID AgentID;
        public long AgentPowers;
        public UUID GroupID;
        public byte[] GroupTitle;
    }
}
