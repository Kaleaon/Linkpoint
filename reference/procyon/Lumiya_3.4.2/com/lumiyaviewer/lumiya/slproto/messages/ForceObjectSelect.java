// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ForceObjectSelect extends SLMessage
{
    public ArrayList<Data> Data_Fields;
    public Header Header_Field;
    
    public ForceObjectSelect() {
        this.Data_Fields = new ArrayList<Data>();
        this.zeroCoded = false;
        this.Header_Field = new Header();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Fields.size() * 4 + 6;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleForceObjectSelect(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-51));
        this.packBoolean(byteBuffer, this.Header_Field.ResetList);
        byteBuffer.put((byte)this.Data_Fields.size());
        final Iterator<Object> iterator = this.Data_Fields.iterator();
        while (iterator.hasNext()) {
            this.packInt(byteBuffer, iterator.next().LocalID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Header_Field.ResetList = this.unpackBoolean(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Data e = new Data();
            e.LocalID = this.unpackInt(byteBuffer);
            this.Data_Fields.add(e);
        }
    }
    
    public static class Data
    {
        public int LocalID;
    }
    
    public static class Header
    {
        public boolean ResetList;
    }
}
