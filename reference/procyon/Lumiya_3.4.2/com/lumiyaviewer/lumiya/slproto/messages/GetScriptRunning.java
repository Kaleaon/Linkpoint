// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GetScriptRunning extends SLMessage
{
    public Script Script_Field;
    
    public GetScriptRunning() {
        this.zeroCoded = false;
        this.Script_Field = new Script();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGetScriptRunning(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-13));
        this.packUUID(byteBuffer, this.Script_Field.ObjectID);
        this.packUUID(byteBuffer, this.Script_Field.ItemID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Script_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.Script_Field.ItemID = this.unpackUUID(byteBuffer);
    }
    
    public static class Script
    {
        public UUID ItemID;
        public UUID ObjectID;
    }
}
