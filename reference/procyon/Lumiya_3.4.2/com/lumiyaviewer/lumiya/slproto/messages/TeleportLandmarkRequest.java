// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TeleportLandmarkRequest extends SLMessage
{
    public Info Info_Field;
    
    public TeleportLandmarkRequest() {
        this.zeroCoded = true;
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 52;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTeleportLandmarkRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)65);
        this.packUUID(byteBuffer, this.Info_Field.AgentID);
        this.packUUID(byteBuffer, this.Info_Field.SessionID);
        this.packUUID(byteBuffer, this.Info_Field.LandmarkID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Info_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Info_Field.LandmarkID = this.unpackUUID(byteBuffer);
    }
    
    public static class Info
    {
        public UUID AgentID;
        public UUID LandmarkID;
        public UUID SessionID;
    }
}
