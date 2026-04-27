package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ScriptDialogReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Data {
        public int ButtonIndex;
        public byte[] ButtonLabel;
        public int ChatChannel;
        public UUID ObjectID;
    }

    public ScriptDialogReply() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.ButtonLabel.length + 25 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptDialogReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -65);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Data_Field.ObjectID);
        packInt(byteBuffer, this.Data_Field.ChatChannel);
        packInt(byteBuffer, this.Data_Field.ButtonIndex);
        packVariable(byteBuffer, this.Data_Field.ButtonLabel, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.ObjectID = unpackUUID(byteBuffer);
        this.Data_Field.ChatChannel = unpackInt(byteBuffer);
        this.Data_Field.ButtonIndex = unpackInt(byteBuffer);
        this.Data_Field.ButtonLabel = unpackVariable(byteBuffer, 1);
    }
}
