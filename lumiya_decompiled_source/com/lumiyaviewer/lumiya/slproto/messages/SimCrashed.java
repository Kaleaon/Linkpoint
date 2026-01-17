package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class SimCrashed extends SLMessage {
    public Data Data_Field;
    public ArrayList<Users> Users_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class Data {
        public int RegionX;
        public int RegionY;
    }

    /* loaded from: classes.dex */
    public static class Users {
        public UUID AgentID;
    }

    public SimCrashed() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.Users_Fields.size() * 16) + 13;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimCrashed(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 72);
        packInt(byteBuffer, this.Data_Field.RegionX);
        packInt(byteBuffer, this.Data_Field.RegionY);
        byteBuffer.put((byte) this.Users_Fields.size());
        for (Users users : this.Users_Fields) {
            packUUID(byteBuffer, users.AgentID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.RegionX = unpackInt(byteBuffer);
        this.Data_Field.RegionY = unpackInt(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            Users users = new Users();
            users.AgentID = unpackUUID(byteBuffer);
            this.Users_Fields.add(users);
        }
    }
}
