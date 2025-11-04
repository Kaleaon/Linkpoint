package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class AgentThrottle extends SLMessage {
    public AgentData AgentData_Field;
    public Throttle Throttle_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class Throttle {
        public int GenCounter;
        public byte[] Throttles;
    }

    public AgentThrottle() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Throttle_Field = new Throttle();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.Throttle_Field.Throttles.length + 5 + 40;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentThrottle(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 81);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.CircuitCode);
        packInt(byteBuffer, this.Throttle_Field.GenCounter);
        packVariable(byteBuffer, this.Throttle_Field.Throttles, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer);
        this.Throttle_Field.GenCounter = unpackInt(byteBuffer);
        this.Throttle_Field.Throttles = unpackVariable(byteBuffer, 1);
    }
}
