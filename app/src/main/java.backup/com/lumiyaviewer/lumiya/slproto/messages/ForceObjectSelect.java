package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ForceObjectSelect extends SLMessage {
    public ArrayList<Data> Data_Fields = new ArrayList<>();
    public Header Header_Field;

    public static class Data {
        public int LocalID;
    }

    public static class Header {
        public boolean ResetList;
    }

    public ForceObjectSelect() {
        this.zeroCoded = false;
        this.Header_Field = new Header();
    }

    public int CalcPayloadSize() {
        return (this.Data_Fields.size() * 4) + 6;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleForceObjectSelect(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -51);
        packBoolean(byteBuffer, this.Header_Field.ResetList);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packInt(byteBuffer, data.LocalID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Header_Field.ResetList = unpackBoolean(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Data data = new Data();
            data.LocalID = unpackInt(byteBuffer);
            this.Data_Fields.add(data);
        }
    }
}
