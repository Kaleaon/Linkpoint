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

public class GroupActiveProposalItemReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;

        public AgentData()
        {
        }
    }

    public static class ProposalData
    {

        public boolean AlreadyVoted;
        public byte EndDateTime[];
        public float Majority;
        public byte ProposalText[];
        public int Quorum;
        public byte StartDateTime[];
        public byte TerseDateID[];
        public byte VoteCast[];
        public UUID VoteID;
        public UUID VoteInitiator;

        public ProposalData()
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


    public AgentData AgentData_Field;
    public ArrayList ProposalData_Fields;
    public TransactionData TransactionData_Field;

    public GroupActiveProposalItemReply()
    {
        ProposalData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        TransactionData_Field = new TransactionData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ProposalData_Fields.iterator();
        ProposalData proposaldata;
        int i;
        int j;
        int k;
        int l;
        int i1;
        for (i = 57; iterator.hasNext(); i = proposaldata.ProposalText.length + (j + 33 + 1 + k + 1 + l + 1 + 1 + i1 + 4 + 4 + 1) + i)
        {
            proposaldata = (ProposalData)iterator.next();
            j = proposaldata.TerseDateID.length;
            k = proposaldata.StartDateTime.length;
            l = proposaldata.EndDateTime.length;
            i1 = proposaldata.VoteCast.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupActiveProposalItemReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)104);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packUUID(bytebuffer, TransactionData_Field.TransactionID);
        packInt(bytebuffer, TransactionData_Field.TotalNumItems);
        bytebuffer.put((byte)ProposalData_Fields.size());
        ProposalData proposaldata;
        for (Iterator iterator = ProposalData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, proposaldata.ProposalText, 1))
        {
            proposaldata = (ProposalData)iterator.next();
            packUUID(bytebuffer, proposaldata.VoteID);
            packUUID(bytebuffer, proposaldata.VoteInitiator);
            packVariable(bytebuffer, proposaldata.TerseDateID, 1);
            packVariable(bytebuffer, proposaldata.StartDateTime, 1);
            packVariable(bytebuffer, proposaldata.EndDateTime, 1);
            packBoolean(bytebuffer, proposaldata.AlreadyVoted);
            packVariable(bytebuffer, proposaldata.VoteCast, 1);
            packFloat(bytebuffer, proposaldata.Majority);
            packInt(bytebuffer, proposaldata.Quorum);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        TransactionData_Field.TransactionID = unpackUUID(bytebuffer);
        TransactionData_Field.TotalNumItems = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ProposalData proposaldata = new ProposalData();
            proposaldata.VoteID = unpackUUID(bytebuffer);
            proposaldata.VoteInitiator = unpackUUID(bytebuffer);
            proposaldata.TerseDateID = unpackVariable(bytebuffer, 1);
            proposaldata.StartDateTime = unpackVariable(bytebuffer, 1);
            proposaldata.EndDateTime = unpackVariable(bytebuffer, 1);
            proposaldata.AlreadyVoted = unpackBoolean(bytebuffer);
            proposaldata.VoteCast = unpackVariable(bytebuffer, 1);
            proposaldata.Majority = unpackFloat(bytebuffer);
            proposaldata.Quorum = unpackInt(bytebuffer);
            proposaldata.ProposalText = unpackVariable(bytebuffer, 1);
            ProposalData_Fields.add(proposaldata);
        }

    }
}
