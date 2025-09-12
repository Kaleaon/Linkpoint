package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SendPostcard extends SLMessage {
    public AgentData AgentData_Field = new AgentData();

    public static class AgentData {
        public UUID AgentID;
        public boolean AllowPublish;
        public UUID AssetID;
        public byte[] From;
        public boolean MaturePublish;
        public byte[] Msg;
        public byte[] Name;
        public LLVector3d PosGlobal;
        public UUID SessionID;
        public byte[] Subject;
        public byte[] To;
    }

    public SendPostcard() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.AgentData_Field.To.length + 73 + 1 + this.AgentData_Field.From.length + 1 + this.AgentData_Field.Name.length + 1 + this.AgentData_Field.Subject.length + 2 + this.AgentData_Field.Msg.length + 1 + 1 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSendPostcard(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -100);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.AssetID);
        packLLVector3d(byteBuffer, this.AgentData_Field.PosGlobal);
        packVariable(byteBuffer, this.AgentData_Field.To, 1);
        packVariable(byteBuffer, this.AgentData_Field.From, 1);
        packVariable(byteBuffer, this.AgentData_Field.Name, 1);
        packVariable(byteBuffer, this.AgentData_Field.Subject, 1);
        packVariable(byteBuffer, this.AgentData_Field.Msg, 2);
        packBoolean(byteBuffer, this.AgentData_Field.AllowPublish);
        packBoolean(byteBuffer, this.AgentData_Field.MaturePublish);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.AssetID = unpackUUID(byteBuffer);
        this.AgentData_Field.PosGlobal = unpackLLVector3d(byteBuffer);
        this.AgentData_Field.To = unpackVariable(byteBuffer, 1);
        this.AgentData_Field.From = unpackVariable(byteBuffer, 1);
        this.AgentData_Field.Name = unpackVariable(byteBuffer, 1);
        this.AgentData_Field.Subject = unpackVariable(byteBuffer, 1);
        this.AgentData_Field.Msg = unpackVariable(byteBuffer, 2);
        this.AgentData_Field.AllowPublish = unpackBoolean(byteBuffer);
        this.AgentData_Field.MaturePublish = unpackBoolean(byteBuffer);
    }
}
