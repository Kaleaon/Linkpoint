package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AttachedSound extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();

    public static class DataBlock {
        public int Flags;
        public float Gain;
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID SoundID;
    }

    public AttachedSound() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 55;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAttachedSound(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put(Ascii.CR);
        packUUID(byteBuffer, this.DataBlock_Field.SoundID);
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID);
        packUUID(byteBuffer, this.DataBlock_Field.OwnerID);
        packFloat(byteBuffer, this.DataBlock_Field.Gain);
        packByte(byteBuffer, (byte) this.DataBlock_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.SoundID = unpackUUID(byteBuffer);
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer);
        this.DataBlock_Field.OwnerID = unpackUUID(byteBuffer);
        this.DataBlock_Field.Gain = unpackFloat(byteBuffer);
        this.DataBlock_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
