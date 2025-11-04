// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ScriptDataReply extends SLMessage
{
    public ArrayList<DataBlock> DataBlock_Fields;
    
    public ScriptDataReply() {
        this.DataBlock_Fields = new ArrayList<DataBlock>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.DataBlock_Fields.iterator();
        int n = 5;
        while (iterator.hasNext()) {
            n += iterator.next().Reply.length + 10;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleScriptDataReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)82);
        byteBuffer.put((byte)this.DataBlock_Fields.size());
        for (final DataBlock dataBlock : this.DataBlock_Fields) {
            this.packLong(byteBuffer, dataBlock.Hash);
            this.packVariable(byteBuffer, dataBlock.Reply, 2);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final DataBlock e = new DataBlock();
            e.Hash = this.unpackLong(byteBuffer);
            e.Reply = this.unpackVariable(byteBuffer, 2);
            this.DataBlock_Fields.add(e);
        }
    }
    
    public static class DataBlock
    {
        public long Hash;
        public byte[] Reply;
    }
}
