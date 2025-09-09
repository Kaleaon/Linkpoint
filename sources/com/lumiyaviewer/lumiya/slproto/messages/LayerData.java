package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class LayerData extends SLMessage {
    public LayerDataData LayerDataData_Field = new LayerDataData();
    public LayerID LayerID_Field = new LayerID();

    public static class LayerDataData {
        public byte[] Data;
    }

    public static class LayerID {
        public int Type;
    }

    public LayerData() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.LayerDataData_Field.Data.length + 2 + 2;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLayerData(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.VT);
        packByte(byteBuffer, (byte) this.LayerID_Field.Type);
        packVariable(byteBuffer, this.LayerDataData_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.LayerID_Field.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.LayerDataData_Field.Data = unpackVariable(byteBuffer, 2);
    }
}
