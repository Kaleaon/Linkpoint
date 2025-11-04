package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class PayPriceReply extends SLMessage {
    public ArrayList<ButtonData> ButtonData_Fields = new ArrayList<>();
    public ObjectData ObjectData_Field;

    /* loaded from: classes.dex */
    public static class ButtonData {
        public int PayButton;
    }

    /* loaded from: classes.dex */
    public static class ObjectData {
        public int DefaultPayPrice;
        public UUID ObjectID;
    }

    public PayPriceReply() {
        this.zeroCoded = false;
        this.ObjectData_Field = new ObjectData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.ButtonData_Fields.size() * 4) + 25;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePayPriceReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -94);
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
        packInt(byteBuffer, this.ObjectData_Field.DefaultPayPrice);
        byteBuffer.put((byte) this.ButtonData_Fields.size());
        Iterator<T> it = this.ButtonData_Fields.iterator();
        while (it.hasNext()) {
            packInt(byteBuffer, ((ButtonData) it.next()).PayButton);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer);
        this.ObjectData_Field.DefaultPayPrice = unpackInt(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            ButtonData buttonData = new ButtonData();
            buttonData.PayButton = unpackInt(byteBuffer);
            this.ButtonData_Fields.add(buttonData);
        }
    }
}
