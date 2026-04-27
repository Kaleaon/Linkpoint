package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RequestGodlikePowers extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public RequestBlock RequestBlock_Field = new RequestBlock();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class RequestBlock {
        public boolean Godlike;
        public UUID Token;
    }

    public RequestGodlikePowers() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 53;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestGodlikePowers(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 1);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packBoolean(byteBuffer, this.RequestBlock_Field.Godlike);
        packUUID(byteBuffer, this.RequestBlock_Field.Token);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.RequestBlock_Field.Godlike = unpackBoolean(byteBuffer);
        this.RequestBlock_Field.Token = unpackUUID(byteBuffer);
    }
}
