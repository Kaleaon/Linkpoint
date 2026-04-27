package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ChatPass extends SLMessage {
    public ChatData ChatData_Field = new ChatData();

    public static class ChatData {
        public int Channel;
        public UUID ID;
        public byte[] Message;
        public byte[] Name;
        public UUID OwnerID;
        public LLVector3 Position;
        public float Radius;
        public int SimAccess;
        public int SourceType;
        public int Type;
    }

    public ChatPass() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.ChatData_Field.Name.length + 49 + 1 + 1 + 4 + 1 + 2 + this.ChatData_Field.Message.length + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChatPass(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -17);
        packInt(byteBuffer, this.ChatData_Field.Channel);
        packLLVector3(byteBuffer, this.ChatData_Field.Position);
        packUUID(byteBuffer, this.ChatData_Field.ID);
        packUUID(byteBuffer, this.ChatData_Field.OwnerID);
        packVariable(byteBuffer, this.ChatData_Field.Name, 1);
        packByte(byteBuffer, (byte) this.ChatData_Field.SourceType);
        packByte(byteBuffer, (byte) this.ChatData_Field.Type);
        packFloat(byteBuffer, this.ChatData_Field.Radius);
        packByte(byteBuffer, (byte) this.ChatData_Field.SimAccess);
        packVariable(byteBuffer, this.ChatData_Field.Message, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ChatData_Field.Channel = unpackInt(byteBuffer);
        this.ChatData_Field.Position = unpackLLVector3(byteBuffer);
        this.ChatData_Field.ID = unpackUUID(byteBuffer);
        this.ChatData_Field.OwnerID = unpackUUID(byteBuffer);
        this.ChatData_Field.Name = unpackVariable(byteBuffer, 1);
        this.ChatData_Field.SourceType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ChatData_Field.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ChatData_Field.Radius = unpackFloat(byteBuffer);
        this.ChatData_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ChatData_Field.Message = unpackVariable(byteBuffer, 2);
    }
}
