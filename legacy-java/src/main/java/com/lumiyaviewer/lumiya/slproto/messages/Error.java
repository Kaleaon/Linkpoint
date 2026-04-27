package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class Error extends SLMessage {
    public AgentData AgentData_Field;
    public Data Data_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
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
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.Data_Field.Token.length + 5 + 16 + 1 + this.Data_Field.System.length + 2 + this.Data_Field.Message.length + 2 + this.Data_Field.Data.length + 20;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleError(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
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
