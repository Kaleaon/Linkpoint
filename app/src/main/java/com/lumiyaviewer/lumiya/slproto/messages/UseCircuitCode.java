package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UseCircuitCode extends SLMessage {
    public CircuitCode CircuitCode_Field = new CircuitCode();

    public static class CircuitCode {
        public int Code;
        public UUID ID;
        public UUID SessionID;
    }

    public UseCircuitCode() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 40;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUseCircuitCode(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 3);
        packInt(byteBuffer, this.CircuitCode_Field.Code);
        packUUID(byteBuffer, this.CircuitCode_Field.SessionID);
        packUUID(byteBuffer, this.CircuitCode_Field.ID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.CircuitCode_Field.Code = unpackInt(byteBuffer);
        this.CircuitCode_Field.SessionID = unpackUUID(byteBuffer);
        this.CircuitCode_Field.ID = unpackUUID(byteBuffer);
    }
}
