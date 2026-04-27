// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupVoteHistoryItemReply extends SLMessage
{
    public AgentData AgentData_Field;
    public HistoryItemData HistoryItemData_Field;
    public TransactionData TransactionData_Field;
    public ArrayList<VoteItem> VoteItem_Fields;
    
    public GroupVoteHistoryItemReply() {
        this.VoteItem_Fields = new ArrayList<VoteItem>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.TransactionData_Field = new TransactionData();
        this.HistoryItemData_Field = new HistoryItemData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int length = this.HistoryItemData_Field.TerseDateID.length;
        final int length2 = this.HistoryItemData_Field.StartDateTime.length;
        final int length3 = this.HistoryItemData_Field.EndDateTime.length;
        final int length4 = this.HistoryItemData_Field.VoteType.length;
        final int length5 = this.HistoryItemData_Field.VoteResult.length;
        final int length6 = this.HistoryItemData_Field.ProposalText.length;
        final Iterator<Object> iterator = this.VoteItem_Fields.iterator();
        int n = length + 17 + 1 + length2 + 1 + length3 + 16 + 1 + length4 + 1 + length5 + 4 + 4 + 2 + length6 + 56 + 1;
        while (iterator.hasNext()) {
            n += iterator.next().VoteCast.length + 17 + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupVoteHistoryItemReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)106);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        this.packInt(byteBuffer, this.TransactionData_Field.TotalNumItems);
        this.packUUID(byteBuffer, this.HistoryItemData_Field.VoteID);
        this.packVariable(byteBuffer, this.HistoryItemData_Field.TerseDateID, 1);
        this.packVariable(byteBuffer, this.HistoryItemData_Field.StartDateTime, 1);
        this.packVariable(byteBuffer, this.HistoryItemData_Field.EndDateTime, 1);
        this.packUUID(byteBuffer, this.HistoryItemData_Field.VoteInitiator);
        this.packVariable(byteBuffer, this.HistoryItemData_Field.VoteType, 1);
        this.packVariable(byteBuffer, this.HistoryItemData_Field.VoteResult, 1);
        this.packFloat(byteBuffer, this.HistoryItemData_Field.Majority);
        this.packInt(byteBuffer, this.HistoryItemData_Field.Quorum);
        this.packVariable(byteBuffer, this.HistoryItemData_Field.ProposalText, 2);
        byteBuffer.put((byte)this.VoteItem_Fields.size());
        for (final VoteItem voteItem : this.VoteItem_Fields) {
            this.packUUID(byteBuffer, voteItem.CandidateID);
            this.packVariable(byteBuffer, voteItem.VoteCast, 1);
            this.packInt(byteBuffer, voteItem.NumVotes);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.TransactionData_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.TransactionData_Field.TotalNumItems = this.unpackInt(byteBuffer);
        this.HistoryItemData_Field.VoteID = this.unpackUUID(byteBuffer);
        this.HistoryItemData_Field.TerseDateID = this.unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.StartDateTime = this.unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.EndDateTime = this.unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.VoteInitiator = this.unpackUUID(byteBuffer);
        this.HistoryItemData_Field.VoteType = this.unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.VoteResult = this.unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.Majority = this.unpackFloat(byteBuffer);
        this.HistoryItemData_Field.Quorum = this.unpackInt(byteBuffer);
        this.HistoryItemData_Field.ProposalText = this.unpackVariable(byteBuffer, 2);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final VoteItem e = new VoteItem();
            e.CandidateID = this.unpackUUID(byteBuffer);
            e.VoteCast = this.unpackVariable(byteBuffer, 1);
            e.NumVotes = this.unpackInt(byteBuffer);
            this.VoteItem_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
    }
    
    public static class HistoryItemData
    {
        public byte[] EndDateTime;
        public float Majority;
        public byte[] ProposalText;
        public int Quorum;
        public byte[] StartDateTime;
        public byte[] TerseDateID;
        public UUID VoteID;
        public UUID VoteInitiator;
        public byte[] VoteResult;
        public byte[] VoteType;
    }
    
    public static class TransactionData
    {
        public int TotalNumItems;
        public UUID TransactionID;
    }
    
    public static class VoteItem
    {
        public UUID CandidateID;
        public int NumVotes;
        public byte[] VoteCast;
    }
}
