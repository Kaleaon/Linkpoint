// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ScriptDialog extends SLMessage
{
    public ArrayList<Buttons> Buttons_Fields;
    public Data Data_Field;
    public ArrayList<OwnerData> OwnerData_Fields;
    
    public ScriptDialog() {
        this.Buttons_Fields = new ArrayList<Buttons>();
        this.OwnerData_Fields = new ArrayList<OwnerData>();
        this.zeroCoded = true;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int length = this.Data_Field.FirstName.length;
        final int length2 = this.Data_Field.LastName.length;
        final int length3 = this.Data_Field.ObjectName.length;
        final int length4 = this.Data_Field.Message.length;
        final Iterator<Object> iterator = this.Buttons_Fields.iterator();
        int n = length + 17 + 1 + length2 + 1 + length3 + 2 + length4 + 4 + 16 + 4 + 1;
        while (iterator.hasNext()) {
            n += iterator.next().ButtonLabel.length + 1;
        }
        return n + 1 + this.OwnerData_Fields.size() * 16;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleScriptDialog(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-66));
        this.packUUID(byteBuffer, this.Data_Field.ObjectID);
        this.packVariable(byteBuffer, this.Data_Field.FirstName, 1);
        this.packVariable(byteBuffer, this.Data_Field.LastName, 1);
        this.packVariable(byteBuffer, this.Data_Field.ObjectName, 1);
        this.packVariable(byteBuffer, this.Data_Field.Message, 2);
        this.packInt(byteBuffer, this.Data_Field.ChatChannel);
        this.packUUID(byteBuffer, this.Data_Field.ImageID);
        byteBuffer.put((byte)this.Buttons_Fields.size());
        final Iterator<Object> iterator = this.Buttons_Fields.iterator();
        while (iterator.hasNext()) {
            this.packVariable(byteBuffer, iterator.next().ButtonLabel, 1);
        }
        byteBuffer.put((byte)this.OwnerData_Fields.size());
        final Iterator<Object> iterator2 = this.OwnerData_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packUUID(byteBuffer, iterator2.next().OwnerID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.Data_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.Data_Field.FirstName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.LastName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.ObjectName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.Message = this.unpackVariable(byteBuffer, 2);
        this.Data_Field.ChatChannel = this.unpackInt(byteBuffer);
        this.Data_Field.ImageID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Buttons e = new Buttons();
            e.ButtonLabel = this.unpackVariable(byteBuffer, 1);
            this.Buttons_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final OwnerData e2 = new OwnerData();
            e2.OwnerID = this.unpackUUID(byteBuffer);
            this.OwnerData_Fields.add(e2);
        }
    }
    
    public static class Buttons
    {
        public byte[] ButtonLabel;
    }
    
    public static class Data
    {
        public int ChatChannel;
        public byte[] FirstName;
        public UUID ImageID;
        public byte[] LastName;
        public byte[] Message;
        public UUID ObjectID;
        public byte[] ObjectName;
    }
    
    public static class OwnerData
    {
        public UUID OwnerID;
    }
}
