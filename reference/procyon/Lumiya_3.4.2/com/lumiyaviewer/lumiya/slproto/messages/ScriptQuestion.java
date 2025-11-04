// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ScriptQuestion extends SLMessage
{
    public Data Data_Field;
    
    public ScriptQuestion() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 33 + 1 + this.Data_Field.ObjectOwner.length + 4 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleScriptQuestion(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-68));
        this.packUUID(byteBuffer, this.Data_Field.TaskID);
        this.packUUID(byteBuffer, this.Data_Field.ItemID);
        this.packVariable(byteBuffer, this.Data_Field.ObjectName, 1);
        this.packVariable(byteBuffer, this.Data_Field.ObjectOwner, 1);
        this.packInt(byteBuffer, this.Data_Field.Questions);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.TaskID = this.unpackUUID(byteBuffer);
        this.Data_Field.ItemID = this.unpackUUID(byteBuffer);
        this.Data_Field.ObjectName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.ObjectOwner = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.Questions = this.unpackInt(byteBuffer);
    }
    
    public static class Data
    {
        public UUID ItemID;
        public byte[] ObjectName;
        public byte[] ObjectOwner;
        public int Questions;
        public UUID TaskID;
    }
}
