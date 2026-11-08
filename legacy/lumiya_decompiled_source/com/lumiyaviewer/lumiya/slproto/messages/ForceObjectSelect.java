package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ForceObjectSelect extends SLMessage {
    public ArrayList<Data> Data_Fields = new ArrayList<>();
    public Header Header_Field;

    /* loaded from: classes.dex */
    public static class Data {
        public int LocalID;
    }

    /* loaded from: classes.dex */
    public static class Header {
        public boolean ResetList;
    }

    public ForceObjectSelect() {
        this.zeroCoded = false;
        this.Header_Field = new Header();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.Data_Fields.size() * 4) + 6;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleForceObjectSelect(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -51);
        packBoolean(byteBuffer, this.Header_Field.ResetList);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packInt(byteBuffer, data.LocalID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Header_Field.ResetList = unpackBoolean(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            Data data = new Data();
            data.LocalID = unpackInt(byteBuffer);
            this.Data_Fields.add(data);
        }
    }
}
