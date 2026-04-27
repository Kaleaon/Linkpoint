package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AttachedSoundGainChange extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();

    public static class DataBlock {
        public float Gain;
        public UUID ObjectID;
    }

    public AttachedSoundGainChange() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 22;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAttachedSoundGainChange(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put(Ascii.SO);
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID);
        packFloat(byteBuffer, this.DataBlock_Field.Gain);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer);
        this.DataBlock_Field.Gain = unpackFloat(byteBuffer);
    }
}
