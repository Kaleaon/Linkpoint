package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class GroupVoteHistoryItemReply extends SLMessage {
    public AgentData AgentData_Field;
    public HistoryItemData HistoryItemData_Field;
    public TransactionData TransactionData_Field;
    public ArrayList<VoteItem> VoteItem_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
    }

    public static class HistoryItemData {
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

    public static class TransactionData {
        public int TotalNumItems;
        public UUID TransactionID;
    }

    public static class VoteItem {
        public UUID CandidateID;
        public int NumVotes;
        public byte[] VoteCast;
    }

    public GroupVoteHistoryItemReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.TransactionData_Field = new TransactionData();
        this.HistoryItemData_Field = new HistoryItemData();
    }

    public int CalcPayloadSize() {
        int length = this.HistoryItemData_Field.TerseDateID.length + 17 + 1 + this.HistoryItemData_Field.StartDateTime.length + 1 + this.HistoryItemData_Field.EndDateTime.length + 16 + 1 + this.HistoryItemData_Field.VoteType.length + 1 + this.HistoryItemData_Field.VoteResult.length + 4 + 4 + 2 + this.HistoryItemData_Field.ProposalText.length + 56 + 1;
        Iterator<T> it = this.VoteItem_Fields.iterator();
        while (true) {
            int i = length;
            if (!it.hasNext()) {
                return i;
            }
            length = ((VoteItem) it.next()).VoteCast.length + 17 + 4 + i;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupVoteHistoryItemReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 106);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        packInt(byteBuffer, this.TransactionData_Field.TotalNumItems);
        packUUID(byteBuffer, this.HistoryItemData_Field.VoteID);
        packVariable(byteBuffer, this.HistoryItemData_Field.TerseDateID, 1);
        packVariable(byteBuffer, this.HistoryItemData_Field.StartDateTime, 1);
        packVariable(byteBuffer, this.HistoryItemData_Field.EndDateTime, 1);
        packUUID(byteBuffer, this.HistoryItemData_Field.VoteInitiator);
        packVariable(byteBuffer, this.HistoryItemData_Field.VoteType, 1);
        packVariable(byteBuffer, this.HistoryItemData_Field.VoteResult, 1);
        packFloat(byteBuffer, this.HistoryItemData_Field.Majority);
        packInt(byteBuffer, this.HistoryItemData_Field.Quorum);
        packVariable(byteBuffer, this.HistoryItemData_Field.ProposalText, 2);
        byteBuffer.put((byte) this.VoteItem_Fields.size());
        for (VoteItem voteItem : this.VoteItem_Fields) {
            packUUID(byteBuffer, voteItem.CandidateID);
            packVariable(byteBuffer, voteItem.VoteCast, 1);
            packInt(byteBuffer, voteItem.NumVotes);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer);
        this.TransactionData_Field.TotalNumItems = unpackInt(byteBuffer);
        this.HistoryItemData_Field.VoteID = unpackUUID(byteBuffer);
        this.HistoryItemData_Field.TerseDateID = unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.StartDateTime = unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.EndDateTime = unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.VoteInitiator = unpackUUID(byteBuffer);
        this.HistoryItemData_Field.VoteType = unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.VoteResult = unpackVariable(byteBuffer, 1);
        this.HistoryItemData_Field.Majority = unpackFloat(byteBuffer);
        this.HistoryItemData_Field.Quorum = unpackInt(byteBuffer);
        this.HistoryItemData_Field.ProposalText = unpackVariable(byteBuffer, 2);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            VoteItem voteItem = new VoteItem();
            voteItem.CandidateID = unpackUUID(byteBuffer);
            voteItem.VoteCast = unpackVariable(byteBuffer, 1);
            voteItem.NumVotes = unpackInt(byteBuffer);
            this.VoteItem_Fields.add(voteItem);
        }
    }
}
