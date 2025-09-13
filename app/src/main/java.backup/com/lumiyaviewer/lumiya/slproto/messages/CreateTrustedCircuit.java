package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class CreateTrustedCircuit extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();

    public static class DataBlock {
        public byte[] Digest;
        public UUID EndPointID;
    }

    public CreateTrustedCircuit() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 52;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateTrustedCircuit(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -120);
        packUUID(byteBuffer, this.DataBlock_Field.EndPointID);
        packFixed(byteBuffer, this.DataBlock_Field.Digest, 32);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.EndPointID = unpackUUID(byteBuffer);
        this.DataBlock_Field.Digest = unpackFixed(byteBuffer, 32);
    }
}
