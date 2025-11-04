// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class PayPriceReply extends SLMessage
{
    public ArrayList<ButtonData> ButtonData_Fields;
    public ObjectData ObjectData_Field;
    
    public PayPriceReply() {
        this.ButtonData_Fields = new ArrayList<ButtonData>();
        this.zeroCoded = false;
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ButtonData_Fields.size() * 4 + 25;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandlePayPriceReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-94));
        this.packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
        this.packInt(byteBuffer, this.ObjectData_Field.DefaultPayPrice);
        byteBuffer.put((byte)this.ButtonData_Fields.size());
        final Iterator<Object> iterator = this.ButtonData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packInt(byteBuffer, iterator.next().PayButton);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ObjectData_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.DefaultPayPrice = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ButtonData e = new ButtonData();
            e.PayButton = this.unpackInt(byteBuffer);
            this.ButtonData_Fields.add(e);
        }
    }
    
    public static class ButtonData
    {
        public int PayButton;
    }
    
    public static class ObjectData
    {
        public int DefaultPayPrice;
        public UUID ObjectID;
    }
}
