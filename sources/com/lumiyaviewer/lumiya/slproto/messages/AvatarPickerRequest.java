package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AvatarPickerRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
        public UUID QueryID;
        public UUID SessionID;
    }

    public static class Data {
        public byte[] Name;
    }

    public AvatarPickerRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.Name.length + 1 + 52;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPickerRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.SUB);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.QueryID);
        packVariable(byteBuffer, this.Data_Field.Name, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer);
        this.Data_Field.Name = unpackVariable(byteBuffer, 1);
    }
}
