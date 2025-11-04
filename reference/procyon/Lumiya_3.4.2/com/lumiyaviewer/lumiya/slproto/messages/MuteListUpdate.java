// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MuteListUpdate extends SLMessage
{
    public MuteData MuteData_Field;
    
    public MuteListUpdate() {
        this.zeroCoded = false;
        this.MuteData_Field = new MuteData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MuteData_Field.Filename.length + 17 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMuteListUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)62);
        this.packUUID(byteBuffer, this.MuteData_Field.AgentID);
        this.packVariable(byteBuffer, this.MuteData_Field.Filename, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.MuteData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.MuteData_Field.Filename = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class MuteData
    {
        public UUID AgentID;
        public byte[] Filename;
    }
}
