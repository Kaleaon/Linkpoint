package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ChatFromSimulator extends SLMessage {
    public ChatData ChatData_Field = new ChatData();

    public static class ChatData {
        public int Audible;
        public int ChatType;
        public byte[] FromName;
        public byte[] Message;
        public UUID OwnerID;
        public LLVector3 Position;
        public UUID SourceID;
        public int SourceType;
    }

    public ChatFromSimulator() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.ChatData_Field.FromName.length + 1 + 16 + 16 + 1 + 1 + 1 + 12 + 2 + this.ChatData_Field.Message.length + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChatFromSimulator(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -117);
        packVariable(byteBuffer, this.ChatData_Field.FromName, 1);
        packUUID(byteBuffer, this.ChatData_Field.SourceID);
        packUUID(byteBuffer, this.ChatData_Field.OwnerID);
        packByte(byteBuffer, (byte) this.ChatData_Field.SourceType);
        packByte(byteBuffer, (byte) this.ChatData_Field.ChatType);
        packByte(byteBuffer, (byte) this.ChatData_Field.Audible);
        packLLVector3(byteBuffer, this.ChatData_Field.Position);
        packVariable(byteBuffer, this.ChatData_Field.Message, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ChatData_Field.FromName = unpackVariable(byteBuffer, 1);
        this.ChatData_Field.SourceID = unpackUUID(byteBuffer);
        this.ChatData_Field.OwnerID = unpackUUID(byteBuffer);
        this.ChatData_Field.SourceType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ChatData_Field.ChatType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ChatData_Field.Audible = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ChatData_Field.Position = unpackLLVector3(byteBuffer);
        this.ChatData_Field.Message = unpackVariable(byteBuffer, 2);
    }
}
