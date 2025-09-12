// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GroupVoteHistoryItemReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;

        public AgentData()
        {
        }
    }

    public static class HistoryItemData
    {

        public byte EndDateTime[];
        public float Majority;
        public byte ProposalText[];
        public int Quorum;
        public byte StartDateTime[];
        public byte TerseDateID[];
        public UUID VoteID;
        public UUID VoteInitiator;
        public byte VoteResult[];
        public byte VoteType[];

        public HistoryItemData()
        {
        }
    }

    public static class TransactionData
    {

        public int TotalNumItems;
        public UUID TransactionID;

        public TransactionData()
        {
        }
    }

    public static class VoteItem
    {

        public UUID CandidateID;
        public int NumVotes;
        public byte VoteCast[];

        public VoteItem()
        {
        }
    }


    public AgentData AgentData_Field;
    public HistoryItemData HistoryItemData_Field;
    public TransactionData TransactionData_Field;
    public ArrayList VoteItem_Fields;

    public GroupVoteHistoryItemReply()
    {
        VoteItem_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        TransactionData_Field = new TransactionData();
        HistoryItemData_Field = new HistoryItemData();
    }

    public int CalcPayloadSize()
    {
        int i = HistoryItemData_Field.TerseDateID.length;
        int j = HistoryItemData_Field.StartDateTime.length;
        int k = HistoryItemData_Field.EndDateTime.length;
        int l = HistoryItemData_Field.VoteType.length;
        int i1 = HistoryItemData_Field.VoteResult.length;
        int j1 = HistoryItemData_Field.ProposalText.length;
        Iterator iterator = VoteItem_Fields.iterator();
        for (i = i + 17 + 1 + j + 1 + k + 16 + 1 + l + 1 + i1 + 4 + 4 + 2 + j1 + 56 + 1; iterator.hasNext(); i = ((VoteItem)iterator.next()).VoteCast.length + 17 + 4 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupVoteHistoryItemReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)106);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packUUID(bytebuffer, TransactionData_Field.TransactionID);
        packInt(bytebuffer, TransactionData_Field.TotalNumItems);
        packUUID(bytebuffer, HistoryItemData_Field.VoteID);
        packVariable(bytebuffer, HistoryItemData_Field.TerseDateID, 1);
        packVariable(bytebuffer, HistoryItemData_Field.StartDateTime, 1);
        packVariable(bytebuffer, HistoryItemData_Field.EndDateTime, 1);
        packUUID(bytebuffer, HistoryItemData_Field.VoteInitiator);
        packVariable(bytebuffer, HistoryItemData_Field.VoteType, 1);
        packVariable(bytebuffer, HistoryItemData_Field.VoteResult, 1);
        packFloat(bytebuffer, HistoryItemData_Field.Majority);
        packInt(bytebuffer, HistoryItemData_Field.Quorum);
        packVariable(bytebuffer, HistoryItemData_Field.ProposalText, 2);
        bytebuffer.put((byte)VoteItem_Fields.size());
        VoteItem voteitem;
        for (Iterator iterator = VoteItem_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, voteitem.NumVotes))
        {
            voteitem = (VoteItem)iterator.next();
            packUUID(bytebuffer, voteitem.CandidateID);
            packVariable(bytebuffer, voteitem.VoteCast, 1);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        TransactionData_Field.TransactionID = unpackUUID(bytebuffer);
        TransactionData_Field.TotalNumItems = unpackInt(bytebuffer);
        HistoryItemData_Field.VoteID = unpackUUID(bytebuffer);
        HistoryItemData_Field.TerseDateID = unpackVariable(bytebuffer, 1);
        HistoryItemData_Field.StartDateTime = unpackVariable(bytebuffer, 1);
        HistoryItemData_Field.EndDateTime = unpackVariable(bytebuffer, 1);
        HistoryItemData_Field.VoteInitiator = unpackUUID(bytebuffer);
        HistoryItemData_Field.VoteType = unpackVariable(bytebuffer, 1);
        HistoryItemData_Field.VoteResult = unpackVariable(bytebuffer, 1);
        HistoryItemData_Field.Majority = unpackFloat(bytebuffer);
        HistoryItemData_Field.Quorum = unpackInt(bytebuffer);
        HistoryItemData_Field.ProposalText = unpackVariable(bytebuffer, 2);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            VoteItem voteitem = new VoteItem();
            voteitem.CandidateID = unpackUUID(bytebuffer);
            voteitem.VoteCast = unpackVariable(bytebuffer, 1);
            voteitem.NumVotes = unpackInt(bytebuffer);
            VoteItem_Fields.add(voteitem);
        }

    }
}
