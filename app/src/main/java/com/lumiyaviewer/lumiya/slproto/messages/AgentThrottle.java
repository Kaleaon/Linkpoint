package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AgentThrottle extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Throttle Throttle_Field = new Throttle();

    public static class AgentData {
        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;
    }

    public static class Throttle {
        public int GenCounter;
        public byte[] Throttles;
    }

    public AgentThrottle() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.Throttle_Field.Throttles.length + 5 + 40;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentThrottle(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 81);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.CircuitCode);
        packInt(byteBuffer, this.Throttle_Field.GenCounter);
        packVariable(byteBuffer, this.Throttle_Field.Throttles, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer);
        this.Throttle_Field.GenCounter = unpackInt(byteBuffer);
        this.Throttle_Field.Throttles = unpackVariable(byteBuffer, 1);
    }
}
