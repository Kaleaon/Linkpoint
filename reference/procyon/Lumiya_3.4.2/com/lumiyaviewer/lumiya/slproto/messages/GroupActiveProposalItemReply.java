// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupActiveProposalItemReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<ProposalData> ProposalData_Fields;
    public TransactionData TransactionData_Field;
    
    public GroupActiveProposalItemReply() {
        this.ProposalData_Fields = new ArrayList<ProposalData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.TransactionData_Field = new TransactionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.ProposalData_Fields.iterator();
        int n = 57;
        while (iterator.hasNext()) {
            final ProposalData proposalData = iterator.next();
            n += proposalData.ProposalText.length + (proposalData.TerseDateID.length + 33 + 1 + proposalData.StartDateTime.length + 1 + proposalData.EndDateTime.length + 1 + 1 + proposalData.VoteCast.length + 4 + 4 + 1);
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupActiveProposalItemReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)104);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        this.packInt(byteBuffer, this.TransactionData_Field.TotalNumItems);
        byteBuffer.put((byte)this.ProposalData_Fields.size());
        for (final ProposalData proposalData : this.ProposalData_Fields) {
            this.packUUID(byteBuffer, proposalData.VoteID);
            this.packUUID(byteBuffer, proposalData.VoteInitiator);
            this.packVariable(byteBuffer, proposalData.TerseDateID, 1);
            this.packVariable(byteBuffer, proposalData.StartDateTime, 1);
            this.packVariable(byteBuffer, proposalData.EndDateTime, 1);
            this.packBoolean(byteBuffer, proposalData.AlreadyVoted);
            this.packVariable(byteBuffer, proposalData.VoteCast, 1);
            this.packFloat(byteBuffer, proposalData.Majority);
            this.packInt(byteBuffer, proposalData.Quorum);
            this.packVariable(byteBuffer, proposalData.ProposalText, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.TransactionData_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.TransactionData_Field.TotalNumItems = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ProposalData e = new ProposalData();
            e.VoteID = this.unpackUUID(byteBuffer);
            e.VoteInitiator = this.unpackUUID(byteBuffer);
            e.TerseDateID = this.unpackVariable(byteBuffer, 1);
            e.StartDateTime = this.unpackVariable(byteBuffer, 1);
            e.EndDateTime = this.unpackVariable(byteBuffer, 1);
            e.AlreadyVoted = this.unpackBoolean(byteBuffer);
            e.VoteCast = this.unpackVariable(byteBuffer, 1);
            e.Majority = this.unpackFloat(byteBuffer);
            e.Quorum = this.unpackInt(byteBuffer);
            e.ProposalText = this.unpackVariable(byteBuffer, 1);
            this.ProposalData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
    }
    
    public static class ProposalData
    {
        public boolean AlreadyVoted;
        public byte[] EndDateTime;
        public float Majority;
        public byte[] ProposalText;
        public int Quorum;
        public byte[] StartDateTime;
        public byte[] TerseDateID;
        public byte[] VoteCast;
        public UUID VoteID;
        public UUID VoteInitiator;
    }
    
    public static class TransactionData
    {
        public int TotalNumItems;
        public UUID TransactionID;
    }
}
