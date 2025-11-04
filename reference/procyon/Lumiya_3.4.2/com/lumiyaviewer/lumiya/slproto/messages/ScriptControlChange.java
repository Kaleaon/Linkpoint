// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ScriptControlChange extends SLMessage
{
    public ArrayList<Data> Data_Fields;
    
    public ScriptControlChange() {
        this.Data_Fields = new ArrayList<Data>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Fields.size() * 6 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleScriptControlChange(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-67));
        byteBuffer.put((byte)this.Data_Fields.size());
        for (final Data data : this.Data_Fields) {
            this.packBoolean(byteBuffer, data.TakeControls);
            this.packInt(byteBuffer, data.Controls);
            this.packBoolean(byteBuffer, data.PassToAgent);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Data e = new Data();
            e.TakeControls = this.unpackBoolean(byteBuffer);
            e.Controls = this.unpackInt(byteBuffer);
            e.PassToAgent = this.unpackBoolean(byteBuffer);
            this.Data_Fields.add(e);
        }
    }
    
    public static class Data
    {
        public int Controls;
        public boolean PassToAgent;
        public boolean TakeControls;
    }
}
