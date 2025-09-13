package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class SimCrashed extends SLMessage {
    public Data Data_Field;
    public ArrayList<Users> Users_Fields = new ArrayList<>();

    public static class Data {
        public int RegionX;
        public int RegionY;
    }

    public static class Users {
        public UUID AgentID;
    }

    public SimCrashed() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }

    public int CalcPayloadSize() {
        return (this.Users_Fields.size() * 16) + 13;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimCrashed(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 72);
        packInt(byteBuffer, this.Data_Field.RegionX);
        packInt(byteBuffer, this.Data_Field.RegionY);
        byteBuffer.put((byte) this.Users_Fields.size());
        for (Users users : this.Users_Fields) {
            packUUID(byteBuffer, users.AgentID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.RegionX = unpackInt(byteBuffer);
        this.Data_Field.RegionY = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Users users = new Users();
            users.AgentID = unpackUUID(byteBuffer);
            this.Users_Fields.add(users);
        }
    }
}
