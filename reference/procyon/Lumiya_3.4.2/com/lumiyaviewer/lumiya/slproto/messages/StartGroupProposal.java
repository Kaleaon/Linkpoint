// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class StartGroupProposal extends SLMessage
{
    public AgentData AgentData_Field;
    public ProposalData ProposalData_Field;
    
    public StartGroupProposal() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ProposalData_Field = new ProposalData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ProposalData_Field.ProposalText.length + 29 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleStartGroupProposal(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)107);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.ProposalData_Field.GroupID);
        this.packInt(byteBuffer, this.ProposalData_Field.Quorum);
        this.packFloat(byteBuffer, this.ProposalData_Field.Majority);
        this.packInt(byteBuffer, this.ProposalData_Field.Duration);
        this.packVariable(byteBuffer, this.ProposalData_Field.ProposalText, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ProposalData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.ProposalData_Field.Quorum = this.unpackInt(byteBuffer);
        this.ProposalData_Field.Majority = this.unpackFloat(byteBuffer);
        this.ProposalData_Field.Duration = this.unpackInt(byteBuffer);
        this.ProposalData_Field.ProposalText = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ProposalData
    {
        public int Duration;
        public UUID GroupID;
        public float Majority;
        public byte[] ProposalText;
        public int Quorum;
    }
}
