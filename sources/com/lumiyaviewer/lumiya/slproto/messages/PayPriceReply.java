package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class PayPriceReply extends SLMessage {
    public ArrayList<ButtonData> ButtonData_Fields = new ArrayList<>();
    public ObjectData ObjectData_Field;

    public static class ButtonData {
        public int PayButton;
    }

    public static class ObjectData {
        public int DefaultPayPrice;
        public UUID ObjectID;
    }

    public PayPriceReply() {
        this.zeroCoded = false;
        this.ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize() {
        return (this.ButtonData_Fields.size() * 4) + 25;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePayPriceReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -94);
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
        packInt(byteBuffer, this.ObjectData_Field.DefaultPayPrice);
        byteBuffer.put((byte) this.ButtonData_Fields.size());
        for (ButtonData buttonData : this.ButtonData_Fields) {
            packInt(byteBuffer, buttonData.PayButton);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer);
        this.ObjectData_Field.DefaultPayPrice = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ButtonData buttonData = new ButtonData();
            buttonData.PayButton = unpackInt(byteBuffer);
            this.ButtonData_Fields.add(buttonData);
        }
    }
}
