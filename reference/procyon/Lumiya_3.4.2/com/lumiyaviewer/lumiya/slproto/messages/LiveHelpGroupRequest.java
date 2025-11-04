// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LiveHelpGroupRequest extends SLMessage
{
    public RequestData RequestData_Field;
    
    public LiveHelpGroupRequest() {
        this.zeroCoded = false;
        this.RequestData_Field = new RequestData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLiveHelpGroupRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)123);
        this.packUUID(byteBuffer, this.RequestData_Field.RequestID);
        this.packUUID(byteBuffer, this.RequestData_Field.AgentID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.RequestData_Field.RequestID = this.unpackUUID(byteBuffer);
        this.RequestData_Field.AgentID = this.unpackUUID(byteBuffer);
    }
    
    public static class RequestData
    {
        public UUID AgentID;
        public UUID RequestID;
    }
}
