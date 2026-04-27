package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class StartGroupProposal extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ProposalData ProposalData_Field = new ProposalData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ProposalData {
        public int Duration;
        public UUID GroupID;
        public float Majority;
        public byte[] ProposalText;
        public int Quorum;
    }

    public StartGroupProposal() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.ProposalData_Field.ProposalText.length + 29 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartGroupProposal(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 107);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.ProposalData_Field.GroupID);
        packInt(byteBuffer, this.ProposalData_Field.Quorum);
        packFloat(byteBuffer, this.ProposalData_Field.Majority);
        packInt(byteBuffer, this.ProposalData_Field.Duration);
        packVariable(byteBuffer, this.ProposalData_Field.ProposalText, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ProposalData_Field.GroupID = unpackUUID(byteBuffer);
        this.ProposalData_Field.Quorum = unpackInt(byteBuffer);
        this.ProposalData_Field.Majority = unpackFloat(byteBuffer);
        this.ProposalData_Field.Duration = unpackInt(byteBuffer);
        this.ProposalData_Field.ProposalText = unpackVariable(byteBuffer, 1);
    }
}
