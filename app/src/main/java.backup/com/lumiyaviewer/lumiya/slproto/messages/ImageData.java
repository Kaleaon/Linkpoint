package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ImageData extends SLMessage {
    public ImageDataData ImageDataData_Field = new ImageDataData();
    public ImageID ImageID_Field = new ImageID();

    public static class ImageDataData {
        public byte[] Data;
    }

    public static class ImageID {
        public int Codec;
        public UUID ID;
        public int Packets;
        public int Size;
    }

    public ImageData() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.ImageDataData_Field.Data.length + 2 + 24;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImageData(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 9);
        packUUID(byteBuffer, this.ImageID_Field.ID);
        packByte(byteBuffer, (byte) this.ImageID_Field.Codec);
        packInt(byteBuffer, this.ImageID_Field.Size);
        packShort(byteBuffer, (short) this.ImageID_Field.Packets);
        packVariable(byteBuffer, this.ImageDataData_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = unpackUUID(byteBuffer);
        this.ImageID_Field.Codec = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ImageID_Field.Size = unpackInt(byteBuffer);
        this.ImageID_Field.Packets = unpackShort(byteBuffer) & 65535;
        this.ImageDataData_Field.Data = unpackVariable(byteBuffer, 2);
    }
}
