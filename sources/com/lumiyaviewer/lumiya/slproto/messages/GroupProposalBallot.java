// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GroupProposalBallot extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ProposalData
    {

        public UUID GroupID;
        public UUID ProposalID;
        public byte VoteCast[];

        public ProposalData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ProposalData ProposalData_Field;

    public GroupProposalBallot()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        ProposalData_Field = new ProposalData();
    }

    public int CalcPayloadSize()
    {
        return ProposalData_Field.VoteCast.length + 33 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupProposalBallot(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)108);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, ProposalData_Field.ProposalID);
        packUUID(bytebuffer, ProposalData_Field.GroupID);
        packVariable(bytebuffer, ProposalData_Field.VoteCast, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ProposalData_Field.ProposalID = unpackUUID(bytebuffer);
        ProposalData_Field.GroupID = unpackUUID(bytebuffer);
        ProposalData_Field.VoteCast = unpackVariable(bytebuffer, 1);
    }
}
