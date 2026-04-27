package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Error extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class Data {
        public int Code;
        public byte[] Data;
        public UUID ID;
        public byte[] Message;
        public byte[] System;
        public byte[] Token;
    }

    public Error() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.Token.length + 5 + 16 + 1 + this.Data_Field.System.length + 2 + this.Data_Field.Message.length + 2 + this.Data_Field.Data.length + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleError(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -89);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packInt(byteBuffer, this.Data_Field.Code);
        packVariable(byteBuffer, this.Data_Field.Token, 1);
        packUUID(byteBuffer, this.Data_Field.ID);
        packVariable(byteBuffer, this.Data_Field.System, 1);
        packVariable(byteBuffer, this.Data_Field.Message, 2);
        packVariable(byteBuffer, this.Data_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.Data_Field.Code = unpackInt(byteBuffer);
        this.Data_Field.Token = unpackVariable(byteBuffer, 1);
        this.Data_Field.ID = unpackUUID(byteBuffer);
        this.Data_Field.System = unpackVariable(byteBuffer, 1);
        this.Data_Field.Message = unpackVariable(byteBuffer, 2);
        this.Data_Field.Data = unpackVariable(byteBuffer, 2);
    }
}
