// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EventLocationReply extends SLMessage
{
    public EventData EventData_Field;
    public QueryData QueryData_Field;
    
    public EventLocationReply() {
        this.zeroCoded = true;
        this.QueryData_Field = new QueryData();
        this.EventData_Field = new EventData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 49;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEventLocationReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)52);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        this.packBoolean(byteBuffer, this.EventData_Field.Success);
        this.packUUID(byteBuffer, this.EventData_Field.RegionID);
        this.packLLVector3(byteBuffer, this.EventData_Field.RegionPos);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        this.EventData_Field.Success = this.unpackBoolean(byteBuffer);
        this.EventData_Field.RegionID = this.unpackUUID(byteBuffer);
        this.EventData_Field.RegionPos = this.unpackLLVector3(byteBuffer);
    }
    
    public static class EventData
    {
        public UUID RegionID;
        public LLVector3 RegionPos;
        public boolean Success;
    }
    
    public static class QueryData
    {
        public UUID QueryID;
    }
}
