package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DenyTrustedCircuit extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();

    public static class DataBlock {
        public UUID EndPointID;
    }

    public DenyTrustedCircuit() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDenyTrustedCircuit(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -119);
        packUUID(byteBuffer, this.DataBlock_Field.EndPointID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.EndPointID = unpackUUID(byteBuffer);
    }
}
