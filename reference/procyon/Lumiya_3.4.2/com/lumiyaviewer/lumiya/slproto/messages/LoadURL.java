// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LoadURL extends SLMessage
{
    public Data Data_Field;
    
    public LoadURL() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 1 + 16 + 16 + 1 + 1 + this.Data_Field.Message.length + 1 + this.Data_Field.URL.length + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLoadURL(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-62));
        this.packVariable(byteBuffer, this.Data_Field.ObjectName, 1);
        this.packUUID(byteBuffer, this.Data_Field.ObjectID);
        this.packUUID(byteBuffer, this.Data_Field.OwnerID);
        this.packBoolean(byteBuffer, this.Data_Field.OwnerIsGroup);
        this.packVariable(byteBuffer, this.Data_Field.Message, 1);
        this.packVariable(byteBuffer, this.Data_Field.URL, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.ObjectName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.Data_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.Data_Field.OwnerIsGroup = this.unpackBoolean(byteBuffer);
        this.Data_Field.Message = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.URL = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class Data
    {
        public byte[] Message;
        public UUID ObjectID;
        public byte[] ObjectName;
        public UUID OwnerID;
        public boolean OwnerIsGroup;
        public byte[] URL;
    }
}
