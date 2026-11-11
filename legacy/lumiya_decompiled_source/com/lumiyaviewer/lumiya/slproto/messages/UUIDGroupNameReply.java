package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
/* loaded from: classes.dex */
public class UUIDGroupNameReply extends SLMessage {
    public ArrayList<UUIDNameBlock> UUIDNameBlock_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class UUIDNameBlock {
        public byte[] GroupName;
        public UUID ID;
    }

    public UUIDGroupNameReply() {
        this.zeroCoded = false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 5;
        Iterator<T> it = this.UUIDNameBlock_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((UUIDNameBlock) it.next()).GroupName.length + 17 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUUIDGroupNameReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -18);
        byteBuffer.put((byte) this.UUIDNameBlock_Fields.size());
        for (UUIDNameBlock uUIDNameBlock : this.UUIDNameBlock_Fields) {
            packUUID(byteBuffer, uUIDNameBlock.ID);
            packVariable(byteBuffer, uUIDNameBlock.GroupName, 1);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            UUIDNameBlock uUIDNameBlock = new UUIDNameBlock();
            uUIDNameBlock.ID = unpackUUID(byteBuffer);
            uUIDNameBlock.GroupName = unpackVariable(byteBuffer, 1);
            this.UUIDNameBlock_Fields.add(uUIDNameBlock);
        }
    }
}
