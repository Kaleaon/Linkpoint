// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AlertMessage extends SLMessage
{
    public AlertData AlertData_Field;
    public ArrayList<AlertInfo> AlertInfo_Fields;
    
    public AlertMessage() {
        this.AlertInfo_Fields = new ArrayList<AlertInfo>();
        this.zeroCoded = false;
        this.AlertData_Field = new AlertData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int length = this.AlertData_Field.Message.length;
        final Iterator<Object> iterator = this.AlertInfo_Fields.iterator();
        int n = length + 1 + 4 + 1;
        while (iterator.hasNext()) {
            final AlertInfo alertInfo = iterator.next();
            n += alertInfo.ExtraParams.length + (alertInfo.Message.length + 1 + 1);
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAlertMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-122));
        this.packVariable(byteBuffer, this.AlertData_Field.Message, 1);
        byteBuffer.put((byte)this.AlertInfo_Fields.size());
        for (final AlertInfo alertInfo : this.AlertInfo_Fields) {
            this.packVariable(byteBuffer, alertInfo.Message, 1);
            this.packVariable(byteBuffer, alertInfo.ExtraParams, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AlertData_Field.Message = this.unpackVariable(byteBuffer, 1);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final AlertInfo e = new AlertInfo();
            e.Message = this.unpackVariable(byteBuffer, 1);
            e.ExtraParams = this.unpackVariable(byteBuffer, 1);
            this.AlertInfo_Fields.add(e);
        }
    }
    
    public static class AlertData
    {
        public byte[] Message;
    }
    
    public static class AlertInfo
    {
        public byte[] ExtraParams;
        public byte[] Message;
    }
}
