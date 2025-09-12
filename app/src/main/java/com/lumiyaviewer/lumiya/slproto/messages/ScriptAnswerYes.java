package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ScriptAnswerYes extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Data {
        public UUID ItemID;
        public int Questions;
        public UUID TaskID;
    }

    public ScriptAnswerYes() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 72;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptAnswerYes(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -124);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Data_Field.TaskID);
        packUUID(byteBuffer, this.Data_Field.ItemID);
        packInt(byteBuffer, this.Data_Field.Questions);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.TaskID = unpackUUID(byteBuffer);
        this.Data_Field.ItemID = unpackUUID(byteBuffer);
        this.Data_Field.Questions = unpackInt(byteBuffer);
    }
}
