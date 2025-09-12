package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AgentHeightWidth extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public HeightWidthBlock HeightWidthBlock_Field = new HeightWidthBlock();

    public static class AgentData {
        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;
    }

    public static class HeightWidthBlock {
        public int GenCounter;
        public int Height;
        public int Width;
    }

    public AgentHeightWidth() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 48;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentHeightWidth(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 83);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.CircuitCode);
        packInt(byteBuffer, this.HeightWidthBlock_Field.GenCounter);
        packShort(byteBuffer, (short) this.HeightWidthBlock_Field.Height);
        packShort(byteBuffer, (short) this.HeightWidthBlock_Field.Width);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer);
        this.HeightWidthBlock_Field.GenCounter = unpackInt(byteBuffer);
        this.HeightWidthBlock_Field.Height = unpackShort(byteBuffer) & 65535;
        this.HeightWidthBlock_Field.Width = unpackShort(byteBuffer) & 65535;
    }
}
