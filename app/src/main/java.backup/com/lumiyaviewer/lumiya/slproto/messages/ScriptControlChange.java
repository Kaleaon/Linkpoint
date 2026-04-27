package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ScriptControlChange extends SLMessage {
    public ArrayList<Data> Data_Fields = new ArrayList<>();

    public static class Data {
        public int Controls;
        public boolean PassToAgent;
        public boolean TakeControls;
    }

    public ScriptControlChange() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return (this.Data_Fields.size() * 6) + 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptControlChange(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -67);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packBoolean(byteBuffer, data.TakeControls);
            packInt(byteBuffer, data.Controls);
            packBoolean(byteBuffer, data.PassToAgent);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Data data = new Data();
            data.TakeControls = unpackBoolean(byteBuffer);
            data.Controls = unpackInt(byteBuffer);
            data.PassToAgent = unpackBoolean(byteBuffer);
            this.Data_Fields.add(data);
        }
    }
}
