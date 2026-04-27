package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class ScriptDialog extends SLMessage {
    public ArrayList<Buttons> Buttons_Fields = new ArrayList<>();
    public Data Data_Field;
    public ArrayList<OwnerData> OwnerData_Fields = new ArrayList<>();

    public static class Buttons {
        public byte[] ButtonLabel;
    }

    public static class Data {
        public int ChatChannel;
        public byte[] FirstName;
        public UUID ImageID;
        public byte[] LastName;
        public byte[] Message;
        public UUID ObjectID;
        public byte[] ObjectName;
    }

    public static class OwnerData {
        public UUID OwnerID;
    }

    public ScriptDialog() {
        this.zeroCoded = true;
        this.Data_Field = new Data();
    }

    public int CalcPayloadSize() {
        int length = this.Data_Field.FirstName.length + 17 + 1 + this.Data_Field.LastName.length + 1 + this.Data_Field.ObjectName.length + 2 + this.Data_Field.Message.length + 4 + 16 + 4 + 1;
        Iterator<T> it = this.Buttons_Fields.iterator();
        while (true) {
            int i = length;
            if (!it.hasNext()) {
                return i + 1 + (this.OwnerData_Fields.size() * 16);
            }
            length = ((Buttons) it.next()).ButtonLabel.length + 1 + i;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptDialog(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -66);
        packUUID(byteBuffer, this.Data_Field.ObjectID);
        packVariable(byteBuffer, this.Data_Field.FirstName, 1);
        packVariable(byteBuffer, this.Data_Field.LastName, 1);
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1);
        packVariable(byteBuffer, this.Data_Field.Message, 2);
        packInt(byteBuffer, this.Data_Field.ChatChannel);
        packUUID(byteBuffer, this.Data_Field.ImageID);
        byteBuffer.put((byte) this.Buttons_Fields.size());
        for (Buttons buttons : this.Buttons_Fields) {
            packVariable(byteBuffer, buttons.ButtonLabel, 1);
        }
        byteBuffer.put((byte) this.OwnerData_Fields.size());
        for (OwnerData ownerData : this.OwnerData_Fields) {
            packUUID(byteBuffer, ownerData.OwnerID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.ObjectID = unpackUUID(byteBuffer);
        this.Data_Field.FirstName = unpackVariable(byteBuffer, 1);
        this.Data_Field.LastName = unpackVariable(byteBuffer, 1);
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1);
        this.Data_Field.Message = unpackVariable(byteBuffer, 2);
        this.Data_Field.ChatChannel = unpackInt(byteBuffer);
        this.Data_Field.ImageID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Buttons buttons = new Buttons();
            buttons.ButtonLabel = unpackVariable(byteBuffer, 1);
            this.Buttons_Fields.add(buttons);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            OwnerData ownerData = new OwnerData();
            ownerData.OwnerID = unpackUUID(byteBuffer);
            this.OwnerData_Fields.add(ownerData);
        }
    }
}
