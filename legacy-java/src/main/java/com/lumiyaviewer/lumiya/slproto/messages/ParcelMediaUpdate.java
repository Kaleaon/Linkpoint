package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class ParcelMediaUpdate extends SLMessage {
    public DataBlockExtended DataBlockExtended_Field;
    public DataBlock DataBlock_Field;

    /* loaded from: classes.dex */
    public static class DataBlock {
        public int MediaAutoScale;
        public UUID MediaID;
        public byte[] MediaURL;
    }

    /* loaded from: classes.dex */
    public static class DataBlockExtended {
        public byte[] MediaDesc;
        public int MediaHeight;
        public int MediaLoop;
        public byte[] MediaType;
        public int MediaWidth;
    }

    public ParcelMediaUpdate() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
        this.DataBlockExtended_Field = new DataBlockExtended();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.DataBlock_Field.MediaURL.length + 1 + 16 + 1 + 4 + this.DataBlockExtended_Field.MediaType.length + 1 + 1 + this.DataBlockExtended_Field.MediaDesc.length + 4 + 4 + 1;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelMediaUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -92);
        packVariable(byteBuffer, this.DataBlock_Field.MediaURL, 1);
        packUUID(byteBuffer, this.DataBlock_Field.MediaID);
        packByte(byteBuffer, (byte) this.DataBlock_Field.MediaAutoScale);
        packVariable(byteBuffer, this.DataBlockExtended_Field.MediaType, 1);
        packVariable(byteBuffer, this.DataBlockExtended_Field.MediaDesc, 1);
        packInt(byteBuffer, this.DataBlockExtended_Field.MediaWidth);
        packInt(byteBuffer, this.DataBlockExtended_Field.MediaHeight);
        packByte(byteBuffer, (byte) this.DataBlockExtended_Field.MediaLoop);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.MediaURL = unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.MediaID = unpackUUID(byteBuffer);
        this.DataBlock_Field.MediaAutoScale = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.DataBlockExtended_Field.MediaType = unpackVariable(byteBuffer, 1);
        this.DataBlockExtended_Field.MediaDesc = unpackVariable(byteBuffer, 1);
        this.DataBlockExtended_Field.MediaWidth = unpackInt(byteBuffer);
        this.DataBlockExtended_Field.MediaHeight = unpackInt(byteBuffer);
        this.DataBlockExtended_Field.MediaLoop = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
