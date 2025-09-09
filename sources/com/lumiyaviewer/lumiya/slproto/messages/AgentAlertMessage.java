package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AgentAlertMessage extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public AlertData AlertData_Field = new AlertData();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class AlertData {
        public byte[] Message;
        public boolean Modal;
    }

    public AgentAlertMessage() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.AlertData_Field.Message.length + 2 + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentAlertMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -121);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packBoolean(byteBuffer, this.AlertData_Field.Modal);
        packVariable(byteBuffer, this.AlertData_Field.Message, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AlertData_Field.Modal = unpackBoolean(byteBuffer);
        this.AlertData_Field.Message = unpackVariable(byteBuffer, 1);
    }
}
