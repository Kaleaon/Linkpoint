package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ChatFromViewer extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ChatData ChatData_Field = new ChatData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ChatData {
        public int Channel;
        public byte[] Message;
        public int Type;
    }

    public ChatFromViewer() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.ChatData_Field.Message.length + 2 + 1 + 4 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChatFromViewer(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 80);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packVariable(byteBuffer, this.ChatData_Field.Message, 2);
        packByte(byteBuffer, (byte) this.ChatData_Field.Type);
        packInt(byteBuffer, this.ChatData_Field.Channel);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ChatData_Field.Message = unpackVariable(byteBuffer, 2);
        this.ChatData_Field.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ChatData_Field.Channel = unpackInt(byteBuffer);
    }
}
