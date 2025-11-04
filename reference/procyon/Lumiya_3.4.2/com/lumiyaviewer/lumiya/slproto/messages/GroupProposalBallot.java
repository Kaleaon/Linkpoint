// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupProposalBallot extends SLMessage
{
    public AgentData AgentData_Field;
    public ProposalData ProposalData_Field;
    
    public GroupProposalBallot() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.ProposalData_Field = new ProposalData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ProposalData_Field.VoteCast.length + 33 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupProposalBallot(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)108);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.ProposalData_Field.ProposalID);
        this.packUUID(byteBuffer, this.ProposalData_Field.GroupID);
        this.packVariable(byteBuffer, this.ProposalData_Field.VoteCast, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ProposalData_Field.ProposalID = this.unpackUUID(byteBuffer);
        this.ProposalData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.ProposalData_Field.VoteCast = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ProposalData
    {
        public UUID GroupID;
        public UUID ProposalID;
        public byte[] VoteCast;
    }
}
