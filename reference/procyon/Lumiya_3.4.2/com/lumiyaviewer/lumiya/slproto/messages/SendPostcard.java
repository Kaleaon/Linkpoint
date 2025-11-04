// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SendPostcard extends SLMessage
{
    public AgentData AgentData_Field;
    
    public SendPostcard() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.AgentData_Field.To.length + 73 + 1 + this.AgentData_Field.From.length + 1 + this.AgentData_Field.Name.length + 1 + this.AgentData_Field.Subject.length + 2 + this.AgentData_Field.Msg.length + 1 + 1 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSendPostcard(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-100));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.AssetID);
        this.packLLVector3d(byteBuffer, this.AgentData_Field.PosGlobal);
        this.packVariable(byteBuffer, this.AgentData_Field.To, 1);
        this.packVariable(byteBuffer, this.AgentData_Field.From, 1);
        this.packVariable(byteBuffer, this.AgentData_Field.Name, 1);
        this.packVariable(byteBuffer, this.AgentData_Field.Subject, 1);
        this.packVariable(byteBuffer, this.AgentData_Field.Msg, 2);
        this.packBoolean(byteBuffer, this.AgentData_Field.AllowPublish);
        this.packBoolean(byteBuffer, this.AgentData_Field.MaturePublish);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.AssetID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.PosGlobal = this.unpackLLVector3d(byteBuffer);
        this.AgentData_Field.To = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.From = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.Subject = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.Msg = this.unpackVariable(byteBuffer, 2);
        this.AgentData_Field.AllowPublish = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.MaturePublish = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
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
}
