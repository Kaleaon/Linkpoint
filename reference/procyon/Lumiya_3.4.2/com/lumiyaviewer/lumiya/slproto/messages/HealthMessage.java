// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class HealthMessage extends SLMessage
{
    public HealthData HealthData_Field;
    
    public HealthMessage() {
        this.zeroCoded = true;
        this.HealthData_Field = new HealthData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 8;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleHealthMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-118));
        this.packFloat(byteBuffer, this.HealthData_Field.Health);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.HealthData_Field.Health = this.unpackFloat(byteBuffer);
    }
    
    public static class HealthData
    {
        public float Health;
    }
}
