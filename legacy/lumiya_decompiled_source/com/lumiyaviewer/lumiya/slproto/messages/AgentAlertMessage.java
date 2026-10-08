package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class AgentAlertMessage extends SLMessage {
    public AgentData AgentData_Field;
    public AlertData AlertData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class AlertData {
        public byte[] Message;
        public boolean Modal;
    }

    public AgentAlertMessage() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.AlertData_Field = new AlertData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.AlertData_Field.Message.length + 2 + 20;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentAlertMessage(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -121);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packBoolean(byteBuffer, this.AlertData_Field.Modal);
        packVariable(byteBuffer, this.AlertData_Field.Message, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AlertData_Field.Modal = unpackBoolean(byteBuffer);
        this.AlertData_Field.Message = unpackVariable(byteBuffer, 1);
    }
}
