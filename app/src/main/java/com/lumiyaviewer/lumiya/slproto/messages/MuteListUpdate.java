package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class MuteListUpdate extends SLMessage {
    public MuteData MuteData_Field = new MuteData();

    public static class MuteData {
        public UUID AgentID;
        public byte[] Filename;
    }

    public MuteListUpdate() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.MuteData_Field.Filename.length + 17 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMuteListUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 62);
        packUUID(byteBuffer, this.MuteData_Field.AgentID);
        packVariable(byteBuffer, this.MuteData_Field.Filename, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MuteData_Field.AgentID = unpackUUID(byteBuffer);
        this.MuteData_Field.Filename = unpackVariable(byteBuffer, 1);
    }
}
