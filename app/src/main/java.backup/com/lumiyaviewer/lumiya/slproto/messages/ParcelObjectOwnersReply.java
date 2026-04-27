package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ParcelObjectOwnersReply extends SLMessage {
    public ArrayList<Data> Data_Fields = new ArrayList<>();

    public static class Data {
        public int Count;
        public boolean IsGroupOwned;
        public boolean OnlineStatus;
        public UUID OwnerID;
    }

    public ParcelObjectOwnersReply() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return (this.Data_Fields.size() * 22) + 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelObjectOwnersReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 57);
        byteBuffer.put((byte) this.Data_Fields.size());
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.OwnerID);
            packBoolean(byteBuffer, data.IsGroupOwned);
            packInt(byteBuffer, data.Count);
            packBoolean(byteBuffer, data.OnlineStatus);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Data data = new Data();
            data.OwnerID = unpackUUID(byteBuffer);
            data.IsGroupOwned = unpackBoolean(byteBuffer);
            data.Count = unpackInt(byteBuffer);
            data.OnlineStatus = unpackBoolean(byteBuffer);
            this.Data_Fields.add(data);
        }
    }
}
