// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RemoveNameValuePair extends SLMessage
{
    public ArrayList<NameValueData> NameValueData_Fields;
    public TaskData TaskData_Field;
    
    public RemoveNameValuePair() {
        this.NameValueData_Fields = new ArrayList<NameValueData>();
        this.zeroCoded = false;
        this.TaskData_Field = new TaskData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.NameValueData_Fields.iterator();
        int n = 21;
        while (iterator.hasNext()) {
            n += iterator.next().NVPair.length + 2;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRemoveNameValuePair(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)74);
        this.packUUID(byteBuffer, this.TaskData_Field.ID);
        byteBuffer.put((byte)this.NameValueData_Fields.size());
        final Iterator<Object> iterator = this.NameValueData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packVariable(byteBuffer, iterator.next().NVPair, 2);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TaskData_Field.ID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final NameValueData e = new NameValueData();
            e.NVPair = this.unpackVariable(byteBuffer, 2);
            this.NameValueData_Fields.add(e);
        }
    }
    
    public static class NameValueData
    {
        public byte[] NVPair;
    }
    
    public static class TaskData
    {
        public UUID ID;
    }
}
