// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SetScriptRunning extends SLMessage
{
    public AgentData AgentData_Field;
    public Script Script_Field;
    
    public SetScriptRunning() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Script_Field = new Script();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 69;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSetScriptRunning(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-11));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.Script_Field.ObjectID);
        this.packUUID(byteBuffer, this.Script_Field.ItemID);
        this.packBoolean(byteBuffer, this.Script_Field.Running);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Script_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.Script_Field.ItemID = this.unpackUUID(byteBuffer);
        this.Script_Field.Running = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Script
    {
        public UUID ItemID;
        public UUID ObjectID;
        public boolean Running;
    }
}
