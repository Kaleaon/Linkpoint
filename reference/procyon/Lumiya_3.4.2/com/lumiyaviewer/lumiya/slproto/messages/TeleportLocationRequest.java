// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TeleportLocationRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public Info Info_Field;
    
    public TeleportLocationRequest() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 68;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTeleportLocationRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)63);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packLong(byteBuffer, this.Info_Field.RegionHandle);
        this.packLLVector3(byteBuffer, this.Info_Field.Position);
        this.packLLVector3(byteBuffer, this.Info_Field.LookAt);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Info_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.Info_Field.Position = this.unpackLLVector3(byteBuffer);
        this.Info_Field.LookAt = this.unpackLLVector3(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Info
    {
        public LLVector3 LookAt;
        public LLVector3 Position;
        public long RegionHandle;
    }
}
