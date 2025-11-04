// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RequestPayPrice extends SLMessage
{
    public ObjectData ObjectData_Field;
    
    public RequestPayPrice() {
        this.zeroCoded = false;
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRequestPayPrice(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-95));
        this.packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ObjectData_Field.ObjectID = this.unpackUUID(byteBuffer);
    }
    
    public static class ObjectData
    {
        public UUID ObjectID;
    }
}
