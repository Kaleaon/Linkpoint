// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class Error extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    
    public Error() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.Token.length + 5 + 16 + 1 + this.Data_Field.System.length + 2 + this.Data_Field.Message.length + 2 + this.Data_Field.Data.length + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleError(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-89));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packInt(byteBuffer, this.Data_Field.Code);
        this.packVariable(byteBuffer, this.Data_Field.Token, 1);
        this.packUUID(byteBuffer, this.Data_Field.ID);
        this.packVariable(byteBuffer, this.Data_Field.System, 1);
        this.packVariable(byteBuffer, this.Data_Field.Message, 2);
        this.packVariable(byteBuffer, this.Data_Field.Data, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Data_Field.Code = this.unpackInt(byteBuffer);
        this.Data_Field.Token = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.ID = this.unpackUUID(byteBuffer);
        this.Data_Field.System = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.Message = this.unpackVariable(byteBuffer, 2);
        this.Data_Field.Data = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class Data
    {
        public int Code;
        public byte[] Data;
        public UUID ID;
        public byte[] Message;
        public byte[] System;
        public byte[] Token;
    }
}
