// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MeanCollisionAlert extends SLMessage
{
    public ArrayList<MeanCollision> MeanCollision_Fields;
    
    public MeanCollisionAlert() {
        this.MeanCollision_Fields = new ArrayList<MeanCollision>();
        this.zeroCoded = true;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MeanCollision_Fields.size() * 41 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMeanCollisionAlert(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-120));
        byteBuffer.put((byte)this.MeanCollision_Fields.size());
        for (final MeanCollision meanCollision : this.MeanCollision_Fields) {
            this.packUUID(byteBuffer, meanCollision.Victim);
            this.packUUID(byteBuffer, meanCollision.Perp);
            this.packInt(byteBuffer, meanCollision.Time);
            this.packFloat(byteBuffer, meanCollision.Mag);
            this.packByte(byteBuffer, (byte)meanCollision.Type);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final MeanCollision e = new MeanCollision();
            e.Victim = this.unpackUUID(byteBuffer);
            e.Perp = this.unpackUUID(byteBuffer);
            e.Time = this.unpackInt(byteBuffer);
            e.Mag = this.unpackFloat(byteBuffer);
            e.Type = (this.unpackByte(byteBuffer) & 0xFF);
            this.MeanCollision_Fields.add(e);
        }
    }
    
    public static class MeanCollision
    {
        public float Mag;
        public UUID Perp;
        public int Time;
        public int Type;
        public UUID Victim;
    }
}
