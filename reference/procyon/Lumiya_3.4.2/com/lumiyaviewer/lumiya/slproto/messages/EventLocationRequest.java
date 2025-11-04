// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EventLocationRequest extends SLMessage
{
    public EventData EventData_Field;
    public QueryData QueryData_Field;
    
    public EventLocationRequest() {
        this.zeroCoded = true;
        this.QueryData_Field = new QueryData();
        this.EventData_Field = new EventData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 24;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEventLocationRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)51);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        this.packInt(byteBuffer, this.EventData_Field.EventID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        this.EventData_Field.EventID = this.unpackInt(byteBuffer);
    }
    
    public static class EventData
    {
        public int EventID;
    }
    
    public static class QueryData
    {
        public UUID QueryID;
    }
}
