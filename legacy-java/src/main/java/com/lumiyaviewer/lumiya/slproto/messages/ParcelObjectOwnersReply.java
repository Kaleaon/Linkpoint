package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

/* loaded from: classes.dex */
public class ParcelObjectOwnersReply extends SLMessage {
    public ArrayList<Data> Data_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class Data {
        public int Count;
        public boolean IsGroupOwned;
        public boolean OnlineStatus;
        public UUID OwnerID;
    }

    public ParcelObjectOwnersReply() {
        this.zeroCoded = true;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.Data_Fields.size() * 22) + 5;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelObjectOwnersReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            Data data = new Data();
            data.OwnerID = unpackUUID(byteBuffer);
            data.IsGroupOwned = unpackBoolean(byteBuffer);
            data.Count = unpackInt(byteBuffer);
            data.OnlineStatus = unpackBoolean(byteBuffer);
            this.Data_Fields.add(data);
        }
    }
}
