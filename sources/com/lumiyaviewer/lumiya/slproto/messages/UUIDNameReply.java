package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class UUIDNameReply extends SLMessage {
    public ArrayList<UUIDNameBlock> UUIDNameBlock_Fields = new ArrayList<>();

    public static class UUIDNameBlock {
        public byte[] FirstName;
        public UUID ID;
        public byte[] LastName;
    }

    public UUIDNameReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        int i = 5;
        Iterator<T> it = this.UUIDNameBlock_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            UUIDNameBlock uUIDNameBlock = (UUIDNameBlock) it.next();
            i = uUIDNameBlock.LastName.length + uUIDNameBlock.FirstName.length + 17 + 1 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUUIDNameReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -20);
        byteBuffer.put((byte) this.UUIDNameBlock_Fields.size());
        for (UUIDNameBlock uUIDNameBlock : this.UUIDNameBlock_Fields) {
            packUUID(byteBuffer, uUIDNameBlock.ID);
            packVariable(byteBuffer, uUIDNameBlock.FirstName, 1);
            packVariable(byteBuffer, uUIDNameBlock.LastName, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            UUIDNameBlock uUIDNameBlock = new UUIDNameBlock();
            uUIDNameBlock.ID = unpackUUID(byteBuffer);
            uUIDNameBlock.FirstName = unpackVariable(byteBuffer, 1);
            uUIDNameBlock.LastName = unpackVariable(byteBuffer, 1);
            this.UUIDNameBlock_Fields.add(uUIDNameBlock);
        }
    }
}
