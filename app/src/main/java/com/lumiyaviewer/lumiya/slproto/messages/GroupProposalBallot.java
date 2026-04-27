package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class GroupProposalBallot extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ProposalData ProposalData_Field = new ProposalData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ProposalData {
        public UUID GroupID;
        public UUID ProposalID;
        public byte[] VoteCast;
    }

    public GroupProposalBallot() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.ProposalData_Field.VoteCast.length + 33 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupProposalBallot(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 108);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.ProposalData_Field.ProposalID);
        packUUID(byteBuffer, this.ProposalData_Field.GroupID);
        packVariable(byteBuffer, this.ProposalData_Field.VoteCast, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ProposalData_Field.ProposalID = unpackUUID(byteBuffer);
        this.ProposalData_Field.GroupID = unpackUUID(byteBuffer);
        this.ProposalData_Field.VoteCast = unpackVariable(byteBuffer, 1);
    }
}
