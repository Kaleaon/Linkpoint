// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupAccountTransactionsRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public MoneyData MoneyData_Field;
    
    public GroupAccountTransactionsRequest() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MoneyData_Field = new MoneyData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 76;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupAccountTransactionsRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)101);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packUUID(byteBuffer, this.MoneyData_Field.RequestID);
        this.packInt(byteBuffer, this.MoneyData_Field.IntervalDays);
        this.packInt(byteBuffer, this.MoneyData_Field.CurrentInterval);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.RequestID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.IntervalDays = this.unpackInt(byteBuffer);
        this.MoneyData_Field.CurrentInterval = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }
    
    public static class MoneyData
    {
        public int CurrentInterval;
        public int IntervalDays;
        public UUID RequestID;
    }
}
