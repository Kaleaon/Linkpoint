package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class PreloadSound extends SLMessage {
    public ArrayList<DataBlock> DataBlock_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class DataBlock {
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID SoundID;
    }

    public PreloadSound() {
        this.zeroCoded = false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.DataBlock_Fields.size() * 48) + 3;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePreloadSound(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) 15);
        byteBuffer.put((byte) this.DataBlock_Fields.size());
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packUUID(byteBuffer, dataBlock.ObjectID);
            packUUID(byteBuffer, dataBlock.OwnerID);
            packUUID(byteBuffer, dataBlock.SoundID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            DataBlock dataBlock = new DataBlock();
            dataBlock.ObjectID = unpackUUID(byteBuffer);
            dataBlock.OwnerID = unpackUUID(byteBuffer);
            dataBlock.SoundID = unpackUUID(byteBuffer);
            this.DataBlock_Fields.add(dataBlock);
        }
    }
}
