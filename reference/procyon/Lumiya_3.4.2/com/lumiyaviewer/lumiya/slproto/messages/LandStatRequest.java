// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LandStatRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public RequestData RequestData_Field;
    
    public LandStatRequest() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.RequestData_Field = new RequestData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.RequestData_Field.Filter.length + 9 + 4 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLandStatRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-91));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.RequestData_Field.ReportType);
        this.packInt(byteBuffer, this.RequestData_Field.RequestFlags);
        this.packVariable(byteBuffer, this.RequestData_Field.Filter, 1);
        this.packInt(byteBuffer, this.RequestData_Field.ParcelLocalID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.RequestData_Field.ReportType = this.unpackInt(byteBuffer);
        this.RequestData_Field.RequestFlags = this.unpackInt(byteBuffer);
        this.RequestData_Field.Filter = this.unpackVariable(byteBuffer, 1);
        this.RequestData_Field.ParcelLocalID = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class RequestData
    {
        public byte[] Filter;
        public int ParcelLocalID;
        public int ReportType;
        public int RequestFlags;
    }
}
