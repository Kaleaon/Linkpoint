package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class GroupProposalBallot extends SLMessage {
    public AgentData AgentData_Field;
    public ProposalData ProposalData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class ProposalData {
        public UUID GroupID;
        public UUID ProposalID;
        public byte[] VoteCast;
    }

    public GroupProposalBallot() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.ProposalData_Field = new ProposalData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.ProposalData_Field.VoteCast.length + 33 + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupProposalBallot(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 108);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.ProposalData_Field.ProposalID);
        packUUID(byteBuffer, this.ProposalData_Field.GroupID);
        packVariable(byteBuffer, this.ProposalData_Field.VoteCast, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ProposalData_Field.ProposalID = unpackUUID(byteBuffer);
        this.ProposalData_Field.GroupID = unpackUUID(byteBuffer);
        this.ProposalData_Field.VoteCast = unpackVariable(byteBuffer, 1);
    }
}
