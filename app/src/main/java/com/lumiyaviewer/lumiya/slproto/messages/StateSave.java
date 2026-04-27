package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class StateSave extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public DataBlock DataBlock_Field = new DataBlock();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class DataBlock {
        public byte[] Filename;
    }

    public StateSave() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.DataBlock_Field.Filename.length + 1 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStateSave(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.DEL);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packVariable(byteBuffer, this.DataBlock_Field.Filename, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.DataBlock_Field.Filename = unpackVariable(byteBuffer, 1);
    }
}
