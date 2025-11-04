// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentAlertMessage extends SLMessage
{
    public AgentData AgentData_Field;
    public AlertData AlertData_Field;
    
    public AgentAlertMessage() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.AlertData_Field = new AlertData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.AlertData_Field.Message.length + 2 + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentAlertMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-121));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packBoolean(byteBuffer, this.AlertData_Field.Modal);
        this.packVariable(byteBuffer, this.AlertData_Field.Message, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AlertData_Field.Modal = this.unpackBoolean(byteBuffer);
        this.AlertData_Field.Message = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class AlertData
    {
        public byte[] Message;
        public boolean Modal;
    }
}
