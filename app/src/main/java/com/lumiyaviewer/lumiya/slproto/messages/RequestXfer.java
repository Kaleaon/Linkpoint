package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RequestXfer extends SLMessage {
    public XferID XferID_Field = new XferID();

    public static class XferID {
        public boolean DeleteOnCompletion;
        public int FilePath;
        public byte[] Filename;
        public long ID;
        public boolean UseBigPackets;
        public UUID VFileID;
        public int VFileType;
    }

    public RequestXfer() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.XferID_Field.Filename.length + 9 + 1 + 1 + 1 + 16 + 2 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestXfer(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -100);
        packLong(byteBuffer, this.XferID_Field.ID);
        packVariable(byteBuffer, this.XferID_Field.Filename, 1);
        packByte(byteBuffer, (byte) this.XferID_Field.FilePath);
        packBoolean(byteBuffer, this.XferID_Field.DeleteOnCompletion);
        packBoolean(byteBuffer, this.XferID_Field.UseBigPackets);
        packUUID(byteBuffer, this.XferID_Field.VFileID);
        packShort(byteBuffer, (short) this.XferID_Field.VFileType);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer);
        this.XferID_Field.Filename = unpackVariable(byteBuffer, 1);
        this.XferID_Field.FilePath = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.XferID_Field.DeleteOnCompletion = unpackBoolean(byteBuffer);
        this.XferID_Field.UseBigPackets = unpackBoolean(byteBuffer);
        this.XferID_Field.VFileID = unpackUUID(byteBuffer);
        this.XferID_Field.VFileType = unpackShort(byteBuffer);
    }
}
