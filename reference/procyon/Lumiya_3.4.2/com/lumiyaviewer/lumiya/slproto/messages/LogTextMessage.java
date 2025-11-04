// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LogTextMessage extends SLMessage
{
    public ArrayList<DataBlock> DataBlock_Fields;
    
    public LogTextMessage() {
        this.DataBlock_Fields = new ArrayList<DataBlock>();
        this.zeroCoded = true;
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.DataBlock_Fields.iterator();
        int n = 5;
        while (iterator.hasNext()) {
            n += iterator.next().Message.length + 54;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLogTextMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-121));
        byteBuffer.put((byte)this.DataBlock_Fields.size());
        for (final DataBlock dataBlock : this.DataBlock_Fields) {
            this.packUUID(byteBuffer, dataBlock.FromAgentId);
            this.packUUID(byteBuffer, dataBlock.ToAgentId);
            this.packDouble(byteBuffer, dataBlock.GlobalX);
            this.packDouble(byteBuffer, dataBlock.GlobalY);
            this.packInt(byteBuffer, dataBlock.Time);
            this.packVariable(byteBuffer, dataBlock.Message, 2);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final DataBlock e = new DataBlock();
            e.FromAgentId = this.unpackUUID(byteBuffer);
            e.ToAgentId = this.unpackUUID(byteBuffer);
            e.GlobalX = this.unpackDouble(byteBuffer);
            e.GlobalY = this.unpackDouble(byteBuffer);
            e.Time = this.unpackInt(byteBuffer);
            e.Message = this.unpackVariable(byteBuffer, 2);
            this.DataBlock_Fields.add(e);
        }
    }
    
    public static class DataBlock
    {
        public UUID FromAgentId;
        public double GlobalX;
        public double GlobalY;
        public byte[] Message;
        public int Time;
        public UUID ToAgentId;
    }
}
